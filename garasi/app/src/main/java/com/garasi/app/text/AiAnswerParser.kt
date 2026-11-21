package com.garasi.app.text

import com.garasi.app.data.Condition

data class AiAnswer(
    val title: String? = null,
    val description: String? = null,
    val condition: Condition? = null,
    val conditionRaw: String? = null,
    val usedMin: Long? = null,
    val usedMax: Long? = null,
    val newPrice: Long? = null,
) {
    val isEmpty get() = title == null && description == null && condition == null &&
        usedMin == null && usedMax == null && newPrice == null
}

/** Parses the strict TITLE/DESCRIPTION/CONDITION/USED_PRICE/NEW_PRICE reply format, tolerantly. */
object AiAnswerParser {
    private val keyLine = Regex(
        """^\s*[*_#>\s]*(TITLE|DESCRIPTION|CONDITION|USED[_ ]PRICE|NEW[_ ]PRICE)[*_\s]*:[*_]*\s?(.*)$""",
    )

    private fun canonical(k: String): String = when (k.uppercase().replace(' ', '_')) {
        "TITLE" -> "TITLE"
        "DESCRIPTION" -> "DESCRIPTION"
        "CONDITION" -> "CONDITION"
        "USED_PRICE" -> "USED_PRICE"
        else -> "NEW_PRICE"
    }

    fun parse(raw: String): AiAnswer {
        val text = raw.replace("\r\n", "\n").replace("```", "")
        val fields = linkedMapOf<String, StringBuilder>()
        var current: String? = null
        for (line in text.lines()) {
            val m = keyLine.find(line)
            if (m != null) {
                current = canonical(m.groupValues[1])
                fields[current] = StringBuilder(m.groupValues[2].trim())
            } else if (current != null) {
                fields[current]!!.append('\n').append(line)
            }
        }
        fun f(k: String) = fields[k]?.toString()?.trim()?.trim('*', '"')?.trim()?.takeIf { it.isNotEmpty() }

        val usedRaw = f("USED_PRICE")
        var usedMin: Long? = null
        var usedMax: Long? = null
        if (usedRaw != null) {
            val parts = usedRaw.split(Regex("""\s*(?:–|—|-|s/d|sampai|to)\s*(?=\D*\d)""", RegexOption.IGNORE_CASE))
                .mapNotNull { Money.parse(it) }
            usedMin = parts.getOrNull(0)
            usedMax = parts.getOrNull(1) ?: usedMin
        }
        val condRaw = f("CONDITION")
        return AiAnswer(
            title = f("TITLE")?.lines()?.first()?.trim(),
            description = f("DESCRIPTION"),
            condition = condRaw?.let { parseCondition(it) },
            conditionRaw = condRaw,
            usedMin = usedMin,
            usedMax = usedMax,
            newPrice = f("NEW_PRICE")?.let { Money.parse(it) },
        )
    }

    fun parseCondition(raw: String): Condition? {
        val s = raw.lowercase()
        return when {
            "like new" in s || "seperti baru" in s || "mulus" in s && "minus" !in s -> Condition.LIKE_NEW
            "flaw" in s || "minus" in s || "cacat" in s || "rusak" in s -> Condition.USED_FLAWS
            "used" in s || "bekas" in s || "second" in s || "baik" in s || "good" in s -> Condition.USED_GOOD
            "new" in s || "baru" in s || "segel" in s -> Condition.NEW
            else -> null
        }
    }
}

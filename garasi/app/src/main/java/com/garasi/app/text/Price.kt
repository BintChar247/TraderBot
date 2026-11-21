package com.garasi.app.text

import com.garasi.app.data.Condition

object PriceCalculator {
    const val NEGOTIATION_BUFFER = 0.10

    fun conditionFactor(c: Condition): Double = when (c) {
        Condition.NEW -> 0.9
        Condition.LIKE_NEW -> 0.8
        Condition.USED_GOOD -> 0.65
        Condition.USED_FLAWS -> 0.45
    }

    data class Suggestion(val fairPrice: Long, val askPrice: Long)

    /** median × condition factor (the price you'd accept) + 10% negotiation buffer (the price you list). */
    fun suggest(median: Long, condition: Condition): Suggestion? {
        if (median <= 0) return null
        val fair = median * conditionFactor(condition)
        return Suggestion(Money.roundNice(fair), Money.roundNice(fair * (1 + NEGOTIATION_BUFFER)))
    }

    /** Median of whatever compare prices were entered (low/median/high may be partially filled). */
    fun effectiveMedian(low: Long, median: Long, high: Long): Long = when {
        median > 0 -> median
        low > 0 && high > 0 -> (low + high) / 2
        else -> maxOf(low, high)
    }
}

package com.garasi.app.ml

import android.content.Context
import android.net.Uri
import com.garasi.app.data.Category
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.File

data class IdentifyResult(
    val barcode: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val category: Category? = null,
    val labels: List<String> = emptyList(),
    val textSnippet: String = "",
) {
    val isEmpty get() = barcode == null && brand == null && model == null && category == null
}

/** Free, offline item identification with ML Kit: barcode + OCR + image labeling. */
class ItemIdentifier(private val context: Context) {

    suspend fun identify(paths: List<String>): IdentifyResult {
        val barcoder = BarcodeScanning.getClient()
        val ocr = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.Builder().setConfidenceThreshold(0.55f).build())
        try {
            var barcode: String? = null
            val text = StringBuilder()
            val labelScores = mutableMapOf<String, Float>()
            for (path in paths.take(10)) {
                val image = InputImage.fromFilePath(context, Uri.fromFile(File(path)))
                if (barcode == null) {
                    val codes = runCatching { barcoder.process(image).await() }.getOrNull().orEmpty()
                    barcode = (codes.firstOrNull { it.rawValue != null && it.format in PRODUCT_FORMATS }
                        ?: codes.firstOrNull { !it.rawValue.isNullOrBlank() && it.valueType != Barcode.TYPE_URL })
                        ?.rawValue
                }
                runCatching { ocr.process(image).await() }.getOrNull()?.let { text.append(it.text).append('\n') }
                runCatching { labeler.process(image).await() }.getOrNull()?.forEach {
                    labelScores[it.text] = (labelScores[it.text] ?: 0f) + it.confidence
                }
            }
            val ocrText = text.toString()
            val labels = labelScores.entries.sortedByDescending { it.value }.map { it.key }
            return IdentifyResult(
                barcode = barcode,
                brand = guessBrand(ocrText),
                model = guessModel(ocrText),
                category = guessCategory(labelScores),
                labels = labels.take(6),
                textSnippet = ocrText.lines().map { it.trim() }.filter { it.length >= 3 }.take(6).joinToString(" · "),
            )
        } finally {
            barcoder.close(); ocr.close(); labeler.close()
        }
    }

    companion object {
        private val PRODUCT_FORMATS = setOf(
            Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E,
        )

        val BRANDS = listOf(
            "Sony", "Samsung", "Apple", "iPhone", "iPad", "MacBook", "Xiaomi", "Redmi", "Poco", "Oppo", "Vivo", "Realme",
            "Infinix", "Tecno", "Huawei", "Asus", "Acer", "Lenovo", "HP", "Dell", "MSI", "Logitech", "Razer",
            "SteelSeries", "Rexus", "Fantech", "Canon", "Nikon", "Fujifilm", "Panasonic", "Olympus", "GoPro", "DJI",
            "LG", "Philips", "Sharp", "Toshiba", "Polytron", "Miyako", "Cosmos", "Maspion", "Sanken", "Kirin",
            "Midea", "Daikin", "Aqua", "Electrolux", "Modena", "Rinnai", "Dyson", "Cuckoo", "Oxone", "Tupperware",
            "Lock&Lock", "IKEA", "Informa", "Nintendo", "PlayStation", "Xbox", "JBL", "Bose", "Sennheiser",
            "Audio-Technica", "Marshall", "Anker", "Baseus", "Ugreen", "Garmin", "Amazfit", "Casio", "Seiko",
            "Fossil", "Kindle", "Lego", "Hot Wheels", "Bandai", "Tamiya", "Funko", "Nike", "Adidas", "Puma",
            "Uniqlo", "Zara", "H&M", "Converse", "Vans", "New Balance", "Reebok", "Asics", "Eiger", "Consina",
            "Erigo", "Bata", "Crocs", "Yamaha", "Roland", "Fender", "Polygon", "United", "Brompton", "Shimano",
            "Gramedia", "Mizone", "Lenovo Legion", "Zyrex", "Advan", "Evercoss", "Epson", "Brother", "TP-Link",
        )

        fun guessBrand(text: String): String? {
            if (text.isBlank()) return null
            val counts = BRANDS.associateWith { b ->
                Regex("(?<![\\p{L}])" + Regex.escape(b) + "(?![\\p{L}])", RegexOption.IGNORE_CASE).findAll(text).count()
            }.filterValues { it > 0 }
            return counts.maxByOrNull { it.value + it.key.length / 100.0 }?.key
        }

        private val modelRegex = Regex("""\b(?=[A-Z0-9-]*\d)(?=[A-Z0-9-]*[A-Z])[A-Z0-9]{1,6}(?:-?[A-Z0-9]{1,8}){0,2}\b""")
        private val noise = Regex("""^(\d+(V|W|MAH|MA|HZ|GB|TB|MM|CM|KG|G|ML|L|X)|[A-Z]?\d{1,2}|20\d\d|\d+[A-Z]{1,2}\d*)$""")

        fun guessModel(text: String): String? = modelRegex.findAll(text.uppercase())
            .map { it.value.trim() }
            .filter { it.length in 4..16 && !noise.matches(it.replace(" ", "")) }
            .groupingBy { it }.eachCount()
            .maxByOrNull { it.value * 10 + it.key.length }?.key

        private val CATEGORY_WORDS: Map<Category, List<String>> = mapOf(
            Category.ELECTRONICS to listOf("mobile phone", "computer", "laptop", "television", "monitor", "camera",
                "headphones", "electronic", "gadget", "keyboard", "screen", "speaker", "remote control", "tablet"),
            Category.FASHION to listOf("shoe", "sneakers", "jacket", "jeans", "shirt", "dress", "bag", "handbag",
                "hat", "sunglasses", "watch", "fashion", "clothing", "denim", "scarf", "skirt", "boot"),
            Category.BOOKS to listOf("book", "paper", "comics", "poster", "newspaper", "menu"),
            Category.TOYS to listOf("toy", "doll", "stuffed toy", "lego", "teddy bear", "balloon", "action figure", "game"),
            Category.HOME to listOf("furniture", "couch", "chair", "table", "lamp", "cushion", "curtain", "kitchen",
                "cookware", "bed", "shelf", "tableware", "cup", "plate", "vase", "houseplant", "pillow"),
            Category.HOBBY to listOf("bicycle", "guitar", "musical instrument", "sports", "racket", "skateboard",
                "fishing", "camping", "tent", "car", "motorcycle", "piano", "drum"),
        )

        fun guessCategory(labelScores: Map<String, Float>): Category? {
            val scores = mutableMapOf<Category, Float>()
            labelScores.forEach { (label, score) ->
                val l = label.lowercase()
                CATEGORY_WORDS.forEach { (cat, words) -> if (words.any { it in l }) scores[cat] = (scores[cat] ?: 0f) + score }
            }
            return scores.maxByOrNull { it.value }?.key
        }
    }
}

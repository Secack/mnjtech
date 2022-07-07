package su.akari.mnjtech.util

@JvmInline
value class FileSize(
    private val bytes: Long
) {
    override fun toString(): String =
        bytes.toDouble().let {
            with(
                if (it < 1024) {
                    it to "B"
                } else if (it < 1024 * 1024) {
                    it / 1024 to "KB"
                } else if (it < 1024 * 1024 * 1024) {
                    it / 1024 / 1024 to "MB"
                } else {
                    it / 1024 / 1024 / 1024 to "GB"
                }
            ) {
                "%.2f".format(first) + " " + second
            }
        }
}
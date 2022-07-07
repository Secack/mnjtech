package su.akari.mnjtech.data.model.profile

data class Profile(
    val id: String = "",
    val name: String = "",
    val term: SchoolTerm? = null
) {
    data class SchoolTerm(
        val year: Int,
        val term: Term,
        val week: Int
    )

    enum class Term(val value: Int) {
        FIRST(3),
        SECOND(12),
        THIRD(16)
    }

    companion object {
        val GUEST = Profile()
    }
}



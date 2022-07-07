package su.akari.mnjtech.data.model.online

data class VideoConfig(
    val categories: List<Category>,
    val languages: List<Language>,
    val regions: List<Region>,
    val tags: List<Tag>,
    val years: List<Year>
)

open class Config(
    @Transient open val id: Int,
    @Transient open val name: String
)

data class Category(
    val genre: String,
    override val id: Int,
    override val name: String
) : Config(id, name)

data class Language(
    override val id: Int,
    override val name: String
) : Config(id, name)

data class Region(
    override val id: Int,
    override val name: String
) : Config(id, name)

data class Tag(
    val genre: String,
    override val id: Int,
    override val name: String
) : Config(id, name)

data class Year(
    override val id: Int,
    override val name: String
) : Config(id, name)
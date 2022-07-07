package su.akari.mnjtech.data.model.jwgl

data class UserModel(
    val monitor: Boolean,
    val roleCount: Int,
    val roleKeys: String,
    val roleValues: String,
    val status: Int,
    val usable: Boolean
)
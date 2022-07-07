package su.akari.mnjtech.data.repo

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import su.akari.mnjtech.util.get

class PreferenceRepo(context: Context) {
    private val Context.store by preferencesDataStore(name = "preference")
    private val store = context.store

    val cookies by store.get("cookies", "{}")

    // Login
    val username by store.get("username", "")
    val password by store.get("password", "")
    val provider by store.get("provider", 0)
    val saveSession by store.get("save_session", false)
    val autoLogin by store.get("auto_login", false)

    // Curriculum
    val startDate by store.get("start_date", -1L)

    // Free room
    val building by store.get("building", "01")
    val classMin by store.get("class_min", 1)
    val classMax by store.get("class_max", 10)

    // Online
    val announcement by store.get("announcement", -1)
}
package su.akari.mnjtech.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.RawRes

val Context.maxBrightness
    get() = with(getSystemService(Context.POWER_SERVICE) as PowerManager) {
        javaClass.declaredFields.find { it.name == "BRIGHTNESS_ON" }?.let {
            it.isAccessible = true
            it.get(this) as Int
        } ?: 255
    }.toFloat()

fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Failed to find activity: ${this.javaClass.name}")
}

inline fun Context.toast(
    text: String,
    length: Int = Toast.LENGTH_SHORT,
    builder: Toast.() -> Unit = {}
) {
    Toast.makeText(this, text, length)
        .apply(builder)
        .show()
}

fun Context.stringResource(id: Int) = this.resources.getString(id)

fun Context.stringResource(id: Int, vararg formatArgs: Any) =
    this.resources.getString(id, *formatArgs)

fun Resources.getRawTextFile(@RawRes id: Int) =
    openRawResource(id).bufferedReader().use { it.readLines() }
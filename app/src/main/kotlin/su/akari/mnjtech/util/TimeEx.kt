package su.akari.mnjtech.util

import su.akari.mnjtech.data.model.jwgl.SectionTime
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

private val sdfDetail = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

fun Long.format(
    isUnix: Boolean = true, detail: Boolean = false
): String {
    val date = Date(if (isUnix) this * 1000 else this)
    return if (detail) {
        sdfDetail.format(date)
    } else {
        sdf.format(date)
    }
}

fun Long.prettyDuration(isSecond: Boolean = false): String {
    val seconds = if (isSecond) this else this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return when {
        hours > 0 -> "${hours.toString().padStart(2, '0')}:${
            (minutes % 60).toString().padStart(2, '0')
        }:${(seconds % 60).toString().padStart(2, '0')}"
        else -> "${minutes.toString().padStart(2, '0')}:${
            (seconds % 60).toString().padStart(2, '0')
        }"
    }
}

fun getHourDisplay() =
    when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0 until 5 -> "凌晨"
        in 5 until 8 -> "早上"
        in 8 until 12 -> "上午"
        in 12 until 13 -> "中午"
        in 13 until 19 -> "下午"
        in 19 until 24 -> "晚上"
        else -> throw  IllegalArgumentException()
    }

fun getDayOfWeekDisplay(dayOfWeek: Int, useAbbr: Boolean = false): String =
    DayOfWeek.of(dayOfWeek).getDisplayName(
        TextStyle.FULL,
        Locale.CHINA
    ).run { if (useAbbr) replace("星期", "周") else this }

fun calculateSectionTime(section: Int) {
    mutableListOf<SectionTime>().apply {
        //LocalDate.of()
        add(
            SectionTime(
                headTime = "08:10",
                sections = 1..4
            )
        )
        add(
            SectionTime(
                headTime = "14:00",
                sections = 5..8
            )
        )
        add(
            SectionTime(
                headTime = "18:20",
                sections = 9..10
            )
        )
    }.find {
        section in it.sections
    }!!.let {
        it.headTime to section - it.sections.first
    }.let {

    }
}
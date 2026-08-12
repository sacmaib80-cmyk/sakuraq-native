package com.sacmaib80.sakuraq.app.domain

import com.sacmaib80.sakuraq.app.ui.i18n.Strs
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToLong

object DateUtil {
    private val KEY_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val TH_MONTHS = listOf("ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค.")
    private val EN_MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    fun localDateKey(date: LocalDate = LocalDate.now()): String = date.format(KEY_FMT)

    fun addDays(dateKey: String, n: Long): String = LocalDate.parse(dateKey, KEY_FMT).plusDays(n).format(KEY_FMT)

    /** "3 ก.ค." / "3 Jul" */
    fun shortLabel(dateKey: String, lang: String = "th"): String {
        val d = LocalDate.parse(dateKey, KEY_FMT)
        val months = if (lang == "en") EN_MONTHS else TH_MONTHS
        return "${d.dayOfMonth} ${months[d.monthValue - 1]}"
    }

    /** index วันแบบ JS getDay(): อาทิตย์ = 0 */
    fun weekdayIndex(dateKey: String): Int {
        val d = LocalDate.parse(dateKey, KEY_FMT)
        return d.dayOfWeek.value % 7
    }

    private val TH_WEEK = listOf("อา", "จ", "อ", "พ", "พฤ", "ศ", "ส")

    fun weekdayLabel(dateKey: String): String = TH_WEEK[weekdayIndex(dateKey)]

    /** legacy Thai-only (จอเดิมเรียก) */
    fun timeAgo(ts: Long, now: Long = System.currentTimeMillis()): String {
        val min = floor((now - ts) / 60000.0).toLong()
        if (min < 1) return "เมื่อกี้"
        if (min < 60) return "$min นาทีก่อน"
        val hr = min / 60
        if (hr < 24) return "$hr ชม.ก่อน"
        return "${hr / 24} วันก่อน"
    }

    fun dayOfMonth(dateKey: String): Int = LocalDate.parse(dateKey, KEY_FMT).dayOfMonth

    fun monthValue(dateKey: String): Int = LocalDate.parse(dateKey, KEY_FMT).monthValue

    fun isToday(dateKey: String): Boolean = dateKey == localDateKey()

    fun fmtClock(totalSeconds: Long): String {
        val s = max(0L, totalSeconds)
        val mm = (s / 60).toString().padStart(2, '0')
        val ss = (s % 60).toString().padStart(2, '0')
        return "$mm:$ss"
    }

    fun timeAgo(ts: Long, s: Strs, now: Long = System.currentTimeMillis()): String {
        val min = floor((now - ts) / 60000.0).toLong()
        if (min < 1) return s.agoJustNow
        if (min < 60) return s.agoMin.tmpl("n" to min)
        val hr = min / 60
        if (hr < 24) return s.agoHour.tmpl("n" to hr)
        return s.agoDay.tmpl("n" to hr / 24)
    }

    // ── HH:MM (ตาราง Plan) ──

    fun parseHHMMtoMin(hhmm: String): Int {
        val p = hhmm.split(":")
        if (p.size != 2) return 0
        return (p[0].toIntOrNull() ?: 0) * 60 + (p[1].toIntOrNull() ?: 0)
    }

    fun minToHHMM(min: Int): String {
        val m = ((min % (24 * 60)) + 24 * 60) % (24 * 60)
        return "${(m / 60).toString().padStart(2, '0')}:${(m % 60).toString().padStart(2, '0')}"
    }

    fun endHHMM(startHHMM: String, durationMin: Int): String =
        minToHHMM(parseHHMMtoMin(startHHMM) + durationMin)

    fun nowHHMM(): String {
        val c = java.util.Calendar.getInstance()
        return "${c.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${c.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')}"
    }
}

/** Math.round(n*10)/10 parity กับ JS */
fun round1(n: Double): Double = (n * 10).roundToLong() / 10.0

/** แสดงคะแนนแบบเดียวกับเว็บ: จำนวนเต็มไม่มีทศนิยม, มีเศษโชว์ 1 ตำแหน่ง */
fun fmtScore(n: Double): String {
    val r = round1(n)
    return if (r == floor(r)) r.toLong().toString() else String.format("%.1f", r)
}

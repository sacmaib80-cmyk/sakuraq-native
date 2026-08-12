package com.sacmaib80.sakuraq.app.domain

import com.sacmaib80.sakuraq.app.data.DayData
import kotlin.math.max
import kotlin.math.min

/** ผลคะแนนของเควสหนึ่งครั้ง */
data class QuestScore(
    val baseMinutesUsed: Int,
    val baseScore: Double,
    val bonus: Int,
    val questScore: Double,
)

/** ประเภทรายงานที่ผู้ใช้เลือก (ตาม flow ของแอปจริง: ปกติ / เปลี่ยนวิธีทำ / ยืนยันพฤติกรรม) */
enum class ReportMode { NORMAL, METHOD, BEHAVIOR }

data class BonusInputs(
    val note: String = "",
    val from: String = "",
    val to: String = "",
    val proof: String = "",
    val mode: ReportMode = ReportMode.NORMAL,
)

object ScoreEngine {

    private val CHAR_RUN = Regex("(.)\\1{5,}")
    // JS ใช้ \b ซึ่งไม่เวิร์กกับอักษรไทยบน JVM — (\S+) ให้ผลเทียบเท่าในเคสจริง
    private val WORD_REPEAT = Regex("(\\S+)(\\s+\\1){2,}", RegexOption.IGNORE_CASE)

    fun isLowQualityText(text: String?): Boolean {
        val t = (text ?: "").trim()
        if (t.isEmpty()) return true
        if (CHAR_RUN.containsMatchIn(t)) return true
        if (WORD_REPEAT.containsMatchIn(t)) return true
        return false
    }

    /**
     * Bonus tiers — เฉพาะเมื่อ minutes >= 25 และ note หลักผ่านคุณภาพ (>= 20 ตัวอักษร)
     *  NORMAL   → +2
     *  METHOD   → +4 เมื่อ from >= 8 และ to >= 8 ผ่านคุณภาพ (ไม่ผ่านตกมาที่ +2)
     *  BEHAVIOR → +6 เมื่อ proof >= 10 ผ่านคุณภาพ (ไม่ผ่านตกมาที่ +2)
     */
    fun calcBonus(minutes: Int, inputs: BonusInputs): Int {
        if (minutes < Rules.BONUS_MIN_MINUTES) return 0

        val note = inputs.note.trim()
        val notePass = note.length >= 20 && !isLowQualityText(note)
        if (!notePass) return 0

        return when (inputs.mode) {
            ReportMode.NORMAL -> 2
            ReportMode.METHOD -> {
                val from = inputs.from.trim()
                val to = inputs.to.trim()
                val pass = from.length >= 8 && to.length >= 8 &&
                    !isLowQualityText(from) && !isLowQualityText(to)
                if (pass) 4 else 2
            }
            ReportMode.BEHAVIOR -> {
                val proof = inputs.proof.trim()
                val pass = proof.length >= 10 && !isLowQualityText(proof)
                if (pass) 6 else 2
            }
        }
    }

    fun normName(s: String?): String = (s ?: "").trim().lowercase()

    /** นาที base ที่ใช้ไปแล้ววันนี้ของเควสชื่อเดียวกัน (สำหรับ cap 60/ชื่อ/วัน) */
    fun baseMinutesUsedToday(day: DayData?, questName: String): Int {
        if (day == null) return 0
        val qn = normName(questName)
        return day.quests.filter { normName(it.questName) == qn }.sumOf { it.baseMinutesUsed }
    }

    fun scoreQuest(day: DayData?, questName: String, minutes: Int, inputs: BonusInputs): QuestScore {
        val used = baseMinutesUsedToday(day, questName)
        val remaining = max(0, Rules.BASE_CAP_PER_QUEST_NAME_PER_DAY - used)
        val baseMinutesUsed = min(max(0, minutes), remaining)
        val baseScore = round1(baseMinutesUsed * Rules.BASE_PER_MINUTE)
        val bonus = calcBonus(minutes, inputs)
        val questScore = round1(baseScore + bonus)
        return QuestScore(baseMinutesUsed, baseScore, bonus, questScore)
    }

    // ── Day / lifetime aggregation ──

    fun dayGross(day: DayData?): Double {
        if (day == null) return 0.0
        return round1(day.quests.sumOf { it.questScore })
    }

    /** ไม่มี penalties/gateBonus ในตัว native → effective = gross */
    fun dayEffective(day: DayData?): Double = dayGross(day)

    fun lifetimePoints(days: Map<String, DayData>): Double =
        round1(days.values.sumOf { dayEffective(it) })

    fun levelFromPoints(points: Double): Int = (points / Rules.POINTS_PER_LEVEL).toInt() + 1

    /** แต้มในเลเวลปัจจุบัน (0..100) */
    fun levelProgress(points: Double): Double = round1(points % Rules.POINTS_PER_LEVEL)

    /** วันติดกันที่มีเควส ≥1 (วันนี้ หรือค้างจากเมื่อวานถ้าวันนี้ยังว่าง) */
    fun computeStreak(days: Map<String, DayData>, todayKey: String): Int {
        fun has(k: String): Boolean = days[k]?.quests?.isNotEmpty() == true
        var cursor = todayKey
        if (!has(cursor)) cursor = DateUtil.addDays(todayKey, -1)
        var count = 0
        while (has(cursor)) {
            count++
            cursor = DateUtil.addDays(cursor, -1)
        }
        return count
    }

    /** เกรดหน้า Today (พอร์ตจาก calcTpGrade ของเว็บ): S≥90 A≥80 B≥60 C≥40 D≥20 F */
    fun tpGrade(score: Double): Pair<String, Long> = when {
        score >= 90 -> "S" to 0xFFF59E0B
        score >= 80 -> "A" to 0xFF22C55E
        score >= 60 -> "B" to 0xFF3B82F6
        score >= 40 -> "C" to 0xFF8B5CF6
        score >= 20 -> "D" to 0xFFF97316
        else -> "F" to 0xFFEF4444
    }
}

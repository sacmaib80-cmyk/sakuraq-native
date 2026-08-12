package com.sacmaib80.sakuraq.app

import com.sacmaib80.sakuraq.app.data.DayData
import com.sacmaib80.sakuraq.app.data.QuestEntry
import com.sacmaib80.sakuraq.app.domain.BonusInputs
import com.sacmaib80.sakuraq.app.domain.ReportMode
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.domain.round1
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreEngineTest {

    private val goodNote = "วันนี้ได้เรียนรู้เรื่อง Kotlin coroutines เยอะมาก" // > 20 chars
    private val goodFrom = "เดิมงงมากเลย" // >= 8 chars
    private val goodTo = "ตอนนี้เข้าใจแล้ว" // >= 8
    private val goodProof = "เขียนโค้ดตัวอย่างได้เอง 3 ไฟล์" // >= 10

    // ── low-quality text ──

    @Test fun lowQuality_blank() {
        assertTrue(ScoreEngine.isLowQualityText(""))
        assertTrue(ScoreEngine.isLowQualityText("   "))
        assertTrue(ScoreEngine.isLowQualityText(null))
    }

    @Test fun lowQuality_charRun() {
        assertTrue(ScoreEngine.isLowQualityText("555555"))
        assertTrue(ScoreEngine.isLowQualityText("aaaaaaaa"))
        assertFalse(ScoreEngine.isLowQualityText("55555")) // 5 ตัว = run 4 ซ้ำ ยังไม่ติด {5,}
    }

    @Test fun lowQuality_wordRepeat() {
        assertTrue(ScoreEngine.isLowQualityText("ดี ดี ดี"))
        assertTrue(ScoreEngine.isLowQualityText("work work work"))
        assertTrue(ScoreEngine.isLowQualityText("Work WORK work")) // case-insensitive
        assertFalse(ScoreEngine.isLowQualityText("ดี ดี"))
        assertFalse(ScoreEngine.isLowQualityText(goodNote))
    }

    // ── bonus tiers ──

    @Test fun bonus_zeroUnder25Minutes() {
        assertEquals(0, ScoreEngine.calcBonus(24, BonusInputs(note = goodNote, from = goodFrom, to = goodTo, proof = goodProof, mode = ReportMode.BEHAVIOR)))
    }

    @Test fun bonus_zeroWhenNoteTooShort() {
        assertEquals(0, ScoreEngine.calcBonus(30, BonusInputs(note = "สั้นไป")))
    }

    @Test fun bonus_zeroWhenNoteLowQuality() {
        assertEquals(0, ScoreEngine.calcBonus(30, BonusInputs(note = "5555555555555555555555")))
    }

    @Test fun bonus_plus2_normalMode() {
        assertEquals(2, ScoreEngine.calcBonus(25, BonusInputs(note = goodNote, mode = ReportMode.NORMAL)))
    }

    @Test fun bonus_plus4_methodMode() {
        assertEquals(4, ScoreEngine.calcBonus(25, BonusInputs(note = goodNote, from = goodFrom, to = goodTo, mode = ReportMode.METHOD)))
    }

    @Test fun bonus_plus6_behaviorMode() {
        assertEquals(6, ScoreEngine.calcBonus(25, BonusInputs(note = goodNote, proof = goodProof, mode = ReportMode.BEHAVIOR)))
    }

    @Test fun bonus_methodFallsTo2WhenFromTooShort() {
        assertEquals(2, ScoreEngine.calcBonus(25, BonusInputs(note = goodNote, from = "สั้น", to = goodTo, mode = ReportMode.METHOD)))
    }

    @Test fun bonus_behaviorFallsTo2WhenProofLowQuality() {
        assertEquals(2, ScoreEngine.calcBonus(25, BonusInputs(note = goodNote, proof = "aaaaaaaaaaaa", mode = ReportMode.BEHAVIOR)))
    }

    // ── base score + cap ──

    private fun entry(name: String, baseMin: Int) = QuestEntry(
        ts = 1L, dateKey = "2026-07-04", questName = name, baseMinutesUsed = baseMin,
    )

    @Test fun score_basic25Min() {
        val r = ScoreEngine.scoreQuest(null, "อ่านหนังสือ", 25, BonusInputs())
        assertEquals(25, r.baseMinutesUsed)
        assertEquals(10.0, r.baseScore, 0.0)
        assertEquals(0, r.bonus)
        assertEquals(10.0, r.questScore, 0.0)
    }

    @Test fun score_25MinWithNote_is12() {
        val r = ScoreEngine.scoreQuest(null, "อ่านหนังสือ", 25, BonusInputs(note = goodNote))
        assertEquals(12.0, r.questScore, 0.0) // เคสอ้างอิงจาก React verify: 25 นาที + note = 12.0
    }

    @Test fun cap_sameNameSameDay() {
        val day = DayData(quests = listOf(entry("อ่านหนังสือ", 40)))
        val r = ScoreEngine.scoreQuest(day, "อ่านหนังสือ", 30, BonusInputs())
        assertEquals(20, r.baseMinutesUsed) // เหลือ 60-40
        assertEquals(8.0, r.baseScore, 0.0)
    }

    @Test fun cap_caseAndSpaceInsensitive() {
        val day = DayData(quests = listOf(entry("  อ่านหนังสือ ", 60)))
        val r = ScoreEngine.scoreQuest(day, "อ่านหนังสือ", 30, BonusInputs())
        assertEquals(0, r.baseMinutesUsed)
        assertEquals(0.0, r.baseScore, 0.0)
    }

    @Test fun cap_englishCase() {
        val day = DayData(quests = listOf(entry("Read Book", 50)))
        val r = ScoreEngine.scoreQuest(day, "read book", 30, BonusInputs())
        assertEquals(10, r.baseMinutesUsed)
    }

    @Test fun cap_bonusStillAppliesWhenBaseCapped() {
        val day = DayData(quests = listOf(entry("อ่านหนังสือ", 60)))
        val r = ScoreEngine.scoreQuest(day, "อ่านหนังสือ", 30, BonusInputs(note = goodNote))
        assertEquals(0.0, r.baseScore, 0.0)
        assertEquals(2, r.bonus) // โบนัสคิดจาก minutes จริง ไม่ใช่ base ที่เหลือ (ตาม reference)
        assertEquals(2.0, r.questScore, 0.0)
    }

    // ── aggregation ──

    @Test fun round1_parity() {
        assertEquals(10.0, round1(9.96), 0.0)
        assertEquals(0.4, round1(0.4000001), 0.0)
        assertEquals(12.4, round1(12.44), 0.0)
    }

    @Test fun level_and_progress() {
        assertEquals(1, ScoreEngine.levelFromPoints(0.0))
        assertEquals(1, ScoreEngine.levelFromPoints(99.9))
        assertEquals(2, ScoreEngine.levelFromPoints(100.0))
        assertEquals(49, ScoreEngine.levelFromPoints(4816.0))
        assertEquals(16.0, ScoreEngine.levelProgress(4816.0), 0.0)
    }

    @Test fun dayGross_sums() {
        val day = DayData(
            quests = listOf(
                QuestEntry(ts = 1, dateKey = "2026-07-04", questScore = 10.0),
                QuestEntry(ts = 2, dateKey = "2026-07-04", questScore = 2.4),
            )
        )
        assertEquals(12.4, ScoreEngine.dayGross(day), 0.0)
    }

    // ── streak ──

    private fun dayWithQuest() = DayData(quests = listOf(QuestEntry(ts = 1, dateKey = "x")))

    @Test fun streak_endingToday() {
        val days = mapOf(
            "2026-07-04" to dayWithQuest(),
            "2026-07-03" to dayWithQuest(),
            "2026-07-02" to dayWithQuest(),
        )
        assertEquals(3, ScoreEngine.computeStreak(days, "2026-07-04"))
    }

    @Test fun streak_holdsFromYesterday() {
        val days = mapOf(
            "2026-07-03" to dayWithQuest(),
            "2026-07-02" to dayWithQuest(),
        )
        assertEquals(2, ScoreEngine.computeStreak(days, "2026-07-04"))
    }

    @Test fun streak_brokenByGap() {
        val days = mapOf(
            "2026-07-04" to dayWithQuest(),
            "2026-07-02" to dayWithQuest(),
        )
        assertEquals(1, ScoreEngine.computeStreak(days, "2026-07-04"))
    }

    @Test fun streak_zero() {
        assertEquals(0, ScoreEngine.computeStreak(emptyMap(), "2026-07-04"))
    }
}

package com.sacmaib80.sakuraq.app.data

import kotlinx.serialization.Serializable

@Serializable
enum class SessionStatus { IDLE, RUNNING, PAUSED, REPORTING }

@Serializable
data class Session(
    val status: SessionStatus = SessionStatus.IDLE,
    val category: String? = null,
    val questName: String = "",
    val plannedMin: Int = 25,
    val targetEndTs: Long = 0L,
    val remainingMs: Long = 0L,
    val elapsedMs: Long = 0L,
    val startedAt: Long = 0L,
)

@Serializable
data class QuestEntry(
    val ts: Long,
    val dateKey: String,
    val category: String? = null,
    val questName: String = "",
    val minutesSpent: Int = 0,
    val baseMinutesUsed: Int = 0,
    val baseScore: Double = 0.0,
    val bonus: Int = 0,
    val questScore: Double = 0.0,
    val note: String = "",
    val why: String = "",
    val from: String = "",
    val to: String = "",
)

@Serializable
data class DayData(
    val quests: List<QuestEntry> = emptyList(),
)

/** การ์ดแผน (หน้า Plan — เขียนวันนี้ไว้ทำพรุ่งนี้ ตามเว็บ) */
@Serializable
data class Plan(
    val id: String,
    val dateKey: String,
    val name: String,
    val category: String,
    val durationMin: Int,
    val startHHMM: String,
    val done: Boolean = false,
)

/** สถานะลูกเต๋าสุ่มเควส: cooldown 30 นาที + กันสุ่มชื่อซ้ำในวันเดียว + ข้อเสนอที่ค้างอยู่ */
@Serializable
data class DiceState(
    val cooldownUntil: Long = 0L,
    val rolledDateKey: String = "",
    val rolledNames: List<String> = emptyList(),
    val offerName: String = "",
    val offerCategory: String = "",
    val offerDuration: Int = 0,
    val offerEnergy: String = "",
) {
    val hasOffer: Boolean get() = offerName.isNotBlank()
}

@Serializable
data class Prefs(
    val language: String = "th",
)

@Serializable
data class AppData(
    val version: Int = 2,
    val days: Map<String, DayData> = emptyMap(),
    val questNameHistory: List<String> = emptyList(),
    val session: Session = Session(),
    val profileName: String = "",
    val plans: List<Plan> = emptyList(),
    val dice: DiceState = DiceState(),
    val prefs: Prefs = Prefs(),
)

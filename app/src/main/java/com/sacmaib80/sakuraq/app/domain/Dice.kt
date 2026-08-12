package com.sacmaib80.sakuraq.app.domain

import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.ui.i18n.Strs
import com.sacmaib80.sakuraq.app.ui.i18n.StringsEn
import com.sacmaib80.sakuraq.app.ui.i18n.StringsTh
import kotlin.random.Random

enum class Energy(val key: String) { LOW("low"), MID("mid"), HIGH("high") }

data class DiceQuest(
    val name: String,
    val category: String,
    val duration: Int,
    val energy: Energy,
)

/**
 * เอนจินสุ่มเควส — พอร์ตแกน SQ_DICE_GEN จากแอปจริง:
 * เลือกพลังงาน → เลือกวิธี (ทำซ้ำเควสเดิม ~40% / สร้างใหม่ตามหมวด) → กันซ้ำกับที่ทำ/สุ่มไปแล้ววันนี้
 */
object DiceGen {

    const val COOLDOWN_MS = 30 * 60 * 1000L

    private val DURATIONS = mapOf(
        Energy.LOW to listOf(10, 15, 20),
        Energy.MID to listOf(30, 35, 40),
        Energy.HIGH to listOf(45, 50, 55, 60),
    )

    // category เรียง index ตรงกับ rq.fallback.<energy>
    private val FALLBACK_CAT = mapOf(
        Energy.LOW to listOf("Learning", "Physical", "Work/Project", "Work/Project"),
        Energy.MID to listOf("Learning", "Physical", "Work/Project", "Work/Project"),
        Energy.HIGH to listOf("Work/Project", "Work/Project", "Physical", "Learning"),
    )

    private val KW_LANGUAGE = listOf(
        "eng", "english", "chinese", "japan", "japanese", "korea", "korean", "vocab", "sentence",
        "reading", "listening", "speaking", "grammar", "ภาษา", "ศัพท์", "ประโยค", "อังกฤษ", "จีน", "ญี่ปุ่น", "เกาหลี",
    )
    private val KW_MATH = listOf(
        "math", "algebra", "equation", "calculus", "stats", "logic", "dp",
        "สมการ", "สถิติ", "ฟังก์ชัน", "ตรรกะ", "โจทย์", "พิสูจน์", "คณิต", "เลข", "ตัวเลข",
    )

    private val TRAILING_INDEX = Regex("\\s+(\\d{1,3})$")

    // ── helpers ──

    private fun <T> pickOne(list: List<T>, random: Random): T = list[random.nextInt(list.size)]

    private fun pickDuration(energy: Energy, random: Random): Int = pickOne(DURATIONS.getValue(energy), random)

    private fun stripTrailingIndex(name: String): String = name.trim().replace(TRAILING_INDEX, "").trim()

    /** จับคู่ substring กับ pool ทั้ง th และ en → index เดียวกัน (history ภาษาไหนก็ map ได้) */
    private fun matchIndex(name: String, th: List<String>, en: List<String>): Int {
        val n = name.lowercase()
        th.forEachIndexed { i, v -> if (n.contains(v.lowercase())) return i }
        en.forEachIndexed { i, v -> if (n.contains(v.lowercase())) return i }
        return -1
    }

    private fun questsInRange(data: AppData, todayKey: String, lookbackDays: Int): List<Pair<String, com.sacmaib80.sakuraq.app.data.QuestEntry>> {
        val out = mutableListOf<Pair<String, com.sacmaib80.sakuraq.app.data.QuestEntry>>()
        for (i in 0 until lookbackDays) {
            val dk = DateUtil.addDays(todayKey, -i.toLong())
            data.days[dk]?.quests?.forEach { out.add(dk to it) }
        }
        return out
    }

    /** ชื่อที่ผู้ใช้ตั้งเอง (ไม่ใช่ของลูกเต๋า "•") ของหมวดนั้นใน 14 วัน → ใช้เป็น domain */
    private fun historyDomains(data: AppData, todayKey: String, category: String): List<String> =
        questsInRange(data, todayKey, 14)
            .filter { it.second.category == category }
            .map { stripTrailingIndex(it.second.questName) }
            .filter { it.isNotBlank() && !it.contains("•") }
            .distinctBy { it.lowercase() }

    private fun classifyLearning(domain: String): String {
        val d = domain.lowercase()
        if (KW_LANGUAGE.any { d.contains(it) }) return "language"
        if (KW_MATH.any { d.contains(it) }) return "math"
        return "generic"
    }

    private fun learnPool(s: Strs, type: String): List<String> = when (type) {
        "language" -> s.rqLearnLanguage
        "math" -> s.rqLearnMath
        else -> s.rqLearnGeneric
    }

    /** อวัยวะที่เพิ่งเล่นไป 2 วัน (กันสุ่มซ้ำส่วนเดิม) */
    private fun recentPhysicalParts(data: AppData, todayKey: String): Set<Int> =
        questsInRange(data, todayKey, 2)
            .filter { it.second.category == "Physical" }
            .map { matchIndex(it.second.questName, StringsTh.rqBodypart, StringsEn.rqBodypart) }
            .filter { it >= 0 }
            .toSet()

    /** workflow progression: งาน domain เดิมขยับสเต็ปถัดไป (วางโครง → ลุย → ตรวจ → …) */
    private fun progressedWorkAction(data: AppData, todayKey: String, domain: String, s: Strs, random: Random): String {
        val flow = s.rqWorkflow
        val last = questsInRange(data, todayKey, 7)
            .filter { it.second.category == "Work/Project" && it.second.questName.lowercase().startsWith(domain.lowercase()) }
            .maxByOrNull { it.second.ts }
        val lastIdx = last?.let { matchIndex(it.second.questName, StringsTh.rqWorkflow, StringsEn.rqWorkflow) } ?: -1
        return if (lastIdx >= 0) flow[(lastIdx + 1) % flow.size] else flow[random.nextInt(2)]
    }

    private class Weighted<T>(val value: T, val weight: Double)

    private fun <T> weightedPick(items: List<Weighted<T>>, random: Random): T? {
        val total = items.sumOf { it.weight }
        if (total <= 0) return null
        var r = random.nextDouble() * total
        items.forEach {
            r -= it.weight
            if (r <= 0) return it.value
        }
        return items.last().value
    }

    // ── builders (พอร์ตจาก rqBuild*Quest) ──

    private fun buildLearning(data: AppData, todayKey: String, energy: Energy, s: Strs, random: Random): DiceQuest {
        val domains = historyDomains(data, todayKey, "Learning")
        val domain = if (domains.isNotEmpty() && random.nextDouble() < 0.6) pickOne(domains, random)
        else pickOne(s.rqLearnDomains, random)
        val act = pickOne(learnPool(s, classifyLearning(domain)), random)
        return DiceQuest("$domain • $act", "Learning", pickDuration(energy, random), energy)
    }

    private fun buildWork(data: AppData, todayKey: String, energy: Energy, s: Strs, random: Random): DiceQuest {
        val domains = historyDomains(data, todayKey, "Work/Project")
        val domain = if (domains.isNotEmpty() && random.nextDouble() < 0.6) pickOne(domains, random)
        else pickOne(s.rqWorkDomains, random)
        val action = progressedWorkAction(data, todayKey, domain, s, random)
        return DiceQuest("$domain • $action", "Work/Project", pickDuration(energy, random), energy)
    }

    private fun buildPhysical(data: AppData, todayKey: String, energy: Energy, s: Strs, random: Random): DiceQuest {
        val duration = pickDuration(energy, random)
        val parts = s.rqBodypart
        fun cardio() = DiceQuest("Physical • ${pickOne(s.rqCardio, random)}", "Physical", duration, energy)
        fun body(): DiceQuest {
            val recent = recentPhysicalParts(data, todayKey)
            val candidates = parts.indices.filter { it !in recent }.ifEmpty { parts.indices.toList() }
            return DiceQuest("Physical • ${parts[pickOne(candidates, random)]}", "Physical", duration, energy)
        }
        return when (energy) {
            Energy.LOW -> cardio()
            Energy.MID -> if (random.nextDouble() < 0.75) body() else cardio()
            Energy.HIGH -> body()
        }
    }

    private fun buildConnection(energy: Energy, s: Strs, random: Random): DiceQuest =
        DiceQuest("Connection • ${pickOne(s.rqConnection, random)}", "Connection", pickOne(listOf(10, 15), random), energy)

    private fun buildFallback(energy: Energy, s: Strs, random: Random): DiceQuest {
        val list = when (energy) {
            Energy.LOW -> s.rqFallbackLow
            Energy.MID -> s.rqFallbackMid
            Energy.HIGH -> s.rqFallbackHigh
        }
        val i = random.nextInt(list.size)
        return DiceQuest(list[i], FALLBACK_CAT.getValue(energy)[i], pickDuration(energy, random), energy)
    }

    /** ทำซ้ำเควสที่ผู้ใช้เคยตั้งเอง — ชื่อรันเลขต่อจากของวันนี้ ("อ่านหนังสือ" → "อ่านหนังสือ 2") */
    private fun buildRepeat(data: AppData, todayKey: String, energy: Energy, random: Random): DiceQuest? {
        val pool = questsInRange(data, todayKey, 14)
            .map { it.second }
            .filter { !it.questName.contains("•") && it.questName.isNotBlank() }
            .groupBy { stripTrailingIndex(it.questName).lowercase() }
            .mapNotNull { (_, entries) ->
                val base = stripTrailingIndex(entries.maxByOrNull { it.ts }!!.questName)
                if (base.isBlank()) null
                else Triple(base, entries.first().category ?: "Learning", entries.size)
            }
        if (pool.isEmpty()) return null
        val picked = weightedPick(pool.map { Weighted(it, it.third.toDouble()) }, random) ?: return null
        val (base, category, _) = picked

        var maxIdx = 0
        data.days[todayKey]?.quests?.forEach { q ->
            val qBase = stripTrailingIndex(q.questName)
            if (qBase.equals(base, ignoreCase = true)) {
                val m = TRAILING_INDEX.find(q.questName.trim())
                maxIdx = maxOf(maxIdx, m?.groupValues?.get(1)?.toIntOrNull() ?: 1)
            }
        }
        val name = if (maxIdx == 0) base else "$base ${maxIdx + 1}"
        return DiceQuest(name, category, pickDuration(energy, random), energy)
    }

    // ── category weighting (rqPickCategory) ──

    private fun pickCategory(data: AppData, todayKey: String, energy: Energy, hour: Int, random: Random): String {
        val recent = questsInRange(data, todayKey, 3).groupingBy { it.second.category ?: "" }.eachCount()
        val yesterdayConn = data.days[DateUtil.addDays(todayKey, -1)]?.quests?.any { it.category == "Connection" } == true

        val weighted = CATEGORIES.map { cat ->
            var w = 1.0
            when (recent[cat.id] ?: 0) {
                0 -> {}
                1 -> w *= 0.72
                else -> w *= 0.55
            }
            if (cat.id == "Connection") {
                if (energy != Energy.LOW || hour !in 9..21) w = 0.0
                else if (yesterdayConn) w *= 0.35
            }
            if (energy == Energy.LOW && cat.id == "Physical") w *= 0.85
            Weighted(cat.id, w)
        }
        return weightedPick(weighted, random) ?: "Learning"
    }

    // ── main roll ──

    fun roll(
        data: AppData,
        energy: Energy,
        s: Strs,
        todayKey: String = DateUtil.localDateKey(),
        hour: Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY),
        random: Random = Random.Default,
    ): DiceQuest {
        val doneSet = buildSet {
            data.days[todayKey]?.quests?.forEach { add(ScoreEngine.normName(it.questName)) }
            if (data.dice.rolledDateKey == todayKey) data.dice.rolledNames.forEach { add(ScoreEngine.normName(it)) }
        }

        repeat(8) {
            val candidate = if (random.nextDouble() < 0.4) {
                buildRepeat(data, todayKey, energy, random)
                    ?: buildByCategory(data, todayKey, energy, s, hour, random)
            } else {
                buildByCategory(data, todayKey, energy, s, hour, random)
            }
            if (ScoreEngine.normName(candidate.name) !in doneSet) return candidate
        }
        return buildFallback(energy, s, random)
    }

    private fun buildByCategory(data: AppData, todayKey: String, energy: Energy, s: Strs, hour: Int, random: Random): DiceQuest =
        when (pickCategory(data, todayKey, energy, hour, random)) {
            "Learning" -> buildLearning(data, todayKey, energy, s, random)
            "Work/Project" -> buildWork(data, todayKey, energy, s, random)
            "Physical" -> buildPhysical(data, todayKey, energy, s, random)
            "Connection" -> buildConnection(energy, s, random)
            else -> buildFallback(energy, s, random)
        }
}

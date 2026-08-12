package com.sacmaib80.sakuraq.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sacmaib80.sakuraq.app.data.AppData
import com.sacmaib80.sakuraq.app.data.DayData
import com.sacmaib80.sakuraq.app.data.DiceState
import com.sacmaib80.sakuraq.app.data.Plan
import com.sacmaib80.sakuraq.app.data.QuestEntry
import com.sacmaib80.sakuraq.app.data.QuestRepository
import com.sacmaib80.sakuraq.app.data.Session
import com.sacmaib80.sakuraq.app.data.SessionStatus
import com.sacmaib80.sakuraq.app.domain.BonusInputs
import com.sacmaib80.sakuraq.app.domain.DateUtil
import com.sacmaib80.sakuraq.app.domain.DiceGen
import com.sacmaib80.sakuraq.app.domain.Energy
import com.sacmaib80.sakuraq.app.domain.ReportMode
import com.sacmaib80.sakuraq.app.domain.Rules
import com.sacmaib80.sakuraq.app.domain.ScoreEngine
import com.sacmaib80.sakuraq.app.notify.QuestNotify
import com.sacmaib80.sakuraq.app.ui.i18n.strsOf
import com.sacmaib80.sakuraq.app.ui.i18n.tmpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class Tab { HOME, TODAY, HISTORY, PLAN, MENU }

sealed class Overlay {
    data object None : Overlay()
    data object QuestFlow : Overlay()
    data object Run : Overlay()
    data object Report : Overlay()
    data object Dice : Overlay()
    data class Celebration(val result: QuestResult) : Overlay()
}

data class QuestResult(
    val questScore: Double,
    val baseScore: Double,
    val bonus: Int,
    val questName: String,
    val category: String?,
    val dayTotalBefore: Double,
    val dayTotalAfter: Double,
)

enum class PlanError { NAME_EXISTS, OVERLAP }

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = QuestRepository(application)

    // in-memory = source of truth (mutation synchronous ไม่มี race), DataStore = persistence side effect
    private val _app = MutableStateFlow(AppData())
    val app: StateFlow<AppData> = _app.asStateFlow()

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

    private val _tab = MutableStateFlow(Tab.HOME)
    val tab: StateFlow<Tab> = _tab.asStateFlow()

    private val _overlay = MutableStateFlow<Overlay>(Overlay.None)
    val overlay: StateFlow<Overlay> = _overlay.asStateFlow()

    private val _nowMs = MutableStateFlow(System.currentTimeMillis())
    val nowMs: StateFlow<Long> = _nowMs.asStateFlow()

    private var tickerJob: Job? = null

    init {
        viewModelScope.launch {
            // restore session หลัง process death — kill กลางเควสแล้วเปิดใหม่ต้องกลับมาจอเดิม
            val first = repo.data.first()
            _app.value = first
            when (first.session.status) {
                SessionStatus.RUNNING, SessionStatus.PAUSED -> {
                    _overlay.value = Overlay.Run
                    if (first.session.status == SessionStatus.RUNNING) scheduleDoneNoti(first.session)
                }
                SessionStatus.REPORTING -> _overlay.value = Overlay.Report
                else -> {}
            }
            _loaded.value = true
        }
        viewModelScope.launch {
            app.collect { data ->
                if (data.session.status == SessionStatus.RUNNING) startTicker() else stopTicker()
            }
        }
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (true) {
                _nowMs.value = System.currentTimeMillis()
                delay(250)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    // ── navigation ──

    fun setTab(t: Tab) { _tab.value = t }
    fun openQuestFlow() { _overlay.value = Overlay.QuestFlow }
    fun openDice() { _overlay.value = Overlay.Dice }
    fun closeOverlay() { _overlay.value = Overlay.None }

    /** mutate ทันที (synchronous) แล้วค่อยเซฟลง DataStore เบื้องหลัง */
    private fun persist(transform: (AppData) -> AppData) {
        val next = transform(_app.value)
        if (next === _app.value) return
        _app.value = next
        viewModelScope.launch { repo.save(next) }
    }

    // ── language / profile ──

    fun setLanguage(lang: String) {
        persist { it.copy(prefs = it.prefs.copy(language = lang)) }
    }

    fun setProfileName(name: String) {
        persist { it.copy(profileName = name.trim().take(20)) }
    }

    // ── notifications ──

    private fun scheduleDoneNoti(session: Session) {
        val s = strsOf(_app.value.prefs.language)
        val name = session.questName.ifBlank { "Quest" }
        QuestNotify.ensureChannel(getApplication(), s.notiChannelName)
        QuestNotify.schedule(
            getApplication(),
            session.targetEndTs,
            s.notiDoneTitle.tmpl("name" to name),
            s.notiDoneBody,
            s.notiChannelName,
        )
    }

    private fun cancelDoneNoti() = QuestNotify.cancel(getApplication())

    // ── quest lifecycle ──

    fun startQuest(category: String, questName: String, plannedMin: Int) {
        val minVal = max(1, plannedMin)
        val now = System.currentTimeMillis()
        val session = Session(
            status = SessionStatus.RUNNING,
            category = category,
            questName = questName.trim(),
            plannedMin = minVal,
            targetEndTs = now + minVal * 60000L,
            remainingMs = minVal * 60000L,
            elapsedMs = 0L,
            startedAt = now,
        )
        persist { it.copy(session = session) }
        scheduleDoneNoti(session)
        _overlay.value = Overlay.Run
    }

    fun pauseQuest() {
        cancelDoneNoti()
        persist { data ->
            val s = data.session
            if (s.status != SessionStatus.RUNNING) return@persist data
            data.copy(session = s.copy(status = SessionStatus.PAUSED, remainingMs = max(0L, s.targetEndTs - System.currentTimeMillis())))
        }
    }

    fun resumeQuest() {
        persist { data ->
            val s = data.session
            if (s.status != SessionStatus.PAUSED) return@persist data
            data.copy(session = s.copy(status = SessionStatus.RUNNING, targetEndTs = System.currentTimeMillis() + s.remainingMs))
        }
        _app.value.session.takeIf { it.status == SessionStatus.RUNNING }?.let { scheduleDoneNoti(it) }
    }

    fun extendQuest(mins: Int) {
        persist { data ->
            val s = data.session
            when (s.status) {
                SessionStatus.RUNNING -> data.copy(session = s.copy(targetEndTs = s.targetEndTs + mins * 60000L, plannedMin = s.plannedMin + mins))
                SessionStatus.PAUSED -> data.copy(session = s.copy(remainingMs = s.remainingMs + mins * 60000L, plannedMin = s.plannedMin + mins))
                else -> data
            }
        }
        _app.value.session.takeIf { it.status == SessionStatus.RUNNING }?.let { scheduleDoneNoti(it) }
    }

    fun stopQuest() {
        cancelDoneNoti()
        persist { data ->
            val s = data.session
            if (s.status != SessionStatus.RUNNING && s.status != SessionStatus.PAUSED) return@persist data
            val now = System.currentTimeMillis()
            val elapsedMs = if (s.status == SessionStatus.RUNNING) max(0L, now - s.startedAt)
            else max(0L, s.plannedMin * 60000L - s.remainingMs)
            val cappedMs = min(elapsedMs, Rules.BASE_CAP_PER_QUEST_NAME_PER_DAY * 60000L)
            data.copy(session = s.copy(status = SessionStatus.REPORTING, elapsedMs = cappedMs))
        }
        _overlay.value = Overlay.Report
    }

    fun discardReport() {
        cancelDoneNoti()
        persist { it.copy(session = Session()) }
        _overlay.value = Overlay.None
    }

    fun submitReport(minutes: Int, note: String, from: String, to: String, proof: String, mode: ReportMode) {
        val data = app.value
        val s = data.session
        val dateKey = DateUtil.localDateKey()
        val qn = s.questName.trim().ifEmpty { "Quest" }
        val day = data.days[dateKey]
        val before = ScoreEngine.dayEffective(day)
        val inputs = BonusInputs(note = note, from = from, to = to, proof = proof, mode = mode)
        val score = ScoreEngine.scoreQuest(day, qn, minutes, inputs)

        val entry = QuestEntry(
            ts = System.currentTimeMillis(),
            dateKey = dateKey,
            category = s.category,
            questName = qn,
            minutesSpent = max(0, minutes),
            baseMinutesUsed = score.baseMinutesUsed,
            baseScore = score.baseScore,
            bonus = score.bonus,
            questScore = score.questScore,
            note = note.trim(), from = from.trim(), to = to.trim(),
        )

        persist { cur ->
            val curDay = cur.days[dateKey] ?: DayData()
            val history = (listOf(qn) + cur.questNameHistory).distinct().take(30)
            cur.copy(
                days = cur.days + (dateKey to curDay.copy(quests = curDay.quests + entry)),
                session = Session(),
                questNameHistory = history,
            )
        }

        _overlay.value = Overlay.Celebration(
            QuestResult(
                questScore = score.questScore,
                baseScore = score.baseScore,
                bonus = score.bonus,
                questName = qn,
                category = s.category,
                dayTotalBefore = before,
                dayTotalAfter = ScoreEngine.dayEffective(_app.value.days[dateKey]),
            )
        )
    }

    fun deleteQuest(dateKey: String, ts: Long) {
        persist { data ->
            val day = data.days[dateKey] ?: return@persist data
            data.copy(days = data.days + (dateKey to day.copy(quests = day.quests.filter { it.ts != ts })))
        }
    }

    /** นาทีจริงที่ใช้ (แสดงใน Report — เว็บไม่ให้แก้เอง) */
    fun elapsedMinutes(): Int = (app.value.session.elapsedMs / 60000.0).roundToInt()

    // ── dice ──

    fun rollDice(energy: Energy) {
        val data = _app.value
        val now = System.currentTimeMillis()
        if (now < data.dice.cooldownUntil) return
        val todayKey = DateUtil.localDateKey()
        val s = strsOf(data.prefs.language)
        val quest = DiceGen.roll(data, energy, s, todayKey)
        persist { cur ->
            val rolled = if (cur.dice.rolledDateKey == todayKey) cur.dice.rolledNames else emptyList()
            cur.copy(
                dice = DiceState(
                    cooldownUntil = now + DiceGen.COOLDOWN_MS,
                    rolledDateKey = todayKey,
                    rolledNames = (rolled + quest.name).takeLast(20),
                    offerName = quest.name,
                    offerCategory = quest.category,
                    offerDuration = quest.duration,
                    offerEnergy = energy.key,
                )
            )
        }
    }

    fun clearDiceOffer() {
        persist { it.copy(dice = it.dice.copy(offerName = "", offerCategory = "", offerDuration = 0, offerEnergy = "")) }
    }

    fun startDiceOffer() {
        val d = _app.value.dice
        if (!d.hasOffer) return
        val name = d.offerName
        val cat = d.offerCategory
        val dur = d.offerDuration
        clearDiceOffer()
        startQuest(cat, name, dur)
    }

    // ── plans (ตารางพรุ่งนี้) ──

    fun addPlan(name: String, category: String, durationMin: Int, startHHMM: String, dateKey: String): PlanError? {
        val data = _app.value
        val trimmed = name.trim()
        val samDay = data.plans.filter { it.dateKey == dateKey }
        if (samDay.any { it.name.equals(trimmed, ignoreCase = true) }) return PlanError.NAME_EXISTS
        val newStart = DateUtil.parseHHMMtoMin(startHHMM)
        val newEnd = newStart + durationMin
        if (samDay.any { p ->
                val ps = DateUtil.parseHHMMtoMin(p.startHHMM)
                val pe = ps + p.durationMin
                newStart < pe && ps < newEnd
            }
        ) return PlanError.OVERLAP

        val plan = Plan(
            id = "p${System.currentTimeMillis()}${(0..999).random()}",
            dateKey = dateKey,
            name = trimmed,
            category = category,
            durationMin = durationMin,
            startHHMM = startHHMM,
        )
        persist { it.copy(plans = it.plans + plan) }
        return null
    }

    fun deletePlan(id: String) {
        persist { it.copy(plans = it.plans.filter { p -> p.id != id }) }
    }

    fun togglePlanDone(id: String) {
        persist { data ->
            data.copy(plans = data.plans.map { if (it.id == id) it.copy(done = !it.done) else it })
        }
    }

    fun startFromPlan(id: String) {
        val plan = _app.value.plans.firstOrNull { it.id == id } ?: return
        startQuest(plan.category, plan.name, plan.durationMin)
    }

    /** เก็บกวาดแผนเก่าเกิน 2 วัน (กันสะสม) */
    fun prunePlans() {
        val today = DateUtil.localDateKey()
        val keepFrom = DateUtil.addDays(today, -1)
        persist { data ->
            val kept = data.plans.filter { it.dateKey >= keepFrom }
            if (kept.size == data.plans.size) data else data.copy(plans = kept)
        }
    }

    // ── data management ──

    fun clearToday() {
        val todayKey = DateUtil.localDateKey()
        persist { data ->
            if (data.days[todayKey] == null) data
            else data.copy(days = data.days - todayKey)
        }
    }

    fun resetAll() {
        cancelDoneNoti()
        val lang = _app.value.prefs.language
        persist { AppData(prefs = it.prefs.copy(language = lang)) }
        _overlay.value = Overlay.None
        _tab.value = Tab.HOME
    }
}

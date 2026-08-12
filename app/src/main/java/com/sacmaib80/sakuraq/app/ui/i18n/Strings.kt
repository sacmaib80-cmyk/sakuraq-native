package com.sacmaib80.sakuraq.app.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

/** แทนที่ {key} ในสตริง template ("+{n} วันนี้" ฯลฯ) */
fun String.tmpl(vararg kv: Pair<String, Any>): String {
    var out = this
    kv.forEach { (k, v) -> out = out.replace("{$k}", v.toString()) }
    return out
}

/** ชุดคำทัก 1 บัคเก็ต: หัว + ท่อนขยาย (สุ่มหยิบจาก list) */
class GreetPack(val prefixes: List<String>, val subs: List<String>)

/**
 * Dictionary ทั้งแอป — copy ตรงจาก I18N ของ www/index.html (แอปจริง)
 * ภาษาเปลี่ยนได้ระหว่างรัน จึงไม่ใช้ res/values (Android locale) แต่เป็น object ใน memory
 */
class Strs(
    val lang: String,

    // ── common ──
    val next: String,
    val cont: String,
    val save: String,
    val cancel: String,
    val close: String,
    val delete: String,
    val confirm: String,
    val min: String,
    val day: String,

    // ── home ──
    val homeStartQuest: String,
    val homeGainToday: String,     // "+{n} วันนี้ ›"
    val homeStreakDays: String,    // "{n} วัน"
    val homePlanWaiting: String,   // "มี {n} quest รออยู่วันนี้"
    val homePlanDefault: String,
    val homeNameDefault: String,
    val homeEditNameTitle: String,
    val homeEditNamePh: String,
    val greet: Map<String, GreetPack>,

    // ── quest flow (hq.*) ──
    val hqStartQuest: String,
    val hqChooseCategory: String,  // มี \n
    val hqQuestSetup: String,
    val hqNameQuest: String,
    val hqQuestNameLabel: String,
    val hqNamePh: String,
    val hqTimeMin: String,
    val hqStartBtn: String,
    val hqRecent: String,
    val catDescLearning: String,
    val catDescWork: String,
    val catDescPhysical: String,
    val catDescConnection: String,

    // ── run ──
    val runSessionN: String,       // "เซสชันที่ {n}"
    val runPausedBadge: String,
    val runOvertimeBadge: String,

    // ── report (hrp.*) ──
    val hrpQuestComplete: String,
    val hrpReportLabel: String,
    val hrpHowWas: String,
    val hrpSummary: String,        // "ทำไป {min} นาที • จะได้ +{score}"
    val hrpMainNote: String,
    val hrpNotePh: String,
    val hrpInsightQ: String,
    val hrpNormalTitle: String,
    val hrpNormalSub: String,
    val hrpMethodTitle: String,
    val hrpMethodSub: String,
    val hrpBehaviorTitle: String,
    val hrpBehaviorSub: String,
    val hrpFromPh: String,
    val hrpToPh: String,
    val hrpProofPh: String,
    val hrpSubmit: String,
    val hrpDiscardTitle: String,
    val hrpDiscardSub: String,
    val hrpDiscardConfirm: String,
    val hrpLockedMin: String,
    val hrpBonusNeedMin: String,   // "โบนัสต้อง ≥ {n} นาที"
    val hrpQuestResult: String,
    val hrpPowerUp: String,
    val hrpDailyProgress: String,  // "ความคืบหน้าวันนี้ {score} / 100"
    val hrpGained: String,
    val hrpBonusQuality: String,   // "โบนัสคุณภาพ +{n}"
    val hrpTapContinue: String,

    // ── today (tp.*) ──
    val tpTitle: String,
    val tpTodayScore: String,
    val tpPts: String,
    val tpJourney: String,
    val tpResetToday: String,
    val tpResetDesc: String,
    val tpResetConfirm: String,
    val tpQuestCount: String,      // "ทำเควสไปแล้ว {n} อัน"
    val tpDailyGoal: String,       // "{pct}% ของเป้าหมายวันนี้"
    val tpDays: List<String>,
    val tpMonths: List<String>,
    val tpEmptyTitle: String,
    val tpEmptyDesc: String,

    // ── history (hist.*) ──
    val histTotalScore: String,
    val histTodayBadge: String,    // "+{score} วันนี้"
    val histTotalQuests: String,
    val histMostUsedCat: String,
    val histAvgSession: String,
    val histBestDay: String,
    val histPickDay: String,
    val histNoQuestsDay: String,
    val histNoChartData: String,
    val histPoints: String,        // "{score} คะแนน"
    val histCustomTitle: String,
    val histCustomHint: String,
    val histViewChart: String,
    val histToday: String,

    // ── schedule (sched.*) ──
    val schedTitle: String,
    val schedWriteCard: String,
    val schedCategory: String,
    val schedQuestName: String,
    val schedNamePh: String,
    val schedDuration: String,
    val schedStartTime: String,
    val schedClear: String,
    val schedToday: String,
    val schedTomorrow: String,
    val schedWaitingToday: String, // "มี {n} quest รออยู่วันนี้"
    val schedMinUnit: String,      // "{min} นาที"
    val schedDays: List<String>,
    val schedNameExists: String,   // "\"{name}\" มีอยู่แล้ว"
    val schedOverlap: String,      // "ซ้อนทับกับ \"{name}\" ({range})"
    val schedStart: String,
    val schedDone: String,
    val schedEmptyTomorrow: String,

    // ── dice (rq.*) ──
    val rqTitle: String,
    val rqSub: String,
    val rqLowDesc: String, val rqLowSub: String,
    val rqMidDesc: String, val rqMidSub: String,
    val rqHighDesc: String, val rqHighSub: String,
    val rqRollBtn: String,
    val rqCooldownHint: String,
    val rqStartNow: String,
    val rqClearQuest: String,
    val rqNewQuest: String,
    val rqCooldownShort: String,   // "cooldown อีก {time}"

    // pools (index ตรงกันทั้ง 2 ภาษา)
    val rqWorkflow: List<String>,
    val rqBodypart: List<String>,
    val rqCardio: List<String>,
    val rqConnection: List<String>,
    val rqLearnLanguage: List<String>,
    val rqLearnMath: List<String>,
    val rqLearnGeneric: List<String>,
    val rqLearnDomains: List<String>,
    val rqWorkDomains: List<String>,
    val rqFallbackLow: List<String>,
    val rqFallbackMid: List<String>,
    val rqFallbackHigh: List<String>,

    // ── menu ──
    val menuGuideTitle: String,
    val menuGuideSub: String,
    val menuLangTitle: String,
    val menuLangSub: String,
    val menuNotiTitle: String,
    val menuNotiSub: String,
    val menuDataTitle: String,
    val menuDataSub: String,
    val menuClearToday: String,
    val menuClearTodaySub: String,
    val menuResetAll: String,
    val menuResetAllSub: String,
    val menuResetAllConfirm: String,
    val menuAboutTitle: String,
    val menuAboutSub: String,
    val menuTapAgain: String,
    val guideLines: List<Pair<String, String>>, // หัวข้อ → เนื้อหา

    // ── notifications ──
    val notiChannelName: String,
    val notiDoneTitle: String,     // "{name} • ครบเวลาแล้ว"
    val notiDoneBody: String,

    // ── time ago ──
    val agoJustNow: String,
    val agoMin: String,            // "{n} นาทีก่อน"
    val agoHour: String,           // "{n} ชม.ก่อน"
    val agoDay: String,            // "{n} วันก่อน"
)

val LocalStrings = staticCompositionLocalOf<Strs> { StringsTh }

fun strsOf(lang: String): Strs = if (lang == "en") StringsEn else StringsTh

// ═══════════════════════════════ ไทย ═══════════════════════════════

val StringsTh = Strs(
    lang = "th",

    next = "ถัดไป", cont = "ไปต่อ", save = "บันทึก", cancel = "ยกเลิก", close = "ปิด",
    delete = "ลบ", confirm = "ยืนยัน", min = "นาที", day = "วัน",

    homeStartQuest = "ไปต่อกันมั้ย?",
    homeGainToday = "+{n} วันนี้ ›",
    homeStreakDays = "{n} วัน",
    homePlanWaiting = "มี {n} quest รออยู่วันนี้",
    homePlanDefault = "มีเควสรออยู่!",
    homeNameDefault = "เพื่อน",
    homeEditNameTitle = "ให้เรียกว่าอะไรดี?",
    homeEditNamePh = "ชื่อเล่นของคุณ",
    greet = mapOf(
        "morning.zero" to GreetPack(
            listOf("อรุณสวัสดิ์ตื่นหรือยังงง ☀️", "สวัสดีตอนเช้าจ้า 🌻"),
            listOf("เริ่มต้นวันใหม่ แวะมากดรับเควสแรกของวันเพื่อเอาฤกษ์เอาชัยหน่อยมั้ย! ✨", "เช้านี้อากาศดีน้า ดื่มน้ำสักแก้วแล้วมาวอร์มอัพเก็บแต้มชิลๆ กัน ☕"),
        ),
        "morning.low" to GreetPack(
            listOf("เช้านี้เริ่มต้นได้สวย! ☀️", "ตื่นปุ๊บก็เก็บแต้มปั๊บ 🌻"),
            listOf("ประเดิมไปแล้ว {s} คะแนนเบาๆ รับยามเช้า ขาดอีก {rem} ค่อยๆ ทยอยเก็บไประหว่างวันน้า 🌿", "ได้มา {s} แต้มแต่เช้าเลย ถือว่าวอร์มอัพได้ดี สู้ๆ กับวันนี้เน้อ ✌️"),
        ),
        "morning.mid" to GreetPack(
            listOf("เช้านี้ไฟแรงเวอร์! 🔥", "ทำไมฟิตแต่เช้าเลยเนี่ย 🌟"),
            listOf("เพิ่งจะเช้าเอง กวาดไป {s} คะแนนแล้ว! ครึ่งทางแล้วนะ เก่งมากๆ 👏", "แต้มพุ่งมา {s} ละ ขาดอีก {rem} วันนี้สบายแน่ๆ ค่อยๆ ปั่นไปน้า ✨"),
        ),
        "morning.good" to GreetPack(
            listOf("โอ้โห เช้านี้เอาเรื่อง! 🚀", "ฟอร์มดีตั้งแต่ไก่โห่ 🐓"),
            listOf("เช้าๆ ปาไป {s} คะแนนแล้ว ขาดอีกแค่ {rem} แบบนี้ช่วงบ่ายก็นอนพักยาวๆ ละ 🌈", "โหดมากก ได้ {s} ละ อีกนิดเดียวจะครบ 100 แล้ว ลุยยย! 💥"),
        ),
        "morning.near" to GreetPack(
            listOf("เช้านี้จะปิดจ๊อบเลยดิ! 🏁", "กลิ่นร้อยแต้มลอยมาแต่เช้า 💨"),
            listOf("ขาดอีกแค่ {rem} แต้มเท่านั้น! จะกดให้จบแต่เช้าเลย หรือจะไปทำธุระก่อนก็ยังเหลือเวลาอีกทั้งวัน ชิลมาก! 🔥🔥"),
        ),
        "morning.max" to GreetPack(
            listOf("ทะลุร้อยตั้งแต่เช้า! ยอมใจ 👑", "พลังงานล้นเหลือสุดๆ 🎉"),
            listOf("{s} คะแนนแตกตั้งแต่เช้า! มงต้องลงแล้วจุดนี้ เก่งมากๆ วันนี้ทั้งวันพักผ่อนให้เต็มที่ไปเลย 🎊🏆"),
        ),
        "noon.zero" to GreetPack(
            listOf("เที่ยงแล้วน้า กินข้าวหรือยัง 🍱", "พักทานข้าวกันก่อนนะ 🍚"),
            listOf("เช้านี้ยังยุ่งๆ อยู่ใช่มั้ยล่ะ กินข้าวให้อิ่มก่อนนะ แล้วบ่ายค่อยมาเริ่มกัน ไม่ต้องรีบ! ✌️", "ครึ่งวันแล้วยังไม่ได้แวะมาเลย พักกินข้าวแป๊บนึงแล้วมากดรับเควสหน่อยน้า 🍲"),
        ),
        "noon.low" to GreetPack(
            listOf("พักเบรกเที่ยงกัน! 🍱", "เที่ยงแล้ว เติมพลังหน่อย ⚡"),
            listOf("ตุนไว้ {s} ละ ไปหาอะไรอร่อยๆ กินก่อนนะ บ่ายค่อยมาเก็บอีก {rem} ชิลๆ 😋", "ได้มา {s} ถือว่าเริ่มต้นได้ดี! พักกินข้าวให้อิ่มก่อนแล้วค่อยลุยต่อ 💪"),
        ),
        "noon.mid" to GreetPack(
            listOf("ครึ่งวัน ครึ่งทางพอดีเป๊ะ! ✨", "เที่ยงแล้ว ฟอร์มดีนะเนี่ย 🔥"),
            listOf("เก็บไป {s} คะแนนแล้ว! พักเที่ยงยาวๆ ได้เลย บ่ายค่อยมาเก็บที่เหลือ 🍱", "แต้มมาถึง {s} แล้ว ขาดอีก {rem} ไปกินข้าวเติมพลังก่อน แล้วบ่ายมาลุยต่อฮะ! 💪"),
        ),
        "noon.good" to GreetPack(
            listOf("วันนี้ไฟแรงเวอร์! 🔥", "เที่ยงนี้แต้มสวยมากก 🌟"),
            listOf("ปาไป {s} แล้ว! ขาดอีกแค่ {rem} กินข้าวไปกดไปเพลินๆ ก็จบแล้วเนี่ย 🚀", "โห ได้ตั้ง {s}! พักกินข้าวให้อิ่มแล้วบ่ายนี้เตรียมตัวมาปิดจ๊อบกัน ✨"),
        ),
        "noon.near" to GreetPack(
            listOf("อีกนิดเดียวจะร้อยแล้ว! 🏁", "โค้งสุดท้ายก่อนมื้อเที่ยง! 💥"),
            listOf("ขาดอีกแค่ {rem} คะแนนเท่านั้น! จะดันให้จบก่อนไปกินข้าว หรือกินเสร็จค่อยมาเก็บก็จัดไป! 🍜🔥"),
        ),
        "noon.max" to GreetPack(
            listOf("เกินไปมั้ยยย จบก่อนเที่ยง! 👑", "เทพเจ้าแห่งการปั่นเควส 🎉"),
            listOf("{s} คะแนนตั้งแต่ยังไม่เที่ยง?! เอาเรื่องจัดๆ ไปจัดมื้อเที่ยงชุดใหญ่ฉลองเลย เก่งมากก 🥩🍣"),
        ),
        "afternoon.zero" to GreetPack(
            listOf("บ่ายแล้วน้าา อย่าเพิ่งหลับ! 🥱", "ครึ่งวันแล้ว แวะมาหน่อยเร็ว ☀️"),
            listOf("กินข้าวอิ่มๆ หนังตาเริ่มหย่อนล่ะสิ มาขยับตัวทำเควสแก้ง่วงสักนิดมั้ย! ☕", "บ่ายแล้วยังศูนย์อยู่เลยน้า เริ่มสักเควสให้พอมีแต้มอุ่นใจหน่อยเร็ว ✌️"),
        ),
        "afternoon.low" to GreetPack(
            listOf("ช่วงบ่ายก็ลุยต่อได้! ☀️", "มาเก็บแต้มแก้ง่วงกัน ☕"),
            listOf("มีตุนไว้ {s} แล้ว ขาดอีก {rem} ค่อยๆ ทยอยเก็บไปน้า ชิลๆ 🌿", "เพิ่งได้ {s} ไม่เป็นไรน้า เหลืออีกตั้ง {rem} บ่ายนี้ยังมีเวลาให้ตามเก็บอีกเยอะ! 💪"),
        ),
        "afternoon.mid" to GreetPack(
            listOf("ครึ่งทางแล้ว เก่งมาก! ☀️", "บ่ายนี้ฟอร์มดีนะเนี่ย ⚡"),
            listOf("เก็บไปได้ {s} คะแนนแล้ว! เหลือ {rem} ไปเติมน้ำหวานหรือกาแฟสักแก้วแล้วมาลุยต่อกัน! 🧋🔥"),
        ),
        "afternoon.good" to GreetPack(
            listOf("บ่ายนี้มาแรงมาก! 🌟", "ใกล้ความจริงแล้ววว 🚀"),
            listOf("ได้มาตั้ง {s} แล้ว! ขาดอีกแค่ {rem} จะร้อยละ รีบปั่นให้จบจะได้ทำอย่างอื่นต่อแบบโล่งๆ ✨"),
        ),
        "afternoon.near" to GreetPack(
            listOf("อีกนิดเดียว ปิดจ๊อบได้เลย! 🏁", "โค้งสุดท้ายของช่วงบ่าย! 💥"),
            listOf("ขาดอีกแค่ {rem} คะแนน! ฮึบเดียวจบเลย ดันให้ถึง 100 ไปเล้ยย! 🔥🔥"),
        ),
        "afternoon.max" to GreetPack(
            listOf("จบเควสตั้งแต่บ่าย! โคตรตึง 🎉", "เวลาเหลือเฟือเลย 👑"),
            listOf("{s} คะแนนเต็มมม! บ่ายนี้เคลียร์งานจบแล้ว จะพักผ่อนยาวๆ หรือเก็บแต้มโบนัสขำๆ ต่อก็จัดไป! 🎊"),
        ),
        "evening.zero" to GreetPack(
            listOf("เย็นแล้วน้า ทานข้าวหรือยัง 🌅", "พระอาทิตย์จะตกแล้วนะ 🌇"),
            listOf("แวะมาเก็บเควสหน่อยเร็ววว กินข้าวเสร็จแล้วมาปั่นสักนิดน้า เดี๋ยวพอดึกแล้วจะขี้เกียจ 🥺", "วันนี้ยังไม่ได้เริ่มเลยใช่มั้ยล่ะ? ค่อยๆ เริ่มตอนนี้ยังทันนะ อย่าปล่อยให้เสียสิทธิ์น้า ✌️"),
        ),
        "evening.low" to GreetPack(
            listOf("ตกเย็นแล้ว ไปต่อกัน! 🌅", "เพิ่งเลิกเรียน/เลิกงานใช่มั้ย 🌇"),
            listOf("ตุนไว้ {s} ละ ขาดอีก {rem} พักเหนื่อยแป๊บนึงแล้วค่อยมาทยอยทำช่วงค่ำก็ได้ สู้ๆ! 💖"),
        ),
        "evening.mid" to GreetPack(
            listOf("มาครึ่งทางแล้ว เย้! 🌅", "เย็นนี้แต้มใช้ได้เลยนะ! 🔥"),
            listOf("มีตุนตั้ง {s} คะแนนแล้ว! ขาดอีก {rem} ไปกินข้าวอาบน้ำก่อน แล้วค่อยมาดันต่อให้จบชิลๆ 💪"),
        ),
        "evening.good" to GreetPack(
            listOf("โห เย็นนี้แต้มพุ่งมาก 🚀", "ใกล้ความจริงแล้ว! 🌟"),
            listOf("ปาไป {s} แล้วเนี่ย! ขาดอีกแค่ {rem} เอง รีบทำให้จบตั้งแต่เย็นนี้ จะได้พักผ่อนยาวๆ เลย ✨"),
        ),
        "evening.near" to GreetPack(
            listOf("อีกนิดเดียววว จะร้อยแล้ว! 💥", "โค้งสุดท้ายของวันนี้! 🏁"),
            listOf("ขาดแค่ {rem} คะแนนเท่านั้น! ฮึบเดียวปิดจ๊อบเลย คืนนี้จะได้ไปหาหนังดูสบายใจ 🔥🔥🔥"),
        ),
        "evening.max" to GreetPack(
            listOf("จบงานตั้งแต่หัววัน! 🎉", "เย็นนี้คือสุดปัง! 👑"),
            listOf("{s} คะแนนเต็ม! เคลียร์เควสจบตั้งแต่ยังไม่ดึก เก่งมากก ไปหาของอร่อยๆ กินให้รางวัลตัวเองเลย 🍣🎊"),
        ),
        "night.zero" to GreetPack(
            listOf("ดึกแล้วน้า ยังไม่ขยับเลย 🥺", "จะหมดวันแล้วนะ มารับเควสหน่อยเร็ว! ⏰"),
            listOf("วันนี้ยังไม่ได้เริ่มเลยหรอ? สักเควสสั้นๆ ก่อนนอนไหม จะได้ไม่เสียเที่ยว! ⚡", "ดึกขนาดนี้ยังศูนย์อยู่เลย! มาปั่นสักนิดน้า จะได้นอนตาหลับ 😴🔥"),
        ),
        "night.low" to GreetPack(
            listOf("ดึกแล้ว แต่ยังสู้ดิวะ! 🌙", "เก็บมาได้นิดนึงแล้ว เก่งมาก 🌙"),
            listOf("ตุนไว้ได้ {s} คะแนนแล้ว เหลืออีก {rem} ทำเท่าที่ไหวแล้วค่อยนอนนะ 💖"),
        ),
        "night.mid" to GreetPack(
            listOf("มาได้ครึ่งทางแล้ว! 🌙", "กลางคืนแล้ว แต่แต้มสวยอยู่! 🌙"),
            listOf("เก็บไปได้ {s} คะแนนแล้ว ถือว่าโอเคเลยนะ! ถ้ายังไหวก็ลุยต่ออีกนิดชิลๆ 🎉"),
        ),
        "night.good" to GreetPack(
            listOf("วันนี้มาไกลมาก! 🌙", "ภูมิใจในตัวเองยัง วันนี้ทำดีแล้วนะ 🌙"),
            listOf("กดไป {s} คะแนนแล้วน้า! อีกนิดเดียวจะ 100 แล้ว นอนหลับสบายใจแน่ 🛌✨"),
        ),
        "night.near" to GreetPack(
            listOf("กลิ่นความสำเร็จโชยมาละ! 🌙🔥", "อีกอึดใจเดียวจะร้อยแล้ว!! 🔥"),
            listOf("อีกแค่ {rem} คะแนนก็ครบ 100 แล้ว! มาปิดจ๊อบสวยๆ ก่อนนอนกัน 💥"),
        ),
        "night.max" to GreetPack(
            listOf("วันนี้คือที่สุด! ปังมาก 🌙👑", "ภารกิจเสร็จสิ้น! นอนได้ 🌙💤"),
            listOf("ฟาดไป {s} คะแนน! วันนี้เก่งที่สุดแล้ว ไปนอนชาร์จแบตเถอะ ฝันดีนะ 🎉"),
        ),
    ),

    hqStartQuest = "เริ่มเควส",
    hqChooseCategory = "เลือก\nหมวดเควส",
    hqQuestSetup = "ตั้งค่าเควส",
    hqNameQuest = "ตั้งชื่อเควส",
    hqQuestNameLabel = "ชื่อเควส",
    hqNamePh = "เช่น English 30 min",
    hqTimeMin = "เวลา (นาที)",
    hqStartBtn = "เริ่มเควส",
    hqRecent = "เควสล่าสุด",
    catDescLearning = "เติมความรู้ทุกวัน ไปข้างหน้าอย่างมั่นใจ",
    catDescWork = "จัดการงานให้สำเร็จ พัฒนาตัวเองไม่หยุด",
    catDescPhysical = "ดูแลร่างกายให้แข็งแรง พร้อมลุยไปทุกวัน",
    catDescConnection = "เชื่อมต่อความสัมพันธ์ที่ดี อยู่เคียงข้างกันเสมอ",

    runSessionN = "เซสชันที่ {n}",
    runPausedBadge = "พักอยู่",
    runOvertimeBadge = "เกินเวลาแล้ว",

    hrpQuestComplete = "ทำเควสสำเร็จ",
    hrpReportLabel = "รายงาน",
    hrpHowWas = "เควสนี้เป็นยังไงบ้าง?",
    hrpSummary = "ทำไป {min} นาที • จะได้ +{score}",
    hrpMainNote = "บันทึกหลัก",
    hrpNotePh = "วันนี้ทำอะไร / ได้อะไร / ติดอะไร",
    hrpInsightQ = "มี insight หรือเปลี่ยนวิธีทำไหม?",
    hrpNormalTitle = "รายงานปกติ",
    hrpNormalSub = "รายงานปกติ เล่าให้ฟังว่าเกิดอะไรขึ้น",
    hrpMethodTitle = "เปลี่ยนวิธีทำ",
    hrpMethodSub = "มีการเปลี่ยนแปลงวิธีทำ อธิบายสิ่งที่เปลี่ยนและผลลัพธ์",
    hrpBehaviorTitle = "ยืนยันพฤติกรรม",
    hrpBehaviorSub = "ยืนยันพฤติกรรมที่ดี แชร์สิ่งที่ทำแล้วได้ผล",
    hrpFromPh = "From / พฤติกรรมเดิม",
    hrpToPh = "To / พฤติกรรมใหม่",
    hrpProofPh = "พิสูจน์ว่าทำจริง เช่น วันนี้ทำซ้ำยังไง",
    hrpSubmit = "ส่งรายงาน",
    hrpDiscardTitle = "ทิ้งเควสนี้?",
    hrpDiscardSub = "ข้อมูลที่ทำมาจะหายหมด",
    hrpDiscardConfirm = "ทิ้งเลย",
    hrpLockedMin = "ล็อกอยู่: อย่างน้อย 1 นาที",
    hrpBonusNeedMin = "โบนัสต้องทำ ≥ {n} นาที",
    hrpQuestResult = "ผลของเควส",
    hrpPowerUp = "เพิ่มพลัง!",
    hrpDailyProgress = "ความคืบหน้าวันนี้ {score} / 100",
    hrpGained = "ได้รับแต้ม",
    hrpBonusQuality = "โบนัสคุณภาพ +{n}",
    hrpTapContinue = "แตะเพื่อไปต่อ",

    tpTitle = "วันนี้",
    tpTodayScore = "คะแนนวันนี้",
    tpPts = "แต้ม",
    tpJourney = "เส้นทางวันนี้",
    tpResetToday = "ล้างวันนี้",
    tpResetDesc = "จะลบเควสและคะแนนทั้งหมดของวันนี้",
    tpResetConfirm = "แตะอีกครั้งเพื่อยืนยัน",
    tpQuestCount = "ทำเควสไปแล้ว {n} อัน",
    tpDailyGoal = "{pct}% ของเป้าหมายวันนี้",
    tpDays = listOf("อาทิตย์", "จันทร์", "อังคาร", "พุธ", "พฤหัสบดี", "ศุกร์", "เสาร์"),
    tpMonths = listOf("มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"),
    tpEmptyTitle = "ยังไม่มีเควสวันนี้",
    tpEmptyDesc = "แตะเพื่อเริ่มเควสแรกของวันนี้",

    histTotalScore = "คะแนนรวม",
    histTodayBadge = "+{score} วันนี้",
    histTotalQuests = "จำนวนเควส",
    histMostUsedCat = "หมวดที่ใช้เยอะสุด",
    histAvgSession = "ค่าเฉลี่ยเวลา/เควส",
    histBestDay = "วันที่ดีที่สุด",
    histPickDay = "เลือกวันเพื่อดูเควส",
    histNoQuestsDay = "ยังไม่มีเควสในวันนี้",
    histNoChartData = "ยังไม่มีข้อมูลกราฟ",
    histPoints = "{score} คะแนน",
    histCustomTitle = "ดูข้อมูลกี่วันย้อนหลัง?",
    histCustomHint = "ใส่จำนวนวัน 2–365 วัน",
    histViewChart = "ดูกราฟ",
    histToday = "วันนี้",

    schedTitle = "ตารางพรุ่งนี้",
    schedWriteCard = "เขียนการ์ด",
    schedCategory = "หมวด",
    schedQuestName = "ชื่อ Quest",
    schedNamePh = "ชื่อ...",
    schedDuration = "เวลาที่ใช้",
    schedStartTime = "เริ่มเวลา",
    schedClear = "ล้าง",
    schedToday = "วันนี้",
    schedTomorrow = "พรุ่งนี้",
    schedWaitingToday = "มี {n} quest รออยู่วันนี้",
    schedMinUnit = "{min} นาที",
    schedDays = listOf("อาทิตย์", "จันทร์", "อังคาร", "พุธ", "พฤหัส", "ศุกร์", "เสาร์"),
    schedNameExists = "\"{name}\" มีอยู่แล้ว",
    schedOverlap = "ซ้อนทับกับ \"{name}\" ({range})",
    schedStart = "เริ่ม",
    schedDone = "เสร็จ",
    schedEmptyTomorrow = "ยังไม่มีการ์ดของพรุ่งนี้ เขียนใบแรกกันเลย",

    rqTitle = "Random Quest",
    rqSub = "เลือกพลังงาน แล้วระบบจะสุ่มเควสให้",
    rqLowDesc = "พลังงานต่ำ", rqLowSub = "เหนื่อยมาก",
    rqMidDesc = "พลังงานกลาง", rqMidSub = "เหนื่อยปานกลาง",
    rqHighDesc = "พลังงานสูง", rqHighSub = "เหนื่อยน้อย",
    rqRollBtn = "สุ่มเควสให้ฉัน!",
    rqCooldownHint = "cooldown 30 นาทีหลังสุ่ม • กันสุ่มซ้ำในวันเดียว",
    rqStartNow = "เริ่มเลย!",
    rqClearQuest = "ยกเลิกเควสนี้",
    rqNewQuest = "เควสใหม่มาแล้ว!",
    rqCooldownShort = "cooldown อีก {time}",

    rqWorkflow = listOf(
        "วางโครงสร้าง / หาไอเดีย",
        "ลงมือลุยงาน (Deep Work)",
        "ตรวจทาน / จัดระเบียบให้เข้าที่",
        "เคลียร์สเต็ปย่อย 1 อย่างให้จบ",
        "สรุปความคืบหน้า / ปิดจ็อบ",
    ),
    rqBodypart = listOf("อก", "หลัง", "ไหล่", "แขน", "ขา", "หน้าท้อง", "แกนกลางลำตัว (Core)"),
    rqCardio = listOf("เดินชิลๆ", "วิ่งจ๊อกกิ้ง", "ยืดเหยียดร่างกาย", "ทำงานบ้านขยับตัว", "เดินเล่นรับลม", "คาร์ดิโอเบาๆ"),
    rqConnection = listOf(
        "ทักทายคนที่ไม่ได้คุยนานแล้ว 1 คน",
        "ส่งข้อความให้กำลังใจเพื่อน/แฟน/ครอบครัว",
        "ถามสารทุกข์สุกดิบเพื่อนร่วมงาน/คนรู้จัก",
        "แชร์เรื่องตลกหรือมีสาระให้คนสนิท",
        "ทำความรู้จักเพื่อนใหม่หรือคนในคอมมูนิตี้ 1 คน",
    ),
    rqLearnLanguage = listOf(
        "ท่องศัพท์/ทบทวนคำศัพท์ + ลองแต่งประโยคดู 5 ประโยค",
        "อ่านบทความ/ข่าว + จดสรุปใจความสำคัญสั้นๆ",
        "ฟังพอดแคสต์/ดูคลิป + สรุปสิ่งที่ได้เรียนรู้ 3 ข้อ",
        "ฝึกพูด/ออกเสียงตาม + อัดเสียงตัวเองเช็คความเป๊ะ",
        "ทำแบบฝึกหัด + จดจุดที่ผิดไว้กันลืม",
    ),
    rqLearnMath = listOf(
        "ลุยทำโจทย์ + จด Error log ข้อที่พลาด",
        "ทบทวนสูตร/คอนเซปต์ + ทำตัวอย่างประกอบ 2 ข้อ",
        "ทำโจทย์ชุดใหญ่ + ตรวจคำตอบ + หาวิธีที่ไวกว่าเดิม",
    ),
    rqLearnGeneric = listOf(
        "อ่านหนังสือ/บทความ + เขียนสรุปไอเดียหลัก 5 บรรทัด",
        "ดูคลิปสาระ/How-to + สรุปทริคที่เอาไปใช้ต่อได้จริง",
        "จัดระเบียบความรู้ + ทำ Mindmap หรือสรุปสั้นๆ",
        "ทบทวนเรื่องที่เคยเรียน + ลองอธิบายให้ตัวเองฟังหน้ากระจก",
    ),
    rqLearnDomains = listOf("ภาษาต่างประเทศ", "เรื่องเงินๆ ทองๆ (Finance)", "ทักษะคอมพิวเตอร์ / เทคโนโลยี", "จิตวิทยา / การพัฒนาตัวเอง", "คอนเทนต์ความรู้ทั่วไป"),
    rqWorkDomains = listOf("งานค้างที่ต้องเคลียร์", "งานบ้าน / ทำความสะอาด", "โปรเจกต์ส่วนตัว", "จัดระเบียบชีวิต / การเงิน", "วางแผนเป้าหมาย"),
    rqFallbackLow = listOf(
        "Learning • อ่านคอนเทนต์มีสาระ + เซฟไอเดียเก็บไว้",
        "Physical • ยืดเส้นยืดสาย / แก้กล้ามเนื้อตึง",
        "Work • เคลียร์โต๊ะ / จัดระเบียบไฟล์คอม",
        "Work • จดลิสต์สิ่งที่ต้องทำพรุ่งนี้ (To-do list)",
    ),
    rqFallbackMid = listOf(
        "Learning • ดูคลิปพัฒนาตัวเอง + จด 1 อย่างที่จะเอาไปใช้",
        "Physical • บอดี้เวทเบาๆ (อก/แขน/ขา)",
        "Work • ลุยเคลียร์งานที่ค้างอยู่ + เอาให้จบ 1 ชิ้น",
        "Work • ทำงานบ้านชิ้นใหญ่ + เช่น ซักผ้า, ถูห้อง",
    ),
    rqFallbackHigh = listOf(
        "Work • จัดการโปรเจกต์สำคัญ + ลุยแบบ Deep Work",
        "Work • เคลียร์งานที่น่าเบื่อที่สุดให้พ้นทาง",
        "Physical • เวทเทรนนิ่ง / ออกกำลังกายจริงจัง",
        "Learning • ศึกษาเรื่องใหม่ที่สนใจแบบเจาะลึก + ทำสรุปความเข้าใจ",
    ),

    menuGuideTitle = "คู่มือ",
    menuGuideSub = "วิธีใช้งานแอป",
    menuLangTitle = "ภาษา",
    menuLangSub = "ภาษา / Language",
    menuNotiTitle = "การแจ้งเตือน",
    menuNotiSub = "เตือนตอนครบเวลาเควส",
    menuDataTitle = "Data Management",
    menuDataSub = "ล้างข้อมูลในเครื่องนี้",
    menuClearToday = "ล้างวันนี้",
    menuClearTodaySub = "ลบเควสและคะแนนของวันนี้",
    menuResetAll = "ล้างข้อมูลทั้งหมด",
    menuResetAllSub = "เริ่มใหม่หมด ทุกวัน ทุกคะแนน",
    menuResetAllConfirm = "แตะอีกครั้งเพื่อลบทั้งหมด (ย้อนกลับไม่ได้)",
    menuAboutTitle = "เกี่ยวกับแอป",
    menuAboutSub = "SakuraQ Native",
    menuTapAgain = "แตะอีกครั้งเพื่อยืนยัน",
    guideLines = listOf(
        "เริ่มเควส" to "เลือกหมวด ตั้งชื่อ ตั้งเวลา แล้วโฟกัสจนจบ — จบแล้วเขียนรายงานเพื่อรับแต้ม",
        "แต้ม" to "1 นาที = 0.4 แต้ม (นับสูงสุด 60 นาที/ชื่อเควส/วัน) เป้าหมายวันละ 100 แต้ม",
        "โบนัสคุณภาพ" to "เควสที่ทำ ≥ 25 นาที เขียนรายงานดีๆ ได้โบนัส +2 / เปลี่ยนวิธีทำ +4 / ยืนยันพฤติกรรม +6",
        "เลเวล" to "ทุก 100 แต้มสะสม = 1 เลเวล สตรีคนับวันที่ทำเควสติดกัน",
        "ลูกเต๋า" to "ไม่รู้จะทำอะไร? แตะลูกเต๋าที่หน้าแรก เลือกพลังงานแล้วให้ระบบสุ่มเควสให้",
        "ตาราง" to "เขียนการ์ดวางแผนพรุ่งนี้ไว้ล่วงหน้า ถึงเวลาแล้วกดเริ่มจากการ์ดได้เลย",
    ),

    notiChannelName = "ครบเวลาเควส",
    notiDoneTitle = "✅ {name} • ครบเวลาแล้ว",
    notiDoneBody = "เปิด SakuraQ เพื่อส่ง Report",

    agoJustNow = "เมื่อกี้",
    agoMin = "{n} นาทีก่อน",
    agoHour = "{n} ชม.ก่อน",
    agoDay = "{n} วันก่อน",
)

// ═══════════════════════════════ English ═══════════════════════════════

val StringsEn = Strs(
    lang = "en",

    next = "Next", cont = "Continue", save = "Save", cancel = "Cancel", close = "Close",
    delete = "Delete", confirm = "Confirm", min = "min", day = "days",

    homeStartQuest = "Ready to go?",
    homeGainToday = "+{n} today ›",
    homeStreakDays = "{n} days",
    homePlanWaiting = "{n} quests waiting today",
    homePlanDefault = "You have quests waiting!",
    homeNameDefault = "friend",
    homeEditNameTitle = "What should we call you?",
    homeEditNamePh = "Your nickname",
    greet = mapOf(
        "morning.zero" to GreetPack(
            listOf("Morning! You up yet? ☀️", "Good morning! 🌻"),
            listOf("New day! Wanna tap your first quest to get things rolling? ✨", "Lovely morning — grab a glass of water and warm up with some easy points ☕"),
        ),
        "morning.low" to GreetPack(
            listOf("Nice start this morning! ☀️", "Up and already scoring 🌻"),
            listOf("Off the mark with {s} early points — {rem} to go, just chip away through the day 🌿", "Got {s} this early — solid warm-up, let's crush today ✌️"),
        ),
        "morning.mid" to GreetPack(
            listOf("On fire this morning! 🔥", "Why so fit this early?! 🌟"),
            listOf("Barely morning and you've swept {s}! Halfway there, amazing 👏", "Points jumped to {s}, only {rem} left — easy day ahead, keep cruising ✨"),
        ),
        "morning.good" to GreetPack(
            listOf("Whoa, big morning! 🚀", "In great form since the crack of dawn 🐓"),
            listOf("{s} points before noon, only {rem} left — afternoon naps incoming 🌈", "Brutal — {s} already, almost at 100, go go go! 💥"),
        ),
        "morning.near" to GreetPack(
            listOf("Closing it out this morning?! 🏁", "I smell 100 points in the air ☀️💨"),
            listOf("Just {rem} points left! Finish early, or run errands first — you've still got all day. So chill! 🔥🔥"),
        ),
        "morning.max" to GreetPack(
            listOf("100+ before noon! I bow 👑", "Energy absolutely overflowing 🎉"),
            listOf("{s} points this early! Crown's yours — rest the whole day, you earned it 🎊🏆"),
        ),
        "noon.zero" to GreetPack(
            listOf("It's noon — eaten yet? 🍱", "Lunch break first, okay 🍚"),
            listOf("Busy morning, huh? Eat well first, start in the afternoon — no rush! ✌️", "Half the day gone and no visit yet — grab a quick bite then tap a quest 🍲"),
        ),
        "noon.low" to GreetPack(
            listOf("Lunch break! 🍱", "Noon — refuel a bit ⚡"),
            listOf("Stashed {s} — go find something tasty, grab the other {rem} this afternoon, chill 😋", "Got {s}, good start! Eat up first, then back at it 💪"),
        ),
        "noon.mid" to GreetPack(
            listOf("Half day, right at the halfway mark! ✨", "Noon and in great form 🔥"),
            listOf("Banked {s} already! Take a long lunch, grab the rest this afternoon 🍱", "Up to {s}, {rem} to go — refuel at lunch then charge on this afternoon! 💪"),
        ),
        "noon.good" to GreetPack(
            listOf("Blazing today! 🔥", "Beautiful score at noon 🌟"),
            listOf("Up to {s}! Only {rem} left — snack and tap, you'll finish in no time 🚀", "Whoa, {s} already! Eat well, then come close it out this afternoon ✨"),
        ),
        "noon.near" to GreetPack(
            listOf("Almost at 100! 🏁", "Final stretch before lunch! 💥"),
            listOf("Only {rem} points left! Push it before lunch, or finish after eating — your call! 🍜🔥"),
        ),
        "noon.max" to GreetPack(
            listOf("Too much, done before noon?! 👑", "God of quest-grinding 🎉"),
            listOf("{s} points before it's even noon?! Beast mode — go celebrate with a big lunch, amazing 🥩🍣"),
        ),
        "afternoon.zero" to GreetPack(
            listOf("Afternoon — don't doze off! 🥱", "Half the day's gone, drop by quick ☀️"),
            listOf("Full belly, eyelids drooping? Move a little, do a quest to beat the slump ☕", "Afternoon and still zero — start a quest to bank some comfort points, quick ✌️"),
        ),
        "afternoon.low" to GreetPack(
            listOf("Afternoon's still good to push! ☀️", "Beat the slump, grab points ☕"),
            listOf("Stashed {s}, {rem} to go — chip away slowly, chill 🌿", "Just {s}, no worries — still {rem} left, plenty of time this afternoon! 💪"),
        ),
        "afternoon.mid" to GreetPack(
            listOf("Halfway, awesome! ☀️", "Good form this afternoon ⚡"),
            listOf("Banked {s}! {rem} left — grab a sweet drink or coffee then back to it! 🧋🔥"),
        ),
        "afternoon.good" to GreetPack(
            listOf("Surging this afternoon! 🌟", "So close to the real thing 🚀"),
            listOf("You've got {s}! Only {rem} to 100 — finish it up so you can move on with a clear head ✨"),
        ),
        "afternoon.near" to GreetPack(
            listOf("Almost — you can close it now! 🏁", "Final stretch of the afternoon! 💥"),
            listOf("Only {rem} points! One push and it's done — drive it to 100, let's gooo! 🔥🔥"),
        ),
        "afternoon.max" to GreetPack(
            listOf("Done by afternoon! Insane 🎉", "Time to spare 👑"),
            listOf("{s} points, full! Cleared it this afternoon — rest long or grab bonus points for fun, your call! 🎊"),
        ),
        "evening.zero" to GreetPack(
            listOf("Evening — eaten yet? 🌅", "Sun's about to set 🌇"),
            listOf("Drop by and grab a quest, quick! Eat then grind a bit — it gets lazy late at night 🥺", "Haven't started today, right? Start now, still in time — don't waste it ✌️"),
        ),
        "evening.low" to GreetPack(
            listOf("Evening — let's keep going! 🌅", "Just off school/work? 🌇"),
            listOf("Stashed {s}, {rem} to go — rest a bit then chip away tonight, you got this! 💖"),
        ),
        "evening.mid" to GreetPack(
            listOf("Halfway there, yay! 🌅", "Solid score this evening! 🔥"),
            listOf("Got {s} banked! {rem} left — eat and shower first, then close it out easy 💪"),
        ),
        "evening.good" to GreetPack(
            listOf("Whoa, score's soaring tonight 🚀", "So close now! 🌟"),
            listOf("Up to {s}! Only {rem} left — finish it this evening so you can rest long ✨"),
        ),
        "evening.near" to GreetPack(
            listOf("Almost at 100! 💥", "Final stretch of the day! 🏁"),
            listOf("Only {rem} points left! One push and it's done — then enjoy a movie tonight 🔥🔥🔥"),
        ),
        "evening.max" to GreetPack(
            listOf("Done before nightfall! 🎉", "Tonight's a banger! 👑"),
            listOf("{s} points, full! Cleared it before it got late — amazing, go grab something tasty as a reward 🍣🎊"),
        ),
        "night.zero" to GreetPack(
            listOf("Late already and not moving 🥺", "Day's almost over — grab a quest, quick! ⏰"),
            listOf("Haven't started today? One short quest before bed so it's not a wasted day! ⚡", "This late and still zero! Grind a little so you can sleep easy 😴🔥"),
        ),
        "night.low" to GreetPack(
            listOf("Late, but still fighting! 🌙", "Got a little done — nice 🌙"),
            listOf("Stashed {s}, {rem} left — do what you can then get some sleep 💖"),
        ),
        "night.mid" to GreetPack(
            listOf("Made it halfway! 🌙", "Night already, but the score's pretty 🌙"),
            listOf("Banked {s} — that's pretty okay! If you're still up for it, push a little more, chill 🎉"),
        ),
        "night.good" to GreetPack(
            listOf("Came so far today! 🌙", "Proud of yourself yet? You did well today 🌙"),
            listOf("Tapped in {s}! A little more to 100 — you'll sleep easy tonight 🛌✨"),
        ),
        "night.near" to GreetPack(
            listOf("I smell success! 🌙🔥", "One more breath to 100!! 🔥"),
            listOf("Just {rem} points to hit 100! Close it out nicely before bed 💥"),
        ),
        "night.max" to GreetPack(
            listOf("Today was the best! Huge 🌙👑", "Mission complete! Sleep time 🌙💤"),
            listOf("Smashed {s} points! Your best today — go recharge, sweet dreams 🎉"),
        ),
    ),

    hqStartQuest = "Start Quest",
    hqChooseCategory = "Choose your\ncategory",
    hqQuestSetup = "Quest Setup",
    hqNameQuest = "Name your quest",
    hqQuestNameLabel = "Quest name",
    hqNamePh = "e.g. English 30 min",
    hqTimeMin = "Time (minutes)",
    hqStartBtn = "Start Quest",
    hqRecent = "Recent quests",
    catDescLearning = "Add knowledge daily, move forward boldly",
    catDescWork = "Get things done, keep leveling yourself up",
    catDescPhysical = "Keep your body strong, ready for every day",
    catDescConnection = "Good relationships, always side by side",

    runSessionN = "Session {n}",
    runPausedBadge = "Paused",
    runOvertimeBadge = "Overtime",

    hrpQuestComplete = "Quest Complete",
    hrpReportLabel = "Report",
    hrpHowWas = "How was this quest?",
    hrpSummary = "{min} min done • you'll get +{score}",
    hrpMainNote = "Main note",
    hrpNotePh = "What did you do today / what did you get / what got stuck",
    hrpInsightQ = "Any insight or change in how you did it?",
    hrpNormalTitle = "Normal report",
    hrpNormalSub = "A regular report — tell us what happened",
    hrpMethodTitle = "Method Change",
    hrpMethodSub = "You changed your method — explain what changed and the result",
    hrpBehaviorTitle = "Confirm Behavior",
    hrpBehaviorSub = "Confirm a good behavior — share what worked",
    hrpFromPh = "From / old behavior",
    hrpToPh = "To / new behavior",
    hrpProofPh = "Prove you really did it, e.g. how you repeated it today",
    hrpSubmit = "Submit Report",
    hrpDiscardTitle = "Discard this quest?",
    hrpDiscardSub = "Everything you've done will be lost",
    hrpDiscardConfirm = "Discard",
    hrpLockedMin = "Locked: minimum 1 min",
    hrpBonusNeedMin = "Bonus needs ≥ {n} min",
    hrpQuestResult = "Quest Result",
    hrpPowerUp = "Power Up",
    hrpDailyProgress = "Daily Progress {score} / 100",
    hrpGained = "Points gained",
    hrpBonusQuality = "Quality bonus +{n}",
    hrpTapContinue = "Tap to continue",

    tpTitle = "Today",
    tpTodayScore = "Today Score",
    tpPts = "pts",
    tpJourney = "Today Journey",
    tpResetToday = "Reset Today",
    tpResetDesc = "This will remove all quests and scores for today.",
    tpResetConfirm = "Tap again to confirm",
    tpQuestCount = "{n} quests completed",
    tpDailyGoal = "{pct}% of daily goal",
    tpDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    tpMonths = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"),
    tpEmptyTitle = "No quests today yet",
    tpEmptyDesc = "Tap to start your first quest today",

    histTotalScore = "Total score",
    histTodayBadge = "+{score} today",
    histTotalQuests = "Total quests",
    histMostUsedCat = "Most used category",
    histAvgSession = "Avg time / quest",
    histBestDay = "Best day",
    histPickDay = "Pick a day to see quests",
    histNoQuestsDay = "No quests on this day yet",
    histNoChartData = "No chart data yet",
    histPoints = "{score} pts",
    histCustomTitle = "How many days back?",
    histCustomHint = "Enter 2–365 days",
    histViewChart = "View chart",
    histToday = "Today",

    schedTitle = "Tomorrow's schedule",
    schedWriteCard = "Write a card",
    schedCategory = "Category",
    schedQuestName = "Quest name",
    schedNamePh = "Name...",
    schedDuration = "Duration",
    schedStartTime = "Start time",
    schedClear = "Clear",
    schedToday = "Today",
    schedTomorrow = "Tomorrow",
    schedWaitingToday = "{n} quests waiting today",
    schedMinUnit = "{min} min",
    schedDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    schedNameExists = "\"{name}\" already exists",
    schedOverlap = "Overlaps with \"{name}\" ({range})",
    schedStart = "Start",
    schedDone = "Done",
    schedEmptyTomorrow = "No cards for tomorrow yet — write your first one",

    rqTitle = "Random Quest",
    rqSub = "Pick your energy and we'll roll a quest for you",
    rqLowDesc = "Low energy", rqLowSub = "Very tired",
    rqMidDesc = "Medium energy", rqMidSub = "Somewhat tired",
    rqHighDesc = "High energy", rqHighSub = "Barely tired",
    rqRollBtn = "Roll me a quest!",
    rqCooldownHint = "30-min cooldown after rolling • no repeat rolls in one day",
    rqStartNow = "Start now!",
    rqClearQuest = "Cancel this quest",
    rqNewQuest = "Your quest is here!",
    rqCooldownShort = "cooldown {time}",

    rqWorkflow = listOf(
        "Lay out structure / brainstorm",
        "Dive into the work (Deep Work)",
        "Review / tidy things up",
        "Finish one small step",
        "Summarize progress / close the job",
    ),
    rqBodypart = listOf("Chest", "Back", "Shoulders", "Arms", "Legs", "Abs", "Core"),
    rqCardio = listOf("Easy walk", "Jogging", "Full-body stretch", "Move around with chores", "Stroll for fresh air", "Light cardio"),
    rqConnection = listOf(
        "Say hi to 1 person you haven't talked to in a while",
        "Send an encouraging message to a friend/partner/family",
        "Check in on a coworker/acquaintance",
        "Share something funny or useful with someone close",
        "Get to know 1 new friend or community member",
    ),
    rqLearnLanguage = listOf(
        "Drill vocab / review words + try writing 5 sentences",
        "Read an article/news + jot a short summary",
        "Listen to a podcast/watch a clip + summarize 3 takeaways",
        "Practice speaking/shadowing + record yourself to check accuracy",
        "Do exercises + note your mistakes so you don't forget",
    ),
    rqLearnMath = listOf(
        "Grind problems + keep an error log of misses",
        "Review formulas/concepts + work 2 example problems",
        "Do a big problem set + check answers + find a faster method",
    ),
    rqLearnGeneric = listOf(
        "Read a book/article + write a 5-line summary of the main idea",
        "Watch a useful clip/How-to + note tricks you can actually use",
        "Organize your knowledge + make a mindmap or short summary",
        "Review something you learned + explain it to yourself in the mirror",
    ),
    rqLearnDomains = listOf("Foreign languages", "Money & finance", "Computer / tech skills", "Psychology / self-development", "General knowledge content"),
    rqWorkDomains = listOf("Pending work to clear", "Housework / cleaning", "Personal project", "Organize life / finances", "Plan your goals"),
    rqFallbackLow = listOf(
        "Learning • Read useful content + save the ideas",
        "Physical • Stretch / loosen tight muscles",
        "Work • Clear your desk / organize computer files",
        "Work • Write tomorrow's to-do list",
    ),
    rqFallbackMid = listOf(
        "Learning • Watch a self-improvement clip + note 1 thing to use",
        "Physical • Light bodyweight (chest/arms/legs)",
        "Work • Clear out pending work + finish 1 item",
        "Work • Do a big chore + e.g. laundry, mopping",
    ),
    rqFallbackHigh = listOf(
        "Work • Tackle an important project + Deep Work",
        "Work • Knock out the most boring task",
        "Physical • Weight training / serious workout",
        "Learning • Study a new topic in depth + write an understanding summary",
    ),

    menuGuideTitle = "Guide",
    menuGuideSub = "How to use the app",
    menuLangTitle = "Language",
    menuLangSub = "ภาษา / Language",
    menuNotiTitle = "Notifications",
    menuNotiSub = "Alert when quest time is up",
    menuDataTitle = "Data Management",
    menuDataSub = "Clear data on this device",
    menuClearToday = "Reset Today",
    menuClearTodaySub = "Remove today's quests and score",
    menuResetAll = "Erase everything",
    menuResetAllSub = "Start over — all days, all points",
    menuResetAllConfirm = "Tap again to erase all (can't undo)",
    menuAboutTitle = "About",
    menuAboutSub = "SakuraQ Native",
    menuTapAgain = "Tap again to confirm",
    guideLines = listOf(
        "Start a quest" to "Pick a category, name it, set the time, then focus till it ends — write a report to collect points",
        "Points" to "1 minute = 0.4 pts (capped at 60 min per quest name per day). Daily goal is 100 pts",
        "Quality bonus" to "Quests ≥ 25 min: a good report earns +2 / method change +4 / confirm behavior +6",
        "Level" to "Every 100 lifetime points = 1 level. Streak counts consecutive quest days",
        "Dice" to "Not sure what to do? Tap the dice on Home, pick your energy, and let it roll a quest",
        "Plan" to "Write cards for tomorrow ahead of time — when it's time, start straight from the card",
    ),

    notiChannelName = "Quest time-up",
    notiDoneTitle = "✅ {name} • time's up",
    notiDoneBody = "Open SakuraQ to submit your Report",

    agoJustNow = "just now",
    agoMin = "{n} min ago",
    agoHour = "{n} hr ago",
    agoDay = "{n} days ago",
)

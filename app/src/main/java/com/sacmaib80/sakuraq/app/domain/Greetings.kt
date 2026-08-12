package com.sacmaib80.sakuraq.app.domain

import com.sacmaib80.sakuraq.app.ui.i18n.Strs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

/**
 * คำทักหน้า Home — พอร์ต getHomeGreetingTexts() จากแอปจริง
 * 5 ช่วงเวลา × 6 บัคเก็ตคะแนน สุ่มประโยคจาก dict
 */
object Greetings {

    fun periodOf(hour: Int): String = when {
        hour in 5..11 -> "morning"
        hour in 12..13 -> "noon"
        hour in 14..17 -> "afternoon"
        hour in 18..20 -> "evening"
        else -> "night"
    }

    fun bucketOf(score: Double): String = when {
        score == 0.0 -> "zero"
        score < 25 -> "low"
        score < 60 -> "mid"
        score < 85 -> "good"
        score < 100 -> "near"
        else -> "max"
    }

    /** คืน (หัวทัก, ท่อนขยาย) — {s} = คะแนน, {rem} = ที่เหลือถึง 100 */
    fun pick(s: Strs, score: Double, hour: Int, random: Random = Random.Default): Pair<String, String> {
        val pack = s.greet["${periodOf(hour)}.${bucketOf(score)}"]
            ?: return "" to ""
        val rem = max(0, ceil(100.0 - score).toInt())
        val sTxt = fmtScore(score)
        fun fill(t: String) = t.replace("{s}", sTxt).replace("{rem}", rem.toString())
        val prefix = pack.prefixes[random.nextInt(pack.prefixes.size)]
        val sub = pack.subs[random.nextInt(pack.subs.size)]
        return fill(prefix) to fill(sub)
    }
}

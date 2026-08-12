package com.sacmaib80.sakuraq.app.domain

object Rules {
    const val BASE_PER_MINUTE = 0.4
    const val BONUS_MIN_MINUTES = 25
    const val BASE_CAP_PER_QUEST_NAME_PER_DAY = 60
    const val DAY_GOAL = 100
    const val POINTS_PER_LEVEL = 100
}

val DURATION_PRESETS = listOf(15, 25, 45, 60, 90)
const val DURATION_MIN = 5
const val DURATION_MAX = 120
const val DURATION_STEP = 5

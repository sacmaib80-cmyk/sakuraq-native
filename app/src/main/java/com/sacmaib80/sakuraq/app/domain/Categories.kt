package com.sacmaib80.sakuraq.app.domain

import androidx.compose.ui.graphics.Color
import com.sacmaib80.sakuraq.app.ui.i18n.Strs

/**
 * หมวดเควส — ชื่อโชว์เป็นอังกฤษเสมอ (ตามแอปจริง) ส่วนคำอธิบายตามภาษา
 * สี: Learning เขียว / Work ฟ้า / Physical ม่วง / Connection ชมพู (ตรง schedule dots ของเว็บ)
 */
data class Category(
    val id: String,
    val label: String,
    val desc: String,
    val color: Color,
    val gradStart: Color,
    val gradEnd: Color,
    val soft: Color,
    // พื้นการ์ดพาสเทลใน quest flow (พอร์ตจาก .hqCatLearn ฯลฯ)
    val tileStart: Color,
    val tileEnd: Color,
    val tileInk: Color,
)

val CATEGORIES = listOf(
    Category(
        id = "Learning",
        label = "Learning",
        desc = "เติมความรู้ทุกวัน ไปข้างหน้าอย่างมั่นใจ",
        color = Color(0xFF10B981),
        gradStart = Color(0xFF34D399),
        gradEnd = Color(0xFF059669),
        soft = Color(0x2934D399),
        tileStart = Color(0xFFD4F5E2),
        tileEnd = Color(0xFFB8EFD0),
        tileInk = Color(0xFF166534),
    ),
    Category(
        id = "Work/Project",
        label = "Work/Project",
        desc = "จัดการงานให้สำเร็จ พัฒนาตัวเองไม่หยุด",
        color = Color(0xFF3B82F6),
        gradStart = Color(0xFF60A5FA),
        gradEnd = Color(0xFF2563EB),
        soft = Color(0x2960A5FA),
        tileStart = Color(0xFFDBEAFE),
        tileEnd = Color(0xFFBFDBFE),
        tileInk = Color(0xFF1E40AF),
    ),
    Category(
        id = "Physical",
        label = "Physical",
        desc = "ดูแลร่างกายให้แข็งแรง พร้อมลุยไปทุกวัน",
        color = Color(0xFF8B5CF6),
        gradStart = Color(0xFFA78BFA),
        gradEnd = Color(0xFF7C3AED),
        soft = Color(0x2EA78BFA),
        tileStart = Color(0xFFEDE9FE),
        tileEnd = Color(0xFFDDD6FE),
        tileInk = Color(0xFF5B21B6),
    ),
    Category(
        id = "Connection",
        label = "Connection",
        desc = "เชื่อมต่อความสัมพันธ์ที่ดี อยู่เคียงข้างกันเสมอ",
        color = Color(0xFFEC4899),
        gradStart = Color(0xFFF472B6),
        gradEnd = Color(0xFFDB2777),
        soft = Color(0x29F472B6),
        tileStart = Color(0xFFFCE7F3),
        tileEnd = Color(0xFFFBCFE8),
        tileInk = Color(0xFF9D174D),
    ),
)

fun categoryById(id: String?): Category = CATEGORIES.firstOrNull { it.id == id } ?: CATEGORIES[0]

fun categoryDesc(cat: Category, s: Strs): String = when (cat.id) {
    "Learning" -> s.catDescLearning
    "Work/Project" -> s.catDescWork
    "Physical" -> s.catDescPhysical
    else -> s.catDescConnection
}

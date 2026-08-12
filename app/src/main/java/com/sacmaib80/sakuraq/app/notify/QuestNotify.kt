package com.sacmaib80.sakuraq.app.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sacmaib80.sakuraq.app.MainActivity
import com.sacmaib80.sakuraq.app.R

/**
 * แจ้งเตือน "ครบเวลาเควส" ผ่าน OS alarm — ตั้งไว้ล่วงหน้าที่ targetEndTs
 * เพราะ tick loop ของแอปหยุดตอนอยู่ background (บทเรียนเดียวกับ scheduleQuestDoneOS ของแอปจริง)
 */
object QuestNotify {

    private const val CHANNEL_ID = "quest_done"
    private const val REQUEST_CODE = 1001
    private const val NOTI_ID = 2001

    const val EXTRA_TITLE = "title"
    const val EXTRA_BODY = "body"
    const val EXTRA_CHANNEL_NAME = "channelName"

    fun ensureChannel(context: Context, channelName: String) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
        }
        nm.createNotificationChannel(ch)
    }

    private fun pendingIntent(context: Context, title: String, body: String, channelName: String): PendingIntent {
        val intent = Intent(context, QuestDoneReceiver::class.java).apply {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
            putExtra(EXTRA_CHANNEL_NAME, channelName)
        }
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun schedule(context: Context, atMs: Long, title: String, body: String, channelName: String) {
        if (atMs <= System.currentTimeMillis()) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, title, body, channelName)
        try {
            val canExact = Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()
            if (canExact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pi)
            } else {
                am.setWindow(AlarmManager.RTC_WAKEUP, atMs, 60_000L, pi)
            }
        } catch (_: SecurityException) {
            am.setWindow(AlarmManager.RTC_WAKEUP, atMs, 60_000L, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, QuestDoneReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        am.cancel(pi)
        pi.cancel()
    }
}

class QuestDoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(QuestNotify.EXTRA_TITLE) ?: "SakuraQ"
        val body = intent.getStringExtra(QuestNotify.EXTRA_BODY) ?: ""
        val channelName = intent.getStringExtra(QuestNotify.EXTRA_CHANNEL_NAME) ?: "Quest"

        QuestNotify.ensureChannel(context, channelName)

        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val noti = NotificationCompat.Builder(context, "quest_done")
            .setSmallIcon(R.drawable.ic_stat_quest)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(0xFF5B6AF0.toInt())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(open)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            nm.notify(2001, noti)
        } catch (_: SecurityException) {
            // ผู้ใช้ปิด permission แจ้งเตือน — เงียบไว้
        }
    }
}

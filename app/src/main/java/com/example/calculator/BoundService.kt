package com.example.calculator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class BoundService : Service() {

    private val binder = LocalBinder()

    private var notificationManager: NotificationManager? = null

    fun calculate(expr : String): Int {
        val calc = expr.split(' ')
        var result = 0
        var nextOp: Char? = null
        calc.forEach {
            when (it) {
                "+", "-", "*", "/" -> {
                    nextOp = it[0]
                }
                else -> {
                    if (it != ".") {
                        when (nextOp) {
                            '+' -> {
                                result += it.toInt()
                            }
                            '/' -> {
                                result /= it.toInt()
                            }
                            '-' -> {
                                result -= it.toInt()
                            }
                            '*' -> {
                                result *= it.toInt()
                            }
                            else -> {
                                result = it.toInt()
                            }
                        }
                    }
                }
            }
        }
        sendMessage(expr.plus(" = $result"))
        return result
    }

    private fun sendMessage(msg: String) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CALC_CHANNEL_ID",
                "Berechnungen",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Benachrichtigungen über Berechnungen."
            notificationManager?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(
            this, "CALC_CHANNEL_ID"
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Benachrichtigungstest")
            .setContentText(msg)
            .build()
        notificationManager?.notify(4711, notification)
    }

    inner class LocalBinder : Binder() {
        fun calculate(expr : String): Int {
            return this@BoundService.calculate(expr)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE)
                as NotificationManager


        return binder
    }
}
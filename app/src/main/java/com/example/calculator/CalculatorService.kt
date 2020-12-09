package com.example.calculator

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder

class CalculatorService: Service(), NotificationInterface {
    private val binder = LocalBinder()

    private var messageService: MessageService.LocalBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            messageService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messageService = (service as? MessageService.LocalBinder) ?: run {
                unbindService(this)
                null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        bindService(Intent(this, MessageService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? = binder


    inner class LocalBinder : Binder() {
        fun calculate(expr : String): Int {
            return this@CalculatorService.calculate(expr)
        }
    }

    fun calculate(expr: String): Int {
        return Calculator(this).calculate(expr)
    }

    override fun sendMessage(title: String, value: String) {
        messageService?.sendMessage(value)
    }

}
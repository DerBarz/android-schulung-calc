package com.example.calculator

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.CalcBaseLayoutKeyboardBinding
import java.util.HashSet

class MainActivity : Activity(), ButtonAction {

    private var calculatorService: CalculatorService.LocalBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            calculatorService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            calculatorService = (service as? CalculatorService.LocalBinder) ?: run {
                unbindService(this)
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = DataBindingUtil.setContentView<CalcBaseLayoutKeyboardBinding>(
            this,
            R.layout.calc_base_layout_keyboard
        )

        contentView.test = this

        findViewById<Button>(R.id.calc_layout_dot).setOnClickListener { setTest(".", true) }
        findViewById<Button>(R.id.calc_layout_plus).setOnClickListener { setTest("+", true) }
        findViewById<Button>(R.id.calc_layout_minus).setOnClickListener { setTest("-", true) }
        findViewById<Button>(R.id.calc_layout_times).setOnClickListener { setTest("*", true) }
        findViewById<Button>(R.id.calc_layout_divide).setOnClickListener { setTest("/", true) }

        bindService(Intent(this, CalculatorService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    private var lastFieldIsOperation = false
    private var resetted = false
    private fun setTest(string: String, operation: Boolean = false) {
        if (lastFieldIsOperation && operation) {
            return
        }
        lastFieldIsOperation = operation
        val textView = findViewById<TextView>(R.id.calc_layout_text)
        if (!resetted) {
            textView.text = string
            resetted = true
        } else if (operation) {
            textView.append(" $string ")
        } else textView.append(string)
    }


    override fun typeNumber(value: Int) {
        setTest(value.toString())
    }

    override fun calculate() {
        if (!resetted) {
            return
        }
        val sharedPreferences = getSharedPreferences("Calculator", Context.MODE_PRIVATE)
        val calculations = sharedPreferences.getStringSet("calculations", HashSet<String>())?.toMutableSet()
        val textView = findViewById<TextView>(R.id.calc_layout_text)

        val result = calculatorService?.calculate(textView.text.toString())

        textView.append(" = $result")
        calculations?.add(textView.text.toString())
        val edit = sharedPreferences.edit()
        edit.putStringSet("calculations", calculations)
        edit.apply()
        resetted = false
    }
}

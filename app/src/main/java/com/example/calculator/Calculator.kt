package com.example.calculator

class Calculator(
    private val notificationService: NotificationInterface
) {

    fun calculate(expr: String): Int {
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
        val message = "$expr = $result"
        notificationService.sendMessage("Berechnung fertig", message)
        return result
    }


}

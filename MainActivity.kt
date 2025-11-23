package com.example.calculatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvHistory: TextView
    private var currentInput = "0"
    private var operator = ""
    private var firstValue = 0.0
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvHistory = findViewById(R.id.tvHistory)

        // Number buttons
        setupNumberButtons()

        // Operation buttons
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("/") }
        findViewById<Button>(R.id.btnModulo).setOnClickListener { setOperator("%") }

        // Special buttons
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { delete() }
        findViewById<Button>(R.id.btnDecimal).setOnClickListener { addDecimal() }
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { button ->
                appendNumber((button as Button).text.toString())
            }
        }
    }

    private fun appendNumber(number: String) {
        if (isNewOperation) {
            currentInput = number
            isNewOperation = false
        } else {
            currentInput = if (currentInput == "0") {
                number
            } else {
                currentInput + number
            }
        }
        updateDisplay()
    }

    private fun setOperator(op: String) {
        if (operator.isNotEmpty()) {
            calculateResult()
        }
        firstValue = currentInput.toDouble()
        operator = op
        tvHistory.text = "${formatNumber(firstValue)} $operator"
        isNewOperation = true
    }

    private fun calculateResult() {
        if (operator.isEmpty()) return

        val secondValue = currentInput.toDouble()
        val result = when (operator) {
            "+" -> firstValue + secondValue
            "-" -> firstValue - secondValue
            "*" -> firstValue * secondValue
            "/" -> {
                if (secondValue != 0.0) {
                    firstValue / secondValue
                } else {
                    currentInput = "Error"
                    tvHistory.text = ""
                    updateDisplay()
                    return
                }
            }
            "%" -> firstValue % secondValue
            else -> secondValue
        }

        tvHistory.text = "${formatNumber(firstValue)} $operator ${formatNumber(secondValue)} ="
        currentInput = formatResult(result)
        operator = ""
        isNewOperation = true
        updateDisplay()
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            // Limit decimal places for very long numbers
            if (result.toString().length > 15) {
                String.format("%.8f", result).trimEnd('0').trimEnd('.')
            } else {
                result.toString()
            }
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }

    private fun clear() {
        currentInput = "0"
        operator = ""
        firstValue = 0.0
        isNewOperation = true
        tvHistory.text = ""
        updateDisplay()
    }

    private fun delete() {
        currentInput = if (currentInput.length > 1) {
            currentInput.substring(0, currentInput.length - 1)
        } else {
            "0"
        }
        updateDisplay()
    }

    private fun addDecimal() {
        if (!currentInput.contains(".")) {
            currentInput += "."
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        tvDisplay.text = currentInput

        // Auto-scroll to the end (right side) to show the latest digits
        tvDisplay.post {
            val scrollView = tvDisplay.parent as? HorizontalScrollView
            scrollView?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }
}

package com.calculadora.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust padding based on system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navigate to SecondActivity when the button is clicked
        val ingresarButton = findViewById<Button>(R.id.button)
        ingresarButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}

class SecondActivity : AppCompatActivity() {

    private var expression: String = ""
    private var isResultShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Referencias a los TextView
        val tvDisplay = findViewById<TextView>(R.id.tv_num1)
        val tvExpression = findViewById<TextView>(R.id.tv_num2)

        // Función para actualizar los TextViews
        fun updateDisplay() {
            tvExpression.text = expression
            if (isResultShown) {
                tvDisplay.text = evaluateExpression(expression)
            } else {
                tvDisplay.text = ""
            }
        }

        // Botones numéricos
        val numberButtons = listOf(
            findViewById<Button>(R.id.btn_0),
            findViewById<Button>(R.id.btn_1),
            findViewById<Button>(R.id.btn_2),
            findViewById<Button>(R.id.btn_3),
            findViewById<Button>(R.id.btn_4),
            findViewById<Button>(R.id.btn_5),
            findViewById<Button>(R.id.btn_6),
            findViewById<Button>(R.id.btn_7),
            findViewById<Button>(R.id.btn_8),
            findViewById<Button>(R.id.btn_9)
        )

        // Acción para los botones numéricos
        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (isResultShown) {
                    expression = ""
                    isResultShown = false
                }
                expression += index.toString()
                updateDisplay()
            }
        }

        // Botones de operaciones
        findViewById<Button>(R.id.btn_suma).setOnClickListener { appendOperation("+") }
        findViewById<Button>(R.id.btn_resta).setOnClickListener { appendOperation("-") }
        findViewById<Button>(R.id.btn_multiplicacion).setOnClickListener { appendOperation("*") }
        findViewById<Button>(R.id.btn_division).setOnClickListener { appendOperation("/") }

        // Botón de resultado
        findViewById<Button>(R.id.btn_resultado).setOnClickListener {
            tvDisplay.text = evaluateExpression(expression)
            isResultShown = true
        }

        // Botón para limpiar
        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            clearAll()
            updateDisplay()
        }
    }

    private fun appendOperation(op: String) {
        if (expression.isNotEmpty() && !isResultShown) {
            expression += " $op "
        }
    }

    private fun evaluateExpression(expr: String): String {
        return try {
            val result = calculate(expr)
            if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }
        } catch (e: Exception) {
            "Error"
        }
    }


    private fun calculate(expr: String): Double {
        val tokens = expr.split(" ").toTypedArray()
        val values: Stack<Double> = Stack()
        val ops: Stack<String> = Stack()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> {
                    values.push(token.toDouble())
                }
                token == "(" -> {
                    ops.push(token)
                }
                token == ")" -> {
                    while (ops.peek() != "(") {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.pop()
                }
                token == "+" || token == "-" || token == "*" || token == "/" -> {
                    while (!ops.isEmpty() && hasPrecedence(token, ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.push(token)
                }
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun applyOp(op: String, b: Double, a: Double): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b == 0.0) throw UnsupportedOperationException("No se puede dividir por 0") else a / b
            else -> 0.0
        }
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op2 == "(" || op2 == ")") {
            return false
        }
        if ((op1 == "*" || op1 == "/") && (op2 == "+" || op2 == "-")) {
            return false
        }
        return true
    }

    private fun clearAll() {
        expression = ""
        isResultShown = false
    }
}

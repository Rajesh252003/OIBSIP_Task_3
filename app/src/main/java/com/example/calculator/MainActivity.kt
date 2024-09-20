package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var userInput: TextView
    private lateinit var display: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userInput = findViewById(R.id.userInput)
        display = findViewById(R.id.display)

        val buttonAC: Button = findViewById(R.id.buttonAC)
        val buttonC: Button = findViewById(R.id.buttonC)
        val buttonDivide: Button = findViewById(R.id.buttonDivide)
        val buttonMultiply: Button = findViewById(R.id.buttonMultiply)
        val buttonSubtract: Button = findViewById(R.id.buttonSubtract)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        val buttonDot: Button = findViewById(R.id.buttonDot)
        val buttonEquals: Button = findViewById(R.id.buttonEquals)
        val button0: Button = findViewById(R.id.button0)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)

        val buttons = listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)

        buttons.forEach { button ->
            button.setOnClickListener {
                userInput.append(button.text)
            }
        }

        buttonDot.setOnClickListener {
            userInput.append(".")
        }

        buttonDivide.setOnClickListener {
            userInput.append(" / ")
        }
        buttonMultiply.setOnClickListener {
            userInput.append(" * ")
        }
        buttonSubtract.setOnClickListener {
            userInput.append(" - ")
        }
        buttonAdd.setOnClickListener {
            userInput.append(" + ")
        }

        buttonAC.setOnClickListener {
            userInput.text = ""
            display.text = ""
        }

        buttonC.setOnClickListener {
            val text = userInput.text.toString()
            if (text.isNotEmpty()) {
                userInput.text = text.substring(0, text.length - 1)
            }
        }

        buttonEquals.setOnClickListener {
            val input = userInput.text.toString()
            val result = try {
                calculate(input)
            } catch (e: Exception) {
                "Error"
            }
            display.text = result
        }
    }

    private fun calculate(expression: String): String {
        val cleanedExpr = expression.replace(" ", "")
        return try {
            val result = eval(cleanedExpr)

            if (result == result.toInt().toDouble()) {
                result.toInt().toString() // Return integer if it's a whole number
            } else {
                result.toString() // Return decimal number otherwise
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun eval(expr: String): Double {
        val tokens = tokenize(expr)
        val postfix = infixToPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expr: String): List<String> {
        val regex = """(\d+(\.\d+)?)|(\S)""".toRegex()
        return regex.findAll(expr).map { it.value }.toList()
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val precedence = mapOf(
            "+" to 1, "-" to 1,
            "*" to 2, "/" to 2
        )
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                token.matches("\\d+(\\.\\d+)?".toRegex()) -> output.add(token)
                token in precedence -> {
                    while (operators.isNotEmpty() && operators.peek() in precedence &&
                        precedence[token]!! <= precedence[operators.peek()]!!) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    private fun evaluatePostfix(tokens: List<String>): Double {
        val stack = Stack<Double>()

        for (token in tokens) {
            when {
                token.matches("\\d+(\\.\\d+)?".toRegex()) -> stack.push(token.toDouble())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    }
                    stack.push(result)
                }
            }
        }

        return stack.pop()
    }
}

package com.learning.newuserkk.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputField.showSoftInputOnFocus = false

        setInputButtonListeners()
        setSpecialButtonListeners()

        // TODO: set common listener to update result
        // TODO: exceptions
        // TODO: fix mod
        // TODO: fix div
        // TODO: add log
        // TODO: add module
        // TODO: add trigonometry
    }

    private fun setInputButtonListeners() {
        button0.setOnClickListener { addTextOnSelection("0") }
        button1.setOnClickListener { addTextOnSelection("1") }
        button2.setOnClickListener { addTextOnSelection("2") }
        button3.setOnClickListener { addTextOnSelection("3") }
        button4.setOnClickListener { addTextOnSelection("4") }
        button5.setOnClickListener { addTextOnSelection("5") }
        button6.setOnClickListener { addTextOnSelection("6") }
        button7.setOnClickListener { addTextOnSelection("7") }
        button8.setOnClickListener { addTextOnSelection("8") }
        button9.setOnClickListener { addTextOnSelection("9") }
        factorialButton.setOnClickListener { addTextOnSelection("!") }
        openingBracketButton.setOnClickListener { addTextOnSelection("(") }
        closingBracketButton.setOnClickListener { addTextOnSelection(")") }
        divideButton.setOnClickListener { addTextOnSelection("/") }
        mulButton.setOnClickListener { addTextOnSelection("*") }
        minusButton.setOnClickListener { addTextOnSelection("-") }
        plusButton.setOnClickListener { addTextOnSelection("+") }
        sqrtButton.setOnClickListener {
            addTextOnSelection("\u221A(")
            // TODO: check for existing buttons
            inputField.setSelection(inputField.selectionStart)
        }
        powButton.setOnClickListener { addTextOnSelection("^") }
        modButton.setOnClickListener { addTextOnSelection("%") }
        pointButton.setOnClickListener { addTextOnSelection(".") }
    }

    private fun setSpecialButtonListeners() {
        clearButton.setOnClickListener {
            inputField.text.clear()
            setEvaluationResult()
        }
        backspaceButton.setOnClickListener {
            removeSelected()
            setEvaluationResult()
        }
        evaluateButton.setOnClickListener { setEvaluationResult() }
    }


    private fun addTextOnSelection(s: String) {
        val currentText = inputField.editableText
        val selection = inputField.selectionEnd
        inputField.text = currentText.insert(selection, s)
        inputField.setSelection(selection + s.length)
        setEvaluationResult()
     }

    private fun removeSelected() {
        val currentText = inputField.editableText
        val selectionStart = inputField.selectionStart
        val selectionEnd = inputField.selectionEnd

        var deletionStart = selectionStart
        if (selectionStart == selectionEnd) {
            deletionStart = if (selectionStart != 0) selectionStart - 1 else 0
        }

        inputField.text = currentText.delete(deletionStart, selectionEnd)
        inputField.setSelection(deletionStart)
    }

    private fun setEvaluationResult() {
        val currentExpression = inputField.text.toString()
        val expr = Expression(currentExpression.replace("\u221A", "sqrt"))
        val result = expr.calculate()
        val isValidResult = (!result.isNaN() && !result.isInfinite())

        var resultString = if (isValidResult) result.toString() else ""

        if (resultString.endsWith(".0")) {
            resultString = resultString.substringBefore(".")
        }

        resultField.text = resultString
    }
}

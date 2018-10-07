package com.learning.newuserkk.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.widget.Button
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression


class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "MainActivity"
    }

    private var calculationResult: String = ""
    private var isValidResult: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputField.showSoftInputOnFocus = false

        setInputButtonListeners()
        setSpecialButtonListeners()

        val typeface = ResourcesCompat.getFont(this, R.font.dejavu_sans)
        (backspaceButton as Button).typeface = typeface

        calculationResult = savedInstanceState?.getString("calculationResult") ?: ""
        isValidResult = savedInstanceState?.getBoolean("isValidResult") ?: false
        resultField.text = calculationResult
        val validTextColor = ResourcesCompat.getColor(resources, R.color.colorTextLight, null)
        val nonValidTextColor = ResourcesCompat.getColor(resources, R.color.colorTextNotValid, null)
        resultField.setTextColor(if (isValidResult) validTextColor else nonValidTextColor)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("calculationResult", calculationResult)
        outState?.putBoolean("isValidResult", isValidResult)
        super.onSaveInstanceState(outState)
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
        expButton.setOnClickListener { addTextOnSelection("e") }
        piButton.setOnClickListener { addTextOnSelection("\u03C0") }
        openingBracketButton.setOnClickListener { addTextOnSelection("(") }
        closingBracketButton.setOnClickListener { addTextOnSelection(")") }
        divideButton.setOnClickListener { addTextOnSelection("/") }
        mulButton.setOnClickListener { addTextOnSelection("*") }
        minusButton.setOnClickListener { addTextOnSelection("-") }
        plusButton.setOnClickListener { addTextOnSelection("+") }
        sqrtButton.setOnClickListener { addTextOnSelection("\u221A") }
        powButton.setOnClickListener { addTextOnSelection("^") }
        percentButton?.setOnClickListener { addTextOnSelection("%") }
        pointButton.setOnClickListener { addTextOnSelection(".") }
    }

    private fun setSpecialButtonListeners() {
        clearButton.setOnClickListener {
            inputField.text.clear()
            updateResult()
        }

        backspaceButton.setOnClickListener {
            removeSelected()
        }

        evaluateButton.setOnClickListener {
            updateResult()
            if (isValidResult) {
                inputField.setText(calculationResult)
                inputField.selectAll()
            }
        }
    }


    private fun addTextOnSelection(s: String) {
        if (inputField.selectionStart != inputField.selectionEnd) {
            removeSelected()
        }

        var textToAdd = s
        val op = getOperation(s)
        val isComplexOperation = (op != null && !op.isInfix)
        if (isComplexOperation) {
            if (getNextSymbol(inputField) != '(') {
                textToAdd += "()"
            }
        }

        val currentText = inputField.editableText
        val selectionEnd = inputField.selectionEnd
        inputField.text = currentText.insert(selectionEnd, textToAdd)
        inputField.setSelection(selectionEnd + textToAdd.length)
        if (isComplexOperation) {
            inputField.setSelection(inputField.selectionEnd - 1)
        }
        updateResult()
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
        updateResult()
    }

    private fun updateResult() {
        val currentExpression = inputField.text.toString()
        val result = evaluate(currentExpression)
        var resultString = ""

        if (result != null) {
            isValidResult = true
            resultString = result.toString()

        } else {
            isValidResult = false
            if (inputField.text.toString() != "") {
                resultString = calculationResult
            }
        }

        val floatDigitsPattern = "(\\d{9,}(?=E))".toRegex()
        val matchResult = floatDigitsPattern.find(resultString)?.value
        if (matchResult != null) {
            resultString = resultString.replace(matchResult, matchResult.substring(0, 8))
        }

        if (resultString.endsWith(".0")) {
            resultString = resultString.substringBefore(".")
        }

        calculationResult = resultString

        resultField.text = calculationResult

        val validTextColor = ResourcesCompat.getColor(resources, R.color.colorTextLight, null)
        val nonValidTextColor = ResourcesCompat.getColor(resources, R.color.colorTextNotValid, null)
        resultField.setTextColor(if (isValidResult) validTextColor else nonValidTextColor)
    }

    private fun evaluate(expression: String): Double? {
        val expr = Expression(replaceEntities(expression))
        return if (expr.checkSyntax()) {
            expr.calculate()
        } else null
    }

    private fun replaceEntities(expression: String) : String {
        var replacedExpression = expression
        for (op: Operation in Operation.values()) {
            if (op.alias != null) {
                replacedExpression = replacedExpression.replace(op.value, op.alias)
            }
        }
        return replacedExpression
    }

    private fun getNextSymbol(editText: EditText): Char? {
        val currentText = editText.editableText
        val selectionEnd = editText.selectionEnd
        return if (selectionEnd != currentText.length) currentText[selectionEnd] else null
    }

    private fun getOperation(token: String): Operation? {
        for (op: Operation in Operation.values()) {
            if (token == op.value || token == op.alias) {
                return op
            }
        }
        return null
    }

    private fun isOperation(token: String): Boolean {
        return getOperation(token) != null
    }
}

package com.learning.newuserkk.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

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

        // TODO: exceptions
        // TODO: fix markup for some dimensions
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
            addTextOnSelection("\u221A")
            inputField.setSelection(inputField.selectionStart)
        }

        powButton.setOnClickListener { addTextOnSelection("^") }
        modButton.setOnClickListener { addTextOnSelection("%") }
        pointButton.setOnClickListener { addTextOnSelection(".") }
    }

    private fun setSpecialButtonListeners() {
        clearButton.setOnClickListener {
            inputField.text.clear()
            updateResult()
        }

        backspaceButton.setOnClickListener { removeSelected() }
        evaluateButton.setOnClickListener { updateResult() }
    }


    private fun addTextOnSelection(s: String) {
        if (inputField.selectionStart != inputField.selectionEnd) {
            removeSelected()
        }

        var textToAdd = s
        if (isOperation(s)) {
            if (getNextSymbol(inputField) != '(') {
                textToAdd += '('
            }
        }

        val currentText = inputField.editableText
        val selectionEnd = inputField.selectionEnd
        inputField.text = currentText.insert(selectionEnd, textToAdd)
        inputField.setSelection(selectionEnd + textToAdd.length)
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
        val isValidResult = (!result.isNaN() && !result.isInfinite())

        var resultString = if (isValidResult) {
            result.toString()
        } else {
            ""
        }

        val floatDigitsPattern = "(\\d{9,}(?=E))".toRegex()
        val matchResult = floatDigitsPattern.find(resultString)?.value
        if (matchResult != null) {
            resultString = resultString.replace(matchResult, matchResult.substring(0, 8))
        }

        if (resultString.endsWith(".0")) {
            resultString = resultString.substringBefore(".")
        }

        resultField.text = resultString
    }

    private fun evaluate(expression: String) : Double {
        val expr = Expression(replaceEntities(expression))
        return expr.calculate()
    }

    private fun replaceEntities(expression: String) : String {
        var replacedExpression = expression
        for (op: OperationAlias in OperationAlias.values()) {
            if (op.alias != null) {
                replacedExpression = replacedExpression.replace(op.value, op.alias)
            }
        }
        return replacedExpression
    }

    private fun getNextSymbol(editText: EditText): Char? {
        val currentText = inputField.editableText
        val selectionEnd = inputField.selectionEnd
        return if (selectionEnd != currentText.length) currentText[selectionEnd] else null
    }

    private fun isOperation(token: String): Boolean {
        for (op: OperationAlias in OperationAlias.values()) {
            if (token == op.value || token == op.alias) {
                return true
            }
        }
        return false
    }
}

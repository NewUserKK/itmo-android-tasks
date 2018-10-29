package com.learning.newuserkk.calculator

enum class Operation(val value: String, val alias: String? = null, val isInfix: Boolean=false) {
    SQRT("\u221A", "sqrt"),
    PI("\u03C0", "pi", true)
}
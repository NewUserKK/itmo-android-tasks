package com.learning.newuserkk.calculator

enum class OperationAlias {

    SQRT("\u221A", "sqrt");

    val value: String
    val alias: String?

    constructor(value: String) {
        this.value = value
        this.alias = null
    }

    constructor(value: String, alias: String) {
        this.value = value
        this.alias = alias
    }
}
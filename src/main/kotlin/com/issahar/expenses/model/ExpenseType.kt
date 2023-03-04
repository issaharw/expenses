package com.issahar.expenses.model

enum class ExpenseType(val value:Int) {
    Bank(1),
    CreditCardIsrael(2),
    CreditCardAbroad(3);

    companion object {
        fun fromValue(value: Int): ExpenseType {
            for (type in ExpenseType.values()) {
                if (value == type.value)
                    return type
            }
            return Bank
        }
    }
}
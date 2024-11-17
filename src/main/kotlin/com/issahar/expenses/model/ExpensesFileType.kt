package com.issahar.expenses.model

enum class ExpensesFileType(val value: String) {
    BankPoalimTransactions("poalimTransactions"),
    CreditCardFromPoalim("poalimCard"),
    CreditCardFromMax("maxCard"),
    CreditCardFromCal("calCard"),
    Unknown("unknown");

    companion object {
        fun fromValue(value: String): ExpensesFileType {
            for (type in ExpensesFileType.values()) {
                if (value == type.value)
                    return type
            }
            return Unknown
        }
    }
}
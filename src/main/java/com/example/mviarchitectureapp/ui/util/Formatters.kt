package com.example.mviarchitectureapp.ui.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

fun Double.formatCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(this)
}

fun Double.signedAmount(): String {
    val formatted = abs(this).formatCurrency()
    return if (this >= 0) "+$formatted" else "-$formatted"
}

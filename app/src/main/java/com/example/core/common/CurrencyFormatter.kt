package com.example.core.common

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Formats financial amounts using integer minor units (cents) to avoid floating-point inaccuracies.
 * Supports configurable demonstration currency (default USD).
 */
object CurrencyFormatter {
    private const val DEFAULT_CURRENCY_CODE = "USD"

    fun formatCents(cents: Long, currencyCode: String = DEFAULT_CURRENCY_CODE): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale.US).apply {
                currency = Currency.getInstance(currencyCode)
                maximumFractionDigits = if (cents % 100 == 0L) 0 else 2
                minimumFractionDigits = if (cents % 100 == 0L) 0 else 2
            }
            format.format(cents / 100.0)
        } catch (_: Exception) {
            "$$cents"
        }
    }

    fun formatDollars(dollars: Int, currencyCode: String = DEFAULT_CURRENCY_CODE): String {
        return formatCents(dollars * 100L, currencyCode)
    }
}

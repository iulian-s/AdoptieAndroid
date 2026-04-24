package com.example.adoptie

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val RO_LOCALE = Locale("ro", "RO")

/**
 * Convertește un timestamp ISO 8601 (ex: "2025-12-09T...") în format relativ sau Ziuă/Lună.
 * Compatibil cu API Level 24.
 */
fun formatRelativeDate(isoDateTimeString: String?): String {
    if (isoDateTimeString.isNullOrEmpty()) {
        return ""
    }

    val dateToParse = isoDateTimeString.substringBeforeLast(".")

    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    val updatedDate: Date = try {
        parser.parse(dateToParse) ?: return dateToParse
    } catch (e: ParseException) {
        return isoDateTimeString.substringBefore("T")
    } catch (e: Exception) {
        return ""
    }

    val todayMidnight = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val updatedMidnight = Calendar.getInstance().apply {
        time = updatedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val diffMillis = todayMidnight - updatedMidnight
    val daysBetween = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS)

    return when (daysBetween) {
        0L -> "Astăzi"
        1L -> "Ieri"
        in 2..7 -> "Acum $daysBetween zile"
        else -> {
            val dayMonthFormatter = SimpleDateFormat("dd MMM.", RO_LOCALE)
            dayMonthFormatter.format(updatedDate)
        }
    }
}
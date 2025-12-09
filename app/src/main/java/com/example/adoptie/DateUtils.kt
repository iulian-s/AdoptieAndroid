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

    // Simplificăm șirul pentru a elimina microsecundele, deoarece SimpleDateFormat nu le poate parsa fiabil.
    val dateToParse = isoDateTimeString.substringBeforeLast(".")

    // Formatul de intrare (ISO 8601)
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    val updatedDate: Date = try {
        parser.parse(dateToParse) ?: return dateToParse
    } catch (e: ParseException) {
        // Dacă parsarea eșuează, afișăm doar data neformatată (partea de YYYY-MM-DD)
        return isoDateTimeString.substringBefore("T")
    } catch (e: Exception) {
        return ""
    }

    // 1. Pregătim datele pentru comparație (setăm ora la miezul nopții)
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

    // 2. Calculează diferența în zile
    val diffMillis = todayMidnight - updatedMidnight
    val daysBetween = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS)

    // 3. Returnează string-ul formatat
    return when (daysBetween) {
        0L -> "Astăzi"
        1L -> "Ieri"
        in 2..7 -> "Acum $daysBetween zile"
        else -> {
            // Afișează ziua și luna (ex: 09 Dec.)
            // Folosim "dd MMM." pentru a obține "09 Dec." (sau echivalentul local)
            val dayMonthFormatter = SimpleDateFormat("dd MMM.", RO_LOCALE)
            dayMonthFormatter.format(updatedDate)
        }
    }
}
package com.example.data.magic

import com.example.data.model.CelestialPlanet
import com.example.data.model.PlanetaryHourInfo
import java.util.Calendar
import java.util.Locale

/**
 * Historical 17th-century Planetary Hours Calculator based on the Chaldean celestial order
 * and the works of Cornelius Agrippa (De Occulta Philosophia, 1533) and the Picatrix.
 */
object PlanetaryHourCalculator {

    // Classical Chaldean descending order of planetary spheres
    val CHALDEAN_ORDER = listOf(
        CelestialPlanet.SATURN,
        CelestialPlanet.JUPITER,
        CelestialPlanet.MARS,
        CelestialPlanet.SOL,
        CelestialPlanet.VENUS,
        CelestialPlanet.MERCURY,
        CelestialPlanet.LUNA
    )

    // First planetary hour of each weekday starting at 6:00 AM
    private val DAY_RULERS = mapOf(
        Calendar.SUNDAY to (CelestialPlanet.SOL to "Dies Solis"),
        Calendar.MONDAY to (CelestialPlanet.LUNA to "Dies Lunæ"),
        Calendar.TUESDAY to (CelestialPlanet.MARS to "Dies Martis"),
        Calendar.WEDNESDAY to (CelestialPlanet.MERCURY to "Dies Mercurii"),
        Calendar.THURSDAY to (CelestialPlanet.JUPITER to "Dies Iovis"),
        Calendar.FRIDAY to (CelestialPlanet.VENUS to "Dies Veneris"),
        Calendar.SATURDAY to (CelestialPlanet.SATURN to "Dies Saturni")
    )

    /**
     * Calculates the current planetary hour based on calendar time.
     */
    fun calculateCurrentPlanetaryHour(calendar: Calendar = Calendar.getInstance()): PlanetaryHourInfo {
        val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Day ruler starts at 6:00 AM (Sunrise benchmark)
        // If before 6:00 AM, the magical day still belongs to the previous astrological day!
        val effectiveDayOfWeek = if (currentHour24 < 6) {
            if (dayOfWeek == Calendar.SUNDAY) Calendar.SATURDAY else dayOfWeek - 1
        } else {
            dayOfWeek
        }

        val (dayRuler, dayLatinName) = DAY_RULERS[effectiveDayOfWeek] ?: (CelestialPlanet.SOL to "Dies Solis")

        // Magical hour index from 0 to 23 (where 0 is 6:00 AM)
        val hourFromDawn = (currentHour24 - 6 + 24) % 24

        // Index in Chaldean order of the day's first ruler
        val startChaldeanIdx = CHALDEAN_ORDER.indexOf(dayRuler)

        // The current hour's ruling planet in the descending cycle
        val currentPlanetIdx = (startChaldeanIdx + hourFromDawn) % CHALDEAN_ORDER.size
        val currentPlanet = CHALDEAN_ORDER[currentPlanetIdx]

        val isDiurnal = hourFromDawn < 12
        val hourNumber = hourFromDawn + 1

        val startHourClock = currentHour24
        val endHourClock = (currentHour24 + 1) % 24
        val startTimeStr = String.format(Locale.US, "%02d:00", startHourClock)
        val endTimeStr = String.format(Locale.US, "%02d:00", endHourClock)
        val minutesRemaining = 60 - currentMinute

        return PlanetaryHourInfo(
            hourNumber = hourNumber,
            isDiurnal = isDiurnal,
            planet = currentPlanet,
            startTimeFormatted = startTimeStr,
            endTimeFormatted = endTimeStr,
            dayRulerPlanet = dayRuler,
            dayNameLatin = dayLatinName,
            minutesRemaining = minutesRemaining
        )
    }

    /**
     * Returns the 24 hours schedule for the active astrological day.
     */
    fun getFullDaySchedule(calendar: Calendar = Calendar.getInstance()): List<PlanetaryHourInfo> {
        val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val effectiveDayOfWeek = if (currentHour24 < 6) {
            if (dayOfWeek == Calendar.SUNDAY) Calendar.SATURDAY else dayOfWeek - 1
        } else {
            dayOfWeek
        }

        val (dayRuler, dayLatinName) = DAY_RULERS[effectiveDayOfWeek] ?: (CelestialPlanet.SOL to "Dies Solis")
        val startChaldeanIdx = CHALDEAN_ORDER.indexOf(dayRuler)

        return (0 until 24).map { hIdx ->
            val clockHour = (6 + hIdx) % 24
            val nextClockHour = (clockHour + 1) % 24
            val planetIdx = (startChaldeanIdx + hIdx) % CHALDEAN_ORDER.size
            val planet = CHALDEAN_ORDER[planetIdx]

            PlanetaryHourInfo(
                hourNumber = hIdx + 1,
                isDiurnal = hIdx < 12,
                planet = planet,
                startTimeFormatted = String.format(Locale.US, "%02d:00", clockHour),
                endTimeFormatted = String.format(Locale.US, "%02d:00", nextClockHour),
                dayRulerPlanet = dayRuler,
                dayNameLatin = dayLatinName,
                minutesRemaining = 0
            )
        }
    }
}

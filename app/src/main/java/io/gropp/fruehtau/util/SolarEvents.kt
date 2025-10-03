package io.gropp.fruehtau.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.roundToLong
import kotlin.math.sin

data class SolarEvents(
    // null in polar day/night
    val sunrise: ZonedDateTime?,
    // null in polar day/night
    val sunset: ZonedDateTime?,
    // true (apparent) solar noon
    val solarNoon: ZonedDateTime,
    // null in polar conditions
    val daylight: Duration?,
    val dayType: DayType,

    // Twilight intervals
    val civilMorning: Interval,
    val civilEvening: Interval,
    val nauticalMorning: Interval,
    val nauticalEvening: Interval,
    val astronomicalMorning: Interval,
    val astronomicalEvening: Interval,

    // Golden hour
    val goldenHourMorning: Interval,
    val goldenHourEvening: Interval,
) {
    enum class DayType {
        NORMAL,
        POLAR_DAY,
        POLAR_NIGHT,
    }
}

/** Generic interval container. */
data class Interval(val start: ZonedDateTime?, val end: ZonedDateTime?)

/**
 * Compute sunrise, sunset, solar noon, twilight, and golden hour.
 *
 * @param date Local calendar date.
 * @param zone Zone for outputs.
 * @param latitude Degrees (+N / -S).
 * @param longitude Degrees (+E / -W).
 * @param goldenTopDeg Upper bound for golden hour (default +6°).
 * @param goldenBottomDeg Lower bound for golden hour (default -4°).
 */
fun calculateSolarEvents(
    date: LocalDate,
    zone: ZoneId,
    latitude: Double,
    longitude: Double,
    goldenTopDeg: Double = 6.0,
    goldenBottomDeg: Double = -4.0,
): SolarEvents {
    // ---- constants & helpers ----
    val j2000 = 2451545.0
    val deg = Math.PI / 180.0
    val hSunrise = Math.toRadians(-0.833)

    fun sinD(x: Double) = sin(x * deg)
    fun normalizeAngle(a: Double): Double {
        var x = a % 360.0
        if (x < 0) x += 360.0
        return x
    }

    fun jdToZoned(jd: Double): ZonedDateTime {
        val daysSinceUnix = jd - 2440587.5
        val instant = Instant.ofEpochMilli((daysSinceUnix * 86400000.0).roundToLong())
        return instant.atZone(ZoneOffset.UTC).withZoneSameInstant(zone)
    }

    val jd0 = 2440587.5 + (date.atStartOfDay(ZoneOffset.UTC).toEpochSecond() / 86400.0)
    val lw = -longitude / 360.0
    val n = round(jd0 - j2000 - 0.0009 - lw)
    val jStar = j2000 + 0.0009 + lw + n
    val m = normalizeAngle(357.5291 + 0.98560028 * (jStar - j2000))
    val c = 1.9148 * sinD(m) + 0.0200 * sinD(2 * m) + 0.0003 * sinD(3 * m)
    val lambda = normalizeAngle(m + c + 102.9372 + 180.0)
    val delta = asin(sinD(lambda) * sinD(23.44))
    val phi = latitude * deg
    val jTransit = jStar + 0.0053 * sinD(m) - 0.0069 * sinD(2 * lambda)

    fun crossingsAtElevation(elevDeg: Double): Pair<Double?, Double?> {
        val h = Math.toRadians(elevDeg)
        val cosH = (sin(h) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
        if (cosH < -1.0 || cosH > 1.0) return Pair(null, null)
        val hFrac = acos(cosH) / (2.0 * Math.PI)
        return Pair(jTransit - hFrac, jTransit + hFrac)
    }

    // --- core events ---
    val (jRise0, jSet0) = crossingsAtElevation(Math.toDegrees(hSunrise))
    val solarNoon = jdToZoned(jTransit)
    val sunrise = jRise0?.let(::jdToZoned)
    val sunset = jSet0?.let(::jdToZoned)
    val dayType =
        when {
            jRise0 == null && jSet0 == null -> {
                // decide by checking 0° elevation reachability
                val (r0, s0) = crossingsAtElevation(0.0)
                if (r0 == null && s0 == null && sin(phi) * sin(delta) + cos(phi) * cos(delta) > 0)
                    SolarEvents.DayType.POLAR_DAY
                else SolarEvents.DayType.POLAR_NIGHT
            }
            else -> SolarEvents.DayType.NORMAL
        }
    val daylight = if (sunrise != null && sunset != null) Duration.between(sunrise, sunset) else null

    // --- twilight + golden ---
    val (civilRise, civilSet) = crossingsAtElevation(-6.0)
    val (nauticalRise, nauticalSet) = crossingsAtElevation(-12.0)
    val (astroRise, astroSet) = crossingsAtElevation(-18.0)
    val (goldBottomRise, goldBottomSet) = crossingsAtElevation(goldenBottomDeg)
    val (goldTopRise, goldTopSet) = crossingsAtElevation(goldenTopDeg)

    val civilMorning = Interval(civilRise?.let(::jdToZoned), sunrise)
    val civilEvening = Interval(sunset, civilSet?.let(::jdToZoned))
    val nauticalMorning = Interval(nauticalRise?.let(::jdToZoned), civilRise?.let(::jdToZoned))
    val nauticalEvening = Interval(civilSet?.let(::jdToZoned), nauticalSet?.let(::jdToZoned))
    val astronomicalMorning = Interval(astroRise?.let(::jdToZoned), nauticalRise?.let(::jdToZoned))
    val astronomicalEvening = Interval(nauticalSet?.let(::jdToZoned), astroSet?.let(::jdToZoned))
    val goldenHourMorning = Interval(goldBottomRise?.let(::jdToZoned), goldTopRise?.let(::jdToZoned))
    val goldenHourEvening = Interval(goldTopSet?.let(::jdToZoned), goldBottomSet?.let(::jdToZoned))

    return SolarEvents(
        sunrise,
        sunset,
        solarNoon,
        daylight,
        dayType,
        civilMorning,
        civilEvening,
        nauticalMorning,
        nauticalEvening,
        astronomicalMorning,
        astronomicalEvening,
        goldenHourMorning,
        goldenHourEvening,
    )
}

fun getNextSunriseAndSunset(latitude: Double, longitude: Double): NextSunriseAndSunset? {
    val now = ZonedDateTime.now()
    val today = now.toLocalDate()
    val tomorrow = today.plus(1, ChronoUnit.DAYS)

    val solarToday = calculateSolarEvents(today, ZoneId.systemDefault(), latitude, longitude)
    val solarTomorrow = calculateSolarEvents(tomorrow, ZoneId.systemDefault(), latitude, longitude)

    val sunrise = solarToday.sunrise?.takeIf { it.isAfter(now) } ?: solarTomorrow.sunrise
    val sunset = solarToday.sunset?.takeIf { it.isAfter(now) } ?: solarTomorrow.sunset

    return sunrise?.let { sunrise ->
        sunset?.let { sunset -> NextSunriseAndSunset(sunrise.toLocalDateTime(), sunset.toLocalDateTime()) }
    }
}

data class NextSunriseAndSunset(val sunrise: LocalDateTime, val sunset: LocalDateTime) {
    val sunriseIsNext: Boolean
        get() = sunrise.isBefore(sunset)

    val next: LocalDateTime
        get() = if (sunriseIsNext) sunrise else sunset
}

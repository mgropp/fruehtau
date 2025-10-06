package io.gropp.fruehtau.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gropp.fruehtau.service.LocationService
import io.gropp.fruehtau.ui.location.WithLocation
import io.gropp.fruehtau.util.NextSunriseAndSunset
import io.gropp.fruehtau.util.getNextSunriseAndSunset
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun SunExtraInfo(locationService: LocationService, modifier: Modifier = Modifier) {
    locationService.WithLocation { location ->
        location?.let { location ->
            val date = LocalDate.now()
            val sunriseSunset =
                remember(location, date) { getNextSunriseAndSunset(location.latitude, location.longitude) }

            var timeUntilSunriseOrSunset by remember { mutableStateOf<Duration?>(null) }

            LaunchedEffect(Unit) {
                while (true) {
                    timeUntilSunriseOrSunset = sunriseSunset?.next?.let { Duration.between(LocalDateTime.now(), it) }

                    delay(1000)
                }
            }

            SunExtraInfoText(sunriseSunset, timeUntilSunriseOrSunset, modifier)
        }
    }
}

@Composable
private fun SunExtraInfoText(
    sunriseSunset: NextSunriseAndSunset?,
    timeUntilSunriseOrSunset: Duration?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        Icon(Icons.Default.WbTwilight, "Sun", tint = Color.White)
        Column {
            Text(
                timeUntilSunriseOrSunset.formatHHMM(),
                color = Color.White,
                style = TextStyle(fontSize = 14.sp, lineHeight = 15.sp),
            )
            Text(
                if (sunriseSunset?.sunriseIsNext != false) "until sunrise" else "until sunset",
                color = Color.White,
                style = TextStyle(fontSize = 12.sp, lineHeight = 14.sp),
            )
        }
    }
}

private fun Duration?.formatHHMM(
    numberStyle: SpanStyle = SpanStyle(fontSize = 14.sp),
    unitStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
    missingStyle: SpanStyle = SpanStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic),
): AnnotatedString =
    this?.toMinutes()?.let { minutes ->
        buildAnnotatedString {
            withStyle(numberStyle) { append(String.format(Locale.ROOT, "%02d", minutes / 60)) }
            withStyle(unitStyle) { append("h") }
            withStyle(numberStyle) { append(String.format(Locale.ROOT, " : %02d", minutes % 60)) }
            withStyle(unitStyle) { append("m") }
        }
    } ?: buildAnnotatedString { withStyle(missingStyle) { append("?") } }

@Preview
@Composable
private fun SunExtraInfoTextPreview() {
    Box(Modifier.background(Color.DarkGray).padding(10.dp)) {
        SunExtraInfoText(
            NextSunriseAndSunset(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)),
            Duration.ofMinutes(171),
        )
    }
}

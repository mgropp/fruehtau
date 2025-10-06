package io.gropp.fruehtau.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gropp.fruehtau.service.Location
import io.gropp.fruehtau.service.LocationService
import io.gropp.fruehtau.ui.location.WithLocation
import java.util.Locale

@Composable
fun LocationInfoDisplay(locationService: LocationService, modifier: Modifier = Modifier) {
    locationService.WithLocation { location -> LocationInfoDisplayText(location, modifier) }
}

@Composable
fun LocationInfoDisplayText(location: Location?, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        Icon(Icons.Default.PinDrop, "Sun", tint = Color.White)
        Text(location.format(), color = Color.Companion.White, modifier = modifier)
    }
}

private fun Location?.format(
    numberParagraphStyle: ParagraphStyle = ParagraphStyle(lineHeight = 14.sp),
    numberSpanStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
): AnnotatedString =
    if (this != null) {
        buildAnnotatedString {
            withStyle(numberParagraphStyle) {
                withStyle(numberSpanStyle) { append(String.format(Locale.ROOT, "%.6f\n%.6f", latitude, longitude)) }
            }
        }
    } else {
        AnnotatedString("No location")
    }

@Preview
@Composable
private fun LocationInfoDisplayTextPreview() {
    Box(Modifier.background(Color.DarkGray).padding(10.dp)) { LocationInfoDisplayText(Location(48.978747, 13.389318)) }
}

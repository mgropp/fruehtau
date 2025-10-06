package io.gropp.fruehtau.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.gropp.fruehtau.ui.common.LoopingHorizontalPager

@Composable
fun InfoDisplay(
    modifier: Modifier = Modifier,
    viewModel: InfoDisplayViewModel = hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current), null),
) {
    val mode by viewModel.infoDisplayMode.collectAsState()

    LoopingHorizontalPager(
        initialPage = mode?.ordinal ?: 0,
        pageCount = InfoDisplayMode.entries.size,
        modifier = modifier.width(200.dp),
        onPageChanged = { page -> viewModel.setInfoDisplayMode(InfoDisplayMode.entries[page]) },
    ) { page ->
        key(page) {
            val pageMode = InfoDisplayMode.entries[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(0.dp),
            ) {
                when (pageMode) {
                    InfoDisplayMode.Sun -> SunExtraInfo(viewModel.locationService, modifier)
                    InfoDisplayMode.Location -> LocationInfoDisplay(viewModel.locationService, modifier)
                }
            }
        }
    }
}

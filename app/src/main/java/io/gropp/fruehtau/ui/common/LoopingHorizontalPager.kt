package io.gropp.fruehtau.ui.common

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.math.absoluteValue

@Composable
fun LoopingHorizontalPager(
    pageCount: Int,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageChanged: (page: Int) -> Unit = {},
    content: @Composable (page: Int) -> Unit,
) {
    require(initialPage >= 0 && initialPage < pageCount) {
        "initialPage is out of range 0 <= $initialPage < $pageCount"
    }

    val virtualCount = Int.MAX_VALUE
    val startVirtual =
        remember(pageCount) {
            val mid = virtualCount / 2
            mid - (mid % pageCount)
        }

    val pagerState = rememberPagerState(initialPage = startVirtual + initialPage, pageCount = { virtualCount })

    val realIndex = pagerState.currentPage % pageCount
    LaunchedEffect(realIndex) { onPageChanged(realIndex) }

    LaunchedEffect(pagerState.currentPage) {
        val distanceFromStart = (pagerState.currentPage - startVirtual).absoluteValue
        if (distanceFromStart > 64_000) {
            val newVirtual = startVirtual + realIndex
            pagerState.scrollToPage(newVirtual)
        }
    }

    HorizontalPager(state = pagerState, modifier = modifier) { virtualPage ->
        val page = virtualPage % pageCount
        content(page)
    }
}

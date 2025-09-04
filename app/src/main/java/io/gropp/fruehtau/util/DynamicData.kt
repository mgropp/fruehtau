@file:OptIn(ExperimentalCoroutinesApi::class)

package io.gropp.fruehtau.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

sealed interface DynamicData<out T> {
    object Empty : DynamicData<Nothing>

    object Loading : DynamicData<Nothing>

    data class Loaded<T>(val data: T) : DynamicData<T>
}

fun <T1, T2, R> combineDynamicData(
    state1: Flow<DynamicData<T1>>,
    state2: Flow<DynamicData<T2>>,
    action: suspend (T1, T2) -> R,
): Flow<DynamicData<R>> =
    combine(state1, state2) { s1, s2 -> Pair(s1, s2) }
        .flatMapLatest { (s1, s2) ->
            if (s1 == DynamicData.Loading || s2 == DynamicData.Loading) {
                flowOf(DynamicData.Loading)
            } else if (s1 is DynamicData.Loaded && s2 is DynamicData.Loaded) {
                flow<DynamicData<R>> {
                    emit(DynamicData.Loading)
                    emit(DynamicData.Loaded(action(s1.data, s2.data)))
                }
            } else {
                flowOf(DynamicData.Empty)
            }
        }

fun <T1, T2, T3, R> combineDynamicData(
    state1: Flow<DynamicData<T1>>,
    state2: Flow<DynamicData<T2>>,
    state3: Flow<DynamicData<T3>>,
    action: suspend (T1, T2, T3) -> R,
): Flow<DynamicData<R>> =
    combine(state1, state2, state3) { s1, s2, s3 -> Triple(s1, s2, s3) }
        .flatMapLatest { (s1, s2, s3) ->
            if (s1 == DynamicData.Loading || s2 == DynamicData.Loading || s3 == DynamicData.Loading) {
                flowOf(DynamicData.Loading)
            } else if (s1 is DynamicData.Loaded && s2 is DynamicData.Loaded && s3 is DynamicData.Loaded) {
                flow<DynamicData<R>> {
                    emit(DynamicData.Loading)
                    emit(DynamicData.Loaded(action(s1.data, s2.data, s3.data)))
                }
            } else {
                flowOf(DynamicData.Empty)
            }
        }

fun <T1, T2, T3, T4, R> combineDynamicData(
    state1: Flow<DynamicData<T1>>,
    state2: Flow<DynamicData<T2>>,
    state3: Flow<DynamicData<T3>>,
    state4: Flow<DynamicData<T4>>,
    action: suspend (T1, T2, T3, T4) -> R,
): Flow<DynamicData<R>> =
    combine(state1, state2, state3, state4) { s1, s2, s3, s4 -> Quadruple(s1, s2, s3, s4) }
        .flatMapLatest { (s1, s2, s3, s4) ->
            if (
                s1 == DynamicData.Loading ||
                    s2 == DynamicData.Loading ||
                    s3 == DynamicData.Loading ||
                    s4 == DynamicData.Loading
            ) {
                flowOf(DynamicData.Loading)
            } else if (
                s1 is DynamicData.Loaded &&
                    s2 is DynamicData.Loaded &&
                    s3 is DynamicData.Loaded &&
                    s4 is DynamicData.Loaded
            ) {
                flow<DynamicData<R>> {
                    emit(DynamicData.Loading)
                    emit(DynamicData.Loaded(action(s1.data, s2.data, s3.data, s4.data)))
                }
            } else {
                flowOf(DynamicData.Empty)
            }
        }

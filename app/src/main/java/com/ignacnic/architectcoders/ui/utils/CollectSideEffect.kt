package com.ignacnic.architectcoders.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
fun <SideEffect> CollectSideEffect(
    sideEfectFlow: Flow<SideEffect>,
    reducer: suspend CoroutineScope.(effect: SideEffect) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(sideEfectFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
            sideEfectFlow.collect {
                reducer(it)
            }
        }
    }
}

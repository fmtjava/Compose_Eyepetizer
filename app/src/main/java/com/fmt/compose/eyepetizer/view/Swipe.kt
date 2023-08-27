package com.fmt.compose.eyepetizer.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Swipe(
    modifier: Modifier = Modifier,
    state: SwipeState = rememberSwipeState(),
    threshold: Float = 0.3f,
    direction: SwipeDirection = SwipeDirection.RightToLeft,
    onChange: ((open: Boolean) -> Unit)? = null,
    background: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var boxWidthPx by remember {
        mutableStateOf(0)
    }
    var boxHeightPx by remember {
        mutableStateOf(0)
    }
    var backgroundWidthPx by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()
    val swipeAbleState = rememberSwipeableState(0)
    state.swipeableState = swipeAbleState
    val anchors by remember(backgroundWidthPx, direction) {
        if (direction == SwipeDirection.RightToLeft) {
            mutableStateOf(mapOf(0f to 0, -backgroundWidthPx.toFloat() to 1))
        } else {
            mutableStateOf(mapOf(0f to 0, backgroundWidthPx.toFloat() to 1))
        }
    }

    remember(swipeAbleState.currentValue) {
        Log.d("Swipe", "swipeAbleState.currentValue:${swipeAbleState.currentValue}")
        onChange?.invoke(swipeAbleState.currentValue == 1)
    }

    LaunchedEffect(key1 = Unit) {
        when (state.currentValue) {
            SwipeValue.Hidden -> scope.launch {
                swipeAbleState.animateTo(0)
            }
            SwipeValue.Open -> scope.launch { swipeAbleState.animateTo(1) }
        }
    }
    val swipeModifier = if (backgroundWidthPx > 0) Modifier.swipeable(
        state = swipeAbleState,
        anchors = anchors,
        thresholds = { _, _ -> FractionalThreshold(threshold) },
        orientation = Orientation.Horizontal,
    ) else Modifier
    Box(modifier = Modifier
        .onSizeChanged {
            boxWidthPx = it.width
            boxHeightPx = it.height
        }
        .clipToBounds()
        .then(swipeModifier)
        .then(modifier)
    ) {

        val backgroundOffsetX = when (direction) {
            SwipeDirection.LeftToRight -> -backgroundWidthPx + swipeAbleState.offset.value.roundToInt()
            SwipeDirection.RightToLeft -> boxWidthPx + swipeAbleState.offset.value.roundToInt()
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = swipeAbleState.offset.value.roundToInt(),
                    y = 0
                )
            }) {
            content()
        }
        Box(modifier = Modifier
            .height(with(LocalDensity.current) { boxHeightPx.toDp() })
            .onSizeChanged {
                backgroundWidthPx = it.width
            }
            .offset {
                IntOffset(
                    x = backgroundOffsetX,
                    y = 0
                )
            }
        ) {
            background()
        }

    }
}

sealed class SwipeDirection {
    object LeftToRight : SwipeDirection()
    object RightToLeft : SwipeDirection()
}

enum class SwipeValue {
    Hidden,
    Open,
}


@Composable
fun rememberSwipeState(
    initialValue: SwipeValue = SwipeValue.Hidden
): SwipeState = rememberSaveable(saver = SwipeState.SAVER) {
    SwipeState(
        initialValue = initialValue,
    )
}

@OptIn(ExperimentalMaterialApi::class)
class SwipeState(
    val initialValue: SwipeValue,
) {
    internal lateinit var swipeableState: SwipeableState<Int>
    private var _currentValue: SwipeValue by mutableStateOf(initialValue)
    val currentValue: SwipeValue
        get() = _currentValue

    suspend fun open() {
        if (swipeableState.currentValue != 1) {
            swipeableState.animateTo(1)
        }
    }

    suspend fun close() {
        if (swipeableState.currentValue != 0) {
            swipeableState.animateTo(0)
        }
    }

    fun isOpen(): Boolean = swipeableState.currentValue == 1

    companion object {
        val SAVER: Saver<SwipeState, *> = Saver(
            save = {
                it.initialValue
            },
            restore = {
                SwipeState(it)
            }
        )
    }
}
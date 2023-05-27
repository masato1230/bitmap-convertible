package com.github.masato1230.bitmapcomvertible

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.github.masato1230.bitmapcomvertible.ui.theme.BitmapComvertibleTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitmapComvertibleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
                    val bitmapConvertibleState = rememberBitmapConvertibleState()
                    Box {
                        BitmapConvertible(state = bitmapConvertibleState) {
                            Image(
                                painter = painterResource(id = R.drawable.train),
                                contentDescription = "train",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                bitmap.value = bitmapConvertibleState.getBitmap()
                                            },
                                        )
                                    },
                            )
                        }

                        Text(
                            text = "Long press to capture bitmap",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(20.dp),
                        )

                        // bitmap thumbnail
                        bitmap.value?.let {
                            var isFullSizeThumbnail by remember { mutableStateOf(true) }
                            var isFinishedSizeAnimation by remember { mutableStateOf(true) }
                            LaunchedEffect(key1 = bitmap.value) {
                                isFinishedSizeAnimation = false
                                isFullSizeThumbnail = true
                                delay(500)
                                isFullSizeThumbnail = false
                            }

                            val animatedThumbnailSize by animateFloatAsState(
                                targetValue = if (isFullSizeThumbnail) 1f else 0.33f,
                                animationSpec = if (isFullSizeThumbnail) snap() else tween(500),
                                finishedListener = { if (!isFullSizeThumbnail) isFinishedSizeAnimation = true },
                            )

                            AnimatedVisibility(
                                visible = !isFinishedSizeAnimation,
                                enter = EnterTransition.None,
                                exit = fadeOut(tween(1000)),
                                modifier = Modifier.align(Alignment.BottomEnd),
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "thumbnail",
                                    modifier = Modifier
                                        .fillMaxSize(animatedThumbnailSize)
                                        .width(200.dp)
                                        .padding(10.dp)
                                        .background(Color.White),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BitmapConvertible(
    state: BitmapConvertibleState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val view = ComposeView(it).apply {
                setContent(content)
            }
            state.setComposeView(view)
            return@AndroidView view
        },
    )
}

@Stable
class BitmapConvertibleState {
    private lateinit var _composeView: ComposeView
    fun setComposeView(view: ComposeView) {
        _composeView = view
    }

    fun getBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        return _composeView.drawToBitmap(config = config)
    }
}

@Composable
fun rememberBitmapConvertibleState() = remember { BitmapConvertibleState() }

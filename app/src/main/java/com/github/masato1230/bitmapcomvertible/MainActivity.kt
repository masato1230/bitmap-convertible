package com.github.masato1230.bitmapcomvertible

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.github.masato1230.bitmapcomvertible.ui.theme.BitmapComvertibleTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitmapComvertibleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bitmapConvertibleState = rememberBitmapConvertibleState()
                    BitmapConvertible(state = bitmapConvertibleState) {
                        Image(
                            painter = painterResource(id = R.drawable.train),
                            contentDescription = "train",
                        )
                    }

                    LaunchedEffect(key1 = Unit) {
                        delay(1000)
                        val bitmap = bitmapConvertibleState.getBitmap();
                    }
                }
            }
        }
    }
}

@Composable
fun BitmapConvertible(
    state: BitmapConvertibleState,
    content: @Composable () -> Unit,
) {
    AndroidView(
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

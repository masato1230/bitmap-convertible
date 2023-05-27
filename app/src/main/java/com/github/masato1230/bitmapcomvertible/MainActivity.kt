package com.github.masato1230.bitmapcomvertible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.github.masato1230.bitmapcomvertible.ui.theme.BitmapComvertibleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            BitmapComvertibleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        factory = {
                            val view = ComposeView(it).apply {
                                setContent {
                                    Image(
                                        painter = painterResource(id = R.drawable.train),
                                        contentDescription = "train",
                                    )
                                }
                            }
                            coroutineScope.launch {
                                delay(1000)
                                val bitmap = view.drawToBitmap()
                                bitmap
                            }
                            return@AndroidView view
                        },
                    )
                }
            }
        }
    }
}

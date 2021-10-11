package me.pavi2410.picsup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import me.pavi2410.picsup.ui.theme.PicsupTheme

const val HOST = "https://picsup.herokuapp.com"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PicsupTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    val client = remember {
                        HttpClient(OkHttp) {
                            install(JsonFeature) {
                                serializer = KotlinxSerializer()
                            }
                        }
                    }
                    DisposableEffect(client) {
                        onDispose { client.close() }
                    }

                    var imageIds: List<String> by remember { mutableStateOf(emptyList()) }

                    LaunchedEffect(true) {
                        // TODO: serializer - handle exceptions
                        val res: ImageIds = client.get("$HOST/images")
                        println("got res = $res")

                        imageIds = res.images
                    }

                    MainScreen(imageIds)
                }
            }
        }
    }
}

@Serializable
data class ImageIds(val images: List<String>)

@Composable
fun MainScreen(imageIds: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(100.dp)
    ) {
        TopAppBar(title = {
            Text("picsup")
        })

        if (imageIds.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(imageIds) { id ->
                    Image(
                        painter = rememberImagePainter(
                            data = "$HOST/image/$id"
                        ) {
                            size(OriginalSize)
                        },
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PicsupTheme {
        MainScreen(
            listOf(
                "6159f0aa4b4b9e568ab70ff0",
                "6159f0b54b4b9e568ab70ff4",
                "6159f0c24b4b9e568ab70ff8"
            )
        )
    }
}
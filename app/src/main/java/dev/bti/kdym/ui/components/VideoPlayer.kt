package dev.bti.kdym.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dev.bti.kdym.KdymApp
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    isPlaying: Boolean,
    onProgressUpdate: (Float, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(KdymApp.videoCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(cacheDataSourceFactory))
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
            }
    }

    LaunchedEffect(videoUrl) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.prepare()
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (exoPlayer.duration > 0) {
                val progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                onProgressUpdate(progress, exoPlayer.currentPosition, exoPlayer.duration)
            }
            delay(200)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                    },
                    onDoubleTap = { offset ->
                        val width = size.width
                        if (offset.x < width / 2) {
                            val newPos = (exoPlayer.currentPosition - 5000).coerceAtLeast(0)
                            exoPlayer.seekTo(newPos)
                        } else {
                            val newPos = (exoPlayer.currentPosition + 5000).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPos)
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = { context ->
                android.view.TextureView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    exoPlayer.setVideoTextureView(this)
                }
            },
            onRelease = { textureView ->
                exoPlayer.clearVideoTextureView(textureView)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

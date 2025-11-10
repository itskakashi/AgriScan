package com.example.agriscan.presentation.tutorial

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun YoutubePlayer(
    youtubeVideoId: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    onPlayerReady: (() -> Unit)? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val youTubePlayerView = YouTubePlayerView(context)

            // Attach lifecycle so the view will be paused/resumed/released automatically
            lifecycleOwner.lifecycle.addObserver(youTubePlayerView)

            // Add listener to get onReady and then load the video
            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    Log.d("YoutubePlayer", "YouTube player ready, id=$youtubeVideoId")
                    onPlayerReady?.invoke()

                    if (autoPlay) {
                        // start playback from beginning
                        youTubePlayer.loadVideo(youtubeVideoId, 0f)
                    } else {
                        // prepare without autoplay
                        youTubePlayer.cueVideo(youtubeVideoId, 0f)
                    }
                }
            })

            youTubePlayerView
        },
        update = { view ->
            // If you need to change video dynamically, use getYouTubePlayerWhenReady
            // Example (uncomment to use):
            // view.getYouTubePlayerWhenReady { player -> player.loadVideo(youtubeVideoId, 0f) }
        },
        onRelease = { view ->
            // ensure cleanup
            try { lifecycleOwner.lifecycle.removeObserver(view) } catch (_: Exception) {}
            try { view.release() } catch (_: Exception) {}
        }
    )
}

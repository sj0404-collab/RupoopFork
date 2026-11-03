package com.santiago43rus.rupoop.legacy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.santiago43rus.rupoop.parser.UniversalVideoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        progress = findViewById(R.id.progress)
        title = intent.getStringExtra(EXTRA_TITLE)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val url = intent.getStringExtra(EXTRA_URL) ?: ""
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            val parsed = withContext(Dispatchers.IO) { UniversalVideoParser.parse(url) }
            progress.visibility = View.GONE
            if (parsed == null) {
                Toast.makeText(this@PlayerActivity, "Не удалось получить поток", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            player?.setMediaItem(MediaItem.fromUri(Uri.parse(parsed.streamUrl)))
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE = "extra_title"
        fun intent(ctx: Context, url: String, title: String) =
            Intent(ctx, PlayerActivity::class.java).putExtra(EXTRA_URL, url).putExtra(EXTRA_TITLE, title)
    }
}

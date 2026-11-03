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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.santiago43rus.rupoop.parser.UniversalVideoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var progress: ProgressBar
    private var streamUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        progress = findViewById(R.id.progress)
        title = intent.getStringExtra(EXTRA_TITLE)

        val selector = DefaultTrackSelector(this)
        player = ExoPlayer.Builder(this).setTrackSelector(selector).build()
        playerView.player = player

        findViewById<View>(R.id.qualityButton).setOnClickListener { showQualityDialog() }

        val url = intent.getStringExtra(EXTRA_URL) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_TITLE) ?: ""
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            val parsed = withContext(Dispatchers.IO) { UniversalVideoParser.parse(url) }
            progress.visibility = View.GONE
            if (parsed == null) {
                Toast.makeText(this@PlayerActivity, "Не удалось получить поток", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            streamUrl = parsed.streamUrl
            HistoryStore.add(this@PlayerActivity, url, parsed.title, parsed.authorName ?: "", intent.getStringExtra(EXTRA_SOURCE) ?: "Rutube")
            player?.setMediaItem(MediaItem.fromUri(Uri.parse(parsed.streamUrl)))
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    private fun showQualityDialog() {
        val p = player ?: return
        val selector = p.trackSelector as? DefaultTrackSelector ?: return
        val groups = p.currentTracks.groups
        val videoGroup = groups.firstOrNull { it.type == C.TRACK_TYPE_VIDEO } ?: return
        val labels = mutableListOf("Авто")
        val overrides = mutableListOf<TrackSelectionOverride?>()
        overrides.add(null)
        for (i in 0 until videoGroup.length) {
            val format = videoGroup.getTrackFormat(i)
            labels.add("${format.height}p")
            overrides.add(TrackSelectionOverride(videoGroup.mediaTrackGroup, listOf(i)))
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Качество видео")
            .setItems(labels.toTypedArray()) { _, which ->
                val ov = overrides[which]
                val params = if (ov == null) {
                    p.trackSelectionParameters.buildUpon().clearOverrides().build()
                } else {
                    p.trackSelectionParameters.buildUpon()
                        .setOverrideForType(ov)
                        .build()
                }
                selector.setParameters(params)
            }
            .show()
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_SOURCE = "extra_source"
        fun intent(ctx: Context, url: String, title: String, source: String = "Rutube") =
            Intent(ctx, PlayerActivity::class.java).putExtra(EXTRA_URL, url).putExtra(EXTRA_TITLE, title).putExtra(EXTRA_SOURCE, source)
    }
}

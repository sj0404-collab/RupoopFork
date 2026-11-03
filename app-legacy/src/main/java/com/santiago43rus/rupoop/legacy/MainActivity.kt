package com.santiago43rus.rupoop.legacy

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiago43rus.rupoop.data.SearchResult
import com.santiago43rus.rupoop.network.RutubePlatformSearchEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val progress = findViewById<ProgressBar>(R.id.progress)

        adapter = VideoAdapter { video ->
            startActivity(PlayerActivity.intent(this, video.videoUrl, video.title))
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        fun doSearch(q: String) {
            progress.visibility = View.VISIBLE
            lifecycleScope.launch {
                val results = withContext(Dispatchers.IO) {
                    if (q.isBlank()) emptyList()
                    else RutubePlatformSearchEngine.search(q)
                }
                progress.visibility = View.GONE
                if (results.isEmpty()) Toast.makeText(this@MainActivity, "Ничего не найдено", Toast.LENGTH_SHORT).show()
                adapter.submit(results)
            }
        }

        findViewById<View>(R.id.searchButton).setOnClickListener {
            doSearch(searchInput.text.toString().trim())
        }
        doSearch("")
    }
}

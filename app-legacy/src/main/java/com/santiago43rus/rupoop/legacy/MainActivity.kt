package com.santiago43rus.rupoop.legacy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiago43rus.rupoop.data.SearchResult
import com.santiago43rus.rupoop.network.LordfilmPlatformSearchEngine
import com.santiago43rus.rupoop.network.OkPlatformSearchEngine
import com.santiago43rus.rupoop.network.PlatformVideoSearchEngine
import com.santiago43rus.rupoop.network.RutubePlatformSearchEngine
import com.santiago43rus.rupoop.network.VkPlatformSearchEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: VideoAdapter
    private var engine: PlatformVideoSearchEngine = RutubePlatformSearchEngine

    private val engines = listOf(
        "Rutube" to RutubePlatformSearchEngine,
        "VK Видео" to VkPlatformSearchEngine,
        "Одноклассники" to OkPlatformSearchEngine,
        "Lordfilm" to LordfilmPlatformSearchEngine
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val platformSpinner = findViewById<Spinner>(R.id.platformSpinner)
        val sectionTitle = findViewById<TextView>(R.id.sectionTitle)

        platformSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, engines.map { it.first })
        platformSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                engine = engines[pos].second
                doSearch(searchInput.text.toString().trim())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        adapter = VideoAdapter(
            onClick = { video -> startActivity(PlayerActivity.intent(this, video.videoUrl, video.title)) },
            onLongClick = { video -> Downloader.download(this, lifecycleScope, video.videoUrl, video.title) }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        fun doSearch(q: String) {
            progress.visibility = View.VISIBLE
            lifecycleScope.launch {
                val results = withContext(Dispatchers.IO) {
                    if (q.isBlank()) emptyList() else engine.search(q)
                }
                progress.visibility = View.GONE
                sectionTitle.text = if (q.isBlank()) "История просмотров" else "Результаты: ${results.size}"
                if (q.isBlank()) {
                    adapter.submit(HistoryStore.list(this@MainActivity).map {
                        SearchResult(videoUrl = it.url, title = it.title, author = com.santiago43rus.rupoop.data.Author(it.author))
                    })
                } else {
                    if (results.isEmpty()) Toast.makeText(this@MainActivity, "Ничего не найдено", Toast.LENGTH_SHORT).show()
                    adapter.submit(results)
                }
            }
        }
        this.doSearch = ::doSearch

        findViewById<View>(R.id.searchButton).setOnClickListener { doSearch(searchInput.text.toString().trim()) }
        doSearch("")
    }

    private lateinit var doSearch: (String) -> Unit

    override fun onResume() {
        super.onResume()
        if (::doSearch.isInitialized) doSearch("")
    }
}

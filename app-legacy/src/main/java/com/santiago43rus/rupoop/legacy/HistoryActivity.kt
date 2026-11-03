package com.santiago43rus.rupoop.legacy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiago43rus.rupoop.data.Author
import com.santiago43rus.rupoop.data.SearchResult

/** Экран истории просмотров: фильтр по источнику + поиск по истории. */
class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: VideoAdapter
    private var sourceFilter: String? = null
    private var textFilter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        title = "История"

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val search = findViewById<EditText>(R.id.historySearch)
        val spinner = findViewById<Spinner>(R.id.sourceSpinner)

        adapter = VideoAdapter(
            onClick = { startActivity(PlayerActivity.intent(this, it.videoUrl, it.title, it.author?.name ?: "Rutube")) },
            onLongClick = { Downloader.download(this, lifecycleScope, it.videoUrl, it.title) }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        fun refreshSpinner() {
            val sources = listOf("Все источники") + HistoryStore.sources(this)
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sources)
        }
        refreshSpinner()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                sourceFilter = if (pos == 0) null else HistoryStore.sources(this@HistoryActivity)[pos - 1]
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textFilter = s?.toString()?.trim() ?: ""
                applyFilters()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        applyFilters()
    }

    private fun applyFilters() {
        val items = HistoryStore.query(this, sourceFilter, textFilter).map {
            SearchResult(videoUrl = it.url, title = "${it.title}  [${it.source}]", author = Author(name = it.author))
        }
        adapter.submit(items)
    }

    override fun onResume() {
        super.onResume()
        applyFilters()
    }
}

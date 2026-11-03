package com.santiago43rus.rupoop.legacy

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Локальная история просмотров на SharedPreferences (до 300 записей, с источником). */
object HistoryStore {
    private const val PREFS = "watch_history"
    private const val KEY = "items"
    private const val MAX = 300

    data class Item(val url: String, val title: String, val author: String, val source: String, val ts: Long)

    fun add(ctx: Context, url: String, title: String, author: String, source: String) {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY, "[]") ?: "[]")
        val out = JSONArray()
        out.put(JSONObject().put("url", url).put("title", title).put("author", author)
            .put("source", source).put("ts", System.currentTimeMillis()))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (o.getString("url") != url) out.put(o)
            if (out.length() >= MAX) break
        }
        prefs.edit().putString(KEY, out.toString()).apply()
    }

    fun list(ctx: Context): List<Item> {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY, "[]") ?: "[]")
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            Item(o.getString("url"), o.getString("title"), o.optString("author"),
                o.optString("source", "Rutube"), o.optLong("ts"))
        }
    }

    /** Уникальные источники, встречающиеся в истории. */
    fun sources(ctx: Context): List<String> = list(ctx).map { it.source }.distinct()

    /** Фильтр по источнику + текстовый поиск по названию/автору. */
    fun query(ctx: Context, source: String?, text: String): List<Item> =
        list(ctx).filter { (source == null || it.source == source) &&
            (text.isBlank() || it.title.contains(text, true) || it.author.contains(text, true)) }
}

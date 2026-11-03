package com.santiago43rus.rupoop.legacy

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Локальная история просмотров на SharedPreferences (до 200 записей). */
object HistoryStore {
    private const val PREFS = "watch_history"
    private const val KEY = "items"
    private const val MAX = 200

    data class Item(val url: String, val title: String, val author: String, val ts: Long)

    fun add(ctx: Context, url: String, title: String, author: String) {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY, "[]") ?: "[]")
        val out = JSONArray()
        out.put(JSONObject().put("url", url).put("title", title).put("author", author).put("ts", System.currentTimeMillis()))
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
            Item(o.getString("url"), o.getString("title"), o.optString("author"), o.optLong("ts"))
        }
    }
}

package com.santiago43rus.rupoop.legacy

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.santiago43rus.rupoop.parser.UniversalVideoParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/** Реальная загрузка видео через системный DownloadManager. */
object Downloader {

    fun download(ctx: Context, scope: CoroutineScope, url: String, title: String) {
        Toast.makeText(ctx, "Получаю поток...", Toast.LENGTH_SHORT).show()
        scope.launch(Dispatchers.IO) {
            val parsed = UniversalVideoParser.parse(url)
            if (parsed == null) {
                launch(Dispatchers.Main) { Toast.makeText(ctx, "Не удалось получить поток", Toast.LENGTH_LONG).show() }
                return@launch
            }
            val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileName = "Rupoop_${System.currentTimeMillis()}.mp4"
            val req = DownloadManager.Request(Uri.parse(parsed.streamUrl))
                .setTitle(title)
                .setDescription(parsed.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, fileName)
            dm.enqueue(req)
            launch(Dispatchers.Main) {
                Toast.makeText(ctx, "Загрузка начата: Movies/$fileName", Toast.LENGTH_LONG).show()
            }
        }
    }
}

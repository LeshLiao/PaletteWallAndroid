package com.palettex.palettewall


import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class DownloadCompletedReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Log.d("DownloadReceiver", "Download complete with ID: $id")
            if(id != -1L) {
//                println("Download with ID $id finished!")
                Toast.makeText(context, "Downloaded successfully!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
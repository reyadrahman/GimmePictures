package ua.andrii.andrushchenko.gimmepictures.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.andrii.andrushchenko.gimmepictures.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    // Create notification channels
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.download_notification_channel_name)
            val descriptionText =
                context.getString(R.string.download_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register channel
            notificationManager.createNotificationChannel(channel)
        }
    }

    /*fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }*/

    fun getProgressNotificationBuilder(fileName: String, cancelIntent: PendingIntent) =
        NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID).apply {
            priority = NotificationCompat.PRIORITY_LOW
            setSmallIcon(android.R.drawable.stat_sys_download)
            setTicker("")
            setOngoing(true)
            setContentTitle(fileName)
            setProgress(0, 0, true)
            addAction(0, context.getString(R.string.cancel), cancelIntent)
        }

    fun updateProgressNotification(
        builder: NotificationCompat.Builder,
        progress: Int,
    ) = builder.apply {
        setProgress(100, progress, false)
        if (progress == 100) setSmallIcon(android.R.drawable.stat_sys_download_done)
    }

    fun showDownloadCompleteNotification(fileName: String, uri: Uri) {
        val builder = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID).apply {
            priority = NotificationCompat.PRIORITY_LOW
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setContentTitle(fileName)
            setContentText(context.getString(R.string.download_complete))
            setContentIntent(getViewPendingIntent(uri))
            setProgress(0, 0, false)
            setAutoCancel(true)
        }
        notificationManager.notify(fileName.hashCode(), builder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getViewPendingIntent(uri: Uri): PendingIntent {
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(uri, "image/*")
        }

        val chooser = Intent.createChooser(viewIntent, context.getString(R.string.open_with))

        return PendingIntent.getActivity(context, 0, chooser, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun showDownloadErrorNotification(fileName: String) {
        val builder = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID).apply {
            priority = NotificationCompat.PRIORITY_LOW
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setContentTitle(fileName)
            setContentText(context.getString(R.string.download_error))
            setProgress(0, 0, false)
        }
        notificationManager.notify(fileName.hashCode(), builder.build())
    }

    companion object {
        private const val DOWNLOAD_CHANNEL_ID = "download_channel_id"
    }
}
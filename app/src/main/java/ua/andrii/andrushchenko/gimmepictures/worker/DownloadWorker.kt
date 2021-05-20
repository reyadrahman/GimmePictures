package ua.andrii.andrushchenko.gimmepictures.worker

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.buffer
import okio.sink
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.download.DownloadService
import ua.andrii.andrushchenko.gimmepictures.util.*
import java.io.File
import java.util.*

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadService: DownloadService,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_INPUT_URL) ?: return Result.failure()
        val fileName = inputData.getString(KEY_OUTPUT_FILE_NAME) ?: return Result.failure()

        val downloadNotificationId = id.hashCode()
        val cancelPendingIntent = WorkManager.getInstance(appContext).createCancelPendingIntent(id)

        val notificationBuilder =
            notificationHelper.getProgressNotificationBuilder(fileName, cancelPendingIntent)

        setForeground(ForegroundInfo(downloadNotificationId, notificationBuilder.build()))

        download(url, fileName, downloadNotificationId, notificationBuilder)

        return Result.success()
    }

    private suspend fun download(
        url: String,
        fileName: String,
        downloadNotificationId: Int,
        notificationBuilder: NotificationCompat.Builder,
    ) = withContext(Dispatchers.IO) {
        try {
            val responseBody = downloadService.downloadFile(url)

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                responseBody.saveImage(appContext, fileName) {
                    launch { onProgress(downloadNotificationId, notificationBuilder, it) }
                }
            } else {
                responseBody.saveImageLegacy(appContext, fileName) {
                    launch { onProgress(downloadNotificationId, notificationBuilder, it) }
                }
            }

            if (uri != null) {
                onSuccess(fileName, uri)
                inputData.getString(KEY_PHOTO_ID)?.let {
                    backendRequest { downloadService.trackDownload(it) }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext,
                        appContext.resources.getString(R.string.download_complete),
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                onError(fileName, Exception("Failed to write to file"), true)
            }

        } catch (e: CancellationException) {
            onError(fileName, e, showNotification = false)
        } catch (e: Exception) {
            onError(fileName, e, showNotification = true)
        }
    }

    private suspend fun onProgress(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        progress: Int,
    ) {
        setForeground(ForegroundInfo(notificationId,
            notificationHelper.updateProgressNotification(builder, progress).build()))
    }

    private fun onError(
        fileName: String,
        exception: Exception,
        showNotification: Boolean,
    ) {
        //Log.e(TAG, "onError: ${exception.message}")
        Toast.makeText(appContext, exception.message, Toast.LENGTH_SHORT).show()
        if (showNotification) {
            notificationHelper.showDownloadErrorNotification(fileName)
        }
    }

    private fun onSuccess(
        fileName: String,
        uri: Uri,
    ) {
        //Log.i(TAG, "onSuccess: $fileName - $uri")
        notificationHelper.showDownloadCompleteNotification(fileName, uri)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun ResponseBody.saveImage(
        context: Context,
        fileName: String,
        onProgress: ((Int) -> Unit)?,
    ): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.SIZE, contentLength())
            put(MediaStore.Images.Media.RELATIVE_PATH, GIMME_PICTURES_RELATIVE_PATH)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            val complete = resolver.openOutputStream(uri)?.use { outputStream ->
                writeToSink(outputStream.sink().buffer(), onProgress)
            } ?: false

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            if (!complete) {
                resolver.delete(uri, null, null)
                throw CancellationException("Cancelled by user")
            }
        }

        return uri
    }

    private fun ResponseBody.saveImageLegacy(
        context: Context,
        fileName: String,
        onProgress: ((Int) -> Unit)
    ): Uri? {
        val path = File(GIMME_PICTURES_LEGACY_PATH)

        if (!path.exists()) {
            if (!path.mkdirs()) return null
        }

        val file = File(path, fileName)

        val complete = writeToSink(file.sink().buffer(), onProgress)

        if (!complete && file.exists()) {
            file.delete()
            throw CancellationException("Cancelled by user")
        }

        // Provide a way for applications to pass a newly
        // created or downloaded media file to the media scanner service
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath),
            arrayOf("image/jpeg"), null)

        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    }

    private fun ResponseBody.writeToSink(
        sink: BufferedSink,
        onProgress: ((Int) -> Unit)?,
    ): Boolean {
        val fileSize = contentLength()

        var totalBytesRead = 0L
        var progressToReport = 0

        while (true) {
            if (isStopped) return false
            val readCount = source().read(sink.buffer, 8192L)
            if (readCount == -1L) break
            sink.emit()
            totalBytesRead += readCount
            val progress = (100.0 * totalBytesRead / fileSize)
            if (progress - progressToReport >= 10) {
                progressToReport = progress.toInt()
                onProgress?.invoke(progressToReport)
            }
        }

        sink.close()
        return true
    }

    companion object {
        //private const val TAG = "DownloadWorker"
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"
        const val KEY_PHOTO_ID = "KEY_PHOTO_ID"

        fun enqueueDownload(
            context: Context,
            url: String,
            fileName: String,
            photoId: String?
        ): UUID {
            val inputData = workDataOf(
                KEY_INPUT_URL to url,
                KEY_OUTPUT_FILE_NAME to fileName,
                KEY_PHOTO_ID to photoId
            )
            val request =
                OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(inputData).build()
            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}
package teachingsolutions.domain_layer.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileStorageManager @Inject constructor(@ApplicationContext private val context: Context) {
    fun getLecturePdf(courseItemId: Int, courseName: String): File? {
        val fileName = "${courseItemId}_${courseName.replace(" ", "_", true)}.pdf"
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file else null
    }

    @Throws(IOException::class)
    fun saveLecturePdf(courseItemId: Int, courseName: String, body: ResponseBody): File {
        val fileName = "${courseItemId}_${courseName.replace(" ", "_", true)}.pdf"
        val file = File(context.filesDir, fileName)

        FileOutputStream(file).use { output ->
            val buffer = ByteArray(20 * 1024)
            var read: Int

            body.byteStream().use { input ->
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }

        return file
    }

    fun deleteLecturePdf(courseItemId: Int, courseName: String): Boolean {
        val fileName = "${courseItemId}_${courseName.replace(" ", "_", true)}.pdf"
        val file = File(context.filesDir, fileName)
        return file.delete()
    }
}
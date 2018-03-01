package org.jetbrains.kotlin.demo.util
import org.jetbrains.kotlin.demo.model.MediaManager
import  org.springframework.security.config.annotation.web.builders.HttpSecurity
import java.io.File
import java.net.URL

object UrlUtils {
    fun retrieveFromTo(url: String, path: String, onProgress: (Int, Int) -> Unit): Boolean {
        val conn = URL(url).openConnection()
        val size = conn.contentLength
        val bufferLength = 4096

        val array = ByteArray(bufferLength)
        try {
            conn.getInputStream().buffered(bufferLength).use { input ->
                File(path).outputStream().use { output ->
                    var n = 0
                    while (true) {
                        val len = input.read(array)
                        if (len < 0) {
                            break
                        }
                        output.write(array, 0, len)
                        n += len
                        onProgress(n, size)
                    }
                }
            }
        } catch (e : Exception){
            if (File(path).exists()){
                try {
                    File(path).delete()
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
            return false
        }
        return true
    }
}
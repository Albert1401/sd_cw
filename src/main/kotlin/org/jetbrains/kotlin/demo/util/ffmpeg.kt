package org.jetbrains.kotlin.demo.util

import org.jetbrains.kotlin.demo.model.TimeDuration
import org.jetbrains.kotlin.demo.model.TrackInfo
import java.nio.file.Files
import java.nio.file.Paths


fun TimeDuration.ffTime(): String {
    return "${hh}:${mm}:${ss}.${m}"
}

object ffmpeg {
    fun extractTrack(mediaPath: String, dir : String, track: TrackInfo) {
        val name = if (track.album_name == "") track.name else track.album_name + " - " + track.name
        Files.createDirectory(Paths.get(dir))
        val builder = ProcessBuilder("ffmpeg",
                "-i", mediaPath,
                "-ss", track.start,
                "-t", track.duration,
                "$dir/$name.mp3")
        val process = builder.start()
        println(process.errorStream.reader().readText())
        println(process.inputStream.reader().readText())
        val excode = process.waitFor()
        if (excode != 0) {
            //TODO
        }
    }
}
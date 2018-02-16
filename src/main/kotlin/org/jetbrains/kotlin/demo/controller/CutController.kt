package org.jetbrains.kotlin.demo.controller

import com.commit451.youtubeextractor.YouTubeExtractor
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.io.IOUtils
import org.jetbrains.kotlin.demo.model.Cut
import org.jetbrains.kotlin.demo.model.MediaInfo
import org.jetbrains.kotlin.demo.model.MediaManager
import org.jetbrains.kotlin.demo.util.UrlUtils
import org.jetbrains.kotlin.demo.util.ffmpeg
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread


@RestController
class CutController {
    @Throws(Exception::class)
    protected fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic().disable();
    }

    //queryId -> Thread (for cancelling)
//    val jobs = ConcurrentHashMap<Int, Thread>()

    @GetMapping(value = ["/media"])
    fun media(@RequestParam(value = "youtubeHashId", required = true) videoId: String): List<MediaInfo> {
        val extractor = YouTubeExtractor.create()
        val info = extractor.extract(videoId).blockingGet()

        return info.videoStreams.map { MediaInfo(videoId, it.url, it.format, it.resolution) }
    }

    @PostMapping(value = ["/cut"])
    fun cut(@RequestBody(required = true) query: Cut,
            @RequestParam(value = "sessionId", required = true) sessionId: Int): ResponseEntity<Int> {
        val mediaId = MediaManager.createOrGetMedia(query.media)
        val queryId = MediaManager.createQuery(sessionId, mediaId)
        downloadAsync(query, queryId, mediaId)
        return ResponseEntity(queryId, HttpStatus.OK)

    }

    data class QResponse(@JsonProperty val mediaProgress: Int, @JsonProperty val tracks: List<Int>)

    @GetMapping(value = ["/get"])
    fun gett(@RequestParam(value = "queryId", required = true) queryId: Int): QResponse? {
        val mediaId = MediaManager.getMediaIdByQueryId(queryId) ?: return null
        val progress = MediaManager.getMediaProgress(mediaId) ?: return null
        if (progress != 100) {
            return QResponse(progress, arrayListOf())
        }
        return QResponse(100, MediaManager.getAllTracksByQueryId(queryId))
    }


    fun downloadAsync(query: Cut, queryId: Int, mediaId: Int) {
        val media = query.media
        //TODO save it to cancel
        val job = thread {
            //TODO cancel handler
            val mediaPath = "${media.youtubeHash}-${media.quality}.${media.format}"
            //TODO error handlers
            if (MediaManager.isMediaComplete(mediaId) || UrlUtils.retrieveFromTo(media.url,
                    mediaPath,
                    { i, n ->
                        MediaManager.setMediaProgress(mediaId, i.toFloat() / n)
                    })) {
                MediaManager.setMediaComplete(mediaId)
                for (track in query.tracks) {
                    val trackId = MediaManager.createTrack(track, queryId)
                    ffmpeg.extractTrack(mediaPath, trackId.toString(), track)
                    MediaManager.setTrackComplete(trackId)
                }
            }
        }
    }


//    fun check(media: MediaInfo): Boolean {
//        return true
//    }
//
//    fun check(sessionId: Int): Boolean {
//        return true
//    }

    @GetMapping(value = ["/track"], produces = ["audio/mp3"])
    fun getTrack(@RequestParam(value = "id", required = true) trackId: Int): ResponseEntity<ByteArrayResource>? {
        //TODO
        val mp3 = Files.walk(Paths.get("$trackId/"), 1)
                .filter { it.toString().endsWith("mp3") }
                .findFirst().orElseGet { null }!!
        return ResponseEntity.ok()
                .body(ByteArrayResource(IOUtils.toByteArray(Files.newInputStream(mp3))))
    }

}
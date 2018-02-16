package org.jetbrains.kotlin.demo.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


object MediaManager {
    fun createSession(): Int {
        return Sessions.insert {}[Sessions.id]
    }

    fun existsSession(sessionId: Int): Boolean {
        return !Sessions.select { Sessions.id.eq(sessionId) }.empty()
    }

    fun createOrGetMedia(media: MediaInfo): Int = transaction {
        val id = Medias.select {
            (Medias.youtubeHash eq media.youtubeHash)
                    .and(Medias.format eq media.format)
                    .and(Medias.quality eq Medias.quality)
        }.map { it[Medias.id] }.firstOrNull()
        if (id != null) {
            return@transaction id
        }
        Medias.insert {
            it[Medias.youtubeHash] = media.youtubeHash
            it[Medias.format] = media.format
            it[Medias.quality] = media.quality
            it[Medias.progress] = 0
            it[Medias.birthday] = DateTime.now()
        }[Medias.id]
    }

    fun setMediaComplete(mediaId: Int) = transaction {
        Medias.update({ Medias.id eq mediaId }, null, {
            it[Medias.progress] = 100
        })
    }

    fun getMediaProgress(mediaId: Int): Int? = transaction {
        Medias.select { Medias.id eq mediaId }.map { it[Medias.progress] }.firstOrNull()
    }

    fun createQuery(sessionId: Int, mediaId: Int): Int = transaction {
        Queries.insert {
            it[Queries.sessionId] = sessionId
            it[Queries.mediaId] = mediaId
        }[Queries.id]
    }

    fun setTrackComplete(trackId: Int) = transaction {
        Tracks.update({ Tracks.id eq trackId }, null, {
            it[Tracks.present] = true
        })
    }

    fun getAllTracksByQueryId(queryId: Int): List<Int> = transaction {
        Tracks.select { Tracks.queryId.eq(queryId) and (Tracks.present.eq(true)) }
                .map { it[Tracks.id] }.toList()
    }

    fun createTrack(track: TrackInfo, queryId: Int) = transaction {
        Tracks.insert {
            it[Tracks.queryId] = queryId
            it[Tracks.albumName] = track.album_name
            it[Tracks.start] = track.start
            it[Tracks.duration] = track.duration
            it[Tracks.present] = false
            it[Tracks.name] = track.name
        }[Tracks.id]
    }

    fun getMediaIdByQueryId(queryId: Int): Int? = transaction {
        Queries.select { Queries.id eq queryId }.map { it[Queries.mediaId] }.firstOrNull()
    }

    fun setMediaProgress(mediaId: Int, progress: Float) = transaction {
        val pr = Math.min((progress * 100).toInt(), 99)
        Medias.update({ Medias.id eq mediaId }, null, {
            it[Medias.progress] = pr
        })
    }

    fun isMediaComplete(mediaId: Int): Boolean = transaction {
        Medias.select { (Medias.id eq mediaId).and(Medias.progress eq 100) }.count() != 0
    }
}
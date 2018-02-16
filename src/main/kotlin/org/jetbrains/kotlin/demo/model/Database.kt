package org.jetbrains.kotlin.demo.model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

private val TABLES = arrayOf(Medias, Sessions, Queries, Tracks);

fun initDatabase(file: String) {
    initDatabaseByFullUrl("jdbc:h2:file:$file")
}

fun initInMemoryDatabase() {
    initDatabaseByFullUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
}

fun dropDatabase() = transaction { drop(*TABLES) }

private fun initDatabaseByFullUrl(url: String) {
    Database.connect(url = url, driver = "org.h2.Driver")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction { create(*TABLES) }
}

//videos: videoId, url, progress, format, quality, timestamp
object Medias : Table(){
    val id = integer("id").autoIncrement().primaryKey()
    val youtubeHash = varchar("youtubeHash", 100)
    val format = varchar("format", 20)
    val quality = varchar("quality", 15)
    val progress = integer("progress")
    val birthday = datetime("birthday")

    init{
        uniqueIndex(youtubeHash, format, quality)
    }
}

//sessions: sessionId, query+,
object Sessions : Table(){
    val id = integer("id").autoIncrement().primaryKey()
}

object ASD : IntIdTable(){

}

//queries: videoId, cutId+
object Queries : Table(){
    val id = integer("id").autoIncrement().primaryKey()
    val mediaId = integer("mediaId") references Medias.id
    val sessionId = integer("sessionId") references Sessions.id
}

//tracks: trackId, trackInfo
object Tracks : Table(){
    val id = integer("id").autoIncrement().primaryKey()
    val queryId = integer("queryId") references Queries.id

    val name = varchar("name", 200)
    val albumName = varchar("albumName", 200)
    val start = varchar("start", 30)
    val duration = varchar("duration", 30)
    val present = bool("present")

}

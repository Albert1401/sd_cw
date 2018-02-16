package org.jetbrains.kotlin.demo.model

import com.fasterxml.jackson.annotation.JsonProperty


data class TimeDuration(@JsonProperty("hh") val hh: Int,
                        @JsonProperty("mm") val mm: Int,
                        @JsonProperty("ss") val ss: Int,
                        @JsonProperty("m") val m: Float)

data class TrackInfo(@JsonProperty("name") val name: String,
                     @JsonProperty("album_name") val album_name: String,
                     @JsonProperty("start") val start: String,
                     @JsonProperty("duration") val duration: String)

data class MediaInfo(@JsonProperty("youtubeHash") val youtubeHash: String,
                     @JsonProperty("url") val url: String,
                     @JsonProperty("format") val format: String,
                     @JsonProperty("quality") val quality: String)

//data class YtIdMeta(@JsonProperty("html_desc") val html_desc: String,
//                    @JsonProperty("title") val title: String,
//                    @JsonProperty("tracks") val tracks: List<TrackInfo>,
//                    @JsonProperty("medias") val medias: List<MediaInfo>)

data class Cut(@JsonProperty("media") val media: MediaInfo,
               @JsonProperty("tracks") val tracks: List<TrackInfo>)


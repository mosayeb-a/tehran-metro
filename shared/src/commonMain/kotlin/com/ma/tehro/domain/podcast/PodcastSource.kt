package com.ma.tehro.domain.podcast

enum class PodcastSource {
    PODBEAN,
    CASTBOX,
    UNKNOWN;

    companion object {
        fun fromUrl(url: String): PodcastSource = when {
            url.contains("podbean.com") -> PODBEAN
            url.contains("castbox.fm") -> CASTBOX
            else -> UNKNOWN
        }
    }
}
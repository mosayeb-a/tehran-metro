package com.ma.tehro.data.podcast.repository.source.remote

data class RssFeedDto(
    val channel: ChannelDto
)

data class ChannelDto(
    val title: String = "",
    val description: String = "",
    val link: String = "",
    val language: String = "",
    val author: String? = null,
    val image: ItunesImageDto? = null,
    val item: List<ItemDto> = emptyList()
)

data class ItemDto(
    val title: String = "",
    val link: String = "",
    val description: String = "",
    val guid: String = "",
    val pubDate: String = "",
    val enclosure: EnclosureDto? = null,
    val duration: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val image: ItunesImageDto? = null
)

data class EnclosureDto(
    val url: String,
    val type: String,
    val length: String? = null
)

data class ItunesImageDto(
    val href: String
)
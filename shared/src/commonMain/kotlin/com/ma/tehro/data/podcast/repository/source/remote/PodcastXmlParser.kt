package com.ma.tehro.data.podcast.repository.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.xmlStreaming

object PodcastXmlParser {

    suspend fun parseFeed(xmlString: String): RssFeedDto = withContext(Dispatchers.Default) {
        val reader: XmlReader = xmlStreaming.newReader(xmlString)

        var channelTitle = ""
        var channelDesc = ""
        var channelLink = ""
        var channelLang = ""
        var channelAuthor: String? = null
        var channelImage: ItunesImageDto? = null
        val items = mutableListOf<ItemDto>()

        var currentItem: MutableMap<String, Any?>? = null
        var inItem = false
        var inChannel = false
        var currentTag = ""

        while (reader.hasNext()) {
            when (val event = reader.next()) {
                EventType.START_ELEMENT -> {
                    currentTag = reader.localName

                    when {
                        currentTag == "channel" -> inChannel = true
                        currentTag == "item" -> {
                            inItem = true
                            currentItem = mutableMapOf()
                        }
                        inItem && currentTag == "enclosure" -> {
                            val url = reader.getAttributeValue("", "url") ?: ""
                            val type = reader.getAttributeValue("", "type") ?: ""
                            val length = reader.getAttributeValue("", "length")
                            currentItem?.put("enclosure", EnclosureDto(url, type, length))
                        }
                        (inItem || inChannel) && currentTag.contains("image") -> {
                            val href = reader.getAttributeValue("", "href")
                            if (href != null && href.isNotEmpty()) {
                                if (inItem) {
                                    currentItem?.put("image", ItunesImageDto(href))
                                } else if (inChannel) {
                                    channelImage = ItunesImageDto(href)
                                }
                            }
                        }
                    }
                }

                EventType.TEXT -> {
                    val text = reader.text.trim()
                    if (text.isNotEmpty()) {
                        when {
                            inItem -> {
                                when (currentTag) {
                                    "title" -> currentItem?.put("title", text)
                                    "link" -> currentItem?.put("link", text)
                                    "description" -> currentItem?.put("description", text)
                                    "guid" -> currentItem?.put("guid", text)
                                    "pubDate" -> currentItem?.put("pubDate", text)
                                    "itunes:duration", "duration" -> currentItem?.put("duration", text)
                                    "itunes:season" -> currentItem?.put("season", text.toIntOrNull())
                                    "itunes:episode" -> currentItem?.put("episode", text.toIntOrNull())
                                }
                            }
                            inChannel -> {
                                when (currentTag) {
                                    "title" -> channelTitle = text
                                    "description" -> channelDesc = text
                                    "link" -> channelLink = text
                                    "language" -> channelLang = text
                                    "author", "itunes:author" -> channelAuthor = text
                                }
                            }
                        }
                    }
                }

                EventType.END_ELEMENT -> {
                    when (reader.localName) {
                        "item" -> {
                            currentItem?.let { item ->
                                val itemDto = ItemDto(
                                    title = item["title"] as? String ?: "",
                                    link = item["link"] as? String ?: "",
                                    description = item["description"] as? String ?: "",
                                    guid = item["guid"] as? String ?: "",
                                    pubDate = item["pubDate"] as? String ?: "",
                                    enclosure = item["enclosure"] as? EnclosureDto,
                                    duration = item["duration"] as? String,
                                    season = item["season"] as? Int,
                                    episode = item["episode"] as? Int,
                                    image = item["image"] as? ItunesImageDto
                                )
                                items.add(itemDto)
                            }
                            currentItem = null
                            inItem = false
                        }
                        "channel" -> inChannel = false
                    }
                }

                else -> Unit
            }
        }
        reader.close()

        RssFeedDto(
            channel = ChannelDto(
                title = channelTitle,
                description = channelDesc,
                link = channelLink,
                language = channelLang,
                author = channelAuthor,
                image = channelImage,
                item = items
            )
        )
    }
}
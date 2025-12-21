package com.ma.tehro.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Place(
    val name: String,
    @Serializable(with = PlaceCategorySerializer::class)
    val category: PlaceCategory,
    @Serializable(with = PlaceTypeSerializer::class)
    val type: PlaceType,
    val longitude: Double,
    val latitude: Double
)

@Serializable
enum class PlaceType(val value: String) {
    @SerialName("ادارات - شرکت ها - سازمان ها")
    ADMINISTRATION_COMPANIES_ORGANIZATIONS("ادارات - شرکت ها - سازمان ها"),
    @SerialName("مکان ها ی مهم")
    IMPORTANT_LOCATIONS("مکان ها ی مهم"),
    @SerialName("درمانی-دارویی")
    MEDICAL_PHARMACEUTICAL("درمانی-دارویی"),
    @SerialName("فرهنگی - مذهبی - ورزشی")
    CULTURAL_RELIGIOUS_SPORTS("فرهنگی - مذهبی - ورزشی"),
    @SerialName("آموزشی")
    EDUCATIONAL("آموزشی"),
    @SerialName("اداری - تجاری")
    ADMINISTRATIVE_COMMERCIAL("اداری - تجاری"),
    @SerialName("حمل و نقل")
    TRANSPORTATION("حمل و نقل"),
    @SerialName("سایر")
    OTHER("سایر")
}

@Serializable
enum class PlaceCategory(val value: String) {
    @SerialName("ادارات")
    ADMINISTRATION("ادارات"),
    @SerialName("مکان مهم")
    IMPORTANT_LOCATION("مکان مهم"),
    @SerialName("ادارات شهرداری")
    MUNICIPAL_ADMINISTRATION("ادارات شهرداری"),
    @SerialName("بیمارستان")
    HOSPITAL("بیمارستان"),
    @SerialName("تئاتر")
    THEATER("تئاتر"),
    @SerialName("سینما")
    CINEMA("سینما"),
    @SerialName("مراکز آموزش عالی")
    HIGHER_EDUCATION("مراکز آموزش عالی"),
    @SerialName("موزه")
    MUSEUM("موزه"),
    @SerialName("میادین")
    SQUARE("میادین"),
    @SerialName("مراکز زیارتی")
    PILGRIMAGE_SITE("مراکز زیارتی"),
    @SerialName("مراکز خرید")
    SHOPPING_CENTER("مراکز خرید"),
    @SerialName("حمل و نقل")
    TRANSPORTATION("حمل و نقل"),
    @SerialName("پارک")
    PARK("پارک"),
    @SerialName("مراکز نظامی")
    MILITARY_CENTER("مراکز نظامی"),
    @SerialName("مساجد")
    MOSQUE("مساجد"),
    @SerialName("مراکز تجاری")
    COMMERCIAL_CENTER("مراکز تجاری"),
    @SerialName("وزارت خانه")
    MINISTRY("وزارت خانه"),
    @SerialName("فرهنگسرا")
    CULTURAL_CENTER("فرهنگسرا"),
    @SerialName("هتل")
    HOTEL("هتل"),
    @SerialName("مراکز ورزشی")
    SPORTS_CENTER("مراکز ورزشی"),
    @SerialName("مراکز مذهبی")
    RELIGIOUS_CENTER("مراکز مذهبی"),
    @SerialName("سازمان")
    ORGANIZATION("سازمان"),
    @SerialName("بهداشتی")
    HEALTHCARE("بهداشتی"),
    @SerialName("مراکز تفریحی")
    RECREATION_CENTER("مراکز تفریحی"),
    @SerialName("پارکینگ")
    PARKING("پارکینگ"),
    @SerialName("مراکز آموزشی")
    EDUCATIONAL_CENTER("مراکز آموزشی"),
    @SerialName("مراکز فرهنگی")
    CULTURAL_FACILITY("مراکز فرهنگی"),
    @SerialName("مراکز حقوقی")
    LEGAL_CENTER("مراکز حقوقی"),
    @SerialName("مراکز درمانی")
    MEDICAL_CENTER("مراکز درمانی"),
    @SerialName("سازمان ها")
    ORGANIZATIONS("سازمان ها"),
    @SerialName("سفارت")
    EMBASSY("سفارت"),
    @SerialName("شرکت ها")
    COMPANIES("شرکت ها"),
    @SerialName("مراکز انتظامی")
    LAW_ENFORCEMENT("مراکز انتظامی"),
    @SerialName("مجتمع تجاری اداری")
    COMMERCIAL_ADMINISTRATIVE_COMPLEX("مجتمع تجاری اداری")
}

object PlaceTypeSerializer : KSerializer<PlaceType> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PlaceType")

    override fun serialize(encoder: Encoder, value: PlaceType) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): PlaceType {
        val value = decoder.decodeString().trim()
        return PlaceType.entries.find { it.value == value }
            ?: throw IllegalArgumentException("unknown placeType: $value")
    }
}
object PlaceCategorySerializer : KSerializer<PlaceCategory> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PlaceCategory")

    override fun serialize(encoder: Encoder, value: PlaceCategory) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): PlaceCategory {
        val value = decoder.decodeString().trim()
        return PlaceCategory.entries.find { it.value == value }
            ?: throw IllegalArgumentException("unknown placeCategory: $value")
    }
}

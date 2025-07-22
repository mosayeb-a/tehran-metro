package app.ma.scripts.place

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val name: String,
    val category: PlaceCategory,
    val type: PlaceType,
    val longitude: Double,
    val latitude: Double
)

enum class PlaceType(val type: String) {
    ADMINISTRATION_COMPANIES_ORGANIZATIONS("ادارات - شرکت ها - سازمان ها"),
    IMPORTANT_LOCATIONS("مکان ها ی مهم"),
    MEDICAL_PHARMACEUTICAL("درمانی-دارویی"),
    CULTURAL_RELIGIOUS_SPORTS("فرهنگی - مذهبی - ورزشی"),
    EDUCATIONAL("آموزشی"),
    ADMINISTRATIVE_COMMERCIAL("اداری - تجاری"),
    TRANSPORTATION("حمل و نقل"),
    OTHER("سایر")
}

enum class PlaceCategory(val category: String) {
    ADMINISTRATION("ادارات"),
    IMPORTANT_LOCATION("مکان مهم"),
    MUNICIPAL_ADMINISTRATION("ادارات شهرداری"),
    HOSPITAL("بیمارستان"),
    THEATER("تئاتر"),
    CINEMA("سینما"),
    HIGHER_EDUCATION("مراکز آموزش عالی"),
    MUSEUM("موزه"),
    SQUARE("میادین"),
    PILGRIMAGE_SITE("مراکز زیارتی"),
    SHOPPING_CENTER("مراکز خرید"),
    TRANSPORTATION("حمل و نقل"),
    PARK("پارک"),
    MILITARY_CENTER("مراکز نظامی"),
    MOSQUE("مساجد"),
    COMMERCIAL_CENTER("مراکز تجاری"),
    MINISTRY("وزارت خانه"),
    CULTURAL_CENTER("فرهنگسرا"),
    HOTEL("هتل"),
    SPORTS_CENTER("مراکز ورزشی"),
    RELIGIOUS_CENTER("مراکز مذهبی"),
    ORGANIZATION("سازمان"),
    HEALTHCARE("بهداشتی"),
    RECREATION_CENTER("مراکز تفریحی"),
    PARKING("پارکینگ"),
    EDUCATIONAL_CENTER("مراکز آموزشی"),
    CULTURAL_FACILITY("مراکز فرهنگی"),
    LEGAL_CENTER("مراکز حقوقی"),
    MEDICAL_CENTER("مراکز درمانی"),
    ORGANIZATIONS("سازمان ها"),
    EMBASSY("سفارت"),
    COMPANIES("شرکت ها"),
    LAW_ENFORCEMENT("مراکز انتظامی"),
    COMMERCIAL_ADMINISTRATIVE_COMPLEX("مجتمع تجاری اداری")
}
package com.ma.tehro.feature.guide.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Security
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

data class GuideSection(
    val title: String,
    val content: AnnotatedString,
    val icon: ImageVector,
)

val guideSections = listOf(
    GuideSection(
        title = "مسیریابی",
        icon = Icons.Rounded.Route,
        content = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("آماده‌سازی برای سفر\n")
            }
            append("پیش از هر چیز، برای ورود به مترو نیاز به ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("بلیت یا کارت مترو")
            }
            append(" دارید. اطلاعات کامل درباره انواع بلیت و نحوه تهیه آن را در بخش ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("«خرید بلیت»")
            }
            append(" مطالعه کنید.\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("انتخاب مبدا و مقصد\n")
            }
            append("برای شروع مسیریابی، ابتدا باید ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ایستگاه مبدا")
            }
            append(" خود را مشخص کنید. ")
            append("این برنامه به شما امکان می‌دهد تا مبدا را از چهار روش انتخاب کنید:\n\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("موقعیت فعلی")
            }
            append(": با انتخاب این گزینه، نزدیک‌ترین ایستگاه به شما پیدا می‌شود.\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("انتخاب از نقشه")
            }
            append(": با مراجعه به نقشه شهر و انتخاب یک نقطه، ایستگاه‌های نزدیک به آن نقطه نمایش داده می‌شوند.\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("جستجو")
            }
            append(": می‌توانید نام ایستگاه را در کادر جستجو تایپ کنید.\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("جستجو بر اساس مکان")
            }
            append(": با جستجو و انتخاب یک مکان، ایستگاه‌های نزدیک به آن را مشاهده کنید.\n\n")
            append("پس از انتخاب مبدا، به همین ترتیب ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ایستگاه مقصد")
            }
            append(" را نیز انتخاب کنید.\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("مسیر پیشنهادی\n")
            }
            append("برنامه کوتاه‌ترین مسیر را بر اساس ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("کمترین تعداد تعویض خط")
            }
            append(" به شما پیشنهاد می‌دهد. ")
            append("در صورتی که مبدا و مقصد شما روی یک خط نباشند، نیاز به تعویض خط خواهید داشت.\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("تعویض خط\n")
            }
            append("در ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ایستگاه‌های تقاطع")
            }
            append(" (ایستگاه‌هایی که دو خط در آن به هم می‌رسند) باید از قطار پیاده شوید و از طریق ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("پله‌ها یا پله برقی")
            }
            append(" به سکوی خط دیگر بروید. ")
            append("برنامه دقیقا به شما نشان می‌دهد که در کدام ایستگاه باید پیاده شوید.\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("ساعت حرکت و زمان سفر\n")
            }
            append("برنامه ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("زمان تقریبی سفر")
            }
            append(" را به شما نشان می‌دهد. ")
            append("توجه داشته باشید که ساعت حرکت قطارها در ایستگاه‌های مختلف متفاوت است و ممکن است به دلایلی مانند ترافیک مسافری یا تعمیرات، قطار دقیقا سر وقت نرسد. ")
            append("برای مشاهده زمان دقیق حرکت هر ایستگاه، به بخش ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("برنامه قطار")
            }
            append(" در صفحه اطلاعات ایستگاه مراجعه کنید.\n\n")
            append("در کل، ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("زمان‌های نمایش داده شده را تخمینی در نظر بگیرید")
            }
            append(" و برای اطمینان بیشتر، چند دقیقه زودتر در ایستگاه حاضر شوید.")
        }
    ),
    GuideSection(
        title = "خرید بلیت",
        icon = Icons.Rounded.ConfirmationNumber,
        content = buildAnnotatedString {
            append("برای استفاده از مترو، دو نوع بلیت اصلی وجود دارد:\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("۱. بلیت تک‌سفره\n")
            }
            append("اگر ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("به‌ندرت از مترو استفاده می‌کنید")
            }
            append("، بلیت تک‌سفره ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("گزینه مناسبی")
            }
            append(" است. ")
            append("این بلیت را می‌توانید از ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("باجه‌های فروش بلیت")
            }
            append(" در ایستگاه‌ها تهیه کنید.\n\n")

            append("قیمت بلیت تک‌سفره در سال ۱۴۰۵:\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("داخل شهری")
            }
            append(": ۱۰,۰۰۰ تومان (نقدی) / ۷,۳۰۰ تومان (شتابی)\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("حومه (خط ۵)")
            }
            append(": ۱۵,۰۰۰ تومان (نقدی) / ۹,۰۰۰ تومان (شتابی)\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("هشتگرد")
            }
            append(": ۲۵,۰۰۰ تومان (نقدی) / ۲۰,۰۰۰ تومان (شتابی)\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("شهر پرند")
            }
            append(": ۲۵۰,۰۰۰ تومان (نقدی) / ۲۰۰,۰۰۰ تومان (شتابی)\n\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.07f)
                )
            ) {
                append("۲. کارت مترو (اعتباری)\n")
            }
            append("اگر ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("به‌طور منظم از مترو استفاده می‌کنید")
            }
            append("، کارت مترو ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("گزینه به‌صرفه‌تر و راحت‌تری")
            }
            append(" است. ")
            append("با این کارت می‌توانید هم‌زمان از مترو و اتوبوس استفاده کنید.\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("تهیه کارت\n")
            }
            append("کارت مترو را می‌توانید از ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("باجه‌های فروش بلیت تمام ایستگاه‌ها")
            }
            append(" تهیه کنید:\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("کارت عادی")
            }
            append(": ۳۰,۰۰۰ تومان\n")
            append(" • ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("کارت طرح خاص (جاسوئیچی)")
            }
            append(": ۵۰,۰۰۰ تومان\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("شارژ کارت\n")
            }
            append("هر کارت را تا سقف ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۶۰۰,۰۰۰ تومان")
            }
            append(" می‌توان شارژ کرد. ")
            append("امکان شارژ ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("حضوری")
            }
            append(" (باجه‌ها) و ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("غیرحضوری")
            }
            append(" (سامانه شهرزاد) وجود دارد.\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("هزینه سفر با کارت\n")
            }
            append("در سال ۱۴۰۵، هزینه هر سفر با کارت مترو بر اساس ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ناحیه مسافت")
            }
            append(" تعیین می‌شود:\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("تا ۲ کیلومتر")
            }
            append(": ۵,۱۶۰ تومان\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۲ تا ۶ کیلومتر")
            }
            append(": ۵,۲۹۰ تومان\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۶ تا ۱۰ کیلومتر")
            }
            append(": ۵,۴۲۰ تومان\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۱۰ تا ۱۸ کیلومتر")
            }
            append(": ۵,۶۹۰ تومان\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۱۸ تا ۳۰ کیلومتر")
            }
            append(": ۶,۰۸۰ تومان\n")
            append(" • مسافت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("بالای ۳۰ کیلومتر")
            }
            append(": ۶,۰۸۴ تومان\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("نکات مهم\n")
            }
            append(" • در صورت ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("عدم ثبت خروج")
            }
            append(" یا گذشت بیش از ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("۲ ساعت")
            }
            append(" از ثبت ورود، مابه‌التفاوت بلیت تک‌سفره از اعتبار کارت کسر می‌شود.\n")
            append(" • بلیت تک‌سفره ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("نقدی")
            }
            append(" و ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("شتابی")
            }
            append(" قیمت متفاوتی دارد.")
        }
    ),
    GuideSection(
        title = "نکات ایمنی",
        icon = Icons.Rounded.Security,
        content = buildAnnotatedString {
            append("برای سفری ایمن، این نکات را رعایت کنید:\n\n")

            append("۱. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("به خط زرد لبه سکو نزدیک نشوید")
            }
            append(" و قبل از توقف کامل قطار از آن عبور نکنید.\n")

            append("۲. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("هنگام ورود و خروج از قطار")
            }
            append("، مراقب فاصله بین سکو و قطار باشید.\n")

            append("۳. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("از دویدن روی سکو و پله‌برقی")
            }
            append(" خودداری کنید. پله‌برقی فقط برای ایستادن است.\n")

            append("۴. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("در زمان باز و بسته شدن درهای قطار")
            }
            append("، فاصله بگیرید و مانع بسته شدن درها نشوید.\n")

            append("۵. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ابتدا اجازه دهید مسافران از قطار خارج شوند")
            }
            append("، سپس از کناره‌های درب وارد شوید.\n")

            append("۶. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("از دستکاری تجهیزات و وسایل موجود در قطار و ایستگاه")
            }
            append(" خودداری کنید.\n")

            append("۷. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ورود هرگونه حیوان، مواد قابل اشتعال و انفجار")
            }
            append(" به فضای مترو ممنوع است.\n")

            append("۸. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("در صورت گم شدن یا نیاز به کمک")
            }
            append("، به پرسنل ایستگاه مراجعه کنید.\n")

            append("۹. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("در صورت مشاهده رفتار مشکوک یا سرقت")
            }
            append("، مراتب را به راهبر قطار یا کارکنان ایستگاه اطلاع دهید.\n")

            append("۱۰. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("کیف و وسایل شخصی خود را در قطار رها نکنید")
            }
            append(" و در ایستگاه‌های شلوغ مراقب آنها باشید.\n")

            append("۱۱. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ورود به تونل و محوطه ریلی")
            }
            append(" به هیچ عنوان مجاز نیست.\n")

            append("۱۲. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("در صورت بروز حریق یا دود")
            }
            append("، از آسانسور استفاده نکنید.\n")

            append("۱۳. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("دست کودک خود را در سکوها، پله‌ها و هنگام سوار شدن")
            }
            append(" نگه دارید.\n")

            append("۱۴. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("به تابلوهای راهنما و برچسب‌های ایمنی")
            }
            append(" توجه کنید و مفاد آنها را رعایت کنید.\n")

            append("۱۵. ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("در صورت احساس ضعف یا بی‌حالی")
            }
            append("، بنشینید و از مسئول ایستگاه کمک بخواهید.")
        }
    ),
    GuideSection(
        title = "سوالات متداول",
        icon = Icons.Rounded.QuestionAnswer,
        content = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("آیا با یک بلیت می‌توانم خط را عوض کنم؟\n")
            }
            append("بله، یک بلیت برای کل مسیر کافی است و می‌توانید هر تعداد خط را عوض کنید.\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("چگونه بفهمم در چه خط و ایستگاهی هستم؟\n")
            }
            append("با نگاه به تابلوهای راهنما و رنگ قطارها می‌توانید خط خود را تشخیص دهید. همچنین تابلوهای داخل ایستگاه و قطار، نام ایستگاه فعلی و خط را نشان می‌دهند.\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("اگر مسیر را اشتباه رفتم چه کنم؟\n")
            }
            append("در ایستگاه بعدی پیاده شوید و به سمت مخالف بروید یا مسیر را دوباره محاسبه کنید.\n\n")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("چرا زمان حرکت قطارها در برنامه با رسیدن قطار یکی نیست؟\n")
            }
            append("زمان‌های نمایش داده شده در برنامه بر اساس ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("برنامه زمانی رسمی مترو تهران")
            }
            append(" تنظیم شده است، اما ممکن است به دلایلی مانند ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("ترافیک مسافری، تعمیرات یا شرایط پیش‌بینی‌نشده")
            }
            append("، قطار با کمی تاخیر یا پیش از زمان اعلام شده حرکت کند.\n\n")
            append("در هر بروزرسانی برنامه، آخرین زمان‌ها قرار می‌گیرند، اما در کل ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("زمان‌ها را تخمینی در نظر بگیرید")
            }
            append(". ")
            append("برای مشاهده برنامه رسمی قطارها، می‌توانید به ")
            pushStringAnnotation(
                tag = "URL",
                annotation = "https://metro.tehran.ir/%D8%AE%D8%AF%D9%85%D8%A7%D8%AA-%D9%85%D8%B3%D8%A7%D9%81%D8%B1%DB%8C/%D8%A8%D8%B1%D9%86%D8%A7%D9%85%D9%87-%D8%AD%D8%B1%DA%A9%D8%AA-%D9%82%D8%B7%D8%A7%D8%B1%D9%87%D8%A7"
            )
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("سایت مترو تهران")
            }
            pop()
            append(" مراجعه کنید.")
        }
    ),
)
package app.ma.scripts.schedule

import app.ma.scripts.common.RES_PATH
import java.io.File
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

private const val PAGE_URL =
    "https://metro.tehran.ir/%D8%AE%D8%AF%D9%85%D8%A7%D8%AA-%D9%85%D8%B3%D8%A7%D9%81%D8%B1%DB%8C/%D8%A8%D8%B1%D9%86%D8%A7%D9%85%D9%87-%D8%AD%D8%B1%D9%83%D8%AA-%D9%82%D8%B7%D8%A7%D8%B1%D9%87%D8%A7"

private val CP1256: Charset = Charset.forName("CP1256")
private const val UA =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

private val RENAME_RULES: List<Pair<String, String>> = listOf(
    "هشتگرد"          to "train_timetable_branch_5.xls",
    "فرودگاه مهرآباد" to "train_timetable_branch_4.xls",
    "پرند"            to "train_timetable_branch_1.xls",
    "خط يك"           to "train_timetable_1.xls",
    "خط یک"           to "train_timetable_1.xls",
    "خط دو"           to "train_timetable_2.xls",
    "خط سه"           to "train_timetable_3.xls",
    "خط چهار"         to "train_timetable_4.xls",
    "خط پنج"          to "train_timetable_5.xls",
    "خط6"             to "train_timetable_6.xls",
    "خط شش"           to "train_timetable_6.xls",
    "خط-هفت"          to "train_timetable_7.xls",
    "خط هفت"          to "train_timetable_7.xls",
)

fun runDownloader() {
    val resDir = File(RES_PATH).apply { mkdirs() }
    println("target dir: ${resDir.absolutePath}")

    println("\n[1/3] fetching page...")
    val html = fetchText(PAGE_URL)
    println("  fetched ${html.length} chars")
    require(html.length > 5000) { "Page too short (${html.length}) - likely an error page" }

    println("\n[2/3] extracting download links...")
    val urls = extractDownloadUrls(html)
    println("  found ${urls.size} urls:")
    urls.forEach { println("    $it") }
    require(urls.isNotEmpty()) { "No download URLs found on page" }

    println("\n[3/3] downloading directly into ${resDir.absolutePath} ...")
    val installed = mutableListOf<File>()
    for (url in urls) {
        println("  -> $url")
        try {
            val file = downloadTo(resDir, url)
            if (file.name.lowercase().endsWith(".zip")) {
                val extracted = extractZipInto(resDir, file)
                file.delete()
                installed += extracted
            } else {
                installed += file
            }
        } catch (e: Exception) {
            println("    FAILED: ${e.message}")
        }
    }

    println("\ninstalled ${installed.size} files:")
    installed.forEach { println("  ${it.absolutePath} (${it.length()} bytes)") }

    val expected = RENAME_RULES.map { it.second }.distinct()
    val missing = expected - installed.map { it.name }.toSet()
    if (missing.isNotEmpty()) println("\nWARNING missing targets: $missing")
    else println("\nall ${expected.size} target files present")
}

private fun Char.isHexDigit(): Boolean =
    this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'

private fun encodeUrl(rawUrl: String): String {
    val q = rawUrl.indexOf('?')
    val path = if (q >= 0) rawUrl.substring(0, q) else rawUrl
    val query = if (q >= 0) rawUrl.substring(q) else ""
    val encoded = buildString {
        var i = 0
        while (i < path.length) {
            val ch = path[i]
            when {
                ch == '%' && i + 2 < path.length &&
                    path[i + 1].isHexDigit() && path[i + 2].isHexDigit() -> append(ch)
                ch == '/' || ch == ':' || ch.isLetterOrDigit() || ch in "-._~" -> append(ch)
                ch == ' ' -> append("%20")
                else -> ch.toString().toByteArray(Charsets.UTF_8).forEach { b ->
                    append('%')
                    val v = b.toInt() and 0xFF
                    append("0123456789ABCDEF"[v ushr 4])
                    append("0123456789ABCDEF"[v and 0x0F])
                }
            }
            i++
        }
    }
    return encoded + query
}

private fun fetchText(url: String): String {
    val proc = ProcessBuilder("curl", "-L", "-sS", "-A", UA, url)
        .redirectErrorStream(true).start()
    val text = proc.inputStream.bufferedReader().readText()
    val exit = proc.waitFor()
    check(exit == 0) { "curl failed ($exit): $text" }
    return text
}

/**
 * Downloads url into resDir. Picks the final filename from RENAME_RULES applied to
 * the decoded URL filename. For zip URLs whose inner filename matches a rule,
 * uses a temporary __dl_*.zip so the caller can extract and delete.
 */
private fun downloadTo(resDir: File, url: String): File {
    val encodedName = url.substringAfterLast('/').substringBefore('?')
    val decoded = try { URLDecoder.decode(encodedName, "UTF-8") } catch (_: Exception) { encodedName }

    val directTarget = RENAME_RULES.firstOrNull { (key, _) -> decoded.contains(key) }?.second
    val finalName = directTarget ?: run {
        val safe = decoded.replace(Regex("[^\\p{L}\\p{N}._-]"), "_").take(80)
        val ext = when {
            encodedName.endsWith(".zip", true) -> ".zip"
            encodedName.endsWith(".xlsx", true) -> ".xlsx"
            else -> ".xls"
        }
        if (safe.endsWith(ext, true)) safe else safe + ext
    }

    val intermediateName = if (decoded.endsWith(".zip", true) && directTarget != null)
        "__dl_${directTarget.removeSuffix(".xls")}.zip"
    else finalName

    val out = File(resDir, intermediateName)
    val encoded = encodeUrl(url)
    val proc = ProcessBuilder(
        "curl", "-L", "-sS", "-o", out.absolutePath,
        "-A", UA, "--retry", "3", "--retry-delay", "2",
        encoded
    ).redirectErrorStream(true).start()

    val exit = proc.waitFor()
    val output = proc.inputStream.bufferedReader().readText()
    if (exit != 0 || !out.exists() || out.length() == 0L) {
        out.delete()
        throw RuntimeException("curl failed ($exit): $output")
    }
    println("     saved ${out.length()} bytes -> ${out.name}")
    return out
}

private val HREF_REGEX = Regex(
    """href="([^"]*?\.(?:zip|xls|xlsx)(?:\?[^"]*)?)"""",
    RegexOption.IGNORE_CASE
)

private fun extractDownloadUrls(html: String): List<String> =
    HREF_REGEX.findAll(html)
        .map { it.groupValues[1] }
        .map {
            when {
                it.startsWith("http") -> it
                it.startsWith("//") -> "https:$it"
                else -> "https://metro.tehran.ir/" + it.trimStart('/')
            }
        }
        .distinct()
        .toList()

/**
 * Extracts the excel entries of `zip` directly into `resDir`, applying RENAME_RULES
 * to derive final filenames. Caller deletes the zip after this returns.
 */
private fun extractZipInto(resDir: File, zip: File): List<File> {
    val results = mutableListOf<File>()
    ZipInputStream(zip.inputStream().buffered(), CP1256).use { zis ->
        while (true) {
            val entry = zis.nextEntry ?: break
            if (entry.isDirectory) continue
            val name = entry.name.substringAfterLast('/').substringAfterLast('\\')
            if (!name.lowercase().endsWith(".xls") && !name.lowercase().endsWith(".xlsx")) {
                println("     (skip non-excel entry: $name)")
                continue
            }
            val targetName = RENAME_RULES.firstOrNull { (k, _) -> name.contains(k) }?.second
                ?: name
            val out = File(resDir, targetName)
            out.outputStream().use { zis.copyTo(it) }
            println("     extracted -> ${out.name} (${out.length()} bytes)")
            results += out
        }
    }
    return results
}

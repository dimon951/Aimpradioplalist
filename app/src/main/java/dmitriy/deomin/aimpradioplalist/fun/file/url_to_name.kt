package dmitriy.deomin.aimpradioplalist.`fun`.file

fun url_to_name(url: String): String {
    val new_url = url
            .replace("http:", "")
            .replace("https:", "")
            .replace(".", "_")
            .replace("/", "_")
            .replace(".m3u", "")
            .replace(Regex("[^a-zA-Zа-яА-Я0-9_ ]"), "")
    return new_url
}
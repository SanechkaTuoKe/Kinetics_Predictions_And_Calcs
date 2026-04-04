data class ParsedId(
    val sub: String,
    val conc: String,
    val uv: String,
    val rep: String
)

fun parseId(id: String): ParsedId {
    val parts = id.split("_")
    return ParsedId(
        sub = parts[0].removePrefix("sub"),
        conc = parts[1].removePrefix("c"),
        uv = parts[2].removePrefix("us"),
        rep = parts[3].removePrefix("r")
    )
}
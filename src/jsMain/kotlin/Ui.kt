import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

fun Card.html(): HTMLDivElement = (document.createElement("div") as HTMLDivElement).apply {
    className = "card"
    innerHTML = rank.toHtml()

    style.backgroundColor = color.toHtml()
}

fun Color.toHtml() =  when (this) {
    Color.BLACK -> "#000000"
    Color.RED -> "#f44336"
    Color.GREEN -> "#4caf50"
    Color.YELLOW -> "#ffeb3b"
    Color.BLUE -> "#3f51b5"
}

fun Rank.toHtml() = when(this){
    Rank.SKIP -> "skip"
    Rank.REVERSE -> "rev"
    Rank.PLUS_2 -> "+2"
    Rank.ZERO -> "0"
    Rank.ONE -> "1"
    Rank.TWO -> "2"
    Rank.THREE -> "3"
    Rank.FOUR -> "4"
    Rank.FIVE -> "5"
    Rank.SIX -> "6"
    Rank.SEVEN -> "7"
    Rank.EIGHT -> "8"
    Rank.NINE -> "9"
    Rank.COLOR_WISH -> "?"
    Rank.COLOR_WISH_PLUS_4 -> "+4"
}

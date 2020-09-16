import Rank.*

class Card(color: Color, val rank: Rank) {

    var color = color
        set(value) = when (this.color) {
            Color.BLACK -> field = value
            else -> throw IllegalStateException()
        }

    override fun toString() = when (color) {
        Color.BLACK -> "$rank"
        else -> "$color $rank"
    }
}

enum class Color {
    RED, GREEN, YELLOW, BLUE, BLACK;
}

enum class Rank {
    SKIP, REVERSE, PLUS_2,
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
    COLOR_WISH, COLOR_WISH_PLUS_4;
}

private val specialRanks = setOf(SKIP, REVERSE, PLUS_2, COLOR_WISH, COLOR_WISH_PLUS_4)
val Card.isSpecial get() = specialRanks.contains(rank)

fun createDeck(): List<Card> = mutableListOf<Card>().apply {
    Color.values().filter { it != Color.BLACK }.forEach { color ->
        values().filter { it <= NINE }.forEach { rank ->
            val amount = if (rank != ZERO) 2 else 1
            repeat(amount) {
                add(Card(color, rank))
            }
        }
    }

    values().filter { it > NINE }.forEach { rank ->
        repeat(4) {
            add(Card(Color.BLACK, rank))
        }
    }
}


import Color.BLACK
import Rank.*


data class Player(
    val name: String,
    val cards: MutableList<Card> = mutableListOf()
)

class Game {
    private val players = mutableListOf<Player>()
    var state = GameState.LOBBY
    private val last get() = discardPile.lastOrNull()

    private val drawPile = mutableListOf<Card>()
    private val discardPile = mutableListOf<Card>()

    private var current = 0
    val currentPlayer get() = players[current]

    private var direction = 1
    private var skip = false

    private var toDraw = 0

    var winner: Player? = null

    fun addPlayer(player: Player) = when (state) {
        GameState.LOBBY -> players.addDistinct(player)
        else -> throw IllegalStateException("Can't add player while game state is $state")
    }

    fun start() {
        if (state == GameState.RUNNING) throw IllegalStateException("Cant start running game")
        if (players.size < 2) {
            throw IllegalArgumentException("At least 2 players needed")
        }
        winner = null
        discardPile.clear()
        drawPile.apply {
            clear()
            addAll(createDeck())
            shuffle()
        }

        players.map(Player::cards).forEach { cards ->
            cards.clear()
            repeat(7) {
                cards.add(drawPile.removeLast())
            }
        }

        while (last?.isSpecial != false){
            discardPile.add(drawPile.removeLast())
        }
        state = GameState.RUNNING
    }

    fun draw(): Card {
        val card = drawPile.removeLast()
        currentPlayer.cards.add(card)
        if (drawPile.isEmpty()) {
            shuffleDiscardIntoDrawPile()
        }
        return card
    }

    fun putCard(card: Card) {
        if (canPlace(card, last) && currentPlayer.cards.remove(card)) {
            discardPile.add(card)

            if ((last!!.rank == PLUS_2 && card.rank != PLUS_2) || (last!!.rank == COLOR_WISH_PLUS_4 && card.rank != COLOR_WISH_PLUS_4)) {
                repeat(toDraw) { draw() }
                toDraw = 0
            }

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (card.rank) {
                REVERSE -> direction *= -1
                SKIP -> skip = true
                PLUS_2 -> toDraw += 2
                COLOR_WISH_PLUS_4 -> toDraw += 4
            }

            endTurn()
        } else throw IllegalArgumentException("Cant place $card on $last")
    }

    private fun canPlace(new: Card, previous: Card?): Boolean {
        return previous != null && (new.color == BLACK || new.color == previous.color || new.rank == previous.rank)
    }

    fun endTurn() {
        checkWin()
        val factor = if (skip) 2 else 1
        current = (current + direction * factor) % players.size
        skip = false
    }

    private fun checkWin() {
        if (currentPlayer.cards.isEmpty()) {
            println("${currentPlayer.name} has won!")
            winner = currentPlayer
            state = GameState.ENDED
        }
    }

    private fun shuffleDiscardIntoDrawPile() {
        val last = discardPile.removeLast()
        drawPile.addAll(discardPile)
        drawPile.shuffle()
        discardPile.clear()
        discardPile.add(last)
    }

    fun gamePerspective(player: Player = currentPlayer) = GamePerspective(player, state, last!!, getEnemies(player), EnemyPlayer(currentPlayer))

    private fun getEnemies(player: Player): List<EnemyPlayer> =
        players.filterNot { it == player }.map { EnemyPlayer(it) }
}

enum class GameState {
    LOBBY, RUNNING, ENDED
}

fun <T> MutableList<T>.addDistinct(element: T) = if (contains(element)) false else add(element)

class EnemyPlayer(player: Player) {
    var cards = player.cards.size
    var name = player.name
    override fun toString() = "$name ($cards cards)"
}

data class GamePerspective(val player: Player, val state: GameState, val lastCard: Card, val enemies: List<EnemyPlayer>, val currentPlayer: EnemyPlayer)

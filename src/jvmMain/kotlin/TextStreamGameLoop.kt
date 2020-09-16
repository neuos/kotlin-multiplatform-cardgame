import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

class TextStreamGameLoop(input: InputStream, private val output: PrintStream) : GameLoop() {

    private val scanner = Scanner(input)

    override suspend fun getPlayers(): Set<Player> {
        var playerCount = 0
        while (playerCount < 2) {
            output.print("how many players? ")
            playerCount = scanner.nextLine().toInt()
        }

        val players = mutableSetOf<Player>()
        while (players.size < playerCount) {
            output.print("name of player ${players.size + 1}: ")
            val name = scanner.nextLine()
            players.add(Player(name))
        }
        return players
    }

    override suspend fun notifyGameState() {
        output.println(game.gamePerspective())
    }

    override suspend fun getAction(): GameAction {
        output.println("Play card [0-${game.currentPlayer.cards.size - 1}] or draw [d]")

        when (val action = scanner.nextLine()) {
            "d" -> return DrawAction
            "e" -> return EndAction
            else -> {
                val card = game.currentPlayer.cards[action.toInt()]
                while (card.color == Color.BLACK) {
                    output.print("What color[R/G/B/Y]? ")
                    card.color = when (scanner.nextLine()) {
                        "R" -> Color.RED
                        "G" -> Color.GREEN
                        "B" -> Color.BLUE
                        "Y" -> Color.YELLOW
                        else -> Color.BLACK
                    }
                }
                return PlaceAction(card)
            }
        }
    }

    override suspend fun notifyDrawn(drawn: Card) {
        output.println("drawn card: $drawn")
        output.println("Play card [0-${game.currentPlayer.cards.size - 1}] or end [e]")
    }

    override suspend fun notifyGameOver() {
        output.println("Gametate is ${game.state} winner is ${game.winner?.name ?: "no one"}")
    }

    override suspend fun notifyException(e: Exception) {
        output.println("!! ${e}")
    }

}

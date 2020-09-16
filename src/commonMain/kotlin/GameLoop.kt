sealed class GameAction {
    override fun toString() = this::class.simpleName!!
}

object DrawAction : GameAction()
object EndAction : GameAction()
class PlaceAction(val card: Card) : GameAction()

abstract class GameLoop {
    protected abstract suspend fun getPlayers(): Set<Player>
    protected abstract suspend fun notifyGameState()
    protected abstract suspend fun getAction(): GameAction
    protected abstract suspend fun notifyDrawn(drawn: Card)
    protected abstract suspend fun notifyGameOver()
    protected abstract suspend fun notifyException(e: Exception)

    val game = Game()

    suspend fun start() {
        val players = getPlayers()
        players.forEach { game.addPlayer(it) }

        game.start()

        var action: GameAction
        while (game.state == GameState.RUNNING) {
            try {
                notifyGameState()
                action = getAction()
                if (action is DrawAction) {
                    val drawn = game.draw()
                    notifyDrawn(drawn)

                    action = getAction()
                    if (action is EndAction) {
                        game.endTurn()
                        continue
                    }
                }

                if (action is PlaceAction) {
                    val placeCard = action.card
                    game.putCard(placeCard)
                } else throw IllegalArgumentException("Cant do $action now")
            } catch (e: Exception) {
                notifyException(e)
            }
        }
        notifyGameOver()
    }
}




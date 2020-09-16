package sample

import Game
import Player
import createDeck
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GameFieldTest {
    @Test
    fun twoPlayers(){
        val game = Game()
        val player1 = Player("p1")
        val player2 = Player("p2")

        game.addPlayer(player1)
        game.addPlayer(player2)

        assertEquals(game.state, GameState.LOBBY)
        game.start()
        assertEquals(game.state, GameState.RUNNING)

        assertEquals(game.gamePerspective(player1).player.cards.size, 7)
        assertEquals(game.gamePerspective(player2).player.cards.size, 7)

    }

    @Test
    fun fullDeck() {
        val deck = createDeck()
        assertEquals(108, deck.size)
    }
}

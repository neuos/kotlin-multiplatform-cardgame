package sample

import Game
import Player
import createDeck
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GameFieldTest {
    @Test
    fun twoPlayers(){
        val gameField = Game()
        val player1 = Player("p1")
        val player2 = Player("p2")

        gameField.addPlayer(player1)
        gameField.addPlayer(player2)


        assertEquals(gameField.gamePerspective(player1).player.cards.size, 7)
        assertEquals(gameField.gamePerspective(player2).player.cards.size, 7)

    }

    @Test
    fun fullDeck() {
        val deck = createDeck()
        assertEquals(10 * 4 + 5, deck.size)
    }
}

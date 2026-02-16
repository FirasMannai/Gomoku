/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ttt;

import java.util.List;

public class StrategyBlock<M> implements IGameKI<M> {
    @Override
    public IGame<M> doBestMove(IGame<M> game) {
        return game.doMove(bestMove(game));
    }

    @Override
    public M bestMove(IGame<M> game) {
        byte me = game.currentPlayer();
        byte opp = game.otherPlayer(me);
        List<M> moves = game.moves();

        // 1. Check for immediate win
        for (M move : moves) {
            IGame<M> next = game.doMove(move);
            if (next.wins(me)) {
                return move;
            }
        }

        // 2. Block opponent's immediate win
        for (M move : moves) {
            IGame<M> next = game.doMove(move);
            if (next.wins(opp)) {
                return move;
            }
        }

        // 3. Fallback to random
        return new StrategyRandom<M>().bestMove(game);
    }
}


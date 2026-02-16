/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ttt;
import java.util.List;

public class StrategyMinimax<M> implements IGameKI<M> {
    @Override
    public IGame<M> doBestMove(IGame<M> game) {
        M move = bestMove(game);
        if (move == null) return game;
        return game.doMove(move);
    }

    @Override
    public M bestMove(IGame<M> game) {
        return bestMove(game, getDepth());
    }

    public M bestMove(IGame<M> game, int depth) {
        List<M> moves = game.moves();
        if (moves.isEmpty()) return null;
        final byte player = game.currentPlayer();
        int val = Integer.MIN_VALUE;
        M result = null;

        for (M move : moves) {
            IGame<M> newState = game.doMove(move);
            int eval = evalNextState(newState, player, depth - 1);

            if (eval > val) {
                val = eval;
                result = move;
            }
        }
        return result;
    }

    protected int evalNextState(IGame<M> game, byte player, int depth) {
        return minimax(game, player, depth);
    }

    protected int minimax(IGame<M> game, byte player, int depth) {
        if (depth == 0 || game.endedGame()) {
            return game.evalState(player);
        }

        boolean isMax = game.currentPlayer() == player;
        int resultVal = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<M> moves = game.moves();
        if (moves.isEmpty()) return game.evalState(player);

        for (M move : moves) {
            IGame<M> child = game.doMove(move);
            int nextVal = minimax(child, player, depth - 1);

            if ((isMax && nextVal > resultVal) || (!isMax && nextVal < resultVal)) {
                resultVal = nextVal;
            }
        }
        return resultVal;
    }

    protected int getDepth() {
        return 3; 
    }
}







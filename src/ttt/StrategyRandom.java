/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ttt;
import java.util.List;
import java.util.Random;

public class StrategyRandom<M> implements IGameKI<M> {
    private final Random rand = new Random();

    @Override
    public IGame<M> doBestMove(IGame<M> game) {
        M move = bestMove(game);
        if (move == null) return game; // No moves: return unchanged
        return game.doMove(move);
    }

    @Override
    public M bestMove(IGame<M> game) {
        List<M> moves = game.moves();
        if (moves.isEmpty()) return null; // <- Prevents crash at game end!
        return moves.get(rand.nextInt(moves.size()));
    }
}



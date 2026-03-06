/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ttt;
import java.util.List;

/**
 * StrategyMinimax implements the Minimax algorithm for game AI.
 * <p>
 * This class provides methods to determine the best move for a given game state
 * using the Minimax strategy. It is generic and works with any game type that
 * implements the IGame interface.
 *
 * @param <M> The move type for the game.
 */
public class StrategyMinimax<M> implements IGameKI<M> {
    /**
     * Executes the best move for the current player using the Minimax algorithm.
     *
     * @param game The current game state.
     * @return The new game state after the best move, or the original state if no move is possible.
     */
    @Override
    public IGame<M> doBestMove(IGame<M> game) {
        M move = bestMove(game);
        if (move == null) return game;
        return game.doMove(move);
    }

    /**
     * Determines the best move for the current player using the default search depth.
     *
     * @param game The current game state.
     * @return The best move found, or null if no moves are available.
     */
    @Override
    public M bestMove(IGame<M> game) {
        return bestMove(game, getDepth());
    }

    /**
     * Determines the best move for the current player using the specified search depth.
     * Implements the Minimax decision process by evaluating all possible moves.
     *
     * @param game The current game state.
     * @param depth The maximum search depth for the Minimax algorithm.
     * @return The best move found, or null if no moves are available.
     */
    public M bestMove(IGame<M> game, int depth) {
        List<M> moves = game.moves();
        if (moves.isEmpty()) return null;
        final byte player = game.currentPlayer();
        int val = Integer.MIN_VALUE;
        M result = null;

        // Evaluate each possible move using Minimax
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

    /**
     * Evaluates the next game state using the Minimax algorithm.
     *
     * @param game The game state to evaluate.
     * @param player The player for whom the evaluation is performed.
     * @param depth The remaining search depth.
     * @return The evaluation score for the game state.
     */
    protected int evalNextState(IGame<M> game, byte player, int depth) {
        return minimax(game, player, depth);
    }

    /**
     * Recursively applies the Minimax algorithm to evaluate game states.
     * <p>
     * The algorithm simulates all possible moves up to the given depth,
     * alternating between maximizing and minimizing the evaluation score
     * depending on the current player.
     *
     * @param game The current game state.
     * @param player The player for whom the evaluation is performed.
     * @param depth The remaining search depth.
     * @return The evaluation score for the game state.
     */
    protected int minimax(IGame<M> game, byte player, int depth) {
        // Base case: reached maximum depth or game ended
        if (depth == 0 || game.endedGame()) {
            return game.evalState(player);
        }

        boolean isMax = game.currentPlayer() == player;
        int resultVal = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<M> moves = game.moves();
        if (moves.isEmpty()) return game.evalState(player);

        // Recursively evaluate all possible moves
        for (M move : moves) {
            IGame<M> child = game.doMove(move);
            int nextVal = minimax(child, player, depth - 1);

            // Update resultVal based on maximizing or minimizing player
            if ((isMax && nextVal > resultVal) || (!isMax && nextVal < resultVal)) {
                resultVal = nextVal;
            }
        }
        return resultVal;
    }

    /**
     * Returns the default search depth for the Minimax algorithm.
     *
     * @return The search depth (default is 3).
     */
    protected int getDepth() {
        return 3; 
    }
}








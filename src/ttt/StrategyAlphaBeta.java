/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import java.util.*;

/**
 * StrategyAlphaBeta implements the Alpha-Beta pruning algorithm for game AI.
 * <p>
 * This class determines the best move for a given game state using Alpha-Beta pruning,
 * an optimization of the Minimax algorithm that reduces the number of nodes evaluated.
 * It is generic and works with any game type implementing the IGame interface.
 *
 * @param <M> The move type for the game.
 */
public class StrategyAlphaBeta<M> implements IGameKI<M> {
    private final int maxDepth = 4; // Fixed depth

    /**
     * Executes the best move for the current player using Alpha-Beta pruning.
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
     * Determines the best move for the current player using Alpha-Beta pruning.
     * <p>
     * The method first checks for immediate wins or blocks, then orders moves,
     * and finally applies Alpha-Beta search to select the optimal move.
     *
     * @param game The current game state.
     * @return The best move found, or null if no moves are available.
     */
    @Override
    public M bestMove(IGame<M> game) {
        List<M> moves = new ArrayList<>(game.moves()); //  mutable list
        if (moves.isEmpty()) return null;

        byte me = game.currentPlayer();
        byte opp = game.otherPlayer(me);

        // Check for immediate win
        for (M move : moves) {
            if (game.doMove(move).wins(me)) return move;
        }

        // Block opponent's immediate win
        for (M move : moves) {
            if (game.doMove(move).wins(opp)) return move;
        }

        // Move ordering at root for efficiency
        if (game instanceof Gomoku) {
            Gomoku gomoku = (Gomoku) game;
            Map<M, Integer> evalCache = new HashMap<>();
            for (M move : moves) {
                Pair<Byte, Byte> pos = (Pair<Byte, Byte>) move;
                evalCache.put(move, gomoku.evalStateForPosition(me, pos.first, pos.second));
            }
            moves.sort(Comparator.comparingInt(m -> -evalCache.get(m)));
        }

        // Alpha-Beta search: efficiently evaluates moves by pruning branches
        int bestVal = Integer.MIN_VALUE;
        M bestMove = null;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;

        for (M move : moves) {
            IGame<M> next = game.doMove(move);
            int val = alphabeta(next, me, maxDepth - 1, alpha, beta, false);
            if (val > bestVal || bestMove == null) {
                bestVal = val;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestVal);
        }

        return bestMove;
    }

    /**
     * Recursively applies the Alpha-Beta pruning algorithm to evaluate game states.
     * <p>
     * Alpha-Beta pruning reduces the number of nodes evaluated by the Minimax algorithm
     * by eliminating branches that cannot affect the final decision. The method alternates
     * between maximizing and minimizing, updating alpha and beta values to prune suboptimal branches.
     *
     * @param game The current game state.
     * @param player The player for whom the evaluation is performed.
     * @param depth The remaining search depth.
     * @param alpha The best already explored option for the maximizer.
     * @param beta The best already explored option for the minimizer.
     * @param isMaximizing True if maximizing player's turn, false otherwise.
     * @return The evaluation score for the game state.
     */
    private int alphabeta(IGame<M> game, byte player, int depth, int alpha, int beta, boolean isMaximizing) {
        // Base case: reached maximum depth or game ended
        if (depth == 0 || game.endedGame()) {
            return game.evalState(player);
        }

        List<M> moves = new ArrayList<>(game.moves());
        if (moves.isEmpty()) return game.evalState(player);

        // Move ordering at depth = 3 for efficiency
        if (depth == 3 && game instanceof Gomoku) {
            Gomoku gomoku = (Gomoku) game;
            Map<M, Integer> evalCache = new HashMap<>();
            for (M move : moves) {
                Pair<Byte, Byte> pos = (Pair<Byte, Byte>) move;
                evalCache.put(move, gomoku.evalStateForPosition(player, pos.first, pos.second));
            }
            moves.sort(Comparator.comparingInt(m -> -evalCache.get(m)));
        }

        if (isMaximizing) {
            int value = Integer.MIN_VALUE;
            for (M move : moves) {
                IGame<M> next = game.doMove(move);
                // Update value and alpha for maximizing player; prune branches where alpha >= beta
                value = Math.max(value, alphabeta(next, player, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // Beta cut-off: prune branch
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (M move : moves) {
                IGame<M> next = game.doMove(move);
                // Update value and beta for minimizing player; prune branches where beta <= alpha
                value = Math.min(value, alphabeta(next, player, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // Alpha cut-off: prune branch
            }
            return value;
        }
    }
}










/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import java.util.*;

public class StrategyAlphaBeta<M> implements IGameKI<M> {
    private final int maxDepth = 4; // Fixed depth

    @Override
    public IGame<M> doBestMove(IGame<M> game) {
        M move = bestMove(game);
        if (move == null) return game;
        return game.doMove(move);
    }

    @Override
    public M bestMove(IGame<M> game) {
        List<M> moves = new ArrayList<>(game.moves()); //  mutable list
        if (moves.isEmpty()) return null;

        byte me = game.currentPlayer();
        byte opp = game.otherPlayer(me);

        // 1. Win immediately?
        for (M move : moves) {
            if (game.doMove(move).wins(me)) return move;
        }

        // 2. Block opponent's win?
        for (M move : moves) {
            if (game.doMove(move).wins(opp)) return move;
        }

        // 3. Move ordering at root (depth = 4)
        if (game instanceof Gomoku) {// Evaluate and sort moves by how good they are
            Gomoku gomoku = (Gomoku) game;
            Map<M, Integer> evalCache = new HashMap<>();
            for (M move : moves) {
                Pair<Byte, Byte> pos = (Pair<Byte, Byte>) move;
                evalCache.put(move, gomoku.evalStateForPosition(me, pos.first, pos.second));
            }
            moves.sort(Comparator.comparingInt(m -> -evalCache.get(m)));
        }

        // 4. Alpha-Beta search
        int bestVal = Integer.MIN_VALUE;// loop over all moves and evaluate using alphabeta()
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

    private int alphabeta(IGame<M> game, byte player, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || game.endedGame()) {
            return game.evalState(player);
        }

        List<M> moves = new ArrayList<>(game.moves()); //  mutable list
        if (moves.isEmpty()) return game.evalState(player);

        // Move ordering at depth = 3 only
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
                //value stores the best score so far for the maximizing player.
                value = Math.max(value, alphabeta(next, player, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // Beta cut-off
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (M move : moves) {
                IGame<M> next = game.doMove(move);
                value = Math.min(value, alphabeta(next, player, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // Alpha cut-off
            }
            return value;
        }
    }
}










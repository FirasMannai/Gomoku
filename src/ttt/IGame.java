/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package ttt;

import java.util.List;

public interface IGame<M> {
    List<M> moves();
    IGame<M> doMove(M move);
    byte currentPlayer();
    byte otherPlayer(byte player);
    boolean noMoreMove();
    boolean endedGame();
    boolean wins(byte player);
    int evalState(byte player);
}


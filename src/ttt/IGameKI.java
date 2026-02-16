/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package ttt;

public interface IGameKI<M> {
    IGame<M> doBestMove(IGame<M> game);
    M bestMove(IGame<M> game);
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ttt;


public interface IRegularGame<M> extends IGame<M> {
    byte getRows();
    byte getCols();
    byte getPlayer1();
    byte getPlayer2();
    byte getPlayerNone();
    byte getAtPosition(byte row, byte col);
    IRegularGame<M> setAtPosition(byte row, byte col);
}

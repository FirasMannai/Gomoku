///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//

package ttt;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * XMLGameStorage is a utility for saving and loading Gomoku game states using XML files.
 * <p>
 * Uses JDOM2 for XML creation and parsing. The XML structure includes:
 * <ul>
 *   <li>Root element <b>Gomoku</b> with attributes: rows, cols, currentPlayer</li>
 *   <li>Element <b>MoveCount</b> for total moves</li>
 *   <li>Multiple <b>Stone</b> elements, each with player, row, col attributes</li>
 * </ul>
 * The save method serializes the current game state, and the load method reconstructs a Gomoku object from XML.
 */
public class XMLGameStorage {
    /**
     * Saves the current Gomoku game state to an XML file.
     * <p>
     * Serializes board size, current player, move count, and all occupied positions as Stone elements.
     * Uses JDOM2 for XML creation.
     *
     * @param game The game state to save.
     * @param file The file to write the XML to.
     * @throws Exception If saving fails.
     */
    public static void save(IRegularGame<Pair<Byte, Byte>> game, File file) throws Exception {
        Element root = new Element("Gomoku");
        root.setAttribute("rows", String.valueOf(game.getRows()));
        root.setAttribute("cols", String.valueOf(game.getCols()));
        root.setAttribute("currentPlayer", String.valueOf(game.currentPlayer()));
        // Store total moves
        root.addContent(new Element("MoveCount").setText(String.valueOf(((Gomoku) game).movesDone)));

        // Store each occupied board position as a Stone element
        for (byte r = 0; r < game.getRows(); r++) {
            for (byte c = 0; c < game.getCols(); c++) {
                byte occupant = game.getAtPosition(r, c);
                if (occupant != game.getPlayerNone()) {
                    Element stone = new Element("Stone")
                        .setAttribute("player", String.valueOf(occupant))
                        .setAttribute("row", String.valueOf(r))
                        .setAttribute("col", String.valueOf(c));
                    root.addContent(stone);
                }
            }
        }

        Document doc = new Document(root);
        try (FileWriter fw = new FileWriter(file)) {
            new XMLOutputter(Format.getPrettyFormat()).output(doc, fw);
        }
    }

    /**
     * Loads a Gomoku game state from an XML file.
     * <p>
     * Parses board size, current player, move count, and all Stone elements to reconstruct the game.
     * Uses JDOM2 for XML parsing.
     *
     * @param file The XML file to load from.
     * @return The reconstructed Gomoku game object.
     * @throws Exception If loading or parsing fails.
     */
    public static Gomoku load(File file) throws Exception {
        SAXBuilder sax = new SAXBuilder();
        Document doc = sax.build(file);
        Element root = doc.getRootElement();

        byte rows = Byte.parseByte(root.getAttributeValue("rows"));
        byte cols = Byte.parseByte(root.getAttributeValue("cols"));
        byte current = Byte.parseByte(root.getAttributeValue("currentPlayer"));

        Gomoku game = new Gomoku(rows, cols);
        game.player = current;

        // Restore move count if present
        Element movesEl = root.getChild("MoveCount");
        if (movesEl != null) {
            game.movesDone = Integer.parseInt(movesEl.getText());
        }

        // Restore board positions from Stone elements
        List<Element> stones = root.getChildren("Stone");
        for (Element stone : stones) {
            byte r = Byte.parseByte(stone.getAttributeValue("row"));
            byte c = Byte.parseByte(stone.getAttributeValue("col"));
            byte p = Byte.parseByte(stone.getAttributeValue("player"));
            game.board[r][c] = p;
            game.lastRow = r;
            game.lastCol = c;
        }

        return game;
    }
}

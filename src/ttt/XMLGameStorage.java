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

public class XMLGameStorage {
    // --- Save ---
    public static void save(IRegularGame<Pair<Byte, Byte>> game, File file) throws Exception {
        Element root = new Element("Gomoku");
        root.setAttribute("rows", String.valueOf(game.getRows()));
        root.setAttribute("cols", String.valueOf(game.getCols()));
        root.setAttribute("currentPlayer", String.valueOf(game.currentPlayer()));
        // Moves done
        root.addContent(new Element("MoveCount").setText(String.valueOf(((Gomoku) game).movesDone)));

        // Stones
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

    // --- Load ---
    public static Gomoku load(File file) throws Exception {
        SAXBuilder sax = new SAXBuilder();
        Document doc = sax.build(file);
        Element root = doc.getRootElement();

        byte rows = Byte.parseByte(root.getAttributeValue("rows"));
        byte cols = Byte.parseByte(root.getAttributeValue("cols"));
        byte current = Byte.parseByte(root.getAttributeValue("currentPlayer"));

        Gomoku game = new Gomoku(rows, cols);
        game.player = current;

        // Move count (optional)
        Element movesEl = root.getChild("MoveCount");
        if (movesEl != null) {
            game.movesDone = Integer.parseInt(movesEl.getText());
        }

        // Stones
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

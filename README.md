🎮 Gomoku – Five in a Row
📌 Project Description

This project implements the board game Gomoku (Five in a Row) in Java using Swing.

The goal of the game is to place exactly five stones in a row
(horizontal, vertical, or diagonal).

⚠️ Important Rule:

- A row of exactly 5 stones wins.
- A row of 6 or more stones does NOT win.
- In that case, the game continues.

🏗 Architecture

The system follows the MVC (Model–View–Controller) pattern:

🧠 Model

Gomoku:

- Contains game logic
- Board state
- Win detection
- Evaluation function for AI

🎨 View

GomokuBoard:

- Draws the board
- Displays stones
- Highlights winning stones
- Shows preview stone

🎮 Controller

GomokuControl:

- Handles mouse input
- AI turns
- Undo functionality
- Game result handling

🤖 Strategy Pattern (AI)

Different AI strategies implement:

IGameKI


Available strategies:

S1 – Random
S2 – Blocking
S3 – Minimax
S4 – Alpha-Beta Pruning

This allows flexible AI replacement without changing the game logic.

🌐 Network Mode

Two players can play over the network:

PP server → Start as server (Player 1)

PP localhost → Connect as client (Player 2)

Network classes:

GomokuNetwork
GomokuNetworkControl

💾 Features

✔ Player vs Player
✔ Player vs Computer
✔ Computer vs Computer
✔ Network Player vs Player
✔ Undo (PC mode only)
✔ Save / Load game (XML)
✔ Debug mode (console + file)
✔ Winning 5 stones highlighted
✔ Ghost preview stone
✔ Statistics tracking

🚀 How to Run
Show Help
java -jar Gomoku.jar H

Player vs Computer
java -jar Gomoku.jar PC S1

Player vs Computer with Debug
java -jar Gomoku.jar PC S4 D

Computer vs Computer
java -jar Gomoku.jar CC S2 S4

Network Mode

Server:

java -jar Gomoku.jar PP server


Client:

java -jar Gomoku.jar PP localhost

🐞 Debug Mode

Use D to enable debug mode.

Example:

java -jar Gomoku.jar PC S3 D


This will:

Print moves in console
Save moves and statistics to gomoku_debug.txt

💾 Save & Load

Games are saved in:

savedgame.xml

XML structure example:

<Gomoku rows="15" cols="15" currentPlayer="1">
    <MoveCount>20</MoveCount>
    <Stone player="1" row="5" col="4"/>
    <Stone player="2" row="6" col="7"/>
</Gomoku>

🧩 Design Patterns Used
1️ Strategy Pattern

Used for AI strategies.
Advantage:

- Easy to extend with new AI types
- No modification of core game logic needed

2️ MVC Pattern

Separation of:

- Game logic
- GUI
- Control flow

Advantage:

- Clean structure
- Easier maintenance
- Better scalability

📚 Technologies

- Java
- Swing
- XML (DOM)
- Sockets (TCP Networking)
- PlantUML (Design Documentation)
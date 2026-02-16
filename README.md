# 🎮 Gomoku – Five in a Row

> **A classic strategy board game implemented in Java using Swing.**

## 📌 Project Description
This project implements the board game **Gomoku** (also known as Five in a Row) using the **Model-View-Controller (MVC)** architectural pattern. It features a robust AI with multiple difficulty levels, network play capabilities, and game state persistence.

### ⚠️ Important Rule
The goal is to place **exactly five stones** in a row (horizontal, vertical, or diagonal).
> **Note:** A row of 6 or more stones (overlines) **does NOT win**. The game continues until an exact 5-stone sequence is formed.

---

## 🏗 Architecture
The system follows the **MVC Pattern** to ensure a clean separation of concerns:

### 🧠 Model (`Gomoku`)
* Contains core game logic & rules.
* Manages the board state (15x15 grid).
* Handles win detection algorithms.
* Provides evaluation functions for the AI.

### 🎨 View (`GomokuBoard`)
* Custom `JPanel` rendering (Graphics2D).
* Draws the grid lines and stones.
* Highlights winning stones with visual cues.
* Displays a "Ghost Stone" preview under the mouse cursor.

### 🎮 Controller (`GomokuControl`)
* Handles user input (Mouse Clicks).
* Manages game flow (Turns, Undo, Reset).
* Orchestrates AI moves.

---

## 🤖 Strategy Pattern (AI)
We use the **Strategy Pattern** to allow flexible switching of AI behaviors without modifying the core game code. All strategies implement the `IGameKI` interface.

| Strategy | Description |
| :--- | :--- |
| **S1** | 🎲 **Random:** Places stones completely randomly. |
| **S2** | 🛡️ **Blocking:** Basic logic to block opponent's winning lines. |
| **S3** | 🧠 **Minimax:** Recursive algorithm (Depth 3) to find optimal moves. |
| **S4** | ⚡ **Alpha-Beta:** Optimized Minimax with pruning for better performance. |

---

## 💾 Features
* [x] **PvP:** Player vs Player (Local).
* [x] **PvC:** Player vs Computer (AI).
* [x] **CvC:** Computer vs Computer (Watch AI battle).
* [x] **Network:** Play over TCP/IP (Server/Client).
* [x] **Undo:** Revert moves (Available in PC mode).
* [x] **Persistence:** Save & Load games via XML.
* [x] **Debug Mode:** Console logging + File output (`gomoku_debug.txt`).
* [x] **Visuals:** Highlight winning stones & Ghost preview.

---

## 🚀 How to Run

### Basic Usage
Display the help menu:
```bash
java -jar Gomoku.jar H

```

### Game Modes

**1. Player vs Computer (Random AI)**

```bash
java -jar Gomoku.jar PC S1

```

**2. Player vs Computer (Alpha-Beta AI + Debug Mode)**

```bash
java -jar Gomoku.jar PC S4 D

```

**3. Computer vs Computer**

```bash
java -jar Gomoku.jar CC S2 S4

```

### 🌐 Network Mode

**Host a Game (Server - Player 1):**

```bash
java -jar Gomoku.jar PP server

```

**Join a Game (Client - Player 2):**

```bash
java -jar Gomoku.jar PP localhost

```

---

## 🐞 Debug & Storage

### Debug Mode (`D`)

Adding the `D` flag enables detailed logging:

1. **Console:** Prints every move and AI evaluation score.
2. **File:** Appends statistics to `gomoku_debug.txt`.

### Save & Load

The game state is saved in **XML format**.

* **File:** `savedgame.xml`
* **Format:** DOM-based XML structure.

**Example XML:**

```xml
<Gomoku rows="15" cols="15">
    <Stone row="7" col="7" player="1" />
    <Stone row="7" col="8" player="2" />
    </Gomoku>

```

---

## 🧩 Design Patterns Used

### 1️⃣ Strategy Pattern

* **Usage:** Encapsulates AI algorithms (`Random`, `Minimax`, `AlphaBeta`).
* **Advantage:** New AI difficulties can be added easily without touching the `Gomoku` logic class.

### 2️⃣ MVC Pattern

* **Usage:** Separates Data (`Model`), Interface (`View`), and Logic (`Controller`).
* **Advantage:** Makes the code scalable, easier to maintain, and allows for swapping the GUI (e.g., to JavaFX) without breaking the logic.

---

## 📚 Technologies

* **Language:** Java 17+
* **GUI:** Swing (Custom Painting)
* **Data:** XML (DOM Parser)
* **Networking:** Java Sockets (TCP)
* **Docs:** PlantUML

```

```

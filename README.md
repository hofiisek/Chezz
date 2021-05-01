# Chezz
Full-featured chess written in Kotlin.

Using TornadoFx (https://tornadofx.io/) for GUI, kotest (https://github.com/kotest/kotest)
for unit testing, ktlint (https://github.com/pinterest/ktlint) for code style,
and detekt (https://github.com/detekt/detekt) for code analysis.

### Why?
- mainly to get familiar with Kotlin and to see its power
- also, lockdown boredom has been going on for too long already

### How to run it?
- the simplest way is to open the project in IntelliJ IDEA and run `ChezzApp.kt`

### Features
- valid movement of pieces (incl. en passant, castling, and promotion)
- check, checkmate, stalemate
- undo last move
- import/export from/to the standard Portable Game Notation (PGN) format
  
### Features/ideas to implement
- minimax/alpha-beta prunning AI
- timer
- GUI improvements/extensions
    - showing taken pieces
    - showing PGN of current game state
    - allow to enable/disable highlighting of allowed moves and check

### TODO list
- definitely a better test coverage (using deterministic tests rather than non-deterministic)
- configure `detekt` appropriately or fix the debt
- fix GUI
  - window size is hard-coded (ideally it should scale automatically based on its content)
  - or at least hard-code the window height properly (menu-bar height is not fixed which breaks it, see the little white strip at the bottom)
- implement the rest of rules that can end the game
  - win on time (when timer is implemented)
  - threefold repetition 
  - fifty-move rule
  - dead position

# Chezz
Full-featured chess written in Kotlin.

Using TornadoFx (https://tornadofx.io/) for GUI, kotest (https://github.com/kotest/kotest)
for unit testing, ktlint (https://github.com/pinterest/ktlint) for code style,
and detekt (https://github.com/detekt/detekt) for code analysis.

### Why?
- mainly to get familiar with Kotlin and to see its power
- also, lockdown boredom has been going on for too long already

### Features
- valid movement of pieces
- check, checkmate, stalemate, promotion
- undo last move
- import/export from/to the standard Portable Game Notation (PGN) format
  
### Yet to be implemented
- minimax/alpha-beta prunning AI
- timer
- GUI improvements/extensions
    - showing taken pieces
    - showing PGN of current game state
    - allow to enable/disable highlighting of allowed moves and check

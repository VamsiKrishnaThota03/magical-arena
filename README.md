# Magical Arena

A turn-based, dice-driven battle simulator written in plain Java: two players fight until one of them runs out of health.

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Dependencies](https://img.shields.io/badge/dependencies-none-brightgreen)

## Overview

Magical Arena is a small, dependency-free console game. Each player has **health**, **strength** and **attack** attributes, all positive integers. Players take turns attacking. On every turn the attacker and the defender each roll a six-sided die, and the result decides how much damage gets through. The match ends as soon as one player's health reaches zero.

The project includes a framework-free test runner (`MagicalArenaTests`) that uses a scripted die for deterministic checks. It exits with a non-zero status when a test fails, so it works in scripts and CI.

## Game rules

| Rule | Implementation |
|------|----------------|
| Who starts | The player with **lower health** attacks first. On a tie, the first player passed to `Match` starts. |
| Attack damage | `attacker.attack × attackRoll` (roll is 1 to 6) |
| Defence | `defender.strength × defendRoll` (roll is 1 to 6) |
| Damage dealt | `max(0, attackDamage − defence)`, subtracted from the defender's health. Health never goes below 0. |
| Turn order | Players alternate until the defender's health is 0 |
| Endless fights | A match where neither player can ever beat the other's lowest defence (`attack × 6 ≤ opponent strength` both ways) is rejected up front |

## Features

- `Player`: validated name, health, strength and attack. Health is clamped at zero.
- `Dice`: a small interface with a fair six-sided implementation (`Dice.sixSided()`, optionally seeded for reproducible runs).
- `Match`: runs the turn loop, logs every attack, returns the winner, and rejects invalid matches (same player twice, or no way for either side to win).
- `MagicalArena`: plays two sample matches, or a custom match from command-line arguments.
- `MagicalArenaTests`: 18 deterministic tests covering validation, the damage formula, turn order, termination and logging.

## Tech stack

- Java 17 or later (uses records), standard library only
- Optional `Makefile` for build, run and test. Plain `javac`/`java` commands also work.

## Project structure

```
magical-arena/
├── Makefile
└── src/
    ├── main/java/com/magicalarena/
    │   ├── MagicalArena.java    # entry point: sample or custom matches
    │   ├── Match.java           # turn loop and attack resolution
    │   ├── Player.java          # player attributes and health
    │   └── Dice.java            # die abstraction and six-sided implementation
    └── test/java/com/magicalarena/
        └── MagicalArenaTests.java   # dependency-free test runner
```

## Getting started

### Prerequisites

- JDK 17 or later (`javac` and `java` on your `PATH`)

### With make

```bash
git clone https://github.com/VamsiKrishnaThota03/magical-arena.git
cd magical-arena

make run                                       # two sample matches
make run ARGS="Hero 60 6 12 Ogre 90 9 7"       # custom match: NAME HEALTH STRENGTH ATTACK x2
make test                                      # run the tests
make clean
```

### Without make

```bash
javac -d out/main $(find src/main/java -name '*.java')
java -cp out/main com.magicalarena.MagicalArena

javac -d out/test -cp out/main $(find src/test/java -name '*.java')
java -cp out/main:out/test com.magicalarena.MagicalArenaTests
```

On Windows, use `;` instead of `:` in the test classpath.

## Example output

Dice rolls are random, so every run is different. A typical run looks like this:

```
The match begins: Player A (health 50, strength 5, attack 10) vs Player B (health 100, strength 10, attack 5)
Player A rolls 4 (40 attack) vs Player B rolls 5 (50 defence): 0 damage. Player B's health is now 100.
Player B rolls 6 (30 attack) vs Player A rolls 1 (5 defence): 25 damage. Player A's health is now 25.
...
Player A is defeated! Player B wins after 9 turns.
```

And `make test`:

```
[PASS] player stores its attributes
[PASS] player rejects blank name and non-positive attributes
...
[PASS] match log reports the winner

18 tests, 18 passed, 0 failed
```

## Design notes

- **Separation of concerns**: `Player` holds state, `Dice` encapsulates randomness, and `Match` owns the game flow and the damage formula, which lives in one place.
- **Testability**: the die is injected, so tests script exact rolls instead of relying on chance. Output goes to a `PrintStream` that tests can capture or turn off.
- **No dependencies**: compiles with nothing but the JDK.

## Contact

Thota Vamsi Krishna (vamsikrishnathota483@gmail.com)

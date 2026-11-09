# Magical Arena

A turn-based, dice-driven battle simulator written in plain Java — two players fight until one of them runs out of health.

![Java](https://img.shields.io/badge/Java-8%2B-orange?logo=openjdk&logoColor=white)
![Dependencies](https://img.shields.io/badge/dependencies-none-brightgreen)

## Overview

Magical Arena is a small, dependency-free console game. Each player has **health**, **strength** and **attack** attributes. Players take turns attacking; on every turn both the attacker and the defender roll a six-sided die, and the outcome decides how much damage gets through. The match ends as soon as one player's health drops to zero or below.

The project also ships with a lightweight, framework-free test harness (`ManualTests`) that checks the core classes and prints `[PASS]` / `[FAIL]` for each assertion.

## Game rules

| Rule | Implementation |
|------|----------------|
| Who starts | The player with **lower health** attacks first (`Match` constructor) |
| Attack damage | `attacker.attack × attackRoll` (roll is 1–6) |
| Defence | `defender.strength × defendRoll` (roll is 1–6) |
| Damage dealt | `max(0, attackDamage − defenceDamage)`, subtracted from the defender's health |
| Turn order | Players alternate until a defender's health is `<= 0` |

## Features

- `Player`: holds name, health, strength and attack, plus a standalone `attackOpponent` method
- `Dice`: rolls a fair six-sided die (`1`–`6`)
- `Match`: runs the turn loop, alternates attacker/defender and logs every attack to the console
- `MagicalArena`: entry point that simulates two sample matches (A vs B, C vs D)
- `ManualTests`: no-framework tests for player initialisation, dice bounds, attack logic and full matches

## Tech stack

- Java (JDK 8 or later), standard library only — no build tool or external dependencies

## Project structure

```
magical-arena/
├── MagicalArena.java   # Entry point: runs two sample matches
├── Match.java          # Turn loop and attack resolution
├── Player.java         # Player attributes and attack method
├── Dice.java           # Six-sided die
└── ManualTests.java    # Assertion-based manual tests
```

## Getting started

### Prerequisites

- JDK 8 or later (`javac` and `java` on your `PATH`)

### Build and run

```bash
git clone https://github.com/VamsiKrishnaThota03/magical-arena.git
cd magical-arena

# Compile all sources
javac *.java

# Play the sample matches
java MagicalArena

# Run the manual tests
java ManualTests
```

## Example output

Dice rolls are random, so every run is different. A typical run of `java MagicalArena` looks like:

```
The match begins!
Player A attacks with damage 20 (30 attack vs 10 defense). Player B's health is now 80.
Player B attacks with damage 0 (5 attack vs 20 defense). Player A's health is now 50.
...
Player A is defeated!
```

And `java ManualTests`:

```
[PASS] Player name should be 'Player A'
[PASS] Player health should be 50
[PASS] Player strength should be 5
[PASS] Player attack should be 10
[PASS] Dice roll should always be between 1 and 6
Player A attacks Player B: 50 - 25 = 25 damage
[PASS] Defender's health should decrease or remain unchanged if the attack was fully defended
[PASS] Match should be successfully initialized
...
[PASS] One player's health should reach 0 or below after the match ends
```

## Design notes

- **Separation of concerns**: `Player` holds state, `Dice` encapsulates randomness, `Match` owns the game flow.
- **No dependencies**: compiles with nothing but the JDK.
- **Testability**: `ManualTests` exercises each class directly without needing JUnit.

## Contact

Thota Vamsi Krishna — vamsikrishnathota483@gmail.com

# EternalCardGameSimulator


[![Build Status](https://travis-ci.org/osake/EternalCardGameSimulator.png?branch=master)](https://travis-ci.org/osake/EternalCardGameSimulator)


## Overview

This is my simulator for Eternal Card Game which is built using sbt.

## Setup

For the Mac, using brew:

```
brew install sbt
sbt compile
```

## Execution

The simulator's runner can accept a number of time to execute the simulator.  For example,
running the simulator 10 times:

```
sbt "run 10"
```

Sample output from a single run, which is a bit cryptic in some spaces, but the intent is to show an ASCII board state.

```
PlayerTwo wins! true
********************
Cards in deck: 58, Power: 9/9, Player health: 5
Cards in hand: Refresh(2), Praxis Displacer(4),
Void:
Board: Timekeeper(0/0), Bold Adventurer(2/3), Dormant Sentinel(0/0), Timekeeper(0/0), Towering Terrazon(6/5), Initiate of the Sands(1/1),
====================
Board: Scavenging Vulture(1/1), Scavenging Vulture(1/1), Dark Wisp(1/1), Sporefolk(1/2), Xenan Destroyer(3/3),
Void:
Cards in hand: Spirit Drain(6), Vara's Favor(2), Dark Return(1),
Cards in deck: 58, Power: 9/9, Player health: 0
********************
```

## Testing

Use `sbt test` to execute the test suite.


---

I originally wrote this using Ruby, but decided to plunk around with Scala, so this is the rewrite.
It is currently unfinished, but gives anyone interested a starting point.




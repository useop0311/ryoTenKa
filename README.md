[English](README.md) / [Korean](README_ko.md)
# Expansion Game
Plugin for Expansion Game within Minecraft

# Language
This plugin support only Korean.

# Rules
■ Progress Period

The period must be set by the server admin. You can stop the game by typing  `/stop_game`.
It ends immediately when only one force remains.

■ Starting People & Forces & Map

Starting People: Minimum : 2 / Recommended : 5 people ~ 16 / Maximum : 16
Force Classification: Color Assignment
Spawn: Random location on the map
Progression: Vanilla Wild
Map Size: The server admin directly sets the world border to ensure balance.

■ Faction Transition Rules

Converting the dead people's power to the dead one's power
No beds, map-wide random respawn upon death
When you die, items drop, and you start with default equipment when you respawn.

■ Plugin Self-balancing Control

When a major force occurs (over a certain number of people, set directly by the server admin), a glowing effect is created or coordinates are revealed at certain times.
Central area (500×500 based on 0,0): PVP prohibited daily from 20:00 to 21:00

■ Continued Death Avoidance (Penalty)

If you die more than 10 times in total, you cannot enter the central area.
How to cancel the penalty: Reduce the number of deaths by 5 times with 1 diamond block.

■ Re-independent System

Condition:
Offer 9 diamond blocks to the 0 0 central altar. You must endure 100X100 at 0 0 for 15 minutes to achieve independence.

On success:
Immediately moves to random location, gives new force color
Starts as a one-person force (can be absorbed again)
Existing force personnel remain the same

■ Victory Conditions

If only 1 faction remains, that faction wins.

# Commands
### Game Management
`/init_game` - Reset the game environment

`/start_game` - Start game

`/stop_game` - Stop game
### User Command
`/escape` - Reduce the number of deaths by 5 times with 1 diamond block.

`/doklib` - Re-independence process begins with 9 diamond blocks

# Contribute
(IG, Discord) @naxunse - Planning Expansion Game

(IG, X, Github, Discord) @useop0311 - Developing Expansion Game

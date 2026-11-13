# AccurateManhunt
**This Minecraft Plugin allows you to play Manhunt**
### Features
- Quick start command
- Multiple speedrunners
- Select between speedrunners to track in the Tracker Menu


## Required Dependency: [ProtocolLib](https://github.com/dmulloy2/ProtocolLib/releases/tag/5.4.0)

## Commands
### Quick Commands

| Command | Description |
| ----------- | ------- |
| **/quickstartmanhunt <speedrunner(s)>** | Used to quickly start manhunt, skipping the configuration. |

- Resets all the old settings
- Assigns all the names in the command to speedrunners, and others to hunters
- Heal, Clear Inventory, Revoke advancements and Set Survival for all players in the game.
- It sets time to 0 ticks
- Starts the countdown, then prepare, and when the speedrunner hits a hunter, it will start the game.

| Command | Description |
| -------- | ---------- |
| **/compass** | Allows hunters to get compass if they lost one.| 


### Configuration Commands

#### Assigning and removing
You have to assign players to be either speedrunner or hunter.|
Suggestion
￼
Feature a gallery image
The featured gallery image is often how your project makes its first impression.
Visit gallery page
Review disclosures
Make sure users are aware of
At least one player needs to be a speedrunner and another player to be a hunter to start a game.

To assign players:
| Command | Description |
| -------- | ---------- |
| /manhunt speedrunner add <targets>        | Assign targets to be SPEEDRUNNERs            |
| /manhunt speedrunner remove <targets>        | Unassignes targets from SPEEDRUNNERs            |
| /manhunt hunter add <targets>        | Assign targets to be HUNTERs            |
| /manhunt hunter remove <targets>        | Unassigns targets frim HUNTERs            |

#### Countdown
When the game state sets to Countdown state, this teleports players to the speedrunner, allows them flight and makes them immune to entity attacks. 
When the countdown finishes, this disables their flight and makes and disable immunity, and sets the game state to Prepared

| Command |
| ------- |
| /manhunt startCountdown |

#### Prepared
When the game state sets to Prepare state, this make the hunter not able to hit other players (including speedrunner(s)). In this state only the speedrunner(s) is/are allowed to hit hunters. When the speedrunner hits the hunter, the game starts.

| Command |
| ------- |
| /manhunt prepare |

#### Start
When the game starts, all hunters get a Tracker.
Only defeating all speedrunners, will the hunter win.
The speedrunner has to Beat the Ender Dragon, inorder to win the game.

| Command |
| ------- |
| /manhunt start |

#### Stop
You can stop the manhunt with command
| Command |
| ------- |
| /manhunt stop |

#### Other Commands

| Command | Description |
| -------- | ---------- |
| /manhunt list | Gives the current state of the game. Lists who are assigned as speedrunner and who are hunter|
| /manhunt resetWorldTimeTick          | Sets the world time ticks to 0            |
| /manhunt heal        | Set all participants health to 20, food level to 20, saturation to 5, and exhaustion to 0            |
| /manhunt clearInventory         | Clears inventory of all participants            |
| /manhunt setSurvival        | Sets all participants' gamemode to survival            |
| /manhunt revokeAdvancement        | Revokes all participants' advancements            |
| /manhunt getCompass        | Gets the hunter (executor) a compass            |


## Permissions
| permission | Description | default |
| ---------- | ---------- | -------- |
| accurate-manhunt.manhunt.use           | Allows to use manhunt commands            | op        |


# Controls

The mod is controlled using a set of commands and key inputs.

## Commands

The commands are invoked with `/minecrafttassimulator <command> [<...args>]`. The commands are listed below.

### `start`

Usage: `/minecrafttassimulator start`

This command starts the editor.

### `stop`

Usage: `/minecrafttassimulator stop`

This command stops the editor.

### `save`

Usage: `/minecrafttassimulator save [<format>]`

Opens the "save to file" dialogue. The file format of the written file may be specified by `<format>`.

### `load`

Usage: `/minecrafttassimulator load [<format>]`

Opens the "open file" dialogue. The file format of the opened file may be specified by `<format>`.

### `reinitsim`

Usage: `/minecrafttassimulator reinitsim`

Re-initiates the simulations. This will remove the footprints that are not saved, as well as replacing the virtual player.

### `setstartpos`

Usage: `/minecrafttassimulator setstartpos [<location>]`

Sets the initial position to a new loction. If `<location>` is present, the new location is set to the coordinates. If it is absent, it is set to the player's current position.

### `setstartmotion`

Usage: `/minecrafttassimulator setstartmotion [<velocity>]`

Sets the initial velocities to a new value. If `<velocity>` is present, the new velocity is set to the motion vector. If it is absent, it is set to the player's current velocity.

### `setstartinvulnerabilityframes`

Usage: `/minecrafttassimulator setstartinvulnerabilityframes <ticks>`

Sets the initial length of invulnerability ticks to `<ticks>` ticks. These act as though the player recently respawned or a S+Q was performed.

### `setstartgametype`

Usage: `/minecrafttassimulator setstartgametype <gamemode>`

Sets the initial game mode. This is set automatically when starting a simulation. If `<gamemode>` is `-1` (`NOT_SET`), the game mode will not be changed.

### `setmousesentivity`

Usage: `/minecrafttassimulator setmousesensitivity <sensitivity>`

Sets the simulated mouse sensitivity. `<sensitivity>` is the save value as saved in `config.txt`, that is a decimal value from `0` to `1` and the default ("100%") is `0.5`. This affects the angles slightly, as by default they are calculated to the nearest pixel.

### `setmousemaxsafemovement`

Usage: `/minecrafttassimulator setmousemaxsafemovement <pixels>`

Sets the maximum mouse movement such that the simulated mouse movements must fit in. If a larger movement is done in a tick, they are simulated to be split within this maximum range. This technically changes angles because of the imprecision of floating-point arithmetic.

### `toggleinheriteffectsfromallticks`

Usage: `/minecrafttassimulator toggleinheriteffectsfromallticks`

Toggles on or off if the virtual player should receive the effects from all simulated ticks.

### `togglegui`

Usage: `/minecrafttassimulator togglegui`

Toggles on or off the GUI.

### `selecttick`

Usage: `/minecrafttassimulator selecttick <tick>`

Directly selects a tick.

### `warpto`

Usage: `/minecrafttassimulator warpto [s|p|save1|save2|save3] [<tick>]`

Teleports to the position and camera angle to the specified simulated tick.

### `setrotationyaw`

Usage: `/minecrafttassimulator setrotationyaw [<angle>]`

Sets the yaw to the specific angle. Use to get a far angle or a precise value. If `<angle>` is not present, it is set to the player's current camera yaw.

### `setrotationpitch`

Usage: `/minecrafttassimulator setrotationpitch [<angle>]`

Sets the pitch to the specific angle. Use to get a far angle or a precise value. If `angle` is not presnt, it is set to the player's current camera pitch.

### `setrotationexact`

Usage: `/minecrafttassimulator setrotationexact true|false`

Sets whether the camera angle should be set to the exact specified angle. This is off by default, which means the angles are approximated to the nearest pixel.

### `setmousebuttoninputs`

Usage: `/minecrafttassimulator setmouseinputs <inputs>`

Sets the mouse button inputs to be simulated. `<inputs>` is a series of the following:

* `L` - Press down left mouse button
* `l` - Release left mouse button
* `R` - Press down right mouse button
* `r` - Release right mouse button

Note that buttons are not automatically released. A single click must be written as pressing down followed by releasing, e.g. `Ll`.

### `detail`

Usage: `/minecrafttassimulator detail [s|p|save1|save2|save3] [<tick>]`

Prints useful information to the chat. If `<tick>` is absent, it will be read from the selected tick.

### `savestatestoslot`

Usage: `/minecrafttassimulator savestatestoslot 1|2|3`

Saves the simulated trajectory to be viewed alongside modifications.

### `removestatesfromslot`

Usage: `/minecrafttassimulator removestatesfromslot 1|2|3`

Removes a saved trajectory.

## Keyboard Controls

The core functions of the mod are controlled using the keyboard.

### `Ctrl+Shift+P`

Starts or stops the editor.

### `Ctrl+Shift+R`

Re-initializes the simulations.

### `R`

Selects the tick before.

### `Shift+R`

Move to before the first tick.

### `Y`

Selects the tick after. It will not move beyond the bound.

### `Shift+Y`

Select the last tick. If the last tick was already selected, select the next tick.

### `Alt+R`

Move the starting tick of prediction a tick before.

### `Alt+Shift+R`

Move the starting tick of prediction to before the first tick.

### `Alt+Y`

Move the starting tick of prediction a tick after.

### `Alt+Shift+Y`

Move the starting tick of prediction to the end. If already starting at the end, move by a tick.

### `Alt+T`

Move the starting tick of prediction to the selected tick.

### `C`, `V`

Respectively shortens or lengthens the length of the simulation by 1 tick.

### `I`, `J`, `K`, `L`, `B`, `N`, `M`

Toggles whether to simulate pressing `W`, `A`, `S`, `D`, jump, sneak, and sprint keys, respectively.

### `Shift+I`, `Shift+J`, `Shift+K`, `Shift+L`

Changes the angle by 1 pixel in their respective direction.

### `Shift+U`

Sets the angles to the player's current camera angles.

### `Alt+K`

Sets the angles to be the same as the tick before.

### `Alt+J`, `Alt+U`, `Alt+O`, `Alt+L`

Changes the angle to the closest pixel by 90 degrees to the left, 45 degrees to the left, 45 degrees to the right, and 90 degrees to the right respectively.

### `Alt+Delete`

Removes the inputs from the selected tick.

<!--### `Ctrl+Shift+Y`

Appends a point to the footprint display of the simulation.-->

### `Ctrl+R`

Runs or aborts the simulation.

### `Ctrl+P`

Runs or aborts the prediction. It is done using a virtul player. It can generally be done faster, but it may be less accurate.

# 0.0.6 - YYYY/MM/DD
* Fixed a fatal crash when attempting to start prediction such that it must be started before the time specified.
* Report the positions of the riding entities precisely.

# 0.0.5 - 2021/12/11
* Made IO to file extensible to different formats, although only `.mcsim` (the default custom format) has been implemented yet. Possible candidates for external formats include `.tas` files for TASmod.
* The information about the riding entities are now recorded.
* Changed commands
  * `save` and `load` now accept a file format as an argument

# 0.0.4 - 2021/12/08
* Added support for simulating mouse clicks
* Added support for changing mouse sensitivity and maximum singular mouse movement to be simulated
* Increased accuracy for a larger range of camera movements
* Added commands
  * `setmousebuttoninputs` sets the mouse clicks to be simulated
* Fixed accepted numerical input for some commands
* Keybinds that toggle something were changed to only be triggered once per tick

# 0.0.3 - 2021/06/17
* Improved accuracy of camera angles

# 0.0.2 - 2021/06/04
* Added commands
  * `savestatestoslot` and `removestatesfromslot` to save the simulated trajectory that are shown alongside the live simulate and predicted trajectories.
* Fixed `i` and `k` key being reversed

# 0.0.1 - 2021/05/17
* A more accurate method to calculate the camera angles, which may be toggled using `setrotationexact` command
* Added commands
  * `setstartgametype` allows to change the game mode automatically when starting simulation
  * `selecttick` selects any tick directly
  * `warpto` teleports to the simulated position for a given tick
  * `setrotationyaw` and `setrotationpitch` sets the angle precisely, to any value
  * `setrotationexact` to use the input angles directly for this tick
* Modified commands
  * `detail` now shows the actual angle used, which may be different from the input
* Fixed several crashes involving chat messages
* Fixed a bug where an incorrect angle is taken when taken from the camera

# 0.0.0 - 2021/04/24
* Initial commit
* Basic keybinds and UI
* Basic save and load functionality
* "Simulation" and "prediction"
* Support for potion effects
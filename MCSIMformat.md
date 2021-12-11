# .MCSIM File Format Documentation

## General Construction

An `.mcsim` file is a text file.
Generally, the inputs are read line by line.
Empty lines or lines that start with `%` are ignored.
There are a few sections containing different parts of the file, which are noted with `!<name>`, case insensitive.

## Sections

### `property`
This section notes various properties for the simulation other than the inputs. Some are configurations for the simulation, while others provide information about the file. They are written as key-value pairs.

* `startPosition=<x>,<y>,<z>`
  Sets the starting position to (`<x>`,`<y>`,`<z>`). If not present, default to (0,0,0).
* `startMotion=<x>,<y>,<z>`
  Sets the starting velocity to (`<x>`,`<y>`,`<z>`). If not present, default to (0,0,0).
* `startInvulnerabilityFrames=<n>`
  Sets the starting invulnerability ticks to `<n>` ticks. If not present, default to 0.
* `startGametype=<str>`
  * Sets starting game mode.
* `mouseSensitivity=<f>`
  * Sets the mouse sensitivity as written in config.txt. If not present, default to 0.5 (100%).
* `mouseMaxSafeMovement=<n>`
  * Sets the maximum pixels of movement done with 1 input. If more pixels are traveled, they are simulated to be broken up into multiple inputs, all but the last inputs being maxed. If not present, default to 900.
* `tickLength=<n>`
  * Sets length of simulation to `<n>`. If not present, default to 0.
* `rerecords=<n>`
  * Sets the simulation rerecord count to `<n>`. If not present, default to 0.
* `predictionRerecords=<n>`
  * Sets the prediction rerecord count to `<n>`. If not present, default to 0.
* `totalRerecords=<n>`
  * Sets the total rerecord count to `<n>`. If not present, default to 0.
* `fileFormatVersion=<n>`
  * Indicates that the file version is `<n>`.
* `editorVersion=<str>`
  * Indicates that the file was created with editor version `<str>`.

### `input`
This section contains the inputs that is simulated. They are written as a table.

* Lines are of format `<tick#>|<buttons>|<yaw>,<pitch>|<flags>`.
* `<tick#>` or `<buttons>` must not contain `|`.
* Leading 0s in `<tick#>` are ignored.
* If some ticks are missing, they are filled down from the last written input.
* If `<tick#>` a string of `#`s, then `<buttons>` represent the columns headers for button table.
* Buttons:
  * `W` or `w` - Move forward
  * `A` or `a` - Strafe left
  * `S` or `s` - Move backward
  * `D` or `d` - Strafe right
  * `_` - Jump
  * `+` - Sneak
  * `^` - Sprint
* Buttons are seen pressed if the corresponding position is not ` ` or `.`.
* Flags:
  * There may be multiple flags for a line. A flag may be followed by `{` to provide additional options, which is closed by `}`.
  * `=` - Yaw and pitch is exact at this point, otherwise approximate using the shortest path by pixel.
  * `m` - Mouse clicks should be simulated. There may be multiple inputs in a tick. The contents are the string of the letters:
    * `L` - Press down LL
    * `l` - Release LC
    * `R` - Press down RC
    * `r` - Release LC
  * `;` - Reserved to do nothing.
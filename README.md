
# InstantReplay

InstantReplay is a Minecraft plugin for Spigot versions 1.8 to 1.17 (inclusive) which allows Minecraft server admins to
review events by re-watching them.

- [Encountering Issues](#encountering-issues)
- [Installation](#installation)
- [Usage](#usage)
  - [Permissions](#permissions)
  - [View commands](#view-commands)
  - [Starting a replay](#starting-a-replay)
  - [Changing replay speed](#changing-replay-speed)
  - [Pausing a replay](#pausing-a-replay)
  - [Resuming a replay](#resuming-a-replay)
  - [Replay timestamps](#replay-timestamps)
    - [Parsing a date-time](#parsing-timestamps-with-a-date-time)
    - [Parsing with timezones](#parsing-timestamps-with-timezones)
  - [Reloading configuration files](#reloading-configuration-files)
  - [Clearing logs](#clearing-logs)
- [Configuration](#configuration)
- [Development](#development)

## Encountering Issues

If, at any point, you encounter issues with InstantReplay (whether that be installing, using or developing the plugin)
open
a [support request](https://github.com/Ben-D-Anderson/InstantReplay/issues/new?assignees=Ben-D-Anderson&labels=support&template=support-request.md&title=)
under the [issues tab](https://github.com/Ben-D-Anderson/InstantReplay/issues) and I'll be happy to help. You should
receive a response within 24 hours of opening the support request.

## Installation

1. Ensure you have made a MySQL database for the plugin to use - **the same database should not be used across multiple
   servers with InstantReplay.**
2. Download the [latest version](https://github.com/dmulloy2/ProtocolLib/releases/latest)
   of [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) and move it into the `plugins` folder of your Minecraft
   server.
3. Download the [latest version](https://github.com/Ben-D-Anderson/InstantReplay/releases/latest) of InstantReplay from
   the [releases tab](https://github.com/Ben-D-Anderson/InstantReplay/releases) and move it into the `plugins` folder of
   your Minecraft server.
4. Start your Minecraft server, and you should see a configuration file generated at `plugins/InstantReplay/config.yml`.
5. Edit the `mysql` section of the configuration file to contain the details of your MySQL database - this is required
   for InstantReplay to function.

   For Example:
      ```yaml
      mysql:
        username: "admin"
        password: "adminpassword"
        host: "127.0.0.1"
        database: "instantreplay"
        port: 3306
      ```

7. Save the changes to the configuration file and restart the Minecraft server. The plugin should now be installed and
   functional.

## Usage
### Permissions

Any player attempting to use the plugin should have the required permissions given by your permission manager of choice.
The permissions can be found in the configuration file under `settings` with the base permission being set
at `settings.replay-permission`.

### View commands

If a player has the base replay permission, they will be able to view their allowed commands using `/replay help`. This
help message is also configurable in config as `invalid-argument`.

### Starting a replay

If a player has the base replay permission, they can start a replay with the command `/replay start <radius> <time>`
where `<radius>` is the block radius around them where the events unfolded and `<time>` is how long ago they wish to
start the replay from. The `<time>` argument supports four formats:

- `{NUMBER}s` - number of seconds ago
- `{NUMBER}m` - number of minutes ago
- `{NUMBER}h` - number of hours ago
- `{NUMBER}t` - a timestamp where `{NUMBER}` is either the seconds or milliseconds
  since [Unix Epoch](https://www.unixtimestamp.com/). See also [replay timestamps](#replay-timestamps).

A speed argument may also be optionally passed to the command with the format `/replay start <radius> <time> [speed]`
where `[speed]` is an integer (whole number). The default speed is `1` (meaning 1x speed). For example, if `2` was
chosen as the speed then the replay run at 2x speed.

### Changing replay speed

You can dynamically change the speed of a running (or paused) replay using the command `/replay speed <speed>`
where `<speed>` is an integer (whole number).

For example, if the replay started with a speed of `2` (2x speed) and you ran the command `/replay speed 1` then the
replay would run at 1x speed (half of the starting speed).

### Pausing a replay

Whilst a user is playing/watching a replay, the can pause it using the command `/replay pause`. This pauses the replay
at its current point and stops rendering the events to the player.

Pausing a replay does not hide the NPCs or block changes that the viewer can currently see, it merely stops rendering
events. The progress of the replay will not be lost. See also [resuming a replay](#resuming-a-replay).

### Resuming a replay

Whilst a user is playing/watching a replay and has paused it, they can resume the replay using the
command `/replay resume`. This resumes the replay from the point it was paused at and begins rendering events again. See
also [pausing a replay](#pausing-a-replay).

### Replay timestamps

If a player has the base replay permission, they can get the current timestamp
in [Unix Epoch seconds](https://www.unixtimestamp.com/) at any time using the command `/replay timestamp`.

Command use cases:

- If the player is currently viewing a replay, it will return the current timestamp in the replay.
- If the player is not viewing a replay, then it will return the current timestamp - not the timestamp in a replay.

#### Parsing timestamps with a date-time

A player can also parse a specified date-time into a timestamp by adding the formatted date-time as an additional
argument to the command. This makes the command `/replay timestamp [datetime]` where `[datetime]` is the date and time
to parse into a timestamp in the format specified in the configuration file as `timestamp-converter-format`.

The default value of `timestamp-converter-format` in the configuration file is `yyyy/MM/dd-HH:mm:ss`. The date is in the
format [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) (`year-month-day`) and the time is in a
conventional `hour:minute:second` format, date and time are seperated by a hyphen. An example of using the default
format would be `/replay timestamp 2021/10/23-18:54:00`.

#### Parsing timestamps with timezones

A player can also provide a timezone when specifying a date-time to parse. This is ideal when a player in a different
timezone reports an event to the server admin and the admin must view the replay using the provided time and knowledge
of the timezone.

This makes the command `/replay timestamp [datetime] [timezone]` where `[timezone]` is ideally in the
format `{area}/{city}`, for example `Europe/Paris` or `America/Los_Angeles`. However, timezone abbreviations are also
supported (but not encouraged), for example `EST` and `PST`. Furthermore, timezone offsets can also be used, for
example (`GMT-8` or `GMT-08:00`) and (`GMT+2` or `GMT+02:00`).

### Reloading configuration files

If a player has the configuration reload permission (usually defined in the configuration file
at `settings.replay-reload-permission`), they can reload the configuration file used by the plugin. This simply means
the plugin refreshes the values it uses from the configuration file for things such as messages and most settings.

### Clearing logs

If a player has the permission to clear event logs (usually defined in the configuration file
at `settings.replay-clearlogs-permission`), they can permanently delete all event logs stored in the MySQL database for
InstantReplay by running the command `/replay clearlogs`.

Once the logs have been deleted, the data in them **cannot be recovered**. It is recommended to store occasional backups
of the MySQL database used by InstantReplay if you wish to recover event data, in the case that the event logs are
cleared. Therefore, it is advised that very few individuals are given the required permissions to clear the event logs.

## Configuration

...

## Development

...

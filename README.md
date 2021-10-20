<div align="center">

# InstantReplay

<br>

_What is it?_

InstantReplay is a Minecraft plugin for Spigot versions 1.8 to 1.17 (inclusive) which allows Minecraft server
administrators to review events by re-watching them.

_Why use it?_

This plugin is ideal for Minecraft servers running game-modes such as Factions and HCF, as players often break rules
which would need recorded proof of the player violating the rule in order to punish them. Gone are the days of recording
your screen to catch rule-breakers, server admins can now re-watch the rule violation occurring in-game.

</div>

<br>

## Contents

- [Encountering Issues](#encountering-issues)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Development](#development)

<br>

## Encountering Issues

If, at any point, you encounter issues with InstantReplay (whether that be installing, using or developing the plugin)
open
a [support request](https://github.com/Ben-D-Anderson/InstantReplay/issues/new?assignees=Ben-D-Anderson&labels=support&template=support-request.md&title=)
under the [issues tab](https://github.com/Ben-D-Anderson/InstantReplay/issues) and I'll be happy to help. You should
receive a response within 24 hours of opening the support request.

<br>

## Installation

1. Ensure you have MySQL database for the plugin to use - **the same database should not be used across multiple servers
   with InstantReplay.**
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

<br>

## Usage

- [Permissions](#permissions)
- [View commands](#view-commands)
- [Starting a replay](#starting-a-replay)
- [Changing replay speed](#changing-replay-speed)
- [Pausing a replay](#pausing-a-replay)
- [Resuming a replay](#resuming-a-replay)
- [Replay timestamps](#replay-timestamps)
    - [Parsing a date-time](#parsing-timestamps-with-a-date-time)
    - [Parsing a time](#parsing-timestamps-with-a-time)
    - [Parsing with timezones](#parsing-timestamps-with-timezones)
- [Reloading configuration files](#reloading-configuration-files)
- [Clearing logs](#clearing-logs)

<br>

### Permissions

Any player attempting to use the plugin should have the required permissions given by your permission manager of choice.
The permissions can be found in the configuration file under `settings` with the base permission being set
at `settings.replay-permission`.

See more information on permissions below:

- [Base permission](#replay-permission-string)
- [Clearlogs permission](#replay-clearlogs-permission-string)
- [Reload permission](#replay-reload-permission-string)

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
- `{NUMBER}t` - a timestamp where `{NUMBER}` is either the seconds or milliseconds since
  [Unix Epoch](https://www.unixtimestamp.com/). See also [replay timestamps](#replay-timestamps).

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
to parse into a timestamp in the format specified in the configuration file as the `timestamp-converter-format-datetime`
setting.

The default value of the `timestamp-converter-format-datetime` setting in the configuration file
is `yyyy-MM-dd/HH:mm:ss`. The date and time are in the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) extended
date-time format, with date and time being seperated by a forward-slash. An example of using the default format would be
`/replay timestamp 2021/10/23-18:54:00` - this represents the 23rd of October 2021, at the time 18:54 (54 minutes past
6pm).

#### Parsing timestamps with a time

A player can also parse a specified time into a timestamp by adding the formatted time as an additional argument to the
command. This makes the command `/replay timestamp [time]` where `[time]` is the time to parse into a timestamp in the
format specified in the configuration file as the `timestamp-converter-format-time`
setting.

This works exactly the same way as [parsing with a datetime](#parsing-timestamps-with-a-date-time), however this parsing
method just assumes that the day it is parsing is the current day.

The default value of the `timestamp-converter-format-time` setting in the configuration file is `HH:mm:ss`. The time is
in the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) extended time format. An example of using the default format
would be `/replay timestamp 18:54:00` - this represents the current day, at the time 18:54 (54 minutes past 6pm).

#### Parsing timestamps with timezones

A player can also provide a timezone when specifying a date-time or time to parse. This is ideal when a player in a
different timezone reports an event to the server admin and the admin must view the replay using the provided time and
knowledge of the timezone.

This makes the command `/replay timestamp [datetime/time] [timezone]` where `[timezone]` is ideally in the
format `{area}/{city}`, for example `Europe/Paris` or `America/Los_Angeles`. However, timezone abbreviations are also
supported (but not encouraged), for example `EST` and `PST`. Furthermore, timezone offsets can also be used, for
example (`GMT-8` or `GMT-08:00`) and (`GMT+2` or `GMT+02:00`).

It is also worth noting that the timezone is used to retrieve the current date if only a time was specified.

### Reloading configuration files

If a player has the configuration reload permission (usually defined in the configuration file
at `settings.replay-reload-permission`), they can reload the configuration file used by the plugin. This simply means
the plugin refreshes the values it uses from the configuration file for things such as messages and most settings.

It is only recommended using the `/replay reload` command to reload messages in the configuration file. For settings,
most of them won't be affected by reloading the configuration file - instead the server should be restarted.

### Clearing logs

If a player has the permission to clear event logs (usually defined in the configuration file
at `settings.replay-clearlogs-permission`), they can permanently delete all event logs stored in the MySQL database for
InstantReplay by running the command `/replay clearlogs`.

Once the logs have been deleted, the data in them **cannot be recovered**. It is recommended to store occasional backups
of the MySQL database used by InstantReplay if you wish to recover event data, in the case that the event logs are
cleared. Therefore, it is advised that very few individuals are given the required permissions to clear the event logs.

<br>

## Configuration

- [MySQL](#mysql)
- [Settings](#settings)
- [Messages](#messages)

<br>

### MySQL

As mentioned in the [installation instructions](#installation), you must configure the MySQL database to be used by
InstantReplay in the `mysql` section of the configuration file. However, this database **should not** be used for
different InstantReplay instances across multiple servers - support for this may be added in the future, but currently
it will cause unexpected behavior.

An example mysql configuration is shown below:

```yaml
mysql:
  username: "admin"
  password: "adminpassword"
  host: "127.0.0.1"
  database: "instantreplay"
  port: 3306
```

If you wish to run InstantReplay on multiple Minecraft servers and use the same MySQL server then consider creating a
different database on the MySQL server for each InstantReplay instance. For example one InstantReplay instance could use
database `instantreplay_1` and another InstantReplay instance could use database `instantreplay_2`.

### Settings

- [use-plugin-prefix](#use-plugin-prefix-boolean)
- [seconds-per-timestamp-output](#seconds-per-timestamp-output-integer)
- [ignore-y-radius](#ignore-y-radius-boolean)
- [seconds-per-player-move-log](#seconds-per-player-move-log-double)
- [seconds-per-player-inventory-log](#seconds-per-player-inventory-log-double)
- [hours-until-logs-deleted](#hours-until-logs-deleted-integer)
- [event-render-buffer](#event-render-buffer-integer)
- [timestamp-converter-format-datetime](#timestamp-converter-format-datetime-string)
- [timestamp-converter-format-time](#timestamp-converter-format-time-string)
- [replay-permission](#replay-permission-string)
- [replay-reload-permission](#replay-reload-permission-string)
- [replay-clearlogs-permission](#replay-clearlogs-permission-string)

#### use-plugin-prefix (boolean)

Determines whether every time a message is sent to a user (from InstantReplay), it should be prefixed with the value
stored as `plugin-prefix` in the configuration file.

#### seconds-per-timestamp-output (integer)

Whilst a user is viewing/watching a replay, every _x_ number of seconds they are sent the current timestamp of the
replay in the chat so that they can start a replay from that exact timestamp later if they wish. This configuration
value determines how often (in seconds) the timestamp is sent to the user whilst viewing/watching a replay.

#### ignore-y-radius (boolean)

When starting a replay, the events to be replayed will be determined by the radius provided in the `/replay start`
command. If `ignore-y-radius` is `true`, then as long as the events are within the radius on the x-axis and z-axis, the
events will be counted as part of the replay. However, if `ignore-y-radius` is `false`, the events will also have to be
within the radius on the y-axis in order to be counted as part of the replay.

For example, let's take the following situation: a player runs the command `/replay start 3 10s` (where `3` is the
radius of the replay and `10s` means for events in the last 10 seconds) after placing a block 20 y-values above his
location 5 seconds ago.

- If `ignore-y-radius` is `false`, then the block placement event will not be included in the replay as the block was
  not placed within the required `3` block radius.
- If `ignore-y-radius` is `true`, then the block placement event will be included in the replay as the location of the
  block on the y-axis is irrelevant.

#### seconds-per-player-move-log (double)

Determines how often the current locations of all the players on the server are saved. Please note that there are some
optimisations built into the saving of player locations, such as the plugin won't continuously save a player's location
if they haven't moved since last movement log.

During a replay, you will see a player moving much more than just teleporting around every-so-often. This is because,
when starting a replay, the plugin runs player movement predictions with the movement events that meet the replay
criteria to try to predict the movements in-between two player movement events.

The recommended value for this setting is `1`, however this can be reduced slightly depending on the player count of the
server. It's best to experiment and find a value that works best for you, however a value of `1` will be adequate for
most use-cases.

#### seconds-per-player-inventory-log (double)

Determines how often the inventory contents (includes armour) and health of all the players on the server are saved.

The recommended value for this setting is `1`, however this can be reduced slightly depending on the player count of the
server. It's best to experiment and find a value that works best for you, however a value of `1` will be adequate for
most use-cases.

#### hours-until-logs-deleted (integer)

Determines how often all the event logs stored in the database are deleted. This setting is in hours and once an event
becomes more than _x_ hours old (where _x_ is `hours-until-logs-deleted`), the event will be permanently deleted from
the database.

This setting is especially useful for servers with a large player count as they may generate lots of events in a short
period of time. These servers may find it slightly more efficient and storage-conscious to regularly clear the logs
every few days - that's what this setting will do.

The default value for this setting is `72`, however you may wish to decrease this value depending on the player count of
the server. Furthermore, your requirements and preferences for how long ago you wish to be able to replay from should
also impact this decision. It is recommended to use the lowest value you can that still suits your use-case.

If you wish to stop the automatic deletion of event logs, change this setting to a negative value such as `-1`.

#### event-render-buffer (integer)

Determines the number of events that are held in an internal buffer during the rendering of a replay - this works much
like how watching a video online will download some video content ahead of your current point in preparation to render
it.

Deciding on a buffer size:

- If you are expecting to have many administrators viewing replays at the same time, then it is recommended either
  keeping the buffer setting at its default value or decreasing it.
- If you are expecting to have many events occurring at the same place in the replay at the same time, then a larger
  buffer may be ideal to ensure you always have events ready to render.
- If you use a buffer that is too small, you may experience momentary (~0.1 second) pauses whilst viewing a replay with
  many events, leading to a less smooth replay experience.
- If you use a buffer that is too large, you may experience increased memory usage by the plugin (more than necessary)
  as many events will be held in the internal buffer (in memory).

The default value for this setting is `100`, however you may wish to change this value depending on your use-case.
Regardless of the buffer size you choose, it is strongly recommended that you keep the setting between the values
of `50`
and `500`.

#### timestamp-converter-format-datetime (string)

Determines the required format of a date-time when parsing it using `/replay timestamp [datetime]`. It is only
recommended changing this value if you have knowledge of date-time parsing, furthermore, the default value for this
setting should be adequate for most use-cases.

Acceptable characters for a date-time format can be
found [here](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns). The format set
should be able to resolve to a date and a time - just a date is not enough.

It is worth noting that, due to the parsing of the command, spaces cannot be used in the format setting.

#### timestamp-converter-format-time (string)

Determines the required format of a time when parsing it using `/replay timestamp [time]`. It is only recommended
changing this value if you have knowledge of time parsing, furthermore, the default value for this setting should be
adequate for most use-cases.

Acceptable characters for a time format can be
found [here](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns). The format set
should be able to resolve to a time - the current date will be used for the resolved date.

It is worth noting that, due to the parsing of the command, spaces cannot be used in the format setting.

#### replay-permission (string)

This setting is the base permission to use the `/replay` command and all it's sub-commands; other than `/replay reload`
and `/replay clearlogs` who have their own permissions.

The default value for this setting is `replay.replay`, changing the value is up to preference or to make it work with
your current permission plugin.

#### replay-reload-permission (string)

This setting is the permission for the `/replay reload` command.

The default value for this setting is `replay.reload`, changing the value is up to preference or to make it work with
your current permission plugin.

#### replay-clearlogs-permission (string)

This setting is the permission for the `/replay clearlogs` command.

The default value for this setting is `replay.clearlogs`, changing the value is up to preference or to make it work with
your current permission plugin.

### Messages

- [plugin-prefix](#plugin-prefix)
- [no-permission](#no-permission)

#### plugin-prefix

Prepended to the start of every message that InstantReplay sends to a user (only when the `use-plugin-prefix` setting
is `true`). If the `use-plugin-prefix` setting is `false`, this value will be ignored.

#### no-permission

Message sent to a user who tries to execute a command belonging to InstantReplay when they don't have permission to do
so.

<br>

## Development

...

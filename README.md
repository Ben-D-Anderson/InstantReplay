# InstantReplay

InstantRelay is a Minecraft plugin for Spigot versions 1.8 to 1.17 (inclusive) which allows Minecraft server admins to
review events by re-watching them.

<a name="installation"></a>

## Installation

Download the [latest version](https://github.com/dmulloy2/ProtocolLib/releases/latest)
of [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) and move it into the `plugins` folder of your Minecraft
server.

Download the [latest version](https://github.com/Ben-D-Anderson/InstantReplay/releases/latest) of InstantReplay from
the [releases tab](https://github.com/Ben-D-Anderson/InstantReplay/releases) and move it into the `plugins` folder of
your Minecraft server.

Start your Minecraft server, and you should see a configuration file generated at `plugins/InstantReplay/config.yml`.

Edit the `mysql` section of the configuration file to contain the details of your MySQL database - this is required for
InstantReplay to function.

For Example:

```yaml
mysql:
  username: "admin"
  password: "adminpassword"
  host: "127.0.0.1"
  database: "instantreplay"
  port: 3306
```

Save the changes to the configuration file and restart the Minecraft server. The plugin should now be installed and
functional.

### Encountered a problem?

Open
a [support request](https://github.com/Ben-D-Anderson/InstantReplay/issues/new?assignees=Ben-D-Anderson&labels=support&template=support-request.md&title=)
under the [issues tab](https://github.com/Ben-D-Anderson/InstantReplay/issues) and I'll be happy to help. You should
receive a response within 24 hours of opening the support request.

<a name="usage"></a>

## Usage

### Permissions

Any player attempting to use the plugin should have the required permissions given by your permission manager of choice.
The permissions can be found in the configuration file under `settings` with the base permission
being `settings.replay-permission`.

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
  since [Unix Epoch](https://www.unixtimestamp.com/). See also `/replay timestamp`.

A speed argument may also be optionally passed to the command with the format `/replay start <radius> <time> [speed]`
where `[speed]` is an integer (whole number). The default speed is `1` (meaning 1x speed). For example, if `2` was
chosen as the speed then the replay run at 2x speed.

### Dynamically change replay speed

You can change the speed of a running (or paused) replay using the command `/replay speed <speed>` where `<speed>` is an
integer (whole number).

For example, if the replay started with a speed of `2` (2x speed) and you ran the command `/replay speed 1` then the
replay would run at 1x speed (half of the starting speed).

<a name="configuration"></a>

## Configuration

...

<a name="development"></a>

## Development

...
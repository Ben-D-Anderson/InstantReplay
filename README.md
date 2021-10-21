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

## Visual Demonstration

<br>

<div align="center">

_Coming Soon..._

</div>

<br>

## Example commands

Starting a replay with a 5 block radius, from 10 seconds ago:

```
/replay start 5 10s
```

Starting a replay with a 6 block radius, from 15 minutes ago, with a 3x speed:

```
/replay start 6 15m 3
```

Pausing a replay:

```
/replay pause
```

Resuming a replay:

```
/replay resume
```

Stopping a replay:

```
/replay stop
```

Getting the current timestamp:

```
/replay timestamp
```

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

6. Save the changes to the configuration file and restart the Minecraft server. The plugin should now be installed and
   functional.

<br>

## The Wiki

Please read [the wiki](https://github.com/Ben-D-Anderson/InstantReplay/wiki) for guides on usage, configuration and
development.
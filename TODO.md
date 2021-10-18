### A list of features/edits to add to InstantReplay in the future

- `/replay seek <time>` to seek to a certain timestamp or forward/backward by _x_ seconds in a replay.
- Joining other people's replay as a viewer (using a 4 digit hexadecimal code).
- Support using same database for multiple InstantReplay instances (saving an id for each server locally and storing all
  database entries with that id).
- Spectator mode whilst watching replays - not get stopped by ghost blocks
- Consider using Hibernate for database access
    - Consider java.sql.Timestamp for event times

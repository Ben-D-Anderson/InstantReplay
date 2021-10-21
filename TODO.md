### A list of features/edits to add to InstantReplay in the future

- `/replay seek <time>` to seek to a certain timestamp or forward/backward by _x_ seconds in a replay.
- Joining other people's replay as a viewer (using a 4 digit hexadecimal code).
- Support using same database for multiple InstantReplay instances (saving an id for each server locally and storing all
  database entries with that id).
- Spectator mode whilst watching replays - not get stopped by ghost blocks
- Consider using Hibernate for database access
  - Consider java.sql.Timestamp for event times
- Automatic database backups (toggle-able, disabled by default)
- Toggle different parts of the logging and replay (i.e. player movement, blocks, damage)
- Consider splitting location elements of tables in order to compare radius in query.
- Refactor command system to make each command a class
- Visualise item drops
- Add more replay events
- Implement datetime/time parsing directly into time parameter of `/replay start`
- Add warning when there are no events for a while, suggest seeking forward
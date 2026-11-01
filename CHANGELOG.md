### Changes

- Added hydrothermal vents to deep ocean ridges.
- Added more guano to caves.
- Added bats. (These are just vanilla bats, no special features right now)
- Improved the snow "catch up" system to allow all rendered chunks to decide whether to be snowed or not.
- Attempted to improve addon compat for the config text parser.
- Some fixes to how the day/night cycle interacts with vanilla systems (#3771):
  - Daylight sensors now should respond better away from the equator.
  - Mobs should now burn when it is actually daytime.
  - Mobs should now spawn when it is actually night (and the config option is enabled).
  - The global sky darkening from vanilla's global rain state is now disabled.
  - In general, rain/thunderstorms should also cause the expected behaviors (eg. prevent mobs from burning, allow more spawns) but in order to keep the changes less invasive it won't work EXACTLY like vanilla's system does.

### Fixes

- Made many internal fixes to entity AI.
- Made powderkeg explosions more closely follow vanilla systems.
- Fixed pluckable mobs not saving the last plucked tick.
- Fixed familiarity not decaying properly.
- Fixed glow squids not glowing.
- Fixed fluid transfer issue with mules.
- Fix possible crash with holding minecarts.
- Fix pests (rats) not playing sounds on the client.
- Fix fox breeding not working properly.
- Fix rabbit breeding not working properly.
- Fix some animals not playing ambient sounds.
- Removed unused config options.
- Fixed collapse-related configs not working.
- Removed several unneeded mixins.
- Fixed issue with field guide tables running off the page at certain row counts (#3766)
- Fixed rare worldgen issue with roses being placed outside loaded chunks.
- Fixed stick loot from double crops not working properly.
- Fixed bad lang key for raw alabaster barrel recipes.
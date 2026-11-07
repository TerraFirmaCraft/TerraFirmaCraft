### Changes

- Added Pond Turtles
- New models/animations/textures:
  - Jellyfish
  - Orca
  - Manatee
  - Milfoil
  - Banana Plants
- Wind particles die down at the middle of the night.
- Changed the look of stars in the night sky slightly. Constellations may be easier to identify now.
- Made some optimizations to oceans that should slightly improve worldgen speed.
- Increased depth requirement for Octopoteuthis spawns.

### Fixes

- Fixed TFC cancelling spawns in other dimensions than the overworld.
- Fixed the night sky being mirrored.
- Fix molds not showing solid/liquid tooltips.
- Plant behavior fixes:
  - Fix a number of plants interacting with fluids incorrectly.
  - Fix a number of plants spreading incorrectly or not at all.
  - Allow dead bushes to spawn on more places.
  - Fixed several bugs with banana plants.
- Worldgen fixes:
  - Fix fissures breaking through rivers.
  - Fix some internal calculation bugs that probably don't show up in game.
  - Fix cave spikes and calcites having wrong spawning logic. We should see them mor enow.
  - Fix an issue causing vines to spawn less.
  - Fix disc veins being cone-shaped.
- Fixed some issues with the Octopoteuthis causing lag.
- Fixed orca animations never playing.
- Fixed shore creatures missing from several biomes.
- Fixed cactus beds rendered inside adjacent blocks.

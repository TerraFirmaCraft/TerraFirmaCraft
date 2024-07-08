Another small update with some small features and fixes.

### Changes

- Added JEI recipe click areas to various TFC menus, which when clicked open the list of recipes in JEI for that device (#2730)
- Kelp in oceans generates less tall on average, and should reach the surface of the ocean less often

### Fixes

- Fixed Distant Horizons' distant world generation causing chunk corruption and crashes (#2661)
- Fixed food added by addons/packs decaying in creative tabs, and JEI views
- Fixed nutrition not working properly after death:
  - In peaceful mode, fast passive regeneration not applying
  - Rotten food never giving adverse effects when eaten
  - The "Full Nutrition" advancement not triggering
- Fixed two missing recipes in the Field Guide
- Fixed a typo in the "Leather" advancement


### Technical Changes

TFC's noise caves are now controllable via [Density Functions](https://minecraft.fandom.com/wiki/Density_function). The defaults are located [here](https://github.com/TerraFirmaCraft/TerraFirmaCraft/tree/1.20.x/src/generated/resources/data/tfc/worldgen/density_function), for anyone who wishes to delve into the realm of noise generation.


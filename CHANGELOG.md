This update is the first adding a mechanical-power system to TFC! This has been something we've wanted for quite some time, and has drawn inspiration from Better Than Wolves, TFC+, and others. Hope you enjoy hooking up that Quern to automation, finally!

### Added

- Added Water Wheels, Windmills, Axles, Gearboxes, Encased Axles, Clutches, for each TFC wood type.
- Querns can be connected to a rotating axle to automatically grind.
- Added a Crankshaft, which can be connected to a bellows to automatically woosh.
- Added Steel Pipes and a Steel Pump, capable of moving multiple source blocks of water, salt water, and spring water.
- Added the Trip Hammer, which can be used to automatically work some items on an Anvil.
- Added new pages to the Field Guide explaining how to use all the new mechanics.

### Changed

- Removed crafting recipe and advancements for the vanilla bucket
- Blue and red steel buckets can no longer move sources, and both can carry lava and water-like fluids.
- More ore vein tweaking, indicator rarity tweaking.
    - Surface veins are largely less common across the board; deeper underground veins were less touched.
    - Sulfur now exclusively spawns near lava.

### Fixes

- Fixed many inconsistencies between salt water and fresh water due to the new Forge Fluid API (#2542)
    - Fixed bubbles not appearing when you bobbed in salt wate
    - Fixed the "enter water" sound being played while moving between salt water and fresh water - this fixes fishes in rivers being annoyingly noisy.
    - Fixed being underwater in salt water having a different fog effect and no ambient sounds.
    - Fixed salt water pushing stronger than fresh water.
    - And others...
- Fixed many cases where sandwiches, soups, or salads were not stackable, despite being crafted in similar/identical situations
- Fix meal items in JEI showing a "Never Decay" tooltip, possibly leading to confusion (#2560).
- Fix compatibility issue with mods doing weird things during creative tab setup.
- Fix a conflict with Patchouli versions >81
- Fix some field guide typos.
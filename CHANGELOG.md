
### Changes

- When log piles are lit, they will take longer to burn up, so you have more time to cover the pile.
- When log piles are lit, they will take 1.5 seconds to light adjacent charcoal piles, rather than this happening instantly.
- Tweaked the particle effects of burning log piles to be more performant.
- Added a recipe for vanilla's 'loom' block that allows TFC wool yarn to be used
- Standardized plant movement modifiers to make them less punishing and more consistent. Small flowers no longer give any slowdown. The slow effect scales with the visual size of the plant, starting from 90% with short grass, up to 60% with large shrubs, ferns, and other dense undergrowth.
- Sheepskin can now be scraped the same way as soaked hide, on a log.
- Updated scraping recipes to allow specifying an 'extra drop' parameter.
- Slowed down the pace of creeping plant spread to be in line with other plants.
- Added TFC saplings to vanilla's sapling tag.
- Updated the Korean translation.
- Cut gems can now be ground into powder.
- Sluicing and panning will now give uncut gems.
- Added info for amethyst and opal to the field guide.

### Fixes
- Fix coral dying even when underwater (#2542)
- Fix horse armor requiring jute rather than jute fiber.
- Fix jam sandwiches not requiring jam.
- Fix the game crashing when summoning a chest boat with commands.
- Fixed cranberry bushes being totally broken (#2133)
- Fixed the texture of bells being missing.
- Fixed concurrent access of TFC's `DataManager`. This may allow the KubeJS multithreading option to work more predictably.
- Fixed some typos in the field guide.
- Fixed the recipes for palm mosaic stairs and slabs.
A few minor fixes, including a fix for the "the sun stopped moving" bug that had been hitting people sporadically.

### Fixes

- Fix `doDaylightCycle` being set to `false` in rare situations.
- Fix `/tfc count` command not functioning (#2623)
- Fix visual issues which could occur when items were heated beyond the "max visible temperature" (i.e. 1600°C ~ Brilliant White)

### Technical Changes

- Remove tag entries for iron + copper powder being tagged as `forge:dust/iron`, `forge:dust/copper` (#2620)
- Add `#minecraft:leaves`, `#minecraft:saplings` to the tag for harvestable by a sharp tool (i.e. scythe, knife)
- Fix addon-dev-only crash due to invalid config value read in `getDecayDateModifier()` (#2599)
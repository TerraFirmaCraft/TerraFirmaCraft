Another small update with some small features and fixes.

### Changes

- Added JEI recipe transfer handler functionality (the "+" button to autofill the recipe.) to various TFC menus. (#2735)
- Add two Rustic and Lattice Windmill Blades for alternative windmill styles (thanks alekiponi!)
- Update the shape of lake biomes, adding small mid-lake islands that can appear
- Climate and Biome generation tweaks:
  - Lakes cause a localized rainfall increase near them
  - Increase rainfall near-ocean bias, and adjust average rainfall slightly downwards to compensate. Inland areas are more dry, costal areas are more rainy
  - Add highly localized, potent, rainfall influence from nearby rivers (the splash image above).
  - Add a max rainfall that badlands biomes appear at
  - Add a min rainfall that low, freshwater-heavy biomes (lowlands, low canyons) appear at
- Removed the "place an encased axle on top of an axle" functionality, that was responsible for deleting the axle. This was from an older iteration of axle/encased axles/axle casings and did not make sense with the fact that an "Encased Axle" already contains an axle.
- Bread, Cooked meat are now heatable, and can burn (be lost) if heated too hot

### Fixes

- Fix rare climate chunk artifacts at the edge of region borders (typically in deep oceans)
- Fix pot recipes with item stack providers using input-based modifiers not working (affects addons/datapacks only)
- Fix stick duplication loop involving fruit tree leaves
- Fix duplicated recipes showing on the sewing table screen
- Fix familiarity appearing to decay on client until interacted with, in certain situations (#2736)

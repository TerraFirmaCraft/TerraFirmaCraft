### Changes

- Made blood lilies an indicator for kaolin clay
- Made kaolin clay grass appear as silt grass in Jade.
- Added a process of making parchment from animal hides.
- Added pumice groundcover rocks.
- Reworked fire spreading to prevent fire spreading through solid blocks.
- Reworked wind:
  - The wind direction in oceans is now largely predictable and based on the z coordinate
  - The wind in oceans switches directions at night.
  - Thunder levels are no longer a factor in how strong wind is. This should make storms a bit less severe.
- Allow pot recipes with no items (#2639)
- Re-added an API for restricting structure spawning based on climate. Use the `tfc:climate` structure placement to take advantage of this.
- Allow creeping plants to be placed on any solid surface (#2566)

### Fixes
- Fix the melt amount of wrought iron grills being wrong (#2642)
- Fix an issue that caused problems depending on TFC in a dev environment (#2638)
- Fix projected disc veins not being compatible with indicators (#2588)
- Fix being able to place items with 'V' in adventure or spectator mode
- Re-disable sodium/embeddium's mixins to biome colors (#2279)
- Fix reused ingredients in meals not combining nutrient values (#2637)
- Disabled vanilla bed recipe (#2647)
- Disabled vanilla lantern recipe
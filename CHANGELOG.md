This update brings tweaks to world generation, a reworked bloomery, some fixes and various other minor improvements. Enjoy!

### Changes

- The bloomery has had a significant rework with how it consumes fuel and ore (#2669)
  - The separate "fuel slots" and "ore slots" are now merged - the bloomery has capacity for **16 items** per layer, up to a maximum of 48
  - The output of the bloomery is now based on the mix of ore and charcoal. One bloom is produced for every **100 mB of iron, and 2x charcoal**.
  - The Field Guide has been updated with this new information
- World generation changes:
  - Shores found near oceans / beaches are now more varied, and can form rocky cliffs, small ledges, or gradual slopes
  - The "Inverted Badlands" biome is less common, primarily in high altitude areas, where it blended poorly with higher altitude biomes
  - Both types of "Badlands" biomes generate less sand, and more grass, in higher rainfall areas
  - Banana trees being are now much more common (#2652)
- Moss now will grow on blocks which are contacted on any side by water, not just directly above
- Added a slider in the world generation customization screen for "grass density", which affects all TFC placed grass features
- Added an `ru_ru` localization for the Field Guide (#2653)

### Fixes

- Fix sheet piles generating incorrect particles when tapped, broken, either in the wrong location or with the wrong metal
- Fix single-count sheet piles dropping as an item when broken in creative mode
- Fix empty molds not stacking in certain situations when they should (#2651)
- Fix ingot piles making unknown texture particles when broken
- Fix ingot pile extract returning an item in creative mode
- Fix the "stack food" keybind voiding inputs if you hovered over the output slot of the inventory crafting menu
- Fix issues with interacting with lamps, either filling, draining, or lighting. They should behave much more predictably now (#2649)
- Fix spectating non-players breaking the experience bar rendering
- Fix a typo in an advancement description
- Fix being able to shift-click items into a pot while boiling (#2654)
- Fix scythe not working in creative mode (#2666)
- Fix client-side particles behaving in non-random ways when certain situations were present (#2665)
- Fix river height not being included in structure lookahead - this should prevent structures (i.e. villages) from generating "floating" over rivers
- Remove (broken) Antique Atlas support (#2664). For more information, please read the [associated commit message](https://github.com/TerraFirmaCraft/TerraFirmaCraft/commit/3d8e09e9f5bc028989ffce485abed12e4af896d3)
- Fix a slab duplication exploit involving the chisel
- Fix the highlight boxes for i.e. chisel placement, sluice highlights, not drawing when viewed from certain angles.

### For Pack Makers

This update changes the format of a bloomery recipe in one important but required way. The `catalyst` field of a [Bloomery Recipe](https://terrafirmacraft.github.io/Documentation/1.20.x/data/recipes/#bloomery) has changed from an [Ingredient](https://terrafirmacraft.github.io/Documentation/1.20.x/data/ingredients/) to an [ItemStackIngredient](https://terrafirmacraft.github.io/Documentation/1.20.x/data/common-types/#item-stack-ingredients), in tandem with the reworked behavior described above.

Old:

```json
  "catalyst": {
    "item": "minecraft:charcoal"
  },
```

New:

```json
  "catalyst": {
    "ingredient": {
      "item": "minecraft:charcoal"
    },
    "count": 2
  },
```
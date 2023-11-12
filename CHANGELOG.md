### Changes

- Add Bell Peppers. These are a pickable crop, meaning they can be harvested before being fully grown, without destroying the crop
- Add "Mossy Loose Rock"s as seperate blocks, rather than model variants. They spawn in >250mm rainfal areas.
- Tweaks to ore vein generation balance. This is still a work in progress, and feedback is welcome. Broadly speaking, copper should be more common, iron, gold should be less common.
- Update various translations from their most recent updates in 1.18

### Fixes

- Fix wooden bucket item not showing the fluid color correctly (#2541)
- Fix Barley grain having no saturation
- Fix missing line in the Climate screen
- Fix "Average Temp: null" in the Climate screen
- Fix placement issues with barrels, barrel racks
- Fix breaking a barrel in a rack not dropping the barrel
- Fix a preservation exploit
- Fix various fluid interaction specifics (technical explanation: we are in the process of migrating from the `#minecraft:water` tag towards fluid API, if it can be done without compromising functionality)
- Fix dead torches emitting lit particles (#2550)
- Fix firestarters deleting blocks that aren't flammable when trying to place fire (#2549)
- Fix TFC death messages not being localized correctly (#2546)
- Fix rain particles falling through thatch and leaves.
- Fix simple pot recipes linking fluid inputs as outputs.
- Fix boulders spawning on top of water
- Fix torch items being extinguished by air (#2554)
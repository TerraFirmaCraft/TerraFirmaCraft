A small update, with some fixes and updated localization.

### Changes

- Updated localization for Russian, Polish

### Fixes

- Fix temperature interpolation being reversed on a chunk basis, causing the temperature in chunks to be subtly different across chunk borders (< 0.1°C)
- Fix undo recipes for cobble and mossy cobble slabs and walls (#2726)
- Fix a duplication exploit involving the Corpse mod (#2676)
- Fix knapping recipes using an ingredient, with a knapping type requiring >1 item, to display correctly in JEI (#2725)
- Fix some pot recipes from addons/datapacks not displaying properly in JEI (#2712)
- Fix some confusing wording in the Bloomery section of the Field Guide


### Technical Fixes

- `ClientSelfTestEvent` now fires regardless of if `-ea` is present
- Pot recipes that use `ItemStackProvider`s must now obey one of two conditions:
  - All item stack providers must not depend on the input
  - There must be an equal number of inputs and providers, and all providers must be identical. This must occur because there is no internal matching from ingredients -> providers, and so any input slot may be matched up to any provider. Output of the recipe is undefined behavior if this rule is not followed.
If you are new to TFC, welcome! We are excited to have you. This build contains a month's worth of fixes, some of which were submitted by hard-working community members, and includes some cool new features to play with. There's also a pile of technical changes suited to addon and packmakers as well as new things for technical players.

### Changes
- Added windmill blade variants.
- Allow creating mud from clicking dirt with a bucket of water.
- Revamped the Cooking Pot GUI.
- Added a 5 second delay between adding ingredients to the pot that match a recipe and the pot boiling. This should make it easier to make soups with 5 ingredients.
- Allow using modded pipes on the cooking pot. (#2682)
- Added baked potatoes. Nerfed raw potatoes.
- In 'shore' type biomes, the wind system now uses the ocean's predictable 'trade wind' functionality, rather than using the random land wind system.
- Changed the bloomery to require both charcoal and ore to be present to begin accepting items. This fixes an issue where mining the bloom block would sometimes immediately cause the bloom items to be sucked into the bloomery. However, this change does not prevent you from putting incorrect or inefficient ratios of items into the bloomery! We are seeking feedback on this change, please visit us in Discord if you have questions.
- Made the familiarity decay limit, the value at which familiarity stops decaying daily, configurable. This means that setting this value to zero makes familiarity never decay.
- Added a jade tooltip asking players to flatten poured glass blocks.
- Dead crops can now be replaced by grass blocks that spread in warm weather. Enjoy weeds in your garden!
- Allow picking plants up with shears.
- Fresh seaweed now can be composted, and also is now considered a vegetable.
- Allow composting the inedible seaweed block.
- Tall wild crops can now be buried by snow in the winter.
- Reset the height of short grass when it is buried by snow in the winter. This should provide some more seasonal variation in areas that the player is in often.
- Allow the quern to interact with hoppers.
- Rabbits are now fully-fledged livestock, and can be bred! Enjoy cuniculture!
- Added fox meat.
- Spawn eating particles when feeding an animal.
- Sticks removed from wattle blocks are now returned to the player rather than dropped on the ground.
- Changed sluice loot to not always drop pumice for certain rocks that contain pumice, allowing you to still obtain loose rocks some of the time.
- The soda ash recipe now requires a higher temperature than before, making it possible to cook kelp into dried kelp in a forge without it immediately turning to ash. (#2696)

### Technical Changes
- Made it easier for addons to make things work with barrel racks.
- Added a interface for barrel recipes to expose them to addons more readily.
- Made pot recipes work with Item Stack Providers.
- Adjusted stack size of redstone to be 64 to match other 'powders'.
- Exposed the max support range value to addons.
- Added a small API for blocks that have special behavior when buried by snow (like fallen leaves turning into humus)

### Fixes
- Allow taking ash out of pots and grilles without removing the pot. (#2683)
- Fix some sounds including anvil hit sounds being stereo instead of mono.
- Fix some plant blocks being pushed by pistons rather than being destroyed.
- Fix an issue where adding water to the pot would not cause the pot to update whether or not it was ready to boil or not.
- Fix querns being able to operate without a handstone after the handstone was broken by an axle. (#2675)
- Fix some typos in the book.
- Fix some mismatches in our loading screen textures.
- Fix icicles causing a lack of friction on the block above (i.e. standing on vanilla ice blocks under a slab) (#2693)
- Fixed (again) squids spawning inside walls and immediately suffocating. (#2580)
- Fixed some plant blocks and items having incorrect colors (#2432, #2424)
- Fix grinding colored items (such as plants) in a quern not releasing colored particles (#2605)
- Fix bad shading on the jar shelf model (#2585)
- Fix the 'ghost block renderer' being broken for some models. (#2552)
- Fix rabbits not being able to jump.
- Fix high carbon black steel having a missing texture when placed on the ground.
- Fix the 'show hoe overlays only when shifting' config not working (#2702)
- Fix two block tall dead crops dropping seeds from the top block, causing some unintended behavior and possible exploits. (#2670)
- Fix the new cobblestone decoration un-crafting recipes being unavailable for slabs and walls.

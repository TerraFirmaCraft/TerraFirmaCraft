#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # =========
    # ITEM TAGS
    # =========

    # Forge Tags

    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.item_tag('forge:double_sheets/any_bronze', *['#forge:double_sheets/%sbronze' % b for b in ('bismuth_', 'black_', '')])
    rm.item_tag('forge:rods/wooden', '#tfc:twigs')
    rm.item_tag('forge:gems/diamond', 'tfc:gem/diamond')
    rm.item_tag('forge:gems/lapis', 'tfc:gem/lapis_lazuli')
    rm.item_tag('forge:gems/emerald', 'tfc:gem/emerald')
    rm.item_tag('forge:string', 'tfc:wool_yarn')
    rm.item_tag('forge:shears', '#tfc:shears')
    rm.item_tag('forge:dusts', '#tfc:powders')

    # Minecraft Tags

    rm.item_tag('minecraft:coals', 'tfc:ore/bituminous_coal', 'tfc:ore/lignite')
    rm.item_tag('minecraft:stone_pressure_plates', 'minecraft:stone_pressure_plate', 'minecraft:polished_blackstone_pressure_plate')
    rm.item_tag('minecraft:fishes', 'tfc:food/cod', 'tfc:food/cooked_cod', 'tfc:food/salmon', 'tfc:food/cooked_salmon', 'tfc:food/tropical_fish', 'tfc:food/cooked_tropical_fish', 'tfc:food/bluegill', 'tfc:food/cooked_bluegill', 'tfc:food/shellfish', 'tfc:food/cooked_shellfish')
    rm.item_tag('minecraft:arrows', 'tfc:glow_arrow')
    rm.item_tag('minecraft:piglin_loved', 'tfc:metal/ingot/gold')
    rm.item_tag('minecraft:pickaxes', '#tfc:pickaxes')
    rm.item_tag('minecraft:axes', '#tfc:axes')
    rm.item_tag('minecraft:shovels', '#tfc:shovels')
    rm.item_tag('minecraft:hoes', '#tfc:hoes')
    rm.item_tag('minecraft:swords', '#tfc:swords')
    rm.item_tag('minecraft:tools', '#tfc:hammers', '#tfc:javelins', '#tfc:knives', '#tfc:chisels', '#tfc:maces', '#tfc:saws', '#tfc:propicks', '#tfc:scythes')
    rm.item_tag('minecraft:trim_materials', '#tfc:trim_materials', replace=True)

    # TFC Tags: Devices

    rm.item_tag('firepit_sticks', '#forge:rods/wooden')
    rm.item_tag('firepit_kindling', 'tfc:straw', 'minecraft:paper', '#tfc:books', 'tfc:groundcover/pinecone', '#tfc:fallen_leaves')
    rm.item_tag('pit_kiln_straw', 'tfc:straw')
    rm.item_tag('handstone', 'tfc:handstone')
    rm.item_tag('forge_fuel', '#minecraft:coals')
    rm.item_tag('firepit_fuel', '#minecraft:logs', 'tfc:peat', 'tfc:peat_grass', 'tfc:stick_bundle', 'minecraft:paper', '#tfc:books', 'tfc:groundcover/pinecone', '#tfc:fallen_leaves', 'tfc:groundcover/driftwood')
    rm.item_tag('blast_furnace_fuel', 'minecraft:charcoal')
    rm.item_tag('log_pile_logs', 'tfc:stick_bundle', '#minecraft:logs')
    rm.item_tag('pit_kiln_logs', 'tfc:stick_bundle', '#minecraft:logs')
    rm.item_tag('firepit_logs', '#minecraft:logs')
    rm.item_tag('waxes_scraping_surface', 'tfc:glue', 'minecraft:honeycomb')
    rm.item_tag('scrapable', *['tfc:%s_%s_hide' % (size, hide) for size in ('small', 'medium', 'large') for hide in ('soaked', 'sheepskin')], 'tfc:unrefined_paper')
    rm.item_tag('glassworking_tools', 'tfc:paddle', 'tfc:jacks', 'tfc:gem_saw')
    rm.item_tag('usable_on_tool_rack', 'tfc:firestarter', 'minecraft:bow', 'minecraft:crossbow', 'minecraft:flint_and_steel', 'minecraft:spyglass', 'minecraft:brush', 'tfc:spindle', '#tfc:all_blowpipes', '#tfc:glassworking_tools', 'tfc:bone_needle', 'tfc:sandpaper')
    rm.item_tag('usable_in_powder_keg', 'minecraft:gunpowder')
    rm.item_tag('usable_in_bookshelf', '#tfc:books')
    rm.item_tag('compost_greens_low', '#tfc:plants')
    rm.item_tag('compost_greens', '#tfc:foods/grains')
    rm.item_tag('compost_greens_high', '#tfc:foods/vegetables', '#tfc:foods/fruits', 'tfc:groundcover/seaweed')
    rm.item_tag('compost_browns_low', *['tfc:plant/%s' % p for p in BROWN_COMPOST_PLANTS], '#tfc:fallen_leaves', 'minecraft:hanging_roots')
    rm.item_tag('compost_browns', 'tfc:powder/wood_ash', 'tfc:jute')
    rm.item_tag('compost_browns_high', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass', 'tfc:groundcover/driftwood', 'tfc:groundcover/pinecone', 'minecraft:paper', 'tfc:melon', 'tfc:pumpkin', 'tfc:jute_fiber')
    rm.item_tag('compost_poisons', '#tfc:foods/meats', 'minecraft:bone')
    rm.item_tag('scribing_ink', 'minecraft:black_dye')
    rm.item_tag('powders', 'minecraft:gunpowder', 'minecraft:redstone', 'minecraft:glowstone_dust', 'minecraft:blaze_powder', 'minecraft:sugar')
    rm.item_tag('sewing_light_cloth', 'tfc:wool_cloth', 'tfc:silk_cloth')
    rm.item_tag('sewing_dark_cloth', 'tfc:burlap_cloth')
    rm.item_tag('sewing_needles', 'tfc:bone_needle')
    rm.item_tag('foods', 'minecraft:egg')
    rm.item_tag('empty_jar_with_lid', 'tfc:empty_jar_with_lid')

    # TFC Tags: Functionality

    rm.item_tag('daub', 'tfc:daub')
    rm.item_tag('wattle_sticks', 'tfc:stick_bunch')
    rm.item_tag('starts_fires_with_durability', 'minecraft:flint_and_steel')
    rm.item_tag('starts_fires_with_items', 'minecraft:fire_charge')
    rm.item_tag('can_be_lit_on_torch', '#forge:rods/wooden')
    rm.item_tag('axes_that_log', '#tfc:axes')
    rm.item_tag('extinguisher', '#minecraft:shovels')
    rm.item_tag('inefficient_logging_axes', *['tfc:stone/axe/%s' % cat for cat in ROCK_CATEGORIES])
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide')
    rm.item_tag('small_fishing_bait', 'tfc:food/shellfish', '#tfc:seeds')
    rm.item_tag('large_fishing_bait', 'tfc:food/cod', 'tfc:food/salmon', 'tfc:food/tropical_fish', 'tfc:food/bluegill')
    rm.item_tag('holds_small_fishing_bait', *['tfc:metal/fishing_rod/%s' % metal for metal, data in METALS.items() if 'tool' in data.types])
    rm.item_tag('holds_large_fishing_bait', *['tfc:metal/fishing_rod/%s' % metal for metal in ('wrought_iron', 'red_steel', 'blue_steel', 'black_steel', 'steel')])
    rm.item_tag('placed_item_whitelist')
    rm.item_tag('placed_item_blacklist')

    # TFC Tags: Crafting

    rm.item_tag('high_quality_cloth', 'tfc:silk_cloth', 'tfc:wool_cloth')
    rm.item_tag('books', 'minecraft:book', 'minecraft:writable_book', 'minecraft:written_book', 'minecraft:enchanted_book')
    rm.item_tag('mortar', 'tfc:mortar')
    rm.item_tag('flux', 'tfc:powder/flux')
    rm.item_tag('fluxstone', 'tfc:food/shellfish', 'tfc:groundcover/mollusk', 'tfc:groundcover/clam', 'minecraft:scute', 'tfc:groundcover/mussel', 'tfc:groundcover/sea_urchin')
    rm.item_tag('magnetic_rocks', *['tfc:ore/%s_magnetite' % grade for grade in ('small', 'normal', 'poor', 'rich')])
    rm.item_tag('clay_knapping', 'minecraft:clay_ball')
    rm.item_tag('fire_clay_knapping', 'tfc:fire_clay')
    rm.item_tag('leather_knapping', '#forge:leather')
    rm.item_tag('goat_horn_knapping', 'tfc:goat_horn')
    rm.item_tag('any_knapping', '#tfc:clay_knapping', '#tfc:fire_clay_knapping', '#tfc:leather_knapping', '#tfc:rock_knapping', '#tfc:goat_horn_knapping')
    rm.item_tag('buckets', 'tfc:wooden_bucket', 'tfc:metal/bucket/red_steel', 'tfc:metal/bucket/blue_steel', 'minecraft:bucket')
    rm.item_tag('bronze_anvils', *['tfc:metal/anvil/%sbronze' % b for b in ('bismuth_', 'black_', '')])
    rm.block_and_item_tag('tfc:anvils', *['tfc:metal/anvil/%s' % metal for metal, data in METALS.items() if 'utility' in data.types])
    rm.item_tag('fluid_item_ingredient_empty_containers', 'minecraft:bucket', 'tfc:wooden_bucket', 'tfc:ceramic/jug', 'tfc:metal/bucket/red_steel', 'tfc:metal/bucket/blue_steel', '#tfc:glass_bottles')
    rm.item_tag('unfired_vessels', 'tfc:ceramic/unfired_vessel', *['tfc:ceramic/%s_unfired_vessel' % c for c in COLORS])
    rm.item_tag('unfired_large_vessels', 'tfc:ceramic/unfired_large_vessel', *['tfc:ceramic/unfired_large_vessel/%s' % c for c in COLORS])
    rm.item_tag('fired_vessels', 'tfc:ceramic/vessel', *['tfc:ceramic/%s_glazed_vessel' % c for c in COLORS])
    rm.block_and_item_tag('fired_large_vessels', 'tfc:ceramic/large_vessel', *['tfc:ceramic/large_vessel/%s' % c for c in COLORS])
    rm.item_tag('unfired_molds', *['tfc:ceramic/unfired_%s_mold' % i for i, d in METAL_ITEMS.items() if d.mold], 'tfc:ceramic/unfired_bell_mold', 'tfc:ceramic/unfired_fire_ingot_mold')
    rm.item_tag('fired_molds', *['tfc:ceramic/%s_mold' % i for i, d in METAL_ITEMS.items() if d.mold], 'tfc:ceramic/bell_mold', 'tfc:ceramic/fire_ingot_mold')
    rm.item_tag('vessels', '#tfc:unfired_vessels', '#tfc:fired_vessels')
    rm.item_tag('large_vessels', '#tfc:unfired_large_vessels', '#tfc:fired_large_vessels')
    rm.item_tag('molds', '#tfc:unfired_molds', '#tfc:fired_molds')
    rm.item_tag('unfired_pottery', '#tfc:unfired_vessels', '#tfc:unfired_large_vessels', '#tfc:unfired_molds', *['tfc:ceramic/unfired_%s' % p for p in SIMPLE_POTTERY + SIMPLE_UNFIRED_POTTERY])
    rm.item_tag('silica_sand', 'tfc:sand/white')
    rm.item_tag('hematitic_sand', 'tfc:sand/yellow', 'tfc:sand/red', 'tfc:sand/pink')
    rm.item_tag('olivine_sand', 'tfc:sand/green', 'tfc:sand/brown')
    rm.item_tag('volcanic_sand', 'tfc:sand/black')
    rm.item_tag('glass_batches', 'tfc:silica_glass_batch', 'tfc:hematitic_glass_batch', 'tfc:olivine_glass_batch', 'tfc:volcanic_glass_batch')
    rm.item_tag('glass_batches_tier_2', 'tfc:silica_glass_batch', 'tfc:hematitic_glass_batch')
    rm.item_tag('glass_batches_tier_3', 'tfc:silica_glass_batch', 'tfc:hematitic_glass_batch', 'tfc:olivine_glass_batch')
    rm.item_tag('glass_batches_not_tier_1', 'tfc:hematitic_glass_batch', 'tfc:olivine_glass_batch', 'tfc:volcanic_glass_batch')
    rm.item_tag('glassworking_powders', *['tfc:powder/%s' % p for p in GLASSWORKING_POWDERS])
    rm.item_tag('glassworking_potash', 'tfc:powder/soda_ash', 'tfc:powder/saltpeter')
    rm.item_tag('blowpipes', 'tfc:blowpipe', 'tfc:ceramic_blowpipe')
    rm.item_tag('glass_blowpipes', 'tfc:blowpipe_with_glass', 'tfc:ceramic_blowpipe_with_glass')
    rm.item_tag('all_blowpipes', '#tfc:blowpipes', '#tfc:glass_blowpipes')
    rm.item_tag('sweetener', 'minecraft:sugar')
    rm.item_tag('jars', 'tfc:empty_jar', 'tfc:empty_jar_with_lid')
    rm.item_tag('sealed_jars', '#tfc:foods/sealed_preserves')
    rm.item_tag('unsealed_jars', '#tfc:foods/preserves', 'tfc:empty_jar', 'tfc:empty_jar_with_lid')
    rm.item_tag('axles', *['tfc:wood/axle/%s' % w for w in WOODS], *['tfc:wood/encased_axle/%s' % w for w in WOODS])
    rm.item_tag('gear_boxes', *['tfc:wood/gear_box/%s' % w for w in WOODS])
    rm.item_tag('clutches', *['tfc:wood/clutch/%s' % w for w in WOODS])
    rm.item_tag('water_wheels', *['tfc:wood/water_wheel/%s' % w for w in WOODS])
    rm.item_tag('default_windmill_blades', 'tfc:windmill_blade', '#tfc:colored_windmill_blades')
    rm.item_tag('colored_windmill_blades', *['tfc:%s_windmill_blade' % color for color in COLORS if color != 'white'])
    rm.item_tag('decorated_windmill_blades', 'tfc:rustic_windmill_blade', 'tfc:lattice_windmill_blade')
    rm.item_tag('all_windmill_blades', '#tfc:default_windmill_blades', '#tfc:decorated_windmill_blades')

    # TFC Tags: Entities

    rm.item_tag('fox_spawns_with', 'minecraft:rabbit_foot', 'minecraft:feather', 'minecraft:bone', 'tfc:food/salmon', 'tfc:food/bluegill', 'minecraft:egg', 'tfc:small_raw_hide', 'tfc:food/cloudberry', 'tfc:food/strawberry', 'tfc:food/gooseberry', 'tfc:food/rabbit', 'minecraft:flint')
    rm.item_tag('piglin_bartering_ingots', 'tfc:metal/ingot/gold')
    rm.item_tag('carried_by_horse', '#forge:chests/wooden', '#tfc:barrels')

    rm.item_tag('pig_food', '#tfc:foods')
    rm.item_tag('cow_food', '#tfc:foods/grains')
    rm.item_tag('sheep_food', '#tfc:foods/grains')
    rm.item_tag('yak_food', '#tfc:foods/grains')
    rm.item_tag('goat_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables')
    rm.item_tag('chicken_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds', '#tfc:foods/breads')
    rm.item_tag('duck_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds', '#tfc:foods/breads')
    rm.item_tag('quail_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds', '#tfc:foods/breads')
    rm.item_tag('alpaca_food', '#tfc:foods/grains', '#tfc:foods/fruits')
    rm.item_tag('mule_food', '#tfc:horse_food')
    rm.item_tag('donkey_food', '#tfc:horse_food')
    rm.item_tag('horse_food', '#tfc:foods/grains', '#tfc:foods/fruits')
    rm.item_tag('musk_ox_food', '#tfc:foods/grains')
    rm.item_tag('cat_food', '#tfc:foods/grains', '#tfc:foods/cooked_meats', '#tfc:foods/dairy', '#minecraft:fishes')
    rm.item_tag('dog_food', '#tfc:foods/grains', '#tfc:foods/meats', 'minecraft:rotten_flesh', '#tfc:foods/vegetables')
    rm.item_tag('rabbit_food', '#tfc:foods/grains', '#tfc:foods/vegetables')
    rm.item_tag('penguin_food', 'tfc:food/dried_kelp', 'tfc:food/dried_seaweed', '#minecraft:fishes')
    rm.item_tag('turtle_food', 'tfc:food/dried_kelp', 'tfc:food/dried_seaweed')
    rm.item_tag('frog_food', 'tfc:food/dried_kelp', 'tfc:food/dried_seaweed', '#tfc:foods/grains')

    rm.item_tag('mob_feet_armor', *['tfc:metal/boots/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_leg_armor', *['tfc:metal/greaves/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_chest_armor', *['tfc:metal/chestplate/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_head_armor', *['tfc:metal/helmet/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_offhand_weapons', *['tfc:metal/shield/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_mainhand_weapons', *['tfc:metal/%s/%s' % (tool, metal) for metal in MOB_ARMOR_METALS for tool in MOB_TOOLS], *['tfc:stone/%s/%s' % (tool, stone) for stone in ROCK_CATEGORIES for tool in STONE_MOB_TOOLS], 'tfc:large_raw_hide', 'tfc:medium_raw_hide', 'tfc:small_raw_hide', 'tfc:handstone')
    rm.item_tag('skeleton_weapons', *['tfc:metal/javelin/%s' % metal for metal in MOB_ARMOR_METALS], *['tfc:stone/%s/%s' % (tool, stone) for stone in ROCK_CATEGORIES for tool in STONE_MOB_TOOLS], 'minecraft:bow')
    rm.item_tag('disabled_monster_held_items', 'minecraft:iron_sword', 'minecraft:iron_shovel', 'minecraft:fishing_rod', 'minecraft:nautilus_shell')
    rm.item_tag('deals_piercing_damage', '#tfc:javelins', '#tfc:knives')
    rm.item_tag('deals_slashing_damage', '#minecraft:swords', '#minecraft:axes', '#tfc:scythes')
    rm.item_tag('deals_crushing_damage', '#tfc:hammers', '#tfc:maces')

    # TFC Tags: Foods

    rm.item_tag('foods/dough', *['tfc:food/%s_dough' % g for g in GRAINS])
    rm.item_tag('foods/flour', *['tfc:food/%s_flour' % g for g in GRAINS])
    rm.item_tag('foods/can_be_salted', '#tfc:foods/raw_meats')
    rm.item_tag('foods/grains', *['tfc:food/%s_grain' % grain for grain in GRAINS])
    rm.item_tag('foods/apples', 'tfc:food/green_apple', 'tfc:food/red_apple')
    rm.item_tag('foods/usable_in_soup', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:foods/meats', '#tfc:foods/cooked_meats', 'tfc:food/cooked_rice')
    rm.item_tag('foods/usable_in_salad', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:foods/cooked_meats')
    rm.item_tag('foods/usable_in_sandwich', '#tfc:foods/vegetables', '#tfc:foods/cooked_meats', '#tfc:foods/dairy')
    rm.item_tag('foods/usable_in_jam_sandwich', '#tfc:foods/dairy', '#tfc:foods/cooked_meats', '#tfc:foods/preserves')
    rm.item_tag('foods/breads', *['tfc:food/%s_bread' % grain for grain in GRAINS])
    rm.item_tag('sandwich_bread', '#tfc:foods/breads')
    rm.item_tag('bowls', 'tfc:ceramic/bowl', 'minecraft:bowl')
    rm.item_tag('soup_bowls', '#tfc:bowls')
    rm.item_tag('salad_bowls', '#tfc:bowls')
    rm.item_tag('dynamic_bowl_items', '#tfc:soups', '#tfc:salads')

    # TFC Tags: Types

    for color in COLORS:
        rm.item_tag('dyes', 'minecraft:%s_dye' % color)
        if color != 'white':
            for variant in VANILLA_DYED_ITEMS:
                rm.item_tag('colored_%s' % variant, 'minecraft:%s_%s' % (color, variant))
        for variant in ('raw', 'bricks', 'polished'):
            rm.item_tag('colored_%s_alabaster' % variant, 'tfc:alabaster/%s/%s' % (variant, color))
        rm.item_tag('colored_shulker_boxes', 'minecraft:%s_shulker_box' % color)
        rm.item_tag('colored_concrete_powder', 'minecraft:%s_concrete_powder' % color)
    for gem in GEMS:
        rm.item_tag('forge:gems', 'tfc:gem/' + gem)
        rm.item_tag('gem_powders', 'tfc:powder/%s' % gem)

    for crop in CROPS:
        rm.block_and_item_tag('tfc:wild_crops', 'tfc:wild_crop/%s' % crop)
        rm.block_tag('crops', 'tfc:crop/%s' % crop)
    for fruit in FRUITS:
        rm.block_and_item_tag('tfc:wild_fruits', 'tfc:plant/%s_sapling' % fruit)
    for fruit in BERRIES:
        rm.block_and_item_tag('tfc:wild_fruits', 'tfc:plant/%s_bush' % fruit)
    for glass in GLASS_TYPES:
        rm.item_tag('%s_items' % glass, 'tfc:%s_glass_batch' % glass, 'tfc:%s_glass_bottle' % glass)

    # TFC Tags: Stairs, Slabs, Walls Tag
    for variant in CUTTABLE_ROCKS:
        for rock in ROCKS.keys():
            rm.block_and_item_tag('minecraft:stairs', 'tfc:rock/%s/%s_stairs' % (variant, rock))
            rm.block_and_item_tag('minecraft:slabs', 'tfc:rock/%s/%s_slab' % (variant, rock))
            rm.block_and_item_tag('minecraft:walls', 'tfc:rock/%s/%s_wall' % (variant, rock))
    for variant in SANDSTONE_BLOCK_TYPES:
        for color in SAND_BLOCK_TYPES:
            rm.block_and_item_tag('minecraft:stairs', 'tfc:%s_sandstone/%s_stairs' % (variant, color))
            rm.block_and_item_tag('minecraft:slabs', 'tfc:%s_sandstone/%s_slab' % (variant, color))
            rm.block_and_item_tag('minecraft:walls', 'tfc:%s_sandstone/%s_wall' % (variant, color))
    for variant in ('bricks', 'polished'):
        for color in COLORS:
            rm.block_and_item_tag('minecraft:stairs', 'tfc:alabaster/%s/%s_stairs' % (variant, color))
            rm.block_and_item_tag('minecraft:slabs', 'tfc:alabaster/%s/%s_slab' % (variant, color))
            rm.block_and_item_tag('minecraft:walls', 'tfc:alabaster/%s/%s_wall' % (variant, color))
    for wood in WOODS.keys():
        rm.block_and_item_tag('minecraft:wooden_stairs', 'tfc:wood/planks/%s_stairs' % wood)
        rm.block_and_item_tag('minecraft:wooden_slabs', 'tfc:wood/planks/%s_slab' % wood)
    for soil in SOIL_BLOCK_VARIANTS:
        rm.block_and_item_tag('minecraft:stairs', 'tfc:mud_bricks/%s_stairs' % soil)
        rm.block_and_item_tag('minecraft:slabs', 'tfc:mud_bricks/%s_slab' % soil)
        rm.block_and_item_tag('minecraft:walls', 'tfc:mud_bricks/%s_wall' % soil)
    rm.block_and_item_tag('minecraft:wooden_slabs', 'tfc:wood/planks/palm_mosaic_slab')
    rm.block_and_item_tag('minecraft:wooden_stairs', 'tfc:wood/planks/palm_mosaic_stairs')
    rm.block_and_item_tag('minecraft:stairs', '#minecraft:wooden_stairs')

    for wood in WOODS.keys():
        def item(_variant: str) -> str:
            return 'tfc:wood/%s/%s' % (_variant, wood)

        def plank(_variant: str) -> str:
            return 'tfc:wood/planks/%s_%s' % (wood, _variant)

        rm.item_tag('minecarts', item('chest_minecart'))
        rm.item_tag('lumber', item('lumber'))
        rm.item_tag('support_beams', item('support'))

        rm.block_and_item_tag('twigs', item('twig'))
        rm.block_and_item_tag('looms', plank('loom'))
        rm.block_and_item_tag('sluices', item('sluice'))
        rm.block_and_item_tag('workbenches', plank('workbench'))
        rm.block_and_item_tag('bookshelves', plank('bookshelf'))
        rm.block_and_item_tag('lecterns', item('lectern'))
        rm.block_and_item_tag('barrels', item('barrel'))
        rm.block_and_item_tag('fallen_leaves', item('fallen_leaves'))
        rm.block_and_item_tag('tool_racks', plank('tool_rack'))
        rm.block_and_item_tag('scribing_tables', item('scribing_table'))
        rm.block_and_item_tag('sewing_tables', item('sewing_table'))
        rm.block_and_item_tag('jar_shelves', item('jar_shelf'))
        rm.block_and_item_tag('water_wheels', item('water_wheel'))
        rm.block_and_item_tag('%s_logs' % wood, item('log'), item('wood'), item('stripped_log'), item('stripped_wood'))
        rm.block_tag('support_beams', item('vertical_support'), item('horizontal_support'))

        rm.block_and_item_tag('minecraft:saplings', item('sapling'))
        rm.block_and_item_tag('minecraft:wooden_buttons', plank('button'))
        rm.block_and_item_tag('minecraft:wooden_fences', plank('fence'), plank('log_fence'))
        rm.block_and_item_tag('minecraft:wooden_doors', plank('door'))
        rm.block_and_item_tag('minecraft:wooden_trapdoors', plank('trapdoor'))
        rm.block_and_item_tag('minecraft:wooden_pressure_plates', plank('pressure_plate'))
        rm.block_and_item_tag('minecraft:logs', '#tfc:%s_logs' % wood)
        rm.block_and_item_tag('minecraft:leaves', item('leaves'))
        rm.block_and_item_tag('minecraft:planks', item('planks'))
        rm.block_tag('minecraft:standing_signs', plank('sign'))
        rm.block_tag('minecraft:wall_signs', plank('wall_sign'))
        rm.item_tag('minecraft:signs', item('sign'))
        rm.item_tag('minecraft:boats', item('boat'))
        for metal, metal_data in METALS.items():
            if 'utility' in metal_data.types:
                rm.block_tag('minecraft:ceiling_hanging_signs', 'tfc:wood/planks/hanging_sign/%s/%s' % (metal, wood))
                rm.block_tag('minecraft:wall_hanging_signs', 'tfc:wood/planks/wall_hanging_sign/%s/%s' % (metal, wood))
                rm.item_tag('minecraft:hanging_signs', 'tfc:wood/hanging_sign/%s/%s' % (metal, wood))

        rm.block_and_item_tag('forge:chests/wooden', item('chest'), item('trapped_chest'))
        rm.block_and_item_tag('forge:fence_gates/wooden', plank('fence_gate'))
        rm.block_and_item_tag('forge:stripped_logs', item('stripped_log'), item('stripped_wood'))

        if wood not in ('pine', 'sequoia', 'spruce', 'white_cedar', 'douglas_fir'):
            rm.block_tag('seasonal_leaves', item('leaves'))

        if wood in TANNIN_WOOD_TYPES:
            rm.item_tag('makes_tannin', item('log'), item('wood'))
    for fruit in FRUITS.keys():
        rm.block_and_item_tag('minecraft:saplings', 'tfc:plant/%s_sapling' % fruit)
        if fruit != 'banana':
            rm.block_and_item_tag('minecraft:leaves', 'tfc:plant/%s_leaves' % fruit)
            rm.block_and_item_tag('fruit_tree_leaves', 'tfc:plant/%s_leaves' % fruit)
            rm.block_tag('fruit_tree_branch', 'tfc:plant/%s_branch' % fruit, 'tfc:plant/%s_growing_branch' % fruit)

    for category in ROCK_CATEGORIES:  # Rock (Category) Tools
        for tool in ROCK_CATEGORY_ITEMS:
            rm.item_tag(TOOL_TAGS[tool], 'tfc:stone/%s/%s' % (tool, category))
            rm.item_tag('usable_on_tool_rack', 'tfc:stone/%s/%s' % (tool, category))
            rm.item_tag('%s_items' % category, 'tfc:stone/%s/%s' % (tool, category))
            rm.item_tag('%s_items' % category, 'tfc:stone/%s_head/%s' % (tool, category))

    for metal, metal_data in METALS.items():
        # Metal Ingots / Sheets, for Ingot/Sheet Piles
        rm.item_tag('forge:ingots/%s' % metal)
        rm.item_tag('tfc:pileable_ingots', '#forge:ingots/%s' % metal)
        if len(metal_data.types) > 0:
            rm.item_tag('forge:sheets/%s' % metal)
            rm.item_tag('tfc:pileable_double_ingots', '#forge:double_ingots/%s' % metal)
            rm.item_tag('tfc:pileable_sheets', '#forge:sheets/%s' % metal)

        # Metal Tools
        if 'tool' in metal_data.types:
            rm.item_tag('metal_item/%s_tools' % metal, *['tfc:metal/%s/%s' % (item, metal) for item in METAL_TOOL_HEADS])

        # Metal Tool Tags
        if 'tool' in metal_data.types:
            for tool_type, tool_tag in TOOL_TAGS.items():
                rm.item_tag(tool_tag, 'tfc:metal/%s/%s' % (tool_type, metal))
                rm.item_tag('usable_on_tool_rack', 'tfc:metal/%s/%s' % (tool_type, metal))
            rm.item_tag('usable_on_tool_rack', 'tfc:metal/fishing_rod/%s' % metal, 'tfc:metal/tuyere/%s' % metal)

        # Metal Blocks + Items
        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                item_name = 'tfc:metal/block/%s_%s' % (metal, item.replace('block_', '')) if 'block_' in item else 'tfc:metal/%s/%s' % (item, metal)
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag, '#%s/%s' % (item_data.tag, metal))
                    rm.item_tag(item_data.tag + '/' + metal, item_name)

                rm.item_tag('metal_item/%s' % metal, item_name)

        if 'utility' in metal_data.types:
            rm.block_and_item_tag('trapdoors', 'tfc:metal/trapdoor/%s' % metal)
            rm.block_and_item_tag('lamps', 'tfc:metal/lamp/%s' % metal)
        if 'part' in metal_data.types:
            rm.block_tag('minecraft:stairs', 'tfc:metal/block/%s_stairs' % metal)
            rm.block_tag('minecraft:slabs', 'tfc:metal/block/%s_slab' % metal)
            rm.block_and_item_tag('metal_plated_blocks', 'tfc:metal/block/%s' % metal)

        if 'armor' in metal_data.types:
            rm.item_tag('minecraft:trimmable_armor', *['tfc:metal/%s/%s' % (section, metal) for section in TFC_ARMOR_SECTIONS])

    for plant in UNIQUE_PLANTS:
        rm.block_tag('plants', 'tfc:plant/%s' % plant)
        if 'plant' not in plant:
            rm.item_tag('plants', 'tfc:plant/%s' % plant)

    # ==========
    # BLOCK TAGS
    # ==========

    # Minecraft/Forge Tags

    rm.block_and_item_tag('forge:sandstone', *['tfc:%s_sandstone/%s' % (variant, c) for c in SAND_BLOCK_TYPES for variant in SANDSTONE_BLOCK_TYPES])
    rm.block_and_item_tag('forge:sand', '#minecraft:sand')  # Forge doesn't reference the vanilla tag for some reason
    rm.block_tag('forge:concrete', *['minecraft:%s_concrete' % c for c in COLORS])

    rm.block_tag('minecraft:valid_spawn', '#tfc:grass', '#minecraft:sand', *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])  # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:geode_invalid_blocks', 'tfc:sea_ice', 'tfc:fluid/salt_water', 'tfc:fluid/river_water', 'tfc:fluid/spring_water')
    rm.block_tag('minecraft:bamboo_plantable_on', '#tfc:grass')
    rm.block_tag('minecraft:climbable', 'tfc:plant/hanging_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/liana', 'tfc:plant/liana_plant', 'tfc:plant/jungle_vines')
    rm.block_tag('minecraft:infiniburn_overworld', 'tfc:pit_kiln')
    rm.block_tag('minecraft:prevent_mob_spawning_inside', 'tfc:thatch', '#minecraft:leaves')
    rm.block_tag('minecraft:wall_post_override', 'tfc:torch', 'tfc:dead_torch')
    rm.block_tag('minecraft:fall_damage_resetting', 'tfc:thatch', '#tfc:berry_bushes')
    rm.block_tag('minecraft:replaceable_by_trees', '#tfc:single_block_replaceable', 'tfc:fluid/salt_water', 'tfc:fluid/spring_water')
    rm.block_tag('minecraft:sword_efficient', '#tfc:plants')
    rm.block_tag('minecraft:maintains_farmland', '#tfc:crops')
    rm.block_tag('minecraft:sniffer_diggable_block', '#tfc:grass', '#tfc:dirt', '#tfc:mud')
    rm.block_tag('minecraft:features_cannot_replace', '#forge:chests/wooden')
    rm.block_tag('minecraft:lava_pool_stone_cannot_replace', '#forge:chests/wooden')
    rm.block_tag('minecraft:frogs_spawnable_on', '#tfc:mud', '#tfc:grass')
    rm.block_and_item_tag('minecraft:dirt', '#tfc:dirt')
    rm.block_and_item_tag('minecraft:sand', *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES])
    rm.block_and_item_tag('minecraft:small_flowers', *['tfc:plant/%s' % plant for plant in SMALL_FLOWERS])
    rm.block_and_item_tag('minecraft:tall_flowers', *['tfc:plant/%s' % plant for plant in TALL_FLOWERS])
    rm.block_and_item_tag('minecraft:candles', '#tfc:candles')
    rm.block_tag('minecraft:candle_cakes', '#tfc:candle_cakes')

    # TFC Tags: Earth

    for v in SOIL_BLOCK_VARIANTS:
        for block_type, tags in SOIL_BLOCK_TAGS.items():
            for tag in tags:
                rm.block_and_item_tag(tag, 'tfc:%s/%s' % (block_type, v))

    rm.block_tag('snow_layer_survives_on', '#tfc:mud')
    rm.block_tag('converts_to_humus', '#tfc:fallen_leaves')
    rm.block_tag('tree_grows_on', '#minecraft:dirt', '#tfc:grass', '#tfc:mud')
    rm.block_tag('spreading_fruit_grows_on', '#tfc:bush_plantable_on', '#tfc:mud', '#forge:gravel')
    rm.block_tag('bush_plantable_on', '#minecraft:dirt', '#tfc:grass', '#tfc:farmland')
    rm.block_tag('kaolin_clay', *['tfc:%s_kaolin_clay' % color for color in KAOLIN_CLAY_TYPES], 'tfc:kaolin_clay_grass')
    rm.block_tag('grass_plantable_on', '#tfc:bush_plantable_on', 'tfc:peat', '#tfc:mud', '#tfc:kaolin_clay')
    rm.block_tag('sea_bush_plantable_on', '#minecraft:dirt', '#minecraft:sand', '#forge:gravel', '#tfc:mud')
    rm.block_tag('halophyte_plantable_on', '#minecraft:dirt', '#tfc:mud')
    rm.block_tag('creeping_plantable_on', '#tfc:bush_plantable_on', '#minecraft:base_stone_overworld', '#minecraft:logs')
    rm.block_tag('creeping_stone_plantable_on', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone', '#minecraft:base_stone_overworld', '#forge:concrete')
    rm.block_tag('wild_crop_grows_on', '#tfc:bush_plantable_on')
    rm.block_tag('kaolin_clay_replaceable', '#tfc:bush_plantable_on', '#forge:stone', '#forge:gravel', '#tfc:rock/raw')

    rm.block_tag('kelp_tree', 'tfc:plant/giant_kelp_flower', 'tfc:plant/giant_kelp_plant')
    rm.block_tag('kelp_flower', 'tfc:plant/giant_kelp_flower')
    rm.block_tag('kelp_branch', 'tfc:plant/giant_kelp_plant')
    rm.block_tag('halophyte', 'tfc:plant/sea_lavender', 'tfc:plant/cordgrass')
    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')
    rm.block_tag('can_be_snow_piled', '#tfc:twigs', '#tfc:fallen_leaves', '#tfc:loose_rocks', '#tfc:wild_crops')
    rm.block_tag('plants', *['tfc:wild_crop/%s' % crop for crop in CROPS.keys()])
    rm.block_and_item_tag('clay_indicators', *['tfc:plant/%s' % plant for plant in ('athyrium_fern', 'canna', 'goldenrod', 'pampas_grass', 'perovskia', 'water_canna')])
    rm.block_tag('tide_pool_blocks', *['tfc:groundcover/%s' % g for g in ('clam', 'mollusk', 'mussel', 'sea_urchin')])

    # TFC Tags: Functionality
    rm.block_tag('supports_landslide', 'minecraft:dirt_path', '#tfc:paths', '#tfc:farmland')
    rm.block_tag('lit_by_dropped_torch', '#tfc:fallen_leaves', 'tfc:log_pile', 'tfc:thatch', 'tfc:pit_kiln')
    rm.block_tag('charcoal_cover_whitelist', 'tfc:log_pile', 'tfc:charcoal_pile', 'tfc:burning_log_pile')
    rm.block_tag('forge_invisible_whitelist', 'tfc:crucible')
    rm.block_tag('any_spreading_bush', '#tfc:spreading_bush')
    rm.block_tag('thorny_bushes', 'tfc:plant/blackberry_bush', 'tfc:plant/raspberry_bush')
    rm.block_tag('logs_that_log', '#minecraft:logs')
    rm.block_tag('scraping_surface', '#minecraft:logs')
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')
    rm.block_tag('forge_insulation', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone')
    rm.block_tag('bloomery_insulation', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone', 'minecraft:bricks', 'tfc:fire_bricks', '#forge:concrete')
    rm.block_tag('blast_furnace_insulation', 'tfc:fire_bricks')
    rm.block_tag('minecart_holdable', 'tfc:crucible', '#tfc:barrels', '#tfc:anvils', 'tfc:powderkeg', '#tfc:fired_large_vessels')
    rm.block_tag('rabbit_raidable', 'tfc:crop/carrot', 'tfc:crop/cabbage', 'minecraft:carrots')
    rm.block_tag('single_block_replaceable', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass', '#tfc:twigs', '#tfc:fallen_leaves')
    rm.block_tag('powder_snow_replaceable', '#minecraft:dirt', '#forge:gravel', '#tfc:grass', 'minecraft:snow')
    rm.block_tag('pet_sits_on', 'tfc:quern', '#forge:chests/wooden', '#minecraft:wool_carpets', '#tfc:fired_large_vessels', '#minecraft:wool')
    rm.block_tag('monster_spawns_on', '#minecraft:dirt', '#forge:gravel', '#tfc:grass', '#forge:stone', '#forge:ores', 'minecraft:obsidian')
    rm.block_tag('bottom_support_accepted', 'minecraft:hopper')
    rm.block_tag('glass_pouring_table', 'tfc:metal/block/brass')
    rm.block_tag('glass_basin_blocks', 'tfc:metal/block/brass')
    rm.block_tag('explosion_proof', 'minecraft:barrier', 'minecraft:light', 'minecraft:bedrock', 'minecraft:command_block', 'minecraft:chain_command_block', 'minecraft:repeating_command_block', 'minecraft:end_gateway', 'minecraft:end_portal', 'minecraft:end_portal_frame', 'minecraft:jigsaw', 'minecraft:structure_block')
    rm.block_tag('powderkeg_breaking_blocks', '#minecraft:dirt', '#forge:gravel', '#tfc:grass', '#forge:stone', '#forge:ores')

    # TFC Tags: Types

    for ore, ore_data in ORES.items():
        for rock in ROCKS.keys():
            if ore_data.graded:
                for grade in ORE_GRADES.keys():
                    rm.block_tag('prospectable', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
                    rm.block('tfc:ore/%s_%s/%s/prospected' % (grade, ore, rock)).with_lang(lang(ore))
            else:
                rm.block_tag('prospectable', 'tfc:ore/%s/%s' % (ore, rock))
                name = lang(ore)
                if ore == 'diamond':
                    name = lang('kimberlite')
                if ore == 'pyrite':
                    name = lang('native gold?')
                rm.block('tfc:ore/%s/%s/prospected' % (ore, rock)).with_lang(name)

    for plant, data in PLANTS.items():  # Plants
        rm.block_and_item_tag('plants', 'tfc:plant/%s' % plant)
        if data.type in ('standard', 'short_grass', 'dry', 'grass_water', 'water', 'beach_grass'):
            rm.block_tag('single_block_replaceable', 'tfc:plant/%s' % plant)
        if data.type in ('standard', 'tall_plant', 'short_grass', 'tall_grass', 'creeping', 'beach_grass'):
            rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)
        if data.type in ('emergent', 'emergent_fresh', 'floating', 'floating_fresh', 'creeping'):
            rm.block_tag('can_be_ice_piled', 'tfc:plant/%s' % plant)
        rm.block_and_item_tag('plants', 'tfc:plant/%s' % plant)
        if data.type != 'cactus':
            rm.block_tag('replaceable_plants', 'tfc:plant/%s' % plant)
    rm.block_tag('replaceable_plants', 'tfc:plant/ivy', 'tfc:plant/jungle_vines')

    # Rocks
    for rock, rock_data in ROCKS.items():
        def block(block_type: str):
            return 'tfc:rock/%s/%s' % (block_type, rock)

        # Type-Based Block Tags
        rm.block_and_item_tag('rock/raw', block('raw'))
        rm.block_and_item_tag('rock/hardened', block('hardened'))
        rm.block_and_item_tag('rock/gravel', block('gravel'))
        rm.block_and_item_tag('rock/smooth', block('smooth'))
        rm.block_and_item_tag('rock/bricks', block('bricks'), block('mossy_bricks'), block('cracked_bricks'), block('chiseled'))
        rm.block_and_item_tag('rock/aqueducts', block('aqueduct'))
        rm.block_and_item_tag('rock/mossy_bricks', block('mossy_bricks'))
        rm.block_and_item_tag('rock/cracked_bricks', block('cracked_bricks'))
        rm.block_and_item_tag('rock/chiseled_bricks', block('chiseled'))

        rm.block_tag('breaks_when_isolated', block('raw'))
        rm.item_tag('rock_knapping', block('loose'), block('mossy_loose'))
        rm.item_tag('%s_rock' % rock_data.category, block('loose'), block('mossy_loose'))

        rm.block_and_item_tag('forge:stone_bricks', '#tfc:rock/bricks')
        rm.block_and_item_tag('forge:gravel', '#tfc:rock/gravel')
        rm.block_and_item_tag('forge:stone', block('raw'), block('hardened'))
        rm.block_and_item_tag('forge:cobblestone/normal', block('cobble'), block('mossy_cobble'))
        rm.block_and_item_tag('forge:smooth_stone', block('smooth'))
        rm.block_and_item_tag('forge:smooth_stone_slab', 'tfc:rock/smooth/%s_slab' % rock)

        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        rm.block_tag('minecraft:stone_buttons', block('button'))
        rm.block_and_item_tag('minecraft:stone_pressure_plates', block('pressure_plate'))

        for ore, ore_data in ORES.items():
            if ore_data.graded:
                for grade in ORE_GRADES.keys():
                    rm.block_tag('rock/ores', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
            else:
                rm.block_tag('rock/ores', 'tfc:ore/%s/%s' % (ore, rock))

        if rock_data.category == 'igneous_extrusive' or rock_data.category == 'igneous_intrusive':
            rm.block_and_item_tag('rock_anvils', 'tfc:rock/anvil/%s' % rock)

        if rock in ['chalk', 'dolomite', 'limestone', 'marble']:
            rm.item_tag('tfc:fluxstone', block('loose'), block('mossy_loose'))

        for ore in ORE_DEPOSITS:
            rm.block_and_item_tag('forge:gravel', 'tfc:deposit/%s/%s' % (ore, rock))
            rm.block_and_item_tag('ore_deposits', 'tfc:deposit/%s/%s' % (ore, rock))

        for block_type in ('raw', 'hardened', 'smooth', 'cobble', 'bricks', 'gravel', 'spike', 'cracked_bricks', 'mossy_bricks', 'mossy_cobble', 'chiseled', 'loose', 'mossy_loose', 'pressure_plate', 'button', 'aqueduct'):
            rm.item_tag('%s_items' % rock_data.category, 'tfc:rock/%s/%s' % (block_type, rock))
            if block_type in CUTTABLE_ROCKS:
                rm.item_tag('%s_items' % rock_data.category, 'tfc:rock/%s/%s_stairs' % (block_type, rock))
                rm.item_tag('%s_items' % rock_data.category, 'tfc:rock/%s/%s_wall' % (block_type, rock))
                rm.item_tag('%s_items' % rock_data.category, 'tfc:rock/%s/%s_slab' % (block_type, rock))

    # Ore tags
    for ore, data in ORES.items():
        if data.tag not in DEFAULT_FORGE_ORE_TAGS:
            rm.block_and_item_tag('forge:ores', '#forge:ores/%s' % data.tag)
        if data.graded:  # graded ores -> each grade is declared as a TFC tag, then added to the forge tag
            rm.block_and_item_tag('forge:ores/%s' % data.tag, '#tfc:ores/%s/poor' % data.tag, '#tfc:ores/%s/normal' % data.tag, '#tfc:ores/%s/rich' % data.tag)
            rm.item_tag('ore_pieces', 'tfc:ore/poor_%s' % ore, 'tfc:ore/normal_%s' % ore, 'tfc:ore/rich_%s' % ore)
            rm.item_tag('small_ore_pieces', 'tfc:ore/small_%s' % ore)
        else:
            rm.item_tag('ore_pieces', 'tfc:ore/%s' % ore)
        for rock in ROCKS.keys():
            if data.graded:
                rm.block_and_item_tag('ores/%s/poor' % data.tag, 'tfc:ore/poor_%s/%s' % (ore, rock))
                rm.block_and_item_tag('ores/%s/normal' % data.tag, 'tfc:ore/normal_%s/%s' % (ore, rock))
                rm.block_and_item_tag('ores/%s/rich' % data.tag, 'tfc:ore/rich_%s/%s' % (ore, rock))
            else:
                rm.block_and_item_tag('forge:ores/%s' % data.tag, 'tfc:ore/%s/%s' % (ore, rock))

    # can_carve Tag
    for rock in ROCKS.keys():
        for variant in ('raw', 'hardened', 'gravel', 'cobble'):
            rm.block_tag('can_carve', 'tfc:rock/%s/%s' % (variant, rock))
    for sand in SAND_BLOCK_TYPES:
        rm.block_tag('can_carve', 'tfc:raw_sandstone/%s' % sand)
    rm.block_tag('can_carve', 'minecraft:powder_snow', '#tfc:dirt', '#tfc:grass', '#minecraft:sand', '#tfc:mud')

    # Soil / Standard blocks are toughness 0 - dirt destroys charcoal
    rm.block_tag('toughness_1', 'tfc:charcoal_pile', 'tfc:charcoal_forge')  # Charcoal is toughness 1 - resistant against destruction from soil
    rm.block_tag('toughness_2', *[
        'tfc:rock/%s/%s' % (variant, rock) for variant in ('raw', 'cobble', 'mossy_cobble') for rock in ROCKS.keys()
    ])  # Stone type blocks are toughness 2
    rm.block_tag('toughness_3', 'minecraft:bedrock')  # Used as a top level 'everything goes'

    # Harvest Tool + Level Tags
    # Note: since we sort our tools *above* the vanilla equivalents (since there's no way we can make them 'exactly equal' because Forge's tool BS doesn't support that ENTIRELY REASONABLE feature), our tools need to effectively define empty tags for blocks that are exclusive to that tool only.
    # In other words, our tools are strictly better than vanilla tools, so our blocks need to not require them

    rm.block_tag('needs_stone_tool')
    rm.block_tag('needs_copper_tool')
    rm.block_tag('needs_wrought_iron_tool')
    rm.block_tag('needs_steel_tool')
    rm.block_tag('needs_colored_steel_tool')

    def needs_tool(_tool: str) -> str:
        return {
            'wood': 'forge:needs_wood_tool', 'stone': 'forge:needs_wood_tool',
            'copper': 'minecraft:needs_stone_tool',
            'bronze': 'minecraft:needs_iron_tool',
            'iron': 'minecraft:needs_iron_tool', 'wrought_iron': 'minecraft:needs_iron_tool',
            'diamond': 'minecraft:needs_diamond_tool', 'steel': 'minecraft:needs_diamond_tool',
            'netherite': 'forge:needs_netherite_tool', 'black_steel': 'tfc:needs_black_steel_tool',
            'colored_steel': 'tfc:needs_colored_steel_tool'
        }[_tool]

    rm.block_tag('minecraft:mineable/hoe', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_knife', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_scythe', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_hammer', '#tfc:mineable_with_blunt_tool')
    rm.block_tag('tfc:mineable_with_glass_saw', *[
        '#forge:glass',
        '#forge:glass_panes',
        'tfc:poured_glass',
        *['tfc:%s_poured_glass' % color for color in COLORS],
        'minecraft:tinted_glass'
    ])
    rm.item_tag('tfc:sharp_tools', '#minecraft:hoes', '#tfc:knives', '#tfc:scythes')

    rm.block_tag('forge:needs_wood_tool')
    rm.block_tag('forge:needs_netherite_tool')

    for ore, data in ORES.items():
        for rock in ROCKS.keys():
            if data.graded:
                rm.block_tag(needs_tool(data.required_tool), 'tfc:ore/poor_%s/%s' % (ore, rock), 'tfc:ore/normal_%s/%s' % (ore, rock), 'tfc:ore/rich_%s/%s' % (ore, rock))
            else:
                rm.block_tag(needs_tool(data.required_tool), 'tfc:ore/%s/%s' % (ore, rock))

    rm.block_tag('minecraft:mineable/shovel', *[
        *['tfc:%s/%s' % (soil, variant) for soil in SOIL_BLOCK_TYPES for variant in SOIL_BLOCK_VARIANTS],
        'tfc:peat',
        'tfc:peat_grass',
        *['tfc:%s_kaolin_clay' % c for c in KAOLIN_CLAY_TYPES],
        'tfc:kaolin_clay_grass',
        *['tfc:sand/%s' % sand for sand in SAND_BLOCK_TYPES],
        'tfc:snow_pile',
        *['tfc:rock/gravel/%s' % rock for rock in ROCKS.keys()],
        *['tfc:deposit/%s/%s' % (ore, rock) for ore in ORE_DEPOSITS for rock in ROCKS.keys()],
        'tfc:aggregate',
        'tfc:fire_clay_block',
        'tfc:charcoal_pile',
        'tfc:charcoal_forge',
        'tfc:smooth_mud_bricks'
    ])
    rm.block_tag('minecraft:mineable/pickaxe', *[
        *['tfc:%s_sandstone/%s' % (variant, sand) for variant in SANDSTONE_BLOCK_TYPES for sand in SAND_BLOCK_TYPES],
        *['tfc:%s_sandstone/%s_%s' % (variant, sand, suffix) for variant in SANDSTONE_BLOCK_TYPES for sand in SAND_BLOCK_TYPES for suffix in ('slab', 'stairs', 'wall')],
        'tfc:icicle',
        'tfc:sea_ice',
        'tfc:ice_pile',
        'tfc:calcite',
        *['tfc:ore/%s/%s' % (ore, rock) for ore, ore_data in ORES.items() for rock in ROCKS.keys() if not ore_data.graded],
        *['tfc:ore/%s_%s/%s' % (grade, ore, rock) for ore, ore_data in ORES.items() for rock in ROCKS.keys() for grade in ORE_GRADES.keys() if ore_data.graded],
        *['tfc:ore/small_%s' % ore for ore, ore_data in ORES.items() if ore_data.graded],
        *['tfc:rock/%s/%s' % (variant, rock) for variant in ('raw', 'hardened', 'smooth', 'cobble', 'bricks', 'spike', 'cracked_bricks', 'mossy_bricks', 'mossy_cobble', 'chiseled', 'loose', 'mossy_loose', 'pressure_plate', 'button', 'aqueduct') for rock in ROCKS.keys()],
        *['tfc:rock/%s/%s_%s' % (variant, rock, suffix) for variant in ('raw', 'smooth', 'cobble', 'bricks', 'cracked_bricks', 'mossy_bricks', 'mossy_cobble') for rock in ROCKS.keys() for suffix in ('slab', 'stairs', 'wall')],
        *['tfc:rock/anvil/%s' % rock for rock, rock_data in ROCKS.items() if rock_data.category == 'igneous_intrusive' or rock_data.category == 'igneous_extrusive'],
        *['tfc:rock/magma/%s' % rock for rock, rock_data in ROCKS.items() if rock_data.category == 'igneous_intrusive' or rock_data.category == 'igneous_extrusive'],
        *['tfc:metal/%s/%s' % (variant, metal) for variant, variant_data in METAL_BLOCKS.items() for metal, metal_data in METALS.items() if variant_data.type in metal_data.types and 'block_' not in variant],
        *['tfc:metal/block/%s_slab' % metal for metal, metal_data in METALS.items() if 'utility' in metal_data.types],
        *['tfc:metal/block/%s_stairs' % metal for metal, metal_data in METALS.items() if 'utility' in metal_data.types],
        *['tfc:coral/%s_%s' % (color, variant) for color in CORALS for variant in CORAL_BLOCKS],
        'tfc:alabaster/raw',
        'tfc:alabaster/bricks',
        'tfc:alabaster/polished',
        *['tfc:alabaster/%s/%s' % (variant, color) for color in COLORS for variant in ('raw', 'bricks', 'polished')],
        *['tfc:alabaster/%s/%s_%s' % (variant, color, suffix) for color in COLORS for variant in ('bricks', 'polished') for suffix in ('wall', 'stairs', 'slab')],
        *['tfc:groundcover/%s' % gc for gc in MISC_GROUNDCOVER],
        'tfc:fire_bricks',
        'tfc:quern',
        'tfc:crucible',
        'tfc:bloomery',
        'tfc:bloom',
        'tfc:pot',
        'tfc:grill',
        'tfc:firepit',
        'tfc:ingot_pile',
        'tfc:double_ingot_pile',
        'tfc:sheet_pile',
        'tfc:blast_furnace',
        'tfc:ceramic/bowl',
        'tfc:crankshaft',
        'tfc:steel_pipe',
        'tfc:steel_pump',
        'tfc:trip_hammer'
    ])
    rm.block_tag('minecraft:mineable/axe', *[
        *[
            'tfc:wood/%s/%s' % (variant, wood)
            for variant in ('log', 'stripped_log', 'wood', 'stripped_wood', 'planks', 'twig', 'vertical_support', 'horizontal_support', 'sluice', 'chest', 'trapped_chest', 'barrel', 'lectern', 'scribing_table', 'sewing_table', 'jar_shelf', 'axle', 'encased_axle', 'bladed_axle', 'clutch', 'gear_box', 'windmill', 'water_wheel')
            for wood in WOODS.keys()
        ],
        *[
            'tfc:wood/planks/%s_%s' % (wood, variant)
            for variant in ('bookshelf', 'door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs', 'tool_rack', 'workbench', 'sign')
            for wood in WOODS.keys()
        ],
        *['tfc:wood/planks/palm_mosaic%s' % variant for variant in ('', '_slab', '_stairs')],
        *['tfc:plant/%s_branch' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:plant/%s_growing_branch' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:wattle/%s' % color for color in COLORS],
        'tfc:wattle',
        'tfc:wattle/unstained',
        'tfc:plant/banana_plant',
        'tfc:plant/dead_banana_plant',
        'tfc:log_pile',
        'tfc:burning_log_pile',
        'tfc:composter',
        'tfc:nest_box',
        'tfc:powderkeg',
        'tfc:wooden_bowl'
    ])
    rm.block_tag('tfc:mineable_with_sharp_tool', *[
        *['tfc:plant/%s' % plant for plant in PLANTS.keys()],
        *['tfc:plant/%s' % plant for plant in UNIQUE_PLANTS],
        *['tfc:wild_crop/%s' % plant for plant in CROPS.keys()],
        *['tfc:dead_crop/%s' % plant for plant in CROPS.keys()],
        *['tfc:crop/%s' % plant for plant in CROPS.keys()],
        'tfc:sea_pickle',
        *['tfc:plant/%s_bush' % bush for bush in ('snowberry', 'bunchberry', 'gooseberry', 'cloudberry', 'strawberry', 'wintergreen_berry')],
        *['tfc:plant/%s_bush%s' % (bush, suffix) for bush in ('blackberry', 'raspberry', 'blueberry', 'elderberry') for suffix in ('', '_cane')],
        'tfc:plant/cranberry_bush',
        'tfc:plant/dead_berry_bush',
        'tfc:plant/dead_cane',
        'tfc:thatch',
        'tfc:thatch_bed',
        '#minecraft:leaves',
        '#tfc:fallen_leaves',
        '#minecraft:saplings',
        'tfc:tree_roots',
    ])
    rm.block_tag('tfc:mineable_with_blunt_tool', *[
        'tfc:wood/%s/%s' % (variant, wood)
        for variant in ('log', 'stripped_log', 'wood', 'stripped_wood')
        for wood in WOODS.keys()
    ])

    rm.block_tag('minecraft:replaceable', *[
        'tfc:freshwater_bubble_column',
        'tfc:saltwater_bubble_column',
        'tfc:light',
        'tfc:snow_pile',
        '#tfc:all_fluids',
        '#tfc:replaceable_plants'
    ])

    # ==========
    # FLUID TAGS
    # ==========

    # Water
    # Any = Includes block waters like flowing, river water. These should never be present in fluid tanks
    # Fresh = only fresh
    # Infinite = infinite waters (fresh + salt)
    # Any = all waters (fresh, salt, spring)

    rm.fluid_tag('any_fresh_water', 'minecraft:water', 'minecraft:flowing_water', 'tfc:river_water')
    rm.fluid_tag('any_infinite_water', '#tfc:any_fresh_water', 'tfc:salt_water', 'tfc:flowing_salt_water')
    rm.fluid_tag('any_water', '#tfc:any_infinite_water', 'tfc:spring_water', 'tfc:flowing_spring_water')

    rm.fluid_tag('fresh_water', 'minecraft:water')
    rm.fluid_tag('infinite_water', '#tfc:fresh_water', 'tfc:salt_water')
    rm.fluid_tag('water', '#tfc:infinite_water', 'tfc:spring_water')

    # Categories
    # None of these use the word 'Any' and such should not include flowing fluids
    rm.fluid_tag('milks', 'minecraft:milk')
    rm.fluid_tag('alcohols', *ALCOHOLS)
    rm.fluid_tag('dyes', *['tfc:%s_dye' % dye for dye in COLORS])
    rm.fluid_tag('ingredients', *SIMPLE_FLUIDS, '#tfc:drinkables', '#tfc:dyes')
    rm.fluid_tag('scribing_ink', 'tfc:black_dye')

    rm.fluid_tag('drinkables', '#tfc:infinite_water', '#tfc:alcohols', '#tfc:milks', 'tfc:vinegar')
    rm.fluid_tag('any_drinkables', '#tfc:drinkables', '#tfc:any_infinite_water')

    rm.fluid_tag('molten_metals', *['tfc:metal/%s' % metal for metal in METALS.keys()])

    # Applications
    rm.fluid_tag('hydrating', '#tfc:any_fresh_water')
    rm.fluid_tag('mixable', '#minecraft:water')

    rm.fluid_tag('usable_in_pot', '#tfc:ingredients')
    rm.fluid_tag('usable_in_jug', '#tfc:drinkables')
    rm.fluid_tag('usable_in_wooden_bucket', '#tfc:ingredients')
    rm.fluid_tag('usable_in_red_steel_bucket', '#tfc:ingredients', 'minecraft:lava')
    rm.fluid_tag('usable_in_blue_steel_bucket', '#tfc:ingredients', 'minecraft:lava')
    rm.fluid_tag('usable_in_barrel', '#tfc:ingredients')
    rm.fluid_tag('usable_in_sluice', '#tfc:any_infinite_water')
    rm.fluid_tag('usable_in_ingot_mold', '#tfc:molten_metals')
    rm.fluid_tag('usable_in_tool_head_mold', 'tfc:metal/copper', 'tfc:metal/bismuth_bronze', 'tfc:metal/black_bronze', 'tfc:metal/bronze')
    rm.fluid_tag('usable_in_bell_mold', 'tfc:metal/bronze', 'tfc:metal/gold', 'tfc:metal/brass')

    # Historically: required in order for fluids to have fluid-like properties
    # Less true in 1.20, but might still be the case for edge cases (i.e. entity AI).
    rm.fluid_tag('minecraft:water', '#tfc:salt_water', '#tfc:spring_water', 'tfc:river_water')

    # Entity Tags

    # Note, for all of these, weapons take priority over entity type
    # So, this is the damage the entity would do, if somehow they attacked you *without* a weapon.
    rm.entity_tag('deals_piercing_damage', 'minecraft:arrow', 'minecraft:bee', 'minecraft:cave_spider', 'minecraft:evoker_fangs', 'minecraft:phantom', 'minecraft:spectral_arrow', 'minecraft:spider', 'minecraft:trident', 'tfc:glow_arrow', 'tfc:thrown_javelin', 'tfc:boar', 'tfc:ocelot', 'tfc:cat', 'tfc:dog', 'tfc:wolf', 'tfc:direwolf', 'tfc:hyena')
    rm.entity_tag('deals_slashing_damage', 'minecraft:polar_bear', 'minecraft:vex', 'minecraft:wolf', 'tfc:polar_bear', 'tfc:grizzly_bear', 'tfc:black_bear', 'tfc:cougar', 'tfc:panther', 'tfc:lion', 'tfc:sabertooth', 'tfc:tiger')
    rm.entity_tag('deals_crushing_damage', 'minecraft:drowned', 'minecraft:enderman', 'minecraft:endermite', 'minecraft:goat', 'minecraft:hoglin', 'minecraft:husk', 'minecraft:iron_golem', 'minecraft:piglin', 'minecraft:piglin_brute', 'minecraft:pillager', 'minecraft:ravager', 'minecraft:silverfish', 'minecraft:slime', 'minecraft:vindicator', 'minecraft:wither', 'minecraft:wither_skeleton', 'minecraft:zoglin', 'minecraft:zombie', 'minecraft:zombie_villager', 'minecraft:zombified_piglin', 'minecraft:skeleton', 'minecraft:stray', 'tfc:falling_block', 'tfc:goat', 'tfc:wildebeest', 'tfc:moose', 'tfc:crocodile')

    # Used for Entity Damage Resistance
    rm.entity_tag('skeletons', 'minecraft:skeleton', 'minecraft:wither_skeleton', 'minecraft:stray')
    rm.entity_tag('creepers', 'minecraft:creeper')
    rm.entity_tag('zombies', 'minecraft:zombie', 'minecraft:husk', 'minecraft:zombie_villager')

    # Misc tags
    rm.entity_tag('turtle_friends', 'minecraft:player', 'tfc:dolphin')
    rm.entity_tag('spawns_on_cold_blocks', 'tfc:penguin', 'tfc:polar_bear', 'tfc:caribou')
    rm.entity_tag('minecraft:boats', 'minecraft:boat', 'minecraft:chest_boat', *['tfc:%s/%s' % (v, wood) for wood in WOODS.keys() for v in ('chest_boat', 'boat')])
    rm.entity_tag('bubble_column_immune', *['tfc:%s' % entity for entity in OCEAN_CREATURES.keys()], *['tfc:%s' % entity for entity in UNDERGROUND_WATER_CREATURES.keys()], *['tfc:%s' % entity for entity in OCEAN_AMBIENT.keys()])
    rm.entity_tag('needs_large_fishing_bait', 'tfc:dolphin', 'tfc:orca')
    rm.entity_tag('land_predators', *['tfc:%s' % entity for entity in LAND_PREDATORS])
    rm.entity_tag('land_prey', *['tfc:%s' % entity for entity in LAND_PREY], '#tfc:livestock')
    rm.entity_tag('livestock', *['tfc:%s' % entity for entity in LIVESTOCK])
    rm.entity_tag('hunts_land_prey', '#tfc:land_predators', 'minecraft:player', 'tfc:cat', 'tfc:dog')
    rm.entity_tag('hunted_by_land_predators', '#tfc:land_prey')
    rm.entity_tag('ocean_predators', *['tfc:%s' % entity for entity in OCEAN_PREDATORS])
    rm.entity_tag('hunted_by_ocean_predators', *['tfc:%s' % entity for entity in OCEAN_PREY])
    rm.entity_tag('vanilla_monsters', *['minecraft:%s' % entity for entity in VANILLA_MONSTERS.keys()])
    rm.entity_tag('horses', 'tfc:horse', 'tfc:mule', 'tfc:donkey')
    rm.entity_tag('animals', *['tfc:%s' % mob for mob in SPAWN_EGG_ENTITIES])
    rm.entity_tag('bears', 'tfc:grizzly_bear', 'tfc:polar_bear', 'tfc:black_bear')
    rm.entity_tag('small_fish', 'tfc:cod', 'tfc:salmon', 'tfc:tropical_fish', 'tfc:pufferfish', 'tfc:bluegill')
    rm.entity_tag('destroyed_by_leaves', 'minecraft:snowball', 'minecraft:egg')
    rm.entity_tag('leashable_wild_animals')
    rm.entity_tag('pests', 'tfc:rat')
    rm.entity_tag('hunted_by_cats', '#tfc:small_fish', '#tfc:land_prey', '#tfc:pests', 'minecraft:player')
    rm.entity_tag('hunted_by_dogs', '#tfc:land_prey', '#tfc:land_predators', 'minecraft:player')
    rm.entity_tag('minecraft:impact_projectiles', 'tfc:thrown_javelin')
    rm.entity_tag('minecraft:arrows', 'tfc:glow_arrow')
    rm.entity_tag('minecraft:fall_damage_immune', 'tfc:chicken', 'tfc:duck', 'tfc:quail', 'tfc:ocelot')
    rm.entity_tag('minecraft:powder_snow_walkable_mobs', 'tfc:fox', 'tfc:polar_bear', 'tfc:penguin')
    rm.entity_tag('minecraft:freeze_immune_entity_types', 'tfc:polar_bear', 'tfc:penguin')
    rm.entity_tag('minecraft:dismounts_underwater', '#tfc:horses')

    # Other Mod Tags
    # This is for things that are extremely simple for us to fix and hard for other mods to fix, not doing packmakers work for them
    rm.block_tag('quark:simple_harvest_blacklisted', '#tfc:crops')  # quark doesn't understand our crop block entities and so their right click harvesting doesn't reset growth, causing infinite food

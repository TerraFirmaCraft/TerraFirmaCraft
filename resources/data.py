#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum, auto

from mcresources import ResourceManager, utils, loot_tables
from mcresources.type_definitions import ResourceIdentifier

from constants import *
from recipes import fluid_ingredient


class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()


class Category(Enum):
    fruit = auto()
    vegetable = auto()
    grain = auto()
    bread = auto()
    dairy = auto()
    meat = auto()
    cooked_meat = auto()
    other = auto()


def generate(rm: ResourceManager):
    # Metal Items

    for metal, metal_data in METALS.items():

        # Metal
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier,
            'fluid': 'tfc:metal/%s' % metal,
            'melt_temperature': metal_data.melt_temperature,
            'heat_capacity': metal_data.heat_capacity,
            'ingots': utils.ingredient('#forge:ingots/%s' % metal),
            'sheets': utils.ingredient('#forge:sheets/%s' % metal)
        })

        # Metal Items and Blocks
        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag + '/' + metal, 'tfc:metal/%s/%s' % (item, metal))
                    ingredient = utils.item_stack('#%s/%s' % (item_data.tag, metal))
                else:
                    ingredient = utils.item_stack('tfc:metal/%s/%s' % (item, metal))

                item_heat(rm, ('metal', metal + '_' + item), ingredient, metal_data.heat_capacity, metal_data.melt_temperature)
                if 'tool' in metal_data.types and item == 'fishing_rod':
                    rm.item_tag('forge:fishing_rods', 'tfc:metal/%s/%s' % (item, metal))

    for ore, ore_data in ORES.items():
        if ore_data.metal and ore_data.graded:
            metal_data = METALS[ore_data.metal]
            item_heat(rm, ('ore', ore), ['tfc:ore/small_%s' % ore, 'tfc:ore/normal_%s' % ore, 'tfc:ore/poor_%s' % ore, 'tfc:ore/rich_%s' % ore], metal_data.heat_capacity, int(metal_data.melt_temperature))

    rm.entity_tag('turtle_friends', 'minecraft:player', 'tfc:dolphin')
    rm.entity_tag('spawns_on_cold_blocks', 'tfc:penguin', 'tfc:polar_bear')
    rm.entity_tag('destroys_floating_plants', 'minecraft:boat', *['tfc:boat/%s' % wood for wood in WOODS.keys()])
    rm.entity_tag('bubble_column_immune', *['tfc:%s' % entity for entity in OCEAN_CREATURES.keys()], *['tfc:%s' % entity for entity in UNDERGROUND_WATER_CREATURES.keys()], *['tfc:%s' % entity for entity in OCEAN_AMBIENT.keys()])
    rm.entity_tag('needs_large_fishing_bait', 'tfc:dolphin', 'tfc:orca')
    rm.entity_tag('land_predators', *['tfc:%s' % entity for entity in LAND_PREDATORS])
    rm.entity_tag('land_prey', *['tfc:%s' % entity for entity in LAND_PREY], '#tfc:livestock')
    rm.entity_tag('livestock', *['tfc:%s' % entity for entity in LIVESTOCK])
    rm.entity_tag('hunts_land_prey', '#tfc:land_predators', 'minecraft:player')
    rm.entity_tag('hunted_by_land_predators', '#tfc:land_prey')
    rm.entity_tag('ocean_predators', *['tfc:%s' % entity for entity in OCEAN_PREDATORS])
    rm.entity_tag('hunted_by_ocean_predators', *['tfc:%s' % entity for entity in OCEAN_PREY])
    rm.entity_tag('vanilla_monsters', *['minecraft:%s' % entity for entity in VANILLA_MONSTERS.keys()])

    # Item Heats

    item_heat(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', 0.35, 1535)
    item_heat(rm, 'twigs', '#tfc:twigs', 0.6)
    item_heat(rm, 'stick', '#forge:rods/wooden', 0.3)
    item_heat(rm, 'stick_bunch', 'tfc:stick_bunch', 0.05)
    item_heat(rm, 'glass_shard', 'tfc:glass_shard', 1)
    item_heat(rm, 'sand', '#forge:sand', 0.8)
    item_heat(rm, 'ceramic_unfired_brick', 'tfc:ceramic/unfired_brick', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_jug', 'tfc:ceramic/unfired_jug', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_pan', 'tfc:ceramic/unfired_pan', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_crucible', 'tfc:ceramic/unfired_crucible', POTTERY_HC)
    item_heat(rm, 'terracotta', ['minecraft:terracotta', *['minecraft:%s_terracotta' % color for color in COLORS]], 0.8)
    item_heat(rm, 'dough', '#tfc:foods/dough', 1)
    item_heat(rm, 'meat', ['tfc:food/%s' % meat for meat in MEATS], 1)
    item_heat(rm, 'edible_plants', ['tfc:plant/%s' % plant for plant in SEAWEED] + ['tfc:plant/giant_kelp_flower', 'tfc:groundcover/seaweed'], 1)
    item_heat(rm, 'egg', 'minecraft:egg', 1)
    item_heat(rm, 'blooms', '#tfc:blooms', 0.35, METALS['wrought_iron'].melt_temperature)

    for pottery in SIMPLE_POTTERY:
        item_heat(rm, 'unfired_' + pottery, 'tfc:ceramic/unfired_' + pottery, POTTERY_HC)
    item_heat(rm, 'unfired_large_vessel', 'tfc:ceramic/unfired_large_vessel', POTTERY_HC)

    for color in COLORS:
        item_heat(rm, 'unfired_%s_vessel' % color, 'tfc:ceramic/%s_unfired_vessel' % color, POTTERY_HC)
        item_heat(rm, 'unfired_large_vessel_%s' % color, 'tfc:ceramic/unfired_large_vessel/%s' % color, POTTERY_HC)

    for item, item_data in METAL_ITEMS.items():
        if item_data.mold:
            item_heat(rm, 'unfired_%s_mold' % item, 'tfc:ceramic/unfired_%s_mold' % item, POTTERY_HC)
            # No need to do fired molds, as they have their own capability implementation

    # Supports

    for wood in WOODS:
        rm.data(('tfc', 'supports', wood), {
            'ingredient': 'tfc:wood/horizontal_support/%s' % wood,
            'support_up': 1,
            'support_down': 1,
            'support_horizontal': 4
        })

    # Fuels

    for wood, wood_data in WOODS.items():
        fuel_item(rm, wood + '_log', ['tfc:wood/log/' + wood, 'tfc:wood/wood/' + wood, 'tfc:wood/stripped_wood/' + wood, 'tfc:wood/stripped_log/' + wood], wood_data.duration, wood_data.temp)

    fuel_item(rm, 'coal', ['minecraft:coal', 'tfc:ore/bituminous_coal'], 2200, 1415)
    fuel_item(rm, 'lignite', 'tfc:ore/lignite', 2200, 1350)
    fuel_item(rm, 'charcoal', 'minecraft:charcoal', 1800, 1350)
    fuel_item(rm, 'peat', 'tfc:peat', 2500, 600)
    fuel_item(rm, 'stick_bundle', 'tfc:stick_bundle', 600, 900)

    # =========
    # ITEM TAGS
    # =========

    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.item_tag('firepit_sticks', '#forge:rods/wooden')
    rm.item_tag('firepit_kindling', 'tfc:straw', 'minecraft:paper', 'minecraft:book', 'tfc:groundcover/pinecone')
    rm.item_tag('starts_fires_with_durability', 'minecraft:flint_and_steel')
    rm.item_tag('starts_fires_with_items', 'minecraft:fire_charge')
    rm.item_tag('handstone', 'tfc:handstone')
    rm.item_tag('high_quality_cloth', 'tfc:silk_cloth', 'tfc:wool_cloth')
    rm.item_tag('minecraft:stone_pressure_plates', 'minecraft:stone_pressure_plate', 'minecraft:polished_blackstone_pressure_plate')
    rm.item_tag('axes_that_log', '#tfc:axes')
    rm.item_tag('inefficient_logging_axes', *['tfc:stone/axe/%s' % cat for cat in ROCK_CATEGORIES])
    rm.item_tag('extinguisher', '#tfc:shovels')
    rm.item_tag('forge:shears', '#tfc:shears')  # forge tag includes TFC shears
    rm.item_tag('minecraft:coals', 'tfc:ore/bituminous_coal', 'tfc:ore/lignite')
    rm.item_tag('forge_fuel', '#minecraft:coals')
    rm.item_tag('firepit_fuel', '#minecraft:logs', 'tfc:peat', 'tfc:peat_grass', 'tfc:stick_bundle')
    rm.item_tag('bloomery_fuel', 'minecraft:charcoal')
    rm.item_tag('blast_furnace_fuel', 'minecraft:charcoal')
    rm.item_tag('log_pile_logs', 'tfc:stick_bundle')
    rm.item_tag('pit_kiln_straw', 'tfc:straw')
    rm.item_tag('firepit_logs', '#minecraft:logs')
    rm.item_tag('log_pile_logs', '#minecraft:logs')
    rm.item_tag('pit_kiln_logs', '#minecraft:logs')
    rm.item_tag('can_be_lit_on_torch', '#forge:rods/wooden')
    rm.item_tag('wattle_sticks', 'tfc:stick_bunch')
    rm.item_tag('mortar', 'tfc:mortar')
    rm.item_tag('flux', 'tfc:powder/flux')
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.item_tag('scrapable', 'tfc:large_soaked_hide', 'tfc:medium_soaked_hide', 'tfc:small_soaked_hide')
    rm.item_tag('clay_knapping', 'minecraft:clay_ball')
    rm.item_tag('fire_clay_knapping', 'tfc:fire_clay')
    rm.item_tag('leather_knapping', '#forge:leather')
    rm.item_tag('knapping_any', '#tfc:clay_knapping', '#tfc:fire_clay_knapping', '#tfc:leather_knapping', '#tfc:rock_knapping')
    rm.item_tag('forge:gems/diamond', 'tfc:gem/diamond')
    rm.item_tag('forge:gems/lapis', 'tfc:gem/lapis_lazuli')
    rm.item_tag('forge:gems/emerald', 'tfc:gem/emerald')
    rm.item_tag('bush_cutting_tools', '#forge:shears', '#tfc:knives')
    rm.item_tag('minecraft:fishes', 'tfc:food/cod', 'tfc:food/cooked_cod', 'tfc:food/salmon', 'tfc:food/cooked_salmon', 'tfc:food/tropical_fish', 'tfc:food/cooked_tropical_fish', 'tfc:food/bluegill', 'tfc:food/cooked_bluegill', 'tfc:food/shellfish', 'tfc:food/cooked_shellfish')
    rm.item_tag('small_fishing_bait', 'tfc:food/shellfish', '#tfc:seeds')
    rm.item_tag('large_fishing_bait', 'tfc:food/cod', 'tfc:food/salmon', 'tfc:food/tropical_fish', 'tfc:food/bluegill')
    rm.item_tag('holds_small_fishing_bait', *['tfc:metal/fishing_rod/%s' % metal for metal, data in METALS.items() if 'tool' in data.types])
    rm.item_tag('holds_large_fishing_bait', *['tfc:metal/fishing_rod/%s' % metal for metal in ('wrought_iron', 'red_steel', 'blue_steel', 'black_steel', 'steel')])
    rm.item_tag('forge:string', 'tfc:wool_yarn')
    rm.item_tag('usable_on_tool_rack', 'tfc:firestarter', 'minecraft:bow', 'minecraft:crossbow', 'minecraft:flint_and_steel')
    rm.item_tag('usable_in_powder_keg', 'minecraft:gunpowder')
    rm.item_tag('buckets', 'tfc:wooden_bucket', 'tfc:metal/bucket/red_steel', 'tfc:metal/bucket/blue_steel')
    rm.item_tag('fox_spawns_with', 'minecraft:rabbit_foot', 'minecraft:feather', 'minecraft:bone', 'tfc:food/salmon', 'tfc:food/bluegill', 'minecraft:egg', 'tfc:small_raw_hide', 'tfc:food/cloudberry', 'tfc:food/strawberry', 'tfc:food/gooseberry', 'tfc:food/rabbit', 'minecraft:flint')

    rm.item_tag('pig_food', '#tfc:foods')
    rm.item_tag('cow_food', '#tfc:foods/grains')
    rm.item_tag('sheep_food', '#tfc:foods/grains')
    rm.item_tag('yak_food', '#tfc:foods/grains')
    rm.item_tag('goat_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables')
    rm.item_tag('chicken_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds')
    rm.item_tag('duck_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds', 'tfc:food/barley_bread', 'tfc:food/wheat_bread', 'tfc:food/rye_bread', 'tfc:food/maize_bread', 'tfc:food/oat_bread', 'tfc:food/oat_bread')
    rm.item_tag('quail_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:seeds')
    rm.item_tag('alpaca_food', '#tfc:foods/grains', '#tfc:foods/fruits')
    rm.item_tag('mule_food', '#tfc:horse_food')
    rm.item_tag('donkey_food', '#tfc:horse_food')
    rm.item_tag('horse_food', '#tfc:foods/grains', '#tfc:foods/fruits')
    rm.item_tag('musk_ox_food', '#tfc:foods/grains')

    rm.item_tag('tfc:foods/dough', *['tfc:food/%s_dough' % g for g in GRAINS])
    rm.item_tag('foods/can_be_salted', '#tfc:foods/raw_meats')
    rm.item_tag('tfc:foods/grains', *['tfc:food/%s_grain' % grain for grain in GRAINS])
    rm.item_tag('tfc:compost_greens', '#tfc:plants', *['tfc:food/%s' % v for v in MISC_FOODS], *['tfc:food/%s' % m for m in FRUITS], *['tfc:food/%s_bread' % grain for grain in GRAINS])
    rm.item_tag('tfc:compost_browns', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass', 'tfc:groundcover/driftwood', 'tfc:groundcover/pinecone', 'minecraft:paper', 'tfc:melon', 'tfc:pumpkin', 'tfc:rotten_melon', 'tfc:rotten_pumpkin')
    rm.item_tag('tfc:compost_poisons', *['tfc:food/%s' % m for m in MEATS], *['tfc:food/cooked_%s' % m for m in MEATS], 'minecraft:bone')
    rm.item_tag('forge:double_sheets/any_bronze', *['#forge:double_sheets/%sbronze' % b for b in ('bismuth_', 'black_', '')])
    rm.item_tag('tfc:bronze_anvils', *['tfc:metal/anvil/%sbronze' % b for b in ('bismuth_', 'black_', '')])
    block_and_item_tag(rm, 'tfc:anvils', *['tfc:metal/anvil/%s' % metal for metal, data in METALS.items() if 'utility' in data.types])
    rm.item_tag('fluxstone', 'tfc:food/shellfish', 'tfc:groundcover/mollusk', 'tfc:groundcover/clam', 'minecraft:scute')
    rm.item_tag('minecraft:arrows', 'tfc:glow_arrow')
    rm.item_tag('foods/apples', 'tfc:food/green_apple', 'tfc:food/red_apple')
    rm.item_tag('foods/usable_in_soup', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:foods/meats', '#tfc:foods/cooked_meats', 'tfc:food/cooked_rice')
    rm.item_tag('foods/usable_in_salad', '#tfc:foods/fruits', '#tfc:foods/vegetables', '#tfc:foods/cooked_meats')
    rm.item_tag('foods/usable_in_sandwich', '#tfc:foods/vegetables', '#tfc:foods/cooked_meats', '#tfc:foods/dairy')
    rm.item_tag('sandwich_bread', *['tfc:food/%s_bread' % grain for grain in GRAINS])
    rm.item_tag('bowls', 'tfc:ceramic/bowl', 'minecraft:bowl')
    rm.item_tag('soup_bowls', '#tfc:bowls')
    rm.item_tag('salad_bowls', '#tfc:bowls')
    rm.item_tag('scribing_ink', 'minecraft:black_dye')
    rm.item_tag('vessels', 'tfc:ceramic/unfired_vessel', 'tfc:ceramic/vessel')
    rm.item_tag('ore_deposits', *['tfc:deposit/%s/%s' % (ore, rock) for ore in ORE_DEPOSITS for rock in ROCKS.keys()])
    block_and_item_tag(rm, 'tfc:barrels', *['tfc:wood/barrel/%s' % wood for wood in WOODS.keys()])
    block_and_item_tag(rm, 'forge:chests/wooden', *['tfc:wood/chest/%s' % wood for wood in WOODS.keys()], *['tfc:wood/trapped_chest/%s' % wood for wood in WOODS.keys()])
    rm.item_tag('mob_feet_armor', *['tfc:metal/boots/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_leg_armor', *['tfc:metal/greaves/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_chest_armor', *['tfc:metal/chestplate/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_helmet_armor', *['tfc:metal/helmet/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_offhand_weapons', *['tfc:metal/shield/%s' % metal for metal in MOB_ARMOR_METALS])
    rm.item_tag('mob_mainhand_weapons', *['tfc:metal/%s/%s' % (tool, metal) for metal in MOB_ARMOR_METALS for tool in MOB_TOOLS], *['tfc:stone/%s/%s' % (tool, stone) for stone in ROCK_CATEGORIES for tool in STONE_MOB_TOOLS], 'tfc:large_raw_hide', 'tfc:medium_raw_hide', 'tfc:small_raw_hide', 'tfc:handstone')
    rm.item_tag('disabled_monster_held_items', 'minecraft:iron_sword', 'minecraft:iron_shovel', 'minecraft:fishing_rod', 'minecraft:nautilus_shell')
    rm.item_tag('deals_piercing_damage', '#tfc:javelins', '#tfc:knives')
    rm.item_tag('deals_slashing_damage', '#tfc:swords', '#tfc:axes')
    rm.item_tag('deals_crushing_damage', '#tfc:hammers', '#tfc:maces')

    for color in COLORS:
        rm.item_tag('vessels', 'tfc:ceramic/%s_unfired_vessel' % color, 'tfc:ceramic/%s_glazed_vessel' % color)
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

    for wood in WOODS.keys():
        def item(_variant: str) -> str:
            return 'tfc:wood/%s/%s' % (_variant, wood)

        def plank(_variant: str) -> str:
            return 'tfc:wood/planks/%s_%s' % (wood, _variant)

        rm.item_tag('lumber', item('lumber'))
        block_and_item_tag(rm, 'twigs', item('twig'))
        block_and_item_tag(rm, 'looms', plank('loom'))
        block_and_item_tag(rm, 'sluices', item('sluice'))
        block_and_item_tag(rm, 'workbenches', plank('workbench'))
        block_and_item_tag(rm, 'bookshelves', plank('bookshelf'))
        block_and_item_tag(rm, 'lecterns', item('lectern'))

        rm.item_tag('minecraft:boats', item('boat'))
        block_and_item_tag(rm, 'minecraft:wooden_buttons', plank('button'))
        block_and_item_tag(rm, 'minecraft:wooden_fences', plank('fence'), plank('log_fence'))
        block_and_item_tag(rm, 'minecraft:wooden_slabs', plank('slab'))
        block_and_item_tag(rm, 'minecraft:wooden_stairs', plank('stairs'))
        block_and_item_tag(rm, 'minecraft:wooden_doors', plank('door'))
        block_and_item_tag(rm, 'minecraft:wooden_trapdoors', plank('trapdoor'))
        block_and_item_tag(rm, 'minecraft:wooden_pressure_plates', plank('pressure_plate'))
        block_and_item_tag(rm, 'minecraft:logs', '#tfc:%s_logs' % wood)
        block_and_item_tag(rm, 'minecraft:leaves', item('leaves'))
        block_and_item_tag(rm, 'minecraft:planks', item('planks'))

        block_and_item_tag(rm, 'forge:fence_gates/wooden', plank('fence_gate'))

        block_and_item_tag(rm, '%s_logs' % wood, item('log'), item('wood'), item('stripped_log'), item('stripped_wood'))

        if wood in TANNIN_WOOD_TYPES:
            rm.item_tag('makes_tannin', item('log'), item('wood'))

    for category in ROCK_CATEGORIES:  # Rock (Category) Tools
        for tool in ROCK_CATEGORY_ITEMS:
            rm.item_tag(TOOL_TAGS[tool], 'tfc:stone/%s/%s' % (tool, category))
            rm.item_tag("usable_on_tool_rack", 'tfc:stone/%s/%s' % (tool, category))

    for metal, metal_data in METALS.items():  # Metal Tools
        if 'tool' in metal_data.types:
            for tool_type, tool_tag in TOOL_TAGS.items():
                rm.item_tag(tool_tag, 'tfc:metal/%s/%s' % (tool_type, metal))
                rm.item_tag("usable_on_tool_rack", 'tfc:metal/%s/%s' % (tool_type, metal))
            rm.item_tag("usable_on_tool_rack", 'tfc:metal/fishing_rod/%s' % metal, 'tfc:metal/tuyere/%s' % metal)
        

    # Blocks and Items
    block_and_item_tag(rm, 'forge:sand', '#minecraft:sand')  # Forge doesn't reference the vanilla tag for some reason

    for wood in WOODS.keys():
        block_and_item_tag(rm, 'tool_racks', 'tfc:wood/planks/%s_tool_rack' % wood)
        rm.block_tag('single_block_replaceable', 'tfc:wood/twig/%s' % wood, 'tfc:wood/fallen_leaves/%s' % wood)

    for plant in PLANTS.keys():
        block_and_item_tag(rm, 'plants', 'tfc:plant/%s' % plant)
    for plant in UNIQUE_PLANTS:
        rm.block_tag('plants', 'tfc:plant/%s' % plant)

    # Sand
    for color in SAND_BLOCK_TYPES:
        block_and_item_tag(rm, 'minecraft:sand', 'tfc:sand/%s' % color)

    # Metal Ingots / Sheets, for Ingot/Sheet Piles
    for metal in METALS.keys():
        rm.item_tag('forge:ingots/%s' % metal)
        rm.item_tag('forge:sheets/%s' % metal)
        rm.item_tag('tfc:pileable_ingots', '#forge:ingots/%s' % metal)
        rm.item_tag('tfc:pileable_sheets', '#forge:sheets/%s' % metal)

    # ==========
    # BLOCK TAGS
    # ==========

    rm.block_tag('minecraft:valid_spawn', *[
        *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS],
        *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES],
        *['tfc:rock/raw/%s' % r for r in ROCKS.keys()]
    ])  # Valid spawn tag - grass, sand, or raw rock
    block_and_item_tag(rm, 'minecraft:dirt', *['tfc:dirt/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:rooted_dirt/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('minecraft:geode_invalid_blocks', 'tfc:sea_ice', 'tfc:fluid/salt_water', 'tfc:fluid/river_water', 'tfc:fluid/spring_water')
    rm.block_tag('minecraft:bamboo_plantable_on', '#tfc:grass')
    rm.block_tag('minecraft:climbable', 'tfc:plant/hanging_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/liana', 'tfc:plant/liana_plant')
    rm.block_tag('minecraft:infiniburn_overworld', 'tfc:pit_kiln')
    rm.block_tag('creeping_plantable_on', '#minecraft:logs')
    rm.block_tag('minecraft:prevent_mob_spawning_inside', 'tfc:thatch', '#minecraft:leaves')

    for color in COLORS:
        rm.block_tag('minecraft:walls', 'tfc:alabaster/polished/%s_wall' % color, 'tfc:alabaster/bricks/%s_wall' % color)

    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#minecraft:dirt', '#tfc:grass', '#tfc:mud')
    rm.block_tag('supports_landslide', 'minecraft:dirt_path', *['tfc:grass_path/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:farmland/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#minecraft:dirt', '#tfc:grass', '#tfc:farmland')
    block_and_item_tag(rm, 'mud', *['tfc:mud/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('grass_plantable_on', '#tfc:bush_plantable_on', 'tfc:peat', '#tfc:mud')
    rm.block_tag('small_spike', 'tfc:calcite')
    rm.block_tag('sea_bush_plantable_on', '#minecraft:dirt', '#minecraft:sand', '#forge:gravel', '#tfc:mud')
    rm.block_tag('creeping_plantable_on', 'minecraft:grass_block', '#tfc:grass', '#minecraft:base_stone_overworld', '#minecraft:logs')
    rm.block_tag('kelp_tree', 'tfc:plant/giant_kelp_flower', 'tfc:plant/giant_kelp_plant')
    rm.block_tag('kelp_flower', 'tfc:plant/giant_kelp_flower')
    rm.block_tag('kelp_branch', 'tfc:plant/giant_kelp_plant')
    rm.block_tag('lit_by_dropped_torch', 'tfc:log_pile', 'tfc:thatch', 'tfc:pit_kiln')
    rm.block_tag('charcoal_cover_whitelist', 'tfc:log_pile', 'tfc:charcoal_pile', 'tfc:burning_log_pile')
    rm.block_tag('forge_invisible_whitelist', 'tfc:crucible')
    rm.block_tag('any_spreading_bush', '#tfc:spreading_bush')
    rm.block_tag('thorny_bushes', 'tfc:plant/blackberry_bush', 'tfc:plant/raspberry_bush')
    rm.block_tag('logs_that_log', '#minecraft:logs')
    rm.block_tag('scraping_surface', '#minecraft:logs')
    rm.block_tag('forge:sand', '#minecraft:sand')  # Forge doesn't reference the vanilla tag
    rm.block_tag('forge:concrete', *['minecraft:%s_concrete' % c for c in COLORS])
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')
    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')
    rm.block_tag('tfc:forge_insulation', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone')
    rm.block_tag('tfc:bloomery_insulation', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone', 'minecraft:bricks', 'tfc:fire_bricks', '#forge:concrete')
    rm.block_tag('tfc:blast_furnace_insulation', 'tfc:fire_bricks')
    rm.block_tag('wild_crop_grows_on', '#tfc:bush_plantable_on')
    rm.block_tag('plants', *['tfc:wild_crop/%s' % crop for crop in CROPS.keys()])
    rm.block_tag('rabbit_raidable', 'tfc:crop/carrot', 'tfc:crop/cabbage', 'minecraft:carrots')
    rm.block_tag('single_block_replaceable', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass')
    rm.block_tag('powder_snow_replaceable', '#minecraft:dirt', '#forge:gravel', '#tfc:grass', 'minecraft:snow')
    rm.item_tag('usable_on_tool_rack', 'tfc:firestarter', 'minecraft:bow', 'minecraft:crossbow', 'minecraft:flint_and_steel')
    rm.block_tag('creates_downward_bubbles', 'minecraft:soul_sand')
    block_and_item_tag(rm, 'clay_indicators', *['tfc:plant/%s' % plant for plant in ('athyrium_fern', 'canna', 'goldenrod', 'pampas_grass', 'perovskia', 'water_canna')])
    rm.block_tag('mud_bricks', 'tfc:mud_bricks/loam', 'tfc:mud_bricks/silt', 'tfc:mud_bricks/sandy_loam', 'tfc:mud_bricks/silty_loam')

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
                rm.block('tfc:ore/%s/%s/prospected' % (ore, rock)).with_lang(name)

    for wood in WOODS.keys():
        rm.block_tag('lit_by_dropped_torch', 'tfc:wood/fallen_leaves/' + wood)
        rm.block_tag('converts_to_humus', 'tfc:wood/fallen_leaves/' + wood)

    for plant, data in PLANTS.items():  # Plants
        block_and_item_tag(rm, 'plants', 'tfc:plant/%s' % plant)
        if data.type in ('standard', 'short_grass', 'dry', 'grass_water', 'water'):
            rm.block_tag('single_block_replaceable', 'tfc:plant/%s' % plant)
        if data.type in ('standard', 'tall_plant', 'short_grass', 'tall_grass', 'creeping'):
            rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)
        if data.type in ('emergent', 'emergent_fresh', 'floating', 'floating_fresh', 'creeping'):
            rm.block_tag('can_be_ice_piled', 'tfc:plant/%s' % plant)

    # Rocks
    for rock, rock_data in ROCKS.items():
        def block(block_type: str):
            return 'tfc:rock/%s/%s' % (block_type, rock)

        block_and_item_tag(rm, 'forge:gravel', 'tfc:rock/gravel/%s' % rock)
        block_and_item_tag(rm, 'forge:stone', block('raw'))
        rm.block_tag('forge:stone', block('hardened'))
        block_and_item_tag(rm, 'forge:cobblestone/normal', block('cobble'), block('mossy_cobble'))
        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        block_and_item_tag(rm, 'forge:stone_bricks', block('bricks'), block('mossy_bricks'), block('cracked_bricks'))
        block_and_item_tag(rm, 'forge:smooth_stone', block('smooth'))
        rm.block_tag('tfc:breaks_when_isolated', block('raw'))
        block_and_item_tag(rm, 'minecraft:stone_pressure_plates', block('pressure_plate'))
        block_and_item_tag(rm, 'forge:smooth_stone_slab', 'tfc:rock/smooth/%s_slab' % rock)
        rm.item_tag('tfc:rock_knapping', block('loose'))
        rm.item_tag('tfc:%s_rock' % rock_data.category, block('loose'))
        if rock_data.category == 'igneous_extrusive' or rock_data.category == 'igneous_intrusive':
            rm.block_tag('creates_upward_bubbles', block('magma'))

        if rock in ['chalk', 'dolomite', 'limestone', 'marble']:
            rm.item_tag('tfc:fluxstone', block('loose'))

        for ore in ORE_DEPOSITS:
            block_and_item_tag(rm, 'forge:gravel', 'tfc:deposit/%s/%s' % (ore, rock))
            rm.block_tag('can_be_panned', 'tfc:deposit/%s/%s' % (ore, rock))

    # Ore tags
    for ore, data in ORES.items():
        if data.tag not in DEFAULT_FORGE_ORE_TAGS:
            rm.block_tag('forge:ores', '#forge:ores/%s' % data.tag)
        if data.graded:  # graded ores -> each grade is declared as a TFC tag, then added to the forge tag
            rm.block_tag('forge:ores/%s' % data.tag, '#tfc:ores/%s/poor' % data.tag, '#tfc:ores/%s/normal' % data.tag, '#tfc:ores/%s/rich' % data.tag)
        for rock in ROCKS.keys():
            if data.graded:
                rm.block_tag('ores/%s/poor' % data.tag, 'tfc:ore/poor_%s/%s' % (ore, rock))
                rm.block_tag('ores/%s/normal' % data.tag, 'tfc:ore/normal_%s/%s' % (ore, rock))
                rm.block_tag('ores/%s/rich' % data.tag, 'tfc:ore/rich_%s/%s' % (ore, rock))
            else:
                rm.block_tag('forge:ores/%s' % data.tag, 'tfc:ore/%s/%s' % (ore, rock))

    # can_carve Tag
    for rock in ROCKS.keys():
        for variant in ('raw', 'hardened', 'gravel', 'cobble'):
            rm.block_tag('can_carve', 'tfc:rock/%s/%s' % (variant, rock))
    for sand in SAND_BLOCK_TYPES:
        rm.block_tag('can_carve', 'tfc:sand/%s' % sand, 'tfc:raw_sandstone/%s' % sand)
    for soil in SOIL_BLOCK_VARIANTS:
        rm.block_tag('can_carve', 'tfc:dirt/%s' % soil, 'tfc:grass/%s' % soil, 'tfc:mud/%s' % soil, 'tfc:rooted_dirt/%s' % soil)
    rm.block_tag('can_carve', 'minecraft:powder_snow')

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
            'netherite': 'forge:needs_netherite_tool', 'black_steel': 'forge:needs_netherite_tool',
            'colored_steel': 'tfc:needs_colored_steel_tool'
        }[_tool]

    rm.block_tag('minecraft:mineable/hoe', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_knife', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_scythe', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_hammer', '#tfc:mineable_with_blunt_tool')
    rm.item_tag('tfc:sharp_tools', '#tfc:hoes', '#tfc:knives', '#tfc:scythes')

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
        *['tfc:sand/%s' % sand for sand in SAND_BLOCK_TYPES],
        'tfc:snow_pile',
        *['tfc:rock/gravel/%s' % rock for rock in ROCKS.keys()],
        *['tfc:deposit/%s/%s' % (ore, rock) for ore in ORE_DEPOSITS for rock in ROCKS.keys()],
        'tfc:aggregate',
        'tfc:fire_clay_block',
        'tfc:charcoal_pile',
        'tfc:charcoal_forge'
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
        *['tfc:rock/%s/%s' % (variant, rock) for variant in ('raw', 'hardened', 'smooth', 'cobble', 'bricks', 'spike', 'cracked_bricks', 'mossy_bricks', 'mossy_cobble', 'chiseled', 'loose', 'pressure_plate', 'button') for rock in ROCKS.keys()],
        *['tfc:rock/%s/%s_%s' % (variant, rock, suffix) for variant in ('raw', 'smooth', 'cobble', 'bricks', 'cracked_bricks', 'mossy_bricks', 'mossy_cobble') for rock in ROCKS.keys() for suffix in ('slab', 'stairs', 'wall')],
        *['tfc:rock/anvil/%s' % rock for rock, rock_data in ROCKS.items() if rock_data.category == 'igneous_intrusive' or rock_data.category == 'igneous_extrusive'],
        *['tfc:rock/magma/%s' % rock for rock, rock_data in ROCKS.items() if rock_data.category == 'igneous_intrusive' or rock_data.category == 'igneous_extrusive'],
        *['tfc:metal/%s/%s' % (variant, metal) for variant, variant_data in METAL_BLOCKS.items() for metal, metal_data in METALS.items() if variant_data.type in metal_data.types],
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
        'tfc:sheet_pile',
        'tfc:blast_furnace'
    ])
    rm.block_tag('minecraft:mineable/axe', *[
        *['tfc:wood/%s/%s' % (variant, wood) for variant in ('log', 'stripped_log', 'wood', 'stripped_wood', 'planks', 'twig', 'vertical_support', 'horizontal_support', 'sluice', 'chest', 'trapped_chest') for wood in WOODS.keys()],
        *['tfc:wood/planks/%s_%s' % (wood, variant) for variant in ('bookshelf', 'door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs', 'tool_rack', 'workbench', 'sign') for wood in WOODS.keys()],
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
        'tfc:powderkeg'
    ])
    rm.block_tag('tfc:mineable_with_sharp_tool', *[
        *['tfc:wood/%s/%s' % (variant, wood) for variant in ('leaves', 'sapling', 'fallen_leaves') for wood in WOODS.keys()],
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
        *['tfc:plant/%s_leaves' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:plant/%s_sapling' % tree for tree in NORMAL_FRUIT_TREES],
        'tfc:plant/banana_sapling',
        'tfc:thatch',
        'tfc:thatch_bed'
    ])
    rm.block_tag('tfc:mineable_with_blunt_tool',
                 *['tfc:wood/%s/%s' % (variant, wood) for variant in ('log', 'stripped_log', 'wood', 'stripped_wood') for wood in WOODS.keys()]
                 )

    # ==========
    # FLUID TAGS
    # ==========

    # Categories
    rm.fluid_tag('milks', 'minecraft:milk')
    rm.fluid_tag('alcohols', *ALCOHOLS)
    rm.fluid_tag('dyes', *['tfc:%s_dye' % dye for dye in COLORS])
    rm.fluid_tag('drinkable_ingredients', '#minecraft:water', '#tfc:alcohols', '#tfc:milks')
    rm.fluid_tag('ingredients', *SIMPLE_FLUIDS, '#tfc:drinkable_ingredients', '#tfc:dyes')

    # Applications
    rm.fluid_tag('drinkables', 'tfc:river_water', '#tfc:drinkable_ingredients')
    rm.fluid_tag('hydrating', 'tfc:river_water', '#minecraft:water')

    rm.fluid_tag('usable_in_pot', '#tfc:ingredients')
    rm.fluid_tag('usable_in_jug', '#tfc:drinkable_ingredients')
    rm.fluid_tag('usable_in_wooden_bucket', '#tfc:ingredients')
    rm.fluid_tag('usable_in_red_steel_bucket', '#tfc:ingredients')
    rm.fluid_tag('usable_in_blue_steel_bucket', '#minecraft:lava', '#tfc:molten_metals')
    rm.fluid_tag('usable_in_barrel', '#tfc:ingredients')
    rm.fluid_tag('usable_in_sluice', '#minecraft:water')
    rm.fluid_tag('scribing_ink', 'tfc:black_dye')

    # Entity Tags

    # Note, for all of these, weapons take priority over entity type
    # So, this is the damage the entity would do, if somehow they attacked you *without* a weapon.
    rm.entity_tag('deals_piercing_damage', 'minecraft:arrow', 'minecraft:bee', 'minecraft:cave_spider', 'minecraft:evoker_fangs', 'minecraft:phantom', 'minecraft:spectral_arrow', 'minecraft:spider', 'minecraft:trident', 'tfc:glow_arrow', 'tfc:thrown_javelin', 'tfc:boar')
    rm.entity_tag('deals_slashing_damage', 'minecraft:polar_bear', 'minecraft:vex', 'minecraft:wolf', 'tfc:polar_bear', 'tfc:grizzly_bear', 'tfc:black_bear', 'tfc:cougar', 'tfc:panther', 'tfc:lion', 'tfc:sabertooth')
    rm.entity_tag('deals_crushing_damage', 'minecraft:drowned', 'minecraft:enderman', 'minecraft:endermite', 'minecraft:goat', 'minecraft:hoglin', 'minecraft:husk', 'minecraft:iron_golem', 'minecraft:piglin', 'minecraft:piglin_brute', 'minecraft:pillager', 'minecraft:ravager', 'minecraft:silverfish', 'minecraft:slime', 'minecraft:vindicator', 'minecraft:wither', 'minecraft:wither_skeleton', 'minecraft:zoglin', 'minecraft:zombie', 'minecraft:zombie_villager', 'minecraft:zombified_piglin', 'minecraft:skeleton', 'minecraft:stray', 'tfc:falling_block', 'tfc:goat')

    # Item Sizes

    item_size(rm, 'logs', '#minecraft:logs', Size.very_large, Weight.medium)
    item_size(rm, 'quern', 'tfc:quern', Size.very_large, Weight.very_heavy)
    item_size(rm, 'tool_racks', '#tfc:tool_racks', Size.large, Weight.very_heavy)
    item_size(rm, 'chests', '#forge:chests', Size.large, Weight.light)
    # todo: add tfc (non-wooden) slabs to minecraft slab tag
    item_size(rm, 'slabs', '#minecraft:slabs', Size.small, Weight.very_light)
    item_size(rm, 'vessels', '#tfc:vessels', Size.normal, Weight.very_heavy)
    item_size(rm, 'doors', '#minecraft:doors', Size.very_large, Weight.heavy)
    item_size(rm, 'mortar', '#tfc:mortar', Size.tiny, Weight.very_light)
    item_size(rm, 'stick_bunch', 'tfc:stick_bunch', Size.normal, Weight.light)
    item_size(rm, 'stick_bundle', 'tfc:stick_bundle', Size.very_large, Weight.medium)
    item_size(rm, 'jute_fiber', 'tfc:jute_fiber', Size.small, Weight.very_light)
    item_size(rm, 'burlap_cloth', 'tfc:burlap_cloth', Size.small, Weight.very_light)
    item_size(rm, 'straw', 'tfc:straw', Size.small, Weight.very_light)
    item_size(rm, 'wool', 'tfc:wool', Size.small, Weight.light)
    item_size(rm, 'wool_cloth', 'tfc:wool_cloth', Size.small, Weight.light)
    item_size(rm, 'silk_cloth', 'tfc:silk_cloth', Size.small, Weight.light)
    item_size(rm, 'alabaster_brick', 'tfc:alabaster_brick', Size.small, Weight.light)
    item_size(rm, 'glue', 'tfc:glue', Size.tiny, Weight.light)
    item_size(rm, 'brass_mechanisms', 'tfc:brass_mechanisms', Size.normal, Weight.light)
    item_size(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', Size.large, Weight.heavy)
    item_size(rm, 'dyes', '#tfc:dyes', Size.tiny, Weight.light)
    item_size(rm, 'foods', '#tfc:foods', Size.small, Weight.very_light)
    item_size(rm, 'plants', '#tfc:plants', Size.tiny, Weight.very_light)
    item_size(rm, 'jute', 'tfc:jute', Size.small, Weight.very_light)
    item_size(rm, 'bloomery', 'tfc:bloomery', Size.large, Weight.very_heavy)
    item_size(rm, 'sluice', '#tfc:sluices', Size.very_large, Weight.very_heavy)
    item_size(rm, 'lamps', '#tfc:lamps', Size.normal, Weight.very_heavy)
    item_size(rm, 'signs', '#minecraft:signs', Size.very_small, Weight.heavy)
    item_size(rm, 'soups', '#tfc:soups', Size.very_small, Weight.very_heavy)
    item_size(rm, 'salads', '#tfc:salads', Size.very_small, Weight.very_heavy)
    item_size(rm, 'buckets', '#tfc:buckets', Size.large, Weight.very_heavy)
    item_size(rm, 'anvils', '#tfc:anvils', Size.huge, Weight.very_heavy)
    item_size(rm, 'minecarts', '#tfc:minecarts', Size.very_large, Weight.very_heavy)
    item_size(rm, 'looms', '#tfc:looms', Size.large, Weight.very_heavy)

    # Food

    food_item(rm, 'banana', 'tfc:food/banana', Category.fruit, 4, 0.2, 0, 2, fruit=1)
    food_item(rm, 'blackberry', 'tfc:food/blackberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=0.75)
    food_item(rm, 'blueberry', 'tfc:food/blueberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=0.75)
    food_item(rm, 'bunchberry', 'tfc:food/bunchberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'cherry', 'tfc:food/cherry', Category.fruit, 4, 0.2, 5, 4, fruit=1)
    food_item(rm, 'cloudberry', 'tfc:food/cloudberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'cranberry', 'tfc:food/cranberry', Category.fruit, 4, 0.2, 5, 1.8, fruit=1)
    food_item(rm, 'elderberry', 'tfc:food/elderberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'gooseberry', 'tfc:food/gooseberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'green_apple', 'tfc:food/green_apple', Category.fruit, 4, 0.5, 0, 2.5, fruit=1)
    food_item(rm, 'lemon', 'tfc:food/lemon', Category.fruit, 4, 0.2, 5, 2, fruit=0.75)
    food_item(rm, 'olive', 'tfc:food/olive', Category.fruit, 4, 0.2, 0, 1.6, fruit=1)
    food_item(rm, 'orange', 'tfc:food/orange', Category.fruit, 4, 0.5, 10, 2.2, fruit=0.5)
    food_item(rm, 'peach', 'tfc:food/peach', Category.fruit, 4, 0.5, 10, 2.8, fruit=0.5)
    food_item(rm, 'plum', 'tfc:food/plum', Category.fruit, 4, 0.5, 5, 2.8, fruit=0.75)
    food_item(rm, 'raspberry', 'tfc:food/raspberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'red_apple', 'tfc:food/red_apple', Category.fruit, 4, 0.5, 0, 1.7, fruit=1)
    food_item(rm, 'snowberry', 'tfc:food/snowberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'strawberry', 'tfc:food/strawberry', Category.fruit, 4, 0.5, 10, 4.9, fruit=0.5)
    food_item(rm, 'wintergreen_berry', 'tfc:food/wintergreen_berry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'barley', 'tfc:food/barley', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'barley_grain', 'tfc:food/barley_grain', Category.grain, 4, 0, 0, 0.25)
    food_item(rm, 'barley_flour', 'tfc:food/barley_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'barley_dough', 'tfc:food/barley_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'barley_bread', 'tfc:food/barley_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    food_item(rm, 'maize', 'tfc:food/maize', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'maize_grain', 'tfc:food/maize_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'maize_flour', 'tfc:food/maize_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'maize_dough', 'tfc:food/maize_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'maize_bread', 'tfc:food/maize_bread', Category.bread, 4, 1, 0, 1, grain=1)
    food_item(rm, 'oat', 'tfc:food/oat', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'oat_grain', 'tfc:food/oat_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'oat_flour', 'tfc:food/oat_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'oat_dough', 'tfc:food/oat_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'oat_bread', 'tfc:food/oat_bread', Category.bread, 4, 1, 0, 1, grain=1)
    food_item(rm, 'rice', 'tfc:food/rice', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'rice_grain', 'tfc:food/rice_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'rice_flour', 'tfc:food/rice_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'rice_dough', 'tfc:food/rice_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'rice_bread', 'tfc:food/rice_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    food_item(rm, 'cooked_rice', 'tfc:food/cooked_rice', Category.bread, 4, 2, 5, 1, grain=1)
    food_item(rm, 'rye', 'tfc:food/rye', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'rye_grain', 'tfc:food/rye_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'rye_flour', 'tfc:food/rye_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'rye_dough', 'tfc:food/rye_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'rye_bread', 'tfc:food/rye_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    food_item(rm, 'wheat', 'tfc:food/wheat', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'wheat_grain', 'tfc:food/wheat_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'wheat_flour', 'tfc:food/wheat_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'wheat_dough', 'tfc:food/wheat_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'wheat_bread', 'tfc:food/wheat_bread', Category.bread, 4, 1, 0, 1, grain=1)
    food_item(rm, 'beet', 'tfc:food/beet', Category.vegetable, 4, 2, 0, 0.7, veg=1)
    food_item(rm, 'cabbage', 'tfc:food/cabbage', Category.vegetable, 4, 0.5, 0, 1.2, veg=1)
    food_item(rm, 'carrot', 'tfc:food/carrot', Category.vegetable, 4, 2, 0, 0.7, veg=1)
    food_item(rm, 'garlic', 'tfc:food/garlic', Category.vegetable, 4, 0.5, 0, 0.4, veg=2)
    food_item(rm, 'green_bean', 'tfc:food/green_bean', Category.vegetable, 4, 0.5, 0, 3.5, veg=1)
    food_item(rm, 'green_bell_pepper', 'tfc:food/green_bell_pepper', Category.vegetable, 4, 0.5, 0, 2.7, veg=1)
    food_item(rm, 'onion', 'tfc:food/onion', Category.vegetable, 4, 0.5, 0, 0.5, veg=1)
    food_item(rm, 'potato', 'tfc:food/potato', Category.vegetable, 4, 2, 0, 0.666, veg=1.5)
    food_item(rm, 'red_bell_pepper', 'tfc:food/red_bell_pepper', Category.vegetable, 4, 1, 0, 2.5, veg=1)
    food_item(rm, 'dried_seaweed', 'tfc:food/dried_seaweed', Category.vegetable, 2, 1, 0, 2.5, veg=0.5)
    food_item(rm, 'dried_kelp', 'tfc:food/dried_kelp', Category.vegetable, 2, 1, 0, 2.5, veg=0.5)
    food_item(rm, 'cattail_root', 'tfc:food/cattail_root', Category.vegetable, 2, 1, 0, 2.5, grain=0.5)
    food_item(rm, 'taro_root', 'tfc:food/taro_root', Category.vegetable, 2, 1, 0, 2.5, grain=0.5)
    food_item(rm, 'soybean', 'tfc:food/soybean', Category.vegetable, 4, 2, 0, 2.5, veg=0.5, protein=1)
    food_item(rm, 'squash', 'tfc:food/squash', Category.vegetable, 4, 1, 0, 1.67, veg=1.5)
    food_item(rm, 'sugarcane', 'tfc:food/sugarcane', Category.vegetable, 4, 0, 0, 0.5)
    food_item(rm, 'tomato', 'tfc:food/tomato', Category.vegetable, 4, 0.5, 5, 3.5, veg=1.5)
    food_item(rm, 'yellow_bell_pepper', 'tfc:food/yellow_bell_pepper', Category.vegetable, 4, 1, 0, 2.5, veg=1)
    food_item(rm, 'pumpkin', 'tfc:pumpkin', Category.vegetable, 4, 0, 0, 0.5)
    food_item(rm, 'melon', 'tfc:melon', Category.vegetable, 4, 0, 0, 0.5)
    food_item(rm, 'melon_slice', 'minecraft:melon_slice', Category.fruit, 4, 0.2, 5, 2.5, fruit=0.75)
    food_item(rm, 'pumpkin_pie', 'minecraft:pumpkin_pie', Category.fruit, 4, 2, 5, 2.5, fruit=1.5, grain=1)
    food_item(rm, 'cheese', 'tfc:food/cheese', Category.dairy, 4, 2, 0, 0.3, dairy=3)
    food_item(rm, 'cooked_egg', 'tfc:food/cooked_egg', Category.other, 4, 0.5, 0, 4, protein=1.5, dairy=0.25)
    food_item(rm, 'boiled_egg', 'tfc:food/boiled_egg', Category.other, 4, 2, 10, 4, protein=1.5, dairy=0.25)
    # todo: figure out what to do with sugarcane, do we need a different plant? or item or something? or modify the vanilla one
    # food_item(rm, 'sugarcane', 'tfc:food/sugarcane', Category.grain, 4, 0, 0, 1.6, grain=0.5)
    food_item(rm, 'beef', 'tfc:food/beef', Category.meat, 4, 0, 0, 2, protein=2)
    food_item(rm, 'pork', 'tfc:food/pork', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'chicken', 'tfc:food/chicken', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'mutton', 'tfc:food/mutton', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'bluegill', 'tfc:food/bluegill', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'shellfish', 'tfc:food/shellfish', Category.meat, 2, 0, 0, 2, protein=0.5)
    food_item(rm, 'cod', 'tfc:food/cod', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'salmon', 'tfc:food/salmon', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'tropical_fish', 'tfc:food/tropical_fish', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'bear', 'tfc:food/bear', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'calamari', 'tfc:food/calamari', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'horse_meat', 'tfc:food/horse_meat', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'turtle', 'tfc:food/turtle', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'pheasant', 'tfc:food/pheasant', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'venison', 'tfc:food/venison', Category.meat, 4, 0, 0, 2, protein=1)
    food_item(rm, 'wolf', 'tfc:food/wolf', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'rabbit', 'tfc:food/rabbit', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'hyena', 'tfc:food/hyena', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'duck', 'tfc:food/duck', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'quail', 'tfc:food/quail', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'chevon', 'tfc:food/chevon', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'gran_feline', 'tfc:food/gran_feline', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'camelidae', 'tfc:food/camelidae', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_beef', 'tfc:food/cooked_beef', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_pork', 'tfc:food/cooked_pork', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_chicken', 'tfc:food/cooked_chicken', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_mutton', 'tfc:food/cooked_mutton', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_shellfish', 'tfc:food/cooked_shellfish', Category.cooked_meat, 2, 2, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_cod', 'tfc:food/cooked_cod', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_tropical_fish', 'tfc:food/cooked_tropical_fish', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_salmon', 'tfc:food/cooked_salmon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_bluegill', 'tfc:food/cooked_bluegill', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_bear', 'tfc:food/cooked_bear', Category.cooked_meat, 4, 1, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_calamari', 'tfc:food/cooked_calamari', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_horse_meat', 'tfc:food/cooked_horse_meat', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_turtle', 'tfc:food/cooked_turtle', Category.meat, 4, 0, 0, 2, protein=2.5)
    food_item(rm, 'cooked_pheasant', 'tfc:food/cooked_pheasant', Category.cooked_meat, 4, 1, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_venison', 'tfc:food/cooked_venison', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_wolf', 'tfc:food/cooked_wolf', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_rabbit', 'tfc:food/cooked_rabbit', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_hyena', 'tfc:food/cooked_hyena', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_duck', 'tfc:food/cooked_duck', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_quail', 'tfc:food/cooked_quail', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_chevon', 'tfc:food/cooked_chevon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_gran_feline', 'tfc:food/cooked_gran_feline', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_camelidae', 'tfc:food/cooked_camelidae', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    # Always Rotten
    food_item(rm, 'rotten_flesh', 'minecraft:rotten_flesh', Category.other, 0, 0, 0, 99999)
    food_item(rm, 'rotten_pumpkin', 'tfc:rotten_pumpkin', Category.other, 0, 0, 0, 99999)
    food_item(rm, 'rotten_melon', 'tfc:rotten_melon', Category.other, 0, 0, 0, 99999)

    # Drinkables

    drinkable(rm, 'fresh_water', ['minecraft:water', 'tfc:river_water'], thirst=10)
    drinkable(rm, 'salt_water', 'tfc:salt_water', thirst=-1, effects=[{'type': 'tfc:thirst', 'duration': 600, 'chance': 0.25}])
    drinkable(rm, 'alcohol', '#tfc:alcohols', thirst=10, intoxication=4000)
    drinkable(rm, 'milk', '#tfc:milks', thirst=10, food={'category': 'dairy', 'hunger': 0, 'saturation': 0, 'dairy': 1.0})

    # Climate Ranges

    for berry, data in BERRIES.items():
        climate_range(rm, 'plant/%s_bush' % berry, temperature=(data.min_temp, data.max_temp, 0), hydration=(hydration_from_rainfall(data.min_rain), 100, 0))

    for fruit, data in FRUITS.items():
        climate_range(rm, 'plant/%s_tree' % fruit, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp - 7, data.max_temp + 7, 0))

    for crop, data in CROPS.items():
        climate_range(rm, 'crop/%s' % crop, hydration=(data.min_hydration, data.max_hydration, 0), temperature=(data.min_temp, data.max_temp, 5))

    # Fertilizer
    rm.data(('tfc', 'fertilizers', 'sylvite'), fertilizer('tfc:powder/sylvite', p=0.5))
    rm.data(('tfc', 'fertilizers', 'wood_ash'), fertilizer('tfc:powder/wood_ash', p=0.1, k=0.3))
    rm.data(('tfc', 'fertilizers', 'guano'), fertilizer('tfc:groundcover/guano', n=0.8, p=0.5, k=0.1))
    rm.data(('tfc', 'fertilizers', 'saltpeter'), fertilizer('tfc:powder/saltpeter', n=0.1, k=0.4))
    rm.data(('tfc', 'fertilizers', 'bone_meal'), fertilizer('minecraft:bone_meal', p=0.1))
    rm.data(('tfc', 'fertilizers', 'compost'), fertilizer('tfc:compost', n=0.4, p=0.2, k=0.4))
    rm.data(('tfc', 'fertilizers', 'pure_nitrogen'), fertilizer('tfc:pure_nitrogen', n=0.1))
    rm.data(('tfc', 'fertilizers', 'pure_phosphorus'), fertilizer('tfc:pure_phosphorus', p=0.1))
    rm.data(('tfc', 'fertilizers', 'pure_potassium'), fertilizer('tfc:pure_potassium', k=0.1))

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna(distance_below_sea_level=20, climate=climate_config(max_temp=14)))
    rm.data(('tfc', 'fauna', 'crayfish'), fauna(distance_below_sea_level=5, climate=climate_config(min_temp=5, min_rain=125)))
    rm.data(('tfc', 'fauna', 'lobster'), fauna(distance_below_sea_level=1, climate=climate_config(max_temp=21)))
    rm.data(('tfc', 'fauna', 'horseshoe_crab'), fauna(distance_below_sea_level=10, climate=climate_config(min_temp=10, max_temp=21, max_rain=400)))
    rm.data(('tfc', 'fauna', 'cod'), fauna(climate=climate_config(max_temp=18), distance_below_sea_level=5))
    rm.data(('tfc', 'fauna', 'pufferfish'), fauna(climate=climate_config(min_temp=10), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'tropical_fish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'jellyfish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'orca'), fauna(distance_below_sea_level=35, climate=climate_config(max_temp=19, min_rain=100), chance=10))
    rm.data(('tfc', 'fauna', 'dolphin'), fauna(distance_below_sea_level=20, climate=climate_config(min_temp=10, min_rain=200), chance=10))
    rm.data(('tfc', 'fauna', 'manatee'), fauna(distance_below_sea_level=3, climate=climate_config(min_temp=20, min_rain=300), chance=10))
    rm.data(('tfc', 'fauna', 'salmon'), fauna(climate=climate_config(min_temp=-5)))
    rm.data(('tfc', 'fauna', 'bluegill'), fauna(climate=climate_config(min_temp=-10, max_temp=26)))
    rm.data(('tfc', 'fauna', 'penguin'), fauna(climate=climate_config(max_temp=-14, min_rain=75)))
    rm.data(('tfc', 'fauna', 'turtle'), fauna(climate=climate_config(min_temp=21, min_rain=250)))
    rm.data(('tfc', 'fauna', 'polar_bear'), fauna(climate=climate_config(max_temp=-10, min_rain=100)))
    rm.data(('tfc', 'fauna', 'grizzly_bear'), fauna(climate=climate_config(min_forest='edge', max_temp=15, min_temp=-15, min_rain=200)))
    rm.data(('tfc', 'fauna', 'black_bear'), fauna(climate=climate_config(min_forest='edge', max_temp=20, min_temp=5, min_rain=250)))
    rm.data(('tfc', 'fauna', 'cougar'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'panther'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'lion'), fauna(climate=climate_config(max_forest='edge', min_temp=16, min_rain=50, max_rain=300)))
    rm.data(('tfc', 'fauna', 'sabertooth'), fauna(climate=climate_config(max_temp=0, min_rain=250)))
    rm.data(('tfc', 'fauna', 'squid'), fauna(distance_below_sea_level=15))
    rm.data(('tfc', 'fauna', 'octopoteuthis'), fauna(max_brightness=0, distance_below_sea_level=33))
    rm.data(('tfc', 'fauna', 'pig'), fauna(climate=climate_config(min_temp=-10, max_temp=35, min_rain=200, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'cow'), fauna(climate=climate_config(min_temp=-10, max_temp=35, min_rain=250)))
    rm.data(('tfc', 'fauna', 'goat'), fauna(climate=climate_config(min_temp=-12, max_temp=25, max_rain=300)))
    rm.data(('tfc', 'fauna', 'yak'), fauna(climate=climate_config(max_temp=-11, min_rain=100)))
    rm.data(('tfc', 'fauna', 'alpaca'), fauna(climate=climate_config(min_temp=-8, max_temp=20, min_rain=250)))
    rm.data(('tfc', 'fauna', 'sheep'), fauna(climate=climate_config(min_temp=0, max_temp=35, min_rain=70, max_rain=300)))
    rm.data(('tfc', 'fauna', 'musk_ox'), fauna(climate=climate_config(min_temp=0, max_temp=25, min_rain=100)))
    rm.data(('tfc', 'fauna', 'chicken'), fauna(climate=climate_config(min_temp=14, min_rain=225, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'duck'), fauna(climate=climate_config(min_temp=-25, max_temp=30, min_rain=100, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'quail'), fauna(climate=climate_config(min_temp=-15, max_temp=10, min_rain=200)))
    rm.data(('tfc', 'fauna', 'rabbit'), fauna(climate=climate_config(min_rain=15)))
    rm.data(('tfc', 'fauna', 'fox'), fauna(climate=climate_config(min_rain=130, max_rain=400, max_temp=25, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'boar'), fauna(climate=climate_config(min_rain=130, max_rain=400, max_temp=25, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'donkey'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'mule'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'horse'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))

    # Lamp Fuel - burn rate = ticks / mB. 8000 ticks @ 250mB ~ 83 days ~ the 1.12 length of olive oil burning
    rm.data(('tfc', 'lamp_fuels', 'olive_oil'), lamp_fuel('tfc:olive_oil', 8000))
    rm.data(('tfc', 'lamp_fuels', 'tallow'), lamp_fuel('tfc:tallow', 1800))
    rm.data(('tfc', 'lamp_fuels', 'lava'), lamp_fuel('minecraft:lava', -1, 'tfc:metal/lamp/blue_steel'))

    # Misc Block Loot
    rm.block_loot('minecraft:glass', {'name': 'tfc:glass_shard', 'conditions': [loot_invert(loot_tables.silk_touch())]}, {'name': 'minecraft:glass', 'conditions': [loot_tables.silk_touch()]})

    # Entity Damage Resistance
    rm.entity_tag('skeletons', 'minecraft:skeleton', 'minecraft:wither_skeleton', 'minecraft:stray')
    rm.entity_tag('creepers', 'minecraft:creeper')
    rm.entity_tag('zombies', 'minecraft:zombie', 'minecraft:husk', 'minecraft:zombie_villager')

    entity_damage_resistance(rm, 'skeletons', 'tfc:skeletons', piercing=1000000000, crushing=-50)
    entity_damage_resistance(rm, 'creeper', 'tfc:creepers', slashing=-25, crushing=50)
    entity_damage_resistance(rm, 'zombies', 'tfc:zombies', piercing=-25, crushing=50)

    # Entity Loot

    for mob in ('cod', 'bluegill', 'tropical_fish', 'salmon'):
        mob_loot(rm, mob, 'tfc:food/%s' % mob)
    mob_loot(rm, 'pufferfish', 'minecraft:pufferfish')
    mob_loot(rm, 'squid', 'minecraft:ink_sac', max_amount=10, extra_pool={'name': 'tfc:food/calamari'})
    mob_loot(rm, 'octopoteuthis', 'minecraft:glow_ink_sac', max_amount=10, extra_pool={'name': 'tfc:food/calamari'})
    for mob in ('isopod', 'lobster', 'horseshoe_crab', 'crayfish'):
        mob_loot(rm, mob, 'tfc:food/shellfish')
    for mob in ('orca', 'dolphin', 'manatee'):
        mob_loot(rm, mob, 'tfc:blubber', min_amount=0, max_amount=2, bones=4)
    mob_loot(rm, 'penguin', 'minecraft:feather', max_amount=3, hide_size='small', hide_chance=0.5, bones=2)
    mob_loot(rm, 'turtle', 'minecraft:scute')
    mob_loot(rm, 'polar_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'grizzly_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'black_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'cougar', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'panther', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'lion', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'sabertooth', 'tfc:large_raw_hide', bones=8)
    mob_loot(rm, 'pig', 'tfc:food/pork', 4, 10, 'medium', bones=3)
    mob_loot(rm, 'cow', 'tfc:food/beef', 6, 12, 'large', bones=4)
    mob_loot(rm, 'goat', 'tfc:food/chevon', 4, 10, 'medium', bones=4)
    mob_loot(rm, 'yak', 'tfc:food/chevon', 8, 16, 'large', bones=4)
    mob_loot(rm, 'alpaca', 'tfc:food/camelidae', 6, 10, 'medium', bones=4, extra_pool={'name': 'tfc:wool'})
    mob_loot(rm, 'sheep', 'tfc:food/mutton', 4, 10, 'medium', bones=4, extra_pool={'name': 'tfc:wool'})
    mob_loot(rm, 'musk_ox', 'tfc:food/mutton', 6, 14, 'large', bones=4, extra_pool={'name': 'tfc:wool'})
    mob_loot(rm, 'chicken', 'tfc:food/chicken', 2, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(1, 4)]})
    mob_loot(rm, 'duck', 'tfc:food/duck', 2, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(1, 4)]})
    mob_loot(rm, 'quail', 'tfc:food/quail', 1, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(1, 4)]})
    mob_loot(rm, 'rabbit', 'tfc:food/rabbit', hide_size='small', hide_chance=0.5, bones=1, extra_pool={'name': 'minecraft:rabbit_foot', 'conditions': [loot_tables.random_chance(0.1)]})
    mob_loot(rm, 'fox', 'tfc:small_raw_hide', bones=2)
    mob_loot(rm, 'boar', 'tfc:food/pork', 1, 3, 'small', hide_chance=0.8, bones=3)
    mob_loot(rm, 'donkey', 'tfc:food/horse_meat', 4, 10, 'medium', bones=6)
    mob_loot(rm, 'mule', 'tfc:food/horse_meat', 4, 10, 'medium', bones=6)
    mob_loot(rm, 'horse', 'tfc:food/horse_meat', 4, 10, 'medium', bones=6)
    mob_loot(rm, 'minecraft:zombie', 'minecraft:rotten_flesh', 0, 2)  # it drops vanilla stuff we do not want


def entity_damage_resistance(rm: ResourceManager, name_parts: ResourceIdentifier, entity_tag: str, piercing: int = 0, slashing: int = 0, crushing: int = 0):
    rm.data(('tfc', 'entity_damage_resistances', name_parts), {
        'entity': entity_tag,
        'piercing': piercing,
        'slashing': slashing,
        'crushing': crushing
    })

def mob_loot(rm: ResourceManager, name: str, drop: str, min_amount: int = 1, max_amount: int = None, hide_size: str = None, hide_chance: float = 1, bones: int = 0, extra_pool: Dict[str, Any] = None):
    func = None if max_amount is None else loot_tables.set_count(min_amount, max_amount)
    pools = [{'name': drop, 'functions': func}]
    if hide_size is not None:
        func = None if hide_chance == 1 else loot_tables.random_chance(hide_chance)
        pools.append({'name': 'tfc:%s_raw_hide' % hide_size, 'conditions': func})
    if bones != 0:
        pools.append({'name': 'minecraft:bone', 'functions': loot_tables.set_count(1, bones)})
    if extra_pool is not None:
        pools.append(extra_pool)
    rm.entity_loot(name, *pools)

def loot_invert(condition: utils.JsonObject):
    return {
        'condition': 'minecraft:inverted',
        'term': condition
    }

def lamp_fuel(fluid: str, burn_rate: int, valid_lamps: str = '#tfc:lamps'):
    return {
        'fluid': fluid,
        'burn_rate': burn_rate,
        'valid_lamps': {'type': 'tfc:tag', 'tag': valid_lamps.replace('#', '')} if '#' in valid_lamps else valid_lamps
    }

def fertilizer(ingredient: str, n: float = None, p: float = None, k: float = None):
    return {
        'ingredient': utils.ingredient(ingredient),
        'nitrogen': n,
        'potassium': k,
        'phosphorus': p
    }


def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None, min_forest: Optional[str] = None, max_forest: Optional[str] = None) -> Dict[str, Any]:
    return {
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'min_forest': 'normal' if needs_forest else min_forest,
        'max_forest': max_forest,
        'fuzzy': fuzzy
    }


def fauna(chance: int = None, distance_below_sea_level: int = None, climate: Dict[str, Any] = None, solid_ground: bool = None, max_brightness: int = None) -> Dict[str, Any]:
    return {
        'chance': chance,
        'distance_below_sea_level': distance_below_sea_level,
        'climate': climate,
        'solid_ground': solid_ground,
        'max_brightness': max_brightness
    }


def food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category, hunger: int, saturation: float, water: int, decay: float, fruit: Optional[float] = None, veg: Optional[float] = None, protein: Optional[float] = None, grain: Optional[float] = None, dairy: Optional[float] = None):
    rm.item_tag('tfc:foods', ingredient)
    rm.data(('tfc', 'food_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'category': category.name,
        'hunger': hunger,
        'saturation': saturation,
        'water': water if water != 0 else None,
        'decay_modifier': decay,
        'fruit': fruit,
        'vegetables': veg,
        'protein': protein,
        'grain': grain,
        'dairy': dairy
    })
    rm.item_tag('foods', ingredient)
    if category in (Category.fruit, Category.vegetable):
        rm.item_tag('foods/%ss' % category.name.lower(), ingredient)
    if category in (Category.meat, Category.cooked_meat):
        rm.item_tag('foods/meats', ingredient)
        if category == Category.cooked_meat:
            rm.item_tag('foods/cooked_meats', ingredient)
        else:
            rm.item_tag('foods/raw_meats', ingredient)
    if category == Category.dairy:
        rm.item_tag('foods/dairy', ingredient)


def drinkable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, fluid: utils.Json, thirst: Optional[int] = None, intoxication: Optional[int] = None, effects: Optional[utils.Json] = None, food: Optional[utils.Json] = None):
    rm.data(('tfc', 'drinkables', name_parts), {
        'ingredient': fluid_ingredient(fluid),
        'thirst': thirst,
        'intoxication': intoxication,
        'effects': effects,
        'food': food
    })


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })


def item_heat(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[float] = None):
    if melt_temperature is not None:
        forging_temperature = melt_temperature * 0.6
        welding_temperature = melt_temperature * 0.8
    else:
        forging_temperature = welding_temperature = None
    rm.data(('tfc', 'item_heats', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'heat_capacity': heat_capacity,
        'forging_temperature': forging_temperature,
        'welding_temperature': welding_temperature
    })


def fuel_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, duration: int, temperature: float):
    rm.data(('tfc', 'fuels', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'duration': duration,
        'temperature': temperature
    })


def climate_range(rm: ResourceManager, name_parts: utils.ResourceIdentifier, hydration: Tuple[int, int, int] = None, temperature: Tuple[float, float, float] = None):
    data = {}
    if hydration is not None:
        data.update({'min_hydration': hydration[0], 'max_hydration': hydration[1], 'hydration_wiggle_range': hydration[2]})
    if temperature is not None:
        data.update({'min_temperature': temperature[0], 'max_temperature': temperature[1], 'temperature_wiggle_range': temperature[2]})
    rm.data(('tfc', 'climate_ranges', name_parts), data)


def hydration_from_rainfall(rainfall: int) -> int:
    return rainfall * 60 // 500


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)

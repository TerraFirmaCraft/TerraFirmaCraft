#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum, auto

from mcresources import ResourceManager, utils, loot_tables
from recipes import fluid_ingredient
from constants import *


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
            'heat_capacity': metal_data.heat_capacity
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
    rm.entity_tag('spawns_on_cold_blocks', 'tfc:penguin', 'minecraft:polar_bear')
    rm.entity_tag('destroys_floating_plants', 'minecraft:boat', *['tfc:boat/%s' % wood for wood in WOODS.keys()])

    # Item Heats

    item_heat(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', 0.35, 1535)
    item_heat(rm, 'stick', '#forge:rods/wooden', 0.3)
    item_heat(rm, 'stick_bunch', 'tfc:stick_bunch', 0.05)
    item_heat(rm, 'glass_shard', 'tfc:glass_shard', 1)
    item_heat(rm, 'sand', '#forge:sand', 0.8)
    item_heat(rm, 'ceramic_unfired_brick', 'tfc:ceramic/unfired_brick', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_jug', 'tfc:ceramic/unfired_jug', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_pan', 'tfc:ceramic/unfired_pan', POTTERY_HC)
    item_heat(rm, 'terracotta', ['minecraft:terracotta', *['minecraft:%s_terracotta' % color for color in COLORS]], 0.8)
    item_heat(rm, 'dough', ['tfc:food/%s_dough' % grain for grain in GRAINS], 1)
    item_heat(rm, 'meat', ['tfc:food/%s' % meat for meat in MEATS], 1)
    item_heat(rm, 'edible_plants', ['tfc:plant/%s' % plant for plant in SEAWEED] + ['tfc:plant/giant_kelp_flower', 'tfc:groundcover/seaweed'], 1)

    for pottery in SIMPLE_POTTERY:
        item_heat(rm, 'unfired_' + pottery, 'tfc:ceramic/unfired_' + pottery, POTTERY_HC)

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
        fuel_item(rm, wood + '_log', ['tfc:wood/log/' + wood, 'tfc:wood/wood/' + wood], wood_data.duration, wood_data.temp)

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
    rm.item_tag('extinguisher', '#tfc:shovels')
    rm.item_tag('forge:shears', '#tfc:shears')  # forge tag includes TFC shears
    rm.item_tag('minecraft:coals', 'tfc:ore/bituminous_coal', 'tfc:ore/lignite')
    rm.item_tag('forge_fuel', '#minecraft:coals')
    rm.item_tag('firepit_fuel', '#minecraft:logs', 'tfc:peat', 'tfc:peat_grass', 'tfc:stick_bundle')
    rm.item_tag('bloomery_fuel', 'minecraft:charcoal')
    rm.item_tag('log_pile_logs', 'tfc:stick_bundle')
    rm.item_tag('pit_kiln_straw', 'tfc:straw')
    rm.item_tag('firepit_logs', '#minecraft:logs')
    rm.item_tag('log_pile_logs', '#minecraft:logs')
    rm.item_tag('pit_kiln_logs', '#minecraft:logs')
    rm.item_tag('can_be_lit_on_torch', '#forge:rods/wooden')
    rm.item_tag('wattle_sticks', 'tfc:stick_bunch')
    rm.item_tag('mortar', 'tfc:mortar')
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
    rm.item_tag('minecraft:fishes', 'tfc:food/cod', 'tfc:food/cooked_cod', 'tfc:food/salmon', 'tfc:food/cooked_salmon', 'tfc:food/tropical_fish', 'tfc:food/cooked_tropical_fish', 'tfc:food/bluegill', 'tfc:food/cooked_bluegill')

    rm.item_tag('pig_food', '#tfc:foods')
    rm.item_tag('cow_food', '#tfc:foods/grains')
    rm.item_tag('chicken_food', '#tfc:foods/grains', '#tfc:foods/fruits', '#tfc:foods/vegetables') # todo : seeds
    rm.item_tag('alpaca_food', '#tfc:foods/grains', '#tfc:foods/fruits')

    rm.item_tag('tfc:foods/grains', *['tfc:food/%s_grain' % grain for grain in GRAINS])
    rm.item_tag('tfc:compost_greens', '#tfc:plants', *['tfc:food/%s' % v for v in VEGETABLES], *['tfc:food/%s' % m for m in FRUITS], *['tfc:food/%s_bread' % grain for grain in GRAINS])
    rm.item_tag('tfc:compost_browns', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass', 'tfc:groundcover/driftwood', 'tfc:groundcover/pinecone', 'minecraft:paper')
    rm.item_tag('tfc:compost_poisons', *['tfc:food/%s' % m for m in MEATS], *['tfc:food/cooked_%s' % m for m in MEATS], 'minecraft:bone')
    rm.item_tag('fluxstone', 'tfc:shell', 'tfc:groundcover/mollusk', 'tfc:groundcover/clam')
    rm.item_tag('minecraft:arrows', 'tfc:glow_arrow')
    rm.item_tag('foods/apples', 'tfc:food/green_apple', 'tfc:food/red_apple')
    rm.item_tag('foods/usable_in_soup', '#tfc:foods/vegetables', '#tfc:foods/fruits', '#tfc:foods/meats')
    rm.item_tag('soup_bowl', 'tfc:ceramic/bowl')

    for color in COLORS:
        rm.item_tag('vessels', 'tfc:ceramic/unfired_vessel', 'tfc:ceramic/vessel', 'tfc:ceramic/%s_unfired_vessel' % color, 'tfc:ceramic/%s_glazed_vessel' % color)
        rm.item_tag('dyes', 'minecraft:%s_dye' % color)

        if color != 'white':
            for variant in VANILLA_DYED_ITEMS:
                rm.item_tag('colored_%s' % variant, 'minecraft:%s_%s' % (color, variant))
            for variant in ('raw_alabaster', 'alabaster_bricks', 'polished_alabaster'):
                rm.item_tag('colored_%s' % variant, 'tfc:alabaster/stained/%s_%s' % (color, variant))
        rm.item_tag('colored_shulker_boxes', 'minecraft:%s_shulker_box' % color)
        rm.item_tag('colored_concrete_powder', 'minecraft:%s_concrete_powder' % color)
    for gem in GEMS:
        rm.item_tag('forge:gems', 'tfc:gem/' + gem)

    for wood in WOODS.keys():
        rm.item_tag('minecraft:logs', 'tfc:wood/log/%s' % wood, 'tfc:wood/wood/%s' % wood, 'tfc:wood/stripped_log/%s' % wood, 'tfc:wood/stripped_wood/%s' % wood)
        rm.item_tag('twigs', 'tfc:wood/twig/%s' % wood)
        rm.item_tag('lumber', 'tfc:wood/lumber/%s' % wood)
        rm.item_tag('sluices', 'tfc:wood/sluice/%s' % wood)
        rm.item_tag('looms', 'tfc:wood/planks/%s_loom' % wood)
        if wood in TANNIN_WOOD_TYPES:
            rm.item_tag('makes_tannin', 'tfc:wood/log/%s' % wood, 'tfc:wood/wood/%s' % wood)

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

    # ==========
    # BLOCK TAGS
    # ==========

    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#minecraft:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:dirt_path', *['tfc:grass_path/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:farmland/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#minecraft:dirt', '#tfc:grass')
    rm.block_tag('small_spike', 'tfc:calcite')
    rm.block_tag('sea_bush_plantable_on', '#minecraft:dirt', '#minecraft:sand', '#forge:gravel')
    rm.block_tag('creeping_plantable_on', 'minecraft:grass_block', '#tfc:grass', '#minecraft:base_stone_overworld', '#minecraft:logs')
    rm.block_tag('minecraft:bamboo_plantable_on', '#tfc:grass')
    rm.block_tag('minecraft:climbable', 'tfc:plant/hanging_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/liana', 'tfc:plant/liana_plant')
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
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')
    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')
    rm.block_tag('tfc:forge_insulation', '#forge:stone', '#forge:cobblestone', '#forge:stone_bricks', '#forge:smooth_stone')
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])  # Valid spawn tag - grass, sand, or raw rock
    block_and_item_tag(rm, 'minecraft:dirt', *['tfc:dirt/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:rooted_dirt/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('minecraft:geode_invalid_blocks', 'tfc:sea_ice', 'tfc:fluid/salt_water', 'tfc:fluid/river_water', 'tfc:fluid/spring_water')
    rm.block_tag('wild_crop_grows_on', '#tfc:bush_plantable_on')
    rm.block_tag('plants', *['tfc:wild_crop/%s' % crop for crop in CROPS.keys()])
    rm.block_tag('single_block_replaceable', 'tfc:groundcover/humus', 'tfc:groundcover/dead_grass')
    rm.item_tag('usable_on_tool_rack', 'tfc:firestarter', 'minecraft:bow', 'minecraft:crossbow', 'minecraft:flint_and_steel')

    for ore, ore_data in ORES.items():
        for rock in ROCKS.keys():
            if ore_data.graded:
                for grade in ORE_GRADES.keys():
                    rm.block_tag('prospectable', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
                    rm.block('tfc:ore/%s_%s/%s/prospected' % (grade, ore, rock)).with_lang(lang(ore))
            else:
                rm.block_tag('prospectable', 'tfc:ore/%s/%s' % (ore, rock))
                rm.block('tfc:ore/%s/%s/prospected' % (ore, rock)).with_lang(lang(ore))

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
        block_and_item_tag(rm, 'forge:cobblestone', block('cobble'), block('mossy_cobble'))
        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        block_and_item_tag(rm, 'forge:stone_bricks', block('bricks'), block('mossy_bricks'), block('cracked_bricks'))
        block_and_item_tag(rm, 'forge:smooth_stone', block('smooth'))
        rm.block_tag('tfc:breaks_when_isolated', block('raw'))
        block_and_item_tag(rm, 'minecraft:stone_pressure_plates', block('pressure_plate'))
        block_and_item_tag(rm, 'forge:smooth_stone_slab', 'tfc:rock/smooth/%s_slab' % rock)
        rm.item_tag('tfc:rock_knapping', block('loose'))
        rm.item_tag('tfc:%s_rock' % rock_data.category, block('loose'))

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
        rm.block_tag('can_carve', 'tfc:dirt/%s' % soil, 'tfc:grass/%s' % soil)

    # Soil / Standard blocks are toughness 0 - dirt destroys charcoal
    rm.block_tag('toughness_1', 'tfc:charcoal_pile', 'tfc:charcoal_forge')  # Charcoal is toughness 1 - resistant against destruction from soil
    rm.block_tag('toughness_2', *[
        'tfc:rock/%s/%s' % (variant, rock) for variant in ('raw', 'cobble', 'mossy_cobble') for rock in ROCKS.keys()
    ])  # Stone type blocks are toughness 2
    rm.block_tag('toughness_3', 'minecraft:bedrock')  # Used as a top level 'everything goes'

    # Harvest Tool + Level Tags

    rm.block_tag('needs_stone_tool', '#forge:needs_wood_tool')
    rm.block_tag('needs_copper_tool', '#minecraft:needs_stone_tool')
    rm.block_tag('needs_wrought_iron_tool', '#minecraft:needs_iron_tool')
    rm.block_tag('needs_steel_tool', '#minecraft:needs_diamond_tool')
    rm.block_tag('needs_colored_steel_tool', '#forge:needs_netherite_tool')

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
                rm.block_tag('needs_%s_tool' % data.required_tool, 'tfc:ore/poor_%s/%s' % (ore, rock), 'tfc:ore/normal_%s/%s' % (ore, rock), 'tfc:ore/rich_%s/%s' % (ore, rock))
            else:
                rm.block_tag('needs_%s_tool' % data.required_tool, 'tfc:ore/%s/%s' % (ore, rock))

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
        *['tfc:metal/%s/%s' % (variant, metal) for variant, variant_data in METAL_BLOCKS.items() for metal, metal_data in METALS.items() if variant_data.type in metal_data.types],
        *['tfc:coral/%s_%s' % (color, variant) for color in CORALS for variant in CORAL_BLOCKS],
        'tfc:alabaster/raw/alabaster',
        'tfc:alabaster/raw/alabaster_bricks',
        'tfc:alabaster/raw/polished_alabaster',
        *['tfc:alabaster/stained/%s%s' % (color, variant) for color in COLORS for variant in ('_raw_alabaster', '_alabaster_bricks', '_polished_alabaster', '_alabaster_bricks_slab', '_alabaster_bricks_stairs', '_alabaster_bricks_wall', '_polished_alabaster_slab', '_polished_alabaster_stairs', '_polished_alabaster_wall')],
        *['tfc:groundcover/%s' % gc for gc in MISC_GROUNDCOVER],
        'tfc:fire_bricks',
        'tfc:quern',
        'tfc:crucible',
        'tfc:pot',
        'tfc:grill',
        'tfc:firepit'
    ])
    rm.block_tag('minecraft:mineable/axe', *[
        *['tfc:wood/%s/%s' % (variant, wood) for variant in ('log', 'stripped_log', 'wood', 'stripped_wood', 'planks', 'twig', 'vertical_support', 'horizontal_support', 'sluice', 'chest', 'trapped_chest') for wood in WOODS.keys()],
        *['tfc:wood/planks/%s_%s' % (wood, variant) for variant in ('bookshelf', 'door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs', 'tool_rack', 'workbench', 'sign') for wood in WOODS.keys()],
        *['tfc:plant/%s_branch' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:plant/%s_growing_branch' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:wattle/%s' % color for color in COLORS],
        'tfc:wattle',
        'tfc:plant/banana_plant',
        'tfc:plant/dead_banana_plant',
        'tfc:log_pile',
        'tfc:burning_log_pile',
        'tfc:composter',
        'tfc:nest_box'
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

    rm.fluid_tag('fluid_ingredients', 'minecraft:water', 'tfc:salt_water', 'tfc:spring_water', '#tfc:alcohols', '#tfc:dye_fluids', *['tfc:%s' % fluid for fluid in SIMPLE_FLUIDS], '#tfc:milks')
    rm.fluid_tag('drinkables', 'minecraft:water', 'tfc:salt_water', 'tfc:river_water', '#tfc:alcohols', '#tfc:milks')
    rm.fluid_tag('hydrating', 'minecraft:water', 'tfc:river_water', '#tfc:milks')
    rm.fluid_tag('milks', 'minecraft:milk')

    rm.fluid_tag('usable_in_pot', '#tfc:fluid_ingredients')
    rm.fluid_tag('usable_in_jug', '#tfc:drinkables')
    rm.fluid_tag('usable_in_wooden_bucket', '#tfc:fluid_ingredients', '#tfc:drinkables')
    rm.fluid_tag('usable_in_barrel', '#tfc:fluid_ingredients', '#tfc:drinkables')

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
    item_size(rm, 'halter', 'tfc:halter', Size.small, Weight.light)
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
    item_size(rm, 'sluice', '#tfc:sluices', Size.very_large, Weight.very_heavy)
    item_size(rm, 'lamps', '#tfc:lamps', Size.normal, Weight.very_heavy)
    item_size(rm, 'signs', '#minecraft:signs', Size.very_small, Weight.heavy)
    item_size(rm, 'soups', '#tfc:soup_bowls', Size.very_small, Weight.very_heavy)

    # unimplemented
    # item_size(rm, 'bloomery', 'tfc:bloomery', Size.large, Weight.very_heavy)
    # item_size(rm, 'loom', 'tfc:loom', Size.large, Weight.very_heavy)

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
    # todo: figure out what to do with rice. thinking rice -> grain -> cooked rice in a pot recipe? so remove flour/dough/bread for this one
    food_item(rm, 'rice', 'tfc:food/rice', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'rice_grain', 'tfc:food/rice_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'rice_flour', 'tfc:food/rice_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'rice_dough', 'tfc:food/rice_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'rice_bread', 'tfc:food/rice_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
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
    food_item(rm, 'cheese', 'tfc:food/cheese', Category.dairy, 4, 2, 0, 0.3, dairy=3)
    food_item(rm, 'cooked_egg', 'tfc:food/cooked_egg', Category.other, 4, 0.5, 0, 4, protein=0.75, dairy=0.25)
    # todo: figure out what to do with sugarcane, do we need a different plant? or item or something? or modify the vanilla one
    # food_item(rm, 'sugarcane', 'tfc:food/sugarcane', Category.grain, 4, 0, 0, 1.6, grain=0.5)
    food_item(rm, 'beef', 'tfc:food/beef', Category.meat, 4, 0, 0, 2, protein=2)
    food_item(rm, 'pork', 'tfc:food/pork', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'chicken', 'tfc:food/chicken', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'mutton', 'tfc:food/mutton', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'bluegill', 'tfc:food/bluegill', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'cod', 'tfc:food/cod', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'salmon', 'tfc:food/salmon', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'tropical_fish', 'tfc:food/tropical_fish', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'bear', 'tfc:food/bear', Category.meat, 4, 0, 0, 2, protein=1.5)
    # food_item(rm, 'calamari', 'tfc:food/calamari', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'horse_meat', 'tfc:food/horse_meat', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'pheasant', 'tfc:food/pheasant', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'venison', 'tfc:food/venison', Category.meat, 4, 0, 0, 2, protein=1)
    food_item(rm, 'wolf', 'tfc:food/wolf', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'rabbit', 'tfc:food/rabbit', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'hyena', 'tfc:food/hyena', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'duck', 'tfc:food/duck', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'chevon', 'tfc:food/chevon', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'gran_feline', 'tfc:food/gran_feline', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'camelidae', 'tfc:food/camelidae', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_beef', 'tfc:food/cooked_beef', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_pork', 'tfc:food/cooked_pork', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_chicken', 'tfc:food/cooked_chicken', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_mutton', 'tfc:food/cooked_mutton', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_cod', 'tfc:food/cooked_cod', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_tropical_fish', 'tfc:food/cooked_tropical_fish', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_salmon', 'tfc:food/cooked_salmon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_bluegill', 'tfc:food/cooked_bluegill', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_bear', 'tfc:food/cooked_bear', Category.cooked_meat, 4, 1, 0, 1.5, protein=2.5)
    # food_item(rm, 'cooked_calamari', 'tfc:food/cooked_calamari', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_horse_meat', 'tfc:food/cooked_horse_meat', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_pheasant', 'tfc:food/cooked_pheasant', Category.cooked_meat, 4, 1, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_venison', 'tfc:food/cooked_venison', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_wolf', 'tfc:food/cooked_wolf', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_rabbit', 'tfc:food/cooked_rabbit', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_hyena', 'tfc:food/cooked_hyena', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_duck', 'tfc:food/cooked_duck', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_chevon', 'tfc:food/cooked_chevon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_gran_feline', 'tfc:food/cooked_gran_feline', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_camelidae', 'tfc:food/cooked_camelidae', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)

    # Drinkables

    drinkable(rm, 'fresh_water', ['minecraft:water', 'tfc:river_water'], thirst=10)
    drinkable(rm, 'salt_water', 'tfc:salt_water', thirst=-1)
    drinkable(rm, 'alcohol', '#tfc:alcohols', thirst=10, intoxication=1000)
    drinkable(rm, 'milk', '#tfc:milks', thirst=10)

    # Climate Ranges

    for berry, data in BERRIES.items():
        climate_range(rm, 'plant/%s_bush' % berry, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp, data.max_temp, 0))
    for fruit, data in FRUITS.items():
        climate_range(rm, 'plant/%s_tree' % fruit, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp - 7, data.max_temp + 7, 0))

    # Crops
    for crop, data in CROPS.items():
        # todo: values
        climate_range(rm, 'crop/%s' % crop, hydration=(40, 100, 30), temperature=(5, 25, 5))

    # Fertilizer
    rm.data(('tfc', 'fertilizers', 'sylvite'), fertilizer('tfc:powder/sylvite', p=0.5))
    rm.data(('tfc', 'fertilizers', 'wood_ash'), fertilizer('tfc:powder/wood_ash', p=0.1, k=0.3))
    rm.data(('tfc', 'fertilizers', 'guano'), fertilizer('tfc:groundcover/guano', n=0.8, p=0.5, k=0.1))
    rm.data(('tfc', 'fertilizers', 'saltpeter'), fertilizer('tfc:powder/saltpeter', n=0.1, k=0.4))
    rm.data(('tfc', 'fertilizers', 'bone_meal'), fertilizer('minecraft:bone_meal', p=0.1))
    rm.data(('tfc', 'fertilizers', 'compost'), fertilizer('tfc:compost', n=0.4, p=0.2, k=0.4))

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna(distance_below_sea_level=20, climate=climate_config(max_temp=14)))
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
    rm.data(('tfc', 'fauna', 'cougar'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'panther'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'lion'), fauna(climate=climate_config(min_temp=16, max_temp=40, min_rain=80, max_rain=300)))
    rm.data(('tfc', 'fauna', 'sabertooth'), fauna(climate=climate_config(max_temp=0, min_rain=250)))
    rm.data(('tfc', 'fauna', 'squid'), fauna(distance_below_sea_level=15))
    rm.data(('tfc', 'fauna', 'octopoteuthis'), fauna(max_brightness=0, distance_below_sea_level=33))
    rm.data(('tfc', 'fauna', 'pig'), fauna(climate=climate_config(min_temp=0, max_temp=25, min_rain=100)))
    rm.data(('tfc', 'fauna', 'cow'), fauna(climate=climate_config(min_temp=0, max_temp=25, min_rain=100)))
    rm.data(('tfc', 'fauna', 'alpaca'), fauna(climate=climate_config(min_temp=0, max_temp=25, min_rain=100)))
    rm.data(('tfc', 'fauna', 'chicken'), fauna(climate=climate_config(min_temp=0, max_temp=25, min_rain=100)))

    # Lamp Fuel - burn rate = ticks / mB. 8000 ticks @ 250mB ~ 83 days ~ the 1.12 length of olive oil burning
    rm.data(('tfc', 'lamp_fuels', 'olive_oil'), lamp_fuel('tfc:olive_oil', 8000))
    rm.data(('tfc', 'lamp_fuels', 'tallow'), lamp_fuel('tfc:tallow', 1800))
    rm.data(('tfc', 'lamp_fuels', 'lava'), lamp_fuel('minecraft:lava', -1, 'tfc:metal/lamp/blue_steel'))

    for mob in ('cod', 'bluegill', 'tropical_fish', 'salmon'):
        mob_loot(rm, mob, 'tfc:food/%s' % mob)
    mob_loot(rm, 'pufferfish', 'minecraft:pufferfish')
    mob_loot(rm, 'squid', 'minecraft:ink_sac', max_amount=3)
    mob_loot(rm, 'octopoteuthis', 'minecraft:glow_ink_sac', max_amount=3)
    for mob in ('isopod', 'lobster', 'horseshoe_crab', 'crayfish'):
        mob_loot(rm, mob, 'tfc:shell')
    for mob in ('orca', 'dolphin', 'manatee'):
        mob_loot(rm, mob, 'tfc:blubber', min_amount=0, max_amount=2, bones=4)
    mob_loot(rm, 'penguin', 'minecraft:feather', max_amount=3, hide_size='small', hide_chance=0.5, bones=2)
    mob_loot(rm, 'turtle', 'minecraft:scute')
    mob_loot(rm, 'polar_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'cougar', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'panther', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'lion', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'sabertooth', 'tfc:large_raw_hide', bones=8)
    mob_loot(rm, 'pig', 'tfc:food/pork', 1, 4, 'medium', bones=3)
    mob_loot(rm, 'cow', 'tfc:food/beef', 1, 4, 'large', bones=4)
    mob_loot(rm, 'alpaca', 'tfc:food/camelidae', 1, 4, 'medium', bones=4, extra_pool={'name': 'tfc:wool'})
    mob_loot(rm, 'chicken', 'tfc:food/chicken', extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(1, 4)]})

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
        'potassium': p,
        'phosphorus': k
    }

def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None) -> Dict[str, Any]:
    return {
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'max_forest': 'normal' if needs_forest else None,
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
        'decay': decay,
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


def drinkable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, fluid: utils.Json, thirst: Optional[int] = None, intoxication: Optional[int] = None):
    rm.data(('tfc', 'drinkables', name_parts), {
        'ingredient': fluid_ingredient(fluid),
        'thirst': thirst,
        'intoxication': intoxication
        # todo: effects
        # todo: milk effects
    })


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })


def item_heat(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[int] = None):
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

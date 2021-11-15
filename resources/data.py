#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum, auto

from mcresources import ResourceManager, utils
from mcresources.utils import item_stack

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
            'heat_capacity': metal_data.heat_capacity
        })

        # Metal Items and Blocks
        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag + '/' + metal, 'tfc:metal/%s/%s' % (item, metal))
                    ingredient = item_stack('tag!%s/%s' % (item_data.tag, metal))
                else:
                    ingredient = item_stack('tfc:metal/%s/%s' % (item, metal))

                item_heat(rm, ('metal', metal + '_' + item), ingredient, metal_data.heat_capacity, metal_data.melt_temperature)
                if 'tool' in metal_data.types and item == 'fishing_rod':
                    rm.item_tag('forge:fishing_rods', 'tfc:metal/%s/%s' % (item, metal))

    for ore, ore_data in ORES.items():
        if ore_data.metal and ore_data.graded:
            metal_data = METALS[ore_data.metal]
            item_heat(rm, ('ore', ore), ['tfc:ore/small_%s' % ore, 'tfc:ore/normal_%s' % ore, 'tfc:ore/poor_%s' % ore, 'tfc:ore/rich_%s' % ore], metal_data.heat_capacity, int(metal_data.melt_temperature))

    # Item Heats

    item_heat(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', 0.35, 1535)
    item_heat(rm, 'stick', 'tag!forge:rods/wooden', 0.3)
    item_heat(rm, 'stick_bunch', 'tfc:stick_bunch', 0.05)
    item_heat(rm, 'glass_shard', 'tfc:glass_shard', 1)
    item_heat(rm, 'sand', 'tag!forge:sand', 0.8)
    item_heat(rm, 'ceramic_unfired_brick', 'tfc:ceramic/unfired_brick', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', POTTERY_HC)
    item_heat(rm, 'ceramic_unfired_jug', 'tfc:ceramic/unfired_jug', POTTERY_HC)
    item_heat(rm, 'terracotta', ['minecraft:terracotta', *['minecraft:%s_terracotta' % color for color in COLORS]], 0.8)
    item_heat(rm, 'dough', ['tfc:food/%s_dough' % grain for grain in GRAINS], 1)

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
    rm.item_tag('mortar', 'tfc:mortar')
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.item_tag('scrapable', 'tfc:large_soaked_hide', 'tfc:medium_soaked_hide', 'tfc:small_soaked_hide')
    rm.item_tag('clay_knapping', 'minecraft:clay_ball')
    rm.item_tag('fire_clay_knapping', 'tfc:fire_clay')
    rm.item_tag('leather_knapping', 'minecraft:leather')
    rm.item_tag('knapping_any', '#tfc:clay_knapping', '#tfc:fire_clay_knapping', '#tfc:leather_knapping', '#tfc:rock_knapping')
    rm.item_tag('forge:gems/diamond', 'tfc:gem/diamond')
    rm.item_tag('forge:gems/lapis', 'tfc:gem/lapis_lazuli')
    rm.item_tag('forge:gems/emerald', 'tfc:gem/emerald')
    rm.item_tag('bush_cutting_tools', '#forge:shears', '#tfc:knives')
    rm.item_tag('minecraft:fishes', 'tfc:food/cod', 'tfc:food/cooked_cod', 'tfc:food/salmon', 'tfc:food/cooked_salmon', 'tfc:food/tropical_fish', 'tfc:food/cooked_tropical_fish', 'tfc:food/bluegill', 'tfc:food/cooked_bluegill')

    for gem in GEMS:
        rm.item_tag('forge:gems', 'tfc:gem/' + gem)

    for wood in WOODS.keys():
        rm.item_tag('minecraft:logs', 'tfc:wood/log/%s' % wood, 'tfc:wood/wood/%s' % wood, 'tfc:wood/stripped_log/%s' % wood, 'tfc:wood/stripped_wood/%s' % wood)
        rm.item_tag('twigs', 'tfc:wood/twig/%s' % wood)
        rm.item_tag('lumber', 'tfc:wood/lumber/%s' % wood)

    for category in ROCK_CATEGORIES:  # Rock (Category) Tools
        for tool in ROCK_CATEGORY_ITEMS:
            rm.item_tag(TOOL_TAGS[tool], 'tfc:stone/%s/%s' % (tool, category))

    for metal, metal_data in METALS.items():  # Metal Tools
        if 'tool' in metal_data.types:
            for tool_type, tool_tag in TOOL_TAGS.items():
                rm.item_tag(tool_tag, 'tfc:metal/%s/%s' % (tool_type, metal))

    # Blocks and Items
    block_and_item_tag(rm, 'forge:sand', '#minecraft:sand')  # Forge doesn't reference the vanilla tag for some reason

    # Sand
    for color in SAND_BLOCK_TYPES:
        block_and_item_tag(rm, 'minecraft:sand', 'tfc:sand/%s' % color)

    for grain in GRAINS:
        rm.item_tag('tfc:animal_temptations', 'tfc:food/%s_grain' % grain)

    # ==========
    # BLOCK TAGS
    # ==========

    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:dirt_path')
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('small_spike', 'tfc:calcite')
    rm.block_tag('sea_bush_plantable_on', '#forge:dirt', '#minecraft:sand', '#forge:gravel')
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
    rm.block_tag('forge:dirt', *['tfc:dirt/%s' % v for v in SOIL_BLOCK_VARIANTS])
    rm.block_tag('prospectable', '#forge:ores')

    for wood in WOODS.keys():
        rm.block_tag('lit_by_dropped_torch', 'tfc:wood/fallen_leaves/' + wood)

    for plant, plant_data in PLANTS.items():  # Plants
        rm.block_tag('plant', 'tfc:plant/%s' % plant)
        if plant_data.type in {'standard', 'short_grass', 'creeping'}:
            rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    # Rocks
    for rock, rock_data in ROCKS.items():
        def block(block_type: str):
            return 'tfc:rock/%s/%s' % (block_type, rock)

        block_and_item_tag(rm, 'forge:gravel', 'tfc:rock/gravel/%s' % rock)
        rm.block_tag('forge:stone', block('raw'), block('hardened'))
        rm.block_tag('forge:cobblestone', block('cobble'), block('mossy_cobble'))
        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        rm.block_tag('forge:stone_bricks', block('bricks'), block('mossy_bricks'), block('cracked_bricks'))
        rm.block_tag('forge:smooth_stone', block('smooth'))
        rm.block_tag('tfc:breaks_when_isolated', block('raw'))
        rm.block_tag('minecraft:stone_pressure_plates', block('pressure_plate'))

        rm.item_tag('forge:smooth_stone', block('smooth'))
        rm.item_tag('forge:smooth_stone_slab', 'tfc:rock/smooth/%s_slab' % rock)
        rm.item_tag('tfc:rock_knapping', block('loose'))
        rm.item_tag('tfc:%s_rock' % rock_data.category, block('loose'))
        rm.item_tag('minecraft:stone_pressure_plates', block('pressure_plate'))

        if rock in ['chalk', 'dolomite', 'limestone', 'marble']:
            rm.item_tag('tfc:fluxstone', block('loose'))

    for plant in PLANTS.keys():
        rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

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

    # Harvest Tool + Level Tags

    rm.block_tag('needs_stone_tool', '#forge:needs_wood_tool')
    rm.block_tag('needs_copper_tool', '#minecraft:needs_stone_tool')
    rm.block_tag('needs_wrought_iron_tool', '#minecraft:needs_iron_tool')
    rm.block_tag('needs_steel_tool', '#minecraft:needs_diamond_tool')
    rm.block_tag('needs_colored_steel_tool', '#forge:needs_netherite_tool')

    rm.block_tag('minecraft:mineable/hoe', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_knife', '#tfc:mineable_with_sharp_tool')
    rm.block_tag('tfc:mineable_with_scythe', '#tfc:mineable_with_sharp_tool')

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
        'tfc:aggregate',
        'tfc:fire_clay_block',
        'tfc:charcoal_pile',
        'tfc:charcoal_forge'
    ])
    rm.block_tag('minecraft:mineable/pickaxe', *[
        *['tfc:%s_sandstone/%s' % (variant, sand) for variant in SANDSTONE_BLOCK_TYPES for sand in SAND_BLOCK_TYPES],
        *['tfc:%s_sandstone/%s_%s' % (variant, sand, suffix) for variant in SANDSTONE_BLOCK_TYPES for sand in SAND_BLOCK_TYPES for suffix in ('slab', 'stairs', 'wall')],
        'tfc:icicle',
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
        'tfc:fire_bricks',
        'tfc:quern'
    ])
    rm.block_tag('minecraft:mineable/axe', *[
        *['tfc:wood/%s/%s' % (variant, wood) for variant in ('log', 'stripped_log', 'wood', 'stripped_wood', 'planks', 'twig', 'vertical_support', 'horizontal_support') for wood in WOODS.keys()],
        *['tfc:wood/planks/%s_%s' % (wood, variant) for variant in ('bookshelf', 'door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs', 'tool_rack', 'workbench') for wood in WOODS.keys()],
        *['tfc:plant/%s_branch' % tree for tree in NORMAL_FRUIT_TREES],
        *['tfc:plant/%s_growing_branch' % tree for tree in NORMAL_FRUIT_TREES],
        'tfc:plant/banana_plant',
        'tfc:log_pile',
        'tfc:burning_log_pile'
    ])
    rm.block_tag('tfc:mineable_with_sharp_tool', *[
        *['tfc:wood/%s/%s' % (variant, wood) for variant in ('leaves', 'sapling', 'fallen_leaves') for wood in WOODS.keys()],
        *['tfc:plant/%s' % plant for plant in PLANTS.keys()],
        *['tfc:plant/%s' % plant for plant in UNIQUE_PLANTS],
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

    # ==========
    # FLUID TAGS
    # ==========

    rm.fluid_tag('fluid_ingredients', 'minecraft:water', 'tfc:salt_water', 'tfc:spring_water')
    rm.fluid_tag('drinkables', 'minecraft:water', 'tfc:salt_water', 'tfc:river_water')
    rm.fluid_tag('hydrating', 'minecraft:water', 'tfc:river_water')

    rm.fluid_tag('usable_in_pot', '#tfc:fluid_ingredients')
    rm.fluid_tag('usable_in_jug', '#tfc:drinkables')

    # Item Sizes

    # todo: specific item size definitions for a whole bunch of items that aren't naturally assigned
    item_size(rm, 'logs', 'tag!minecraft:logs', Size.very_large, Weight.medium)

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
    food_item(rm, 'soybean', 'tfc:food/soybean', Category.vegetable, 4, 2, 0, 2.5, veg=0.5, protein=1)
    food_item(rm, 'squash', 'tfc:food/squash', Category.vegetable, 4, 1, 0, 1.67, veg=1.5)
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

    # Climate Ranges

    for berry, data in BERRIES.items():
        climate_range(rm, 'plant/%s_bush' % berry, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp, data.max_temp, 0))

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna(distance_below_sea_level=20, climate=climate_config(max_temp=14)))
    rm.data(('tfc', 'fauna', 'lobster'), fauna(distance_below_sea_level=20, climate=climate_config(max_temp=21)))
    rm.data(('tfc', 'fauna', 'horseshoe_crab'), fauna(distance_below_sea_level=10, climate=climate_config(min_temp=10, max_temp=21, max_rain=400)))
    rm.data(('tfc', 'fauna', 'cod'), fauna(climate=climate_config(max_temp=18), distance_below_sea_level=5))
    rm.data(('tfc', 'fauna', 'pufferfish'), fauna(climate=climate_config(min_temp=10), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'tropical_fish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'jellyfish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    # rm.data(('tfc', 'fauna', 'orca'), fauna(distance_below_sea_level=35, climate=climate_config(max_temp=19, min_rain=100), chance=10))
    # rm.data(('tfc', 'fauna', 'dolphin'), fauna(distance_below_sea_level=20, climate=climate_config(min_temp=10, min_rain=200), chance=10))
    # rm.data(('tfc', 'fauna', 'manatee'), fauna(distance_below_sea_level=3, climate=climate_config(min_temp=20, min_rain=300), chance=10))
    rm.data(('tfc', 'fauna', 'salmon'), fauna(climate=climate_config(min_temp=-5)))
    rm.data(('tfc', 'fauna', 'bluegill'), fauna(climate=climate_config(min_temp=-10, max_temp=26)))
    # rm.data(('tfc', 'fauna', 'penguin'), fauna(climate=climate_config(max_temp=-14, min_rain=75)))
    # rm.data(('tfc', 'fauna', 'turtle'), fauna(climate=climate_config(min_temp=21, min_rain=250)))

    mob_loot(rm, 'cod', 'tfc:food/cod')
    mob_loot(rm, 'bluegill', 'tfc:food/bluegill')
    mob_loot(rm, 'tropical_fish', 'tfc:food/tropical_fish')
    mob_loot(rm, 'salmon', 'tfc:food/salmon')
    mob_loot(rm, 'pufferfish', 'minecraft:pufferfish')


def mob_loot(rm: ResourceManager, name_parts: utils.ResourceIdentifier, loot_pools: utils.Json):
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'loot_tables', 'entities', res.path), {
        'type': 'minecraft:entity',
        'pools': utils.loot_pool_list(loot_pools, 'entity')
    })


def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None) -> Dict[str, Any]:
    return {
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'max_forest': 'normal' if needs_forest else None,
        'fuzzy': fuzzy
    }


def fauna(chance: int = None, distance_below_sea_level: int = None, climate: Dict[str, Any] = None, solid_ground: bool = None) -> Dict[str, Any]:
    return {
        'chance': chance,
        'distance_below_sea_level': distance_below_sea_level,
        'climate': climate,
        'solid_ground': solid_ground
    }


def food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category, hunger: int, saturation: float, water: int, decay: float, fruit: Optional[float] = None, veg: Optional[float] = None, protein: Optional[float] = None, grain: Optional[float] = None, dairy: Optional[float] = None):
    if type(ingredient) != utils.Json:
        rm.item_tag('tfc:pig_food', ingredient)
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

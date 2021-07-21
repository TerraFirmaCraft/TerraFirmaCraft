#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum, auto

from mcresources import ResourceManager, utils
from mcresources.utils import item_stack

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


def generate(rm: ResourceManager):
    # Metals
    for metal, metal_data in METALS.items():
        # The metal itself
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier,
            'fluid': 'tfc:metal/%s' % metal
        })

        # for each registered metal item
        for item, item_data in {**METAL_ITEMS, **METAL_BLOCKS}.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag + '/' + metal, 'tfc:metal/%s/%s' % (item, metal))
                    ingredient = item_stack('tag!%s/%s' % (item_data.tag, metal))
                else:
                    ingredient = item_stack('tfc:metal/%s/%s' % (item, metal))
                if item == 'shovel':
                    rm.item_tag('extinguisher', 'tfc:metal/shovel/' + metal)
                if item == 'knife':
                    rm.item_tag('knives', 'tfc:metal/knife/' + metal)

                metal_item(rm, ('metal', metal + '_' + item), ingredient, 'tfc:%s' % metal, item_data.smelt_amount)
                heat_item(rm, ('metal', metal + '_' + item), ingredient, metal_data.heat_capacity, metal_data.melt_temperature)

        # Common metal crafting tools
        if 'tool' in metal_data.types:
            for tool in ('hammer', 'chisel', 'axe', 'pickaxe', 'shovel'):
                rm.item_tag('tfc:%ss' % tool, 'tfc:metal/%s/%s' % (tool, metal))
            rm.item_tag('forge:shears', 'tfc:metal/shears/%s' % metal)

    # Misc metal items
    metal_item(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', 'tfc:cast_iron', 100)

    # Item heat definitions
    heat_item(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', 0.35, 1535)
    heat_item(rm, 'stick', 'tag!forge:rods/wooden', 0.3)
    heat_item(rm, 'stick_bunch', 'tfc:stick_bunch', 0.05)
    heat_item(rm, 'glass_shard', 'tfc:glass_shard', 1)
    heat_item(rm, 'sand', 'tag!forge:sand', 0.8)
    heat_item(rm, 'ceramic_unfired_brick', 'tfc:ceramic/unfired_brick', 1)
    heat_item(rm, 'ceramic_unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', 1)
    heat_item(rm, 'ceramic_unfired_jug', 'tfc:ceramic/unfired_jug', 1)
    heat_item(rm, 'terracotta', ['minecraft:terracotta', *['minecraft:%s_terracotta' % color for color in COLORS]], 0.8)

    for pottery in PAIRED_POTTERY:
        heat_item(rm, 'unfired_' + pottery, 'tfc:ceramic/unfired_' + pottery, 1)

    # Rocks
    for rock, rock_data in ROCKS.items():
        rm.data(('tfc', 'rocks', rock), {
            **dict((block_type, 'tfc:rock/%s/%s' % (block_type, rock)) for block_type in ROCK_BLOCKS_IN_JSON),
            'sand': 'tfc:sand/%s' % rock_data.sand,
            'sandstone': 'tfc:raw_sandstone/%s' % rock_data.sand
        })

        def block(block_type: str):
            return 'tfc:rock/%s/%s' % (block_type, rock)

        rm.block_tag('forge:gravel', block('gravel'))
        rm.block_tag('forge:stone', block('raw'), block('hardened'))
        rm.block_tag('forge:cobblestone', block('cobble'), block('mossy_cobble'))
        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        rm.block_tag('tfc:breaks_when_isolated', block('raw'))
        rm.item_tag('tfc:rock_knapping', block('loose'))
        rm.item_tag('tfc:%s_rock' % rock_data.category, block('loose'))

        if rock in ['chalk', 'dolomite', 'limestone', 'marble']:
            rm.item_tag('tfc:fluxstone', block('loose'))

    for category in ROCK_CATEGORIES:
        rm.item_tag('tfc:knives', 'tfc:stone/knife/%s' % category)

    rm.item_tag('tfc:clay_knapping', 'minecraft:clay_ball')
    rm.item_tag('tfc:fire_clay_knapping', 'tfc:fire_clay')
    rm.item_tag('tfc:leather_knapping', 'minecraft:leather')

    # Plants
    for plant, plant_data in PLANTS.items():
        rm.block_tag('plant', 'tfc:plant/%s' % plant)
        if plant_data.type in {'standard', 'short_grass', 'creeping'}:
            rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    # Sand
    for color in SAND_BLOCK_TYPES:
        rm.block_tag('minecraft:sand', 'tfc:sand/%s' % color)

    for wood in WOODS:
        rm.data(('tfc', 'supports', wood), {
            'ingredient': 'tfc:wood/horizontal_support/%s' % wood,
            'support_up': 1,
            'support_down': 1,
            'support_horizontal': 4
        })

    # Forge you dingus, use vanilla tags
    rm.block_tag('forge:sand', '#minecraft:sand')

    for wood, wood_data in WOODS.items():
        rm.item_tag('minecraft:logs', 'tfc:wood/log/%s' % wood)
        rm.item_tag('minecraft:logs', 'tfc:wood/wood/%s' % wood)
        rm.item_tag('forge:rods/wooden',  'tfc:wood/twig/%s' % wood)
        rm.block_tag('lit_by_dropped_torch', 'tfc:wood/fallen_leaves/' + wood)
        fuel_item(rm, wood + '_log', 'tfc:wood/log/' + wood, wood_data.duration, wood_data.temp, firepit=True)

    rm.item_tag('log_pile_logs', 'tfc:stick_bundle')
    rm.item_tag('pit_kiln_straw', 'tfc:straw')
    rm.item_tag('firepit_fuel', '#minecraft:logs')
    rm.item_tag('firepit_logs', '#minecraft:logs')
    rm.item_tag('log_pile_logs', '#minecraft:logs')
    rm.item_tag('pit_kiln_logs', '#minecraft:logs')
    rm.item_tag('can_be_lit_on_torch', '#forge:rods/wooden')

    fuel_item(rm, 'coal', ['minecraft:coal', 'tfc:ore/bituminous_coal'], 2200, 1415)
    fuel_item(rm, 'lignite', 'tfc:ore/lignite', 2200, 1350)
    fuel_item(rm, 'charcoal', 'minecraft:charcoal', 1800, 1350, bloomery=True)
    fuel_item(rm, 'peat', 'tfc:peat', 2500, 600, firepit=True)
    fuel_item(rm, 'stick_bundle', 'tfc:stick_bundle', 600, 900, firepit=True)

    rm.item_tag('minecraft:coals', 'tfc:ore/bituminous_coal', 'tfc:ore/lignite')
    rm.item_tag('forge_fuel', '#minecraft:coals')

    # Tags
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.item_tag('firepit_sticks', '#forge:rods/wooden')
    rm.item_tag('firepit_kindling', 'tfc:straw', 'minecraft:paper', 'minecraft:book', 'tfc:groundcover/pinecone')
    rm.item_tag('starts_fires_with_durability', 'minecraft:flint_and_steel')
    rm.item_tag('starts_fires_with_items', 'minecraft:fire_charge')
    rm.item_tag('handstone', 'tfc:handstone')

    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:grass_path')
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
    rm.block_tag('any_spreading_bush', '#tfc:spreading_bush')

    # Thatch Bed
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')

    # Misc
    rm.item_tag('mortar', 'tfc:mortar')

    # Fluids
    rm.fluid_tag('usable_in_pot', '#tfc:fluid_ingredients')
    rm.fluid_tag('fluid_ingredients', 'minecraft:water', 'tfc:salt_water', 'tfc:spring_water')

    for mat in VANILLA_TOOL_MATERIALS:
        rm.item_tag('extinguisher', 'minecraft:' + mat + '_shovel')

    # Plants
    for plant in PLANTS.keys():
        rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')

    # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])
    rm.block_tag('forge:dirt', *['tfc:dirt/%s' % v for v in SOIL_BLOCK_VARIANTS])

    # todo: specific item size definitions for a whole bunch of items that aren't naturally assigned
    item_size(rm, 'logs', 'tag!minecraft:logs', Size.very_large, Weight.medium)


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })


def metal_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, metal: str, amount: int):
    rm.data(('tfc', 'metal_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'metal': metal,
        'amount': amount
    })


def heat_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[int] = None):
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


def fuel_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, duration: int, temperature: float, forge: bool = False, bloomery: bool = False, firepit: bool = False):
    if forge:
        rm.item_tag('forge_fuel', ingredient)
    if bloomery:
        rm.item_tag('bloomery_fuel', ingredient)
    if firepit:
        rm.item_tag('firepit_fuel', ingredient)
    rm.data(('tfc', 'fuels', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'duration': duration,
        'temperature': temperature
    })

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager
from mcresources.utils import item_stack

from constants import *


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

                # The IMetal capability
                rm.data(('tfc', 'metal_items', metal, item), {
                    'ingredient': ingredient,
                    'metal': 'tfc:%s' % metal,
                    'amount': item_data.smelt_amount
                })

                # And the IHeat capability
                rm.data(('tfc', 'item_heats', metal, item), {
                    'ingredient': ingredient,
                    'heat_capacity': metal_data.heat_capacity,
                    'forging_temperature': metal_data.melt_temperature * 0.6,
                    'welding_temperature': metal_data.melt_temperature * 0.8
                })

        # Common metal crafting tools
        if 'tool' in metal_data.types:
            for tool in ('hammer', 'chisel', 'axe', 'pickaxe', 'shovel'):
                rm.item_tag('tfc:%ss' % tool, 'tfc:metal/%s/%s' % (tool, metal))
    # Grill
    rm.data(('tfc', 'metal_items', 'wrought_iron', 'grill'), metal_item('tfc:wrought_iron_grill', 'tfc:wrought_iron', 100))
    rm.data(('tfc', 'item_heats', 'wrought_iron', 'grill'), item_heat('tfc:wrought_iron_grill', 0.35, 1535))

    # Rocks
    for rock, rock_data in ROCKS.items():
        rm.data(('tfc', 'rocks', rock), {
            'blocks': dict((block_type, 'tfc:rock/%s/%s' % (block_type, rock)) for block_type in ROCK_BLOCK_TYPES),
            'category': rock_data.category,
            'desert_sand_color': rock_data.desert_sand_color,
            'beach_sand_color': rock_data.beach_sand_color
        })

        rm.block_tag('minecraft:base_stone_overworld', 'tfc:rock/raw/%s' % rock)  # used by vanilla, provided for consistiency
        rm.block_tag('tfc:breaks_when_isolated', 'tfc:rock/raw/%s' % rock)  # only raw rock

    for wood, wood_data in WOODS.items():
        rm.item_tag('firepit_logs', 'tfc:wood/log/' + wood)
        rm.item_tag('firepit_logs', 'tfc:wood/wood/' + wood)
        rm.data(('tfc', 'fuels', 'wood', wood + '_log'), fuel('tfc:wood/log/' + wood, wood_data.amount, wood_data.temp))

    rm.data(('tfc', 'fuels', 'coal'), fuel('minecraft:coal', 2200, 1415, forge=True))  # vanilla coal for compat
    rm.data(('tfc', 'fuels', 'bituminous_coal'), fuel('tfc:ore/bituminous_coal', 2200, 1415, forge=True))
    rm.data(('tfc', 'fuels', 'lignite'), fuel('tfc:ore/lignite', 2000, 1350, forge=True))
    rm.data(('tfc', 'fuels', 'charcoal'), fuel('minecraft:charcoal', 1800, 1350, forge=True, bloomery=True))
    rm.data(('tfc', 'fuels', 'peat'), fuel('tfc:peat', 2500, 680))
    rm.data(('tfc', 'fuels', 'stick_bundle'), fuel('tfc:stick_bundle', 600, 900))

    rm.data(('tfc', 'item_heats', 'stick'), item_heat('minecraft:stick', 0.1, 1000))
    rm.data(('tfc', 'item_heats', 'stick_bunch'), item_heat('tfc:stick_bunch', 0.2, 1000))

    # Tags
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.item_tag('firepit_sticks', '#forge:rods/wooden')
    rm.item_tag('firepit_kindling', 'tfc:straw', 'minecraft:paper', 'minecraft:book', 'tfc:groundcover/pinecone')
    rm.item_tag('starts_fires_with_durability', 'minecraft:flint_and_steel')
    rm.item_tag('starts_fires_with_items', 'minecraft:fire_charge')
    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:grass_path')
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('small_spike', 'tfc:calcite')

    # Thatch Bed
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')

    # Misc
    rm.item_tag('mortar', 'tfc:mortar')

    for mat in VANILLA_TOOL_MATERIALS:
        rm.item_tag('extinguisher', 'minecraft:' + mat + '_shovel')

    # Plants
    for plant in PLANTS.keys():
        rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')

    # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])

def metal_item(ingredient: str, metal: str, amount: int):
    return {
        'ingredient': item_stack(ingredient),
        'metal': metal,
        'amount': amount
    }

def item_heat(ingredient: str, heat_capacity, melt_temperature: int):
    return {
        'ingredient': item_stack(ingredient),
        'heat_capacity': heat_capacity,
        'forging_temperature': melt_temperature * 0.6,
        'welding_temperature': melt_temperature * 0.8
    }

def fuel(ingredient: str, amount: int, temp: float, forge=False, bloomery=False):
    return {
        'ingredient': item_stack(ingredient),
        'amount': amount,
        'temperature': temp,
        'isForgeFuel': forge,
        'isBloomeryFuel': bloomery
    }

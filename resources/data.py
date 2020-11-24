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

    # Tags
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:grass_path')
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')

    # Thatch Bed
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')

    # Plants
    for plant in PLANTS.keys():
        rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')

    # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.
from typing import Union

from mcresources import ResourceManager, utils
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

        def block(block_type: str):
            return 'tfc:rock/%s/%s' % (block_type, rock)

        rm.block_tag('forge:gravel', block('gravel'))
        rm.block_tag('forge:stone', block('raw'), block('hardened'))
        rm.block_tag('forge:cobblestone', block('cobble'), block('mossy_cobble'))
        rm.block_tag('minecraft:base_stone_overworld', block('raw'), block('hardened'))
        rm.block_tag('tfc:breaks_when_isolated', block('raw'))  # only raw rock

    # Plants
    for plant, plant_data in PLANTS.items():
        rm.block_tag('plant', 'tfc:plant/%s' % plant)
        if plant_data.type in {'standard', 'short_grass', 'creeping'}:
            rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    # Sand
    for color in SAND_BLOCK_TYPES:
        rm.block_tag('minecraft:sand', 'tfc:sand/%s' % color)

    # Forge you dingus, use vanilla tags
    rm.block_tag('forge:sand', '#minecraft:sand')

    # Tags
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    rm.block_tag('tree_grows_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('supports_landslide', 'minecraft:grass_path')
    rm.block_tag('bush_plantable_on', 'minecraft:grass_block', '#forge:dirt', '#tfc:grass')
    rm.block_tag('sea_bush_plantable_on', '#forge:dirt', '#minecraft:sand', '#forge:gravel')
    rm.block_tag('creeping_plantable_on', 'minecraft:grass_block', '#tfc:grass', '#minecraft:base_stone_overworld', '#minecraft:logs')
    rm.block_tag('minecraft:bamboo_plantable_on', '#tfc:grass')
    rm.block_tag('minecraft:climbable', 'tfc:plant/hanging_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/liana', 'tfc:plant/liana_plant')
    rm.block_tag('kelp_tree', 'tfc:plant/giant_kelp_flower', 'tfc:plant/giant_kelp_plant')
    rm.block_tag('kelp_flower', 'tfc:plant/giant_kelp_flower')
    rm.block_tag('kelp_branch', 'tfc:plant/giant_kelp_plant')

    # Thatch Bed
    rm.item_tag('thatch_bed_hides', 'tfc:large_raw_hide', 'tfc:large_sheepskin_hide')
    rm.block_tag('thatch_bed_thatch', 'tfc:thatch')

    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')

    # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna('tfc:isopod', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(max_temp=14)))
    rm.data(('tfc', 'fauna', 'lobster'), fauna('tfc:lobster', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(max_temp=21)))
    rm.data(('tfc', 'fauna', 'cod'), fauna('tfc:cod', fluid='tfc:salt_water', climate=climate_config(max_temp=18)))
    rm.data(('tfc', 'fauna', 'pufferfish'), fauna('tfc:pufferfish', fluid='tfc:salt_water', climate=climate_config(min_temp=10)))
    rm.data(('tfc', 'fauna', 'tropical_fish'), fauna('tfc:tropical_fish', fluid='tfc:salt_water', climate=climate_config(min_temp=18)))
    rm.data(('tfc', 'fauna', 'jellyfish'), fauna('tfc:jellyfish', fluid='tfc:salt_water', climate=climate_config(min_temp=18)))
    rm.data(('tfc', 'fauna', 'orca'), fauna('tfc:orca', fluid='tfc:salt_water', distance_below_sea_level=35, climate=climate_config(max_temp=19, min_rain=100), chance=10))
    rm.data(('tfc', 'fauna', 'dolphin'), fauna('tfc:dolphin', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(min_temp=10, min_rain=200), chance=10))
    rm.data(('tfc', 'fauna', 'manatee'), fauna('tfc:manatee', fluid='minecraft:water', distance_below_sea_level=3, climate=climate_config(min_temp=20, min_rain=300), chance=10))
    rm.data(('tfc', 'fauna', 'salmon'), fauna('tfc:salmon', fluid='minecraft:water', climate=climate_config(min_temp=-5)))


def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None) -> Dict[str, Any]:
    return utils.del_none({
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'max_forest': 'normal' if needs_forest else None,
        'fuzzy': fuzzy
    })


def fauna(entity: str, fluid: str = 'minecraft:empty', chance: int = 1, distance_below_sea_level: int = -1, climate: Dict[str, Any] = None) -> Dict[str, Any]:
    return utils.del_none({
        'entity': entity,
        'fluid': fluid,
        'chance': chance,
        'distance_below_sea_level': distance_below_sea_level,
        'climate': climate
    })
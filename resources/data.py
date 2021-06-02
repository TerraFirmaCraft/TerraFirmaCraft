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
            rm.item_tag('forge:shears', 'tfc:metal/shears/%s' % metal)
    # Grill
    rm.data(('tfc', 'metal_items', 'wrought_iron', 'grill'), metal_item('tfc:wrought_iron_grill', 'tfc:wrought_iron', 100))
    rm.data(('tfc', 'item_heats', 'wrought_iron', 'grill'), item_heat('tfc:wrought_iron_grill', 0.35, 1535))

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

    rm.block_tag('forge:dirt', *['tfc:dirt/%s' % v for v in SOIL_BLOCK_VARIANTS])

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
        rm.block_tag('lit_by_dropped_torch', 'tfc:wood/fallen_leaves/' + wood)
        rm.data(('tfc', 'fuels', 'wood', wood + '_log'), fuel(rm, 'tfc:wood/log/' + wood, wood_data.duration, wood_data.temp))

    rm.item_tag('log_pile_logs', 'tfc:stick_bundle')
    rm.item_tag('pit_kiln_straw', 'tfc:straw')
    rm.item_tag('firepit_fuel', '#minecraft:logs')
    rm.item_tag('firepit_logs', '#minecraft:logs')
    rm.item_tag('log_pile_logs', '#minecraft:logs')
    rm.item_tag('pit_kiln_logs', '#minecraft:logs')
    rm.item_tag('can_be_lit_on_torch', '#forge:rods/wooden')

    rm.data(('tfc', 'fuels', 'coal'), fuel(rm, 'minecraft:coal', 2200, 1415))  # vanilla coal for compat
    rm.data(('tfc', 'fuels', 'bituminous_coal'), fuel(rm, 'tfc:ore/bituminous_coal', 2200, 1415))
    rm.data(('tfc', 'fuels', 'lignite'), fuel(rm, 'tfc:ore/lignite', 2000, 1350))
    rm.data(('tfc', 'fuels', 'charcoal'), fuel(rm, 'minecraft:charcoal', 1800, 1350, bloomery=True))
    rm.data(('tfc', 'fuels', 'peat'), fuel(rm, 'tfc:peat', 2500, 680, firepit=True))
    rm.data(('tfc', 'fuels', 'stick_bundle'), fuel(rm, 'tfc:stick_bundle', 600, 900, firepit=True))

    rm.item_tag('minecraft:coals', 'tfc:ore/bituminous_coal', 'tfc:ore/lignite')
    rm.item_tag('forge_fuel', '#minecraft:coals')

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

    for mat in VANILLA_TOOL_MATERIALS:
        rm.item_tag('extinguisher', 'minecraft:' + mat + '_shovel')

    # Plants
    for plant in PLANTS.keys():
        rm.block_tag('can_be_snow_piled', 'tfc:plant/%s' % plant)

    rm.block_tag('snow', 'minecraft:snow', 'minecraft:snow_block', 'tfc:snow_pile')

    # Valid spawn tag - grass, sand, or raw rock
    rm.block_tag('minecraft:valid_spawn', *['tfc:grass/%s' % v for v in SOIL_BLOCK_VARIANTS], *['tfc:sand/%s' % c for c in SAND_BLOCK_TYPES], *['tfc:rock/raw/%s' % r for r in ROCKS.keys()])

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna('tfc:isopod', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(max_temp=14)))
    rm.data(('tfc', 'fauna', 'lobster'), fauna('tfc:lobster', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(max_temp=21)))
    rm.data(('tfc', 'fauna', 'cod'), fauna('tfc:cod', fluid='tfc:salt_water', climate=climate_config(max_temp=18), distance_below_sea_level=5))
    rm.data(('tfc', 'fauna', 'pufferfish'), fauna('tfc:pufferfish', fluid='tfc:salt_water', climate=climate_config(min_temp=10), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'tropical_fish'), fauna('tfc:tropical_fish', fluid='tfc:salt_water', climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'jellyfish'), fauna('tfc:jellyfish', fluid='tfc:salt_water', climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'orca'), fauna('tfc:orca', fluid='tfc:salt_water', distance_below_sea_level=35, climate=climate_config(max_temp=19, min_rain=100), chance=10))
    rm.data(('tfc', 'fauna', 'dolphin'), fauna('tfc:dolphin', fluid='tfc:salt_water', distance_below_sea_level=20, climate=climate_config(min_temp=10, min_rain=200), chance=10))
    rm.data(('tfc', 'fauna', 'manatee'), fauna('tfc:manatee', fluid='minecraft:water', distance_below_sea_level=3, climate=climate_config(min_temp=20, min_rain=300), chance=10))
    rm.data(('tfc', 'fauna', 'salmon'), fauna('tfc:salmon', fluid='minecraft:water', climate=climate_config(min_temp=-5)))
    rm.data(('tfc', 'fauna', 'bluegill'), fauna('tfc:bluegill', fluid='minecraft:water', climate=climate_config(min_temp=-10, max_temp=26)))
    rm.data(('tfc', 'fauna', 'penguin'), fauna('tfc:penguin', climate=climate_config(max_temp=-14, min_rain=75)))
    rm.data(('tfc', 'fauna', 'turtle'), fauna('tfc:turtle', climate=climate_config(min_temp=21, min_rain=250)))


def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None) -> Dict[str, Any]:
    return utils.del_none({
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'max_forest': 'normal' if needs_forest else None,
        'fuzzy': fuzzy
    })


def fauna(entity: str, fluid: str = None, chance: int = None, distance_below_sea_level: int = None, climate: Dict[str, Any] = None, solid_ground: bool = None) -> Dict[str, Any]:
    return utils.del_none({
        'entity': entity,
        'fluid': fluid,
        'chance': chance,
        'distance_below_sea_level': distance_below_sea_level,
        'climate': climate,
        'solid_ground': solid_ground
    })


def metal_item(ingredient: str, metal: str, amount: int):
    return {
        'ingredient': item_stack(ingredient),
        'metal': metal,
        'amount': amount
    }


def item_heat(ingredient: str, heat_capacity: float, melt_temperature: int = 0):
    if melt_temperature > 0:
        return {
            'ingredient': item_stack(ingredient),
            'heat_capacity': heat_capacity,
            'forging_temperature': melt_temperature * 0.6,
            'welding_temperature': melt_temperature * 0.8
        }
    else:
        return {
            'ingredient': item_stack(ingredient),
            'heat_capacity': heat_capacity
        }


def fuel(rm: ResourceManager, ingredient: str, duration: int, temp: float, forge=False, bloomery=False, firepit=False):
    if forge:
        rm.item_tag('forge_fuel', ingredient)
    if bloomery:
        rm.item_tag('bloomery_fuel', ingredient)
    if firepit:
        rm.item_tag('firepit_fuel', ingredient)
    return {
        'ingredient': item_stack(ingredient),
        'duration': duration,
        'temperature': temp
    }

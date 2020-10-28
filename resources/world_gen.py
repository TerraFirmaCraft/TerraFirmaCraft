# Handles generation of all world gen objects

from enum import IntEnum
from typing import Tuple

from mcresources import ResourceManager, world_gen as wg

from constants import *

BiomeTemperature = NamedTuple('BiomeTemperature', id=str, temperature=float, water_color=float, water_fog_color=float)
BiomeRainfall = NamedTuple('BiomeRainfall', id=str, downfall=float)

TEMPERATURES = (
    BiomeTemperature('frozen', 0, 3750089, 329011),
    BiomeTemperature('cold', 0.25, 4020182, 329011),
    BiomeTemperature('normal', 0.5, 4159204, 329011),
    BiomeTemperature('lukewarm', 0.75, 4566514, 267827),
    BiomeTemperature('warm', 1.0, 4445678, 270131)
)

RAINFALLS = (
    BiomeRainfall('arid', 0),
    BiomeRainfall('dry', 0.2),
    BiomeRainfall('normal', 0.45),
    BiomeRainfall('damp', 0.7),
    BiomeRainfall('wet', 0.9)
)

DEFAULT_FOG_COLOR = 12638463
DEFAULT_SKY_COLOR = 0x84E6FF


class Decoration(IntEnum):
    RAW_GENERATION = 0
    LAKES = 1
    LOCAL_MODIFICATIONS = 2
    UNDERGROUND_STRUCTURES = 3
    SURFACE_STRUCTURES = 4
    STRONGHOLDS = 5
    UNDERGROUND_ORES = 6
    UNDERGROUND_DECORATION = 7
    VEGETAL_DECORATION = 8
    TOP_LAYER_MODIFICATION = 9


def generate(rm: ResourceManager):
    # Surface Builder Configs
    grass_dirt_sand = wg.surface_builder_config('minecraft:grass_block[snowy=false]', 'minecraft:dirt', 'minecraft:sand')
    grass_dirt_gravel = wg.surface_builder_config('minecraft:grass_block[snowy=false]', 'minecraft:dirt', 'minecraft:gravel')
    air_air_air = wg.surface_builder_config('minecraft:air', 'minecraft:air', 'minecraft:air')

    # Surface Builders
    surface_builder(rm, 'badlands', wg.configure('tfc:badlands', grass_dirt_sand))
    surface_builder(rm, 'canyons', wg.configure('tfc:thin', grass_dirt_sand))
    surface_builder(rm, 'deep', wg.configure('tfc:deep', grass_dirt_gravel))
    surface_builder(rm, 'plateau', wg.configure('tfc:plateau', grass_dirt_sand))
    surface_builder(rm, 'default', wg.configure('tfc:normal', grass_dirt_sand))
    surface_builder(rm, 'underwater', wg.configure('tfc:underwater', air_air_air))
    surface_builder(rm, 'mountains', wg.configure('tfc:mountains', grass_dirt_sand))
    surface_builder(rm, 'shore', wg.configure('tfc:shore', air_air_air))

    # Configured Features
    rm.feature('ore_veins', wg.configure('tfc:ore_veins'))
    rm.feature('erosion', wg.configure('tfc:erosion'))
    rm.feature('ice_and_snow', wg.configure('tfc:ice_and_snow'))
    rm.feature('glacier', wg.configure('tfc:glacier'))

    rm.feature('lake', wg.configure_decorated(wg.configure('tfc:lake'), ('minecraft:chance', {'chance': 15}), 'minecraft:heightmap_world_surface', 'minecraft:square'))
    rm.feature('flood_fill_lake', wg.configure_decorated(wg.configure('tfc:flood_fill_lake'), 'minecraft:heightmap_world_surface', 'minecraft:square'))

    for spring_cfg in (('water', 80), ('lava', 35)):
        rm.feature('%s_spring' % spring_cfg[0], wg.configure_decorated(wg.configure('tfc:spring', {
            'state': wg.block_state('minecraft:%s[falling=true]' % spring_cfg[0]),
            'valid_blocks': ['tfc:rock/raw/%s' % rock for rock in ROCKS.keys()]
        }), ('minecraft:count', {'count': spring_cfg[1]}), 'minecraft:square', ('minecraft:range_biased', {'bottom_offset': 8, 'top_offset': 8, 'maximum': 256})))

    # todo: rework, they look like crap and are causing problems
    # rm.feature('water_fissure', wg.configure_decorated(wg.configure('tfc:fissure', {'state': wg.block_state('minecraft:water[level=0]')}), ('minecraft:chance', {'chance': 60}), 'minecraft:heightmap_world_surface', 'minecraft:square'))
    # rm.feature('lava_fissure', wg.configure_decorated(wg.configure('tfc:fissure', {'state': wg.block_state('minecraft:lava[level=0]')}), ('minecraft:chance', {'chance': 60}), 'minecraft:heightmap_world_surface', 'minecraft:square'))

    rm.feature('cave_spike', wg.configure_decorated(wg.configure('tfc:cave_spike'), ('minecraft:carving_mask', {'step': 'air', 'probability': 0.09})))
    rm.feature('large_cave_spike', wg.configure_decorated(wg.configure('tfc:large_cave_spike'), ('minecraft:carving_mask', {'step': 'air', 'probability': 0.02})))

    for boulder_cfg in (('raw_boulder', 'raw', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.feature(boulder_cfg[0], wg.configure_decorated(wg.configure('tfc:boulder', {
            'base_type': boulder_cfg[1],
            'decoration_type': boulder_cfg[2]
        }), 'minecraft:heightmap_world_surface', 'minecraft:square', ('minecraft:chance', {'chance': 12}), 'tfc:flat_enough'))

    # Trees / Forests
    rm.feature('forest', wg.configure('tfc:forest', {
        'entries': [
            forest_config(30, 210, 17, 32, 'acacia', True),
            forest_config(60, 240, 1, 15, 'ash', True),
            forest_config(350, 500, -18, 5, 'aspen', False),
            forest_config(125, 310, -11, 7, 'birch', False),
            forest_config(0, 180, 12, 32, 'blackwood', True),
            forest_config(180, 370, -4, 17, 'chestnut', False),
            forest_config(290, 500, -16, -1, 'douglas_fir', True),
            forest_config(210, 400, 9, 24, 'hickory', True),
            forest_config(270, 500, 17, 32, 'kapok', False),
            forest_config(270, 500, -1, 15, 'maple', True),
            forest_config(240, 450, -9, 11, 'oak', False),
            forest_config(180, 470, 20, 32, 'palm', False),
            forest_config(60, 270, -18, -4, 'pine', True),
            forest_config(140, 310, 8, 31, 'rosewood', False),
            forest_config(250, 420, -14, 2, 'sequoia', True),
            forest_config(110, 320, -17, 1, 'spruce', True),
            forest_config(230, 480, 15, 29, 'sycamore', True),
            forest_config(10, 220, -13, 9, 'white_cedar', True),
            forest_config(330, 500, 11, 32, 'willow', True),
        ]
    }))

    rm.feature(('tree', 'acacia'), wg.configure('tfc:random_tree', random_config('acacia', 35)))
    rm.feature(('tree', 'acacia_large'), wg.configure('tfc:random_tree', random_config('acacia', 6, 2, True)))
    rm.feature(('tree', 'ash'), wg.configure('tfc:overlay_tree', overlay_config('ash', 3, 5)))
    rm.feature(('tree', 'ash_large'), wg.configure('tfc:random_tree', random_config('ash', 5, 2, True)))
    rm.feature(('tree', 'aspen'), wg.configure('tfc:random_tree', random_config('aspen', 16, trunk=[3, 5, 1])))
    rm.feature(('tree', 'birch'), wg.configure('tfc:random_tree', random_config('birch', 16, trunk=[2, 3, 1])))
    rm.feature(('tree', 'blackwood'), wg.configure('tfc:random_tree', random_config('blackwood', 10)))
    rm.feature(('tree', 'blackwood_large'), wg.configure('tfc:random_tree', random_config('blackwood', 10, 1, True)))
    rm.feature(('tree', 'chestnut'), wg.configure('tfc:overlay_tree', overlay_config('chestnut', 2, 4)))
    rm.feature(('tree', 'douglas_fir'), wg.configure('tfc:random_tree', random_config('douglas_fir', 9)))
    rm.feature(('tree', 'douglas_fir_large'), wg.configure('tfc:random_tree', random_config('douglas_fir', 5, 2, True)))
    rm.feature(('tree', 'hickory'), wg.configure('tfc:random_tree', random_config('hickory', 9)))
    rm.feature(('tree', 'hickory_large'), wg.configure('tfc:random_tree', random_config('hickory', 5, 2, True)))
    rm.feature(('tree', 'kapok'), wg.configure('tfc:random_tree', random_config('kapok', 17)))
    rm.feature(('tree', 'maple'), wg.configure('tfc:overlay_tree', overlay_config('maple', 2, 4)))
    rm.feature(('tree', 'maple_large'), wg.configure('tfc:random_tree', random_config('maple', 5, 2, True)))
    rm.feature(('tree', 'oak'), wg.configure('tfc:overlay_tree', overlay_config('oak', 3, 5)))
    rm.feature(('tree', 'palm'), wg.configure('tfc:random_tree', random_config('palm', 7)))
    rm.feature(('tree', 'pine'), wg.configure('tfc:random_tree', random_config('pine', 9)))
    rm.feature(('tree', 'pine_large'), wg.configure('tfc:random_tree', random_config('pine', 5, 2, True)))
    rm.feature(('tree', 'rosewood'), wg.configure('tfc:overlay_tree', overlay_config('rosewood', 1, 3)))
    rm.feature(('tree', 'sequoia'), wg.configure('tfc:random_tree', random_config('sequoia', 7)))
    rm.feature(('tree', 'sequoia_large'), wg.configure('tfc:stacked_tree', stacked_config('sequoia', 5, 9, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'spruce'), wg.configure('tfc:random_tree', random_config('sequoia', 7)))
    rm.feature(('tree', 'spruce_large'), wg.configure('tfc:stacked_tree', stacked_config('spruce', 5, 9, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'sycamore'), wg.configure('tfc:overlay_tree', overlay_config('sycamore', 2, 5)))
    rm.feature(('tree', 'sycamore_large'), wg.configure('tfc:random_tree', random_config('sycamore', 5, 2, True)))
    rm.feature(('tree', 'white_cedar'), wg.configure('tfc:overlay_tree', overlay_config('white_cedar', 2, 4)))
    rm.feature(('tree', 'white_cedar_large'), wg.configure('tfc:overlay_tree', overlay_config('white_cedar', 2, 5, 1, 1, True)))
    rm.feature(('tree', 'willow'), wg.configure('tfc:random_tree', random_config('willow', 7)))
    rm.feature(('tree', 'willow_large'), wg.configure('tfc:random_tree', random_config('willow', 14, 1, True)))

    # Plants
    plant_entries = []
    for plant_name, plant_data in PLANTS.items():
        # Add each config to the list
        plant_entries.append(plant_config(plant_data.min_rain, plant_data.max_rain, plant_data.min_temp, plant_data.max_temp, plant_data.type, plant_data.clay, plant_name))
        # Generate the corresponding placement feature
        if plant_data.type == 'standard':
            flower_feature(rm, plant_name)
        elif plant_data.type == 'short_grass':
            grass_feature(rm, plant_name)
        elif plant_data.type == 'tall_grass':
            tall_grass_feature(rm, plant_name)
        elif plant_data.type == 'epiphyte':
            epiphyte_plant_feature(rm, plant_name)
        elif plant_data.type == 'creeping':
            creeping_plant_feature(rm, plant_name)
        elif plant_data.type == 'hanging':
            hanging_plant_feature(rm, plant_name)
        elif plant_data.type == 'floating':
            water_plant_feature(rm, plant_name)
        # Add lang
        rm.lang('block.tfc.plant.' + plant_name, lang('%s', plant_name))
    # Generate the collection feature
    rm.feature('plants', wg.configure('tfc:plants', {
        'standard': 2,
        'floating': 3,
        'short_grass': 5,
        'tall_grass': 3,
        'creeping': 3,
        'hanging': 3,
        'epiphyte': 3,
        'entries': plant_entries
    }))
    
    # Carvers
    rm.carver('cave', wg.configure('tfc:cave', {'probability': 0.1}))
    rm.carver('canyon', wg.configure('tfc:canyon', {'probability': 0.015}))
    rm.carver('worley_cave', wg.configure('tfc:worley_cave'))

    # Biomes
    for temp in TEMPERATURES:
        for rain in RAINFALLS:
            biome(rm, 'badlands', temp, rain, 'mesa', 'tfc:badlands')
            biome(rm, 'canyons', temp, rain, 'plains', 'tfc:canyons', boulders=True)
            biome(rm, 'low_canyons', temp, rain, 'swamp', 'tfc:canyons', boulders=True)
            biome(rm, 'plains', temp, rain, 'plains', 'tfc:deep')
            biome(rm, 'plateau', temp, rain, 'extreme_hills', 'tfc:mountains', boulders=True)
            biome(rm, 'hills', temp, rain, 'plains', 'tfc:default')
            biome(rm, 'rolling_hills', temp, rain, 'plains', 'tfc:default', boulders=True)
            biome(rm, 'lake', temp, rain, 'river', 'tfc:underwater', spawnable=False)
            biome(rm, 'lowlands', temp, rain, 'swamp', 'tfc:deep')
            biome(rm, 'mountains', temp, rain, 'extreme_hills', 'tfc:mountains')
            biome(rm, 'old_mountains', temp, rain, 'extreme_hills', 'tfc:mountains')
            biome(rm, 'flooded_mountains', temp, rain, 'extreme_hills', 'tfc:mountains')
            biome(rm, 'ocean', temp, rain, 'ocean', 'tfc:underwater', spawnable=False)
            biome(rm, 'deep_ocean', temp, rain, 'ocean', 'tfc:underwater', spawnable=False)
            biome(rm, 'river', temp, rain, 'river', 'tfc:underwater', spawnable=False)
            biome(rm, 'mountain_river', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'shore', temp, rain, 'beach', 'tfc:shore')


def surface_builder(rm: ResourceManager, name: str, surface_builder):
    rm.surface_builder(name, surface_builder)
    rm.surface_builder(name + '_with_glaciers', wg.configure('tfc:with_glaciers', {
        'parent': 'tfc:%s' % name
    }))


def forest_config(min_rain: float, max_rain: float, min_temp: float, max_temp: float, tree: str, old_growth: bool):
    cfg = {
        'min_rain': min_rain,
        'max_rain': max_rain,
        'min_temp': min_temp,
        'max_temp': max_temp,
        'normal_tree': 'tfc:tree/%s' % tree
    }
    if old_growth:
        cfg['old_growth_tree'] = 'tfc:tree/%s_large' % tree
    return cfg
    
    
def plant_config(min_rain: float, max_rain: float, min_temp: float, max_temp: float, type: str, clay: bool, plant: str):
    return {
        'min_rain': min_rain,
        'max_rain': max_rain,
        'min_temp': min_temp,
        'max_temp': max_temp,
        'type': type,
        'clay_indicator': clay,
        'feature': 'tfc:plant/%s' % plant
    }

def overlay_config(tree: str, min_height: int, max_height: int, width: int = 1, radius: int = 1, large: bool = False):
    block = 'tfc:wood/log/%s[axis=y]' % tree
    if large:
        tree += '_large'
    return {
        'base': 'tfc:%s/base' % tree,
        'overlay': 'tfc:%s/overlay' % tree,
        'trunk': trunk_config(block, min_height, max_height, width),
        'radius': radius
    }


def random_config(tree: str, structure_count: int, radius: int = 1, large: bool = False, trunk: List = None):
    block = 'tfc:wood/log/%s[axis=y]' % tree
    if large:
        tree += '_large'
    cfg = {
        'structures': ['tfc:%s/%d' % (tree, i) for i in range(1, 1 + structure_count)],
        'radius': radius
    }
    if trunk is not None:
        cfg['trunk'] = trunk_config(block, *trunk)
    return cfg


def stacked_config(tree: str, min_height: int, max_height: int, width: int, layers: List[Tuple[int, int, int]], radius: int = 1, large: bool = False):
    # layers consists of each layer, which is a (min_count, max_count, total_templates)
    block = 'tfc:wood/log/%s[axis=y]' % tree
    if large:
        tree += '_large'
    return {
        'trunk': trunk_config(block, min_height, max_height, width),
        'layers': [{
            'templates': ['tfc:%s/layer%d_%d' % (tree, 1 + i, j) for j in range(1, 1 + layer[2])],
            'min_count': layer[0],
            'max_count': layer[1]
        } for i, layer in enumerate(layers)],
        'radius': radius
    }


def trunk_config(block: str, min_height: int, max_height: int, width: int):
    return {
        'state': wg.block_state(block),
        'min_height': min_height,
        'max_height': max_height,
        'width': width
    }


def biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, surface_builder: str, boulders: bool = False, spawnable: bool = True):
    if rain.id == 'arid':
        rain_type = 'none'
    elif temp.id in ('cold', 'frozen'):
        rain_type = 'snow'
        surface_builder += '_with_glaciers'
    else:
        rain_type = 'rain'
    features = [
        ['tfc:erosion'],  # raw generation
        ['tfc:flood_fill_lake', 'tfc:lake'],  # lakes
        [],  # local modification
        [],  # underground structure
        [],  # surface structure
        [],  # strongholds
        ['tfc:ore_veins'],  # underground ores
        ['tfc:cave_spike', 'tfc:large_cave_spike', 'tfc:water_spring', 'tfc:lava_spring'],  # underground decoration
        ['tfc:forest', 'tfc:plants'],  # vegetal decoration
        ['tfc:ice_and_snow']  # top layer modification
    ]
    if boulders:
        features[Decoration.SURFACE_STRUCTURES] += ['tfc:raw_boulder', 'tfc:cobble_boulder']
        if rain.id in ('damp', 'wet'):
            features[Decoration.SURFACE_STRUCTURES].append('tfc:mossy_boulder')
    rm.biome(
        name_parts='%s_%s_%s' % (name, temp.id, rain.id),
        precipitation=rain_type,
        category=category,
        temperature=temp.temperature,
        downfall=rain.downfall,
        effects={
            'fog_color': DEFAULT_FOG_COLOR,
            'sky_color': DEFAULT_SKY_COLOR,
            'water_color': temp.water_color,
            'water_fog_color': temp.water_fog_color
        },
        surface_builder=surface_builder,
        air_carvers=['tfc:worley_cave', 'tfc:cave', 'tfc:canyon'],
        water_carvers=[],
        features=features,
        player_spawn_friendly=spawnable
    )

# Plants feature generation
def flower_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 1,
        'xspread': 15,
        'zspread': 15,
        'tries': 10
    }))
    
def water_plant_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 1,
        'xspread': 15,
        'zspread': 15,
        'tries': 10
    }))

def grass_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 1,
        'xspread': 15,
        'zspread': 15
    }))
    
#todo tweak
def tall_grass_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1',
                    'part': 'lower'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 1,
        'xspread': 15,
        'zspread': 15
    }))
    
# todo tweak
def hanging_plant_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1',
                    'hanging': 'false'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 9,
        'xspread': 15,
        'zspread': 15
    }))
    
# todo tweak
def epiphyte_plant_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1',
                    'facing': 'north'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 6,
        'xspread': 15,
        'zspread': 15
    }))
    
# todo tweak
def creeping_plant_feature(rm: ResourceManager, plant_name: str):
    rm.feature(('plant', plant_name), wg.configure('minecraft:random_patch', {
        'state_provider': {
            'type': 'minecraft:simple_state_provider',
            'state': {
                'Name': 'tfc:plant/%s' % plant_name,
                'Properties': {
                    'dayperiod': '1',
                    'age': '1',
                    'stage': '1',
                    'up': 'false',
                    'down': 'true',
                    'north': 'false',
                    'east': 'false',
                    'south': 'false',
                    'west': 'false'
                }
            },
        },
        'block_placer': {
            'type': 'minecraft:simple_block_placer',
            'config': {}
        },
        'whitelist': [],
        'blacklist': [],
        'yspread': 9,
        'xspread': 15,
        'zspread': 15
    }))
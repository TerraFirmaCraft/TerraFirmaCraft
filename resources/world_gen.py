# Handles generation of all world gen objects

import hashlib
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
    surface_builder(rm, 'default', wg.configure('tfc:normal', grass_dirt_sand))
    surface_builder(rm, 'underwater', wg.configure('tfc:underwater', air_air_air))
    surface_builder(rm, 'mountains', wg.configure('tfc:mountains', grass_dirt_sand))
    surface_builder(rm, 'shore', wg.configure('tfc:shore', air_air_air))

    # Configured Features
    rm.feature('erosion', wg.configure('tfc:erosion'))
    rm.feature('ice_and_snow', wg.configure('tfc:ice_and_snow'))

    rm.feature('lake', wg.configure_decorated(wg.configure('tfc:lake'), ('minecraft:chance', {'chance': 15}), 'minecraft:heightmap_world_surface', 'minecraft:square'))
    rm.feature('flood_fill_lake', wg.configure_decorated(wg.configure('tfc:flood_fill_lake'), 'minecraft:square', 'minecraft:heightmap_world_surface'))

    for spring_cfg in (('water', 80), ('lava', 35)):
        rm.feature('%s_spring' % spring_cfg[0], wg.configure_decorated(wg.configure('tfc:spring', {
            'state': wg.block_state('minecraft:%s[falling=true]' % spring_cfg[0]),
            'valid_blocks': ['tfc:rock/raw/%s' % rock for rock in ROCKS.keys()]
        }), ('minecraft:count', {'count': spring_cfg[1]}), 'minecraft:square', ('minecraft:range_biased', {'bottom_offset': 8, 'top_offset': 8, 'maximum': 256})))

    clay = [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:clay/%s' % soil} for soil in SOIL_BLOCK_VARIANTS] + [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:clay_grass/%s' % soil} for soil in SOIL_BLOCK_VARIANTS]
    rm.feature('clay_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 3,
        'max_radius': 5,
        'height': 3,
        'states': clay
    }), ('minecraft:chance', {'chance': 12}), 'minecraft:square', 'minecraft:heightmap_world_surface', ('tfc:climate', {'min_rainfall': 200})))
    rm.feature('water_clay_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 2,
        'max_radius': 3,
        'height': 2,
        'states': clay
    }), ('minecraft:chance', {'chance': 1}), 'minecraft:square', 'minecraft:heightmap_world_surface', 'tfc:near_water'))
    rm.feature('peat_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 5,
        'max_radius': 9,
        'height': 7,
        'states': [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:peat'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:peat_grass'} for soil in SOIL_BLOCK_VARIANTS]
    }), ('minecraft:chance', {'chance': 10}), 'minecraft:square', 'minecraft:heightmap_world_surface', ('tfc:climate', {'min_rainfall': 350, 'min_temperature': 12})))

    rm.feature('cave_spike', wg.configure_decorated(wg.configure('tfc:cave_spike'), ('minecraft:carving_mask', {'step': 'air', 'probability': 0.09})))
    rm.feature('large_cave_spike', wg.configure_decorated(wg.configure('tfc:large_cave_spike'), ('minecraft:carving_mask', {'step': 'air', 'probability': 0.02})))

    for boulder_cfg in (('raw_boulder', 'raw', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.feature(boulder_cfg[0], wg.configure_decorated(wg.configure('tfc:boulder', {
            'base_type': boulder_cfg[1],
            'decoration_type': boulder_cfg[2]
        }), 'minecraft:square', 'minecraft:heightmap_world_surface', ('minecraft:chance', {'chance': 12}), 'tfc:flat_enough'))

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

    # Ore Veins
    for vein_name, vein in ORE_VEINS.items():
        rocks = expand_rocks(vein.rocks, vein_name)
        ore = ORES[vein.ore]  # standard ore
        if ore.graded:  # graded ore vein
            rm.feature(('vein', vein_name), wg.configure('tfc:%s_vein' % vein.type, {
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density * 0.01,
                'blocks': [{
                    'stone': ['tfc:rock/raw/%s' % rock],
                    'ore': [{
                        'weight': vein.poor,
                        'block': 'tfc:ore/poor_%s/%s' % (vein.ore, rock)
                    }, {
                        'weight': vein.normal,
                        'block': 'tfc:ore/normal_%s/%s' % (vein.ore, rock)
                    }, {
                        'weight': vein.rich,
                        'block': 'tfc:ore/rich_%s/%s' % (vein.ore, rock)
                    }]
                } for rock in rocks],
                'salt': int(hashlib.sha256(vein_name.encode('utf-8')).hexdigest(), 16) & 0xFFFFFFFF
            }))
        else:  # non-graded ore vein (mineral)
            rm.feature(('vein', vein_name), wg.configure('tfc:%s_vein' % vein.type, {
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density,
                'blocks': [{
                    'stone': ['tfc:rock/raw/%s' % rock],
                    'ore': [{'block': 'tfc:ore/%s/%s' % (vein.ore, rock)}]
                } for rock in rocks],
                'salt': int(hashlib.sha256(vein_name.encode('utf-8')).hexdigest(), 16) & 0xFFFFFFFF
            }))

    # Carvers
    rm.carver('cave', wg.configure('tfc:cave', {'probability': 0.1}))
    rm.carver('canyon', wg.configure('tfc:canyon', {'probability': 0.015}))
    rm.carver('worley_cave', wg.configure('tfc:worley_cave'))

    rm.carver('underwater_cave', wg.configure('tfc:underwater_cave', {'probability': 0.03}))
    rm.carver('underwater_canyon', wg.configure('tfc:underwater_canyon', {'probability': 0.02}))

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
            biome(rm, 'flooded_mountains', temp, rain, 'extreme_hills', 'tfc:mountains', ocean_carvers=True)
            biome(rm, 'ocean', temp, rain, 'ocean', 'tfc:underwater', spawnable=False, ocean_carvers=True)
            biome(rm, 'deep_ocean', temp, rain, 'ocean', 'tfc:underwater', spawnable=False, ocean_carvers=True)
            biome(rm, 'river', temp, rain, 'river', 'tfc:underwater', spawnable=False)
            biome(rm, 'shore', temp, rain, 'beach', 'tfc:shore')

            biome(rm, 'mountain_river', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'old_mountain_river', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'flooded_mountain_river', temp, rain, 'river', 'tfc:mountains', spawnable=False, ocean_carvers=True)
            biome(rm, 'mountain_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'old_mountain_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'flooded_mountain_lake', temp, rain, 'river', 'tfc:mountains', spawnable=False, ocean_carvers=True)
            biome(rm, 'plateau_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False, boulders=True)


def surface_builder(rm: ResourceManager, name: str, surface_builder):
    # Add a surface builder, and also one with glaciers for cold biomes
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


def biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, surface_builder: str, boulders: bool = False, spawnable: bool = True, ocean_carvers: bool = False):
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
        ['tfc:clay_disc', 'tfc:water_clay_disc', 'tfc:peat_disc'],  # local modification
        [],  # underground structure
        [],  # surface structure
        [],  # strongholds
        ['tfc:vein/%s' % vein for vein in ORE_VEINS.keys()],  # underground ores
        ['tfc:cave_spike', 'tfc:large_cave_spike', 'tfc:water_spring', 'tfc:lava_spring'],  # underground decoration
        ['tfc:forest'],  # vegetal decoration
        ['tfc:ice_and_snow']  # top layer modification
    ]
    if boulders:
        features[Decoration.SURFACE_STRUCTURES] += ['tfc:raw_boulder', 'tfc:cobble_boulder']
        if rain.id in ('damp', 'wet'):
            features[Decoration.SURFACE_STRUCTURES].append('tfc:mossy_boulder')
    air_carvers = ['tfc:worley_cave', 'tfc:cave', 'tfc:canyon']
    water_carvers = []
    if ocean_carvers:
        water_carvers += ['tfc:underwater_cave', 'tfc:underwater_canyon']

    rm.lang('biome.tfc.%s_%s_%s' % (name, temp.id, rain.id), '(%s / %s) %s' % (temp.id.title(), rain.id.title(), lang(name)))
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
        air_carvers=air_carvers,
        water_carvers=water_carvers,
        features=features,
        player_spawn_friendly=spawnable
    )


def expand_rocks(rocks_list: List, path: str) -> List[str]:
    rocks = []
    for rock_spec in rocks_list:
        if rock_spec in ROCKS:
            rocks.append(rock_spec)
        elif rock_spec in ROCK_CATEGORIES:
            rocks += [r for r, d in ROCKS.items() if d.category == rock_spec]
        else:
            raise RuntimeError('Unknown rock or rock category specification: %s at %s' % (rock_spec, path))
    return rocks

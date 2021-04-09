# Handles generation of all world gen objects

import hashlib
from enum import IntEnum
from typing import Tuple, Any, Literal, Union

from mcresources import ResourceManager, world_gen as wg, utils

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
    # Surface Builders
    surface_builder(rm, 'badlands', wg.configure('tfc:badlands'))
    surface_builder(rm, 'volcanic', wg.configure('tfc:with_volcanoes', {'parent': 'tfc:normal'}))
    surface_builder(rm, 'normal', wg.configure('tfc:normal'))
    surface_builder(rm, 'icebergs', wg.configure('tfc:icebergs'))
    surface_builder(rm, 'mountains', wg.configure('tfc:mountains'))
    surface_builder(rm, 'volcanic_mountains', wg.configure('tfc:with_volcanoes', {'parent': 'tfc:mountains'}))
    surface_builder(rm, 'shore', wg.configure('tfc:shore'))

    # Configured Features
    rm.feature('erosion', wg.configure('tfc:erosion'))
    rm.feature('ice_and_snow', wg.configure('tfc:ice_and_snow'))
    for block in ('packed', 'blue'):
        rm.feature('iceberg_%s' % block, wg.configure_decorated(wg.configure('tfc:iceberg', {'state': wg.block_state('minecraft:%s_ice' % block)}), decorate_chance(14), 'minecraft:square', decorate_climate(max_temp=-23)))
        rm.feature('iceberg_%s_rare' % block, wg.configure_decorated(wg.configure('tfc:iceberg', {'state': wg.block_state('minecraft:%s_ice' % block)}), decorate_chance(30), 'minecraft:square', decorate_climate(max_temp=-18)))

    rm.feature('lake', wg.configure_decorated(wg.configure('tfc:lake'), decorate_chance(25), 'minecraft:heightmap_world_surface', 'minecraft:square'))
    rm.feature('flood_fill_lake', wg.configure_decorated(wg.configure('tfc:flood_fill_lake', {
        'state': 'minecraft:water',
        'replace_fluids': [],
    }), decorate_chance(5), 'minecraft:square', 'minecraft:heightmap_world_surface'))
    rm.feature('underground_flood_fill_lake', wg.configure_decorated(wg.configure('tfc:flood_fill_lake', {
        'state': 'minecraft:water',
        'replace_fluids': [],
    }), decorate_count(3), 'minecraft:square', ('minecraft:range', {'bottom_offset': 16, 'top_offset': 16, 'maximum': 100})))

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
    }), ('minecraft:chance', {'chance': 20}), 'minecraft:square', 'minecraft:heightmap_world_surface', ('tfc:climate', {'min_rainfall': 175})))
    rm.feature('water_clay_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 2,
        'max_radius': 3,
        'height': 2,
        'states': clay
    }), ('minecraft:chance', {'chance': 10}), 'minecraft:square', 'minecraft:heightmap_world_surface', 'tfc:near_water'))
    rm.feature('peat_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 5,
        'max_radius': 9,
        'height': 7,
        'states': [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:peat'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:peat_grass'} for soil in SOIL_BLOCK_VARIANTS]
    }), ('minecraft:chance', {'chance': 10}), 'minecraft:square', 'minecraft:heightmap_world_surface', ('tfc:climate', {'min_rainfall': 350, 'min_temperature': 12})))
    rm.feature('loam_disc', wg.configure_decorated(wg.configure('tfc:soil_disc', {
        'min_radius': 3,
        'max_radius': 5,
        'height': 3,
        'states': [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:dirt/loam'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:grass/loam'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:clay/%s' % soil, 'with': 'tfc:clay/loam'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:clay_grass/%s' % soil, 'with': 'tfc:clay_grass/loam'} for soil in SOIL_BLOCK_VARIANTS]
    }), ('minecraft:chance', {'chance': 120}), 'minecraft:square', 'minecraft:heightmap_world_surface', ('tfc:climate', {'min_rainfall': 400})))

    for step, prefix in (('air', ''), ('liquid', 'underwater_')):
        rm.feature('%scave_spike' % prefix, wg.configure_decorated(wg.configure('tfc:cave_spike'), ('minecraft:carving_mask', {'step': step, 'probability': 0.09})))
        rm.feature('%slarge_cave_spike' % prefix, wg.configure_decorated(wg.configure('tfc:large_cave_spike'), ('tfc:bounded_carving_mask', {'step': step, 'probability': 0.006, 'max_y': 45})))

    rm.feature('calcite', wg.configure_decorated(wg.configure('tfc:thin_spike', {
        'state': 'tfc:calcite',
        'radius': 5,
        'tries': 20,
        'min_height': 2,
        'max_height': 5
    }), ('minecraft:count', {'count': 4}), 'minecraft:square', ('minecraft:range_biased', {'bottom_offset': 8, 'top_offset': 8, 'maximum': 100})))
    rm.feature('mega_calcite', wg.configure_decorated(wg.configure('tfc:thin_spike', {
        'state': 'tfc:calcite',
        'radius': 12,
        'tries': 70,
        'min_height': 3,
        'max_height': 9
    }), decorate_chance(20), 'minecraft:square', ('minecraft:range_biased', {'bottom_offset': 8, 'top_offset': 8, 'maximum': 60})))

    rm.feature('icicle', wg.configure_decorated(wg.configure('tfc:thin_spike', {
        'state': 'tfc:icicle',
        'radius': 10,
        'tries': 50,
        'min_height': 2,
        'max_height': 5
    }), ('minecraft:count', {'count': 3}), 'minecraft:square', ('minecraft:range_biased', {'bottom_offset': 8, 'top_offset': 8, 'maximum': 128}), decorate_climate(max_temp=-4)))

    for boulder_cfg in (('raw_boulder', 'raw', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.feature(boulder_cfg[0], wg.configure_decorated(wg.configure('tfc:boulder', {
            'base_type': boulder_cfg[1],
            'decoration_type': boulder_cfg[2]
        }), 'minecraft:square', 'minecraft:heightmap_world_surface', ('minecraft:chance', {'chance': 12}), 'tfc:flat_enough'))

    rm.feature('volcano_rivulet', wg.configure_decorated(wg.configure('tfc:rivulet', {
        'state': 'minecraft:magma_block'
    }), decorate_count(2), 'minecraft:square', ('tfc:volcano', {'distance': 0.7})))

    rm.feature('volcano_caldera', wg.configure_decorated(wg.configure('tfc:flood_fill_lake', {
        'overfill': True,
        'replace_fluids': ['minecraft:water'],
        'state': 'minecraft:lava'
    }), ('tfc:volcano', {'center': True}), 'minecraft:heightmap_world_surface'))

    # todo: this might be cool in cold mountain biomes, high altitude
    rm.feature('ice_rivulet', wg.configure_decorated(wg.configure('tfc:rivulet', {
        'state': 'minecraft:packed_ice'
    }), decorate_chance(7), 'minecraft:square', 'minecraft:heightmap_world_surface'))

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
    rm.feature(('tree', 'sequoia_large'), wg.configure('tfc:stacked_tree', stacked_config('sequoia', 8, 16, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'spruce'), wg.configure('tfc:random_tree', random_config('spruce', 7)))
    rm.feature(('tree', 'spruce_large'), wg.configure('tfc:stacked_tree', stacked_config('spruce', 5, 9, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'sycamore'), wg.configure('tfc:overlay_tree', overlay_config('sycamore', 2, 5)))
    rm.feature(('tree', 'sycamore_large'), wg.configure('tfc:random_tree', random_config('sycamore', 5, 2, True)))
    rm.feature(('tree', 'white_cedar'), wg.configure('tfc:overlay_tree', overlay_config('white_cedar', 2, 4)))
    rm.feature(('tree', 'white_cedar_large'), wg.configure('tfc:overlay_tree', overlay_config('white_cedar', 2, 5, 1, 1, True)))
    rm.feature(('tree', 'willow'), wg.configure('tfc:random_tree', random_config('willow', 7)))
    rm.feature(('tree', 'willow_large'), wg.configure('tfc:random_tree', random_config('willow', 14, 1, True)))

    def vein_ore_blocks(vein: Vein, rock: str) -> List[Dict[str, Any]]:
        ore_blocks = [{
            'weight': vein.poor,
            'block': 'tfc:ore/poor_%s/%s' % (vein.ore, rock)
        }, {
            'weight': vein.normal,
            'block': 'tfc:ore/normal_%s/%s' % (vein.ore, rock)
        }, {
            'weight': vein.rich,
            'block': 'tfc:ore/rich_%s/%s' % (vein.ore, rock)
        }]
        if vein.spoiler_ore is not None and rock in vein.spoiler_rocks:
            p = vein.spoiler_rarity * 0.01  # as a percentage of the overall vein
            ore_blocks.append({
                'weight': int(100 * p / (1 - p)),
                'block': 'tfc:ore/%s/%s' % (vein.spoiler_ore, rock)
            })
        return ore_blocks

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
                    'ore': vein_ore_blocks(vein, rock)
                } for rock in rocks],
                'indicator': {
                    'rarity': 12,
                    'blocks': [{
                        'block': 'tfc:ore/small_%s' % vein.ore
                    }]
                },
                'salt': vein_salt(vein_name)
            }))
        else:  # non-graded ore vein (mineral)
            rm.feature(('vein', vein_name), wg.configure('tfc:%s_vein' % vein.type, {
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density * 0.01,
                'blocks': [{
                    'stone': ['tfc:rock/raw/%s' % rock],
                    'ore': [{'block': 'tfc:ore/%s/%s' % (vein.ore, rock)}]
                } for rock in rocks],
                'salt': vein_salt(vein_name)
            }))

    rm.feature(('vein', 'gravel'), wg.configure('tfc:cluster_vein', {
        'rarity': 30,
        'min_y': 0,
        'max_y': 180,
        'size': 20,
        'density': 1,
        'blocks': [{
            'stone': ['tfc:rock/raw/%s' % rock],
            'ore': [{'block': 'tfc:rock/gravel/%s' % rock}]
        } for rock in ROCKS.keys()],
        'salt': vein_salt('gravel')
    }))

    # todo: change to use cave biomes in 1.17
    rm.feature('cave_vegetation', wg.configure_decorated(wg.configure('tfc:cave_vegetation', {
        'blocks': [{
            'stone': ['tfc:rock/raw/%s' % rock],
            'ore': [{'block': 'tfc:rock/mossy_cobble/%s' % rock, 'weight': 8},
                    {'block': 'tfc:rock/cobble/%s' % rock, 'weight': 2}]
        } for rock in ROCKS.keys()]
    }), decorate_climate(16, 32, 150, 470, fuzzy=True), decorate_carving_mask(0.01, 0, 130), decorate_range(64, 130)))

    rm.feature('ice_cave', wg.configure_decorated(wg.configure('tfc:ice_cave'), decorate_climate(-50, 0, 0, 500), decorate_carving_mask(0.05, 0, 130), decorate_range(20, 130)))

    # Plants
    rm.feature(('plant', 'allium'), wg.configure_decorated(plant_feature('tfc:plant/allium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(10, 18, 150, 400)))
    rm.feature(('plant', 'athyrium_fern'), wg.configure_decorated(plant_feature('tfc:plant/athyrium_fern[age=1,stage=1]', 1, 10, 128, True), decorate_chance(1), 'minecraft:square', decorate_climate(20, 30, 200, 500)))
    rm.feature(('plant', 'badderlocks'), wg.configure_decorated(plant_feature('tfc:plant/badderlocks[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(-20, 20, 150, 500)))
    rm.feature(('plant', 'barrel_cactus'), wg.configure_decorated(plant_feature('tfc:plant/barrel_cactus[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), 'minecraft:square', decorate_climate(-6, 50, 0, 85)))
    rm.feature(('plant', 'black_orchid'), wg.configure_decorated(plant_feature('tfc:plant/black_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(30, 41, 290, 410)))
    rm.feature(('plant', 'blood_lily'), wg.configure_decorated(plant_feature('tfc:plant/blood_lily[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(33, 45, 200, 500)))
    rm.feature(('plant', 'blue_orchid'), wg.configure_decorated(plant_feature('tfc:plant/blue_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(8, 16, 250, 390)))
    rm.feature(('plant', 'butterfly_milkweed'), wg.configure_decorated(plant_feature('tfc:plant/butterfly_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-40, 25, 75, 300)))
    rm.feature(('plant', 'calendula'), wg.configure_decorated(plant_feature('tfc:plant/calendula[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-5, 15, 130, 300)))
    rm.feature(('plant', 'canna'), wg.configure_decorated(plant_feature('tfc:plant/canna[age=1,stage=1]', 1, 10, 128, True), decorate_chance(1), 'minecraft:square', decorate_climate(30, 50, 270, 500)))
    rm.feature(('plant', 'cattail'), wg.configure_decorated(plant_feature('tfc:plant/cattail[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(-10, 50, 150, 500)))
    rm.feature(('plant', 'coontail'), wg.configure_decorated(plant_feature('tfc:plant/coontail[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(5, 50, 250, 500)))
    rm.feature(('plant', 'dandelion'), wg.configure_decorated(plant_feature('tfc:plant/dandelion[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(0, 22, 120, 400)))
    rm.feature(('plant', 'dead_bush'), wg.configure_decorated(plant_feature('tfc:plant/dead_bush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), 'minecraft:square', decorate_climate(10, 50, 0, 120)))
    rm.feature(('plant', 'duckweed'), wg.configure_decorated(plant_feature('tfc:plant/duckweed[age=1,stage=1]', 1, 7, 100), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 25, 0, 500), ('tfc:shoreline', {'radius': 9})))
    rm.feature(('plant', 'eel_grass'), wg.configure_decorated(plant_feature('tfc:plant/eel_grass[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(-10, 25, 200, 500)))
    rm.feature(('plant', 'field_horsetail'), wg.configure_decorated(plant_feature('tfc:plant/field_horsetail[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(10, 26, 300, 500)))
    rm.feature(('plant', 'fountain_grass'), wg.configure_decorated(plant_feature('tfc:plant/fountain_grass[age=1,stage=1]', 1, 20), decorate_chance(1), 'minecraft:square', decorate_climate(-12, 40, 75, 150)))
    rm.feature(('plant', 'foxglove'), wg.configure_decorated(plant_feature('tfc:plant/foxglove[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), 'minecraft:square', decorate_climate(15, 29, 150, 300)))
    rm.feature(('plant', 'goldenrod'), wg.configure_decorated(plant_feature('tfc:plant/goldenrod[age=1,stage=1]', 1, 10, 128, True), decorate_chance(1), 'minecraft:square', decorate_climate(17, 33, 75, 310)))
    rm.feature(('plant', 'grape_hyacinth'), wg.configure_decorated(plant_feature('tfc:plant/grape_hyacinth[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(5, 33, 150, 250)))
    rm.feature(('plant', 'gutweed'), wg.configure_decorated(plant_feature('tfc:plant/gutweed[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), 'minecraft:square', decorate_climate(-50, 50, 100, 500)))
    rm.feature(('plant', 'guzmania'), wg.configure_decorated(plant_feature('tfc:plant/guzmania[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), 'minecraft:square', decorate_climate(15, 29, 290, 480)))
    rm.feature(('plant', 'houstonia'), wg.configure_decorated(plant_feature('tfc:plant/houstonia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-46, 10, 150, 500)))
    rm.feature(('plant', 'labrador_tea'), wg.configure_decorated(plant_feature('tfc:plant/labrador_tea[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-5, 9, 200, 380)))
    rm.feature(('plant', 'lady_fern'), wg.configure_decorated(plant_feature('tfc:plant/lady_fern[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-50, 16, 200, 490)))
    rm.feature(('plant', 'licorice_fern'), wg.configure_decorated(plant_feature('tfc:plant/licorice_fern[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), 'minecraft:square', decorate_climate(-15, 25, 300, 400)))
    rm.feature(('plant', 'laminaria'), wg.configure_decorated(plant_feature('tfc:plant/laminaria[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), 'minecraft:square', decorate_climate(-50, 12, 100, 500)))
    rm.feature(('plant', 'lotus'), wg.configure_decorated(plant_feature('tfc:plant/lotus[age=1,stage=1]', 1, 7, 100), decorate_chance(5), 'minecraft:square', decorate_climate(15, 50, 0, 500), ('tfc:shoreline', {'radius': 9})))
    rm.feature(('plant', 'manatee_grass'), wg.configure_decorated(plant_feature('tfc:plant/manatee_grass[age=1,stage=1,fluid=empty]', 1, 15, 128, water_plant=True), decorate_chance(1), 'minecraft:square', decorate_climate(25, 50, 250, 500)))
    rm.feature(('plant', 'marigold'), wg.configure_decorated(plant_feature('tfc:plant/marigold[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(20, 50, 50, 390)))
    rm.feature(('plant', 'meads_milkweed'), wg.configure_decorated(plant_feature('tfc:plant/meads_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-10, 14, 130, 380)))
    rm.feature(('plant', 'milfoil'), wg.configure_decorated(plant_feature('tfc:plant/milfoil[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), 'minecraft:square', decorate_climate(5, 50, 250, 500)))
    rm.feature(('plant', 'morning_glory'), wg.configure_decorated(plant_feature('tfc:plant/morning_glory[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), 'minecraft:square', decorate_climate(-11, 19, 300, 500)))
    rm.feature(('plant', 'moss'), wg.configure_decorated(plant_feature('tfc:plant/moss[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), 'minecraft:square', decorate_climate(-7, 30, 250, 450)))
    rm.feature(('plant', 'nasturtium'), wg.configure_decorated(plant_feature('tfc:plant/nasturtium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-19, 0, 150, 380)))
    rm.feature(('plant', 'orchard_grass'), wg.configure_decorated(plant_feature('tfc:plant/orchard_grass[age=1,stage=1]', 1, 20), decorate_chance(1), 'minecraft:square', decorate_climate(-29, 27, 75, 300)))
    rm.feature(('plant', 'ostrich_fern'), wg.configure_decorated(plant_feature('tfc:plant/ostrich_fern[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), 'minecraft:square', decorate_climate(-49, 14, 290, 470)))
    rm.feature(('plant', 'oxeye_daisy'), wg.configure_decorated(plant_feature('tfc:plant/oxeye_daisy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(10, 40, 120, 300)))
    rm.feature(('plant', 'pampas_grass'), wg.configure_decorated(plant_feature('tfc:plant/pampas_grass[age=1,stage=1,part=lower]', 1, 15, True, tall_plant=True), decorate_chance(1), 'minecraft:square', decorate_climate(20, 50, 0, 200)))
    rm.feature(('plant', 'perovskia'), wg.configure_decorated(plant_feature('tfc:plant/perovskia[age=1,stage=1]', 1, 15, 128, True), decorate_chance(1), 'minecraft:square', decorate_climate(-50, 20, 0, 200)))
    rm.feature(('plant', 'pistia'), wg.configure_decorated(plant_feature('tfc:plant/pistia[age=1,stage=1]', 1, 7, 100), decorate_chance(5), 'minecraft:square', decorate_climate(10, 45, 0, 400), ('tfc:shoreline', {'radius': 9})))
    rm.feature(('plant', 'poppy'), wg.configure_decorated(plant_feature('tfc:plant/poppy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-40, 36, 150, 250)))
    rm.feature(('plant', 'primrose'), wg.configure_decorated(plant_feature('tfc:plant/primrose[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 33, 150, 300)))
    rm.feature(('plant', 'pulsatilla'), wg.configure_decorated(plant_feature('tfc:plant/pulsatilla[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-50, 30, 50, 200)))
    rm.feature(('plant', 'reindeer_lichen'), wg.configure_decorated(plant_feature('tfc:plant/reindeer_lichen[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), 'minecraft:square', decorate_climate(10, 33, 50, 470)))
    rm.feature(('plant', 'rose'), wg.configure_decorated(plant_feature('tfc:plant/rose[age=1,stage=1,part=lower]', 1, 15, 128, True, tall_plant=True), decorate_chance(1), 'minecraft:square', decorate_climate(-5, 20, 150, 300)))
    rm.feature(('plant', 'ryegrass'), wg.configure_decorated(plant_feature('tfc:plant/ryegrass[age=1,stage=1]', 1, 20), decorate_chance(1), 'minecraft:square', decorate_climate(-10, 35, 150, 320)))
    rm.feature(('plant', 'sacred_datura'), wg.configure_decorated(plant_feature('tfc:plant/sacred_datura[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(18, 31, 75, 150)))
    rm.feature(('plant', 'sago'), wg.configure_decorated(plant_feature('tfc:plant/sago[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), 'minecraft:square', decorate_climate(-10, 50, 200, 500)))
    rm.feature(('plant', 'sagebrush'), wg.configure_decorated(plant_feature('tfc:plant/sagebrush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 50, 0, 120)))
    rm.feature(('plant', 'sapphire_tower'), wg.configure_decorated(plant_feature('tfc:plant/sapphire_tower[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), 'minecraft:square', decorate_climate(16, 39, 75, 200)))
    rm.feature(('plant', 'sargassum'), wg.configure_decorated(plant_feature('tfc:plant/sargassum[age=1,stage=1]', 1, 7, 100), decorate_chance(5), 'minecraft:square', decorate_climate(0, 25, 0, 500), ('tfc:shoreline', {'radius': 6})))
    rm.feature(('plant', 'scutch_grass'), wg.configure_decorated(plant_feature('tfc:plant/scutch_grass[age=1,stage=1]', 1, 20), decorate_chance(1), 'minecraft:square', decorate_climate(0, 50, 150, 500)))
    rm.feature(('plant', 'snapdragon_pink'), wg.configure_decorated(plant_feature('tfc:plant/snapdragon_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(24, 36, 150, 300)))
    rm.feature(('plant', 'snapdragon_red'), wg.configure_decorated(plant_feature('tfc:plant/snapdragon_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(24, 36, 150, 300)))
    rm.feature(('plant', 'snapdragon_white'), wg.configure_decorated(plant_feature('tfc:plant/snapdragon_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(24, 36, 150, 300)))
    rm.feature(('plant', 'snapdragon_yellow'), wg.configure_decorated(plant_feature('tfc:plant/snapdragon_yellow[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(24, 36, 150, 300)))
    rm.feature(('plant', 'spanish_moss'), wg.configure_decorated(plant_feature('tfc:plant/spanish_moss[age=1,stage=1,hanging=false]', 1, 5), decorate_chance(5), 'minecraft:square', decorate_climate(35, 41, 400, 500)))
    rm.feature(('plant', 'star_grass'), wg.configure_decorated(plant_feature('tfc:plant/star_grass[age=1,stage=1,fluid=empty]', 1, 15, 128, water_plant=True), decorate_chance(1), 'minecraft:square', decorate_climate(-50, 50, 50, 260)))
    rm.feature(('plant', 'strelitzia'), wg.configure_decorated(plant_feature('tfc:plant/strelitzia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(35, 50, 50, 300)))
    rm.feature(('plant', 'switchgrass'), wg.configure_decorated(plant_feature('tfc:plant/switchgrass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(-29, 29, 110, 390)))
    rm.feature(('plant', 'sword_fern'), wg.configure_decorated(plant_feature('tfc:plant/sword_fern[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-40, 25, 100, 500)))
    rm.feature(('plant', 'tall_fescue_grass'), wg.configure_decorated(plant_feature('tfc:plant/tall_fescue_grass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True), decorate_chance(2), 'minecraft:square', decorate_climate(-10, 15, 280, 430)))
    rm.feature(('plant', 'timothy_grass'), wg.configure_decorated(plant_feature('tfc:plant/timothy_grass[age=1,stage=1]', 1, 20), decorate_chance(1), 'minecraft:square', decorate_climate(15, 29, 289, 500)))
    rm.feature(('plant', 'toquilla_palm'), wg.configure_decorated(plant_feature('tfc:plant/toquilla_palm[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), 'minecraft:square', decorate_climate(25, 50, 250, 500)))
    rm.feature(('plant', 'trillium'), wg.configure_decorated(plant_feature('tfc:plant/trillium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(10, 33, 150, 300)))
    rm.feature(('plant', 'tropical_milkweed'), wg.configure_decorated(plant_feature('tfc:plant/tropical_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(28, 41, 120, 300)))
    rm.feature(('plant', 'tulip_orange'), wg.configure_decorated(plant_feature('tfc:plant/tulip_orange[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 0, 100, 200)))
    rm.feature(('plant', 'tulip_pink'), wg.configure_decorated(plant_feature('tfc:plant/tulip_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 0, 100, 200)))
    rm.feature(('plant', 'tulip_red'), wg.configure_decorated(plant_feature('tfc:plant/tulip_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 0, 100, 200)))
    rm.feature(('plant', 'tulip_white'), wg.configure_decorated(plant_feature('tfc:plant/tulip_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 0, 100, 200)))
    rm.feature(('plant', 'turtle_grass'), wg.configure_decorated(plant_feature('tfc:plant/turtle_grass[age=1,stage=1,fluid=empty]', 1, 15, 128, water_plant=True), decorate_chance(1), 'minecraft:square', decorate_climate(-50, 25, 240, 500)))
    rm.feature(('plant', 'vriesea'), wg.configure_decorated(plant_feature('tfc:plant/vriesea[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), 'minecraft:square', decorate_climate(22, 31, 200, 400)))
    rm.feature(('plant', 'water_canna'), wg.configure_decorated(plant_feature('tfc:plant/water_canna[age=1,stage=1]', 1, 7, 128, True), decorate_chance(1), 'minecraft:square', decorate_climate(0, 36, 150, 500), ('tfc:shoreline', {'radius': 9})))
    rm.feature(('plant', 'water_lily'), wg.configure_decorated(plant_feature('tfc:plant/water_lily[age=1,stage=1]', 1, 7, 100), decorate_chance(5), 'minecraft:square', decorate_climate(-5, 38, 0, 500), ('tfc:shoreline', {'radius': 9})))
    rm.feature(('plant', 'yucca'), wg.configure_decorated(plant_feature('tfc:plant/yucca[age=1,stage=1]', 1, 15, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-34, 36, 0, 75)))

    rm.feature(('plant', 'hanging_vines'), wg.configure_decorated(tall_feature('tfc:weeping_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 21), 'minecraft:heightmap_world_surface', 'minecraft:square', decorate_climate(16, 32, 150, 470, True, fuzzy=True)))
    rm.feature(('plant', 'hanging_vines_cave'), wg.configure_decorated(tall_feature('tfc:weeping_vines', 'tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 22), decorate_carving_mask(0.003, 0, 190), decorate_range(64, 130), decorate_climate(16, 32, 150, 470, True, fuzzy=True)))
    rm.feature(('plant', 'liana'), wg.configure_decorated(tall_feature('tfc:weeping_vines', 'tfc:plant/liana_plant', 'tfc:plant/liana', 40, 10, 8, 16), decorate_carving_mask(0.003, 0, 110), decorate_range(64, 110), decorate_climate(16, 32, 150, 470, True, fuzzy=True)))
    rm.feature(('plant', 'ivy'), wg.configure_decorated(vine_feature('tfc:plant/ivy', 15, 7, 96, 150), decorate_climate(-4, 14, 90, 450, True, fuzzy=True), decorate_chance(5)))
    rm.feature(('plant', 'jungle_vines'), wg.configure_decorated(vine_feature('tfc:plant/jungle_vines', 33, 7, 64, 160), decorate_climate(16, 32, 150, 470, True, fuzzy=True), decorate_chance(5)))
    rm.feature(('plant', 'tree_fern'), wg.configure_decorated(tall_feature('tfc:twisting_vines', 'tfc:plant/tree_fern_plant', 'tfc:plant/tree_fern', 8, 7, 2, 6), 'minecraft:heightmap_world_surface', decorate_chance(5), 'minecraft:square', decorate_climate(19, 50, 300, 500)))
    rm.feature(('plant', 'arundo'), wg.configure_decorated(tall_feature('tfc:twisting_vines', 'tfc:plant/arundo_plant', 'tfc:plant/arundo', 70, 7, 5, 8), 'minecraft:heightmap_world_surface', decorate_chance(3), 'minecraft:square', decorate_climate(5, 22, 100, 500), ('tfc:near_water', {'radius': 6})))
    rm.feature(('plant', 'winged_kelp'), wg.configure_decorated(tall_feature('tfc:kelp', 'tfc:plant/winged_kelp_plant', 'tfc:plant/winged_kelp', 64, 12, 14, 21), 'minecraft:top_solid_heightmap', 'minecraft:square', decorate_chance(2), decorate_climate(-15, 15, 0, 450, fuzzy=True)))
    rm.feature(('plant', 'leafy_kelp'), wg.configure_decorated(tall_feature('tfc:kelp', 'tfc:plant/leafy_kelp_plant', 'tfc:plant/leafy_kelp', 64, 12, 14, 21), 'minecraft:top_solid_heightmap', 'minecraft:square', decorate_chance(2), decorate_climate(-20, 20, 0, 500, fuzzy=True)))
    rm.feature(('plant', 'giant_kelp'), wg.configure_decorated(random_patch('tfc:plant/giant_kelp_flower[age=0,fluid=empty]', {'type': 'tfc:kelp_tree'}, 2, 10, 20, target='water'), 'minecraft:top_solid_heightmap', 'minecraft:square', decorate_climate(-18, 18, 0, 500, fuzzy=True)))
    rm.feature(('plant', 'moss_cover'), wg.configure_decorated(plant_feature('tfc:plant/moss[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_climate(15, 35, 200, 500, True, fuzzy=True), decorate_carving_mask(0.002, 0, 130), decorate_range(96, 130)))
    rm.feature(('plant', 'morning_glory_cover'), wg.configure_decorated(plant_feature('tfc:plant/morning_glory[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_climate(5, 19, 160, 440, True, fuzzy=True), decorate_carving_mask(0.002, 0, 130), decorate_range(64, 130)))
    rm.feature(('plant', 'reindeer_lichen_cover'), wg.configure_decorated(plant_feature('tfc:plant/reindeer_lichen[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_climate(-7, 7, 110, 390, True, fuzzy=True), decorate_carving_mask(0.002, 0, 130), decorate_range(64, 130)))

    for berry, info in BERRIES.items():
        config = {
            'min_temperature': info.min_temp,
            'max_temperature': info.max_temp,
            'min_rainfall': info.min_rain,
            'max_rainfall': info.max_rain,
            'min_forest': info.min_forest,
            'max_forest': info.max_forest,
            'fuzzy': False
        }
        rm.feature(('plant', berry), wg.configure_decorated(wg.configure('tfc:berry_bushes', {'state': 'tfc:berry_bush/%s_bush' % berry}), 'minecraft:heightmap_world_surface', 'minecraft:square', ('tfc:climate', config), decorate_chance(15)))
    for fruit, info in FRUITS.items():
        config = {
            'min_temperature': info.min_temp,
            'max_temperature': info.max_temp,
            'min_rainfall': info.min_rain,
            'max_rainfall': info.max_rain,
            'max_forest': 'normal'
        }
        feature = 'tfc:fruit_trees'
        state = 'tfc:fruit_tree/%s_growing_branch' % fruit
        if fruit == 'banana':
            feature = 'tfc:bananas'
            state = 'tfc:fruit_tree/banana_plant'
        rm.feature(('plant', fruit), wg.configure_decorated(wg.configure(feature, {'state': state}), 'minecraft:heightmap_world_surface', 'minecraft:square', ('tfc:climate', config), decorate_chance(200)))

    # todo: convert the creeping plant blocks to use the target climate thing natively in the spreadsheet rather than here

    rm.feature('bamboo', wg.configure_decorated(wg.configure('minecraft:bamboo', {'probability': 0.2}), decorate_chance(30), decorate_climate(18, 28, 300, 500, True, fuzzy=True), ('minecraft:count_noise_biased', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), 'minecraft:square', 'minecraft:heightmap_world_surface'))

    rm.feature('coral_reef', wg.configure_decorated(wg.configure('minecraft:simple_random_selector', {'features': [
        wg.configure('tfc:coral_tree'),
        wg.configure('tfc:coral_mushroom'),
        wg.configure('tfc:coral_claw')
    ]}), decorate_climate(min_temp=18), ('minecraft:count_noise_biased', {
        "noise_to_count_ratio": 10,
        "noise_factor": 200,
        "noise_offset": 1
    }), 'minecraft:square', 'minecraft:top_solid_heightmap'))

    # Groundcover
    # Whitelist / Blacklist elements. Lenient block states are used as only the block is referenced.
    sand = ['tfc:sand/%s' % color for color in SAND_BLOCK_TYPES]
    gravel = ['tfc:rock/gravel/%s' % rock for rock in ROCKS.keys()]
    raw = ['tfc:rock/raw/%s' % rock for rock in ROCKS.keys()]
    grass = ['tfc:grass/%s' % soil for soil in SOIL_BLOCK_VARIANTS]

    rm.feature('driftwood', wg.configure_decorated(simple_patch_feature('tfc:groundcover/driftwood[facing=north,fluid=empty]', 1, 15, 10, sand + gravel, True), decorate_chance(6), 'minecraft:square', decorate_climate(-10, 50, 200, 500)))
    rm.feature('clam', wg.configure_decorated(simple_patch_feature('tfc:groundcover/clam[facing=north,fluid=empty]', 1, 15, 10, sand + gravel, True), decorate_chance(6), 'minecraft:square', decorate_climate(-50, 22, 10, 450)))
    rm.feature('mollusk', wg.configure_decorated(simple_patch_feature('tfc:groundcover/mollusk[facing=north,fluid=empty]', 1, 15, 10, sand + gravel, True), decorate_chance(6), 'minecraft:square', decorate_climate(-10, 30, 150, 500)))
    rm.feature('mussel', wg.configure_decorated(simple_patch_feature('tfc:groundcover/mussel[facing=north,fluid=empty]', 1, 15, 10, sand + gravel, True), decorate_chance(6), 'minecraft:square', decorate_climate(10, 50, 100, 500)))

    rm.feature('sticks_shore', wg.configure_decorated(simple_patch_feature('tfc:groundcover/stick[facing=north,fluid=empty]', 1, 15, 25, sand + gravel, True), decorate_chance(2), 'minecraft:square', decorate_climate(-50, 50, 100, 500)))
    rm.feature('seaweed', wg.configure_decorated(simple_patch_feature('tfc:groundcover/seaweed[facing=north,fluid=empty]', 1, 15, 10, sand + gravel, True), decorate_chance(5), 'minecraft:square', decorate_climate(-20, 50, 150, 500)))

    # Forest Only
    rm.feature('sticks_forest', wg.configure_decorated(simple_patch_feature('tfc:groundcover/stick[facing=north,fluid=empty]', 1, 15, 20), decorate_chance(3), 'minecraft:square', decorate_climate(-20, 50, 70, 500, True)))
    rm.feature('pinecone', wg.configure_decorated(simple_patch_feature('tfc:groundcover/pinecone[facing=north,fluid=empty]', 1, 15, 10), decorate_chance(5), 'minecraft:square', decorate_climate(-5, 33, 200, 500, True)))
    rm.feature('podzol', wg.configure_decorated(simple_patch_feature('tfc:groundcover/podzol[facing=north,fluid=empty]', 1, 5, 100), decorate_chance(5), 'minecraft:square', decorate_climate(8, 20, 180, 420, True, fuzzy=True)))
    rm.feature('salt_lick', wg.configure_decorated(simple_patch_feature('tfc:groundcover/salt_lick[facing=north,fluid=empty]', 1, 5, 100, raw), decorate_chance(110), 'minecraft:square', decorate_climate(5, 33, 100, 500, True)))
    rm.feature('dead_grass', wg.configure_decorated(simple_patch_feature('tfc:groundcover/dead_grass[facing=north,fluid=empty]', 1, 5, 100, grass), decorate_chance(70), 'minecraft:square', decorate_climate(10, 20, 0, 150, True, fuzzy=True)))

    # Loose Rocks - Both Surface + Underground
    rm.feature('surface_loose_rocks', wg.configure_decorated(wg.configure('tfc:loose_rock'), decorate_count(6), 'minecraft:square', 'minecraft:top_solid_heightmap'))

    # Underground decoration
    rm.feature('underground_loose_rocks', wg.configure_decorated(wg.configure('tfc:loose_rock'), decorate_carving_mask(0.05, 12, 90)))
    rm.feature('underground_guano', wg.configure_decorated(cave_patch_feature('tfc:groundcover/guano[facing=north,fluid=empty]', 5, 5, 60), decorate_chance(3), 'minecraft:square', decorate_range(80, 130)))

    # Carvers
    rm.carver('cave', wg.configure('tfc:cave', {'probability': 0.1}))
    rm.carver('canyon', wg.configure('tfc:canyon', {'probability': 0.015}))
    rm.carver('worley_cave', wg.configure('tfc:worley_cave'))

    rm.carver('underwater_cave', wg.configure('tfc:underwater_cave', {'probability': 0.03}))
    rm.carver('underwater_canyon', wg.configure('tfc:underwater_canyon', {'probability': 0.02}))

    # Biomes
    for temp in TEMPERATURES:
        for rain in RAINFALLS:
            biome(rm, 'badlands', temp, rain, 'mesa', 'tfc:badlands', lake_features=False)
            biome(rm, 'canyons', temp, rain, 'plains', 'tfc:volcanic', boulders=True, lake_features=False, volcano_features=True)
            biome(rm, 'low_canyons', temp, rain, 'swamp', 'tfc:normal', boulders=True, lake_features=False)
            biome(rm, 'plains', temp, rain, 'plains', 'tfc:normal')
            biome(rm, 'plateau', temp, rain, 'extreme_hills', 'tfc:mountains', boulders=True)
            biome(rm, 'hills', temp, rain, 'plains', 'tfc:normal')
            biome(rm, 'rolling_hills', temp, rain, 'plains', 'tfc:normal', boulders=True)
            biome(rm, 'lake', temp, rain, 'river', 'tfc:normal', spawnable=False)
            biome(rm, 'lowlands', temp, rain, 'swamp', 'tfc:normal', lake_features=False)
            biome(rm, 'mountains', temp, rain, 'extreme_hills', 'tfc:mountains')
            biome(rm, 'volcanic_mountains', temp, rain, 'extreme_hills', 'tfc:volcanic_mountains', volcano_features=True)
            biome(rm, 'old_mountains', temp, rain, 'extreme_hills', 'tfc:mountains')
            biome(rm, 'oceanic_mountains', temp, rain, 'extreme_hills', 'tfc:mountains', ocean_features=True, ocean_carvers=True)
            biome(rm, 'volcanic_oceanic_mountains', temp, rain, 'extreme_hills', 'tfc:volcanic_mountains', spawnable=False, ocean_carvers=True, ocean_features=True, volcano_features=True)
            biome(rm, 'ocean', temp, rain, 'ocean', 'tfc:normal', spawnable=False, ocean_carvers=True, ocean_features=True)
            biome(rm, 'ocean_reef', temp, rain, 'ocean', 'tfc:normal', spawnable=False, ocean_carvers=True, ocean_features=True, reef_features=True)
            biome(rm, 'deep_ocean', temp, rain, 'ocean', 'tfc:normal', spawnable=False, ocean_carvers=True, ocean_features=True)
            biome(rm, 'deep_ocean_trench', temp, rain, 'ocean', 'tfc:normal', spawnable=False, ocean_carvers=True, ocean_features=True)
            biome(rm, 'river', temp, rain, 'river', 'tfc:normal', spawnable=False)
            biome(rm, 'shore', temp, rain, 'beach', 'tfc:shore', spawnable=False, ocean_features=True)

            biome(rm, 'mountain_river', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'volcanic_mountain_river', temp, rain, 'extreme_hills', 'tfc:volcanic_mountains', spawnable=False, volcano_features=True)
            biome(rm, 'old_mountain_river', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'oceanic_mountain_river', temp, rain, 'river', 'tfc:mountains', spawnable=False, ocean_features=True, ocean_carvers=True)
            biome(rm, 'volcanic_oceanic_mountain_river', temp, rain, 'river', 'tfc:volcanic_mountains', spawnable=False, ocean_features=True, ocean_carvers=True, volcano_features=True)
            biome(rm, 'mountain_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'volcanic_mountain_lake', temp, rain, 'extreme_hills', 'tfc:volcanic_mountains', spawnable=False, volcano_features=True)
            biome(rm, 'old_mountain_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False)
            biome(rm, 'oceanic_mountain_lake', temp, rain, 'river', 'tfc:mountains', spawnable=False, ocean_features=True, ocean_carvers=True)
            biome(rm, 'volcanic_oceanic_mountain_lake', temp, rain, 'river', 'tfc:volcanic_mountains', spawnable=False, ocean_carvers=True, ocean_features=True, volcano_features=True)
            biome(rm, 'plateau_lake', temp, rain, 'extreme_hills', 'tfc:mountains', spawnable=False, boulders=True)


def surface_builder(rm: ResourceManager, name: str, surface_builder):
    # Add a surface builder, and also one with glaciers for cold biomes
    rm.surface_builder(name, surface_builder)


def forest_config(min_rain: float, max_rain: float, min_temp: float, max_temp: float, tree: str, old_growth: bool):
    cfg = {
        'min_rain': min_rain,
        'max_rain': max_rain,
        'min_temp': min_temp,
        'max_temp': max_temp,
        'log': 'tfc:wood/wood/%s' % tree,
        'leaves': 'tfc:wood/leaves/%s' % tree,
        'twig': 'tfc:wood/twig/%s' % tree,
        'fallen_leaves': 'tfc:wood/fallen_leaves/%s' % tree,
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


def vein_salt(vein_name: str) -> int:
    return int(hashlib.sha256(vein_name.encode('utf-8')).hexdigest(), 16) & 0xFFFFFFFF


def plant_feature(block: str, vertical_spread: int, horizontal_spread: int, count: int = None, requires_clay: bool = False, water_plant: bool = False, emergent_plant: bool = False, tall_plant: bool = False):
    placer = random_property_placer(block, 'age')
    target = 'air'
    if water_plant:
        target = 'water'
    if emergent_plant:
        target = 'emergent'
        placer = {'type': 'tfc:emergent_plant'}
    if tall_plant:
        placer = {'type': 'tfc:tall_plant'}
    whitelist = ['tfc:clay_grass/%s' % soil for soil in SOIL_BLOCK_VARIANTS] if requires_clay else []
    return random_patch(wg.block_state(block), placer, vertical_spread, horizontal_spread, count, not requires_clay and not water_plant, target=target, whitelist=whitelist)


def simple_patch_feature(block: str, vertical_spread: int, horizontal_spread: int, count: int = None, whitelist: List[Any] = None, water_agnostic: bool = False):
    return random_patch(wg.block_state(block), random_property_placer(block, 'facing'), vertical_spread, horizontal_spread, count, target='both' if water_agnostic else 'air', project='sea' if water_agnostic else 'land', whitelist=whitelist)


def cave_patch_feature(block: str, vertical_spread: int, horizontal_spread: int, count: Optional[int] = None) -> Dict[str, Any]:
    return random_patch(wg.block_state(block), random_property_placer(block, 'facing'), vertical_spread, horizontal_spread, count, False, project=False, only_underground=True)


def random_patch(state_provider: Union[str, Dict[str, Any]], block_placer: Dict[str, Any], vertical_spread: int, horizontal_spread: int, count: int, use_density: bool = False, target: Union[Literal['air', 'water', 'both', 'emergent'], str] = 'air', project: Union[Literal['sea', 'land', False], str] = 'land', only_underground: bool = False, blacklist: Optional[List[str]] = None, whitelist: Optional[List[str]] = None) -> Dict[str, Any]:
    """
    TFC Modified Random Patch Feature
    """
    if isinstance(state_provider, str) or 'type' not in state_provider:
        state_provider = simple_state_provider(state_provider)
    cfg = {
        'state_provider': state_provider,
        'block_placer': block_placer,
        'whitelist': whitelist if whitelist is not None else [],
        'blacklist': blacklist if blacklist is not None else [],
        'yspread': vertical_spread,
        'xspread': horizontal_spread,
        'zspread': horizontal_spread,
        'tries': count
    }
    if not project:
        cfg.update({'project': False})
    elif project == 'sea':
        cfg.update({'project_to_ocean_floor': True})

    if target == 'water':
        cfg.update({
            'can_replace_air': False,
            'can_replace_water': True,
            'project_to_ocean_floor': True  # project is inferred
        })
    elif target == 'both':
        cfg.update({
            'can_replace_water': True
        })
    elif target == 'emergent':
        cfg.update({
            'can_replace_air': False,
            'can_replace_surface_water': True,
            'project_to_ocean_floor': True  # project is inferred
        })

    if only_underground:
        cfg['only_underground'] = True

    if use_density:
        cfg['use_density'] = True

    return wg.configure('tfc:random_patch', cfg)


def simple_state_provider(block: Union[str, Dict[str, Any]]) -> Dict[str, Any]:
    return {
        'type': 'minecraft:simple_state_provider',
        'state': wg.block_state(block)
    }


def random_property_placer(block: str, property_name: str):
    return {
        'type': 'tfc:random_property',
        'block': wg.block_state(block)['Name'],  # Excludes, but allows extraneous property specifications
        'property': property_name
    }


def tall_feature(feature: str, state1: str, state2: str, tries: int, radius: int, min_height: int, max_height: int) -> Dict[str, Any]:
    return wg.configure(feature, {
        'body': state1,
        'head': state2,
        'tries': tries,
        'radius': radius,
        'minHeight': min_height,
        'maxHeight': max_height
    })


def vine_feature(state: str, tries: int, radius: int, min_height: int, max_height: int) -> Dict[str, Any]:
    return wg.configure('tfc:vines', {
        'state': state,
        'tries': tries,
        'radius': radius,
        'minHeight': min_height,
        'maxHeight': max_height
    })


def decorate_range(bottom: int, top: int) -> Tuple[str, Dict[str, Any]]:
    return ('minecraft:range', {
        'bottom_offset': bottom,
        'top_offset': 0,
        'maximum': top
    })


def decorate_carving_mask(probability: float, min_y: Optional[int] = None, max_y: Optional[int] = None) -> Tuple[str, Dict[str, Any]]:
    return ('tfc:bounded_carving_mask', utils.del_none({
        'step': 'air',
        'probability': probability,
        'min_y': min_y,
        'max_y': max_y
    }))


def decorate_climate(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None) -> Tuple[str, Dict[str, Any]]:
    return ('tfc:climate', utils.del_none({
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'max_forest': 'normal' if needs_forest else None,
        'fuzzy': fuzzy
    }))


def decorate_chance(chance: int) -> Tuple[str, Dict[str, Any]]:
    return 'minecraft:chance', {'chance': chance}


def decorate_count(count: int) -> Tuple[str, Dict[str, Any]]:
    return 'minecraft:count', {'count': count}


def biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, surface_builder: str, boulders: bool = False, spawnable: bool = True, ocean_carvers: bool = False, ocean_features: Union[bool, Literal['both']] = False, lake_features: Union[bool, Literal['default']] = 'default', volcano_features: bool = False, reef_features: bool = False):
    # Temperature properties
    if rain.id == 'arid':
        rain_type = 'none'
    elif temp.id in ('cold', 'frozen'):
        rain_type = 'snow'
        if category == 'ocean':
            surface_builder = 'tfc:icebergs'
    else:
        rain_type = 'rain'

    if ocean_features == 'both':  # Both applies both ocean + land features. True or false applies only one
        land_features = True
        ocean_features = True
    else:
        land_features = not ocean_features
    if lake_features == 'default':  # Default = Lakes are on all non-ocean biomes. True/False to force either way
        lake_features = not ocean_features

    # Features
    features = [
        ['tfc:erosion'],  # raw generation
        ['tfc:underground_flood_fill_lake'],  # lakes
        [],  # local modification
        [],  # underground structure
        [],  # surface structure
        [],  # strongholds
        ['tfc:vein/gravel', *['tfc:vein/%s' % vein for vein in ORE_VEINS.keys()]],  # underground ores
        [
            'tfc:cave_spike',
            'tfc:large_cave_spike',
            'tfc:underwater_cave_spike',
            'tfc:underwater_large_cave_spike',
            'tfc:water_spring',
            'tfc:lava_spring',
            'tfc:ice_cave',
            'tfc:calcite',
            'tfc:mega_calcite',
            'tfc:icicle',
            'tfc:underground_loose_rocks',
            'tfc:underground_guano'
        ],  # underground decoration
        [],  # vegetal decoration
        ['tfc:surface_loose_rocks']  # top layer modification
    ]
    if boulders:
        features[Decoration.SURFACE_STRUCTURES] += ['tfc:raw_boulder', 'tfc:cobble_boulder']
        if rain.id in ('damp', 'wet'):
            features[Decoration.SURFACE_STRUCTURES].append('tfc:mossy_boulder')

    # Oceans
    if ocean_features:
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/%s' % plant for plant, data in PLANTS.items() if data.type in OCEAN_PLANT_TYPES]

        if name == 'shore':
            features[Decoration.TOP_LAYER_MODIFICATION] += ['tfc:%s' % beach_item for beach_item in SHORE_DECORATORS]
        else:
            features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/giant_kelp', 'tfc:plant/winged_kelp', 'tfc:plant/leafy_kelp']  # Kelp

        features[Decoration.TOP_LAYER_MODIFICATION] += ['tfc:mussel', 'tfc:clam', 'tfc:mollusk']

        if temp.id in ('cold', 'frozen'):
            features[Decoration.LOCAL_MODIFICATIONS] += ['tfc:iceberg_packed', 'tfc:iceberg_blue', 'tfc:iceberg_packed_rare', 'tfc:iceberg_blue_rare']

    if reef_features and temp.id in ('lukewarm', 'warm'):
        features[Decoration.LOCAL_MODIFICATIONS].append('tfc:coral_reef')

    # Continental / Land Features
    if land_features:

        features[Decoration.LOCAL_MODIFICATIONS] += ['tfc:clay_disc', 'tfc:water_clay_disc', 'tfc:peat_disc']
        features[Decoration.VEGETAL_DECORATION] += ['tfc:forest', 'tfc:bamboo', 'tfc:cave_vegetation']
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/%s' % plant for plant in MISC_PLANT_FEATURES]

        features[Decoration.TOP_LAYER_MODIFICATION] += ['tfc:%s' % vegetal for vegetal in FOREST_DECORATORS if not ocean_features]

        # leaving freshwater plants to spawn anywhere so that they populate small lakes (something vanilla doesn't think to do)
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/%s' % plant for plant, data in PLANTS.items() if data.type not in OCEAN_PLANT_TYPES]
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/moss_cover', 'tfc:plant/reindeer_lichen_cover', 'tfc:plant/morning_glory_cover', 'tfc:plant/tree_fern', 'tfc:plant/arundo']
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/%s' % berry for berry in BERRIES]
        features[Decoration.VEGETAL_DECORATION] += ['tfc:plant/%s' % fruit for fruit in FRUITS]

    if volcano_features:
        features[Decoration.SURFACE_STRUCTURES] += ['tfc:volcano_rivulet', 'tfc:volcano_caldera']

    if lake_features:
        features[Decoration.LAKES] += ['tfc:flood_fill_lake', 'tfc:lake']

    features[Decoration.TOP_LAYER_MODIFICATION].append('tfc:ice_and_snow')  # This must go last

    # Carvers
    air_carvers = ['tfc:worley_cave', 'tfc:cave', 'tfc:canyon']
    water_carvers = []
    if ocean_carvers:
        water_carvers += ['tfc:underwater_cave', 'tfc:underwater_canyon']

    # Generate based on properties
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

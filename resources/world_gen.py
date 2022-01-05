# Handles generation of all world gen objects

import hashlib
import typing

from enum import IntEnum
from typing import Union

from mcresources import ResourceManager, utils
from mcresources.type_definitions import ResourceIdentifier, JsonObject, Json, VerticalAnchor
from constants import *


class BiomeTemperature(NamedTuple):
    id: str
    temperature: float
    water_color: float
    water_fog_color: float


class BiomeRainfall(NamedTuple):
    id: str
    downfall: float


TEMPERATURES: Tuple[BiomeTemperature, ...] = (
    BiomeTemperature('cold', 0, 3750089, 329011),
    BiomeTemperature('normal', 0.5, 4159204, 329011),
    BiomeTemperature('warm', 1.0, 4445678, 270131)
)

RAINFALLS: Tuple[BiomeRainfall, ...] = (
    BiomeRainfall('dry', 0.1),
    BiomeRainfall('normal', 0.5),
    BiomeRainfall('wet', 0.9)
)

DEFAULT_FOG_COLOR = 12638463
DEFAULT_SKY_COLOR = 0x84E6FF


class Decoration(IntEnum):
    EROSION = 0
    LAKES = 1
    SOIL_DISKS = 2
    VEINS = 3
    UNDERGROUND_DECORATION = 4
    LARGE_FEATURES = 5
    SURFACE_DECORATION = 6
    ICE_AND_SNOW = 7
    # unused - 8, 9


def generate(rm: ResourceManager):
    # Carvers
    rm.configured_carver('cave', 'tfc:cave', {
        'probability': 0.3,
        'y': height_provider(-56, 126),
        'yScale': uniform_float(0.1, 0.9),
        'lava_level': utils.vertical_anchor(8, 'above_bottom'),
        'aquifers_enabled': True,
        'horizontal_radius_multiplier': uniform_float(0.7, 1.4),
        'vertical_radius_multiplier': uniform_float(0.8, 1.3),
        'floor_level': uniform_float(-1, -0.4)
    })

    rm.configured_carver('canyon', 'tfc:canyon', {
        'probability': 0.03,
        'y': height_provider(10, 67),
        'yScale': 3,
        'lava_level': utils.vertical_anchor(8, 'above_bottom'),
        'aquifers_enabled': True,
        'vertical_rotation': uniform_float(-0.125, 0.125),
        'shape': {
            'distance_factor': uniform_float(0.75, 1.0),
            'thickness': trapezoid_float(0.0, 6.0, 2.0),
            'width_smoothness': 3,
            'horizontal_radius_factor': uniform_float(0.75, 1.0),
            'vertical_radius_default_factor': 1.0,
            'vertical_radius_center_factor': 0.0
        }
    })

    # Biomes
    for temp in TEMPERATURES:
        for rain in RAINFALLS:
            make_biome(rm, 'badlands', temp, rain, 'mesa', lake_features=False)
            make_biome(rm, 'inverted_badlands', temp, rain, 'mesa', lake_features=False)
            make_biome(rm, 'canyons', temp, rain, 'plains', boulders=True, lake_features=False, volcano_features=True, hot_spring_features=True)
            make_biome(rm, 'low_canyons', temp, rain, 'swamp', boulders=True, lake_features=False, hot_spring_features='empty')
            make_biome(rm, 'plains', temp, rain, 'plains')
            make_biome(rm, 'plateau', temp, rain, 'extreme_hills', boulders=True, hot_spring_features='empty')
            make_biome(rm, 'hills', temp, rain, 'plains')
            make_biome(rm, 'rolling_hills', temp, rain, 'plains', boulders=True, hot_spring_features='empty')
            make_biome(rm, 'lake', temp, rain, 'river', spawnable=False)
            make_biome(rm, 'lowlands', temp, rain, 'swamp', lake_features=False)
            make_biome(rm, 'mountains', temp, rain, 'extreme_hills')
            make_biome(rm, 'volcanic_mountains', temp, rain, 'extreme_hills', volcano_features=True, hot_spring_features=True)
            make_biome(rm, 'old_mountains', temp, rain, 'extreme_hills', hot_spring_features=True)
            make_biome(rm, 'oceanic_mountains', temp, rain, 'extreme_hills', ocean_features='both')
            make_biome(rm, 'volcanic_oceanic_mountains', temp, rain, 'extreme_hills', spawnable=False, ocean_features='both', volcano_features=True)
            make_biome(rm, 'ocean', temp, rain, 'ocean', spawnable=False, ocean_features=True)
            make_biome(rm, 'ocean_reef', temp, rain, 'ocean', spawnable=False, ocean_features=True, reef_features=True)
            make_biome(rm, 'deep_ocean', temp, rain, 'ocean', spawnable=False, ocean_features=True)
            make_biome(rm, 'deep_ocean_trench', temp, rain, 'ocean', spawnable=False, ocean_features=True)
            make_biome(rm, 'river', temp, rain, 'river', spawnable=False)
            make_biome(rm, 'shore', temp, rain, 'beach', spawnable=False, ocean_features=True)

            make_biome(rm, 'mountain_river', temp, rain, 'extreme_hills', spawnable=False)
            make_biome(rm, 'volcanic_mountain_river', temp, rain, 'extreme_hills', spawnable=False, volcano_features=True)
            make_biome(rm, 'old_mountain_river', temp, rain, 'extreme_hills', spawnable=False)
            make_biome(rm, 'oceanic_mountain_river', temp, rain, 'river', spawnable=False, ocean_features='both')
            make_biome(rm, 'volcanic_oceanic_mountain_river', temp, rain, 'river', spawnable=False, ocean_features='both', volcano_features=True)
            make_biome(rm, 'mountain_lake', temp, rain, 'extreme_hills', spawnable=False)
            make_biome(rm, 'volcanic_mountain_lake', temp, rain, 'extreme_hills', spawnable=False, volcano_features=True)
            make_biome(rm, 'old_mountain_lake', temp, rain, 'extreme_hills', spawnable=False)
            make_biome(rm, 'oceanic_mountain_lake', temp, rain, 'river', spawnable=False, ocean_features='both')
            make_biome(rm, 'volcanic_oceanic_mountain_lake', temp, rain, 'river', spawnable=False, ocean_features='both', volcano_features=True)
            make_biome(rm, 'plateau_lake', temp, rain, 'extreme_hills', boulders=True, spawnable=False)

    # Configured and Placed Features

    configured_placed_feature(rm, 'tfc:erosion')
    configured_placed_feature(rm, 'tfc:ice_and_snow')

    for block in ('packed', 'blue'):
        rm.configured_feature('iceberg_%s' % block, 'tfc:iceberg', {'state': utils.block_state('minecraft:%s_ice' % block)})
        rm.configured_feature('iceberg_%s_rare' % block, 'tfc:iceberg', {'state': utils.block_state('minecraft:%s_ice' % block)})

        rm.placed_feature('iceberg_%s' % block, 'tfc:iceberg_%s' % block, decorate_chance(14), decorate_square(), decorate_climate(max_temp=-23))
        rm.placed_feature('iceberg_%s_rare' % block, 'tfc:iceberg_%s_rare' % block, decorate_chance(30), decorate_square(), decorate_climate(max_temp=-18))

    rm.configured_feature('powder_snow', 'tfc:powder_snow', {'state': utils.block_state('minecraft:powder_snow')})
    rm.placed_feature('powder_snow', 'tfc:powder_snow', decorate_chance(15), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(max_temp=-17), 'tfc:flat_enough')

    rm.configured_feature('flood_fill_lake', 'tfc:flood_fill_lake', {
        'state': 'minecraft:water',
        'replace_fluids': [],
    })

    rm.placed_feature('flood_fill_lake', 'tfc:flood_fill_lake', decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'))
    rm.placed_feature('underground_flood_fill_lake', 'tfc:flood_fill_lake', decorate_chance(3), decorate_square(), decorate_range(-56, 63))

    for spring_cfg in (('water', 110), ('lava', 50)):
        rm.configured_feature('%s_spring' % spring_cfg[0], 'tfc:spring', {
            'state': utils.block_state('minecraft:%s[falling=true]' % spring_cfg[0]),
            'valid_blocks': ['tfc:rock/raw/%s' % rock for rock in ROCKS.keys()]
        })
        y = -64
        rm.placed_feature('%s_spring' % spring_cfg[0], 'tfc:%s_spring' % spring_cfg[0], decorate_count(spring_cfg[1]), decorate_square(), decorate_range(y, 180, bias='biased_to_bottom'))

    clay = [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:clay/%s' % soil} for soil in SOIL_BLOCK_VARIANTS] + [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:clay_grass/%s' % soil} for soil in SOIL_BLOCK_VARIANTS]

    # Clay discs have decorators added later, where they're paired with indicator plants
    configured_placed_feature(rm, 'clay_disc', 'tfc:soil_disc', {
        'min_radius': 3,
        'max_radius': 5,
        'height': 3,
        'states': clay
    })
    configured_placed_feature(rm, 'water_clay_disc', 'tfc:soil_disc', {
        'min_radius': 2,
        'max_radius': 3,
        'height': 2,
        'states': clay
    })

    rm.configured_feature('peat_disc', 'tfc:soil_disc', {
        'min_radius': 5,
        'max_radius': 9,
        'height': 7,
        'states': [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:peat'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:peat_grass'} for soil in SOIL_BLOCK_VARIANTS]
    })
    rm.placed_feature('peat_disc', 'tfc:peat_disc', decorate_chance(10), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_rain=350, min_temp=12))

    rm.configured_feature('cave_spike', 'tfc:cave_spike')
    rm.configured_feature('large_cave_spike', 'tfc:large_cave_spike')

    rm.placed_feature('cave_spike', 'tfc:cave_spike', decorate_carving_mask(), decorate_chance(0.09))
    rm.placed_feature('large_cave_spike', 'tfc:large_cave_spike', decorate_carving_mask(utils.vertical_anchor(25, 'above_bottom')), decorate_chance(0.006))

    rm.configured_feature('calcite', 'tfc:thin_spike', {
        'state': 'tfc:calcite',
        'radius': 5,
        'tries': 20,
        'min_height': 2,
        'max_height': 5
    })
    rm.configured_feature('mega_calcite', 'tfc:thin_spike', {
        'state': 'tfc:calcite',
        'radius': 12,
        'tries': 70,
        'min_height': 3,
        'max_height': 9
    })

    min_y = -56
    rm.placed_feature('calcite', 'tfc:calcite', decorate_count(4), decorate_square(), decorate_range(min_y, 60, bias='biased_to_bottom'))
    y1 = -56
    rm.placed_feature('mega_calcite', 'tfc:mega_calcite', decorate_chance(20), decorate_square(), decorate_range(y1, 30, bias='biased_to_bottom'))

    rm.configured_feature('icicle', 'tfc:thin_spike', {
        'state': 'tfc:icicle',
        'radius': 10,
        'tries': 50,
        'min_height': 2,
        'max_height': 5
    })
    y2 = -32
    rm.placed_feature('icicle', 'tfc:icicle', decorate_count(3), decorate_square(), decorate_range(y2, 100, bias='biased_to_bottom'), decorate_climate(max_temp=-4))

    for boulder_cfg in (('raw_boulder', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.configured_feature(boulder_cfg[0], 'tfc:boulder', {
            'states': [{
                'rock': 'tfc:rock/raw/%s' % rock,
                'blocks': ['tfc:rock/%s/%s' % (t, rock) for t in boulder_cfg[1:]]
            } for rock in ROCKS.keys()]
        })
        rm.placed_feature(boulder_cfg[0], 'tfc:%s' % boulder_cfg[0], decorate_chance(12), decorate_square(), decorate_heightmap('world_surface_wg'), 'tfc:flat_enough')

    rm.configured_feature('volcano_rivulet', 'tfc:rivulet', {'state': 'minecraft:magma_block'})
    rm.configured_feature('volcano_caldera', 'tfc:flood_fill_lake', {
        'overfill': True,
        'replace_fluids': ['minecraft:water'],
        'state': 'minecraft:lava'
    })

    rm.placed_feature('volcano_rivulet', 'tfc:volcano_rivulet', decorate_count(2), decorate_square(), ('tfc:volcano', {'distance': 0.7}))
    rm.placed_feature('volcano_caldera', 'tfc:volcano_caldera', ('tfc:volcano', {'center': True}), decorate_heightmap('world_surface_wg'))

    configured_placed_feature(rm, 'random_volcano_fissure', 'minecraft:simple_random_selector', {
        'features': count_weighted_list(
            ('tfc:topaz_volcano_fissure', 3),
            ('tfc:kimberlite_volcano_fissure', 1),
            ('tfc:volcano_fissure', 4)
        )
    })

    rocks = expand_rocks(['igneous_extrusive', 'igneous_intrusive', 'metamorphic'])
    for ore in ('kimberlite', 'topaz', ''):
        name = join_not_empty('_', ore, 'volcano_fissure')
        rm.configured_feature(name, 'tfc:fissure', {
            'wall_state': 'tfc:rock/raw/basalt',
            'fluid_state': 'minecraft:lava',
            'count': 3,
            'radius': 6,
            'decoration': {
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock],
                    'with': [{'block': 'tfc:ore/%s/%s' % (ore, rock)}]
                } for rock in rocks],
                'radius': 3,
                'count': 6,
                'rarity': 3
            }
        })
        rm.placed_feature(name, 'tfc:' + name, ('tfc:volcano', {'center': True}), decorate_heightmap('world_surface_wg'))

    # six different variants: both filled + not, and both sapphire, emerald, and no decoration
    for ore in ('sapphire', 'emerald', ''):
        for variant, fill_state, count in (('empty', 'minecraft:air', 2), ('', 'tfc:fluid/spring_water', 5)):
            configured_placed_feature(rm, join_not_empty('_', ore, variant, 'hot_spring'), 'tfc:hot_spring', {
                'fluid_state': fill_state,
                'radius': 14,
                'decoration': {
                    'blocks': [{
                        'replace': ['tfc:rock/raw/%s' % rock],
                        'with': [{'block': 'tfc:ore/%s/%s' % (ore, rock)}]
                    } for rock in rocks],
                    'radius': 5,
                    'count': count,
                    'rarity': 3
                } if ore != '' else None
            })

    rm.configured_feature('random_empty_hot_spring', 'minecraft:simple_random_selector', {
        'features': count_weighted_list(
            ('tfc:sapphire_empty_hot_spring', 1),
            ('tfc:emerald_empty_hot_spring', 1),
            ('tfc:empty_hot_spring', 2)
        )
    })
    rm.configured_feature('random_active_hot_spring', 'minecraft:simple_random_selector', {
        'features': count_weighted_list(
            ('tfc:sapphire_empty_hot_spring', 1),
            ('tfc:emerald_empty_hot_spring', 1),
            ('tfc:empty_hot_spring', 2),
            ('tfc:sapphire_hot_spring', 3),
            ('tfc:emerald_hot_spring', 3),
            ('tfc:hot_spring', 6)
        )
    })

    rm.placed_feature('random_empty_hot_spring', 'tfc:random_empty_hot_spring', decorate_chance(70), decorate_square())
    rm.placed_feature('random_active_hot_spring', 'tfc:random_active_hot_spring', decorate_chance(50), decorate_square())

    # Trees / Forests
    configured_placed_feature(rm, 'forest', 'tfc:forest', {
        'entries': [
            forest_config(30, 210, 17, 40, 'acacia', True),
            forest_config(60, 240, 1, 15, 'ash', True),
            forest_config(350, 500, -18, 5, 'aspen', False),
            forest_config(125, 310, -11, 7, 'birch', False),
            forest_config(0, 180, 12, 35, 'blackwood', True),
            forest_config(180, 370, -4, 17, 'chestnut', False),
            forest_config(290, 500, -16, -1, 'douglas_fir', True),
            forest_config(210, 400, 7, 15, 'hickory', True),
            forest_config(270, 500, 17, 40, 'kapok', False),
            forest_config(270, 500, -1, 15, 'maple', True),
            forest_config(240, 450, -9, 11, 'oak', False),
            forest_config(180, 470, 20, 35, 'palm', False),
            forest_config(60, 270, -18, -4, 'pine', True),
            forest_config(140, 310, 8, 31, 'rosewood', False),
            forest_config(250, 420, -14, 2, 'sequoia', True),
            forest_config(110, 320, -17, 1, 'spruce', True),
            forest_config(230, 480, 15, 29, 'sycamore', True),
            forest_config(10, 220, -13, 9, 'white_cedar', True),
            forest_config(330, 500, 11, 35, 'willow', True),
        ]
    })

    configured_placed_feature(rm, ('tree', 'acacia'), 'tfc:random_tree', random_config('acacia', 35))
    configured_placed_feature(rm, ('tree', 'acacia_large'), 'tfc:random_tree', random_config('acacia', 6, 2, True))
    configured_placed_feature(rm, ('tree', 'ash'), 'tfc:overlay_tree', overlay_config('ash', 3, 5))
    configured_placed_feature(rm, ('tree', 'ash_large'), 'tfc:random_tree', random_config('ash', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'aspen'), 'tfc:random_tree', random_config('aspen', 16, trunk=[3, 5, 1]))
    configured_placed_feature(rm, ('tree', 'birch'), 'tfc:random_tree', random_config('birch', 16, trunk=[2, 3, 1]))
    configured_placed_feature(rm, ('tree', 'blackwood'), 'tfc:random_tree', random_config('blackwood', 10))
    configured_placed_feature(rm, ('tree', 'blackwood_large'), 'tfc:random_tree', random_config('blackwood', 10, 1, True))
    configured_placed_feature(rm, ('tree', 'chestnut'), 'tfc:overlay_tree', overlay_config('chestnut', 2, 4))
    configured_placed_feature(rm, ('tree', 'douglas_fir'), 'tfc:random_tree', random_config('douglas_fir', 9))
    configured_placed_feature(rm, ('tree', 'douglas_fir_large'), 'tfc:random_tree', random_config('douglas_fir', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'hickory'), 'tfc:random_tree', random_config('hickory', 9))
    configured_placed_feature(rm, ('tree', 'hickory_large'), 'tfc:random_tree', random_config('hickory', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'kapok'), 'tfc:random_tree', random_config('kapok', 17))
    configured_placed_feature(rm, ('tree', 'maple'), 'tfc:overlay_tree', overlay_config('maple', 2, 4))
    configured_placed_feature(rm, ('tree', 'maple_large'), 'tfc:random_tree', random_config('maple', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'oak'), 'tfc:overlay_tree', overlay_config('oak', 3, 5))
    configured_placed_feature(rm, ('tree', 'palm'), 'tfc:random_tree', random_config('palm', 7))
    configured_placed_feature(rm, ('tree', 'pine'), 'tfc:random_tree', random_config('pine', 9))
    configured_placed_feature(rm, ('tree', 'pine_large'), 'tfc:random_tree', random_config('pine', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'rosewood'), 'tfc:overlay_tree', overlay_config('rosewood', 1, 3))
    configured_placed_feature(rm, ('tree', 'sequoia'), 'tfc:random_tree', random_config('sequoia', 7))
    configured_placed_feature(rm, ('tree', 'sequoia_large'), 'tfc:stacked_tree', stacked_config('sequoia', 8, 16, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True))
    configured_placed_feature(rm, ('tree', 'spruce'), 'tfc:random_tree', random_config('spruce', 7))
    configured_placed_feature(rm, ('tree', 'spruce_large'), 'tfc:stacked_tree', stacked_config('spruce', 5, 9, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True))
    configured_placed_feature(rm, ('tree', 'sycamore'), 'tfc:overlay_tree', overlay_config('sycamore', 2, 5))
    configured_placed_feature(rm, ('tree', 'sycamore_large'), 'tfc:random_tree', random_config('sycamore', 5, 2, True))
    configured_placed_feature(rm, ('tree', 'white_cedar'), 'tfc:overlay_tree', overlay_config('white_cedar', 2, 4))
    configured_placed_feature(rm, ('tree', 'white_cedar_large'), 'tfc:overlay_tree', overlay_config('white_cedar', 2, 5, 1, 1, True))
    configured_placed_feature(rm, ('tree', 'willow'), 'tfc:random_tree', random_config('willow', 7))
    configured_placed_feature(rm, ('tree', 'willow_large'), 'tfc:random_tree', random_config('willow', 14, 1, True))

    # Ore Veins
    for vein_name, vein in ORE_VEINS.items():
        rocks = expand_rocks(vein.rocks, vein_name)
        ore = ORES[vein.ore]  # standard ore
        if ore.graded:  # graded ore vein
            configured_placed_feature(rm, ('vein', vein_name), 'tfc:%s_vein' % vein.type, {
                'rarity': vein.rarity,
                'min_y': utils.vertical_anchor(vein.min_y, 'absolute'),
                'max_y': utils.vertical_anchor(vein.max_y, 'absolute'),
                'size': vein.size,
                'density': vein_density(vein.density),
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock],
                    'with': vein_ore_blocks(vein, rock)
                } for rock in rocks],
                'indicator': {
                    'rarity': 12,
                    'blocks': [{
                        'block': 'tfc:ore/small_%s' % vein.ore
                    }]
                },
                'random_name': vein_name,
                'biomes': vein_biome_filter(vein.biomes)
            })
        else:  # non-graded ore vein (mineral)
            vein_config = {
                'rarity': vein.rarity,
                'min_y': utils.vertical_anchor(vein.min_y, 'absolute'),
                'max_y': utils.vertical_anchor(vein.max_y, 'absolute'),
                'size': vein.size,
                'density': vein_density(vein.density),
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock],
                    'with': [{'block': 'tfc:ore/%s/%s' % (vein.ore, rock)}]
                } for rock in rocks],
                'random_name': vein_name,
                'biomes': vein_biome_filter(vein.biomes)
            }
            if vein.type == 'pipe':
                vein_config['min_skew'] = 5
                vein_config['max_skew'] = 13
                vein_config['min_slant'] = 0
                vein_config['max_slant'] = 2
            configured_placed_feature(rm, ('vein', vein_name), 'tfc:%s_vein' % vein.type, vein_config)

    configured_placed_feature(rm, ('vein', 'gravel'), 'tfc:disc_vein', {
        'rarity': 30,
        'min_y': utils.vertical_anchor(-64, 'absolute'),
        'max_y': utils.vertical_anchor(100, 'absolute'),
        'size': 44,
        'height': 2,
        'density': 0.98,
        'blocks': [{
            'replace': ['tfc:rock/raw/%s' % rock],
            'with': [{'block': 'tfc:rock/gravel/%s' % rock}]
        } for rock in ROCKS.keys()],
        'random_name': 'tfc:vein/gravel'
    })

    for rock, data in ROCKS.items():
        if data.category == 'igneous_intrusive':
            configured_placed_feature(rm, ('vein', '%s_dike' % rock), 'tfc:pipe_vein', {
                'rarity': 220,
                'min_y': utils.vertical_anchor(-64, 'absolute'),
                'max_y': utils.vertical_anchor(180, 'absolute'),
                'size': 150,
                'density': 0.98,
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock_in],
                    'with': [{'block': 'tfc:rock/raw/%s' % rock}]
                } for rock_in in ROCKS.keys()] + [{
                    'replace': ['tfc:rock/gravel/%s' % rock_in],
                    'with': [{'block': 'tfc:rock/raw/%s' % rock}]
                } for rock_in in ROCKS.keys()] + [{
                    'replace': ['tfc:rock/hardened/%s' % rock_in],
                    'with': [{'block': 'tfc:rock/raw/%s' % rock}]
                } for rock_in in ROCKS.keys()],
                'random_name': '%s_dike' % rock,
                'radius': 4,
                'minSkew': 7,
                'maxSkew': 20,
                'minSlant': 2,
                'maxSlant': 5
            })

    rm.configured_feature('cave_vegetation', 'tfc:cave_vegetation', {
        'blocks': [{
            'replace': ['tfc:rock/raw/%s' % rock],
            'with': [
                {'block': 'tfc:rock/mossy_cobble/%s' % rock, 'weight': 8},
                {'block': 'tfc:rock/cobble/%s' % rock, 'weight': 2}
            ]
        } for rock in ROCKS.keys()]
    })
    rm.placed_feature('cave_vegetation', 'tfc:cave_vegetation', decorate_climate(16, 32, 150, 470, fuzzy=True), decorate_carving_mask(15, 100), decorate_chance(0.01))

    rm.configured_feature('hanging_roots', 'minecraft:simple_block', {'to_place': simple_state_provider('minecraft:hanging_roots[waterlogged=false]')})
    rm.placed_feature('hanging_roots', 'tfc:hanging_roots', decorate_air_or_empty_fluid(), decorate_would_survive('minecraft:hanging_roots[waterlogged=false]'))
    rm.configured_feature('hanging_roots_patch', 'minecraft:vegetation_patch', {
        'type': 'minecraft:vegetation_patch',
        'vegetation_chance': 0.08,
        'xz_radius': uniform_int(4, 7),
        'extra_edge_column_chance': 0.3,
        'extra_bottom_block_chance': 0.0,
        'vertical_range': 5,
        'vegetation_feature': 'tfc:hanging_roots',
        'surface': 'ceiling',
        'depth': uniform_int(1, 2),
        'replaceable': 'minecraft:base_stone_overworld',
        'ground_state': simple_state_provider('tfc:rooted_dirt/silt')
    })
    rm.placed_feature('hanging_roots_patch', 'tfc:hanging_roots_patch', decorate_count(10), decorate_square(), decorate_range(40, 72), decorate_scanner('up', 12), decorate_random_offset(0, -1), decorate_climate(min_rain=300, min_temp=0), decorate_biome())

    # Plants
    configured_plant_patch_feature(rm, ('plant', 'allium'), plant_config('tfc:plant/allium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, -2, 150, 400, min_forest='edge', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'badderlocks'), plant_config('tfc:plant/badderlocks[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-18, 2, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'barrel_cactus'), plant_config('tfc:plant/barrel_cactus[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(4, 18, 0, 85))
    configured_plant_patch_feature(rm, ('plant', 'black_orchid'), plant_config('tfc:plant/black_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(14, 40, 290, 410))
    configured_plant_patch_feature(rm, ('plant', 'blood_lily'), plant_config('tfc:plant/blood_lily[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(8, 18, 200, 500, min_forest='normal', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'blue_orchid'), plant_config('tfc:plant/blue_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(10, 40, 250, 390))
    configured_plant_patch_feature(rm, ('plant', 'butterfly_milkweed'), plant_config('tfc:plant/butterfly_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-16, 18, 75, 300))
    configured_plant_patch_feature(rm, ('plant', 'calendula'), plant_config('tfc:plant/calendula[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(4, 22, 130, 300))
    configured_plant_patch_feature(rm, ('plant', 'cattail'), plant_config('tfc:plant/cattail[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-16, 22, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'coontail'), plant_config('tfc:plant/coontail[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), decorate_square(), decorate_climate(2, 18, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'dandelion'), plant_config('tfc:plant/dandelion[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-22, 40, 120, 400))
    configured_plant_patch_feature(rm, ('plant', 'dead_bush'), plant_config('tfc:plant/dead_bush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_climate(-12, 40, 0, 120))
    configured_noise_plant_feature(rm, ('plant', 'duckweed'), plant_config('tfc:plant/duckweed[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_climate(-18, 2, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'eel_grass'), plant_config('tfc:plant/eel_grass[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), decorate_square(), decorate_climate(6, 40, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'field_horsetail'), plant_config('tfc:plant/field_horsetail[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-12, 20, 300, 500))
    configured_plant_patch_feature(rm, ('plant', 'foxglove'), plant_config('tfc:plant/foxglove[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(-8, 16, 150, 300, min_forest='none', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'grape_hyacinth'), plant_config('tfc:plant/grape_hyacinth[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, 10, 150, 250))
    configured_plant_patch_feature(rm, ('plant', 'gutweed'), plant_config('tfc:plant/gutweed[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_climate(-6, 18, 100, 500))
    configured_plant_patch_feature(rm, ('plant', 'guzmania'), plant_config('tfc:plant/guzmania[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), decorate_square(), decorate_climate(20, 40, 290, 480))
    configured_plant_patch_feature(rm, ('plant', 'houstonia'), plant_config('tfc:plant/houstonia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-12, 10, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'labrador_tea'), plant_config('tfc:plant/labrador_tea[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-18, 0, 200, 380))
    configured_plant_patch_feature(rm, ('plant', 'lady_fern'), plant_config('tfc:plant/lady_fern[age=1,stage=1]', 1, 10, 15), decorate_chance(5), decorate_square(), decorate_climate(-10, 8, 200, 500, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'licorice_fern'), plant_config('tfc:plant/licorice_fern[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), decorate_square(), decorate_climate(2, 10, 300, 400))
    configured_plant_patch_feature(rm, ('plant', 'laminaria'), plant_config('tfc:plant/laminaria[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_climate(-24, -2, 100, 500))
    configured_noise_plant_feature(rm, ('plant', 'lotus'), plant_config('tfc:plant/lotus[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_climate(-4, 18, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'marigold'), plant_config('tfc:plant/marigold[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-8, 18, 50, 390))
    configured_plant_patch_feature(rm, ('plant', 'meads_milkweed'), plant_config('tfc:plant/meads_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, 2, 130, 380))
    configured_plant_patch_feature(rm, ('plant', 'milfoil'), plant_config('tfc:plant/milfoil[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_climate(-14, 22, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'morning_glory'), plant_config('tfc:plant/morning_glory[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), decorate_square(), decorate_climate(-11, 19, 300, 500))
    configured_plant_patch_feature(rm, ('plant', 'moss'), plant_config('tfc:plant/moss[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), decorate_square(), decorate_climate(-7, 30, 250, 450))
    configured_plant_patch_feature(rm, ('plant', 'nasturtium'), plant_config('tfc:plant/nasturtium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(6, 22, 150, 380))
    configured_plant_patch_feature(rm, ('plant', 'ostrich_fern'), plant_config('tfc:plant/ostrich_fern[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(8), decorate_square(), decorate_climate(-14, 6, 290, 470, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'oxeye_daisy'), plant_config('tfc:plant/oxeye_daisy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-14, 10, 120, 300, min_forest='none', max_forest='edge'))
    configured_noise_plant_feature(rm, ('plant', 'pistia'), plant_config('tfc:plant/pistia[age=1,stage=1]', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_climate(6, 26, 0, 400))
    configured_plant_patch_feature(rm, ('plant', 'poppy'), plant_config('tfc:plant/poppy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-12, 14, 150, 250))
    configured_plant_patch_feature(rm, ('plant', 'primrose'), plant_config('tfc:plant/primrose[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-8, 10, 150, 300, min_forest='edge', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'pulsatilla'), plant_config('tfc:plant/pulsatilla[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, 2, 50, 200))
    configured_plant_patch_feature(rm, ('plant', 'reindeer_lichen'), plant_config('tfc:plant/reindeer_lichen[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 6), decorate_chance(15), decorate_square(), decorate_climate(10, 33, 50, 470))
    configured_plant_patch_feature(rm, ('plant', 'rose'), plant_config('tfc:plant/rose[age=1,stage=1,part=lower]', 1, 15, 128, True, tall_plant=True), decorate_square(), decorate_climate(-5, 20, 150, 300))
    configured_plant_patch_feature(rm, ('plant', 'sacred_datura'), plant_config('tfc:plant/sacred_datura[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(4, 18, 75, 150))
    configured_plant_patch_feature(rm, ('plant', 'sago'), plant_config('tfc:plant/sago[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_climate(-18, 18, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'sagebrush'), plant_config('tfc:plant/sagebrush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, 14, 0, 120))
    configured_plant_patch_feature(rm, ('plant', 'sapphire_tower'), plant_config('tfc:plant/sapphire_tower[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(10, 22, 75, 200, min_forest='none', max_forest='sparse'))
    configured_noise_plant_feature(rm, ('plant', 'sargassum'), plant_config('tfc:plant/sargassum[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_climate(-10, 16, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'sword_fern'), plant_config('tfc:plant/sword_fern[age=1,stage=1]', 1, 10, 15), decorate_chance(5), decorate_square(), decorate_climate(-12, 12, 100, 500, min_forest='sparse', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_pink'), plant_config('tfc:plant/snapdragon_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(16, 24, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_red'), plant_config('tfc:plant/snapdragon_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(12, 20, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_white'), plant_config('tfc:plant/snapdragon_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(8, 16, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_yellow'), plant_config('tfc:plant/snapdragon_yellow[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(6, 24, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'spanish_moss'), plant_config('tfc:plant/spanish_moss[age=1,stage=1,hanging=false]', 1, 5), decorate_chance(5), decorate_square(), decorate_climate(8, 22, 400, 500))
    configured_plant_patch_feature(rm, ('plant', 'strelitzia'), plant_config('tfc:plant/strelitzia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(14, 26, 50, 300))
    configured_plant_patch_feature(rm, ('plant', 'toquilla_palm'), plant_config('tfc:plant/toquilla_palm[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(16, 40, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'trillium'), plant_config('tfc:plant/trillium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-10, 8, 250, 500, min_forest='normal', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'tropical_milkweed'), plant_config('tfc:plant/tropical_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(8, 24, 120, 300, min_forest='sparse', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_orange'), plant_config('tfc:plant/tulip_orange[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(2, 10, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_pink'), plant_config('tfc:plant/tulip_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-6, 2, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_red'), plant_config('tfc:plant/tulip_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(0, 4, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_white'), plant_config('tfc:plant/tulip_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(-12, -4, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'turtle_grass'), plant_config('tfc:plant/turtle_grass[age=1,stage=1,fluid=empty]', 1, 15, 128, water_plant=True), decorate_chance(1), decorate_square(), decorate_climate(14, 40, 240, 500))
    configured_plant_patch_feature(rm, ('plant', 'vriesea'), plant_config('tfc:plant/vriesea[age=1,stage=1,facing=north]', 6, 5), decorate_chance(5), decorate_square(), decorate_climate(14, 40, 200, 400))
    configured_noise_plant_feature(rm, ('plant', 'water_lily'), plant_config('tfc:plant/water_lily[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_climate(-12, 40, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'yucca'), plant_config('tfc:plant/yucca[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_climate(-4, 22, 0, 75))
    configured_plant_patch_feature(rm, ('plant', 'switchgrass'), plant_config('tfc:plant/switchgrass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-6, 22, 110, 390))
    configured_plant_patch_feature(rm, ('plant', 'tall_fescue_grass'), plant_config('tfc:plant/tall_fescue_grass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-10, 10, 280, 430))
    configured_plant_patch_feature(rm, ('plant', 'arrowhead'), plant_config('tfc:plant/arrowhead[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-10, 22, 180, 500))
    configured_plant_patch_feature(rm, ('plant', 'bur_reed'), plant_config('tfc:plant/bur_reed[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-16, 4, 250, 400))
    configured_plant_patch_feature(rm, ('plant', 'water_taro'), plant_config('tfc:plant/water_taro[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(12, 40, 260, 500))
    configured_plant_patch_feature(rm, ('plant', 'phragmite'), plant_config('tfc:plant/phragmite[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-6, 18, 50, 250))
    configured_plant_patch_feature(rm, ('plant', 'pickerelweed'), plant_config('tfc:plant/pickerelweed[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_climate(-14, 16, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'red_sealing_wax_palm'), plant_config('tfc:plant/red_sealing_wax_palm[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(10), decorate_square(), decorate_climate(18, 40, 280, 500, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'hibiscus'), plant_config('tfc:plant/hibiscus[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(10, 24, 260, 450, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'heliconia'), plant_config('tfc:plant/heliconia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(14, 40, 320, 500))
    configured_plant_patch_feature(rm, ('plant', 'blue_ginger'), plant_config('tfc:plant/blue_ginger[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(16, 26, 300, 450))
    configured_plant_patch_feature(rm, ('plant', 'king_fern'), plant_config('tfc:plant/king_fern[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(18, 40, 350, 500, min_forest='normal', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'kangaroo_paw'), plant_config('tfc:plant/kangaroo_paw[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(14, 40, 100, 300))
    configured_plant_patch_feature(rm, ('plant', 'lilac'), plant_config('tfc:plant/lilac[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True), decorate_chance(5), decorate_square(), decorate_climate(-10, 6, 150, 300))
    configured_plant_patch_feature(rm, ('plant', 'silver_spurflower'), plant_config('tfc:plant/silver_spurflower[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(14, 24, 230, 400, min_forest='sparse', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'desert_flame'), plant_config('tfc:plant/desert_flame[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(0, 20, 40, 170, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'anthurium'), plant_config('tfc:plant/anthurium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_climate(12, 40, 290, 500))

    configured_placed_feature(rm, ('plant', 'hanging_vines'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 21), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_climate(16, 32, 150, 470, True, fuzzy=True), decorate_biome(), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'hanging_vines_cave'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 22), decorate_carving_mask(30, 100), decorate_chance(0.003), decorate_climate(16, 32, 150, 470, True, fuzzy=True), decorate_biome(), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'liana'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/liana_plant', 'tfc:plant/liana', 40, 10, 8, 16), decorate_carving_mask(30, 100), decorate_chance(0.003), decorate_climate(16, 32, 150, 470, True, fuzzy=True), decorate_biome())
    configured_placed_feature(rm, ('plant', 'tree_fern'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/tree_fern_plant', 'tfc:plant/tree_fern', 8, 7, 2, 6), decorate_heightmap('world_surface_wg'), decorate_chance(5), decorate_square(), decorate_climate(19, 50, 300, 500), decorate_biome(), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'arundo'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/arundo_plant', 'tfc:plant/arundo', 70, 7, 5, 8), decorate_heightmap('world_surface_wg'), decorate_chance(3), decorate_square(), decorate_climate(5, 22, 100, 500), ('tfc:near_water', {'radius': 6}), decorate_biome(), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'winged_kelp'), 'tfc:kelp', tall_plant_config('tfc:plant/winged_kelp_plant', 'tfc:plant/winged_kelp', 64, 12, 14, 21), decorate_heightmap('ocean_floor_wg'), decorate_square(), decorate_chance(2), decorate_climate(-15, 15, 0, 450, fuzzy=True), decorate_biome(), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'leafy_kelp'), 'tfc:kelp', tall_plant_config('tfc:plant/leafy_kelp_plant', 'tfc:plant/leafy_kelp', 64, 12, 14, 21), decorate_heightmap('ocean_floor_wg'), decorate_square(), decorate_chance(2), decorate_climate(-20, 20, 0, 500, fuzzy=True), decorate_biome(), decorate_air_or_empty_fluid())

    configured_patch_feature(rm, ('plant', 'giant_kelp'), patch_config('tfc:plant/giant_kelp_flower[age=0,fluid=empty]', 2, 10, 20, water='salt', custom_feature='tfc:kelp_tree'), decorate_square(), decorate_climate(-18, 18, 0, 500, fuzzy=True))

    configured_placed_feature(rm, ('plant', 'ivy'), 'tfc:vines', vine_config('tfc:plant/ivy', 15, 7, 96, 150), decorate_climate(-4, 14, 90, 450, True, fuzzy=True), decorate_chance(5), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'jungle_vines'), 'tfc:vines', vine_config('tfc:plant/jungle_vines', 33, 7, 64, 160), decorate_climate(16, 32, 150, 470, True, fuzzy=True), decorate_chance(5), decorate_air_or_empty_fluid())

    # Grass-Type / Basic Plants
    configured_plant_patch_feature(rm, ('plant', 'fountain_grass'), plant_config('tfc:plant/fountain_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(0, 26, 75, 150))
    configured_plant_patch_feature(rm, ('plant', 'manatee_grass'), plant_config('tfc:plant/manatee_grass[age=1,stage=1]', 1, 15, 64, water_plant=True), decorate_square(), decorate_climate(12, 40, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'orchard_grass'), plant_config('tfc:plant/orchard_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(-30, 10, 75, 300))
    configured_plant_patch_feature(rm, ('plant', 'ryegrass'), plant_config('tfc:plant/ryegrass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(-24, 40, 150, 320))
    configured_plant_patch_feature(rm, ('plant', 'scutch_grass'), plant_config('tfc:plant/scutch_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(0, 40, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'star_grass'), plant_config('tfc:plant/star_grass[age=1,stage=1]', 1, 15, 64, water_plant=True), decorate_square(), decorate_climate(2, 40, 50, 260))
    configured_plant_patch_feature(rm, ('plant', 'timothy_grass'), plant_config('tfc:plant/timothy_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(-22, 16, 289, 500))
    configured_plant_patch_feature(rm, ('plant', 'bromegrass'), plant_config('tfc:plant/bromegrass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(4, 20, 140, 360))
    configured_plant_patch_feature(rm, ('plant', 'bluegrass'), plant_config('tfc:plant/bluegrass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(-4, 12, 110, 280))
    configured_plant_patch_feature(rm, ('plant', 'raddia_grass'), plant_config('tfc:plant/raddia_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_climate(18, 40, 330, 500))

    # Covers
    configured_noise_plant_feature(rm, ('plant', 'moss_cover'), plant_config('tfc:plant/moss[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 7, 100), decorate_climate(18, 35, 340, 500, True, fuzzy=True), decorate_square(), water=False)
    configured_noise_plant_feature(rm, ('plant', 'morning_glory_cover'), plant_config('tfc:plant/morning_glory[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 7, 100), decorate_climate(9, 13, 160, 230, True, fuzzy=True), decorate_square(), water=False)
    configured_noise_plant_feature(rm, ('plant', 'reindeer_lichen_cover'), plant_config('tfc:plant/reindeer_lichen[age=1,stage=1,up=false,down=true,north=false,east=false,west=false,south=false]', 1, 7, 100), decorate_climate(-6, 2, 220, 310, True, fuzzy=True), decorate_square(), water=False)

    # Clay Indicator Plants
    # These piggy back on the clay disc feature, and so have limited decorators
    configured_plant_patch_feature(rm, ('plant', 'athyrium_fern'), plant_config('tfc:plant/athyrium_fern[age=1,stage=1]', 1, 10, requires_clay=True), decorate_climate(-10, 14, 270, 500))
    configured_plant_patch_feature(rm, ('plant', 'canna'), plant_config('tfc:plant/canna[age=1,stage=1]', 1, 10, requires_clay=True), decorate_climate(10, 40, 270, 500))
    configured_plant_patch_feature(rm, ('plant', 'goldenrod'), plant_config('tfc:plant/goldenrod[age=1,stage=1]', 1, 10, requires_clay=True), decorate_climate(-16, 6, 75, 310))
    configured_plant_patch_feature(rm, ('plant', 'pampas_grass'), plant_config('tfc:plant/pampas_grass[age=1,stage=1,part=lower]', 1, 10, requires_clay=True, tall_plant=True), decorate_climate(12, 40, 0, 300))
    configured_plant_patch_feature(rm, ('plant', 'perovskia'), plant_config('tfc:plant/perovskia[age=1,stage=1]', 1, 10, requires_clay=True), decorate_climate(-6, 12, 0, 270))
    configured_noise_plant_feature(rm, ('plant', 'water_canna'), plant_config('tfc:plant/water_canna[age=1,stage=1]', 1, 10, requires_clay=True), decorate_climate(0, 36, 150, 500))

    clay_plant_features = [
        'tfc:plant/athyrium_fern_patch',
        'tfc:plant/canna_patch',
        'tfc:plant/goldenrod_patch',
        'tfc:plant/pampas_grass_patch',
        'tfc:plant/perovskia_patch',
        'tfc:plant/water_canna_patch'
    ]
    configured_placed_feature(rm, 'clay_disc_with_indicator', 'tfc:multiple', {
        'features': [
            'tfc:clay_disc',
            *clay_plant_features
        ]
    }, decorate_chance(20), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_rain=175))
    configured_placed_feature(rm, 'water_clay_disc_with_indicator', 'tfc:multiple', {
        'features': [
            'tfc:water_clay_disc',
            *clay_plant_features
        ]
    }, decorate_chance(10), decorate_square(), decorate_heightmap('world_surface_wg'), 'tfc:near_water')

    for berry, info in BERRIES.items():
        decorators = decorate_square(), decorate_climate(info.min_temp, info.max_temp, info.min_rain, info.max_rain, min_forest=info.min_forest, max_forest=info.max_forest), decorate_chance(15)
        if info.type == 'stationary':
            configured_patch_feature(rm, ('plant', berry + '_bush'), patch_config('tfc:plant/%s_bush[lifecycle=healthy,stage=0]' % berry, 1, 4, 8), *decorators)
        elif info.type == 'waterlogged':
            configured_patch_feature(rm, ('plant', berry + '_bush'), patch_config('tfc:plant/%s_bush[lifecycle=healthy,stage=0,fluid=empty]' % berry, 1, 4, 8, water=True), *decorators)
        else:
            # todo: implement spreading bush features
            configured_placed_feature(rm, ('plant', berry + '_bush'), 'no_op', {})

    for fruit, info in FRUITS.items():
        config = {
            'min_temperature': info.min_temp,
            'max_temperature': info.max_temp,
            'min_rainfall': info.min_rain,
            'max_rainfall': info.max_rain,
            'max_forest': 'normal'
        }
        feature = 'tfc:fruit_trees'
        state = 'tfc:plant/%s_growing_branch' % fruit
        if fruit == 'banana':
            feature = 'tfc:bananas'
            state = 'tfc:plant/banana_plant'
        configured_placed_feature(rm, ('plant', fruit), feature, {'state': state}, decorate_heightmap('world_surface_wg'), decorate_square(), ('tfc:climate', config), decorate_chance(200))

    configured_placed_feature(rm, 'bamboo', 'minecraft:bamboo', {'probability': 0.2}, decorate_chance(30), decorate_climate(18, 28, 300, 500, True, fuzzy=True), ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), decorate_square(), decorate_heightmap('world_surface_wg'))

    for coral in ('tree', 'mushroom', 'claw'):
        configured_placed_feature(rm, 'coral_%s' % coral, 'tfc:coral_%s' % coral, {})
    configured_placed_feature(rm, 'coral_reef', 'minecraft:simple_random_selector', {'features': ['tfc:coral_tree', 'tfc:coral_mushroom', 'tfc:coral_claw']}, decorate_climate(min_temp=18))

    # Groundcover
    configured_patch_feature(rm, 'driftwood', patch_config('tfc:groundcover/driftwood[fluid=empty]', 1, 15, 10, True), decorate_chance(6), decorate_square(), decorate_climate(-10, 50, 200, 500))
    configured_patch_feature(rm, 'clam', patch_config('tfc:groundcover/clam[fluid=empty]', 1, 15, 10, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(-50, 22, 10, 450))
    configured_patch_feature(rm, 'mollusk', patch_config('tfc:groundcover/mollusk[fluid=empty]', 1, 15, 10, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(-10, 30, 150, 500))
    configured_patch_feature(rm, 'mussel', patch_config('tfc:groundcover/mussel[fluid=empty]', 1, 15, 10, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(10, 50, 100, 500))

    configured_patch_feature(rm, 'sticks_shore', patch_config('tfc:groundcover/stick[fluid=empty]', 1, 15, 25, True), decorate_chance(2), decorate_square(), decorate_climate(-50, 50, 100, 500))
    configured_patch_feature(rm, 'seaweed', patch_config('tfc:groundcover/seaweed[fluid=empty]', 1, 15, 10, True), decorate_chance(5), decorate_square(), decorate_climate(-20, 50, 150, 500))

    # Forest Only
    configured_patch_feature(rm, 'sticks_forest', patch_config('tfc:groundcover/stick[fluid=empty]', 1, 15, 20), decorate_chance(3), decorate_square(), decorate_climate(-20, 50, 70, 500, True))
    configured_patch_feature(rm, 'pinecone', patch_config('tfc:groundcover/pinecone[fluid=empty]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_climate(-5, 33, 200, 500, True))
    configured_patch_feature(rm, 'podzol', patch_config('tfc:groundcover/podzol[fluid=empty]', 1, 5, 100), decorate_chance(5), decorate_square(), decorate_climate(8, 20, 180, 420, True, fuzzy=True))
    configured_patch_feature(rm, 'salt_lick', patch_config('tfc:groundcover/salt_lick[fluid=empty]', 1, 5, 100), decorate_chance(110), decorate_square(), decorate_climate(5, 33, 100, 500, True))
    configured_patch_feature(rm, 'dead_grass', patch_config('tfc:groundcover/dead_grass[fluid=empty]', 1, 5, 100), decorate_chance(70), decorate_square(), decorate_climate(10, 20, 0, 150, True, fuzzy=True))

    # Loose Rocks - Both Surface + Underground
    configured_placed_feature(rm, 'surface_loose_rocks', 'tfc:loose_rock', decorate_count(8), decorate_square(), decorate_heightmap('ocean_floor_wg'))

    # Underground decoration
    # todo: underground only filter decorator?
    configured_placed_feature(rm, 'underground_loose_rocks', 'tfc:loose_rock', decorate_carving_mask(), decorate_chance(0.05))
    configured_patch_feature(rm, 'underground_guano', patch_config('tfc:groundcover/guano[fluid=empty]', 5, 5, 60), decorate_chance(3), decorate_square(), decorate_range(40, 100))
    rm.configured_feature('geode', 'tfc:geode', {'outer': 'tfc:rock/hardened/basalt', 'middle': 'tfc:rock/raw/quartzite', 'inner': [
        {'data': 'tfc:ore/amethyst/quartzite', 'weight': 1}, {'data': 'tfc:rock/raw/quartzite', 'weight': 5}
    ]})
    rm.placed_feature('geode', 'tfc:geode', decorate_chance(500), decorate_square(), decorate_range(6, 30), decorate_biome())

def configured_placed_feature(rm: ResourceManager, name_parts: ResourceIdentifier, feature: Optional[ResourceIdentifier] = None, config: JsonObject = None, *placements: Json):
    res = utils.resource_location(rm.domain, name_parts)
    if feature is None:
        feature = res
    rm.configured_feature(res, feature, config)
    rm.placed_feature(res, res, *placements)

def tall_plant_config(state1: str, state2: str, tries: int, radius: int, min_height: int, max_height: int) -> Json:
    return {
        'body': state1,
        'head': state2,
        'tries': tries,
        'radius': radius,
        'min_height': min_height,
        'max_height': max_height
    }

def vine_config(state: str, tries: int, radius: int, min_height: int, max_height: int) -> Json:
    return {
        'state': state,
        'tries': tries,
        'radius': radius,
        'min_height': min_height,
        'max_height': max_height
    }

class PlantConfig(NamedTuple):
    block: str
    y_spread: int
    xz_spread: int
    tries: int
    requires_clay: bool
    water_plant: bool
    emergent_plant: bool
    tall_plant: bool

def plant_config(block: str, y_spread: int, xz_spread: int, tries: int = None, requires_clay: bool = False, water_plant: bool = False, emergent_plant: bool = False, tall_plant: bool = False) -> PlantConfig:
    return PlantConfig(block, y_spread, xz_spread, tries, requires_clay, water_plant, emergent_plant, tall_plant)

def configured_plant_patch_feature(rm: ResourceManager, name_parts: ResourceIdentifier, config: PlantConfig, *patch_decorators: Json):
    state_provider = {
        'type': 'tfc:random_property',
        'state': utils.block_state(config.block), 'property': 'age'
    }
    feature = 'simple_block', {'to_place': state_provider}
    heightmap: Heightmap = 'world_surface_wg'
    would_survive = decorate_would_survive(config.block)

    if config.water_plant or config.emergent_plant:
        heightmap = 'ocean_floor_wg'
        would_survive = decorate_would_survive_with_fluid(config.block)

    if config.water_plant:
        feature = 'tfc:block_with_fluid', feature[1]
    if config.emergent_plant:
        feature = 'tfc:emergent_plant', {'state': utils.block_state(config.block)}
    if config.tall_plant:
        feature = 'tfc:tall_plant', {'state': utils.block_state(config.block)}

    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)

    rm.configured_feature(patch_feature, 'minecraft:random_patch', {
        'tries': config.tries,
        'xz_spread': config.xz_spread,
        'y_spread': config.y_spread,
        'feature': singular_feature.join()
    })
    rm.configured_feature(singular_feature, *feature)
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators, decorate_biome())
    rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), decorate_air_or_empty_fluid(), would_survive)


class PatchConfig(NamedTuple):
    block: str
    y_spread: int
    xz_spread: int
    tries: int
    any_water: bool
    salt_water: bool
    custom_feature: str
    custom_config: Json

def patch_config(block: str, y_spread: int, xz_spread: int, tries: int = 64, water: Union[bool, Literal['salt']] = False, custom_feature: Optional[str] = None, custom_config: Json = None) -> PatchConfig:
    return PatchConfig(block, y_spread, xz_spread, tries, water == 'salt' or water == True, water == 'salt', custom_feature, custom_config)

def configured_patch_feature(rm: ResourceManager, name_parts: ResourceIdentifier, patch: PatchConfig, *patch_decorators: Json):
    feature = 'minecraft:simple_block'
    config = {'to_place': {'type': 'minecraft:simple_state_provider', 'state': utils.block_state(patch.block)}}
    singular_decorators = []

    if patch.any_water:
        feature = 'tfc:block_with_fluid'
        if patch.salt_water:
            singular_decorators.append(decorate_matching_blocks('tfc:fluid/salt_water'))
        else:
            singular_decorators.append(decorate_air_or_empty_fluid())
    else:
        singular_decorators.append(decorate_matching_blocks('minecraft:air'))

    if patch.custom_feature is not None:
        feature = patch.custom_feature
        config = config['to_place']  # assume that for custom features, it uses just a single state (not state provider)
        if patch.custom_config is not None:
            config = patch.custom_config

    heightmap: Heightmap = 'world_surface_wg'
    if patch.any_water:
        heightmap = 'ocean_floor_wg'
        singular_decorators.append(decorate_would_survive_with_fluid(patch.block))
    else:
        singular_decorators.append(decorate_would_survive(patch.block))

    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)

    rm.configured_feature(patch_feature, 'minecraft:random_patch', {
        'tries': patch.tries,
        'xz_spread': patch.xz_spread,
        'y_spread': patch.y_spread,
        'feature': singular_feature.join()
    })
    rm.configured_feature(singular_feature, feature, config)
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators, decorate_biome())
    rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), *singular_decorators)

def configured_noise_plant_feature(rm: ResourceManager, name_parts: ResourceIdentifier, config: PlantConfig, *patch_decorators: Json, water: bool = True):
    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)
    placed_decorators = [decorate_heightmap('world_surface_wg'), decorate_air_or_empty_fluid(), decorate_would_survive(config.block)]
    if water:
        placed_decorators.append(decorate_shallow())

    rm.configured_feature(singular_feature, 'minecraft:simple_block', {
        'to_place': {
            'seed': 2345,
            'noise': normal_noise(-3, 1.0),
            'scale': 1.0,
            'states': [utils.block_state(config.block)],
            'variety': [1, 1],
            'slow_noise': normal_noise(-10, 1.0),
            'slow_scale': 1.0,
            'type': 'minecraft:dual_noise_provider'
        }
    })
    rm.configured_feature(patch_feature, 'minecraft:random_patch', {
        'tries': config.tries,
        'xz_spread': config.xz_spread,
        'y_spread': config.y_spread,
        'feature': singular_feature.join()
    })
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators, decorate_biome())
    rm.placed_feature(singular_feature, singular_feature, *placed_decorators)

def normal_noise(first_octave: int, amplitude: float):
    return {'firstOctave': first_octave, 'amplitudes': [amplitude]}

def simple_state_provider(name: str) -> Dict[str, Any]:
    return {'type': 'minecraft:simple_state_provider', 'state': utils.block_state(name)}

# Vein Helper Functions

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

def vein_biome_filter(biome_filter: Optional[str] = None) -> Optional[List[Any]]:
    if biome_filter == 'river':
        return [{'category': 'river'}]
    elif biome_filter == 'volcanic':
        return [{'biome_dictionary': 'volcanic'}]
    elif biome_filter is not None:
        raise ValueError('Unknown biome filter %s? not sure how to handle...' % biome_filter)
    else:
        return None

def vein_density(density: int) -> float:
    assert 0 <= density <= 100, 'Invalid density: %s' % str(density)
    return round(density * 0.01, 2)


# Tree Helper Functions

def forest_config(min_rain: float, max_rain: float, min_temp: float, max_temp: float, tree: str, old_growth: bool):
    cfg = {
        'min_rain': min_rain,
        'max_rain': max_rain,
        'min_temp': min_temp,
        'max_temp': max_temp,
        'groundcover': ['tfc:wood/twig/%s' % tree],
        'normal_tree': 'tfc:tree/%s' % tree
    }
    if tree != 'palm':
        cfg['groundcover'].append('tfc:wood/fallen_leaves/%s' % tree)
    if tree not in ('acacia', 'willow'):
        cfg.update({'fallen_log': 'tfc:wood/log/%s' % tree})
    if tree not in ('palm', 'rosewood', 'sycamore'):
        cfg['bush_log'] = utils.block_state('tfc:wood/wood/%s[natural=true,axis=y]' % tree)
        cfg.update({'bush_leaves': 'tfc:wood/leaves/%s' % tree})
    if old_growth:
        cfg['old_growth_tree'] = 'tfc:tree/%s_large' % tree
    return cfg

def overlay_config(tree: str, min_height: int, max_height: int, width: int = 1, radius: int = 1, large: bool = False):
    block = 'tfc:wood/log/%s[axis=y,natural=true]' % tree
    if large:
        tree += '_large'
    return {
        'base': 'tfc:%s/base' % tree,
        'overlay': 'tfc:%s/overlay' % tree,
        'trunk': trunk_config(block, min_height, max_height, width),
        'radius': radius
    }

def random_config(tree: str, structure_count: int, radius: int = 1, large: bool = False, trunk: List = None):
    block = 'tfc:wood/log/%s[axis=y,natural=true]' % tree
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
    block = 'tfc:wood/log/%s[axis=y,natural=true]' % tree
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
        'state': utils.block_state(block),
        'min_height': min_height,
        'max_height': max_height,
        'width': width
    }


Heightmap = Literal['motion_blocking', 'motion_blocking_no_leaves', 'ocean_floor', 'ocean_floor_wg', 'world_surface', 'world_surface_wg']
HeightProviderType = Literal['constant', 'uniform', 'biased_to_bottom', 'very_biased_to_bottom', 'trapezoid', 'weighted_list']


# Decorators / Placements

def decorate_square() -> Json:
    return 'minecraft:in_square'

def decorate_biome() -> Json:
    return 'minecraft:biome'

def decorate_chance(rarity_or_probability: Union[int, float]) -> Json:
    return {'type': 'minecraft:rarity_filter', 'chance': round(1 / rarity_or_probability) if isinstance(rarity_or_probability, float) else rarity_or_probability}

def decorate_count(count: int) -> Json:
    return {'type': 'minecraft:count', 'count': count}

def decorate_shallow(depth: int = 5) -> Json:
    return {'type': 'tfc:shallow_water', 'max_depth': depth}

def decorate_heightmap(heightmap: Heightmap) -> Json:
    assert heightmap in typing.get_args(Heightmap)
    return 'minecraft:heightmap', {'heightmap': heightmap.upper()}

def decorate_range(min_y: VerticalAnchor, max_y: VerticalAnchor, bias: HeightProviderType = 'uniform') -> Json:
    return {
        'type': 'minecraft:height_range',
        'height': height_provider(min_y, max_y, bias)
    }

def decorate_carving_mask(min_y: Optional[VerticalAnchor] = None, max_y: Optional[VerticalAnchor] = None) -> Json:
    return {
        'type': 'tfc:carving_mask',
        'step': 'air',
        'min_y': utils.as_vertical_anchor(min_y) if min_y is not None else None,
        'max_y': utils.as_vertical_anchor(max_y) if max_y is not None else None
    }

def decorate_climate(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None, min_forest: Optional[str] = None, max_forest: Optional[str] = None) -> Json:
    return {
        'type': 'tfc:climate',
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'min_forest': 'normal' if needs_forest else min_forest,
        'max_forest': max_forest,
        'fuzzy': fuzzy
    }

def decorate_scanner(direction: str, max_steps: int) -> Json:
    return {
        'type': 'minecraft:environment_scan',
        'max_steps': max_steps,
        'direction_of_search': direction,
        'target_condition': {'type': 'minecraft:solid'},
        'allowed_search_condition': {'type': 'minecraft:matching_blocks', 'blocks': ['minecraft:air']}
    }

def decorate_random_offset(xz: int, y: int) -> Json:
    return {'xz_spread': xz, 'y_spread': y, 'type': 'minecraft:random_offset'}

def decorate_matching_blocks(*blocks: str) -> Json:
    return decorate_block_predicate({
        'type': 'matching_blocks',
        'blocks': list(blocks)
    })

def decorate_would_survive(block: str) -> Json:
    return decorate_block_predicate({
        'type': 'would_survive',
        'state': utils.block_state(block)
    })

def decorate_would_survive_with_fluid(block: str) -> Json:
    return decorate_block_predicate({
        'type': 'tfc:would_survive_with_fluid',
        'state': utils.block_state(block)
    })

def decorate_air_or_empty_fluid() -> Json:
    return decorate_block_predicate({'type': 'tfc:air_or_empty_fluid'})

def decorate_block_predicate(predicate: Json) -> Json:
    return {
        'type': 'block_predicate_filter',
        'predicate': predicate
    }

# Value Providers

def uniform_float(min_inclusive: float, max_exclusive: float) -> Dict[str, Any]:
    return {
        'type': 'uniform',
        'value': {
            'min_inclusive': min_inclusive,
            'max_exclusive': max_exclusive
        }
    }

def uniform_int(min_inclusive: int, max_inclusive: int) -> Dict[str, Any]:
    return {
        'type': 'uniform',
        'value': {
            'min_inclusive': min_inclusive,
            'max_inclusive': max_inclusive
        }
    }

def trapezoid_float(min_value: float, max_value: float, plateau: float) -> Dict[str, Any]:
    return {
        'type': 'trapezoid',
        'value': {
            'min': min_value,
            'max': max_value,
            'plateau': plateau
        }
    }

def height_provider(min_y: VerticalAnchor, max_y: VerticalAnchor, height_type: HeightProviderType = 'uniform') -> Dict[str, Any]:
    assert height_type in typing.get_args(HeightProviderType)
    return {
        'type': height_type,
        'min_inclusive': utils.as_vertical_anchor(min_y),
        'max_inclusive': utils.as_vertical_anchor(max_y)
    }


def make_biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, boulders: bool = False, spawnable: bool = True, ocean_features: Union[bool, Literal['both']] = False, lake_features: Union[bool, Literal['default']] = 'default', volcano_features: bool = False, reef_features: bool = False, hot_spring_features: Union[bool, Literal['empty']] = False):
    # Temperature properties
    if rain.id == 'arid':
        rain_type = 'none'
    elif temp.id in ('cold', 'frozen'):
        rain_type = 'snow'
    else:
        rain_type = 'rain'

    spawners = {}

    if ocean_features == 'both':  # Both applies both ocean + land features. True or false applies only one
        land_features = True
        ocean_features = True
    else:
        land_features = not ocean_features
    if lake_features == 'default':  # Default = Lakes are on all non-ocean biomes. True/False to force either way
        lake_features = not ocean_features

    dike_veins = []
    for rock, data in ROCKS.items():
        if data.category == 'igneous_intrusive':
            dike_veins += ['tfc:vein/%s_dike' % rock]

    # Features
    features = [
        ['tfc:erosion'],  # erosion
        ['tfc:underground_flood_fill_lake'],  # lakes
        [],  # soil disks
        ['tfc:vein/gravel', *dike_veins, *('tfc:vein/%s' % v for v in ORE_VEINS.keys())],  # veins
        ['tfc:cave_spike',
         'tfc:large_cave_spike',
         'tfc:water_spring',
         'tfc:lava_spring',
         'tfc:calcite',
         'tfc:mega_calcite',
         'tfc:icicle',
         'tfc:underground_loose_rocks',
         'tfc:underground_guano',
         'tfc:hanging_roots_patch'],  # underground decoration
        ['tfc:geode'],  # large features
        ['tfc:surface_loose_rocks'],  # surface decoration
        [], []  # unused
    ]

    if boulders:
        features[Decoration.LARGE_FEATURES] += ['tfc:raw_boulder', 'tfc:cobble_boulder']
        if rain.id in ('damp', 'wet'):
            features[Decoration.LARGE_FEATURES].append('tfc:mossy_boulder')

    # Oceans
    if ocean_features:
        if temp.id in ('cold', 'frozen'):
            features[Decoration.LARGE_FEATURES] += ['tfc:iceberg_packed', 'tfc:iceberg_blue', 'tfc:iceberg_packed_rare', 'tfc:iceberg_blue_rare']

        features[Decoration.SURFACE_DECORATION] += ['tfc:plant/%s_patch' % plant for plant, data in PLANTS.items() if data.type in OCEAN_PLANT_TYPES and not data.clay]

        if name == 'shore':
            features[Decoration.SURFACE_DECORATION] += ['tfc:%s_patch' % v for v in SHORE_DECORATORS]
        else:
            features[Decoration.SURFACE_DECORATION] += ['tfc:plant/giant_kelp_patch', 'tfc:plant/winged_kelp', 'tfc:plant/leafy_kelp']  # Kelp
            features[Decoration.SURFACE_DECORATION] += ['tfc:clam_patch', 'tfc:mollusk_patch', 'tfc:mussel_patch']

        spawners.update({
            'water_ambient': [entity for entity in OCEAN_AMBIENT.values()]
        })
    if category == 'river':
        spawners.update({
            'water_ambient': [entity for entity in LAKE_AMBIENT.values()]
        })

    if reef_features and temp.id in ('lukewarm', 'warm'):
        features[Decoration.LARGE_FEATURES].append('tfc:coral_reef')

    # Continental / Land Features
    if land_features:
        features[Decoration.SOIL_DISKS] += ['tfc:clay_disc_with_indicator', 'tfc:water_clay_disc_with_indicator', 'tfc:peat_disc']
        if temp.id in ('cold', 'frozen'):
            features[Decoration.SOIL_DISKS] += ['tfc:powder_snow']
        features[Decoration.LARGE_FEATURES] += ['tfc:forest', 'tfc:bamboo', 'tfc:cave_vegetation']
        features[Decoration.SURFACE_DECORATION] += ['tfc:plant/%s' % plant for plant in MISC_PLANT_FEATURES]

        features[Decoration.SURFACE_DECORATION] += ['tfc:%s_patch' % v for v in FOREST_DECORATORS if not ocean_features]

        # leaving freshwater plants to spawn anywhere so that they populate small lakes (something vanilla doesn't think to do)
        features[Decoration.SURFACE_DECORATION] += ['tfc:plant/%s_patch' % plant for plant, data in PLANTS.items() if data.type not in OCEAN_PLANT_TYPES and not data.clay]
        features[Decoration.SURFACE_DECORATION] += ['tfc:plant/moss_cover', 'tfc:plant/reindeer_lichen_cover', 'tfc:plant/morning_glory_cover', 'tfc:plant/tree_fern', 'tfc:plant/arundo']
        #features[Decoration.SURFACE_DECORATION] += ['tfc:plant/%s_bush' % berry for berry in BERRIES] todo: broken
        features[Decoration.SURFACE_DECORATION] += ['tfc:plant/%s' % fruit for fruit in FRUITS]

    if volcano_features:
        features[Decoration.LARGE_FEATURES] += ['tfc:volcano_rivulet', 'tfc:volcano_caldera', 'tfc:random_volcano_fissure']

    if hot_spring_features:  # can be True, 'empty'
        if hot_spring_features == 'empty':
            features[Decoration.LARGE_FEATURES].append('tfc:random_empty_hot_spring')
        else:
            features[Decoration.LARGE_FEATURES].append('tfc:random_active_hot_spring')

    if lake_features:
        features[Decoration.LAKES] += ['tfc:flood_fill_lake']

    features[Decoration.ICE_AND_SNOW].append('tfc:ice_and_snow')  # This must go last

    # Carvers
    air_carvers = ['tfc:cave', 'tfc:canyon']
    water_carvers = []

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
        spawners=spawners,
        air_carvers=air_carvers,
        water_carvers=water_carvers,
        features=features,
        player_spawn_friendly=spawnable
    )


def expand_rocks(rocks_list: List[str], path: Optional[str] = None) -> List[str]:
    rocks = []
    for rock_spec in rocks_list:
        if rock_spec in ROCKS:
            rocks.append(rock_spec)
        elif rock_spec in ROCK_CATEGORIES:
            rocks += [r for r, d in ROCKS.items() if d.category == rock_spec]
        else:
            raise RuntimeError('Unknown rock or rock category specification: %s at %s' % (rock_spec, path if path is not None else '??'))
    return rocks


def join_not_empty(c: str, *elements: str) -> str:
    return c.join((item for item in elements if item != ''))


def count_weighted_list(*pairs: Tuple[Any, int]) -> List[Any]:
    return [item for item, count in pairs for _ in range(count)]

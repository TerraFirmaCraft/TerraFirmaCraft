# Handles generation of all world gen objects

from typing import Union, Literal, List, get_args

from mcresources import ResourceManager, utils
from mcresources.type_definitions import ResourceIdentifier, JsonObject, Json, VerticalAnchor

from constants import *


def generate(rm: ResourceManager):

    # Biome Feature Tags
    # Biomes -> in_biome/<step>/<optional biome>
    # in_biome/ -> other tags in the form feature/<name>s
    # feature/ -> individual features

    # Tags: in_biome/
    rm.placed_feature_tag('in_biome/erosion', 'tfc:erosion')
    rm.placed_feature_tag('in_biome/underground_lakes', 'tfc:underground_flood_fill_lake')
    rm.placed_feature_tag('in_biome/all_lakes', 'tfc:underground_flood_fill_lake', 'tfc:flood_fill_lake')
    rm.placed_feature_tag('in_biome/veins', *[
        'tfc:vein/gravel',
        'tfc:vein/kaolin_disc',
        *['tfc:vein/%s_dike' % rock for rock, data in ROCKS.items() if data.category == 'igneous_intrusive'],
        *('tfc:vein/%s' % v for v in ORE_VEINS.keys()),
        'tfc:geode'
    ])
    rm.placed_feature_tag('in_biome/underground_decoration', *UNDERGROUND_FEATURES)
    rm.placed_feature_tag('in_biome/top_layer_modification', 'tfc:surface_loose_rocks', 'tfc:ice_and_snow')

    rm.placed_feature_tag('in_biome/underground_structures')
    rm.placed_feature_tag('in_biome/surface_structures')
    rm.placed_feature_tag('in_biome/strongholds')

    # Tags: feature/
    rm.placed_feature_tag('feature/land_plants', *[
        *['tfc:plant/%s' % plant for plant in MISC_PLANT_FEATURES],
        '#tfc:feature/crops',
        '#tfc:feature/forest_plants',
        'tfc:plant/arundo_forest',
        'tfc:surface_grasses',  # Special, because it uses noise to select which to place
        *['tfc:plant/%s_patch' % plant for plant, data in PLANTS.items() if data.type not in OCEAN_PLANT_TYPES and not data.clay and data.worldgen],
        '#tfc:feature/berry_bushes',
        '#tfc:feature/fruit_trees'
    ])
    rm.placed_feature_tag('feature/forest_plants', *['tfc:%s_patch' % d for d in FOREST_DECORATORS])
    rm.placed_feature_tag('feature/ocean_plants', *['tfc:plant/%s_patch' % plant for plant, data in PLANTS.items() if data.type in OCEAN_PLANT_TYPES and not data.clay])
    rm.placed_feature_tag('feature/shore_decorations', 'tfc:tide_pool', 'tfc:big_tide_pool', *['tfc:%s_patch' % v for v in SHORE_DECORATORS])
    rm.placed_feature_tag('feature/ocean_decorations', 'tfc:plant/giant_kelp_patch', 'tfc:plant/winged_kelp', 'tfc:plant/leafy_kelp', 'tfc:clam_patch', 'tfc:mollusk_patch', 'tfc:mussel_patch')
    rm.placed_feature_tag('feature/clay_indicators', 'tfc:plant/athyrium_fern_patch', 'tfc:plant/canna_patch', 'tfc:plant/goldenrod_patch', 'tfc:plant/pampas_grass_patch', 'tfc:plant/perovskia_patch', 'tfc:plant/water_canna_patch')
    rm.placed_feature_tag('feature/surface_grasses', *['tfc:plant/%s_patch' % p for p, data in PLANTS.items() if data.type == 'short_grass'])
    rm.placed_feature_tag('feature/icebergs', 'tfc:iceberg_packed', 'tfc:iceberg_blue', 'tfc:iceberg_packed_rare', 'tfc:iceberg_blue_rare')
    rm.placed_feature_tag('feature/boulders', 'tfc:raw_boulder', 'tfc:cobble_boulder', 'tfc:mossy_boulder', 'tfc:raw_boulder_small_patch', 'tfc:cobble_boulder_small_patch', 'tfc:mossy_boulder_small_patch')
    rm.placed_feature_tag('feature/soil_discs', 'tfc:clay_disc_with_indicator', 'tfc:water_clay_disc_with_indicator', 'tfc:peat_disc', 'tfc:powder_snow', 'tfc:rooted_dirt')
    rm.placed_feature_tag('feature/volcanoes', 'tfc:volcano_rivulet', 'tfc:volcano_caldera', 'tfc:random_volcano_fissure', 'tfc:pumice_patch')

    # Biomes
    biome(rm, 'badlands', 'mesa', lake_features=False)
    biome(rm, 'inverted_badlands', 'mesa', lake_features=False)
    biome(rm, 'canyons', 'plains', boulders=True, lake_features=False, volcano_features=True, hot_spring_features=True)
    biome(rm, 'low_canyons', 'swamp', boulders=True, lake_features=False, hot_spring_features='empty')
    biome(rm, 'plains', 'plains')
    biome(rm, 'plateau', 'extreme_hills', boulders=True, hot_spring_features='empty')
    biome(rm, 'hills', 'plains')
    biome(rm, 'rolling_hills', 'plains', boulders=True)
    biome(rm, 'highlands', 'plains', boulders=True, hot_spring_features='empty')
    biome(rm, 'lake', 'river')
    biome(rm, 'lowlands', 'swamp', lake_features=False, ocean_features='both')
    biome(rm, 'salt_marsh', 'swamp', lake_features=False, ocean_features='both')
    biome(rm, 'mountains', 'extreme_hills')
    biome(rm, 'volcanic_mountains', 'extreme_hills', volcano_features=True, hot_spring_features=True)
    biome(rm, 'old_mountains', 'extreme_hills', hot_spring_features=True)
    biome(rm, 'oceanic_mountains', 'extreme_hills', ocean_features='both')
    biome(rm, 'volcanic_oceanic_mountains', 'extreme_hills', ocean_features='both', volcano_features=True)
    biome(rm, 'ocean', 'ocean', ocean_features=True)
    biome(rm, 'ocean_reef', 'ocean', ocean_features=True, reef_features=True)
    biome(rm, 'deep_ocean', 'ocean', ocean_features=True)
    biome(rm, 'deep_ocean_trench', 'ocean', ocean_features=True)
    biome(rm, 'river', 'river')
    biome(rm, 'shore', 'beach', ocean_features=True)
    biome(rm, 'tidal_flats', 'beach', ocean_features=True)
    biome(rm, 'salt_flats', 'plains', barren=True)
    biome(rm, 'mud_flats', 'plains', barren=True)
    biome(rm, 'dune_sea', 'plains', barren=True)
    biome(rm, 'grassy_dunes', 'plains')

    biome(rm, 'mountain_lake', 'extreme_hills')
    biome(rm, 'volcanic_mountain_lake', 'extreme_hills', volcano_features=True)
    biome(rm, 'old_mountain_lake', 'extreme_hills')
    biome(rm, 'oceanic_mountain_lake', 'river', ocean_features='both')
    biome(rm, 'volcanic_oceanic_mountain_lake', 'river', ocean_features='both', volcano_features=True)
    biome(rm, 'plateau_lake', 'extreme_hills', boulders=True)

    # Carvers
    rm.configured_carver('cave', 'tfc:cave', {
        'probability': 0.3,
        'y': height_provider(-56, 126),
        'yScale': uniform_float(0.1, 0.9),
        'lava_level': utils.vertical_anchor(8, 'above_bottom'),
        'aquifers_enabled': True,
        'horizontal_radius_multiplier': uniform_float(0.7, 1.4),
        'vertical_radius_multiplier': uniform_float(0.8, 1.3),
        'floor_level': uniform_float(-1, -0.4),
        'replaceable': '#minecraft:overworld_carver_replaceables',
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
        },
        'replaceable': '#minecraft:overworld_carver_replaceables',
    })

    # Configured and Placed Features

    configured_placed_feature(rm, 'surface_grasses', 'tfc:noisy_multiple', {'features': '#tfc:feature/surface_grasses'})

    # Clay Discs
    # []_with_indicator (PF) -> if_then (CF) -> if [] -> then clay_indicator
    clay = [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:clay/%s' % soil} for soil in SOIL_BLOCK_VARIANTS] + [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:clay_grass/%s' % soil} for soil in SOIL_BLOCK_VARIANTS]
    water_clay = clay + [{'replace': 'tfc:mud/%s' % soil, 'with': 'tfc:clay/%s' % soil} for soil in SOIL_BLOCK_VARIANTS]

    configured_placed_feature(rm, 'clay_disc_with_indicator', 'tfc:if_then', {
        'if': 'tfc:clay_disc',
        'then': 'tfc:clay_indicator'
    }, decorate_chance(20), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_water=175))
    configured_placed_feature(rm, 'water_clay_disc_with_indicator', 'tfc:if_then', {
        'if': 'tfc:water_clay_disc',
        'then': 'tfc:clay_indicator'
    }, decorate_chance(10), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_near_water(radius=4, fresh_water=True))

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
        'states': water_clay
    })

    configured_placed_feature(rm, 'rooted_dirt', 'tfc:soil_disc', {
        'min_radius': 4,
        'max_radius': 6,
        'height': 2,
        'integrity': 0.8,
        'states': [{
            'replace': 'tfc:%s/%s' % (variant, soil),
            'with': 'tfc:rooted_dirt/%s' % soil
        } for soil in SOIL_BLOCK_VARIANTS for variant in ('grass', 'dirt')]
    }, decorate_chance(4), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_forest='normal', min_water=100, max_water=350, fuzzy=True))

    # Individual indicator plants are invoked through multiple, which has decorators attached already
    configured_placed_feature(rm, 'clay_indicator', 'tfc:multiple', {'features': '#tfc:feature/clay_indicators'})

    configured_placed_feature(rm, 'tfc:erosion')
    configured_placed_feature(rm, 'tfc:ice_and_snow')

    for block in ('packed', 'blue'):
        rm.configured_feature('iceberg_%s' % block, 'tfc:iceberg', {'state': utils.block_state('minecraft:%s_ice' % block)})
        rm.configured_feature('iceberg_%s_rare' % block, 'tfc:iceberg', {'state': utils.block_state('minecraft:%s_ice' % block)})

        rm.placed_feature('iceberg_%s' % block, 'tfc:iceberg_%s' % block, decorate_chance(14), decorate_square(), decorate_climate(max_temp=-23))
        rm.placed_feature('iceberg_%s_rare' % block, 'tfc:iceberg_%s_rare' % block, decorate_chance(30), decorate_square(), decorate_climate(max_temp=-18))

    rm.configured_feature('powder_snow', 'tfc:powder_snow', {'state': utils.block_state('minecraft:powder_snow')})
    rm.placed_feature('powder_snow', 'tfc:powder_snow', decorate_chance(15), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(max_temp=-17), decorate_flat_enough(flatness=0.6))

    rm.configured_feature('flood_fill_lake', 'tfc:flood_fill_lake', {
        'state': 'minecraft:water',
        'replace_fluids': [],
    })

    rm.placed_feature('flood_fill_lake', 'tfc:flood_fill_lake', decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_water=125))
    rm.placed_feature('underground_flood_fill_lake', 'tfc:flood_fill_lake', decorate_chance(3), decorate_square(), decorate_range(-56, 63))

    for spring_cfg in (('water', 110), ('lava', 50)):
        rm.configured_feature('%s_spring' % spring_cfg[0], 'tfc:spring', {
            'state': utils.block_state('minecraft:%s[falling=true]' % spring_cfg[0]),
            'valid_blocks': ['tfc:rock/raw/%s' % rock for rock in ROCKS.keys()]
        })
        rm.placed_feature('%s_spring' % spring_cfg[0], 'tfc:%s_spring' % spring_cfg[0], decorate_count(spring_cfg[1]), decorate_square(), decorate_range(-64, 180, bias='biased_to_bottom'))

    rm.configured_feature('peat_disc', 'tfc:soil_disc', {
        'min_radius': 5,
        'max_radius': 9,
        'height': 7,
        'states': [{'replace': 'tfc:dirt/%s' % soil, 'with': 'tfc:peat'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:grass/%s' % soil, 'with': 'tfc:peat_grass'} for soil in SOIL_BLOCK_VARIANTS] +
                  [{'replace': 'tfc:mud/%s' % soil, 'with': 'tfc:peat'} for soil in SOIL_BLOCK_VARIANTS]
    })
    rm.placed_feature('peat_disc', 'tfc:peat_disc', decorate_chance(40), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(min_water=350, min_temp=12))

    for ore in ORE_DEPOSITS:
        configured_placed_feature(rm, '%s_deposit' % ore, 'tfc:soil_disc', {
            'min_radius': 1,
            'max_radius': 3,
            'height': 2,
            'integrity': 0.9,
            'states': [{'replace': 'tfc:rock/gravel/%s' % rock, 'with': 'tfc:deposit/%s/%s' % (ore, rock)} for rock in ROCKS.keys()]
        }, decorate_chance(12), decorate_square(), decorate_heightmap('ocean_floor_wg'), decorate_biome())

        configured_placed_feature(rm, '%s_deep_deposit' % ore, 'tfc:soil_disc', {
            'min_radius': 3,
            'max_radius': 10,
            'height': 3,
            'integrity': 0.9,
            'states': [{'replace': 'tfc:rock/raw/%s' % rock, 'with': 'tfc:deposit/%s/%s' % (ore, rock)} for rock in ROCKS.keys()]
        }, decorate_chance(24), decorate_square(), decorate_range(40, 63), decorate_biome())

        rm.placed_feature_tag('feature/ore_deposits', 'tfc:%s_deposit' % ore, 'tfc:%s_deep_deposit' % ore)

    rm.configured_feature('cave_spike', 'tfc:cave_spike')
    rm.configured_feature('large_cave_spike', 'tfc:large_cave_spike')

    rm.placed_feature('cave_spike', 'tfc:cave_spike', decorate_carving_mask(), decorate_chance(5))
    rm.placed_feature('large_cave_spike', 'tfc:large_cave_spike', decorate_carving_mask(utils.vertical_anchor(25, 'above_bottom')), decorate_chance(0.006))

    configured_placed_feature(rm, 'cave_column', 'tfc:cave_column', {}, decorate_carving_mask(utils.vertical_anchor(25, 'above_bottom')), decorate_chance(0.0015))

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

    rm.placed_feature('calcite', 'tfc:calcite', decorate_count(4), decorate_square(), decorate_range(-56, 60, bias='biased_to_bottom'))
    rm.placed_feature('mega_calcite', 'tfc:mega_calcite', decorate_chance(20), decorate_square(), decorate_range(-56, 30, bias='biased_to_bottom'))

    rm.configured_feature('icicle', 'tfc:thin_spike', {
        'state': 'tfc:icicle',
        'radius': 10,
        'tries': 50,
        'min_height': 2,
        'max_height': 5
    })
    rm.placed_feature('icicle', 'tfc:icicle', decorate_count(3), decorate_square(), decorate_range(-32, 100, bias='biased_to_bottom'), decorate_climate(max_temp=-4))

    for boulder_cfg in (('raw_boulder', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.configured_feature(boulder_cfg[0], 'tfc:boulder', {
            'states': [{
                'rock': 'tfc:rock/raw/%s' % rock,
                'blocks': ['tfc:rock/%s/%s' % (t, rock) for t in boulder_cfg[1:]]
            } for rock in ROCKS.keys()]
        })
        rm.placed_feature(boulder_cfg[0], 'tfc:%s' % boulder_cfg[0], decorate_chance(22), decorate_square(), decorate_heightmap('ocean_floor_wg'), decorate_flat_enough(flatness=0.4))

        name = boulder_cfg[0] + '_small'
        rm.configured_feature(name, 'tfc:baby_boulder', {
            'states': [{
                'rock': 'tfc:rock/raw/%s' % rock,
                'blocks': ['tfc:rock/%s/%s' % (t, rock) for t in boulder_cfg[1:]]
            } for rock in ROCKS.keys()]
        })
        rm.placed_feature(name, 'tfc:%s' % name, decorate_heightmap('ocean_floor_wg'), decorate_flat_enough(flatness=0.2))
        configured_placed_feature(rm, 'tfc:%s_patch' % name, 'minecraft:random_patch', {'tries': 6, 'xz_spread': 5, 'y_spread': 1, 'feature': 'tfc:%s' % name}, decorate_chance(36), decorate_square())


    rm.configured_feature('volcano_rivulet', 'tfc:rivulet', {'state': 'tfc:rock/magma/basalt'})
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
            ('tfc:diamond_volcano_fissure', 1),
            ('tfc:volcano_fissure', 4)
        )
    })

    rocks = expand_rocks(['igneous_extrusive', 'igneous_intrusive', 'metamorphic'])
    for ore in ('diamond', 'topaz', ''):
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
            } if ore != '' else None,
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

    igneous_rocks = expand_rocks(['igneous_extrusive', 'igneous_intrusive'])
    configured_placed_feature(rm, 'lava_hot_spring', 'tfc:hot_spring', {
        'fluid_state': 'minecraft:lava',
        'radius': 10,
        'allow_underwater': True,
        'replaces_on_fluid_contact': [{
            'replace': ['tfc:rock/raw/%s' % rock],
            'with': [
                {'block': 'tfc:rock/magma/%s' % rock, 'weight': 1},
                {'block': 'tfc:rock/hardened/%s' % rock, 'weight': 2}
            ]
        } for rock in igneous_rocks]
    }, decorate_chance(20), decorate_square())

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
    forest_config(rm, 90, 275, 8, 38, 'acacia', True)
    forest_config(rm, 60, 240, -1.1, 13.4, 'ash', True)
    forest_config(rm, 350, 500, -15.7, -1.1, 'aspen', True, old_growth_chance=1, krum=True)
    forest_config(rm, 125, 310, -12.1, 6.1, 'birch', False, old_growth_chance=1)
    forest_config(rm, 35, 180, 8, 38, 'blackwood', True)
    forest_config(rm, 150, 340, -3, 11.6, 'chestnut', True)
    forest_config(rm, 305, 500, -15.7, 6.1, 'douglas_fir', True, krum=True)
    forest_config(rm, 210, 400, 4.3, 15.3, 'hickory', True)
    forest_config(rm, 320, 500, 17.1, 38, 'kapok', False)
    forest_config(rm, 200, 500, 15.7, 28.2, 'mangrove', False, floating=True)
    forest_config(rm, 240, 470, -8.4, 8, 'maple', True)
    forest_config(rm, 210, 320, -3, 15.3, 'oak', False)
    forest_config(rm, 200, 405, 15.3, 38, 'palm', False)
    forest_config(rm, 185, 320, -8.4, 9.8, 'pine', True, old_growth_chance=1, krum=True)
    forest_config(rm, 210, 400, 9.8, 38, 'rosewood', True)
    forest_config(rm, 320, 500, 4.3, 11.6, 'sequoia', True, old_growth_chance=3)
    forest_config(rm, 220, 470, -17.5, -6.6, 'spruce', True, krum=True)
    forest_config(rm, 330, 480, -6.6, 13.4, 'sycamore', True)
    forest_config(rm, 100, 285, -15.7, 0.7, 'white_cedar', True, krum=True)
    forest_config(rm, 330, 500, 6.1, 24.4, 'willow', True)
    # flat: acacia, ash, chestnut, maple, sequoia, spruce, willow

    for wood in ('aspen', 'douglas_fir', 'pine', 'spruce', 'white_cedar'):
        rm.configured_feature('tree/%s_krummholz' % wood, 'tfc:krummholz', {
            'block': 'tfc:plant/%s_krummholz' % wood,
            'height': uniform_int(1, 4),
        })

    configured_placed_feature(rm, 'forest', 'tfc:forest', {'entries': '#tfc:forest_trees'})
    configured_placed_feature(rm, 'mangrove_forest', 'tfc:forest', {'entries': '#tfc:mangrove_forest_trees'})

    configured_placed_feature(rm, 'dead_forest', 'tfc:forest', {'entries': '#tfc:dead_forest_trees'})

    rm.configured_feature_tag('forest_trees', *['tfc:tree/%s_entry' % tree for tree in WOODS.keys() if tree != 'mangrove'])
    rm.configured_feature_tag('dead_forest_trees', *['tfc:tree/dead_%s_entry' % tree for tree in WOODS.keys() if tree != 'mangrove'])
    rm.configured_feature_tag('mangrove_forest_trees', 'tfc:tree/mangrove_entry')

    configured_placed_feature(rm, ('tree', 'acacia'), 'tfc:random_tree', random_config('acacia', 35, place=tree_placement_config(1, 3)))
    configured_placed_feature(rm, ('tree', 'acacia_large'), 'tfc:random_tree', random_config('acacia', 11, 1, '_large', place=tree_placement_config(2, 5)))
    configured_placed_feature(rm, ('tree', 'acacia_dead'), 'tfc:random_tree', random_config('acacia', 6, 1, '_dead', place=tree_placement_config(1, 6, 'submerged')))
    configured_placed_feature(rm, ('tree', 'ash'), 'tfc:random_tree', random_config('ash', 16, 1, trunk=[3, 6, 1], place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'ash_large'), 'tfc:random_tree', random_config('ash', 7, 1, '_large', trunk=[4, 6, 1], place=tree_placement_config(2, 5)))
    configured_placed_feature(rm, ('tree', 'ash_dead'), 'tfc:random_tree', random_config('ash', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'aspen'), 'tfc:random_tree', random_config('aspen', 16, trunk=[3, 5, 1], place=tree_placement_config(1, 7)))
    configured_placed_feature(rm, ('tree', 'aspen_large'), 'tfc:random_tree', random_config('aspen', 7, 1, '_large', trunk=[5, 8, 1], place=tree_placement_config(1, 10)))
    configured_placed_feature(rm, ('tree', 'aspen_dead'), 'tfc:random_tree', random_config('aspen', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'birch'), 'tfc:random_tree', random_config('birch', 6, trunk=[3, 5, 1], place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'birch_large'), 'tfc:random_tree', random_config('birch', 6, 1, '_large', trunk=[4, 7, 1], place=tree_placement_config(1, 10)))
    configured_placed_feature(rm, ('tree', 'birch_dead'), 'tfc:random_tree', random_config('birch', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'blackwood'), 'tfc:random_tree', random_config('blackwood', 10, place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'blackwood_large'), 'tfc:random_tree', random_config('blackwood', 10, 1, '_large', place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'blackwood_dead'), 'tfc:random_tree', random_config('blackwood', 6, 1, '_dead', place=tree_placement_config(1, 6, 'submerged')))
    configured_placed_feature(rm, ('tree', 'chestnut'), 'tfc:random_tree', random_config('chestnut', 6, 1, trunk=[1, 2, 1], place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'chestnut_large'), 'tfc:random_tree', random_config('chestnut', 5, 2, '_large', place=tree_placement_config(2, 5)))
    configured_placed_feature(rm, ('tree', 'chestnut_dead'), 'tfc:random_tree', random_config('chestnut', 6, 1, '_dead', place=tree_placement_config(1, 6, 'submerged')))
    configured_placed_feature(rm, ('tree', 'douglas_fir'), 'tfc:random_tree', random_config('douglas_fir', 10, place=tree_placement_config(1, 2)))
    configured_placed_feature(rm, ('tree', 'douglas_fir_large'), 'tfc:stacked_tree', stacked_config('douglas_fir', 8, 12, 2, [(2, 3, 3), (1, 1, 3), (1, 1, 3)], 2, '_large', place=tree_placement_config(2, 7)))
    configured_placed_feature(rm, ('tree', 'douglas_fir_dead'), 'tfc:random_tree', random_config('douglas_fir', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'hickory'), 'tfc:random_tree', random_config('hickory', 10, trunk=[1, 3, 1], place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'hickory_large'), 'tfc:random_tree', random_config('hickory', 6, 2, '_large', place=tree_placement_config(1, 9)))
    configured_placed_feature(rm, ('tree', 'hickory_dead'), 'tfc:random_tree', random_config('hickory', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'kapok'), 'tfc:random_tree', random_config('kapok', 17, place=tree_placement_config(1, 5, 'shallow_water'), roots=root_config(4, 6, 20)))
    configured_placed_feature(rm, ('tree', 'kapok_dead'), 'tfc:random_tree', random_config('kapok', 4, 1, '_dead', place=tree_placement_config(2, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'mangrove'), 'tfc:random_tree', random_config('mangrove', 12, place=tree_placement_config(1, 3, 'floating'), roots=root_config(15, 8, 1, mangrove=True)))
    configured_placed_feature(rm, ('tree', 'mangrove_dead'), 'tfc:random_tree', random_config('mangrove', 3, 1, '_dead', place=tree_placement_config(2, 3, 'submerged_allow_saltwater'), roots=root_config(4, 2, 13)))
    configured_placed_feature(rm, ('tree', 'maple'), 'tfc:random_tree', random_config('maple', 23, 1, place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'maple_large'), 'tfc:random_tree', random_config('maple', 6, 2, '_large', place=tree_placement_config(2, 5)))
    configured_placed_feature(rm, ('tree', 'maple_dead'), 'tfc:random_tree', random_config('maple', 6, 1, '_dead', place=tree_placement_config(1, 6, 'submerged')))
    configured_placed_feature(rm, ('tree', 'oak'), 'tfc:random_tree', random_config('oak', 8, 1, place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'oak_dead'), 'tfc:random_tree', random_config('oak', 6, 1, '_dead', place=tree_placement_config(1, 6, 'submerged')))
    configured_placed_feature(rm, ('tree', 'palm'), 'tfc:random_tree', random_config('palm', 7, place=tree_placement_config(1, 5, 'sand')))
    configured_placed_feature(rm, ('tree', 'palm_dead'), 'tfc:random_tree', random_config('palm', 3, 1, '_dead', place=tree_placement_config(2, 3, 'submerged')))
    configured_placed_feature(rm, ('tree', 'pine'), 'tfc:random_tree', random_config('pine', 9, trunk=[1, 2, 1], place=tree_placement_config(1, 3)))
    configured_placed_feature(rm, ('tree', 'pine_large'), 'tfc:random_tree', random_config('pine', 9, 1, trunk=[3, 5, 1], place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'pine_dead'), 'tfc:random_tree', random_config('pine', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'rosewood'), 'tfc:random_tree', random_config('rosewood', 18, 1, place=tree_placement_config(1, 9)))
    configured_placed_feature(rm, ('tree', 'rosewood_large'), 'tfc:random_tree', random_config('rosewood', 8, 1, '_large', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'rosewood_dead'), 'tfc:random_tree', random_config('rosewood', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'sequoia'), 'tfc:random_tree', random_config('sequoia', 7, place=tree_placement_config(1, 3), roots=root_config(4, 2, 15)))
    configured_placed_feature(rm, ('tree', 'sequoia_large'), 'tfc:stacked_tree', stacked_config('sequoia', 8, 16, 2, [(2, 3, 3), (1, 2, 3), (1, 1, 3)], 2, '_large', place=tree_placement_config(2, 7), roots=root_config(6, 3, 33)))
    configured_placed_feature(rm, ('tree', 'sequoia_dead'), 'tfc:random_tree', random_config('sequoia', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged'), roots=root_config(4, 2, 15)))
    configured_placed_feature(rm, ('tree', 'spruce'), 'tfc:random_tree', random_config('spruce', 11, place=tree_placement_config(1, 3)))
    configured_placed_feature(rm, ('tree', 'spruce_large'), 'tfc:random_tree', random_config('spruce', 11, trunk=[3, 5, 1], place=tree_placement_config(1, 3)))
    configured_placed_feature(rm, ('tree', 'spruce_dead'), 'tfc:random_tree', random_config('spruce', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'sycamore'), 'tfc:random_tree', random_config('sycamore', 5, 1, trunk=[1, 2, 1], place=tree_placement_config(1, 5, 'shallow_water')))
    configured_placed_feature(rm, ('tree', 'sycamore_large'), 'tfc:random_tree', random_config('sycamore', 5, 2, '_large', trunk=[1, 2, 2], place=tree_placement_config(2, 5, 'shallow_water')))
    configured_placed_feature(rm, ('tree', 'sycamore_dead'), 'tfc:random_tree', random_config('sycamore', 6, 1, '_dead', place=tree_placement_config(1, 6, 'shallow_water')))
    configured_placed_feature(rm, ('tree', 'white_cedar'), 'tfc:overlay_tree', overlay_config('white_cedar', 2, 4, place=tree_placement_config(1, 5)))
    configured_placed_feature(rm, ('tree', 'white_cedar_large'), 'tfc:overlay_tree', overlay_config('white_cedar', 2, 5, 1, 1, '_large', place=tree_placement_config(1, 9)))
    configured_placed_feature(rm, ('tree', 'white_cedar_dead'), 'tfc:random_tree', random_config('white_cedar', 6, 1, '_dead', place=tree_placement_config(1, 9, 'submerged')))
    configured_placed_feature(rm, ('tree', 'willow'), 'tfc:random_tree', random_config('willow', 7, place=tree_placement_config(1, 3, 'shallow_water'), roots=root_config(4, 2, 15)))
    configured_placed_feature(rm, ('tree', 'willow_large'), 'tfc:random_tree', random_config('willow', 14, 1, '_large', place=tree_placement_config(2, 3, 'shallow_water'), roots=root_config(5, 4, 20)))
    configured_placed_feature(rm, ('tree', 'willow_dead'), 'tfc:random_tree', random_config('willow', 3, 1, '_dead', place=tree_placement_config(2, 3, 'submerged'), roots=root_config(4, 2, 20)))

    # Ore Veins
    for vein_name, vein in ORE_VEINS.items():
        rocks = expand_rocks(vein.rocks)
        ore = ORES[vein.ore]  # standard ore
        if ore.graded:  # graded ore vein
            configured_placed_feature(rm, ('vein', vein_name), vein.vein_type, {
                **vein.config(),
                'random_name': vein_name,
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock],
                    'with': vein_ore_blocks(vein, rock)
                } for rock in rocks],
                'indicator': {
                    'rarity': vein.indicator_rarity,
                    'depth': 35,
                    'underground_rarity': vein.underground_rarity,
                    'underground_count': vein.underground_count,
                    'blocks': [{
                        'block': 'tfc:ore/small_%s' % vein.ore
                    }]
                },
            })
        else:  # non-graded ore vein (mineral)
            configured_placed_feature(rm, ('vein', vein_name), vein.vein_type, {
                **vein.config(),
                'random_name': vein_name,
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % rock],
                    'with': mineral_ore_blocks(vein, rock)
                } for rock in rocks],
            })

    configured_placed_feature(rm, ('vein', 'kaolin_disc'), 'tfc:kaolin_disc_vein', {
        'rarity': 50,
        'min_y': -7,
        'max_y': 1,
        'size': 18,
        'height': 6,
        'density': 1.0,
        'project': True,
        'random_name': 'kaolin',
        'biomes': '#tfc:kaolin_clay_spawns_in',
        'indicator': {
            'depth': 35,
            'spread': 5,
            'rarity': 10,
            'underground_rarity': 1,
            'underground_count': 0,
            'blocks': [{'block': 'tfc:plant/blood_lily'}]
        },
        'blocks': [],
    }, decorate_climate(min_water=300, min_temp=18))
    rm.biome_tag('kaolin_clay_spawns_in', 'tfc:plateau', 'tfc:highlands', 'tfc:old_mountains', 'tfc:rolling_hills')

    configured_placed_feature(rm, ('vein', 'gravel'), 'tfc:disc_vein', {
        'rarity': 30,
        'min_y': -64,
        'max_y': 100,
        'size': 44,
        'height': 2,
        'density': 0.98,
        'project': False,
        'random_name': 'gravel',
        'blocks': [{
            'replace': ['tfc:rock/raw/%s' % rock],
            'with': [{'block': 'tfc:rock/gravel/%s' % rock}]
        } for rock in ROCKS.keys()],
    })

    for rock, data in ROCKS.items():
        if data.category == 'igneous_intrusive':
            configured_placed_feature(rm, ('vein', '%s_dike' % rock), 'tfc:pipe_vein', {
                'rarity': 300,
                'min_y': -64,
                'max_y': 180,
                'density': 0.98,
                'random_name': rock,
                'height': 150,
                'radius': 18,
                'min_skew': 7,
                'max_skew': 20,
                'min_slant': 2,
                'max_slant': 5,
                'project': False,
                'sign': 0,
                'blocks': [{
                    'replace': ['tfc:rock/raw/%s' % r for r in ROCKS] + ['tfc:rock/gravel/%s' % r for r in ROCKS],
                    'with': [{'block': 'tfc:rock/raw/%s' % rock}]
                }, {
                    'replace': ['tfc:rock/hardened/%s' % r for r in ROCKS],
                    'with': [{'block': 'tfc:rock/hardened/%s' % rock}]
                }],
            })
    configured_placed_feature(rm, 'sea_stacks', 'tfc:sea_stacks', {}, decorate_heightmap('ocean_floor_wg'), decorate_replaceable())
    configured_placed_feature(rm, 'sea_stacks_patch', 'minecraft:random_patch', {'feature': 'tfc:sea_stacks', 'tries': 6, 'xz_spread': 8, 'y_spread': 2}, decorate_chance(120), decorate_square(), decorate_heightmap('ocean_floor_wg'))


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
        'replaceable': '#minecraft:base_stone_overworld',
        'ground_state': simple_state_provider('tfc:rooted_dirt/silt')
    })
    rm.placed_feature('hanging_roots_patch', 'tfc:hanging_roots_patch', decorate_count(10), decorate_square(), decorate_range(40, 72), decorate_scanner('up', 12), decorate_random_offset(0, -1), decorate_climate(min_water=300, min_temp=0), decorate_biome())

    # Plants
    configured_plant_patch_feature(rm, ('plant', 'allium'), plant_config('tfc:plant/allium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, -1.1, 150, 400, min_forest='edge', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'anthurium'), plant_config('tfc:plant/anthurium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(11.6, 38, 290, 500))
    configured_plant_patch_feature(rm, ('plant', 'arrowhead'), plant_config('tfc:plant/arrowhead[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 20.7, 180, 500))
    configured_plant_patch_feature(rm, ('plant', 'artists_conk'), plant_config('tfc:plant/artists_conk[age=1,stage=1,facing=north]', 6, 5, epiphyte_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-14.4, 18.6, 150, 420, min_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'houstonia'), plant_config('tfc:plant/houstonia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 9.8, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'badderlocks'), plant_config('tfc:plant/badderlocks[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, tall_water_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-15.7, 2.5, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'barrel_cactus'), plant_config('tfc:plant/barrel_cactus[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, no_solid_neighbors=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(4.3, 17.1, 0, 85))
    configured_plant_patch_feature(rm, ('plant', 'blue_orchid'), plant_config('tfc:plant/blue_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(9.8, 38, 250, 390))
    configured_plant_patch_feature(rm, ('plant', 'blue_ginger'), plant_config('tfc:plant/blue_ginger[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(15.3, 24.4, 300, 450))
    configured_plant_patch_feature(rm, ('plant', 'calendula'), plant_config('tfc:plant/calendula[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(4.3, 20.7, 130, 300))
    configured_plant_patch_feature(rm, ('plant', 'cattail'), plant_config('tfc:plant/cattail[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-13.9, 20.7, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'cordgrass'), plant_config('tfc:plant/cordgrass[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-15.7, 2.5, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'laminaria'), plant_config('tfc:plant/laminaria[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-21.2, -1.1, 100, 500))
    configured_plant_patch_feature(rm, ('plant', 'marigold'), plant_config('tfc:plant/marigold[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-6.6, 17.1, 50, 390))
    configured_plant_patch_feature(rm, ('plant', 'bur_reed'), plant_config('tfc:plant/bur_reed[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-13.9, 4.3, 250, 400))
    configured_plant_patch_feature(rm, ('plant', 'butterfly_milkweed'), plant_config('tfc:plant/butterfly_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-13.9, 17.1, 75, 300))
    configured_plant_patch_feature(rm, ('plant', 'black_orchid'), plant_config('tfc:plant/black_orchid[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 38, 290, 410))
    configured_plant_patch_feature(rm, ('plant', 'coontail'), plant_config('tfc:plant/coontail[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(2.5, 17.1, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'dandelion'), plant_config('tfc:plant/dandelion[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-19.4, 38, 120, 400))
    configured_plant_patch_feature(rm, ('plant', 'dead_bush'), plant_config('tfc:plant/dead_bush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 38, 0, 120))
    configured_plant_patch_feature(rm, ('plant', 'desert_flame'), plant_config('tfc:plant/desert_flame[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(0.7, 18.9, 40, 170, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'duckweed'), plant_config('tfc:plant/duckweed[age=1,stage=1]', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(9.8, 38, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'eel_grass'), plant_config('tfc:plant/eel_grass[age=1,stage=1,fluid=empty]', 1, 15, 100, water_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(6.1, 38, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'field_horsetail'), plant_config('tfc:plant/field_horsetail[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 18.9, 300, 500))
    configured_plant_patch_feature(rm, ('plant', 'foxglove'), plant_config('tfc:plant/foxglove[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-6.6, 15.3, 150, 300, min_forest='none', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'grape_hyacinth'), plant_config('tfc:plant/grape_hyacinth[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 9.8, 150, 250))
    configured_noise_plant_feature(rm, ('plant', 'green_algae'), plant_config('tfc:plant/green_algae[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-22.4, 27.6, 215, 450), decorate_range(0, 70), water_depth=2)
    configured_plant_patch_feature(rm, ('plant', 'gutweed'), plant_config('tfc:plant/gutweed[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-4.8, 17.1, 100, 500))
    configured_plant_patch_feature(rm, ('plant', 'heliconia'), plant_config('tfc:plant/heliconia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 38, 320, 500))
    configured_plant_patch_feature(rm, ('plant', 'heather'), plant_config('tfc:plant/heather[age=1,stage=1]', 1, 10, 20, no_solid_neighbors=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-4.8, 6.1, 180, 380, max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'hibiscus'), plant_config('tfc:plant/hibiscus[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(9.8, 22.5, 260, 450, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'kangaroo_paw'), plant_config('tfc:plant/kangaroo_paw[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 38, 100, 300))
    configured_plant_patch_feature(rm, ('plant', 'king_fern'), plant_config('tfc:plant/king_fern[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, no_solid_neighbors=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(17.1, 38, 350, 500, min_forest='normal', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'labrador_tea'), plant_config('tfc:plant/labrador_tea[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-15.7, 0.7, 200, 380))
    configured_plant_patch_feature(rm, ('plant', 'lady_fern'), plant_config('tfc:plant/lady_fern[age=1,stage=1]', 1, 10, 15), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 8, 200, 500, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'licorice_fern'), plant_config('tfc:plant/licorice_fern[age=1,stage=1,facing=north]', 6, 5, epiphyte_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(2.5, 9.8, 300, 400, min_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'lily_of_the_valley'), plant_config('tfc:plant/lily_of_the_valley[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-13.9, 13.4, 180, 415, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'lilac'), plant_config('tfc:plant/lilac[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 6.1, 150, 300))
    configured_plant_patch_feature(rm, ('plant', 'lotus'), plant_config('tfc:plant/lotus[age=1,stage=1]', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-3, 17.1, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'maiden_pink'), plant_config('tfc:plant/maiden_pink[age=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(2.5, 22.6, 100, 350))
    configured_plant_patch_feature(rm, ('plant', 'meads_milkweed'), plant_config('tfc:plant/meads_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 2.5, 130, 380))
    configured_plant_patch_feature(rm, ('plant', 'milfoil'), plant_config('tfc:plant/milfoil[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.1, 20.7, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'nasturtium'), plant_config('tfc:plant/nasturtium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(6.1, 20.7, 150, 380))
    configured_plant_patch_feature(rm, ('plant', 'ostrich_fern'), plant_config('tfc:plant/ostrich_fern[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, limit_density=True), decorate_chance(8), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.1, 6.1, 290, 470, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'oxeye_daisy'), plant_config('tfc:plant/oxeye_daisy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.1, 9.8, 120, 300, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'phragmite'), plant_config('tfc:plant/phragmite[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-4.8, 17.1, 50, 250))
    configured_plant_patch_feature(rm, ('plant', 'pickerelweed'), plant_config('tfc:plant/pickerelweed[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.1, 15.3, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'pistia'), plant_config('tfc:plant/pistia[age=1,stage=1]', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(6.1, 24.4, 0, 400))
    configured_plant_patch_feature(rm, ('plant', 'poppy'), plant_config('tfc:plant/poppy[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 13.4, 150, 250))
    configured_plant_patch_feature(rm, ('plant', 'primrose'), plant_config('tfc:plant/primrose[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-6.6, 9.8, 150, 300, min_forest='edge', max_forest='normal'))
    configured_plant_patch_feature(rm, ('plant', 'pulsatilla'), plant_config('tfc:plant/pulsatilla[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 2.5, 50, 200))
    configured_noise_plant_feature(rm, ('plant', 'red_algae'), plant_config('tfc:plant/red_algae[age=1,stage=1]', 1, 7, 100), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-22.4, 27.6, 215, 450), decorate_range(0, 70), water_depth=8, min_water_depth=5)
    configured_plant_patch_feature(rm, ('plant', 'red_sealing_wax_palm'), plant_config('tfc:plant/red_sealing_wax_palm[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, no_solid_neighbors=True, limit_density=True), decorate_chance(10), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(17.1, 38, 280, 500, min_forest='edge', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'sacred_datura'), plant_config('tfc:plant/sacred_datura[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(4.3, 17.1, 75, 150))
    configured_plant_patch_feature(rm, ('plant', 'sagebrush'), plant_config('tfc:plant/sagebrush[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 13.4, 0, 120))
    configured_plant_patch_feature(rm, ('plant', 'sago'), plant_config('tfc:plant/sago[age=1,stage=1,fluid=empty]', 1, 10, 10, water_plant=True), decorate_chance(4), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-15.7, 17.1, 200, 500))
    configured_plant_patch_feature(rm, ('plant', 'sapphire_tower'), plant_config('tfc:plant/sapphire_tower[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, no_solid_neighbors=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(9.8, 20.7, 75, 200, min_forest='none', max_forest='sparse'))
    configured_noise_plant_feature(rm, ('plant', 'sargassum'), plant_config('tfc:plant/sargassum[age=1,stage=1]', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 15.3, 0, 500), water_depth=8, min_water_depth=4)
    configured_plant_patch_feature(rm, ('plant', 'sea_palm'), plant_config('tfc:plant/sea_palm[age=1]', 1, 10, 10), decorate_chance(15), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.4, 13.6, 50, 500))
    configured_plant_patch_feature(rm, ('plant', 'sea_lavender'), plant_config('tfc:plant/sea_lavender[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-5.7, 13.9, 300, 450))
    configured_plant_patch_feature(rm, ('plant', 'guzmania'), plant_config('tfc:plant/guzmania[age=1,stage=1,facing=north]', 6, 5, epiphyte_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(18.9, 38, 290, 480, min_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'silver_spurflower'), plant_config('tfc:plant/silver_spurflower[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 22.5, 230, 400))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_pink'), plant_config('tfc:plant/snapdragon_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(15.3, 22.5, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_red'), plant_config('tfc:plant/snapdragon_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(11.6, 18.9, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_white'), plant_config('tfc:plant/snapdragon_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(8, 15.3, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'snapdragon_yellow'), plant_config('tfc:plant/snapdragon_yellow[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(6.1, 22.5, 150, 300, min_forest='none', max_forest='sparse'))
    configured_plant_patch_feature(rm, ('plant', 'strelitzia'), plant_config('tfc:plant/strelitzia[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 24.4, 50, 300))
    configured_plant_patch_feature(rm, ('plant', 'switchgrass'), plant_config('tfc:plant/switchgrass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True, limit_density=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-4.8, 20.7, 110, 390))
    configured_plant_patch_feature(rm, ('plant', 'sword_fern'), plant_config('tfc:plant/sword_fern[age=1,stage=1]', 1, 10, 15, no_solid_neighbors=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 11.6, 100, 500, min_forest='sparse', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'tall_fescue_grass'), plant_config('tfc:plant/tall_fescue_grass[age=1,stage=1,part=lower]', 1, 15, tall_plant=True, limit_density=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 9.8, 280, 430))
    configured_plant_patch_feature(rm, ('plant', 'toquilla_palm'), plant_config('tfc:plant/toquilla_palm[age=1,stage=1,part=lower]', 1, 15, 10, tall_plant=True, no_solid_neighbors=True, limit_density=True), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(15.3, 38, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'trillium'), plant_config('tfc:plant/trillium[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-8.4, 8, 250, 500, min_forest='normal', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'tropical_milkweed'), plant_config('tfc:plant/tropical_milkweed[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(8, 22.5, 120, 300, min_forest='sparse', max_forest='old_growth'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_orange'), plant_config('tfc:plant/tulip_orange[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(2.5, 9.8, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_pink'), plant_config('tfc:plant/tulip_pink[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-4.8, 2.5, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_red'), plant_config('tfc:plant/tulip_red[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(0.7, 4.3, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'tulip_white'), plant_config('tfc:plant/tulip_white[age=1,stage=1]', 1, 10, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, -3, 200, 400, min_forest='none', max_forest='edge'))
    configured_plant_patch_feature(rm, ('plant', 'turtle_grass'), plant_config('tfc:plant/turtle_grass[age=1,stage=1,fluid=empty]', 1, 15, 128, water_plant=True), decorate_chance(1), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 38, 240, 500))
    configured_plant_patch_feature(rm, ('plant', 'vriesea'), plant_config('tfc:plant/vriesea[age=1,stage=1,facing=north]', 6, 5, epiphyte_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(13.4, 38, 200, 400, min_forest='edge'))

    configured_plant_patch_feature(rm, ('plant', 'white_water_lily'), plant_config('tfc:plant/white_water_lily', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-12.5, 8, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'yellow_water_lily'), plant_config('tfc:plant/yellow_water_lily', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-2.5, 16, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'purple_water_lily'), plant_config('tfc:plant/purple_water_lily', 1, 7, 100), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(9.5, 38, 0, 500))
    configured_plant_patch_feature(rm, ('plant', 'water_taro'), plant_config('tfc:plant/water_taro[age=1,stage=1,fluid=empty,part=lower]', 1, 7, 100, emergent_plant=True), decorate_chance(2), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(11.6, 38, 260, 500))
    configured_plant_patch_feature(rm, ('plant', 'yucca'), plant_config('tfc:plant/yucca[age=1,stage=1]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-3, 20.7, 0, 75))

    configured_placed_feature(rm, ('plant', 'hanging_vines'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 21), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_climate(13.6, 29.6, 150, 470, True, fuzzy=True), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'hanging_vines_cave'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/hanging_vines_plant', 'tfc:plant/hanging_vines', 90, 10, 14, 22), decorate_carving_mask(30, 100), decorate_chance(0.003), decorate_climate(13.6, 29.6, 150, 470, True, fuzzy=True), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'spanish_moss'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/spanish_moss_plant', 'tfc:plant/spanish_moss', 90, 10, 14, 21), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_climate(5.6, 19.6, 400, 500, True, fuzzy=True), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'liana'), 'tfc:weeping_vines', tall_plant_config('tfc:plant/liana_plant', 'tfc:plant/liana', 40, 10, 8, 16), decorate_carving_mask(30, 100), decorate_chance(0.003), decorate_climate(13.6, 29.6, 150, 470, True, fuzzy=True))
    configured_placed_feature(rm, ('plant', 'tree_fern'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/tree_fern_plant', 'tfc:plant/tree_fern', 8, 7, 2, 6), decorate_heightmap('world_surface_wg'), decorate_chance(5), decorate_square(), decorate_climate(16.6, 47.6, 300, 500), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'arundo'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/arundo_plant', 'tfc:plant/arundo', 70, 7, 5, 8), decorate_heightmap('world_surface_wg'), decorate_chance(3), decorate_square(), decorate_climate(2.5, 19.6, 150, 400), decorate_near_water(radius=6), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'arundo_forest'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/arundo_plant', 'tfc:plant/arundo', 70, 7, 5, 8), decorate_heightmap('world_surface_wg'), decorate_count(2), decorate_square(), decorate_climate(6, 19.6, 220, 370, forest_types=['dead_bamboo', 'edge_bamboo', 'secondary_bamboo']), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'dry_phragmite'), 'tfc:twisting_vines', tall_plant_config('tfc:plant/dry_phragmite_plant', 'tfc:plant/dry_phragmite', 70, 7, 3, 5), decorate_range(62, 66), decorate_count(5), decorate_square(), decorate_climate(-7.4, 27.6, 100, 370, forest_types=['dead_bamboo', 'edge_bamboo', 'secondary_bamboo']), decorate_dry_replaceable())

    configured_placed_feature(rm, ('plant', 'winged_kelp'), 'tfc:kelp', tall_plant_config('tfc:plant/winged_kelp_plant', 'tfc:plant/winged_kelp', 64, 12, 7, 14), decorate_heightmap('ocean_floor_wg'), decorate_square(), decorate_chance(2), decorate_climate(-17.4, 12.6, 0, 450, fuzzy=True), decorate_air_or_empty_fluid())
    configured_placed_feature(rm, ('plant', 'leafy_kelp'), 'tfc:kelp', tall_plant_config('tfc:plant/leafy_kelp_plant', 'tfc:plant/leafy_kelp', 64, 12, 7, 14), decorate_heightmap('ocean_floor_wg'), decorate_square(), decorate_chance(2), decorate_climate(-22.4, 17.6, 0, 500, fuzzy=True), decorate_air_or_empty_fluid())

    configured_patch_feature(rm, ('plant', 'giant_kelp'), patch_config('tfc:plant/giant_kelp_flower[age=0,fluid=empty]', 2, 10, 6, water='salt', custom_feature='tfc:kelp_tree', custom_config={'block': 'tfc:plant/giant_kelp_flower'}), decorate_square(), decorate_climate(-20.4, 15.6, 0, 500, fuzzy=True))
    configured_patch_feature(rm, ('plant', 'saguaro'), patch_config('tfc:plant/saguaro[north=false,up=false,south=false,east=false,west=false,down=false]', 2, 10, 3, custom_feature='tfc:branching_cactus', custom_config={'block': 'tfc:plant/saguaro'}), decorate_chance(10), decorate_square(), decorate_climate(7.6, 27.6, 0, 100, fuzzy=True))

    configured_placed_feature(rm, ('plant', 'jungle_vines'), 'tfc:vines', {'state': utils.block_state('tfc:plant/jungle_vines[up=false,north=false,east=false,south=false,west=false]')}, decorate_count(127), decorate_square(), decorate_range(48, 110), decorate_replaceable(), decorate_climate(12.6, 29.6, 150, 470, True, fuzzy=True))
    configured_placed_feature(rm, ('plant', 'marsh_jungle_vines'), 'tfc:vines', {'state': utils.block_state('tfc:plant/jungle_vines[up=false,north=false,east=false,south=false,west=false]')}, decorate_count(127), decorate_square(), decorate_range(60, 80), decorate_replaceable(), decorate_climate(200, 500, 15.7, 28.2, True, fuzzy=True))

    # Grass-Type / Basic Plants

    configured_plant_patch_feature(rm, ('plant', 'beachgrass'), plant_config('tfc:plant/beachgrass[age=1,stage=1]', 1, 15, 28, limit_density=True), decorate_chance(3), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-10.3, 38, 190, 500))
    configured_plant_patch_feature(rm, ('plant', 'bluegrass'), plant_config('tfc:plant/bluegrass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-3, 11.6, 110, 280))
    configured_plant_patch_feature(rm, ('plant', 'bromegrass'), plant_config('tfc:plant/bromegrass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(4.3, 18.9, 140, 360))
    configured_plant_patch_feature(rm, ('plant', 'fountain_grass'), plant_config('tfc:plant/fountain_grass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(0.7, 24.4, 75, 150))
    configured_plant_patch_feature(rm, ('plant', 'manatee_grass'), plant_config('tfc:plant/manatee_grass[age=1,stage=1]', 1, 15, 64, water_plant=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(11.6, 38, 250, 500))
    configured_plant_patch_feature(rm, ('plant', 'orchard_grass'), plant_config('tfc:plant/orchard_grass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-33, 9.8, 75, 300))
    configured_plant_patch_feature(rm, ('plant', 'ryegrass'), plant_config('tfc:plant/ryegrass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-21.2, 38, 150, 320))
    configured_plant_patch_feature(rm, ('plant', 'scutch_grass'), plant_config('tfc:plant/scutch_grass[age=1,stage=1]', 1, 15, 64), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(0.7, 38, 150, 500))
    configured_plant_patch_feature(rm, ('plant', 'star_grass'), plant_config('tfc:plant/star_grass[age=1,stage=1]', 1, 15, 64, water_plant=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(2.5, 38, 50, 260))
    configured_plant_patch_feature(rm, ('plant', 'timothy_grass'), plant_config('tfc:plant/timothy_grass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-19.4, 15.3, 289, 500))
    configured_plant_patch_feature(rm, ('plant', 'raddia_grass'), plant_config('tfc:plant/raddia_grass[age=1,stage=1]', 1, 15, 64, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(17.1, 38, 330, 500))

    # Covers
    configured_placed_feature(rm, ('plant', 'moss_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/moss', 'height': 7, 'radius': 4, 'integrity': 0.7}, decorate_chance(10), decorate_climate(-12.4, 27.6, 250, 450), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'moss_cover'), 'tfc:creeping_plant', {'block': 'tfc:plant/moss', 'height': 7, 'radius': 4, 'integrity': 0.95}, decorate_climate(15.6, 32.6, 340, 500, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'morning_glory_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/morning_glory', 'height': 7, 'radius': 4, 'integrity': 0.7}, decorate_chance(10), decorate_climate(-16.4, 16.6, 300, 500), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'morning_glory_cover'), 'tfc:creeping_plant', {'block': 'tfc:plant/morning_glory', 'height': 7, 'radius': 4, 'integrity': 0.95}, decorate_climate(6.6, 10.6, 160, 230, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'philodendron_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/philodendron', 'height': 7, 'radius': 4, 'integrity': 0.7}, decorate_chance(10), decorate_climate(13.6, 27.6, 380, 500, min_forest='edge'), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'philodendron_cover'), 'tfc:creeping_plant', {'block': 'tfc:plant/philodendron', 'height': 7, 'radius': 4, 'integrity': 0.95}, decorate_climate(17.6, 27.6, 450, 500, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'reindeer_lichen_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/reindeer_lichen', 'height': 7, 'radius': 4, 'integrity': 0.7}, decorate_chance(10), decorate_climate(-33, -10.4, 50, 470), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'reindeer_lichen_cover'), 'tfc:creeping_plant', {'block': 'tfc:plant/reindeer_lichen', 'height': 7, 'radius': 4, 'integrity': 0.95}, decorate_climate(-22.4, -12.4, 220, 310, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'ivy_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/ivy', 'height': 7, 'radius': 4, 'integrity': 0.7}, decorate_chance(10), decorate_climate(-6.4, 11.6, 90, 450, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'ivy_cover'), 'tfc:creeping_plant', {'block': 'tfc:plant/ivy', 'height': 7, 'radius': 4, 'integrity': 0.95}, decorate_climate(-6.4, -2.4, 180, 250, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, ('plant', 'cobblestone_lichen_patch'), 'tfc:creeping_plant', {'block': 'tfc:plant/cobblestone_lichen', 'height': 5, 'radius': 5, 'integrity': 0.4}, decorate_chance(5), decorate_climate(-33, 17.6, 25, 450, True, fuzzy=True), decorate_square(), decorate_heightmap('world_surface_wg'))

    # Clay Indicator Plants
    # These piggyback on the clay disc feature, and so have limited decorators
    configured_plant_patch_feature(rm, ('plant', 'athyrium_fern'), plant_config('tfc:plant/athyrium_fern[age=1,stage=1]', 1, 6, 16, requires_clay=True), decorate_climate(-6.6, 13.4, 270, 500))
    configured_plant_patch_feature(rm, ('plant', 'canna'), plant_config('tfc:plant/canna[age=1,stage=1]', 1, 6, 16, requires_clay=True), decorate_climate(11.6, 38, 290, 500))
    configured_plant_patch_feature(rm, ('plant', 'goldenrod'), plant_config('tfc:plant/goldenrod[age=1,stage=1]', 1, 6, 16, requires_clay=True), decorate_climate(-15.7, -4.8, 75, 500))
    configured_plant_patch_feature(rm, ('plant', 'pampas_grass'), plant_config('tfc:plant/pampas_grass[age=1,stage=1,part=lower]', 1, 6, 16, requires_clay=True, tall_plant=True), decorate_climate(8, 38, 0, 300))
    configured_plant_patch_feature(rm, ('plant', 'perovskia'), plant_config('tfc:plant/perovskia[age=1,stage=1]', 1, 6, 16, requires_clay=True), decorate_climate(-8.4, 11.6, 0, 280))
    configured_plant_patch_feature(rm, ('plant', 'rose'), plant_config('tfc:plant/rose[age=1,stage=1,part=lower]', 1, 15, 10, True, tall_plant=True, limit_density=True), decorate_square(), decorate_heightmap('world_surface_wg'), decorate_climate(-7.4, 17.6, 150, 300))
    configured_noise_plant_feature(rm, ('plant', 'water_canna'), plant_config('tfc:plant/water_canna[age=1,stage=1]', 1, 6, 16, requires_clay=True), decorate_climate(11.6, 38, 150, 500))

    # Crops
    for crop, crop_data in CROPS.items():
        name_parts = ('plant', 'wild_crop', crop)
        name = 'tfc:wild_crop/%s' % crop
        heightmap: Heightmap = 'world_surface_wg'
        replaceable = decorate_replaceable()

        if crop_data.type == 'double' or crop_data.type == 'double_stick':
            feature = 'tfc:tall_wild_crop', {'block': name}
            name += '[part=bottom]'
        elif crop == 'rice':  # waterlogged
            feature = 'tfc:block_with_fluid', {'to_place': simple_state_provider(name)}
            heightmap = 'ocean_floor_wg'
            replaceable = decorate_shallow(1)
        elif crop_data.type == 'spreading':
            feature = 'tfc:spreading_crop', {'block': name}
        else:
            feature = 'simple_block', {'to_place': simple_state_provider(name)}

        res = utils.resource_location(rm.domain, name_parts)
        patch_feature = res.join() + '_patch'
        singular_feature = utils.resource_location(rm.domain, name_parts)

        rm.placed_feature_tag('feature/crops', patch_feature)

        rm.configured_feature(patch_feature, 'minecraft:random_patch', {'tries': 6, 'xz_spread': 5, 'y_spread': 1, 'feature': singular_feature.join()})
        rm.configured_feature(singular_feature, *feature)
        rm.placed_feature(patch_feature, patch_feature, decorate_chance(90), decorate_square(), decorate_climate(crop_data.min_temp, crop_data.max_temp, crop_data.min_water, crop_data.max_water, min_forest=crop_data.min_forest, max_forest=crop_data.max_forest))
        rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), replaceable, decorate_would_survive(name))

    for berry, info in BERRIES.items():
        if info.type == 'spreading':
            configured_placed_feature(rm, ('plant', berry + '_bush'), 'tfc:spreading_bush', {'block': 'tfc:plant/%s_bush' % berry}, decorate_climate(info.min_temp, info.max_temp, info.min_water, info.max_water, min_forest=info.min_forest, max_forest=info.max_forest), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_chance(22))
            rm.placed_feature_tag('feature/berry_bushes', 'tfc:plant/%s_bush' % berry)
        else:
            bush_block = 'tfc:plant/%s_bush[lifecycle=healthy,stage=0%s]' % (berry, ',fluid=empty' if info.type == 'waterlogged' else '')
            configured_patch_feature(rm, ('plant', berry + '_bush'), patch_config(bush_block, 1, 4, 4, 'fresh' if info.type == 'waterlogged' else False), decorate_climate(info.min_temp, info.max_temp, info.min_water, info.max_water, min_forest=info.min_forest, max_forest=info.max_forest), decorate_square(), decorate_chance(30), biome_check=False)
            rm.placed_feature_tag('feature/berry_bushes', 'tfc:plant/%s_bush_patch' % berry)

    for fruit, info in FRUITS.items():
        config = {
            'min_temperature': info.min_temp,
            'max_temperature': info.max_temp,
            'min_groundwater': info.min_water,
            'max_groundwater': info.max_water,
            'max_forest': 3
        }
        feature = 'tfc:fruit_trees'
        state = 'tfc:plant/%s_growing_branch' % fruit
        if fruit == 'banana':
            feature = 'tfc:bananas'
            state = 'tfc:plant/banana_plant'
        configured_placed_feature(rm, ('plant', fruit), feature, {'state': state}, ('tfc:climate', config), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_chance(50))

        rm.placed_feature_tag('feature/fruit_trees', 'tfc:plant/%s' % fruit, 'tfc:plant/%s' % fruit)

    configured_placed_feature(rm, 'rare_bamboo', 'tfc:bamboo', {'probability': 0.2, 'state': 'minecraft:bamboo'}, decorate_chance(30), decorate_climate(21, 40, 300, 500, True, fuzzy=True), ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, 'bamboo', 'tfc:bamboo', {'probability': 0.25, 'state': 'minecraft:bamboo'}, decorate_count(6), decorate_climate(21, 40, 320, 500, fuzzy=True, forest_types=['dead_bamboo', 'edge_bamboo', 'secondary_bamboo']), ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, 'rare_bamboo_golden', 'tfc:bamboo', {'probability': 0.2, 'state': 'tfc:plant/golden_bamboo'}, decorate_chance(30), decorate_climate(11, 21.5, 300, 500, True, fuzzy=True), ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), decorate_square(), decorate_heightmap('world_surface_wg'))
    configured_placed_feature(rm, 'bamboo_golden', 'tfc:bamboo', {'probability': 0.25, 'state': 'tfc:plant/golden_bamboo'}, decorate_count(6), decorate_climate(14, 21.5, 320, 500, fuzzy=True, forest_types=['dead_bamboo', 'edge_bamboo', 'secondary_bamboo']), ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 160,
        'noise_factor': 80.0,
        'noise_offset': 0.3
    }), decorate_square(), decorate_heightmap('world_surface_wg'))

    for coral in ('tree', 'mushroom', 'claw'):
        configured_placed_feature(rm, 'coral_%s' % coral, 'tfc:coral_%s' % coral, {})
        rm.placed_feature_tag('feature/corals', 'tfc:coral_%s' % coral)

    configured_placed_feature(rm, 'coral_reef', 'minecraft:simple_random_selector', {
        'features': '#tfc:feature/corals'
    }, ('minecraft:noise_based_count', {
        'noise_to_count_ratio': 20,
        'noise_factor': 200,
        'noise_offset': 1
    }), decorate_square(), decorate_climate(min_temp=12, max_temp=50, fuzzy=True), decorate_heightmap('ocean_floor_wg'))

    configured_placed_feature(rm, 'tide_pool', 'tfc:tide_pool', {}, decorate_chance(5), decorate_count(10), decorate_square(), decorate_heightmap('ocean_floor_wg'), decorate_biome())
    rm.placed_feature('big_tide_pool', 'tfc:tide_pool', decorate_chance(15), decorate_count(40), decorate_square(), decorate_heightmap('ocean_floor_wg'), decorate_biome())

    # Groundcover
    configured_patch_feature(rm, 'driftwood', patch_config('tfc:groundcover/driftwood[fluid=empty]', 1, 15, 5, True), decorate_chance(6), decorate_square(), decorate_climate(-10, 50, 200, 500))
    configured_patch_feature(rm, 'clam', patch_config('tfc:groundcover/clam[fluid=empty]', 1, 15, 5, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(-50, 22, 10, 450))
    configured_patch_feature(rm, 'mollusk', patch_config('tfc:groundcover/mollusk[fluid=empty]', 1, 15, 5, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(-10, 30, 150, 500))
    configured_patch_feature(rm, 'mussel', patch_config('tfc:groundcover/mussel[fluid=empty]', 1, 15, 5, 'salt'), decorate_chance(6), decorate_square(), decorate_climate(10, 50, 100, 500))

    configured_patch_feature(rm, 'sticks_shore', patch_config('tfc:groundcover/stick[fluid=empty]', 1, 15, 18, True), decorate_chance(2), decorate_square(), decorate_climate(-50, 50, 50, 500))
    configured_patch_feature(rm, 'seaweed', patch_config('tfc:groundcover/seaweed[fluid=empty]', 1, 15, 8, True), decorate_chance(5), decorate_square(), decorate_climate(-20, 50, 150, 500))
    configured_patch_feature(rm, 'guano', patch_config('tfc:groundcover/guano[fluid=empty]', 1, 10, 10), decorate_chance(15), decorate_square(), decorate_climate(-30, 10, 100, 500), decorate_on_top_of('tfc:creeping_stone_plantable_on'))

    # Forest Only
    configured_patch_feature(rm, 'sticks_forest', patch_config('tfc:groundcover/stick[fluid=empty]', 1, 15, 20), decorate_chance(3), decorate_square(), decorate_climate(-20, 50, 70, 500, True), biome_check=False)
    configured_patch_feature(rm, 'pinecone', patch_config('tfc:groundcover/pinecone[fluid=empty]', 1, 15, 10), decorate_chance(5), decorate_square(), decorate_climate(-14, 0, 60, 320, True), biome_check=False)
    configured_patch_feature(rm, 'humus', patch_config('tfc:groundcover/humus[fluid=empty]', 1, 5, 100), decorate_chance(5), decorate_square(), decorate_climate(8, 20, 180, 420, True, fuzzy=True), biome_check=False)
    configured_patch_feature(rm, 'salt_lick', patch_config('tfc:groundcover/salt_lick[fluid=empty]', 1, 5, 100), decorate_chance(110), decorate_square(), decorate_climate(5, 33, 100, 500, True), biome_check=False)
    configured_patch_feature(rm, 'dead_grass', patch_config('tfc:groundcover/dead_grass[fluid=empty]', 1, 5, 100), decorate_chance(70), decorate_square(), decorate_climate(10, 20, 0, 150, True, fuzzy=True), biome_check=False)
    configured_patch_feature(rm, 'rotten_flesh', patch_config('tfc:groundcover/rotten_flesh[fluid=empty]', 1, 10, 10), decorate_chance(100), decorate_square(), decorate_climate(-30, 30, 0, 400), biome_check=False)
    configured_patch_feature(rm, 'pumice', patch_config('tfc:groundcover/pumice[fluid=empty]', 1, 10, 10), decorate_chance(3), decorate_square(), ('tfc:volcano', {'distance': 0.8}), biome_check=False)

    # Loose Rocks - Both Surface + Underground
    configured_placed_feature(rm, 'loose_rock', 'tfc:loose_rock', {}, decorate_heightmap('ocean_floor_wg'))
    configured_placed_feature(rm, 'surface_loose_rocks', 'minecraft:random_patch', {'tries': 8, 'xz_spread': 7, 'y_spread': 1, 'feature': 'tfc:loose_rock'}, decorate_square())

    # Underground decoration
    configured_placed_feature(rm, 'underground_loose_rocks', 'tfc:loose_rock', decorate_carving_mask(), decorate_chance(0.05), decorate_count(25), decorate_range(-32, 59), decorate_scanner('down', 12), decorate_random_offset(0, 1))
    configured_patch_feature(rm, 'underground_guano', patch_config('tfc:groundcover/guano[fluid=empty]', 5, 5, 60), decorate_chance(40), decorate_count(25), decorate_range(-32, 32), decorate_scanner('down', 12), decorate_random_offset(0, 1), extra_singular_decorators=[decorate_underground()])
    rm.configured_feature('geode', 'tfc:geode', {'outer': 'tfc:rock/hardened/basalt', 'middle': 'tfc:rock/raw/quartzite', 'inner': [
        {'data': 'tfc:ore/amethyst/quartzite', 'weight': 1}, {'data': 'tfc:rock/raw/quartzite', 'weight': 5}
    ]})
    rm.placed_feature('geode', 'tfc:geode', decorate_chance(500), decorate_square(), decorate_range(-48, 32), decorate_biome())

    rm.biome_tag('has_predictable_winds', '#tfc:is_ocean', 'tfc:shore', 'tfc:tidal_flats')


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
    epiphyte_plant: bool
    limit_density: bool
    no_solid_neighbors: bool
    tall_water_plant: bool


def plant_config(block: str, y_spread: int, xz_spread: int, tries: int = None, requires_clay: bool = False, water_plant: bool = False, emergent_plant: bool = False, tall_plant: bool = False, epiphyte_plant: bool = False, limit_density: bool = False, no_solid_neighbors: bool = False, tall_water_plant: bool = False) -> PlantConfig:
    return PlantConfig(block, y_spread, xz_spread, tries, requires_clay, water_plant, emergent_plant, tall_plant, epiphyte_plant, limit_density, no_solid_neighbors, tall_water_plant)


def configured_plant_patch_feature(rm: ResourceManager, name_parts: ResourceIdentifier, config: PlantConfig, *patch_decorators: Json):
    state_provider = {
        'type': 'tfc:random_property',
        'state': utils.block_state(config.block), 'property': 'age'
    }
    feature = 'simple_block', {'to_place': state_provider}
    heightmap: Heightmap = 'world_surface_wg'
    would_survive = decorate_would_survive(config.block)

    if config.water_plant or config.emergent_plant or config.tall_water_plant:
        heightmap = 'ocean_floor_wg'
        would_survive = decorate_would_survive_with_fluid(config.block)

    if config.water_plant:
        feature = 'tfc:block_with_fluid', feature[1]
    if config.emergent_plant:
        feature = 'tfc:emergent_plant', {'block': utils.block_state(config.block)['Name']}
    if config.tall_plant:
        feature = 'tfc:tall_plant', {'block': utils.block_state(config.block)['Name']}
    if config.epiphyte_plant:
        feature = 'tfc:epiphyte_plant', {'block': utils.block_state(config.block)['Name']}
    if config.tall_water_plant:
        feature = 'tfc:submerged_tall_plant', {'block': utils.block_state(config.block)['Name']}

    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)
    predicate = decorate_air_or_empty_fluid() if not config.requires_clay else decorate_replaceable()

    rm.configured_feature(patch_feature, 'minecraft:random_patch' if not config.limit_density else 'tfc:dynamic_random_patch', {
        'tries': config.tries,
        'xz_spread': config.xz_spread,
        'y_spread': config.y_spread,
        'feature': singular_feature.join()
    })
    rm.configured_feature(singular_feature, *feature)
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators)
    if config.no_solid_neighbors:
        rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), predicate, would_survive, decorate_no_solid_neighbors())
    else:
        rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), predicate, would_survive)

class PatchConfig(NamedTuple):
    block: str
    y_spread: int
    xz_spread: int
    tries: int
    any_water: bool
    salt_water: bool
    fresh_water: bool
    custom_feature: str
    custom_config: Json


def patch_config(block: str, y_spread: int, xz_spread: int, tries: int = 64, water: Union[bool, Literal['salt']] = False, custom_feature: Optional[str] = None, custom_config: Json = None) -> PatchConfig:
    return PatchConfig(block, y_spread, xz_spread, tries, (isinstance(water, bool) and water) or isinstance(water, str), water == 'salt', water == 'fresh', custom_feature, custom_config)

def configured_patch_feature(rm: ResourceManager, name_parts: ResourceIdentifier, patch: PatchConfig, *patch_decorators: Json, extra_singular_decorators: Optional[List[Json]] = None, biome_check: bool = True):
    feature = 'minecraft:simple_block'
    config = {'to_place': {'type': 'minecraft:simple_state_provider', 'state': utils.block_state(patch.block)}}
    singular_decorators = []

    if patch.any_water:
        feature = 'tfc:block_with_fluid'
        if patch.salt_water:
            singular_decorators.append(decorate_matching_blocks('tfc:fluid/salt_water'))
        elif patch.fresh_water:
            singular_decorators.append(decorate_matching_blocks('minecraft:water'))
        else:
            singular_decorators.append(decorate_air_or_empty_fluid())
    else:
        singular_decorators.append(decorate_replaceable())

    if patch.custom_feature is not None:
        assert patch.custom_config
        feature = patch.custom_feature
        config = patch.custom_config

    heightmap: Heightmap = 'world_surface_wg'
    if patch.any_water:
        heightmap = 'ocean_floor_wg'
        singular_decorators.append(decorate_would_survive_with_fluid(patch.block))
    else:
        singular_decorators.append(decorate_would_survive(patch.block))

    if extra_singular_decorators is not None:
        singular_decorators += extra_singular_decorators
    if biome_check:
        patch_decorators = [*patch_decorators, decorate_biome()]

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
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators)
    rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), *singular_decorators)


def configured_noise_plant_feature(rm: ResourceManager, name_parts: ResourceIdentifier, config: PlantConfig, *patch_decorators: Json, water: bool = True, water_depth: int = 5, min_water_depth: int = None):
    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)
    placed_decorators = [decorate_heightmap('world_surface_wg'), decorate_air_or_empty_fluid(), decorate_would_survive(config.block)]
    if water:
        placed_decorators.append(decorate_shallow(water_depth, min_water_depth))

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
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators)
    rm.placed_feature(singular_feature, singular_feature, *placed_decorators)


def normal_noise(first_octave: int, amplitude: float):
    return {'firstOctave': first_octave, 'amplitudes': [amplitude]}


def simple_state_provider(name: str) -> Dict[str, Any]:
    return {'type': 'minecraft:simple_state_provider', 'state': utils.block_state(name)}


# Vein Helper Functions

def vein_ore_blocks(vein: Vein, rock: str) -> List[Dict[str, Any]]:
    poor, normal, rich = vein.grade
    ore_blocks = [{
        'weight': poor,
        'block': 'tfc:ore/poor_%s/%s' % (vein.ore, rock)
    }, {
        'weight': normal,
        'block': 'tfc:ore/normal_%s/%s' % (vein.ore, rock)
    }, {
        'weight': rich,
        'block': 'tfc:ore/rich_%s/%s' % (vein.ore, rock)
    }]
    if vein.deposits:
        ore_blocks.append({
            'weight': 10,
            'block': 'tfc:deposit/%s/%s' % (vein.ore, rock)
        })
    return ore_blocks


def mineral_ore_blocks(vein: Vein, rock: str) -> List[Dict[str, Any]]:
    return [{'block': 'tfc:ore/%s/%s' % (vein.ore, rock)}]


def vein_density(density: int) -> float:
    assert 0 <= density <= 100, 'Invalid density: %s' % str(density)
    return round(density * 0.01, 2)


# Tree Helper Functions

def forest_config(rm: ResourceManager, min_water: float, max_water: float, min_temp: float, max_temp: float, tree: str, old_growth: bool, old_growth_chance: int = None, spoiler_chance: int = None, krum: bool = False, floating: bool = None):
    cfg = {
        'climate': {
            'min_temperature': min_temp,
            'max_temperature': max_temp,
            'min_groundwater': min_water,
            'max_groundwater': max_water
        },
        'groundcover': [{'block': 'tfc:wood/twig/%s' % tree}],
        'normal_tree': 'tfc:tree/%s' % tree,
        'dead_tree': 'tfc:tree/%s_dead' % tree,
        'krummholz': None if not krum else 'tfc:tree/%s_krummholz' % tree,
        'old_growth_chance': old_growth_chance,
        'spoiler_old_growth_chance': spoiler_chance,
        'floating': floating,
    }
    if tree != 'palm':
        cfg['groundcover'] += [{'block': 'tfc:wood/fallen_leaves/%s' % tree}]
    if tree == 'pine':
        cfg['groundcover'] += [{'block': 'tfc:groundcover/pinecone'}]
    if tree not in ('acacia', 'willow'):
        cfg['fallen_log'] = 'tfc:wood/log/%s' % tree
        cfg['fallen_leaves'] = 'tfc:wood/fallen_leaves/%s' % tree
    else:
        cfg['fallen_tree_chance'] = 0
    if tree not in ('palm', 'rosewood', 'sycamore'):
        cfg['bush_log'] = utils.block_state('tfc:wood/wood/%s[branch_direction=down,axis=y]' % tree)
        cfg['bush_leaves'] = 'tfc:wood/leaves/%s' % tree
    if old_growth:
        cfg['old_growth_tree'] = 'tfc:tree/%s_large' % tree
    rm.configured_feature('tree/%s_entry' % tree, 'tfc:forest_entry', cfg)
    cfg['dead_chance'] = 1
    cfg['fallen_tree_chance'] = 8
    cfg['floating'] = None
    rm.configured_feature('tree/dead_%s_entry' % tree, 'tfc:forest_entry', cfg)


def overlay_config(tree: str, min_height: int, max_height: int, width: int = 1, radius: int = 1, suffix: str = '', place=None, roots=None):
    block = 'tfc:wood/log/%s[axis=y,branch_direction=none]' % tree
    tree += suffix
    return {
        'base': 'tfc:%s/base' % tree,
        'overlay': 'tfc:%s/overlay' % tree,
        'trunk': trunk_config(block, min_height, max_height, width),
        'radius': radius,
        'placement': place,
        'root_system': roots
    }


def random_config(tree: str, structure_count: int, radius: int = 1, suffix: str = '', trunk: List = None, place=None, roots=None):
    block = 'tfc:wood/log/%s[axis=y,branch_direction=none]' % tree
    tree += suffix
    cfg = {
        'structures': ['tfc:%s/%d' % (tree, i) for i in range(1, 1 + structure_count)],
        'radius': radius,
        'placement': place,
        'root_system': roots
    }
    if trunk is not None:
        cfg['trunk'] = trunk_config(block, *trunk)
    return cfg


def stacked_config(tree: str, min_height: int, max_height: int, width: int, layers: List[Tuple[int, int, int]], radius: int = 1, suffix: str = '', place: Json = None, roots=None) -> JsonObject:
    # layers consists of each layer, which is a (min_count, max_count, total_templates)
    block = 'tfc:wood/log/%s[axis=y,branch_direction=none]' % tree
    tree += suffix
    return {
        'trunk': trunk_config(block, min_height, max_height, width),
        'layers': [{
            'templates': ['tfc:%s/layer%d_%d' % (tree, 1 + i, j) for j in range(1, 1 + layer[2])],
            'min_count': layer[0],
            'max_count': layer[1]
        } for i, layer in enumerate(layers)],
        'radius': radius,
        'placement': place,
        'root_system': roots
    }


def trunk_config(block: str, min_height: int, max_height: int, width: int) -> JsonObject:
    assert width == 1 or width == 2
    return {
        'state': utils.block_state(block),
        'min_height': min_height,
        'max_height': max_height,
        'wide': width == 2,
    }

def root_config(width: int, height: int, tries: int, mangrove: bool = False) -> JsonObject:
    blocks = [{
        'replace': ['tfc:%s/%s' % (variant, soil)],
        'with': [{'block': 'tfc:rooted_dirt/%s' % soil}]
    } for soil in SOIL_BLOCK_VARIANTS for variant in ('grass', 'dirt')]
    blocks += [{
        'replace': ['tfc:mud/%s' % soil],
        'with': [{'block': 'tfc:muddy_roots/%s' % soil}]
    } for soil in SOIL_BLOCK_VARIANTS]
    cfg = {
        'blocks': blocks,
        'width': width,
        'height': height,
        'tries': tries
    }
    if mangrove:
        cfg['special_placer'] = {
            'skew_chance': 0.2
        }
        cfg['required'] = True
    return cfg


def tree_placement_config(width: int, height: int, ground_type: str = None) -> JsonObject:
    return {
        'width': width,
        'height': height,
        'ground_type': ground_type
    }


Heightmap = Literal['motion_blocking', 'motion_blocking_no_leaves', 'ocean_floor', 'ocean_floor_wg', 'world_surface', 'world_surface_wg']
HeightProviderType = Literal['constant', 'uniform', 'biased_to_bottom', 'very_biased_to_bottom', 'trapezoid', 'weighted_list']


# Decorators / Placements

def decorate_square() -> Json:
    return 'minecraft:in_square'


def decorate_biome() -> Json:
    return 'tfc:biome'


def decorate_chance(rarity_or_probability: Union[int, float]) -> Json:
    return {'type': 'minecraft:rarity_filter', 'chance': round(1 / rarity_or_probability) if isinstance(rarity_or_probability, float) else rarity_or_probability}


def decorate_count(count: int) -> Json:
    return {'type': 'minecraft:count', 'count': count}


def decorate_shallow(max_depth: int = 5, min_depth: int = None) -> Json:
    return {'type': 'tfc:shallow_water', 'max_depth': max_depth, 'min_depth': min_depth}

def decorate_flat_enough(flatness: float = None, radius: int = None, max_depth: int = None):
    return {'type': 'tfc:flat_enough', 'flatness': flatness, 'radius': radius, 'max_depth': max_depth}

def decorate_underground() -> Json:
    return 'tfc:underground'

def decorate_heightmap(heightmap: Heightmap) -> Json:
    assert heightmap in get_args(Heightmap)
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


def decorate_climate(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_water: Optional[float] = None, max_water: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None, min_forest: Optional[str] = None, max_forest: Optional[str] = None, forest_types: Optional[List[str]] = None) -> Json:
    minf = None
    if min_forest == 'sparse':
        minf = 1
    elif min_forest == 'edge':
        minf = 2
    elif min_forest == 'normal':
        minf = 3
    elif min_forest == 'old_growth':
        minf = 4
    maxf = None
    if max_forest == 'sparse':
        maxf = 1
    elif max_forest == 'edge':
        maxf = 2
    elif max_forest == 'normal':
        maxf = 3
    elif max_forest == 'old_growth':
        maxf = 4

    return {
        'type': 'tfc:climate',
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_groundwater': min_water,
        'max_groundwater': max_water,
        'min_forest': 3 if needs_forest else minf,
        'max_forest': maxf,
        'forest_types': forest_types,
        'fuzzy': fuzzy
    }

def decorate_no_solid_neighbors() -> Json:
    return 'tfc:no_solid_neighbors'

def decorate_scanner(direction: str, max_steps: int) -> Json:
    return {
        'type': 'minecraft:environment_scan',
        'max_steps': max_steps,
        'direction_of_search': direction,
        'target_condition': {'type': 'minecraft:solid'},
        'allowed_search_condition': {'type': 'minecraft:matching_blocks', 'blocks': ['minecraft:air']}
    }

def decorate_on_top_of(tag: str) -> Json:
    return {
        'type': 'tfc:on_top',
        'predicate': {
            'type': 'minecraft:matching_block_tag',
            'tag': tag
        }
    }

def decorate_near_water(radius: int = None, salt_water: bool = False, fresh_water: bool = False) -> Json:
    fluids = ['tfc:salt_water', 'minecraft:water', 'tfc:spring_water']
    if salt_water:
        fluids = ['tfc:salt_water']
    if fresh_water:
        fluids = ['minecraft:water']
    return {
        'type': 'tfc:near_fluid',
        'fluids': fluids,
        'radius': radius
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

def decorate_replaceable() -> Json:
    return decorate_block_predicate({'type': 'tfc:replaceable'})

def decorate_dry_replaceable() -> Json:
    return decorate_block_predicate({'type': 'tfc:dry_replaceable'})

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
        'min_inclusive': min_inclusive,
        'max_exclusive': max_exclusive
    }


def uniform_int(min_inclusive: int, max_inclusive: int) -> Dict[str, Any]:
    return {
        'type': 'uniform',
        'min_inclusive': min_inclusive,
        'max_inclusive': max_inclusive
    }


def trapezoid_float(min_value: float, max_value: float, plateau: float) -> Dict[str, Any]:
    return {
        'type': 'trapezoid',
        'min': min_value,
        'max': max_value,
        'plateau': plateau
    }


def height_provider(min_y: VerticalAnchor, max_y: VerticalAnchor, height_type: HeightProviderType = 'uniform') -> Dict[str, Any]:
    assert height_type in get_args(HeightProviderType)
    return {
        'type': height_type,
        'min_inclusive': utils.as_vertical_anchor(min_y),
        'max_inclusive': utils.as_vertical_anchor(max_y)
    }


def spawner(entity: str, weight: int = 1, min_count: int = 1, max_count: int = 4) -> dict[str, Any]:
    return {
        'type': entity,
        'weight': weight,
        'minCount': min_count,
        'maxCount': max_count
    }

SALT_MARSH_AMBIENT: Dict[str, Dict[str, Any]] = {
    'isopod': spawner('tfc:isopod'),
    'lobster': spawner('tfc:lobster'),
    'horseshoe_crab': spawner('tfc:horseshoe_crab'),
    'salmon': spawner('tfc:salmon')
}

OCEAN_AMBIENT: Dict[str, Dict[str, Any]] = {
    'isopod': spawner('tfc:isopod'),
    'lobster': spawner('tfc:lobster'),
    'horseshoe_crab': spawner('tfc:horseshoe_crab'),
    'cod': spawner('tfc:cod', weight=10),
    'pufferfish': spawner('tfc:pufferfish', max_count=2),
    'tropical_fish': spawner('tfc:tropical_fish', weight=10, max_count=6),
    'jellyfish': spawner('tfc:jellyfish', min_count=2, max_count=6)
}

OCEAN_CREATURES: Dict[str, Dict[str, Any]] = {
    'orca': spawner('tfc:orca', min_count=1, max_count=3),
    'dolphin': spawner('tfc:dolphin', min_count=1, max_count=3),
    'squid': spawner('tfc:squid', min_count=1, max_count=3, weight=2)
}

UNDERGROUND_WATER_CREATURES: Dict[str, Dict[str, Any]] = {
    'octopoteuthis': spawner('tfc:octopoteuthis', min_count=1, max_count=2)
}

LAKE_AMBIENT: Dict[str, Dict[str, Any]] = {
    **dict(('%s' % fish, spawner('tfc:%s' % fish, min_count=2, max_count=4, weight=10)) for fish in SIMPLE_FRESHWATER_FISH if 'trout' not in fish),
    'crayfish': spawner('tfc:crayfish', min_count=1, max_count=4, weight=5)
}

RIVER_AMBIENT: Dict[str, Dict[str, Any]] = {
    **dict(('%s' % fish, spawner('tfc:%s' % fish, min_count=2, max_count=4, weight=10)) for fish in SIMPLE_FRESHWATER_FISH if 'trout' in fish),
}

LAKE_CREATURES: Dict[str, Dict[str, Any]] = {
    'manatee': spawner('tfc:manatee', min_count=1, max_count=2)
}

SHORE_CREATURES: Dict[str, Dict[str, Any]] = {
    'penguin': spawner('tfc:penguin', min_count=2, max_count=5, weight=10),
    'turtle': spawner('tfc:turtle', min_count=2, max_count=5, weight=10)
}

LAND_CREATURES: Dict[str, Dict[str, Any]] = {
    'crocodile': spawner('tfc:crocodile', min_count=1, max_count=1, weight=20),
    'pig': spawner('tfc:pig', min_count=1, max_count=4),
    'cow': spawner('tfc:cow', min_count=1, max_count=4),
    'goat': spawner('tfc:goat', min_count=1, max_count=4),
    'yak': spawner('tfc:yak', min_count=1, max_count=4),
    'alpaca': spawner('tfc:alpaca', min_count=1, max_count=4),
    'sheep': spawner('tfc:sheep', min_count=1, max_count=4),
    'musk_ox': spawner('tfc:musk_ox', min_count=1, max_count=4),
    'chicken': spawner('tfc:chicken', min_count=2, max_count=6),
    'duck': spawner('tfc:duck', min_count=2, max_count=6),
    'quail': spawner('tfc:quail', min_count=2, max_count=6),
    'polar_bear': spawner('tfc:polar_bear', min_count=1, max_count=1, weight=2),
    'grizzly_bear': spawner('tfc:grizzly_bear', min_count=1, max_count=1, weight=2),
    'black_bear': spawner('tfc:black_bear', min_count=1, max_count=1, weight=2),
    'lion': spawner('tfc:lion', min_count=1, max_count=3, weight=2),
    'sabertooth': spawner('tfc:sabertooth', min_count=1, max_count=1, weight=2),
    'tiger': spawner('tfc:tiger', min_count=1, max_count=1, weight=2),
    'rabbit': spawner('tfc:rabbit', min_count=1, max_count=4, weight=3),
    'fox': spawner('tfc:fox', min_count=1, max_count=1),
    'panda': spawner('tfc:panda', min_count=3, max_count=5),
    'boar': spawner('tfc:boar', min_count=1, max_count=2, weight=2),
    'wildebeest': spawner('tfc:wildebeest', min_count=1, max_count=2, weight=2),
    'moose': spawner('tfc:moose', min_count=1, max_count=1),
    'bongo': spawner('tfc:bongo', min_count=2, max_count=4, weight=3),
    'caribou': spawner('tfc:caribou', min_count=2, max_count=4, weight=3),
    'deer': spawner('tfc:deer', min_count=2, max_count=4, weight=3),
    'gazelle': spawner('tfc:gazelle', min_count=2, max_count=4, weight=3),
    'grouse': spawner('tfc:grouse', min_count=2, max_count=4),
    'pheasant': spawner('tfc:pheasant', min_count=2, max_count=4),
    'turkey': spawner('tfc:turkey', min_count=2, max_count=4),
    'peafowl': spawner('tfc:peafowl', min_count=2, max_count=4),
    'wolf': spawner('tfc:wolf', min_count=6, max_count=9),
    'hyena': spawner('tfc:hyena', min_count=5, max_count=9),
    'direwolf': spawner('tfc:direwolf', min_count=3, max_count=7),
    'donkey': spawner('tfc:donkey', min_count=1, max_count=3),
    'horse': spawner('tfc:horse', min_count=1, max_count=3),
    'ocelot': spawner('tfc:ocelot', min_count=1, max_count=3),
    'frog': spawner('tfc:frog', min_count=2, max_count=4),
}

VANILLA_MONSTERS: Dict[str, Dict[str, Any]] = {
    'spider': spawner('minecraft:spider', weight=100, min_count=4, max_count=4),
    'zombie': spawner('minecraft:zombie', weight=95, min_count=4, max_count=4),
    'skeleton': spawner('minecraft:skeleton', weight=100, min_count=4, max_count=4),
    'creeper': spawner('minecraft:creeper', weight=100, min_count=4, max_count=4),
    'slime': spawner('minecraft:slime', weight=100, min_count=4, max_count=4),
}


def biome(rm: ResourceManager, name: str, category: str, boulders: bool = False, ocean_features: Union[bool, Literal['both']] = False, lake_features: Union[bool, Literal['default']] = 'default', volcano_features: bool = False, reef_features: bool = False, barren: bool = False, hot_spring_features: Union[bool, Literal['empty']] = False):
    spawners = {}
    soil_discs = []
    large_features = []
    surface_decorations = []
    costs = {}

    if ocean_features == 'both':  # Both applies both ocean + land features. True or false applies only one
        land_features = True
        ocean_features = True
    else:
        land_features = not ocean_features
    if lake_features == 'default':  # Default = Lakes are on all non-ocean biomes. True/False to force either way
        lake_features = not ocean_features

    if boulders:
        large_features.append('#tfc:feature/boulders')

    # Oceans
    if ocean_features:
        large_features.append('#tfc:feature/icebergs')
        if name != 'tidal_flats':
            surface_decorations.append('#tfc:feature/ocean_plants')
        else:
            surface_decorations.append('tfc:sea_stacks_patch')
        if name == 'shore':
            surface_decorations.append('tfc:plant/beachgrass_patch')
            surface_decorations.append('tfc:plant/sea_palm_patch')

        if category == 'beach':
            surface_decorations.append('#tfc:feature/shore_decorations')
            spawners['creature'] = [entity for entity in SHORE_CREATURES.values()]
        else:
            surface_decorations.append('#tfc:feature/ocean_decorations')

        spawners['water_ambient'] = [entity for entity in OCEAN_AMBIENT.values()]
        spawners['water_creature'] = [entity for entity in OCEAN_CREATURES.values()]
        spawners['underground_water_creature'] = [entity for entity in UNDERGROUND_WATER_CREATURES.values()]
        costs['tfc:octopoteuthis'] = {'energy_budget': 0.12, 'charge': 1.0}

    if category in ('river', 'lake'):
        soil_discs.append('#tfc:feature/ore_deposits')
    if category in ('lake', 'swamp', 'river'):
        surface_decorations.append('tfc:plant/dry_phragmite')
    if category == 'river':
        spawners['water_ambient'] = [entity for entity in RIVER_AMBIENT.values()]

    if name == 'deep_ocean_trench':
        large_features.append('tfc:lava_hot_spring')

    if 'lake' in name:
        spawners['water_ambient'] = [entity for entity in LAKE_AMBIENT.values()]
        spawners['water_creature'] = [entity for entity in LAKE_CREATURES.values()]
    if 'swamp' == category:
        spawners['water_ambient'] = [entity for entity in LAKE_AMBIENT.values()]
    if 'salt_marsh' == name:
        spawners['water_ambient'] = [entity for entity in SALT_MARSH_AMBIENT.values()]
    spawners['monster'] = [entity for entity in VANILLA_MONSTERS.values()]

    if reef_features:
        large_features.append('tfc:coral_reef')

    # Continental / Land Features
    # Exclude these features from salt flat biomes
    if land_features and not barren:
        soil_discs.append('#tfc:feature/soil_discs')
        if 'salt_marsh' not in name:
            large_features += ['tfc:forest']
        else:
            large_features += ['tfc:mangrove_forest']
            surface_decorations += ['tfc:plant/marsh_jungle_vines']
        if 'lowlands' in name:
            large_features += ['tfc:dead_forest']
        large_features += ['tfc:rare_bamboo', 'tfc:bamboo', 'tfc:rare_bamboo_golden', 'tfc:bamboo_golden', 'tfc:cave_vegetation']
        surface_decorations.append('#tfc:feature/land_plants')
        spawners['creature'] = [entity for entity in LAND_CREATURES.values()]

    if volcano_features:
        large_features.append('#tfc:feature/volcanoes')

    if hot_spring_features:  # can be True, 'empty'
        if hot_spring_features == 'empty':
            large_features.append('tfc:random_empty_hot_spring')
        else:
            large_features.append('tfc:random_active_hot_spring')

    # Feature Tags
    # We don't directly use vanilla's generation step, but we line this up *approximately* with it, so that mods that add features add them in roughly the right location
    feature_tags = [
        '#tfc:in_biome/erosion',  # Raw Generation
        '#tfc:in_biome/all_lakes' if lake_features else '#tfc:in_biome/underground_lakes',  # Lakes
        '#tfc:in_biome/soil_discs/%s' % name,  # Local Modifications
        '#tfc:in_biome/underground_structures',  # Underground Structures
        '#tfc:in_biome/surface_structures',  # Surface Structures
        '#tfc:in_biome/strongholds',  # Strongholds
        '#tfc:in_biome/veins',  # Underground Ores
        '#tfc:in_biome/underground_decoration',  # Underground Decoration
        '#tfc:in_biome/large_features/%s' % name,  # Fluid Springs (we co-opt this as they likely won't interfere and it's in the right order)
        '#tfc:in_biome/surface_decoration/%s' % name,  # Vegetal Decoration
        '#tfc:in_biome/top_layer_modification'  # Top Layer Modification
    ]

    rm.placed_feature_tag(('in_biome/soil_discs', name), *soil_discs)
    rm.placed_feature_tag(('in_biome/large_features', name), *large_features)
    rm.placed_feature_tag(('in_biome/surface_decoration', name), *surface_decorations)

    if volcano_features:
        rm.biome_tag('is_volcanic', name)
    if 'lake' in name:
        rm.biome_tag('is_lake', name)
    if 'river' in name:
        rm.biome_tag('is_river', name)
    if 'ocean' in name and 'mountain' not in name:
        rm.biome_tag('is_ocean', name)

    rm.lang('biome.tfc.%s' % name, lang(name))
    rm.biome(
        name_parts=name,
        has_precipitation=True,
        temperature=0.5,
        downfall=0.5,
        effects={
            'fog_color': 0xC0D8FF,
            'sky_color': 0x84E6FF,
            'water_color': 0x3F76E4,
            'water_fog_color': 0x050533
        },
        spawners=spawners,
        air_carvers=['tfc:cave', 'tfc:canyon'],
        water_carvers=[],
        features=feature_tags,
        creature_spawn_probability=0.08,
        spawn_costs=costs
    )


def expand_rocks(rocks: list[str]) -> list[str]:
    assert all(r in ROCKS or r in ROCK_CATEGORIES for r in rocks)
    return [
        rock
        for spec in rocks
        for rock in ([spec] if spec in ROCKS else [r for r, d in ROCKS.items() if d.category == spec])
    ]


def join_not_empty(c: str, *elements: str) -> str:
    return c.join((item for item in elements if item != ''))


def count_weighted_list(*pairs: Tuple[Any, int]) -> List[Any]:
    return [item for item, count in pairs for _ in range(count)]

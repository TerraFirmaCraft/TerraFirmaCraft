# Handles generation of all world gen objects

from mcresources import ResourceManager, world_gen as wg
from typing import NamedTuple

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


def generate(rm: ResourceManager):

    # Surface Builder Configs
    grass_dirt_sand = wg.surface_builder_config('minecraft:grass_block[snowy=false]', 'minecraft:dirt', 'minecraft:sand')
    grass_dirt_gravel = wg.surface_builder_config('minecraft:grass_block[snowy=false]', 'minecraft:dirt', 'minecraft:gravel')
    air_air_air = wg.surface_builder_config('minecraft:air', 'minecraft:air', 'minecraft:air')

    # Surface Builders
    rm.surface_builder('badlands', wg.configure('badlands', grass_dirt_sand))
    rm.surface_builder('canyons', wg.configure('thin', grass_dirt_sand))
    rm.surface_builder('deep', wg.configure('deep', grass_dirt_gravel))
    rm.surface_builder('plateau', wg.configure('plateau', grass_dirt_sand))
    rm.surface_builder('default', wg.configure('normal', grass_dirt_sand))
    rm.surface_builder('underwater', wg.configure('underwater', air_air_air))
    rm.surface_builder('mountains', wg.configure('mountains', grass_dirt_sand))
    rm.surface_builder('shore', wg.configure('shore', air_air_air))

    # Configured Features
    rm.feature('ore_veins', wg.configure('tfc:ore_veins'))
    rm.feature('erosion', wg.configure('tfc:erosion'))

    rm.feature('water_fissure',
        wg.decorated(
            wg.decorated(
                wg.configure('tfc:fissure', {'state': wg.block_state('minecraft:water[level=0]')}),
                'minecraft:chance', {'chance': 60}),
            'minecraft:heightmap_world_surface'))
    rm.feature('lava_fissure',
        wg.decorated(
            wg.decorated(
                wg.configure('tfc:fissure', {'state': wg.block_state('minecraft:lava[level=0]')}),
                'minecraft:chance', {'chance': 60}),
            'minecraft:heightmap_world_surface'))

    rm.feature('cave_spike',
        wg.decorated(
            wg.configure('tfc:cave_spike'),
            'minecraft:carving_mask', {'step': 'air', 'probability': 0.09}))
    rm.feature('large_cave_spike',
        wg.decorated(
            wg.configure('tfc:large_cave_spike'),
            'minecraft:carving_mask', {'step': 'air', 'probability': 0.02}))

    for boulder_cfg in (('raw_boulder', 'raw', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm.feature(boulder_cfg[0],
            wg.decorated(
                wg.decorated(
                    wg.configure('tfc:boulder', {'base_type': boulder_cfg[1], 'decoration_type': boulder_cfg[2]}),
                    'minecraft:chance', {'chance': 60}),
                'minecraft:heightmap_world_surface'))

    # Trees / Forests
    rm.feature('forest', wg.configure('tfc:forest', {'entries': [
        {'min_rain': 30, 'max_rain': 210, 'min_temp': 21, 'max_temp': 31, 'tree_feature': 'tfc:tree/acacia', 'old_growth_feature': 'tfc:tree/acacia_large'},
        {'min_rain': 60, 'max_rain': 140, 'min_temp': -6, 'max_temp': 12, 'tree_feature': 'tfc:tree/ash', 'old_growth_feature': 'tfc:tree/ash_large'},
        {'min_rain': 10, 'max_rain': 180, 'min_temp': -10, 'max_temp': 16, 'tree_feature': 'tfc:tree/aspen', 'old_growth_feature': 'tfc:tree/aspen'},
        {'min_rain': 20, 'max_rain': 180, 'min_temp': -15, 'max_temp': 7, 'tree_feature': 'tfc:tree/birch', 'old_growth_feature': 'tfc:tree/birch'},
        {'min_rain': 0, 'max_rain': 120, 'min_temp': 15, 'max_temp': 35, 'tree_feature': 'tfc:tree/blackwood', 'old_growth_feature': 'tfc:tree/blackwood'},
        {'min_rain': 160, 'max_rain': 320, 'min_temp': 11, 'max_temp': 35, 'tree_feature': 'tfc:tree/chestnut', 'old_growth_feature': 'tfc:tree/chestnut'},
        {'min_rain': 290, 'max_rain': 500, 'min_temp': -4, 'max_temp': 15, 'tree_feature': 'tfc:tree/douglas_fir', 'old_growth_feature': 'tfc:tree/douglas_fir_large'},
        {'min_rain': 90, 'max_rain': 250, 'min_temp': 7, 'max_temp': 27, 'tree_feature': 'tfc:tree/hickory', 'old_growth_feature': 'tfc:tree/hickory_large'},
        {'min_rain': 240, 'max_rain': 500, 'min_temp': 15, 'max_temp': 35, 'tree_feature': 'tfc:tree/kapok', 'old_growth_feature': 'tfc:tree/kapok'},
        {'min_rain': 140, 'max_rain': 410, 'min_temp': -5, 'max_temp': 20, 'tree_feature': 'tfc:tree/maple', 'old_growth_feature': 'tfc:tree/maple_large'},
        {'min_rain': 180, 'max_rain': 430, 'min_temp': -10, 'max_temp': 12, 'tree_feature': 'tfc:tree/oak', 'old_growth_feature': 'tfc:tree/oak'},
        {'min_rain': 280, 'max_rain': 500, 'min_temp': 20, 'max_temp': 35, 'tree_feature': 'tfc:tree/palm', 'old_growth_feature': 'tfc:tree/palm'},
        {'min_rain': 60, 'max_rain': 250, 'min_temp': -15, 'max_temp': 7, 'tree_feature': 'tfc:tree/pine', 'old_growth_feature': 'tfc:tree/pine_large'},
        {'min_rain': 10, 'max_rain': 190, 'min_temp': 5, 'max_temp': 20, 'tree_feature': 'tfc:tree/rosewood', 'old_growth_feature': 'tfc:tree/rosewood'},
        {'min_rain': 250, 'max_rain': 420, 'min_temp': -5, 'max_temp': 12, 'tree_feature': 'tfc:tree/sequoia', 'old_growth_feature': 'tfc:tree/sequoia'},
        {'min_rain': 120, 'max_rain': 430, 'min_temp': -14, 'max_temp': 7, 'tree_feature': 'tfc:tree/spruce', 'old_growth_feature': 'tfc:tree/spruce'},
        {'min_rain': 120, 'max_rain': 290, 'min_temp': 17, 'max_temp': 33, 'tree_feature': 'tfc:tree/sycamore', 'old_growth_feature': 'tfc:tree/sycamore_large'},
        {'min_rain': 10, 'max_rain': 240, 'min_temp': -8, 'max_temp': 17, 'tree_feature': 'tfc:tree/white_cedar', 'old_growth_feature': 'tfc:tree/white_cedar'},
        {'min_rain': 260, 'max_rain': 480, 'min_temp': 15, 'max_temp': 32, 'tree_feature': 'tfc:tree/willow', 'old_growth_feature': 'tfc:tree/willow'}
    ]}))

    rm.feature(('tree', 'acacia'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('acacia', 35), 'radius': 1}))
    rm.feature(('tree', 'acacia_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('acacia', 6), 'radius': 2}))
    rm.feature(('tree', 'ash'), wg.configure('tfc:overlay_tree', {'base': 'tfc:ash/base', 'overlay': 'tfc:ash/overlay', 'height_min': 3, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/ash[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'ash_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('ash_large', 5), 'radius': 2}))
    rm.feature(('tree', 'aspen'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('aspen', 16), 'radius': 1}))
    rm.feature(('tree', 'birch'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('birch', 16), 'radius': 1}))
    rm.feature(('tree', 'blackwood'), wg.configure('tfc:overlay_tree', {'base': 'tfc:blackwood/base', 'overlay': 'tfc:blackwood/overlay', 'height_min': 1, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/blackwood[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'chestnut'), wg.configure('tfc:overlay_tree', {'base': 'tfc:blackwood/base', 'overlay': 'tfc:blackwood/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/chestnut[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'douglas_fir'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('douglas_fir', 9), 'radius': 1}))
    rm.feature(('tree', 'douglas_fir_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('douglas_fir_large', 5), 'radius': 2}))
    rm.feature(('tree', 'hickory'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('hickory', 9), 'radius': 1}))
    rm.feature(('tree', 'hickory_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('hickory_large', 5), 'radius': 2}))
    rm.feature(('tree', 'kapok'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('kapok', 7), 'radius': 1}))
    rm.feature(('tree', 'maple'), wg.configure('tfc:overlay_tree', {'base': 'tfc:maple/base', 'overlay': 'tfc:maple/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/maple[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'maple_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('maple_large', 5), 'radius': 2}))
    rm.feature(('tree', 'oak'), wg.configure('tfc:overlay_tree', {'base': 'tfc:oak/base', 'overlay': 'tfc:oak/overlay', 'height_min': 3, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/oak[axis=y]'), 'radius': 2}))
    rm.feature(('tree', 'palm'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('palm', 7), 'radius': 1}))
    rm.feature(('tree', 'pine'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('pine', 9), 'radius': 1}))
    rm.feature(('tree', 'pine_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('pine_large', 9), 'radius': 1}))
    rm.feature(('tree', 'rosewood'), wg.configure('tfc:overlay_tree', {'base': 'tfc:rosewood/base', 'overlay': 'tfc:rosewood/overlay', 'height_min': 1, 'height_range': 2, 'trunk_state': wg.block_state('tfc:wood/log/rosewood[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'sequoia'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('sequoia', 7), 'radius': 1}))
    rm.feature(('tree', 'spruce'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('spruce', 7), 'radius': 1}))
    rm.feature(('tree', 'sycamore'), wg.configure('tfc:overlay_tree', {'base': 'tfc:sycamore/base', 'overlay': 'tfc:sycamore/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/sycamore[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'sycamore_large'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('sycamore_large', 5), 'radius': 2}))
    rm.feature(('tree', 'white_cedar'), wg.configure('tfc:overlay_tree', {'base': 'tfc:white_cedar/base', 'overlay': 'tfc:white_cedar/overlay', 'height_min': 1, 'height_range': 3, 'trunk_state': wg.block_state('tfc:wood/log/white_cedar[axis=y]'), 'radius': 1}))
    rm.feature(('tree', 'willow'), wg.configure('tfc:random_tree', {'structures': random_tree_structures('willow', 7), 'radius': 2}))

    # Carvers
    rm.carver('cave', wg.configure('tfc:cave', {'probability': 0.1}))
    rm.carver('canyon', wg.configure('tfc:canyon', {'probability': 0.015}))

    # Biomes
    for temp in TEMPERATURES:
        for rain in RAINFALLS:
            default_biome(rm, 'badlands', temp, rain, category='mesa', surface_builder='tfc:badlands')
            default_biome(rm, 'canyons', temp, rain, category='plains', surface_builder='tfc:canyons')
            default_biome(rm, 'low_canyons', temp, rain, category='swamp', surface_builder='tfc:canyons')
            default_biome(rm, 'plains', temp, rain, category='plains', surface_builder='tfc:deep')
            default_biome(rm, 'plateau', temp, rain, category='extreme_hills', surface_builder='tfc:plateau')
            default_biome(rm, 'hills', temp, rain, category='plains', surface_builder='tfc:default')
            default_biome(rm, 'rolling_hills', temp, rain, category='plains', surface_builder='tfc:default')
            default_biome(rm, 'lake', temp, rain, category='river', surface_builder='tfc:underwater')
            default_biome(rm, 'lowlands', temp, rain, category='swamp', surface_builder='tfc:deep')
            default_biome(rm, 'mountains', temp, rain, category='extreme_hills', surface_builder='tfc:mountains')
            default_biome(rm, 'old_mountains', temp, rain, category='extreme_hills', surface_builder='tfc:mountains')
            default_biome(rm, 'flooded_mountains', temp, rain, category='extreme_hills', surface_builder='tfc:mountains')
            default_biome(rm, 'ocean', temp, rain, category='ocean', surface_builder='tfc:underwater')
            default_biome(rm, 'deep_ocean', temp, rain, category='ocean', surface_builder='tfc:underwater')
            default_biome(rm, 'river', temp, rain, category='river', surface_builder='tfc:underwater')
            default_biome(rm, 'shore', temp, rain, category='beach', surface_builder='tfc:shore')


def random_tree_structures(name: str, max_count: int):
    return ['tfc:%s/%d' % (name, i) for i in range(1, 1 + max_count)]


def default_biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, surface_builder: str):
    if rain.id == 'arid':
        rain_type = 'none'
    elif temp.id in ('cold', 'frozen'):
        rain_type = 'snow'
    else:
        rain_type = 'rain'
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
        air_carvers=['tfc:cave', 'tfc:canyon'],
        water_carvers=[],
        features=[
            ['tfc:erosion'],  # raw generation
            [],  # lakes
            [],  # local modification
            ['tfc:lava_fissure', 'tfc:water_fissure'],  # underground structure
            ['tfc:raw_boulder', 'tfc:cobble_boulder', 'tfc:mossy_boulder'],  # surface structure
            [],  # strongholds
            ['tfc:ore_veins'],  # underground ores
            [],  # underground decoration
            ['tfc:forest'],  # vegetal decoration
            []   # top layer modification
        ])

# Handles generation of all world gen objects

from mcresources import ResourceManager, world_gen as wg
from typing import NamedTuple, Tuple, List

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
    rm.surface_builder('badlands', wg.configure('tfc:badlands', grass_dirt_sand))
    rm.surface_builder('canyons', wg.configure('tfc:thin', grass_dirt_sand))
    rm.surface_builder('deep', wg.configure('tfc:deep', grass_dirt_gravel))
    rm.surface_builder('plateau', wg.configure('tfc:plateau', grass_dirt_sand))
    rm.surface_builder('default', wg.configure('tfc:normal', grass_dirt_sand))
    rm.surface_builder('underwater', wg.configure('tfc:underwater', air_air_air))
    rm.surface_builder('mountains', wg.configure('tfc:mountains', grass_dirt_sand))
    rm.surface_builder('shore', wg.configure('tfc:shore', air_air_air))

    # Configured Features
    rm.feature('ore_veins', wg.configure('tfc:ore_veins'))
    rm.feature('flora', wg.configure('tfc:flora'))
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
        forest_config(30, 210, 21, 31, 'acacia', True),
        forest_config(60, 140, -6, 12, 'ash', True),
        forest_config(10, 180, -10, 16, 'aspen', False),
        forest_config(20, 180, -15, 7, 'birch', False),
        forest_config(0, 120, 15, 35, 'blackwood', False),
        forest_config(160, 320, 11, 35, 'chestnut', False),
        forest_config(290, 500, -4, 15, 'douglas_fir', True),
        forest_config(90, 250, 7, 27, 'hickory', True),
        forest_config(240, 500, 15, 35, 'kapok', False),
        forest_config(140, 410, -5, 20, 'maple', True),
        forest_config(180, 430, -10, 12, 'oak', False),
        forest_config(280, 500, 20, 35, 'palm', False),
        forest_config(60, 250, -15, 7, 'pine', True),
        forest_config(10, 190, 5, 20, 'rosewood', False),
        forest_config(250, 420, -5, 12, 'sequoia', True),
        forest_config(120, 430, -14, 7, 'spruce', True),
        forest_config(120, 290, 17, 33, 'sycamore', True),
        forest_config(10, 240, -8, 17, 'white_cedar', False),
        forest_config(260, 480, 15, 32, 'willow', False),
    ]}))

    rm.feature(('tree', 'acacia'), wg.configure('tfc:random_tree', random_config('acacia', 35)))
    rm.feature(('tree', 'acacia_large'), wg.configure('tfc:random_tree', random_config('acacia', 6, 2, True)))
    rm.feature(('tree', 'ash'), wg.configure('tfc:overlay_tree', overlay_config('ash', 3, 5)))
    rm.feature(('tree', 'ash_large'), wg.configure('tfc:random_tree', random_config('ash', 5, 2, True)))
    rm.feature(('tree', 'aspen'), wg.configure('tfc:random_tree', random_config('aspen', 16, trunk=[3, 5, 1])))
    rm.feature(('tree', 'birch'), wg.configure('tfc:random_tree', random_config('birch', 16, trunk=[2, 3, 1])))
    rm.feature(('tree', 'blackwood'), wg.configure('tfc:overlay_tree', overlay_config('blackwood', 1, 3)))
    rm.feature(('tree', 'chestnut'), wg.configure('tfc:overlay_tree', overlay_config('chestnut', 2, 4)))
    rm.feature(('tree', 'douglas_fir'), wg.configure('tfc:random_tree', random_config('douglas_fir', 9)))
    rm.feature(('tree', 'douglas_fir_large'), wg.configure('tfc:random_tree', random_config('douglas_fir', 5, 2, True)))
    rm.feature(('tree', 'hickory'), wg.configure('tfc:random_tree', random_config('hickory', 9)))
    rm.feature(('tree', 'hickory_large'), wg.configure('tfc:random_tree', random_config('hickory', 5, 2, True)))
    rm.feature(('tree', 'kapok'), wg.configure('tfc:random_tree', random_config('kapok', 10)))
    rm.feature(('tree', 'maple'), wg.configure('tfc:overlay_tree', overlay_config('maple', 2, 4)))
    rm.feature(('tree', 'maple_large'), wg.configure('tfc:random_tree', random_config('maple', 5, 2, True)))
    rm.feature(('tree', 'oak'), wg.configure('tfc:overlay_tree', overlay_config('oak', 3, 5)))
    rm.feature(('tree', 'palm'), wg.configure('tfc:random_tree', random_config('palm', 7)))
    rm.feature(('tree', 'pine'), wg.configure('tfc:random_tree', random_config('pine', 9)))
    rm.feature(('tree', 'pine_large'), wg.configure('tfc:random_tree', random_config('pine', 5, 2, True)))
    rm.feature(('tree', 'rosewood'), wg.configure('tfc:overlay_tree', overlay_config('rosewood', 1, 3)))
    rm.feature(('tree', 'sequoia'), wg.configure('tfc:random_tree', random_config('sequoia', 7)))
    rm.feature(('tree', 'sequoia_large'), wg.configure('tfc:stacked_tree', stacked_config('sequoia', 3, 7, 2, [(2, 4, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'spruce'), wg.configure('tfc:random_tree', random_config('sequoia', 7)))
    rm.feature(('tree', 'spruce_large'), wg.configure('tfc:stacked_tree', stacked_config('spruce', 3, 5, 2, [(1, 3, 3), (1, 2, 3), (1, 1, 3)], 2, True)))
    rm.feature(('tree', 'sycamore'), wg.configure('tfc:overlay_tree', overlay_config('sycamore', 2, 5)))
    rm.feature(('tree', 'sycamore_large'), wg.configure('tfc:random_tree', random_config('sycamore', 5, 2, True)))
    rm.feature(('tree', 'white_cedar'), wg.configure('tfc:overlay_tree', overlay_config('white_cedar', 1, 5)))
    rm.feature(('tree', 'willow'), wg.configure('tfc:random_tree', random_config('willow', 7)))

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


def forest_config(min_rain: float, max_rain: float, min_temp: float, max_temp: float, tree: str, old_growth: bool):
    cfg = {
        'min_rain': min_rain,
        'max_rain': max_rain,
        'min_temp': min_temp,
        'max_temp': max_temp,
        'tree_feature': 'tfc:tree/%s' % tree
    }
    if old_growth:
        cfg['old_growth_tree_feature'] = 'tfc:tree/%s_large' % tree
    return cfg


def overlay_config(tree: str, min_height: int, max_height: int, width: int = 1, radius: int = 1, large: bool = False):
    block = 'tfc:wood/%s/maple[axis=y]' % tree
    if large:
        tree += '_large'
    return {
        'base': 'tfc:%s/base' % tree,
        'overlay': 'tfc:%s/overlay' % tree,
        'trunk': trunk_config(block, min_height, max_height, width),
        'radius': radius
    }


def random_config(tree: str, structure_count: int, radius: int = 1, large: bool = False, trunk: List = None):
    block = 'tfc:wood/%s/maple[axis=y]' % tree
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
    block = 'tfc:wood/%s/maple[axis=y]' % tree
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
            ['tfc:flora'],  # plants, flowers
            []   # top layer modification
        ])

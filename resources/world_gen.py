# Handles generation of all world gen objects

from mcresources import ResourceManager
from mcresources import utils

from typing import NamedTuple, List, Dict, Optional, Any, Union, Sequence

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

DEFAULT_FOG_COLOR = 12638463  # todo: colormap
DEFAULT_SKY_COLOR = 0x84E6FF  # todo: this was a complete guess. Make it color map


def generate(rm: ResourceManager):

    # Surface Builder Configs
    grass_dirt_sand = wg_surface_builder_config('minecraft:grass[snowy=false]', 'minecraft:dirt', 'minecraft:sand')
    grass_dirt_gravel = wg_surface_builder_config('minecraft:grass[snowy=false]', 'minecraft:dirt', 'minecraft:gravel')
    air_air_air = wg_surface_builder_config('minecraft:air', 'minecraft:air', 'minecraft:air')

    # Surface Builders
    rm_surface_builder(rm, 'badlands', 'badlands', grass_dirt_sand)
    rm_surface_builder(rm, 'canyons', 'thin', grass_dirt_sand)
    rm_surface_builder(rm, 'deep', 'deep', grass_dirt_gravel)
    rm_surface_builder(rm, 'plateau', 'plateau', grass_dirt_sand)
    rm_surface_builder(rm, 'default', 'normal', grass_dirt_sand)
    rm_surface_builder(rm, 'underwater', 'underwater', air_air_air)
    rm_surface_builder(rm, 'mountains', 'mountains', grass_dirt_sand)
    rm_surface_builder(rm, 'shore', 'shore', air_air_air)

    # Configured Features
    rm_feature(rm, 'ore_veins', wg_configure('tfc:ore_veins'))
    rm_feature(rm, 'erosion', wg_configure('tfc:erosion'))

    rm_feature(rm, 'water_fissure',
        wg_decorated(
            wg_decorated(
                wg_configure('tfc:fissure', {'state': utils_wg_block_state('minecraft:water')}),
                'minecraft:chance', {'chance': 60}),
            'minecraft:heightmap_world_surface'))
    rm_feature(rm, 'lava_fissure',
        wg_decorated(
            wg_decorated(
                wg_configure('tfc:fissure', {'state': utils_wg_block_state('minecraft:lava')}),
                'minecraft:chance', {'chance': 60}),
            'minecraft:heightmap_world_surface'))

    rm_feature(rm, 'cave_spike',
        wg_decorated(
            wg_configure('tfc:cave_spike'),
            'minecraft:carving_mask', {'step': 'air', 'probability': 0.09}))
    rm_feature(rm, 'large_cave_spike',
        wg_decorated(
            wg_configure('tfc:large_cave_spike'),
            'minecraft:carving_mask', {'step': 'air', 'probability': 0.02}))

    for boulder_cfg in (('raw_boulder', 'raw', 'raw'), ('cobble_boulder', 'raw', 'cobble'), ('mossy_boulder', 'cobble', 'mossy_cobble')):
        rm_feature(rm, boulder_cfg[0],
            wg_decorated(
                wg_decorated(
                    wg_configure('tfc:boulder', {'base_type': boulder_cfg[1], 'decoration_type': boulder_cfg[2]}),
                    'minecraft:chance', {'chance': 60}),
                'minecraft:heightmap_world_surface'))

    # Trees / Forests
    rm_feature(rm, 'forests', wg_configure('forest', {'entries': [
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

    rm_feature(rm, ('tree', 'acacia'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('acacia', 35), 'radius': 1}))
    rm_feature(rm, ('tree', 'acacia_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('acacia', 6), 'radius': 2}))
    rm_feature(rm, ('tree', 'ash'), wg_configure('tfc:overlay_tree', {'base': 'tfc:ash/base', 'overlay': 'tfc:ash/overlay', 'height_min': 3, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/ash[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'ash_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('ash_large', 5), 'radius': 2}))
    rm_feature(rm, ('tree', 'aspen'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('aspen', 16), 'radius': 1}))
    rm_feature(rm, ('tree', 'birch'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('birch', 16), 'radius': 1}))
    rm_feature(rm, ('tree', 'blackwood'), wg_configure('tfc:overlay_tree', {'base': 'tfc:blackwood/base', 'overlay': 'tfc:blackwood/overlay', 'height_min': 1, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/blackwood[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'chestnut'), wg_configure('tfc:overlay_tree', {'base': 'tfc:blackwood/base', 'overlay': 'tfc:blackwood/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/chestnut[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'douglas_fir'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('douglas_fir', 9), 'radius': 1}))
    rm_feature(rm, ('tree', 'douglas_fir_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('douglas_fir_large', 5), 'radius': 2}))
    rm_feature(rm, ('tree', 'hickory'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('hickory', 9), 'radius': 1}))
    rm_feature(rm, ('tree', 'hickory_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('hickory_large', 5), 'radius': 2}))
    rm_feature(rm, ('tree', 'kapok'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('kapok', 7), 'radius': 1}))
    rm_feature(rm, ('tree', 'maple'), wg_configure('tfc:overlay_tree', {'base': 'tfc:maple/base', 'overlay': 'tfc:maple/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/maple[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'maple_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('maple_large', 5), 'radius': 2}))
    rm_feature(rm, ('tree', 'oak'), wg_configure('tfc:overlay_tree', {'base': 'tfc:oak/base', 'overlay': 'tfc:oak/overlay', 'height_min': 3, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/oak[axis=y]'), 'radius': 2}))
    rm_feature(rm, ('tree', 'palm'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('palm', 7), 'radius': 1}))
    rm_feature(rm, ('tree', 'pine'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('pine', 9), 'radius': 1}))
    rm_feature(rm, ('tree', 'pine_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('pine_large', 9), 'radius': 1}))
    rm_feature(rm, ('tree', 'rosewood'), wg_configure('tfc:overlay_tree', {'base': 'tfc:rosewood/base', 'overlay': 'tfc:rosewood/overlay', 'height_min': 1, 'height_range': 2, 'trunk_state': utils_wg_block_state('tfc:wood/log/rosewood[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'sequoia'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('sequoia', 7), 'radius': 1}))
    rm_feature(rm, ('tree', 'spruce'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('spruce', 7), 'radius': 1}))
    rm_feature(rm, ('tree', 'sycamore'), wg_configure('tfc:overlay_tree', {'base': 'tfc:sycamore/base', 'overlay': 'tfc:sycamore/overlay', 'height_min': 2, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/sycamore[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'sycamore_large'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('sycamore_large', 5), 'radius': 2}))
    rm_feature(rm, ('tree', 'white_cedar'), wg_configure('tfc:overlay_tree', {'base': 'tfc:white_cedar/base', 'overlay': 'tfc:white_cedar/overlay', 'height_min': 1, 'height_range': 3, 'trunk_state': utils_wg_block_state('tfc:wood/log/white_cedar[axis=y]'), 'radius': 1}))
    rm_feature(rm, ('tree', 'willow'), wg_configure('tfc:random_tree', {'structures': random_tree_structures('willow', 7), 'radius': 2}))

    # Carvers
    rm_carver(rm, 'cave', wg_configure('tfc:cave', {'probability': 0.1}))
    rm_carver(rm, 'canyon', wg_configure('tfc:canyon', {'probability': 0.015}))

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


def default_features():
    return [
        ['tfc:erosion'],  # raw generation
        [],  # lakes
        [],  # local modification
        ['tfc:lava_fissure', 'tfc:water_fissure'],  # underground structure
        ['tfc:raw_boulder', 'tfc:cobble_boulder', 'tfc:mossy_boulder'],  # surface structure
        [],  # strongholds
        ['tfc:ore_veins'],  # underground ores
        [],  # underground decoration
        ['tfc:forests'],  # vegetal decoration
        []   # top layer modification
    ]


def default_biome(rm: ResourceManager, name: str, temp: BiomeTemperature, rain: BiomeRainfall, category: str, surface_builder: str):
    if rain.id == 'arid':
        rain_type = 'none'
    elif temp.id in ('cold', 'frozen'):
        rain_type = 'snow'
    else:
        rain_type = 'rain'
    rm_biome(
        rm,
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
            ['tfc:forests'],  # vegetal decoration
            []   # top layer modification
        ])


# TODO: move all below to mcresources ====================================


def rm_biome(self: ResourceManager, name_parts, precipitation: str = 'none', category: str = 'none', depth: float = 0, scale: float = 0, temperature: float = 0, temperature_modifier: str = 'none', downfall: float = 0.5, effects: Dict = None, surface_builder: str = 'minecraft:nope', air_carvers: Optional[List[str]] = None, water_carvers: Optional[List[str]] = None, features: List[List[str]] = None, structures: List[str] = None, spawners=None, player_spawn_friendly: bool = True, creature_spawn_probability: float = 0.5, parent: Optional[str] = None, spawn_costs=None):
    if effects is None:
        effects = {}  # todo: default effects
    if air_carvers is None:
        air_carvers = []
    if water_carvers is None:
        water_carvers = []
    if features is None:
        features = []
    if structures is None:
        structures = []
    res = utils.resource_location(self.domain, name_parts)
    utils.write((*self.resource_dir, 'data', res.domain, 'worldgen', 'biome', res.path), {
        'precipitation': precipitation,
        'category': category,
        'depth': depth,
        'scale': scale,
        'temperature': temperature,
        'temperature_modifier': temperature_modifier,
        'downfall': downfall,
        'effects': effects,
        'carvers': {
            'air': air_carvers,
            'liquid': water_carvers
        },
        'surface_builder': surface_builder,
        'features': features,
        'starts': structures,
        'spawners': spawners,
        'player_spawn_friendly': player_spawn_friendly,
        'creature_spawn_probability': creature_spawn_probability,
        'parent': parent,
        'spawn_costs': spawn_costs
    }, self.indent)


def rm_feature(self: ResourceManager, name_parts: utils.ResourceIdentifier, data: Any):
    res = utils.resource_location(self.domain, name_parts)
    utils.write((*self.resource_dir, 'data', res.domain, 'worldgen', 'configured_feature', res.path), utils_wg_expand_type_config(data), self.indent)


def rm_carver(self: ResourceManager, name_parts: utils.ResourceIdentifier, data: Any):
    res = utils.resource_location(self.domain, name_parts)
    utils.write((*self.resource_dir, 'data', res.domain, 'worldgen', 'configured_carver', res.path), utils_wg_expand_type_config(data), self.indent)


def rm_surface_builder(self: ResourceManager, name_parts: utils.ResourceIdentifier, surface_builder_name_parts: utils.ResourceIdentifier = None, config: Optional[Dict[str, Any]] = None):
    if surface_builder_name_parts is None:
        surface_builder_name_parts = name_parts
    if config is None:
        config = {}
    surface_builder_res = utils.resource_location(self.domain, surface_builder_name_parts)
    res = utils.resource_location(self.domain, name_parts)
    utils.write((*self.resource_dir, 'data', res.domain, 'worldgen', 'surface_builder', res.path), {
        'type': surface_builder_res.join(),
        'config': config
    })


def wg_configure(name_parts: utils.ResourceIdentifier, config: Optional[Dict[str, Any]] = None):
    res = utils.resource_location(name_parts)
    if config is None:
        config = {}
    return {
        'type': res.join(),
        'config': config
    }


def wg_decorated(feature: Dict[str, Any], decorator_name_parts: utils.ResourceIdentifier, config: Optional[Dict[str, Any]] = None):
    decorator_res = utils.resource_location(decorator_name_parts)
    return {
        'type': 'minecraft:decorated',
        'config': {
            'feature': feature,
            'decorator': {
                'type': decorator_res.join(),
                'config': config
            }
        }
    }


def wg_surface_builder_config(top_material: Union[str, Dict[str, Any]], under_material: Union[str, Dict[str, Any]], underwater_material: Union[str, Dict[str, Any]]):
    return {
        'top_material': utils_wg_block_state(top_material),
        'under_material': utils_wg_block_state(under_material),
        'underwater_material': utils_wg_block_state(underwater_material)
    }


def utils_wg_expand_type_config(data: Union[str, Sequence, Dict[str, Any]]) -> Dict[str, Any]:
    if isinstance(data, Dict):
        return data
    elif isinstance(data, str):
        return {'type': data, 'config': {}}
    elif isinstance(data, Sequence) and len(data) == 2 and isinstance(data[0], str) and isinstance(data[1], Dict):
        return {'type': data[0], 'config': data[1]}
    else:
        raise ValueError('Unknown data')


def utils_wg_block_state(data: Any):
    if isinstance(data, str):
        property_map = {}
        if '[' in data and data.endswith(']'):
            name, properties = data[:-1].split('[')
            for pair in properties.split(']'):
                key, value = pair.split('=')
                property_map[key] = value
        else:
            name = data
        return {'Name': name, 'Properties': property_map}
    elif isinstance(data, dict):
        return data
    else:
        raise ValueError('Unknown data type: ' + data)


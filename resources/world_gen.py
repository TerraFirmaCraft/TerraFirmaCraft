# Handles generation of all world gen objects

from mcresources import ResourceManager

from typing import NamedTuple, List, Dict, Optional

BiomeTemperature = NamedTuple('BiomeClimate', id=str, temperature=float, water_color=float, water_fog_color=float)
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


def generate(rm: ResourceManager):
    for temp in TEMPERATURES:
        for rain in RAINFALLS:
            biome(rm, biome_id(temp, rain, 'badlands'), category='mesa',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:badlands',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'canyons'), category='plains',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:thin',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'low_canyons'), category='swamp',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:thin',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'plains'), category='plains',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:deep',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'plateau'), category='extreme_hills',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:plateau',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'hills'), category='plains',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:default',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'rolling_hills'), category='plains',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:default',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'lake'), category='river',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:lake',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'lowlands'), category='swamp',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:lake',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'lake'), category='river',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:underwater',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'mountains'), category='extreme_hills',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:mountains',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'old_mountains'), category='extreme_hills',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:mountains',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'flooded_mountains'), category='extreme_hills',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:mountains',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'ocean'), category='ocean',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:underwater',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'deep_ocean'), category='ocean',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:underwater',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'river'), category='river',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:underwater',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())

            biome(rm, biome_id(temp, rain, 'shore'), category='beach',
                  precipitation=default_precipitation(temp, rain),
                  temperature=temp.temperature,
                  downfall=rain.downfall,
                  surface_builder='tfc:shore',
                  air_carvers=default_air_carvers(),
                  water_carvers=default_water_carvers(),
                  features=default_features())


def biome_id(temp: BiomeTemperature, rain: BiomeRainfall, name: str):
    return '%s_%s_%s' % (name, temp.id, rain.id)


def default_precipitation(temperature: BiomeTemperature, rainfall: BiomeRainfall):
    if rainfall.id == 'arid':
        return 'none'
    elif temperature.id in ('cold', 'frozen'):
        return 'snow'
    else:
        return 'rain'


def default_air_carvers():
    return ['tfc:caves', 'tfc:ravines']


def default_water_carvers():
    return []


def default_features():
    return [
        [],  # raw generation
        [],  # lakes
        [],  # local modification
        [],  # underground structure
        [],  # surface structure
        [],  # strongholds
        [],  # underground ores
        [],  # underground decoration
        [],  # vegetal decoration
        []   # top layer modification
    ]


def biome(rm: ResourceManager, biome_name, precipitation: str = 'none', category: str = 'none', depth: float = 0, scale: float = 0, temperature: float = 0, temperature_modifier: str = 'none', downfall: float = 0.5, effects: Dict = None, surface_builder: str = 'minecraft:nope', air_carvers: Optional[List[str]] = None, water_carvers: Optional[List[str]] = None, features: List[List[str]] = None, structures: List[str] = None, spawners=None):
    if effects is None:
        effects = biome_effects()
    if air_carvers is None:
        air_carvers = []
    if water_carvers is None:
        water_carvers = []
    if features is None:
        features = []
    if structures is None:
        structures = []
    rm.data(('worldgen', 'biome', biome_name), {
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
        'spawners': spawners
    })


def biome_effects():
    return None

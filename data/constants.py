from collections import namedtuple
from typing import Dict, List

Rock = namedtuple('Rock', ('category',))
Metal = namedtuple('Metal', ('tier', 'usable'))
Ore = namedtuple('Ore', ('metal', 'graded'))
OreGrade = namedtuple('OreGrade', ('weight',))
Vein = namedtuple('Vein',
                  ('ore', 'type', 'rarity', 'size', 'min_y', 'max_y', 'density', 'poor', 'normal', 'rich', 'rocks'))

HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCKS: Dict[str, Rock] = {
    'chalk': Rock('sedimentary'),
    'chert': Rock('sedimentary'),
    'claystone': Rock('sedimentary'),
    'conglomerate': Rock('sedimentary'),
    'dolomite': Rock('sedimentary'),
    'limestone': Rock('sedimentary'),
    'shale': Rock('sedimentary'),
    'gneiss': Rock('metamorphic'),
    'marble': Rock('metamorphic'),
    'phyllite': Rock('metamorphic'),
    'quartzite': Rock('metamorphic'),
    'schist': Rock('metamorphic'),
    'slate': Rock('metamorphic'),
    'diorite': Rock('igneous_intrusive'),
    'gabbro': Rock('igneous_intrusive'),
    'granite': Rock('igneous_intrusive'),
    'andesite': Rock('igneous_extrusive'),
    'basalt': Rock('igneous_extrusive'),
    'dacite': Rock('igneous_extrusive'),
    'rhyolite': Rock('igneous_extrusive')
}
METALS: Dict[str, Metal] = {
    'bismuth': Metal(1, True),
    'bismuth_bronze': Metal(2, True),
    'black_bronze': Metal(2, True),
    'bronze': Metal(2, True),
    'copper': Metal(1, True),
    'gold': Metal(1, True),
    'nickel': Metal(1, True),
    'rose_gold': Metal(1, True),
    'silver': Metal(1, True),
    'tin': Metal(1, True),
    'zinc': Metal(1, True),
    'sterling_silver': Metal(1, True),
    'wrought_iron': Metal(3, True),
    'cast_iron': Metal(1, True),
    'pig_iron': Metal(3, True),
    'steel': Metal(4, True),
    'black_steel': Metal(5, True),
    'blue_steel': Metal(6, True),
    'red_steel': Metal(7, True),
    'weak_steel': Metal(5, False),
    'weak_blue_steel': Metal(5, False),
    'weak_red_steel': Metal(5, False),
    'unknown': Metal(1, False)
}
ORES: Dict[str, Ore] = {
    'native_copper': Ore('copper', True),
    'native_gold': Ore('gold', True),
    'hematite': Ore('cast_iron', True),
    'native_silver': Ore('silver', True),
    'cassiterite': Ore('tin', True),
    'bismuthinite': Ore('bismuth', True),
    'garnierite': Ore('nickel', True),
    'malachite': Ore('copper', True),
    'magnetite': Ore('cast_iron', True),
    'limonite': Ore('cast_iron', True),
    'sphalerite': Ore('zinc', True),
    'tetrahedrite': Ore('copper', True),
    'bituminous_coal': Ore(None, False),
    'lignite': Ore(None, False),
    'kaolinite': Ore(None, False),
    'gypsum': Ore(None, False),
    'graphite': Ore(None, False),
    'sulfur': Ore(None, False),
    'cinnabar': Ore(None, False),
    'cryolite': Ore(None, False),
    'saltpeter': Ore(None, False),
    'sylvite': Ore(None, False),
    'borax': Ore(None, False),
    'lapis_lazuli': Ore(None, False),
    'halite': Ore(None, False)
}
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50),
    'poor': OreGrade(30),
    'rich': OreGrade(20)
}
ORE_VEINS: Dict[str, Vein] = {
    'normal_native_copper': Vein('native_copper', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'surface_native_copper': Vein('native_copper', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_extrusive']),
    'normal_native_gold': Vein('native_gold', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30,
                               ['igneous_extrusive', 'igneous_intrusive']),
    'deep_native_gold': Vein('native_gold', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60,
                             ['igneous_extrusive', 'igneous_intrusive']),
    'normal_native_silver': Vein('native_silver', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['granite', 'gneiss']),
    'poor_native_silver': Vein('native_silver', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['granite', 'metamorphic']),
    'normal_hematite': Vein('hematite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'deep_hematite': Vein('hematite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive']),
    'normal_cassiterite': Vein('cassiterite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive']),
    'surface_cassiterite': Vein('cassiterite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_intrusive']),
    'normal_bismuthinite': Vein('bismuthinite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30,
                                ['igneous_intrusive', 'sedimentary']),
    'surface_bismuthinite': Vein('bismuthinite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10,
                                 ['igneous_intrusive', 'sedimentary']),
    'normal_garnierite': Vein('garnierite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['gabbro']),
    'poor_garnierite': Vein('garnierite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['igneous_intrusive']),
    'normal_malachite': Vein('malachite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['marble', 'limestone']),
    'poor_malachite': Vein('malachite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10,
                           ['marble', 'limestone', 'phyllite', 'chalk', 'dolomite']),
    'normal_magnetite': Vein('magnetite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary']),
    'deep_magnetite': Vein('magnetite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary']),
    'normal_limonite': Vein('limonite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary']),
    'deep_limonite': Vein('limonite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary']),
    'normal_sphalerite': Vein('sphalerite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic']),
    'surface_sphalerite': Vein('sphalerite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['metamorphic']),
    'normal_tetrahedrite': Vein('tetrahedrite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic']),
    'surface_tetrahedrite': Vein('tetrahedrite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['metamorphic']),

    'bituminous_coal': Vein('bituminous_coal', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'lignite': Vein('lignite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'kaolinite': Vein('kaolinite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'graphite': Vein('graphite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['gneiss', 'marble', 'quartzite', 'schist']),
    'cinnabar': Vein('cinnabar', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['igneous_extrusive', 'quartzite', 'shale']),
    'cryolite': Vein('cryolite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['granite']),
    'saltpeter': Vein('saltpeter', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'sulfur': Vein('sulfur', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['igneous_extrusive']),
    'sylvite': Vein('sylvite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['shale', 'claystone', 'chert']),
    'borax': Vein('borax', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['slate']),
    'gypsum': Vein('gypsum', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['metamorphic']),
    'lapis_lazuli': Vein('lapis_lazuli', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['limestone', 'marble']),

    'clay': Vein('clay', 'disc', 20, 14, 90, 120, 100, 0, 0, 0, ['soil']),
    'peat': Vein('peat', 'disc', 50, 25, 90, 120, 100, 0, 0, 0, ['soil']),
    'gravel': Vein('gravel', 'disc', 100, 40, 20, 80, 60, 0, 0, 0,
                   ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']),
    'halite': Vein('halite', 'disc', 120, 30, 80, 100, 80, 0, 0, 0, ['sedimentary']),

    'kimberlite': Vein('kimberlite', 'pipe', 60, 60, 5, 140, 40, 0, 0, 0, ['gabbro']),
}

ROCK_BLOCK_TYPES = ['raw', 'bricks', 'cobble', 'gravel', 'smooth', 'spike']
ROCK_SPIKE_PARTS = ['base', 'middle', 'tip']
SAND_BLOCK_TYPES = ['brown', 'white', 'black', 'red', 'yellow', 'gray']
SOIL_BLOCK_TYPES = ['dirt', 'grass', 'grass_path']
SOIL_BLOCK_VARIANTS = ['silt', 'loam', 'sandy_loam', 'silty_loam', 'clay_loam', 'clay', 'peat']
STANDARD_SOIL_BLOCK_VARIANTS = ['silt', 'loam', 'sandy_loam', 'clay_loam']

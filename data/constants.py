from collections import namedtuple
from typing import Dict, List

Rock = namedtuple('Rock', ('category',))
Metal = namedtuple('Metal', ('tier', 'usable'))
Ore = namedtuple('Ore', ('metal', 'graded'))
OreGrade = namedtuple('OreGrade', ('weight',))
Vein = namedtuple('Vein', ('ore', 'rarity', 'size', 'min_y', 'max_y', 'density', 'shape', 'rocks'))

HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCKS: Dict[str, Rock] = {
    'chalk': Rock('sedimentary'),
    'chert': Rock('sedimentary'),
    'claystone': Rock('sedimentary'),
    'conglomerate': Rock('sedimentary'),
    'dolomite': Rock('sedimentary'),
    'limestone': Rock('sedimentary'),
    'rocksalt': Rock('sedimentary'),
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
    'lapis_lazuli': Ore(None, False)
}
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50),
    'poor': OreGrade(30),
    'rich': OreGrade(20)
}
ORE_VEINS: Dict[str, Vein] = {
    'native_copper': Vein('native_copper', 120, 40, 5, 80, 70, 'cluster', ['igneous_extrusive']),
    'native_gold': Vein('native_gold', 160, 50, 5, 65, 70, 'cluster', ['igneous_extrusive', 'igneous_intrusive']),
    'native_silver': Vein('native_silver', 140, 30, 5, 65, 70, 'cluster', ['granite', 'gneiss']),
    'hematite': Vein('hematite', 120, 40, 5, 80, 70, 'cluster', ['igneous_extrusive']),
    'cassiterite': Vein('cassiterite', 100, 30, 5, 80, 70, 'cluster', ['igneous_intrusive']),
    'bismuthinite': Vein('bismuthinite', 100, 40, 5, 80, 70, 'cluster', ['igneous_extrusive', 'sedimentary']),
    'garnierite': Vein('garnierite', 60, 30, 5, 45, 90, ['cluster'], ['gabbro']),
    'malachite': Vein('malachite', 100, 30, 5, 80, 70, 'cluster', ['limestone', 'marble']),
    'magnetite': Vein('magnetite', 150, 50, 5, 70, 70, 'cluster', ['sedimentary']),
    'limonite': Vein('limonite', 150, 50, 5, 70, 70, 'cluster', ['sedimentary']),
    'sphalerite': Vein('sphalerite', 90, 30, 40, 110, 70, 'cluster', ['metamorphic']),
    'tetrahedrite': Vein('tetrahedrite', 100, 30, 5, 90, 70, 'cluster', ['metamorphic']),
    'bituminous_coal': Vein('bituminous_coal', 130, 60, 5, 180, 90, 'cluster', ['sedimentary']),
    'lignite': Vein('lignite', 150, 30, 5, 80, 70, 'cluster', ['sedimentary']),
    'kaolinite': Vein('kaolinite', 90, 30, 20, 90, 70, 'cluster', ['sedimentary']),
    'graphite': Vein('graphite', 90, 30, 20, 90, 70, 'cluster', ['marble', 'gneiss', 'quartzite', 'schist']),
    'cinnabar': Vein('cinnabar', 120, 20, 5, 60, 90, 'cluster', ['igneous_extrusive', 'shale', 'quartzite']),
    'cryolite': Vein('cryolite', 120, 20, 40, 80, 90, 'cluster', ['granite']),
    'saltpeter': Vein('saltpeter', 140, 20, 60, 180, 90, 'cluster', ['sedimentary']),
    'sulfur': Vein('sulfur', 140, 20, 60, 180, 90, 'cluster', ['sedimentary', 'metamorphic']),
    'sylvite': Vein('sylvite', 100, 20, 5, 80, 70, 'cluster', ['rocksalt']),
    'borax': Vein('borax', 100, 20, 5, 80, 70, 'cluster', ['shale', 'slate']),
    'gypsum': Vein('gypsum', 100, 20, 5, 80, 70, 'cluster', ['chalk']),
    'lapis_lazuli': Vein('lapis_lazuli', 100, 40, 5, 80, 70, 'cluster', ['marble']),
    'surface_native_copper': Vein('native_copper', 80, 20, 60, 80, 70, 'cluster', ['igneous_extrusive']),
    'surface_cassiterite': Vein('cassiterite', 70, 15, 60, 80, 70, 'cluster', ['igneous_intrusive']),
    'surface_bismuthinite': Vein('bismuthinite', 70, 20, 60, 80, 70, 'cluster', ['igneous_extrusive', 'sedimentary']),
    'surface_sphalerite': Vein('sphalerite', 70, 15, 80, 180, 70, 'cluster', ['metamorphic']),
    'surface_tetrahedrite': Vein('tetrahedrite', 80, 15, 60, 90, 70, 'cluster', ['metamorphic']),
}

ROCK_BLOCK_TYPES = ['raw', 'bricks', 'cobble', 'gravel', 'smooth', 'spike']
ROCK_SPIKE_PARTS = ['base', 'middle', 'tip']
SAND_BLOCK_TYPES = ['brown', 'white', 'black', 'red', 'yellow', 'gray']
SOIL_BLOCK_TYPES = ['dirt', 'grass', 'grass_path']
SOIL_BLOCK_VARIANTS = ['silt', 'loam', 'sandy_loam', 'silty_loam', 'clay_loam', 'clay', 'peat']

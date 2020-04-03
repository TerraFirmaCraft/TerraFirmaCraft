HORIZONTAL_DIRECTIONS = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCKS = {
    'chalk': {
        'category': 'sedimentary'
    },
    'chert': {
        'category': 'sedimentary'
    },
    'claystone': {
        'category': 'sedimentary'
    },
    'conglomerate': {
        'category': 'sedimentary'
    },
    'dolomite': {
        'category': 'sedimentary'
    },
    'limestone': {
        'category': 'sedimentary'
    },
    'rocksalt': {
        'category': 'sedimentary'
    },
    'shale': {
        'category': 'sedimentary'
    },
    'gneiss': {
        'category': 'metamorphic'
    },
    'marble': {
        'category': 'metamorphic'
    },
    'phyllite': {
        'category': 'metamorphic'
    },
    'quartzite': {
        'category': 'metamorphic'
    },
    'schist': {
        'category': 'metamorphic'
    },
    'slate': {
        'category': 'metamorphic'
    },
    'diorite': {
        'category': 'igneous_intrusive'
    },
    'gabbro': {
        'category': 'igneous_intrusive'
    },
    'granite': {
        'category': 'igneous_intrusive'
    },
    'andesite': {
        'category': 'igneous_extrusive'
    },
    'basalt': {
        'category': 'igneous_extrusive'
    },
    'dacite': {
        'category': 'igneous_extrusive'
    },
    'rhyolite': {
        'category': 'igneous_extrusive'
    }
}
ORES = {
    'native_copper': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'native_gold': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'native_platinum': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'hematite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'native_silver': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'cassiterite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'galena': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'bismuthinite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'garnierite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'malachite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'magnetite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'limonite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'sphalerite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'tetrahedrite': {
        'graded': True,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'bituminous_coal': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'lignite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'kaolinite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'gypsum': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'satinspar': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'selenite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'graphite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'kimberlite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'petrified_wood': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'sulfur': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'jet': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'microcline': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'pitchblende': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'cinnabar': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'cryolite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'saltpeter': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'serpentine': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'sylvite': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'borax': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'olivine': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    },
    'lapis_lazuli': {
        'graded': False,
        'rarity': 0,
        'size': 0,
        'rocks': ['sedimentary', 'andesite']
    }
}
ORE_GRADES = {
    'normal': {'weight': 50},
    'poor': {'weight': 30},
    'rich': {'weight': 20}
}
ROCK_BLOCK_TYPES = ['raw', 'bricks', 'cobble', 'gravel', 'smooth']
SAND_BLOCK_TYPES = ['brown', 'white', 'black', 'red', 'yellow', 'gray']
SOIL_BLOCK_TYPES = ['dirt', 'grass', 'grass_path']
SOIL_BLOCK_VARIANTS = ['silt', 'loam', 'sandy_loam', 'silty_loam', 'clay_loam', 'clay', 'peat']

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Dict, List, Set, NamedTuple, Sequence, Optional, Tuple, Any


class Rock(NamedTuple):
    category: str
    sand: str

class MetalItem(NamedTuple):
    type: str
    smelt_amount: int
    parent_model: str
    tag: Optional[str]
    mold: bool
    durability: bool

class Ore(NamedTuple):
    metal: Optional[str]
    graded: bool
    required_tool: str
    tag: str

class OreGrade(NamedTuple):
    weight: int
    grind_amount: int

class Vein(NamedTuple):
    ore: str
    type: str
    rarity: int
    size: int
    min_y: int
    max_y: int
    density: float
    poor: float
    normal: float
    rich: float
    rocks: List[str]
    spoiler_ore: str
    spoiler_rarity: int
    spoiler_rocks: List[str]
    biomes: Optional[str]
    height: Optional[int]
    deposits: bool

class Plant(NamedTuple):
    clay: bool
    min_temp: float
    max_temp: float
    min_rain: float
    max_rain: float
    type: str

class Wood(NamedTuple):
    temp: float
    duration: int

class Berry(NamedTuple):
    min_temp: float
    max_temp: float
    min_rain: float
    max_rain: float
    type: str
    min_forest: str
    max_forest: str

class Fruit(NamedTuple):
    min_temp: float
    max_temp: float
    min_rain: float
    max_rain: float

class Crop(NamedTuple):
    type: str
    stages: int
    nutrient: str
    min_temp: float
    max_temp: float
    min_rain: float
    max_rain: float
    min_hydration: int
    max_hydration: int
    min_forest: Optional[str]
    max_forest: Optional[str]

class Metal(NamedTuple):
    tier: int
    types: Set[str]
    heat_capacity_base: float  # Do not access directly, use one of specific or ingot heat capacity.
    melt_temperature: float
    melt_metal: Optional[str]

    def specific_heat_capacity(self) -> float: return round(300 / self.heat_capacity_base) / 100_000
    def ingot_heat_capacity(self) -> float: return 1 / self.heat_capacity_base


POTTERY_MELT = 1400 - 1
POTTERY_HEAT_CAPACITY = 1.2  # Heat Capacity

HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES = ('sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive')
ROCK_CATEGORY_ITEMS = ('axe', 'hammer', 'hoe', 'javelin', 'knife', 'shovel')

TOOL_TAGS: Dict[str, str] = {
    # Rock
    'axe': 'axes',
    'hammer': 'hammers',
    'hoe': 'hoes',
    'javelin': 'javelins',
    'knife': 'knives',
    'shovel': 'shovels',
    # Metal Only
    'pickaxe': 'pickaxes',
    'chisel': 'chisels',
    'mace': 'maces',
    'sword': 'swords',
    'saw': 'saws',
    'propick': 'propicks',
    'scythe': 'scythes',
    'shears': 'shears',
    'tuyere': 'tuyeres'
}

ROCKS: Dict[str, Rock] = {
    'granite': Rock('igneous_intrusive', 'white'),
    'diorite': Rock('igneous_intrusive', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black'),
    'shale': Rock('sedimentary', 'black'),
    'claystone': Rock('sedimentary', 'brown'),
    'limestone': Rock('sedimentary', 'white'),
    'conglomerate': Rock('sedimentary', 'green'),
    'dolomite': Rock('sedimentary', 'black'),
    'chert': Rock('sedimentary', 'yellow'),
    'chalk': Rock('sedimentary', 'white'),
    'rhyolite': Rock('igneous_extrusive', 'red'),
    'basalt': Rock('igneous_extrusive', 'red'),
    'andesite': Rock('igneous_extrusive', 'red'),
    'dacite': Rock('igneous_extrusive', 'red'),
    'quartzite': Rock('metamorphic', 'white'),
    'slate': Rock('metamorphic', 'brown'),
    'phyllite': Rock('metamorphic', 'brown'),
    'schist': Rock('metamorphic', 'green'),
    'gneiss': Rock('metamorphic', 'green'),
    'marble': Rock('metamorphic', 'yellow')
}
METALS: Dict[str, Metal] = {
    'bismuth': Metal(1, {'part'}, 0.14, 270, None),
    'bismuth_bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 985, None),
    'black_bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 1070, None),
    'bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 950, None),
    'brass': Metal(2, {'part'}, 0.35, 930, None),
    'copper': Metal(1, {'part', 'tool', 'armor', 'utility'}, 0.35, 1080, None),
    'gold': Metal(1, {'part'}, 0.6, 1060, None),
    'nickel': Metal(1, {'part'}, 0.48, 1453, None),
    'rose_gold': Metal(1, {'part'}, 0.35, 960, None),
    'silver': Metal(1, {'part'}, 0.48, 961, None),
    'tin': Metal(1, {'part'}, 0.14, 230, None),
    'zinc': Metal(1, {'part'}, 0.21, 420, None),
    'sterling_silver': Metal(1, {'part'}, 0.35, 950, None),
    'wrought_iron': Metal(3, {'part', 'tool', 'armor', 'utility'}, 0.35, 1535, 'cast_iron'),
    'cast_iron': Metal(1, {'part'}, 0.35, 1535, None),
    'pig_iron': Metal(3, set(), 0.35, 1535, None),
    'steel': Metal(4, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540, None),
    'black_steel': Metal(5, {'part', 'tool', 'armor', 'utility'}, 0.35, 1485, None),
    'blue_steel': Metal(6, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540, None),
    'red_steel': Metal(6, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540, None),
    'weak_steel': Metal(4, set(), 0.35, 1540, None),
    'weak_blue_steel': Metal(5, set(), 0.35, 1540, None),
    'weak_red_steel': Metal(5, set(), 0.35, 1540, None),
    'high_carbon_steel': Metal(3, set(), 0.35, 1540, 'pig_iron'),
    'high_carbon_black_steel': Metal(4, set(), 0.35, 1540, 'weak_steel'),
    'high_carbon_blue_steel': Metal(5, set(), 0.35, 1540, 'weak_blue_steel'),
    'high_carbon_red_steel': Metal(5, set(), 0.35, 1540, 'weak_red_steel'),
    'unknown': Metal(0, set(), 0.5, 400, None)
}
METAL_BLOCKS: Dict[str, MetalItem] = {
    'anvil': MetalItem('utility', 1400, 'tfc:block/anvil', None, False, False),
    'chain': MetalItem('utility', 6, 'tfc:block/chain', None, False, False),
    'lamp': MetalItem('utility', 100, 'tfc:block/lamp', None, False, False),
    'trapdoor': MetalItem('utility', 200, 'tfc:block/trapdoor', None, False, False)
}
METAL_ITEMS: Dict[str, MetalItem] = {
    'ingot': MetalItem('all', 100, 'item/generated', 'forge:ingots', True, False),
    'double_ingot': MetalItem('part', 200, 'item/generated', 'forge:double_ingots', False, False),
    'sheet': MetalItem('part', 200, 'item/generated', 'forge:sheets', False, False),
    'double_sheet': MetalItem('part', 400, 'item/generated', 'forge:double_sheets', False, False),
    'rod': MetalItem('part', 50, 'item/handheld_rod', 'forge:rods', False, False),

    'tuyere': MetalItem('tool', 400, 'item/generated', None, False, True),
    'fish_hook': MetalItem('tool', 200, 'item/generated', None, False, False),
    'fishing_rod': MetalItem('tool', 200, 'item/generated', 'forge:fishing_rods', False, True),
    'pickaxe': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'pickaxe_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'shovel': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'shovel_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'axe': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'axe_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'hoe': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'hoe_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'chisel': MetalItem('tool', 100, 'tfc:item/handheld_flipped', None, False, True),
    'chisel_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'sword': MetalItem('tool', 200, 'item/handheld', None, False, True),
    'sword_blade': MetalItem('tool', 200, 'item/generated', None, True, False),
    'mace': MetalItem('tool', 200, 'item/handheld', None, False, True),
    'mace_head': MetalItem('tool', 200, 'item/generated', None, True, False),
    'saw': MetalItem('tool', 100, 'tfc:item/handheld_flipped', None, False, True),
    'saw_blade': MetalItem('tool', 100, 'item/generated', None, True, False),
    'javelin': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'javelin_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'hammer': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'hammer_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'propick': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'propick_head': MetalItem('tool', 100, 'item/generated', None, True, False),
    'knife': MetalItem('tool', 100, 'tfc:item/handheld_flipped', None, False, True),
    'knife_blade': MetalItem('tool', 100, 'item/generated', None, True, False),
    'scythe': MetalItem('tool', 100, 'item/handheld', None, False, True),
    'scythe_blade': MetalItem('tool', 100, 'item/generated', None, True, False),
    'shears': MetalItem('tool', 200, 'item/handheld', None, False, True),

    'unfinished_helmet': MetalItem('armor', 400, 'item/generated', None, False, False),
    'helmet': MetalItem('armor', 600, 'item/generated', None, False, True),
    'unfinished_chestplate': MetalItem('armor', 400, 'item/generated', None, False, False),
    'chestplate': MetalItem('armor', 800, 'item/generated', None, False, True),
    'unfinished_greaves': MetalItem('armor', 400, 'item/generated', None, False, False),
    'greaves': MetalItem('armor', 600, 'item/generated', None, False, True),
    'unfinished_boots': MetalItem('armor', 200, 'item/generated', None, False, False),
    'boots': MetalItem('armor', 400, 'item/generated', None, False, True),

    'shield': MetalItem('tool', 400, 'item/handheld', None, False, True)
}
METAL_ITEMS_AND_BLOCKS: Dict[str, MetalItem] = {**METAL_ITEMS, **METAL_BLOCKS}
METAL_TOOL_HEADS = ('chisel', 'hammer', 'hoe', 'javelin', 'knife', 'mace', 'pickaxe', 'propick', 'saw', 'scythe', 'shovel', 'sword', 'axe')

ORES: Dict[str, Ore] = {
    'native_copper': Ore('copper', True, 'copper', 'copper'),
    'native_gold': Ore('gold', True, 'copper', 'gold'),
    'hematite': Ore('cast_iron', True, 'copper', 'iron'),
    'native_silver': Ore('silver', True, 'copper', 'silver'),
    'cassiterite': Ore('tin', True, 'copper', 'tin'),
    'bismuthinite': Ore('bismuth', True, 'copper', 'bismuth'),
    'garnierite': Ore('nickel', True, 'bronze', 'nickel'),
    'malachite': Ore('copper', True, 'copper', 'copper'),
    'magnetite': Ore('cast_iron', True, 'copper', 'iron'),
    'limonite': Ore('cast_iron', True, 'copper', 'iron'),
    'sphalerite': Ore('zinc', True, 'copper', 'zinc'),
    'tetrahedrite': Ore('copper', True, 'copper', 'copper'),
    'bituminous_coal': Ore(None, False, 'copper', 'coal'),
    'lignite': Ore(None, False, 'copper', 'coal'),
    'kaolinite': Ore(None, False, 'copper', 'kaolinite'),
    'gypsum': Ore(None, False, 'copper', 'gypsum'),
    'graphite': Ore(None, False, 'copper', 'graphite'),
    'sulfur': Ore(None, False, 'copper', 'sulfur'),
    'cinnabar': Ore(None, False, 'bronze', 'redstone'),
    'cryolite': Ore(None, False, 'bronze', 'redstone'),
    'saltpeter': Ore(None, False, 'copper', 'saltpeter'),
    'sylvite': Ore(None, False, 'copper', 'sylvite'),
    'borax': Ore(None, False, 'copper', 'borax'),
    'halite': Ore(None, False, 'bronze', 'halite'),
    'amethyst': Ore(None, False, 'steel', 'amethyst'),  # Mohs: 7
    'diamond': Ore(None, False, 'black_steel', 'diamond'),  # Mohs: 10
    'emerald': Ore(None, False, 'steel', 'emerald'),  # Mohs: 7.5-8
    'lapis_lazuli': Ore(None, False, 'wrought_iron', 'lapis'),  # Mohs: 5-6
    'opal': Ore(None, False, 'wrought_iron', 'opal'),  # Mohs: 5.5-6.5
    'pyrite': Ore(None, False, 'copper', 'pyrite'),
    'ruby': Ore(None, False, 'black_steel', 'ruby'),  # Mohs: 9
    'sapphire': Ore(None, False, 'black_steel', 'sapphire'),  # Mohs: 9
    'topaz': Ore(None, False, 'steel', 'topaz')  # Mohs: 8
}
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50, 5),
    'poor': OreGrade(30, 3),
    'rich': OreGrade(20, 7)
}
DEFAULT_FORGE_ORE_TAGS: Tuple[str, ...] = ('coal', 'diamond', 'emerald', 'gold', 'iron', 'lapis', 'netherite_scrap', 'quartz', 'redstone')


def vein(ore: str, vein_type: str, rarity: int, size: int, min_y: int, max_y: int, density: float, poor: float, normal: float, rich: float, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 2, deposits: bool = False):
    # Factory method to allow default values
    return Vein(ore, vein_type, rarity, size, min_y, max_y, density, poor, normal, rich, rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height, deposits)


def preset_vein(ore: str, vein_type: str, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 0, preset: Tuple[int, int, int, int, int, int, int, int] = None, deposits: bool = False):
    assert preset is not None
    return Vein(ore, vein_type, preset[0], preset[1], preset[2], preset[3], preset[4], preset[5], preset[6], preset[7], rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height, deposits)


# Default parameters for common ore veins
# rarity, size, min_y, max_y, density, poor, normal, rich
POOR_METAL_ORE = (80, 15, 0, 100, 40, 40, 30, 10)
NORMAL_METAL_ORE = (60, 20, -32, 75, 60, 20, 50, 30)
DEEP_METAL_ORE = (100, 30, -64, 30, 70, 10, 30, 60)
SURFACE_METAL_ORE = (20, 15, 60, 210, 50, 60, 30, 10)

POOR_S_METAL_ORE = (100, 12, 0, 100, 40, 60, 30, 10)
NORMAL_S_METAL_ORE = (70, 15, -32, 60, 60, 20, 50, 30)
DEEP_S_METAL_ORE = (110, 25, -64, 30, 70, 10, 30, 60)

DEEP_MINERAL_ORE = (90, 10, -48, 100, 60, 0, 0, 0)
HIGH_MINERAL_ORE = (90, 10, 0, 210, 60, 0, 0, 0)

ORE_VEINS: Dict[str, Vein] = {
    'normal_native_copper': preset_vein('native_copper', 'cluster', ['igneous_extrusive'], preset=NORMAL_METAL_ORE),
    'surface_native_copper': preset_vein('native_copper', 'cluster', ['igneous_extrusive'], preset=SURFACE_METAL_ORE, deposits=True),
    'normal_native_gold': preset_vein('native_gold', 'cluster', ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 20, ['igneous_extrusive', 'igneous_intrusive'], preset=NORMAL_S_METAL_ORE),
    'deep_native_gold': preset_vein('native_gold', 'cluster', ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 10, ['igneous_extrusive', 'igneous_intrusive'], preset=DEEP_S_METAL_ORE),
    'normal_native_silver': preset_vein('native_silver', 'cluster', ['granite', 'gneiss'], preset=NORMAL_METAL_ORE),
    'poor_native_silver': preset_vein('native_silver', 'cluster', ['granite', 'metamorphic'], preset=POOR_METAL_ORE),
    'normal_hematite': preset_vein('hematite', 'cluster', ['igneous_extrusive'], preset=NORMAL_METAL_ORE),
    'deep_hematite': preset_vein('hematite', 'cluster', ['igneous_extrusive'], preset=DEEP_METAL_ORE),
    'normal_cassiterite': preset_vein('cassiterite', 'cluster', ['igneous_intrusive'], 'topaz', 10, ['granite'], preset=NORMAL_METAL_ORE),
    'surface_cassiterite': preset_vein('cassiterite', 'cluster', ['igneous_intrusive'], 'topaz', 20, ['granite'], preset=SURFACE_METAL_ORE, deposits=True),
    'normal_bismuthinite': preset_vein('bismuthinite', 'cluster', ['igneous_intrusive', 'sedimentary'], preset=NORMAL_METAL_ORE),
    'surface_bismuthinite': preset_vein('bismuthinite', 'cluster', ['igneous_intrusive', 'sedimentary'], preset=SURFACE_METAL_ORE),
    'normal_garnierite': preset_vein('garnierite', 'cluster', ['gabbro'], preset=NORMAL_S_METAL_ORE),
    'poor_garnierite': preset_vein('garnierite', 'cluster', ['igneous_intrusive'], preset=POOR_S_METAL_ORE),
    'normal_malachite': preset_vein('malachite', 'cluster', ['marble', 'limestone'], 'gypsum', 10, ['limestone'], preset=NORMAL_METAL_ORE),
    'poor_malachite': preset_vein('malachite', 'cluster', ['marble', 'limestone', 'phyllite', 'chalk', 'dolomite'], 'gypsum', 20, ['limestone'], preset=POOR_METAL_ORE),
    'normal_magnetite': preset_vein('magnetite', 'cluster', ['sedimentary'], preset=NORMAL_METAL_ORE),
    'deep_magnetite': preset_vein('magnetite', 'cluster', ['sedimentary'], preset=DEEP_METAL_ORE),
    'normal_limonite': preset_vein('limonite', 'cluster', ['sedimentary'], 'ruby', 20, ['limestone', 'shale'], preset=NORMAL_METAL_ORE),
    'deep_limonite': preset_vein('limonite', 'cluster', ['sedimentary'], 'ruby', 10, ['limestone', 'shale'], preset=DEEP_METAL_ORE),
    'normal_sphalerite': preset_vein('sphalerite', 'cluster', ['metamorphic'], preset=NORMAL_METAL_ORE),
    'surface_sphalerite': preset_vein('sphalerite', 'cluster', ['metamorphic'], preset=SURFACE_METAL_ORE),
    'normal_tetrahedrite': preset_vein('tetrahedrite', 'cluster', ['metamorphic'], preset=NORMAL_METAL_ORE),
    'surface_tetrahedrite': preset_vein('tetrahedrite', 'cluster', ['metamorphic'], preset=SURFACE_METAL_ORE),

    'bituminous_coal': preset_vein('bituminous_coal', 'cluster', ['sedimentary'], preset=HIGH_MINERAL_ORE),
    'lignite': preset_vein('lignite', 'cluster', ['sedimentary'], preset=DEEP_MINERAL_ORE),
    'kaolinite': preset_vein('kaolinite', 'cluster', ['sedimentary'], preset=HIGH_MINERAL_ORE),
    'graphite': preset_vein('graphite', 'cluster', ['gneiss', 'marble', 'quartzite', 'schist'], preset=DEEP_MINERAL_ORE),
    'cinnabar': preset_vein('cinnabar', 'cluster', ['igneous_extrusive', 'quartzite', 'shale'], 'opal', 10, ['quartzite'], preset=DEEP_MINERAL_ORE),
    'cryolite': preset_vein('cryolite', 'cluster', ['granite'], preset=DEEP_MINERAL_ORE),
    'saltpeter': preset_vein('saltpeter', 'cluster', ['sedimentary'], 'gypsum', 20, ['limestone'], preset=DEEP_MINERAL_ORE),
    'sulfur': preset_vein('sulfur', 'cluster', ['igneous_extrusive'], 'gypsum', 20, ['rhyolite'], preset=HIGH_MINERAL_ORE),
    'sylvite': preset_vein('sylvite', 'cluster', ['shale', 'claystone', 'chert'], preset=HIGH_MINERAL_ORE),
    'borax': preset_vein('borax', 'cluster', ['claystone', 'limestone', 'shale'], preset=HIGH_MINERAL_ORE),
    'gypsum': vein('gypsum', 'disc', 120, 20, 30, 90, 60, 0, 0, 0, ['metamorphic']),
    'lapis_lazuli': preset_vein('lapis_lazuli', 'cluster', ['limestone', 'marble'], preset=DEEP_MINERAL_ORE),
    'halite': vein('halite', 'disc', 120, 30, 30, 90, 80, 0, 0, 0, ['sedimentary']),
    'diamond': vein('diamond', 'pipe', 60, 60, -64, 100, 40, 0, 0, 0, ['gabbro']),
    'emerald': vein('emerald', 'pipe', 80, 60, -64, 100, 40, 0, 0, 0, ['igneous_intrusive']),
    'volcanic_sulfur': vein('sulfur', 'disc', 25, 14, 80, 180, 40, 0, 0, 0, ['igneous_extrusive', 'igneous_intrusive'], biomes='#tfc:is_volcanic', height=6),
    'amethyst': vein('amethyst', 'disc', 14, 8, 40, 60, 20, 0, 0, 0, ['sedimentary', 'metamorphic'], biomes='#tfc:is_river', height=4),
    'opal': vein('opal', 'disc', 14, 8, 40, 60, 20, 0, 0, 0, ['sedimentary', 'igneous_extrusive'], biomes='#tfc:is_river', height=4)
}

ALL_MINERALS = ('bituminous_coal', 'lignite', 'kaolinite', 'graphite', 'cinnabar', 'cryolite', 'saltpeter', 'sulfur', 'sylvite', 'borax', 'gypsum', 'lapis_lazuli', 'halite', 'diamond', 'emerald', 'sulfur', 'amethyst', 'opal')

DEPOSIT_RARES: Dict[str, str] = {
    'granite': 'topaz',
    'diorite': 'emerald',
    'gabbro': 'diamond',
    'shale': 'borax',
    'claystone': 'amethyst',
    'limestone': 'lapis_lazuli',
    'conglomerate':'lignite',
    'dolomite': 'amethyst',
    'chert': 'ruby',
    'chalk': 'sapphire',
    'rhyolite': 'pyrite',
    'basalt': 'pyrite',
    'andesite': 'pyrite',
    'dacite': 'pyrite',
    'quartzite': 'opal',
    'slate': 'pyrite',
    'phyllite': 'pyrite',
    'schist': 'pyrite',
    'gneiss': 'gypsum',
    'marble': 'lapis_lazuli'
}

ROCK_BLOCK_TYPES = ('raw', 'hardened', 'bricks', 'cobble', 'gravel', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks', 'chiseled', 'spike', 'loose', 'pressure_plate', 'button')
ROCK_BLOCKS_IN_JSON = ('raw', 'hardened', 'cobble', 'gravel', 'spike', 'loose')
CUTTABLE_ROCKS = ('raw', 'bricks', 'cobble', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks')
ROCK_SPIKE_PARTS = ('base', 'middle', 'tip')
SAND_BLOCK_TYPES = ('brown', 'white', 'black', 'red', 'yellow', 'green', 'pink')
SANDSTONE_BLOCK_TYPES = ('raw', 'smooth', 'cut')
SOIL_BLOCK_TYPES = ('dirt', 'grass', 'grass_path', 'clay', 'clay_grass', 'farmland', 'rooted_dirt', 'mud', 'mud_bricks', 'drying_bricks')
SOIL_BLOCK_VARIANTS = ('silt', 'loam', 'sandy_loam', 'silty_loam')
ORE_DEPOSITS = ('native_copper', 'cassiterite', 'native_silver', 'native_gold')
GEMS = ('amethyst', 'diamond', 'emerald', 'lapis_lazuli', 'opal', 'pyrite', 'ruby', 'sapphire', 'topaz')
MISC_GROUNDCOVER = ('bone', 'clam', 'driftwood', 'mollusk', 'mussel', 'pinecone', 'seaweed', 'stick', 'dead_grass', 'feather', 'flint', 'guano', 'humus', 'rotten_flesh', 'salt_lick')
COLORS = ('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')
SIMPLE_FLUIDS = ('brine', 'curdled_milk', 'limewater', 'lye', 'milk_vinegar', 'olive_oil', 'olive_oil_water', 'tallow', 'tannin', 'vinegar')
ALCOHOLS = ('beer', 'cider', 'rum', 'sake', 'vodka', 'whiskey', 'corn_whiskey', 'rye_whiskey')
VANILLA_WOODS = ('oak', 'spruce', 'acacia', 'birch', 'jungle', 'dark_oak', 'crimson', 'warped')

WOODS: Dict[str, Wood] = {
    'acacia': Wood(650, 1000),
    'ash': Wood(696, 1250),
    'aspen': Wood(611, 1000),
    'birch': Wood(652, 1750),
    'blackwood': Wood(720, 1750),
    'chestnut': Wood(651, 1500),
    'douglas_fir': Wood(707, 1500),
    'hickory': Wood(762, 2000),
    'kapok': Wood(645, 1000),
    'maple': Wood(745, 2000),
    'oak': Wood(728, 2250),
    'palm': Wood(730, 1250),
    'pine': Wood(627, 1250),
    'rosewood': Wood(640, 1500),
    'sequoia': Wood(612, 1750),
    'spruce': Wood(608, 1500),
    'sycamore': Wood(653, 1750),
    'white_cedar': Wood(625, 1500),
    'willow': Wood(603, 1000)
}

# DO NOT EDIT DIRECTLY - Imported directly from spreadsheet
# https://docs.google.com/spreadsheets/d/1Ghw3dCmVO5Gv0MMGBydUxox_nwLYmmcZkGSbbf0QSAE/
CROPS: Dict[str, Crop] = {
    # Grains
    'barley': Crop('default', 8, 'nitrogen', -8, 26, 70, 310, 18, 75, None, 'edge'),
    'oat': Crop('default', 8, 'phosphorus', 3, 40, 140, 400, 35, 100, None, 'edge'),
    'rye': Crop('default', 8, 'phosphorus', -11, 30, 100, 350, 25, 85, None, 'edge'),
    'maize': Crop('double', 6, 'phosphorus', 13, 40, 300, 500, 75, 100, None, 'edge'),
    'wheat': Crop('default', 8, 'phosphorus', -4, 35, 100, 400, 25, 100, None, 'edge'),
    'rice': Crop('default', 8, 'phosphorus', 15, 30, 100, 500, 25, 100, 'normal', None),
    # Vegetables
    'beet': Crop('default', 6, 'potassium', -5, 20, 70, 300, 18, 85, None, None),
    'cabbage': Crop('default', 6, 'nitrogen', -10, 27, 60, 280, 15, 65, None, None),
    'carrot': Crop('default', 5, 'potassium', 3, 30, 100, 400, 25, 100, None, None),
    'garlic': Crop('default', 5, 'nitrogen', -20, 18, 60, 310, 15, 75, None, None),
    'green_bean': Crop('double_stick', 8, 'nitrogen', 2, 35, 150, 410, 38, 100, 'normal', None),
    'melon': Crop('spreading', 8, 'phosphorus', 5, 37, 200, 500, 75, 100, 'normal', None),
    'potato': Crop('default', 7, 'potassium', -1, 37, 200, 410, 50, 100, None, None),
    'pumpkin': Crop('spreading', 8, 'phosphorus', 0, 30, 120, 390, 30, 80, None, None),
    'onion': Crop('default', 7, 'nitrogen', 0, 30, 100, 390, 25, 90, None, None),
    'soybean': Crop('default', 7, 'nitrogen', 8, 30, 160, 410, 40, 100, 'normal', None),
    'squash': Crop('default', 8, 'potassium', 5, 33, 90, 390, 23, 95, 'normal', None),
    'sugarcane': Crop('double', 8, 'potassium', 12, 38, 160, 500, 40, 100, None, None),
    'tomato': Crop('double_stick', 8, 'potassium', 0, 36, 120, 390, 30, 95, 'normal', None),
    'jute': Crop('double', 6, 'potassium', 5, 37, 100, 410, 25, 100, None, None),
    'papyrus': Crop('double', 6, 'potassium', 19, 37, 310, 500, 70, 100, None, None),
}

PLANTS: Dict[str, Plant] = {
    'athyrium_fern': Plant(True, -10, 14, 270, 500, 'standard'),
    'canna': Plant(True, 10, 40, 270, 500, 'standard'),
    'goldenrod': Plant(True, -16, 6, 75, 310, 'standard'),
    'pampas_grass': Plant(True, 12, 40, 0, 300, 'tall_grass'),
    'perovskia': Plant(True, -6, 12, 0, 270, 'dry'),

    'bluegrass': Plant(False, -4, 12, 110, 280, 'short_grass'),
    'bromegrass': Plant(False, 4, 20, 140, 360, 'short_grass'),
    'fountain_grass': Plant(False, 0, 26, 75, 150, 'short_grass'),
    'manatee_grass': Plant(False, 12, 40, 250, 500, 'grass_water'),
    'orchard_grass': Plant(False, -30, 10, 75, 300, 'short_grass'),
    'ryegrass': Plant(False, -24, 40, 150, 320, 'short_grass'),
    'scutch_grass': Plant(False, 0, 40, 150, 500, 'short_grass'),
    'star_grass': Plant(False, 2, 40, 50, 260, 'grass_water'),
    'timothy_grass': Plant(False, -22, 16, 289, 500, 'short_grass'),
    'raddia_grass': Plant(False, 18, 40, 330, 500, 'short_grass'),

    'allium': Plant(False, -10, -2, 150, 400, 'standard'),
    'anthurium': Plant(False, 12, 40, 290, 500, 'standard'),
    'arrowhead': Plant(False, -10, 22, 180, 500, 'emergent_fresh'),
    'houstonia': Plant(False, -12, 10, 150, 500, 'standard'),
    'badderlocks': Plant(False, -18, 2, 150, 500, 'emergent'),
    'barrel_cactus': Plant(False, 4, 18, 0, 85, 'cactus'),
    'blood_lily': Plant(False, 8, 18, 200, 500, 'standard'),
    'blue_orchid': Plant(False, 10, 40, 250, 390, 'standard'),
    'blue_ginger': Plant(False, 16, 26, 300, 450, 'standard'),
    'cattail': Plant(False, -16, 22, 150, 500, 'emergent_fresh'),
    'calendula': Plant(False, 4, 22, 130, 300, 'standard'),
    'laminaria': Plant(False, -24, -2, 100, 500, 'water'),
    'marigold': Plant(False, -8, 18, 50, 390, 'emergent_fresh'),
    'bur_reed': Plant(False, -16, 4, 250, 400, 'emergent_fresh'),
    'butterfly_milkweed': Plant(False, -16, 18, 75, 300, 'standard'),
    'black_orchid': Plant(False, 14, 40, 290, 410, 'standard'),
    'coontail': Plant(False, 2, 18, 250, 500, 'grass_water_fresh'),
    'dandelion': Plant(False, -22, 40, 120, 400, 'standard'),
    'dead_bush': Plant(False, -12, 40, 0, 120, 'dry'),
    'desert_flame': Plant(False, 0, 20, 40, 170, 'standard'),
    'duckweed': Plant(False, -18, 2, 0, 500, 'floating_fresh'),
    'eel_grass': Plant(False, 6, 40, 200, 500, 'grass_water_fresh'),
    'field_horsetail': Plant(False, -12, 20, 300, 500, 'standard'),
    'foxglove': Plant(False, -8, 16, 150, 300, 'tall_plant'),
    'grape_hyacinth': Plant(False, -10, 10, 150, 250, 'standard'),
    'gutweed': Plant(False, -6, 18, 100, 500, 'water'),
    'heliconia': Plant(False, 14, 40, 320, 500, 'standard'),
    'hibiscus': Plant(False, 10, 24, 260, 450, 'tall_plant'),
    'kangaroo_paw': Plant(False, 14, 40, 100, 300, 'standard'),
    'king_fern': Plant(False, 18, 40, 350, 500, 'tall_plant'),
    'labrador_tea': Plant(False, -18, 0, 200, 380, 'standard'),
    'lady_fern': Plant(False, -10, 8, 200, 500, 'standard'),
    'licorice_fern': Plant(False, 2, 10, 300, 400, 'epiphyte'),
    'lilac': Plant(False, -10, 6, 150, 300, 'tall_plant'),
    'lotus': Plant(False, -4, 18, 0, 500, 'floating_fresh'),
    'maiden_pink': Plant(False, 5, 25, 100, 350, 'standard'),
    'meads_milkweed': Plant(False, -10, 2, 130, 380, 'standard'),
    'milfoil': Plant(False, -14, 22, 250, 500, 'water_fresh'),
    'morning_glory': Plant(False, -11, 19, 300, 500, 'creeping'),
    'moss': Plant(False, -7, 30, 250, 450, 'creeping'),
    'nasturtium': Plant(False, 6, 22, 150, 380, 'standard'),
    'ostrich_fern': Plant(False, -14, 6, 290, 470, 'tall_plant'),
    'oxeye_daisy': Plant(False, -14, 10, 120, 300, 'standard'),
    'phragmite': Plant(False, -6, 18, 50, 250, 'emergent_fresh'),
    'pickerelweed': Plant(False, -14, 16, 200, 500, 'emergent_fresh'),
    'pistia': Plant(False, 6, 26, 0, 400, 'floating_fresh'),
    'poppy': Plant(False, -12, 14, 150, 250, 'standard'),
    'primrose': Plant(False, -8, 10, 150, 300, 'standard'),
    'pulsatilla': Plant(False, -10, 2, 50, 200, 'standard'),
    'red_sealing_wax_palm': Plant(False, 18, 40, 280, 500, 'tall_plant'),
    'reindeer_lichen': Plant(False, -24, -8, 50, 470, 'creeping'),
    'rose': Plant(True, -5, 20, 150, 300, 'tall_plant'),
    'sacred_datura': Plant(False, 4, 18, 75, 150, 'standard'),
    'sagebrush': Plant(False, -10, 14, 0, 120, 'dry'),
    'sago': Plant(False, -18, 18, 200, 500, 'water_fresh'),
    'sapphire_tower': Plant(False, 10, 22, 75, 200, 'tall_plant'),
    'sargassum': Plant(False, -10, 16, 0, 500, 'floating'),
    'guzmania': Plant(False, 20, 40, 290, 480, 'epiphyte'),
    'silver_spurflower': Plant(False, 14, 24, 230, 400, 'standard'),
    'snapdragon_pink': Plant(False, 16, 24, 150, 300, 'standard'),
    'snapdragon_red': Plant(False, 12, 20, 150, 300, 'standard'),
    'snapdragon_white': Plant(False, 8, 16, 150, 300, 'standard'),
    'snapdragon_yellow': Plant(False, 6, 24, 150, 300, 'standard'),
    'strelitzia': Plant(False, 14, 26, 50, 300, 'standard'),
    'switchgrass': Plant(False, -6, 22, 110, 390, 'tall_grass'),
    'sword_fern': Plant(False, -12, 12, 100, 500, 'standard'),
    'tall_fescue_grass': Plant(False, -10, 10, 280, 430, 'tall_grass'),
    'toquilla_palm': Plant(False, 16, 40, 250, 500, 'tall_plant'),
    'trillium': Plant(False, -10, 8, 250, 500, 'standard'),
    'tropical_milkweed': Plant(False, 8, 24, 120, 300, 'standard'),
    'tulip_orange': Plant(False, 2, 10, 200, 400, 'standard'),
    'tulip_pink': Plant(False, -6, 2, 200, 400, 'standard'),
    'tulip_red': Plant(False, 0, 4, 200, 400, 'standard'),
    'tulip_white': Plant(False, -12, -4, 200, 400, 'standard'),
    'turtle_grass': Plant(False, 14, 40, 240, 500, 'grass_water'),
    'vriesea': Plant(False, 14, 40, 200, 400, 'epiphyte'),
    'water_canna': Plant(True, 0, 36, 150, 500, 'floating_fresh'),
    'water_lily': Plant(False, -12, 40, 0, 500, 'floating_fresh'),
    'water_taro': Plant(False, 12, 40, 260, 500, 'emergent_fresh'),
    'yucca': Plant(False, -4, 22, 0, 75, 'dry'),
}

SMALL_FLOWERS = ('allium', 'anthurium', 'black_orchid', 'blood_lily', 'blue_orchid', 'blue_ginger', 'butterfly_milkweed', 'calendula', 'canna', 'dandelion', 'desert_flame', 'goldenrod', 'grape_hyacinth', 'guzmania', 'kangaroo_paw', 'labrador_tea', 'lotus', 'nasturtium', 'oxeye_daisy', 'pistia', 'poppy', 'primrose', 'pulsatilla', 'rose', 'sacred_datura', 'sagebrush', 'sapphire_tower', 'sargassum', 'silver_spurflower', 'snapdragon_red', 'snapdragon_pink', 'snapdragon_white', 'snapdragon_yellow', 'strelitzia', 'trillium', 'tropical_milkweed', 'tulip_orange', 'tulip_red', 'tulip_pink', 'tulip_white', 'vriesea', 'water_lily', 'yucca')

TALL_FLOWERS = ('foxglove', 'hibiscus', 'lilac', 'toquilla_palm', 'marigold')

FLOWERPOT_CROSS_PLANTS = {
    'allium': 'allium_2',
    'anthurium': 'anthurium_0',
    'athyrium_fern': 'single',
    'black_orchid': 'black_orchid_0',
    'blood_lily': 'blood_lily_0',
    'blue_orchid': 'blue_orchid_1',
    'blue_ginger': 'blue_ginger_0',
    'butterfly_milkweed': 'butterfly_milkweed_3',
    'calendula': 'calendula_3',
    'canna': 'canna_3',
    'dandelion': 'dandelion_2',
    'dead_bush': 'dead_bush0',
    'desert_flame': 'desert_flame_0',
    'field_horsetail': 'potted',
    'foxglove': 'item',
    'goldenrod': 'goldenrod_2',
    'grape_hyacinth': 'grape_hyacinth_1',
    'heliconia': 'heliconia_0',
    'houstonia': 'houstonia_1',
    'kangaroo_paw': 'item',
    'labrador_tea': 'labrador_tea_4',
    'lady_fern': 'item',
    'maiden_pink': 'potted',
    'meads_milkweed': 'meads_milkweed_3',
    'nasturtium': 'nasturtium_2',
    'ostrich_fern': 'ostrich_fern_3',
    'oxeye_daisy': 'oxeye_daisy_3',
    'perovskia': 'perovskia_3',
    'poppy': 'poppy_2',
    'primrose': 'primrose',
    'pulsatilla': 'pulsatilla_3',
    'rose': 'classic',
    'sacred_datura': 'sacred_datura_3',
    'sagebrush': 'sagebrush_4',
    'sapphire_tower': 'potted',
    'silver_spurflower': 'silver_spurflower_2',
    'snapdragon_pink': 'snapdragon_pink_1',
    'snapdragon_red': 'snapdragon_red_1',
    'snapdragon_white': 'snapdragon_white_1',
    'snapdragon_yellow': 'snapdragon_yellow_1',
    'strelitzia': 'strelitzia_0',
    'sword_fern': 'potted',
    'toquilla_palm': 'potted',
    'trillium': 'trillium',
    'tropical_milkweed': 'tropical_milkweed_3',
    'tulip_orange': 'tulip_orange_1',
    'tulip_pink': 'tulip_pink_1',
    'tulip_red': 'tulip_red_1',
    'tulip_white': 'tulip_white_1',
    'yucca': 'potted'
}

SIMPLE_TALL_PLANTS = {
    'foxglove': 5
}
MISC_POTTED_PLANTS = ['barrel_cactus', 'morning_glory', 'moss', 'reindeer_lichen', 'rose', 'toquilla_palm', 'tree_fern']

SIMPLE_STAGE_PLANTS: Dict[str, int] = {
    'allium': 8,
    'anthurium': 2,
    'black_orchid': 3,
    'blood_lily': 4,
    'blue_ginger': 2,
    'blue_orchid': 3,
    'butterfly_milkweed': 7,
    'desert_flame': 2,
    'heliconia': 3,
    'houstonia': 3,
    'goldenrod': 5,
    'grape_hyacinth': 4,
    'kangaroo_paw': 2,  # tinted
    'labrador_tea': 7,
    'meads_milkweed': 7,
    'nasturtium': 5,
    'oxeye_daisy': 6,
    'perovskia': 6,
    'poppy': 5,
    'primrose': 3,
    'pulsatilla': 6,
    'sacred_datura': 5,  # different
    'silver_spurflower': 3,
    'strelitzia': 3,
    'trillium': 6,  # different
    'tropical_milkweed': 4,
    'yucca': 4
}

MODEL_PLANTS: List[str] = ['arundo', 'arundo_plant', 'athyrium_fern', 'dry_phragmite', 'dry_phragmite_plant', 'hanging_vines', 'hanging_vines_plant', 'spanish_moss', 'spanish_moss_plant', 'lady_fern', 'laminaria', 'liana', 'liana_plant', 'milfoil', 'sago', 'sword_fern', 'tree_fern', 'tree_fern_plant', 'winged_kelp', 'winged_kelp_plant']
SEAGRASS: List[str] = ['star_grass', 'manatee_grass', 'eel_grass', 'turtle_grass', 'coontail']

UNIQUE_PLANTS: List[str] = ['hanging_vines_plant', 'hanging_vines', 'spanish_moss', 'spanish_moss_plant', 'liana_plant', 'liana', 'tree_fern_plant', 'tree_fern', 'arundo_plant', 'arundo', 'dry_phragmite', 'dry_phragmite_plant', 'winged_kelp_plant', 'winged_kelp', 'leafy_kelp_plant', 'leafy_kelp', 'giant_kelp_plant', 'giant_kelp_flower', 'ivy', 'jungle_vines']
BROWN_COMPOST_PLANTS: List[str] = ['hanging_vines', 'spanish_moss', 'liana', 'tree_fern', 'arundo', 'dry_phragmite', 'ivy', 'jungle_vines']
SEAWEED: List[str] = ['sago', 'gutweed', 'laminaria', 'milfoil']
CORALS: List[str] = ['tube', 'brain', 'bubble', 'fire', 'horn']
CORAL_BLOCKS: List[str] = ['dead_coral', 'dead_coral', 'dead_coral_fan', 'coral_fan', 'dead_coral_wall_fan', 'coral_wall_fan']

PLANT_COLORS: Dict[str, List[str]] = {
    'white': ['houstonia', 'oxeye_daisy', 'primrose', 'snapdragon_white', 'trillium', 'spanish_moss', 'tulip_white'],
    'orange': ['butterfly_milkweed', 'canna', 'nasturtium', 'strelitzia', 'tulip_orange', 'water_canna'],
    'magenta': ['athyrium_fern', 'morning_glory', 'pulsatilla'],
    'light_blue': ['labrador_tea', 'sapphire_tower'],
    'yellow': ['calendula', 'dandelion', 'meads_milkweed', 'goldenrod', 'snapdragon_yellow'],
    'lime': ['moss'],
    'pink': ['foxglove', 'sacred_datura', 'tulip_pink', 'snapdragon_pink'],
    'light_gray': ['yucca'],
    'purple': ['allium', 'black_orchid', 'perovskia'],
    'blue': ['blue_orchid', 'grape_hyacinth'],
    'brown': ['field_horsetail', 'sargassum'],
    'green': ['barrel_cactus', 'reindeer_lichen'],
    'red': ['guzmania', 'poppy', 'rose', 'snapdragon_red', 'tropical_milkweed', 'tulip_red', 'vriesea']
}

COLOR_COMBOS = [
    ('red', 'yellow', 'orange'),
    ('blue', 'white', 'light_blue'),
    ('purple', 'pink', 'magenta'),
    ('red', 'white', 'pink'),
    ('white', 'gray', 'light_gray'),
    ('white', 'black', 'gray'),
    ('green', 'white', 'lime'),
    ('green', 'blue', 'cyan'),
    ('red', 'blue', 'purple'),
    ('yellow', 'blue', 'green')
]

VESSEL_TYPES = {
    'blue': 'a',
    'brown': 'a',
    'gray': 'a',
    'light_gray': 'a',
    'magenta': 'a',
    'orange': 'a',
    'white': 'a',
    'pink': 'b',
    'cyan': 'b',
    'purple': 'b',
    'yellow': 'c',
    'black': 'c',
    'light_blue': 'c',
    'lime': 'c',
    'red': 'c',
    'green': 'd'
}

DISC_COLORS = {
    'yellow': '13',
    'orange': 'blocks',
    'lime': 'cat',
    'red': 'chirp',
    'green': 'far',
    'purple': 'mall',
    'magenta': 'mellohi',
    'cyan': 'otherside',
    'black': 'stal',
    'white': 'strad',
    'light_blue': 'wait',
    'blue': 'ward',
}

SIMPLE_BLOCKS = ('peat', 'aggregate', 'fire_bricks', 'fire_clay_block', 'thatch')
SIMPLE_ITEMS = ('alabaster_brick', 'blank_disc', 'blubber', 'brass_mechanisms', 'burlap_cloth', 'compost', 'daub', 'dirty_jute_net', 'fire_clay', 'firestarter', 'glass_shard', 'glow_arrow', 'glue',
                'jute', 'jute_fiber', 'jute_net', 'mortar', 'olive_paste', 'papyrus', 'papyrus_strip',  'pure_nitrogen', 'pure_phosphorus', 'pure_potassium', 'rotten_compost', 'silk_cloth', 'soaked_papyrus_strip', 'soot', 'spindle',
                'stick_bunch', 'stick_bundle', 'straw', 'unrefined_paper', 'wool', 'wool_cloth', 'wool_yarn', 'wrought_iron_grill')
GENERIC_POWDERS = {
    'charcoal': 'black',
    'coke': 'black',
    'graphite': 'blue',
    'hematite': 'red',
    'kaolinite': 'pink',
    'limonite': 'yellow',
    'malachite': 'green',
    'sylvite': 'orange',
    'lapis_lazuli': 'blue'
}
POWDERS = ('flux', 'salt', 'saltpeter', 'sulfur', 'wood_ash')
VANILLA_DYED_ITEMS = ('wool', 'carpet', 'bed', 'terracotta', 'stained_glass', 'stained_glass_pane', 'banner', 'glazed_terracotta')
SIMPLE_POTTERY = ('bowl', 'fire_brick', 'pot', 'spindle_head', 'vessel')
SIMPLE_UNFIRED_POTTERY = ('brick', 'crucible', 'flower_pot', 'jug', 'pan')
VANILLA_TOOL_MATERIALS = ('netherite', 'diamond', 'iron', 'stone', 'wooden', 'golden')
SHORE_DECORATORS = ('driftwood', 'clam', 'mollusk', 'mussel', 'seaweed', 'sticks_shore', 'guano')
FOREST_DECORATORS = ('sticks_forest', 'pinecone', 'salt_lick', 'dead_grass', 'humus', 'rotten_flesh')
OCEAN_PLANT_TYPES = ('grass_water', 'floating', 'water', 'emergent', 'tall_water')
MISC_PLANT_FEATURES = ('hanging_vines', 'hanging_vines_cave', 'spanish_moss', 'ivy', 'jungle_vines', 'liana', 'moss_cover_patch', 'reindeer_lichen_cover_patch', 'morning_glory_cover_patch', 'tree_fern', 'arundo')
SURFACE_GRASS_FEATURES = ('fountain_', 'orchard_', 'rye', 'scutch_', 'timothy_', 'brome', 'blue', 'raddia_')
UNDERGROUND_FEATURES = ('cave_column', 'cave_spike', 'large_cave_spike', 'water_spring', 'lava_spring', 'calcite', 'mega_calcite', 'icicle', 'underground_loose_rocks', 'underground_guano_patch')

# todo: bush hydration / rainfall separation and proper ranges
# When this gest updated, it needs to be updated in both the book (generate_book.py) and in the climate range (data.py) to use the new hydration and rainfall values
# Alternatively, we ditch rainfall and/or hydration entirely.
BERRIES: Dict[str, Berry] = {
    'blackberry': Berry(7, 24, 200, 500, 'spreading', 'none', 'edge'),
    'raspberry': Berry(5, 25, 200, 500, 'spreading', 'none', 'edge'),
    'blueberry': Berry(7, 29, 100, 400, 'spreading', 'none', 'edge'),
    'elderberry': Berry(10, 33, 100, 400, 'spreading', 'none', 'edge'),
    'bunchberry': Berry(15, 35, 200, 500, 'stationary', 'edge', 'old_growth'),
    'gooseberry': Berry(5, 27, 200, 500, 'stationary', 'edge', 'old_growth'),
    'snowberry': Berry(-7, 18, 200, 500, 'stationary', 'edge', 'old_growth'),
    'cloudberry': Berry(-2, 17, 80, 380, 'stationary', 'edge', 'old_growth'),
    'strawberry': Berry(5, 28, 100, 400, 'stationary', 'edge', 'old_growth'),
    'wintergreen_berry': Berry(-6, 17, 100, 400, 'stationary', 'edge', 'old_growth'),
    'cranberry': Berry(-5, 17, 250, 500, 'waterlogged', 'edge', 'old_growth')
}

FRUITS: Dict[str, Fruit] = {
    'banana': Fruit(17, 35, 280, 500),
    'cherry': Fruit(5, 25, 100, 350),
    'green_apple': Fruit(1, 25, 110, 280),
    'lemon': Fruit(10, 30, 180, 470),
    'olive': Fruit(5, 30, 150, 500),
    'orange': Fruit(15, 36, 250, 500),
    'peach': Fruit(4, 27, 60, 230),
    'plum': Fruit(15, 31, 250, 400),
    'red_apple': Fruit(1, 25, 100, 280)
}
NORMAL_FRUIT_TREES: List[str] = [k for k in FRUITS.keys() if k != 'banana']

GRAINS = ('barley', 'maize', 'oat', 'rice', 'rye', 'wheat')
GRAIN_SUFFIXES = ('', '_grain', '_flour', '_dough', '_bread')
MISC_FOODS = ('beet', 'cabbage', 'carrot', 'garlic', 'green_bean', 'green_bell_pepper', 'onion', 'potato', 'red_bell_pepper', 'soybean', 'squash', 'tomato', 'yellow_bell_pepper', 'cheese', 'cooked_egg', 'boiled_egg', 'dried_seaweed', 'dried_kelp', 'cattail_root', 'taro_root', 'sugarcane', 'cooked_rice')
MEATS = ('beef', 'pork', 'chicken', 'quail', 'mutton', 'bear', 'horse_meat', 'pheasant', 'turkey', 'grouse', 'venison', 'wolf', 'rabbit', 'hyena', 'duck', 'chevon', 'gran_feline', 'camelidae', 'cod', 'bluegill', 'salmon', 'tropical_fish', 'turtle', 'calamari', 'shellfish')
NUTRIENTS = ('grain', 'fruit', 'vegetables', 'protein', 'dairy')

SPAWN_EGG_ENTITIES = ('isopod', 'lobster', 'crayfish', 'cod', 'pufferfish', 'tropical_fish', 'jellyfish', 'orca', 'dolphin', 'salmon', 'bluegill', 'manatee', 'penguin', 'turtle', 'horseshoe_crab', 'polar_bear', 'grizzly_bear', 'black_bear', 'cougar', 'panther', 'lion', 'sabertooth', 'squid', 'octopoteuthis', 'pig', 'cow', 'goat', 'yak', 'alpaca', 'musk_ox', 'sheep', 'chicken', 'duck', 'quail', 'rabbit', 'fox', 'boar', 'donkey', 'mule', 'horse', 'deer', 'moose', 'boar', 'rat', 'cat', 'dog', 'wolf', 'panda', 'grouse', 'pheasant', 'turkey', 'ocelot', 'direwolf')
BUCKETABLE_FISH = ('cod', 'pufferfish', 'tropical_fish', 'jellyfish', 'salmon', 'bluegill')
LAND_PREDATORS = ('polar_bear', 'grizzly_bear', 'black_bear', 'cougar', 'panther', 'lion', 'sabertooth', 'wolf', 'direwolf', 'ocelot')
OCEAN_PREDATORS = ('dolphin', 'orca')
OCEAN_PREY = ('isopod', 'lobster', 'crayfish', 'cod', 'tropical_fish', 'horseshoe_crab')
LIVESTOCK = ('pig', 'cow', 'goat', 'yak', 'alpaca', 'sheep', 'musk_ox', 'chicken', 'duck', 'quail', 'horse', 'mule', 'donkey')
LAND_PREY = ('rabbit', 'fox', 'boar', 'turtle', 'penguin', 'deer', 'panda', 'moose', 'grouse', 'pheasant', 'turkey', 'ocelot')

BLOCK_ENTITIES = ('log_pile', 'burning_log_pile', 'placed_item', 'pit_kiln', 'charcoal_forge', 'quern', 'scraping', 'crucible', 'bellows', 'composter', 'chest', 'trapped_chest', 'barrel', 'loom', 'sluice', 'tool_rack', 'sign', 'lamp', 'berry_bush', 'crop', 'firepit', 'pot', 'grill', 'pile', 'farmland', 'tick_counter', 'nest_box', 'bloomery', 'bloom', 'anvil', 'ingot_pile', 'sheet_pile', 'blast_furnace', 'large_vessel', 'powderkeg')
TANNIN_WOOD_TYPES = ('oak', 'birch', 'chestnut', 'douglas_fir', 'hickory', 'maple', 'sequoia')

def spawner(entity: str, weight: int = 1, min_count: int = 1, max_count: int = 4) -> Dict[str, Any]:
    return {
        'type': entity,
        'weight': weight,
        'minCount': min_count,
        'maxCount': max_count
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
    'salmon': spawner('tfc:salmon', min_count=2, max_count=6, weight=10),
    'bluegill': spawner('tfc:bluegill', min_count=2, max_count=4, weight=10),
    'crayfish': spawner('tfc:crayfish', min_count=1, max_count=4, weight=3)
}

LAKE_CREATURES: Dict[str, Dict[str, Any]] = {
    'manatee': spawner('tfc:manatee', min_count=1, max_count=2)
}

SHORE_CREATURES: Dict[str, Dict[str, Any]] = {
    'penguin': spawner('tfc:penguin', min_count=2, max_count=5, weight=10),
    'turtle': spawner('tfc:turtle', min_count=2, max_count=5, weight=10)
}

LAND_CREATURES: Dict[str, Dict[str, Any]] = {
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
    'rabbit': spawner('tfc:rabbit', min_count=1, max_count=4, weight=3),
    'fox': spawner('tfc:fox', min_count=1, max_count=1),
    'panda': spawner('tfc:panda', min_count=3, max_count=5),
    'boar': spawner('tfc:boar', min_count=1, max_count=2, weight=2),
    'deer': spawner('tfc:deer', min_count=2, max_count=4, weight=3),
    'moose': spawner('tfc:deer', min_count=1, max_count=1),
    'grouse': spawner('tfc:grouse', min_count=2, max_count=4),
    'pheasant': spawner('tfc:pheasant', min_count=2, max_count=4),
    'turkey': spawner('tfc:turkey', min_count=2, max_count=4),
    'wolf': spawner('tfc:wolf', min_count=6, max_count=9),
    'direwolf': spawner('tfc:wolf', min_count=3, max_count=7),
    'donkey': spawner('tfc:donkey', min_count=1, max_count=3),
    'horse': spawner('tfc:horse', min_count=1, max_count=3),
    'ocelot': spawner('tfc:ocelot', min_count=1, max_count=3),
}

VANILLA_MONSTERS: Dict[str, Dict[str, Any]] = {
    'spider': spawner('minecraft:spider', weight=100, min_count=4, max_count=4),
    'zombie': spawner('minecraft:zombie', weight=95, min_count=4, max_count=4),
    'skeleton': spawner('minecraft:skeleton', weight=100, min_count=4, max_count=4),
    'creeper': spawner('minecraft:creeper', weight=100, min_count=4, max_count=4),
    'slime': spawner('minecraft:slime', weight=100, min_count=4, max_count=4),
}

DISABLED_VANILLA_RECIPES = ('flint_and_steel', 'turtle_helmet', 'campfire', 'bucket', 'composter', 'tinted_glass', 'enchanting_table', 'bowl', 'blaze_rod', 'bone_meal', 'flower_pot', 'painting', 'torch', 'soul_torch', 'sticky_piston', 'clock', 'compass', 'white_wool_from_string', 'hay_block', 'anvil', 'wheat', 'lapis_lazuli', 'leather_horse_armor', 'map', 'furnace', 'jack_o_lantern', 'melon_seeds', 'melon', 'pumpkin_pie', 'chest', 'barrel', 'trapped_chest', 'bricks', 'bookshelf', 'crafting_table', 'lectern', 'chest_minecart', 'rail', 'beetroot_soup', 'mushroom_stew', 'rabbit_stew_from_red_mushroom', 'rabbit_stew_from_brown_mushroom', 'suspicious_stew', 'scaffolding', 'bow', 'glass_bottle', 'fletching_table', 'shield', 'lightning_rod', 'fishing_rod', 'iron_door', 'iron_trapdoor')
ARMOR_SECTIONS = ('chestplate', 'leggings', 'boots', 'helmet')
TFC_ARMOR_SECTIONS = ('helmet', 'chestplate', 'greaves', 'boots')
VANILLA_ARMOR_TYPES = ('leather', 'golden', 'iron', 'diamond', 'netherite')
VANILLA_TOOLS = ('sword', 'shovel', 'pickaxe', 'axe', 'hoe')
MOB_ARMOR_METALS = ('copper', 'bronze', 'black_bronze', 'bismuth_bronze', 'wrought_iron')
MOB_TOOLS = ('axe', 'sword', 'javelin', 'mace', 'scythe')
STONE_MOB_TOOLS = ('axe', 'javelin')
TFC_BIOMES = ('badlands', 'inverted_badlands', 'canyons', 'low_canyons', 'plains', 'plateau', 'hills', 'rolling_hills', 'lake', 'lowlands', 'mountains', 'volcanic_mountains', 'old_mountains', 'oceanic_mountains', 'volcanic_oceanic_mountains', 'ocean', 'ocean_reef', 'deep_ocean', 'deep_ocean_trench', 'river', 'shore', 'mountain_river', 'volcanic_mountain_river', 'old_mountain_river', 'oceanic_mountain_river', 'volcanic_oceanic_mountain_river', 'mountain_lake', 'volcanic_mountain_lake', 'old_mountain_lake', 'oceanic_mountain_lake', 'volcanic_oceanic_mountain_lake', 'plateau_lake')

ALLOYS: Dict[str, Tuple[Tuple[str, float, float], ...]] = {
    'bismuth_bronze': (('zinc', 0.2, 0.3), ('copper', 0.5, 0.65), ('bismuth', 0.1, 0.2)),
    'black_bronze': (('copper', 0.5, 0.7), ('silver', 0.1, 0.25), ('gold', 0.1, 0.25)),
    'bronze': (('copper', 0.88, 0.92), ('tin', 0.08, 0.12)),
    'brass': (('copper', 0.88, 0.92), ('zinc', 0.08, 0.12)),
    'rose_gold': (('copper', 0.15, 0.3), ('gold', 0.7, 0.85)),
    'sterling_silver': (('copper', 0.2, 0.4), ('silver', 0.6, 0.8)),
    'weak_steel': (('steel', 0.5, 0.7), ('nickel', 0.15, 0.25), ('black_bronze', 0.15, 0.25)),
    'weak_blue_steel': (('black_steel', 0.5, 0.55), ('steel', 0.2, 0.25), ('bismuth_bronze', 0.1, 0.15), ('sterling_silver', 0.1, 0.15)),
    'weak_red_steel': (('black_steel', 0.5, 0.55), ('steel', 0.2, 0.25), ('brass', 0.1, 0.15), ('rose_gold', 0.1, 0.15))
}

# This is here because it's used all over, and it's easier to import with all constants
def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()


def lang_enum(name: str, values: Sequence[str]) -> Dict[str, str]:
    return dict(('tfc.enum.%s.%s' % (name, value), lang(value)) for value in values)


# This is here as it's used only once in a generic lang call by generate_resources.py
DEFAULT_LANG = {
    # Misc
    'generator.tfc.tng': 'TerraFirmaCraft',
    'death.attack.tfc.grill': '%1$s grilled themself to death',
    'death.attack.tfc.grill.player': '%1$s grilled themselves while trying to escape %2$s',
    'death.attack.tfc.pot': '%1$s boiled themselves into soup',
    'death.attack.tfc.pot.player': '%1$s boiled themself while trying to escape %2$s',
    'death.attack.tfc.dehydration': '%1$s dehydrated to death',
    'death.attack.tfc.dehydration.player': '%1$s dehydrated to death while trying to escape %2$s',
    'death.attack.tfc.coral': '%1$s impaled themself on a coral reef.',
    'death.attack.tfc.coral.player': '%1$s impaled themself on a coral reef while trying to escape %2$s',
    'death.attack.tfc.pluck': '%1$s was plucked to death.',
    'death.attack.tfc.pluck.player': '%1$s was plucked to death by %2$s, which is surprising, because people don\'t typically grow feathers.',
    'effect.tfc.pinned': 'Pinned',
    'effect.tfc.ink': 'Ink',
    'effect.tfc.glow_ink': 'Glowing Ink',
    'effect.tfc.overburdened': 'Overburdened',
    'effect.tfc.thirst': 'Thirst',
    'effect.tfc.exhausted': 'Exhausted',
    'item.minecraft.glow_ink_sac': 'Glowing Ink Sac',
    'item.minecraft.shield': 'Wooden Shield',
    'block.minecraft.bell': 'Golden Bell',
    **dict(('item.minecraft.shield.%s' % color, '%s Wooden Shield' % lang(color)) for color in COLORS),
    'tfc.key.place_block': 'Place Block',
    'tfc.key.cycle_chisel_mode': 'Cycle Chisel Mode',
    'tfc.key.stack_food': 'Stack Food',
    # Sounds
    'subtitles.block.tfc.crop.stick_add': 'Stick placed in farmland',
    'subtitles.block.tfc.bloomery.crackle': 'Bloomery crackles',
    'subtitles.block.tfc.quern.drag': 'Quern grinding',
    'subtitles.block.tfc.loom.weave': 'Loom clacking',
    'subtitles.block.tfc.bellows.blow': 'Air whooshing',
    'subtitles.block.tfc.tool_rack.place_item': 'Item placed on Tool Rack',
    'subtitles.block.tfc.wattle.dyed': 'Wattle stained',
    'subtitles.block.tfc.wattle.daubed': 'Wattle daubed',
    'subtitles.block.tfc.wattle.woven': 'Wattle woven',
    'subtitles.block.tfc.scribing_table.rename_item': 'Player scribbling',
    'subtitles.block.tfc.barrel.opened': 'Barrel opened',
    'subtitles.block.tfc.barrel.closed': 'Barrel closed',
    'subtitles.block.tfc.vessel.opened': 'Vessel opened',
    'subtitles.block.tfc.vessel.closed': 'Vessel closed',
    'subtitles.block.tfc.anvil.hit': 'Anvil clangs',
    'subtitles.block.tfc.barrel.drip': 'Barrel leaks water',
    'subtitles.item.tfc.fertilizer.use': 'Fertilizer spread',
    'subtitles.item.tfc.pan.use': 'Pan sifting',
    'subtitles.item.tfc.ceramic.break': 'Ceramics shattering',
    'subtitles.item.tfc.jug.blow': 'Jug whistles',
    'subtitles.item.tfc.knapping.clay': 'Clay squishes',
    'subtitles.item.tfc.knapping.leather': 'Leather scrapes',
    'subtitles.item.tfc.knapping.rock': 'Rock clacks',
    'subtitles.item.tfc.javelin.hit': 'Javelin stabs',
    'subtitles.item.tfc.javelin.hit_ground': 'Javelin vibrates',
    'subtitles.item.tfc.javelin.throw': 'Javelin clangs',
    **dict(('subtitles.item.armor.equip_%s' % metal, '%s armor equips' % lang(metal)) for metal, data in METALS.items() if 'armor' in data.types),
    'subtitles.item.tfc.firestarter.use': 'Firestarter scratches',
    'subtitles.entity.tfc.alpaca.ambient': 'Alpaca bleats',
    'subtitles.entity.tfc.alpaca.hurt': 'Alpaca yelps',
    'subtitles.entity.tfc.alpaca.death': 'Alpaca dies',
    'subtitles.entity.tfc.yak.ambient': 'Yak grumbles',
    'subtitles.entity.tfc.yak.hurt': 'Yak groans',
    'subtitles.entity.tfc.yak.death': 'Yak dies',
    'subtitles.entity.tfc.musk_ox.ambient': 'Musk Ox pants',
    'subtitles.entity.tfc.musk_ox.hurt': 'Musk Ox bellows',
    'subtitles.entity.tfc.musk_ox.death': 'Musk Ox dies',
    'subtitles.entity.tfc.duck.ambient': 'Duck quacks',
    'subtitles.entity.tfc.duck.hurt': 'Duck quacks angrily',
    'subtitles.entity.tfc.duck.death': 'Duck dies',
    'subtitles.entity.tfc.penguin.ambient': 'Penguin quacks',
    'subtitles.entity.tfc.penguin.hurt': 'Penguin quacks angrily',
    'subtitles.entity.tfc.penguin.death': 'Penguin dies',
    'subtitles.entity.tfc.quail.ambient': 'Quail calls',
    'subtitles.entity.tfc.quail.hurt': 'Quail yelps',
    'subtitles.entity.tfc.quail.death': 'Quail dies',
    'subtitles.entity.tfc.bear.ambient': 'Bear groans',
    'subtitles.entity.tfc.bear.attack': 'Bear roars',
    'subtitles.entity.tfc.bear.hurt': 'Bear hurts',
    'subtitles.entity.tfc.bear.death': 'Bear dies',
    'subtitles.entity.tfc.bear.sleep': 'Bear snores',
    'subtitles.entity.tfc.cougar.death': 'Cougar dies',
    'subtitles.entity.tfc.cougar.attack': 'Cougar roars',
    'subtitles.entity.tfc.cougar.ambient': 'Cougar screams',
    'subtitles.entity.tfc.cougar.hurt': 'Cougar yowls',
    'subtitles.entity.tfc.cougar.sleep': 'Cougar snores',
    'subtitles.entity.tfc.lion.death': 'Lion dies',
    'subtitles.entity.tfc.lion.attack': 'Lion roars',
    'subtitles.entity.tfc.lion.ambient': 'Lion grunts',
    'subtitles.entity.tfc.lion.hurt': 'Lion roars',
    'subtitles.entity.tfc.lion.sleep': 'Lion snores',
    'subtitles.entity.tfc.sabertooth.death': 'Sabertooth dies',
    'subtitles.entity.tfc.sabertooth.attack': 'Sabertooth roars',
    'subtitles.entity.tfc.sabertooth.ambient': 'Sabertooth calls',
    'subtitles.entity.tfc.sabertooth.hurt': 'Sabertooth yowls',
    'subtitles.entity.tfc.sabertooth.sleep': 'Sabertooth snores',
    'subtitles.entity.tfc.deer.death': 'Deer dies',
    'subtitles.entity.tfc.deer.ambient': 'Deer brays',
    'subtitles.entity.tfc.deer.hurt': 'Deer yelps',
    'subtitles.entity.tfc.deer.step': 'Deer walks',
    'subtitles.entity.tfc.moose.death': 'Moose dies',
    'subtitles.entity.tfc.moose.ambient': 'Moose brays',
    'subtitles.entity.tfc.moose.hurt': 'Moose yelps',
    'subtitles.entity.tfc.moose.step': 'Moose walks',
    'subtitles.entity.tfc.grouse.death': 'Grouse dies',
    'subtitles.entity.tfc.grouse.ambient': 'Grouse calls',
    'subtitles.entity.tfc.grouse.hurt': 'Grouse squeals',
    'subtitles.entity.tfc.grouse.step': 'Grouse walks',
    'subtitles.entity.tfc.pheasant.chick.ambient': 'Chick chirps',
    'subtitles.entity.tfc.pheasant.hurt': 'Pheasant crows',
    'subtitles.entity.tfc.pheasant.death': 'Pheasant dies',
    'subtitles.entity.tfc.pheasant.ambient': 'Pheasant calls',
    'subtitles.entity.tfc.pheasant.step': 'Pheasant walks',
    'subtitles.entity.tfc.turkey.death': 'Turkey dies',
    'subtitles.entity.tfc.turkey.ambient': 'Turkey gobbles',
    'subtitles.entity.tfc.turkey.hurt': 'Turkey yelps',
    'subtitles.entity.tfc.turkey.step': 'Turkey walks',
    'subtitles.entity.tfc.rat.death': 'Rat dies',
    'subtitles.entity.tfc.rat.ambient': 'Rat squeaks',
    'subtitles.entity.tfc.rat.hurt': 'Rat squeals',
    'subtitles.entity.tfc.rat.step': 'Rat patters',
    'subtitles.entity.tfc.rooster.cry': 'Rooster calls',
    'subtitles.entity.tfc.bluegill.ambient': 'Bluegill splashes',
    'subtitles.entity.tfc.bluegill.flop': 'Bluegill flops',
    'subtitles.entity.tfc.bluegill.death': 'Bluegill dies',
    'subtitles.entity.tfc.bluegill.hurt': 'Bluegill hurts',
    'subtitles.entity.tfc.manatee.ambient': 'Manatee splashes',
    'subtitles.entity.tfc.manatee.flop': 'Manatee flops',
    'subtitles.entity.tfc.manatee.death': 'Manatee dies',
    'subtitles.entity.tfc.manatee.hurt': 'Manatee hurts',
    'subtitles.entity.tfc.jellyfish.ambient': 'Jellyfish splashes',
    'subtitles.entity.tfc.jellyfish.flop': 'Jellyfish flops',
    'subtitles.entity.tfc.jellyfish.death': 'Jellyfish dies',
    'subtitles.entity.tfc.jellyfish.hurt': 'Jellyfish hurts',
    'subtitles.generic.tfc.dirt_slide': 'Soil landslides',
    'subtitles.generic.tfc.rock_slide_long': 'Rock collapses',
    'subtitles.generic.tfc.rock_slide_long_fake': 'Rock creaks',
    'subtitles.generic.tfc.rock_slide_short': 'Rock crumbles',
    'subtitles.generic.tfc.rock_smash': 'Rock smashes',

    # Item groups
    'itemGroup.tfc.earth': 'TFC Earth',
    'itemGroup.tfc.ores': 'TFC Ores',
    'itemGroup.tfc.rock': 'TFC Rock Stuffs',
    'itemGroup.tfc.metals': 'TFC Metal Stuffs',
    'itemGroup.tfc.wood': 'TFC Wooden Stuffs',
    'itemGroup.tfc.flora': 'TFC Flora',
    'itemGroup.tfc.devices': 'TFC Devices',
    'itemGroup.tfc.food': 'TFC Food',
    'itemGroup.tfc.misc': 'TFC Misc',
    'itemGroup.tfc.decorations': 'TFC Decorations',
    # Containers
    'tfc.screen.calendar': 'Calendar',
    'tfc.screen.nutrition': 'Nutrition',
    'tfc.screen.climate': 'Climate',
    'tfc.screen.rock_knapping': 'Rock Knapping',
    'tfc.screen.clay_knapping': 'Clay Knapping',
    'tfc.screen.fire_clay_knapping': 'Fire Clay Knapping',
    'tfc.screen.leather_knapping': 'Leather Knapping',
    'tfc.screen.scribing_table': 'Rename Items',
    'tfc.screen.pet_command': 'Pet Commands',
    # Tooltips
    'tfc.tooltip.forging': '§f - Can Work',
    'tfc.tooltip.welding': '§f - Can Weld',
    'tfc.tooltip.danger': '§f - Danger!!',
    'tfc.tooltip.anvil_plan': 'Plans',
    'tfc.tooltip.calendar_days_years': '%d, %04d',
    'tfc.tooltip.calendar_hour_minute_month_day_year': '%s %s %d, %04d',
    'tfc.tooltip.calendar_season': 'Season : %s',
    'tfc.tooltip.calendar_day': 'Day : %s',
    'tfc.tooltip.calendar_birthday': '%s\'s Birthday!',
    'tfc.tooltip.calendar_date': 'Date : %s',
    'tfc.tooltip.climate_plate_tectonics_classification': 'Region: %s',
    'tfc.tooltip.climate_koppen_climate_classification': 'Climate: %s',
    'tfc.tooltip.climate_average_temperature': 'Avg. Temp: %s\u00b0C',
    'tfc.tooltip.climate_annual_rainfall': 'Annual Rainfall: %smm',
    'tfc.tooltip.climate_current_temp': 'Current Temp: %s\u00b0C',
    'tfc.tooltip.f3_rainfall': 'Rainfall: %s',
    'tfc.tooltip.f3_average_temperature': 'Avg. Temp: %s\u00b0C',
    'tfc.tooltip.f3_temperature': 'Actual. Temp: %s\u00b0C',
    'tfc.tooltip.f3_forest_type': 'Forest Type: ',
    'tfc.tooltip.f3_forest_properties': 'Forest Density = %s, Weirdness = %s',
    'tfc.tooltip.f3_invalid_chunk_data': 'Invalid Chunk Data',
    'tfc.tooltip.food_expiry_date': 'Expires on: %s',
    'tfc.tooltip.food_expiry_left': 'Expires in: %s',
    'tfc.tooltip.food_expiry_date_and_left': 'Expires on: %s (in %s)',
    'tfc.tooltip.food_infinite_expiry': 'Never expires',
    'tfc.tooltip.food_rotten': 'Rotten!',
    'tfc.tooltip.food_rotten_special': 'Ewwww, are you really thinking of eating that? It looks disgusting',
    'tfc.tooltip.nutrition': 'Nutrition:',
    'tfc.tooltip.nutrition_saturation': ' - Saturation: %s%%',
    'tfc.tooltip.nutrition_water': ' - Water: %s%%',
    'tfc.tooltip.nutrition_none': '- None!',
    'tfc.tooltip.hold_shift_for_nutrition_info': 'Hold (Shift) for Nutrition Info',
    'tfc.tooltip.salad': 'Salad',
    'tfc.tooltip.contents': 'Contents:',
    'tfc.tooltip.propick.found_very_large': 'Found a very large sample of %s',
    'tfc.tooltip.propick.found_large': 'Found a large sample of %s',
    'tfc.tooltip.propick.found_medium': 'Found a medium sample of %s',
    'tfc.tooltip.propick.found_small': 'Found a small sample of %s',
    'tfc.tooltip.propick.found_traces': 'Found traces of %s',
    'tfc.tooltip.propick.found': 'Found %s',
    'tfc.tooltip.propick.nothing': 'Found nothing.',
    'tfc.tooltip.propick.accuracy': 'Accuracy: %s%%',
    'tfc.tooltip.pan.contents': '§7Contains ',
    'tfc.tooltip.pan.water': 'You need to stand in water to be able to pan.',
    'tfc.tooltip.small_vessel.inventory_too_hot': 'Too hot to open!',
    'tfc.tooltip.small_vessel.alloy_solid': 'Contents have solidified!',
    'tfc.tooltip.small_vessel.alloy_molten': 'Contents are still liquid!',
    'tfc.tooltip.small_vessel.contents': 'Contents:',
    'tfc.tooltip.small_vessel.solid': ' - Solid.',
    'tfc.tooltip.small_vessel.molten': ' - Molten!',
    'tfc.tooltip.small_vessel.still_has_unmelted_items': 'Contains un-melted items!',
    'tfc.tooltip.mold.fluid_incompatible': 'This metal can\'t go in the mold!',
    'tfc.tooltip.food_trait.salted': 'Salted',
    'tfc.tooltip.food_trait.brined': 'Brined',
    'tfc.tooltip.food_trait.pickled': 'Pickled',
    'tfc.tooltip.food_trait.preserved': 'Preserved',
    'tfc.tooltip.food_trait.vinegar': 'Preserved in Vinegar',
    'tfc.tooltip.food_trait.charcoal_grilled': 'Charcoal Grilled',
    'tfc.tooltip.food_trait.wood_grilled': 'Wood Grilled',
    'tfc.tooltip.food_trait.wild': 'Wild',
    'tfc.tooltip.food_trait.burnt_to_a_crisp': 'Burnt to a crisp!',
    'tfc.tooltip.item_melts_into': '§7Melts into %s mB of §f%s§7 (at %s§7)',
    'tfc.tooltip.fuel_burns_at': '§7Burns at §f%s§7 for §f%s',
    'tfc.tooltip.time_delta_hours_minutes': '%s:%s',
    'tfc.tooltip.time_delta_days': '%s day(s)',
    'tfc.tooltip.time_delta_months_days': '%s month(s) and %s day(s)',
    'tfc.tooltip.time_delta_years_months_days': '%s year(s), %s month(s) and %s day(s)',
    'tfc.tooltip.temperature_celsius': '%s\u00b0C',
    'tfc.tooltip.temperature_fahrenheit': '%s\u00b0F',
    'tfc.tooltip.temperature_rankine': '%s\u00b0R',
    'tfc.tooltip.temperature_kelvin': '%s K',
    'tfc.tooltip.fluid_units': '%s mB',
    'tfc.tooltip.fluid_units_of': '%s mB of %s',
    'tfc.tooltip.fluid_units_and_capacity': '%s / %s mB',
    'tfc.tooltip.fluid_units_and_capacity_of': '%s / %s mB of %s',
    'tfc.tooltip.less_than_one_fluid_units': '< 1 mB',
    'tfc.tooltip.farmland.mature': '§aMature',
    'tfc.tooltip.farmland.hydration': '§1Hydration: §r%s%%',
    'tfc.tooltip.farmland.hydration_too_low': ' - §4Too low! §r(>%s%%)',
    'tfc.tooltip.farmland.hydration_too_high': ' - §4Too high! §r(<%s%%)',
    'tfc.tooltip.farmland.temperature': '§4Temperature: §r%s\u00b0C',
    'tfc.tooltip.farmland.temperature_too_low': ' - §4Too low! §r(>%s\u00b0C)',
    'tfc.tooltip.farmland.temperature_too_high': ' - §4Too high! §r(<%s\u00b0C)',
    'tfc.tooltip.farmland.just_right': ' - §2Good§r',
    'tfc.tooltip.farmland.nutrients': '§b(N) Nitrogen: §r%s%%, §6(P) Phosphorus: §r%s%%, §d(K) Potassium: §r%s%%',
    'tfc.tooltip.fruit_tree.done_growing': 'This block is done growing',
    'tfc.tooltip.fruit_tree.growing': 'This block could grow under the right conditions.',
    'tfc.tooltip.fruit_tree.sapling_wrong_month': 'Wrong season to grow a tree.',
    'tfc.tooltip.fruit_tree.sapling_splice': 'May be spliced',
    'tfc.tooltip.fertilizer.nitrogen': '§b(N) Nitrogen: §r%s%%',
    'tfc.tooltip.fertilizer.phosphorus': '§6(P) Phosphorus: §r%s%%',
    'tfc.tooltip.fertilizer.potassium': '§d(K) Potassium: §r%s%%',
    'tfc.tooltip.seal_barrel': 'Seal',
    'tfc.tooltip.unseal_barrel': 'Unseal',
    'tfc.tooltip.while_sealed': 'While sealed',
    'tfc.tooltip.while_sealed_description': 'While the barrel is sealed and the required fluid is present',
    'tfc.tooltip.anvil_is_too_low_tier_to_weld': 'The Anvil is not a high enough tier to weld that!',
    'tfc.tooltip.anvil_is_too_low_tier_to_work': 'The Anvil is not a high enough tier to work that!',
    'tfc.tooltip.not_hot_enough_to_weld': 'Not hot enough to weld!',
    'tfc.tooltip.not_hot_enough_to_work': 'Not hot enough to work!',
    'tfc.tooltip.no_flux_to_weld': 'There is no flux in the anvil!',
    'tfc.tooltip.hammer_required_to_work': 'A hammer is required to work in the anvil!',
    'tfc.tooltip.anvil_has_been_worked': 'Worked',
    'tfc.tooltip.blast_furnace_ore': 'Input: %d / %d',
    'tfc.tooltip.blast_furnace_fuel': 'Fuel: %d / %d',
    'tfc.tooltip.crucible_content_line': '  %s (§2%s%%§r)',
    'tfc.tooltip.fertilized': '§6Fertilized',
    'tfc.tooltip.egg_hatch': 'Will hatch in %s days',
    'tfc.tooltip.egg_hatch_today': 'Will hatch today!',
    'tfc.tooltip.fishing.bait': '§6Bait: ',
    'tfc.tooltip.animal.pregnant': 'This %s is pregnant!',
    'tfc.tooltip.animal.male_milk': 'This %s is a male.',
    'tfc.tooltip.animal.old': 'This %s is too old to produce.',
    'tfc.tooltip.animal.young': 'This %s is too young to produce.',
    'tfc.tooltip.animal.low_familiarity': 'This %s is not familiar enough to produce.',
    'tfc.tooltip.animal.no_milk': 'This %s has no milk.',
    'tfc.tooltip.animal.no_wool': 'This %s has no wool.',
    'tfc.tooltip.animal.horse_angry_overburdened': 'The horse kicked you off for putting too much weight on it!',
    'tfc.tooltip.scribing_table.missingink': 'Ink is missing!',
    'tfc.tooltip.scribing_table.invalidink': 'Item isn\'t ink!',
    'tfc.tooltip.deals_damage.slashing': '§7Deals §fSlashing§r Damage',
    'tfc.tooltip.deals_damage.piercing': '§7Deals §fPiercing§r Damage',
    'tfc.tooltip.deals_damage.crushing': '§7Deals §fCrushing§r Damage',
    'tfc.tooltip.resists_damage': '§7Resistances: §fSlashing§r %s, §fPiercing§r %s, §fCrushing§r %s',
    'tfc.tooltip.immune_to_damage': 'Immune',
    'tfc.tooltip.pot_boiling': 'Boiling!',
    'tfc.tooltip.pot_finished': 'Finished',
    'tfc.tooltip.pot_ready': 'Ready',
    'tfc.tooltip.infestation': 'This container has a foul smell.',
    'tfc.tooltip.knapping.knife_needed': 'A knife is needed to knap this item.',

    'tfc.jade.sealed_date': 'Sealed Date: %s',
    'tfc.jade.catalyst_stacks': '%sx Catalyst Stacks',
    'tfc.jade.input_stacks': '%sx Input Stacks',
    'tfc.jade.fuel_stacks': '%sx Fuel Stacks',
    'tfc.jade.straws': '%s Straw',
    'tfc.jade.logs': '%s Logs',
    'tfc.jade.creating': 'Creating %s',
    'tfc.jade.burn_rate': 'Burn Rate: %s ticks / mB',
    'tfc.jade.burn_forever': 'Will burn indefinitely',
    'tfc.jade.time_left': 'Time left: %s',
    'tfc.jade.ready_to_grow': 'Ready to Grow',
    'tfc.jade.air_ticks': '%s added air ticks remaining',
    'tfc.jade.animal_wear': 'Wear & Tear: %s',
    'tfc.jade.familiarity': 'Familiarity: %s',
    'tfc.jade.adulthood_progress': 'Becomes adult in %s',
    'tfc.jade.juvenile': 'Juvenile',
    'tfc.jade.animal_size': 'Size: %s',
    'tfc.jade.product.generic': 'Has Animal Product',
    'tfc.jade.product.eggs': 'Has Eggs',
    'tfc.jade.product.milk': 'Ready to Milk',
    'tfc.jade.product.wool': 'Ready to Shear',
    'tfc.jade.can_mate': 'Ready to Mate',
    'tfc.jade.old_animal': 'Old, cannot reproduce or provide useful products',
    'tfc.jade.gestation_time_left': 'Gestation Time Left: %s',
    'tfc.jade.may_ride_horse': 'May be ridden',
    'tfc.jade.explosion_strength': 'Explosion Strength: %s',
    'tfc.jade.yield': 'Yield Multiplier: %s',
    'tfc.jade.no_stick': 'Needs stick to reach max growth',
    'tfc.jade.variant_and_markings': '%s, %s',
    'tfc.jade.raining_mud_bricks': 'Raining, cannot start drying',
    'tfc.jade.dried_mud_bricks': 'Dried',
    'tfc.jade.mud_bricks_nearly_done': 'Almost dry',
    'tfc.jade.loom_progress': 'Weaving Progress: %s / %s making %s',
    'tfc.jade.squid_size': 'Size: %s',
    'tfc.jade.freshwater': 'Freshwater',
    'tfc.jade.saltwater': 'Saltwater',
    'tfc.jade.diurnal': 'Diurnal',
    'tfc.jade.nocturnal': 'Nocturnal',
    'tfc.jade.pack_respect': 'Pack Respect: %s',
    'tfc.jade.large_bait': 'Needs large fishing bait to catch',
    'tfc.jade.hooked': 'Hooked Entity: %s',
    'tfc.jade.bait': 'Attached Bait: %s',
    'tfc.jade.smoke_level': 'Smoke Level: %s / 4',
    **{'tfc.jade.bellows_%s' % i: 'W' + ('o' * (2 + i)) + 'sh' for i in range(1, 11)},

    # Commands

    'tfc.commands.time.query.daytime': 'The day time is %s',
    'tfc.commands.time.query.game_time': 'The game time is %s',
    'tfc.commands.time.query.day': 'The day is %s',
    'tfc.commands.time.query.player_ticks': 'The player ticks is %s',
    'tfc.commands.time.query.calendar_ticks': 'The calendar ticks is %s',
    'tfc.commands.heat.set_heat': 'Held item heat set to %s',
    'tfc.commands.clear_world.starting': 'Clearing world. Prepare for lag...',
    'tfc.commands.clear_world.done': 'Cleared %d Block(s).',
    'tfc.commands.countblock.done': 'Found %d %s',
    'tfc.commands.countblock.invalid_block': 'Not a block or block tag: %s',
    'tfc.commands.player.query_hunger': 'Hunger is %s / 20',
    'tfc.commands.player.query_saturation': 'Saturation is %s / 20',
    'tfc.commands.player.query_water': 'Water is %s / 100',
    'tfc.commands.player.query_nutrition': 'Player nutrition:',
    'tfc.commands.player.fail_invalid_food_stats': 'Player does not have any TFC nutrition or hydration data',
    'tfc.commands.locate.unknown_vein': 'Unknown vein: %s',
    'tfc.commands.locate.vein_not_found': 'Unable to find vein %s within reasonable distance (16 chunks radius)',
    'tfc.commands.locate.invalid_biome': 'Invalid biome: \"%s\"',
    'tfc.commands.locate.invalid_biome_source': 'This world does not have a compatible biome source',
    'tfc.commands.locate.not_found': 'Could not find a biome of type \"%s\" within reasonable distance',
    'tfc.commands.locate.volcano_not_found': 'Could not find a volcano within reasonable distance',
    'tfc.commands.propick.found_blocks': 'The propick scan found %s %s',
    'tfc.commands.propick.cleared': 'Cleared %s blocks, Found %s prospectable blocks',
    'tfc.commands.particle.no_fluid': 'Unknown Fluid: %s',

    # Entities
    'entity.tfc.cod': 'Cod',
    'entity.tfc.pufferfish': 'Pufferfish',
    'entity.tfc.tropical_fish': 'Tropical Fish',
    'entity.tfc.salmon': 'Salmon',
    'entity.tfc.bluegill': 'Bluegill',
    'entity.tfc.jellyfish': 'Jellyfish',
    'entity.tfc.manatee': 'Manatee',
    'entity.tfc.orca': 'Orca',
    'entity.tfc.dolphin': 'Dolphin',
    'entity.tfc.isopod': 'Isopod',
    'entity.tfc.lobster': 'Lobster',
    'entity.tfc.crayfish': 'Crayfish',
    'entity.tfc.horseshoe_crab': 'Horseshoe Crab',
    'entity.tfc.penguin': 'Penguin',
    'entity.tfc.turtle': 'Turtle',
    'entity.tfc.pig': 'Pig',
    'entity.tfc.pig.male': 'Pig',
    'entity.tfc.pig.female': 'Sow',
    'entity.tfc.cow': 'Cow',
    'entity.tfc.cow.female': 'Cow',
    'entity.tfc.cow.male': 'Bull',
    'entity.tfc.goat': 'Goat',
    'entity.tfc.goat.female': 'Nanny Goat',
    'entity.tfc.goat.male': 'Billy Goat',
    'entity.tfc.alpaca': 'Alpaca',
    'entity.tfc.alpaca.female': 'Female Alpaca',
    'entity.tfc.alpaca.male': 'Male Alpaca',
    'entity.tfc.sheep': 'Sheep',
    'entity.tfc.sheep.female': 'Ewe',
    'entity.tfc.sheep.male': 'Ram',
    'entity.tfc.musk_ox': 'Musk Ox',
    'entity.tfc.musk_ox.female': 'Musk Ox Cow',
    'entity.tfc.musk_ox.male': 'Musk Ox Bull',
    'entity.tfc.yak': 'Yak',
    'entity.tfc.yak.female': 'Female Yak',
    'entity.tfc.yak.male': 'Male Yak',
    'entity.tfc.polar_bear': 'Polar Bear',
    'entity.tfc.grizzly_bear': 'Grizzly Bear',
    'entity.tfc.black_bear': 'Black Bear',
    'entity.tfc.cougar': 'Cougar',
    'entity.tfc.panther': 'Panther',
    'entity.tfc.lion': 'Lion',
    'entity.tfc.sabertooth': 'Sabertooth',
    'entity.tfc.falling_block': 'Falling Block',
    'entity.tfc.fishing_bobber': 'Fishing Bobber',
    'entity.tfc.chest_minecart': 'Chest Minecart',
    'entity.tfc.holding_minecart': 'Holding Minecart',
    'entity.tfc.squid': 'Squid',
    'entity.tfc.octopoteuthis': 'Octopoteuthis',
    'entity.tfc.glow_arrow': 'Glowing Arrow',
    'entity.tfc.thrown_javelin': 'Javelin',
    'entity.tfc.seat': 'Seat',
    'entity.tfc.chicken': 'Chicken',
    'entity.tfc.chicken.male': 'Rooster',
    'entity.tfc.chicken.female': 'Chicken',
    'entity.tfc.duck': 'Duck',
    'entity.tfc.duck.male': 'Drake',
    'entity.tfc.duck.female': 'Duck',
    'entity.tfc.quail': 'Quail',
    'entity.tfc.quail.male': 'Male Quail',
    'entity.tfc.quail.female': 'Female Quail',
    'entity.tfc.rabbit': 'Rabbit',
    'entity.tfc.fox': 'Fox',
    'entity.tfc.panda': 'Panda',
    'entity.tfc.boar': 'Boar',
    'entity.tfc.ocelot': 'Ocelot',
    'entity.tfc.deer': 'Deer',
    'entity.tfc.moose': 'Moose',
    'entity.tfc.grouse': 'Grouse',
    'entity.tfc.pheasant': 'Pheasant',
    'entity.tfc.turkey': 'Turkey',
    'entity.tfc.rat': 'Rat',
    'entity.tfc.cat': 'Cat',
    'entity.tfc.cat.female': 'Female Cat',
    'entity.tfc.cat.male': 'Male Cat',
    'entity.tfc.dog': 'Dog',
    'entity.tfc.dog.male': 'Male Dog',
    'entity.tfc.dog.female': 'Female Dog',
    'entity.tfc.wolf': 'Wolf',
    'entity.tfc.direwolf': 'Direwolf',
    'entity.tfc.mule': 'Mule',
    'entity.tfc.mule.male': 'Mule',
    'entity.tfc.mule.female': 'Mule',
    'entity.tfc.donkey': 'Donkey',
    'entity.tfc.donkey.male': 'Jack Donkey',
    'entity.tfc.donkey.female': 'Jenny Donkey',
    'entity.tfc.horse': 'Horse',
    'entity.tfc.horse.male': 'Stallion',
    'entity.tfc.horse.female': 'Mare',
    **{'entity.tfc.boat.%s' % wood : lang('%s boat', wood) for wood in WOODS.keys()},

    # Enums

    **dict(('tfc.enum.tier.tier_%s' % tier, 'Tier %s' % tier.upper()) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')),
    **lang_enum('heat', ('warming', 'hot', 'very_hot', 'faint_red', 'dark_red', 'bright_red', 'orange', 'yellow', 'yellow_white', 'white', 'brilliant_white')),
    **lang_enum('month', ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')),
    **lang_enum('day', ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')),
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'edge', 'none')),
    **lang_enum('koppenclimateclassification', ('arctic', 'tundra', 'humid_subarctic', 'subarctic', 'cold_desert', 'hot_desert', 'temperate', 'subtropical', 'humid_subtropical', 'humid_oceanic', 'humid_subtropical', 'tropical_savanna', 'tropical_rainforest')),
    **dict(('tfc.enum.platetectonicsclassification.%s' % k, v) for k, v in {
        'oceanic': 'Oceanic',
        'continental_low': 'Low Altitude Continental',
        'continental_mid': 'Mid Altitude Continental',
        'continental_high': 'High Altitude Continental',
        'ocean_ocean_diverging': 'Mid-Ocean Ridge',
        'ocean_ocean_converging': 'Oceanic Subduction',
        'ocean_continent_diverging': 'Continental Subduction',
        'ocean_continent_converging': 'Continental Subduction',
        'continent_continent_diverging': 'Continental Rift',
        'continent_continent_converging': 'Orogenic Belt',
        'continental_shelf': 'Continental Shelf'
    }.items()),
    'tfc.enum.season.january': 'Winter',
    'tfc.enum.season.february': 'Late Winter',
    'tfc.enum.season.march': 'Early Spring',
    'tfc.enum.season.april': 'Spring',
    'tfc.enum.season.may': 'Late Spring',
    'tfc.enum.season.june': 'Early Summer',
    'tfc.enum.season.july': 'Summer',
    'tfc.enum.season.august': 'Late Summer',
    'tfc.enum.season.september': 'Early Autumn',
    'tfc.enum.season.october': 'Autumn',
    'tfc.enum.season.november': 'Late Autumn',
    'tfc.enum.season.december': 'Early Winter',
    'tfc.enum.gender.male': 'Male',
    'tfc.enum.gender.female': 'Female',
    'tfc.enum.variant.white': 'White Variant',
    'tfc.enum.variant.creamy': 'Creamy Variant',
    'tfc.enum.variant.chestnut': 'Chestnut Variant',
    'tfc.enum.variant.brown': 'Brown Variant',
    'tfc.enum.variant.black': 'Black Variant',
    'tfc.enum.variant.gray': 'Gray Variant',
    'tfc.enum.variant.darkbrown': 'Dark Brown',
    'tfc.enum.markings.none': 'No Markings',
    'tfc.enum.markings.white': 'White Markings',
    'tfc.enum.markings.white_field': 'White Field Markings',
    'tfc.enum.markings.white_dots': 'White Dot Markings',
    'tfc.enum.markings.black_dots': 'Black Dot Markings',
    'tfc.enum.size.tiny': 'Tiny',
    'tfc.enum.size.very_small': 'Very Small',
    'tfc.enum.size.small': 'Small',
    'tfc.enum.size.normal': 'Normal',
    'tfc.enum.size.large': 'Large',
    'tfc.enum.size.very_large': 'Very Large',
    'tfc.enum.size.huge': 'Huge',
    'tfc.enum.weight.very_light': 'Very Light',
    'tfc.enum.weight.light': 'Light',
    'tfc.enum.weight.medium': 'Medium',
    'tfc.enum.weight.heavy': 'Heavy',
    'tfc.enum.weight.very_heavy': 'Very Heavy',
    'tfc.enum.nutrient.grain': 'Grain',
    'tfc.enum.nutrient.fruit': 'Fruit',
    'tfc.enum.nutrient.vegetables': 'Vegetables',
    'tfc.enum.nutrient.protein': 'Protein',
    'tfc.enum.nutrient.dairy': 'Dairy',
    'tfc.enum.forgingbonus.none': 'No Forging Bonus',
    'tfc.enum.forgingbonus.poorly_forged': 'Poorly Forged',
    'tfc.enum.forgingbonus.well_forged': 'Well Forged',
    'tfc.enum.forgingbonus.expertly_forged': 'Expertly Forged',
    'tfc.enum.forgingbonus.perfectly_forged': 'Perfectly Forged!',
    'tfc.enum.forgestep.hit': 'Hit',
    'tfc.enum.forgestep.hit_light': 'Light Hit',
    'tfc.enum.forgestep.hit_medium': 'Medium Hit',
    'tfc.enum.forgestep.hit_hard': 'Hard Hit',
    'tfc.enum.forgestep.draw': 'Draw',
    'tfc.enum.forgestep.punch': 'Punch',
    'tfc.enum.forgestep.bend': 'Bend',
    'tfc.enum.forgestep.upset': 'Upset',
    'tfc.enum.forgestep.shrink': 'Shrink',
    'tfc.enum.order.any': 'Any',
    'tfc.enum.order.last': 'Last',
    'tfc.enum.order.not_last': 'Not Last',
    'tfc.enum.order.second_last': 'Second Last',
    'tfc.enum.order.third_last': 'Third Last',
    'tfc.enum.command.relax': 'Relax',
    'tfc.enum.command.home': 'We\'re Home',
    'tfc.enum.command.sit': 'Sit',
    'tfc.enum.command.follow': 'Follow Me',
    'tfc.enum.command.hunt': 'Hunt With Me',
    'tfc.enum.command.relax.tooltip': 'The animal will wander around its home.',
    'tfc.enum.command.home.tooltip': 'Tells the animal to recognize this location as home.',
    'tfc.enum.command.sit.tooltip': 'The animal will sit for a while, but not forever.',
    'tfc.enum.command.follow.tooltip': 'The animal will follow you, but not try to aid in combat.',
    'tfc.enum.command.hunt.tooltip': 'The animal will follow you and engage in combat.',
    'tfc.pet.not_owner': 'You are not its owner, this pet will not obey your commands!',
    'tfc.pet.will_not_listen': 'It ignores your command.',
    'tfc.rabbit_0': 'Brown Fur',
    'tfc.rabbit_1': 'White Fur',
    'tfc.rabbit_2': 'Black Fur',
    'tfc.rabbit_3': 'White Splotched Fur',
    'tfc.rabbit_4': 'Golden Fur',
    'tfc.rabbit_5': 'Salty Fur',
    'tfc.rabbit_99': '§cEvil',

    'tfc.thatch_bed.use_no_sleep_no_spawn': 'This bed is too uncomfortable to sleep in.',
    'tfc.thatch_bed.use_sleep_no_spawn': 'This bed does not allow you to set your spawn.',
    'tfc.thatch_bed.use_no_sleep_spawn': 'This bed is too uncomfortable to sleep in, but your spawn point was set.',
    'tfc.thatch_bed.use_sleep_spawn': 'Spawn point set.',
    'tfc.thatch_bed.thundering': 'You are too scared to sleep.',
    'tfc.composter.rotten': 'This composter is smelly and might attract animals. You should empty it.',
    'tfc.composter.too_many_greens': 'This composter has enough green items',
    'tfc.composter.too_many_browns': 'This composter has enough brown items',
    'tfc.composter.green_items': '%s Green Items',
    'tfc.composter.brown_items': '%s Brown Items',
    'tfc.chisel.cannot_place': 'The chiseled version of this block cannot exist here',
    'tfc.chisel.no_recipe': 'This block cannot be chiseled',
    'tfc.chisel.bad_fluid': 'The chiseled version of this block cannot contain the fluid here',
    'tfc.fishing.no_bait': 'This fishing rod needs bait!',
    'tfc.fishing.pulled_too_hard': 'You pulled too hard, and the fish got away with the bait.',

    **dict(('metal.tfc.%s' % metal, lang(metal)) for metal in METALS.keys()),

    'tfc.jei.heating': 'Heating Recipe',
    'tfc.jei.quern': 'Quern Recipe',
    'tfc.jei.scraping': 'Scraping Recipe',
    'tfc.jei.clay_knapping': 'Clay Knapping Recipe',
    'tfc.jei.fire_clay_knapping': 'Fire Clay Knapping Recipe',
    'tfc.jei.leather_knapping': 'Leather Knapping Recipe',
    'tfc.jei.rock_knapping': 'Rock Knapping Recipe',
    'tfc.jei.soup_pot': 'Soup Pot',
    'tfc.jei.simple_pot': 'Pot',
    'tfc.jei.casting': 'Casting',
    'tfc.jei.alloying': 'Alloying',
    'tfc.jei.loom': 'Loom',
    'tfc.jei.instant_barrel': 'Instant Barrel Recipe',
    'tfc.jei.instant_fluid_barrel': 'Instant Fluid Barrel Recipe',
    'tfc.jei.sealed_barrel': 'Sealed Barrel Recipe',
    'tfc.jei.bloomery': 'Bloomery',
    'tfc.jei.welding': 'Welding',
    'tfc.jei.anvil': 'Anvil',
    'tfc.jei.chisel': 'Chisel',

    'tfc.field_guide.book_name': 'TerraFirmaCraft',
    'tfc.field_guide.book_landing_text': 'Welcome traveller! This book will be the source of all you need to know as you explore the world of TerraFirmaCraft (TFC).'
}

# Automatically Generated by generate_trees.py
TREE_SAPLING_DROP_CHANCES = {
    'acacia': 0.0294,
    'ash': 0.0215,
    'aspen': 0.0428,
    'birch': 0.0428,
    'blackwood': 0.0780,
    'chestnut': 0.0215,
    'douglas_fir': 0.0543,
    'hickory': 0.0543,
    'kapok': 0.0115,
    'maple': 0.0215,
    'oak': 0.0103,
    'palm': 0.0914,
    'pine': 0.0543,
    'rosewood': 0.0103,
    'sequoia': 0.0238,
    'spruce': 0.0238,
    'sycamore': 0.0215,
    'white_cedar': 0.0286,
    'willow': 0.0143,
}

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any

Tier = Literal['stone', 'copper', 'bronze', 'wrought_iron', 'steel', 'black_steel', 'colored_steel']
RockCategory = Literal['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
BerryBushType = Literal['stationary', 'spreading', 'waterlogged']
Rock = NamedTuple('Rock', category=RockCategory, sand=str)
Metal = NamedTuple('Metal', tier=int, types=set, heat_capacity=float, melt_temperature=float, melt_metal=Optional[str])
MetalItem = NamedTuple('MetalItem', type=str, smelt_amount=int, parent_model=str, tag=Optional[str], mold=bool)
Ore = NamedTuple('Ore', metal=Optional[str], graded=bool, required_tool=Tier, tag=str)
OreGrade = NamedTuple('OreGrade', weight=int, grind_amount=int)
Vein = NamedTuple('Vein', ore=str, type=str, rarity=int, size=int, min_y=int, max_y=int, density=float, poor=float, normal=float, rich=float, rocks=List[str], spoiler_ore=str, spoiler_rarity=int, spoiler_rocks=List[str], biomes=Optional[str], height=Optional[int])
Plant = NamedTuple('Plant', clay=bool, min_temp=float, max_temp=float, min_rain=float, max_rain=float, type=str)
Wood = NamedTuple('Wood', temp=float, duration=int)
Berry = NamedTuple('Berry', min_temp=float, max_temp=float, min_rain=float, max_rain=float, type=BerryBushType, min_forest=str, max_forest=str)
Fruit = NamedTuple('Fruit', min_temp=float, max_temp=float, min_rain=float, max_rain=float)
Crop = NamedTuple('Crop', type=str, stages=int)

# Melting Temps
POTTERY_MELT = 1200 - 1

# Heat Capacities
POTTERY_HC = 0.2

HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCK_CATEGORY_ITEMS: List[str] = ['axe', 'hammer', 'hoe', 'javelin', 'knife', 'shovel']

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
    'shears': 'shears'
}

ROCKS: Dict[str, Rock] = {
    'chalk': Rock('sedimentary', 'white'),
    'chert': Rock('sedimentary', 'yellow'),
    'claystone': Rock('sedimentary', 'brown'),
    'conglomerate': Rock('sedimentary', 'green'),
    'dolomite': Rock('sedimentary', 'black'),
    'limestone': Rock('sedimentary', 'white'),
    'shale': Rock('sedimentary', 'black'),
    'gneiss': Rock('metamorphic', 'green'),
    'marble': Rock('metamorphic', 'yellow'),
    'phyllite': Rock('metamorphic', 'brown'),
    'quartzite': Rock('metamorphic', 'white'),
    'schist': Rock('metamorphic', 'green'),
    'slate': Rock('metamorphic', 'brown'),
    'diorite': Rock('igneous_intrusive', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black'),
    'granite': Rock('igneous_intrusive', 'white'),
    'andesite': Rock('igneous_extrusive', 'red'),
    'basalt': Rock('igneous_extrusive', 'red'),
    'dacite': Rock('igneous_extrusive', 'red'),
    'rhyolite': Rock('igneous_extrusive', 'red')
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
    'anvil': MetalItem('utility', 1400, 'tfc:block/anvil', None, False),
    'lamp': MetalItem('utility', 100, 'tfc:block/lamp', None, False)
}
METAL_ITEMS: Dict[str, MetalItem] = {
    'ingot': MetalItem('all', 100, 'item/generated', 'forge:ingots', True),
    'double_ingot': MetalItem('part', 200, 'item/generated', 'forge:double_ingots', False),
    'sheet': MetalItem('part', 200, 'item/generated', 'forge:sheets', False),
    'double_sheet': MetalItem('part', 400, 'item/generated', 'forge:double_sheets', False),
    'rod': MetalItem('part', 100, 'item/generated', 'forge:rods', False),

    'tuyere': MetalItem('tool', 100, 'item/generated', None, False),
    'fish_hook': MetalItem('tool', 100, 'item/generated', None, False),
    'fishing_rod': MetalItem('tool', 100, 'item/generated', None, False),
    'pickaxe': MetalItem('tool', 100, 'item/handheld', None, False),
    'pickaxe_head': MetalItem('tool', 100, 'item/generated', None, True),
    'shovel': MetalItem('tool', 100, 'item/handheld', None, False),
    'shovel_head': MetalItem('tool', 100, 'item/generated', None, True),
    'axe': MetalItem('tool', 100, 'item/handheld', None, False),
    'axe_head': MetalItem('tool', 100, 'item/generated', None, True),
    'hoe': MetalItem('tool', 100, 'item/handheld', None, False),
    'hoe_head': MetalItem('tool', 100, 'item/generated', None, True),
    'chisel': MetalItem('tool', 100, 'item/handheld', None, False),
    'chisel_head': MetalItem('tool', 100, 'item/generated', None, True),
    'sword': MetalItem('tool', 200, 'item/handheld', None, False),
    'sword_blade': MetalItem('tool', 200, 'item/generated', None, True),
    'mace': MetalItem('tool', 200, 'item/handheld', None, False),
    'mace_head': MetalItem('tool', 200, 'item/generated', None, True),
    'saw': MetalItem('tool', 100, 'item/handheld', None, False),
    'saw_blade': MetalItem('tool', 100, 'item/generated', None, True),
    'javelin': MetalItem('tool', 100, 'item/handheld', None, False),
    'javelin_head': MetalItem('tool', 100, 'item/generated', None, True),
    'hammer': MetalItem('tool', 100, 'item/handheld', None, False),
    'hammer_head': MetalItem('tool', 100, 'item/generated', None, True),
    'propick': MetalItem('tool', 100, 'item/handheld', None, False),
    'propick_head': MetalItem('tool', 100, 'item/generated', None, True),
    'knife': MetalItem('tool', 100, 'tfc:item/handheld_flipped', None, False),
    'knife_blade': MetalItem('tool', 100, 'item/generated', None, True),
    'scythe': MetalItem('tool', 100, 'item/handheld', None, False),
    'scythe_blade': MetalItem('tool', 100, 'item/generated', None, True),
    'shears': MetalItem('tool', 200, 'item/handheld', None, False),

    'unfinished_helmet': MetalItem('armor', 400, 'item/generated', None, False),
    'helmet': MetalItem('armor', 600, 'item/generated', None, False),
    'unfinished_chestplate': MetalItem('armor', 400, 'item/generated', None, False),
    'chestplate': MetalItem('armor', 800, 'item/generated', None, False),
    'unfinished_greaves': MetalItem('armor', 400, 'item/generated', None, False),
    'greaves': MetalItem('armor', 600, 'item/generated', None, False),
    'unfinished_boots': MetalItem('armor', 200, 'item/generated', None, False),
    'boots': MetalItem('armor', 400, 'item/generated', None, False),

    'shield': MetalItem('tool', 400, 'item/handheld', None, False)
}
METAL_ITEMS_AND_BLOCKS = {**METAL_ITEMS, **METAL_BLOCKS}
METAL_TOOL_HEADS = ('chisel', 'hammer', 'hoe', 'javelin', 'knife', 'mace', 'pickaxe', 'propick', 'saw', 'scythe', 'shovel', 'sword')
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
DEFAULT_FORGE_ORE_TAGS: List[str] = ['coal', 'diamond', 'emerald', 'gold', 'iron', 'lapis', 'netherite_scrap', 'quartz', 'redstone']


def vein(ore: str, vein_type: str, rarity: int, size: int, min_y: int, max_y: int, density: float, poor: float, normal: float, rich: float, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 0):
    # Factory method to allow default values
    return Vein(ore, vein_type, rarity, size, min_y, max_y, density, poor, normal, rich, rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height)


def preset_vein(ore: str, vein_type: str, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 0, preset: Tuple[int, int, int, int, int, int, int, int] = None):
    assert preset is not None
    return Vein(ore, vein_type, preset[0], preset[1], preset[2], preset[3], preset[4], preset[5], preset[6], preset[7], rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height)


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
    'surface_native_copper': preset_vein('native_copper', 'cluster', ['igneous_extrusive'], preset=SURFACE_METAL_ORE),
    'normal_native_gold': preset_vein('native_gold', 'cluster', ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 20, ['igneous_extrusive', 'igneous_intrusive'], preset=NORMAL_S_METAL_ORE),
    'deep_native_gold': preset_vein('native_gold', 'cluster', ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 10, ['igneous_extrusive', 'igneous_intrusive'], preset=DEEP_S_METAL_ORE),
    'normal_native_silver': preset_vein('native_silver', 'cluster', ['granite', 'gneiss'], preset=NORMAL_METAL_ORE),
    'poor_native_silver': preset_vein('native_silver', 'cluster', ['granite', 'metamorphic'], preset=POOR_METAL_ORE),
    'normal_hematite': preset_vein('hematite', 'cluster', ['igneous_extrusive'], preset=NORMAL_METAL_ORE),
    'deep_hematite': preset_vein('hematite', 'cluster', ['igneous_extrusive'], preset=DEEP_METAL_ORE),
    'normal_cassiterite': preset_vein('cassiterite', 'cluster', ['igneous_intrusive'], 'topaz', 10, ['granite'], preset=NORMAL_METAL_ORE),
    'surface_cassiterite': preset_vein('cassiterite', 'cluster', ['igneous_intrusive'], 'topaz', 20, ['granite'], preset=SURFACE_METAL_ORE),
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
    'borax': preset_vein('borax', 'cluster', ['slate'], preset=HIGH_MINERAL_ORE),
    'gypsum': vein('gypsum', 'disc', 120, 20, 30, 90, 60, 0, 0, 0, ['metamorphic']),
    'lapis_lazuli': preset_vein('lapis_lazuli', 'cluster', ['limestone', 'marble'], preset=DEEP_MINERAL_ORE),
    'halite': vein('halite', 'disc', 120, 30, 30, 90, 80, 0, 0, 0, ['sedimentary']),
    'diamond': vein('diamond', 'pipe', 60, 60, -64, 100, 40, 0, 0, 0, ['gabbro']),
    'emerald': vein('emerald', 'pipe', 80, 60, -64, 100, 40, 0, 0, 0, ['igneous_intrusive']),
    'volcanic_sulfur': vein('sulfur', 'disc', 25, 14, 80, 180, 40, 0, 0, 0, ['igneous_extrusive', 'igneous_intrusive'], biomes='volcanic', height=6),
    'amethyst': vein('amethyst', 'disc', 14, 8, 40, 60, 20, 0, 0, 0, ['sedimentary', 'metamorphic'], biomes='river', height=4),
    'opal': vein('opal', 'disc', 14, 8, 40, 60, 20, 0, 0, 0, ['sedimentary', 'igneous_extrusive'], biomes='river', height=4)
}

ROCK_BLOCK_TYPES = ('raw', 'hardened', 'bricks', 'cobble', 'gravel', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks', 'chiseled', 'spike', 'loose', 'pressure_plate', 'button')
ROCK_BLOCKS_IN_JSON = ('raw', 'hardened', 'cobble', 'gravel', 'spike', 'loose')
CUTTABLE_ROCKS = ('raw', 'bricks', 'cobble', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks')
ROCK_SPIKE_PARTS = ('base', 'middle', 'tip')
SAND_BLOCK_TYPES = ('brown', 'white', 'black', 'red', 'yellow', 'green', 'pink')
SANDSTONE_BLOCK_TYPES = ('raw', 'smooth', 'cut')
SOIL_BLOCK_TYPES = ('dirt', 'grass', 'grass_path', 'clay', 'clay_grass', 'farmland', 'rooted_dirt')
SOIL_BLOCK_VARIANTS = ('silt', 'loam', 'sandy_loam', 'silty_loam')

GEMS = ('amethyst', 'diamond', 'emerald', 'lapis_lazuli', 'opal', 'pyrite', 'ruby', 'sapphire', 'topaz')

MISC_GROUNDCOVER = ['bone', 'clam', 'driftwood', 'mollusk', 'mussel', 'pinecone', 'seaweed', 'stick', 'dead_grass', 'feather', 'flint', 'guano', 'humus', 'rotten_flesh', 'salt_lick']

COLORS = ('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')

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

CROPS: Dict[str, Crop] = {
    'barley': Crop('default', 8),
    'oat': Crop('default', 8),
    'rye': Crop('default', 8),
    'maize': Crop('double', 6),
    'wheat': Crop('default', 8),
    'rice': Crop('default', 8),
    'beet': Crop('default', 6),
    'cabbage': Crop('default', 6),
    'carrot': Crop('default', 5),
    'garlic': Crop('default', 5),
    'green_bean': Crop('double_stick', 8),
    'potato': Crop('default', 7),
    'onion': Crop('default', 7),
    'soybean': Crop('default', 7),
    'squash': Crop('default', 8),
    'sugarcane': Crop('double', 8),
    'tomato': Crop('double_stick', 8),
    'jute': Crop('double', 6)
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
    'reindeer_lichen': Plant(False, 10, 33, 50, 470, 'creeping'),
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
    'spanish_moss': Plant(False, 8, 22, 400, 500, 'hanging'),
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
UNIQUE_PLANTS: List[str] = ['hanging_vines_plant', 'hanging_vines', 'liana_plant', 'liana', 'tree_fern_plant', 'tree_fern', 'arundo_plant', 'arundo', 'winged_kelp_plant', 'winged_kelp', 'leafy_kelp_plant', 'leafy_kelp', 'giant_kelp_plant', 'giant_kelp_flower', 'ivy', 'jungle_vines']
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

SIMPLE_BLOCKS = ('peat', 'aggregate', 'fire_bricks', 'fire_clay_block', 'thatch')
SIMPLE_ITEMS = ('alabaster_brick', 'brass_mechanisms', 'burlap_cloth', 'compost', 'daub', 'dirty_jute_net', 'fire_clay', 'firestarter', 'glass_shard', 'glue',
                'halter', 'jute', 'jute_disc', 'jute_fiber', 'jute_net', 'mortar', 'olive_jute_disc', 'olive_paste', 'rotten_compost', 'silk_cloth', 'spindle',
                'stick_bunch', 'stick_bundle', 'straw', 'wool', 'wool_cloth', 'wool_yarn', 'wrought_iron_grill')
GENERIC_POWDERS = ('charcoal', 'coke', 'graphite', 'hematite', 'kaolinite', 'limonite', 'malachite', 'sylvite')
POWDERS = ('flux', 'salt', 'saltpeter', 'sulfur', 'wood_ash')
SIMPLE_POTTERY = ('bowl', 'fire_brick', 'pot', 'spindle_head', 'vessel')
SIMPLE_UNFIRED_POTTERY = ('brick', 'crucible', 'flower_pot', 'jug')
VANILLA_TOOL_MATERIALS = ('netherite', 'diamond', 'iron', 'stone', 'wooden', 'golden')
SHORE_DECORATORS = ('driftwood', 'clam', 'mollusk', 'mussel', 'seaweed', 'sticks_shore')
FOREST_DECORATORS = ('sticks_forest', 'pinecone', 'salt_lick', 'dead_grass', 'humus')
OCEAN_PLANT_TYPES = ('grass_water', 'floating', 'water', 'emergent', 'tall_water')
MISC_PLANT_FEATURES = ('hanging_vines', 'hanging_vines_cave', 'ivy', 'jungle_vines', 'liana')

BERRIES: Dict[str, Berry] = {
    'blackberry': Berry(7, 20, 100, 400, 'spreading', 'edge', 'edge'),
    'raspberry': Berry(5, 20, 100, 400, 'spreading', 'edge', 'edge'),
    'blueberry': Berry(7, 25, 100, 400, 'spreading', 'edge', 'edge'),
    'elderberry': Berry(10, 29, 100, 400, 'spreading', 'edge', 'edge'),
    'bunchberry': Berry(15, 30, 100, 400, 'stationary', 'edge', 'normal'),
    'gooseberry': Berry(5, 27, 100, 400, 'stationary', 'none', 'sparse'),
    'snowberry': Berry(-5, 18, 100, 400, 'stationary', 'normal', 'old_growth'),
    'cloudberry': Berry(3, 17, 80, 370, 'stationary', 'normal', 'old_growth'),
    'strawberry': Berry(5, 28, 100, 400, 'stationary', 'none', 'sparse'),
    'wintergreen_berry': Berry(-5, 17, 100, 400, 'stationary', 'old_growth', 'old_growth'),
    'cranberry': Berry(-5, 17, 250, 500, 'waterlogged', 'edge', 'old_growth')
}

FRUITS: Dict[str, Fruit] = {
    'banana': Fruit(23, 35, 280, 480),
    'cherry': Fruit(5, 21, 100, 350),
    'green_apple': Fruit(8, 25, 110, 280),
    'lemon': Fruit(10, 30, 180, 470),
    'olive': Fruit(13, 30, 150, 380),
    'orange': Fruit(23, 36, 250, 480),
    'peach': Fruit(9, 27, 60, 230),
    'plum': Fruit(18, 31, 250, 400),
    'red_apple': Fruit(9, 25, 100, 280)
}
NORMAL_FRUIT_TREES: List[str] = [k for k in FRUITS.keys() if k != 'banana']

GRAINS = ('barley', 'maize', 'oat', 'rice', 'rye', 'wheat')
GRAIN_SUFFIXES = ('', '_grain', '_flour', '_dough', '_bread')
MEATS = ('beef', 'pork', 'chicken', 'mutton', 'bear', 'horse_meat', 'pheasant', 'venison', 'wolf', 'rabbit', 'hyena', 'duck', 'chevon', 'gran_feline', 'camelidae', 'cod', 'bluegill', 'salmon', 'tropical_fish')
VEGETABLES = ('beet', 'cabbage', 'carrot', 'garlic', 'green_bean', 'green_bell_pepper', 'onion', 'potato', 'red_bell_pepper', 'soybean', 'squash', 'tomato', 'yellow_bell_pepper', 'cheese', 'cooked_egg', 'dried_seaweed', 'dried_kelp', 'cattail_root', 'sugarcane')

SPAWN_EGG_ENTITIES = ['isopod', 'lobster', 'cod', 'pufferfish', 'tropical_fish', 'jellyfish', 'orca', 'dolphin', 'salmon', 'bluegill', 'manatee', 'penguin', 'turtle', 'vulture', 'horseshoe_crab']
BUCKETABLE_FISH = ['cod', 'pufferfish', 'tropical_fish', 'jellyfish', 'salmon', 'bluegill']


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

"""OCEAN_CREATURES: Dict[str, Dict[str, Any]] = {
    'orca': spawner('tfc:orca', min_count=1, max_count=3),
    'dolphin': spawner('tfc:dolphin', min_count=1, max_count=3)
}"""

LAKE_AMBIENT: Dict[str, Dict[str, Any]] = {
    'salmon': spawner('tfc:salmon', min_count=2, max_count=6, weight=10),
    'bluegill': spawner('tfc:bluegill', min_count=2, max_count=4, weight=10)
}

"""LAKE_CREATURES: Dict[str, Dict[str, Any]] = {
    'manatee': spawner('tfc:manatee', min_count=1, max_count=2)
}

SHORE_CREATURES: Dict[str, Dict[str, Any]] = {
    'penguin': spawner('tfc:penguin', min_count=2, max_count=5),
    'turtle': spawner('tfc:turtle', min_count=2, max_count=5)
}"""


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
    'tfc.tile_entity.pot': 'Pot',
    'tfc.tile_entity.grill': 'Grill',
    'tfc.tile_entity.firepit': 'Firepit',
    'tfc.tile_entity.log_pile': 'Log Pile',
    'tfc.tile_entity.charcoal_forge': 'Forge',
    'tfc.tile_entity.crucible': 'Crucible',
    'item.tfc.handstone': 'Handstone',
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
    # Tooltips
    'tfc.tooltip.forging': '§f - Can Work',
    'tfc.tooltip.welding': '§f - Can Weld',
    'tfc.tooltip.calendar_days_years': '%d, %04d',
    'tfc.tooltip.calendar_season': 'Season : ',
    'tfc.tooltip.calendar_day': 'Day : ',
    'tfc.tooltip.calendar_birthday': '%s\'s Birthday!',
    'tfc.tooltip.calendar_date': 'Date : ',
    'tfc.tooltip.climate_plate_tectonics_classification': 'Region: ',
    'tfc.tooltip.climate_koppen_climate_classification': 'Climate: ',
    'tfc.tooltip.climate_average_temperature': 'Avg. Temp: %s\u00b0C',
    'tfc.tooltip.climate_annual_rainfall': 'Annual Rainfall: %smm',
    'tfc.tooltip.climate_current_temp': 'Current Temp: %s\u00b0C',
    'tfc.tooltip.f3_rainfall': 'Rainfall: %s',
    'tfc.tooltip.f3_average_temperature': 'Avg. Temp: %s\u00b0C',
    'tfc.tooltip.f3_temperature': 'Actual. Temp: %s\u00b0C',
    'tfc.tooltip.f3_forest_type': 'Forest Type: ',
    'tfc.tooltip.f3_forest_properties': 'Forest Density = %s, Weirdness = %s',
    'tfc.tooltip.f3_invalid_chunk_data': 'Invalid Chunk Data',
    'tfc.tooltip.food_expiry_date': 'Expires on: ',
    'tfc.tooltip.food_expiry_left': 'Expires in: ',
    'tfc.tooltip.food_expiry_and_days_left': ' (in ',
    'tfc.tooltip.food_expiry_and_days_left_close': ')',
    'tfc.tooltip.food_infinite_expiry': 'Never expires',
    'tfc.tooltip.food_rotten': 'Rotten!',
    'tfc.tooltip.food_rotten_special': 'Ewwww, are you really thinking of eating that? It looks disgusting',
    'tfc.tooltip.nutrition': 'Nutrition:',
    'tfc.tooltip.nutrition_saturation': ' - Saturation: %s%%',
    'tfc.tooltip.nutrition_water': ' - Water: %s%%',
    'tfc.tooltip.nutrition_none': '- None!',
    'tfc.tooltip.hold_shift_for_nutrition_info': 'Hold (Shift) for Nutrition Info',
    'tfc.tooltip.propick.found_very_large': 'Found a very large sample of',
    'tfc.tooltip.propick.found_large': 'Found a large sample of',
    'tfc.tooltip.propick.found_medium': 'Found a medium sample of',
    'tfc.tooltip.propick.found_small': 'Found a small sample of',
    'tfc.tooltip.propick.found_traces': 'Found traces of',
    'tfc.tooltip.propick.found': 'Found',
    'tfc.tooltip.propick.nothing': 'Found nothing.',
    'tfc.tooltip.propick.accuracy': 'Accuracy: %s%%',
    'tfc.tooltip.small_vessel.inventory_too_hot': 'Too hot to open!',
    'tfc.tooltip.small_vessel.alloy_solid': 'Contents have solidified!',
    'tfc.tooltip.small_vessel.contents': 'Contents:',
    'tfc.tooltip.small_vessel.solid': '- Solid.',
    'tfc.tooltip.small_vessel.molten': '- Molten!',
    'tfc.tooltip.small_vessel.still_has_unmelted_items': 'Contains un-melted items!',
    'tfc.tooltip.food_trait.salted': 'Salted',
    'tfc.tooltip.food_trait.brined': 'Brined',
    'tfc.tooltip.food_trait.pickled': 'Pickled',
    'tfc.tooltip.food_trait.preserved': 'Preserved',
    'tfc.tooltip.food_trait.vinegar': 'Preserved in Vinegar',
    'tfc.tooltip.food_trait.charcoal_grilled': 'Charcoal Grilled',
    'tfc.tooltip.food_trait.wood_grilled': 'Wood Grilled',
    'tfc.tooltip.food_trait.burnt_to_a_crisp': 'Burnt to a crisp!',
    'tfc.tooltip.item_melts_into': '§7Melts into %s mB of §f',
    'tfc.tooltip.item_melts_into_open': '§7 (at ',
    'tfc.tooltip.item_melts_into_close': '§7)',
    'tfc.tooltip.fuel_burns_at': '§7Burns at §f',
    'tfc.tooltip.fuel_burns_at_duration': '§7 for §f',
    'tfc.tooltip.time_delta_hours_minutes': '%s:%s',
    'tfc.tooltip.time_delta_days': '%s day(s)',
    'tfc.tooltip.time_delta_months_days': '%s month(s) and %s day(s)',
    'tfc.tooltip.time_delta_years_months_days': '%s year(s), %s month(s) and %s day(s)',
    'tfc.tooltip.temperature_celsius': '%s\u00b0C',
    'tfc.tooltip.temperature_fahrenheit': '%s\u00b0F',
    'tfc.tooltip.fluid_units': '%s mB',
    'tfc.tooltip.fluid_units_of': '%s mB of ',
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
    'tfc.tooltip.fertilizer.nitrogen': '§b(N) Nitrogen: §r%s%%',
    'tfc.tooltip.fertilizer.phosphorus': '§6(P) Phosphorus: §r%s%%',
    'tfc.tooltip.fertilizer.potassium': '§d(K) Potassium: §r%s%%',

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
    'tfc.commands.player.query_hunger': 'Hunger is %s / 20',
    'tfc.commands.player.query_saturation': 'Saturation is %s / 20',
    'tfc.commands.player.query_water': 'Water is %s / 100',
    'tfc.commands.player.query_nutrition': 'Player nutrition:',
    'tfc.commands.player.fail_invalid_food_stats': 'Player does not have any TFC nutrition or hydration data',
    'tfc.commands.locatevein.unknown_vein': 'Unknown vein: %s',
    'tfc.commands.locatevein.vein_not_found': 'Unable to find vein %s within reasonable distance (16 chunks radius)',
    'tfc.commands.locate.invalid_biome': 'Invalid biome: \"%s\"',
    'tfc.commands.locate.invalid_biome_source': 'This world does not have a compatible biome source',
    'tfc.commands.locate.not_found': 'Could not find a biome of type \"%s\" within reasonable distance',
    'tfc.commands.locate.volcano_not_found': 'Could not find a volcano within reasonable distance',

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
    'entity.tfc.horseshoe_crab': 'Horseshoe Crab',
    'entity.tfc.penguin': 'Penguin',
    'entity.tfc.turtle': 'Turtle',
    'entity.tfc.falling_block': 'Falling Block',
    'entity.tfc.fishing_bobber': 'Fishing Bobber',
    **{'entity.tfc.boat.%s' % wood : lang('%s boat', wood) for wood in WOODS.keys()},

    # Enums

    **dict(('tfc.enum.tier.tier_%s' % tier, 'Tier %s' % lang(tier)) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')),
    **lang_enum('heat', ('warming', 'hot', 'very_hot', 'faint_red', 'dark_red', 'bright_red', 'orange', 'yellow', 'yellow_white', 'white', 'brilliant_white')),
    **lang_enum('month', ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')),
    **lang_enum('day', ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')),
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'edge', 'none')),
    **lang_enum('koppenclimateclassification', ('arctic', 'tundra', 'subarctic', 'cold_desert', 'hot_desert', 'temperate', 'subtropical', 'humid_subtropical', 'humid_oceanic', 'humid_subtropical', 'tropical_savanna', 'tropical_rainforest')),
    **dict(('tfc.enum.platetectonicsclassification.%s' % k, v) for k, v in {
        'oceanic': 'Oceanic',
        'continental_low': 'Low Altitude Continental',
        'continental_mid': 'Mid Altitude Continental',
        'continental_high': 'High Altitude Continental',
        'ocean_ocean_diverging': 'Mid-Ocean Ridge',
        'ocean_ocean_converging_lower': 'Oceanic Subduction',
        'ocean_ocean_converging_upper': 'Oceanic Subduction',
        'ocean_continent_converging_lower': 'Continental Subduction',
        'ocean_continent_converging_upper': 'Continental Subduction',
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

    'tfc.thatch_bed.use': 'This bed is too uncomfortable to sleep in.',
    'tfc.thatch_bed.thundering': 'You are too scared to sleep.',
    'tfc.composter.rotten': 'This composter is smelly and might attract animals. You should empty it.',
    'tfc.composter.too_many_greens': 'This composter has enough green items',
    'tfc.composter.too_many_browns': 'This composter has enough brown items',

    **dict(('metal.tfc.%s' % metal, lang(metal)) for metal in METALS.keys()),

    'tfc.jei.heating': 'Heating Recipe',
    'tfc.jei.quern': 'Quern Recipe',
    'tfc.jei.scraping': 'Scraping Recipe',
    'tfc.jei.clay_knapping': 'Clay Knapping Recipe',
    'tfc.jei.fire_clay_knapping': 'Fire Clay Knapping Recipe',
    'tfc.jei.leather_knapping': 'Leather Knapping Recipe',
    'tfc.jei.rock_knapping': 'Rock Knapping Recipe',
    'tfc.jei.soup_pot': 'Soup Pot',
    'tfc.jei.fluid_pot': 'Fluid Pot',
    'tfc.jei.casting': 'Casting',
    'tfc.jei.alloying': 'Alloying',
}

# Automatically Generated by generate_trees.py
TREE_SAPLING_DROP_CHANCES = {
    'acacia': 0.0210,
    'ash': 0.0153,
    'aspen': 0.0306,
    'birch': 0.0306,
    'blackwood': 0.0557,
    'chestnut': 0.0153,
    'douglas_fir': 0.0388,
    'hickory': 0.0388,
    'kapok': 0.0067,
    'maple': 0.0153,
    'oak': 0.0057,
    'palm': 0.0651,
    'pine': 0.0388,
    'rosewood': 0.0057,
    'sequoia': 0.0170,
    'spruce': 0.0170,
    'sycamore': 0.0153,
    'white_cedar': 0.0204,
    'willow': 0.0092
}

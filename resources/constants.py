#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Dict, List, NamedTuple, Sequence, Optional

Rock = NamedTuple('Rock', category=str, desert_sand_color=str, beach_sand_color=str)
Metal = NamedTuple('Metal', tier=int, types=set, heat_capacity=float, melt_temperature=float)
MetalItem = NamedTuple('MetalItem', type=str, smelt_amount=int, parent_model=str, tag=Optional[str])
Ore = NamedTuple('Ore', metal=Optional[str], graded=bool)
OreGrade = NamedTuple('OreGrade', weight=int)
Vein = NamedTuple('Vein', ore=str, type=str, rarity=int, size=int, min_y=int, max_y=int, density=float, poor=float, normal=float, rich=float, rocks=List[str], spoiler_ore=str, spoiler_rarity=int, spoiler_rocks=List[str])
Plant = NamedTuple('Plant', clay=bool, min_temp=float, max_temp=float, min_rain=float, max_rain=float, type=str)
Wood = NamedTuple('Wood', temp=float, duration=int)
Berry = NamedTuple('Berry', min_temp=float, max_temp=float, min_rain=float, max_rain=float, type=str, min_forest=str, max_forest=str)
Fruit = NamedTuple('Fruit', min_temp=float, max_temp=float, min_rain=float, max_rain=float)


HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCK_ITEMS: List[str] = ['axe', 'axe_head', 'hammer', 'hammer_head', 'hoe', 'hoe_head', 'javelin', 'javelin_head', 'knife', 'knife_head', 'shovel', 'shovel_head']

ROCKS: Dict[str, Rock] = {
    # Desert sand color is either white, brown, black, or yellow based on rock color. red is an indicator of hematite rocks
    # Beach sand color is either white, gray, yellow, black or green based on rock color. Pink and black are used for accents in tropical regions.
    'chalk': Rock('sedimentary', 'white', 'white'),
    'chert': Rock('sedimentary', 'brown', 'yellow'),
    'claystone': Rock('sedimentary', 'brown', 'yellow'),
    'conglomerate': Rock('sedimentary', 'yellow', 'green'),
    'dolomite': Rock('sedimentary', 'black', 'black'),
    'limestone': Rock('sedimentary', 'yellow', 'white'),
    'shale': Rock('sedimentary', 'black', 'black'),
    'gneiss': Rock('metamorphic', 'brown', 'brown'),
    'marble': Rock('metamorphic', 'yellow', 'white'),
    'phyllite': Rock('metamorphic', 'brown', 'yellow'),
    'quartzite': Rock('metamorphic', 'white', 'white'),
    'schist': Rock('metamorphic', 'yellow', 'green'),
    'slate': Rock('metamorphic', 'brown', 'yellow'),
    'diorite': Rock('igneous_intrusive', 'white', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black', 'black'),
    'granite': Rock('igneous_intrusive', 'white', 'white'),
    'andesite': Rock('igneous_extrusive', 'red', 'green'),
    'basalt': Rock('igneous_extrusive', 'red', 'black'),
    'dacite': Rock('igneous_extrusive', 'red', 'yellow'),
    'rhyolite': Rock('igneous_extrusive', 'red', 'white')
}
METALS: Dict[str, Metal] = {
    'bismuth': Metal(1, {'part'}, 0.14, 270),
    'bismuth_bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 985),
    'black_bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 1070),
    'bronze': Metal(2, {'part', 'tool', 'armor', 'utility'}, 0.35, 950),
    'brass': Metal(2, {'part'}, 0.35, 930),
    'copper': Metal(1, {'part', 'tool', 'armor', 'utility'}, 0.35, 1080),
    'gold': Metal(1, {'part'}, 0.6, 1060),
    'nickel': Metal(1, {'part'}, 0.48, 1453),
    'rose_gold': Metal(1, {'part'}, 0.35, 960),
    'silver': Metal(1, {'part'}, 0.48, 961),
    'tin': Metal(1, {'part'}, 0.14, 230),
    'zinc': Metal(1, {'part'}, 0.21, 420),
    'sterling_silver': Metal(1, {'part'}, 0.35, 950),
    'wrought_iron': Metal(3, {'part', 'tool', 'armor', 'utility'}, 0.35, 1535),
    'cast_iron': Metal(1, {'part'}, 0.35, 1535),
    'pig_iron': Metal(3, set(), 0.35, 1535),
    'steel': Metal(4, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540),
    'black_steel': Metal(5, {'part', 'tool', 'armor', 'utility'}, 0.35, 1485),
    'blue_steel': Metal(6, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540),
    'red_steel': Metal(6, {'part', 'tool', 'armor', 'utility'}, 0.35, 1540),
    'weak_steel': Metal(4, set(), 0.35, 1540),
    'weak_blue_steel': Metal(5, set(), 0.35, 1540),
    'weak_red_steel': Metal(5, set(), 0.35, 1540),
    'high_carbon_steel': Metal(3, set(), 0.35, 1540),
    'high_carbon_black_steel': Metal(4, set(), 0.35, 1540),
    'high_carbon_blue_steel': Metal(5, set(), 0.35, 1540),
    'high_carbon_red_steel': Metal(5, set(), 0.35, 1540),
    'unknown': Metal(0, set(), 0.5, 1250)
}
METAL_BLOCKS: Dict[str, MetalItem] = {
    'anvil': MetalItem('utility', 1400, 'tfc:block/anvil', None),
    'lamp': MetalItem('utility', 100, 'tfc:block/lamp', None)
}
METAL_ITEMS: Dict[str, MetalItem] = {
    'ingot': MetalItem('all', 100, 'item/generated', 'forge:ingots'),
    'double_ingot': MetalItem('part', 200, 'item/generated', 'forge:double_ingots'),
    'sheet': MetalItem('part', 200, 'item/generated', 'forge:sheets'),
    'double_sheet': MetalItem('part', 400, 'item/generated', 'forge:double_sheets'),
    'rod': MetalItem('part', 100, 'item/generated', 'forge:rods'),

    'tuyere': MetalItem('tool', 100, 'item/generated', None),
    'pickaxe': MetalItem('tool', 100, 'item/handheld', None),
    'pickaxe_head': MetalItem('tool', 100, 'item/generated', None),
    'shovel': MetalItem('tool', 100, 'item/handheld', None),
    'shovel_head': MetalItem('tool', 100, 'item/generated', None),
    'axe': MetalItem('tool', 100, 'item/handheld', None),
    'axe_head': MetalItem('tool', 100, 'item/generated', None),
    'hoe': MetalItem('tool', 100, 'item/handheld', None),
    'hoe_head': MetalItem('tool', 100, 'item/generated', None),
    'chisel': MetalItem('tool', 100, 'item/handheld', None),
    'chisel_head': MetalItem('tool', 100, 'item/generated', None),
    'sword': MetalItem('tool', 100, 'item/handheld', None),
    'sword_blade': MetalItem('tool', 100, 'item/generated', None),
    'mace': MetalItem('tool', 100, 'item/handheld', None),
    'mace_head': MetalItem('tool', 100, 'item/generated', None),
    'saw': MetalItem('tool', 100, 'item/handheld', None),
    'saw_blade': MetalItem('tool', 100, 'item/generated', None),
    'javelin': MetalItem('tool', 100, 'item/handheld', None),
    'javelin_head': MetalItem('tool', 100, 'item/generated', None),
    'hammer': MetalItem('tool', 100, 'item/handheld', None),
    'hammer_head': MetalItem('tool', 100, 'item/generated', None),
    'propick': MetalItem('tool', 100, 'item/handheld', None),
    'propick_head': MetalItem('tool', 100, 'item/generated', None),
    'knife': MetalItem('tool', 100, 'tfc:item/handheld_flipped', None),
    'knife_blade': MetalItem('tool', 100, 'item/generated', None),
    'scythe': MetalItem('tool', 100, 'item/handheld', None),
    'scythe_blade': MetalItem('tool', 100, 'item/generated', None),
    'shears': MetalItem('tool', 200, 'item/handheld', None),

    'unfinished_helmet': MetalItem('armor', 400, 'item/generated', None),
    'helmet': MetalItem('armor', 600, 'item/generated', None),
    'unfinished_chestplate': MetalItem('armor', 400, 'item/generated', None),
    'chestplate': MetalItem('armor', 800, 'item/generated', None),
    'unfinished_greaves': MetalItem('armor', 400, 'item/generated', None),
    'greaves': MetalItem('armor', 600, 'item/generated', None),
    'unfinished_boots': MetalItem('armor', 200, 'item/generated', None),
    'boots': MetalItem('armor', 400, 'item/generated', None),

    'shield': MetalItem('tool', 400, 'item/handheld', None)
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
    'halite': Ore(None, False),
    'amethyst': Ore(None, False),
    'diamond': Ore(None, False),
    'emerald': Ore(None, False),
    'lapis_lazuli': Ore(None, False),
    'opal': Ore(None, False),
    'pyrite': Ore(None, False),
    'ruby': Ore(None, False),
    'sapphire': Ore(None, False),
    'topaz': Ore(None, False)
}
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50),
    'poor': OreGrade(30),
    'rich': OreGrade(20)
}


def vein(ore: str, vein_type: str, rarity: int, size: int, min_y: int, max_y: int, density: float, poor: float, normal: float, rich: float, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None):
    # Factory method to allow default values
    return Vein(ore, vein_type, rarity, size, min_y, max_y, density, poor, normal, rich, rocks, spoiler_ore, spoiler_rarity, spoiler_rocks)


ORE_VEINS: Dict[str, Vein] = {
    'normal_native_copper': vein('native_copper', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'surface_native_copper': vein('native_copper', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['igneous_extrusive']),
    'normal_native_gold': vein('native_gold', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 20, ['igneous_extrusive', 'igneous_intrusive']),
    'deep_native_gold': vein('native_gold', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 10, ['igneous_extrusive', 'igneous_intrusive']),
    'normal_native_silver': vein('native_silver', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['granite', 'gneiss']),
    'poor_native_silver': vein('native_silver', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['granite', 'metamorphic']),
    'normal_hematite': vein('hematite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'deep_hematite': vein('hematite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive']),
    'normal_cassiterite': vein('cassiterite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive'], 'topaz', 10, ['granite']),
    'surface_cassiterite': vein('cassiterite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['igneous_intrusive'], 'topaz', 20, ['granite']),
    'normal_bismuthinite': vein('bismuthinite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive', 'sedimentary']),
    'surface_bismuthinite': vein('bismuthinite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['igneous_intrusive', 'sedimentary']),
    'normal_garnierite': vein('garnierite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['gabbro']),
    'poor_garnierite': vein('garnierite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['igneous_intrusive']),
    'normal_malachite': vein('malachite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['marble', 'limestone'], 'gypsum', 10, ['limestone']),
    'poor_malachite': vein('malachite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['marble', 'limestone', 'phyllite', 'chalk', 'dolomite'], 'gypsum', 20, ['limestone']),
    'normal_magnetite': vein('magnetite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary']),
    'deep_magnetite': vein('magnetite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary']),
    'normal_limonite': vein('limonite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary'], 'ruby', 20, ['limestone', 'shale']),
    'deep_limonite': vein('limonite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary'], 'ruby', 10, ['limestone', 'shale']),
    'normal_sphalerite': vein('sphalerite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic']),
    'surface_sphalerite': vein('sphalerite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['metamorphic']),
    'normal_tetrahedrite': vein('tetrahedrite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic']),
    'surface_tetrahedrite': vein('tetrahedrite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['metamorphic']),

    'bituminous_coal': vein('bituminous_coal', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'lignite': vein('lignite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'kaolinite': vein('kaolinite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary']),
    'graphite': vein('graphite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['gneiss', 'marble', 'quartzite', 'schist']),
    'cinnabar': vein('cinnabar', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['igneous_extrusive', 'quartzite', 'shale'], 'opal', 10, ['quartzite']),
    'cryolite': vein('cryolite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['granite']),
    'saltpeter': vein('saltpeter', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary'], 'gypsum', 20, ['limestone']),
    'sulfur': vein('sulfur', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['igneous_extrusive'], 'gypsum', 20, ['rhyolite']),
    'sylvite': vein('sylvite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['shale', 'claystone', 'chert']),
    'borax': vein('borax', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['slate']),
    'gypsum': vein('gypsum', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['metamorphic']),
    'lapis_lazuli': vein('lapis_lazuli', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['limestone', 'marble']),
    'halite': vein('halite', 'disc', 120, 30, 80, 100, 80, 0, 0, 0, ['sedimentary']),

    'diamond': vein('diamond', 'pipe', 60, 60, 5, 140, 40, 0, 0, 0, ['gabbro']),
    'emerald': vein('emerald', 'pipe', 80, 60, 5, 140, 40, 0, 0, 0, ['igneous_intrusive']),
}

ROCK_BLOCK_TYPES = ('raw', 'hardened', 'bricks', 'cobble', 'gravel', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks', 'chiseled', 'spike', 'loose')
CUTTABLE_ROCKS = ('raw', 'bricks', 'cobble', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks')
ROCK_SPIKE_PARTS = ('base', 'middle', 'tip')
SAND_BLOCK_TYPES = ('brown', 'white', 'black', 'red', 'yellow', 'green', 'pink')
SOIL_BLOCK_TYPES = ('dirt', 'grass', 'grass_path', 'clay', 'clay_grass')
SOIL_BLOCK_VARIANTS = ('silt', 'loam', 'sandy_loam', 'silty_loam')

GEMS = ('amethyst', 'diamond', 'emerald', 'lapis_lazuli', 'opal', 'pyrite', 'ruby', 'sapphire', 'topaz')

MISC_GROUNDCOVER = ['bone', 'clam', 'driftwood', 'mollusk', 'mussel', 'pinecone', 'seaweed', 'stick', 'dead_grass', 'feather', 'flint', 'guano', 'podzol', 'rotten_flesh', 'salt_lick']

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

PLANTS: Dict[str, Plant] = {
    'allium': Plant(False, 10, 18, 150, 400, 'standard'),
    'athyrium_fern': Plant(True, 20, 30, 200, 500, 'standard'),
    'badderlocks': Plant(False, -20, 20, 150, 500, 'emergent'),
    'barrel_cactus': Plant(False, -6, 50, 0, 85, 'cactus'),
    'black_orchid': Plant(False, 30, 41, 290, 410, 'standard'),
    'blood_lily': Plant(False, 33, 45, 200, 500, 'standard'),
    'blue_orchid': Plant(False, 8, 16, 250, 390, 'standard'),
    'butterfly_milkweed': Plant(False, -40, 25, 75, 300, 'standard'),
    'calendula': Plant(False, -5, 15, 130, 300, 'standard'),
    'canna': Plant(True, 30, 50, 270, 500, 'standard'),
    'cattail': Plant(False, -10, 50, 150, 500, 'emergent_fresh'),
    'coontail': Plant(False, 5, 50, 250, 500, 'grass_water_fresh'),
    'dandelion': Plant(False, 0, 22, 120, 400, 'standard'),
    'dead_bush': Plant(False, 10, 50, 0, 120, 'dry'),
    'duckweed': Plant(False, -34, 25, 0, 500, 'floating_fresh'),
    'eel_grass': Plant(False, -10, 25, 200, 500, 'grass_water_fresh'),
    'field_horsetail': Plant(False, 10, 26, 300, 500, 'standard'),
    'fountain_grass': Plant(False, -12, 40, 75, 150, 'short_grass'),
    'foxglove': Plant(False, 15, 29, 150, 300, 'tall_plant'),
    'goldenrod': Plant(True, 17, 33, 75, 310, 'standard'),
    'grape_hyacinth': Plant(False, 5, 33, 150, 250, 'standard'),
    'gutweed': Plant(False, -50, 50, 100, 500, 'water'),
    'guzmania': Plant(False, 15, 29, 290, 480, 'epiphyte'),
    'houstonia': Plant(False, -46, 10, 150, 500, 'standard'),
    'labrador_tea': Plant(False, -5, 9, 200, 380, 'standard'),
    'lady_fern': Plant(False, -50, 16, 200, 490, 'standard'),
    'licorice_fern': Plant(False, -15, 25, 300, 400, 'epiphyte'),
    'laminaria': Plant(False, -50, 12, 100, 500, 'water'),
    'lotus': Plant(False, 15, 50, 0, 500, 'floating_fresh'),
    'manatee_grass': Plant(False, 25, 50, 250, 500, 'grass_water'),
    'marigold': Plant(False, 20, 50, 50, 390, 'emergent_fresh'),
    'meads_milkweed': Plant(False, -10, 14, 130, 380, 'standard'),
    'milfoil': Plant(False, 5, 50, 250, 500, 'water_fresh'),
    'morning_glory': Plant(False, -11, 19, 300, 500, 'creeping'),
    'moss': Plant(False, -7, 30, 250, 450, 'creeping'),
    'nasturtium': Plant(False, -19, 0, 150, 380, 'standard'),
    'orchard_grass': Plant(False, -29, 27, 75, 300, 'short_grass'),
    'ostrich_fern': Plant(False, -49, 14, 290, 470, 'tall_plant'),
    'oxeye_daisy': Plant(False, 10, 40, 120, 300, 'standard'),
    'pampas_grass': Plant(True, 20, 50, 0, 200, 'tall_grass'),
    'perovskia': Plant(True, -50, 20, 0, 200, 'dry'),
    'pistia': Plant(False, 10, 45, 0, 400, 'floating_fresh'),
    'poppy': Plant(False, -40, 36, 150, 250, 'standard'),
    'primrose': Plant(False, -34, 33, 150, 300, 'standard'),
    'pulsatilla': Plant(False, -50, 30, 50, 200, 'standard'),
    'reindeer_lichen': Plant(False, 10, 33, 50, 470, 'creeping'),
    'rose': Plant(True, -5, 20, 150, 300, 'tall_plant'),
    'ryegrass': Plant(False, -10, 35, 150, 320, 'short_grass'),
    'sacred_datura': Plant(False, 18, 31, 75, 150, 'standard'),
    'sago': Plant(False, -10, 50, 200, 500, 'water_fresh'),
    'sagebrush': Plant(False, -34, 50, 0, 120, 'dry'),
    'sapphire_tower': Plant(False, 16, 39, 75, 200, 'tall_plant'),
    'sargassum': Plant(False, 0, 25, 0, 500, 'floating'),
    'scutch_grass': Plant(False, 0, 50, 150, 500, 'short_grass'),
    'snapdragon_pink': Plant(False, 24, 36, 150, 300, 'standard'),
    'snapdragon_red': Plant(False, 24, 36, 150, 300, 'standard'),
    'snapdragon_white': Plant(False, 24, 36, 150, 300, 'standard'),
    'snapdragon_yellow': Plant(False, 24, 36, 150, 300, 'standard'),
    'spanish_moss': Plant(False, 35, 41, 400, 500, 'hanging'),
    'star_grass': Plant(False, -50, 50, 50, 260, 'grass_water'),
    'strelitzia': Plant(False, 35, 50, 50, 300, 'standard'),
    'switchgrass': Plant(False, -29, 29, 110, 390, 'tall_grass'),
    'sword_fern': Plant(False, -40, 25, 100, 500, 'standard'),
    'tall_fescue_grass': Plant(False, -10, 15, 280, 430, 'tall_grass'),
    'timothy_grass': Plant(False, 15, 29, 289, 500, 'short_grass'),
    'toquilla_palm': Plant(False, 25, 50, 250, 500, 'tall_plant'),
    'trillium': Plant(False, 10, 33, 150, 300, 'standard'),
    'tropical_milkweed': Plant(False, 28, 41, 120, 300, 'standard'),
    'tulip_orange': Plant(False, -34, 0, 100, 200, 'standard'),
    'tulip_pink': Plant(False, -34, 0, 100, 200, 'standard'),
    'tulip_red': Plant(False, -34, 0, 100, 200, 'standard'),
    'tulip_white': Plant(False, -34, 0, 100, 200, 'standard'),
    'turtle_grass': Plant(False, -50, 25, 240, 500, 'grass_water'),
    'vriesea': Plant(False, 22, 31, 200, 400, 'epiphyte'),
    'water_canna': Plant(True, 0, 36, 150, 500, 'floating_fresh'),
    'water_lily': Plant(False, -5, 38, 0, 500, 'floating_fresh'),
    'yucca': Plant(False, -34, 36, 0, 75, 'dry')
}

SIMPLE_ITEMS = ('alabaster_brick', 'brass_mechanisms', 'burlap_cloth', 'dirty_jute_net', 'fire_clay', 'firestarter', 'glass_shard', 'glue',
                'halter', 'jute', 'jute_disc', 'jute_fiber', 'jute_net', 'mortar', 'olive_jute_disc', 'olive_paste', 'silk_cloth', 'spindle',
                'stick_bunch', 'stick_bundle', 'straw', 'wool', 'wool_cloth', 'wool_yarn', 'wrought_iron_grill')
GENERIC_POWDERS = ('charcoal', 'coke', 'graphite', 'hematite', 'kaolinite', 'limonite', 'malachite')
POWDERS = ('fertilizer', 'flux', 'salt', 'saltpeter', 'sulfur', 'wood_ash')
PAIRED_POTTERY = ('bowl', 'fire_brick', 'pot', 'spindle_head', 'vessel')
UNFIRED_ITEMS = ('brick', 'crucible', 'flower_pot', 'jug')
VANILLA_TOOL_MATERIALS = ('netherite', 'diamond', 'iron', 'stone', 'wooden', 'golden')
SHORE_DECORATORS = ['driftwood', 'clam', 'mollusk', 'mussel', 'sticks_shore', 'seaweed']
FOREST_DECORATORS = ['sticks_forest', 'pinecone', 'salt_lick', 'dead_grass', 'podzol']
OCEAN_PLANT_TYPES = ['grass_water', 'floating', 'water', 'emergent', 'tall_water']
MISC_PLANT_FEATURES = ['hanging_vines', 'hanging_vines_cave', 'ivy', 'jungle_vines', 'liana']

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

# This is here because it's used all over, and it's easier to import with all constants
def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()


def lang_enum(name: str, values: Sequence[str]) -> Dict[str, str]:
    return dict(('tfc.enum.%s.%s' % (name, value), lang(value)) for value in values)


# This is here as it's used only once in a generic lang call by generate_resources.py
DEFAULT_LANG = {
    # Misc
    'generator.tfc.tng': 'TerraFirmaCraft',
    'death.attack.grill': '%1$s grilled themself to death',
    'death.attack.grill.player': '%1$s grilled themselves while trying to escape %2$s',
    'death.attack.pot': '%1$s boiled themselves into soup',
    'death.attack.pot.player': '%1$s boiled themself while trying to escape %2$s',
    'tfc.tile_entity.pot': 'Pot',
    'tfc.tile_entity.grill': 'Grill',
    'tfc.tile_entity.firepit': 'Firepit',
    'tfc.tile_entity.log_pile': 'Log Pile',
    # Item groups
    'itemGroup.tfc.earth': 'TFC Earth',
    'itemGroup.tfc.ores': 'TFC Ores',
    'itemGroup.tfc.rock': 'TFC Rock Stuffs',
    'itemGroup.tfc.metal': 'TFC Metal Stuffs',
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
    # Tooltips
    'tfc.tooltip.metal': '§fMetal:§7 %s',
    'tfc.tooltip.units': '%d units',
    'tfc.tooltip.forging': '§f - Can Work',
    'tfc.tooltip.welding': '§f - Can Weld',
    'tfc.tooltip.fuel': 'Fuel Duration: %d Temp: %d',
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
    'tfc.tooltip.debug_times': 'PT: %d | CT: %d | DT: %d',
    'tfc.tooltip.f3_rainfall': 'Rainfall: %s',
    'tfc.tooltip.f3_average_temperature': 'Avg. Temp: %s\u00b0C',
    'tfc.tooltip.f3_temperature': 'Actual. Temp: %s\u00b0C',
    'tfc.tooltip.f3_forest_type': 'Forest Type: ',
    'tfc.tooltip.f3_forest_properties': 'Forest Density = %s, Weirdness = %s',
    'tfc.tooltip.f3_invalid_chunk_data': 'Invalid Chunk Data',

    # Commands
    'tfc.commands.time.query.daytime': 'The day time is %s',
    'tfc.commands.time.query.game_time': 'The game time is %s',
    'tfc.commands.time.query.day': 'The day is %s',
    'tfc.commands.time.query.player_ticks': 'The player ticks is %s',
    'tfc.commands.time.query.calendar_ticks': 'The calendar ticks is %s',
    'tfc.commands.heat.set_heat': 'Held item heat set to %s',
    'tfc.commands.clear_world.starting': 'Clearing world. Prepare for lag...',
    'tfc.commands.clear_world.done': 'Cleared %d Block(s).',
    'tfc.commands.player.query_hunger': 'Hunger is %s',
    'tfc.commands.player.query_saturation': 'Saturation is %s',

    # ENUMS

    **dict(('tfc.enum.tier.tier_%s' % tier, 'Tier %s' % lang(tier)) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')),
    **lang_enum('heat', ('warming', 'hot', 'very_hot', 'faint_red', 'dark_red', 'bright_red', 'orange', 'yellow', 'yellow_white', 'white', 'brilliant_white')),
    **lang_enum('month', ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')),
    **lang_enum('day', ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')),
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'edge', 'none')),
    **lang_enum('koppenclimateclassification', ('arctic', 'tundra', 'subarctic', 'cold_desert', 'hot_desert', 'temperate', 'subtropical', 'humid_subtropical', 'humid_oceanic', 'humid_subtropical', 'tropical_savanna', 'tropical_rainforest')),
    'tfc.enum.platetectonicsclassification.oceanic': 'Oceanic Plate',
    'tfc.enum.platetectonicsclassification.continental_low': 'Low Altitude Continental',
    'tfc.enum.platetectonicsclassification.continental_mid': 'Mid Altitude Continental',
    'tfc.enum.platetectonicsclassification.continental_high': 'High Altitude Continental',
    'tfc.enum.platetectonicsclassification.ocean_ocean_diverging': 'Oceanic Rift',
    'tfc.enum.platetectonicsclassification.ocean_ocean_converging': 'Oceanic Orogeny',
    'tfc.enum.platetectonicsclassification.ocean_continent_diverging': 'Coastal Rift',
    'tfc.enum.platetectonicsclassification.ocean_continent_converging': 'Subduction Zone',
    'tfc.enum.platetectonicsclassification.continent_continent_diverging': 'Continental Rift',
    'tfc.enum.platetectonicsclassification.continent_continent_converging': 'Orogenic Belt',
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
    'tfc.thatch_bed.use': 'This bed is too uncomfortable to sleep in.',
    'tfc.thatch_bed.thundering': 'You are too scared to sleep.',

    **dict(('metal.tfc.%s' % metal, lang('%s' % metal)) for metal in METALS.keys())
}

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Dict, List, NamedTuple, Sequence, Optional

Rock = NamedTuple('Rock', category=str, desert_sand_color=str, beach_sand_color=str)
Metal = NamedTuple('Metal', tier=int, types=set, heat_capacity=float, melt_temperature=float)
MetalItem = NamedTuple('MetalItem', type=str, smelt_amount=int, parent_model=str, tag=Optional[str])
Ore = NamedTuple('Ore', metal=Optional[str], graded=bool)
OreGrade = NamedTuple('OreGrade', weight=int)
Vein = NamedTuple('Vein', ore=str, type=str, rarity=int, size=int, min_y=int, max_y=int, density=float, poor=float, normal=float, rich=float, rocks=List[str], constituent=any, c_rarity=int, c_rocks=any)
Plant = NamedTuple('Plant', clay=bool, min_temp=float, max_temp=float, min_rain=float, max_rain=float, type=str)

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

ORE_VEINS: Dict[str, Vein] = {
    'normal_native_copper': Vein('native_copper', 'cluster', 40, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive'], None, 0, None),
    'surface_native_copper': Vein('native_copper', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['igneous_extrusive'], None, 0, None),
    'normal_native_gold': Vein('native_gold', 'cluster', 115, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 5, ['igneous_extrusive', 'igneous_intrusive']),
    'deep_native_gold': Vein('native_gold', 'cluster', 150, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 2, ['igneous_extrusive', 'igneous_intrusive']),
    'mixed_native_gold': Vein('native_gold', 'cluster', 180, 20, 30, 100, 60, 30, 20, 5, ['igneous_extrusive', 'igneous_intrusive'], 'pyrite', 80, ['igneous_extrusive', 'igneous_intrusive']),
    'normal_native_silver': Vein('native_silver', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['granite', 'gneiss'], None, 0, None),
    'poor_native_silver': Vein('native_silver', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['granite', 'metamorphic'], None, 0, None),
    'normal_hematite': Vein('hematite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive'], None, 0, None),
    'deep_hematite': Vein('hematite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive'], None, 0, None),
    'normal_cassiterite': Vein('cassiterite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive'], 'topaz', 5, ['granite']),
    'surface_cassiterite': Vein('cassiterite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_intrusive'], None, 0, None),
    'normal_bismuthinite': Vein('bismuthinite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive', 'sedimentary'], None, 0, None),
    'surface_bismuthinite': Vein('bismuthinite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['igneous_intrusive', 'sedimentary'], None, 0, None),
    'normal_garnierite': Vein('garnierite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['gabbro'], None, 0, None),
    'poor_garnierite': Vein('garnierite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['igneous_intrusive'], None, 0, None),
    'normal_malachite': Vein('malachite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['marble', 'limestone'], 'gypsum', 4, ['limestone']),
    'poor_malachite': Vein('malachite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['marble', 'limestone', 'phyllite', 'chalk', 'dolomite'], 'gypsum', 10, ['limestone']),
    'normal_magnetite': Vein('magnetite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary'], None, 0, None),
    'deep_magnetite': Vein('magnetite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary'], None, 0, None),
    'normal_limonite': Vein('limonite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['sedimentary'], 'ruby', 3, ['limestone', 'shale']),
    'deep_limonite': Vein('limonite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['sedimentary'], 'ruby', 10, ['limestone', 'shale']),
    'normal_sphalerite': Vein('sphalerite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic'], None, 0, None),
    'surface_sphalerite': Vein('sphalerite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['metamorphic'], None, 0, None),
    'normal_tetrahedrite': Vein('tetrahedrite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['metamorphic'], None, 0, None),
    'surface_tetrahedrite': Vein('tetrahedrite', 'cluster', 40, 15, 60, 160, 60, 60, 30, 10, ['metamorphic'], None, 0, None),

    'amethyst': Vein('amethyst', 'cluster', 120, 7, 5, 160, 95, 0, 0, 0, ['igneous_extrusive'], None, 0, None),
    'bituminous_coal': Vein('bituminous_coal', 'disc', 135, 25, 5, 150, 80, 0, 0, 0, ['sedimentary'], None, 0, None),
    'borax': Vein('borax', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['slate'], None, 0, None),
    'cinnabar': Vein('cinnabar', 'cluster', 100, 10, 5, 100, 60, 0, 0, 0, ['igneous_extrusive', 'quartzite', 'shale'], 'opal', 3, ['quartzite']),
    'cryolite': Vein('cryolite', 'cluster', 100, 10, 5, 100, 60, 0, 0, 0, ['granite', 'quartzite', 'dolomite'], None, 0, None),
    'diamond': Vein('diamond', 'pipe', 60, 60, 5, 140, 40, 0, 0, 0, ['gabbro'], None, 0, None),
    'emerald': Vein('emerald', 'pipe', 80, 60, 5, 140, 40, 0, 0, 0, ['igneous_intrusive'], None, 0, None),
    'graphite': Vein('graphite', 'cluster', 120, 10, 5, 90, 60, 0, 0, 0, ['gneiss', 'marble', 'quartzite', 'schist'], None, 0, None),
    'gypsum': Vein('gypsum', 'disc', 120, 40, 50, 160, 60, 0, 0, 0, ['sedimentary'], None, 0, None),
    'halite': Vein('halite', 'disc', 120, 30, 80, 100, 80, 0, 0, 0, ['sedimentary'], 'sylvite', 8, ['chalk']),
    'kaolinite': Vein('kaolinite', 'cluster', 120, 10, 40, 130, 60, 0, 0, 0, ['sedimentary'], None, 0, None),
    'lapis_lazuli': Vein('lapis_lazuli', 'cluster', 120, 14, 5, 64, 70, 0, 0, 0, ['metamorphic'], None, 0, None),
    'lignite': Vein('lignite', 'cluster', 120, 10, 5, 100, 60, 0, 0, 0, ['sedimentary'], None, 0, None),
    'opal': Vein('opal', 'cluster', 120, 10, 70, 140, 50, 0, 0, 0, ['basalt', 'rhyolite'], None, 0, None),
    'pyrite': Vein('pyrite', 'cluster', 190, 16, 30, 100, 60, 0, 0, 0, ['igneous_extrusive', 'igneous_intrusive'], None, 0, None),
    'saltpeter': Vein('saltpeter', 'cluster', 110, 10, 5, 100, 60, 0, 0, 0, ['sedimentary'], 'gypsum', 20, ['limestone']),
    'sulfur': Vein('sulfur', 'cluster', 120, 10, 5, 100, 70, 0, 0, 0, ['igneous_extrusive'], 'gypsum', 20, ['rhyolite']),
    'sylvite': Vein('sylvite', 'cluster', 120, 13, 5, 96, 80, 0, 0, 0, ['shale', 'claystone', 'chert'], 'halite', 5, ['shale']),
    'topaz': Vein('topaz', 'cluster', 140, 10, 80, 160, 45, 0, 0, 0, ['granite', 'rhyolite'], None, 0, None),
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

WOODS = ('acacia', 'ash', 'aspen', 'birch', 'blackwood', 'chestnut', 'douglas_fir', 'hickory', 'kapok', 'maple', 'oak', 'palm', 'pine', 'rosewood', 'sequoia', 'spruce', 'sycamore', 'white_cedar', 'willow')

PLANTS: Dict[str, Plant] = {
    'allium': Plant(False, -40, 33, 150, 500, 'standard'),
    'athyrium_fern': Plant(True, -35, 31, 200, 500, 'short_grass'),
    'barrel_cactus': Plant(False, -6, 50, 0, 75, 'tall_grass'),
    'black_orchid': Plant(False, 10, 50, 300, 500, 'standard'),
    'blood_lily': Plant(False, 10, 50, 200, 500, 'standard'),
    'blue_orchid': Plant(False, 10, 50, 300, 500, 'standard'),
    'butterfly_milkweed': Plant(False, -40, 32, 75, 300, 'standard'),
    'calendula': Plant(False, -46, 30, 130, 300, 'standard'),
    'canna': Plant(True, -12, 36, 150, 500, 'short_grass'),
    'dandelion': Plant(False, -40, 40, 75, 400, 'standard'),
    'duckweed': Plant(False, -34, 38, 0, 500, 'floating'),
    'field_horsetail': Plant(False, -40, 33, 300, 500, 'short_grass'),
    'fountain_grass': Plant(False, -12, 40, 75, 150, 'short_grass'),
    'foxglove': Plant(False, -34, 34, 150, 300, 'tall_grass'),
    'goldenrod': Plant(True, -29, 32, 75, 300, 'short_grass'),
    'grape_hyacinth': Plant(False, -34, 32, 150, 250, 'standard'),
    'guzmania': Plant(False, 15, 50, 300, 500, 'epiphyte'),
    'houstonia': Plant(False, -46, 36, 150, 500, 'standard'),
    'labrador_tea': Plant(False, -50, 33, 300, 500, 'standard'),
    'lady_fern': Plant(False, -34, 32, 200, 500, 'short_grass'),
    'licorice_fern': Plant(False, -29, 25, 300, 500, 'epiphyte'),
    'lotus': Plant(False, 10, 50, 0, 500, 'standard'),
    'meads_milkweed': Plant(False, -23, 31, 130, 500, 'standard'),
    'morning_glory': Plant(False, -40, 31, 150, 500, 'creeping'),
    'moss': Plant(False, -7, 36, 250, 500, 'creeping'),
    'nasturtium': Plant(False, -46, 38, 150, 500, 'standard'),
    'orchard_grass': Plant(False, -29, 30, 75, 300, 'short_grass'),
    'ostrich_fern': Plant(False, -40, 33, 300, 500, 'tall_grass'),
    'oxeye_daisy': Plant(False, -40, 33, 120, 300, 'standard'),
    'pampas_grass': Plant(True, -12, 36, 75, 200, 'tall_grass'),
    'perovskia': Plant(True, -29, 32, 0, 200, 'short_grass'),
    'pistia': Plant(False, 10, 50, 0, 500, 'floating'),
    'poppy': Plant(False, -40, 36, 150, 250, 'standard'),
    'primrose': Plant(False, -34, 33, 150, 300, 'standard'),
    'pulsatilla': Plant(False, -50, 30, 50, 200, 'standard'),
    'reindeer_lichen': Plant(False, -50, 33, 50, 500, 'creeping'),
    'rose': Plant(True, -29, 34, 150, 300, 'tall_grass'),
    'ryegrass': Plant(False, -46, 32, 150, 300, 'short_grass'),
    'sacred_datura': Plant(False, 5, 33, 75, 150, 'short_grass'),
    'sagebrush': Plant(False, -34, 50, 0, 100, 'short_grass'),
    'sapphire_tower': Plant(False, -6, 38, 75, 200, 'tall_grass'),
    'sargassum': Plant(False, 0, 38, 0, 500, 'floating'),
    'scutch_grass': Plant(False, -17, 50, 150, 500, 'short_grass'),
    'snapdragon_pink': Plant(False, -28, 36, 150, 300, 'standard'),
    'snapdragon_red': Plant(False, -28, 36, 150, 300, 'standard'),
    'snapdragon_white': Plant(False, -28, 36, 150, 300, 'standard'),
    'snapdragon_yellow': Plant(False, -28, 36, 150, 300, 'standard'),
    'spanish_moss': Plant(False, 0, 40, 300, 500, 'hanging'),
    'strelitzia': Plant(False, 5, 50, 50, 300, 'standard'),
    'switchgrass': Plant(False, -29, 32, 100, 300, 'tall_grass'),
    'sword_fern': Plant(False, -40, 30, 100, 500, 'short_grass'),
    'tall_fescue_grass': Plant(False, -29, 30, 300, 500, 'tall_grass'),
    'timothy_grass': Plant(False, -46, 30, 300, 500, 'short_grass'),
    'toquilla_palm': Plant(False, 10, 50, 250, 500, 'tall_grass'),
    'tree_fern': Plant(False, 10, 50, 300, 500, 'tall_grass'),
    'trillium': Plant(False, -34, 33, 150, 300, 'standard'),
    'tropical_milkweed': Plant(False, -6, 36, 120, 300, 'standard'),
    'tulip_orange': Plant(False, -34, 33, 100, 200, 'standard'),
    'tulip_pink': Plant(False, -34, 33, 100, 200, 'standard'),
    'tulip_red': Plant(False, -34, 33, 100, 200, 'standard'),
    'tulip_white': Plant(False, -34, 33, 100, 200, 'standard'),
    'vriesea': Plant(False, 15, 50, 300, 500, 'epiphyte'),
    'water_canna': Plant(True, -12, 36, 150, 500, 'floating'),
    'water_lily': Plant(False, -34, 38, 0, 500, 'floating'),
    'yucca': Plant(False, -34, 36, 0, 75, 'standard')
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
    # Containers
    'tfc.screen.calendar': 'Calendar',
    'tfc.screen.nutrition': 'Nutrition',
    'tfc.screen.climate': 'Climate',
    # Tooltips
    'tfc.tooltip.metal': '§fMetal:§7 %s',
    'tfc.tooltip.units': '%d units',
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
    'tfc.tooltip.debug_times': 'PT: %d | CT: %d | DT: %d',
    'tfc.tooltip.f3_rainfall': 'Rainfall: %s',
    'tfc.tooltip.f3_average_temperature': 'Avg. Temp: %s\u00b0C',
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
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'none')),
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

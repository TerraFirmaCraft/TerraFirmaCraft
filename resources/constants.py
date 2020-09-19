#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from collections import namedtuple
from typing import Dict, List, NamedTuple, Sequence

Rock = NamedTuple('Rock', category=str, desert_sand_color=str, beach_sand_color=str)
Metal = NamedTuple('Metal', tier=int, types=set, heat_capacity=float, melt_temperature=float)
MetalItem = namedtuple('MetalItem', ('type', 'smelt_amount', 'parent_model', 'tag'))
Ore = namedtuple('Ore', ('metal', 'graded'))
OreGrade = namedtuple('OreGrade', ('weight',))
Vein = namedtuple('Vein', ('ore', 'type', 'rarity', 'size', 'min_y', 'max_y', 'density', 'poor', 'normal', 'rich', 'rocks'))

HORIZONTAL_DIRECTIONS: List[str] = ['east', 'west', 'north', 'south']

ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ROCK_ITEMS: List[str] = ['axe', 'axe_head', 'hammer', 'hammer_head', 'hoe', 'hoe_head', 'javelin', 'javelin_head', 'knife', 'knife_head', 'shovel', 'shovel_head']
MISC_ROCK_ITEMS: List[str] = ['brick', 'rock']

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
    'cast_iron': Metal(1, {'part', 'tool'}, 0.35, 1535),
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
    'normal_native_copper': Vein('native_copper', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'surface_native_copper': Vein('native_copper', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_extrusive']),
    'normal_native_gold': Vein('native_gold', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive', 'igneous_intrusive']),
    'deep_native_gold': Vein('native_gold', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive', 'igneous_intrusive']),
    'normal_native_silver': Vein('native_silver', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['granite', 'gneiss']),
    'poor_native_silver': Vein('native_silver', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['granite', 'metamorphic']),
    'normal_hematite': Vein('hematite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_extrusive']),
    'deep_hematite': Vein('hematite', 'cluster', 120, 30, 5, 60, 60, 10, 30, 60, ['igneous_extrusive']),
    'normal_cassiterite': Vein('cassiterite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive']),
    'surface_cassiterite': Vein('cassiterite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_intrusive']),
    'normal_bismuthinite': Vein('bismuthinite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['igneous_intrusive', 'sedimentary']),
    'surface_bismuthinite': Vein('bismuthinite', 'cluster', 80, 15, 60, 120, 60, 60, 30, 10, ['igneous_intrusive', 'sedimentary']),
    'normal_garnierite': Vein('garnierite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['gabbro']),
    'poor_garnierite': Vein('garnierite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['igneous_intrusive']),
    'normal_malachite': Vein('malachite', 'cluster', 100, 20, 30, 100, 60, 20, 50, 30, ['marble', 'limestone']),
    'poor_malachite': Vein('malachite', 'cluster', 140, 15, 5, 100, 60, 60, 30, 10, ['marble', 'limestone', 'phyllite', 'chalk', 'dolomite']),
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
    'gravel': Vein('gravel', 'disc', 100, 40, 20, 80, 60, 0, 0, 0, ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']),
    'halite': Vein('halite', 'disc', 120, 30, 80, 100, 80, 0, 0, 0, ['sedimentary']),

    'diamond': Vein('diamond', 'pipe', 60, 60, 5, 140, 40, 0, 0, 0, ['gabbro']),
    'emerald': Vein('emerald', 'pipe', 80, 60, 5, 140, 40, 0, 0, 0, ['igneous_intrusive']),
}
ROCK_BLOCK_TYPES = ('raw', 'bricks', 'cobble', 'gravel', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks', 'chiseled', 'spike')
CUTTABLE_ROCKS = ('raw', 'bricks', 'cobble', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks')
ROCK_SPIKE_PARTS = ('base', 'middle', 'tip')
SAND_BLOCK_TYPES = ('brown', 'white', 'black', 'red', 'yellow', 'green', 'pink')
SOIL_BLOCK_TYPES = ('dirt', 'grass', 'grass_path', 'clay', 'clay_grass')
SOIL_BLOCK_VARIANTS = ('silt', 'loam', 'sandy_loam', 'silty_loam')

GEMS = ('amethyst', 'diamond', 'emerald', 'lapis_lazuli', 'opal', 'pyrite', 'ruby', 'sapphire', 'topaz')
GEM_GRADES = ('cut', 'uncut', 'powder')

COLORS = ('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')

WOODS = ('acacia', 'ash', 'aspen', 'birch', 'blackwood', 'chestnut', 'douglas_fir', 'hickory', 'kapok', 'maple', 'oak', 'palm', 'pine', 'rosewood', 'sequoia', 'spruce', 'sycamore', 'white_cedar', 'willow')


# This is here because it's used all over, and it's easier to import with all constants
def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()


def lang_enum(name: str, values: Sequence[str]) -> Dict[str, str]:
    return dict(('tfc.enum.%s.%s' % (name, value), lang(value)) for value in values)


# This is here as it's used only once in a generic lang call by generate_resources.py
DEFAULT_LANG = {
    # Item groups
    'itemGroup.tfc.rock.blocks': 'TFC Rock Blocks',
    'itemGroup.tfc.metals': 'TFC Metals',
    'itemGroup.tfc.gems': 'TFC Gems',
    'itemGroup.tfc.misc': 'TFC Misc',
    'itemGroup.tfc.decorations': 'TFC Decorations',
    'itemGroup.tfc.wood.blocks': 'TFC Wood Blocks',
    # Containers
    'tfc.screen.calendar': 'Calendar',
    'tfc.screen.nutrition': 'Nutrition',
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
    'tfc.commands.clear_world.done': 'Cleared.',
    'tfc.commands.player.query_hunger': 'Hunger is %s',
    'tfc.commands.player.query_saturation': 'Saturation is %s',

    # ENUMS

    **dict(('tfc.enum.tier.tier_%s' % tier, 'Tier %s' % lang(tier)) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')),
    **lang_enum('heat', ('warming', 'hot', 'very_hot', 'faint_red', 'dark_red', 'bright_red', 'orange', 'yellow', 'yellow_white', 'white', 'brilliant_white')),
    **lang_enum('month', ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')),
    **lang_enum('day', ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')),
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'none')),
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

    **dict(('metal.tfc.%s' % metal, lang('%s' % metal)) for metal in METALS.keys())
}

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
    dye_color: Optional[str] = None


class OreGrade(NamedTuple):
    grind_amount: int


class Vein(NamedTuple):
    ore: str  # The name of the ore (as found in ORES)
    vein_type: str  # Either 'cluster', 'pipe' or 'disc'
    rarity: int
    size: int
    min_y: int
    max_y: int
    density: float
    grade: tuple[int, int, int]  # (poor, normal, rich) weights
    rocks: tuple[str, ...]  # Rock, or rock categories
    biomes: str | None
    height: int
    radius: int
    deposits: bool
    indicator_rarity: int  # Above-ground indicators
    underground_rarity: int  # Underground indicators
    underground_count: int
    project: bool | None  # Project to surface
    project_offset: bool | None  # Project offset
    near_lava: bool | None

    @staticmethod
    def new(
        ore: str,
        rarity: int,
        size: int,
        min_y: int,
        max_y: int,
        density: float,
        rocks: tuple[str, ...],

        vein_type: str = 'cluster',
        grade: tuple[int, int, int] = (),
        biomes: str = None,
        height: int = 2,  # For disc type veins, `size` is the width
        radius: int = 5,  # For pipe type veins, `size` is the height
        deposits: bool = False,
        indicator: int = 12,  # Indicator rarity
        deep_indicator: tuple[int, int] = (1, 0),  # Pair of (rarity, count) for underground indicators
        project: str | bool = None,  # Projects to surface. Either True or 'offset'
        near_lava: bool | None = None,
    ):
        assert 0 < density < 1
        assert isinstance(rocks, tuple), 'Forgot the trailing comma in a single element tuple: %s' % repr(rocks)
        assert vein_type in ('cluster', 'disc', 'pipe')
        assert project is None or project is True or project == 'offset'

        underground_rarity, underground_count = deep_indicator
        return Vein(ore, 'tfc:%s_vein' % vein_type, rarity, size, min_y, max_y, density, grade, rocks, biomes, height, radius, deposits, indicator, underground_rarity, underground_count, None if project is None else True, None if project != 'offset' else True, near_lava)

    def config(self) -> dict[str, Any]:
        cfg = {
            'rarity': self.rarity,
            'density': self.density,
            'min_y': self.min_y,
            'max_y': self.max_y,
            'project': self.project,
            'project_offset': self.project_offset,
            'biomes': self.biomes,
            'near_lava': self.near_lava,
        }
        if self.vein_type == 'tfc:cluster_vein':
            cfg.update(size=self.size)
        elif self.vein_type == 'tfc:pipe_vein':
            cfg.update(min_skew=5, max_skew=13, min_slant=0, max_slant=2, sign=0, height=self.size, radius=self.radius)
        else:
            cfg.update(size=self.size, height=self.height)
        return cfg


class Plant(NamedTuple):
    clay: bool
    min_temp: float
    max_temp: float
    min_rain: float
    max_rain: float
    type: str
    worldgen: bool = True


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
    types: Set[str]  # One of 'part', 'tool', 'armor', 'utility'
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
    'dacite': Rock('igneous_extrusive', 'yellow'),
    'quartzite': Rock('metamorphic', 'white'),
    'slate': Rock('metamorphic', 'yellow'),
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
    'block': MetalItem('part', 100, 'block/block', None, False, False),
    'block_slab': MetalItem('part', 50, 'block/block', None, False, False),
    'block_stairs': MetalItem('part', 75, 'block/block', None, False, False),
    'bars': MetalItem('utility', 25, 'item/generated', None, False, False),
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
    'unfinished_lamp': MetalItem('utility', 100, 'item/generated', None, False, False),

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
    'horse_armor': MetalItem('armor', 1200, 'item/generated', None, False, False),

    'shield': MetalItem('tool', 400, 'item/handheld', None, False, True)
}
METAL_ITEMS_AND_BLOCKS: Dict[str, MetalItem] = {**METAL_ITEMS, **METAL_BLOCKS}
METAL_TOOL_HEADS = ('chisel', 'hammer', 'hoe', 'javelin', 'knife', 'mace', 'pickaxe', 'propick', 'saw', 'scythe', 'shovel', 'sword', 'axe')

ORES: Dict[str, Ore] = {
    'native_copper': Ore('copper', True, 'copper', 'copper', 'orange'),
    'native_gold': Ore('gold', True, 'copper', 'gold'),
    'hematite': Ore('cast_iron', True, 'copper', 'iron', 'red'),
    'native_silver': Ore('silver', True, 'copper', 'silver', 'light_gray'),
    'cassiterite': Ore('tin', True, 'copper', 'tin', 'gray'),
    'bismuthinite': Ore('bismuth', True, 'copper', 'bismuth', 'green'),
    'garnierite': Ore('nickel', True, 'bronze', 'nickel', 'brown'),
    'malachite': Ore('copper', True, 'copper', 'copper', 'green'),
    'magnetite': Ore('cast_iron', True, 'copper', 'iron', 'gray'),
    'limonite': Ore('cast_iron', True, 'copper', 'iron', 'yellow'),
    'sphalerite': Ore('zinc', True, 'copper', 'zinc', 'gray'),
    'tetrahedrite': Ore('copper', True, 'copper', 'copper', 'gray'),
    'bituminous_coal': Ore(None, False, 'copper', 'coal'),
    'lignite': Ore(None, False, 'copper', 'coal'),
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
    'normal': OreGrade(5),
    'poor': OreGrade(3),
    'rich': OreGrade(7)
}
DEFAULT_FORGE_ORE_TAGS: Tuple[str, ...] = ('coal', 'diamond', 'emerald', 'gold', 'iron', 'lapis', 'netherite_scrap', 'quartz', 'redstone')

POOR = 70, 25, 5  # = 1550
NORMAL = 35, 40, 25  # = 2400
RICH = 15, 25, 60  # = 2550

ORE_VEINS: dict[str, Vein] = {
    # Copper
    # Native - only in IE, only surface, and common to compensate for the y-level getting cut off.
    # Malachite + Tetrahedrite - Sed + MM, can spawn in larger deposits, hence more common. Tetrahedrite also spawns at high altitude MM
    # All copper have high indicator rarity because it's necessary early on
    'surface_native_copper': Vein.new('native_copper', 24, 20, 40, 130, 0.25, ('igneous_extrusive',), grade=POOR, deposits=True, indicator=14),
    'surface_malachite': Vein.new('malachite', 32, 20, 40, 130, 0.25, ('marble', 'limestone', 'chalk', 'dolomite'), grade=POOR, indicator=14),
    'surface_tetrahedrite': Vein.new('tetrahedrite', 7, 20, 90, 170, 0.25, ('metamorphic',), grade=POOR, indicator=8),

    'normal_malachite': Vein.new('malachite', 45, 30, -30, 70, 0.5, ('marble', 'limestone', 'chalk', 'dolomite'), grade=NORMAL, indicator=25),
    'normal_tetrahedrite': Vein.new('tetrahedrite', 40, 30, -30, 70, 0.5, ('metamorphic',), grade=NORMAL, indicator=25),

    # Native Gold - IE and II at all y levels, larger deeper
    'normal_native_gold': Vein.new('native_gold', 90, 15, 0, 70, 0.25, ('igneous_extrusive', 'igneous_intrusive'), grade=NORMAL, indicator=40),
    'rich_native_gold': Vein.new('native_gold', 50, 40, -80, 20, 0.5, ('igneous_intrusive',), grade=RICH, indicator=0, deep_indicator=(1, 4)),

    # In the same area as native gold deposits, pyrite veins - vast majority pyrite, but some native gold - basically troll veins
    'fake_native_gold': Vein.new('pyrite', 16, 15, -50, 70, 0.35, ('igneous_extrusive', 'igneous_intrusive'), indicator=0),

    # Silver - black bronze (T2 with gold), or for black steel. Rare and small in uplift mountains via high II or plentiful near bottom of world
    'surface_native_silver': Vein.new('native_silver', 15, 10, 90, 180, 0.2, ('granite', 'diorite'), grade=POOR),
    'normal_native_silver': Vein.new('native_silver', 25, 25, -80, 20, 0.6, ('granite', 'diorite', 'gneiss', 'schist'), grade=RICH, indicator=0, deep_indicator=(1, 9)),

    # Tin - bronze T2, rare situation (II uplift mountain) but common and rich.
    'surface_cassiterite': Vein.new('cassiterite', 5, 15, 80, 180, 0.4, ('igneous_intrusive',), grade=NORMAL, deposits=True),

    # Bismuth - bronze T2 surface via Sed, deep and rich via II
    'surface_bismuthinite': Vein.new('bismuthinite', 32, 20, 40, 130, 0.3, ('sedimentary',), grade=POOR, indicator=14),
    'normal_bismuthinite': Vein.new('bismuthinite', 45, 40, -80, 20, 0.6, ('igneous_intrusive',), grade=RICH, indicator=0, deep_indicator=(1, 4)),

    # Zinc - bronze T2, requires different source from bismuth, surface via IE, or deep via II
    'surface_sphalerite': Vein.new('sphalerite', 30, 20, 40, 130, 0.3, ('igneous_extrusive',), grade=POOR),
    'normal_sphalerite': Vein.new('sphalerite', 45, 40, -80, 20, 0.6, ('igneous_intrusive',), grade=RICH, indicator=0, deep_indicator=(1, 5)),

    # Iron - both surface via IE and Sed. IE has one, Sed has two, so the two are higher rarity
    'surface_hematite': Vein.new('hematite', 45, 20, 10, 90, 0.4, ('igneous_extrusive',), grade=NORMAL, indicator=24),
    'surface_magnetite': Vein.new('magnetite', 90, 20, 10, 90, 0.4, ('sedimentary',), grade=NORMAL, indicator=24),
    'surface_limonite': Vein.new('limonite', 90, 20, 10, 90, 0.4, ('sedimentary',), grade=NORMAL, indicator=24),

    # Nickel - only deep spawning II. Extra veins in gabbro
    'normal_garnierite': Vein.new('garnierite', 25, 18, -80, 0, 0.3, ('igneous_intrusive',), grade=NORMAL),
    'gabbro_garnierite': Vein.new('garnierite', 20, 30, -80, 0, 0.6, ('gabbro',), grade=RICH, indicator=0, deep_indicator=(1, 7)),

    # Graphite - for steel, found in low MM. Along with Kao, which is high altitude sed (via clay deposits)
    'graphite': Vein.new('graphite', 20, 20, -30, 60, 0.4, ('gneiss', 'marble', 'quartzite', 'schist')),

    # Coal, spawns roughly based on IRL grade (lignite -> bituminous -> anthracite), big flat discs
    'lignite': Vein.new('lignite', 160, 40, -20, -8, 0.85, ('sedimentary',), vein_type='disc', height=2, project='offset'),
    'bituminous_coal': Vein.new('bituminous_coal', 210, 50, -35, -12, 0.9, ('sedimentary',), vein_type='disc', height=3, project='offset'),

    # Sulfur spawns near lava level in any low-level rock, common, but small veins
    'sulfur': Vein.new('sulfur', 4, 18, -64, -45, 0.25, ('igneous_intrusive', 'metamorphic'), vein_type='disc', height=5, near_lava=True),

    # Redstone: Cryolite is deep II, cinnabar is deep MM, both are common enough within these rocks but rare to find
    'cryolite': Vein.new('cryolite', 16, 18, -70, -10, 0.7, ('granite', 'diorite')),
    'cinnabar': Vein.new('cinnabar', 14, 18, -70, 10, 0.6, ('quartzite', 'phyllite', 'gneiss', 'schist')),

    # Misc minerals - all spawning in discs, mostly in sedimentary rock. Rare, but all will spawn together
    # Gypsum is decorative, so more common, and Borax is sad, so more common (but smaller)
    # Veins that spawn in all sedimentary are rarer than those that don't
    'saltpeter': Vein.new('saltpeter', 110, 35, 40, 100, 0.4, ('sedimentary',), vein_type='disc', height=5),
    'sylvite': Vein.new('sylvite', 60, 35, 40, 100, 0.35, ('shale', 'claystone', 'chert'), vein_type='disc', height=5),
    'borax': Vein.new('borax', 40, 23, 40, 100, 0.2, ('claystone', 'limestone', 'shale'), vein_type='disc', height=3),
    'gypsum': Vein.new('gypsum', 70, 25, 40, 100, 0.3, ('sedimentary',), vein_type='disc', height=5),
    'halite': Vein.new('halite', 110, 35, -45, -12, 0.85, ('sedimentary',), vein_type='disc', height=4, project='offset'),

    # Gems - these are all fairly specific but since we don't have a gameplay need for gems they can be a bit niche
    'lapis_lazuli': Vein.new('lapis_lazuli', 30, 30, -20, 80, 0.12, ('limestone', 'marble')),

    'diamond': Vein.new('diamond', 30, 60, -64, 100, 0.15, ('gabbro',), vein_type='pipe', radius=5),
    'emerald': Vein.new('emerald', 80, 60, -64, 100, 0.15, ('igneous_intrusive',), vein_type='pipe', radius=5),

    'amethyst': Vein.new('amethyst', 25, 8, 40, 60, 0.2, ('sedimentary', 'metamorphic'), vein_type='disc', biomes='#tfc:is_river', height=4),
    'opal': Vein.new('opal', 25, 8, 40, 60, 0.2, ('sedimentary', 'igneous_extrusive'), vein_type='disc', biomes='#tfc:is_river', height=4),
}

ALL_MINERALS = ('bituminous_coal', 'lignite', 'graphite', 'cinnabar', 'cryolite', 'saltpeter', 'sulfur', 'sylvite', 'borax', 'gypsum', 'lapis_lazuli', 'halite', 'diamond', 'emerald', 'sulfur', 'amethyst', 'opal')

DEPOSIT_RARES: Dict[str, str] = {
    'granite': 'topaz',
    'diorite': 'emerald',
    'gabbro': 'diamond',
    'shale': 'borax',
    'claystone': 'amethyst',
    'limestone': 'lapis_lazuli',
    'conglomerate': 'lignite',
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
SOIL_BLOCK_TYPES = ('dirt', 'grass', 'grass_path', 'clay', 'clay_grass', 'farmland', 'rooted_dirt', 'mud', 'mud_bricks', 'drying_bricks', 'muddy_roots')
SOIL_BLOCK_VARIANTS = ('silt', 'loam', 'sandy_loam', 'silty_loam')
KAOLIN_CLAY_TYPES = ('red', 'pink', 'white')
SOIL_BLOCK_TAGS: Dict[str, List[str]] = {
    'grass': ['grass'],
    'dirt': ['dirt'],
    'rooted_dirt': ['dirt'],
    'clay_grass': ['clay_grass', 'grass', 'clay'],
    'clay': ['clay'],
    'mud': ['mud'],
    'grass_path': ['paths'],
    'farmland': ['farmland'],
    'muddy_roots': ['dirt'],
    'mud_bricks': ['mud_bricks']
}
ORE_DEPOSITS = ('native_copper', 'cassiterite', 'native_silver', 'native_gold')
GEMS = ('amethyst', 'diamond', 'emerald', 'lapis_lazuli', 'opal', 'pyrite', 'ruby', 'sapphire', 'topaz')
TRIM_MATERIALS = (*GEMS, 'rose_gold', 'gold', 'silver', 'sterling_silver', 'bismuth')
MISC_GROUNDCOVER = ('bone', 'clam', 'driftwood', 'mollusk', 'mussel', 'pinecone', 'seaweed', 'stick', 'dead_grass', 'feather', 'flint', 'guano', 'humus', 'rotten_flesh', 'salt_lick', 'sea_urchin', 'pumice')
COLORS = ('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')
SIMPLE_FLUIDS = ('brine', 'curdled_milk', 'limewater', 'lye', 'milk_vinegar', 'olive_oil', 'olive_oil_water', 'tallow', 'tannin', 'vinegar')
ALCOHOLS = ('beer', 'cider', 'rum', 'sake', 'vodka', 'whiskey', 'corn_whiskey', 'rye_whiskey')

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
    'mangrove': Wood(655, 1000),
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
    'red_bell_pepper': Crop('pickable', 7, 'potassium', 16, 30, 190, 400, 25, 60, None, None),
    'yellow_bell_pepper': Crop('pickable', 7, 'potassium', 16, 30, 190, 400, 25, 60, None, None),
}

PLANTS: Dict[str, Plant] = {
    'athyrium_fern': Plant(True, -3.9, 15.7, 270, 500, 'standard'),
    'canna': Plant(True, 13.9, 40, 290, 500, 'standard'),
    'goldenrod': Plant(True, -12.9, -2.1, 75, 500, 'standard'),
    'pampas_grass': Plant(True, 10.4, 40, 0, 300, 'tall_grass'),
    'perovskia': Plant(True, -5.7, 13.9, 0, 280, 'dry'),

    'beachgrass': Plant(False, -8, 30, 190, 500, 'beach_grass', False),
    'bluegrass': Plant(False, -0.4, 13.9, 110, 280, 'short_grass', False),
    'bromegrass': Plant(False, 6.8, 21.1, 140, 360, 'short_grass', False),
    'fountain_grass': Plant(False, 3.2, 26.4, 75, 150, 'short_grass', False),
    'manatee_grass': Plant(False, 13.9, 40, 250, 500, 'grass_water', False),
    'orchard_grass': Plant(False, -30, 12.1, 75, 300, 'short_grass', False),
    'ryegrass': Plant(False, -18.2, 40, 150, 320, 'short_grass', False),
    'scutch_grass': Plant(False, 3.2, 40, 150, 500, 'short_grass', False),
    'star_grass': Plant(False, 5, 40, 50, 260, 'grass_water', False),
    'timothy_grass': Plant(False, -16.4, 17.5, 289, 500, 'short_grass', False),
    'raddia_grass': Plant(False, 19.3, 40, 330, 500, 'short_grass', False),

    'allium': Plant(False, -5.7, 1.4, 150, 400, 'standard'),
    'anthurium': Plant(False, 13.9, 40, 290, 500, 'standard'),
    'arrowhead': Plant(False, -5.7, 22.9, 180, 500, 'emergent_fresh'),
    'houstonia': Plant(False, -7.5, 12.1, 150, 500, 'standard'),
    'badderlocks': Plant(False, -12.9, 5, 150, 500, 'submerged_tall'),
    'cordgrass': Plant(False, -16.4, 22.9, 50, 500, 'emergent'),
    'barrel_cactus': Plant(False, 6.8, 19.3, 0, 85, 'cactus'),
    'blood_lily': Plant(True, 10.4, 19.3, 200, 500, 'standard'),
    'blue_orchid': Plant(False, 12.1, 40, 250, 390, 'standard'),
    'blue_ginger': Plant(False, 17.5, 26.4, 300, 450, 'standard'),
    'calendula': Plant(False, 6.8, 22.9, 130, 300, 'standard'),
    'cattail': Plant(False, -11.1, 22.9, 150, 500, 'emergent_fresh'),
    'laminaria': Plant(False, -18.2, 1.4, 100, 500, 'water'),
    'marigold': Plant(False, -3.9, 19.3, 50, 390, 'emergent_fresh'),
    'bur_reed': Plant(False, -11.1, 6.8, 250, 400, 'emergent_fresh'),
    'butterfly_milkweed': Plant(False, -11.1, 19.3, 75, 300, 'standard'),
    'black_orchid': Plant(False, 15.7, 40, 290, 410, 'standard'),
    'cobblestone_lichen': Plant(False, -30, 20, 25, 450, 'creeping'),
    'coontail': Plant(False, 5, 19.3, 250, 500, 'grass_water_fresh'),
    'dandelion': Plant(False, -16.4, 40, 120, 400, 'standard'),
    'dead_bush': Plant(False, -7.5, 40, 0, 120, 'dry'),
    'desert_flame': Plant(False, 3.2, 21.1, 40, 170, 'standard'),
    'duckweed': Plant(False, 12.1, 40, 0, 500, 'floating_fresh'),
    'eel_grass': Plant(False, 8.6, 40, 200, 500, 'grass_water_fresh'),
    'field_horsetail': Plant(False, -7.5, 21.1, 300, 500, 'standard'),
    'foxglove': Plant(False, -3.9, 17.5, 150, 300, 'tall_plant'),
    'grape_hyacinth': Plant(False, -5.7, 12.1, 150, 250, 'standard'),
    'green_algae': Plant(False, -20, 30, 215, 450, 'floating_fresh'),
    'gutweed': Plant(False, -2.1, 19.3, 100, 500, 'water'),
    'heliconia': Plant(False, 15.7, 40, 320, 500, 'standard'),
    'heather': Plant(False, -2.1, 8.6, 180, 380, 'standard'),
    'hibiscus': Plant(False, 12.1, 24.6, 260, 450, 'tall_plant'),
    'ivy': Plant(False, -4, 14, 90, 450, 'creeping'),
    'kangaroo_paw': Plant(False, 15.7, 40, 100, 300, 'standard'),
    'king_fern': Plant(False, 19.3, 40, 350, 500, 'tall_plant'),
    'labrador_tea': Plant(False, -12.9, 3.2, 200, 380, 'standard'),
    'lady_fern': Plant(False, -5.7, 10.4, 200, 500, 'standard'),
    'licorice_fern': Plant(False, 5, 12.1, 300, 400, 'epiphyte'),
    'artists_conk': Plant(False, -12, 21, 150, 420, 'epiphyte'),
    'lily_of_the_valley': Plant(False, -11.1, 15.7, 180, 415, 'standard'),
    'lilac': Plant(False, -5.7, 8.6, 150, 300, 'tall_plant'),
    'lotus': Plant(False, -0.4, 19.3, 0, 500, 'floating_fresh'),
    'maiden_pink': Plant(False, 5, 25, 100, 350, 'standard'),

    'meads_milkweed': Plant(False, -5.7, 5, 130, 380, 'standard'),
    'milfoil': Plant(False, -9.3, 22.9, 250, 500, 'water_fresh'),
    'morning_glory': Plant(False, -14, 19, 300, 500, 'creeping'),
    'philodendron': Plant(False, 16, 30, 380, 500, 'creeping'),
    'moss': Plant(False, -10, 30, 250, 450, 'creeping'),
    'nasturtium': Plant(False, 8.6, 22.9, 150, 380, 'standard'),
    'ostrich_fern': Plant(False, -9.3, 8.6, 290, 470, 'tall_plant'),
    'oxeye_daisy': Plant(False, -9.3, 12.1, 120, 300, 'standard'),
    'phragmite': Plant(False, -2.1, 19.3, 50, 250, 'emergent_fresh'),
    'pickerelweed': Plant(False, -9.3, 17.5, 200, 500, 'emergent_fresh'),
    'pistia': Plant(False, 8.6, 26.4, 0, 400, 'floating_fresh'),
    'poppy': Plant(False, -7.5, 15.7, 150, 250, 'standard'),
    'primrose': Plant(False, -3.9, 12.1, 150, 300, 'standard'),
    'pulsatilla': Plant(False, -5.7, 5, 50, 200, 'standard'),
    'red_algae': Plant(False, -20, 30, 215, 450, 'floating'),
    'red_sealing_wax_palm': Plant(False, 19.3, 40, 280, 500, 'tall_plant'),
    'reindeer_lichen': Plant(False, -30, -8, 50, 470, 'creeping'),
    'rose': Plant(True, -5, 20, 150, 300, 'tall_plant'),
    'sacred_datura': Plant(False, 6.8, 19.3, 75, 150, 'standard'),
    'sagebrush': Plant(False, -5.7, 15.7, 0, 120, 'dry'),
    'sago': Plant(False, -12.9, 19.3, 200, 500, 'water_fresh'),
    'saguaro_fruit': Plant(False, -18, 18, 200, 500, 'cactus_fruit', False),
    'sapphire_tower': Plant(False, 12.1, 22.9, 75, 200, 'tall_plant'),
    'sargassum': Plant(False, -5.7, 17.5, 0, 500, 'floating'),
    'sea_lavender': Plant(False, -5.7, 13.9, 300, 450, 'emergent'),
    'sea_palm': Plant(False, -18, 20, 10, 450, 'dry', False),
    'guzmania': Plant(False, 21.1, 40, 290, 480, 'epiphyte'),
    'silver_spurflower': Plant(False, 15.7, 24.6, 230, 400, 'standard'),
    'snapdragon_pink': Plant(False, 17.5, 24.6, 150, 300, 'standard'),
    'snapdragon_red': Plant(False, 13.9, 21.1, 150, 300, 'standard'),
    'snapdragon_white': Plant(False, 10.4, 17.5, 150, 300, 'standard'),
    'snapdragon_yellow': Plant(False, 8.6, 24.6, 150, 300, 'standard'),
    'strelitzia': Plant(False, 15.7, 26.4, 50, 300, 'standard'),
    'switchgrass': Plant(False, -2.1, 22.9, 110, 390, 'tall_grass'),
    'sword_fern': Plant(False, -7.5, 13.9, 100, 500, 'standard'),
    'tall_fescue_grass': Plant(False, -5.7, 12.1, 280, 430, 'tall_grass'),
    'toquilla_palm': Plant(False, 17.5, 40, 250, 500, 'tall_plant'),
    'trillium': Plant(False, -5.7, 10.4, 250, 500, 'standard'),
    'tropical_milkweed': Plant(False, 10.4, 24.6, 120, 300, 'standard'),
    'tulip_orange': Plant(False, 5, 12.1, 200, 400, 'standard'),
    'tulip_pink': Plant(False, -2.1, 5, 200, 400, 'standard'),
    'tulip_red': Plant(False, 3.2, 6.8, 200, 400, 'standard'),
    'tulip_white': Plant(False, -7.5, -0.4, 200, 400, 'standard'),
    'turtle_grass': Plant(False, 15.7, 40, 240, 500, 'grass_water'),
    'vriesea': Plant(False, 15.7, 40, 200, 400, 'epiphyte'),
    'water_canna': Plant(True, 13.9, 40, 150, 500, 'floating_fresh'),
    'water_lily': Plant(False, -7.5, 40, 0, 500, 'floating_fresh'),
    'water_taro': Plant(False, 13.9, 40, 260, 500, 'emergent_fresh'),
    'yucca': Plant(False, -0.4, 22.9, 0, 75, 'dry'),
}

SMALL_FLOWERS = ('allium', 'anthurium', 'black_orchid', 'blood_lily', 'blue_orchid', 'blue_ginger', 'butterfly_milkweed', 'calendula', 'canna', 'dandelion', 'desert_flame', 'goldenrod', 'grape_hyacinth', 'guzmania', 'kangaroo_paw', 'labrador_tea', 'lily_of_the_valley', 'lotus', 'nasturtium', 'oxeye_daisy', 'pistia', 'poppy', 'primrose', 'pulsatilla', 'rose', 'sacred_datura', 'sagebrush', 'sapphire_tower', 'sargassum', 'silver_spurflower', 'snapdragon_red', 'snapdragon_pink', 'snapdragon_white', 'snapdragon_yellow', 'strelitzia', 'trillium', 'tropical_milkweed', 'tulip_orange', 'tulip_red', 'tulip_pink', 'tulip_white', 'vriesea', 'water_lily', 'yucca')

TALL_FLOWERS = ('foxglove', 'hibiscus', 'lilac', 'toquilla_palm', 'marigold')

FLOWERPOT_CROSS_PLANTS = {
    'allium': 'allium_2',
    'anthurium': 'anthurium_0',
    'athyrium_fern': 'single',
    'black_orchid': 'black_orchid_0',
    'blood_lily': 'blood_lily_0',
    'blue_orchid': 'blue_orchid_1',
    'blue_ginger': 'blue_ginger_0',
    'butterfly_milkweed': 'potted',
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
    'heather': 'potted',
    'houstonia': 'houstonia_1',
    'kangaroo_paw': 'item',
    'labrador_tea': 'labrador_tea_4',
    'lady_fern': 'item',
    'lily_of_the_valley': 'lily_of_the_valley_3',
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
    'saguaro_fruit': 'saguaro_fruit_1',
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
MISC_POTTED_PLANTS = ['barrel_cactus', 'morning_glory', 'moss', 'reindeer_lichen', 'rose', 'toquilla_palm', 'tree_fern', 'sea_palm', 'philodendron']

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
    'lily_of_the_valley': 6,
    'meads_milkweed': 7,
    'nasturtium': 5,
    'oxeye_daisy': 6,
    'perovskia': 6,
    'poppy': 5,
    'primrose': 3,
    'pulsatilla': 6,
    'sacred_datura': 5,  # different
    'saguaro_fruit': 2,
    'silver_spurflower': 3,
    'strelitzia': 3,
    'trillium': 6,  # different
    'tropical_milkweed': 4,
    'yucca': 4
}

MODEL_PLANTS: List[str] = ['arundo', 'arundo_plant', 'athyrium_fern', 'dry_phragmite', 'dry_phragmite_plant', 'hanging_vines', 'hanging_vines_plant', 'spanish_moss', 'spanish_moss_plant', 'lady_fern', 'laminaria', 'liana', 'liana_plant', 'milfoil', 'sago', 'sword_fern', 'tree_fern', 'tree_fern_plant', 'winged_kelp', 'winged_kelp_plant', 'sea_palm']
SEAGRASS: List[str] = ['star_grass', 'manatee_grass', 'eel_grass', 'turtle_grass', 'coontail']

UNIQUE_PLANTS: List[str] = ['hanging_vines_plant', 'hanging_vines', 'spanish_moss', 'spanish_moss_plant', 'liana_plant', 'liana', 'tree_fern_plant', 'tree_fern', 'arundo_plant', 'arundo', 'dry_phragmite', 'dry_phragmite_plant', 'winged_kelp_plant', 'winged_kelp', 'leafy_kelp_plant', 'leafy_kelp', 'giant_kelp_plant', 'giant_kelp_flower', 'jungle_vines', 'saguaro', 'saguaro_plant']
BROWN_COMPOST_PLANTS: List[str] = ['hanging_vines', 'spanish_moss', 'liana', 'tree_fern', 'arundo', 'dry_phragmite', 'jungle_vines']
SEAWEED: List[str] = ['sago', 'gutweed', 'laminaria', 'milfoil']
CORALS: List[str] = ['tube', 'brain', 'bubble', 'fire', 'horn']
CORAL_BLOCKS: List[str] = ['dead_coral', 'dead_coral', 'dead_coral_fan', 'coral_fan', 'dead_coral_wall_fan', 'coral_wall_fan']

PLANT_COLORS: Dict[str, List[str]] = {
    'white': ['houstonia', 'oxeye_daisy', 'primrose', 'snapdragon_white', 'trillium', 'spanish_moss', 'tulip_white', 'water_lily', 'lily_of_the_valley'],
    'orange': ['butterfly_milkweed', 'canna', 'nasturtium', 'strelitzia', 'tulip_orange', 'water_canna', 'marigold'],
    'magenta': ['athyrium_fern', 'morning_glory', 'pulsatilla', 'lilac', 'silver_spurflower'],
    'light_blue': ['labrador_tea', 'sapphire_tower'],
    'yellow': ['calendula', 'dandelion', 'meads_milkweed', 'goldenrod', 'snapdragon_yellow', 'desert_flame'],
    'lime': ['moss'],
    'pink': ['foxglove', 'sacred_datura', 'tulip_pink', 'snapdragon_pink', 'hibiscus', 'lotus', 'maiden_pink'],
    'light_gray': ['yucca'],
    'purple': ['allium', 'black_orchid', 'perovskia', 'blue_ginger', 'pickerelweed', 'heather'],
    'blue': ['blue_orchid', 'grape_hyacinth'],
    'brown': ['field_horsetail', 'sargassum'],
    'green': ['barrel_cactus', 'reindeer_lichen'],
    'red': ['guzmania', 'poppy', 'rose', 'snapdragon_red', 'tropical_milkweed', 'tulip_red', 'vriesea', 'anthurium', 'blood_lily', 'heliconia', 'kangaroo_paw']
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

SIMPLE_BLOCKS = ('peat', 'aggregate', 'fire_bricks', 'fire_clay_block', 'smooth_mud_bricks')
SIMPLE_ITEMS = ('alabaster_brick', 'bone_needle', 'blank_disc', 'blubber', 'brass_mechanisms', 'burlap_cloth', 'compost', 'daub', 'dirty_jute_net', 'empty_jar', 'empty_jar_with_lid', 'fire_clay', 'goat_horn', 'gem_saw', 'glow_arrow', 'glue', 'hematitic_glass_batch', 'jacks', 'jar_lid',
                'jute', 'jute_fiber', 'jute_net', 'kaolin_clay', 'lamp_glass', 'lens', 'mortar', 'olive_paste', 'olivine_glass_batch', 'paddle', 'papyrus', 'papyrus_strip', 'pure_nitrogen', 'pure_phosphorus', 'pure_potassium', 'rotten_compost', 'sandpaper', 'silica_glass_batch', 'silk_cloth', 'soaked_papyrus_strip', 'soot', 'spindle',
                'stick_bunch', 'stick_bundle', 'straw', 'treated_hide', 'unrefined_paper', 'volcanic_glass_batch', 'wool', 'wool_cloth', 'wool_yarn', 'wrought_iron_grill')
GENERIC_POWDERS = {
    'charcoal': 'black',
    'coke': 'black',
    'graphite': 'blue',
    'kaolinite': 'pink',
    'sylvite': 'orange',
    'lapis_lazuli': 'blue'
}
POWDERS = ('flux', 'lime', 'salt', 'saltpeter', 'soda_ash', 'sulfur', 'wood_ash')
GLASSWORKING_POWDERS = ('soda_ash', 'sulfur', 'graphite', 'hematite', 'limonite', 'magnetite', 'native_gold', 'native_copper', 'malachite', 'tetrahedrite', 'cassiterite', 'garnierite', 'native_silver', 'amethyst', 'ruby', 'lapis_lazuli', 'pyrite', 'sapphire')
VANILLA_DYED_ITEMS = ('wool', 'carpet', 'bed', 'terracotta', 'banner', 'glazed_terracotta')
SIMPLE_POTTERY = ('bowl', 'fire_brick', 'pot', 'spindle_head', 'vessel')
SIMPLE_UNFIRED_POTTERY = ('brick', 'crucible', 'flower_pot', 'jug', 'pan', 'blowpipe')
GLASS_TYPES = ('silica', 'hematitic', 'olivine', 'volcanic')
VANILLA_TOOL_MATERIALS = ('netherite', 'diamond', 'iron', 'stone', 'wooden', 'golden')
SHORE_DECORATORS = ('driftwood', 'clam', 'mollusk', 'mussel', 'seaweed', 'sticks_shore', 'guano')
FOREST_DECORATORS = ('sticks_forest', 'pinecone', 'salt_lick', 'dead_grass', 'humus', 'rotten_flesh')
OCEAN_PLANT_TYPES = ('grass_water', 'floating', 'water', 'emergent', 'tall_water')
MISC_PLANT_FEATURES = ('hanging_vines', 'hanging_vines_cave', 'spanish_moss', 'saguaro_patch', 'jungle_vines', 'liana', 'moss_cover', 'reindeer_lichen_cover', 'morning_glory_cover', 'philodendron_cover', 'tree_fern', 'arundo')
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
JAR_FRUITS: List[str] = [*BERRIES.keys(), *FRUITS.keys(), 'pumpkin_chunks', 'melon_slice']
NORMAL_FRUIT_TREES: List[str] = [k for k in FRUITS.keys() if k != 'banana']

SIMPLE_FRESHWATER_FISH = ('bluegill', 'crappie', 'lake_trout', 'largemouth_bass', 'rainbow_trout', 'salmon', 'smallmouth_bass',)

GRAINS = ('barley', 'maize', 'oat', 'rice', 'rye', 'wheat')
GRAIN_SUFFIXES = ('', '_grain', '_flour', '_dough', '_bread', '_bread_sandwich', '_bread_jam_sandwich')
MISC_FOODS = ('beet', 'cabbage', 'carrot', 'garlic', 'green_bean', 'green_bell_pepper', 'onion', 'potato', 'baked_potato', 'red_bell_pepper', 'soybean', 'squash', 'tomato', 'yellow_bell_pepper', 'cheese', 'cooked_egg', 'boiled_egg', 'fresh_seaweed', 'dried_seaweed', 'dried_kelp', 'cattail_root', 'taro_root', 'sugarcane', 'cooked_rice', 'pumpkin_chunks', 'melon_slice')
MEATS = ('beef', 'pork', 'chicken', 'quail', 'mutton', 'bear', 'horse_meat', 'pheasant', 'turkey', 'peafowl', 'grouse', 'venison', 'wolf', 'rabbit', 'hyena', 'duck', 'chevon', 'gran_feline', 'camelidae', 'cod', 'tropical_fish', 'turtle', 'calamari', 'shellfish', *SIMPLE_FRESHWATER_FISH, 'frog_legs', 'fox')
NUTRIENTS = ('grain', 'fruit', 'vegetables', 'protein', 'dairy')

SPAWN_EGG_ENTITIES = ('isopod', 'lobster', 'crayfish', 'cod', 'pufferfish', 'tropical_fish', 'jellyfish', 'orca', 'dolphin', 'manatee', 'penguin', 'frog', 'turtle', 'horseshoe_crab', 'polar_bear', 'grizzly_bear', 'black_bear', 'cougar', 'panther', 'lion', 'sabertooth', 'squid', 'octopoteuthis', 'pig', 'cow', 'goat', 'yak', 'alpaca', 'musk_ox', 'sheep', 'chicken', 'duck', 'quail', 'rabbit', 'fox', 'boar', 'donkey', 'mule', 'horse', 'deer', 'moose', 'boar', 'rat', 'cat', 'dog', 'wolf', 'panda', 'grouse', 'pheasant', 'turkey', 'ocelot', 'direwolf', 'hyena', 'tiger', 'crocodile', 'bongo', 'caribou', 'gazelle', 'wildebeest', 'peafowl', *SIMPLE_FRESHWATER_FISH)
BUCKETABLE_FISH = ('cod', 'pufferfish', 'tropical_fish', 'jellyfish', *SIMPLE_FRESHWATER_FISH)
LAND_PREDATORS = ('polar_bear', 'grizzly_bear', 'black_bear', 'cougar', 'panther', 'lion', 'sabertooth', 'wolf', 'direwolf', 'ocelot', 'tiger', 'hyena', 'crocodile')
AMPHIBIOUS_PREDATORS = 'crocodile'
OCEAN_PREDATORS = ('dolphin', 'orca')
OCEAN_PREY = ('isopod', 'lobster', 'crayfish', 'cod', 'tropical_fish', 'horseshoe_crab', *SIMPLE_FRESHWATER_FISH)
LIVESTOCK = ('pig', 'cow', 'goat', 'yak', 'alpaca', 'sheep', 'musk_ox', 'chicken', 'duck', 'quail', 'horse', 'mule', 'donkey')
LAND_PREY = ('rabbit', 'fox', 'turtle', 'penguin', 'frog', 'deer', 'bongo', 'panda', 'grouse', 'pheasant', 'turkey', 'ocelot', 'caribou', 'gazelle', 'peafowl')
LAND_NEUTRALS = ('boar', 'moose', 'wildebeest')

BLOCK_ENTITIES = ('log_pile', 'burning_log_pile', 'placed_item', 'pit_kiln', 'charcoal_forge', 'quern', 'scraping', 'crucible', 'bellows', 'composter', 'chest', 'trapped_chest', 'barrel', 'loom', 'sluice', 'tool_rack', 'sign', 'lamp', 'berry_bush', 'crop', 'firepit', 'pot', 'grill', 'pile', 'farmland', 'tick_counter', 'nest_box', 'bloomery', 'bloom', 'anvil', 'ingot_pile', 'sheet_pile', 'blast_furnace', 'large_vessel', 'powderkeg', 'bowl', 'hot_poured_glass', 'glass_basin', 'axle', 'hand_wheel', 'sewing_table')
TANNIN_WOOD_TYPES = ('oak', 'birch', 'chestnut', 'douglas_fir', 'hickory', 'maple', 'sequoia')

def spawner(entity: str, weight: int = 1, min_count: int = 1, max_count: int = 4) -> Dict[str, Any]:
    return {
        'type': entity,
        'weight': weight,
        'minCount': min_count,
        'maxCount': max_count
    }

SALT_MARSH_AMBIENT: Dict[str, Dict[str, Any]] = {
    'isopod': spawner('tfc:isopod'),
    'lobster': spawner('tfc:lobster'),
    'horseshoe_crab': spawner('tfc:horseshoe_crab'),
    'salmon': spawner('tfc:salmon')
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
    **dict(('%s' % fish, spawner('tfc:%s' % fish, min_count=2, max_count=4, weight=10)) for fish in SIMPLE_FRESHWATER_FISH if 'trout' not in fish),
    'crayfish': spawner('tfc:crayfish', min_count=1, max_count=4, weight=5)
}

RIVER_AMBIENT: Dict[str, Dict[str, Any]] = {
    **dict(('%s' % fish, spawner('tfc:%s' % fish, min_count=2, max_count=4, weight=10)) for fish in SIMPLE_FRESHWATER_FISH if 'trout' in fish),
}

LAKE_CREATURES: Dict[str, Dict[str, Any]] = {
    'manatee': spawner('tfc:manatee', min_count=1, max_count=2)
}

SHORE_CREATURES: Dict[str, Dict[str, Any]] = {
    'penguin': spawner('tfc:penguin', min_count=2, max_count=5, weight=10),
    'turtle': spawner('tfc:turtle', min_count=2, max_count=5, weight=10)
}

LAND_CREATURES: Dict[str, Dict[str, Any]] = {
    'crocodile': spawner('tfc:crocodile', min_count=1, max_count=1, weight=20),
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
    'tiger': spawner('tfc:tiger', min_count=1, max_count=1, weight=2),
    'rabbit': spawner('tfc:rabbit', min_count=1, max_count=4, weight=3),
    'fox': spawner('tfc:fox', min_count=1, max_count=1),
    'panda': spawner('tfc:panda', min_count=3, max_count=5),
    'boar': spawner('tfc:boar', min_count=1, max_count=2, weight=2),
    'wildebeest': spawner('tfc:wildebeest', min_count=1, max_count=2, weight=2),
    'moose': spawner('tfc:moose', min_count=1, max_count=1),
    'bongo': spawner('tfc:bongo', min_count=2, max_count=4, weight=3),
    'caribou': spawner('tfc:caribou', min_count=2, max_count=4, weight=3),
    'deer': spawner('tfc:deer', min_count=2, max_count=4, weight=3),
    'gazelle': spawner('tfc:gazelle', min_count=2, max_count=4, weight=3),
    'grouse': spawner('tfc:grouse', min_count=2, max_count=4),
    'pheasant': spawner('tfc:pheasant', min_count=2, max_count=4),
    'turkey': spawner('tfc:turkey', min_count=2, max_count=4),
    'peafowl': spawner('tfc:peafowl', min_count=2, max_count=4),
    'wolf': spawner('tfc:wolf', min_count=6, max_count=9),
    'hyena': spawner('tfc:hyena', min_count=5, max_count=9),
    'direwolf': spawner('tfc:direwolf', min_count=3, max_count=7),
    'donkey': spawner('tfc:donkey', min_count=1, max_count=3),
    'horse': spawner('tfc:horse', min_count=1, max_count=3),
    'ocelot': spawner('tfc:ocelot', min_count=1, max_count=3),
    'frog': spawner('tfc:frog', min_count=2, max_count=4),
}

VANILLA_MONSTERS: Dict[str, Dict[str, Any]] = {
    'spider': spawner('minecraft:spider', weight=100, min_count=4, max_count=4),
    'zombie': spawner('minecraft:zombie', weight=95, min_count=4, max_count=4),
    'skeleton': spawner('minecraft:skeleton', weight=100, min_count=4, max_count=4),
    'creeper': spawner('minecraft:creeper', weight=100, min_count=4, max_count=4),
    'slime': spawner('minecraft:slime', weight=100, min_count=4, max_count=4),
}

DISABLED_VANILLA_RECIPES = ('flint_and_steel', 'turtle_helmet', 'campfire', 'bucket', 'composter', 'tinted_glass', 'glass_pane', 'enchanting_table', 'bowl', 'blaze_rod', 'bone_meal', 'flower_pot', 'painting', 'torch', 'soul_torch', 'sticky_piston', 'clock', 'compass', 'white_wool_from_string', 'hay_block', 'anvil', 'wheat', 'lapis_lazuli', 'leather_horse_armor', 'map', 'furnace', 'jack_o_lantern', 'melon_seeds', 'melon', 'pumpkin_pie', 'chest', 'barrel', 'trapped_chest', 'bricks', 'bookshelf', 'crafting_table', 'lectern', 'chest_minecart', 'rail', 'beetroot_soup', 'mushroom_stew', 'rabbit_stew_from_red_mushroom',
                            'rabbit_stew_from_brown_mushroom', 'suspicious_stew', 'scaffolding', 'bow', 'glass_bottle', 'fletching_table', 'shield', 'lightning_rod', 'fishing_rod', 'iron_door', 'iron_trapdoor', 'spyglass', 'slime_ball', 'smoker', 'soul_campfire', 'loom', 'lantern', 'soul_lantern', 'flower_banner_pattern', 'skull_banner_pattern', 'creeper_banner_pattern', 'mojang_banner_pattern')
ARMOR_SECTIONS = ('chestplate', 'leggings', 'boots', 'helmet')
TFC_ARMOR_SECTIONS = ('helmet', 'chestplate', 'greaves', 'boots')
VANILLA_ARMOR_TYPES = ('leather', 'golden', 'iron', 'diamond', 'netherite')
VANILLA_TOOLS = ('sword', 'shovel', 'pickaxe', 'axe', 'hoe')
MOB_ARMOR_METALS = ('copper', 'bronze', 'black_bronze', 'bismuth_bronze', 'wrought_iron')
MOB_TOOLS = ('axe', 'sword', 'javelin', 'mace', 'scythe')
STONE_MOB_TOOLS = ('axe', 'javelin')
TFC_BIOMES = ('badlands', 'inverted_badlands', 'canyons', 'low_canyons', 'plains', 'plateau', 'hills', 'rolling_hills', 'lake', 'lowlands', 'salt_marsh', 'mountains', 'volcanic_mountains', 'old_mountains', 'oceanic_mountains', 'volcanic_oceanic_mountains', 'ocean', 'ocean_reef', 'deep_ocean', 'deep_ocean_trench', 'river', 'shore', 'tidal_shore', 'mountain_river', 'volcanic_mountain_river', 'old_mountain_river', 'oceanic_mountain_river', 'volcanic_oceanic_mountain_river', 'mountain_lake', 'volcanic_mountain_lake', 'old_mountain_lake', 'oceanic_mountain_lake', 'volcanic_oceanic_mountain_lake', 'plateau_lake')
PAINTINGS = ('golden_field', 'hot_spring', 'lake', 'supports', 'volcano')
VANILLA_TRIMS = ('coast', 'sentry', 'dune', 'wild', 'ward', 'eye', 'vex', 'tide', 'snout', 'rib', 'spire', 'wayfinder', 'shaper', 'silence', 'raiser', 'host')

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


VANILLA_OVERRIDE_LANG = {
    'item.minecraft.glow_ink_sac': 'Glowing Ink Sac',
    'item.minecraft.shield': 'Wooden Shield',
    'block.minecraft.bell': 'Golden Bell',
    'block.minecraft.slime_block': 'Glue Block',
    'block.minecraft.loom': 'Banner Loom',
    **dict(('item.minecraft.shield.%s' % color, '%s Wooden Shield' % lang(color)) for color in COLORS),
}

# This is here as it's used only once in a generic lang call by generate_resources.py
DEFAULT_LANG = {
    # Misc
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
    'subtitles.item.tfc.cool': 'Something hisses',
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
    'subtitles.entity.tfc.tiger.death': 'Tiger dies',
    'subtitles.entity.tfc.tiger.attack': 'Tiger roars',
    'subtitles.entity.tfc.tiger.ambient': 'Tiger chuffs',
    'subtitles.entity.tfc.tiger.hurt': 'Tiger yowls',
    'subtitles.entity.tfc.tiger.sleep': 'Tiger snores',
    'subtitles.entity.tfc.crocodile.death': 'Crocodile dies',
    'subtitles.entity.tfc.crocodile.attack': 'Crocodile roars',
    'subtitles.entity.tfc.crocodile.ambient': 'Crocodile snorts',
    'subtitles.entity.tfc.crocodile.hurt': 'Crocodile roars',
    'subtitles.entity.tfc.crocodile.sleep': 'Crocodile snores',
    'subtitles.entity.tfc.bongo.death': 'Bongo dies',
    'subtitles.entity.tfc.bongo.ambient': 'Bongo brays',
    'subtitles.entity.tfc.bongo.hurt': 'Bongo yelps',
    'subtitles.entity.tfc.caribou.death': 'Caribou dies',
    'subtitles.entity.tfc.caribou.ambient': 'Caribou brays',
    'subtitles.entity.tfc.caribou.hurt': 'Caribou yelps',
    'subtitles.entity.tfc.deer.death': 'Deer dies',
    'subtitles.entity.tfc.deer.ambient': 'Deer brays',
    'subtitles.entity.tfc.deer.hurt': 'Deer yelps',
    'subtitles.entity.tfc.gazelle.death': 'Gazelle dies',
    'subtitles.entity.tfc.gazelle.ambient': 'Gazelle brays',
    'subtitles.entity.tfc.gazelle.hurt': 'Gazelle yelps',
    'subtitles.entity.tfc.moose.death': 'Moose dies',
    'subtitles.entity.tfc.moose.ambient': 'Moose brays',
    'subtitles.entity.tfc.moose.hurt': 'Moose yelps',
    'subtitles.entity.tfc.moose.attack': 'Moose groans',
    'subtitles.entity.tfc.boar.death': 'Boar dies',
    'subtitles.entity.tfc.boar.ambient': 'Boar oinks',
    'subtitles.entity.tfc.boar.hurt': 'Boar squeals',
    'subtitles.entity.tfc.boar.attack': 'Boar grunts',
    'subtitles.entity.tfc.wildbeest.death': 'Wildebeest dies',
    'subtitles.entity.tfc.wildebeest.ambient': 'Wildebeest grunts',
    'subtitles.entity.tfc.wildebeest.hurt': 'Wildebeest yelps',
    'subtitles.entity.tfc.wildebeest.attack': 'Wildebeest rams',
    'subtitles.entity.tfc.grouse.death': 'Grouse dies',
    'subtitles.entity.tfc.grouse.ambient': 'Grouse calls',
    'subtitles.entity.tfc.grouse.hurt': 'Grouse squeals',
    'subtitles.entity.tfc.pheasant.chick.ambient': 'Chick chirps',
    'subtitles.entity.tfc.pheasant.hurt': 'Pheasant crows',
    'subtitles.entity.tfc.pheasant.death': 'Pheasant dies',
    'subtitles.entity.tfc.pheasant.ambient': 'Pheasant calls',
    'subtitles.entity.tfc.turkey.death': 'Turkey dies',
    'subtitles.entity.tfc.turkey.ambient': 'Turkey gobbles',
    'subtitles.entity.tfc.turkey.hurt': 'Turkey yelps',
    'subtitles.entity.tfc.peafowl.death': 'Peacock dies',
    'subtitles.entity.tfc.peafowl.ambient': 'Peacock crows',
    'subtitles.entity.tfc.peafowl.hurt': 'Peacock yelps',
    'subtitles.entity.tfc.rat.death': 'Rat dies',
    'subtitles.entity.tfc.rat.ambient': 'Rat squeaks',
    'subtitles.entity.tfc.rat.hurt': 'Rat squeals',
    'subtitles.entity.tfc.rooster.cry': 'Rooster calls',
    'subtitles.entity.tfc.dog.ambient': 'Dog Barks',
    'subtitles.entity.tfc.dog.hurt': 'Dog Yelps',
    'subtitles.entity.tfc.dog.death': 'Dog Dies',
    'subtitles.entity.tfc.dog.attack': 'Dog Bites',
    'subtitles.entity.tfc.dog.sleep': 'Dog Snores',
    'subtitles.entity.tfc.tfc_wolf.ambient': 'Wolf barks',
    'subtitles.entity.tfc.tfc_wolf.hurt': 'Wolf yelps',
    'subtitles.entity.tfc.tfc_wolf.death': 'Wolf dies',
    'subtitles.entity.tfc.tfc_wolf.attack': 'Wolf bites',
    'subtitles.entity.tfc.tfc_wolf.sleep': 'Wolf snores',
    'subtitles.entity.tfc.hyena.ambient': 'Hyena laughs',
    'subtitles.entity.tfc.hyena.hurt': 'Hyena yelps',
    'subtitles.entity.tfc.hyena.death': 'Hyena dies',
    'subtitles.entity.tfc.hyena.attack': 'Hyena bites',
    'subtitles.entity.tfc.hyena.sleep': 'Hyena snores',
    'subtitles.entity.tfc.ramming.impact': 'Ram impacts',
    **dict(('subtitles.entity.tfc.%s.ambient' % fish, '%s splashes' % fish.title().replace('_', ' ')) for fish in (*SIMPLE_FRESHWATER_FISH, 'manatee', 'jellyfish')),
    **dict(('subtitles.entity.tfc.%s.flop' % fish, '%s flops' % fish.title().replace('_', ' ')) for fish in (*SIMPLE_FRESHWATER_FISH, 'manatee', 'jellyfish')),
    **dict(('subtitles.entity.tfc.%s.death' % fish, '%s dies' % fish.title().replace('_', ' ')) for fish in (*SIMPLE_FRESHWATER_FISH, 'manatee', 'jellyfish')),
    **dict(('subtitles.entity.tfc.%s.hurt' % fish, '%s hurts' % fish.title().replace('_', ' ')) for fish in (*SIMPLE_FRESHWATER_FISH, 'manatee', 'jellyfish')),
    'subtitles.generic.tfc.dirt_slide': 'Soil landslides',
    'subtitles.generic.tfc.rock_slide_long': 'Rock collapses',
    'subtitles.generic.tfc.rock_slide_long_fake': 'Rock creaks',
    'subtitles.generic.tfc.rock_slide_short': 'Rock crumbles',
    'subtitles.generic.tfc.rock_smash': 'Rock smashes',

    # Creative Tabs
    'tfc.creative_tab.earth': 'TFC Earth',
    'tfc.creative_tab.ores': 'TFC Ores',
    'tfc.creative_tab.rock': 'TFC Rock Stuffs',
    'tfc.creative_tab.metals': 'TFC Metal Stuffs',
    'tfc.creative_tab.wood': 'TFC Wooden Stuffs',
    'tfc.creative_tab.flora': 'TFC Flora',
    'tfc.creative_tab.devices': 'TFC Devices',
    'tfc.creative_tab.food': 'TFC Food',
    'tfc.creative_tab.misc': 'TFC Misc',
    'tfc.creative_tab.decorations': 'TFC Decorations',
    # Containers
    'tfc.screen.calendar': 'Calendar',
    'tfc.screen.nutrition': 'Nutrition',
    'tfc.screen.climate': 'Climate',
    'tfc.screen.knapping': 'Knapping',
    'tfc.screen.scribing_table': 'Rename Items',
    'tfc.screen.pet_command': 'Pet Commands',
    'tfc.screen.sewing_table': 'Sewing Table',
    # Tooltips
    'tfc.tooltip.forging': '§f - Can Work',
    'tfc.tooltip.welding': '§f - Can Weld',
    'tfc.tooltip.danger': '§f - Danger!!',
    'tfc.tooltip.anvil_plan': 'Plans',
    'tfc.tooltip.anvil_tier_required': 'Requires %s Anvil',
    'tfc.tooltip.calendar_days_years': '%d, %04d',
    'tfc.tooltip.calendar_hour_minute_month_day_year': '%s %s %d, %04d',
    'tfc.tooltip.calendar_season': 'Season : %s',
    'tfc.tooltip.calendar_day': 'Day : %s',
    'tfc.tooltip.calendar_birthday': '%s\'s Birthday!',
    'tfc.tooltip.calendar_date': 'Date : %s',
    'tfc.tooltip.climate_koppen_climate_classification': 'Climate: %s',
    'tfc.tooltip.climate_average_temperature': 'Avg. Temp: %s',
    'tfc.tooltip.climate_annual_rainfall': 'Annual Rainfall: %smm',
    'tfc.tooltip.climate_current_temp': 'Current Temp: %s',
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
    'tfc.tooltip.berry_bush.not_underwater': 'Must be underwater to grow!',
    'tfc.tooltip.fertilizer.nitrogen': '§b(N) Nitrogen: §r%s%%',
    'tfc.tooltip.fertilizer.phosphorus': '§6(P) Phosphorus: §r%s%%',
    'tfc.tooltip.fertilizer.potassium': '§d(K) Potassium: §r%s%%',
    'tfc.tooltip.seal_barrel': 'Seal',
    'tfc.tooltip.unseal_barrel': 'Unseal',
    'tfc.tooltip.while_sealed': 'While sealed',
    'tfc.tooltip.while_sealed_description': 'While the barrel is sealed and the required fluid is present',
    'tfc.tooltip.windmill_not_enough_space': 'There is not enough space to place a windmill here!',
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
    'tfc.tooltip.animal.cannot_pluck': 'This animal cannot be plucked for %s',
    'tfc.tooltip.animal.cannot_pluck_old_or_sick': 'This animal is too worn out to be plucked.',
    'tfc.tooltip.scribing_table.missing_ink': 'Ink is missing!',
    'tfc.tooltip.scribing_table.invalid_ink': 'Item isn\'t ink!',
    'tfc.tooltip.deals_damage.slashing': '§7Deals §fSlashing§7 Damage',
    'tfc.tooltip.deals_damage.piercing': '§7Deals §fPiercing§7 Damage',
    'tfc.tooltip.deals_damage.crushing': '§7Deals §fCrushing§7 Damage',
    'tfc.tooltip.resists_damage': '§7Resistances: §fSlashing§r %s, §fPiercing§r %s, §fCrushing§r %s',
    'tfc.tooltip.immune_to_damage': 'Immune',
    'tfc.tooltip.pot_boiling': 'Boiling!',
    'tfc.tooltip.pot_finished': 'Finished',
    'tfc.tooltip.pot_ready': 'Ready',
    'tfc.tooltip.infestation': 'This container has a foul smell.',
    'tfc.tooltip.usable_in_pan': 'Can be processed with a pan',
    'tfc.tooltip.usable_in_sluice': 'Can be processed in a sluice',
    'tfc.tooltip.usable_in_sluice_and_pan': 'Can be processed with a sluice or pan',
    'tfc.tooltip.powderkeg.disabled': 'Powderkegs are disabled on this server!',
    'tfc.tooltip.glass.title': 'Glass Operations:',
    'tfc.tooltip.glass.not_hot_enough': 'The glass is not hot enough to manipulate.',
    'tfc.tooltip.glass.tool_description': '§7Performs §f%s',
    'tfc.tooltip.glass.silica': 'Silica Glass',
    'tfc.tooltip.glass.hematitic': 'Hematitic Glass',
    'tfc.tooltip.glass.olivine': 'Olivine Glass',
    'tfc.tooltip.glass.volcanic': 'Volcanic Glass',
    'tfc.tooltip.glass.flatten_me': 'Right click with a paddle to flatten',
    'tfc.tooltip.sealed': 'Sealed',
    'tfc.tooltip.unsealed': 'Unsealed',
    'tfc.tooltip.switch_sides': 'Switch Sides',
    'tfc.tooltip.legend': 'Legend',
    'tfc.tooltip.chance': '%s%% chance',
    'tfc.tooltip.wind_speed': '%s km/h, %s%% %s, %s%% %s',
    'tfc.tooltip.javelin.thrown_damage': '%s Thrown Damage',
    'tfc.tooltip.rotation.angular_velocity': 'Rotating at \u03c9=%s rad/s',
    'tfc.tooltip.sewing.dark_cloth': 'Dark Cloth',
    'tfc.tooltip.sewing.light_cloth': 'Light Cloth',
    'tfc.tooltip.sewing.stitch': 'Stitch',
    'tfc.tooltip.sewing.remove_stitch': 'Remove Stitch',
    'tfc.tooltip.sewing.select_recipe': 'Select Recipe',

    **dict(('trim_material.tfc.%s' % mat, lang('%s material', mat)) for mat in TRIM_MATERIALS),

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
    'tfc.jade.yield': 'Yield Multiplier: %s%%',
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

    'config.jade.plugin_tfc.barrel': 'Barrel',
    'config.jade.plugin_tfc.bellows': 'Bellows',
    'config.jade.plugin_tfc.sapling': 'Sapling',
    'config.jade.plugin_tfc.blast_furnace': 'Blast Furnace',
    'config.jade.plugin_tfc.bloomery': 'Bloomery',
    'config.jade.plugin_tfc.bloom': 'Bloom',
    'config.jade.plugin_tfc.charcoal_forge': 'Charcoal Forge',
    'config.jade.plugin_tfc.composter': 'Composter',
    'config.jade.plugin_tfc.crop': 'Crop',
    'config.jade.plugin_tfc.crucible': 'Crucible',
    'config.jade.plugin_tfc.firepit': 'Firepit',
    'config.jade.plugin_tfc.fruit_tree_sapling': 'Fruit Tree Sapling',
    'config.jade.plugin_tfc.hoe_overlay': 'Hoe Overlay',
    'config.jade.plugin_tfc.lamp': 'Lamp',
    'config.jade.plugin_tfc.nest_box': 'Nest Box',
    'config.jade.plugin_tfc.pit_kiln_internal': 'Pit Kiln',
    'config.jade.plugin_tfc.pit_kiln_above': 'Pit Kiln',
    'config.jade.plugin_tfc.powder_keg': 'Powder Keg',
    'config.jade.plugin_tfc.torch': 'Torch',
    'config.jade.plugin_tfc.wall_torch': 'Torch',
    'config.jade.plugin_tfc.candle': 'Candle',
    'config.jade.plugin_tfc.candle_cake': 'Candle Cake',
    'config.jade.plugin_tfc.jack_o_lantern': 'Jack O Lantern',
    'config.jade.plugin_tfc.mud_bricks': 'Mud Bricks',
    'config.jade.plugin_tfc.decaying': 'Decaying Block',
    'config.jade.plugin_tfc.loom': 'Loom',
    'config.jade.plugin_tfc.sheet_pile': 'Sheet Pile',
    'config.jade.plugin_tfc.ingot_pile': 'Ingot Pile',
    'config.jade.plugin_tfc.axle': 'Axle',
    'config.jade.plugin_tfc.encased_axle': 'Encased Axle',
    'config.jade.plugin_tfc.clutch': 'Clutch',
    'config.jade.plugin_tfc.hand_wheel': 'Hand Wheel',
    'config.jade.plugin_tfc.gearbox': 'Gearbox',
    'config.jade.plugin_tfc.crankshaft': 'Crankshaft',
    'config.jade.plugin_tfc.quern': 'Quern',
    'config.jade.plugin_tfc.water_wheel': 'Water Wheel',
    'config.jade.plugin_tfc.windmill': 'Windmill',
    'config.jade.plugin_tfc.hot_poured_glass': 'Hot Poured Glass',

    'config.jade.plugin_tfc.animal': 'Animal',
    'config.jade.plugin_tfc.frog': 'Frog',
    'config.jade.plugin_tfc.horse': 'Horse',
    'config.jade.plugin_tfc.chested_horse': 'Chested Horse',
    'config.jade.plugin_tfc.wild_animal': 'Wild Animal',
    'config.jade.plugin_tfc.squid': 'Squid',
    'config.jade.plugin_tfc.fish': 'Fish',
    'config.jade.plugin_tfc.predator': 'Predator',
    'config.jade.plugin_tfc.pack_predator': 'Pack Predator',
    'config.jade.plugin_tfc.ocelot': 'Ocelot',
    'config.jade.plugin_tfc.rabbit': 'Rabbit',
    'config.jade.plugin_tfc.fishing_hook': 'Fishing Hook',


    # Commands

    'tfc.commands.time.query.daytime': 'The day time is %s',
    'tfc.commands.time.query.game_time': 'The game time is %s',
    'tfc.commands.time.query.day': 'The day is %s',
    'tfc.commands.time.query.player_ticks': 'The player ticks is %s',
    'tfc.commands.time.query.calendar_ticks': 'The calendar ticks is %s',
    'tfc.commands.heat.set_heat': 'Held item heat set to %s',
    'tfc.commands.clear_world.starting': 'Clearing world. Prepare for lag...',
    'tfc.commands.clear_world.done': 'Cleared %d Block(s).',
    'tfc.commands.count_block.done': 'Found %d',
    'tfc.commands.player.query_hunger': 'Hunger is %s / 20',
    'tfc.commands.player.query_saturation': 'Saturation is %s / 20',
    'tfc.commands.player.query_water': 'Water is %s / 100',
    'tfc.commands.player.query_nutrition': 'Player nutrition:',
    'tfc.commands.player.fail_invalid_food_stats': 'Player does not have any TFC nutrition or hydration data',
    'tfc.commands.locate.unknown_vein': 'Unknown vein: %s',
    'tfc.commands.locate.vein_not_found': 'Unable to find vein %s within reasonable distance (16 chunks radius)',
    'tfc.commands.locate.invalid_biome_source': 'This world does not have a compatible biome source',
    'tfc.commands.locate.volcano_not_found': 'Could not find a volcano within reasonable distance',
    'tfc.commands.propick.found_blocks': 'The propick scan found %s %s',
    'tfc.commands.propick.cleared': 'Cleared %s blocks, Found %s prospectable blocks',
    'tfc.commands.particle.no_fluid': 'Unknown Fluid: %s',
    'tfc.commands.trim.not_applied': 'A trim cannot be applied to this item',
    'tfc.commands.trim.not_armor': 'The metal specified does not have armor items',
    'tfc.commands.trim.bad_material': 'Material item not recognized',
    'tfc.commands.trim.bad_template': 'Template item not recognized',

    # Create World Screen Options
    'tfc.settings.km': '%s km',
    'generator.tfc.overworld': 'TerraFirmaCraft',
    'tfc.tooltip.create_world.title': 'TerraFirmaCraft World Settings',
    'tfc.create_world.flat_bedrock': 'Flat Bedrock',
    'tfc.create_world.spawn_distance': 'Spawn Distance',
    'tfc.create_world.spawn_distance.tooltip': 'Radial distance from the spawn center that the world spawn point can be.',
    'tfc.create_world.spawn_center_x': 'Spawn Center X',
    'tfc.create_world.spawn_center_x.tooltip': 'The midpoint of x positions that the world spawn can be.',
    'tfc.create_world.spawn_center_z': 'Spawn Center Z',
    'tfc.create_world.spawn_center_z.tooltip': 'The midpoint of z positions that the world spawn can be.',
    'tfc.create_world.temperature_scale': 'Temperature Scale',
    'tfc.create_world.temperature_scale.tooltip': 'The distance between temperature peaks / poles / extremes.',
    'tfc.create_world.rainfall_scale': 'Rainfall Scale',
    'tfc.create_world.rainfall_scale.tooltip': 'The distance between rainfall peaks / poles / extremes.',
    'tfc.create_world.temperature_constant': 'Constant Temperature',
    'tfc.create_world.temperature_constant.tooltip': 'The relative constant temperature of a world.',
    'tfc.create_world.rainfall_constant': 'Constant Rainfall',
    'tfc.create_world.rainfall_constant.tooltip': 'The relative constant rainfall of a world.',
    'tfc.create_world.continentalness': 'Continentalness',
    'tfc.create_world.continentalness.tooltip': 'The proportion of the world that is made up of land rather than water',
    'tfc.create_world.grass_density': 'Grass Density',
    'tfc.create_world.grass_density.tooltip': 'Multiplier that applies to the amount of short and tall grass placed within a chunk.',

    # Entities
    **dict(('entity.tfc.%s' % fish, lang(fish)) for fish in SIMPLE_FRESHWATER_FISH),
    'entity.tfc.cod': 'Cod',
    'entity.tfc.pufferfish': 'Pufferfish',
    'entity.tfc.tropical_fish': 'Tropical Fish',
    'entity.tfc.jellyfish': 'Jellyfish',
    'entity.tfc.manatee': 'Manatee',
    'entity.tfc.orca': 'Orca',
    'entity.tfc.dolphin': 'Dolphin',
    'entity.tfc.isopod': 'Isopod',
    'entity.tfc.lobster': 'Lobster',
    'entity.tfc.crayfish': 'Crayfish',
    'entity.tfc.horseshoe_crab': 'Horseshoe Crab',
    'entity.tfc.penguin': 'Penguin',
    'entity.tfc.frog': 'Frog',
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
    'entity.tfc.rabbit.female': 'Doe Rabbit',
    'entity.tfc.rabbit.male': 'Buck Rabbit',
    'entity.tfc.polar_bear': 'Polar Bear',
    'entity.tfc.grizzly_bear': 'Grizzly Bear',
    'entity.tfc.black_bear': 'Black Bear',
    'entity.tfc.cougar': 'Cougar',
    'entity.tfc.panther': 'Panther',
    'entity.tfc.lion': 'Lion',
    'entity.tfc.sabertooth': 'Sabertooth',
    'entity.tfc.tiger': 'Tiger',
    'entity.tfc.crocodile': 'Crocodile',
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
    'entity.tfc.wildebeest': 'Wildebeest',
    'entity.tfc.ocelot': 'Ocelot',
    'entity.tfc.bongo': 'Bongo',
    'entity.tfc.caribou': 'Caribou',
    'entity.tfc.deer': 'Deer',
    'entity.tfc.gazelle': 'Gazelle',
    'entity.tfc.moose': 'Moose',
    'entity.tfc.grouse': 'Grouse',
    'entity.tfc.pheasant': 'Pheasant',
    'entity.tfc.turkey': 'Turkey',
    'entity.tfc.peafowl': 'Peafowl',
    'entity.tfc.peafowl.male': 'Peacock',
    'entity.tfc.peafowl.female': 'Peahen',
    'entity.tfc.rat': 'Rat',
    'entity.tfc.cat': 'Cat',
    'entity.tfc.cat.female': 'Female Cat',
    'entity.tfc.cat.male': 'Male Cat',
    'entity.tfc.dog': 'Dog',
    'entity.tfc.dog.male': 'Male Dog',
    'entity.tfc.dog.female': 'Female Dog',
    'entity.tfc.wolf': 'Wolf',
    'entity.tfc.hyena': 'Hyena',
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
    **{'entity.tfc.boat.%s' % wood: lang('%s boat', wood) for wood in WOODS.keys()},
    **{'entity.tfc.chest_boat.%s' % wood: lang('%s boat with chest', wood) for wood in WOODS.keys()},

    # Enums

    **dict(('tfc.enum.tier.tier_%s' % tier, 'Tier %s' % tier.upper()) for tier in ('0', 'i', 'ii', 'iii', 'iv', 'v', 'vi')),
    **lang_enum('heat', ('warming', 'hot', 'very_hot', 'faint_red', 'dark_red', 'bright_red', 'orange', 'yellow', 'yellow_white', 'white', 'brilliant_white')),
    **lang_enum('month', ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')),
    **lang_enum('day', ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')),
    **lang_enum('foresttype', ('sparse', 'old_growth', 'normal', 'edge', 'none')),
    **lang_enum('koppenclimateclassification', ('arctic', 'tundra', 'humid_subarctic', 'subarctic', 'cold_desert', 'hot_desert', 'temperate', 'subtropical', 'humid_subtropical', 'humid_oceanic', 'humid_subtropical', 'tropical_savanna', 'tropical_rainforest')),
    **lang_enum('direction', ('north', 'south', 'east', 'west', 'down', 'up')),
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
    'tfc.enum.horse_variant.white': 'White Variant',
    'tfc.enum.horse_variant.creamy': 'Creamy Variant',
    'tfc.enum.horse_variant.chestnut': 'Chestnut Variant',
    'tfc.enum.horse_variant.brown': 'Brown Variant',
    'tfc.enum.horse_variant.black': 'Black Variant',
    'tfc.enum.horse_variant.gray': 'Gray Variant',
    'tfc.enum.horse_variant.dark_brown': 'Dark Brown',
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
    'tfc.enum.forgingbonus.modestly_forged': 'Modestly Forged',
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
    'tfc.enum.glassoperation.blow': 'Blow',
    'tfc.enum.glassoperation.roll': 'Roll',
    'tfc.enum.glassoperation.stretch': 'Stretch',
    'tfc.enum.glassoperation.pinch': 'Pinch',
    'tfc.enum.glassoperation.flatten': 'Flatten',
    'tfc.enum.glassoperation.saw': 'Saw',
    'tfc.enum.glassoperation.amethyst': 'Amethyst Powder',
    'tfc.enum.glassoperation.soda_ash': 'Soda Ash',
    'tfc.enum.glassoperation.sulfur': 'Sulfur',
    'tfc.enum.glassoperation.iron': 'Iron Powder',
    'tfc.enum.glassoperation.ruby': 'Ruby Powder',
    'tfc.enum.glassoperation.lapis_lazuli': 'Lapis Powder',
    'tfc.enum.glassoperation.pyrite': 'Pyrite Powder',
    'tfc.enum.glassoperation.sapphire': 'Sapphire Powder',
    'tfc.enum.glassoperation.gold': 'Gold Powder',
    'tfc.enum.glassoperation.graphite': 'Graphite Powder',
    'tfc.enum.glassoperation.copper': 'Copper Powder',
    'tfc.enum.glassoperation.nickel': 'Nickel Powder',
    'tfc.enum.glassoperation.tin': 'Tin Powder',
    'tfc.enum.glassoperation.silver': 'Silver Powder',
    'tfc.enum.glassoperation.table_pour': 'Table Pour',
    'tfc.enum.glassoperation.basin_pour': 'Basin Pour',
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
    'tfc.enum.rabbit_variant.brown': 'Brown Fur',
    'tfc.enum.rabbit_variant.white': 'White Fur',
    'tfc.enum.rabbit_variant.black': 'Black Fur',
    'tfc.enum.rabbit_variant.white_splotched': 'White Splotched Fur',
    'tfc.enum.rabbit_variant.gold': 'Golden Fur',
    'tfc.enum.rabbit_variant.salt': 'Salty Fur',
    'tfc.enum.rabbit_variant.evil': '§cEvil',
    'tfc.enum.rockcategory.igneous_intrusive': 'Igneous Intrusive',
    'tfc.enum.rockcategory.igneous_extrusive': 'Igneous Extrusive',
    'tfc.enum.rockcategory.sedimentary': 'Sedimentary',
    'tfc.enum.rockcategory.metamorphic': 'Metamorphic',
    'tfc.enum.rockdisplaycategory.mafic_igneous_intrusive': 'Mafic Igneous Intrusive',
    'tfc.enum.rockdisplaycategory.intermediate_igneous_intrusive': 'Igneous Intrusive',
    'tfc.enum.rockdisplaycategory.felsic_igneous_intrusive': 'Felsic Igneous Intrusive',
    'tfc.enum.rockdisplaycategory.mafic_igneous_extrusive': 'Igneous Extrusive',
    'tfc.enum.rockdisplaycategory.intermediate_igneous_extrusive': 'Igneous Extrusive',
    'tfc.enum.rockdisplaycategory.felsic_igneous_extrusive': 'Igneous Extrusive',
    'tfc.enum.rockdisplaycategory.sedimentary': 'Sedimentary',
    'tfc.enum.rockdisplaycategory.metamorphic': 'Metamorphic',

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
    'painting.tfc.golden_field.title': 'Golden Field',
    'painting.tfc.golden_field.author': 'EERussianguy',
    'painting.tfc.hot_spring.title': 'Spring Dream',
    'painting.tfc.hot_spring.author': 'EERussianguy',
    'painting.tfc.volcano.title': 'Magma Rising',
    'painting.tfc.volcano.author': 'EERussianguy',
    'painting.tfc.supports.title': 'Endless Mineshaft',
    'painting.tfc.supports.author': 'Facu',
    'painting.tfc.lake.title': 'Lake',
    'painting.tfc.lake.author': 'Pxlsamosa',
    **dict(('metal.tfc.%s' % metal, lang(metal)) for metal in METALS.keys()),

    'tfc.jei.heating': 'Heating Recipe',
    'tfc.jei.quern': 'Quern Recipe',
    'tfc.jei.scraping': 'Scraping Recipe',
    'tfc.jei.clay_knapping': 'Clay Knapping Recipe',
    'tfc.jei.fire_clay_knapping': 'Fire Clay Knapping Recipe',
    'tfc.jei.leather_knapping': 'Leather Knapping Recipe',
    'tfc.jei.rock_knapping': 'Rock Knapping Recipe',
    'tfc.jei.goat_horn_knapping': 'Goat Horn Knapping Recipe',
    'tfc.jei.soup_pot': 'Soup Pot',
    'tfc.jei.simple_pot': 'Pot',
    'tfc.jei.jam_pot': 'Jam Pot',
    'tfc.jei.casting': 'Casting',
    'tfc.jei.alloying': 'Alloying',
    'tfc.jei.loom': 'Loom',
    'tfc.jei.glassworking': 'Glassworking',
    'tfc.jei.blast_furnace': 'Blast Furnace',
    'tfc.jei.instant_barrel': 'Instant Barrel Recipe',
    'tfc.jei.instant_fluid_barrel': 'Instant Fluid Barrel Recipe',
    'tfc.jei.sealed_barrel': 'Sealed Barrel Recipe',
    'tfc.jei.bloomery': 'Bloomery',
    'tfc.jei.welding': 'Welding',
    'tfc.jei.anvil': 'Anvil',
    'tfc.jei.chisel': 'Chisel',
    'tfc.jei.sewing': 'Sewing',

    'tfc.field_guide.book_name': 'TerraFirmaCraft',
    'tfc.field_guide.book_landing_text': 'Welcome traveller! This book will be the source of all you need to know as you explore the world of TerraFirmaCraft (TFC).'
}

# Automatically Generated by generate_trees.py
TREE_SAPLING_DROP_CHANCES = {
    'acacia': 0.0292,
    'ash': 0.0428,
    'aspen': 0.0428,
    'birch': 0.0311,
    'blackwood': 0.0780,
    'chestnut': 0.0112,
    'douglas_fir': 0.0132,
    'hickory': 0.0140,
    'kapok': 0.0115,
    'mangrove': 0.0447,
    'maple': 0.0201,
    'oak': 0.0130,
    'palm': 0.0911,
    'pine': 0.0248,
    'rosewood': 0.0193,
    'sequoia': 0.0238,
    'spruce': 0.0318,
    'sycamore': 0.0175,
    'white_cedar': 0.0318,
    'willow': 0.0143,
}

from mcresources import ResourceManager, utils, advancements
from mcresources.advancements import AdvancementCategory
from mcresources.type_definitions import Json

from constants import *


def generate(rm: ResourceManager):
    story = AdvancementCategory(rm, 'story', 'tfc:textures/block/rock/mossy_cobble/diorite.png')

    story.advancement('root', icon('tfc:metal/hammer/wrought_iron'), 'TerraFirmaCraft Story', 'TFC\'s main progression line', None, root_trigger(), chat=False)
    story.advancement('find_rock', icon('tfc:rock/loose/schist'), 'Just a Rock', 'Pick up a stone from the ground', 'root', inventory_changed('#tfc:rock_knapping'))
    story.advancement('stone_age', icon('tfc:stone/axe/sedimentary'), 'Paleolithic!', 'Enter the Stone Age by making a stone tool', 'find_rock', inventory_changed('#tfc:stone_tools'))
    story.advancement('get_straw', icon('tfc:straw'), 'Grasping at Straws', 'Cut some grass with a knife to get straw', 'stone_age', inventory_changed('tfc:straw'))
    story.advancement('logging', icon('tfc:wood/log/oak'), 'Timberrr!', 'Use an axe to cut down a tree', 'get_straw', inventory_changed('#minecraft:logs'))
    story.advancement('firestarter', icon('tfc:firestarter'), 'Embers', 'Craft a firestarter from two sticks', 'logging', inventory_changed('tfc:firestarter'))
    story.advancement('firepit', icon('tfc:firepit'), 'Into Fire', 'Use the firestarter to light some logs and sticks', 'firestarter', generic('tfc:firepit_created', {'block': 'tfc:firepit'}))
    story.advancement('pot', icon('tfc:pot'), 'Pot Head', 'Add a ceramic pot to the firepit', 'firepit', generic('tfc:firepit_created', {'block': 'tfc:pot'}))
    story.advancement('grill', icon('tfc:grill'), 'Grilling Time!', 'Add a wrought iron grill to the firepit', 'firepit', generic('tfc:firepit_created', {'block': 'tfc:grill'}))
    # Parented to firestarter
    story.advancement('charcoal', icon('minecraft:charcoal'), 'A Better Fuel', 'Get some charcoal from a charcoal pit', 'firestarter', inventory_changed('minecraft:charcoal'))
    story.advancement('forge', icon('tfc:rock/cobble/andesite'), 'Forging', 'Light a charcoal forge', 'charcoal', generic('tfc:lit', {'block': 'tfc:charcoal_forge'}))
    story.advancement('stone_anvil', icon('tfc:stone/hammer/sedimentary'), 'Hammer Time', 'Create a stone anvil with a hammer', 'forge', generic('tfc:rock_anvil', {'tag': 'tfc:rock_anvils'}))
    story.advancement('metal_anvil', icon('tfc:metal/anvil/copper'), 'Dropping the Anvil', 'Create a copper anvil out of double ingots', 'stone_anvil', inventory_changed('tfc:metal/anvil/copper'))
    story.advancement('fire_clay', icon('tfc:fire_clay'), 'Fireproof', 'Craft some fire clay', 'metal_anvil', inventory_changed('tfc:fire_clay'))
    story.advancement('crucible', icon('tfc:crucible'), 'The Crucible', 'Knap a crucible out of some fire clay and fire it', 'fire_clay', inventory_changed('tfc:crucible'))
    # Parented to firestarter
    story.advancement('find_clay', icon('minecraft:clay_ball'), 'Locating Clay', 'Find and dig clay for pottery', 'firestarter', inventory_changed('minecraft:clay_ball'))
    story.advancement('knap_clay', icon('tfc:ceramic/unfired_vessel'), 'Clay Forming', 'Knap clay into a new shape', 'find_clay', inventory_changed('#tfc:unfired_pottery'))
    story.advancement('pit_kiln', icon('tfc:ceramic/vessel'), 'Potter', 'Light a pit kiln on fire', 'knap_clay', generic('tfc:lit', {'block': 'tfc:pit_kiln'}))
    story.advancement('mold', icon('tfc:ceramic/axe_head_mold'), 'Pouring Metal', 'Fire a mold for making a metal tool head', 'pit_kiln', inventory_changed('#tfc:fired_molds'))
    story.advancement('copper_age', icon('tfc:metal/axe_head/copper'), 'The Copper Age', 'Enter the Copper Age by smithing a copper tool', 'mold', inventory_changed('#tfc:metal_item/copper_tools'))
    story.advancement('bronze_age', icon('tfc:metal/sword/bronze'), 'The Bronze Age', 'Enter the Bronze Age by smithing a bronze item', 'copper_age', multiple(inventory_changed('#tfc:metal_item/bronze'), inventory_changed('#tfc:metal_item/bismuth_bronze'), inventory_changed('#tfc:metal_item/black_bronze')))
    story.advancement('bloomery', icon('tfc:bloomery'), 'Ironworks', 'Craft a bloomery', 'bronze_age', inventory_changed('tfc:bloomery'))
    story.advancement('iron_bloom', icon('tfc:raw_iron_bloom'), 'In Bloom', 'Create an iron bloom', 'bloomery', inventory_changed('tfc:raw_iron_bloom'))
    story.advancement('iron_age', icon('tfc:metal/ingot/wrought_iron'), 'The Iron Age', 'Refine a bloom into wrought iron ingot', 'iron_bloom', inventory_changed('tfc:metal/ingot/wrought_iron'), frame='challenge')
    story.advancement('blast_furnace', icon('tfc:blast_furnace'), 'Blast off!', 'Craft a blast furnace', 'iron_age', inventory_changed('tfc:blast_furnace'))
    story.advancement('steel_age', icon('tfc:metal/ingot/steel'), 'Industrialized', 'Make your first steel item', 'blast_furnace', inventory_changed('#tfc:metal_item/steel'), frame='challenge')
    story.advancement('black_steel', icon('tfc:metal/ingot/black_steel'), 'Back in Black', 'Make your first black steel item', 'steel_age', inventory_changed('#tfc:metal_item/black_steel'), frame='challenge')
    story.advancement('blue_steel', icon('tfc:metal/ingot/blue_steel'), 'Feeling Blue', 'Make your first blue steel item', 'black_steel', inventory_changed('#tfc:metal_item/blue_steel'), frame='challenge')
    story.advancement('red_steel', icon('tfc:metal/ingot/red_steel'), 'Seeing Red', 'Make your first red steel item', 'black_steel', inventory_changed('#tfc:metal_item/red_steel'), frame='challenge')
    # Tool advancements parented to copper age
    story.advancement('pickaxe', icon('tfc:metal/pickaxe/copper'), 'Time to Mine (Finally!)', 'Make your first metal pickaxe', 'copper_age', inventory_changed('#tfc:pickaxes'))
    story.advancement('saw', icon('tfc:metal/saw/copper'), 'Carpenter', 'Make a metal saw', 'copper_age', inventory_changed('#tfc:saws'))
    story.advancement('chisel', icon('tfc:metal/chisel/copper'), 'Sculptor', 'Make a metal chisel', 'copper_age', inventory_changed('#tfc:chisels'))
    story.advancement('propick', icon('tfc:metal/propick/copper'), 'Prospector', 'Make a prospector\'s pickaxe', 'copper_age', inventory_changed('#tfc:propicks'))
    # Parented to chisel
    story.advancement('smooth_stone', icon('tfc:rock/smooth/chert'), 'Super Smooth', 'Chisel a piece of raw stone into a smooth stone block', 'chisel', generic('tfc:chiseled', {'tag': 'c:stones/smooth'}))
    story.advancement('raw_stone', icon('tfc:rock/hardened/basalt'), 'Raw Emotions', 'Isolate a piece of raw rock to make it pop off', 'smooth_stone', inventory_changed('#c:stones'))
    story.advancement('quern', icon('tfc:quern'), 'The Grind', 'Craft a quern and a handstone', 'raw_stone', multiple(inventory_changed('tfc:handstone'), inventory_changed('tfc:quern')), requirements=[['quern'], ['handstone']])
    story.advancement('flux', icon('tfc:powder/flux'), 'In Flux', 'Grind some flux using a quern', 'quern', inventory_changed('#tfc:flux'), frame='challenge')
    story.advancement('welding', icon('tfc:metal/double_ingot/copper'), 'Double Trouble', 'Weld a double ingot in an anvil', 'flux', inventory_changed('#c:double_ingots'))
    story.advancement('sheet', icon('tfc:metal/sheet/copper'), 'The Flattening', 'Pound a double ingot into a sheet', 'welding', inventory_changed('#c:sheets'))
    story.advancement('shield', icon('tfc:metal/shield/copper'), 'Take Cover!', 'Craft a shield from a double sheet', 'sheet', inventory_changed('#tfc:shields'))
    # Parented to saw
    story.advancement('barrel', icon('tfc:wood/barrel/acacia'), 'Do a Barrel Roll!', 'Craft a barrel using a saw and lumber', 'saw', inventory_changed('#tfc:barrels'))
    story.advancement('scraped_hide', icon('tfc:large_scraped_hide'), 'Soak and Scrape', 'Soak a hide in limewater and then scrape it', 'barrel', inventory_changed('#tfc:scraped_hides'))
    story.advancement('leather', icon('minecraft:leather'), 'Genuine Leather', 'Finish preparing the hide by soaking it in water then tannin', 'scraped_hide', inventory_changed('minecraft:leather'))
    story.advancement('bellows', icon('tfc:bellows'), 'Blow You Away', 'Make a bellows out of leather', 'leather', inventory_changed('tfc:bellows'))
    story.advancement('papyrus', icon('tfc:papyrus'), 'I\'m a Skeleton With Very High Standards!', 'Locate papyrus', 'barrel', inventory_changed('tfc:papyrus'))
    # Mechanical power advancements parented to copper age
    story.advancement('brass_mechanisms', icon('tfc:brass_mechanisms'), 'Mechanical Age', 'Make brass mechanisms', 'copper_age', inventory_changed('tfc:brass_mechanisms'))
    story.advancement('gear_box', icon('tfc:wood/gear_box/pine'), 'Gearing Up', 'Make a gear box', 'brass_mechanisms', inventory_changed('#tfc:gear_boxes'))
    story.advancement('trip_hammer', icon('tfc:trip_hammer'), 'Tripping Hazard', 'Make a trip hammer', 'brass_mechanisms', inventory_changed('tfc:trip_hammer'))
    story.advancement('crankshaft', icon('tfc:crankshaft'), 'Automatic Cranking', 'Make a crankshaft', 'brass_mechanisms', inventory_changed('tfc:crankshaft'))
    story.advancement('water_wheel', icon('tfc:wood/water_wheel/sequoia'), 'River Power', 'Make a water wheel', 'gear_box', inventory_changed('#tfc:water_wheels'))
    story.advancement('windmill', icon('tfc:windmill_blade'), 'Wind Power', 'Make a windmill and add five blades to it', 'gear_box', generic('tfc:max_windmill', None), frame='challenge')
    story.advancement('pump', icon('tfc:steel_pump'), 'Moving Water', 'Make a steel pump', 'gear_box', inventory_changed('tfc:steel_pump'), frame='challenge')
    # Glassblowing and metal block advancements parented to sheets
    story.advancement('plated_block', icon('tfc:metal/block/copper'), 'Mostly Wood', 'Make a metal plated block', 'sheet', inventory_changed('#tfc:metal_plated_blocks'))
    story.advancement('brass_plated_block', icon('tfc:metal/block/brass'), 'Pouring Table', 'Make a brass plated block, needed for glass pouring', 'plated_block', inventory_changed('tfc:metal/block/brass'))
    story.advancement('basin_pour', icon('minecraft:glass'), 'Basin Pour', 'Pour some glass into a basin', 'brass_plated_block', generic('tfc:basin_pour', None))
    story.advancement('glass_colors', icon('minecraft:red_stained_glass'), 'Stained Glass', 'Make all colors of stained glass blocks, as well as clear and tinted glass', 'basin_pour', multiple(inventory_changed('minecraft:glass'), inventory_changed('minecraft:tinted_glass'), *[inventory_changed('minecraft:%s_stained_glass' % c, name=c) for c in COLORS]), requirements=[['glass'], ['tinted_glass'], *[[c] for c in COLORS]], frame='challenge')
    story.advancement('table_pour', icon('minecraft:glass_pane'), 'Table Pour', 'Pour some glass onto a table', 'brass_plated_block', generic('tfc:table_pour', None))
    story.advancement('blowpipe', icon('tfc:ceramic_blowpipe'), 'Blowpipe', 'Make a blowpipe', 'sheet', inventory_changed('#tfc:blowpipes'))
    story.advancement('glassworking_tools', icon('tfc:ceramic_blowpipe'), 'Glassworking Tools', 'Make a paddle, jacks, a wool cloth, and a gem saw', 'blowpipe', multiple(*[inventory_changed('tfc:%s' % m, name=m) for m in ('paddle', 'jacks', 'wool_cloth', 'gem_saw')]), requirements=[[m] for m in ('paddle', 'jacks', 'wool_cloth', 'gem_saw')], frame='challenge')
    story.advancement('glass_bottle', icon('tfc:silica_glass_bottle'), 'Glass Bottle', 'Make a glass bottle', 'blowpipe', inventory_changed('#tfc:glass_bottles'))
    story.advancement('preserves', icon('tfc:jar/blueberry'), 'Preserves', 'Make fruit preserves', 'blowpipe', inventory_changed('#tfc:foods/preserves'))
    story.advancement('lens', icon('tfc:lens'), 'Optician', 'Use a lens to craft a daylight detector, spyglass, and a compass', 'blowpipe', multiple(*[inventory_changed('minecraft:compass'), inventory_changed('minecraft:daylight_detector'), inventory_changed('minecraft:spyglass')]), requirements=[['compass'], ['daylight_detector'], ['spyglass']])
    # Misc advancements
    story.advancement('get_unknown', icon('tfc:metal/ingot/unknown'), 'Rite of Passage', 'You made useless unknown metal', 'pit_kiln', inventory_changed('tfc:metal/ingot/unknown'), hidden=True)
    story.advancement('flint_and_steel', icon('minecraft:flint_and_steel'), 'Sea of Flame', 'Craft a Flint and Steel', 'steel_age', inventory_changed('minecraft:flint_and_steel'))
    story.advancement('iron_armor', icon('tfc:metal/chestplate/wrought_iron'), 'Knight in Shining Armor', 'Create a full set of wrought iron armor, a sword, and a shield', 'iron_age', multiple(inventory_changed('tfc:metal/sword/wrought_iron'), inventory_changed('tfc:metal/shield/wrought_iron'), *[inventory_changed('tfc:metal/%s/wrought_iron' % piece) for piece in TFC_ARMOR_SECTIONS]), requirements=[['metal/%s/wrought_iron' % item] for item in ('chestplate', 'helmet', 'greaves', 'boots', 'sword', 'shield')])
    story.advancement('cast_iron', icon('tfc:metal/ingot/cast_iron'), 'I Can\'t Believe it\'s not Wrought!', 'Make a cast iron ingot', 'pit_kiln', inventory_changed('tfc:metal/ingot/cast_iron'))
    story.advancement('the_future', icon('minecraft:clock'), 'See the Future', 'You\'ve been around a while. Are there computers? Are there cars? Oh, you still farm your own crops? Weird.', 'root', generic('tfc:present_day', None), hidden=True)
    story.advancement('perfectly_forged', icon('tfc:metal/hammer/red_steel'), 'Perfectly Forged', 'Gain the Perfectly Forged bonus on an item', 'metal_anvil', generic('tfc:perfectly_forged', None), frame='challenge')

    world = AdvancementCategory(rm, 'world', 'tfc:textures/block/mud/silt.png')
    world.advancement('root', icon('tfc:plant/morning_glory'), 'TerraFirmaCraft World', 'Exploring the world of TFC', None, root_trigger(), chat=False)
    world.advancement('seeds', icon('tfc:seeds/tomato'), 'Gatherer', 'Get seeds from a wild crop', 'root', inventory_changed('#tfc:seeds'))
    world.advancement('eat_rotten_food', icon('minecraft:rotten_flesh'), 'Desperation', 'Eat something rotten', 'seeds', generic('tfc:eat_rotten_food', None), hidden=True)
    world.advancement('all_crops', icon('tfc:metal/hoe/black_steel'), 'True Farmer', 'Gather every seed in TFC', 'seeds', multiple(*[inventory_changed('tfc:seeds/%s' % c, name=c) for c in CROPS]), requirements=[[c] for c in CROPS])
    world.advancement('bread', icon('tfc:food/rye_bread'), 'Baker', 'Make a loaf of bread', 'seeds', inventory_changed('#tfc:sandwich_bread'))
    world.advancement('full_nutrition', icon('tfc:food/rye_bread_sandwich'), 'Doctors Hate Him', 'Get your nutrition to max', 'seeds', generic('tfc:full_nutrition', None), frame='challenge')
    world.advancement('wattle', icon('tfc:wattle'), 'Wattle and Daub', 'Craft some wattle', 'root', inventory_changed('tfc:wattle'))
    world.advancement('mud_bricks', icon('tfc:mud_brick/sandy_loam'), 'Playing in the Mud', 'Dry out some mud to make bricks', 'root', inventory_changed('#tfc:mud_bricks'))
    world.advancement('lava_lamp', icon('tfc:metal/lamp/blue_steel'), 'Lava Lamp', 'Light a lamp that burns forever', 'root', generic('tfc:lava_lamp', None))
    world.advancement('pan', icon('tfc:pan/empty'), 'Gold Rush', 'Craft a pan for sifting', 'root', inventory_changed('tfc:pan/empty'))
    world.advancement('spindle', icon('tfc:spindle'), 'Spindly', 'Craft a spindle', 'loom', inventory_changed('tfc:spindle'))
    world.advancement('loom', icon('tfc:wood/planks/pine_loom'), 'Weaver', 'Craft a loom for weaving', 'root', inventory_changed('#tfc:looms'))
    world.advancement('bed', icon('minecraft:red_bed'), 'A Good Night\'s Rest', 'Craft a bed', 'spindle', inventory_changed('#minecraft:beds'))
    world.advancement('volcano', icon('tfc:rock/magma/basalt'), 'Pacific Rim', 'Find an area with high volcanic activity', 'root', multiple(*[biome(b) for b in TFC_BIOMES if 'volcanic' in b]))
    world.advancement('coral_reef', icon('tfc:coral/brain_coral_fan'), 'What a rel-Reef!', 'Find a coral reef', 'volcano', biome('ocean_reef'))
    world.advancement('trench', icon('tfc:rock/magma/diorite'), 'In the Trenches', 'Find a deep ocean trench', 'volcano', biome('deep_ocean_trench'))
    world.advancement('adventuring_time', icon('tfc:metal/boots/red_steel'), 'Adventuring Time', 'Discover every biome in TFC', 'volcano', multiple(*[biome(b) for b in TFC_BIOMES]), requirements=[[b] for b in TFC_BIOMES], frame='challenge')
    world.advancement('globe_trotter', icon('minecraft:map'), 'Globe Trotter', 'Travel to positive 10,000 and -10,000 z, the hottest and coldest points nearest to spawn', 'root', multiple(location({'position': {'z': {'min': 10000}}}, name='high'), location({'position': {'z': {'max': -10000}}}, name='low')), requirements=[['high'], ['low']], frame='challenge')
    world.advancement('dune', icon('tfc:plant/barrel_cactus'), 'Dune', 'Travel to a desert and find a barrel cactus', 'seeds', inventory_changed('tfc:plant/barrel_cactus'))
    world.advancement('fruit', icon('tfc:food/orange'), 'Healthy Diet', 'Eat every berry and tree fruit in TFC', 'root', multiple(*[consume_item('tfc:food/%s' % f, name=f) for f in (*BERRIES, *FRUITS)]), requirements=[[f] for f in (*BERRIES, *FRUITS)], frame='challenge')
    world.advancement('saplings', icon('tfc:wood/sapling/pine'), 'Arborist', 'Find every (non-fruit) tree sapling in TFC', 'root', multiple(*[inventory_changed('tfc:wood/sapling/%s' % t, name=t) for t in WOODS.keys()]), requirements=[[t] for t in WOODS.keys()])
    world.advancement('nugget', icon('tfc:ore/small_native_copper'), 'A Weird Rock', 'Find a metal nugget on the ground', 'root', inventory_changed('#tfc:nuggets'))
    world.advancement('coal', icon('tfc:ore/lignite'), 'Carboniferous', 'Find Bituminous Coal or Lignite', 'nugget', multiple(inventory_changed('tfc:ore/lignite'), inventory_changed('tfc:ore/bituminous_coal')))
    world.advancement('diamond', icon('tfc:ore/diamond'), 'DIAM- oh, wait', 'Find Diamonds (Kimberlite)', 'nugget', inventory_changed('tfc:ore/diamond'))
    world.advancement('graphite', icon('tfc:ore/graphite'), 'Better than Diamonds', 'Find Graphite', 'nugget', inventory_changed('tfc:ore/graphite'), frame='goal')
    world.advancement('kaolinite', icon('tfc:kaolin_clay'), 'Pink Unicorn', 'Find Kaolin Clay', 'nugget', inventory_changed('tfc:kaolin_clay'), frame='goal')
    world.advancement('sylvite', icon('tfc:ore/sylvite'), 'Plant Food', 'Find Sylvite', 'nugget', inventory_changed('tfc:ore/sylvite'))
    world.advancement('nickel', icon('tfc:ore/small_garnierite'), 'Nickels and Dimes', 'Find Garnierite', 'nugget', inventory_changed('tfc:ore/small_garnierite'), frame='goal')
    world.advancement('iron', icon('tfc:ore/small_hematite'), 'Pretty Ironic', 'Find Hematite, Limonite, and Magnetite nuggets', 'nugget', multiple(*[inventory_changed('tfc:ore/small_%s' % o, name=o) for o in ('hematite', 'limonite', 'magnetite')]), requirements=[[o] for o in ('hematite', 'limonite', 'magnetite')])
    world.advancement('compost', icon('tfc:compost'), 'Reduce Reuse Recycle', 'Make compost in a composter', 'root', inventory_changed('tfc:compost'))
    world.advancement('rotten_compost', icon('tfc:rotten_compost'), 'Wasteful', 'Kill a plant with rotten compost', 'compost', generic('tfc:rotten_compost_kill', None))
    world.advancement('guano', icon('tfc:groundcover/guano'), 'Gift from the Birds', 'Find guano', 'root', inventory_changed('tfc:groundcover/guano'))
    world.advancement('full_fertilizer', icon('tfc:pure_nitrogen'), 'Fully Fertile', 'Raise a block of farmland to 10/10/10 nutrient levels', 'seeds', generic('tfc:full_fertilizer', None))
    world.advancement('hunter', icon('tfc:food/chevon'), 'Hunter', 'Kill an animal', 'root', kill_mob('#tfc:animals'))
    world.advancement('glow_hunter', icon('minecraft:glow_ink_sac'), 'Mystery of the Depths', 'Kill the Octopoteuthis', 'hunter', kill_mob('tfc:octopoteuthis'), hidden=True, frame='goal')
    world.advancement('bear_hunter', icon('tfc:large_raw_hide'), 'Bear Attack', 'Kill a Bear', 'hunter', kill_mob('#tfc:bears'))
    world.advancement('fishing', icon('tfc:metal/fishing_rod/copper'), 'Fisherman', 'Hook a fish with a fishing rod', 'hunter', generic('tfc:hooked_entity', {'entity': {'type': '#tfc:small_fish'}}))
    world.advancement('advanced_fishing', icon('tfc:metal/fishing_rod/red_steel'), 'Master Fisherman', 'Hook a dolphin or an orca with a fishing rod', 'fishing', generic('tfc:hooked_entity', {'entity': {'type': '#tfc:needs_large_fishing_bait'}}), frame='goal')
    world.advancement('all_fish', icon('tfc:food/rainbow_trout'), 'Gone Fishing', 'Obtain every small fish, a shellfish, and some calamari', 'fishing', multiple(*[inventory_changed('tfc:food/%s' % f) for f in (*SIMPLE_FRESHWATER_FISH, 'cod', 'calamari', 'tropical_fish')]), frame='goal')
    world.advancement('greatest_hunter', icon('tfc:metal/javelin/red_steel'), 'Greatest Hunter', 'Hit a rabbit from 50m away with a javelin', 'hunter', generic('tfc:stab_entity', {'entity': {'type': 'tfc:rabbit', 'distance': {'horizontal': {'min': 50}}}}))
    world.advancement('artist', icon('minecraft:red_dye'), 'Artist', 'Procure all 16 colors of dye', 'root', multiple(*[inventory_changed('minecraft:%s_dye' % c, name=c) for c in COLORS]), requirements=[[c] for c in COLORS], frame='goal')
    world.advancement('familiarity', icon('tfc:food/wheat_grain'), 'A New Friend', 'Feed an animal some food to familiarize it', 'root', generic('tfc:fed_animal', {'entity': {'type': '#tfc:animals'}}))
    world.advancement('powderkeg', icon('tfc:powderkeg'), 'Big Boom', 'Light a powderkeg', 'root', generic('tfc:lit', {'block': 'tfc:powderkeg'}))
    world.advancement('full_powderkeg', icon('minecraft:gunpowder'), 'Pakratt', 'Light a fully loaded powderkeg', 'powderkeg', generic('tfc:full_powderkeg', None))
    world.advancement('gemologist', icon('tfc:gem/amethyst'), 'Gemologist', 'Find every gem ore in TFC', 'nugget', multiple(*[inventory_changed('tfc:ore/%s' % g, name=g) for g in GEMS]), requirements=[[g] for g in GEMS], frame='goal')
    world.advancement('minerologist', icon('tfc:ore/halite'), 'Minerologist', 'Find every non-metal mineral in TFC', 'gemologist', multiple(*[inventory_changed('tfc:ore/%s' % g, name=g) for g in ALL_MINERALS]), frame='goal', requirements=[[g] for g in ALL_MINERALS])
    world.advancement('metallurgist', icon('tfc:metal/ingot/gold'), 'Metallurgist', 'Obtain a metal-bearing specimen for every metal in TFC', 'nugget', multiple(*[inventory_changed('#tfc:metal_item/%s' % m, name=m) for m in METALS.keys()]), requirements=[[m] for m in METALS], frame='goal')

def kill_mob(mob: str, other: Dict = None) -> Json:
    return generic('minecraft:player_killed_entity', {'entity': [entity_predicate(mob, other)]})

# if the predicate is (and usually it will be) an EntityPredicate.Composite, this should be inside an array.
# EntityPredicate.Composite wraps a vanilla loot event in a trigger event
def entity_predicate(mob: str, other: Dict = None) -> Json:
    dic = {
        'condition': 'minecraft:entity_properties',
        'predicate': {'type': mob},  # can be a hashtag to refer to entity tags
        'entity': 'this',
    }
    if other is not None:
        dic.update(other)
    return dic


def consume_item(item: str, name: str = 'item_consumed') -> Json:
    if isinstance(item, str) and name == 'item_consumed':
        name = item.split(':')[1]
    return generic('minecraft:consume_item', {'item': utils.item_predicate(item)}, name=name)

def icon(name: str) -> Json:
    return {'item': name}

def biome(biome_name: str) -> Json:
    return location({'biome': 'tfc:%s' % biome_name}, biome_name)

def location(location_predicate: Json, name: str):
    return generic('minecraft:location', {
        'player': [
            {
                'condition': 'minecraft:entity_properties',
                'entity': 'this',
                'predicate': {
                    'location': location_predicate
                }
            }
        ]}, name=name)

def multiple(*conditions: Json) -> Json:
    merged = {}
    for c in conditions:
        merged.update(c)
    return merged

def generic(trigger_type: str, conditions: Json, name: str = 'special_condition') -> Json:
    return {name: {'trigger': trigger_type, 'conditions': conditions}}

def inventory_changed(item: str | Json, name: str = 'item_obtained') -> Json:
    if isinstance(item, str) and name == 'item_obtained':
        name = item.split(':')[1]
    return {name: advancements.inventory_changed(item)}

def item_use_on_block(block: str, item: str, name: str = 'item_use_on_block_condition'):
    block_json = {'tag': block[1:]} if block[0] == '#' else {'blocks': [block]}
    return {name: {'trigger': 'minecraft:item_used_on_block', 'conditions': {
        'location': {'block': block_json},
        'item': {'items': [item]}
    }}}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}


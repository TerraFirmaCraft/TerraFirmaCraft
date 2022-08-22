from mcresources import ResourceManager
from mcresources import advancements as adv
from mcresources.type_definitions import Json


def main(rm: ResourceManager):
    story = adv.AdvancementCategory(rm, 'story', 'tfc:textures/rock/mossy_cobble/diorite.png')

    story.advancement('root', icon('tfc:metal/hammer/wrought_iron'), 'TerraFirmaCraft Story', 'TFC\'s main progression line.', None, root_trigger(), chat=False)
    story.advancement('find_rock', icon('tfc:rock/loose/schist'), 'Just a Rock', 'Pick up a stone from the ground.', 'root', inventory_changed('#tfc:rock_knapping'))
    story.advancement('stone_age', icon('tfc:stone/axe/sedimentary'), 'Paleolithic!', 'Enter the Stone Age by making a stone tool.', 'find_rock', inventory_changed('#tfc:stone_tools'))
    story.advancement('get_straw', icon('tfc:straw'), 'Grasping at Straws', 'Cut some grass with a knife to get straw.', 'stone_age', inventory_changed('tfc:straw'))
    story.advancement('logging', icon('tfc:wood/log/oak'), 'Timberrr!', 'Use an axe to cut down a tree.', 'get_straw', inventory_changed('#minecraft:logs'))
    story.advancement('firestarter', icon('tfc:firestarter'), 'Embers', 'Craft a firestarter from two sticks.', 'logging', inventory_changed('tfc:firestarter'))
    story.advancement('firepit', icon('tfc:firepit'), 'Into Fire', 'Use the firestarter to light some logs and sticks.', 'firestarter', generic('tfc:firepit_created', {'block': 'tfc:firepit'}))
    story.advancement('pot', icon('tfc:pot'), 'Pot Head', 'Add a ceramic pot to the firepit.', 'firepit', generic('tfc:firepit_created', {'block': 'tfc:pot'}))
    story.advancement('grill', icon('tfc:grill'), 'Grilling Time!', 'Add a wrought iron grill to the firepit.', 'firepit', generic('tfc:firepit_created', {'block': 'tfc:grill'}))
    # Parented to firestarter
    story.advancement('charcoal', icon('minecraft:charcoal'), 'A Better Fuel', 'Get some charcoal from a charcoal pit.', 'firestarter', inventory_changed('minecraft:charcoal'))
    story.advancement('forge', icon('tfc:rock/cobble/andesite'), 'Forging', 'Light a charcoal forge.', 'charcoal', generic('tfc:lit', {'block': 'tfc:pit_kiln'}))
    story.advancement('stone_anvil', icon('tfc:stone/hammer/sedimentary'), 'Hammer Time', 'Create a stone anvil with a hammer.', 'forge', generic('tfc:rock_anvil', {'tag': 'tfc:rock_anvils'}))
    story.advancement('metal_anvil', icon('tfc:metal/anvil/copper'), 'Dropping the Anvil', 'Create a copper anvil out of double ingots.', 'stone_anvil', inventory_changed('tfc:metal/anvil/copper'))
    story.advancement('fire_clay', icon('tfc:fire_clay'), 'Fireproof', 'Craft some fire clay.', 'metal_anvil', inventory_changed('tfc:fire_clay'))
    story.advancement('crucible', icon('tfc:crucible'), 'The Crucible', 'Knap a crucible out of some fire clay and fire it.', 'fire_clay', inventory_changed('tfc:crucible'))
    # Parented to firestarter
    story.advancement('find_clay', icon('minecraft:clay_ball'), 'Locating Clay', 'Find and dig clay for pottery.', 'firestarter', inventory_changed('minecraft:clay_ball'))
    story.advancement('knap_clay', icon('tfc:ceramic/unfired_vessel'), 'Clay Forming', 'Knap clay into a new shape.', 'find_clay', inventory_changed('#tfc:unfired_pottery'))
    story.advancement('pit_kiln', icon('tfc:ceramic/vessel'), 'Potter', 'Light a pit kiln on fire.', 'knap_clay', generic('tfc:lit', {'block': 'tfc:pit_kiln'}))
    story.advancement('mold', icon('tfc:ceramic/axe_head_mold'), 'Pouring Metal', 'Fire a mold for making a metal tool head.', 'pit_kiln', inventory_changed('#tfc:fired_molds'))
    story.advancement('copper_age', icon('tfc:metal/axe_head/copper'), 'The Copper Age', 'Enter the Copper Age by smithing a copper tool', 'mold', inventory_changed({'type': 'tfc:metal_tool', 'metal': 'tfc:copper'}), frame='challenge')
    story.advancement('bronze_age', icon('tfc:metal/sword/bronze'), 'The Bronze Age', 'Enter the Bronze Age by smithing a bronze item.', 'copper_age', inventory_changed({'type': 'tfc:metal_item', 'tier': 2}))
    story.advancement('bloomery', icon('tfc:bloomery'), 'Ironworks', 'Craft a bloomery.', 'bronze_age', inventory_changed('tfc:bloomery'))
    story.advancement('iron_bloom', icon('tfc:raw_iron_bloom'), 'In Bloom', 'Create an iron bloom.', 'bloomery', inventory_changed('tfc:raw_iron_bloom'))
    story.advancement('iron_age', icon('tfc:metal/ingot/wrought_iron'), 'The Iron Age', 'Refine a bloom into wrought iron ingot.', 'iron_bloom', inventory_changed('tfc:metal/ingot/wrought_iron'), frame='challenge')
    story.advancement('blast_furnace', icon('tfc:blast_furnace'), 'Blast off!', 'Craft a blast furnace.', 'iron_age', inventory_changed('tfc:blast_furnace'))
    story.advancement('steel_age', icon('tfc:metal/ingot/steel'), 'Industrialized', 'Make your first steel item.', 'blast_furnace', inventory_changed({'type': 'tfc:metal_item', 'metal': 'tfc:steel'}), frame='challenge')
    story.advancement('black_steel', icon('tfc:metal/ingot/black_steel'), 'Back in Black', 'Make your first black steel item.', 'steel_age', inventory_changed({'type': 'tfc:metal_item', 'metal': 'tfc:black_steel'}), frame='challenge')
    story.advancement('blue_steel', icon('tfc:metal/ingot/blue_steel'), 'Feeling Blue', 'Make your first blue steel item.', 'black_steel', inventory_changed({'type': 'tfc:metal_item', 'metal': 'tfc:blue_steel'}), frame='challenge')
    story.advancement('red_steel', icon('tfc:metal/ingot/blue_steel'), 'Seeing Red', 'Make your first red steel item.', 'black_steel', inventory_changed({'type': 'tfc:metal_item', 'metal': 'tfc:red_steel'}), frame='challenge')
    story.advancement('red_steel_bucket', icon('tfc:metal/bucket/red_steel'), 'Tsunami', 'Make a red steel bucket.', 'red_steel', inventory_changed('tfc:metal/bucket/red_steel'))
    story.advancement('blue_steel_bucket', icon('tfc:metal/bucket/blue_steel'), 'Hot Stuff', 'Make a blue steel bucket.', 'blue_steel', inventory_changed('tfc:metal/bucket/blue_steel'))
    story.advancement('ultimate_goal', icon('minecraft:bucket'), 'All This for a Bucket!?', 'Combine your red and blue steel buckets into a vanilla bucket.', 'blue_steel_bucket', inventory_changed('minecraft:bucket'), frame='goal')
    # Tool advancements parented to copper age
    story.advancement('pickaxe', icon('tfc:metal/pickaxe/copper'), 'Time to Mine (Finally!)', 'Make your first metal pickaxe.', 'copper_age', inventory_changed('#tfc:pickaxes'))
    story.advancement('saw', icon('tfc:metal/saw/copper'), 'Carpenter', 'Make a metal saw.', 'copper_age', inventory_changed('#tfc:saws'))
    story.advancement('chisel', icon('tfc:metal/chisel/copper'), 'Sculptor', 'Make a metal chisel.', 'copper_age', inventory_changed('#tfc:chisels'))
    story.advancement('propick', icon('tfc:metal/propick/copper'), 'Prospector', 'Make a prospector\'s pickaxe.', 'copper_age', inventory_changed('#tfc:propicks'))
    # Parented to chisel
    story.advancement('smooth_stone', icon('tfc:rock/smooth/chert'), 'Super Smooth', 'Chisel a piece of raw stone into a smooth stone block.', 'chisel', generic('tfc:chiseled', {'tag': 'forge:smooth_stone'}))
    story.advancement('raw_stone', icon('tfc:rock/hardened/basalt'), 'Raw Emotions', 'Isolate a piece of raw rock to make it pop off.', 'smooth_stone', inventory_changed('#forge:stone'))
    story.advancement('quern', icon('tfc:quern'), 'The Grind', 'Craft a quern and a handstone.', 'raw_stone', multiple(inventory_changed('tfc:handstone'), inventory_changed('tfc:quern')))
    story.advancement('flux', icon('tfc:powder/flux'), 'In Flux', 'Grind some flux using a quern.', 'quern', inventory_changed('#tfc:flux'), frame='challenge')
    story.advancement('welding', icon('tfc:metal/double_ingot/copper'), 'Double Trouble', 'Weld a double ingot in an anvil.', 'flux', inventory_changed('#forge:double_ingots'))
    story.advancement('sheet', icon('tfc:metal/sheet/copper'), 'The Flattening', 'Pound a double ingot into a sheet.', 'welding', inventory_changed('#forge:sheets'))
    story.advancement('shield', icon('tfc:metal/shield/copper'), 'Take Cover!', 'Craft a shield from a double sheet.', 'sheet', inventory_changed('#tfc:shields'))
    # Parented to saw
    story.advancement('barrel', icon('tfc:wood/barrel/acacia'), 'Do a Barrel Roll!', 'Craft a barrel using a saw and lumber.', 'saw', inventory_changed('#tfc:barrels'))
    story.advancement('scraped_hide', icon('tfc:large_scraped_hide'), 'Soak and Scrape', 'Soak a hide in limewater and then scrape it.', 'barrel', inventory_changed('#tfc:scraped_hides'))
    story.advancement('leather', icon('minecraft:leather'), 'Genuine Leather', 'Finish preparing the hide by soaking it in watter then tannin.', 'scraped_hide', inventory_changed('minecraft:leather'))
    story.advancement('bellows', icon('tfc:bellows'), 'Blow You Away', 'Make a bellows out of leather.', 'leather', inventory_changed('tfc:bellows'))
    # Misc advancements
    story.advancement('get_unknown', icon('tfc:metal/ingot/unknown'), 'Rite of Passage', 'You made useless unknown metal.', 'pit_kiln', inventory_changed('tfc:metal/ingot/unknown'), hidden=True)
    story.advancement('flint_and_steel', icon('minecraft:flint_and_steel'), 'Sea of Flame', 'Craft a Flint and Steel', 'steel_age', inventory_changed('minecraft:flint_and_steel'))
    story.advancement('iron_armor', icon('tfc:metal/chestplate/wrought_iron'), 'Knight in Shining Armor', 'Create a full set of wrought iron armor, a sword, and a shield.', 'iron_age', multiple(inventory_changed('tfc:metal/sword/wrought_iron'), inventory_changed('tfc:metal/shield/wrought_iron'), *[inventory_changed('tfc:metal/%s/wrought_iron' % piece) for piece in ('helmet', 'chestplate', 'greaves', 'boots')]))
    story.advancement('cast_iron', icon('tfc:metal/ingot/cast_iron'), 'I Can\'t Believe it\'s not Wrought!', 'Make a cast iron ingot.', 'pit_kiln', inventory_changed('tfc:metal/ingot/cast_iron'))

    world = adv.AdvancementCategory(rm, 'world', 'tfc:textures/item/mud/silt.png')
    world.advancement('root', icon('tfc:plant/morning_glory'), 'TerraFirmaCraft World', 'Exploring the world of TFC.', None, root_trigger(), chat=False)
    world.advancement('seeds', icon('tfc:seeds/tomato'), 'Gatherer', 'Get seeds from a wild crop.', 'root', inventory_changed('#tfc:seeds'))
    world.advancement('bread', icon('tfc:food/rye_bread'), 'Baker', 'Make a loaf of bread.', 'seeds', inventory_changed('#tfc:sandwich_bread'))
    world.advancement('wattle', icon('tfc:wattle'), 'Wattle and Daub', 'Craft some wattle.', 'root', inventory_changed('tfc:wattle'))
    world.advancement('mud_bricks', icon('tfc:mud_brick/sandy_loam'), 'Playing in the Mud', 'Dry out some mud to make bricks.', 'root', inventory_changed('#tfc:mud_bricks'))
    world.advancement('pan', icon('tfc:pan/empty'), 'Gold Rush', 'Make a pan for sifting.', 'root', inventory_changed('tfc:pan/empty'))

def icon(name: str) -> Json:
    return {'item': name}

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
    return {name: adv.inventory_changed(item)}

def item_use_on_block(block: str, item: str, name: str = 'item_use_on_block_condition'):
    block_json = {'tag': block[1:]} if block[0] == '#' else {'blocks': [block]}
    return {name: {'trigger': 'minecraft:item_used_on_block', 'conditions': {
        'location': {'block': block_json},
        'item': {'items': [item]}
    }}}

def placed_block(block: str, name: str = 'block_placed_condition') -> Json:
    return {name: {'trigger': 'minecraft:placed_block', 'conditions': {'block': block}}}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}


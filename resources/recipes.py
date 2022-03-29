#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager, RecipeContext, utils
from constants import *


def generate(rm: ResourceManager):
    # Rock Things
    for rock in ROCKS.keys():

        cobble = 'tfc:rock/cobble/%s' % rock
        raw = 'tfc:rock/raw/%s' % rock
        loose = 'tfc:rock/loose/%s' % rock
        hardened = 'tfc:rock/hardened/%s' % rock
        bricks = 'tfc:rock/bricks/%s' % rock
        smooth = 'tfc:rock/smooth/%s' % rock
        cracked_bricks = 'tfc:rock/cracked_bricks/%s' % rock
        chiseled = 'tfc:rock/chiseled/%s' % rock

        brick = 'tfc:brick/%s' % rock

        # Cobble <-> Loose Rocks
        rm.crafting_shapeless('crafting/rock/%s_cobble_to_loose_rocks' % rock, cobble, (4, loose)).with_advancement(cobble)
        rm.crafting_shaped('crafting/rock/%s_loose_rocks_to_cobble' % rock, ['XX', 'XX'], loose, cobble).with_advancement(loose)

        # Stairs, Slabs and Walls
        for block_type in CUTTABLE_ROCKS:
            block = 'tfc:rock/%s/%s' % (block_type, rock)

            rm.crafting_shaped('crafting/rock/%s_%s_slab' % (rock, block_type), ['XXX'], block, (6, block + '_slab')).with_advancement(block)
            rm.crafting_shaped('crafting/rock/%s_%s_stairs' % (rock, block_type), ['X  ', 'XX ', 'XXX'], block, (6, block + '_stairs')).with_advancement(block)
            rm.crafting_shaped('crafting/rock/%s_%s_wall' % (rock, block_type), ['XXX', 'XXX'], block, (6, block + '_wall')).with_advancement(block)

            # Vanilla allows stone cutting from any -> any, we only allow stairs/slabs/walls as other variants require mortar / chisel
            stone_cutting(rm, 'rock/%s_%s_slab' % (rock, block_type), block, block + '_slab', 2).with_advancement(block)
            stone_cutting(rm, 'rock/%s_%s_stairs' % (rock, block_type), block, block + '_stairs', 1).with_advancement(block)
            stone_cutting(rm, 'rock/%s_%s_wall' % (rock, block_type), block, block + '_wall', 1).with_advancement(block)

        # Other variants
        damage_shapeless(rm, 'crafting/rock/%s_smooth' % rock, (raw, '#tfc:chisels'), smooth).with_advancement(raw)
        damage_shapeless(rm, 'crafting/rock/%s_brick' % rock, (loose, '#tfc:chisels'), brick).with_advancement(loose)
        damage_shapeless(rm, 'crafting/rock/%s_chiseled' % rock, (smooth, '#tfc:chisels'), chiseled).with_advancement(smooth)
        damage_shapeless(rm, 'crafting/rock/%s_button' % rock, ('#tfc:chisels', brick), 'tfc:rock/button/%s' % rock).with_advancement(brick)
        damage_shapeless(rm, 'crafting/rock/%s_pressure_plate' % rock, ('#tfc:chisels', brick, brick), 'tfc:rock/pressure_plate/%s' % rock).with_advancement(brick)

        rm.crafting_shaped('crafting/rock/%s_hardened' % rock, ['XMX', 'MXM', 'XMX'], {'X': raw, 'M': '#tfc:mortar'}, (2, hardened)).with_advancement(raw)
        rm.crafting_shaped('crafting/rock/%s_bricks' % rock, ['XMX', 'MXM', 'XMX'], {'X': brick, 'M': '#tfc:mortar'}, (4, bricks)).with_advancement(brick)

        damage_shapeless(rm, 'crafting/rock/%s_cracked' % rock, (bricks, '#tfc:hammers'), cracked_bricks).with_advancement(bricks)

    for metal, metal_data in METALS.items():
        if 'utility' in metal_data.types:
            rm.crafting_shaped('crafting/metal/anvil/%s' % metal, ['XXX', ' X ', 'XXX'], {'X': 'tfc:metal/double_ingot/%s' % metal}, 'tfc:metal/anvil/%s' % metal).with_advancement('tfc:metal/double_ingot/%s' % metal)
        if 'tool' in metal_data.types:
            for tool in METAL_TOOL_HEADS:
                suffix = '_blade' if tool in ('knife', 'saw', 'scythe', 'sword') else '_head'
                rm.crafting_shaped('crafting/metal/%s/%s' % (tool, metal), ['X', 'Y'], {'X': 'tfc:metal/%s%s/%s' % (tool, suffix, metal), 'Y': '#forge:rods/wooden'}, 'tfc:metal/%s/%s' % (tool, metal)).with_advancement('tfc:metal/%s%s/%s' % (tool, suffix, metal))

    rm.crafting_shaped('crafting/wood/stick_from_twigs', ['X', 'X'], {'X': '#tfc:twigs'}, 'minecraft:stick')  # todo: advancement?

    for wood in WOODS.keys():
        def item(thing: str):
            return 'tfc:wood/%s/%s' % (thing, wood)

        def plank(thing: str):
            return 'tfc:wood/planks/%s_%s' % (wood, thing)

        rm.crafting_shaped('crafting/wood/%s_twig' % wood, ['X', 'X'], {'X': item('twig')}, 'minecraft:stick').with_advancement(item('twig'))
        rm.crafting_shaped('crafting/wood/%s_bookshelf' % wood, ['XXX', 'YYY', 'XXX'], {'X': item('planks'), 'Y': 'minecraft:book'}, plank('bookshelf')).with_advancement('minecraft:book')
        rm.crafting_shapeless('crafting/wood/%s_button' % wood, item('planks'), plank('button')).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_door' % wood, ['XX', 'XX', 'XX'], {'X': item('lumber')}, (2, plank('door'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_fence' % wood, ['XYX', 'XYX'], {'X': item('planks'), 'Y': item('lumber')}, (8, plank('fence'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_log_fence' % wood, ['XYX', 'XYX'], {'X': item('log'), 'Y': item('lumber')}, (8, plank('log_fence'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_fence_gate' % wood, ['YXY', 'YXY'], {'X': item('planks'), 'Y': item('lumber')}, (2, plank('fence_gate'))).with_advancement(item('lumber'))
        damage_shapeless(rm, 'crafting/wood/%s_lumber_log' % wood, (item('log'), '#tfc:saws'), (8, item('lumber'))).with_advancement(item('log'))
        damage_shapeless(rm, 'crafting/wood/%s_lumber_planks' % wood, (item('planks'), '#tfc:saws'), (4, item('lumber'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_stairs' % wood, ['X  ', 'XX ', 'XXX'], {'X': item('planks')}, (8, plank('stairs'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_slab' % wood, ['XXX'], {'X': item('planks')}, (6, plank('slab'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_planks' % wood, ['XX', 'XX'], {'X': item('lumber')}, item('planks')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_tool_rack' % wood, ['XXX', '   ', 'XXX'], {'X': item('lumber')}, plank('tool_rack')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_trapdoor' % wood, ['XXX', 'XXX'], {'X': item('lumber')}, (3, plank('trapdoor'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_workbench' % wood, ['XX', 'XX'], {'X': item('planks')}, plank('workbench')).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_pressure_plate' % wood, ['XX'], {'X': item('lumber')}, plank('pressure_plate')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_boat' % wood, ['X X', 'XXX'], {'X': item('planks')}, item('boat')).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_chest' % wood, ['XXX', 'X X', 'XXX'], {'X': item('lumber')}, item('chest')).with_advancement(item('lumber'))
        rm.crafting_shapeless('crafting/wood/%s_trapped_chest' % wood, (item('chest'), 'minecraft:tripwire_hook'), (1, item('trapped_chest'))).with_advancement(item('chest'))
        damage_shapeless(rm, 'crafting/wood/%s_support' % wood, (item('log'), item('log'), '#tfc:saws'), (8, item('support'))).with_advancement('#tfc:saws')
        rm.crafting_shaped('crafting/wood/%s_loom' % wood, ['XXX', 'XSX', 'X X'], {'X': item('lumber'), 'S': 'minecraft:stick'}, plank('loom')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_sluice' % wood, ['  X', ' XY', 'XYY'], {'X': '#forge:rods/wooden', 'Y': item('lumber')}, item('sluice')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_sign' % wood, ['XXX', 'XXX', ' Y '], {'X': item('lumber'), 'Y': '#forge:rods/wooden'}, (3, item('sign'))).with_advancement(item('lumber'))

    rm.crafting_shaped('crafting/aggregate', ['XYX', 'Y Y', 'XYX'], {'X': '#forge:sand', 'Y': '#forge:gravel'}, (8, 'tfc:aggregate')).with_advancement('#forge:sand')
    damage_shapeless(rm, 'crafting/alabaster_brick', ('tfc:ore/gypsum', '#tfc:chisels'), (4, 'tfc:alabaster_brick')).with_advancement('tfc:ore/gypsum')
    rm.crafting_shaped('crafting/alabaster_bricks', ['XYX', 'YXY', 'XYX'], {'X': 'tfc:alabaster_brick', 'Y': '#tfc:mortar'}, (4, 'tfc:alabaster/raw/alabaster_bricks')).with_advancement('tfc:alabaster_brick')
    rm.crafting_shaped('crafting/bellows', ['XXX', 'YYY', 'XXX'], {'X': '#tfc:lumber', 'Y': '#forge:leather'}, 'tfc:bellows').with_advancement('#forge:leather')
    rm.crafting_shaped('crafting/bricks', ['XYX', 'YXY', 'XYX'], {'X': 'minecraft:brick', 'Y': '#tfc:mortar'}, (2, 'minecraft:bricks')).with_advancement('minecraft:brick')
    rm.crafting_shaped('crafting/fire_bricks', ['XYX', 'YXY', 'XYX'], {'X': 'tfc:ceramic/fire_brick', 'Y': '#tfc:mortar'}, (2, 'tfc:fire_bricks')).with_advancement('minecraft:brick')
    rm.crafting_shaped('crafting/fire_clay', ['XYX', 'YZY', 'XYX'], {'X': 'tfc:powder/kaolinite', 'Y': 'tfc:powder/graphite', 'Z': 'minecraft:clay_ball'}, 'tfc:fire_clay').with_advancement('tfc:powder/kaolinite')
    rm.crafting_shaped('crafting/fire_clay_block', ['XX', 'XX'], {'X': 'tfc:fire_clay'}, 'tfc:fire_clay_block').with_advancement('tfc:fire_clay')
    rm.crafting_shaped('crafting/firestarter', [' X', 'X '], {'X': '#forge:rods/wooden'}, 'tfc:firestarter').with_advancement('#forge:rods/wooden')
    damage_shapeless(rm, 'crafting/flux', ('#tfc:fluxstone', '#tfc:hammers'), (2, 'tfc:powder/flux')).with_advancement('#tfc:fluxstone')
    rm.crafting_shapeless('crafting/gunpowder', ('tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/sulfur', 'tfc:powder/charcoal'), (4, 'minecraft:gunpowder')).with_advancement('tfc:powder/sulfur')
    rm.crafting_shapeless('crafting/gunpowder_graphite', ('tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/sulfur', 'tfc:powder/sulfur', 'tfc:powder/charcoal', 'tfc:powder/charcoal', 'tfc:powder/graphite'), (12, 'minecraft:gunpowder')).with_advancement('tfc:powder/graphite')
    rm.crafting_shaped('crafting/halter', ['XYX', 'X X'], {'X': '#forge:leather', 'Y': 'minecraft:lead'}, 'tfc:halter').with_advancement('minecraft:lead')
    rm.crafting_shaped('crafting/handstone', ['Y  ', 'XXX'], {'X': '#forge:stone', 'Y': '#forge:rods/wooden'}, 'tfc:handstone').with_advancement('#forge:stone')
    rm.crafting_shaped('crafting/jute_net', ['X X', ' X ', 'X X'], {'X': 'tfc:jute_fiber'}, 'tfc:jute_net').with_advancement('tfc:jute_fiber')
    rm.crafting_shaped('crafting/lead', [' XX', ' XX', 'X  '], {'X': 'tfc:jute_fiber'}, 'minecraft:lead').with_advancement('tfc:jute_fiber')
    rm.crafting_shaped('crafting/quern', ['XXX', 'YYY'], {'X': '#forge:smooth_stone', 'Y': '#forge:stone'}, 'tfc:quern').with_advancement('#forge:smooth_stone')
    rm.crafting_shaped('crafting/spindle', ['X', 'Y'], {'X': 'tfc:ceramic/spindle_head', 'Y': '#forge:rods/wooden'}, 'tfc:spindle').with_advancement('tfc:ceramic/spindle_head')
    rm.crafting_shapeless('crafting/stick_from_bunch', 'tfc:stick_bunch', (9, 'minecraft:stick')).with_advancement('tfc:stick_bunch')
    rm.crafting_shapeless('crafting/stick_from_bundle', 'tfc:stick_bundle', (18, 'minecraft:stick')).with_advancement('tfc:stick_bundle')
    rm.crafting_shaped('crafting/stick_bunch', ['XXX', 'XXX', 'XXX'], {'X': '#forge:rods/wooden'}, 'tfc:stick_bunch').with_advancement('#forge:rods/wooden')
    rm.crafting_shaped('crafting/stick_bundle', ['X', 'X'], {'X': 'tfc:stick_bunch'}, 'tfc:stick_bundle').with_advancement('tfc:stick_bunch')
    rm.crafting_shapeless('crafting/straw', 'tfc:thatch', (4, 'tfc:straw')).with_advancement('tfc:thatch')
    rm.crafting_shaped('crafting/thatch', ['XX', 'XX'], {'X': 'tfc:straw'}, 'tfc:thatch').with_advancement('tfc:straw')
    damage_shapeless(rm, 'crafting/wool_yarn', ('tfc:spindle', 'tfc:wool'), (8, 'tfc:wool_yarn')).with_advancement('tfc:wool')
    rm.crafting_shaped('crafting/wattle', ['X', 'X'], {'X': '#minecraft:logs'}, (6, 'tfc:wattle')).with_advancement('#minecraft:logs')
    rm.crafting_shapeless('crafting/daub', ('tfc:straw', 'tfc:straw', 'tfc:straw', 'minecraft:clay_ball', 'minecraft:clay_ball', 'minecraft:clay_ball', '#minecraft:dirt', '#minecraft:dirt', '#minecraft:dirt'), (6, 'tfc:daub'))
    rm.crafting_shaped('crafting/composter', ['X X', 'XYX', 'XYX'], {'X': '#tfc:lumber', 'Y': '#minecraft:dirt'}, 'tfc:composter').with_advancement('#tfc:lumber')
    rm.crafting_shaped('crafting/glow_arrow', ['XXX', 'XYX', 'XXX'], {'X': 'minecraft:arrow', 'Y': 'minecraft:glow_ink_sac'}, (8, 'tfc:glow_arrow')).with_advancement('minecraft:glow_ink_sac')
    rm.crafting_shaped('crafting/wooden_bucket', ['X X', ' X '], {'X': '#tfc:lumber'}, 'tfc:wooden_bucket').with_advancement('#tfc:lumber')
    damage_shapeless(rm, 'crafting/paper', ('tfc:food/sugarcane', 'tfc:food/sugarcane', 'tfc:food/sugarcane', '#tfc:knives'), (3, 'minecraft:paper')).with_advancement('tfc:food/sugarcane')
    # todo: bf, bloomery, nestbox, pkeg, salting, food combining

    rm.crafting_shaped('crafting/vanilla/armor_stand', ['XXX', ' X ', 'XYX'], {'X': '#minecraft:planks', 'Y': '#forge:smooth_stone_slab'}, 'minecraft:armor_stand').with_advancement('#forge:smooth_stone_slab')
    rm.crafting_shaped('crafting/vanilla/armor_stand_bulk', ['X', 'Y'], {'X': 'tfc:stick_bunch', 'Y': '#forge:smooth_stone_slab'}, 'minecraft:armor_stand').with_advancement('#forge:smooth_stone_slab')
    rm.crafting_shaped('crafting/vanilla/color/white_bed', ['XXX', 'YYY'], {'X': '#tfc:high_quality_cloth', 'Y': '#tfc:lumber'}, 'minecraft:white_bed').with_advancement('#tfc:high_quality_cloth')
    rm.crafting_shaped('crafting/vanilla/bucket', ['XRX', 'XBX', ' X '], {'X': '#forge:ingots/wrought_iron', 'R': 'tfc:bucket/metal/red_steel', 'B': 'tfc:bucket/metal/blue_steel'}, 'minecraft:bucket').with_advancement('tfc:bucket/metal/red_steel')
    rm.crafting_shaped('crafting/vanilla/cauldron', ['X X', 'X X', 'XXX'], {'X': '#forge:sheets/wrought_iron'}, 'minecraft:cauldron').with_advancement('#forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/compass', [' X ', 'XYX', ' X '], {'X': '#forge:sheets/wrought_iron', 'Y': '#forge:dusts/redstone'}, 'minecraft:compass').with_advancement('#forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/crossbow', ['LIL', 'STS', ' L '], {'L': '#tfc:lumber', 'I': '#forge:rods/wrought_iron', 'S': '#forge:string', 'T': 'minecraft:tripwire_hook'}, 'minecraft:crossbow').with_advancement('#forge:ingots/wrought_iron')
    rm.crafting_shapeless('crafting/vanilla/fire_charge', ('minecraft:gunpowder', 'tfc:firestarter', '#minecraft:coals'), (3, 'minecraft:fire_charge')).with_advancement('minecraft:gunpowder')
    rm.crafting_shaped('crafting/vanilla/flint_and_steel', ['X ', ' Y'], {'X': '#forge:ingots/steel', 'Y': 'minecraft:flint'}, 'minecraft:flint_and_steel').with_advancement('#forge:ingots/steel')
    rm.crafting_shapeless('crafting/vanilla/hay', 'minecraft:hay_block', (9, 'tfc:straw')).with_advancement('minecraft:hay_block')
    rm.crafting_shaped('crafting/vanilla/hay_bale', ['XXX', 'XXX', 'XXX'], {'X': 'tfc:straw'}, 'minecraft:hay_block').with_advancement('tfc:straw')
    rm.crafting_shaped('crafting/vanilla/item_frame', ['XXX', 'XYX', 'XXX'], {'X': '#tfc:lumber', 'Y': '#forge:leather'}, (4, 'minecraft:item_frame')).with_advancement('#forge:leather')
    rm.crafting_shaped('crafting/vanilla/ladder', ['X X', 'X X', 'X X'], {'X': '#tfc:lumber'}, (16, 'minecraft:ladder')).with_advancement('#tfc:lumber')
    rm.crafting_shaped('crafting/vanilla/lapis_block', ['XXX', 'XXX', 'XXX'], {'X': 'tfc:gem/lapis_lazuli'}, 'minecraft:lapis_block').with_advancement('tfc:gem/lapis_lazuli')
    rm.crafting_shaped('crafting/vanilla/name_tag', ['XX', 'XY', 'XX'], {'X': 'minecraft:string', 'Y': 'minecraft:paper'}, 'minecraft:name_tag')
    rm.crafting_shaped('crafting/vanilla/painting', ['XXX', 'XYX', 'XXX'], {'X': '#tfc:high_quality_cloth', 'Y': '#forge:rods/wooden'}, 'minecraft:painting').with_advancement('#tfc:high_quality_cloth')
    rm.crafting_shaped('crafting/vanilla/tnt', ['XYX', 'YXY', 'XYX'], {'X': 'minecraft:gunpowder', 'Y': 'minecraft:sand'}, 'minecraft:tnt').with_advancement('minecraft:gunpowder')
    rm.crafting_shaped('crafting/vanilla/spyglass', ['X', 'Y', 'Y'], {'Y': '#forge:sheets/copper', 'X': 'minecraft:glass_pane'}, 'minecraft:spyglass').with_advancement('#forge:sheets/copper')

    # todo: redstone lamp
    rm.crafting_shaped('crafting/vanilla/redstone/hopper', ['X X', ' Y '], {'X': '#forge:sheets/wrought_iron', 'Y': '#forge:chests/wooden'}, 'minecraft:hopper').with_advancement('#forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/observer', ['CCC', 'RRB', 'CCC'], {'C': '#forge:cobblestone', 'R': '#forge:dusts/redstone', 'B': 'tfc:brass_mechanisms'}, 'minecraft:observer').with_advancement('tfc:brass_mechanisms')
    rm.crafting_shaped('crafting/vanilla/redstone/piston', ['WWW', 'SXS', 'SBS'], {'X': '#forge:rods/wrought_iron', 'S': '#forge:cobblestone', 'W': '#tfc:lumber', 'B': 'tfc:brass_mechanisms'}, 'minecraft:piston').with_advancement('tfc:brass_mechanisms')
    rm.crafting_shaped('crafting/vanilla/redstone/comparator', [' T ', 'TRT', 'SSS'], {'R': '#forge:dusts/redstone', 'T': 'minecraft:redstone_torch', 'S': '#forge:smooth_stone'}, 'minecraft:comparator').with_advancement('minecraft:redstone_torch')
    rm.crafting_shaped('crafting/vanilla/redstone/repeater', ['TRT', 'SSS'], {'T': 'minecraft:redstone_torch', 'R': '#forge:dusts/redstone', 'S': '#forge:smooth_stone'}, 'minecraft:repeater').with_advancement('minecraft:redstone_torch')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_hopper', ['X X', ' Y '], {'X': '#forge:sheets/steel', 'Y': '#forge:chests/wooden'}, (2, 'minecraft:hopper')).with_advancement('#forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/heavy_weighted_pressure_plate', ['XX'], {'X': '#forge:ingots/wrought_iron'}, 'minecraft:heavy_weighted_pressure_plate').with_advancement('#forge:ingots/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/daylight_detector', ['GGG', 'RRR', 'WWW'], {'G': '#forge:glass', 'R': '#forge:dusts/redstone', 'W': '#tfc:lumber'}, 'minecraft:daylight_detector').with_advancement('#forge:dusts/redstone')
    rm.crafting_shaped('crafting/vanilla/redstone/tripwire_hook', ['I', 'W', 'S'], {'I': '#forge:sheets/wrought_iron', 'W': '#tfc:lumber', 'S': '#forge:rods/wooden'}, (2, 'minecraft:tripwire_hook')).with_advancement('#forge:sheets/wrought_iron')


    rm.crafting_shaped('crafting/vanilla/redstone/activator_rail', ['SRS', 'SWS', 'SRS'], {'S': '#forge:rods/wrought_iron', 'W': 'minecraft:redstone_torch', 'R': '#forge:rods/wooden'}, (4, 'minecraft:activator_rail')).with_advancement('#forge:rods/gold')
    rm.crafting_shaped('crafting/vanilla/redstone/detector_rail', ['S S', 'SWS', 'SRS'], {'S': '#forge:rods/wrought_iron', 'W': '#minecraft:stone_pressure_plates', 'R': '#forge:dusts/redstone'}, (4, 'minecraft:detector_rail')).with_advancement('#forge:rods/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/minecart', ['X X', 'XXX'], {'X': '#forge:sheets/wrought_iron'}, 'minecraft:minecart').with_advancement('#forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/powered_rail', ['SWS', 'SRS', 'SWS'], {'S': '#forge:rods/gold', 'W': '#forge:rods/wooden', 'R': '#forge:dusts/redstone'}, (8, 'minecraft:powered_rail')).with_advancement('#forge:rods/gold')
    rm.crafting_shaped('crafting/vanilla/redstone/rail', ['S S', 'SWS', 'S S'], {'W': '#forge:rods/wooden', 'S': '#forge:rods/wrought_iron'}, (8, 'minecraft:rail')).with_advancement('#forge:rods/wrought_iron')

    rm.crafting_shaped('crafting/vanilla/redstone/steel_activator_rail', ['SRS', 'SWS', 'SRS'], {'S': '#forge:rods/steel', 'W': 'minecraft:redstone_torch', 'R': '#forge:rods/wooden'}, (8, 'minecraft:activator_rail')).with_advancement('#forge:rods/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_detector_rail', ['S S', 'SWS', 'SRS'], {'S': '#forge:rods/steel', 'W': '#minecraft:stone_pressure_plates', 'R': '#forge:dusts/redstone'}, (8, 'minecraft:detector_rail')).with_advancement('#forge:rods/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_minecart', ['X X', 'XXX'], {'X': '#forge:sheets/steel'}, (2, 'minecraft:minecart')).with_advancement('#forge:sheets/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_rail', ['S S', 'SWS', 'S S'], {'W': '#forge:rods/wooden', 'S': '#forge:rods/steel'}, (16, 'minecraft:rail')).with_advancement('#forge:rods/steel')

    # ============================
    # Collapse / Landslide Recipes
    # ============================

    for rock in ROCKS:
        raw = 'tfc:rock/raw/%s' % rock
        cobble = 'tfc:rock/cobble/%s' % rock
        mossy_cobble = 'tfc:rock/mossy_cobble/%s' % rock
        gravel = 'tfc:rock/gravel/%s' % rock
        spike = 'tfc:rock/spike/%s' % rock

        # Raw rock can TRIGGER and START, and FALL into cobble
        # Ores can FALL into cobble
        rm.block_tag('can_trigger_collapse', raw)
        rm.block_tag('can_start_collapse', raw)
        rm.block_tag('can_collapse', raw)

        collapse_recipe(rm, '%s_cobble' % rock, [
            raw,
            *['tfc:ore/%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if not ore_data.graded],
            *['tfc:ore/poor_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded],
            *['tfc:ore/normal_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded],
            *['tfc:ore/rich_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded]
        ], cobble)

        for ore, ore_data in ORES.items():
            if ore_data.graded:
                for grade in ORE_GRADES.keys():
                    rm.block_tag('can_start_collapse', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
                    rm.block_tag('can_collapse', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
            else:
                rm.block_tag('can_start_collapse', 'tfc:ore/%s/%s' % (ore, rock))
                rm.block_tag('can_collapse', 'tfc:ore/%s/%s' % (ore, rock))

        # Gravel and cobblestone have landslide recipes
        rm.block_tag('can_landslide', cobble, gravel, mossy_cobble)

        landslide_recipe(rm, '%s_cobble' % rock, cobble, cobble)
        landslide_recipe(rm, '%s_mossy_cobble' % rock, mossy_cobble, mossy_cobble)
        landslide_recipe(rm, '%s_gravel' % rock, gravel, gravel)

        # Spikes can collapse, but produce nothing
        rm.block_tag('can_collapse', spike)
        collapse_recipe(rm, '%s_spike' % rock, spike, copy_input=True)

        for deposit in ORE_DEPOSITS:
            rm.block_tag('can_landslide', 'tfc:deposit/%s/%s' % (deposit, rock))
            landslide_recipe(rm, '%s_%s_deposit' % (deposit, rock), 'tfc:deposit/%s/%s' % (deposit, rock), gravel)

    # Soil Blocks
    for variant in SOIL_BLOCK_VARIANTS:
        for block_type in SOIL_BLOCK_TYPES:
            rm.block_tag('can_landslide', 'tfc:%s/%s' % (block_type, variant))

        # Blocks that create normal dirt
        landslide_recipe(rm, '%s_dirt' % variant, ['tfc:%s/%s' % (block_type, variant) for block_type in ('dirt', 'grass', 'grass_path', 'farmland', 'rooted_dirt')], 'tfc:dirt/%s' % variant)
        landslide_recipe(rm, '%s_clay_dirt' % variant, ['tfc:%s/%s' % (block_type, variant) for block_type in ('clay', 'clay_grass')], 'tfc:clay/%s' % variant)

    # Sand
    for variant in SAND_BLOCK_TYPES:
        rm.block_tag('can_landslide', 'tfc:sand/%s' % variant)
        landslide_recipe(rm, '%s_sand' % variant, 'tfc:sand/%s' % variant, 'tfc:sand/%s' % variant)

    # Vanilla landslide blocks
    for block in ('sand', 'red_sand', 'gravel', 'cobblestone', 'mossy_cobblestone'):
        rm.block_tag('can_landslide', 'minecraft:%s' % block)
        landslide_recipe(rm, 'vanilla_%s' % block, 'minecraft:%s' % block, 'minecraft:%s' % block)

    vanilla_dirt_landslides = ('grass_block', 'dirt', 'coarse_dirt', 'podzol')
    for block in vanilla_dirt_landslides:
        rm.block_tag('can_landslide', 'minecraft:%s' % block)
    landslide_recipe(rm, 'vanilla_dirt', ['minecraft:%s' % block for block in vanilla_dirt_landslides], 'minecraft:dirt')

    # Vanilla collapsible blocks
    for rock in ('stone', 'andesite', 'granite', 'diorite'):
        block = 'minecraft:%s' % rock
        rm.block_tag('can_trigger_collapse', block)
        rm.block_tag('can_start_collapse', block)
        rm.block_tag('can_collapse', block)

        collapse_recipe(rm, 'vanilla_%s' % rock, block, block if rock != 'stone' else 'minecraft:cobblestone')

    # ============
    # Heat Recipes
    # ============

    heat_recipe(rm, 'torch_from_stick', '#forge:rods/wooden', 60, result_item=(2, 'tfc:torch'))
    heat_recipe(rm, 'torch_from_stick_bunch', 'tfc:stick_bunch', 60, result_item=(18, 'tfc:torch'))
    heat_recipe(rm, 'glass_from_shards', 'tfc:glass_shard', 180, result_item='minecraft:glass')
    heat_recipe(rm, 'glass_from_sand', '#forge:sand', 180, result_item='minecraft:glass')
    heat_recipe(rm, 'brick', 'tfc:ceramic/unfired_brick', POTTERY_MELT, result_item='minecraft:brick')
    heat_recipe(rm, 'flower_pot', 'tfc:ceramic/unfired_flower_pot', POTTERY_MELT, result_item='minecraft:flower_pot')
    heat_recipe(rm, 'ceramic_jug', 'tfc:ceramic/unfired_jug', POTTERY_MELT, result_item='tfc:ceramic/jug')
    heat_recipe(rm, 'ceramic_pan', 'tfc:ceramic/unfired_pan', POTTERY_MELT, result_item='tfc:pan/empty')
    heat_recipe(rm, 'terracotta', 'minecraft:clay', POTTERY_MELT, result_item='minecraft:terracotta')

    for ore, ore_data in ORES.items():
        if ore_data.metal and ore_data.graded:
            temp = METALS[ore_data.metal].melt_temperature
            heat_recipe(rm, ('ore', 'small_%s' % ore), 'tfc:ore/small_%s' % ore, temp, None, 'tfc:metal/%s' % ore_data.metal, 10)
            heat_recipe(rm, ('ore', 'poor_%s' % ore), 'tfc:ore/poor_%s' % ore, temp, None, 'tfc:metal/%s' % ore_data.metal, 15)
            heat_recipe(rm, ('ore', 'normal_%s' % ore), 'tfc:ore/normal_%s' % ore, temp, None, 'tfc:metal/%s' % ore_data.metal, 25)
            heat_recipe(rm, ('ore', 'rich_%s' % ore), 'tfc:ore/rich_%s' % ore, temp, None, 'tfc:metal/%s' % ore_data.metal, 35)

    for metal, metal_data in METALS.items():
        melt_metal = metal if metal_data.melt_metal is None else metal_data.melt_metal
        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            if item_data.type == 'all' or item_data.type in metal_data.types:
                heat_recipe(rm, ('metal', '%s_%s' % (metal, item)), 'tfc:metal/%s/%s' % (item, metal), metal_data.melt_temperature, None, 'tfc:metal/%s' % melt_metal, item_data.smelt_amount)

    # Mold, Ceramic Firing
    for tool, tool_data in METAL_ITEMS.items():
        if tool_data.mold:
            heat_recipe(rm, ('%s_mold' % tool), 'tfc:ceramic/unfired_%s_mold' % tool, POTTERY_MELT, 'tfc:ceramic/%s_mold' % tool)

    for pottery in SIMPLE_POTTERY:
        heat_recipe(rm, 'fired_' + pottery, 'tfc:ceramic/unfired_' + pottery, POTTERY_MELT, result_item='tfc:ceramic/' + pottery)

    for color in COLORS:
        heat_recipe(rm, 'glazed_terracotta_%s' % color, 'minecraft:%s_terracotta' % color, POTTERY_MELT, result_item='minecraft:%s_glazed_terracotta' % color)
        heat_recipe(rm, 'glazed_ceramic_vessel_%s' % color, 'tfc:ceramic/%s_unfired_vessel' % color, POTTERY_MELT, 'tfc:ceramic/%s_glazed_vessel' % color)

        rm.crafting_shapeless('crafting/ceramic/%s_unfired_vessel' % color, ('minecraft:%s_dye' % color, 'tfc:ceramic/unfired_vessel'), 'tfc:ceramic/%s_unfired_vessel' % color).with_advancement('minecraft:%s_dye' % color)
        if color != 'white':
            rm.crafting_shaped('crafting/vanilla/color/%s_bed' % color, ['ZZZ', 'XXX', 'YYY'], {'X': '#tfc:high_quality_cloth', 'Y': '#tfc:lumber', 'Z': 'minecraft:%s_dye' % color}, 'minecraft:%s_bed' % color).with_advancement('#tfc:high_quality_cloth')
        rm.crafting_shapeless('crafting/vanilla/color/%s_concrete_powder' % color, ('minecraft:%s_dye' % color, '#forge:sand', '#forge:sand', '#forge:sand', '#forge:sand', '#forge:gravel', '#forge:gravel', '#forge:gravel', '#forge:gravel'), (8, 'minecraft:%s_concrete_powder' % color))

    for name in DISABLED_VANILLA_RECIPES:
        disable_recipe(rm, 'minecraft:' + name)

    # Quern
    quern_recipe(rm, 'olive', 'tfc:food/olive', 'tfc:olive_paste')
    quern_recipe(rm, 'borax', 'tfc:ore/borax', 'tfc:powder/flux', count=6)
    quern_recipe(rm, 'fluxstone', '#tfc:fluxstone', 'tfc:powder/flux', count=2)
    quern_recipe(rm, 'cinnabar', 'tfc:ore/cinnabar', 'minecraft:redstone', count=8)
    quern_recipe(rm, 'cryolite', 'tfc:ore/cryolite', 'minecraft:redstone', count=8)
    quern_recipe(rm, 'bone', 'minecraft:bone', 'minecraft:bone_meal', count=3)
    quern_recipe(rm, 'bone_block', 'minecraft:bone_block', 'minecraft:bone_meal', count=9)
    quern_recipe(rm, 'charcoal', 'minecraft:charcoal', 'tfc:powder/charcoal', count=4)
    quern_recipe(rm, 'salt', 'tfc:ore/halite', 'tfc:powder/salt', count=4)
    quern_recipe(rm, 'blaze_rod', 'minecraft:blaze_rod', 'minecraft:blaze_powder', count=2)
    quern_recipe(rm, 'raw_limestone', 'tfc:rock/raw/limestone', 'tfc:ore/gypsum')
    quern_recipe(rm, 'sylvite', 'tfc:ore/sylvite', 'tfc:powder/sylvite', count=4)

    for grain in GRAINS:
        heat_recipe(rm, grain + '_dough', 'tfc:food/%s_dough' % grain, 200, result_item='tfc:food/%s_bread' % grain)
        quern_recipe(rm, grain + '_grain', 'tfc:food/%s_grain' % grain, 'tfc:food/%s_flour' % grain)

    for meat in MEATS:
        heat_recipe(rm, meat, 'tfc:food/%s' % meat, 200, result_item='tfc:food/cooked_%s' % meat)

    heat_recipe(rm, 'seaweed', 'tfc:groundcover/seaweed', 200, 'tfc:food/dried_seaweed')
    heat_recipe(rm, 'giant_kelp_flower', 'tfc:plant/giant_kelp_flower', 200, 'tfc:food/dried_kelp')

    for ore in ['hematite', 'limonite', 'malachite']:
        for grade, data in ORE_GRADES.items():
            quern_recipe(rm, '%s_%s' % (grade, ore), 'tfc:ore/%s_%s' % (grade, ore), 'tfc:powder/%s' % ore, count=data.grind_amount)
        quern_recipe(rm, 'small_%s' % ore, 'tfc:ore/small_%s' % ore, 'tfc:powder/%s' % ore, count=2)

    for ore in ['sulfur', 'saltpeter', 'graphite', 'kaolinite']:
        quern_recipe(rm, ore, 'tfc:ore/%s' % ore, 'tfc:powder/%s' % ore, count=4)
    for gem in GEMS:
        quern_recipe(rm, gem, 'tfc:ore/%s' % gem, 'tfc:powder/%s' % gem, count=4)

    for color, plants in PLANT_COLORS.items():
        for plant in plants:
            quern_recipe(rm, 'plant/%s' % plant, 'tfc:plant/%s' % plant, 'minecraft:%s_dye' % color, count=2)

    for i, size in enumerate(('small', 'medium', 'large')):
        scraping_recipe(rm, '%s_soaked_hide' % size, 'tfc:%s_soaked_hide' % size, 'tfc:%s_scraped_hide' % size)
        damage_shapeless(rm, 'crafting/%s_sheepskin' % size, ('tfc:%s_sheepskin_hide' % size, '#tfc:knives'), (i + 1, 'tfc:wool')).with_advancement('tfc:%s_sheepskin_hide' % size)

    # todo: actual pot recipes

    paste = utils.ingredient('tfc:olive_paste')
    rm.recipe(('pot', 'olive_oil_water'), 'tfc:pot_fluid', {
        'ingredients': [paste, paste, paste, paste],
        'fluid_ingredient': fluid_stack_ingredient('minecraft:water', 1000),
        'duration': 4000,
        'temperature': 300,
        'fluid_output': fluid_stack('tfc:olive_oil_water', 1000)
    })

    blubber = utils.ingredient('tfc:blubber')
    rm.recipe(('pot', 'tallow'), 'tfc:pot_fluid', {
        'ingredients': [blubber, blubber, blubber, blubber],
        'fluid_ingredient': fluid_stack_ingredient('minecraft:water', 1000),
        'duration': 8000,
        'temperature': 600,
        'fluid_output': fluid_stack('tfc:tallow', 1000)
    })

    rm.recipe(('pot', 'mushroom_soup'), 'tfc:pot_soup', {
        'ingredients': [utils.ingredient('minecraft:red_mushroom'), utils.ingredient('minecraft:brown_mushroom')],
        'fluid_ingredient': fluid_stack_ingredient('minecraft:water', 1000),
        'duration': 200,
        'temperature': 300
    })

    clay_knapping(rm, 'vessel', [' XXX ', 'XXXXX', 'XXXXX', 'XXXXX', ' XXX '], 'tfc:ceramic/unfired_vessel')
    clay_knapping(rm, 'jug', [' X   ', 'XXXX ', 'XXX X', 'XXXX ', 'XXX  '], 'tfc:ceramic/unfired_jug')
    clay_knapping(rm, 'pot', ['X   X', 'X   X', 'X   X', 'XXXXX', ' XXX '], 'tfc:ceramic/unfired_pot')
    clay_knapping(rm, 'bowl_2', ['X   X', ' XXX '], (2, 'tfc:ceramic/unfired_bowl'), False)
    clay_knapping(rm, 'bowl_4', ['X   X', ' XXX ', '     ', 'X   X', ' XXX '], (4, 'tfc:ceramic/unfired_bowl'))
    clay_knapping(rm, 'brick', ['XXXXX', '     ', 'XXXXX', '     ', 'XXXXX'], (3, 'tfc:ceramic/unfired_brick'))
    clay_knapping(rm, 'flower_pot', [' X X ', ' XXX ', '     ', ' X X ', ' XXX '], (2, 'tfc:ceramic/unfired_flower_pot'))
    clay_knapping(rm, 'spindle_head', ['  X  ', 'XXXXX', '  X  '], 'tfc:ceramic/unfired_spindle_head', False)

    clay_knapping(rm, 'ingot_mold', ['XXXX', 'X  X', 'X  X', 'X  X', 'XXXX'], 'tfc:ceramic/unfired_ingot_mold')
    clay_knapping(rm, 'axe_head_mold', ['X XXX', '    X', '     ', '    X', 'X XXX'], 'tfc:ceramic/unfired_axe_head_mold', True)
    clay_knapping(rm, 'chisel_head_mold', ['XX XX', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], 'tfc:ceramic/unfired_chisel_head_mold', True)
    clay_knapping(rm, 'hammer_head_mold', ['XXXXX', '     ', '     ', 'XX XX', 'XXXXX'], 'tfc:ceramic/unfired_hammer_head_mold', True)
    clay_knapping(rm, 'hoe_head_mold', ['XXXXX', '     ', '  XXX', 'XXXXX'], 'tfc:ceramic/unfired_hoe_head_mold', True)
    clay_knapping(rm, 'javelin_head_mold', ['   XX', '    X', '     ', 'X   X', 'XX XX'], 'tfc:ceramic/unfired_javelin_head_mold', True)
    clay_knapping(rm, 'knife_blade_mold', ['XX X', 'X  X', 'X  X', 'X  X', 'X  X'], 'tfc:ceramic/unfired_knife_blade_mold', True)
    clay_knapping(rm, 'mace_blade_mold', ['XX XX', 'X   X', 'X   X', 'X   X', 'XX XX'], 'tfc:ceramic/unfired_mace_head_mold', True)
    clay_knapping(rm, 'pickaxe_head_mold', ['XXXXX', 'X   X', ' XXX ', 'XXXXX'], 'tfc:ceramic/unfired_pickaxe_head_mold', True)
    clay_knapping(rm, 'propick_head_mold', ['XXXXX', '    X', ' XXX ', ' XXXX', 'XXXXX'], 'tfc:ceramic/unfired_propick_head_mold', True)
    clay_knapping(rm, 'saw_blade_mold', ['  XXX', '   XX', 'X   X', 'X    ', 'XXX  '], 'tfc:ceramic/unfired_saw_blade_mold', True)
    clay_knapping(rm, 'shovel_head_mold', ['X   X', 'X   X', 'X   X', 'X   X', 'XX XX'], 'tfc:ceramic/unfired_shovel_head_mold', True)
    clay_knapping(rm, 'sword_blade_mold', ['  XXX', '   XX', 'X   X', 'XX  X', 'XXXX '], 'tfc:ceramic/unfired_sword_blade_mold', True)
    clay_knapping(rm, 'scythe_blade_mold', ['XXXXX', 'X    ', '    X', '  XXX', 'XXXXX'], 'tfc:ceramic/unfired_scythe_blade_mold', True)

    fire_clay_knapping(rm, 'crucible', ['X   X', 'X   X', 'X   X', 'X   X', 'XXXXX'], 'tfc:ceramic/unfired_crucible')
    fire_clay_knapping(rm, 'brick', ['XXXXX', '     ', 'XXXXX', '     ', 'XXXXX'], (3, 'tfc:ceramic/unfired_fire_brick'))

    leather_knapping(rm, 'helmet', ['XXXXX', 'X   X', 'X   X', '     ', '     '], 'minecraft:leather_helmet')
    leather_knapping(rm, 'chestplate', ['X   X', 'XXXXX', 'XXXXX', 'XXXXX', 'XXXXX'], 'minecraft:leather_chestplate')
    leather_knapping(rm, 'leggings', ['XXXXX', 'XXXXX', 'XX XX', 'XX XX', 'XX XX'], 'minecraft:leather_leggings')
    leather_knapping(rm, 'boots', ['XX   ', 'XX   ', 'XX   ', 'XXXX ', 'XXXXX'], 'minecraft:leather_boots')
    leather_knapping(rm, 'saddle', ['  X  ', 'XXXXX', 'XXXXX', 'XXXXX', '  X  '], 'minecraft:saddle')
    # todo: quiver

    for category in ROCK_CATEGORIES:
        predicate = '#tfc:%s_rock' % category
        rock_knapping(rm, 'axe_head_%s' % category, [' X   ', 'XXXX ', 'XXXXX', 'XXXX ', ' X   '], 'tfc:stone/axe_head/%s' % category, predicate)
        rock_knapping(rm, 'shovel_head_%s' % category, ['XXX', 'XXX', 'XXX', 'XXX', ' X '], 'tfc:stone/shovel_head/%s' % category, predicate)
        rock_knapping(rm, 'hoe_head_%s' % category, ['XXXXX', '   XX'], 'tfc:stone/hoe_head/%s' % category, predicate)
        rock_knapping(rm, 'knife_head_%s' % category, ['X ', 'XX', 'XX', 'XX', 'XX'], 'tfc:stone/knife_head/%s' % category, predicate)
        rock_knapping(rm, 'knife_head_1_%s' % category, ['X  X ', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], (2, 'tfc:stone/knife_head/%s' % category), predicate)
        rock_knapping(rm, 'knife_head_2_%s' % category, ['X   X', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], (2, 'tfc:stone/knife_head/%s' % category), predicate)
        rock_knapping(rm, 'knife_head_3_%s' % category, [' X X ', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], (2, 'tfc:stone/knife_head/%s' % category), predicate)
        rock_knapping(rm, 'hoe_head_1_%s' % category, ['XXXXX', 'XX   ', '     ', 'XXXXX', 'XX   '], (2, 'tfc:stone/hoe_head/%s' % category), predicate)
        rock_knapping(rm, 'hoe_head_2_%s' % category, ['XXXXX', 'XX   ', '     ', 'XXXXX', '   XX'], (2, 'tfc:stone/hoe_head/%s' % category), predicate)
        rock_knapping(rm, 'knife_head_%s' % category, ['X ', 'XX', 'XX', 'XX', 'XX'], 'tfc:stone/knife_head/%s' % category, predicate)
        rock_knapping(rm, 'javelin_head_%s' % category, ['XXX  ', 'XXXX ', 'XXXXX', ' XXX ', '  X  '], 'tfc:stone/javelin_head/%s' % category, predicate)
        rock_knapping(rm, 'hammer_head_%s' % category, ['XXXXX', 'XXXXX', '  X  '], 'tfc:stone/hammer_head/%s' % category, predicate)

        for tool in ROCK_CATEGORY_ITEMS:
            rm.crafting_shaped('crafting/stone/%s_%s' % (tool, category), ['X', 'Y'], {'X': 'tfc:stone/%s_head/%s' % (tool, category), 'Y': '#forge:rods/wooden'}, 'tfc:stone/%s/%s' % (tool, category)).with_advancement('tfc:stone/%s_head/%s' % (tool, category))

    # Casting Recipes

    for metal, metal_data in METALS.items():
        for tool, tool_data in METAL_ITEMS.items():
            if tool == 'ingot' or (tool_data.mold and 'tool' in metal_data.types and metal_data.tier <= 2):
                casting_recipe(rm, '%s_%s' % (metal, tool), tool, metal, tool_data.smelt_amount, 0.1 if tool == 'ingot' else 1)

    rm.recipe('casting', 'tfc:casting_crafting', {})  # simple recipe to allow any casting recipe to be used in a crafting grid

    # Alloy Recipes

    alloy_recipe(rm, 'bismuth_bronze', 'bismuth_bronze', ('zinc', 0.2, 0.3), ('copper', 0.5, 0.65), ('bismuth', 0.1, 0.2))
    alloy_recipe(rm, 'black_bronze', 'black_bronze', ('copper', 0.5, 0.7), ('silver', 0.1, 0.25), ('gold', 0.1, 0.25))
    alloy_recipe(rm, 'bronze', 'bronze', ('copper', 0.88, 0.92), ('tin', 0.08, 0.12))
    alloy_recipe(rm, 'brass', 'brass', ('copper', 0.88, 0.92), ('zinc', 0.08, 0.12))
    alloy_recipe(rm, 'rose_gold', 'rose_gold', ('copper', 0.15, 0.3), ('gold', 0.7, 0.85))
    alloy_recipe(rm, 'sterling_silver', 'sterling_silver', ('copper', 0.2, 0.4), ('silver', 0.6, 0.8))
    alloy_recipe(rm, 'weak_steel', 'weak_steel', ('steel', 0.5, 0.7), ('nickel', 0.15, 0.25), ('black_bronze', 0.15, 0.25))
    alloy_recipe(rm, 'weak_blue_steel', 'weak_blue_steel', ('black_steel', 0.5, 0.55), ('steel', 0.2, 0.25), ('bismuth_bronze', 0.1, 0.15), ('sterling_silver', 0.1, 0.15))
    alloy_recipe(rm, 'weak_red_steel', 'weak_red_steel', ('black_steel', 0.5, 0.55), ('steel', 0.2, 0.25), ('brass', 0.1, 0.15), ('rose_gold', 0.1, 0.15))

    # Loom Recipes
    
    loom_recipe(rm, 'burlap_cloth', 'tfc:jute_fiber', 12, 'tfc:burlap_cloth', 12, 'tfc:block/burlap')
    loom_recipe(rm, 'wool_cloth', 'tfc:wool_yarn', 16, 'tfc:wool_cloth', 16, 'minecraft:block/white_wool')
    loom_recipe(rm, 'silk_cloth', 'minecraft:string', 24, 'tfc:silk_cloth', 24, 'minecraft:block/white_wool')
    loom_recipe(rm, 'wool_block', 'tfc:wool_cloth', 4, (8, 'minecraft:white_wool'), 4, 'minecraft:block/white_wool')
    
    
def disable_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier):
    rm.recipe(name_parts, 'forge:conditional', {'recipes': []})


def collapse_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient, result: Optional[utils.Json] = None, copy_input: Optional[bool] = None):
    assert result is not None or copy_input
    rm.recipe(('collapse', name_parts), 'tfc:collapse', {
        'ingredient': ingredient,
        'result': result,
        'copy_input': copy_input
    })


def landslide_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, result: utils.Json):
    rm.recipe(('landslide', name_parts), 'tfc:landslide', {
        'ingredient': ingredient,
        'result': result
    })


def stone_cutting(rm: ResourceManager, name_parts: utils.ResourceIdentifier, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('stonecutting', name_parts), 'minecraft:stonecutting', {
        'ingredient': utils.ingredient(item),
        'result': result,
        'count': count
    })


def damage_shapeless(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: utils.Json, result: utils.Json, group: str = None, conditions: utils.Json = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:damage_inputs_shapeless_crafting',
        'recipe': {
            'type': 'minecraft:crafting_shapeless',
            'group': group,
            'ingredients': utils.item_stack_list(ingredients),
            'result': utils.item_stack(result),
            'conditions': utils.recipe_condition(conditions)
        }
    })
    return RecipeContext(rm, res)


# todo: damage inputs shaped, if we need it


def quern_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('quern', name), 'tfc:quern', {
        'ingredient': utils.ingredient(item),
        'result': utils.item_stack((count, result))
    })


def scraping_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('scraping', name), 'tfc:scraping', {
        'ingredient': utils.ingredient(item),
        'result': utils.item_stack((count, result))
    })


def clay_knapping(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: List[str], result: utils.Json, outside_slot_required: bool = None):
    knapping_recipe(rm, 'clay_knapping', name_parts, pattern, result, outside_slot_required)


def fire_clay_knapping(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: List[str], result: utils.Json, outside_slot_required: bool = None):
    knapping_recipe(rm, 'fire_clay_knapping', name_parts, pattern, result, outside_slot_required)


def leather_knapping(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: List[str], result: utils.Json, outside_slot_required: bool = None):
    knapping_recipe(rm, 'leather_knapping', name_parts, pattern, result, outside_slot_required)


def knapping_recipe(rm: ResourceManager, knapping_type: str, name_parts: utils.ResourceIdentifier, pattern: List[str], result: utils.Json, outside_slot_required: bool = None):
    rm.recipe((knapping_type, name_parts), 'tfc:%s' % knapping_type, {
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'result': utils.item_stack(result)
    })


def rock_knapping(rm: ResourceManager, name, pattern: List[str], result: utils.ResourceIdentifier, ingredient: str = None, outside_slot_required: bool = False):
    ingredient = None if ingredient is None else utils.ingredient(ingredient)
    return rm.recipe(('rock_knapping', name), 'tfc:rock_knapping', {
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'result': utils.item_stack(result),
        'ingredient': ingredient
    })


def heat_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, temperature: float, result_item: Optional[utils.Json] = None, result_fluid: Optional[str] = None, amount: int = 1000) -> RecipeContext:
    result_item = None if result_item is None else utils.item_stack(result_item)
    result_fluid = None if result_fluid is None else fluid_stack(result_fluid, amount)
    return rm.recipe(('heating', name_parts), 'tfc:heating', {
        'ingredient': utils.ingredient(ingredient),
        'result_item': result_item,
        'result_fluid': result_fluid,
        'temperature': temperature
    })


def casting_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, mold: str, metal: str, amount: int, break_chance: float):
    rm.recipe(('casting', name_parts), 'tfc:casting', {
        'mold': {'item': 'tfc:ceramic/%s_mold' % mold},
        'fluid': fluid_stack_ingredient('tfc:metal/%s' % metal, amount),
        'result': utils.item_stack('tfc:metal/%s/%s' % (mold, metal)),
        'break_chance': break_chance
    })


def alloy_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, metal: str, *parts: Tuple[str, float, float]):
    rm.recipe(('alloy', name_parts), 'tfc:alloy', {
        'result': 'tfc:%s' % metal,
        'contents': [{
            'metal': 'tfc:%s' % p[0],
            'min': p[1],
            'max': p[2]
        } for p in parts]
    })


def loom_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, ingredient: str, input_count: int, result: utils.Json, steps: int, in_progress_texture: str):
    return rm.recipe(('loom', name), 'tfc:loom', {
        'ingredient': utils.ingredient(ingredient),
        'input_count': input_count,
        'result': utils.item_stack(result),
        'steps_required': steps,
        'in_progress_texture': in_progress_texture
    })


def fluid_stack(fluid: str, amount: int) -> Dict[str, Any]:
    return {
        'fluid': fluid,
        'amount': amount
    }


def fluid_stack_ingredient(fluid: utils.Json, amount: int) -> Dict[str, Any]:
    return {
        'fluid': fluid_ingredient(fluid),
        'amount': amount
    }


def fluid_ingredient(data_in: utils.Json) -> utils.Json:
    if isinstance(data_in, str):
        if data_in[0:4] == '#':
            return {'tag': data_in[4:]}
        elif data_in[0] == '#':
            return {'tag': data_in[1:]}
        else:
            return data_in  # raw strings are accepted as fluids
    elif isinstance(data_in, Sequence):
        return [*utils.flatten_list([fluid_ingredient(e) for e in data_in])]
    elif isinstance(data_in, dict):
        if 'tag' in data_in:
            return {'tag': data_in['tag']}
        if 'fluid' in data_in:
            return data_in['fluid']
        raise ValueError('fluid_ingredient must have fluid or tag entries.')

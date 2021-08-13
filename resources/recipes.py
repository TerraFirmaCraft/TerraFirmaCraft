#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Any

from mcresources import ResourceManager, utils
from mcresources.recipe_context import RecipeContext

from constants import *


# Crafting recipes
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
        damage_shapeless(rm, 'crafting/rock/%s_smooth' % rock, (raw, 'tag!tfc:chisels'), smooth).with_advancement(raw)
        damage_shapeless(rm, 'crafting/rock/%s_brick' % rock, (loose, 'tag!tfc:chisels'), brick).with_advancement(loose)
        damage_shapeless(rm, 'crafting/rock/%s_chiseled' % rock, (smooth, 'tag!tfc:chisels'), chiseled).with_advancement(smooth)
        damage_shapeless(rm, 'crafting/rock/%s_button' % rock, ('tag!tfc:chisels', brick), 'tfc:rock/button/%s' % rock).with_advancement(brick)
        damage_shapeless(rm, 'crafting/rock/%s_pressure_plate' % rock, ('tag!tfc:chisels', brick, brick), 'tfc:rock/pressure_plate/%s' % rock).with_advancement(brick)

        rm.crafting_shaped('crafting/rock/%s_hardened' % rock, ['XMX', 'MXM', 'XMX'], {'X': raw, 'M': 'tag!tfc:mortar'}, (2, hardened)).with_advancement(raw)
        rm.crafting_shaped('crafting/rock/%s_bricks' % rock, ['XMX', 'MXM', 'XMX'], {'X': brick, 'M': 'tag!tfc:mortar'}, (4, bricks)).with_advancement(brick)

        damage_shapeless(rm, 'crafting/rock/%s_cracked' % rock, (bricks, 'tag!tfc:hammers'), cracked_bricks).with_advancement(bricks)

    for metal, metal_data in METALS.items():
        if 'utility' in metal_data.types:
            rm.crafting_shaped('crafting/metal/anvil/%s' % metal, ['XXX', ' X ', 'XXX'], {'X': 'tfc:metal/double_ingot/%s' % metal}, 'tfc:metal/anvil/%s' % metal).with_advancement('tfc:metal/double_ingot/%s' % metal)
        if 'tool' in metal_data.types:
            for tool in METAL_TOOL_HEADS:
                suffix = '_blade' if tool in ('knife', 'saw', 'scythe', 'sword') else '_head'
                rm.crafting_shaped('crafting/metal/%s/%s' % (tool, metal), ['X', 'Y'], {'X': 'tfc:metal/%s%s/%s' % (tool, suffix, metal), 'Y': 'tag!forge:rods/wooden'}, 'tfc:metal/%s/%s' % (tool, metal)).with_advancement('tfc:metal/%s%s/%s' % (tool, suffix, metal))

    for wood in WOODS.keys():
        def item(thing: str):
            return 'tfc:wood/%s/%s' % (thing, wood)

        def plank(thing: str):
            return 'tfc:wood/planks/%s_%s' % (wood, thing)

        rm.crafting_shapeless('crafting/wood/%s_twig' % wood, item('twig'), 'minecraft:stick').with_advancement(item('twig'))
        rm.crafting_shaped('crafting/wood/%s_bookshelf' % wood, ['XXX', 'YYY', 'XXX'], {'X': item('planks'), 'Y': 'minecraft:book'}, plank('bookshelf')).with_advancement('minecraft:book')
        rm.crafting_shapeless('crafting/wood/%s_button' % wood, item('planks'), plank('button')).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_door' % wood, ['XX', 'XX', 'XX'], {'X': item('lumber')}, (2, plank('door'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_fence' % wood, ['XYX', 'XYX'], {'X': item('planks'), 'Y': item('lumber')}, (8, plank('fence'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_log_fence' % wood, ['XYX', 'XYX'], {'X': item('log'), 'Y': item('lumber')}, (8, plank('log_fence'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_fence_gate' % wood, ['YXY', 'YXY'], {'X': item('planks'), 'Y': item('lumber')}, (2, plank('fence_gate'))).with_advancement(item('lumber'))
        damage_shapeless(rm, 'crafting/wood/%s_lumber_log' % wood, (item('log'), 'tag!tfc:saws'), (8, item('lumber'))).with_advancement(item('log'))
        damage_shapeless(rm, 'crafting/wood/%s_lumber_planks' % wood, (item('planks'), 'tag!tfc:saws'), (4, item('lumber'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_stairs' % wood, ['X  ', 'XX ', 'XXX'], {'X': item('planks')}, (8, plank('stairs'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_slab' % wood, ['XXX'], {'X': item('planks')}, (6, plank('slab'))).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_planks' % wood, ['XX', 'XX'], {'X': item('lumber')}, item('planks')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_tool_rack' % wood, ['XXX', '   ', 'XXX'], {'X': item('lumber')}, plank('tool_rack')).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_trapdoor' % wood, ['XXX', 'XXX'], {'X': item('lumber')}, (3, plank('trapdoor'))).with_advancement(item('lumber'))
        rm.crafting_shaped('crafting/wood/%s_workbench' % wood, ['XX', 'XX'], {'X': item('planks')}, plank('workbench')).with_advancement(item('planks'))
        rm.crafting_shaped('crafting/wood/%s_pressure_plate' % wood, ['XX'], {'X': item('lumber')}, plank('pressure_plate')).with_advancement(item('lumber'))
        # todo: support, chests, boat

    rm.crafting_shaped('crafting/aggregate', ['XYX', 'Y Y', 'XYX'], {'X': 'tag!forge:sand', 'Y': 'tag!forge:gravel'}, (8, 'tfc:aggregate')).with_advancement('tag!forge:sand')
    damage_shapeless(rm, 'crafting/alabaster_brick', ('tfc:ore/gypsum', 'tag!tfc:chisels'), (4, 'tfc:alabaster_brick')).with_advancement('tfc:ore/gypsum')
    rm.crafting_shaped('crafting/alabaster_bricks', ['XYX', 'YXY', 'XYX'], {'X': 'tfc:alabaster_brick', 'Y': 'tag!tfc:mortar'}, (4, 'tfc:alabaster/raw/alabaster_bricks')).with_advancement('tfc:alabaster_brick')
    rm.crafting_shaped('crafting/bricks', ['XYX', 'YXY', 'XYX'], {'X': 'minecraft:brick', 'Y': 'tag!tfc:mortar'}, (2, 'minecraft:bricks')).with_advancement('minecraft:brick')
    rm.crafting_shaped('crafting/fire_bricks', ['XYX', 'YXY', 'XYX'], {'X': 'tfc:ceramic/fire_brick', 'Y': 'tag!tfc:mortar'}, (2, 'tfc:fire_bricks')).with_advancement('minecraft:brick')
    rm.crafting_shaped('crafting/fire_clay', ['XYX', 'YZY', 'XYX'], {'X': 'tfc:powder/kaolinite', 'Y': 'tfc:powder/graphite', 'Z': 'minecraft:clay_ball'}, 'tfc:fire_clay').with_advancement('tfc:powder/kaolinite')
    rm.crafting_shaped('crafting/fire_clay_block', ['XX', 'XX'], {'X': 'tfc:fire_clay'}, 'tfc:fire_clay_block').with_advancement('tfc:fire_clay')
    rm.crafting_shaped('crafting/firestarter', [' X', 'X '], {'X': 'tag!forge:rods/wooden'}, 'tfc:firestarter').with_advancement('tag!forge:rods/wooden')
    damage_shapeless(rm, 'crafting/flux', ('tag!tfc:fluxstone', 'tag!tfc:hammers'), (2, 'tfc:powder/flux')).with_advancement('tag!tfc:fluxstone')
    rm.crafting_shapeless('crafting/gunpowder', ('tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/sulfur', 'tfc:powder/charcoal'), (4, 'minecraft:gunpowder')).with_advancement('tfc:powder/sulfur')
    rm.crafting_shapeless('crafting/gunpowder_graphite', ('tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/saltpeter', 'tfc:powder/sulfur', 'tfc:powder/sulfur', 'tfc:powder/charcoal', 'tfc:powder/charcoal', 'tfc:powder/graphite'), (12, 'minecraft:gunpowder')).with_advancement('tfc:powder/graphite')
    rm.crafting_shaped('crafting/halter', ['XYX', 'X X'], {'X': 'minecraft:leather', 'Y': 'minecraft:lead'}, 'tfc:halter').with_advancement('minecraft:lead')
    rm.crafting_shaped('crafting/handstone', ['Y  ', 'XXX'], {'X': 'tag!forge:stone', 'Y': 'tag!forge:rods/wooden'}, 'tfc:handstone').with_advancement('tag!forge:stone')
    rm.crafting_shaped('crafting/jute_disc', [' X ', 'XXX', ' X '], {'X': 'tfc:jute_fiber'}, 'tfc:jute_disc').with_advancement('tfc:jute_fiber')
    rm.crafting_shaped('crafting/jute_net', ['X X', ' X ', 'X X'], {'X': 'tfc:jute_fiber'}, 'tfc:jute_net').with_advancement('tfc:jute_fiber')
    rm.crafting_shaped('crafting/lead', [' XX', ' XX', 'X  '], {'X': 'tfc:jute_fiber'}, 'minecraft:lead').with_advancement('tfc:jute_fiber')
    rm.crafting_shapeless('crafting/olive_jute_disc', ('tfc:jute_disc', 'tfc:olive_paste'), 'tfc:olive_jute_disc').with_advancement('tfc:jute_disc')
    rm.crafting_shaped('crafting/quern', ['XXX', 'YYY'], {'X': 'tag!forge:smooth_stone', 'Y': 'tag!forge:stone'}, 'tfc:quern').with_advancement('tag!forge:smooth_stone')
    rm.crafting_shaped('crafting/spindle', ['X', 'Y'], {'X': 'tfc:ceramic/spindle_head', 'Y': 'tag!forge:rods/wooden'}, 'tfc:spindle').with_advancement('tfc:ceramic/spindle_head')
    rm.crafting_shapeless('crafting/stick_from_bunch', 'tfc:stick_bunch', (9, 'minecraft:stick')).with_advancement('tfc:stick_bunch')
    rm.crafting_shapeless('crafting/stick_from_bundle', 'tfc:stick_bundle', (18, 'minecraft:stick')).with_advancement('tfc:stick_bundle')
    rm.crafting_shaped('crafting/stick_bunch', ['XXX', 'XXX', 'XXX'], {'X': 'tag!forge:rods/wooden'}, 'tfc:stick_bunch').with_advancement('tag!forge:rods/wooden')
    rm.crafting_shaped('crafting/stick_bundle', ['X', 'X'], {'X': 'tfc:stick_bunch'}, 'tfc:stick_bundle').with_advancement('tfc:stick_bunch')
    rm.crafting_shapeless('crafting/straw', 'tfc:thatch', (4, 'tfc:straw')).with_advancement('tfc:thatch')
    rm.crafting_shaped('crafting/thatch', ['XX', 'XX'], {'X': 'tfc:straw'}, 'tfc:thatch').with_advancement('tfc:straw')
    rm.crafting_shapeless('crafting/wool_yarn', ('tfc:spindle', 'tfc:wool'), (8, 'tfc:wool_yarn')).with_advancement('tfc:wool')
    # todo: bellows, bf, bloomery, goldpan, nestbox, paper, pkeg, salting, food combining, wooden bucket

    rm.crafting_shaped('crafting/vanilla/armor_stand', ['XXX', ' X ', 'XYX'], {'X': 'tag!minecraft:planks', 'Y': 'tag!forge:smooth_stone_slab'}, 'minecraft:armor_stand').with_advancement('tag!forge:smooth_stone_slab')
    rm.crafting_shaped('crafting/vanilla/armor_stand_bulk', ['X', 'Y'], {'X': 'tfc:stick_bunch', 'Y': 'tag!forge:smooth_stone_slab'}, 'minecraft:armor_stand').with_advancement('tag!forge:smooth_stone_slab')
    rm.crafting_shaped('crafting/vanilla/color/white_bed', ['XXX', 'YYY'], {'X': 'tag!tfc:high_quality_cloth', 'Y': 'tag!tfc:lumber'}, 'minecraft:white_bed').with_advancement('tag!tfc:high_quality_cloth')
    rm.crafting_shaped('crafting/vanilla/bucket', ['XRX', 'XBX', ' X '], {'X': 'tag!forge:ingots/wrought_iron', 'R': 'tfc:bucket/metal/red_steel', 'B': 'tfc:bucket/metal/blue_steel'}, 'minecraft:bucket').with_advancement('tfc:bucket/metal/red_steel')
    rm.crafting_shaped('crafting/vanilla/cauldron', ['X X', 'X X', 'XXX'], {'X': 'tag!forge:sheets/wrought_iron'}, 'minecraft:cauldron').with_advancement('tag!forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/compass', [' X ', 'XYX', ' X '], {'X': 'tag!forge:sheets/wrought_iron', 'Y': 'tag!forge:dusts/redstone'}, 'minecraft:compass').with_advancement('tag!forge:sheets/wrought_iron')
    rm.crafting_shapeless('crafting/vanilla/fire_charge', ('minecraft:gunpowder', 'tfc:firestarter', 'tag!minecraft:coals'), (3, 'minecraft:fire_charge')).with_advancement('minecraft:gunpowder')
    rm.crafting_shaped('crafting/vanilla/flint_and_steel', ['X ', ' Y'], {'X': 'tag!forge:ingots/steel', 'Y': 'minecraft:flint'}, 'minecraft:flint_and_steel').with_advancement('tag!forge:ingots/steel')
    rm.crafting_shapeless('crafting/vanilla/hay', 'minecraft:hay_block', (9, 'tfc:straw')).with_advancement('minecraft:hay_block')
    rm.crafting_shaped('crafting/vanilla/hay_bale', ['XXX', 'XXX', 'XXX'], {'X': 'tfc:straw'}, 'minecraft:hay_block').with_advancement('tfc:straw')
    rm.crafting_shaped('crafting/vanilla/item_frame', ['XXX', 'XYX', 'XXX'], {'X': 'tag!tfc:lumber', 'Y': 'minecraft:leather'}, (4, 'minecraft:item_frame')).with_advancement('minecraft:leather')
    rm.crafting_shaped('crafting/vanilla/ladder', ['X X', 'X X', 'X X'], {'X': 'tag!tfc:lumber'}, (16, 'minecraft:ladder')).with_advancement('tag!tfc:lumber')
    rm.crafting_shaped('crafting/vanilla/lapis_block', ['XXX', 'XXX', 'XXX'], {'X': 'tfc:gem/lapis_lazuli'}, 'minecraft:lapis_block').with_advancement('tfc:gem/lapis_lazuli')
    rm.crafting_shaped('crafting/vanilla/name_tag', ['XX', 'XY', 'XX'], {'X': 'minecraft:string', 'Y': 'minecraft:paper'}, 'minecraft:name_tag')
    rm.crafting_shaped('crafting/vanilla/painting', ['XXX', 'XYX', 'XXX'], {'X': 'tag!tfc:high_quality_cloth', 'Y': 'tag!forge:rods/wooden'}, 'minecraft:painting').with_advancement('tag!tfc:high_quality_cloth')
    rm.crafting_shaped('crafting/vanilla/tnt', ['XYX', 'YXY', 'XYX'], {'X': 'minecraft:gunpowder', 'Y': 'minecraft:sand'}, 'minecraft:tnt').with_advancement('minecraft:gunpowder')

    # todo: daylight sensor, redstone lamp,
    rm.crafting_shaped('crafting/vanilla/redstone/hopper', ['X X', ' Y '], {'X': 'tag!forge:sheets/wrought_iron', 'Y': 'tag!forge:chests/wooden'}, 'minecraft:hopper').with_advancement('tag!forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/observer', ['CCC', 'RRB', 'CCC'], {'C': 'tag!forge:cobblestone', 'R': 'tag!forge:dusts/redstone', 'B': 'tfc:brass_mechanisms'}, 'minecraft:observer').with_advancement('tfc:brass_mechanisms')
    rm.crafting_shaped('crafting/vanilla/redstone/piston', ['WWW', 'SXS', 'SBS'], {'X': 'tag!forge:rods/wrought_iron', 'S': 'tag!forge:cobblestone', 'W': 'tag!tfc:lumber', 'B': 'tfc:brass_mechanisms'}, 'minecraft:piston').with_advancement('tfc:brass_mechanisms')
    rm.crafting_shaped('crafting/vanilla/redstone/comparator', [' T ', 'TRT', 'SSS'], {'R': 'tag!forge:dusts/redstone', 'T': 'minecraft:redstone_torch', 'S': 'tag!forge:smooth_stone'}, 'minecraft:comparator').with_advancement('minecraft:redstone_torch')
    rm.crafting_shaped('crafting/vanilla/redstone/repeater', ['TRT', 'SSS'], {'T': 'minecraft:redstone_torch', 'R': 'tag!forge:dusts/redstone', 'S': 'tag!forge:smooth_stone'}, 'minecraft:repeater').with_advancement('minecraft:redstone_torch')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_hopper', ['X X', ' Y '], {'X': 'tag!forge:sheets/steel', 'Y': 'tag!forge:chests/wooden'}, (2, 'minecraft:hopper')).with_advancement('tag!forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/heavy_weighted_pressure_plate', ['XX'], {'X': 'tag!forge:ingots/wrought_iron'}, 'minecraft:heavy_weighted_pressure_plate').with_advancement('tag!forge:ingots/wrought_iron')

    rm.crafting_shaped('crafting/vanilla/redstone/activator_rail', ['SRS', 'SWS', 'SRS'], {'S': 'tag!forge:rods/wrought_iron', 'W': 'minecraft:redstone_torch', 'R': 'tag!forge:rods/wooden'}, (4, 'minecraft:activator_rail')).with_advancement('tag!forge:rods/gold')
    rm.crafting_shaped('crafting/vanilla/redstone/detector_rail', ['S S', 'SWS', 'SRS'], {'S': 'tag!forge:rods/wrought_iron', 'W': 'tag!minecraft:stone_pressure_plates', 'R': 'tag!forge:dusts/redstone'}, (4, 'minecraft:detector_rail')).with_advancement('tag!forge:rods/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/minecart', ['X X', 'XXX'], {'X': 'tag!forge:sheets/wrought_iron'}, 'minecraft:minecart').with_advancement('tag!forge:sheets/wrought_iron')
    rm.crafting_shaped('crafting/vanilla/redstone/powered_rail', ['SWS', 'SRS', 'SWS'], {'S': 'tag!forge:rods/gold', 'W': 'tag!forge:rods/wooden', 'R': 'tag!forge:dusts/redstone'}, (8, 'minecraft:powered_rail')).with_advancement('tag!forge:rods/gold')
    rm.crafting_shaped('crafting/vanilla/redstone/rail', ['S S', 'SWS', 'S S'], {'W': 'tag!forge:rods/wooden', 'S': 'tag!forge:rods/wrought_iron'}, (8, 'minecraft:rail')).with_advancement('tag!forge:rods/wrought_iron')

    rm.crafting_shaped('crafting/vanilla/redstone/steel_activator_rail', ['SRS', 'SWS', 'SRS'], {'S': 'tag!forge:rods/steel', 'W': 'minecraft:redstone_torch', 'R': 'tag!forge:rods/wooden'}, (8, 'minecraft:activator_rail')).with_advancement('tag!forge:rods/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_detector_rail', ['S S', 'SWS', 'SRS'], {'S': 'tag!forge:rods/steel', 'W': 'tag!minecraft:stone_pressure_plates', 'R': 'tag!forge:rods/wooden'}, (8, 'minecraft:detector_rail')).with_advancement('tag!forge:rods/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_minecart', ['X X', 'XXX'], {'X': 'tag!forge:sheets/steel'}, (2, 'minecraft:minecart')).with_advancement('tag!forge:sheets/steel')
    rm.crafting_shaped('crafting/vanilla/redstone/steel_rail', ['S S', 'SWS', 'S S'], {'W': 'tag!forge:rods/wooden', 'S': 'tag!forge:rods/steel'}, (16, 'minecraft:rail')).with_advancement('tag!forge:rods/steel')

    # Heat Recipes
    heat_recipe(rm, 'torch_from_stick', 'tag!forge:rods/wooden', 60, result_item=(2, 'tfc:torch'))
    heat_recipe(rm, 'torch_from_stick_bunch', 'tfc:stick_bunch', 60, result_item=(18, 'tfc:torch'))
    heat_recipe(rm, 'glass_from_shards', 'tfc:glass_shard', 180, result_item='minecraft:glass')
    heat_recipe(rm, 'glass_from_sand', 'tag!forge:sand', 180, result_item='minecraft:glass')
    heat_recipe(rm, 'brick', 'tfc:ceramic/unfired_brick', 1500, result_item='minecraft:brick')
    heat_recipe(rm, 'flower_pot', 'tfc:ceramic/unfired_flower_pot', 1500, result_item='minecraft:flower_pot')
    heat_recipe(rm, 'ceramic_jug', 'tfc:ceramic/unfired_jug', 1500, result_item='tfc:ceramic/jug')
    heat_recipe(rm, 'terracotta', 'minecraft:clay', 1200, result_item='minecraft:terracotta')

    for color in COLORS:
        heat_recipe(rm, 'glazed_terracotta_%s' % color, 'minecraft:%s_terracotta' % color, 1200, result_item='minecraft:%s_glazed_terracotta' % color)
        rm.crafting_shapeless('crafting/ceramic/%s_unfired_vessel' % color, ('minecraft:%s_dye' % color, 'tfc:ceramic/unfired_vessel'), 'tfc:ceramic/%s_unfired_vessel' % color).with_advancement('minecraft:%s_dye' % color)
        if color != 'white':
            rm.crafting_shaped('crafting/vanilla/color/%s_bed' % color, ['ZZZ', 'XXX', 'YYY'], {'X': 'tag!tfc:high_quality_cloth', 'Y': 'tag!tfc:lumber', 'Z': 'minecraft:%s_dye' % color}, 'minecraft:%s_bed' % color).with_advancement('tag!tfc:high_quality_cloth')
        rm.crafting_shapeless('crafting/vanilla/color/%s_concrete_powder' % color, ('minecraft:%s_dye' % color, 'tag!minecraft:sand', 'tag!minecraft:sand', 'tag!minecraft:sand', 'tag!minecraft:sand', 'tag!forge:gravel', 'tag!forge:gravel', 'tag!forge:gravel', 'tag!forge:gravel'), (8, 'minecraft:%s_concrete_powder' % color))

    # Quern
    quern_recipe(rm, 'olive', 'tfc:food/olive', 'tfc:olive_paste')
    quern_recipe(rm, 'borax', 'tfc:ore/borax', 'tfc:powder/flux', count=6)
    quern_recipe(rm, 'fluxstone', 'tag!tfc:fluxstone', 'tfc:powder/flux', count=2)
    quern_recipe(rm, 'cinnabar', 'tfc:ore/cinnabar', 'minecraft:redstone', count=8)
    quern_recipe(rm, 'cryolite', 'tfc:ore/cryolite', 'minecraft:redstone', count=8)
    quern_recipe(rm, 'bone', 'minecraft:bone', 'minecraft:bone_meal', count=3)
    quern_recipe(rm, 'bone_block', 'minecraft:bone_block', 'minecraft:bone_meal', count=9)
    quern_recipe(rm, 'charcoal', 'minecraft:charcoal', 'tfc:powder/charcoal', count=4)
    quern_recipe(rm, 'salt', 'tfc:ore/halite', 'tfc:powder/salt', count=4)
    quern_recipe(rm, 'blaze_rod', 'minecraft:blaze_rod', 'minecraft:blaze_powder', count=2)
    quern_recipe(rm, 'raw_limestone', 'tfc:rock/raw/limestone', 'tfc:ore/gypsum')
    quern_recipe(rm, 'sylvite', 'tfc:ore/sylvite', 'tfc:powder/fertilizer', count=4)

    for grain in GRAINS:
        heat_recipe(rm, grain + '_dough', 'tfc:food/%s_dough' % grain, 200, result_item='tfc:food/%s_bread' % grain)
        quern_recipe(rm, grain + '_grain', 'tfc:food/%s_grain' % grain, 'tfc:food/%s_flour' % grain)

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

    for pottery in PAIRED_POTTERY:
        heat_recipe(rm, 'fired_' + pottery, 'tfc:ceramic/unfired_' + pottery, 1500, result_item='tfc:ceramic/' + pottery)

    for i, size in enumerate(('small', 'medium', 'large')):
        scraping_recipe(rm, '%s_soaked_hide' % size, 'tfc:%s_soaked_hide' % size, 'tfc:%s_scraped_hide' % size)
        damage_shapeless(rm, 'crafting/%s_sheepskin' % size, ('tfc:%s_sheepskin_hide' % size, 'tag!tfc:knives'), (i + 1, 'tfc:wool')).with_advancement('tfc:%s_sheepskin_hide' % size)

    # todo: actual pot recipes
    rm.recipe(('pot', 'fresh_from_salt_water'), 'tfc:pot_fluid', {
        'ingredients': [utils.ingredient('minecraft:gunpowder')],
        'fluid_ingredient': fluid_ingredient('tfc:salt_water', 1000),
        'duration': 200,
        'temperature': 300,
        'fluid_output': fluid_stack('minecraft:water', 1000)
    })

    rm.recipe(('pot', 'mushroom_soup'), 'tfc:pot_soup', {
        'ingredients': [utils.ingredient('minecraft:red_mushroom'), utils.ingredient('minecraft:brown_mushroom')],
        'fluid_ingredient': fluid_ingredient('minecraft:water', 1000),
        'duration': 200,
        'temperature': 300
    })

    knapping_recipe(rm, 'clay_knapping', 'vessel', [' XXX ', 'XXXXX', 'XXXXX', 'XXXXX', ' XXX '], 'tfc:ceramic/unfired_vessel')
    knapping_recipe(rm, 'clay_knapping', 'jug', [' X   ', 'XXXX ', 'XXX X', 'XXXX ', 'XXX  '], 'tfc:ceramic/unfired_jug')
    knapping_recipe(rm, 'clay_knapping', 'pot', ['X   X', 'X   X', 'X   X', 'XXXXX', ' XXX '], 'tfc:ceramic/unfired_pot')
    knapping_recipe(rm, 'clay_knapping', 'bowl_2', ['X   X', ' XXX '], 'tfc:ceramic/unfired_bowl', count=2, outside_slot_required=False)
    knapping_recipe(rm, 'clay_knapping', 'bowl_4', ['X   X', ' XXX ', '     ', 'X   X', ' XXX '], 'tfc:ceramic/unfired_bowl', count=4)
    knapping_recipe(rm, 'clay_knapping', 'brick', ['XXXXX', '     ', 'XXXXX', '     ', 'XXXXX'], 'tfc:ceramic/unfired_brick', count=3)
    knapping_recipe(rm, 'clay_knapping', 'flower_pot', [' X X ', ' XXX ', '     ', ' X X ', ' XXX '], 'tfc:ceramic/unfired_flower_pot', count=2)
    knapping_recipe(rm, 'clay_knapping', 'spindle_head', ['  X  ', 'XXXXX', '  X  '], 'tfc:ceramic/unfired_spindle_head', outside_slot_required=False)
    # todo: large vessel, molds

    knapping_recipe(rm, 'leather_knapping', 'helmet', ['XXXXX', 'X   X', 'X   X', '     ', '     '], 'minecraft:leather_helmet')
    knapping_recipe(rm, 'leather_knapping', 'chestplate', ['X   X', 'XXXXX', 'XXXXX', 'XXXXX', 'XXXXX'], 'minecraft:leather_chestplate')
    knapping_recipe(rm, 'leather_knapping', 'leggings', ['XXXXX', 'XXXXX', 'XX XX', 'XX XX', 'XX XX'], 'minecraft:leather_leggings')
    knapping_recipe(rm, 'leather_knapping', 'boots', ['XX   ', 'XX   ', 'XX   ', 'XXXX ', 'XXXXX'], 'minecraft:leather_boots')
    knapping_recipe(rm, 'leather_knapping', 'saddle', ['  X  ', 'XXXXX', 'XXXXX', 'XXXXX', '  X  '], 'minecraft:saddle')
    # todo: quiver

    knapping_recipe(rm, 'fire_clay_knapping', 'crucible', ['X   X', 'X   X', 'X   X', 'X   X', 'XXXXX'], 'tfc:ceramic/unfired_crucible')
    knapping_recipe(rm, 'fire_clay_knapping', 'brick', ['XXXXX', '     ', 'XXXXX', '     ', 'XXXXX'], 'tfc:ceramic/unfired_fire_brick', count=3)

    for category in ROCK_CATEGORIES:
        predicate = 'tag!tfc:%s_rock' % category
        rock_knapping_recipe(rm, 'axe_head_%s' % category, [' X   ', 'XXXX ', 'XXXXX', 'XXXX ', ' X   '], 'tfc:stone/axe_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'shovel_head_%s' % category, ['XXX', 'XXX', 'XXX', 'XXX', ' X '], 'tfc:stone/shovel_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'hoe_head_%s' % category, ['XXXXX', '   XX'], 'tfc:stone/hoe_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'knife_head_%s' % category, ['X ', 'XX', 'XX', 'XX', 'XX'], 'tfc:stone/knife_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'knife_head_1_%s' % category, ['X  X ', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], 'tfc:stone/knife_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'knife_head_2_%s' % category, ['X   X', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], 'tfc:stone/knife_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'knife_head_3_%s' % category, [' X X ', 'XX XX', 'XX XX', 'XX XX', 'XX XX'], 'tfc:stone/knife_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'hoe_head_1_%s' % category, ['XXXXX', 'XX   ', '     ', 'XXXXX', 'XX   '], 'tfc:stone/hoe_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'hoe_head_2_%s' % category, ['XXXXX', 'XX   ', '     ', 'XXXXX', '   XX'], 'tfc:stone/hoe_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'knife_head_%s' % category, ['X ', 'XX', 'XX', 'XX', 'XX'], 'tfc:stone/knife_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'javelin_head_%s' % category, ['XXX  ', 'XXXX ', 'XXXXX', ' XXX ', '  X  '], 'tfc:stone/javelin_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'hammer_head_%s' % category, ['XXXXX', 'XXXXX', '  X  '], 'tfc:stone/hammer_head/%s' % category, predicate)

        for tool in ROCK_ITEMS:
            rm.crafting_shaped('crafting/stone/%s_%s' % (tool, category), ['X', 'Y'], {'X': 'tfc:stone/%s_head/%s' % (tool, category), 'Y': 'tag!forge:rods/wooden'}, 'tfc:stone/%s/%s' % (tool, category)).with_advancement('tfc:stone/%s_head/%s' % (tool, category))


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


def quern_recipe(rm: ResourceManager, name, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('quern', name), 'tfc:quern', {
        'ingredient': utils.ingredient(item),
        'result': utils.item_stack((count, result))
    })


def scraping_recipe(rm: ResourceManager, name, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('scraping', name), 'tfc:scraping', {
        'ingredient': utils.ingredient(item),
        'result': utils.item_stack((count, result))
    })


def heat_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, item: str, temperature: float, result_item: Optional[utils.Json] = None, result_fluid: Optional[str] = None, amount: int = 1000) -> RecipeContext:
    result_item = None if result_item is None else utils.item_stack(result_item)
    result_fluid = None if result_fluid is None else fluid_stack(result_fluid, amount)
    return rm.recipe(('heating', name_parts), 'tfc:heating', {
        'ingredient': utils.ingredient(item),
        'result_item': result_item,
        'result_fluid': result_fluid,
        'temperature': temperature
    })


def knapping_recipe(rm: ResourceManager, knap_type: str, name, pattern: List[str], item: str, count: int = 1, outside_slot_required: bool = None):
    return rm.recipe((knap_type, name), 'tfc:%s' % knap_type, {
        'matrix': {
            'outside_slot_required': outside_slot_required,
            'pattern': pattern
        },
        'result': utils.item_stack((count, item))
    })


def rock_knapping_recipe(rm: ResourceManager, name, pattern: List[str], item: str, ingredient: str = None, count: int = 1, outside_slot_required: bool = False):
    ingredient = None if ingredient is None else utils.ingredient(ingredient)
    return rm.recipe(('rock_knapping', name), 'tfc:rock_knapping', {
        'matrix': {
            'outside_slot_required': outside_slot_required,
            'pattern': pattern
        },
        'result': utils.item_stack((count, item)),
        'ingredient': ingredient
    })


def fluid_stack(fluid: str, amount: int) -> Dict[str, Any]:
    return {
        'fluid': fluid,
        'amount': amount
    }


def fluid_ingredient(fluid: str, amount: int = None) -> Dict[str, Any]:
    if fluid.startswith('#'):
        return {'tag': fluid[1:], 'amount': amount}
    else:
        return {'fluid': fluid, 'amount': amount}

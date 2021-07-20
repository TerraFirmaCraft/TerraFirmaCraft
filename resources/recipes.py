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

        rm.crafting_shaped('crafting/rock/%s_hardened' % rock, ['XMX', 'MXM', 'XMX'], {'X': raw, 'M': 'tag!tfc:mortar'}, (2, hardened)).with_advancement(raw)
        rm.crafting_shaped('crafting/rock/%s_bricks' % rock, ['XMX', 'MXM', 'XMX'], {'X': brick, 'M': 'tag!tfc:mortar'}, (4, bricks)).with_advancement(brick)

        damage_shapeless(rm, 'crafting/rock/%s_cracked' % rock, (bricks, 'tag!tfc:hammers'), cracked_bricks).with_advancement(bricks)

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
        rock_knapping_recipe(rm, 'hoe_head_1_%s' % category, ['XXXXX', 'XX   ', '     ', 'XXXXX', '   XX'], 'tfc:stone/hoe_head/%s' % category, predicate, count=2)
        rock_knapping_recipe(rm, 'knife_head_%s' % category, ['X ', 'XX', 'XX', 'XX', 'XX'], 'tfc:stone/knife_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'javelin_head_%s' % category, ['XXX ', 'XXXX ', 'XXXXX', ' XXX ', '  X  '], 'tfc:stone/javelin_head/%s' % category, predicate)
        rock_knapping_recipe(rm, 'hammer_head_%s' % category, ['XXXXX', 'XXXXX', '  X  '], 'tfc:stone/hammer_head/%s' % category, predicate)


def stone_cutting(rm: ResourceManager, name_parts: utils.ResourceIdentifier, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('stonecutting', name_parts), 'minecraft:stonecutting', {
        'ingredient': utils.ingredient(item),
        'result': result,
        'count': count
    })


def damage_shapeless(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: utils.Json, result: utils.Json, group: str = None, conditions: utils.Json = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:damage_inputs_crafting',
        'recipe': {
            'type': 'minecraft:crafting_shapeless',
            'group': group,
            'ingredients': utils.item_stack_list(ingredients),
            'result': utils.item_stack(result),
            'conditions': utils.recipe_condition(conditions)
        }
    })
    return RecipeContext(rm, res)


def quern_recipe(rm: ResourceManager, name, item: str, result: str, count: int = 1) -> RecipeContext:
    return rm.recipe(('quern', name), 'tfc:quern', {
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
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'result': utils.item_stack((count, item))
    })


def rock_knapping_recipe(rm: ResourceManager, name, pattern: List[str], item: str, predicate: str = None, count: int = 1, outside_slot_required: bool = False):
    predicate = None if predicate is None else utils.ingredient(predicate)
    return rm.recipe(('rock_knapping', name), 'tfc:rock_knapping', {
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'result': utils.item_stack((count, item)),
        'predicate': predicate
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

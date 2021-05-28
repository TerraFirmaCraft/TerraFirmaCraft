#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager, utils
from mcresources.recipe_context import RecipeContext

from constants import *


# Crafting recipes
def generate(rm: ResourceManager):
    def stone_cutting(name, item: str, result: str, count: int = 1) -> RecipeContext:
        return rm.recipe(('stonecutting', name), 'minecraft:stonecutting', {
            'ingredient': utils.ingredient(item),
            'result': result,
            'count': count
        })

    def damage_shapeless(name_parts: utils.ResourceIdentifier, ingredients: utils.Json, result: utils.Json, group: str = None, conditions: utils.Json = None) -> RecipeContext:
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

    def heat_recipe(name, item: str, result: str, count: int = 1, temperature: float = 1599, heat_capacity: float = 0) -> RecipeContext:
        if heat_capacity > 0:
            rm.data(('tfc', 'item_heats', name), {
                'ingredient': utils.ingredient(item),
                'heat_capacity': heat_capacity
            })
        return rm.recipe(('heating', name), 'tfc:heating', {
            'ingredient': utils.ingredient(item),
            'result': utils.item_stack((count, result)),
            'temperature': temperature
        })

    def pot_recipe(name: utils.ResourceIdentifier, ingredients: List[str], outputs: List[str], fluid_in: str, fluid_out: str, duration: int, temperature: float) -> RecipeContext:
        res = utils.resource_location(rm.domain, name)
        rm.recipe(('simple_pot', name), 'tfc:simple_pot', {
            'ingredients': [utils.ingredient(i) for i in ingredients],
            'outputs': [utils.item_stack(i) for i in outputs],
            'fluidInput': fluid_stack(fluid_in, 1000),
            'fluidOutput': fluid_stack(fluid_out, 1000),
            'duration': duration,
            'temperature': temperature
        })
        return RecipeContext(rm, res)

    def fluid_stack(fluid: str, amount: int):
        return {'FluidName': fluid, 'Amount': amount}

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
            stone_cutting('rock/%s_%s_slab' % (rock, block_type), block, block + '_slab', 2).with_advancement(block)
            stone_cutting('rock/%s_%s_stairs' % (rock, block_type), block, block + '_stairs', 1).with_advancement(block)
            stone_cutting('rock/%s_%s_wall' % (rock, block_type), block, block + '_wall', 1).with_advancement(block)

        # Other variants
        damage_shapeless('crafting/rock/%s_smooth' % rock, (raw, 'tag!tfc:chisels'), smooth).with_advancement(raw)
        damage_shapeless('crafting/rock/%s_brick' % rock, (loose, 'tag!tfc:chisels'), brick).with_advancement(loose)
        damage_shapeless('crafting/rock/%s_chiseled' % rock, (smooth, 'tag!tfc:chisels'), chiseled).with_advancement(smooth)

        rm.crafting_shaped('crafting/rock/%s_hardened' % rock, ['XMX', 'MXM', 'XMX'], {'X': raw, 'M': 'tag!tfc:mortar'}, (2, hardened)).with_advancement(raw)
        rm.crafting_shaped('crafting/rock/%s_bricks' % rock, ['XMX', 'MXM', 'XMX'], {'X': brick, 'M': 'tag!tfc:mortar'}, (4, bricks)).with_advancement(brick)

        damage_shapeless('crafting/rock/%s_cracked' % rock, (bricks, 'tag!tfc:hammers'), cracked_bricks).with_advancement(bricks)

    heat_recipe('stick', 'tag!forge:rods/wooden', 'tfc:torch', count=2, temperature=40, heat_capacity=0.1)
    heat_recipe('stick_bunch', 'tfc:stick_bunch', 'minecraft:torch', count=18, temperature=60, heat_capacity=0.2)
    heat_recipe('glass_shard', 'tfc:glass_shard', 'minecraft:glass', temperature=600, heat_capacity=1.0)
    heat_recipe('sand', 'tag!forge:sand', 'minecraft:glass', temperature=600, heat_capacity=1.0)
    heat_recipe('unfired_brick', 'tfc:ceramic/unfired_brick', 'minecraft:brick', heat_capacity=1.1)
    heat_recipe('unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', 'minecraft:flower_pot', heat_capacity=1.0)
    heat_recipe('unfired_jug', 'tfc:ceramic/unfired_jug', 'tfc:ceramic/jug', heat_capacity=1.0)
    # todo: crucible
    heat_recipe('clay_block', 'minecraft:clay', 'minecraft:terracotta', temperature=600, heat_capacity=1.0)
    for color in COLORS:
        heat_recipe('terracotta_%s' % color, 'minecraft:%s_terracotta' % color, 'minecraft:%s_glazed_terracotta' % color, temperature=1200, heat_capacity=1.0)
    for pottery in PAIRED_POTTERY:
        heat_recipe(pottery, 'tfc:ceramic/' + pottery, 'tfc:ceramic/unfired_' + pottery, heat_capacity=1.0)

    pot_recipe('test', ['tfc:jute', 'tfc:jute', 'tfc:straw'], ['tfc:glue', 'tfc:glass_shard'], 'minecraft:water', 'tfc:salt_water', 200, 500)

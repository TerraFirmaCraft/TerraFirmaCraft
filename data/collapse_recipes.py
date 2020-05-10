from mcresources import ResourceManager

from data.constants import *


def generate(rm: ResourceManager):
    # Generates all collapse recipes, including adding blocks to the required tags
    for rock in ROCKS:
        raw = 'tfc:rock/raw/%s' % rock
        cobble = 'tfc:rock/cobble/%s' % rock

        # Raw rock can TRIGGER and START, and FALL into cobble
        rm.block_tag('can_trigger_collapse', raw)
        rm.block_tag('can_start_collapse', raw)
        rm.recipe(('collapse', '%s_cobble' % rock), 'tfc:collapse', {
            'ingredient': [
                raw,
                *['tfc:ore/%s/%s' % (ore, rock) for ore in ORES]
            ],
            'result': cobble
        })

    # todo: add proper support blocks.
    # use below as template
    rm.data(('tfc', 'supports', 'scaffolding'), {
        'ingredient': 'minecraft:oak_log[axis=y]',
        'support_up': 3,
        'support_down': 1,
        'support_horizontal': 4
    })

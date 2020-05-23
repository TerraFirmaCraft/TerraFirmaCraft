from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    """ Handles all landslide and collapse recipes, including the relevant tags """

    for rock in ROCKS:
        raw = 'tfc:rock/raw/%s' % rock
        cobble = 'tfc:rock/cobble/%s' % rock
        gravel = 'tfc:rock/gravel/%s' % rock

        # Raw rock can TRIGGER and START, and FALL into cobble
        # Ores can FALL into cobble
        rm.block_tag('can_trigger_collapse', raw)
        rm.block_tag('can_start_collapse', raw)
        rm.block_tag('can_collapse', raw)
        rm.recipe(('collapse', '%s_cobble' % rock), 'tfc:collapse', {
            'ingredient': [
                cobble, raw, 'tfc:rock/mossy_cobble/%s' % rock,
                *['tfc:ore/%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if not ore_data.graded],
                *['tfc:ore/poor_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded],
                *['tfc:ore/normal_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded],
                *['tfc:ore/rich_%s/%s' % (ore, rock) for ore, ore_data in ORES.items() if ore_data.graded]
            ],
            'result': cobble
        })

        for ore, ore_data in ORES.items():
            if ore_data.graded:
                for grade in ORE_GRADES.keys():
                    rm.block_tag('can_start_collapse', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
                    rm.block_tag('can_collapse', 'tfc:ore/%s_%s/%s' % (grade, ore, rock))
            else:
                rm.block_tag('can_start_collapse', 'tfc:ore/%s/%s' % (ore, rock))
                rm.block_tag('can_collapse', 'tfc:ore/%s/%s' % (ore, rock))

        # Gravel and cobblestone both have collapse, and landslide recipes
        rm.block_tag('can_collapse', cobble)
        rm.block_tag('can_landslide', cobble)
        rm.recipe(('landslide', 'cobble_%s' % rock), 'tfc:landslide', {
            'ingredient': cobble,
            'result': cobble
        })

        rm.block_tag('can_collapse', 'tfc:rock/spike/%s' % rock)
        rm.recipe(('collapse', '%s_spike' % rock), 'tfc:collapse', {
            'ingredient': 'tfc:rock/spike/%s' % rock,
            'copy_input': True
        })

        rm.block_tag('can_collapse', gravel)
        rm.block_tag('can_landslide', gravel)
        rm.recipe(('collapse', 'gravel_%s' % rock), 'tfc:collapse', {
            'ingredient': gravel,
            'result': gravel
        })
        rm.recipe(('landslide', 'gravel_%s' % rock), 'tfc:landslide', {
            'ingredient': gravel,
            'result': gravel
        })

    # todo: add proper support blocks.
    # use below as template
    rm.data(('tfc', 'supports', 'scaffolding'), {
        'ingredient': 'minecraft:glass',
        'support_up': 1,
        'support_down': 1,
        'support_horizontal': 4
    })

    # All soil block types can landslide
    for variant in SOIL_BLOCK_VARIANTS:
        for block_type in SOIL_BLOCK_TYPES:
            rm.block_tag('can_landslide', 'tfc:%s/%s' % (block_type, variant))
        rm.recipe(('landslide', 'dirt_%s' % variant), 'tfc:landslide', {
            'ingredient': ['tfc:%s/%s' % (block_type, variant) for block_type in SOIL_BLOCK_TYPES],
            'result': 'tfc:dirt/%s' % variant
        })

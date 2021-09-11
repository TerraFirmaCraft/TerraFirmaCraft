#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    """ Handles all landslide and collapse recipes, including the relevant tags """

    def collapse(name: str, ingredient, result=None, copy_input: Optional[bool] = None):
        if result is None and not copy_input:
            raise RuntimeError('This is probably wrong: %s has result = None and copy_input = False' % name)
        rm.recipe(('collapse', name), 'tfc:collapse', {
            'ingredient': ingredient,
            'result': result,
            'copy_input': copy_input
        })

    def landslide(name: str, ingredient, result):
        rm.recipe(('landslide', name), 'tfc:landslide', {
            'ingredient': ingredient,
            'result': result
        })

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

        collapse('%s_cobble' % rock, [
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

        landslide('%s_cobble' % rock, cobble, cobble)
        landslide('%s_mossy_cobble' % rock, mossy_cobble, mossy_cobble)
        landslide('%s_gravel' % rock, gravel, gravel)

        # Spikes can collapse, but produce nothing
        rm.block_tag('can_collapse', spike)
        collapse('%s_spike' % rock, spike, copy_input=True)

    # Soil Blocks
    for variant in SOIL_BLOCK_VARIANTS:
        for block_type in SOIL_BLOCK_TYPES:
            rm.block_tag('can_landslide', 'tfc:%s/%s' % (block_type, variant))

        # Blocks that create normal dirt
        landslide('%s_dirt' % variant, ['tfc:%s/%s' % (block_type, variant) for block_type in ('dirt', 'grass', 'grass_path', 'farmland')], 'tfc:dirt/%s' % variant)
        landslide('%s_clay_dirt' % variant, ['tfc:%s/%s' % (block_type, variant) for block_type in ('clay', 'clay_grass')], 'tfc:clay/%s' % variant)

    # Sand
    for variant in SAND_BLOCK_TYPES:
        rm.block_tag('can_landslide', 'tfc:sand/%s' % variant)
        landslide('%s_sand' % variant, 'tfc:sand/%s' % variant, 'tfc:sand/%s' % variant)

    # Vanilla landslide blocks
    for block in ('sand', 'red_sand', 'gravel', 'cobblestone', 'mossy_cobblestone'):
        rm.block_tag('can_landslide', 'minecraft:%s' % block)
        landslide('vanilla_%s' % block, 'minecraft:%s' % block, 'minecraft:%s' % block)

    vanilla_dirt_landslides = ('grass_block', 'dirt', 'coarse_dirt', 'podzol')
    for block in vanilla_dirt_landslides:
        rm.block_tag('can_landslide', 'minecraft:%s' % block)
    landslide('vanilla_dirt', ['minecraft:%s' % block for block in vanilla_dirt_landslides], 'minecraft:dirt')

    # Vanilla collapsible blocks
    for rock in ('stone', 'andesite', 'granite', 'diorite'):
        block = 'minecraft:%s' % rock
        rm.block_tag('can_trigger_collapse', block)
        rm.block_tag('can_start_collapse', block)
        rm.block_tag('can_collapse', block)

        collapse('vanilla_%s' % rock, block, block if rock != 'stone' else 'minecraft:cobblestone')

#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    for vein_name, vein in ORE_VEINS.items():
        if vein.ore in ORES:
            rocks = expand_rocks(vein.rocks, vein_name)
            ore = ORES[vein.ore]  # standard ore
            if ore.graded:  # graded ore vein
                rm.data(('tfc', 'veins', vein_name), {
                    'type': 'tfc:' + vein.type,
                    'rarity': vein.rarity,
                    'min_y': vein.min_y,
                    'max_y': vein.max_y,
                    'size': vein.size,
                    'density': vein.density,
                    'blocks': [{
                        'stone': 'tfc:rock/raw/%s' % rock,
                        'ore': [{
                            'weight': vein.poor,
                            'block': 'tfc:ore/poor_%s/%s' % (vein.ore, rock)
                        }, {
                            'weight': vein.normal,
                            'block': 'tfc:ore/normal_%s/%s' % (vein.ore, rock)
                        }, {
                            'weight': vein.rich,
                            'block': 'tfc:ore/rich_%s/%s' % (vein.ore, rock)
                        }]
                    } for rock in rocks]
                })
            else:  # non-graded ore vein (mineral)
                rm.data(('tfc', 'veins', vein_name), {
                    'type': 'tfc:' + vein.type,
                    'rarity': vein.rarity,
                    'min_y': vein.min_y,
                    'max_y': vein.max_y,
                    'size': vein.size,
                    'density': vein.density,
                    'blocks': [{
                        'stone': 'tfc:rock/raw/%s' % rock,
                        'ore': 'tfc:ore/%s/%s' % (vein.ore, rock)
                    } for rock in rocks]
                })
        else:
            if vein_name == 'clay':
                blocks = [{
                    'stone': 'tfc:dirt/%s' % variant,
                    'ore': 'tfc:clay_dirt/%s' % variant
                } for variant in SOIL_BLOCK_VARIANTS] + [{
                    'stone': 'tfc:grass/%s' % variant,
                    'ore': 'tfc:clay_grass/%s' % variant
                } for variant in SOIL_BLOCK_VARIANTS]
            elif vein_name == 'peat':
                blocks = [{
                    'stone': ['tfc:dirt/%s' % variant for variant in SOIL_BLOCK_VARIANTS],
                    'ore': 'tfc:peat'
                }] + [{
                    'stone': ['tfc:grass/%s' % variant for variant in SOIL_BLOCK_VARIANTS],
                    'ore': 'tfc:peat_grass'
                }]
            elif vein_name == 'gravel':
                rocks = expand_rocks(vein.rocks, vein_name)
                blocks = [{
                    'stone': 'tfc:rock/raw/%s' % rock,
                    'ore': 'tfc:rock/gravel/%s' % rock
                } for rock in rocks]
            else:
                raise RuntimeError('Unknown vein name %s, data gen not know what to do. big sad.' % vein_name)
            rm.data(('tfc', 'veins', vein_name), {
                'type': 'tfc:' + vein.type,
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density,
                'blocks': blocks
            })


def expand_rocks(rocks_list: List, path: str) -> List[str]:
    rocks = []
    for rock_spec in rocks_list:
        if rock_spec in ROCKS:
            rocks.append(rock_spec)
        elif rock_spec in ROCK_CATEGORIES:
            rocks += [r for r, d in ROCKS.items() if d.category == rock_spec]
        else:
            raise RuntimeError('Unknown rock or rock category specification: %s at %s' % (rock_spec, path))
    return rocks

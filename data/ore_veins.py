from mcresources import ResourceManager

from data.constants import *


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
                            'block': 'tfc:ore/%s/%s[grade=poor]' % (vein.ore, rock)
                        }, {
                            'weight': vein.normal,
                            'block': 'tfc:ore/%s/%s[grade=normal]' % (vein.ore, rock)
                        }, {
                            'weight': vein.rich,
                            'block': 'tfc:ore/%s/%s[grade=rich]' % (vein.ore, rock)
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
        elif vein.rocks == ['soil']:  # clay or peat discs
            rm.data(('tfc', 'veins', vein_name), {
                'type': 'tfc:' + vein.type,
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density,
                'blocks': [{
                    'stone': ['tfc:dirt/%s' % variant for variant in STANDARD_SOIL_BLOCK_VARIANTS],
                    'ore': 'tfc:dirt/%s' % vein.ore
                }] + [{
                    'stone': ['tfc:grass/%s' % variant for variant in STANDARD_SOIL_BLOCK_VARIANTS],
                    'ore': 'tfc:grass/%s' % vein.ore
                }]
            })
        elif vein.ore == 'gravel':  # Not an ore, but still spawns per rock type
            rocks = expand_rocks(vein.rocks, vein_name)
            rm.data(('tfc', 'veins', vein_name), {
                'type': 'tfc:' + vein.type,
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density,
                'blocks': [{
                    'stone': 'tfc:rock/raw/%s' % rock,
                    'ore': 'tfc:rock/gravel/%s' % rock
                } for rock in rocks]
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

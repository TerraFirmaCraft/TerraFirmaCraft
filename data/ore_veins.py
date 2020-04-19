from mcresources import ResourceManager

from data.constants import *


def generate(rm: ResourceManager):
    for vein_name, vein in ORE_VEINS.items():
        ore = ORES[vein.ore]
        rocks = expand_rocks(vein.rocks, vein_name)
        if ore.graded:
            for grade in ORE_GRADES:
                rm.data(vein_name, {
                    'group': 'tfc:%s' % vein_name,
                    'rarity': vein.rarity,
                    'min_y': vein.min_y,
                    'max_y': vein.max_y,
                    'size': vein.size,
                    'density': vein.density,
                    'blocks': [{
                        'stone': 'tfc:rock/raw/%s' % rock,
                        'ore': 'tfc:ore/%s/%s[grade=%s]' % (vein_name, rock, grade)
                    } for rock in rocks]
                }, root_domain='tfc/veins')
        else:
            rm.data(vein_name, {
                'rarity': vein.rarity,
                'min_y': vein.min_y,
                'max_y': vein.max_y,
                'size': vein.size,
                'density': vein.density,
                'blocks': [{
                    'stone': 'tfc:rock/raw/%s' % rock,
                    'ore': 'tfc:ore/%s/%s' % (vein_name, rock)
                } for rock in rocks]
            }, root_domain='tfc/veins')


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

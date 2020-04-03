from typing import List

from mcresources import *

from data.constants import *


def generate(rm: ResourceManager):
    for ore, ore_data in ORES.items():
        rocks = expand_rocks(ore_data['rocks'], ore)
        if ore_data['graded']:
            for grade in ORE_GRADES:
                rm.data(ore, {  # todo: all of this data
                    'group': 'tfc:%s' % ore,
                    'rarity': 1 + ore_data['rarity'],
                    'min_y': 0,
                    'max_y': 1,
                    'size': ore_data['size'],
                    'density': 0,
                    'blocks': [{
                        'stone': 'tfc:rock/raw/%s' % rock,
                        'ore': 'tfc:ore/%s/%s[grade=%s]' % (ore, rock, grade)
                    } for rock in rocks]
                }, root_domain=('tfc', 'veins'))
        else:
            rm.data(ore, {  # todo: all of this data
                'rarity': 1 + ore_data['rarity'],
                'min_y': 0,
                'max_y': 1,
                'size': ore_data['size'],
                'density': 0,
                'blocks': [{
                    'stone': 'tfc:rock/raw/%s' % rock,
                    'ore': 'tfc:ore/%s/%s' % (ore, rock)
                } for rock in rocks]
            }, root_domain=('tfc', 'veins'))


def expand_rocks(rocks_list: List, path: str) -> List[str]:
    rocks = []
    for rock_spec in rocks_list:
        if rock_spec in ROCKS:
            rocks.append(rock_spec)
        elif rock_spec in ROCK_CATEGORIES:
            rocks += [r for r in ROCKS if r['category'] == rock_spec]
        else:
            raise RuntimeError('Unknown rock or rock category specification: %s at %s' % (rock_spec, path))
    return rocks

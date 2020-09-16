#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import os
from typing import Optional

from nbtlib import nbt
from nbtlib.tag import String

TREES = {
    'acacia': 'acacia',
    'ash': 'normal',
    'aspen': 'aspen',
    'birch': 'aspen',
    'blackwood': 'tall',
    'chestnut': 'normal',
    'douglas_fir': 'fir',
    'hickory': 'fir',
    'maple': 'normal',
    'oak': 'tall',
    'palm': 'tropical',
    'pine': 'fir',
    'rosewood': 'tall',
    'sequoia': 'conifer',
    'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'willow',
    'kapok': 'jungle'
}

LARGE_TREES = {
    'acacia': 'kapok_large',  # Use kapok structure as they are closer
    'ash': 'normal_large',
    # 'aspen': 'aspen',  # larger trunk done in code
    # 'birch': 'aspen',  # larger trunk done in code
    # 'blackwood': 'tall_large',  # todo: needs rework
    'chestnut': 'normal_large',
    'douglas_fir': 'fir_large',
    'hickory': 'fir_large',
    'maple': 'normal_large',
    # 'oak': 'tall_large',  # todo: needs rework
    # 'palm': 'tropical',  # larger trunk done in code
    'pine': 'fir_large',
    # 'rosewood': 'tall_large',  # todo: needs rework
    'sequoia': 'conifer_large',
    'spruce': 'conifer_large',
    'sycamore': 'normal_large',
    # 'white_cedar': 'tall_large',  # todo: needs rework
    # 'willow': 'willow_large',  # todo: need templates
    # 'kapok': 'jungle'  # No large variant, all trees are varied heights. todo: need more varied heights (structures)
}


def main():
    for wood, key in TREES.items():
        make_tree_variant(wood, key)

    for wood, key in LARGE_TREES.items():
        make_tree_variant(wood, key)


def make_tree_variant(wood: str, variant: str):
    if variant == 'normal':
        make_tree_structure('normal', wood, 'base')
        make_tree_structure('normal_overlay', wood, 'overlay')
    elif variant == 'normal_large':
        for i in range(1, 1 + 5):
            make_tree_structure('normal_large%d' % i, wood, str(i), wood + '_large')
    elif variant == 'tall':
        make_tree_structure('tall', wood, 'base')
        make_tree_structure('tall_overlay', wood, 'overlay')
    elif variant == 'tall_large':
        make_tree_structure('tall_large', wood, 'base', wood + '_large')
        make_tree_structure('tall_large_overlay', wood, 'overlay', wood + '_large')
    elif variant == 'acacia':
        for i in range(1, 1 + 35):
            make_tree_structure('acacia%d' % i, wood, str(i))
    elif variant == 'tropical':
        for i in range(1, 1 + 7):
            make_tree_structure('tropical%d' % i, wood, str(i))
    elif variant == 'willow':
        for i in range(1, 1 + 7):
            make_tree_structure('willow%d' % i, wood, str(i))
    elif variant == 'jungle':
        for i in range(1, 1 + 7):
            make_tree_structure('jungle%d' % i, wood, str(i))
    elif variant == 'conifer':
        for i in range(1, 1 + 9):
            make_tree_structure('conifer%d' % i, wood, str(i))
    elif variant == 'conifer_large':
        for i in range(1, 1 + 3):
            for struct in ('base', 'mid', 'top'):
                make_tree_structure('sequoia_%s%d' % (struct, i), wood, struct + str(i), wood + '_large')
    elif variant == 'fir':
        for i in range(1, 1 + 9):
            make_tree_structure('fir%d' % i, wood, str(i))
    elif variant == 'fir_large':
        for i in range(1, 1 + 5):
            make_tree_structure('fir_large%d' % i, wood, str(i), wood + '_large')
    elif variant == 'kapok_large':
        for i in range(1, 1 + 6):
            make_tree_structure('kapok_large%d' % i, wood, str(i), wood + '_large')
    elif variant == 'aspen':
        for i in range(1, 1 + 16):
            make_tree_structure('aspen%d' % i, wood, str(i))
    else:
        raise NotImplementedError(variant)


def make_tree_structure(template: str, wood: str, dest: Optional[str] = None, wood_dir: Optional[str] = None):
    if dest is None:
        dest = template
    if wood_dir is None:
        wood_dir = wood

    f = nbt.load('./structure_templates/%s.nbt' % template)
    for block in f.root['palette']:
        if block['Name'] == 'minecraft:oak_log':
            block['Name'] = String('tfc:wood/log/%s' % wood)
        elif block['Name'] == 'minecraft:oak_wood':
            block['Name'] = String('tfc:wood/wood/%s' % wood)
        elif block['Name'] == 'minecraft:oak_leaves':
            block['Name'] = String('tfc:wood/leaves/%s' % wood)
            block['Properties']['persistent'] = String('false')

    result_dir = '../src/main/resources/data/tfc/structures/%s/' % wood_dir
    if not os.path.exists(result_dir):
        os.makedirs(result_dir)
    f.save(result_dir + dest + '.nbt')


if __name__ == '__main__':
    main()

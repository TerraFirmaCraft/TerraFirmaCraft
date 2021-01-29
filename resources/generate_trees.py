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
    'blackwood': 'blackwood',
    'chestnut': 'normal',
    'douglas_fir': 'fir',
    'hickory': 'fir',
    'maple': 'normal',
    'oak': 'tall',  # todo: change out for another model
    'palm': 'tropical',
    'pine': 'fir',
    'rosewood': 'tall',
    'sequoia': 'conifer',
    'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'white_cedar',
    'willow': 'willow',
    'kapok': 'jungle'
}

LARGE_TREES = {
    'acacia': 'kapok_large',
    'ash': 'normal_large',
    'blackwood': 'blackwood_large',
    'chestnut': 'normal_large',
    'douglas_fir': 'fir_large',
    'hickory': 'fir_large',
    'maple': 'normal_large',
    # todo: better large oaks
    'pine': 'fir_large',
    'sequoia': 'conifer_large',
    'spruce': 'conifer_large',
    'sycamore': 'normal_large',
    'white_cedar': 'tall',
    'willow': 'willow_large'
}


def main():
    for wood, key in TREES.items():
        make_tree_variant(wood, key, False)

    for wood, key in LARGE_TREES.items():
        make_tree_variant(wood, key, True)

    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (Count.NEW, Count.MODIFIED, Count.SKIPPED, Count.ERRORS))


def make_tree_variant(wood: str, variant: str, large: bool):
    result = wood + '_large' if large else wood
    if variant == 'normal':  # Close to vanilla structure but not quite
        make_tree_structure('normal', wood, 'base', result)
        make_tree_structure('normal_overlay', wood, 'overlay', result)
    elif variant == 'normal_large':
        for i in range(1, 1 + 5):
            make_tree_structure('normal_large%d' % i, wood, str(i), result)
    elif variant == 'white_cedar':  # This is a smaller version of the 'tall' structure
        make_tree_structure('white_cedar', wood, 'base', result)
        make_tree_structure('white_cedar_overlay', wood, 'overlay', result)
    elif variant == 'tall':
        make_tree_structure('tall', wood, 'base', result)
        make_tree_structure('tall_overlay', wood, 'overlay', result)
    elif variant == 'tall_large':
        make_tree_structure('tall_large', wood, 'base', result)
        make_tree_structure('tall_large_overlay', wood, 'overlay', result)
    elif variant == 'acacia':
        for i in range(1, 1 + 35):
            make_tree_structure('acacia%d' % i, wood, str(i), result)
    elif variant == 'tropical':
        for i in range(1, 1 + 7):
            make_tree_structure('tropical%d' % i, wood, str(i), result)
    elif variant == 'willow':
        for i in range(1, 1 + 7):
            make_tree_structure('willow%d' % i, wood, str(i), result)
    elif variant == 'jungle':
        for i in range(1, 1 + 17):
            make_tree_structure('jungle%d' % i, wood, str(i), result)
    elif variant == 'conifer':
        for i in range(1, 1 + 9):
            make_tree_structure('conifer%d' % i, wood, str(i), result)
    elif variant == 'conifer_large':
        for i in range(1, 1 + 3):
            for layer in (1, 2, 3):
                make_tree_structure('conifer_large_layer%d_%d' % (layer, i), wood, 'layer%d_%d' % (layer, i), result)
    elif variant == 'fir':
        for i in range(1, 1 + 9):
            make_tree_structure('fir%d' % i, wood, str(i), result)
    elif variant == 'fir_large':
        for i in range(1, 1 + 5):
            make_tree_structure('fir_large%d' % i, wood, str(i), result)
    elif variant == 'kapok_large':
        for i in range(1, 1 + 6):
            make_tree_structure('kapok_large%d' % i, wood, str(i), result)
    elif variant == 'aspen':
        for i in range(1, 1 + 16):
            make_tree_structure('aspen%d' % i, wood, str(i), result)
    elif variant == 'willow_large':
        for i in range(1, 1 + 14):
            make_tree_structure('willow_large%d' % i, wood, str(i), result)
    elif variant == 'blackwood':
        for i in range(1, 1 + 10):
            make_tree_structure('blackwood%d' % i, wood, str(i), result)
    elif variant == 'blackwood_large':
        for i in range(1, 1 + 10):
            make_tree_structure('blackwood_large%d' % i, wood, str(i), result)
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
        else:
            print('Structure: %s has an invalid block state \'%s\'' % (template, block['Name']))

    result_dir = '../src/main/resources/data/tfc/structures/%s/' % wood_dir
    if not os.path.exists(result_dir):
        os.makedirs(result_dir)

    file_name = result_dir + dest + '.nbt'
    try:
        if os.path.isfile(file_name):
            # Load and diff the original file - do not overwrite if source identical to avoid unnecessary git diffs due to gzip inconsistencies.
            original = nbt.load(file_name)
            if original == f:
                Count.SKIPPED += 1
                return
            else:
                Count.MODIFIED += 1
        else:
            Count.NEW += 1
        f.save(result_dir + dest + '.nbt')
    except Exception:
        Count.ERRORS += 1


class Count:  # global mutable variables that doesn't require using the word "global" :)
    SKIPPED = 0
    NEW = 0
    MODIFIED = 0
    ERRORS = 0


if __name__ == '__main__':
    main()

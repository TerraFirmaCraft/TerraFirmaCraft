#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import os
from typing import Optional

from nbtlib import nbt
from nbtlib.tag import String

TREES = {
    'acacia': 'acacia',
    'ash': 'normal',
    'aspen': 'normal',
    'birch': 'normal',
    'blackwood': 'tall',
    'chestnut': 'normal',
    'douglas_fir': 'tall',
    'hickory': 'normal',
    'maple': 'normal',
    'oak': 'tall',
    'palm': 'tropical',
    'pine': 'conifer',
    'rosewood': 'tall',
    'sequoia': 'conifer',
    'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'willow',
    'kapok': 'jungle'
}


def main():
    for wood, key in TREES.items():

        if key == 'normal':
            make_tree('normal', wood, 'base')
            make_tree('normal_overlay', wood, 'overlay')
        elif key == 'tall':
            make_tree('tall', wood, 'base')
            make_tree('tall_overlay', wood, 'overlay')
        elif key == 'acacia':
            for i in range(1, 1 + 35):
                make_tree('acacia%d' % i, wood, str(i))
        elif key == 'tropical':
            for i in range(1, 1 + 7):
                make_tree('tropical%d' % i, wood, str(i))
        elif key == 'willow':
            for i in range(1, 1 + 7):
                make_tree('willow%d' % i, wood, str(i))
        elif key == 'jungle':
            for i in range(1, 1 + 7):
                make_tree('jungle%d' % i, wood, str(i))
        elif key == 'conifer':
            for i in range(1, 1 + 7):
                make_tree('conifer%d' % i, wood, str(i))


def make_tree(template: str, wood: str, dest: Optional[str] = None):
    if dest is None:
        dest = template

    f = nbt.load('./structure_templates/%s.nbt' % template)
    for block in f.root['palette']:
        if block['Name'] == 'minecraft:oak_log':
            block['Name'] = String('tfc:wood/log/%s' % wood)
        elif block['Name'] == 'minecraft:oak_wood':
            block['Name'] = String('tfc:wood/wood/%s' % wood)
        elif block['Name'] == 'minecraft:oak_leaves':
            block['Name'] = String('tfc:wood/leaves/%s' % wood)
            block['Properties']['persistent'] = String('false')

    result_dir = '../src/main/resources/data/tfc/structures/%s/' % wood
    if not os.path.exists(result_dir):
        os.makedirs(result_dir)
    f.save(result_dir + dest + '.nbt')


if __name__ == '__main__':
    main()

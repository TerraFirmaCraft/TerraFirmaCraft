#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import os

from nbtlib import nbt
from nbtlib.tag import *


def main():
    os.chdir('../')

    for wood, key in TREES.items():
        # normal (vanilla oak)
        if key == 'normal':
            tree('resources/structure_templates/normal', wood, 'base')
            tree('resources/structure_templates/normal_overlay', wood, 'overlay')

        # tall (tfc douglas fir, but smaller)
        if key == 'tall':
            tree('resources/structure_templates/tall', wood, 'base')
            tree('resources/structure_templates/tall_overlay', wood, 'overlay')

        # tallXL (tfc douglas fir, full size-ish)
        if key == 'tallXL':
            tree('resources/structure_templates/tall2', wood, 'base')
            tree('resources/structure_templates/tall2_overlay', wood, 'overlay')

        # overhang (willow)
        if key == 'willow':
            tree('resources/structure_templates/willow', wood, 'base')
            tree('resources/structure_templates/willow_overlay', wood, 'overlay')

        # conifer (vanilla spruce)
        if key == 'conifer':
            for s in ['1', '2', '3', '4', '5', '6', '7']:
                tree('resources/structure_templates/conifer' + s, wood, s)

        # sequoia (large vanilla spruce kind of)
        if key == 'sequoia':
            for s in ['base', 'mid', 'top']:
                for t in ['1', '2', '3']:
                    tree('resources/structure_templates/conifer_large_' + s + t, wood, s + t)

        # acacia (vanilla acacia, bit bigger)
        if key == 'acacia':
            for s in ['1', '2', '3']:
                tree('resources/structure_templates/acacia_branch' + s, wood, 'branch' + s)

        # palm like trees
        if key == 'tropical':
            for s in ['1', '2', '3', '4', '5', '6', '7']:
                tree('resources/structure_templates/tropical' + s, wood, s)

        # kapok (vanilla jungle trees, but better) Also have a vanilla oak variant
        if key == 'jungle':
            for s in ['branch1', 'branch2', 'branch3', 'top']:
                tree('resources/structure_templates/jungle_' + s, wood, s)
            tree('resources/structure_templates/normal', wood, 'base')
            tree('resources/structure_templates/normal_overlay', wood, 'overlay')


def tree(origin, wood, name_out):
    f = nbt.load(origin + '.nbt')
    for block in f.root['palette']:

        if block['Name'] == 'minecraft:oak_log':
            block['Name'] = String('tfc:wood/log/' + wood)

        if block['Name'] == 'minecraft:oak_planks':  # Planks indicate bark blocks
            block['Name'] = String('tfc:wood/wood/' + wood)

        if block['Name'] == 'minecraft:oak_leaves':
            block['Name'] = String('tfc:wood/leaves/' + wood)

    if not os.path.exists('src/main/resources/data/tfc/structures/' + wood):
        os.makedirs('src/main/resources/data/tfc/structures/' + wood)
    f.save('src/main/resources/data/tfc/structures/' + wood + '/' + name_out + '.nbt')


def fruit_tree(name):
    f = nbt.load('structure_templates/fruit_tree_base.nbt')
    for block in f.root['palette']:
        if block['Name'] == 'tfc:fruit_trees/branch/peach':
            block['Name'] = String('tfc:fruit_trees/branch/' + name)
        elif block['Name'] == 'tfc:fruit_trees/leaves/peach':
            block['Name'] = String('tfc:fruit_trees/leaves/' + name)
        elif block['Name'] == 'tfc:fruit_trees/trunk/peach':
            block['Name'] = String('tfc:fruit_trees/trunk/' + name)

    if not os.path.exists('src/main/resources/data/tfc/structures/fruit_trees'):
        os.makedirs('src/main/resources/data/tfc/structures/fruit_trees')
    f.save('src/main/resources/data/tfc/structures/fruit_trees/' + name + '.nbt')


TREES = {
    'acacia': 'acacia',
    'ash': 'normal',
    'aspen': 'normal',
    'birch': 'normal',
    'blackwood': 'tall',
    'chestnut': 'normal',
    'douglas_fir': 'tallXL',
    'hickory': 'normal',
    'maple': 'normal',
    'oak': 'tallXL',
    'palm': 'tropical',
    'pine': 'conifer',
    'rosewood': 'tall',
    'sequoia': 'sequoia',
    'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'willow',
    'kapok': 'jungle'
}

FRUIT_TREES = [
    'banana',
    'cherry',
    'olive',
    'red_apple',
    'green_apple',
    'lemon',
    'orange',
    'peach',
    'plum'
]

if __name__ == '__main__':
    main()

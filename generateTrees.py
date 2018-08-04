import os

from nbtlib import nbt
from nbtlib.tag import *


def tree(origin, wood, nameout):
    f = nbt.load(origin + '.nbt')

    for block in f.root['palette']:

        if block['Name'] == 'minecraft:log':
            block['Name'] = String('tfc:wood/log/' + wood)
            prop = block['Properties']
            block['Properties'] = Compound({
                'small': String('false'),
                'placed': String('false'),
                'axis': prop['axis']
            })

        if block['Name'] == 'minecraft:leaves':
            block['Name'] = String('tfc:wood/leaves/' + wood)
            block['Properties'] = Compound({
                'check_decay': String('true'),
                'decayable': String('true')
            })
    if not os.path.exists('src/main/resources/assets/tfc/structures/' + wood):
        os.makedirs('src/main/resources/assets/tfc/structures/' + wood)
    f.save('src/main/resources/assets/tfc/structures/' + wood + '/' + nameout + '.nbt')


WOOD_TYPES = {
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

for wood, key in WOOD_TYPES.items():
    # normal (vanilla oak)
    if key == 'normal':
        tree('structure_templates/normal', wood, 'base')
        tree('structure_templates/normal_overlay', wood, 'overlay')

    # tall (tfc douglas fir, but smaller)
    if key == 'tall':
        tree('structure_templates/tall', wood, 'base')
        tree('structure_templates/tall_overlay', wood, 'overlay')

    # tallXL (tfc douglas fir, full size-ish)
    if key == 'tallXL':
        tree('structure_templates/large2_base', wood, 'base')
        tree('structure_templates/large2_overlay', wood, 'overlay')

    # overhang (willow)
    if key == 'willow':
        tree('structure_templates/w1', wood, 'base')
        tree('structure_templates/w2', wood, 'overlay')

    # conifer (vanilla spruce)
    if key == 'conifer':
        for s in ['1', '2', '3', '4', '5', '6', '7']:
            tree('structure_templates/conifer' + s, wood, s)

    # sequoia (large vanilla spruce kind of)
    if key == 'sequoia':
        for s in ['base', 'mid', 'top']:
            for t in ['1', '2', '3']:
                tree('structure_templates/conifer_large_' + s + t, wood, s + t)

    # acacia (vanilla acacia, bit bigger)
    if key == 'acacia':
        for s in ['1', '2', '3']:
            tree('structure_templates/acacia_branch' + s, wood, 'branch' + s)

    # palm like trees
    if key == 'tropical':
        for s in ['1', '2', '3', '4', '5', '6', '7']:
            tree('structure_templates/tropical' + s, wood, s)

    # kapok (vanilla jungle trees, but better) These also need to include a normal variant as well
    if key == 'jungle':
        for s in ['branch1', 'branch2', 'branch3', 'top']:
            tree('structure_templates/jungle_' + s, wood, s)
        tree('structure_templates/normal', wood, 'base')
        tree('structure_templates/normal_overlay', wood, 'overlay')

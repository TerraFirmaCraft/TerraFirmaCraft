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
                'placed': String('true'),
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
    'acacia': 'normal',
    'ash': 'normal',
    'aspen': 'normal',
    'birch': 'normal',
    'blackwood': 'normal',
    'chestnut': 'normal',
    'douglas_fir': 'tall',
    'hickory': 'normal',
    'maple': 'normal',
    'oak': 'normal',
    'palm': 'tropical',
    'pine': 'conifer',
    'rosewood': 'tall',
    'sequoia': 'sequoia',
    'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'willow',
    'kapok': 'normal'
}

for wood, key in WOOD_TYPES.items():
    # normal (vanilla oak)
    if key == 'normal':
        tree('structure_templates/normal', wood, 'base')
        tree('structure_templates/normal_overlay', wood, 'overlay')

    # tall (douglas fir)
    if key == 'tall':
        tree('structure_templates/tall', wood, 'base')
        tree('structure_templates/tall_overlay', wood, 'overlay')

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


    # palm like trees
    if key == 'tropical':
        for s in ['1', '2', '3', '4', '5', '6', '7']:
            tree('structure_templates/t' + s, wood, s)

    # todo: 2x2 coniferous trees (built in parts)
    # todo: acacia trees (vanilla style, but bigger?)
    # todo: kapok trees (vanilla style jungle, built in parts)

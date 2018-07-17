import os
from nbtlib import nbt
from nbtlib.tag import *

os.chdir('src/main/resources/assets/tfc/structures/')

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
    if not os.path.exists(wood):
        os.mkdir(wood)
    f.save(wood + '/' + nameout + '.nbt')


WOOD_TYPES = {
    'acacia': 'normal',
    'ash': 'normal',
    'aspen': 'normal',
    'birch': 'normal',
    'blackwood': 'normal'
    'chestnut': 'normal',
    'douglas_fir': 'tall',
    'hickory': 'normal',
    'maple': 'normal',
    'oak': 'normal',
'palm': 'palm'
        'pine': 'conifer',
                'rosewood': 'tall'
    'sequoia': 'normal',
               'spruce': 'conifer',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'normal',
'kapok': 'normal'
}

for wood, key in WOOD_TYPES.items():
    # normal
    if key == 'normal':
        tree('base/normal', wood, 'base')
        tree('base/normal_overlay', wood, 'overlay')
    # tall (douglas fir)
    if key == 'tall':
        tree('base/tall', wood, 'base')
        tree('base/tall_overlay', wood, 'overlay')
    # todo: palm trees
    if key == 'conifer':
        for s in ['1', '2', '3', '4', '5', '6', '7']:
            tree('base/conifer' + s, wood, s)
    # todo: 2x2 coniferous trees
    # todo: willow trees

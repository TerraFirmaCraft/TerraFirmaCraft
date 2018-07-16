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
    'ash': 'normal',
    'aspen': 'normal',
    'birch': 'normal',
    'chestnut': 'normal',
    'douglas_fir': 'tall',
    'hickory': 'normal',
    'maple': 'normal',
    'oak': 'normal',
    'pine': 'normal',
    'sequoia': 'normal',
    'spruce': 'normal',
    'sycamore': 'normal',
    'white_cedar': 'tall',
    'willow': 'normal',
    'kapok': 'normal',
    'acacia': 'normal',
    'rosewood': 'normal',
    'blackwood': 'tall',
    'palm': 'normal'
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

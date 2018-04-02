#!/bin/env python3

import json
import os

os.chdir("src/main/resources/assets/tfc/")

types = [
    'granite',
    'diorite',
    'gabbro',
    'shale',
    'claystone',
    'rocksalt',
    'limestone',
    'conglomerate',
    'dolomite',
    'chert',
    'chalk',
    'rhyolite',
    'basalt',
    'andesite',
    'dacite',
    'quartzite',
    'slate',
    'phyllite',
    'schist',
    'gneiss',
    'marble',
]
fullblock_types = [
    'raw',
    'smooth',
    'cobble',
    'brick',
    'sand',
    'gravel',
    'dirt',
]
grass_types = [
    'grass',
    'dry_grass',
]

for rock_type in types:
    for block_type in fullblock_types + grass_types:
        t = '{}_{}'.format(block_type, rock_type)
        with open('blockstates/%s.json' % t, 'w') as f:
            json.dump({'variants': {'normal': {'model': 'tfc:%s' % t}}}, f)
    for block_type in fullblock_types:
        t = '{}_{}'.format(block_type, rock_type)
        with open('models/item/%s.json' % t, 'w') as f:
            json.dump({'parent': 'tfc:block/%s' % t}, f)
        with open('models/block/%s.json' % t, 'w') as f:
            json.dump({'parent': 'block/cube_all', 'textures': {'all': 'tfc:blocks/stonetypes/%s' % t}}, f)

    with open('models/item/grass_%s.json' % rock_type, 'w') as f:
        json.dump({'parent': 'tfc:block/grass_%s' % rock_type}, f)
    with open('models/block/grass_{}.json'.format(rock_type), 'w') as f:
        json.dump({
            'parent': 'tfc:block/grass',
            'textures': {
                'all': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                'particle': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                'overlay': 'tfc:blocks/grass_side',
                'top': 'tfc:blocks/grass_top',
            }
        }, f)
    with open('models/item/dry_grass_%s.json' % rock_type, 'w') as f:
        json.dump({'parent': 'tfc:block/dry_grass_%s' % rock_type}, f)
    with open('models/block/dry_grass_{}.json'.format(rock_type), 'w') as f:
        json.dump({
            'parent': 'tfc:block/grass',
            'textures': {
                'all': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                'particle': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                'overlay': 'tfc:blocks/grass_dry_side',
                'top': 'tfc:blocks/grass_dry_top',
            }
        }, f)

#!/bin/env python3

# noinspection PyUnresolvedReferences
import json
# noinspection PyUnresolvedReferences
import os
# noinspection PyUnresolvedReferences
import time
# noinspection PyUnresolvedReferences
import zipfile


def zipfolder(zip_name, target_dir):
    zipobj = zipfile.ZipFile(zip_name, 'w', zipfile.ZIP_DEFLATED)
    rootlen = len(target_dir) + 1
    for base, dirs, files in os.walk(target_dir):
        for file in files:
            fn = os.path.join(base, file)
            zipobj.write(fn, fn[rootlen:])


if not os.path.isdir('assets_backups'):
    os.mkdir('assets_backups')
    with open('assets_backups/.gitignore', 'w') as f:
        print('*', file=f)

zipfolder('assets_backups/{}.zip'.format(int(time.time())), 'src/main/resources/assets/tfc')

os.chdir('src/main/resources/assets/tfc/')

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
ores = [
    'native_copper',
    'native_gold',
    'native_platinum',
    'hematite',
    'native_silver',
    'cassiterite',
    'galena',
    'bismuthinite',
    'garnierite',
    'malachite',
    'magnetite',
    'limonite',
    'sphalerite',
    'tetrahedrite',
    'bituminous_coal',
    'lignite',
    'kaolinite',
    'gypsum',
    'satinspar',
    'selenite',
    'graphite',
    'kimberlite',
    'petrified_wood',
    'sulfur',
    'jet',
    'microcline',
    'pitchblende',
    'cinnabar',
    'cryolite',
    'saltpeter',
    'serpentine',
    'sylvite',
    'borax',
    'olivine',
    'lapis_lazuli'
]

for rock_type in types:
    for block_type in fullblock_types:
        t = '{}_{}'.format(block_type, rock_type)
        with open('blockstates/%s.json' % t, 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'cube_all',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/%s' % t
                    }
                },
                'variants': {
                    'normal': [{}]
                }
            }, f)

    for block_type in ores:
        t = '{}_{}'.format(block_type, rock_type)
        with open('blockstates/%s.json' % t, 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'tfc:ore',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/raw_%s' % rock_type,
                        'particle': 'tfc:blocks/stonetypes/raw_%s' % rock_type,
                        'overlay': 'tfc:blocks/ores/%s' % block_type,
                    }
                },
                'variants': {
                    'normal': [{}]
                }
            }, f)

    for block_type in grass_types:
        t = '{}_{}'.format(block_type, rock_type)
        with open('blockstates/%s.json' % t, 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'tfc:grass',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                        'particle': 'tfc:blocks/stonetypes/dirt_%s' % rock_type,
                        'top': 'tfc:blocks/%s_top' % block_type,
                        'north': 'tfc:blocks/%s_side' % block_type,
                        'south': 'tfc:blocks/%s_side' % block_type,
                        'east': 'tfc:blocks/%s_side' % block_type,
                        'west': 'tfc:blocks/%s_side' % block_type,
                    }
                },
                'variants': {
                    side: [{}] if side is 'normal' else {
                        'true': {
                            'textures': {
                                side: 'tfc:blocks/%s_top' % block_type,
                            }
                        },
                        'false': {}
                    } for side in ['north', 'south', 'east', 'west', 'normal']
                }
            }, f)

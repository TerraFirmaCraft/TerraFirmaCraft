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
    'clay',
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
woods = [
    'ash',
    'aspen',
    'birch',
    'chestnut',
    'douglas_fir',
    'hickory',
    'maple',
    'oak',
    'pine',
    'sequoia',
    'spruce',
    'sycamore',
    'white_cedar',
    'willow',
    'kapok',
    'acacia',
    'rosewood',
    'blackwood',
    'palm'
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
    with open('blockstates/clay_grass_%s.json' % rock_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'tfc:grass',
                'textures': {
                    'all': 'tfc:blocks/stonetypes/clay_%s' % rock_type,
                    'particle': 'tfc:blocks/stonetypes/clay_%s' % rock_type,
                    'top': 'tfc:blocks/grass_top',
                    'north': 'tfc:blocks/grass_side',
                    'south': 'tfc:blocks/grass_side',
                    'east': 'tfc:blocks/grass_side',
                    'west': 'tfc:blocks/grass_side',
                }
            },
            'variants': {
                side: [{}] if side is 'normal' else {
                    'true': {
                        'textures': {
                            side: 'tfc:blocks/grass_top',
                        }
                    },
                    'false': {}
                } for side in ['north', 'south', 'east', 'west', 'normal']
            }
        }, f)

for wood_type in woods:
    with open('blockstates/log_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'cube_column',
                'textures': {
                    'particle': 'tfc:blocks/wood/log_%s' % wood_type,
                    'end': 'tfc:blocks/wood/top_%s' % wood_type,
                    'side': 'tfc:blocks/wood/log_%s' % wood_type,
                }
            },
            'variants': {
                'normal': [{}],
                'axis': {
                    'y': {},
                    'z': {'x': 90},
                    'x': {'x': 90, 'y': 90},
                    'none': {
                        'model': 'cube_all',
                        'textures': {
                            'all': 'tfc:blocks/wood/log_%s' % wood_type,
                        }
                    }
                }
            }
        }, f)

    with open('blockstates/planks_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'cube_all',
                'textures': {
                    'all': 'tfc:blocks/wood/planks_%s' % wood_type
                }
            },
            'variants': {
                'normal': [{}]
            }
        }, f)

    with open('blockstates/leaves_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'leaves',
                'textures': {
                    'all': 'tfc:blocks/wood/leaves_%s' % wood_type
                }
            },
            'variants': {
                'normal': [{}]
            }
        }, f)

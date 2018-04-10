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

rock_types = [
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
    'bricks',
    'sand',
    'gravel',
    'dirt',
    'clay',
]
grass_types = [
    'grass',
    'dry_grass',
]
ores = {
    'native_copper': True,
    'native_gold': True,
    'native_platinum': True,
    'hematite': True,
    'native_silver': True,
    'cassiterite': True,
    'galena': True,
    'bismuthinite': True,
    'garnierite': True,
    'malachite': True,
    'magnetite': True,
    'limonite': True,
    'sphalerite': True,
    'tetrahedrite': True,
    'bituminous_coal': False,
    'lignite': False,
    'kaolinite': False,
    'gypsum': False,
    'satinspar': False,
    'selenite': False,
    'graphite': False,
    'kimberlite': False,
    'petrified_wood': False,
    'sulfur': False,
    'jet': False,
    'microcline': False,
    'pitchblende': False,
    'cinnabar': False,
    'cryolite': False,
    'saltpeter': False,
    'serpentine': False,
    'sylvite': False,
    'borax': False,
    'olivine': False,
    'lapis_lazuli': False,
}
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
    'palm',
]

for rock_type in rock_types:
    for block_type in fullblock_types:
        with open('blockstates/%s_%s.json' % (block_type, rock_type), 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'cube_all',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type)
                    }
                },
                'variants': {
                    'normal': [{}]
                }
            }, f)

    for block_type in ores:
        with open('blockstates/%s_%s.json' % (block_type, rock_type), 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'tfc:ore',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/raw/%s' % rock_type,
                        'particle': 'tfc:blocks/stonetypes/raw/%s' % rock_type,
                        'overlay': 'tfc:blocks/ores/%s' % block_type,
                    }
                },
                'variants': {
                    'normal': [{}]
                }
            }, f)

    for block_type in grass_types:
        with open('blockstates/%s_%s.json' % (block_type, rock_type), 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'tfc:grass',
                    'textures': {
                        'all': 'tfc:blocks/stonetypes/dirt/%s' % rock_type,
                        'particle': 'tfc:blocks/stonetypes/dirt/%s' % rock_type,
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
                    'all': 'tfc:blocks/stonetypes/clay/%s' % rock_type,
                    'particle': 'tfc:blocks/stonetypes/clay/%s' % rock_type,
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

    for block_type in ['cobble', 'bricks']:
        with open('blockstates/wall_%s_%s.json' % (block_type, rock_type), 'w') as f:
            json.dump({
                'forge_marker': 1,
                'defaults': {
                    # 'transform': 'forge:default-item',
                    'model': 'tfc:empty',
                    'textures': {
                        'particle': 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type),
                        'wall': 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type),
                    },
                },
                'variants': {
                    # 'normal': [{}],
                    'inventory': {'model': 'wall_inventory'},
                    # 'variant': {'cobblestone': {}, 'mossy_cobblestone': {}},  # unused
                    'north': {
                        'true': {
                            'submodel': 'wall_side',
                        },
                        'false': {}
                    },
                    'east': {
                        'true': {
                            'submodel': 'wall_side',
                            'y': 90,
                        },
                        'false': {}
                    },
                    'south': {
                        'true': {
                            'submodel': 'wall_side',
                            'y': 180,
                        },
                        'false': {}
                    },
                    'west': {
                        'true': {
                            'submodel': 'wall_side',
                            'y': 270,
                        },
                        'false': {}
                    },
                    'up': {
                        'true': {
                            'submodel': 'wall_post',
                            'y': 270,
                        },
                        'false': {}
                    },
                },
            }, f)

for wood_type in woods:
    with open('blockstates/log_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'cube_column',
                'textures': {
                    'particle': 'tfc:blocks/wood/log/%s' % wood_type,
                    'end': 'tfc:blocks/wood/top/%s' % wood_type,
                    'side': 'tfc:blocks/wood/log/%s' % wood_type,
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
                            'all': 'tfc:blocks/wood/log/%s' % wood_type,
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
                    'all': 'tfc:blocks/wood/planks/%s' % wood_type
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
                    'all': 'tfc:blocks/wood/leaves/%s' % wood_type
                }
            },
            'variants': {
                'normal': [{}]
            }
        }, f)

    with open('blockstates/fence_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'fence_post',
                'textures': {
                    'texture': 'tfc:blocks/wood/planks/%s' % wood_type
                }
            },
            'variants': {
                'normal': [{}],
                'inventory': {'model': 'fence_inventory'},
                'north': {
                    'true': {
                        'submodel': 'fence_side',
                    },
                    'false': {}
                },
                'east': {
                    'true': {
                        'submodel': 'fence_side',
                        'y': 90,
                    },
                    'false': {}
                },
                'south': {
                    'true': {
                        'submodel': 'fence_side',
                        'y': 180,
                    },
                    'false': {}
                },
                'west': {
                    'true': {
                        'submodel': 'fence_side',
                        'y': 270,
                    },
                    'false': {}
                },
            }
        }, f)

    with open('blockstates/fence_gate_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'defaults': {
                # 'transform': 'forge:default-item',
                'model': 'fence_gate_closed',
                'textures': {
                    'texture': 'tfc:blocks/wood/planks/%s' % wood_type
                }
            },
            'variants': {
                'normal': [{}],
                'inventory': [{}],
                'facing': {
                    'south': {},
                    'west': {'y': 90},
                    'north': {'y': 180},
                    'east': {'y': 270},
                },
                'open': {
                    'true': {
                        'model': 'fence_gate_open',
                    },
                    'false': {}
                },
                'in_wall': {
                    'true': {
                        'transform': {
                            'translation': [0, -3/16, 0]
                        }
                    },
                    'false': {},
                },
            }
        }, f)
    with open('blockstates/sapling_%s.json' % wood_type, 'w') as f:
        json.dump({
            'forge_marker': 1,
            'variants': {
                'normal': {
                    'model': 'cross',
                    'textures': {
                        'cross': 'tfc:blocks/saplings/%s' % wood_type
                    }
                },
                'inventory': {
                    'model': 'builtin/generated',
                    'textures': {
                        'layer0': 'tfc:blocks/saplings/%s' % wood_type
                    },
                    'transform': 'forge:default-item',
                },
            },
        }, f)

for ore_type in ores:
    if ores[ore_type]:
        for grade in ['poor', 'rich']:
            with open('models/item/%s_ore_%s.json' % (grade, ore_type), 'w') as f:
                json.dump({
                    'parent': 'item/generated',
                    'textures': {
                        'layer0': 'tfc:items/ore/%s/%s' % (grade, ore_type)
                    },
                }, f)
    with open('models/item/ore_%s.json' % ore_type, 'w') as f:
        json.dump({
            'parent': 'item/generated',
            'textures': {
                'layer0': 'tfc:items/ore/%s' % ore_type
            },
        }, f)

for rock_type in rock_types:
    for item_type in ['rock', 'brick']:
        with open('models/item/%s_%s.json' % (item_type, rock_type), 'w') as f:
            json.dump({
                'parent': 'item/generated',
                'textures': {
                    'layer0': 'tfc:items/stonetypes/%s/%s' % (item_type, rock_type)
                },
            }, f)

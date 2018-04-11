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

ROCK_TYPES = [
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
FULLBLOCK_TYPES = [
    'raw',
    'smooth',
    'cobble',
    'bricks',
    'sand',
    'gravel',
    'dirt',
    'clay',
]
GRASS_TYPES = [
    'grass',
    'dry_grass',
]
ORE_TYPES = {
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
WOOD_TYPES = [
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


def del_none(d):
    """
    https://stackoverflow.com/a/4256027/4355781
    Modifies input!
    """
    for key, value in list(d.items()):
        if value is None:
            del d[key]
        elif isinstance(value, dict):
            del_none(value)
    return d


def blockstate(filename_parts, model, textures, variants=None):
    """
    Magic.
    :param filename_parts: Iterable of strings.
    :param model: String or None
    :param textures: Dict of <string>:<string> OR <iterable of strings>:<string>
    :param variants: Dict of <string>:<variant> OR "normal":None (to disable the normal default)
    """
    _variants = {
        'normal': [{}]
    }
    if variants:
        _variants.update(variants)

    _textures = {}
    for key, val in textures.items():
        if isinstance(key, str):
            _textures[key] = val
        else:
            for x in key:
                _textures[x] = val

    with open('blockstates/%s.json' % '_'.join(filename_parts), 'w') as file:
        json.dump(del_none({
            'forge_marker': 1,
            'defaults': {
                'model': model,
                'textures': _textures,
            },
            'variants': _variants,
        }), file)


def cube_all(filename_parts, texture, variants=None, model='cube_all'):
    blockstate(filename_parts, model, textures={'all': texture}, variants=variants)


def model(folder, filename_parts, parent, textures):
    with open('models/%s/%s.json' % (folder, '_'.join(filename_parts)), 'w') as file:
        json.dump(del_none({
            'parent': parent,
            'textures': textures,
        }), file)


def item(filename_parts, *layers):
    model('item', filename_parts, 'item/generated', {'layer%d' % i: v for i, v in enumerate(layers)})


# BLOCKSTATES

# ROCK STUFF
for rock_type in ROCK_TYPES:
    # FULL BLOCKS
    for block_type in FULLBLOCK_TYPES:
        cube_all((block_type, rock_type), 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type))

    # ORES
    for block_type in ORE_TYPES:
        blockstate((block_type, rock_type), 'tfc:ore', textures={
            ('all', 'particle'): 'tfc:blocks/stonetypes/raw/%s' % rock_type,
            'overlay': 'tfc:blocks/ores/%s' % block_type,
        })

    # GRASS
    for block_type in GRASS_TYPES:
        blockstate((block_type, rock_type), 'tfc:grass', textures={
            ('all', 'particle'): 'tfc:blocks/stonetypes/dirt/%s' % rock_type,
            'particle': 'tfc:blocks/stonetypes/dirt/%s' % rock_type,
            'top': 'tfc:blocks/%s_top' % block_type,
            ('north', 'south', 'east', 'west'): 'tfc:blocks/%s_side' % block_type,
        }, variants={
            side: {
                'true': {'textures': {side: 'tfc:blocks/%s_top' % block_type}},
                'false': {}
            } for side in ['north', 'south', 'east', 'west']
        })

    # CLAY GRASS
    blockstate(('clay_grass', rock_type), 'tfc:grass', textures={
        ('all', 'particle'): 'tfc:blocks/stonetypes/clay/%s' % rock_type,
        'top': 'tfc:blocks/grass_top',
        ('north', 'south', 'east', 'west'): 'tfc:blocks/grass_side',
    }, variants={
        side: {
            'true': {'textures': {side: 'tfc:blocks/grass_top'}},
            'false': {}
        } for side in ['north', 'south', 'east', 'west']
    })

    # WALLS (cobble & bricks only)
    for block_type in ['cobble', 'bricks']:
        blockstate(('wall', block_type, rock_type), 'tfc:empty', textures={
            ('wall', 'particle'): 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type),
        }, variants={
            'normal': None,
            'inventory': {'model': 'wall_inventory'},
            'north': {'true': {'submodel': 'wall_side'}, 'false': {}},
            'east': {'true': {'submodel': 'wall_side', 'y': 90}, 'false': {}},
            'south': {'true': {'submodel': 'wall_side', 'y': 180}, 'false': {}},
            'west': {'true': {'submodel': 'wall_side', 'y': 270}, 'false': {}},
            'up': {'true': {'submodel': 'wall_post', 'y': 270}, 'false': {}}
        })

# WOOD STUFF
for wood_type in WOOD_TYPES:
    # LOG BLOCKS
    blockstate(('log', wood_type), 'cube_column', textures={
        ('particle', 'side'): 'tfc:blocks/wood/log/%s' % wood_type,
        'end': 'tfc:blocks/wood/top/%s' % wood_type,
        'layer0': 'tfc:items/wood/log/%s' % wood_type,
    }, variants={
        'axis': {
            'y': {},
            'z': {'x': 90},
            'x': {'x': 90, 'y': 90},
            'none': {
                'textures': {'end': 'tfc:blocks/wood/log/%s' % wood_type}
            }
        },
        'small': {
            'true': {'model': 'tfc:small_log'},
            'false': {},
        }
    })

    # PLANKS BLOCKS
    cube_all(('planks', wood_type), 'tfc:blocks/wood/planks/%s' % wood_type)
    # LEAVES BLOCKS
    cube_all(('leaves', wood_type), 'tfc:blocks/wood/leaves/%s' % wood_type, model='leaves')

    # FENCES
    blockstate(('fence', wood_type), 'fence_post', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type
    }, variants={
        'inventory': {'model': 'fence_inventory'},
        'north': {'true': {'submodel': 'fence_side'}, 'false': {}},
        'east': {'true': {'submodel': 'fence_side', 'y': 90}, 'false': {}},
        'south': {'true': {'submodel': 'fence_side', 'y': 180}, 'false': {}},
        'west': {'true': {'submodel': 'fence_side', 'y': 270}, 'false': {}},
    })

    # FENCE GATES
    blockstate(('fence_gate', wood_type), 'fence_gate_closed', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type
    }, variants={
        'inventory': [{}],
        'facing': {
            'south': {},
            'west': {'y': 90},
            'north': {'y': 180},
            'east': {'y': 270},
        },
        'open': {'true': {'model': 'fence_gate_open'}, 'false': {}},
        'in_wall': {'true': {'transform': {'translation': [0, -3 / 16, 0]}}, 'false': {}},
    })

    # SAPLINGS
    blockstate(('sapling', wood_type), 'cross', textures={
        ('cross', 'layer0'): 'tfc:blocks/saplings/%s' % wood_type
    }, variants={
        'inventory': {
            'model': 'builtin/generated',
            'transform': 'forge:default-item'
        }
    })

    # There is no method to this madness. Don't even try.
    variants = {'normal': None,
                "facing=east,half=lower,hinge=left,open=false": {"model": "door_bottom"},
                "facing=south,half=lower,hinge=left,open=false": {"model": "door_bottom", "y": 90},
                "facing=west,half=lower,hinge=left,open=false": {"model": "door_bottom", "y": 180},
                "facing=north,half=lower,hinge=left,open=false": {"model": "door_bottom", "y": 270},
                "facing=east,half=lower,hinge=right,open=false": {"model": "door_bottom_rh"},
                "facing=south,half=lower,hinge=right,open=false": {"model": "door_bottom_rh", "y": 90},
                "facing=west,half=lower,hinge=right,open=false": {"model": "door_bottom_rh", "y": 180},
                "facing=north,half=lower,hinge=right,open=false": {"model": "door_bottom_rh", "y": 270},
                "facing=east,half=lower,hinge=left,open=true": {"model": "door_bottom_rh", "y": 90},
                "facing=south,half=lower,hinge=left,open=true": {"model": "door_bottom_rh", "y": 180},
                "facing=west,half=lower,hinge=left,open=true": {"model": "door_bottom_rh", "y": 270},
                "facing=north,half=lower,hinge=left,open=true": {"model": "door_bottom_rh"},
                "facing=east,half=lower,hinge=right,open=true": {"model": "door_bottom", "y": 270},
                "facing=south,half=lower,hinge=right,open=true": {"model": "door_bottom"},
                "facing=west,half=lower,hinge=right,open=true": {"model": "door_bottom", "y": 90},
                "facing=north,half=lower,hinge=right,open=true": {"model": "door_bottom", "y": 180},
                "facing=east,half=upper,hinge=left,open=false": {"model": "door_top"},
                "facing=south,half=upper,hinge=left,open=false": {"model": "door_top", "y": 90},
                "facing=west,half=upper,hinge=left,open=false": {"model": "door_top", "y": 180},
                "facing=north,half=upper,hinge=left,open=false": {"model": "door_top", "y": 270},
                "facing=east,half=upper,hinge=right,open=false": {"model": "door_top_rh"},
                "facing=south,half=upper,hinge=right,open=false": {"model": "door_top_rh", "y": 90},
                "facing=west,half=upper,hinge=right,open=false": {"model": "door_top_rh", "y": 180},
                "facing=north,half=upper,hinge=right,open=false": {"model": "door_top_rh", "y": 270},
                "facing=east,half=upper,hinge=left,open=true": {"model": "door_top_rh", "y": 90},
                "facing=south,half=upper,hinge=left,open=true": {"model": "door_top_rh", "y": 180},
                "facing=west,half=upper,hinge=left,open=true": {"model": "door_top_rh", "y": 270},
                "facing=north,half=upper,hinge=left,open=true": {"model": "door_top_rh"},
                "facing=east,half=upper,hinge=right,open=true": {"model": "door_top", "y": 270},
                "facing=south,half=upper,hinge=right,open=true": {"model": "door_top"},
                "facing=west,half=upper,hinge=right,open=true": {"model": "door_top", "y": 90},
                "facing=north,half=upper,hinge=right,open=true": {"model": "door_top", "y": 180}
                }
    blockstate(('door', wood_type), None, textures={
        'bottom': 'tfc:blocks/wood/door/lower/%s' % wood_type,
        'top': 'tfc:blocks/wood/door/upper/%s' % wood_type,
    }, variants=variants)

# ITEMS

# ORES
for ore_type in ORE_TYPES:
    if ORE_TYPES[ore_type]:
        for grade in ['poor', 'rich']:
            item((grade, 'ore', ore_type), 'tfc:items/ore/%s/%s' % (grade, ore_type))
    item(('ore', ore_type), 'tfc:items/ore/%s' % ore_type)

# ROCKS
for rock_type in ROCK_TYPES:
    for item_type in ['rock', 'brick']:
        item((item_type, rock_type), 'tfc:items/stonetypes/%s/%s' % (item_type, rock_type))

# DOORS
for wood_type in WOOD_TYPES:
    item(('log', wood_type), 'tfc:items/wood/log/%s' % wood_type)
    item(('door', wood_type), 'tfc:items/wood/door/%s' % wood_type)

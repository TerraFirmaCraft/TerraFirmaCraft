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
ROCK_CATEGORIES = [
    'sedimentary',
    'metamorphic',
    'igneous_intrusive',
    'igneous_extrusive',
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
GEM_TYPES = [
    'agate',
    'amethyst',
    'beryl',
    'diamond',
    'emerald',
    'garnet',
    'jade',
    'jasper',
    'opal',
    'ruby',
    'sapphire',
    'topaz',
    'tourmaline',
]
GEM_GRADES = [
    'normal',
    'flawed',
    'flawless',
    'chipped',
    'exquisite',
]
METAL_TYPES = {
    'bismuth': False,
    'bismuth_bronze': True,
    'black_bronze': True,
    'brass': False,
    'bronze': True,
    'copper': True,
    'gold': False,
    'lead': False,
    'nickel': False,
    'rose_gold': False,
    'silver': False,
    'tin': False,
    'zinc': False,
    'sterling_silver': False,
    'wrought_iron': True,
    'pig_iron': False,
    'steel': True,
    'platinum': False,
    'black_steel': True,
    'blue_steel': True,
    'red_steel': True,
}  # + unknown
METAL_ITEMS = {
    'unshaped': False,
    'ingot': False,
    'double_ingot': False,
    'scrap': False,
    'dust': False,
    'nugget': False,
    'sheet': False,
    'double_sheet': False,
    'anvil': True,
    'tuyere': True,
    'lamp': False,
    'pick': True,
    'pick_head': True,
    'shovel': True,
    'shovel_head': True,
    'axe': True,
    'axe_head': True,
    'hoe': True,
    'hoe_head': True,
    'chisel': True,
    'chisel_head': True,
    'sword': True,
    'sword_blade': True,
    'mace': True,
    'mace_head': True,
    'saw': True,
    'saw_blade': True,
    'javelin': True,
    'javelin_head': True,
    'hammer': True,
    'hammer_head': True,
    'propick': True,
    'propick_head': True,
    'knife': True,
    'knife_blade': True,
    'scythe': True,
    'scythe_blade': True,
    'unfinished_chestplate': True,
    'chestplate': True,
    'unfinished_greaves': True,
    'greaves': True,
    'unfinished_boots': True,
    'boots': True,
    'unfinished_helmet': True,
    'helmet': True,
}
HANDHELDS = [
    'pick', 'shovel', 'axe', 'hoe', 'chisel', 'sword', 'mace', 'saw', 'javelin', 'hammer', 'knife', 'scythe'
]
DOOR_VARIANTS = {
    'normal': None,
    'facing=east,half=lower,hinge=left,open=false': {'model': 'door_bottom'},
    'facing=south,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 90},
    'facing=west,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 180},
    'facing=north,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 270},
    'facing=east,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh'},
    'facing=south,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 90},
    'facing=west,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 180},
    'facing=north,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 270},
    'facing=east,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 90},
    'facing=south,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 180},
    'facing=west,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 270},
    'facing=north,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh'},
    'facing=east,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 270},
    'facing=south,half=lower,hinge=right,open=true': {'model': 'door_bottom'},
    'facing=west,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 90},
    'facing=north,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 180},
    'facing=east,half=upper,hinge=left,open=false': {'model': 'door_top'},
    'facing=south,half=upper,hinge=left,open=false': {'model': 'door_top', 'y': 90},
    'facing=west,half=upper,hinge=left,open=false': {'model': 'door_top', 'y': 180},
    'facing=north,half=upper,hinge=left,open=false': {'model': 'door_top', 'y': 270},
    'facing=east,half=upper,hinge=right,open=false': {'model': 'door_top_rh'},
    'facing=south,half=upper,hinge=right,open=false': {'model': 'door_top_rh', 'y': 90},
    'facing=west,half=upper,hinge=right,open=false': {'model': 'door_top_rh', 'y': 180},
    'facing=north,half=upper,hinge=right,open=false': {'model': 'door_top_rh', 'y': 270},
    'facing=east,half=upper,hinge=left,open=true': {'model': 'door_top_rh', 'y': 90},
    'facing=south,half=upper,hinge=left,open=true': {'model': 'door_top_rh', 'y': 180},
    'facing=west,half=upper,hinge=left,open=true': {'model': 'door_top_rh', 'y': 270},
    'facing=north,half=upper,hinge=left,open=true': {'model': 'door_top_rh'},
    'facing=east,half=upper,hinge=right,open=true': {'model': 'door_top', 'y': 270},
    'facing=south,half=upper,hinge=right,open=true': {'model': 'door_top'},
    'facing=west,half=upper,hinge=right,open=true': {'model': 'door_top', 'y': 90},
    'facing=north,half=upper,hinge=right,open=true': {'model': 'door_top', 'y': 180}
 }
STAIR_VARIANTS = {
    'normal': {'model': 'stairs'},
    'facing=east,half=bottom,shape=straight': {'model': 'stairs'},
    'facing=west,half=bottom,shape=straight': {'model': 'stairs', 'y': 180},
    'facing=south,half=bottom,shape=straight': {'model': 'stairs', 'y': 90},
    'facing=north,half=bottom,shape=straight': {'model': 'stairs', 'y': 270},
    'facing=east,half=bottom,shape=outer_right': {'model': 'outer_stairs'},
    'facing=west,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 180},
    'facing=south,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 90},
    'facing=north,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 270},
    'facing=east,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 270},
    'facing=west,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 90},
    'facing=south,half=bottom,shape=outer_left': {'model': 'outer_stairs'},
    'facing=north,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 180},
    'facing=east,half=bottom,shape=inner_right': {'model': 'inner_stairs'},
    'facing=west,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 180},
    'facing=south,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 90},
    'facing=north,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 270},
    'facing=east,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 270},
    'facing=west,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 90},
    'facing=south,half=bottom,shape=inner_left': {'model': 'inner_stairs'},
    'facing=north,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 180},
    'facing=east,half=top,shape=straight': {'model': 'stairs', 'x': 180},
    'facing=west,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 270},
    'facing=east,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 90},
    'facing=west,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 270},
    'facing=south,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 180},
    'facing=north,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180},
    'facing=east,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180},
    'facing=west,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 270},
    'facing=east,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 90},
    'facing=west,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 270},
    'facing=south,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 180},
    'facing=north,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180},
    'facing=east,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180},
    'facing=west,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 270}
}


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

    p = os.path.join('blockstates', *filename_parts) + '.json'
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as file:
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


def model(filename_parts, parent, textures):
    p = os.path.join('models', *filename_parts) + '.json'
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as file:
        json.dump(del_none({
            'parent': parent,
            'textures': textures,
        }), file)


def item(filename_parts, *layers, parent='item/generated'):
    model(('item', *filename_parts), parent, {'layer%d' % i: v for i, v in enumerate(layers)})


# BLOCKSTATES

# ROCK STUFF
for rock_type in ROCK_TYPES:
    # FULL BLOCKS
    for block_type in FULLBLOCK_TYPES:
        cube_all((block_type, rock_type), 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type))

    # ORES
    for block_type in ORE_TYPES:
        blockstate(('ore', block_type, rock_type), 'tfc:ore', textures={
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

    # (ROCK) STAIRS & SLABS
    for block_type in ['smooth', 'cobble', 'bricks']:
        blockstate(('stairs', block_type, rock_type), None, textures={
            ('top', 'bottom', 'side'): 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type),
        }, variants=STAIR_VARIANTS)
        blockstate(('slab', 'half', block_type, rock_type), 'half_slab', textures={
            ('top', 'bottom', 'side'): 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type),
        }, variants={
            'half': {
                'bottom': {},
                'top': {'model': 'upper_slab'}
            }
        })
        cube_all(('slab', 'full', block_type, rock_type), 'tfc:blocks/stonetypes/%s/%s' % (block_type, rock_type))


# WOOD STUFF
for wood_type in WOOD_TYPES:
    # LOG BLOCKS
    blockstate(('wood', 'log', wood_type), 'cube_column', textures={
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
    cube_all(('wood', 'planks', wood_type), 'tfc:blocks/wood/planks/%s' % wood_type)
    # LEAVES BLOCKS
    cube_all(('wood', 'leaves', wood_type), 'tfc:blocks/wood/leaves/%s' % wood_type, model='leaves')

    # FENCES
    blockstate(('wood', 'fence', wood_type), 'fence_post', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type
    }, variants={
        'inventory': {'model': 'fence_inventory'},
        'north': {'true': {'submodel': 'fence_side'}, 'false': {}},
        'east': {'true': {'submodel': 'fence_side', 'y': 90}, 'false': {}},
        'south': {'true': {'submodel': 'fence_side', 'y': 180}, 'false': {}},
        'west': {'true': {'submodel': 'fence_side', 'y': 270}, 'false': {}},
    })

    # FENCE GATES
    blockstate(('wood', 'fence_gate', wood_type), 'fence_gate_closed', textures={
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
    blockstate(('wood', 'sapling', wood_type), 'cross', textures={
        ('cross', 'layer0'): 'tfc:blocks/saplings/%s' % wood_type
    }, variants={
        'inventory': {
            'model': 'builtin/generated',
            'transform': 'forge:default-item'
        }
    })

    # DOORS
    blockstate(('wood', 'door', wood_type), None, textures={
        'bottom': 'tfc:blocks/wood/door/lower/%s' % wood_type,
        'top': 'tfc:blocks/wood/door/upper/%s' % wood_type,
    }, variants=DOOR_VARIANTS)

    # (WOOD) STAIRS & SLABS
    blockstate(('stairs', 'wood', wood_type), None, textures={
        ('top', 'bottom', 'side'): 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants=STAIR_VARIANTS)
    blockstate(('slab', 'half', 'wood', wood_type), 'half_slab', textures={
        ('top', 'bottom', 'side'): 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants={
        'half': {
            'bottom': {},
            'top': {'model': 'upper_slab'}
        }
    })
    cube_all(('slab', 'full', 'wood', wood_type), 'tfc:blocks/wood/planks/%s' % wood_type)

# ITEMS

# ORES
for ore_type in ORE_TYPES:
    if ORE_TYPES[ore_type]:
        for grade in ['poor', 'rich', 'small']:
            item(('ore', grade, ore_type), 'tfc:items/ore/%s/%s' % (grade, ore_type))
    item(('ore', 'normal', ore_type), 'tfc:items/ore/%s' % ore_type)

# ROCKS
for rock_type in ROCK_TYPES:
    for item_type in ['rock', 'brick']:
        item((item_type, rock_type), 'tfc:items/stonetypes/%s/%s' % (item_type, rock_type))

# DOORS
for wood_type in WOOD_TYPES:
    item(('wood', 'log', wood_type), 'tfc:items/wood/log/%s' % wood_type)
    item(('wood', 'door', wood_type), 'tfc:items/wood/door/%s' % wood_type)

# GEMS
for gem in GEM_TYPES:
    for grade in GEM_GRADES:
        item(('gem', grade, gem), 'tfc:items/gem/%s/%s' % (grade, gem))

# METALS
for item_type, tool_item in METAL_ITEMS.items():
    for metal, tool_metal in METAL_TYPES.items():
        if tool_item and not tool_metal:
            continue
        parent = 'item/handheld' if item_type in HANDHELDS else 'item/generated'
        if item_type in ['knife', 'javelin']:
            parent = 'tfc:item/handheld_flipped'
        item(('metal', item_type, metal), 'tfc:items/metal/%s/%s' % (item_type.replace('unfinished_', ''), metal), parent=parent)
for x in ['ingot', 'unshaped']:
    item(('metal', x, 'unknown'), 'tfc:items/metal/%s/%s' % (x, 'unknown'))

# WOOD STUFF
for wood_type in WOOD_TYPES:
    item(('wood', 'lumber', wood_type), 'tfc:items/wood/lumber/%s' % wood_type)

# ROCK TOOLS
for rock_cat in ROCK_CATEGORIES:
    for item_type in ['axe', 'shovel', 'hoe', 'knife', 'javelin', 'hammer']:
        parent = 'item/handheld'
        if item_type in ['knife', 'javelin']:
            parent = 'tfc:item/handheld_flipped'
        item(('stone', item_type, rock_cat), 'tfc:items/stone/%s' % item_type, parent=parent)

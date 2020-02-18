from mcresources import *

from data.constants import *


def main():
    clean_generated_resources('../src/main/resources')

    rm = ResourceManager('tfc', resource_dir='../src/main/resources')

    # Rock block variants
    for rock in ROCKS:
        for block_type in ROCK_BLOCK_TYPES:
            rm.blockstate(('rock', block_type, rock))
            rm.block_model(('rock', block_type, rock), 'tfc:block/rock/%s/%s' % (block_type, rock))
            rm.block_item_model(('rock', block_type, rock))
            rm.block_loot(('rock', block_type, rock), 'tfc:rock/%s/%s' % (block_type, rock))

    for rock, rock_data in ROCKS.items():
        rm.data(('tfc', 'rocks', rock), {
            'blocks': dict((block_type, 'tfc:rock/%s/%s' % (block_type, rock)) for block_type in ROCK_BLOCK_TYPES),
            **rock_data
        })

    # Sand
    for sand in SAND_BLOCK_TYPES:
        rm.blockstate(('sand', sand))
        rm.block_model(('sand', sand), textures='tfc:block/sand/%s' % sand)
        rm.block_item_model(('sand', sand))
        rm.block_loot(('sand', sand), 'tfc:sand/%s' % sand)

    # Dirt
    for dirt in SOIL_BLOCK_VARIANTS:
        rm.blockstate(('dirt', dirt),
                      variants={'': [{'model': 'tfc:block/dirt/%s' % dirt, 'y': i} for i in range(0, 360, 90)]},
                      use_default_model=False)
        rm.block_model(('dirt', dirt))
        rm.block_item_model(('dirt', dirt))
        rm.block_loot(('dirt', dirt), 'tfc:dirt/%s' % dirt)

    # Grass / Dry Grass
    north_face = {
        'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#texture', 'cullface': 'north'}}
    }
    north_face_tint0 = {
        'from': [0, 0, 0], 'to': [16, 16, 0],
        'faces': {'north': {'texture': '#overlay', 'cullface': 'north', 'tintindex': 0}}
    }
    for var in SOIL_BLOCK_VARIANTS:
        rm.blockstate_multipart(('grass', var), [
            {'model': 'tfc:block/grass/%s_top' % var, 'x': 270},
            {'model': 'tfc:block/grass/%s_bottom' % var, 'x': 90},
            ({'north': True}, {'model': 'tfc:block/grass/%s_top' % var}),
            ({'east': True}, {'model': 'tfc:block/grass/%s_top' % var, 'y': 90}),
            ({'south': True}, {'model': 'tfc:block/grass/%s_top' % var, 'y': 180}),
            ({'west': True}, {'model': 'tfc:block/grass/%s_top' % var, 'y': 270}),
            ({'north': False}, {'model': 'tfc:block/grass/%s_side' % var}),
            ({'east': False}, {'model': 'tfc:block/grass/%s_side' % var, 'y': 90}),
            ({'south': False}, {'model': 'tfc:block/grass/%s_side' % var, 'y': 180}),
            ({'west': False}, {'model': 'tfc:block/grass/%s_side' % var, 'y': 270}),
        ])
        rm.block_model(('grass', '%s_top' % var), {
            'overlay': 'tfc:block/grass_top',
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face_tint0])
        rm.block_model(('grass', '%s_side' % var), {
            'overlay': 'tfc:block/grass_side',
            'texture': 'tfc:block/dirt/%s' % var,
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face, north_face_tint0])
        rm.block_model(('grass', '%s_bottom' % var), {
            'texture': 'tfc:block/dirt/%s' % var,
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face])
        rm.block_loot(('grass', var), 'tfc:dirt/%s' % var)

        rm.blockstate_multipart(('dry_grass', var), [
            {'model': 'tfc:block/dry_grass/%s_top' % var, 'x': 270},
            {'model': 'tfc:block/dry_grass/%s_bottom' % var, 'x': 90},
            ({'north': True}, {'model': 'tfc:block/dry_grass/%s_top' % var}),
            ({'east': True}, {'model': 'tfc:block/dry_grass/%s_top' % var, 'y': 90}),
            ({'south': True}, {'model': 'tfc:block/dry_grass/%s_top' % var, 'y': 180}),
            ({'west': True}, {'model': 'tfc:block/dry_grass/%s_top' % var, 'y': 270}),
            ({'north': False}, {'model': 'tfc:block/dry_grass/%s_side' % var}),
            ({'east': False}, {'model': 'tfc:block/dry_grass/%s_side' % var, 'y': 90}),
            ({'south': False}, {'model': 'tfc:block/dry_grass/%s_side' % var, 'y': 180}),
            ({'west': False}, {'model': 'tfc:block/dry_grass/%s_side' % var, 'y': 270}),
        ])
        rm.block_model(('dry_grass', '%s_top' % var), {
            'overlay': 'tfc:block/dry_grass_top',
            'texture': 'tfc:block/dirt/%s' % var,
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face, north_face_tint0])
        rm.block_model(('dry_grass', '%s_side' % var), {
            'overlay': 'tfc:block/dry_grass_side',
            'texture': 'tfc:block/dirt/%s' % var,
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face, north_face_tint0])
        rm.block_model(('dry_grass', '%s_bottom' % var), {
            'texture': 'tfc:block/dirt/%s' % var,
            'particle': 'tfc:block/dirt/%s' % var
        }, parent='block/block', elements=[north_face])

    grass_blocks = ['tfc:grass/%s' % var for var in SOIL_BLOCK_VARIANTS] + ['tfc:dry_grass/%s' % var for var in
                                                                            SOIL_BLOCK_VARIANTS]
    rm.block_tag('grass', *grass_blocks)


if __name__ == '__main__':
    main()

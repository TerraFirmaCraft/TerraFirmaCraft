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
        rm.blockstate(('dirt', dirt))
        rm.block_model(('dirt', dirt))
        rm.block_item_model(('dirt', dirt))
        rm.block_loot(('dirt', dirt), 'tfc:dirt/%s' % dirt)


if __name__ == '__main__':
    main()


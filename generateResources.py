from mcresources import *

ROCKS = ['granite', 'diorite', 'gabbro', 'shale', 'claystone', 'rocksalt', 'limestone', 'conglomerate', 'dolomite',
         'chert', 'chalk', 'rhyolite', 'basalt', 'andesite', 'dacite', 'quartzite', 'slate', 'phyllite', 'schist',
         'gneiss', 'marble']
ROCK_BLOCK_TYPES = ['raw', 'bricks', 'cobble', 'gravel', 'smooth']


def main():
    clean_generated_resources()

    rm = ResourceManager('tfc')

    for rock in ROCKS:
        for block_type in ROCK_BLOCK_TYPES:
            rm.blockstate(('rock', block_type, rock))
            rm.block_model(('rock', block_type, rock), textures={'all': 'tfc:block/rock/%s/%s' % (block_type, rock)})
            rm.block_item_model(('rock', block_type, rock))
            rm.block_loot(('rock', 'block_type', 'rock'), 'tfc:rock/%s/%s' % (block_type, rock))

    for rock in ROCKS:
        rm.data(('tfc', 'rocks', rock), {
            'blocks': dict((block_type, 'tfc:rock/%s/%s' % (block_type, rock)) for block_type in
                           ('raw', 'smooth', 'cobble', 'bricks', 'gravel')),
            'category': 'metamorphic'
        })


if __name__ == '__main__':
    main()

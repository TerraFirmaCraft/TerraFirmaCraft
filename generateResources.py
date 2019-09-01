from mcresources import ResourceManager, clean_generated_resources

ROCKS = ['granite', 'diorite', 'gabbro', 'shale', 'claystone', 'rocksalt', 'limestone', 'conglomerate', 'dolomite',
         'chert', 'chalk', 'rhyolite', 'basalt', 'andesite', 'dacite', 'quartzite', 'slate', 'phyllite', 'schist',
         'gneiss', 'marble']
ROCK_BLOCK_TYPES = ['raw', 'bricks', 'cobble', 'gravel', 'smooth']


def main():
    clean_generated_resources()

    rm = ResourceManager('tfc')

    for stone in ROCKS:
        for block_type in ROCK_BLOCK_TYPES:
            rm.blockstate(('rock', block_type, stone))
            rm.block_model(('rock', block_type, stone), parent='block/cube_all',
                           textures={'all': 'tfc:block/rock/%s/%s' % (block_type, stone)})


if __name__ == '__main__':
    main()

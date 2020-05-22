# Script to run all resource generation

from mcresources import ResourceManager, clean_generated_resources

import assets.stones
import assets.metals
import data.ore_veins
import data.rocks
import data.metals
import recipes.collapse
import recipes.metal_item
import lang.metals
import lang.misc


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    data.ore_veins.generate(rm)
    data.rocks.generate(rm)
    data.metals.generate(rm)

    recipes.collapse.generate(rm)
    recipes.metal_item.generate(rm)

    assets.stones.generate(rm)
    assets.metals.generate(rm)
    lang.metals.generate(rm)
    lang.misc.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

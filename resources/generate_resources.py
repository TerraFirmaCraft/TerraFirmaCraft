# Script to run all resource generation

from mcresources import ResourceManager, clean_generated_resources

import assets
import data.ore_veins
import data.rocks
import recipes.collapse


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    data.ore_veins.generate(rm)
    data.rocks.generate(rm)

    recipes.collapse.generate(rm)

    assets.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

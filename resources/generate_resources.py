#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

# Script to run all resource generation

from mcresources import ResourceManager, clean_generated_resources

import assets
import data.ore_veins
import data.rocks
import lang.plants
import recipes.collapse


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    data.ore_veins.generate(rm)
    data.rocks.generate(rm)

    recipes.collapse.generate(rm)

    assets.generate(rm)
    lang.plants.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

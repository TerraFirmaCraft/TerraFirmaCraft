# Script to run all resource generation

from mcresources import ResourceManager, clean_generated_resources

import data.assets as assets
import data.collapse_recipes as collapse_recipes
import data.ore_veins as ore_veins


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    assets.generate(rm)
    ore_veins.generate(rm)
    collapse_recipes.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

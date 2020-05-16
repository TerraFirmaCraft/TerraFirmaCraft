# Script to run all resource generation

from mcresources import ResourceManager, clean_generated_resources

import assets
import data
import recipes


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    assets.generate(rm)
    data.ore_veins.generate(rm)
    recipes.collapse.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

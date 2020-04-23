# Script to run all resource generation

from mcresources import clean_generated_resources, ResourceManager

import data.assets as assets
import data.ore_veins as ore_veins


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('../src/main/resources')

    assets.generate(rm)
    ore_veins.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()

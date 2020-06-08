#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

# Script to run all resource generation

<<<<<<< HEAD
import assets.metals
import assets.stones
import assets.gems
import data.item_heats
import data.metal_items
import data.metals
import data.ore_veins
import data.rocks
import lang.metals
import lang.misc
import recipes.collapse
import vanilla.tags
=======
>>>>>>> 39c047c5f45d35107306a69a78b44701108986d6
from mcresources import ResourceManager, clean_generated_resources

import assets
import collapse_recipes
import data
import ore_veins
from constants import *


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    # do simple lang keys first, because it's ordered intentionally
    rm.lang(DEFAULT_LANG)

    # generic assets / data
    assets.generate(rm)
    data.generate(rm)

    # more complex stuff n things
    ore_veins.generate(rm)
    collapse_recipes.generate(rm)

<<<<<<< HEAD
    assets.stones.generate(rm)
    assets.metals.generate(rm)
    assets.gems.generate(rm)
    lang.metals.generate(rm)
    lang.misc.generate(rm)
    
    vanilla.tags.generate(rm)
=======
    # Random things
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
>>>>>>> 39c047c5f45d35107306a69a78b44701108986d6

    rm.flush()


if __name__ == '__main__':
    main()

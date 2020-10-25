#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

# Script to run all resource generation

import argparse

from mcresources import ResourceManager, clean_generated_resources

import assets
import collapse_recipes
import data
import recipes
import world_gen
from constants import *


def main():
    parser = argparse.ArgumentParser(description='Generate resources for TFC')
    parser.add_argument('--clean', action='store_true', dest='clean', help='Clean all auto generated resources')
    args = parser.parse_args()

    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    if args.clean:
        clean_generated_resources('/'.join(rm.resource_dir))
        return

    # do simple lang keys first, because it's ordered intentionally
    rm.lang(DEFAULT_LANG)

    # generic assets / data
    assets.generate(rm)
    data.generate(rm)
    world_gen.generate(rm)
    recipes.generate(rm)

    # more complex stuff n things
    collapse_recipes.generate(rm)

    rm.flush()

    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


if __name__ == '__main__':
    main()

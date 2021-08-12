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
    parser.add_argument('--hotswap', type=str, default=None, help='A secondary target directory to write resources to, creates a resource hotswap.')
    args = parser.parse_args()

    rm = ResourceManager('tfc', resource_dir='../src/main/resources')

    if args.clean:
        # Stupid windows file locking errors.
        for tries in range(1, 1 + 3):
            try:
                clean_generated_resources('/'.join(rm.resource_dir))
                print('Clean Success')
                return
            except:
                print('Failed, retrying (%d / 3)' % tries)
        print('Clean Aborted')
        return

    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))

    if args.hotswap is not None:
        # Generate into the /out/production/resources folder, which is used when build + run with Intellij
        rm = ResourceManager('tfc', resource_dir=args.hotswap)
        generate_all(rm)
        print('Hotswap Finished')


def generate_all(rm: ResourceManager):
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


if __name__ == '__main__':
    main()

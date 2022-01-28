#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

# Script to run all resource generation

import sys
import argparse
import traceback

from mcresources import ResourceManager, utils
from mcresources.type_definitions import Json

import assets
import data
import recipes
import world_gen

from constants import *

class ModificationLoggingResourceManager(ResourceManager):

    def write(self, path_parts: Sequence[str], data_in: Json):
        m = self.modified_files
        super(ModificationLoggingResourceManager, self).write(path_parts, data_in)
        if m != self.modified_files:
            print('Modified: ' + utils.resource_location(self.domain, path_parts).join(), file=sys.stderr)
            traceback.print_stack()
            print('', file=sys.stderr)


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
                utils.clean_generated_resources('/'.join(rm.resource_dir))
                print('Clean Success')
                return
            except:
                print('Failed, retrying (%d / 3)' % tries)
        print('Clean Aborted')
        return

    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))

    if args.hotswap is not None:
        # Optionally generate all resources into a second directory (the build dir, either gradle or IDEA's, for resource hot swapping
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

    rm.flush()


if __name__ == '__main__':
    main()

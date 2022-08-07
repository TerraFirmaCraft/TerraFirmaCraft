"""
Entrypoint for all common scripting infrastructure.

Invoke like 'python resources <actions>'
Where actions can be any list of actions to take.

"""

from argparse import ArgumentParser
from mcresources import ResourceManager, utils


def main():
    parser = ArgumentParser(description='Entrypoint for all common scripting infrastructure.')
    parser.add_argument('actions', nargs='+', choices=(
        'clean',  # clean all resources (assets / data), including book
        'all',  # generate all resources (assets / data)
        'assets',  # only assets.py
        'data',  # only data.py
        'recipes',  # only recipes.py
        'worldgen',  # only world gen data (excluding tags)
        'hotswap',  # generate and hotswap all resources (assets / data)
        'book',  # generate the book
        'trees',  # generate tree NBT structures from templates
        'format_lang',  # format language files
    ))
    parser.add_argument('--translate', type=str, default='en_us', help='Used for \'book\'')
    parser.add_argument('--local', type=str, default=None, help='Used for \'book\'')
    parser.add_argument('--hotswap', type=str, default='./out/production/resources', help='Used for \'hotswap\'')

    args = parser.parse_args()

    for action in args.actions:
        if action == 'clean':
            clean()
        elif action == 'all':
            resources(do_assets=True, do_data=True, do_recipes=True, do_worldgen=True)
        elif action == 'assets':
            resources(do_assets=True)
        elif action == 'data':
            resources(do_data=True)
        elif action == 'recipes':
            resources(do_recipes=True)
        elif action == 'worldgen':
            resources(do_worldgen=True)
        elif action == 'hotswap':
            resources(dest=args.hotswap, do_assets=True, do_data=True, do_recipes=True, do_worldgen=True)
        elif action == 'book':
            import generate_book
            generate_book.main(translate_lang=args.translate, local_minecraft_dir=args.local)
        elif action == 'trees':
            import generate_trees
            generate_trees.main()
        elif action == 'format_lang':
            import format_lang
            format_lang.main()


def clean():
    """ Cleans all generated resources files """
    for tries in range(1, 1 + 3):
        try:
            utils.clean_generated_resources('./src/main/resources')
            print('Clean Success')
            return
        except OSError:
            print('Failed, retrying (%d / 3)' % tries)
    print('Clean Aborted')


def resources(dest: str = './src/main/resources', do_assets: bool = False, do_data: bool = False, do_recipes: bool = False, do_worldgen: bool = False):
    """ Generates resource files, or a subset of them """

    rm = ResourceManager('tfc', resource_dir=dest)

    # do simple lang keys first, because it's ordered intentionally
    import constants
    rm.lang(constants.DEFAULT_LANG)

    # generic assets / data
    if do_assets:
        import assets
        assets.generate(rm)
    if do_data:
        import data
        data.generate(rm)
    if do_recipes:
        import recipes
        recipes.generate(rm)
    if do_worldgen:
        import world_gen
        world_gen.generate(rm)

    if all((do_assets, do_data, do_worldgen, do_recipes)):
        # Only generate this when generating all, as it's shared
        rm.flush()

    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


if __name__ == '__main__':
    main()

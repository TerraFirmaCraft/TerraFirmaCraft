"""
Entrypoint for all common scripting infrastructure.

Invoke like 'python resources <actions>'
Where actions can be any list of actions to take.

"""

import difflib
import json
import os
import shutil
import sys
import zipfile
from argparse import ArgumentParser
from typing import Optional

from mcresources import ResourceManager, utils

import advancements
import assets
import constants
import data
import tags
import format_lang
import generate_book
import generate_textures
import generate_trees
import recipes
import validate_assets
import world_gen

BOOK_LANGUAGES = ('en_us', 'ja_jp', 'ko_kr', 'pt_br', 'uk_ua', 'zh_cn', 'zh_tw', 'zh_hk')
MOD_LANGUAGES = ('en_us', 'es_es', 'de_de', 'ja_jp', 'ko_kr', 'pl_pl', 'pt_br', 'ru_ru', 'tr_tr', 'uk_ua', 'zh_cn', 'zh_tw', 'zh_hk')


def main():
    parser = ArgumentParser(description='Entrypoint for all common scripting infrastructure.')
    parser.add_argument('actions', nargs='+', choices=(
        'clean',  # clean all resources (assets / data), including book
        'validate',  # validate no resources are changed when re-running
        'validate_assets',  # manual validation for certain important resources
        'all',  # generate all resources (assets / data / book)
        'assets',  # only assets.py
        'data',  # only data.py
        'recipes',  # only recipes.py
        'worldgen',  # only world gen data (excluding tags)
        'advancements',  # only advancements.py (which excludes recipe advancements)
        'book',  # generate the book
        'trees',  # generate tree NBT structures from templates
        'format_lang',  # format language files
        'textures',  # generate textures
        'zip',  # zips resources for faster loading in dev
    ))
    parser.add_argument('--translate', type=str, default='en_us', help='Runs the book translation using a single provided language')
    parser.add_argument('--translate-all', action='store_true', dest='translate_all', help='Runs the book against all provided translations')
    parser.add_argument('--reverse-translate', action='store_true', dest='reverse_translate', help='Reverses a book translation, creating a <lang>.json from translated book files')
    parser.add_argument('--local', type=str, default=None, help='Points to a local minecraft instance. Used for \'book\', to generate a hot reloadable book, and used for \'clean\', to clean said instance\'s book')
    parser.add_argument('--hotswap', action='store_true', dest='hotswap', help='Causes resource generation to also generate to --hotswap-dir')
    parser.add_argument('--hotswap-dir', type=str, default='./out/production/resources', help='Used for \'--hotswap\'')

    args = parser.parse_args()
    hotswap = args.hotswap_dir if args.hotswap else None

    for action in args.actions:
        if action == 'clean':
            clean(args.local)
        elif action == 'validate':
            validate_resources()
        elif action == 'validate_assets':
            validate_assets.main()
        elif action == 'all':
            resources(hotswap=hotswap, do_assets=True, do_data=True, do_recipes=True, do_worldgen=True, do_advancements=True)
            format_lang.main(False, 'minecraft', MOD_LANGUAGES)  # format_lang
            format_lang.main(False, 'tfc', MOD_LANGUAGES)
            for lang in BOOK_LANGUAGES:  # Translate all
                generate_book.main(lang, args.local, False)
        elif action == 'assets':
            resources(hotswap=hotswap, do_assets=True)
        elif action == 'data':
            resources(hotswap=hotswap, do_data=True)
        elif action == 'recipes':
            resources(hotswap=hotswap, do_recipes=True)
        elif action == 'worldgen':
            resources(hotswap=hotswap, do_worldgen=True)
        elif action == 'advancements':
            resources(hotswap=hotswap, do_advancements=True)
        elif action == 'textures':
            generate_textures.main()
        elif action == 'book':
            if args.translate_all:
                for lang in BOOK_LANGUAGES:
                    generate_book.main(lang, args.local, validate=False, reverse_translate=args.reverse_translate)
            else:
                generate_book.main(args.translate, args.local, validate=False, reverse_translate=args.reverse_translate)
        elif action == 'trees':
            generate_trees.main()
        elif action == 'format_lang':
            format_lang.main(False, 'minecraft', MOD_LANGUAGES)
            format_lang.main(False, 'tfc', MOD_LANGUAGES)
        elif action == 'zip':
            zip_resources()

def clean(local: Optional[str]):
    """ Cleans all generated resources files """
    clean_at('./src/main/resources')
    if local:
        clean_at(local)

def clean_at(location: str):
    for tries in range(1, 1 + 3):
        try:
            utils.clean_generated_resources(location)
            print('Clean %s' % location)
            return
        except OSError:
            print('Failed, retrying (%d / 3)' % tries)
    print('Clean Aborted')


def validate_resources():
    """ Validates all resources are unchanged. """
    rm = ValidatingResourceManager('tfc', './src/main/resources')
    resources_at(rm, True, True, True, True, True)
    error = rm.error_files != 0

    for lang in BOOK_LANGUAGES:
        try:
            generate_book.main(lang, None, True, rm)
            error |= rm.error_files != 0
        except AssertionError as e:
            print(e)
            error = True

    for lang in MOD_LANGUAGES:
        try:
            format_lang.main(True, 'minecraft', (lang,))
            format_lang.main(True, 'tfc', (lang,))
        except AssertionError as e:
            print(e)
            error = True

    assert not error, 'Validation Errors Were Present'

def zip_resources():
    asset_count = zip_asset_type('assets')
    data_count = zip_asset_type('data')

    rescue_folder('META-INF')
    rescue_folder('data/tfc/patchouli_books')
    rescue_asset('tfc.mixins.json')
    rescue_asset('assets_zipped.zip')
    rescue_asset('data_zipped.zip')

    print(f'Zipped {asset_count} asset files, {data_count} data files.')

def zip_asset_type(asset_type: str):
    count = 0
    with zipfile.ZipFile(f'./src/main/resources/{asset_type}_zipped.zip', 'w') as zf:
        for dirname, subdirs, files in os.walk('./src/main/resources'):
            if asset_type in dirname:
                arcname = dirname.replace('./src/main/resources\\', '')
                zf.write(dirname, arcname=arcname)
                for fn in files:
                    fn_file_name = os.path.join(dirname, fn)
                    fn_arcname = fn_file_name.replace('./src/main/resources\\', '')
                    zf.write(fn_file_name, arcname=fn_arcname)
                    count += 1
        zf.write('./src/main/resources/pack.mcmeta', arcname='pack.mcmeta')
    return count

def rescue_asset(path: str):
    shutil.copy('./src/main/resources/%s' % path, './out/production/resources/%s' % path)

def rescue_folder(path: str):
    shutil.copytree('./src/main/resources/%s' % path, './out/production/resources/%s' % path, dirs_exist_ok=True)


def resources(hotswap: str = None, do_assets: bool = False, do_data: bool = False, do_recipes: bool = False, do_worldgen: bool = False, do_advancements: bool = False):
    """ Generates resource files, or a subset of them """
    resources_at(ResourceManager('tfc', resource_dir='./src/main/resources'), do_assets, do_data, do_recipes, do_worldgen, do_advancements)
    if hotswap:
        resources_at(ResourceManager('tfc', resource_dir=hotswap), do_assets, do_data, do_recipes, do_worldgen, do_advancements)


def resources_at(rm: ResourceManager, do_assets: bool, do_data: bool, do_recipes: bool, do_worldgen: bool, do_advancements: bool):
    # do simple lang keys first, because it's ordered intentionally
    rm.lang(constants.DEFAULT_LANG)

    # generic assets / data
    if do_assets:
        assets.generate(rm)
    if do_data:
        data.generate(rm)
        tags.generate(rm)
    if do_recipes:
        recipes.generate(rm)
    if do_worldgen:
        world_gen.generate(rm)
    if do_advancements:
        advancements.generate(rm)

    if all((do_assets, do_data, do_worldgen, do_recipes, do_advancements)):
        # Only generate this when generating all, as it's shared
        rm.flush()

        # Separate generation for vanilla override lang
        vanilla_rm = ResourceManager('minecraft', resource_dir=rm.resource_dir)
        vanilla_rm.lang(constants.VANILLA_OVERRIDE_LANG)
        vanilla_rm.flush()

    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


class ValidatingResourceManager(ResourceManager):

    def __init__(self, domain: str, resource_dir):
        super(ValidatingResourceManager, self).__init__(domain, resource_dir)
        self.validation_error = False

    def write(self, path_parts, data_to_write):
        data_to_write = utils.del_none({'__comment__': 'This file was automatically created by mcresources', **data_to_write})
        path = os.path.join(*path_parts) + '.json'
        try:
            if not os.path.isfile(path):
                print('Error: resource generation created new file \'%s\'' % path, file=sys.stderr)
                self.error_files += 1
                return
            with open(path, 'r', encoding='utf-8') as file:
                old_data = json.load(file)
            if old_data != data_to_write:
                old_text = json.dumps(old_data, indent=self.indent)
                text = json.dumps(data_to_write, indent=self.indent)
                diff = '\n'.join(difflib.unified_diff(old_text.split('\n'), text.split('\n'), 'old', 'new', n=1))
                print('Error: resource generation modified file \'%s\' Diff:\n%s\n' % (path, diff), file=sys.stderr)
                self.error_files += 1
        except Exception as e:
            self.on_error(path, e)
            self.error_files += 1


if __name__ == '__main__':
    main()

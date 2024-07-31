"""
Entrypoint for all common scripting infrastructure.

Invoke like 'python resources <actions>'
Where actions can be any list of actions to take.

"""

import difflib
import json
import os
import sys
from argparse import ArgumentParser
from typing import Optional

from mcresources import ResourceManager, utils

import advancements
import assets
import constants
import format_lang
import generate_book
import generate_textures
import generate_trees
import recipes
import validate_assets
import world_gen

BOOK_LANGUAGES = ('en_us', 'ja_jp', 'ko_kr', 'pt_br', 'ru_ru', 'uk_ua', 'zh_cn', 'zh_tw', 'zh_hk')
MOD_LANGUAGES = ('en_us', 'es_es', 'de_de', 'ja_jp', 'ko_kr', 'pl_pl', 'pt_br', 'ru_ru', 'tr_tr', 'uk_ua', 'zh_cn', 'zh_tw', 'zh_hk')
RESOURCE_DIR = 'src/main/resources'
TEST_RESOURCE_DIR = 'src/test/resources'


def main():
    parser = ArgumentParser(description='Entrypoint for all common scripting infrastructure.')
    parser.add_argument('actions', nargs='+', choices=(
        'all',  # generate all resources (assets / data / book)
        'clean',  # clean all resources (assets / data), including book
        'validate',  # validate no resources are changed when re-running
        'book',  # generate the book
        'trees',  # generate tree NBT structures from templates
        'format_lang',  # format language files
        'textures',  # generate textures
    ))
    parser.add_argument('--translate', type=str, default='en_us', help='Runs the book translation using a single provided language')
    parser.add_argument('--translate-all', action='store_true', dest='translate_all', help='Runs the book against all provided translations')
    parser.add_argument('--reverse-translate', action='store_true', dest='reverse_translate', help='Reverses a book translation, creating a <lang>.json from translated book files')
    parser.add_argument('--local', type=str, default=None, help='Points to a local minecraft instance. Used for \'book\', to generate a hot reloadable book, and used for \'clean\', to clean said instance\'s book')
    parser.add_argument('--hotswap', action='store_true', dest='hotswap', help='Causes resource generation to also generate to --hotswap-dir')
    parser.add_argument('--hotswap-main', type=str, default='./out/production/resources', help='Used for \'--hotswap\'')
    parser.add_argument('--hotswap-test', type=str, default='./out/test/resources', help='Used for \'--hotswap\'')

    args = parser.parse_args()

    for action in args.actions:
        if action == 'clean':
            clean(args.local)
        elif action == 'validate':
            validate_resources()
            validate_assets.main()
        elif action == 'all':
            resources_at(
                ResourceManager('tfc', resource_dir=RESOURCE_DIR),
                ResourceManager('minecraft', resource_dir=RESOURCE_DIR),
                ResourceManager('tfc', resource_dir=TEST_RESOURCE_DIR)
            )
            if args.hotswap:
                resources_at(
                    ResourceManager('tfc', resource_dir=args.hotswap_main),
                    ResourceManager('minecraft', resource_dir=args.hotswap_main),
                    ResourceManager('tfc', resource_dir=args.hotswap_test)
                )
            format_lang.main(False, 'minecraft', MOD_LANGUAGES)  # format_lang
            format_lang.main(False, 'tfc', MOD_LANGUAGES)
            for lang in BOOK_LANGUAGES:  # Translate all
                generate_book.main(lang, args.local, False)
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


def clean(local: Optional[str]):
    """ Cleans all generated resources files """
    clean_at(RESOURCE_DIR)
    clean_at(TEST_RESOURCE_DIR)
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
    resources_at(
        rm := ValidatingResourceManager('tfc', RESOURCE_DIR),
        vanilla_rm := ValidatingResourceManager('minecraft', RESOURCE_DIR),
        test_rm := ValidatingResourceManager('tfc', TEST_RESOURCE_DIR)
    )

    error = rm.error_files != 0 or vanilla_rm.error_files != 0 or test_rm.error_files != 0

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


def resources_at(
    rm: ResourceManager,
    vanilla_rm: ResourceManager,
    test_rm: ResourceManager
):
    # Do lang keys first, because it's ordered intentionally
    rm.lang(constants.DEFAULT_LANG)
    vanilla_rm.lang(constants.VANILLA_OVERRIDE_LANG)

    assets.generate(rm)
    #data.generate(rm)
    #tags.generate(rm)
    #recipes.generate(rm)
    recipes.generate_test(test_rm)
    world_gen.generate(rm)
    advancements.generate(rm)

    # Tags should be done exclusively via code datagen, but notably right now we're missing world gen datagen
    validate_no_tags(rm)
    validate_no_tags(vanilla_rm)

    # Flush
    rm.flush()
    vanilla_rm.flush()

    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (
        rm.new_files + test_rm.new_files + vanilla_rm.new_files,
        rm.modified_files + test_rm.modified_files + vanilla_rm.modified_files,
        rm.unchanged_files + test_rm.unchanged_files + vanilla_rm.unchanged_files,
        rm.error_files + test_rm.error_files + vanilla_rm.error_files))


def validate_no_tags(rm: ResourceManager):
    # Tags should be done exclusively via code datagen, but notably right now we're missing world gen datagen
    for tag_type, tag_names in rm.tags_buffer.items():
        if tag_type.startswith('worldgen/'):
            print('Allowing %d %s tags...' % (len(tag_names), tag_type))
        else:
            raise ValueError('Tag datagen is done in java. Tried to generate %s tags:\n%s' % (tag_type, '\n'.join([t.join() for t in tag_names])))


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

from glob import glob
import json
from mcresources import utils

ASSETS_PATH = './src/main/resources/assets/'
TEXTURE_FORGIVENESS_PATHS = ('_fluff', 'block/burlap', 'block/molten_flow', 'block/paper', 'block/unrefined_paper', 'yellow_bell', 'red_bell', 'green_bell', 'metal/full', 'plant/', 'sandstone/side', 'quiver', 'placed_item')
LANG_PATH = ASSETS_PATH + 'tfc/lang/en_us.json'
SOUNDS_PATH = ASSETS_PATH + 'tfc/sounds.json'

def main():
    errors = 0
    model_locations = glob(ASSETS_PATH + 'tfc/models/**/*.json', recursive=True)
    state_locations = glob(ASSETS_PATH + 'tfc/blockstates/**/*.json', recursive=True)
    lang_json = load(LANG_PATH)
    sound_json = load(SOUNDS_PATH)
    errors += validate_lang(state_locations, lang_json, sound_json)
    errors += validate_model_parents(model_locations)
    errors += validate_textures(model_locations)
    errors += validate_blockstate_models(state_locations)
    assert errors == 0

def validate_lang(state_locations, lang_json, sound_json):
    tested = 0
    tested_sound = 0
    errors = 0
    for f in state_locations:
        name = f.replace('\\', '/')
        name = name.replace(ASSETS_PATH + 'tfc/blockstates/', '')
        name = name.replace('.json', '')
        name = name.replace('/', '.')
        if 'block.tfc.%s' % name not in lang_json and 'plant' not in name:
            print('Block without lang entry: %s' % name)
            errors += 1
        tested += 1
    for sound, data in sound_json.items():
        tested_sound += 1
        if 'subtitle' not in data:
            print('Sound without subtitle key: %s' % sound)
            errors += 1
        else:
            sub = data['subtitle']
            if 'tfc' in sub and sub not in lang_json:
                print('Sound subtitle missing for sound: %s with key: %s' % (sound, sub))
                errors += 1
    print('Lang Validation: %s blocks tested, %s sounds tested, %s errors' % (tested, tested_sound, errors))
    return errors

def validate_blockstate_models(state_locations):
    tested = 0
    errors = 0
    for f in state_locations:
        state_file = load(f)
        if 'variants' in state_file:
            variants = state_file['variants']
            for variant in variants.values():
                if 'model' in variant:
                    model = variant['model']
                    tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
        elif 'multipart' in state_file:
            multipart = state_file['multipart']
            for mp in multipart:
                if 'apply' in mp:
                    apply = mp['apply']
                    model = None
                    if isinstance(apply, list):
                        for entry in apply:
                            if 'model' in entry:
                                model = entry['model']
                    elif 'model' in apply:
                        model = apply['model']
                    if model is not None:
                        tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
    print('Blockstate Validation: Validated %s files, found %s errors' % (tested, errors))
    return errors

def validate_model_parents(model_locations):
    tested = 0
    errors = 0
    for f in model_locations:
        model_file = load(f)
        if 'parent' in model_file:
            parent = model_file['parent']
            tested, errors = find_model_file(f, parent, tested, errors, 'Model parent not found. Model: %s, Parent: %s')
    print('Parent Validation: Validated %s files, found %s errors' % (tested, errors))
    return errors

def validate_textures(model_locations):
    tested = 0
    files_tested = 0
    errors = 0
    existing_textures = []
    for f in model_locations:
        model_file = load(f)
        if 'textures' in model_file:
            textures = model_file['textures']
            if isinstance(textures, dict):
                files_tested += 1
                for texture in textures.values():
                    if '#' not in texture:
                        res = utils.resource_location(texture)
                        if res.domain == 'tfc':
                            tested += 1
                            path = ASSETS_PATH + 'tfc/textures/%s.png' % res.path
                            if len(glob(path)) == 0:
                                print('Texture file not found. Name: %s Filepath: %s' % (f, path))
                                errors += 1
                            else:
                                existing_textures.append(path)
    for f in glob(ASSETS_PATH + 'tfc/textures/**/*.png', recursive=True):
        f = f.replace('\\', '/')
        if f not in existing_textures and ('block/' in f or 'item/' in f):
            forgiven = False
            for check in TEXTURE_FORGIVENESS_PATHS:
                if check in f:
                    forgiven = True
            if not forgiven:
                print('Texture not matched to any model file: %s' % f)
                errors += 1

    print('Texture Validation: Verified %s files, %s texture entries, found %s errors' % (files_tested, tested, errors))
    return errors

def find_model_file(file_path: str, initial_path: str, tested: int, errors: int, on_error: str):
    res = utils.resource_location(initial_path)
    if res.domain == 'tfc':
        tested += 1
        path = ASSETS_PATH + 'tfc/models/%s.json' % res.path
        found = len(glob(path))
        if found != 1:
            print(on_error % (file_path, path))
            errors += 1
    return tested, errors


def load(fn: str):
    with open(fn, 'r', encoding='utf-8') as f:
        return json.load(f)


from glob import glob
import json
from typing import List

from mcresources import utils

ASSETS_PATH = './src/main/resources/assets/'
TEXTURE_FORGIVENESS_PATHS: List = ['_fluff', 'block/burlap', 'block/powder', 'metal/smooth', 'metal/block', 'block/molten_flow', 'block/paper', 'block/unrefined_paper', 'yellow_bell', 'red_bell', 'green_bell', 'sandstone/side', 'quiver', 'placed_item']
MODEL_FORGIVENESS_PATHS: List = ['block/jar', 'block/firepit_log_']
LANG_PATH = ASSETS_PATH + 'tfc/lang/en_us.json'
SOUNDS_PATH = ASSETS_PATH + 'tfc/sounds.json'

def main():
    errors = 0
    model_locations = glob(ASSETS_PATH + 'tfc/models/**/*.json', recursive=True)
    state_locations = glob(ASSETS_PATH + 'tfc/blockstates/**/*.json', recursive=True)
    mc_state_locations = glob(ASSETS_PATH + 'minecraft/blockstates/**/*.json', recursive=True)
    lang_json = load(LANG_PATH)
    sound_json = load(SOUNDS_PATH)
    errors += validate_lang(state_locations, lang_json, sound_json)
    errors, km = validate_model_parents(model_locations)
    errors += validate_textures(model_locations)
    bs_errors, km2 = validate_blockstate_models(state_locations)
    bs_errors2, km3 = validate_blockstate_models(mc_state_locations)
    errors += bs_errors
    errors += bs_errors2
    errors += validate_models_used(model_locations, km + km2 + km3)
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
    known_models = []
    for f in state_locations:
        state_file = load(f)
        if 'variants' in state_file:
            variants = state_file['variants']
            for variant in variants.values():
                if isinstance(variant, list):  # catches randomized models
                    for v in variant:
                        model = v['model']
                        tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
                        known_models.append(model)
                elif 'model' in variant:
                    model = variant['model']
                    tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
                    known_models.append(model)
        elif 'multipart' in state_file:
            multipart = state_file['multipart']
            for mp in multipart:
                if 'apply' in mp:
                    apply = mp['apply']
                    if isinstance(apply, list):
                        for entry in apply:
                            if 'model' in entry:
                                model = entry['model']
                                tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
                                known_models.append(model)
                    elif 'model' in apply:
                        model = apply['model']
                        tested, errors = find_model_file(f, model, tested, errors, 'Blockstate file %s points to non-existent model: %s')
                        known_models.append(model)
    print('Blockstate Validation: Validated %s files, found %s errors' % (tested, errors))
    return errors, known_models


def validate_models_used(model_locations, known_models):
    tested = 0
    errors = 0
    fixed_km = []
    fixed_ml = [f.replace('\\', '/') for f in model_locations if 'item' not in f]
    for f in known_models:
        res = utils.resource_location(f)
        fixed_km.append(ASSETS_PATH + 'tfc/models/%s.json' % res.path)
    for f in fixed_ml:
        tested += 1
        forgiven = True
        if f not in fixed_km:
            for path in MODEL_FORGIVENESS_PATHS:
                if path in f:
                    forgiven = True
            if not forgiven:
                errors += 1
                print('Model not in a blockstate file or used as parent: %s' % f)
    print('Unused model validation: Validated %s files, found %s errors' % (tested, errors))
    return errors

def validate_model_parents(model_locations):
    tested = 0
    errors = 0
    known_models = []
    for f in model_locations:
        model_file = load(f)
        if 'parent' in model_file:
            parent = model_file['parent']
            tested, errors = find_model_file(f, parent, tested, errors, 'Model parent not found. Model: %s, Parent: %s')
            known_models.append(parent)
    print('Parent Validation: Validated %s files, found %s errors' % (tested, errors))
    return errors, known_models

def validate_textures(model_locations):
    tested = 0
    files_tested = 0
    errors = 0
    existing_textures = []
    atlas = load(ASSETS_PATH + 'minecraft/atlases/blocks.json')
    for source in atlas['sources']:
        if source['type'] == 'paletted_permutations':
            for tex in source['textures']:
                TEXTURE_FORGIVENESS_PATHS.append(tex.replace('tfc:', ''))
                for suffix in source['permutations'].keys():
                    model_like_path = tex + '_' + suffix + '.png'
                    path = model_like_path.replace('tfc:', ASSETS_PATH + 'tfc/textures/')
                    existing_textures.append(path)
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
                            if path not in existing_textures:
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


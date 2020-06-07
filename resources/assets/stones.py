#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # Gems
    for gems in GEMS:
        for gem_grades in GEM_GRADES:
            rm.item_model(('gem', '%s' % gem_grades, '%s' % gems), \
                'tfc:item/gem/%s/%s' % (gem_grades, gems), \
                parent='item/handheld') \
                .with_lang(lang('%s %s' % (gem_grades, gems)))
    # Rock block variants
    for rock in ROCKS.keys():
        for block_type in ROCK_BLOCK_TYPES:
            if block_type == 'spike':
                # Spikes have special block states
                rm.blockstate(('rock', block_type, rock), variants=dict(
                    ('part=%s' % part, {'model': 'tfc:block/rock/%s/%s_%s' % (block_type, rock, part)}) for part in
                    ROCK_SPIKE_PARTS)) \
                    .with_lang(lang('%s Spike', rock))
                for part in ROCK_SPIKE_PARTS:
                    rm.block_model(('rock', block_type, '%s_%s' % (rock, part)), {
                        'texture': 'tfc:block/rock/raw/%s' % rock,
                        'particle': 'tfc:block/rock/raw/%s' % rock
                    }, parent='tfc:block/rock/spike_%s' % part) \
                        .with_item_model()
            else:
                block = rm.blockstate(('rock', block_type, rock)) \
                    .with_block_model('tfc:block/rock/%s/%s' % (block_type, rock)) \
                    .with_item_model() \
                    .with_block_loot('tfc:rock/%s/%s' % (block_type, rock))  # todo: fix raw -> rocks
                if block_type in {'smooth', 'raw'}:
                    block.with_lang(lang('%s %s', block_type, rock))
                else:
                    block.with_lang(lang('%s %s', rock, block_type))

        # Ores
        # todo: fix / add loot tables
        for ore, ore_data in ORES.items():
            if ore_data.graded:
                for grade in ORE_GRADES:
                    rm.blockstate(('ore', grade + '_' + ore, rock), 'tfc:block/ore/%s_%s/%s' % (grade, ore, rock)) \
                        .with_block_model(
                        {
                            'all': 'tfc:block/rock/raw/%s' % rock,
                            'particle': 'tfc:block/rock/raw/%s' % rock,
                            'overlay': 'tfc:block/ore/%s_%s' % (grade, ore)
                        }, parent='tfc:block/ore') \
                        .with_item_model() \
                        .with_lang(lang('%s %s %s', grade, rock, ore))
            else:
                rm.blockstate(('ore', ore, rock), 'tfc:block/ore/%s/%s' % (ore, rock)) \
                    .with_block_model(
                    {
                        'all': 'tfc:block/rock/raw/%s' % rock,
                        'particle': 'tfc:block/rock/raw/%s' % rock,
                        'overlay': 'tfc:block/ore/%s' % ore
                    }, parent='tfc:block/ore') \
                    .with_item_model() \
                    .with_lang(lang('%s %s', rock, ore))

    # Sand
    for sand in SAND_BLOCK_TYPES:
        rm.blockstate(('sand', sand)) \
            .with_block_model('tfc:block/sand/%s' % sand) \
            .with_item_model() \
            .with_block_loot('tfc:sand/%s' % sand) \
            .with_lang(lang('%s Sand', sand))

    # Dirt
    for soil in SOIL_BLOCK_VARIANTS:
        rm.blockstate(('dirt', soil),
                      variants={'': [{'model': 'tfc:block/dirt/%s' % soil, 'y': i} for i in range(0, 360, 90)]},
                      use_default_model=False) \
            .with_block_model() \
            .with_item_model() \
            .with_block_loot('tfc:dirt/%s' % soil) \
            .with_lang(lang('%s Dirt', soil))
        # todo: fix loot table
        rm.blockstate(('clay', soil),
                      variants={'': [{'model': 'tfc:block/clay/%s' % soil, 'y': i} for i in range(0, 360, 90)]},
                      use_default_model=False) \
            .with_block_model() \
            .with_item_model() \
            .with_block_loot('tfc:clay/%s' % soil) \
            .with_lang(lang('%s Clay Dirt', soil))

    # Grass
    north_face = {
        'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#texture', 'cullface': 'north'}}
    }
    north_face_tint0 = {
        'from': [0, 0, 0], 'to': [16, 16, 0],
        'faces': {'north': {'texture': '#overlay', 'cullface': 'north', 'tintindex': 0}}
    }
    for var in SOIL_BLOCK_VARIANTS:
        for grass_var, dirt in (('grass', 'tfc:block/dirt/%s' % var), ('clay_grass', 'tfc:block/clay/%s' % var)):
            rm.blockstate_multipart((grass_var, var), [
                {'model': 'tfc:block/%s/%s_top' % (grass_var, var), 'x': 270},
                {'model': 'tfc:block/%s/%s_bottom' % (grass_var, var), 'x': 90},
                ({'north': True}, {'model': 'tfc:block/%s/%s_top' % (grass_var, var)}),
                ({'east': True}, {'model': 'tfc:block/%s/%s_top' % (grass_var, var), 'y': 90}),
                ({'south': True}, {'model': 'tfc:block/%s/%s_top' % (grass_var, var), 'y': 180}),
                ({'west': True}, {'model': 'tfc:block/%s/%s_top' % (grass_var, var), 'y': 270}),
                ({'north': False}, {'model': 'tfc:block/%s/%s_side' % (grass_var, var)}),
                ({'east': False}, {'model': 'tfc:block/%s/%s_side' % (grass_var, var), 'y': 90}),
                ({'south': False}, {'model': 'tfc:block/%s/%s_side' % (grass_var, var), 'y': 180}),
                ({'west': False}, {'model': 'tfc:block/%s/%s_side' % (grass_var, var), 'y': 270}),
            ]) \
                .with_item_model() \
                .with_block_loot('tfc:dirt/%s' % var) \
                .with_tag('grass') \
                .with_lang(lang('%s %s', var, grass_var))
            # Grass Models, one for the side, top and bottom
            rm.block_model((grass_var, '%s_top' % var), {
                'overlay': 'tfc:block/grass_top',
                'particle': dirt
            }, parent='block/block', elements=[north_face_tint0])
            rm.block_model((grass_var, '%s_side' % var), {
                'overlay': 'tfc:block/grass_side',
                'texture': dirt,
                'particle': dirt
            }, parent='block/block', elements=[north_face, north_face_tint0])
            rm.block_model((grass_var, '%s_bottom' % var), {
                'texture': dirt,
                'particle': dirt
            }, parent='block/block', elements=[north_face])

    # Rock Tools
    for rock in ROCK_CATEGORIES:
        for rock_item in ROCK_ITEMS:
            rm.item_model(('stone', '%s' % rock_item, '%s' % rock), \
                'tfc:item/stone/%s' % rock_item, \
                parent='item/handheld') \
                .with_lang(lang('Stone %s' % rock_item))
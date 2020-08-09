#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # Rock block variants
    for rock in ROCKS.keys():
        for block_type in ROCK_BLOCK_TYPES:
            if block_type == 'spike':
                # Spikes have special block states
                rm.blockstate(('rock', block_type, rock), variants=dict(
                    ('part=%s' % part, {'model': 'tfc:block/rock/%s/%s_%s' % (block_type, rock, part)}) for part in
                    ROCK_SPIKE_PARTS)) \
                    .with_lang(lang('%s Spike', rock)) \
                    .with_block_loot("tfc:rock/rock/%s" % rock)
                rm.item_model(('rock', block_type, rock), 'tfc:block/rock/raw/%s' % rock, parent='tfc:block/rock/spike/%s_base' % rock)
                for part in ROCK_SPIKE_PARTS:
                    rm.block_model(('rock', block_type, '%s_%s' % (rock, part)), {
                        'texture': 'tfc:block/rock/raw/%s' % rock,
                        'particle': 'tfc:block/rock/raw/%s' % rock
                    }, parent='tfc:block/rock/spike_%s' % part)
            else:
                 block = rm.blockstate(('rock', block_type, rock)) \
                    .with_block_model('tfc:block/rock/%s/%s' % (block_type, rock)) \
                    .with_item_model()
                 if block_type in CUTTABLE_ROCKS:
                    # Stairs
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_stairs()
                    rm.block_loot('tfc:rock/' + block_type + '/' + rock + '_stairs','tfc:rock/' + block_type + '/' + rock + '_stairs')
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_stairs',lang('%s %s Stairs', rock, block_type))
                    # Slabs
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_slab()
                    slab_namespace = 'tfc:rock/' + block_type + '/' + rock + '_slab'
                    rm.block_loot(slab_namespace,{"rolls":1,"entries":[{"type":"minecraft:item","functions":[{"function":"minecraft:set_count","conditions":[{"condition":"minecraft:block_state_property","block":slab_namespace,"properties":{"type":"double"}}],"count":2},{"function":"minecraft:explosion_decay"}],"name":slab_namespace}]})
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_slab',lang('%s %s Slab', rock, block_type))
                    # Walls
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_wall()
                    rm.block_loot('tfc:rock/' + block_type + '/' + rock + '_wall','tfc:rock/' + block_type + '/' + rock + '_wall')
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_wall',lang('%s %s Wall', rock, block_type))
                    rm.block_tag('minecraft:walls','tfc:rock/' + block_type + '/' + rock + '_wall')
                 if block_type == 'raw':
                    block.with_block_loot({"entries": "tfc:rock/rock/%s" % rock, "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 3, "type": "minecraft:uniform"}}]})
                 else:
                    block.with_block_loot('tfc:rock/%s/%s' % (block_type, rock))
                 if block_type in {'smooth', 'raw', 'chiseled'}:
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

    # Peat
    rm.blockstate('peat') \
        .with_block_model('tfc:block/peat') \
        .with_item_model() \
        .with_block_loot('tfc:peat') \
        .with_lang(lang('Peat'))

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
            .with_block_loot('tfc:clay/%s' % soil) \
            .with_lang(lang('%s Clay Dirt', soil)) \
            .with_item_model()

    # Grass
    north_face = {
        'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#texture', 'cullface': 'north'}}
    }
    north_face_tint0 = {
        'from': [0, 0, 0], 'to': [16, 16, 0],
        'faces': {'north': {'texture': '#overlay', 'cullface': 'north', 'tintindex': 0}}
    }

    # Peat Grass
    rm.blockstate_multipart(('peat_grass'), [
        {'model': 'tfc:block/peat_grass/peat_grass_top', 'x': 270},
        {'model': 'tfc:block/peat_grass/peat_grass_bottom', 'x': 90},
        ({'north': True}, {'model': 'tfc:block/peat_grass/peat_grass_top'}),
        ({'east': True}, {'model': 'tfc:block/peat_grass/peat_grass_top', 'y': 90}),
        ({'south': True}, {'model': 'tfc:block/peat_grass/peat_grass_top', 'y': 180}),
        ({'west': True}, {'model': 'tfc:block/peat_grass/peat_grass_top', 'y': 270}),
        ({'north': False}, {'model': 'tfc:block/peat_grass/peat_grass_side'}),
        ({'east': False}, {'model': 'tfc:block/peat_grass/peat_grass_side', 'y': 90}),
        ({'south': False}, {'model': 'tfc:block/peat_grass/peat_grass_side', 'y': 180}),
        ({'west': False}, {'model': 'tfc:block/peat_grass/peat_grass_side', 'y': 270}),
    ]) \
        .with_block_loot('tfc:peat') \
        .with_tag('grass') \
        .with_lang(lang('Peat Grass'))
    # Peat Grass Models, one for the side, top and bottom
    rm.block_model(('peat_grass', 'peat_grass_top'), {
        'overlay': 'tfc:block/grass_top',
        'particle': 'tfc:block/peat'
    }, parent='block/block', elements=[north_face_tint0])
    rm.block_model(('peat_grass','peat_grass_side'), {
        'overlay': 'tfc:block/grass_side',
        'texture': 'tfc:block/peat',
        'particle': 'tfc:block/peat'
    }, parent='block/block', elements=[north_face, north_face_tint0])
    rm.block_model(('peat_grass','peat_grass_bottom'), {
        'texture': 'tfc:block/peat',
        'particle': 'tfc:block/peat'
    }, parent='block/block', elements=[north_face])

    #Grass Blocks
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

    # Rock Items
    for rock in ROCKS.keys():
        for misc_rock_item in MISC_ROCK_ITEMS:
            rm.item_model(('rock', '%s' % misc_rock_item, '%s' % rock), 'tfc:item/rock/%s/%s' % (misc_rock_item,rock), parent='item/handheld') \
                .with_lang(lang('%s %s' % (rock, misc_rock_item)))

    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type in metal_data.types or metal_item_data.type == 'all':
                rm.item_model(('metal', '%s' % metal_item, '%s' % metal), 'tfc:item/metal/%s/%s' % (metal_item, metal), parent=metal_item_data.parent_model) \
                    .with_lang(lang('%s %s' % (metal, metal_item)))

        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type in metal_data.types or metal_block_data.type == 'all':
                rm.blockstate(('metal', '%s' % metal_block, metal)) \
                    .with_block_model({
                    'all': 'tfc:block/metal/%s' % metal,
                    'particle': 'tfc:block/metal/%s' % metal
                }, parent=metal_block_data.parent_model) \
                    .with_block_loot('tfc:metal/%s/%s' % (metal_block, metal)) \
                    .with_lang(lang('%s %s' % (metal, metal_block))) \
                    .with_item_model()

    # Gems
    for gem in GEMS:
        for grade in GEM_GRADES:
            rm.item_model(('gem', grade, gem), 'tfc:item/gem/%s/%s' % (grade, gem)) \
                .with_lang(lang('%s %s' % (grade, gem)))

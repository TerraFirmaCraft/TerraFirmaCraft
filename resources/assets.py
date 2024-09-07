#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import itertools

from mcresources import ResourceManager, ItemContext, utils, block_states, loot_tables, atlases, BlockContext
from mcresources.type_definitions import ResourceIdentifier, JsonObject

from constants import *

TAG_SHEARS = 'c:tools/shear'
TAG_SHARP = 'tfc:tools/sharp'

STICKS_WHEN_NOT_SHEARED = loot_tables.alternatives({
    'name': 'minecraft:stick',
    'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.random_chance(0.2)],
    'functions': [loot_tables.set_count(1, 2)]
}, {
    'name': 'minecraft:stick',
    'conditions': [loot_tables.random_chance(0.05)],
    'functions': [loot_tables.set_count(1, 2)]
}, conditions=[loot_tables.inverted(loot_tables.any_of(loot_tables.match_tag(TAG_SHEARS), loot_tables.silk_touch()))])


def copy_block_entity(*components: str):
    return {
        'function': 'minecraft:copy_components',
        'source': 'block_entity',
        'include': ['minecraft:custom_name'] + list(components)
    }


def generate(rm: ResourceManager):

    # Rock Type Blocks
    for rock, rock_data in ROCKS.items():

        # Aqueducts
        block = rm.blockstate_multipart(('rock', 'aqueduct', rock), *[
            {'model': 'tfc:block/rock/aqueduct/%s/base' % rock},
            ({'north': 'false'}, {'model': 'tfc:block/rock/aqueduct/%s/north' % rock}),
            ({'east': 'false'}, {'model': 'tfc:block/rock/aqueduct/%s/east' % rock}),
            ({'south': 'false'}, {'model': 'tfc:block/rock/aqueduct/%s/south' % rock}),
            ({'west': 'false'}, {'model': 'tfc:block/rock/aqueduct/%s/west' % rock}),
        ])

        block.with_lang(lang('%s aqueduct', rock))
        block.with_block_loot('tfc:rock/aqueduct/%s' % rock)

        rm.item_model(('rock', 'aqueduct', rock), parent='tfc:block/rock/aqueduct/%s/base' % rock, no_textures=True)

        textures = {'texture': 'tfc:block/rock/bricks/%s' % rock, 'particle': 'tfc:block/rock/bricks/%s' % rock}
        rm.block_model('rock/aqueduct/%s/base' % rock, textures, parent='tfc:block/aqueduct/base')
        rm.block_model('rock/aqueduct/%s/north' % rock, textures, parent='tfc:block/aqueduct/north')
        rm.block_model('rock/aqueduct/%s/east' % rock, textures, parent='tfc:block/aqueduct/east')
        rm.block_model('rock/aqueduct/%s/south' % rock, textures, parent='tfc:block/aqueduct/south')
        rm.block_model('rock/aqueduct/%s/west' % rock, textures, parent='tfc:block/aqueduct/west')

        # Spikes
        block = rm.blockstate(('rock', 'spike', rock), variants=dict(('part=%s' % part, {'model': 'tfc:block/rock/spike/%s_%s' % (rock, part)}) for part in ROCK_SPIKE_PARTS))
        block.with_lang(lang('%s spike', rock))
        block.with_block_loot('1-2 tfc:rock/loose/%s' % rock)

        # Individual models
        rm.item_model(('rock', 'spike', rock), 'tfc:block/rock/raw/%s' % rock, parent='tfc:block/rock/spike/%s_base' % rock)
        for part in ROCK_SPIKE_PARTS:
            rm.block_model(('rock', 'spike', '%s_%s' % (rock, part)), {
                'texture': 'tfc:block/rock/raw/%s' % rock,
                'particle': 'tfc:block/rock/raw/%s' % rock
            }, parent='tfc:block/rock/spike_%s' % part)

        # Loose Rocks
        # One block state and multiple models for the block
        rm.blockstate('rock/loose/%s' % rock, variants=dict(('count=%s' % i, four_ways('tfc:block/rock/loose/%s_%s' % (rock, i))) for i in range(1, 4)), use_default_model=False)
        rm.blockstate('rock/mossy_loose/%s' % rock, variants=dict(('count=%s' % i, four_ways('tfc:block/rock/loose/%s_%s_mossy' % (rock, i))) for i in range(1, 4)), use_default_model=False)
        for i in range(1, 4):
            rm.block_model('tfc:rock/loose/%s_%s' % (rock, i), {'texture': 'tfc:block/rock/raw/%s' % rock}, parent='tfc:block/rock/loose_%s_%s' % (rock_data.category, i))
            rm.block_model('tfc:rock/loose/%s_%s_mossy' % (rock, i), {'texture': 'tfc:block/rock/mossy_cobble/%s' % rock}, parent='tfc:block/rock/loose_%s_%s' % (rock_data.category, i))

        for variant in ('loose', 'mossy_loose'):
            block = rm.block(('rock', variant, rock))
            block.with_lang(lang('%s %s rock', variant, rock)).with_block_loot({
                'name': 'tfc:rock/%s/%s' % (variant, rock),
                'functions': [
                    {**loot_tables.set_count(2), 'conditions': [loot_tables.block_state_property('tfc:rock/%s/%s[count=2]' % (variant, rock))]},
                    {**loot_tables.set_count(3), 'conditions': [loot_tables.block_state_property('tfc:rock/%s/%s[count=3]' % (variant, rock))]},
                    loot_tables.explosion_decay()
                ]
            })
            if variant == 'loose':
                rm.item_model(('rock', variant, rock), 'tfc:item/loose_rock/%s' % rock)
            else:
                rm.item_model(('rock', variant, rock), 'tfc:item/loose_rock/%s' % rock, 'tfc:item/loose_rock/moss')

        # Pressure Plate
        block = rm.block(('rock', 'pressure_plate', rock))
        block.make_pressure_plate(pressure_plate_suffix='', texture='tfc:block/rock/raw/%s' % rock)
        block.with_lang(lang('%s pressure plate', rock))
        block.with_block_loot('tfc:rock/pressure_plate/%s' % rock)

        # Button
        block = rm.block(('rock', 'button', rock))
        block.make_button(button_suffix='', texture='tfc:block/rock/raw/%s' % rock)
        block.with_lang(lang('%s button', rock))
        block.with_block_loot('tfc:rock/button/%s' % rock)

        def rock_lang(_lhs: str, _rhs: str):
            if _lhs in ('smooth', 'raw', 'hardened'):
                return _lhs, _rhs
            if _lhs == 'chiseled':
                return _rhs, _lhs + ' bricks'
            return _rhs, _lhs

        for block_type in ('raw', 'hardened', 'bricks', 'cobble', 'gravel', 'smooth', 'mossy_cobble', 'mossy_bricks', 'cracked_bricks', 'chiseled'):
            if block_type in ('raw', 'hardened'):
                normal = 'tfc:block/rock/raw/%s' % rock
                mirror = normal + '_mirrored'
                if rock in ('chalk', 'schist', 'marble', 'phyllite', 'slate', 'chert', 'dolomite'):
                    block = rm.blockstate(('rock', block_type, rock), variants={
                        'axis=x': [{'model': normal, 'x': 90, 'y': 90}, {'model': mirror, 'x': 90, 'y': 90}],
                        'axis=y': [{'model': normal}, {'model': mirror}],
                        'axis=z': [{'model': normal, 'x': 90}, {'model': mirror, 'x': 90}],
                    }, use_default_model=False)
                else:
                    block = rm.blockstate(('rock', block_type, rock), variants={
                        'axis=x': [{'model': normal, 'x': 90, 'y': 90}, {'model': mirror, 'x': 90, 'y': 90}],
                        'axis=y': [{'model': normal}, {'model': mirror}, {'model': normal, 'y': 180}, {'model': mirror, 'y': 180}],
                        'axis=z': [{'model': normal, 'x': 90}, {'model': mirror, 'x': 90}, {'model': normal, 'x': 90, 'y': 180}, {'model': mirror, 'x': 90, 'y': 180}],
                    }, use_default_model=False)
                if rock in ('shale', 'claystone'):
                    if block_type == 'raw':
                        for suffix in ('', '_mirrored'):
                            rm.block_model(('rock', 'raw', rock + suffix), {
                                'side': 'tfc:block/rock/raw/%s' % rock,
                                'end': 'tfc:block/rock/raw/%s_top' % rock
                            }, parent='minecraft:block/cube_column' + suffix)
                else:
                    for suffix in ('', '_mirrored'):
                        rm.block_model(('rock', 'raw', rock + suffix), 'tfc:block/rock/raw/%s' % rock)
            else:
                block = rm.blockstate(('rock', block_type, rock))
                block.with_block_model()
            rm.item_model(('rock', block_type, rock), parent='tfc:block/rock/%s/%s' % ('raw' if block_type == 'hardened' else block_type, rock), no_textures=True)

            # Loot
            if block_type == 'raw' or block_type == 'hardened':
                block.with_block_loot(({
                    'name': 'tfc:rock/raw/%s' % rock,
                    'conditions': ['tfc:is_isolated'],
                }, {
                    'name': 'tfc:rock/loose/%s' % rock,
                    'functions': [loot_tables.set_count(1, 4)]
                }))
            elif block_type == 'gravel':
                block.with_block_loot((
                    when_silk_touch('tfc:rock/gravel/%s' % rock),
                    loot_tables.alternatives({
                        'type': 'minecraft:item',
                        'conditions': [loot_tables.fortune_table((0.1, 0.14285715, 0.25, 1.0))],
                        'name': 'minecraft:flint'
                    }, {
                        'type': 'minecraft:item',
                        'name': 'tfc:rock/gravel/%s' % rock
                    }, conditions=['minecraft:survives_explosion'])))
            else:
                block.with_block_loot('tfc:rock/%s/%s' % (block_type, rock))

            block.with_lang(lang('%s %s', *rock_lang(block_type, rock)))

        # Decorations
        for block_type in ROCK_DECORATIONS:
            type_name = block_type.replace('bricks', 'brick')

            # Stairs
            rm.block(('rock', block_type, rock)).make_stairs()
            rm.block(('rock', block_type, rock + '_stairs')).with_lang(lang('%s %s Stairs', *rock_lang(type_name, rock))).with_block_loot('tfc:rock/%s/%s_stairs' % (block_type, rock))
            # Slabs
            rm.block(('rock', block_type, rock)).make_slab()
            rm.block(('rock', block_type, rock + '_slab')).with_lang(lang('%s %s Slab', *rock_lang(type_name, rock)))
            slab_loot(rm, 'tfc:rock/%s/%s_slab' % (block_type, rock))
            # Walls
            rm.block(('rock', block_type, rock)).make_wall()
            rm.block(('rock', block_type, rock + '_wall')).with_lang(lang('%s %s Wall', *rock_lang(type_name, rock))).with_block_loot('tfc:rock/%s/%s_wall' % (block_type, rock))

        if rock_data.category == 'igneous_extrusive' or rock_data.category == 'igneous_intrusive':
            rm.blockstate('tfc:rock/anvil/%s' % rock, model='tfc:block/rock/anvil/%s' % rock).with_lang(lang('%s Anvil', rock)).with_block_loot('1-4 tfc:rock/loose/%s' % rock).with_item_model()
            textures = {'texture': 'tfc:block/rock/raw/%s' % rock}
            if rock in ('shale', 'claystone'):
                textures['top'] = 'tfc:block/rock/raw/%s_top' % rock
            rm.block_model('tfc:rock/anvil/%s' % rock, parent='tfc:block/rock/anvil', textures=textures)
            rm.blockstate('tfc:rock/magma/%s' % rock, model='tfc:block/rock/magma/%s' % rock).with_block_model(parent='minecraft:block/cube_all', textures={'all': 'tfc:block/rock/magma/%s' % rock}).with_lang(lang('%s magma block', rock)).with_item_model().with_block_loot('tfc:rock/magma/%s' % rock)

        # Ores
        for ore, ore_data in ORES.items():
            if ore_data.graded:
                # Small Ores / Groundcover Blocks
                block = rm.blockstate('tfc:ore/small_%s' % ore, variants={"": four_ways('tfc:block/groundcover/%s' % ore)}, use_default_model=False)
                block.with_lang(lang('small %s', ore)).with_block_loot('tfc:ore/small_%s' % ore)

                rm.item_model('tfc:ore/small_%s' % ore).with_lang(lang('small %s', ore))

                for grade in ORE_GRADES:
                    block = rm.blockstate(('ore', grade + '_' + ore, rock), 'tfc:block/ore/%s_%s/%s' % (grade, ore, rock))

                    if rock == 'claystone' or rock == 'shale':
                        block.with_block_model({
                            'side': 'tfc:block/rock/raw/%s' % rock,
                            'end': 'tfc:block/rock/raw/%s_top' % rock,
                            'overlay': 'tfc:block/ore/%s_%s' % (grade, ore),
                            'overlay_end': 'tfc:block/ore/%s_%s' % (grade, ore)
                        }, parent='tfc:block/ore_column')
                    else:
                        block.with_block_model({
                            'all': 'tfc:block/rock/raw/%s' % rock,
                            'overlay': 'tfc:block/ore/%s_%s' % (grade, ore),
                        }, parent='tfc:block/ore')
                    block.with_item_model()
                    block.with_lang(lang('%s %s %s', grade, rock, ore))
                    block.with_block_loot('tfc:ore/%s_%s' % (grade, ore))
            else:
                block = rm.blockstate(('ore', ore, rock), 'tfc:block/ore/%s/%s' % (ore, rock))
                if rock == 'claystone' or rock == 'shale':
                    block.with_block_model({
                        'side': 'tfc:block/rock/raw/%s' % rock,
                        'end': 'tfc:block/rock/raw/%s_top' % rock,
                        'overlay': 'tfc:block/ore/%s' % ore,
                        'overlay_end': 'tfc:block/ore/%s' % ore,
                    }, parent='tfc:block/ore_column')
                else:
                    block.with_block_model({
                        'all': 'tfc:block/rock/raw/%s' % rock,
                        'overlay': 'tfc:block/ore/%s' % ore
                    }, parent='tfc:block/ore')
                block.with_item_model()
                if ore == 'diamond':
                    block.with_lang(lang('%s kimberlite', rock))
                else:
                    block.with_lang(lang('%s %s', rock, ore))
                rm.block_loot('tfc:ore/%s/%s' % (ore, rock), 'tfc:ore/%s' % ore)

    # Loose Ore Items
    for ore, ore_data in ORES.items():
        if ore_data.graded:
            for grade in ORE_GRADES:
                rm.item_model('tfc:ore/%s_%s' % (grade, ore)).with_lang(lang('%s %s', grade, ore))
            rm.item_model('tfc:ore/small_%s' % ore).with_lang(lang('small %s', ore))
            rm.item_model('tfc:powder/%s' % ore).with_lang(lang('%s powder', ore))
        else:
            item = rm.item_model('tfc:ore/%s' % ore)
            if ore == 'diamond':
                item.with_lang(lang('kimberlite'))
            else:
                item.with_lang(lang(ore))

    # Sand
    for sand in SAND_BLOCK_TYPES:
        rm.blockstate(('sand', sand), variants={"": four_ways('tfc:block/sand/%s' % sand)}, use_default_model=False).with_block_model().with_item_model().with_block_loot('tfc:sand/%s' % sand).with_lang(lang('%s sand', sand))

        # Sandstone
        raw = 'tfc:block/sandstone/bottom/%s' % sand  # vanilla sandstone bottom
        top = 'tfc:block/sandstone/top/%s' % sand  # vanilla sandstone top
        cut = 'tfc:block/sandstone/cut/%s' % sand  # vanilla sandstone side

        for variant in ('raw', 'cut', 'smooth'):
            block = rm.blockstate(('%s_sandstone' % variant, sand)).with_block_loot('tfc:%s_sandstone/%s' % (variant, sand))
            if variant == 'raw':
                block.with_block_model(raw)
                block.make_slab(bottom_texture=raw, side_texture=raw, top_texture=raw)
                block.make_stairs(bottom_texture=raw, side_texture=raw, top_texture=raw)
                block.make_wall(texture=raw)
            elif variant == 'smooth':
                block.with_block_model(top)
                block.make_slab(bottom_texture=top, side_texture=top, top_texture=top)
                block.make_stairs(bottom_texture=top, side_texture=top, top_texture=top)
                block.make_wall(texture=top)
            else:
                block.with_block_model({
                    'end': top,
                    'side': cut
                }, parent='minecraft:block/cube_column')
                block.make_slab(bottom_texture=top, side_texture=cut, top_texture=top)
                block.make_stairs(bottom_texture=top, side_texture=cut, top_texture=top)
                block.make_wall(texture=cut)
            block.with_item_model()

            for extra in ('', '_slab', '_stairs', '_wall'):
                block = rm.block(('%s_sandstone' % variant, sand + extra))
                if extra == '_slab':
                    slab_loot(rm, 'tfc:%s_sandstone/%s%s' % (variant, sand, extra))
                else:
                    block.with_block_loot('tfc:%s_sandstone/%s%s' % (variant, sand, extra))
                block.with_lang(lang('%s %s sandstone' + extra, variant, sand))

    # Groundcover
    for misc in MISC_GROUNDCOVER:
        block = rm.block(('groundcover', misc))
        if misc == 'stick':
            variants = []
            for i in range(1, 5):
                variants += four_ways('tfc:block/groundcover/stick%s' % i)
            block.with_blockstate(variants={"": variants}, use_default_model=False)
        else:
            block.with_blockstate(variants={"": four_ways('tfc:block/groundcover/%s' % misc)}, use_default_model=False)
        block.with_lang(lang(misc))

        if misc in {'stick', 'flint', 'feather', 'rotten_flesh', 'bone'}:  # Vanilla ground cover
            block.with_block_loot('minecraft:%s' % misc)
        elif misc == 'salt_lick':
            block.with_block_loot('tfc:powder/salt')
        else:
            block.with_block_loot('tfc:groundcover/%s' % misc)
            rm.item_model(('groundcover', misc), 'tfc:item/groundcover/%s' % misc)

    for block in SIMPLE_BLOCKS:
        rm.blockstate(block).with_block_model().with_item_model().with_block_loot('tfc:%s' % block).with_lang(lang(block))
    rm.blockstate('thatch').with_block_model({'texture': 'tfc:block/thatch'}, parent='block/powder_snow').with_item_model().with_block_loot('tfc:thatch').with_lang(lang('thatch'))

    for name in ('pumpkin', 'melon'):
        # Loot table for the non-rotten block is done via code, as we need to select rotten/not via tile entity
        rm.block_model(name, parent='minecraft:block/%s' % name, no_textures=True)
        rm.blockstate(name, model='tfc:block/%s' % name).with_lang(lang(name))
        rm.item_model(name, 'tfc:item/food/%s' % name)
        rm.block_model('rotten_' + name, parent='minecraft:block/%s' % name, textures={'side': 'tfc:block/crop/rotten_%s_side' % name, 'end': 'tfc:block/crop/rotten_%s_top' % name})
        rm.blockstate('rotten_' + name, model='tfc:block/rotten_%s' % name).with_lang(lang('rotten %s', name)).with_block_loot({'name': 'tfc:%s' % name, 'functions': ['tfc:rotten']})

    rm.blockstate('jack_o_lantern', variants=four_rotations('minecraft:block/jack_o_lantern', (90, 0, 180, 270))).with_block_loot('minecraft:carved_pumpkin').with_lang(lang('Jack o\'Lantern'))
    rm.item_model('jack_o_lantern', parent='minecraft:block/jack_o_lantern', no_textures=True)

    rm.blockstate('freshwater_bubble_column', model='minecraft:block/water').with_lang(lang('bubble column'))
    rm.blockstate('saltwater_bubble_column', model='tfc:block/fluid/salt_water').with_lang(lang('bubble column'))

    for variant in ('raw', 'bricks', 'polished'):
        rm.blockstate(('alabaster', variant)).with_block_model().with_item_model().with_block_loot('tfc:alabaster/%s' % variant).with_lang(lang('%s Alabaster', variant) if variant != 'bricks' else lang('Alabaster %s', variant))

    for color in COLORS:
        rm.blockstate(('alabaster', 'raw', color)).with_block_model().with_item_model().with_block_loot('tfc:alabaster/raw/%s' % color).with_lang(lang('%s Raw Alabaster', color))
        bricks = rm.blockstate(('alabaster', 'bricks', color)).with_block_model().with_item_model().with_block_loot('tfc:alabaster/bricks/%s' % color).with_lang(lang('%s Alabaster Bricks', color))
        polished = rm.blockstate(('alabaster', 'polished', color)).with_block_model().with_item_model().with_block_loot('tfc:alabaster/polished/%s' % color).with_lang(lang('%s Polished Alabaster', color))
        bricks.make_slab().make_stairs().make_wall()
        polished.make_slab().make_stairs().make_wall()
        for extra in ('slab', 'stairs', 'wall'):
            block = rm.block(('alabaster', 'bricks', color + '_' + extra)).with_lang(lang('%s Alabaster Brick %s', color, extra))
            if extra != 'slab':
                block.with_block_loot('tfc:alabaster/bricks/%s_%s' % (color, extra))
            else:
                slab_loot(rm, 'tfc:alabaster/bricks/%s_%s' % (color, extra))
            block = rm.block(('alabaster', 'polished', color + '_' + extra)).with_lang(lang('%s Polished Alabaster %s', color, extra))
            if extra != 'slab':
                block.with_block_loot('tfc:alabaster/polished/%s_%s' % (color, extra))
            else:
                slab_loot(rm, 'tfc:alabaster/polished/%s_%s' % (color, extra))

    rm.item_model('torch', 'minecraft:block/torch')
    rm.item_model('dead_torch', 'tfc:block/torch_off')
    rm.block_model('dead_torch', parent='minecraft:block/template_torch', textures={'torch': 'tfc:block/torch_off'})
    rm.block_model('dead_wall_torch', parent='minecraft:block/template_torch_wall', textures={'torch': 'tfc:block/torch_off'})
    rm.blockstate('wall_torch', variants=four_rotations('minecraft:block/wall_torch', (None, 270, 90, 180))).with_lang(lang('Torch'))
    rm.blockstate('dead_wall_torch', variants=four_rotations('tfc:block/dead_wall_torch', (None, 270, 90, 180))).with_lang(lang('Burnt Out Torch'))
    rm.blockstate('torch', 'minecraft:block/torch').with_block_loot((
        {'name': 'minecraft:stick', 'conditions': ['tfc:is_burnt_out', loot_tables.random_chance(0.25)]},
        {'name': 'tfc:powder/wood_ash', 'conditions': ['tfc:is_burnt_out', loot_tables.random_chance(0.25)]},
        {'name': 'tfc:torch', 'conditions': [{'condition': 'minecraft:inverted', 'term': {'condition': 'tfc:is_burnt_out'}}]},
    )).with_lang(lang('Torch'))
    rm.blockstate('dead_torch', 'tfc:block/dead_torch').with_block_loot({'name': 'minecraft:stick', 'conditions': [loot_tables.random_chance(0.5)]}).with_lang(lang('Burnt Out Torch'))
    
    for wattle in ('woven_wattle', 'unstained_wattle'):
        rm.block_model('tfc:wattle/%s' % wattle, {
            'all': 'tfc:block/wattle/%s' % wattle if wattle != 'wattle' else 'tfc:block/empty',
            'particle': 'tfc:block/wattle/wattle_sides',
            'overlay': 'tfc:block/wattle/wattle_sides',
            'overlay_end': 'tfc:block/wattle/end'
        }, parent='tfc:block/cube_column_overlay')
    for part in ('top', 'bottom', 'left', 'right'):
        rm.block_model('tfc:wattle/%s' % part, {'side': 'tfc:block/wattle/%s' % part, 'end': 'tfc:block/wattle/end'}, parent='minecraft:block/cube_column')

    stages = ['empty', 'woven']
    wattle_variants = []
    wattle_variants += [({'woven': stage == 'woven'}, {'model': 'tfc:block/wattle/%s_wattle' % stage}) for stage in stages]
    wattle_variants += [({side: True, 'woven': stage == 'woven'}, {'model': 'tfc:block/wattle/%s%s' % ('empty_' if stage == 'empty' else '', side)}) for (stage, side) in itertools.product(stages, ('top', 'bottom', 'left', 'right'))]
    rm.blockstate_multipart('wattle', *wattle_variants).with_lang(lang('wattle'))
    rm.block_loot('wattle',
        {'name': 'tfc:wattle'},
        {'name': 'minecraft:stick', 'functions': [loot_tables.set_count(4)], 'conditions': [loot_tables.block_state_property('tfc:wattle[woven=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle[top=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle[bottom=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle[left=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle[right=true]')]}
    )
    rm.item_model('wattle', parent='tfc:block/wattle/empty_wattle', no_textures=True)

    rm.item_model('wattle/unstained', parent='tfc:block/wattle/unstained_wattle', no_textures=True)
    rm.blockstate_multipart('wattle/unstained',
        ({'model': 'tfc:block/wattle/unstained_wattle'}),
        ({'top': True}, {'model': 'tfc:block/wattle/top'}),
        ({'bottom': True}, {'model': 'tfc:block/wattle/bottom'}),
        ({'left': True}, {'model': 'tfc:block/wattle/left'}),
        ({'right': True}, {'model': 'tfc:block/wattle/right'})
    ).with_lang(lang('Unstained Wattle'))
    rm.block_loot('wattle/unstained',
        {'name': 'tfc:wattle/unstained'},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle/unstained[top=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle/unstained[bottom=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle/unstained[left=true]')]},
        {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property('tfc:wattle/unstained[right=true]')]}
    )

    for color in COLORS:
        wattle = 'tfc:wattle/%s' % color
        rm.block_model('tfc:wattle/stained/%s' % color, {
            'all': 'tfc:block/wattle/stained/%s' % color,
            'particle': 'tfc:block/wattle/wattle_sides',
            'overlay': 'tfc:block/wattle/wattle_sides',
            'overlay_end': 'tfc:block/wattle/end'
        }, parent='tfc:block/cube_column_overlay')
        rm.item_model(wattle, parent='tfc:block/wattle/stained/%s' % color, no_textures=True)
        rm.blockstate_multipart('wattle/%s' % color,
            ({'model': 'tfc:block/wattle/stained/%s' % color}),
            ({'top': True}, {'model': 'tfc:block/wattle/top'}),
            ({'bottom': True}, {'model': 'tfc:block/wattle/bottom'}),
            ({'left': True}, {'model': 'tfc:block/wattle/left'}),
            ({'right': True}, {'model': 'tfc:block/wattle/right'})
        ).with_lang(lang('%s stained wattle', color))
        rm.block_loot(wattle,
            {'name': wattle},
            {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property(wattle + '[top=true]')]},
            {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property(wattle + '[bottom=true]')]},
            {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property(wattle + '[left=true]')]},
            {'name': 'minecraft:stick', 'conditions': [loot_tables.block_state_property(wattle + '[right=true]')]}
        )

    # Fired large undyed vessel
    block = rm.blockstate('tfc:ceramic/large_vessel', variants={
        'sealed=true,axis=x': {'model': 'tfc:block/ceramic/large_vessel_sealed'},
        'sealed=true,axis=z': {'model': 'tfc:block/ceramic/large_vessel_sealed', 'y': 90},
        'sealed=false,axis=x': {'model': 'tfc:block/ceramic/large_vessel_opened'},
        'sealed=false,axis=z': {'model': 'tfc:block/ceramic/large_vessel_opened', 'y': 90}
    })
    block.with_lang(lang('large vessel'))
    block.with_block_loot(({
        'name': 'tfc:ceramic/large_vessel',
        'functions': [copy_block_entity('tfc:contents')],
        'conditions': [loot_tables.block_state_property('tfc:ceramic/large_vessel[sealed=true]')]
    }, 'tfc:ceramic/large_vessel'))
    rm.block_model('tfc:ceramic/large_vessel_sealed', textures={
        'top': 'tfc:block/ceramic/large_vessel/top',
        'side': 'tfc:block/ceramic/large_vessel/side',
        'bottom': 'tfc:block/ceramic/large_vessel/bottom',
        'particle': 'tfc:block/ceramic/large_vessel/side'
    }, parent='tfc:block/large_vessel_sealed')
    rm.block_model('tfc:ceramic/large_vessel_opened', textures={
        'side': 'tfc:block/ceramic/large_vessel/side',
        'bottom': 'tfc:block/ceramic/large_vessel/bottom',
        'particle': 'tfc:block/ceramic/large_vessel/side'
    }, parent='tfc:block/large_vessel_opened')
    rm.item_model(block.res, parent='tfc:block/ceramic/large_vessel_opened', no_textures=True, overrides=[override('tfc:block/ceramic/large_vessel_sealed', 'tfc:sealed')])

    # Unfired large undyed vessel
    rm.item_model('tfc:ceramic/unfired_large_vessel', {'top': 'tfc:block/ceramic/large_vessel/top_clay', 'side': 'tfc:block/ceramic/large_vessel/side_clay', 'bottom': 'tfc:block/ceramic/large_vessel/bottom_clay', 'particle': 'tfc:block/ceramic/large_vessel/side_clay'}, parent='tfc:block/ceramic/large_vessel_sealed').with_lang(lang('unfired large vessel'))

    for color in COLORS:
        vessel = 'tfc:ceramic/large_vessel/%s' % color
        block = rm.blockstate(vessel, variants={
            'sealed=true,axis=x': {'model': 'tfc:block/ceramic/%s_large_vessel_sealed' % color},
            'sealed=true,axis=z': {'model': 'tfc:block/ceramic/%s_large_vessel_sealed' % color, 'y': 90},
            'sealed=false,axis=x': {'model': 'tfc:block/ceramic/%s_large_vessel_opened' % color},
            'sealed=false,axis=z': {'model': 'tfc:block/ceramic/%s_large_vessel_opened' % color, 'y': 90}
        })
        block.with_lang(lang('%s large vessel', color))
        block.with_block_loot(({
            'name': vessel,
            'functions': [copy_block_entity('tfc:contents')],
            'conditions': [loot_tables.block_state_property(vessel + '[sealed=true]')]
        }, vessel))
        tex = 'tfc:block/ceramic/large_vessel/glazed/%s' % color
        normal_tex = {'top': tex + '/top', 'side': tex + '/side', 'bottom': tex + '/bottom'}
        clay_tex = {'top': tex + '/top_clay', 'bottom': tex + '/bottom_clay', 'side': tex + '/side_clay'}
        parent_model = 'tfc:block/ceramic/large_vessel_' + VESSEL_TYPES[color]
        if VESSEL_TYPES[color] == 'd':
            normal_tex['back'] = tex + '/back'
            clay_tex['back'] = tex + '/back_clay'
        if VESSEL_TYPES[color] == 'c' or VESSEL_TYPES[color] == 'd':
            normal_tex['front'] = tex + '/front'
            clay_tex['front'] = tex + '/front_clay'
        rm.block_model('tfc:ceramic/%s_large_vessel_sealed' % color, textures=normal_tex, parent=parent_model + '_sealed')
        rm.block_model('tfc:ceramic/%s_large_vessel_opened' % color, textures=normal_tex, parent=parent_model + '_opened')
        rm.item_model(block.res, parent='tfc:block/ceramic/%s_large_vessel_opened' % color, no_textures=True, overrides=[override('tfc:block/ceramic/%s_large_vessel_sealed' % color, 'tfc:sealed')])
        rm.item_model('tfc:ceramic/unfired_large_vessel/%s' % color, clay_tex, parent=parent_model + '_sealed').with_lang(lang('%s unfired large vessel', color))
        rm.block_model('ceramic/%s_small_vessel' % color, textures=normal_tex, parent='tfc:block/ceramic/glazed_small_vessel')
        rm.block_model('ceramic/%s_small_vessel_unfired' % color, textures=clay_tex, parent='tfc:block/ceramic/glazed_small_vessel')

    rm.blockstate('charcoal_pile', variants=dict((('layers=%d' % i), {'model': 'tfc:block/charcoal_pile/charcoal_height%d' % (i * 2) if i != 8 else 'tfc:block/charcoal_pile/charcoal_block'}) for i in range(1, 1 + 8))).with_lang(lang('Charcoal Pile')).with_block_loot('minecraft:charcoal')
    rm.blockstate('charcoal_forge', variants=dict((('heat_level=%d' % i), {'model': 'tfc:block/charcoal_forge/heat_%d' % i}) for i in range(0, 7 + 1))).with_lang(lang('Forge')).with_block_loot('7 minecraft:charcoal')
    rm.blockstate('log_pile', variants={'axis=x': {'model': 'tfc:block/log_pile', 'y': 90, 'x': 90}, 'axis=z': {'model': 'tfc:block/log_pile', 'x': 90}}) \
        .with_block_model(textures={'side': 'tfc:block/log_pile_side', 'end': 'tfc:block/log_pile_front'}, parent='minecraft:block/cube_column_horizontal').with_lang(lang('Log Pile'))
    rm.blockstate('burning_log_pile', model='tfc:block/burning_log_pile').with_block_model(parent='minecraft:block/cube_all', textures={'all': 'tfc:block/devices/charcoal_forge/lit'}).with_lang(lang('Burning Log Pile'))

    for stage in range(0, 7 + 1):
        rm.block_model('charcoal_forge/heat_%d' % stage, parent='tfc:block/charcoal_forge/template_forge', textures={'top': 'tfc:block/devices/charcoal_forge/%d' % stage})

    # Uses a custom block model
    rm.blockstate('crucible').with_item_model().with_lang(lang('crucible')).with_block_loot({
        'name': 'tfc:crucible',
        'functions': [copy_block_entity('tfc:crucible')]
    })

    block = rm.block('thatch_bed')
    block.with_lang(lang('thatch bed'))
    rm.item_model('thatch_bed', 'tfc:item/thatch_bed')

    rm.blockstate('nest_box', model='tfc:block/nest_box').with_block_loot('tfc:nest_box').with_lang(lang('nest box')).with_item_model()

    for bell, bar, post in (('brass', 'dolomite', 'ash'), ('bronze', 'schist', 'aspen')):
        bell_name = '%s_bell' % bell
        alt_facing = {'east': 0, 'north': 270, 'south': 90, 'west': 180}
        block = rm.blockstate(bell_name, variants=dict(('attachment=%s,facing=%s' % (a, f), {'model': 'tfc:block/bell/%s_%s' % (bell, a), 'y': y if a != 'single_wall' else alt_facing[f]}) for a in ('ceiling', 'double_wall', 'floor', 'single_wall') for f, y in (('east', 90), ('north', 0), ('south', 180), ('west', 270))))
        for variant, vanilla in (('ceiling', 'ceiling'), ('double_wall', 'between_walls'), ('floor', 'floor'), ('single_wall', 'wall')):
            rm.block_model('bell/%s_%s' % (bell, variant), parent='minecraft:block/bell_%s' % vanilla, textures={'bar': 'tfc:block/rock/raw/%s' % bar, 'post': 'tfc:block/wood/planks/%s' % post, 'particle': 'tfc:block/metal/smooth/%s' % bell})
        block.with_lang(lang('%s bell', bell)).with_block_loot('tfc:%s' % bell_name)
        rm.item_model('tfc:%s' % bell_name, 'tfc:item/%s' % bell_name)

    rm.blockstate('firepit', variants={
        'lit=true,axis=x': {'model': 'tfc:block/firepit_lit'},
        'lit=true,axis=z': {'model': 'tfc:block/firepit_lit', 'y': 90},
        'lit=false,axis=x': {'model': 'tfc:block/firepit_unlit'},
        'lit=false,axis=z': {'model': 'tfc:block/firepit_unlit', 'y': 90}
    }).with_lang(lang('Firepit')).with_block_loot('tfc:powder/wood_ash')
    rm.item_model('firepit', 'tfc:item/firepit')

    for stage in ('cold', 'dried', 'fresh', 'white', 'red'):
        for i in range(1, 5):
            rm.block_model('firepit_log_%s_%s' % (i, stage), {'all': 'tfc:block/devices/firepit/log_%s' % stage}, parent='tfc:block/firepit_log_%s' % i)

    rm.blockstate_multipart('grill',
        ({'axis': 'x'}, {'model': 'tfc:block/firepit_grill'}),
        ({'axis': 'z'}, {'model': 'tfc:block/firepit_grill', 'y': 90}),
        ({'lit': True, 'axis': 'x'}, {'model': 'tfc:block/firepit_lit_low'}),
        ({'lit': True, 'axis': 'z'}, {'model': 'tfc:block/firepit_lit_low', 'y': 90}),
        ({'lit': False, 'axis': 'x'}, {'model': 'tfc:block/firepit_unlit'}),
        ({'lit': False, 'axis': 'z'}, {'model': 'tfc:block/firepit_unlit', 'y': 90})
    ).with_lang(lang('Grill')).with_block_loot('tfc:powder/wood_ash', 'tfc:wrought_iron_grill')
    rm.item_model('grill', 'tfc:item/firepit_grill')

    rm.blockstate_multipart('pot',
        ({'axis': 'x'}, {'model': 'tfc:block/firepit_pot'}),
        ({'axis': 'z'}, {'model': 'tfc:block/firepit_pot', 'y': 90}),
        ({'lit': True, 'axis': 'x'}, {'model': 'tfc:block/firepit_lit_low'}),
        ({'lit': True, 'axis': 'z'}, {'model': 'tfc:block/firepit_lit_low', 'y': 90}),
        ({'lit': False, 'axis': 'x'}, {'model': 'tfc:block/firepit_unlit'}),
        ({'lit': False, 'axis': 'z'}, {'model': 'tfc:block/firepit_unlit', 'y': 90})
    ).with_lang(lang('Pot')).with_block_loot('tfc:powder/wood_ash', 'tfc:ceramic/pot')
    rm.item_model('pot', 'tfc:item/firepit_pot')

    block = rm.blockstate('powderkeg', variants={
        'lit=false,sealed=true': {'model': 'tfc:block/powderkeg_sealed'},
        'lit=false,sealed=false': {'model': 'tfc:block/powderkeg'},
        'lit=true,sealed=true': {'model': 'tfc:block/powderkeg_lit'},
        'lit=true,sealed=false': {'model': 'tfc:block/powderkeg'}  # cannot occur
    }).with_lang(lang('Powderkeg'))
    block.with_block_loot(({
        'name': 'tfc:powderkeg',
        'functions': [copy_block_entity('tfc:contents')],
        'conditions': [loot_tables.block_state_property('tfc:powderkeg[sealed=true]')]
    }, 'tfc:powderkeg'))
    block.with_item_model(overrides=[override('tfc:block/powderkeg_sealed', 'tfc:sealed')])

    states: stages = [({'model': 'tfc:block/composter/composter'})]
    for i in range(1, 9):
        for age in ('normal', 'ready', 'rotten'):
            rm.block_model('tfc:composter/%s_%s' % (age, i), parent='tfc:block/composter/compost_%s' % i, textures={'0': 'tfc:block/devices/composter/%s' % age})
            states.append(({'type': age, 'stage': i}, {'model': 'tfc:block/composter/%s_%s' % (age, i)}))
    rm.blockstate_multipart('composter', *states).with_lang(lang('composter')).with_block_loot('tfc:composter')
    rm.item_model('composter', parent='tfc:block/composter/composter', no_textures=True)

    rm.blockstate('quern', 'tfc:block/quern').with_item_model().with_lang(lang('Quern')).with_block_loot('tfc:quern')

    rm.blockstate('bloom', variants=dict((('layers=%d' % i), {'model': 'tfc:block/bloom/bloom_height%d' % (i * 2) if i != 8 else 'tfc:block/bloom/bloom_block'}) for i in range(1, 1 + 8))).with_lang(lang('Bloom'))
    rm.item_model('bloom', parent='tfc:block/bloom/bloom_block', no_textures=True)

    rm.blockstate('molten', variants=dict(
        ('layers=%s,lit=%s' % (i, j), {'model': 'tfc:block/molten/molten%s%s' % (k, l)})
        for i, l in ((1, '_height4'), (2, '_height8'), (3, '_height12'), (4, '_block'))
        for j, k in (('true', '_lit'), ('false', ''))
    )).with_lang(lang('Molten'))
    rm.item_model('molten', parent='tfc:block/molten/molten_block', no_textures=True)

    rm.blockstate('bloomery', variants=dict(
        ('facing=%s,open=%s,lit=%s' % (d, b, l), {'model': m, 'y': r})
        for d, r in (('north', None), ('east', 90), ('south', 180), ('west', 270))
        for b, l, m in (('true', 'false', 'tfc:block/bloomery/open_off'), ('false', 'false', 'tfc:block/bloomery/closed_off'), ('false', 'true', 'tfc:block/bloomery/closed_on'), ('true', 'true', 'tfc:block/bloomery/open_on'))
    )).with_lang(lang('Bloomery')).with_block_loot('tfc:bloomery')
    rm.item_model('bloomery', {'all': 'tfc:block/devices/bloomery/off'}, parent='tfc:block/bloomery/inventory')

    rm.item_model('raw_iron_bloom', 'tfc:item/bloom/unrefined').with_lang(lang('Raw Iron Bloom'))
    rm.item_model('refined_iron_bloom', 'tfc:item/bloom/refined').with_lang(lang('Refined Iron Bloom'))

    block = rm.blockstate('blast_furnace', variants={
        'lit=false': {'model': 'tfc:block/blast_furnace/unlit'},
        'lit=true': {'model': 'tfc:block/blast_furnace/lit'}
    })
    block.with_lang(lang('blast furnace'))
    block.with_block_loot('tfc:blast_furnace')
    rm.item_model(block.res, parent='tfc:block/blast_furnace/unlit', no_textures=True)
    rm.block_model('blast_furnace/unlit', {'side': 'tfc:block/devices/blast_furnace/side', 'end': 'tfc:block/devices/blast_furnace/top', 'particle': 'tfc:block/devices/blast_furnace/side'}, 'block/cube_column')
    rm.block_model('blast_furnace/lit', {'side': 'tfc:block/devices/blast_furnace/side_lit', 'end': 'tfc:block/devices/blast_furnace/top_lit', 'particle': 'tfc:block/devices/blast_furnace/side_lit'}, 'block/cube_column')

    rm.blockstate('placed_item', 'tfc:block/empty').with_lang(lang('placed items'))
    rm.blockstate('scraping', 'tfc:block/scraping').with_lang(lang('scraped item'))
    rm.custom_block_model('scraping', 'tfc:scraping', {})
    rm.blockstate('pit_kiln', variants=dict((('stage=%d' % i), {'model': 'tfc:block/pitkiln/pitkiln_%d' % i}) for i in range(0, 1 + 16))).with_lang(lang('Pit Kiln'))
    rm.blockstate('minecraft:slime_block', 'tfc:block/glue_block')
    rm.item_model('minecraft:slime_block', parent='tfc:block/glue_block', no_textures=True)
    rm.block_model('glue_block', {'particle': 'tfc:block/glue_block', 'texture': 'tfc:block/glue_block'}, parent='minecraft:block/slime_block')
    rm.blockstate('minecraft:flower_pot', model='tfc:block/flower_pot')
    rm.block_model('flower_pot', {'dirt': 'tfc:block/dirt/loam'}, parent='minecraft:block/flower_pot')

    # Dirt
    for soil in SOIL_BLOCK_VARIANTS:
        # Regular Dirt
        block = rm.blockstate(('dirt', soil), variants={'': [{'model': 'tfc:block/dirt/%s' % soil}]}, use_default_model=False)
        block.with_block_model().with_item_model().with_block_loot('tfc:dirt/%s' % soil).with_lang(lang('%s Dirt', soil))
        for variant in ('mud', 'rooted_dirt', 'mud_bricks'):
            rm.blockstate((variant, soil)).with_block_model().with_item_model().with_block_loot('tfc:%s/%s' % (variant, soil)).with_lang(lang('%s %s', soil, variant))

        rm.item_model('mud_brick/%s' % soil).with_lang(lang('%s mud brick', soil))
        mud_bricks = rm.block(('mud_bricks', soil))
        mud_bricks.make_slab()
        mud_bricks.make_stairs()
        mud_bricks.make_wall()
        for variant in ('_stairs', '_slab', '_wall'):
            block = rm.block('mud_bricks/%s%s' % (soil, variant)).with_lang(lang('%s mud brick%s', soil, variant))
            if variant == '_slab':
                slab_loot(rm, 'tfc:mud_bricks/%s%s' % (soil, variant))
            else:
                block.with_block_loot('tfc:mud_bricks/%s%s' % (soil, variant))

        for variant in ('dry', 'wet'):
            texture = {'mud': 'tfc:block/mud_bricks/%s' % soil if variant == 'dry' else 'tfc:block/mud/%s' % soil}
            for i in range(1, 5):
                rm.block_model('mud_bricks/%s_%s_%s' % (variant, i, soil), textures=texture, parent='tfc:block/mud_bricks/%s' % i)
        block = rm.blockstate(('drying_bricks', soil), variants=dict(
            ('count=%s,dried=%s' % (c, d), {'model': 'tfc:block/mud_bricks/%s_%s_%s' % (e, c, soil)})
            for c in range(1, 5) for d, e in (('true', 'dry'), ('false', 'wet'))
        )).with_lang(lang('wet %s mud bricks', soil))
        loot_pools = []
        for i in range(1, 5):
            for m, d in (('tfc:drying_bricks/%s' % soil, 'false'), ('tfc:mud_brick/%s' % soil, 'true')):
                loot_pools += [{'name': m, 'conditions': [loot_tables.block_state_property('tfc:drying_bricks/%s[count=%s,dried=%s]' % (soil, i, d))], 'functions': [loot_tables.set_count(i)]}]
        block.with_block_loot(*loot_pools)
        rm.item_model('tfc:drying_bricks/%s' % soil, 'tfc:item/mud_brick/%s_wet' % soil)

        # Clay Dirt
        block = rm.blockstate(('clay', soil), use_default_model=False)
        block.with_block_model()
        block.with_block_loot({
            'name': 'minecraft:clay_ball',
            'functions': [loot_tables.set_count(1, 3)]
        })
        block.with_lang(lang('%s Clay Dirt', soil))
        block.with_item_model()

        block = rm.block(('grass_path', soil))
        block.with_lang(lang('%s path', soil))
        block.with_block_loot('tfc:dirt/%s' % soil)

        rm.blockstate(('muddy_roots', soil), variants={
            'axis=x': {'model': 'tfc:block/muddy_roots/%s' % soil, 'x': 90, 'y': 90},
            'axis=y': {'model': 'tfc:block/muddy_roots/%s' % soil},
            'axis=z': {'model': 'tfc:block/muddy_roots/%s' % soil, 'x': 90}
        }).with_block_model({'end': 'tfc:block/mud/%s_roots_top' % soil, 'side': 'tfc:block/mud/%s_roots_side' % soil}, 'minecraft:block/cube_column').with_item_model().with_lang(lang('%s muddy roots', soil)).with_block_loot('tfc:muddy_roots/%s' % soil)

    # Grass
    north_face = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#texture', 'cullface': 'north'}}}
    north_face_overlay = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#overlay', 'cullface': 'north'}}}
    north_face_overlay_tint0 = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#overlay', 'cullface': 'north', 'tintindex': 1}}}

    rm.block_model('grass_top', textures={'overlay': 'tfc:block/grass_top', 'particle': 'tfc:block/grass_top'}, parent='block/block', elements=[north_face_overlay_tint0])
    rm.block_model('grass_snowy_top', textures={'overlay': 'minecraft:block/snow', 'particle': 'minecraft:block/snow'}, parent='block/block', elements=[north_face_overlay])
    rm.block_model('grass_side', textures={'overlay': 'tfc:block/grass_side', 'particle': 'tfc:block/grass_side'}, parent='block/block', elements=[north_face, north_face_overlay_tint0])
    rm.block_model('grass_snowy_side', textures={'overlay': 'tfc:block/grass_snowy_side', 'particle': '#texture'}, parent='block/block', elements=[north_face, north_face_overlay])
    rm.block_model('grass_bottom', textures={'texture': '#texture', 'particle': '#texture'}, parent='block/block', elements=[north_face])

    # Grass (Peat, Normal + Clay) - Helper Functions
    def grass_multipart(model: str):
        return [
            {'model': model + '/bottom', 'x': 90},
            ({'snowy': False}, [{'model': model + '/top', 'x': 270, 'y': y} for y in (90, None, 180, 270)]),
            ({'snowy': True}, [{'model': model + '/snowy_top', 'x': 270, 'y': y} for y in (90, None, 180, 270)]),
            ({'north': True, 'snowy': False}, {'model': model + '/top'}),
            ({'east': True, 'snowy': False}, {'model': model + '/top', 'y': 90}),
            ({'south': True, 'snowy': False}, {'model': model + '/top', 'y': 180}),
            ({'west': True, 'snowy': False}, {'model': model + '/top', 'y': 270}),
            ({'north': True, 'snowy': True}, {'model': model + '/snowy_top'}),
            ({'east': True, 'snowy': True}, {'model': model + '/snowy_top', 'y': 90}),
            ({'south': True, 'snowy': True}, {'model': model + '/snowy_top', 'y': 180}),
            ({'west': True, 'snowy': True}, {'model': model + '/snowy_top', 'y': 270}),
            ({'north': False, 'snowy': False}, {'model': model + '/side'}),
            ({'east': False, 'snowy': False}, {'model': model + '/side', 'y': 90}),
            ({'south': False, 'snowy': False}, {'model': model + '/side', 'y': 180}),
            ({'west': False, 'snowy': False}, {'model': model + '/side', 'y': 270}),
            ({'north': False, 'snowy': True}, {'model': model + '/snowy_side'}),
            ({'east': False, 'snowy': True}, {'model': model + '/snowy_side', 'y': 90}),
            ({'south': False, 'snowy': True}, {'model': model + '/snowy_side', 'y': 180}),
            ({'west': False, 'snowy': True}, {'model': model + '/snowy_side', 'y': 270})
        ]

    def grass_models(_name: ResourceIdentifier, _texture: str):
        for _variant in ('top', 'snowy_top', 'side', 'snowy_side', 'bottom'):
            rm.block_model((_name, _variant), {'texture': _texture}, parent='tfc:block/grass_%s' % _variant)

    # Peat Grass
    rm.blockstate_multipart('peat_grass', *grass_multipart('tfc:block/peat_grass')).with_block_loot('tfc:peat').with_lang(lang('Peat Grass'))
    grass_models('peat_grass', 'tfc:block/peat')

    # Grass Blocks
    for soil in SOIL_BLOCK_VARIANTS:
        for grass_var, dirt in (('grass', 'tfc:block/dirt/%s' % soil), ('clay_grass', 'tfc:block/clay/%s' % soil)):
            block = rm.blockstate_multipart((grass_var, soil), *grass_multipart('tfc:block/%s/%s' % (grass_var, soil)))
            if grass_var == 'grass':
                block.with_block_loot('tfc:dirt/%s' % soil)
            else:
                block.with_block_loot('1-3 minecraft:clay_ball')
            block.with_lang(lang('%s %s', soil, grass_var))
            grass_models((grass_var, soil), dirt)

        # Farmland
        block = rm.blockstate(('farmland', soil))
        block.with_block_model({
            'dirt': 'tfc:block/dirt/%s' % soil,
            'top': 'tfc:block/farmland/%s' % soil
        }, parent='block/template_farmland')
        block.with_item_model()
        block.with_block_loot('tfc:dirt/%s' % soil)
        block.with_lang(lang('%s farmland', soil))

    # Kaolin Clay
    clay_count = 2
    for color in KAOLIN_CLAY_TYPES:
        rm.blockstate('%s_kaolin_clay' % color, use_default_model=False).with_block_model().with_block_loot('1-%s tfc:kaolin_clay' % clay_count).with_item_model().with_lang(lang('%s kaolin clay', color))
        clay_count += 1
    rm.blockstate_multipart('kaolin_clay_grass', *grass_multipart('tfc:block/kaolin_clay_grass')).with_block_loot('1-2 tfc:kaolin_clay').with_lang(lang('kaolin clay grass'))
    grass_models('kaolin_clay_grass', 'tfc:block/kaolin_clay_grass')

    # Snow Piles
    block = rm.blockstate('snow_pile', variants=dict((('layers=%d' % i), {'model': 'minecraft:block/snow_height%d' % (i * 2) if i != 8 else 'minecraft:block/snow_block'}) for i in range(1, 1 + 8)))
    block.with_lang(lang('Snow Pile'))
    rm.item_model('snow_pile', parent='minecraft:block/snow_height2', no_textures=True)

    block = rm.blockstate('ice_pile', 'minecraft:block/ice').with_lang(lang('ice pile'))
    block.with_block_loot(when_silk_touch('minecraft:ice'))
    rm.item_model('ice_pile', parent='minecraft:item/ice', no_textures=True)

    # Loot table for snow blocks and snow piles - override the vanilla one to only return one snowball per layer
    def snow_block_loot_table(block: str):
        rm.block_loot(block, loot_tables.pool(loot_tables.alternatives(
            when_silk_touch('minecraft:snow'),
            'minecraft:snowball'
        ), conditions=({
            'condition': 'minecraft:entity_properties',
            'predicate': {},
            'entity': 'this'
        })))

    snow_block_loot_table('snow_pile')
    snow_block_loot_table('minecraft:snow')

    # Sea Ice
    block = rm.blockstate('sea_ice').with_block_model().with_item_model().with_lang(lang('sea ice'))
    block.with_block_loot(when_silk_touch('minecraft:ice'))

    # Hides
    for size in ('small', 'medium', 'large'):
        for hide in ('prepared', 'raw', 'scraped', 'sheepskin', 'soaked'):
            item = rm.item_model('%s_%s_hide' % (size, hide), 'tfc:item/hide/%s/%s' % (size, hide))
            if item != 'sheepskin':
                item.with_lang(lang('%s %s hide', size, hide))
            else:
                item.with_lang(lang('%s %s', size, hide))

    # Rock Tools
    for rock in ROCK_CATEGORIES:
        for rock_item in ROCK_CATEGORY_ITEMS:
            for suffix in ('', '_head'):
                rock_item = rock_item + suffix
                if suffix == '' and rock_item == 'javelin':
                    item = make_javelin(rm, 'stone/%s/%s' % (rock_item, rock), 'tfc:item/stone/%s' % rock_item)
                elif suffix == '' and rock_item == 'knife':
                    item = rm.item_model(('stone', rock_item, rock), 'tfc:item/stone/%s' % rock_item, parent='tfc:item/handheld_flipped')
                else:
                    item = rm.item_model(('stone', rock_item, rock), 'tfc:item/stone/%s' % rock_item, parent='item/handheld')
                item.with_lang(lang('stone %s', rock_item))

    # Rock Items
    for rock, rock_data in ROCKS.items():
        rm.item_model(('brick', rock), 'tfc:item/brick/%s' % rock).with_lang(lang('%s brick', rock))

    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_data.has(metal_item_data):
                texture = 'tfc:item/metal/%s/%s' % (metal_item, metal) if metal_item != 'shield' or metal in ('red_steel', 'blue_steel', 'wrought_iron') else 'tfc:item/metal/shield/%s_front' % metal
                if metal_item == 'fishing_rod':
                    rm.item_model(('metal', metal_item, metal + '_cast'), 'tfc:item/metal/fishing_rod/alt_cast' if metal == 'red_steel' or metal == 'blue_steel' else 'minecraft:item/fishing_rod_cast', parent='minecraft:item/fishing_rod')
                    item = rm.item_model(('metal', metal_item, metal), texture, parent='minecraft:item/handheld_rod', overrides=[override('tfc:item/metal/fishing_rod/%s_cast' % metal, 'tfc:cast')])
                elif metal_item == 'shield':
                    item = rm.item(('metal', metal_item, metal))  # Shields have a custom model for inventory and blocking
                elif metal_item == 'javelin':
                    item = make_javelin(rm, 'metal/%s/%s' % (metal_item, metal), 'tfc:item/metal/javelin/%s' % metal)
                elif metal_item in TFC_ARMOR_SECTIONS:
                    item = trim_model(rm, ('metal', metal_item, metal), 'tfc:item/metal/%s/%s' % (metal_item, metal), 'tfc:item/%s_trim' % metal_item)
                else:
                    item = rm.item_model(('metal', metal_item, metal), texture, parent=metal_item_data.parent_model)

                if metal_item == 'propick':
                    item.with_lang('%s Prospector\'s Pick' % lang(metal))  # .title() works weird w.r.t the possessive.
                elif metal_item == 'propick_head':
                    item.with_lang('%s Prospector\'s Pick Head' % lang(metal))
                else:
                    item.with_lang(lang('%s %s', metal, metal_item))

        texture = 'tfc:block/metal/smooth/%s' % metal

        if metal_data.type == 'all':

            # Anvil
            block = rm.blockstate('metal/anvil/%s' % metal, variants={
                'facing=north': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 90},
                'facing=east': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 180},
                'facing=south': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 270},
                'facing=west': {'model': 'tfc:block/metal/anvil/%s' % metal}
            })
            block.with_block_model({
                'all': texture,
                'particle': texture
            }, parent='tfc:block/anvil')
            block.with_block_loot('tfc:metal/anvil/%s' % metal)
            block.with_lang(lang('%s anvil', metal))
            block.with_item_model()

            # Chain
            block = rm.blockstate(('metal', 'chain', metal), variants={
                'axis=x': {'model': 'tfc:block/metal/chain/%s' % metal, 'x': 90, 'y': 90},
                'axis=y': {'model': 'tfc:block/metal/chain/%s' % metal},
                'axis=z': {'model': 'tfc:block/metal/chain/%s' % metal, 'x': 90}
            })
            block.with_lang(lang('%s chain', metal))
            block.with_block_loot('tfc:metal/chain/%s' % metal)
            rm.block_model(('metal', 'chain', metal), {
                'all': 'tfc:block/metal/chain/%s' % metal,
                'particle': 'tfc:block/metal/chain/%s' % metal
            }, parent='minecraft:block/chain')
            rm.item_model(('metal', 'chain', metal), 'tfc:item/metal/chain/%s' % metal)

            # Lamp
            block = rm.blockstate('metal/lamp/%s' % metal, variants={
                'hanging=false,lit=false': {'model': 'tfc:block/metal/lamp/%s_off' % metal},
                'hanging=true,lit=false': {'model': 'tfc:block/metal/lamp/%s_hanging_off' % metal},
                'hanging=false,lit=true': {'model': 'tfc:block/metal/lamp/%s_on' % metal},
                'hanging=true,lit=true': {'model': 'tfc:block/metal/lamp/%s_hanging_on' % metal},
            })
            block.with_lang(lang('%s lamp', metal))
            block.with_block_loot({
                'name': 'tfc:metal/lamp/%s' % metal,
                'functions': [{'function': 'tfc:copy_fluid'}]
            })
            rm.lang('block.tfc.metal.lamp.%s.filled' % metal, lang('filled %s lamp', metal))
            rm.block_model('tfc:metal/lamp/%s_hanging_on' % metal, {'metal': texture, 'chain': 'tfc:block/metal/chain/%s' % metal, 'lamp': 'tfc:block/lamp'}, parent='tfc:block/lamp_hanging')
            rm.block_model('tfc:metal/lamp/%s_hanging_off' % metal, {'metal': texture, 'chain': 'tfc:block/metal/chain/%s' % metal, 'lamp': 'tfc:block/lamp_off'}, parent='tfc:block/lamp_hanging')
            rm.block_model('tfc:metal/lamp/%s_on' % metal, {'metal': texture, 'lamp': 'tfc:block/lamp'}, parent='tfc:block/lamp')
            rm.block_model('tfc:metal/lamp/%s_off' % metal, {'metal': texture, 'lamp': 'tfc:block/lamp_off'}, parent='tfc:block/lamp')
            rm.item_model(('metal', 'lamp', metal))

            # Trapdoor
            block = rm.block('metal/trapdoor/%s' % metal)
            block.make_trapdoor(trapdoor_suffix='', texture='tfc:block/metal/trapdoor/%s' % metal)
            block.with_lang(lang('%s trapdoor', metal))
            block.with_block_loot('tfc:metal/trapdoor/%s' % metal)

            # Bars
            bars = 'metal/bars/%s' % metal
            block = rm.blockstate_multipart(
                'metal/bars/%s' % metal,
                ({'model': 'tfc:block/bars/%s_bars_post_ends' % metal}),
                ({'north': False, 'south': False, 'east': False, 'west': False}, {'model': 'tfc:block/bars/%s_bars_post' % metal}),
                ({'north': True, 'south': False, 'east': False, 'west': False}, {'model': 'tfc:block/bars/%s_bars_cap' % metal}),
                ({'north': False, 'south': False, 'east': True, 'west': False}, {'model': 'tfc:block/bars/%s_bars_cap' % metal, 'y': 90}),
                ({'north': False, 'south': True, 'east': False, 'west': False}, {'model': 'tfc:block/bars/%s_bars_cap_alt' % metal}),
                ({'north': False, 'south': False, 'east': False, 'west': True}, {'model': 'tfc:block/bars/%s_bars_cap_alt' % metal, 'y': 90}),
                ({'north': True}, {'model': 'tfc:block/bars/%s_bars_side' % metal}),
                ({'east': True}, {'model': 'tfc:block/bars/%s_bars_side' % metal, 'y': 90}),
                ({'south': True}, {'model': 'tfc:block/bars/%s_bars_side_alt' % metal}),
                ({'west': True}, {'model': 'tfc:block/bars/%s_bars_side_alt' % metal, 'y': 90}),
            )
            block.with_block_loot('tfc:%s' % bars)
            block.with_lang(lang('%s bars', metal))
            for var in ('post_ends', 'post', 'cap', 'cap_alt', 'side', 'side_alt'):
                rm.block_model('bars/%s_bars_%s' % (metal, var), parent='minecraft:block/iron_bars_%s' % var, textures={
                    'particle': 'tfc:block/metal/bars/%s' % metal,
                    'bars': 'tfc:block/metal/bars/%s' % metal,
                    'edge': texture
                })
            rm.item_model(bars, 'tfc:block/%s' % bars)

        # Storage Blocks (+ Stair, Slab)
        # Includes weathering variants for metals that support that
        for variant, word in (
            ('block', ''),
            ('exposed_block', 'exposed'),
            ('weathered_block', 'weathered'),
            ('oxidized_block', None)
        ):
            if metal_data.has_block(variant):
                if word is None:
                    word = OXIDIZED_METAL_NAMES[metal]
                block = rm.blockstate(('metal', variant, metal))
                block.with_block_model()
                block.with_lang(lang('%s %s plated block', word, metal))
                block.with_item_model()
                block.with_block_loot('tfc:metal/%s/%s' % (variant, metal))
                block.make_slab()
                block.make_stairs()
                rm.block('metal/%s/%s_slab' % (variant, metal)).with_lang(lang('%s %s plated slab', word, metal))
                slab_loot(rm, 'tfc:metal/%s/%s_slab' % (variant, metal))
                stairs = rm.block('metal/%s/%s_stairs' % (variant, metal))
                stairs.with_lang(lang('%s %s plated stairs', word, metal))
                stairs.with_block_loot('tfc:metal/%s/%s_stairs' % (variant, metal))

    for section in ARMOR_SECTIONS:
        trim_model(rm, 'minecraft:leather_%s' % section, 'minecraft:item/leather_%s' % section, 'tfc:item/%s_trim' % section.replace('leggings', 'greaves'), 'minecraft:item/leather_%s_overlay' % section)
        trim_model(rm, 'minecraft:chainmail_%s' % section, 'minecraft:item/chainmail_%s' % section, 'tfc:item/%s_trim' % section.replace('leggings', 'greaves'))

    # Misc Items
    for gem in GEMS:
        rm.item_model(('gem', gem)).with_lang(lang('cut %s', gem))
        rm.item_model(('powder', gem)).with_lang(lang('%s powder', gem))

    for powder in GENERIC_POWDERS.keys():
        rm.item_model(('powder', powder)).with_lang(lang('%s Powder', powder))

    for powder in POWDERS:
        rm.item_model(('powder', powder)).with_lang(lang(powder))

    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))

    rm.item_model('blowpipe/empty_gui', 'tfc:item/blowpipe')
    rm.item_model('blowpipe/ceramic_empty_gui', 'tfc:item/ceramic_blowpipe')
    rm.item_model('blowpipe/empty_held', parent='tfc:item/blowpipe/empty', no_textures=True)
    rm.item_model('blowpipe/ceramic_empty_held', {'0': 'tfc:block/glass/ceramic_blowpipe'}, parent='tfc:item/blowpipe/empty')

    def get_perspectives(model: str):
        return {
            'none': {'parent': model},
            'fixed': {'parent': model},
            'ground': {'parent': model},
            'gui': {'parent': model}
        }

    rm.item_model(('blowpipe', 'ceramic_blowpipe'), {'0': 'tfc:block/glass/ceramic_blowpipe'}, parent='tfc:item/blowpipe/blowpipe')
    for pref in ('', 'ceramic_'):
        rm.custom_item_model('%sblowpipe' % pref, 'neoforge:separate_transforms', {
            'base': {'parent': 'tfc:item/blowpipe/%sempty_held' % pref},
            'perspectives': get_perspectives('tfc:item/blowpipe/%sempty_gui' % pref)
        }).with_lang(lang('%sblowpipe', pref))
        rm.item_model('blowpipe/%sgui_cold' % pref, 'tfc:item/%sblowpipe_with_glass' % pref)
        rm.item_model('blowpipe/%sgui_hot' % pref, 'tfc:item/%sblowpipe_with_glass_hot' % pref)
        for i in range(0, 6):
            rm.item_model('blowpipe/%s%s' % (pref, i), {'1': 'tfc:block/glass/%s' % i}, parent='tfc:item/blowpipe/%sblowpipe' % pref)
            rm.custom_item_model('blowpipe/%s%s_st' % (pref, i), 'neoforge:separate_transforms', {
                'base': {'parent': 'tfc:item/blowpipe/%s%s' % (pref, i)},
                'perspectives': get_perspectives('tfc:item/blowpipe/%sgui_cold' % pref if i == 0 else 'tfc:item/blowpipe/%sgui_hot' % pref),
            })
        item = rm.item_model('%sblowpipe_with_glass' % pref, parent='tfc:item/blowpipe/%s0' % pref, no_textures=True, overrides=[
            override('tfc:item/blowpipe/%s0_st' % pref, 'tfc:heat', 0),
            override('tfc:item/blowpipe/%s1_st' % pref, 'tfc:heat', 0.3),
            override('tfc:item/blowpipe/%s2_st' % pref, 'tfc:heat', 0.4),
            override('tfc:item/blowpipe/%s3_st' % pref, 'tfc:heat', 0.5),
            override('tfc:item/blowpipe/%s4_st' % pref, 'tfc:heat', 0.75),
            override('tfc:item/blowpipe/%s5_st' % pref, 'tfc:heat', 0.9),
        ])
        item.with_lang(lang('%sblowpipe with glass', pref))

    rm.blockstate('wooden_bowl').with_block_model({'all': 'tfc:block/wooden_bowl'}, 'tfc:block/template_bowl').with_lang(lang('bowl')).with_block_loot('minecraft:bowl')
    rm.blockstate('ceramic/bowl').with_block_model({'all': 'tfc:block/ceramic_bowl'}, 'tfc:block/template_bowl').with_lang(lang('ceramic bowl')).with_block_loot('tfc:ceramic/bowl')

    rm.blockstate('barrel_rack').with_item_model().with_lang(lang('barrel rack')).with_block_loot('tfc:barrel_rack')
    rm.lang('item.tfc.pan.empty', lang('Empty Pan'))
    rm.item_model('firestarter', parent='item/handheld').with_lang(lang('firestarter'))

    for metal_id, metal in enumerate(('copper', 'silver', 'gold', 'tin')):
        ore = 'native_' + metal if metal != 'tin' else 'cassiterite'
        rm.item_model(('pan', ore, 'result'), {'material': 'tfc:block/metal/smooth/' + metal}, parent='tfc:item/pan/result')
        for rock_id, rock in enumerate(ROCKS.keys()):
            rm.item_model(('pan', ore, rock + '_full'), {'material': 'tfc:block/rock/gravel/%s' % rock}, parent='tfc:item/pan/full')
            rm.item_model(('pan', ore, rock + '_half'), {'material': 'tfc:block/rock/gravel/%s' % rock}, parent='tfc:item/pan/half')
            block = rm.blockstate(('deposit', ore, rock)).with_lang(lang('%s %s Deposit', rock, ore)).with_item_model()
            block.with_block_model({
                'all': 'tfc:block/rock/gravel/%s' % rock,
                'overlay': 'tfc:block/deposit/%s' % ore
            }, parent='tfc:block/ore')
            rare = DEPOSIT_RARES[rock]
            block.with_block_loot('tfc:deposit/%s/%s' % (ore, rock))
            rm.loot('%s_%s' % (ore, rock), loot_tables.alternatives({
               'name': 'tfc:ore/small_%s' % ore,
               'conditions': [loot_tables.random_chance(0.5)],  # 50% chance
            }, {
                'name': 'tfc:ore/small_%s' % ore,
                'conditions': [loot_tables.random_chance(0.1), 'tfc:is_sluice'],  # +10% chance (for sluice)
            }, {
               'name': 'tfc:rock/loose/%s' % rock,
               'conditions': [loot_tables.random_chance(0.5)],  # 25% chance
            }, {
                'name': 'tfc:rock/loose/%s' % rock if rock not in ('rhyolite', 'dacite', 'andesite') else 'tfc:groundcover/pumice',
                'conditions': [loot_tables.random_chance(0.25)],  # 6.25% chance... for when its not pumice this just rerolls loose rocks
            }, {
               'name': 'tfc:ore/%s' % rare,
               'conditions': [loot_tables.random_chance(0.0533)],  # 1% chance
            }), path='deposit', loot_type='minecraft:empty')

    rm.item_model(('pan', 'filled'), {'particle': 'tfc:item/pan/interior'}, parent='tfc:item/entity_with_transforms').with_lang(lang('Filled Pan'))

    rm.item_model('handstone', parent='tfc:item/handstone_healthy', no_textures=True, overrides=[override('tfc:item/handstone_damaged', 'tfc:damaged')]).with_lang(lang('handstone'))
    rm.item_model('handstone_damaged', {'handstone': 'tfc:block/devices/quern/handstone_top_damaged', 'particle': 'tfc:block/devices/quern/handstone_top_damaged', 'side': 'tfc:block/devices/quern/handstone_side_damaged'}, parent='tfc:item/handstone_healthy')

    # Pottery
    for pottery in SIMPLE_POTTERY:  # both fired and unfired items
        rm.item_model(('ceramic', pottery)).with_lang(lang(pottery))
        rm.item_model(('ceramic', 'unfired_' + pottery)).with_lang(lang('Unfired %s', pottery))

    for pottery in SIMPLE_UNFIRED_POTTERY:  # just the unfired item (fired is a vanilla item)
        rm.item_model(('ceramic', 'unfired_' + pottery)).with_lang(lang('Unfired %s', pottery))

    contained_fluid(rm, ('ceramic', 'jug'), 'tfc:item/ceramic/jug_empty', 'tfc:item/ceramic/jug_overlay').with_lang(lang('Ceramic Jug'))
    contained_fluid(rm, 'wooden_bucket', 'tfc:item/bucket/wooden_bucket_empty', 'tfc:item/bucket/wooden_bucket_overlay').with_lang(lang('Wooden Bucket'))
    contained_fluid(rm, ('metal', 'bucket', 'red_steel'), 'tfc:item/metal/bucket/red_steel', 'tfc:item/metal/bucket/overlay').with_lang(lang('red steel bucket'))
    contained_fluid(rm, ('metal', 'bucket', 'blue_steel'), 'tfc:item/metal/bucket/blue_steel', 'tfc:item/metal/bucket/overlay').with_lang(lang('blue steel bucket'))
    for glass in GLASS_TYPES:
        contained_fluid(rm, '%s_glass_bottle' % glass, 'tfc:item/bucket/%s_glass_bottle' % glass, 'tfc:item/bucket/glass_bottle_overlay').with_lang(lang('glass bottle'))
        rm.lang('item.tfc.%s_glass_bottle.filled' % glass, '%s Glass Bottle')

    rm.lang('item.tfc.wooden_bucket.filled', '%s Wooden Bucket')
    rm.lang('item.tfc.ceramic.jug.filled', '%s Ceramic Jug')
    rm.lang('item.tfc.metal.bucket.red_steel.filled', '%s Red Steel Bucket')
    rm.lang('item.tfc.metal.bucket.blue_steel.filled', '%s Blue Steel Bucket')

    # Small Ceramic Vessels (colored)
    for color in COLORS:
        rm.item_model(('ceramic', color + '_unfired_vessel')).with_lang(lang('%s Unfired Vessel', color))
        rm.item_model(('ceramic', color + '_glazed_vessel')).with_lang(lang('%s Glazed Vessel', color))

    # Molds
    for variant, data in METAL_ITEMS.items():
        if data.mold:
            rm.item_model(('ceramic', 'unfired_%s_mold' % variant), 'tfc:item/ceramic/unfired_%s' % variant).with_lang(lang('unfired %s mold', variant))
            contained_fluid(rm, ('ceramic', '%s_mold' % variant), 'tfc:item/ceramic/fired_mold/%s_empty' % variant, 'tfc:item/ceramic/fired_mold/%s_overlay' % variant).with_lang(lang('%s mold', variant))
    rm.item_model(('ceramic', 'unfired_bell_mold'), 'tfc:item/ceramic/unfired_bell').with_lang(lang('unfired bell mold'))
    contained_fluid(rm, ('ceramic', 'bell_mold'), 'tfc:item/ceramic/fired_mold/bell_empty', 'tfc:item/ceramic/fired_mold/bell_overlay').with_lang(lang('bell mold'))
    rm.item_model(('ceramic', 'unfired_fire_ingot_mold'), 'tfc:item/ceramic/unfired_fire_ingot').with_lang(lang('unfired fire ingot mold'))
    contained_fluid(rm, ('ceramic', 'fire_ingot_mold'), 'tfc:item/ceramic/fired_mold/fire_ingot_empty', 'tfc:item/ceramic/fired_mold/fire_ingot_overlay').with_lang(lang('fire ingot mold'))

    # Crops
    for crop, crop_data in CROPS.items():
        name = 'tfc:' + crop if crop == 'jute' or crop == 'papyrus' else 'tfc:food/%s' % crop
        if crop_data.type in ('default', 'spreading', 'pickable'):
            if crop_data.type == 'spreading':
                rm.block_model(('crop', crop + '_side'), parent='tfc:block/crop/spreading_crop_side', textures={'crop': 'tfc:block/crop/%s_side' % crop})
                block = rm.blockstate_multipart(('crop', crop),
                    *(({'age': i}, {'model': 'tfc:block/crop/%s_age_%d' % (crop, i)}) for i in range(crop_data.stages)),
                    ({'east': True}, {'model': 'tfc:block/crop/%s_side' % crop, 'y': 90}),
                    ({'north': True}, {'model': 'tfc:block/crop/%s_side' % crop}),
                    ({'south': True}, {'model': 'tfc:block/crop/%s_side' % crop, 'y': 180}),
                    ({'west': True}, {'model': 'tfc:block/crop/%s_side' % crop, 'y': 270})
                )
            else:
                block = rm.blockstate(('crop', crop), variants=dict(('age=%d' % i, {'model': 'tfc:block/crop/%s_age_%d' % (crop, i)}) for i in range(crop_data.stages)))
            block.with_lang(lang(crop))
            for i in range(crop_data.stages):
                rm.block_model(('crop', crop + '_age_%d' % i), textures={'crop': 'tfc:block/crop/%s_%d' % (crop, i)}, parent='block/crop')

            if crop_data.type == 'spreading':
                block.with_block_loot('tfc:seeds/%s' % crop)
            elif crop_data.type == 'pickable':
                block.with_block_loot(
                    {
                        'name': 'tfc:food/green_bell_pepper',
                        'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s]' % (crop, crop_data.stages - 2)),
                        'functions': crop_yield(1, (4, 5))
                    },
                    {
                        'name': name,
                        'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s]' % (crop, crop_data.stages - 1)),
                        'functions': crop_yield(1, (4, 5))
                    },
                    'tfc:seeds/%s' % crop,
                )
            else:
                block.with_block_loot({
                    'name': name,
                    'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s]' % (crop, crop_data.stages - 1)),
                    'functions': crop_yield(0, (6, 10))
                }, {
                    'name': 'tfc:seeds/%s' % crop
                })

            block = rm.blockstate(('dead_crop', crop), variants={
                'mature=true': {'model': 'tfc:block/dead_crop/%s' % crop},
                'mature=false': {'model': 'tfc:block/dead_crop/%s_young' % crop}
            })
            block.with_lang(lang('dead %s', crop))
            rm.block_model(('dead_crop', crop + '_young'), textures={'crop': 'tfc:block/crop/%s_dead_young' % crop}, parent='block/crop')
            rm.block_model(('dead_crop', crop), textures={'crop': 'tfc:block/crop/%s_dead' % crop}, parent='block/crop')

            block.with_block_loot(loot_tables.alternatives({
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=true]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=false]' % crop)
            }))

            block = rm.block(('wild_crop', crop)).with_lang(lang('Wild %s', crop))
            block.with_block_model(textures={'crop': 'tfc:block/crop/%s_wild' % crop}, parent='tfc:block/wild_crop/crop')
            rm.item_model(('wild_crop', crop), parent='tfc:block/wild_crop/%s' % crop, no_textures=True)

            if crop_data.type == 'spreading':
                block.with_block_loot({'name': 'tfc:seeds/%s' % crop})
                rm.block_model(('wild_crop', crop + '_side'), parent='tfc:block/crop/spreading_crop_side', textures={'crop': 'tfc:block/crop/%s_side' % crop})
                rm.block_model(('dead_crop', crop + '_side'), parent='tfc:block/crop/spreading_crop_side', textures={'crop': 'tfc:block/crop/%s_side_dead' % crop})
                block.with_blockstate_multipart(
                    ({'mature': True}, {'model': 'tfc:block/wild_crop/%s' % crop}),
                    ({'mature': False}, {'model': 'tfc:block/dead_crop/%s' % crop}),
                    ({'east': True, 'mature': True}, {'model': 'tfc:block/wild_crop/%s_side' % crop, 'y': 90}),
                    ({'north': True, 'mature': True}, {'model': 'tfc:block/wild_crop/%s_side' % crop}),
                    ({'south': True, 'mature': True}, {'model': 'tfc:block/wild_crop/%s_side' % crop, 'y': 180}),
                    ({'west': True, 'mature': True}, {'model': 'tfc:block/wild_crop/%s_side' % crop, 'y': 270}),
                    ({'east': True, 'mature': False}, {'model': 'tfc:block/dead_crop/%s_side' % crop, 'y': 90}),
                    ({'north': True, 'mature': False}, {'model': 'tfc:block/dead_crop/%s_side' % crop}),
                    ({'south': True, 'mature': False}, {'model': 'tfc:block/dead_crop/%s_side' % crop, 'y': 180}),
                    ({'west': True, 'mature': False}, {'model': 'tfc:block/dead_crop/%s_side' % crop, 'y': 270})
                )
            else:
                block.with_blockstate(variants={'mature=true': {'model': 'tfc:block/wild_crop/%s' % crop}, 'mature=false': {'model': 'tfc:block/dead_crop/%s' % crop}}, use_default_model=False)
                block.with_block_loot({
                    'name': name,
                    'functions': loot_tables.set_count(1, 3),
                    'conditions': [loot_tables.block_state_property('tfc:wild_crop/%s[mature=true]' % crop)]
                }, 'tfc:seeds/%s' % crop if crop not in GRAINS else '2-3 tfc:seeds/%s' % crop)

        elif crop_data.type == 'double':
            half = crop_data.stages // 2
            block = rm.blockstate(('crop', crop), variants={
                **dict(('age=%d' % i, {'model': 'tfc:block/crop/%s_age_%d' % (crop, i)}) for i in range(half)),
                **dict(('age=%d,part=bottom' % i, {'model': 'tfc:block/crop/%s_age_%d_bottom' % (crop, i)}) for i in range(half, crop_data.stages)),
                **dict(('age=%d,part=top' % i, {'model': 'tfc:block/crop/%s_age_%d_top' % (crop, i)}) for i in range(half, crop_data.stages))
            })
            block.with_lang(lang(crop))
            for i in range(crop_data.stages):
                if i < half:
                    rm.block_model(('crop', '%s_age_%d' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d' % (crop, i)}, parent='block/crop')
                else:
                    rm.block_model(('crop', '%s_age_%d_bottom' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d_bottom' % (crop, i)}, parent='block/crop')
                    rm.block_model(('crop', '%s_age_%d_top' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d_top' % (crop, i)}, parent='block/crop')

            block.with_block_loot({
                'name': name,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s,part=bottom]' % (crop, crop_data.stages - 1)),
                'functions': crop_yield(0, (6, 10))
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[part=bottom,part=bottom]' % crop)
            })

            block = rm.blockstate(('dead_crop', crop), variants={
                'mature=false': {'model': 'tfc:block/dead_crop/%s_young' % crop},
                'mature=true,part=top': {'model': 'tfc:block/dead_crop/%s_top' % crop},
                'mature=true,part=bottom': {'model': 'tfc:block/dead_crop/%s_bottom' % crop}
            })
            block.with_lang(lang('dead %s', crop))
            for variant in ('young', 'top', 'bottom'):
                rm.block_model(('dead_crop', '%s_%s' % (crop, variant)), {'crop': 'tfc:block/crop/%s_dead_%s' % (crop, variant)}, parent='block/crop')

            block.with_block_loot(loot_tables.alternatives({
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=true,part=bottom]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=false,part=bottom]' % crop)
            }))

        elif crop_data.type == 'double_stick':
            half = crop_data.stages // 2
            block = rm.blockstate(('crop', crop), variants={
                **dict(('age=%d,stick=false' % i, {'model': 'tfc:block/crop/%s_age_%d' % (crop, i)}) for i in range(half)),
                **dict(('age=%d,stick=true,part=bottom' % i, {'model': 'tfc:block/crop/%s_age_%d_stick' % (crop, i)}) for i in range(half)),
                **dict(('age=%d,stick=true,part=top' % i, {'model': 'tfc:block/crop/stick'}) for i in range(half)),
                **dict(('age=%d,part=bottom' % i, {'model': 'tfc:block/crop/%s_age_%d_bottom' % (crop, i)}) for i in range(half, crop_data.stages)),
                **dict(('age=%d,part=top' % i, {'model': 'tfc:block/crop/%s_age_%d_top' % (crop, i)}) for i in range(half, crop_data.stages))
            })
            block.with_lang(lang(crop))
            for i in range(crop_data.stages):
                if i < half:
                    rm.block_model(('crop', '%s_age_%d' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d' % (crop, i)}, parent='block/crop')
                    rm.block_model(('crop', '%s_age_%d_stick' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d_stick' % (crop, i)}, parent='block/crop')
                else:
                    rm.block_model(('crop', '%s_age_%d_bottom' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d_bottom' % (crop, i)}, parent='block/crop')
                    rm.block_model(('crop', '%s_age_%d_top' % (crop, i)), textures={'crop': 'tfc:block/crop/%s_%d_top' % (crop, i)}, parent='block/crop')

            stick_loot = {
                'name': 'minecraft:stick',
                'conditions': loot_tables.block_state_property('tfc:crop/%s[part=bottom,stick=true]' % crop)
            }
            block.with_block_loot({
                'name': name,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s,part=bottom]' % (crop, crop_data.stages - 1)),
                'functions': crop_yield(0, (6, 10))
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[part=bottom]' % crop)
            }, stick_loot)

            block = rm.blockstate(('dead_crop', crop), variants={
                'mature=false,stick=false': {'model': 'tfc:block/dead_crop/%s_young' % crop},
                'mature=false,stick=true,part=top': {'model': 'tfc:block/crop/stick'},
                'mature=false,stick=true,part=bottom': {'model': 'tfc:block/dead_crop/%s_young_stick' % crop},
                'mature=true,part=top': {'model': 'tfc:block/dead_crop/%s_top' % crop},
                'mature=true,part=bottom': {'model': 'tfc:block/dead_crop/%s_bottom' % crop}
            })
            block.with_lang(lang('dead %s', crop))
            for variant in ('young', 'young_stick', 'top', 'bottom'):
                rm.block_model(('dead_crop', '%s_%s' % (crop, variant)), {'crop': 'tfc:block/crop/%s_dead_%s' % (crop, variant)}, parent='block/crop')

            block.with_block_loot(loot_tables.alternatives({
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=true,part=bottom]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=false,part=bottom]' % crop)
            }), stick_loot)

        rm.item_model(('seeds', crop)).with_lang(lang('%s seeds', crop))
        if crop_data.type == 'double' or crop_data.type == 'double_stick':
            block = rm.blockstate(('wild_crop', crop), variants={
                'part=top,mature=true': {'model': 'tfc:block/wild_crop/%s_top' % crop},
                'part=top,mature=false': {'model': 'tfc:block/dead_crop/%s_top' % crop},
                'part=bottom,mature=true': {'model': 'tfc:block/wild_crop/%s_bottom' % crop},
                'part=bottom,mature=false': {'model': 'tfc:block/dead_crop/%s_bottom' % crop}
            })
            rm.item_model(('wild_crop', crop), parent='tfc:block/wild_crop/%s_bottom' % crop, no_textures=True)
            block.with_lang(lang('wild %s', crop))
            rm.block_model(('wild_crop', '%s_top' % crop), {'crop': 'tfc:block/crop/%s_wild_top' % crop}, parent='block/crop')
            rm.block_model(('wild_crop', '%s_bottom' % crop), {'crop': 'tfc:block/crop/%s_wild_bottom' % crop}, parent='tfc:block/wild_crop/crop')

            block.with_block_loot({
                'name': name,
                'conditions': loot_tables.block_state_property('tfc:wild_crop/%s[part=bottom,mature=true]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:wild_crop/%s[part=bottom]' % crop)
            })

    rm.block_model(('crop', 'stick'), {'crop': 'tfc:block/crop/stick_top'}, parent='block/crop')

    # Plants
    shears_or_knife = loot_tables.any_of(loot_tables.match_tag(TAG_SHARP), loot_tables.match_tag(TAG_SHEARS))
    for plant, plant_data in PLANTS.items():
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        p = 'tfc:plant/%s' % plant
        lower_only = loot_tables.block_state_property(p + '[part=lower]')
        if plant_data.type == 'short_grass' or plant_data.type == 'beach_grass':
            rm.block_loot(p, ({
                'name': p,
                'conditions': [loot_tables.match_tag(TAG_SHEARS)],
            }, {
                'name': 'tfc:straw',
                'conditions': [loot_tables.match_tag(TAG_SHARP)]
            }))
        elif plant_data.type == 'tall_grass':
            rm.block_loot(p, ({
                'name': p,
                'conditions': [loot_tables.match_tag(TAG_SHEARS), lower_only],
            }, {
                'name': 'tfc:straw',
                'conditions': [loot_tables.match_tag(TAG_SHARP)]
            }))
        elif plant in SEAWEED:
            rm.block_loot(p, (
                {'name': 'tfc:food/fresh_seaweed', 'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.random_chance(0.3)]},
                {'name': p, 'conditions': [loot_tables.match_tag(TAG_SHEARS)]}
            ))
        elif plant_data.type in ('tall_plant', 'emergent', 'emergent_fresh', 'cactus'):
            if plant == 'cattail':
                rm.block_loot(p, (
                    {'name': 'tfc:food/cattail_root', 'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.random_chance(0.3), lower_only]},
                    {'name': p, 'conditions': [loot_tables.match_tag(TAG_SHEARS), lower_only]}
                ))
            elif plant == 'water_taro':
                rm.block_loot(p, (
                    {'name': 'tfc:food/taro_root', 'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.random_chance(0.3), lower_only]},
                    {'name': p, 'conditions': [loot_tables.match_tag(TAG_SHEARS), lower_only]}
                ))
            else:
                rm.block_loot(p, {'name': p, 'conditions': [shears_or_knife, lower_only]})
        else:
            rm.block_loot(p, {'name': p, 'conditions': [shears_or_knife]})
    # todo this is a mess
    for plant in ('hanging_vines', 'jungle_vines', 'ivy', 'liana', 'tree_fern', 'arundo', 'spanish_moss'):
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
    for plant in ('tree_fern', 'arundo', 'winged_kelp', 'leafy_kelp', 'giant_kelp_flower', 'dry_phragmite'):
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        rm.block_loot('tfc:plant/%s' % plant, 'tfc:plant/%s' % plant)
    for plant in ('tree_fern', 'arundo', 'winged_kelp', 'leafy_kelp', 'giant_kelp', 'hanging_vines', 'spanish_moss', 'liana', 'dry_phragmite'):
        rm.lang('block.tfc.plant.%s_plant' % plant, lang(plant))
    for plant in ('hanging_vines', 'jungle_vines', 'liana', 'spanish_moss'):
        rm.block_loot('tfc:plant/%s' % plant, {'name': 'tfc:plant/%s' % plant, 'conditions': [loot_tables.match_tag(TAG_SHARP)]})

    cactus = 'saguaro'
    for variation in ('', '_plant'):
        rm.blockstate_multipart(('plant', cactus + variation),
            ({'down': True}, {'model': 'tfc:block/plant/%s_branch_down' % cactus}),
            ({'up': True}, {'model': 'tfc:block/plant/%s_branch_up' % cactus}),
            ({'north': True}, {'model': 'tfc:block/plant/%s_branch_side' % cactus, 'y': 90}),
            ({'south': True}, {'model': 'tfc:block/plant/%s_branch_side' % cactus, 'y': 270}),
            ({'west': True}, {'model': 'tfc:block/plant/%s_branch_side' % cactus}),
            ({'east': True}, {'model': 'tfc:block/plant/%s_branch_side' % cactus, 'y': 180})
            ).with_lang(lang('%s Branch', cactus))
    for part in ('down', 'side', 'up'):
        rm.block_model('tfc:plant/%s_branch_%s' % (cactus, part), parent='tfc:block/plant/cactus_branch_%s' % part, textures={'0': 'tfc:block/plant/%s/bark' % cactus, '1': 'tfc:block/plant/%s/bark_top' % cactus})
    rm.item_model(('plant', cactus), 'tfc:item/plant/%s' % cactus)

    for plant, texture in FLOWERPOT_CROSS_PLANTS.items():
        plant_folder = plant
        if 'tulip' in plant:
            plant_folder = 'tulip'
        elif 'snapdragon' in plant:
            plant_folder = 'snapdragon'
        flower_pot_cross(rm, plant, 'tfc:plant/potted/%s' % plant, 'plant/flowerpot/%s' % plant, 'tfc:block/plant/%s/%s' % (plant_folder, texture), 'tfc:plant/%s' % plant)
    for plant in MISC_POTTED_PLANTS:
        rm.blockstate('plant/potted/%s' % plant, model='tfc:block/plant/flowerpot/%s' % plant).with_lang(lang('potted %s', plant)).with_block_loot('tfc:plant/%s' % plant, 'minecraft:flower_pot')
    for plant, stages in SIMPLE_STAGE_PLANTS.items():
        if plant not in ('kangaroo_paw', 'trillium'):
            for i in range(0, stages):
                rm.block_model(f'plant/{plant}_{i}', parent='block/cross', textures={'cross': f'tfc:block/plant/{plant}/{plant}_{i}'})
    for plant, states in SINGLE_BLOCK_STAGE_PLANTS.items():
        rm.blockstate('plant/%s' % plant,  model= 'tfc:block/plant/%s_dynamic' % plant)
        rm.custom_block_model('plant/%s_dynamic' % plant, 'tfc:plant', {'blooming': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[0])}, 'seeding': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[1])}, 'dying': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[2])}, 'dormant': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[3])}, 'sprouting': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[4])}, 'budding': {'parent': 'tfc:block/plant/%s_%s' % (plant, states[5])}})
    for plant in MODEL_PLANTS:
        rm.blockstate('plant/%s' % plant, model='tfc:block/plant/%s' % plant)
    for plant in SEAGRASS:
        rm.blockstate('plant/%s' % plant, variants=dict({'age=%s' % i: {'model': 'tfc:block/plant/%s_%s' % (plant, i)} for i in range(0, 4)}))
        for i in range(0, 4):
            rm.block_model('plant/%s_%s' % (plant, i), parent='minecraft:block/template_seagrass', textures={'texture': 'tfc:block/plant/%s/%s' % (plant, i)})
    rm.blockstate('plant/dead_bush', variants={"": [{'model': 'tfc:block/plant/dead_bush_large'}, *[{'model': 'tfc:block/plant/dead_bush%s' % i} for i in range(0, 7)]]}, use_default_model=False)
    for i in range(0, 7):
        rm.block_model('plant/dead_bush%s' % i, parent='minecraft:block/cross', textures={'cross': 'tfc:block/plant/dead_bush/dead_bush%s' % i})
    for i in range(1, 5):
        rm.block_model('plant/maiden_pink_%s' % i, parent='tfc:block/plant/flowerbed_%s' % i, textures={'flowerbed': 'tfc:block/plant/maiden_pink/petals', 'stem': 'tfc:block/plant/maiden_pink/stem'})

    rm.block('sea_pickle').with_lang(lang('sea pickle')).with_block_loot([{
        'name': 'tfc:sea_pickle',
        'conditions': loot_tables.block_state_property('tfc:sea_pickle[pickles=%d]' % i),
        'functions': [loot_tables.set_count(i)]
    } for i in (1, 2, 3, 4)])

    for plant in ('duckweed', 'lotus', 'sargassum', 'water_lily', 'green_algae', 'red_algae'):
        if plant not in ('water_lily', 'lotus'):
            rm.blockstate(('plant', plant), variants={'': four_ways('tfc:block/plant/%s' % plant)}, use_default_model=False)
        tinted = plant not in ('green_algae', 'sargassum', 'red_algae')
        rm.block_model(('plant', plant), parent='tfc:block/plant/template_floating%s' % ('_tinted' if tinted else ''), textures={'pad': 'tfc:block/plant/%s/%s' % (plant, plant)})
        rm.item_model(('plant', plant), 'tfc:item/plant/%s' % plant)

    # Food
    for berry in BERRIES.keys():
        rm.item_model('food/' + berry).with_lang(lang(berry))

    for grain in GRAINS:
        for suffix in GRAIN_SUFFIXES:
            rm.item_model('food/%s%s' % (grain, suffix)).with_lang(lang('%s%s', grain, suffix))

    for meat in MEATS:
        rm.item_model(('food', meat)).with_lang(lang('raw %s', meat))
        rm.item_model(('food', 'cooked_' + meat)).with_lang(lang('cooked %s', meat))
    for veg in MISC_FOODS:
        rm.item_model(('food', veg)).with_lang(lang(veg))

    funny_names = {  # Dict[nutrient, (soup, salad)]
        'grain': ('Wholesome', 'Crunchy'),
        'fruit': ('Tasty', 'Sweet'),
        'vegetables': ('Filling', 'Healthy'),
        'protein': ('Hearty', 'Flavorful'),
        'dairy': ('Creamy', 'Disgusting')
    }
    for nutrient in NUTRIENTS:
        funny_soup_name, funny_salad_name = funny_names[nutrient]
        rm.item_model(('food', '%s_soup' % nutrient)).with_lang(lang('%s soup', funny_soup_name))
        rm.item_model(('food', '%s_salad' % nutrient)).with_lang(lang('%s salad', funny_salad_name))

    rm.block_model('jar/empty', textures={'2': 'tfc:block/jar_no_lid'}, parent='tfc:block/jar')
    for fruit in JAR_FRUITS:
        rm.block_model('jar/%s' % fruit, textures={'1': 'tfc:block/jar/%s' % fruit}, parent='tfc:block/jar')
        rm.block_model('jar/%s_unsealed' % fruit, textures={'1': 'tfc:block/jar/%s' % fruit, '2': 'tfc:block/jar_no_lid'}, parent='tfc:block/jar')
        fixed_name = fruit.replace('_chunks', '').replace('_slice', '')
        rm.item_model('tfc:jar/%s' % fruit, 'tfc:item/jar/%s' % fruit).with_lang(lang('%s jam', fixed_name))
        rm.item_model('tfc:jar/%s_unsealed' % fruit, 'tfc:item/jar/%s_unsealed' % fruit).with_lang(lang('%s jam', fixed_name))

    # Berry Bushes
    lifecycle_to_model = {'healthy': '', 'dormant': 'dry_', 'fruiting': 'fruiting_', 'flowering': 'flowering_'}
    lifecycles = ('healthy', 'dormant', 'fruiting', 'flowering')

    for berry, data in BERRIES.items():
        rm.blockstate('plant/%s_bush' % berry, variants=dict(
            (
                'lifecycle=%s,stage=%d' % (lifecycle, stage),
                {'model': 'tfc:block/plant/%s%s_bush_%d' % (lifecycle_to_model[lifecycle], berry, stage)}
            ) for lifecycle, stage in itertools.product(lifecycles, range(0, 3))
        )).with_lang(lang('%s Bush', berry))

        if data.type == 'stationary' or data.type == 'waterlogged':
            rm.item_model('plant/%s_bush' % berry, parent='tfc:block/plant/%s_bush_2' % berry, no_textures=True)
            rm.block_loot('plant/%s_bush' % berry, {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag(TAG_SHARP)]})
            for lifecycle, stage in itertools.product(lifecycle_to_model.values(), range(0, 3)):
                rm.block_model('plant/%s%s_bush_%d' % (lifecycle, berry, stage), parent='tfc:block/plant/stationary_bush_%d' % stage, textures={'bush': 'tfc:block/berry_bush/' + lifecycle + '%s_bush' % berry})
        else:
            rm.item_model('plant/%s_bush' % berry, 'tfc:block/berry_bush/%s_cane' % berry)
            rm.block_loot('plant/%s_bush' % berry, (
                {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.block_state_property('tfc:plant/%s_bush[stage=2]' % berry)]},
                {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag(TAG_SHARP), loot_tables.random_chance(0.5)]}
            ), 'minecraft:stick')

    rm.blockstate('plant/dead_berry_bush', variants={
        'stage=0': {'model': 'tfc:block/plant/dead_berry_bush_0'},
        'stage=1': {'model': 'tfc:block/plant/dead_berry_bush_1'},
        'stage=2': {'model': 'tfc:block/plant/dead_berry_bush_2'}
    }).with_lang(lang('Dead Bush')).with_block_loot('minecraft:stick')
    rm.blockstate('plant/dead_cane', variants={
        **four_rotations('tfc:block/plant/dead_berry_bush_side_0', (90, None, 180, 270), suffix=',stage=0'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_1', (90, None, 180, 270), suffix=',stage=1'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_2', (90, None, 180, 270), suffix=',stage=2')
    }).with_lang(lang('Dead Cane')).with_block_loot('minecraft:stick')

    for berry in ('blackberry', 'raspberry', 'blueberry', 'elderberry'):
        cane_dict = {}
        for lifecycle, stage in itertools.product(lifecycles, range(3)):
            cane_dict.update(four_rotations('tfc:block/plant/%s%s_bush_side_%d' % (lifecycle_to_model[lifecycle], berry, stage), (90, None, 180, 270), suffix=',stage=%d' % stage, prefix='lifecycle=%s,' % lifecycle))
        rm.blockstate('plant/%s_bush_cane' % berry, variants=cane_dict).with_lang(lang('%s Cane', berry)).with_block_loot('minecraft:stick')
        for lifecycle in lifecycle_to_model.values():
            bush_textures = {'cane': 'tfc:block/berry_bush/' + lifecycle + '%s_cane' % berry, 'bush': 'tfc:block/berry_bush/' + lifecycle + '%s_bush' % berry}
            for stage in range(0, 3):
                rm.block_model('plant/' + lifecycle + berry + '_bush_%d' % stage, parent='tfc:block/plant/berry_bush_%d' % stage, textures=bush_textures)
                rm.block_model('plant/' + lifecycle + berry + '_bush_side_%d' % stage, parent='tfc:block/plant/berry_bush_side_%d' % stage, textures=bush_textures)

    for stage in range(0, 3):
        rm.block_model('plant/dead_berry_bush_%d' % stage, parent='tfc:block/plant/berry_bush_%d' % stage, textures={'cane': 'tfc:block/berry_bush/dead_cane', 'bush': 'tfc:block/berry_bush/dead_bush'})
        rm.block_model('plant/dead_berry_bush_side_%d' % stage, parent='tfc:block/plant/berry_bush_side_%d' % stage, textures={'cane': 'tfc:block/berry_bush/dead_cane', 'bush': 'tfc:block/berry_bush/dead_bush'})

    for fruit in FRUITS.keys():
        if fruit != 'banana':
            for prefix in ('', 'growing_'):
                block = rm.blockstate_multipart('plant/' + fruit + '_' + prefix + 'branch',
                    ({'model': 'tfc:block/plant/%s_branch_core' % fruit}),
                    ({'down': True}, {'model': 'tfc:block/plant/%s_branch_down' % fruit}),
                    ({'up': True}, {'model': 'tfc:block/plant/%s_branch_up' % fruit}),
                    ({'north': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 90}),
                    ({'south': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 270}),
                    ({'west': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit}),
                    ({'east': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 180})
                ).with_lang(lang('%s Branch', fruit))
                if prefix == '':
                    block.with_block_loot({
                        'name': 'tfc:plant/%s_sapling' % fruit,
                        'conditions': loot_tables.all_of(
                            loot_tables.any_of(*[
                                loot_tables.block_state_property('tfc:plant/%s_branch[up=true,%s=true]' % (fruit, direction))
                                for direction in ('west', 'east', 'north', 'south')
                            ]),
                            loot_tables.match_tag('tfc:axes')
                        )
                    }, {
                        'name': 'minecraft:stick',
                        'functions': [loot_tables.set_count(1, 4)]
                    })
                else:
                    block.with_block_loot({'name': 'minecraft:stick', 'functions': [loot_tables.set_count(1, 4)]})
            for part in ('down', 'side', 'up', 'core'):
                rm.block_model('tfc:plant/%s_branch_%s' % (fruit, part), parent='tfc:block/plant/branch_%s' % part, textures={'bark': 'tfc:block/fruit_tree/%s_branch' % fruit})
            rm.blockstate('plant/%s_leaves' % fruit, variants={
                'lifecycle=flowering': {'model': 'tfc:block/plant/%s_flowering_leaves' % fruit},
                'lifecycle=fruiting': {'model': 'tfc:block/plant/%s_fruiting_leaves' % fruit},
                'lifecycle=dormant': {'model': 'tfc:block/plant/%s_dry_leaves' % fruit},
                'lifecycle=healthy': {'model': 'tfc:block/plant/%s_leaves' % fruit}
            }).with_item_model().with_lang(lang('%s Leaves', fruit)).with_block_loot({
                'name': 'tfc:food/%s' % fruit,
                'conditions': [loot_tables.block_state_property('tfc:plant/%s_leaves[lifecycle=fruiting]' % fruit)]
            },
                when_sheared('tfc:plant/%s_leaves' % fruit),
                STICKS_WHEN_NOT_SHEARED)

            for life in ('', '_fruiting', '_flowering', '_dry'):
                rm.block_model('tfc:plant/%s%s_leaves' % (fruit, life), parent='block/leaves', textures={'all': 'tfc:block/fruit_tree/%s%s_leaves' % (fruit, life)})

            rm.blockstate(('plant', '%s_sapling' % fruit), variants={'saplings=%d' % i: {'model': 'tfc:block/plant/%s_sapling_%d' % (fruit, i)} for i in range(1, 4 + 1)}).with_lang(lang('%s Sapling', fruit))
            rm.block_loot(('plant', '%s_sapling' % fruit), {
                'name': 'tfc:plant/%s_sapling' % fruit,
                'functions': [stages({**loot_tables.set_count(i), 'conditions': [loot_tables.block_state_property('tfc:plant/%s_sapling[saplings=%s]' % (fruit, i))]} for i in range(1, 5)), loot_tables.explosion_decay()]
            })
            for stage in range(2, 4 + 1):
                rm.block_model(('plant', '%s_sapling_%d' % (fruit, stage)), parent='tfc:block/plant/cross_%s' % stage, textures={'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit})
            rm.block_model(('plant', '%s_sapling_1' % fruit), {'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit}, 'block/cross')
        else:
            def banana_suffix(state: str, i: int) -> str:
                if i == 2 and (state == 'fruiting' or state == 'flowering'):
                    return '%d_%s' % (i, state)
                return str(i)

            rm.blockstate('plant/banana_plant', variants=dict({'lifecycle=%s,stage=%d' % (state, i): {'model': 'tfc:block/plant/banana_trunk_%s' % banana_suffix(state, i)} for state, i in itertools.product(lifecycles, range(0, 3))})).with_lang(lang('Banana Plant')).with_block_loot({
                'name': 'tfc:plant/banana_sapling',
                'functions': [{**loot_tables.set_count(1, 2)}],
                'conditions': [loot_tables.block_state_property('tfc:plant/banana_plant[stage=2]')]
            })
            rm.blockstate(('plant', 'dead_banana_plant'), variants=dict({'stage=%d' % i: {'model': 'tfc:block/plant/banana_trunk_%d_dead' % i} for i in range(0, 3)})).with_lang(lang('Dead Banana Plant')).with_block_loot({
                'name': 'minecraft:stick'
            }, {
                'name': 'tfc:plant/banana_sapling',
                'functions': [{**loot_tables.set_count(1, 2)}],
                'conditions': [loot_tables.block_state_property('tfc:plant/dead_banana_plant[stage=2]')]
            })
            rm.block_model(('plant', 'banana_trunk_0_dead'), textures={"particle": "tfc:block/fruit_tree/banana_branch", "0": "tfc:block/fruit_tree/banana_branch"}, parent='tfc:block/plant/banana_trunk_0')
            rm.block_model(('plant', 'banana_trunk_1_dead'), textures={"particle": "tfc:block/fruit_tree/banana_branch", "0": "tfc:block/fruit_tree/banana_branch"}, parent='tfc:block/plant/banana_trunk_1')
            rm.block_model(('plant', 'banana_trunk_2_dead'), textures={"particle": "tfc:block/fruit_tree/banana_leaves_dead", "1_0": "tfc:block/fruit_tree/banana_leaves_dead"}, parent='tfc:block/plant/banana_trunk_2')
            rm.block_model(('plant', 'banana_sapling'), textures={'cross': 'tfc:block/fruit_tree/banana_sapling'}, parent='block/cross')
            rm.blockstate(('plant', 'banana_sapling'), model='tfc:block/plant/banana_sapling').with_lang(lang('Banana Sapling')).with_block_loot('tfc:plant/banana_sapling')

        rm.item_model(('plant', '%s_sapling' % fruit), 'tfc:block/fruit_tree/%s_sapling' % fruit)
        rm.item_model(('food', fruit), 'tfc:item/food/%s' % fruit).with_lang(lang(fruit))
        flower_pot_cross(rm, '%s sapling' % fruit, 'tfc:plant/potted/%s_sapling' % fruit, 'plant/flowerpot/%s_sapling' % fruit, 'tfc:block/fruit_tree/%s_sapling' % fruit, 'tfc:plant/%s_sapling' % fruit)

    # Wood Blocks
    for wood in WOODS:
        # Logs
        for variant in ('log', 'stripped_log', 'wood', 'stripped_wood'):
            block = rm.blockstate(('wood', variant, wood), variants={
                'axis=y': {'model': 'tfc:block/wood/%s/%s' % (variant, wood)},
                'axis=z': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'x': 90},
                'axis=x': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'x': 90, 'y': 90}
            }, use_default_model=False)

            stick_with_hammer = {
                'name': 'minecraft:stick',
                'conditions': [loot_tables.match_tag('tfc:hammers')],
                'functions': [loot_tables.set_count(1, 4)]
            }
            if variant == 'wood' or variant == 'stripped_wood':
                block.with_block_loot((
                    stick_with_hammer,
                    {  # wood blocks will only drop themselves if non-natural (aka branch_direction=none)
                        'name': 'tfc:wood/%s/%s' % (variant, wood),
                        'conditions': loot_tables.block_state_property('tfc:wood/%s/%s[branch_direction=none]' % (variant, wood))
                    },
                    'tfc:wood/%s/%s' % (variant.replace('wood', 'log'), wood)
                ))
            else:
                block.with_block_loot((
                    stick_with_hammer,
                    'tfc:wood/%s/%s' % (variant, wood)  # logs drop themselves always
                ))

            rm.item_model(('wood', variant, wood), 'tfc:item/wood/%s/%s' % (variant, wood))
            block.with_block_model({
                'end': 'tfc:block/wood/%s/%s' % (variant.replace('log', 'log_top').replace('wood', 'log'), wood),
                'side': 'tfc:block/wood/%s/%s' % (variant.replace('wood', 'log'), wood)
            }, parent='block/cube_column')
            if 'stripped' in variant:
                block.with_lang(lang(variant.replace('_', ' ' + wood + ' ')))
            else:
                block.with_lang(lang('%s %s', wood, variant))

        # Signs + Hanging Signs
        rm.item_model(('wood', 'sign', wood), 'tfc:item/wood/sign/%s' % wood, 'tfc:item/wood/sign_head_%s' % wood, 'tfc:item/wood/sign_head_overlay%s' % ('_white' if wood in ('blackwood', 'willow', 'hickory') else '')).with_lang(lang('%s sign', wood))
        for metal, metal_data in METALS.items():
            if metal_data.type == 'all':
                rm.item_model(('wood', 'hanging_sign', metal, wood), 'tfc:item/wood/hanging_sign_head_%s' % wood, 'tfc:item/wood/hanging_sign_head_overlay%s' % ('_white' if wood in ('blackwood', 'willow', 'hickory') else ''), 'tfc:item/metal/hanging_sign/%s' % metal).with_lang(lang('%s %s hanging sign', metal, wood))

        rm.item_model(('wood', 'boat', wood), 'tfc:item/wood/boat_%s' % wood).with_lang(lang('%s %s', wood, ('boat' if wood != 'palm' else 'raft')))
        rm.item_model(('wood', 'lumber', wood), 'tfc:item/wood/lumber_%s' % wood).with_lang(lang('%s lumber', wood))
        rm.item_model(('wood', 'chest_minecart', wood), 'tfc:item/wood/chest_minecart_base', 'tfc:item/wood/chest_minecart_cover_%s' % wood).with_lang(lang('%s chest minecart', wood))

        # Groundcover
        block = rm.blockstate(('wood', 'twig', wood), variants={"": four_ways('tfc:block/wood/twig/%s' % wood)}, use_default_model=False)
        block.with_lang(lang('%s twig', wood))

        block.with_block_model({'side': 'tfc:block/wood/log/%s' % wood, 'top': 'tfc:block/wood/log_top/%s' % wood}, parent='tfc:block/groundcover/twig')
        rm.item_model('wood/twig/%s' % wood, 'tfc:item/wood/twig_%s' % wood, parent='item/handheld_rod')
        block.with_block_loot('tfc:wood/twig/%s' % wood)

        block = rm.blockstate(('wood', 'fallen_leaves', wood), variants=dict((('layers=%d' % i), {'model': 'tfc:block/wood/fallen_leaves/%s_height%d' % (wood, i * 2) if i != 8 else 'tfc:block/wood/leaves/%s' % wood}) for i in range(1, 1 + 8))).with_lang(lang('fallen %s leaves', wood))
        tex = {'all': 'tfc:block/wood/leaves/%s' % wood}
        if wood in ('mangrove', 'willow'):
            tex['top'] = 'tfc:block/wood/leaves/%s_top' % wood
        for i in range(1, 8):
            rm.block_model(('wood', 'fallen_leaves', '%s_height%s' % (wood, i * 2)), tex, parent='tfc:block/groundcover/fallen_leaves_height%s' % (i * 2))
        rm.item_model(('wood', 'fallen_leaves', wood), 'tfc:item/groundcover/fallen_leaves')
        block.with_block_loot(*[{'name': 'tfc:wood/fallen_leaves/%s' % wood, 'conditions': [loot_tables.block_state_property('tfc:wood/fallen_leaves/%s[layers=%s]' % (wood, i))], 'functions': [loot_tables.set_count(i)]} for i in range(1, 9)])

        # Krummholz
        if wood in ('pine', 'spruce', 'white_cedar', 'douglas_fir', 'aspen'):
            name = 'plant/%s_krummholz' % wood
            for i in range(1, 10):
                rm.block_model('plant/krummholz/%s_%s' % (wood, i), {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/leaves/%s' % wood, 'particle': 'tfc:block/wood/log/%s' % wood}, parent='tfc:block/plant/krummholz_%s' % i)
                rm.block_model('plant/krummholz/%s_%s_snowy' % (wood, i), {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/leaves/%s_frozen' % wood, 'particle': 'tfc:block/wood/log/%s' % wood}, parent='tfc:block/plant/krummholz_%s' % i)
            for i in range(1, 4):
                rm.block_model('plant/krummholz/%s_%s_tip' % (wood, i), {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/leaves/%s' % wood, 'particle': 'tfc:block/wood/log/%s' % wood}, parent='tfc:block/plant/krummholz_tip_%s' % i)
                rm.block_model('plant/krummholz/%s_%s_tip_snowy' % (wood, i), {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/leaves/%s_frozen' % wood, 'particle': 'tfc:block/wood/log/%s' % wood}, parent='tfc:block/plant/krummholz_tip_%s' % i)
            block = rm.blockstate_multipart(name,
                ({'tip': True, 'snowy': False}, [{'model': 'tfc:block/plant/krummholz/%s_%s_tip' % (wood, i), 'y': y} for i in range(1, 4) for y in (None, 90)]),
                ({'tip': True, 'snowy': True}, [{'model': 'tfc:block/plant/krummholz/%s_%s_tip_snowy' % (wood, i), 'y': y} for i in range(1, 4) for y in (None, 90)]),
                ({'tip': False, 'snowy': False}, [{'model': 'tfc:block/plant/krummholz/%s_%s' % (wood, i), 'y': y} for i in range(1, 10) for y in (None, 90)]),
                ({'tip': False, 'snowy': True}, [{'model': 'tfc:block/plant/krummholz/%s_%s_snowy' % (wood, i), 'y': y} for i in range(1, 10) for y in (None, 90)]),
                ({'snowy': True, 'bottom': True}, {'model': 'minecraft:block/snow_height2'})
            ).with_lang(lang('%s krummholz', wood))
            block.with_block_loot(loot_tables.alternatives(
                {'name': 'tfc:plant/%s_krummholz' % wood, 'conditions': [loot_tables.block_state_property('tfc:plant/%s_krummholz[tip=true]' % wood), loot_tables.match_tag('tfc:axes')]},
                {'name': 'tfc:wood/sapling/%s' % wood, 'conditions': [loot_tables.random_chance(0.02)]},
                '1-3 minecraft:stick'
            ))
            rm.item_model('plant/%s_krummholz' % wood, parent='tfc:block/plant/krummholz/%s_1_tip' % wood, no_textures=True)
            block = rm.blockstate('plant/potted/%s_krummholz' % wood, model='tfc:block/plant/flowerpot/%s_krummholz' % wood)
            rm.block_model('plant/flowerpot/%s_krummholz' % wood, {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/leaves/%s' % wood}, parent='tfc:block/plant/flowerpot/template_potted_krummholz')
            block.with_lang(lang('potted %s krummholz', wood)).with_block_loot('tfc:plant/%s_krummholz' % wood, 'minecraft:flower_pot')

        # Leaves
        block = rm.blockstate(('wood', 'leaves', wood), model='tfc:block/wood/leaves/%s' % wood).with_lang(lang('%s leaves', wood))
        if wood == 'palm' or wood == 'willow' or wood == 'mangrove':
            block.with_block_model({
                'side': 'tfc:block/wood/leaves/%s' % wood,
                'end': 'tfc:block/wood/leaves/%s_top' % wood
            }, parent='tfc:block/tinted_column')
        else:
            block.with_block_model('tfc:block/wood/leaves/%s' % wood, parent='block/leaves')
        block.with_item_model()
        block.with_block_loot(
            when_sheared('tfc:wood/leaves/%s' % wood),
            {
                'name': 'tfc:wood/sapling/%s' % wood,
                'conditions': ['minecraft:survives_explosion', loot_tables.random_chance(TREE_SAPLING_DROP_CHANCES[wood])]
            },
            STICKS_WHEN_NOT_SHEARED)

        # Sapling
        block = rm.blockstate(('wood', 'sapling', wood), 'tfc:block/wood/sapling/%s' % wood).with_lang(lang('%s %s', wood, 'propagule' if wood == 'mangrove' else 'seed' if wood == 'palm' else 'sapling'))
        block.with_block_model({'cross': 'tfc:block/wood/sapling/%s' % wood}, 'block/cross')
        block.with_block_loot('tfc:wood/sapling/%s' % wood)
        rm.item_model(('wood', 'sapling', wood), 'tfc:block/wood/sapling/%s' % wood)

        flower_pot_cross(rm, '%s sapling' % wood, 'tfc:wood/potted_sapling/%s' % wood, 'wood/potted_sapling/%s' % wood, 'tfc:block/wood/sapling/%s' % wood, 'tfc:wood/sapling/%s' % wood)

        # Planks
        block = rm.block(('wood', 'planks', wood))
        block.with_blockstate()
        block.with_block_model()
        block.with_item_model()
        block.with_block_loot('tfc:wood/planks/%s' % wood)
        block.with_lang(lang('%s planks', wood))

        # Slabs, Stairs
        # N.B. These use the naming convention of `tfc:wood/planks/<wood>_<slab>`. This is for general consistency with
        # how we label slabs and stairs across the mod, and also to indicate that these are slabs of wood planks
        block.make_slab()
        block.make_stairs()

        rm.block_loot('wood/planks/%s_stairs' % wood, 'tfc:wood/planks/%s_stairs' % wood).with_lang(lang('%s stairs', wood))
        slab_loot(rm, 'tfc:wood/planks/%s_slab' % wood).with_lang(lang('%s slab', wood))

        # Pressure Plate
        block = rm.block('wood/pressure_plate/%s' % wood)
        block.make_pressure_plate('', 'tfc:block/wood/planks/%s' % wood)
        block.with_lang(lang('%s pressure plate', wood))
        block.with_block_loot('tfc:wood/pressure_plate/%s' % wood)

        # Button
        block = rm.block('wood/button/%s' % wood)
        block.make_button('', 'tfc:block/wood/planks/%s' % wood)
        block.with_lang(lang('%s button', wood))
        block.with_block_loot('tfc:wood/button/%s' % wood)

        # Tool Rack
        block = rm.blockstate('tfc:wood/tool_rack/%s' % wood, model='tfc:block/wood/tool_rack/%s' % wood, variants=four_rotations('tfc:block/wood/tool_rack/%s' % wood, (270, 180, None, 90)))
        block.with_block_model(textures={
            'texture': 'tfc:block/wood/planks/%s' % wood,
            'particle': 'tfc:block/wood/planks/%s' % wood
        }, parent='tfc:block/tool_rack')
        block.with_lang(lang('%s Tool Rack', wood))
        block.with_block_loot('tfc:wood/tool_rack/%s' % wood)
        block.with_item_model()

        # Loom
        block = rm.blockstate('tfc:wood/loom/%s' % wood, model='tfc:block/wood/loom/%s' % wood, variants=four_rotations('tfc:block/wood/loom/%s' % wood, (270, 180, None, 90)))
        block.with_block_model(textures={
            'texture': 'tfc:block/wood/planks/%s' % wood,
            'particle': 'tfc:block/wood/planks/%s' % wood
        }, parent='tfc:block/loom')
        block.with_item_model()
        block.with_lang(lang('%s loom', wood))
        block.with_block_loot('tfc:wood/loom/%s' % wood)

        # Bookshelf
        faces = (('east', 90), ('north', None), ('west', 270), ('south', 180))
        parts = [
            ({'facing': face}, {'model': 'tfc:block/wood/bookshelf/%s' % wood, 'y': y, 'uvlock': True})
            for face, y in faces
        ] + [
            ({'AND': [{'facing': face}, {f'slot_{i}_occupied': is_occupied}]}, {'model': f'tfc:block/wood/bookshelf/{wood}_{occupation}_{slot_type}', 'y': y})
            for face, y in faces
            for slot_type, i in (('top_right', 2), ('bottom_mid', 4), ('top_left', 0), ('bottom_right', 5), ('bottom_left', 3), ('top_mid', 1))
            for occupation, is_occupied in (('empty', 'false'), ('occupied', 'true'))
        ]

        block = rm.blockstate_multipart(('wood', 'bookshelf', wood), *parts)
        block.with_lang(lang('%s bookshelf', wood))
        block.with_block_loot('tfc:wood/bookshelf/%s' % wood)
        rm.block_model(('wood', 'bookshelf', wood), {
            'top': 'tfc:block/wood/bookshelf/top_%s' % wood,
            'side': 'tfc:block/wood/bookshelf/side_%s' % wood
        }, parent='minecraft:block/chiseled_bookshelf')
        rm.block_model('wood/bookshelf/%s_inventory' % wood, {
            'top': 'tfc:block/wood/bookshelf/top_%s' % wood,
            'side': 'tfc:block/wood/bookshelf/side_%s' % wood,
            'front': 'tfc:block/wood/bookshelf/%s_empty' % wood
        }, parent='minecraft:block/chiseled_bookshelf_inventory')
        rm.item_model('tfc:wood/bookshelf/%s' % wood, parent='tfc:block/wood/bookshelf/%s_inventory' % wood, no_textures=True)

        for slot in ('bottom_left', 'bottom_mid', 'bottom_right', 'top_left', 'top_mid', 'top_right'):
            for occupancy in ('empty', 'occupied'):
                rm.block_model(f'wood/bookshelf/{wood}_{occupancy}_{slot}', {
                    'texture': f'tfc:block/wood/bookshelf/{wood}_{occupancy}'
                }, parent=f'minecraft:block/chiseled_bookshelf_{occupancy}_slot_{slot}')

        # Workbench
        block = rm.blockstate(('wood', 'workbench', wood)).with_block_model(parent='minecraft:block/cube', textures={
            'particle': 'tfc:block/wood/workbench/%s_front' % wood,
            'north': 'tfc:block/wood/workbench/%s_front' % wood,
            'south': 'tfc:block/wood/workbench/%s_side' % wood,
            'east': 'tfc:block/wood/workbench/%s_side' % wood,
            'west': 'tfc:block/wood/workbench/%s_front' % wood,
            'up': 'tfc:block/wood/workbench/%s_top' % wood,
            'down': 'tfc:block/wood/planks/%s' % wood
        })
        block.with_item_model()
        block.with_lang(lang('%s Workbench', wood))
        block.with_block_loot('tfc:wood/workbench/%s' % wood)

        # Doors
        block = rm.blockstate('wood/door/%s' % wood, variants=door_blockstate('tfc:block/wood/door/%s' % wood))
        rm.item_model('tfc:wood/door/%s' % wood, 'tfc:item/wood/door/%s' % wood)
        block.with_lang(lang('%s door', wood))
        block.with_block_loot({
            'name': 'tfc:wood/door/%s' % wood,
            'conditions': [loot_tables.block_state_property('tfc:wood/door/%s[half=lower]' % wood)]
        })

        for model in ('bottom_left', 'bottom_left_open', 'bottom_right', 'bottom_right_open', 'top_left', 'top_left_open', 'top_right', 'top_right_open'):
            rm.block_model('tfc:wood/door/%s_%s' % (wood, model), {
                'top': 'tfc:block/wood/door/%s_top' % wood,
                'bottom': 'tfc:block/wood/door/%s_bottom' % wood
            }, parent='block/door_%s' % model)

        # Trapdoor
        block = rm.block('wood/trapdoor/%s' % wood)
        block.make_trapdoor('', 'tfc:block/wood/trapdoor/%s' % wood)
        block.with_lang(lang('%s trapdoor', wood))
        block.with_block_loot('tfc:wood/trapdoor/%s' % wood)

        # Fences, Log Fences, Fence Gates
        block = rm.block('wood/fence/%s' % wood)
        block.make_fence('', 'tfc:block/wood/planks/%s' % wood)
        block.with_lang(lang('%s fence', wood))
        block.with_block_loot('tfc:wood/fence/%s' % wood)

        block = rm.block('wood/fence_gate/%s' % wood)
        block.make_fence_gate('', 'tfc:block/wood/planks/%s' % wood)
        block.with_lang(lang('%s fence gate', wood))
        block.with_block_loot('tfc:wood/fence_gate/%s' % wood)

        # Log Fences - need to copy `make_fence()` because we have separate textures for post and side
        block = rm.blockstate_multipart('wood/log_fence/%s' % wood, *block_states.fence_multipart('tfc:block/wood/log_fence/%s_post' % wood, 'tfc:block/wood/log_fence/%s_side' % wood))
        block.with_lang(lang('%s log fence', wood))
        block.with_block_loot('tfc:wood/log_fence/%s' % wood)
        rm.block_model('wood/log_fence/%s_post' % wood, textures={'texture': 'tfc:block/wood/log/' + wood}, parent='block/fence_post')
        rm.block_model('wood/log_fence/%s_side' % wood, textures={'texture': 'tfc:block/wood/planks/' + wood}, parent='block/fence_side')
        rm.block_model('wood/log_fence/%s_inventory' % wood, textures={
            'log': 'tfc:block/wood/log/' + wood,
            'planks': 'tfc:block/wood/planks/' + wood
        }, parent='tfc:block/wood/log_fence/inventory')
        rm.item_model('wood/log_fence/%s' % wood, parent='tfc:block/wood/log_fence/%s_inventory' % wood, no_textures=True)

        # Support Beams
        texture = 'tfc:block/wood/sheet/%s' % wood
        connection = 'tfc:block/wood/support/%s_connection' % wood
        rm.blockstate_multipart(('wood', 'vertical_support', wood),
            {'model': 'tfc:block/wood/support/%s_vertical' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ).with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)
        rm.blockstate_multipart(('wood', 'horizontal_support', wood),
            {'model': 'tfc:block/wood/support/%s_horizontal' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ).with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)

        rm.block_model('tfc:wood/support/%s_inventory' % wood, textures={'texture': texture}, parent='tfc:block/wood/support/inventory')
        rm.block_model('tfc:wood/support/%s_vertical' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/vertical')
        rm.block_model('tfc:wood/support/%s_connection' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/connection')
        rm.block_model('tfc:wood/support/%s_horizontal' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/horizontal')
        rm.item_model(('wood', 'support', wood), no_textures=True, parent='tfc:block/wood/support/%s_inventory' % wood).with_lang(lang('%s Support', wood))

        # Chests / Trapped Chests
        for chest in ('chest', 'trapped_chest'):
            rm.blockstate(('wood', chest, wood), model='tfc:block/wood/%s/%s' % (chest, wood)).with_lang(lang('%s %s', wood, chest))
            rm.block_model(('wood', chest, wood), textures={'particle': 'tfc:block/wood/planks/%s' % wood}, parent=None)
            rm.item_model(('wood', chest, wood), {'particle': 'tfc:block/wood/planks/%s' % wood}, parent='minecraft:item/chest')
            rm.block_loot(('wood', chest, wood), {'name': 'tfc:wood/%s/%s' % (chest, wood)})

        rm.block_model('wood/sluice/%s_upper' % wood, textures={'texture': 'tfc:block/wood/sheet/%s' % wood}, parent='tfc:block/sluice_upper')
        rm.block_model('wood/sluice/%s_lower' % wood, textures={'texture': 'tfc:block/wood/sheet/%s' % wood}, parent='tfc:block/sluice_lower')
        block = rm.blockstate(('wood', 'sluice', wood), variants={**four_rotations('tfc:block/wood/sluice/%s_upper' % wood, (90, 0, 180, 270), suffix=',upper=true'), **four_rotations('tfc:block/wood/sluice/%s_lower' % wood, (90, 0, 180, 270), suffix=',upper=false')}).with_lang(lang('%s sluice', wood))
        block.with_block_loot({'name': 'tfc:wood/sluice/%s' % wood, 'conditions': [loot_tables.block_state_property('tfc:wood/sluice/%s[upper=true]' % wood)]})
        rm.item_model(('wood', 'sluice', wood), parent='tfc:block/wood/sluice/%s_lower' % wood, no_textures=True)

        # Signs / Hanging Signs
        rm.block_model('wood/sign/%s_particle' % wood, {
            'particle': 'tfc:block/wood/planks/%s' % wood
        }, parent=None)

        for variant in ('sign', 'wall_sign'):
            block = rm.blockstate(('wood', variant, wood), model='tfc:block/wood/sign/%s_particle' % wood)
            block.with_lang(lang('%s %s', wood, variant))
            block.with_block_loot('tfc:wood/sign/%s' % wood)

        for metal, metal_data in METALS.items():
            if metal_data.type == 'all':
                for variant in ('hanging_sign', 'wall_hanging_sign'):
                    block = rm.blockstate(('wood', variant, metal, wood), model='tfc:block/wood/sign/%s_particle' % wood)
                    block.with_lang(lang('%s %s %s', metal, wood, variant))
                    block.with_block_loot('tfc:wood/hanging_sign/%s/%s' % (metal, wood))

        # Barrels
        texture = 'tfc:block/wood/planks/%s' % wood
        textures = {'particle': texture, 'planks': texture, 'sheet': 'tfc:block/wood/sheet/%s' % wood}

        faces = (('up', 0), ('east', 0), ('west', 180), ('south', 90), ('north', 270))
        seals = (('true', 'barrel_sealed'), ('false', 'barrel'))
        racks = (('true', '_rack'), ('false', ''))
        block = rm.blockstate(('wood', 'barrel', wood), variants=dict((
            'facing=%s,rack=%s,sealed=%s' % (face, rack, is_seal), {'model': 'tfc:block/wood/%s/%s%s%s' % (seal_type, wood, '_side' if face != 'up' else '', suffix if face != 'up' else ''), 'y': yrot if yrot != 0 else None}
        ) for face, yrot in faces for rack, suffix in racks for is_seal, seal_type in seals))

        rm.item_model(('wood', 'barrel', wood), no_textures=True, parent='tfc:block/wood/barrel/%s' % wood, overrides=[override('tfc:block/wood/barrel_sealed/%s' % wood, 'tfc:sealed')])
        block.with_block_model(textures, 'tfc:block/barrel')
        rm.block_model(('wood', 'barrel', wood + '_side'), textures, 'tfc:block/barrel_side')
        rm.block_model(('wood', 'barrel', wood + '_side_rack'), textures, 'tfc:block/barrel_side_rack')
        rm.block_model(('wood', 'barrel_sealed', wood + '_side_rack'), textures, 'tfc:block/barrel_side_sealed_rack')
        rm.block_model(('wood', 'barrel_sealed', wood), textures, 'tfc:block/barrel_sealed')
        rm.block_model(('wood', 'barrel_sealed', wood + '_side'), textures, 'tfc:block/barrel_side_sealed')
        block.with_lang(lang('%s barrel', wood))
        block.with_block_loot(({
            'name': 'tfc:wood/barrel/%s' % wood,
            'functions': [copy_block_entity('tfc:barrel')],
            'conditions': [loot_tables.block_state_property('tfc:wood/barrel/%s[sealed=true]' % wood)]
        }, 'tfc:wood/barrel/%s' % wood))

        # Lecterns
        block = rm.blockstate('tfc:wood/lectern/%s' % wood, variants=four_rotations('tfc:block/wood/lectern/%s' % wood, (90, None, 180, 270)))
        block.with_block_model(textures={'bottom': 'tfc:block/wood/planks/%s' % wood, 'base': 'tfc:block/wood/lectern/%s/base' % wood, 'front': 'tfc:block/wood/lectern/%s/front' % wood, 'sides': 'tfc:block/wood/lectern/%s/sides' % wood, 'top': 'tfc:block/wood/lectern/%s/top' % wood, 'particle': 'tfc:block/wood/lectern/%s/sides' % wood}, parent='minecraft:block/lectern')
        block.with_item_model().with_lang(lang("%s lectern" % wood)).with_block_loot('tfc:wood/lectern/%s' % wood)
        # Scribing Table
        block = rm.blockstate('tfc:wood/scribing_table/%s' % wood, variants=four_rotations('tfc:block/wood/scribing_table/%s' % wood, (90, None, 180, 270)))
        block.with_block_model(textures={
            'top': 'tfc:block/wood/scribing_table/%s' % wood,
            'leg': 'tfc:block/wood/log/%s' % wood,
            'side': 'tfc:block/wood/planks/%s' % wood,
            'misc': 'tfc:block/wood/scribing_table/scribing_paraphernalia',
            'particle': 'tfc:block/wood/planks/%s' % wood
        }, parent='tfc:block/scribing_table')
        block.with_item_model().with_lang(lang("%s scribing table" % wood)).with_block_loot('tfc:wood/scribing_table/%s' % wood)

        block = rm.blockstate('wood/sewing_table/%s' % wood, variants=four_rotations('tfc:block/wood/sewing_table/%s' % wood, (90, None, 180, 270))).with_item_model()
        rm.block_model(('wood', 'sewing_table', wood), {'0': 'tfc:block/wood/log/%s' % wood, '1': 'tfc:block/wood/planks/%s' % wood}, 'tfc:block/sewing_table')
        block.with_lang(lang('%s sewing table', wood)).with_block_loot('tfc:wood/sewing_table/%s' % wood)

        # Shelf
        block = rm.blockstate('wood/shelf/%s' % wood, variants=four_rotations('tfc:block/wood/shelf/%s' % wood, (90, None, 180, 270)))
        block.with_block_model(textures={
            '0': 'tfc:block/wood/planks/%s' % wood
        }, parent='tfc:block/wood/shelf')
        block.with_item_model()
        block.with_lang(lang('%s shelf', wood))
        block.with_block_loot('tfc:wood/shelf/%s' % wood)

        # Axle
        block = rm.blockstate('tfc:wood/axle/%s' % wood, 'tfc:block/empty')
        block.with_lang(lang('%s axle', wood))
        block.with_block_loot('tfc:wood/axle/%s' % wood)
        block.with_block_model({'wood': 'tfc:block/wood/sheet/%s' % wood}, 'tfc:block/axle')
        rm.item_model('tfc:wood/axle/%s' % wood, no_textures=True, parent='tfc:block/wood/axle/%s' % wood)

        # Bladed Axle
        block = rm.blockstate('tfc:wood/bladed_axle/%s' % wood, 'tfc:block/empty')
        block.with_lang(lang('%s bladed axle', wood))
        block.with_block_loot('tfc:wood/bladed_axle/%s' % wood)
        block.with_block_model({'wood': 'tfc:block/wood/sheet/%s' % wood}, 'tfc:block/bladed_axle')
        rm.item_model('tfc:wood/bladed_axle/%s' % wood, no_textures=True, parent='tfc:block/wood/bladed_axle/%s' % wood)

        # Encased Axle
        block = rm.blockstate(('wood', 'encased_axle', wood), variants={
            'axis=x': {'model': 'tfc:block/wood/encased_axle/%s' % wood, 'x': 90, 'y': 90},
            'axis=y': {'model': 'tfc:block/wood/encased_axle/%s' % wood},
            'axis=z': {'model': 'tfc:block/wood/encased_axle/%s' % wood, 'x': 90},
        })
        block.with_lang(lang('%s encased axle', wood))
        block.with_block_loot('tfc:wood/encased_axle/%s' % wood)
        block.with_block_model({
            'side': 'tfc:block/wood/stripped_log/%s' % wood,
            'end': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing',
            'overlay_end': 'tfc:block/axle_casing_front',
            'particle': 'tfc:block/wood/stripped_log/%s' % wood
        }, parent='tfc:block/ore_column')
        block.with_item_model()

        # Clutch
        block = rm.blockstate(('wood', 'clutch', wood), variants={
            'axis=x,powered=false': {'model': 'tfc:block/wood/clutch/%s' % wood, 'x': 90, 'y': 90},
            'axis=x,powered=true': {'model': 'tfc:block/wood/clutch/%s_powered' % wood, 'x': 90, 'y': 90},
            'axis=y,powered=false': {'model': 'tfc:block/wood/clutch/%s' % wood},
            'axis=y,powered=true': {'model': 'tfc:block/wood/clutch/%s_powered' % wood},
            'axis=z,powered=false': {'model': 'tfc:block/wood/clutch/%s' % wood, 'x': 90},
            'axis=z,powered=true': {'model': 'tfc:block/wood/clutch/%s_powered' % wood, 'x': 90},
        })
        block.with_lang(lang('%s clutch', wood))
        block.with_block_loot('tfc:wood/clutch/%s' % wood)
        block.with_block_model({
            'side': 'tfc:block/wood/stripped_log/%s' % wood,
            'end': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing_unpowered',
            'overlay_end': 'tfc:block/axle_casing_front',
            'particle': 'tfc:block/wood/stripped_log/%s' % wood
        }, parent='tfc:block/ore_column')
        rm.block_model(('wood', 'clutch', '%s_powered' % wood), {
            'side': 'tfc:block/wood/stripped_log/%s' % wood,
            'end': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing_powered',
            'overlay_end': 'tfc:block/axle_casing_front',
            'particle': 'tfc:block/wood/stripped_log/%s' % wood
        }, parent='tfc:block/ore_column')
        block.with_item_model()

        # Gearbox
        gearbox_port = 'tfc:block/wood/gear_box_port/%s' % wood
        gearbox_face = 'tfc:block/wood/gear_box_face/%s' % wood

        block = rm.blockstate_multipart(
            ('wood', 'gear_box', wood),
            ({'north': True}, {'model': gearbox_port}),
            ({'north': False}, {'model': gearbox_face}),
            ({'south': True}, {'model': gearbox_port, 'y': 180}),
            ({'south': False}, {'model': gearbox_face, 'y': 180}),
            ({'east': True}, {'model': gearbox_port, 'y': 90}),
            ({'east': False}, {'model': gearbox_face, 'y': 90}),
            ({'west': True}, {'model': gearbox_port, 'y': 270}),
            ({'west': False}, {'model': gearbox_face, 'y': 270}),
            ({'down': True}, {'model': gearbox_port, 'x': 90}),
            ({'down': False}, {'model': gearbox_face, 'x': 90}),
            ({'up': True}, {'model': gearbox_port, 'x': 270}),
            ({'up': False}, {'model': gearbox_face, 'x': 270}),
        )
        block.with_lang(lang('%s gear box', wood))
        block.with_block_loot('tfc:wood/gear_box/%s' % wood)

        rm.block_model(('wood', 'gear_box_port', wood), {
            'all': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing_front',
        }, parent='tfc:block/gear_box_port')
        rm.block_model(('wood', 'gear_box_face', wood), {
            'all': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing_round'
        }, parent='tfc:block/gear_box_face')

        rm.item_model(('wood', 'gear_box', wood), {
            'all': 'tfc:block/wood/planks/%s' % wood,
            'overlay': 'tfc:block/axle_casing_front'
        }, parent='tfc:block/ore')

        # Windmill
        block = rm.blockstate('tfc:wood/windmill/%s' % wood, 'tfc:block/empty')
        block.with_lang(lang('%s windmill', wood))
        block.with_block_loot('tfc:wood/axle/%s' % wood,)

        # Water Wheel
        block = rm.blockstate('tfc:wood/water_wheel/%s' % wood)
        block.with_block_model({'particle': 'tfc:block/wood/planks/%s' % wood}, parent=None)
        block.with_lang(lang('%s water wheel', wood))
        block.with_block_loot('tfc:wood/water_wheel/%s' % wood)
        rm.item_model('tfc:wood/water_wheel/%s' % wood, 'tfc:item/wood/water_wheel_%s' % wood)

    # Extra Wood Stuff
    rm.blockstate(('wood', 'planks', 'palm_mosaic')).with_block_model().with_block_loot('tfc:wood/planks/palm_mosaic').with_lang(lang('palm mosaic')).with_item_model().make_slab().make_stairs()
    slab_loot(rm, 'tfc:wood/planks/palm_mosaic_slab')
    rm.block(('wood', 'planks', 'palm_mosaic_slab')).with_lang(lang('palm mosaic slab'))
    rm.block(('wood', 'planks', 'palm_mosaic_stairs')).with_lang(lang('palm mosaic stairs')).with_block_loot('tfc:wood/planks/palm_mosaic_stairs')

    rm.blockstate('tree_roots', model='minecraft:block/mangrove_roots').with_block_loot('tfc:tree_roots').with_lang(lang('tree roots'))
    rm.item_model('tree_roots', parent='minecraft:block/mangrove_roots', no_textures=True)

    rm.blockstate('light', variants={'level=%s' % i: {'model': 'minecraft:block/light_%s' % i if i >= 10 else 'minecraft:block/light_0%s' % i} for i in range(0, 15 + 1)}).with_lang(lang('Light'))
    rm.item_model('light', no_textures=True, parent='minecraft:item/light')

    rm.block_loot('minecraft:chest', {'name': 'tfc:wood/chest/oak', 'functions': [copy_block_entity()]})
    rm.block_loot('minecraft:trapped_chest', {'name': 'tfc:wood/trapped_chest/oak', 'functions': [copy_block_entity()]})

    # Pipes + Pump
    pipe_on = 'tfc:block/pipe_on'
    pipe_off = 'tfc:block/pipe_off'

    block = rm.blockstate_multipart(
        'steel_pipe',
        {'model': 'tfc:block/pipe_center'},
        ({'north': True}, {'model': pipe_on, 'x': 270}),
        ({'north': False}, {'model': pipe_off, 'x': 270}),
        ({'south': True}, {'model': pipe_on, 'x': 90}),
        ({'south': False}, {'model': pipe_off, 'x': 90}),
        ({'east': True}, {'model': pipe_on, 'x': 90, 'y': 270}),
        ({'east': False}, {'model': pipe_off, 'x': 90, 'y': 270}),
        ({'west': True}, {'model': pipe_on, 'x': 90, 'y': 90}),
        ({'west': False}, {'model': pipe_off, 'x': 90, 'y': 90}),
        ({'down': True}, {'model': pipe_on}),
        ({'down': False}, {'model': pipe_off}),
        ({'up': True}, {'model': pipe_on, 'x': 180}),
        ({'up': False}, {'model': pipe_off, 'x': 180}),
    )
    block.with_block_loot('tfc:steel_pipe')
    block.with_lang(lang('steel pipe'))
    rm.item_model('steel_pipe', parent='tfc:block/pipe_inventory', no_textures=True)

    block = rm.blockstate('steel_pump', variants=four_rotations('tfc:block/pump', (270, 180, None, 90)))
    block.with_block_loot('tfc:steel_pump')
    block.with_lang(lang('steel pump'))
    rm.item_model('steel_pump', parent='tfc:block/pump', no_textures=True)

    # Crankshaft
    block = rm.blockstate('crankshaft', variants={
        **four_rotations('tfc:block/crankshaft', (90, None, 180, 270), suffix=',part=base'),  # ENSW
        'part=shaft': {'model': 'tfc:block/empty'}
    })
    block.with_lang(lang('crankshaft'))
    block.with_block_loot({
        'name': 'tfc:crankshaft',
        'conditions': [loot_tables.block_state_property('tfc:crankshaft[part=base]')]
    }, {
        'name': 'tfc:metal/rod/steel',
        'conditions': [loot_tables.block_state_property('tfc:crankshaft[part=shaft]')]
    })
    rm.item_model('crankshaft', parent='tfc:block/crankshaft_with_wheel', no_textures=True)

    # Trip Hammer
    block = rm.blockstate('trip_hammer', variants={
        **four_rotations('tfc:block/trip_hammer', (90, None, 180, 270)),  # ENSW
    })
    block.with_lang(lang('trip hammer'))
    block.with_block_loot('tfc:trip_hammer')
    rm.item_model('trip_hammer', parent='tfc:block/trip_hammer', no_textures=True)

    # Candles
    for color in [None, *COLORS]:
        namespace = 'tfc:candle' + ('/' + color if color else '')
        candle = '%s_candle' % color if color else 'candle'
        block = rm.blockstate(namespace, variants={
            'candles=1,lit=false': {'model': 'minecraft:block/%s_one_candle' % candle},
            'candles=1,lit=true': {'model': 'minecraft:block/%s_one_candle_lit' % candle},
            'candles=2,lit=false': {'model': 'minecraft:block/%s_two_candles' % candle},
            'candles=2,lit=true': {'model': 'minecraft:block/%s_two_candles_lit' % candle},
            'candles=3,lit=false': {'model': 'minecraft:block/%s_three_candles' % candle},
            'candles=3,lit=true': {'model': 'minecraft:block/%s_three_candles_lit' % candle},
            'candles=4,lit=false': {'model': 'minecraft:block/%s_four_candles' % candle},
            'candles=4,lit=true': {'model': 'minecraft:block/%s_four_candles_lit' % candle}
        })
        block.with_lang(lang('%s candle' % color if color else 'candle'))
        block.with_block_loot(*[{'name': namespace, 'functions': [loot_tables.set_count(i)], 'conditions': [loot_tables.block_state_property('%s[candles=%s]' % (namespace, i))]} for i in range(1, 5)])
        rm.item_model(namespace, parent='minecraft:item/%s' % candle, no_textures=True)

        cake_space = 'tfc:candle_cake' + ('/' + color if color else '')
        cake = '%s_candle_cake' % color if color else 'candle_cake'
        rm.blockstate(cake_space, variants={
            'lit=true': {'model': 'minecraft:block/%s_lit' % cake},
            'lit=false': {'model': 'minecraft:block/%s' % cake},
        }).with_block_loot(namespace).with_lang(lang('%s candle cake' % color if color else 'candle cake'))


    rm.blockstate('cake', variants=dict(('bites=%s' % i, {'model': 'minecraft:block/cake%s' % ('_slice' + str(i) if i != 0 else '')}) for i in range(0, 7))).with_lang(lang('cake'))
    rm.item_model('cake', parent='minecraft:item/cake', no_textures=True)

    for color in COLORS:
        rm.blockstate('%s_poured_glass' % color).with_block_model({'all': 'minecraft:block/%s_stained_glass' % color}, parent='tfc:block/template_poured_glass').with_block_loot('minecraft:%s_stained_glass_pane' % color).with_lang(lang('%s poured glass', color))
        rm.item_model('%s_poured_glass' % color, 'minecraft:block/%s_stained_glass' % color)

        rm.block_loot('minecraft:%s_stained_glass' % color, 'minecraft:%s_stained_glass' % color)
        rm.block_loot('minecraft:%s_stained_glass_pane' % color, 'minecraft:%s_stained_glass_pane' % color)

        rm.item_model('windmill_blade/%s' % color, 'tfc:item/windmill_blade/%s' % color).with_lang(lang('%s windmill blade', color))

    rm.item_model('lattice_windmill_blade', 'tfc:item/windmill_blade/lattice').with_lang(lang('lattice windmill blade'))
    rm.item_model('rustic_windmill_blade', 'tfc:item/windmill_blade/rustic').with_lang(lang('rustic windmill blade'))

    rm.blockstate('poured_glass').with_block_model({'all': 'minecraft:block/glass'}, parent='tfc:block/template_poured_glass').with_lang(lang('poured glass')).with_block_loot('minecraft:glass_pane')
    rm.item_model('poured_glass', 'minecraft:block/glass')
    rm.blockstate('hot_poured_glass').with_block_model({'particle': 'tfc:block/glass/1'}, parent=None).with_lang(lang('hot poured glass'))
    rm.blockstate('glass_basin').with_block_model({'particle': 'tfc:block/glass/1'}, parent=None).with_lang(lang('glass basin'))

    rm.block_loot('minecraft:glass', 'minecraft:glass')
    rm.block_loot('minecraft:tinted_glass', 'minecraft:tinted_glass')
    rm.block_loot('minecraft:glass_pane', 'minecraft:glass_pane')

    # Entity Stuff
    for creature in SPAWN_EGG_ENTITIES:
        rm.item_model('spawn_egg/%s' % creature, parent='minecraft:item/template_spawn_egg', no_textures=True).with_lang(lang('%s Spawn Egg', creature))
    for creature in BUCKETABLE_FISH:
        rm.item_model('bucket/%s' % creature).with_lang(lang('%s Bucket', creature))

    # Fluids

    water_based_fluid(rm, 'salt_water')
    water_based_fluid(rm, 'spring_water')

    # River water, since it doesn't have a bucket
    rm.blockstate(('fluid', 'river_water')).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None).with_lang(lang('water'))

    for metal in METALS.keys():
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None).with_lang(lang('Molten %s', metal))
        rm.lang('fluid.tfc.metal.%s' % metal, lang('%s', metal))

        item = rm.custom_item_model(('bucket', 'metal', metal), 'neoforge:fluid_container', {
            'parent': 'neoforge:item/bucket',
            'fluid': 'tfc:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))

    # Thin Spikes: Calcite + Icicles
    for variant, texture in (('calcite', 'tfc:block/calcite'), ('icicle', 'minecraft:block/ice')):
        block = rm.blockstate(variant, variants={
            'tip=true': {'model': 'tfc:block/%s_tip' % variant},
            'tip=false': {'model': 'tfc:block/%s' % variant}
        })
        rm.item_model(variant)
        block.with_lang(lang(variant))

        rm.block_model(variant, textures={'0': texture, 'particle': texture}, parent='tfc:block/thin_spike')
        rm.block_model(variant + '_tip', textures={'0': texture, 'particle': texture}, parent='tfc:block/thin_spike_tip')

    for color in ('tube', 'brain', 'bubble', 'fire', 'horn'):
        corals(rm, color, False)
        corals(rm, color, True)

    rm.blockstate('bellows', model='tfc:block/bellows', variants=four_rotations('tfc:block/bellows', (270, 180, None, 90))).with_lang(lang('Bellows')).with_block_loot('tfc:bellows')

    rm.blockstate('ingot_pile', 'tfc:block/ingot_pile').with_lang(lang('ingot pile'))
    rm.blockstate('double_ingot_pile', 'tfc:block/double_ingot_pile').with_lang(lang('double ingot pile'))
    rm.blockstate('sheet_pile', 'tfc:block/sheet_pile').with_lang(lang('sheet pile'))

    rm.custom_block_model('ingot_pile', 'tfc:ingot_pile', {})
    rm.custom_block_model('double_ingot_pile', 'tfc:double_ingot_pile', {})
    rm.custom_block_model('sheet_pile', 'tfc:sheet_pile', {})


    for fluid in SIMPLE_FLUIDS:
        water_based_fluid(rm, fluid)

    for color in COLORS:
        water_based_fluid(rm, color + '_dye')

    # Atlases are combined like block tags
    # New atlas locations have to be registered and then used, there is not total freedom
    # For items, they use the blocks atlas
    rm.atlas('minecraft:blocks',
        atlases.palette(
            key='tfc:color_palettes/wood/planks/palette',
            textures=[
                'tfc:block/wood/bookshelf/top',
                'tfc:block/wood/bookshelf/side'
            ],
            permutations={
                wood: 'tfc:color_palettes/wood/planks/%s' % wood
                for wood in WOODS
            }
        ),
        atlases.palette(
            key='tfc:color_palettes/wood/planks/palette',
            textures=['tfc:item/wood/%s' % v for v in ('twig', 'lumber', 'chest_minecart_cover', 'stripped_log', 'sign_head', 'hanging_sign_head', 'water_wheel')],
            permutations=dict((wood, 'tfc:color_palettes/wood/plank_items/%s' % wood) for wood in WOODS.keys())
        ),
        atlases.palette(
            key='tfc:color_palettes/wood/planks/palette',
            textures=['tfc:item/wood/boat'],
            permutations=dict((wood, 'tfc:color_palettes/wood/plank_items/%s' % wood) for wood in WOODS.keys() if wood != 'palm')
        ),  # palm textures are manually done because it's a raft
        atlases.palette(
            key='trims/color_palettes/trim_palette',
            textures=['tfc:item/%s_trim' % section for section in TFC_ARMOR_SECTIONS],
            permutations=dict((mat + '_tfc', 'tfc:color_palettes/trims/%s' % mat) for mat in TRIM_MATERIALS)
        ),
        atlases.directory('entity/bell')
    )

    rm.atlas('minecraft:armor_trims',atlases.palette(
        key='trims/color_palettes/trim_palette',
        textures=['trims/models/armor/%s%s' % (pattern, suffix) for pattern in VANILLA_TRIMS for suffix in ('', '_leggings')],
        permutations=dict((mat + '_tfc', 'tfc:color_palettes/trims/%s' % mat) for mat in TRIM_MATERIALS)
    ))


def flower_pot_cross(rm: ResourceManager, simple_name: str, name: str, model: str, texture: str, loot: str):
    rm.blockstate(name, model='tfc:block/%s' % model).with_lang(lang('potted %s', simple_name)).with_block_loot(loot, 'minecraft:flower_pot')
    rm.block_model(model, parent='minecraft:block/flower_pot_cross', textures={'plant': texture, 'dirt': 'tfc:block/dirt/loam'})


def water_based_fluid(rm: ResourceManager, name: str):
    rm.blockstate(('fluid', name)).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None).with_lang(lang(name))

    item = rm.custom_item_model(('bucket', name), 'neoforge:fluid_container', {
        'parent': 'neoforge:item/bucket',
        'fluid': 'tfc:%s' % name
    })
    item.with_lang(lang('%s bucket', name))
    rm.lang('fluid.tfc.%s' % name, lang(name))


def corals(rm: ResourceManager, color: str, dead: bool):
    # vanilla and tfc have a different convention for dead/color order
    left = 'dead_' + color if dead else color
    right = color + '_dead' if dead else color

    rm.blockstate('coral/%s_coral' % right, 'minecraft:block/%s_coral' % left).with_block_loot('tfc:coral/%s_coral' % right)
    rm.blockstate('coral/%s_coral_fan' % right, 'minecraft:block/%s_coral_fan' % left).with_block_loot('tfc:coral/%s_coral_fan' % right)
    rm.blockstate('coral/%s_coral_wall_fan' % right, variants=dict(
        ('facing=%s' % d, {'model': 'minecraft:block/%s_coral_wall_fan' % left, 'y': r})
        for d, r in (('north', None), ('east', 90), ('south', 180), ('west', 270))
    )).with_block_loot('tfc:coral/%s_coral_fan' % right)

    for variant in ('coral', 'coral_fan', 'coral_wall_fan'):
        rm.item_model('coral/%s_%s' % (right, variant), 'minecraft:block/%s_%s' % (left, variant))
        rm.lang('block.tfc.coral.%s_%s' % (right, variant), lang('%s %s', left, variant))


def four_ways(model: str) -> list[dict[str, Any]]:
    return [
        {'model': model, 'y': 90},
        {'model': model},
        {'model': model, 'y': 180},
        {'model': model, 'y': 270}
    ]


def four_rotations(model: str, rots: Tuple[Any, Any, Any, Any], suffix: str = '', prefix: str = '') -> Dict[str, Dict[str, Any]]:
    return {
        '%sfacing=east%s' % (prefix, suffix): {'model': model, 'y': rots[0]},
        '%sfacing=north%s' % (prefix, suffix): {'model': model, 'y': rots[1]},
        '%sfacing=south%s' % (prefix, suffix): {'model': model, 'y': rots[2]},
        '%sfacing=west%s' % (prefix, suffix): {'model': model, 'y': rots[3]}
    }

def crop_yield(lo: int, hi: Tuple[int, int]) -> utils.Json:
    return {
        'function': 'minecraft:set_count',
        'count': {
            'type': 'tfc:crop_yield_uniform',
            'min': lo,
            'max': {
                'type': 'minecraft:uniform',
                'min': hi[0],
                'max': hi[1]
            }
        }
    }


def make_javelin(rm: ResourceManager, name_parts: str, texture: str) -> 'ItemContext':
    rm.item_model(name_parts + '_throwing_base', {'particle': texture}, parent='minecraft:item/trident_throwing')
    rm.item_model(name_parts + '_in_hand', {'particle': texture}, parent='minecraft:item/trident_in_hand')
    rm.item_model(name_parts + '_gui', texture)
    model = rm.domain + ':item/' + name_parts
    correct_perspectives = {
        'none': {'parent': model + '_gui'},
        'fixed': {'parent': model + '_gui'},
        'ground': {'parent': model + '_gui'},
        'gui': {'parent': model + '_gui'}
    }
    rm.custom_item_model(name_parts + '_throwing', 'neoforge:separate_transforms', {
        'gui_light': 'front',
        'base': {'parent': model + '_throwing_base'},
        'perspectives': correct_perspectives
    })

    return rm.custom_item_model(name_parts, 'neoforge:separate_transforms', {
        'gui_light': 'front',
        'overrides': [{'predicate': {'tfc:throwing': 1}, 'model': model + '_throwing'}],
        'base': {'parent': model + '_in_hand'},
        'perspectives': correct_perspectives
    })


def contained_fluid(rm: ResourceManager, name_parts: utils.ResourceIdentifier, base: str, overlay: str) -> 'ItemContext':
    return rm.custom_item_model(name_parts, 'tfc:fluid_container', {
        'parent': 'neoforge:item/default',
        'textures': {
            'base': base,
            'fluid': overlay
        },
    })

def trim_model(rm: ResourceManager, name_parts: utils.ResourceIdentifier, base: str, trim: str, overlay: str = None) -> 'ItemContext':
    return rm.custom_item_model(name_parts, 'tfc:trim', {
        'parent': 'neoforge:item/default',
        'textures': {
            'armor': base,
            'trim': trim,
            'overlay': overlay
        }
    })


def slab_loot(rm: ResourceManager, loot: str) -> BlockContext:
    return rm.block_loot(loot, {
        'name': loot,
        'functions': [{
            'function': 'minecraft:set_count',
            'conditions': [loot_tables.block_state_property(loot + '[type=double]')],
            'count': 2,
            'add': False
        }]
    })


def door_blockstate(base: str) -> JsonObject:
    left = base + '_bottom_left'
    left_open = base + '_bottom_left_open'
    right = base + '_bottom_right'
    right_open = base + '_bottom_right_open'
    top_left = base + '_top_left'
    top_left_open = base + '_top_left_open'
    top_right = base + '_top_right'
    top_right_open = base + '_top_right_open'
    return {
        'facing=east,half=lower,hinge=left,open=false': {'model': left},
        'facing=east,half=lower,hinge=left,open=true': {'model': left_open, 'y': 90},
        'facing=east,half=lower,hinge=right,open=false': {'model': right},
        'facing=east,half=lower,hinge=right,open=true': {'model': right_open, 'y': 270},
        'facing=east,half=upper,hinge=left,open=false': {'model': top_left},
        'facing=east,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 90},
        'facing=east,half=upper,hinge=right,open=false': {'model': top_right},
        'facing=east,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 270},
        'facing=north,half=lower,hinge=left,open=false': {'model': left, 'y': 270},
        'facing=north,half=lower,hinge=left,open=true': {'model': left_open},
        'facing=north,half=lower,hinge=right,open=false': {'model': right, 'y': 270},
        'facing=north,half=lower,hinge=right,open=true': {'model': right_open, 'y': 180},
        'facing=north,half=upper,hinge=left,open=false': {'model': top_left, 'y': 270},
        'facing=north,half=upper,hinge=left,open=true': {'model': top_left_open},
        'facing=north,half=upper,hinge=right,open=false': {'model': top_right, 'y': 270},
        'facing=north,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 180},
        'facing=south,half=lower,hinge=left,open=false': {'model': left, 'y': 90},
        'facing=south,half=lower,hinge=left,open=true': {'model': left_open, 'y': 180},
        'facing=south,half=lower,hinge=right,open=false': {'model': right, 'y': 90},
        'facing=south,half=lower,hinge=right,open=true': {'model': right_open},
        'facing=south,half=upper,hinge=left,open=false': {'model': top_left, 'y': 90},
        'facing=south,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 180},
        'facing=south,half=upper,hinge=right,open=false': {'model': top_right, 'y': 90},
        'facing=south,half=upper,hinge=right,open=true': {'model': top_right_open},
        'facing=west,half=lower,hinge=left,open=false': {'model': left, 'y': 180},
        'facing=west,half=lower,hinge=left,open=true': {'model': left_open, 'y': 270},
        'facing=west,half=lower,hinge=right,open=false': {'model': right, 'y': 180},
        'facing=west,half=lower,hinge=right,open=true': {'model': right_open, 'y': 90},
        'facing=west,half=upper,hinge=left,open=false': {'model': top_left, 'y': 180},
        'facing=west,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 270},
        'facing=west,half=upper,hinge=right,open=false': {'model': top_right, 'y': 180},
        'facing=west,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 90}
    }


def when_silk_touch(item: str):
    return {'name': item, 'conditions': [loot_tables.silk_touch()]}


def when_sheared(item: str):
    return {'name': item, 'conditions': [loot_tables.any_of(
        loot_tables.match_tag(TAG_SHEARS),
        loot_tables.silk_touch()
    )]}


def override(model: str, name: str, value: float = 1.0):
    return {'predicate': {name: value}, 'model': model}

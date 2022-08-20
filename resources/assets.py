#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import itertools

from mcresources import ResourceManager, ItemContext, utils, block_states, loot_tables

from constants import *


def generate(rm: ResourceManager):
    # Rock block variants
    for rock, rock_data in ROCKS.items():
        for block_type in ROCK_BLOCK_TYPES:
            if block_type == 'spike':
                # Spikes have special block states
                block = rm.blockstate(('rock', block_type, rock), variants=dict(('part=%s' % part, {'model': 'tfc:block/rock/%s/%s_%s' % (block_type, rock, part)}) for part in ROCK_SPIKE_PARTS))
                block.with_lang(lang('%s spike', rock))
                block.with_block_loot('1-2 tfc:rock/loose/%s' % rock)
                # Individual models
                rm.item_model(('rock', block_type, rock), 'tfc:block/rock/raw/%s' % rock, parent='tfc:block/rock/spike/%s_base' % rock)
                for part in ROCK_SPIKE_PARTS:
                    rm.block_model(('rock', block_type, '%s_%s' % (rock, part)), {
                        'texture': 'tfc:block/rock/raw/%s' % rock,
                        'particle': 'tfc:block/rock/raw/%s' % rock
                    }, parent='tfc:block/rock/spike_%s' % part)

            elif block_type == 'loose':
                # One block state and multiple models for the block
                block = rm.blockstate('rock/loose/%s' % rock, variants={
                    'count=1': four_ways('tfc:block/rock/pebble/%s' % rock),
                    'count=2': four_ways('tfc:block/rock/rubble/%s' % rock),
                    'count=3': four_ways('tfc:block/rock/boulder/%s' % rock),
                }, use_default_model=False)
                for loose_type in ('pebble', 'rubble', 'boulder'):
                    rm.block_model('tfc:rock/%s/%s' % (loose_type, rock), 'tfc:item/loose_rock/%s' % rock, parent='tfc:block/groundcover/%s' % loose_type)

                block.with_lang(lang('loose %s', rock)).with_tag('can_be_snow_piled').with_block_loot({
                    'name': 'tfc:rock/loose/%s' % rock,
                    'functions': [
                        {**loot_tables.set_count(2), 'conditions': [loot_tables.block_state_property('tfc:rock/loose/%s[count=2]' % rock)]},
                        {**loot_tables.set_count(3), 'conditions': [loot_tables.block_state_property('tfc:rock/loose/%s[count=3]' % rock)]},
                        loot_tables.explosion_decay()
                    ]
                })

                # Model for the item
                rm.item_model(('rock', 'loose', rock), 'tfc:item/loose_rock/%s' % rock)

            elif block_type == 'pressure_plate':
                block = rm.block(('rock', 'pressure_plate', rock))
                block.make_pressure_plate(pressure_plate_suffix='', texture='tfc:block/rock/raw/%s' % rock)
                block.with_lang(lang('%s pressure plate', rock))
                block.with_block_loot('tfc:rock/pressure_plate/%s' % rock)
            elif block_type == 'button':
                block = rm.block(('rock', 'button', rock))
                block.make_button(button_suffix='', texture='tfc:block/rock/raw/%s' % rock)
                block.with_lang(lang('%s button', rock))
                block.with_block_loot('tfc:rock/button/%s' % rock)
            else:
                block = rm.blockstate(('rock', block_type, rock))
                if block_type == 'hardened':
                    block.with_block_model('tfc:block/rock/raw/%s' % rock)  # Hardened uses the raw model
                else:
                    block.with_block_model('tfc:block/rock/%s/%s' % (block_type, rock))
                block.with_item_model()

                if block_type in CUTTABLE_ROCKS:
                    # Stairs
                    rm.block(('rock', block_type, rock)).make_stairs()
                    rm.block(('rock', block_type, rock + '_stairs')).with_lang(lang('%s %s Stairs', rock, block_type)).with_block_loot('tfc:rock/%s/%s_stairs' % (block_type, rock))
                    # Slabs
                    rm.block(('rock', block_type, rock)).make_slab()
                    rm.block(('rock', block_type, rock + '_slab')).with_lang(lang('%s %s Slab', rock, block_type)).with_tag('minecraft:slabs')
                    slab_loot(rm, 'tfc:rock/%s/%s_slab' % (block_type, rock))
                    # Walls
                    rm.block(('rock', block_type, rock)).make_wall()
                    rm.block(('rock', block_type, rock + '_wall')).with_lang(lang('%s %s Wall', rock, block_type)).with_block_loot('tfc:rock/%s/%s_wall' % (block_type, rock)).with_tag('minecraft:walls')
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
                    block.with_block_loot(({
                        'conditions': [loot_tables.silk_touch()],
                        'name': 'tfc:rock/gravel/%s' % rock
                    }, {
                        'type': 'minecraft:alternatives',
                        'conditions': 'minecraft:survives_explosion',
                        'children': [{
                            'type': 'minecraft:item',
                            'conditions': [loot_tables.fortune_table((0.1, 0.14285715, 0.25, 1.0))],
                            'name': 'minecraft:flint'
                        }, {
                            'type': 'minecraft:item',
                            'name': 'tfc:rock/gravel/%s' % rock
                        }]
                    }))
                else:
                    block.with_block_loot('tfc:rock/%s/%s' % (block_type, rock))
                # Lang
                if block_type in {'smooth', 'raw', 'chiseled', 'hardened'}:
                    block.with_lang(lang('%s %s', block_type, rock))
                else:
                    block.with_lang(lang('%s %s', rock, block_type))

        if rock_data.category == 'igneous_extrusive' or rock_data.category == 'igneous_intrusive':
            rm.blockstate('tfc:rock/anvil/%s' % rock, model='tfc:block/rock/anvil/%s' % rock).with_lang(lang('%s Anvil', rock)).with_block_loot('1-4 tfc:rock/loose/%s' % rock).with_item_model()
            rm.block_model('tfc:rock/anvil/%s' % rock, parent='tfc:block/rock/anvil', textures={'texture': 'tfc:block/rock/raw/%s' % rock})
            rm.blockstate('tfc:rock/magma/%s' % rock, model='tfc:block/rock/magma/%s' % rock).with_block_model(parent='minecraft:block/cube_all', textures={'all': 'tfc:block/rock/magma/%s' % rock}).with_lang(lang('%s magma block', rock)).with_item_model().with_block_loot('tfc:rock/magma/%s' % rock)

        # Ores
        for ore, ore_data in ORES.items():
            if ore_data.graded:
                # Small Ores / Groundcover Blocks
                block = rm.blockstate('tfc:ore/small_%s' % ore, variants={"": four_ways('tfc:block/groundcover/%s' % ore)}, use_default_model=False)
                block.with_lang(lang('small %s', ore)).with_block_loot('tfc:ore/small_%s' % ore).with_tag('can_be_snow_piled')

                rm.item_model('tfc:ore/small_%s' % ore).with_lang(lang('small %s', ore))

                for grade in ORE_GRADES.keys():
                    block = rm.blockstate(('ore', grade + '_' + ore, rock), 'tfc:block/ore/%s_%s/%s' % (grade, ore, rock))
                    block.with_block_model({
                        'all': 'tfc:block/rock/raw/%s' % rock,
                        'particle': 'tfc:block/rock/raw/%s' % rock,
                        'overlay': 'tfc:block/ore/%s_%s' % (grade, ore)
                    }, parent='tfc:block/ore')
                    block.with_item_model()
                    block.with_lang(lang('%s %s %s', grade, rock, ore))
                    block.with_block_loot('tfc:ore/%s_%s' % (grade, ore))
            else:
                block = rm.blockstate(('ore', ore, rock), 'tfc:block/ore/%s/%s' % (ore, rock))
                block.with_block_model({
                    'all': 'tfc:block/rock/raw/%s' % rock,
                    'particle': 'tfc:block/rock/raw/%s' % rock,
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
            for grade in ORE_GRADES.keys():
                rm.item_model('tfc:ore/%s_%s' % (grade, ore)).with_lang(lang('%s %s', grade, ore))
            rm.item_model('tfc:ore/small_%s' % ore).with_lang(lang('small %s', ore))
        else:
            item = rm.item_model('tfc:ore/%s' % ore)
            if ore == 'diamond':
                item.with_lang(lang('kimberlite'))
            else:
                item.with_lang(lang(ore))

    # Sand
    for sand in SAND_BLOCK_TYPES:
        block = rm.blockstate(('sand', sand))
        block.with_block_model('tfc:block/sand/%s' % sand)
        block.with_item_model()
        block.with_block_loot('tfc:sand/%s' % sand)
        block.with_lang(lang('%s Sand', sand))

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
            rm.block_tag('minecraft:walls', 'tfc:%s_sandstone/%s_wall' % (variant, sand))

            for extra in ('', '_slab', '_stairs', '_wall'):
                block = rm.block(('%s_sandstone' % variant, sand + extra))
                if extra == '_slab':
                    slab_loot(rm, 'tfc:%s_sandstone/%s%s' % (variant, sand, extra))
                    rm.block_tag('minecraft:slabs', 'tfc:%s_sandstone/%s%s' % (variant, sand, extra))
                else:
                    block.with_block_loot('tfc:%s_sandstone/%s%s' % (variant, sand, extra))
                block.with_lang(lang('%s %s sandstone' + extra, variant, sand))

    # Groundcover
    for misc in MISC_GROUNDCOVER:
        block = rm.blockstate(('groundcover', misc), variants={"": four_ways('tfc:block/groundcover/%s' % misc)}, use_default_model=False)
        block.with_lang(lang(misc))
        block.with_tag('can_be_snow_piled')

        if misc in {'stick', 'flint', 'feather', 'rotten_flesh', 'bone'}:  # Vanilla ground cover
            block.with_block_loot('minecraft:%s' % misc)
        elif misc == 'salt_lick':
            block.with_block_loot('tfc:powder/salt')
        else:
            block.with_block_loot('tfc:groundcover/%s' % misc)
            rm.item_model(('groundcover', misc), 'tfc:item/groundcover/%s' % misc)

    for block in SIMPLE_BLOCKS:
        rm.blockstate(block).with_block_model().with_item_model().with_block_loot('tfc:%s' % block).with_lang(lang(block))

    for name in ('pumpkin', 'melon'):
        rm.block_model(name, parent='minecraft:block/%s' % name, no_textures=True)
        rm.blockstate(name, model='tfc:block/%s' % name).with_lang(lang(name)).with_tag('tfc:mineable_with_sharp_tool')
        rm.item_model(name, 'tfc:item/food/%s' % name)
        rm.block_model('rotten_' + name, parent='minecraft:block/%s' % name, textures={'side': 'tfc:block/crop/rotten_%s_side' % name, 'end': 'tfc:block/crop/rotten_%s_top' % name})
        rm.blockstate('rotten_' + name, model='tfc:block/rotten_%s' % name).with_lang(lang('rotten %s', name)).with_block_loot('tfc:rotten_%s' % name).with_tag('tfc:mineable_with_sharp_tool')
        rm.item_model('rotten_' + name, 'tfc:item/food/%s' % name)

    rm.blockstate('jack_o_lantern', variants=four_rotations('minecraft:block/jack_o_lantern', (90, 0, 180, 270))).with_tag('tfc:mineable_with_sharp_tool').with_block_loot('tfc:jack_o_lantern').with_lang(lang('Jack o\'Lantern'))
    rm.item_model('jack_o_lantern', parent='minecraft:block/jack_o_lantern')

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
            block = rm.block(('alabaster', 'bricks', color + '_' + extra)).with_lang(lang('%s Alabaster Bricks %s', color, extra))
            if extra != 'slab':
                block.with_block_loot('tfc:alabaster/bricks/%s_%s' % (color, extra))
            else:
                slab_loot(rm, 'tfc:alabaster/bricks/%s_%s' % (color, extra))
                rm.block_tag('minecraft:slabs', 'tfc:alabaster/bricks/%s_%s' % (color, extra))
            block = rm.block(('alabaster', 'polished', color + '_' + extra)).with_lang(lang('%s Polished Alabaster %s', color, extra))
            if extra != 'slab':
                block.with_block_loot('tfc:alabaster/polished/%s_%s' % (color, extra))
            else:
                slab_loot(rm, 'tfc:alabaster/polished/%s_%s' % (color, extra))
                rm.block_tag('minecraft:slabs', 'tfc:alabaster/polished/%s_%s' % (color, extra))

    rm.item_model('torch', 'minecraft:block/torch')
    rm.item_model('dead_torch', 'tfc:block/torch_off')
    rm.block_model('dead_torch', parent='minecraft:block/template_torch', textures={'torch': 'tfc:block/torch_off'})
    rm.block_model('dead_wall_torch', parent='minecraft:block/template_torch_wall', textures={'torch': 'tfc:block/torch_off'})
    rm.blockstate('wall_torch', variants=four_rotations('minecraft:block/wall_torch', (None, 270, 90, 180))).with_lang(lang('Torch'))
    rm.blockstate('dead_wall_torch', variants=four_rotations('tfc:block/dead_wall_torch', (None, 270, 90, 180))).with_lang(lang('Burnt Out Torch'))
    rm.blockstate('torch', 'minecraft:block/torch').with_block_loot('tfc:torch').with_lang(lang('Torch'))
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
        'sealed=true': {'model': 'tfc:block/ceramic/large_vessel_sealed'},
        'sealed=false': {'model': 'tfc:block/ceramic/large_vessel_opened'}
    })
    block.with_lang(lang('large vessel'))
    block.with_block_loot(({
        'name': 'tfc:ceramic/large_vessel',
        'functions': [loot_tables.copy_block_entity_name(), loot_tables.copy_block_entity_nbt()],
        'conditions': [loot_tables.block_state_property('tfc:ceramic/large_vessel[sealed=true]')]
    }, 'tfc:ceramic/large_vessel'))
    block.with_tag('minecraft:mineable/pickaxe')
    rm.block_model('tfc:ceramic/large_vessel_sealed', textures={'top': 'tfc:block/ceramic/large_vessel/top', 'side': 'tfc:block/ceramic/large_vessel/side','bottom':'tfc:block/ceramic/large_vessel/bottom', 'particle': 'tfc:block/ceramic/large_vessel/side'}, parent='tfc:block/large_vessel_sealed')
    rm.block_model('tfc:ceramic/large_vessel_opened', textures={'side': 'tfc:block/ceramic/large_vessel/side','bottom':'tfc:block/ceramic/large_vessel/bottom', 'particle': 'tfc:block/ceramic/large_vessel/side'}, parent='tfc:block/large_vessel_opened')
    item_model_property(rm, 'tfc:ceramic/large_vessel', [{'predicate': {'tfc:sealed': 1.0}, 'model': 'tfc:block/ceramic/large_vessel_sealed'}], {'parent': 'tfc:block/ceramic/large_vessel_opened'})
    
    # Unfired large undyed vessel
    rm.item_model('tfc:ceramic/unfired_large_vessel', {'top': 'tfc:block/ceramic/large_vessel/top_clay', 'side': 'tfc:block/ceramic/large_vessel/side_clay','bottom':'tfc:block/ceramic/large_vessel/bottom_clay', 'particle': 'tfc:block/ceramic/large_vessel/side_clay'}, parent='tfc:block/ceramic/large_vessel_sealed')
    rm.lang('item.tfc.ceramic.unfired_large_vessel', lang("unfired large vessel"))

    for color in COLORS:
        vessel = 'tfc:ceramic/large_vessel/%s' % color
        block = rm.blockstate(vessel, variants={
            'sealed=true': {'model': 'tfc:block/ceramic/%s_large_vessel_sealed' % color},
            'sealed=false': {'model': 'tfc:block/ceramic/%s_large_vessel_opened' % color}
        })
        block.with_lang(lang('%s large vessel', color))
        block.with_block_loot(({
            'name': vessel,
            'functions': [loot_tables.copy_block_entity_name(), loot_tables.copy_block_entity_nbt()],
            'conditions': [loot_tables.block_state_property(vessel + '[sealed=true]')]
        }, vessel))
        block.with_tag('minecraft:mineable/pickaxe')
        rm.block_model('tfc:ceramic/%s_large_vessel_sealed' % color, textures={'top': 'tfc:block/ceramic/large_vessel/glazed/%s/top' % color, 'side': 'tfc:block/ceramic/large_vessel/glazed/%s/side' % color,'bottom':'tfc:block/ceramic/large_vessel/glazed/%s/bottom' % color, 'particle': 'tfc:block/ceramic/large_vessel/glazed/%s/side' % color}, parent='tfc:block/large_vessel_sealed')
        rm.block_model('tfc:ceramic/%s_large_vessel_opened' % color, textures={'side': 'tfc:block/ceramic/large_vessel/glazed/%s/side' % color,'bottom':'tfc:block/ceramic/large_vessel/glazed/%s/bottom' % color, 'particle': 'tfc:block/ceramic/large_vessel/glazed/%s/side' % color}, parent='tfc:block/large_vessel_opened')
        item_model_property(rm, 'tfc:ceramic/large_vessel/%s' % color, [{'predicate': {'tfc:sealed': 1.0}, 'model': 'tfc:block/ceramic/%s_large_vessel_sealed' % color}], {'parent': 'tfc:block/ceramic/%s_large_vessel_opened' % color})
        rm.item_model('tfc:ceramic/unfired_large_vessel/%s' % color, {'top': 'tfc:block/ceramic/large_vessel/glazed/%s/top_clay' % color, 'side': 'tfc:block/ceramic/large_vessel/glazed/%s/side_clay' % color,'bottom':'tfc:block/ceramic/large_vessel/glazed/%s/bottom_clay' % color, 'particle': 'tfc:block/ceramic/large_vessel/glazed/%s/side_clay' % color}, parent='tfc:block/ceramic/large_vessel_sealed')
        rm.lang('item.tfc.ceramic.unfired_large_vessel.%s' % color, lang("%s unfired large vessel", color))
    

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
        'functions': [loot_tables.copy_block_entity_name(), loot_tables.copy_block_entity_nbt()]
    })

    block = rm.block('thatch_bed')
    block.with_lang(lang('thatch bed'))

    rm.blockstate('nest_box', model='tfc:block/nest_box').with_block_loot('tfc:nest_box').with_lang(lang('nest box')).with_item_model()

    rm.blockstate('firepit', variants={
        'lit=true': {'model': 'tfc:block/firepit_lit'},
        'lit=false': {'model': 'tfc:block/firepit_unlit'}
    }).with_lang(lang('Firepit')).with_block_loot('1-4 tfc:powder/wood_ash')
    rm.item_model('firepit', parent='tfc:block/firepit_unlit')

    rm.blockstate_multipart('grill',
        ({'model': 'tfc:block/firepit_grill'}),
        ({'lit': True}, {'model': 'tfc:block/firepit_lit'}),
        ({'lit': False}, {'model': 'tfc:block/firepit_unlit'})
    ).with_lang(lang('Grill')).with_block_loot('1-4 tfc:powder/wood_ash', 'tfc:wrought_iron_grill')
    rm.item_model('grill', parent='tfc:block/firepit_grill')

    rm.blockstate_multipart('pot',
        ({'model': 'tfc:block/firepit_pot'}),
        ({'lit': True}, {'model': 'tfc:block/firepit_lit'}),
        ({'lit': False}, {'model': 'tfc:block/firepit_unlit'})
    ).with_lang(lang('Pot')).with_block_loot('1-4 tfc:powder/wood_ash', 'tfc:ceramic/pot')
    rm.item_model('pot', parent='tfc:block/firepit_pot', no_textures=True)

    block = rm.blockstate('powderkeg', variants={
        'lit=false,sealed=true': {'model': 'tfc:block/powderkeg_sealed'},
        'lit=false,sealed=false': {'model': 'tfc:block/powderkeg'},
        'lit=true,sealed=true': {'model': 'tfc:block/powderkeg_lit'},
        'lit=true,sealed=false': {'model': 'tfc:block/powderkeg'}  # cannot occur
    }).with_lang(lang('Powderkeg')).with_tag('minecraft:mineable/axe')
    block.with_block_loot(({
        'name': 'tfc:powderkeg',
        'functions': [loot_tables.copy_block_entity_name(), loot_tables.copy_block_entity_nbt()],
        'conditions': [loot_tables.block_state_property('tfc:powderkeg[sealed=true]')]
    }, 'tfc:powderkeg'))
    item_model_property(rm, 'tfc:powderkeg', [{'predicate': {'tfc:sealed': 1.0}, 'model': 'tfc:block/powderkeg_sealed'}], {'parent': 'tfc:block/powderkeg'})
     
    states = [({'model': 'tfc:block/composter/composter'})]
    for i in range(1, 9):
        for age in ('normal', 'ready', 'rotten'):
            rm.block_model('tfc:composter/%s_%s' % (age, i), parent='tfc:block/composter/compost_%s' % i, textures={'0': 'tfc:block/devices/composter/%s' % age})
            states.append(({'type': age, 'stage': i}, {'model': 'tfc:block/composter/%s_%s' % (age, i)}),)
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

    rm.item_model('raw_iron_bloom', 'tfc:item/bloom/unrefined').with_lang(lang('Raw Iron Bloom')).with_tag('blooms')
    rm.item_model('refined_iron_bloom', 'tfc:item/bloom/refined').with_lang(lang('Refined Iron Bloom')).with_tag('blooms')

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
    rm.blockstate('scraping', 'tfc:block/empty').with_lang(lang('scraped item'))
    rm.blockstate('pit_kiln', variants=dict((('stage=%d' % i), {'model': 'tfc:block/pitkiln/pitkiln_%d' % i}) for i in range(0, 1 + 16))).with_lang(lang('Pit Kiln'))

    # Dirt
    for soil in SOIL_BLOCK_VARIANTS:
        # Regular Dirt
        block = rm.blockstate(('dirt', soil), variants={'': [{'model': 'tfc:block/dirt/%s' % soil, 'y': i} for i in range(0, 360, 90)]}, use_default_model=False)
        block.with_block_model().with_item_model().with_block_loot('tfc:dirt/%s' % soil).with_lang(lang('%s Dirt', soil))
        for variant in ('mud', 'rooted_dirt', 'mud_bricks'):
            rm.blockstate((variant, soil)).with_block_model().with_item_model().with_block_loot('tfc:%s/%s' % (variant, soil)).with_lang(lang('%s %s', soil, variant))

        rm.item_model('mud_brick/%s' % soil).with_lang(lang('%s mud brick', soil))
        mud_bricks = rm.block(('mud_bricks', soil))
        mud_bricks.make_slab()
        mud_bricks.make_stairs()
        mud_bricks.make_wall()
        rm.block_tag('minecraft:walls', ('mud_bricks', soil + '_wall'))
        for variant in ('_stairs', '_slab', '_wall'):
            block = rm.block('mud_bricks/%s%s' % (soil, variant)).with_lang(lang('%s mud bricks%s', soil, variant)).with_tag('minecraft:mineable/shovel')
            if variant == '_slab':
                slab_loot(rm, 'tfc:mud_bricks/%s%s' % (soil, variant))
                rm.block_tag('minecraft:slabs', 'tfc:mud_bricks/%s%s' % (soil, variant))
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

    # Grass
    north_face = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#texture', 'cullface': 'north'}}}
    north_face_overlay = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#overlay', 'cullface': 'north'}}}
    north_face_overlay_tint0 = {'from': [0, 0, 0], 'to': [16, 16, 0], 'faces': {'north': {'texture': '#overlay', 'cullface': 'north', 'tintindex': 0}}}

    rm.block_model('grass_top', textures={'overlay': 'tfc:block/grass_top', 'particle': '#texture'}, parent='block/block', elements=[north_face_overlay_tint0])
    rm.block_model('grass_snowy_top', textures={'overlay': 'minecraft:block/snow', 'particle': '#texture'}, parent='block/block', elements=[north_face_overlay])
    rm.block_model('grass_side', textures={'overlay': 'tfc:block/grass_side', 'particle': '#texture'}, parent='block/block', elements=[north_face, north_face_overlay_tint0])
    rm.block_model('grass_snowy_side', textures={'overlay': 'tfc:block/grass_snowy_side', 'particle': '#texture'}, parent='block/block', elements=[north_face, north_face_overlay])
    rm.block_model('grass_bottom', textures={'texture': '#texture', 'particle': '#texture'}, parent='block/block', elements=[north_face])

    # Grass (Peat, Normal + Clay) - Helper Functions
    def grass_multipart(model: str):
        return [
            {'model': model + '/bottom', 'x': 90},
            ({'snowy': False}, {'model': model + '/top', 'x': 270}),
            ({'snowy': True}, {'model': model + '/snowy_top', 'x': 270}),
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

    def grass_models(name: utils.ResourceIdentifier, texture: str):
        for variant in ('top', 'snowy_top', 'side', 'snowy_side', 'bottom'):
            rm.block_model((name, variant), {'texture': texture}, parent='tfc:block/grass_%s' % variant)

    # Peat Grass
    rm.blockstate_multipart('peat_grass', *grass_multipart('tfc:block/peat_grass')).with_block_loot('tfc:peat').with_tag('grass').with_lang(lang('Peat Grass'))
    grass_models('peat_grass', 'tfc:block/peat')

    # Grass Blocks
    for soil in SOIL_BLOCK_VARIANTS:
        for grass_var, dirt in (('grass', 'tfc:block/dirt/%s' % soil), ('clay_grass', 'tfc:block/clay/%s' % soil)):
            block = rm.blockstate_multipart((grass_var, soil), *grass_multipart('tfc:block/%s/%s' % (grass_var, soil)))
            if grass_var == 'grass':
                block.with_block_loot('tfc:dirt/%s' % soil)
            else:
                block.with_block_loot('1-3 minecraft:clay_ball')
            block.with_tag('grass')
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
        block.with_tag('farmland')
        block.with_lang(lang('%s farmland', soil))

    # Snow Piles
    block = rm.blockstate('snow_pile', variants=dict((('layers=%d' % i), {'model': 'minecraft:block/snow_height%d' % (i * 2) if i != 8 else 'minecraft:block/snow_block'}) for i in range(1, 1 + 8)))
    block.with_lang(lang('Snow Pile'))
    rm.item_model('snow_pile', parent='minecraft:block/snow_height2', no_textures=True)

    block = rm.blockstate('ice_pile', 'minecraft:block/ice').with_lang(lang('ice pile')).with_tag('minecraft:ice')
    block.with_block_loot({
        'name': 'minecraft:ice',
        'conditions': [loot_tables.silk_touch()]
    })
    rm.item_model('ice_pile', parent='minecraft:item/ice', no_textures=True)

    # Loot table for snow blocks and snow piles - override the vanilla one to only return one snowball per layer
    def snow_block_loot_table(block: str):
        rm.block_loot(block, loot_tables.pool(loot_tables.alternatives({
            'conditions': [loot_tables.silk_touch()],
            'name': 'minecraft:snow'
        }, 'minecraft:snowball'), conditions=({
            'condition': 'minecraft:entity_properties',
            'predicate': {},
            'entity': 'this'
        })))

    snow_block_loot_table('snow_pile')
    snow_block_loot_table('minecraft:snow')

    # Sea Ice
    block = rm.blockstate('sea_ice').with_block_model().with_item_model().with_lang(lang('sea ice')).with_tag('minecraft:ice')
    block.with_block_loot({
        'name': 'minecraft:ice',
        'conditions': [loot_tables.silk_touch()]
    })

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
                else:
                    item = rm.item_model(('stone', rock_item, rock), 'tfc:item/stone/%s' % rock_item, parent='item/handheld')
                item.with_lang(lang('stone %s', rock_item))

    # Rock Items
    for rock in ROCKS.keys():
        rm.item_model(('brick', rock), 'tfc:item/brick/%s' % rock).with_lang(lang('%s brick', rock))

    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type in metal_data.types or metal_item_data.type == 'all':
                texture = 'tfc:item/metal/%s/%s' % (metal_item, metal) if metal_item != 'shield' or metal in ('red_steel', 'blue_steel', 'wrought_iron') else 'tfc:item/metal/shield/%s_front' % metal
                if metal_item == 'fishing_rod':
                    item = item_model_property(rm, ('metal', metal_item, metal), [{'predicate': {'tfc:cast': 1}, 'model': 'minecraft:item/fishing_rod_cast'}], {'parent': 'minecraft:item/handheld_rod', 'textures': {'layer0': texture}})
                elif metal_item == 'shield':
                    item = rm.item(('metal', metal_item, metal))  # Shields have a custom model for inventory and blocking
                elif metal_item == 'javelin':
                    item = make_javelin(rm, 'metal/%s/%s' % (metal_item, metal), 'tfc:item/metal/javelin/%s' % metal)
                else:
                    item = rm.item_model(('metal', metal_item, metal), texture, parent=metal_item_data.parent_model)
                if metal_item == 'propick':
                    item.with_lang('%s Prospector\'s Pick' % lang(metal))  # .title() works weird w.r.t the possessive.
                elif metal_item == 'propick_head':
                    item.with_lang('%s Prospector\'s Pick Head' % lang(metal))
                else:
                    item.with_lang(lang('%s %s', metal, metal_item))

        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type in metal_data.types or metal_block_data.type == 'all':
                rm.block_tag('minecraft:mineable/pickaxe', 'tfc:metal/%s/%s' % (metal_block, metal))
                metal_dir = 'tfc:block/metal/%s/%s'
                metal_tex = metal_dir % ('full', metal)
                if metal_block == 'lamp':
                    rm.block_model('tfc:metal/lamp/%s_hanging_on' % metal, {'metal': metal_tex, 'chain': metal_dir % ('chain', metal), 'lamp': 'tfc:block/lamp'}, parent='tfc:block/lamp_hanging')
                    rm.block_model('tfc:metal/lamp/%s_hanging_off' % metal, {'metal': metal_tex, 'chain': metal_dir % ('chain', metal), 'lamp': 'tfc:block/lamp_off'}, parent='tfc:block/lamp_hanging')
                    rm.block_model('tfc:metal/lamp/%s_on' % metal, {'metal': metal_tex, 'lamp': 'tfc:block/lamp'}, parent='tfc:block/lamp')
                    rm.block_model('tfc:metal/lamp/%s_off' % metal, {'metal': metal_tex, 'lamp': 'tfc:block/lamp_off'}, parent='tfc:block/lamp')
                    rm.item_model(('metal', 'lamp', metal))
                    rm.blockstate(('metal', metal_block, metal), variants={
                        'hanging=false,lit=false': {'model': 'tfc:block/metal/lamp/%s_off' % metal},
                        'hanging=true,lit=false': {'model': 'tfc:block/metal/lamp/%s_hanging_off' % metal},
                        'hanging=false,lit=true': {'model': 'tfc:block/metal/lamp/%s_on' % metal},
                        'hanging=true,lit=true': {'model': 'tfc:block/metal/lamp/%s_hanging_on' % metal},
                    }).with_lang(lang('%s lamp', metal)).with_block_loot({
                        'name': 'tfc:metal/lamp/%s' % metal,
                        'functions': [{'function': 'tfc:copy_fluid'}]
                    }).with_tag('lamps')
                    rm.item_tag('lamps', 'tfc:metal/%s/%s' % (metal_block, metal))
                    rm.lang('block.tfc.metal.lamp.%s.filled' % metal, lang('filled %s lamp', metal))
                elif metal_block == 'chain':
                    rm.block_model(('metal', 'chain', metal), {'all': metal_dir % (metal_block, metal), 'particle': metal_dir % (metal_block, metal)}, parent='minecraft:block/chain')
                    rm.blockstate(('metal', 'chain', metal), variants={
                        'axis=x': {'model': 'tfc:block/metal/chain/%s' % metal, 'x': 90, 'y': 90},
                        'axis=y': {'model': 'tfc:block/metal/chain/%s' % metal},
                        'axis=z': {'model': 'tfc:block/metal/chain/%s' % metal, 'x': 90}
                    }).with_lang(lang('%s chain', metal)).with_block_loot('tfc:metal/chain/%s' % metal)
                    rm.item_model(('metal', 'chain', metal), 'tfc:item/metal/chain/%s' % metal)
                elif metal_block == 'trapdoor':
                    rm.block(('metal', metal_block, metal)).make_trapdoor(trapdoor_suffix='', texture=metal_dir % (metal_block, metal)).with_lang(lang('%s trapdoor', metal)).with_block_loot('tfc:metal/%s/%s' % (metal_block, metal))
                elif metal_block == 'anvil':
                    block = rm.blockstate(('metal', '%s' % metal_block, metal), variants={
                        'facing=north': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 90},
                        'facing=east': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 180},
                        'facing=south': {'model': 'tfc:block/metal/anvil/%s' % metal, 'y': 270},
                        'facing=west': {'model': 'tfc:block/metal/anvil/%s' % metal}
                    })
                    block.with_block_model({
                        'all': metal_tex,
                        'particle': metal_tex
                    }, parent=metal_block_data.parent_model)
                    block.with_block_loot('tfc:metal/%s/%s' % (metal_block, metal))
                    block.with_lang(lang('%s %s' % (metal, metal_block)))
                    block.with_item_model()
                else:
                    block = rm.blockstate(('metal', '%s' % metal_block, metal))
                    block.with_block_model({
                        'all': metal_tex,
                        'particle': metal_tex
                    }, parent=metal_block_data.parent_model)
                    block.with_block_loot('tfc:metal/%s/%s' % (metal_block, metal))
                    block.with_lang(lang('%s %s' % (metal, metal_block)))
                    block.with_item_model()

    # Misc Items
    for gem in GEMS:
        rm.item_model(('gem', gem)).with_lang(lang('cut %s', gem))
        rm.item_model(('powder', gem)).with_lang(lang('%s powder', gem))

    for powder in GENERIC_POWDERS:
        rm.item_model(('powder', powder)).with_lang(lang('%s Powder', powder))

    for powder in POWDERS:
        rm.item_model(('powder', powder)).with_lang(lang(powder))

    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))

    rm.lang('item.tfc.pan.empty', lang('Empty Pan'))

    stages = [
        {'predicate': {'tfc:stage': 0.1, 'tfc:ore': 0}, 'model': 'tfc:item/pan/native_copper/result'},
        {'predicate': {'tfc:stage': 0.1, 'tfc:ore': 1}, 'model': 'tfc:item/pan/native_silver/result'},
        {'predicate': {'tfc:stage': 0.1, 'tfc:ore': 2}, 'model': 'tfc:item/pan/native_gold/result'},
        {'predicate': {'tfc:stage': 0.1, 'tfc:ore': 3}, 'model': 'tfc:item/pan/cassiterite/result'}
    ]
    for metal_id, metal in enumerate(('copper', 'silver', 'gold', 'tin')):
        ore = 'native_' + metal if metal != 'tin' else 'cassiterite'
        rm.item_model(('pan', ore, 'result'), {'material': 'tfc:block/metal/full/' + metal}, parent='tfc:item/pan/result')
        for rock_id, rock in enumerate(ROCKS.keys()):
            rm.item_model(('pan', ore, rock + '_full'), {'material': 'tfc:block/rock/gravel/%s' % rock}, parent='tfc:item/pan/full')
            rm.item_model(('pan', ore, rock + '_half'), {'material': 'tfc:block/rock/gravel/%s' % rock}, parent='tfc:item/pan/half')
            stages.append({'predicate': {'tfc:rock': rock_id, 'tfc:stage': 0.4, 'tfc:ore': metal_id}, 'model': 'tfc:item/pan/%s/%s_half' % (ore, rock)})
            stages.append({'predicate': {'tfc:rock': rock_id, 'tfc:stage': 0.7, 'tfc:ore': metal_id}, 'model': 'tfc:item/pan/%s/%s_full' % (ore, rock)})
            block = rm.blockstate(('deposit', ore, rock)).with_lang(lang('%s %s Deposit', rock, ore)).with_item_model()
            block.with_block_model({
                'all': 'tfc:block/rock/gravel/%s' % rock,
                'particle': 'tfc:block/rock/gravel/%s' % rock,
                'overlay': 'tfc:block/deposit/%s' % ore
            }, parent='tfc:block/ore')
            rare = DEPOSIT_RARES[rock]
            block.with_block_loot(({
               'name': 'tfc:ore/small_%s' % ore,
               'conditions': ['tfc:is_panned', loot_tables.random_chance(0.5)],  # 50% chance
            }, {
               'name': 'tfc:rock/loose/%s' % rock,
               'conditions': ['tfc:is_panned', loot_tables.random_chance(0.5)],  # 25% chance
            }, {
               'name': 'tfc:gem/%s' % rare if rare in GEMS else 'tfc:ore/%s' % rare,
               'conditions': ['tfc:is_panned', loot_tables.random_chance(0.04)],  # 1% chance
            }, {
               'name': 'tfc:deposit/%s/%s' % (ore, rock),
               'conditions': [{'condition': 'minecraft:inverted', 'term': {'condition': 'tfc:is_panned'}}]
            }))
    item_model_property(rm, ('pan', 'filled'), stages, {'parent': 'tfc:item/pan/empty'}).with_lang(lang('Filled Pan'))
    item_model_property(rm, 'handstone', [{'predicate': {'tfc:damaged': 1.0}, 'model': 'tfc:item/handstone_damaged'}], {'parent': 'tfc:item/handstone_healthy'}).with_lang(lang('Handstone'))
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

# Small Ceramic Vessels (colored)
    for color in COLORS:
        rm.item_model(('ceramic', color + '_unfired_vessel')).with_lang(lang('%s Unfired Vessel', color))
        rm.item_model(('ceramic', color + '_glazed_vessel')).with_lang(lang('%s Glazed Vessel', color))

    # Molds
    for variant, data in METAL_ITEMS.items():
        if data.mold:
            rm.item_model(('ceramic', 'unfired_%s_mold' % variant), 'tfc:item/ceramic/unfired_%s' % variant).with_lang(lang('unfired %s mold', variant))
            contained_fluid(rm, ('ceramic', '%s_mold' % variant), 'tfc:item/ceramic/fired_mold/%s_empty' % variant, 'tfc:item/ceramic/fired_mold/%s_overlay' % variant).with_lang(lang('%s mold', variant))

    # Crops
    for crop, crop_data in CROPS.items():
        name = 'tfc:jute' if crop == 'jute' else 'tfc:food/%s' % crop
        if crop_data.type == 'default' or crop_data.type == 'spreading':
            block = rm.blockstate(('crop', crop), variants=dict(('age=%d' % i, {'model': 'tfc:block/crop/%s_age_%d' % (crop, i)}) for i in range(crop_data.stages)))
            block.with_lang(lang(crop))
            for i in range(crop_data.stages):
                rm.block_model(('crop', crop + '_age_%d' % i), textures={'crop': 'tfc:block/crop/%s_%d' % (crop, i)}, parent='block/crop')

            if crop_data.type == 'spreading':
                block.with_block_loot({'name': 'tfc:seeds/%s' % crop})
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

            block = rm.blockstate(('wild_crop', crop), model='tfc:block/wild_crop/%s' % crop).with_lang(lang('Wild %s', crop)).with_item_model().with_tag('can_be_snow_piled')
            block.with_block_model(textures={'crop': 'tfc:block/crop/%s_wild' % crop}, parent='tfc:block/wild_crop/crop')

            if crop_data.type == 'spreading':
                block.with_block_loot({'name': 'tfc:seeds/%s' % crop})
            else:
                block.with_block_loot({
                    'name': name,
                    'functions': loot_tables.set_count(1, 3)
                }, {
                    'name': 'tfc:seeds/%s' % crop
                })

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
                'name': 'tfc:seeds/%s' % crop
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
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=true]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:dead_crop/%s[mature=false]' % crop)
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

            block.with_block_loot({
                'name': name,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[age=%s,part=bottom]' % (crop, crop_data.stages - 1)),
                'functions': crop_yield(0, (6, 10))
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:crop/%s[part=bottom]' % crop)
            }, {
                'name': 'minecraft:stick',
                'conditions': loot_tables.block_state_property('tfc:crop/%s[part=bottom,stick=true]' % crop)
            })

            block = rm.blockstate(('dead_crop', crop), variants={
                'mature=false,stick=false': {'model': 'tfc:block/dead_crop/%s_young' % crop},
                'mature=false,stick=true,part=top': {'model': 'tfc:block/dead_crop/%s_top' % crop},
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
            }), {
                'name': 'tfc:seeds/%s' % crop
            })

        rm.item_model(('seeds', crop)).with_lang(lang('%s seeds', crop)).with_tag('seeds')
        if crop_data.type == 'double' or crop_data.type == 'double_stick':
            block = rm.blockstate(('wild_crop', crop), variants={
                'part=top': {'model': 'tfc:block/wild_crop/%s_top' % crop},
                'part=bottom': {'model': 'tfc:block/wild_crop/%s_bottom' % crop}
            })
            rm.item_model(('wild_crop', crop), parent='tfc:block/wild_crop/%s_bottom' % crop, no_textures=True)
            block.with_lang(lang('wild %s', crop))
            rm.block_model(('wild_crop', '%s_top' % crop), {'crop': 'tfc:block/crop/%s_wild_top' % crop}, parent='block/crop')
            rm.block_model(('wild_crop', '%s_bottom' % crop), {'crop': 'tfc:block/crop/%s_wild_bottom' % crop}, parent='tfc:block/wild_crop/crop')

            block.with_block_loot({
                'name': name,
                'conditions': loot_tables.block_state_property('tfc:wild_crop/%s[part=bottom]' % crop),
                'functions': loot_tables.set_count(1, 3)
            }, {
                'name': 'tfc:seeds/%s' % crop,
                'conditions': loot_tables.block_state_property('tfc:wild_crop/%s[part=bottom]' % crop)
            })

    rm.block_model(('crop', 'stick'), {'crop': 'tfc:block/crop/stick_top'}, parent='block/crop')

    # Plants
    for plant, plant_data in PLANTS.items():
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        p = 'tfc:plant/%s' % plant
        lower_only = loot_tables.block_state_property(p + '[part=lower]')
        if plant_data.type == 'short_grass':
            rm.block_loot(p, ({
                'name': p,
                'conditions': [loot_tables.match_tag('forge:shears')],
            }, {
                'name': 'tfc:straw',
                'conditions': [loot_tables.match_tag('tfc:sharp_tools')]
            }))
        elif plant_data.type == 'tall_grass':
            rm.block_loot(p, ({
                'name': p,
                'conditions': [loot_tables.match_tag('forge:shears'), lower_only],
            }, {
                'name': 'tfc:straw',
                'conditions': [loot_tables.match_tag('tfc:sharp_tools')]
            }))
        elif plant in SEAWEED:
            rm.block_loot(p, (
                {'name': 'tfc:groundcover/seaweed', 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.random_chance(0.3)]},
                {'name': p, 'conditions': [loot_tables.match_tag('forge:shears')]}
            ))
        elif plant_data.type in ('tall_plant', 'emergent', 'emergent_fresh', 'cactus'):
            if plant == 'cattail':
                rm.block_loot(p, (
                    {'name': 'tfc:food/cattail_root', 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.random_chance(0.3), lower_only]},
                    {'name': p, 'conditions': [loot_tables.match_tag('forge:shears'), lower_only]}
                ))
            elif plant == 'water_taro':
                rm.block_loot(p, (
                    {'name': 'tfc:food/taro_root', 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.random_chance(0.3), lower_only]},
                    {'name': p, 'conditions': [loot_tables.match_tag('forge:shears'), lower_only]}
                ))
            else:
                rm.block_loot(p, {'name': p, 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), lower_only]})
        else:
            rm.block_loot(p, {'name': p, 'conditions': [loot_tables.match_tag('tfc:sharp_tools')]})
    for plant in ('hanging_vines', 'jungle_vines', 'ivy', 'liana', 'tree_fern', 'arundo'):
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
    for plant in ('tree_fern', 'arundo', 'winged_kelp', 'leafy_kelp', 'giant_kelp_flower', 'dry_phragmite'):
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        rm.block_loot('tfc:plant/%s' % plant, 'tfc:plant/%s' % plant)

    rm.block('sea_pickle').with_lang(lang('sea pickle')).with_block_loot([{
        'name': 'tfc:sea_pickle',
        'conditions': loot_tables.block_state_property('tfc:sea_pickle[pickles=%d]' % i),
        'functions': [loot_tables.set_count(i)]
    } for i in (1, 2, 3, 4)])

    for plant in ('tree_fern', 'arundo', 'winged_kelp', 'leafy_kelp', 'giant_kelp', 'hanging_vines', 'liana', 'dry_phragmite'):
        rm.lang('block.tfc.plant.%s_plant' % plant, lang(plant))
    for plant in ('hanging_vines', 'ivy', 'jungle_vines', 'liana'):
        rm.block_loot('tfc:plant/%s' % plant, {'name': 'tfc:plant/%s' % plant, 'conditions': [loot_tables.match_tag('tfc:sharp_tools')]})

    for plant in ('duckweed', 'lotus', 'sargassum', 'water_lily'):
        rm.block_model('plant/%s' % plant, parent='tfc:block/plant/template_floating_tinted', textures={'pad': 'tfc:block/plant/%s/%s' % (plant, plant)})

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
        rm.item_model(('food', '%s_soup' % nutrient)).with_lang(lang('%s soup', funny_soup_name)).with_tag('soups')
        rm.item_model(('food', '%s_salad' % nutrient)).with_lang(lang('%s salad', funny_salad_name)).with_tag('salads')

    for grain in GRAINS:
        rm.item_model(('food', '%s_bread_sandwich' % grain)).with_lang(lang('%s Bread Sandwich', grain)).with_tag('sandwiches').with_tag('foods')

    # Berry Bushes
    lifecycle_to_model = {'healthy': '', 'dormant': 'dry_', 'fruiting': 'fruiting_', 'flowering': 'flowering_'}
    lifecycles = ('healthy', 'dormant', 'fruiting', 'flowering')

    for berry, data in BERRIES.items():
        rm.blockstate('plant/%s_bush' % berry, variants=dict(
            (
                'lifecycle=%s,stage=%d' % (lifecycle, stage),
                {'model': 'tfc:block/plant/%s%s_bush_%d' % (lifecycle_to_model[lifecycle], berry, stage)}
            ) for lifecycle, stage in itertools.product(lifecycles, range(0, 3))
        )).with_lang(lang('%s Bush', berry)).with_tag('berry_bushes')

        if data.type == 'stationary' or data.type == 'waterlogged':
            rm.item_model('plant/%s_bush' % berry, parent='tfc:block/plant/%s_bush_2' % berry, no_textures=True)
            rm.block_loot('plant/%s_bush' % berry, {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag('tfc:sharp_tools')]})
            for lifecycle, stage in itertools.product(lifecycle_to_model.values(), range(0, 3)):
                rm.block_model('plant/%s%s_bush_%d' % (lifecycle, berry, stage), parent='tfc:block/plant/stationary_bush_%d' % stage, textures={'bush': 'tfc:block/berry_bush/' + lifecycle + '%s_bush' % berry})
            rm.block_tag('fox_raidable', 'tfc:plant/%s_bush' % berry)
        else:
            rm.item_model('plant/%s_bush' % berry, 'tfc:block/berry_bush/%s_cane' % berry)
            rm.block_loot('plant/%s_bush' % berry, (
                {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.block_state_property('tfc:plant/%s_bush[stage=2]' % berry)]},
                {'name': 'tfc:plant/%s_bush' % berry, 'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.random_chance(0.5)]}
            ), 'minecraft:stick')

    rm.blockstate('plant/dead_berry_bush', variants={
        'stage=0': {'model': 'tfc:block/plant/dead_berry_bush_0'},
        'stage=1': {'model': 'tfc:block/plant/dead_berry_bush_1'},
        'stage=2': {'model': 'tfc:block/plant/dead_berry_bush_2'}
    }).with_lang(lang('Dead Bush')).with_tag('any_spreading_bush').with_block_loot('minecraft:stick')
    rm.blockstate('plant/dead_cane', variants={
        **four_rotations('tfc:block/plant/dead_berry_bush_side_0', (90, None, 180, 270), suffix=',stage=0'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_1', (90, None, 180, 270), suffix=',stage=1'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_2', (90, None, 180, 270), suffix=',stage=2')
    }).with_lang(lang('Dead Cane')).with_tag('any_spreading_bush').with_block_loot('minecraft:stick')

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
        rm.block_tag('spreading_bush', 'plant/%s_bush' % berry, 'plant/%s_bush_cane' % berry)

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
                ).with_tag('fruit_tree_branch').with_item_model().with_lang(lang('%s Branch', fruit))
                if prefix == '':
                    block.with_block_loot({
                        'name': 'tfc:plant/%s_sapling' % fruit,
                        'conditions': [{
                            'condition': 'minecraft:alternative',
                            'terms': [loot_tables.block_state_property('tfc:plant/%s_branch[up=true,%s=true]' % (fruit, direction)) for direction in ('west', 'east', 'north', 'south')]
                        },
                            loot_tables.match_tag('tfc:axes')]
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
            }).with_item_model().with_tag('minecraft:leaves').with_tag('fruit_tree_leaves').with_lang(lang('%s Leaves', fruit)).with_block_loot({
                'name': 'tfc:plant/%s_leaves' % fruit,
                'conditions': [loot_tables.block_state_property('tfc:plant/%s_leaves[lifecycle=fruiting]' % fruit)]
            })
            for life in ('', '_fruiting', '_flowering', '_dry'):
                rm.block_model('tfc:plant/%s%s_leaves' % (fruit, life), parent='block/leaves', textures={'all': 'tfc:block/fruit_tree/%s%s_leaves' % (fruit, life)})

            rm.blockstate(('plant', '%s_sapling' % fruit), variants={'saplings=%d' % i: {'model': 'tfc:block/plant/%s_sapling_%d' % (fruit, i)} for i in range(1, 4 + 1)}).with_lang(lang('%s Sapling', fruit)).with_tag('fruit_tree_sapling')
            rm.block_loot(('plant', '%s_sapling' % fruit), {
                'name': 'tfc:plant/%s_sapling' % fruit,
                'functions': [list({**loot_tables.set_count(i), 'conditions': [loot_tables.block_state_property('tfc:plant/%s_sapling[saplings=%s]' % (fruit, i))]} for i in range(1, 5)), loot_tables.explosion_decay()]
            })
            for stage in range(2, 4 + 1):
                rm.block_model(('plant', '%s_sapling_%d' % (fruit, stage)), parent='tfc:block/plant/cross_%s' % stage, textures={'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit})
            rm.block_model(('plant', '%s_sapling_1' % fruit), {'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit}, 'block/cross')
        else:
            def banana_suffix(state: str, i: int) -> str:
                if i == 2 and (state == 'fruiting' or state == 'flowering'):
                    return '%d_%s' % (i, state)
                return str(i)

            rm.blockstate('plant/banana_plant', variants=dict({'lifecycle=%s,stage=%d' % (state, i): {'model': 'tfc:block/plant/banana_trunk_%s' % banana_suffix(state, i)} for state, i in itertools.product(lifecycles, range(0, 3))})).with_lang(lang('Banana Plant')).with_tag('fruit_tree_branch').with_block_loot({
                'name': 'tfc:plant/banana_sapling',
                'functions': [{**loot_tables.set_count(1, 2)}],
                'conditions': [loot_tables.block_state_property('tfc:plant/banana_plant[stage=2]')]
            })
            rm.blockstate(('plant', 'dead_banana_plant'), variants=dict({'stage=%d' % i: {'model': 'tfc:block/plant/banana_trunk_%d_dead' % i} for i in range(0, 3)})).with_lang(lang('Dead Banana Plant')).with_tag('fruit_tree_branch').with_block_loot({
                'name': 'minecraft:stick'
            }, {
                'name': 'tfc:plant/banana_sapling',
                'functions': [{**loot_tables.set_count(1, 2)}],
                'conditions': [loot_tables.block_state_property('tfc:plant/banana_plant[stage=2]')]
            })
            rm.block_model(('plant', 'banana_trunk_0_dead'), textures={"particle": "tfc:block/fruit_tree/banana_branch", "0": "tfc:block/fruit_tree/banana_branch"}, parent='tfc:block/plant/banana_trunk_0')
            rm.block_model(('plant', 'banana_trunk_1_dead'), textures={"particle": "tfc:block/fruit_tree/banana_branch", "0": "tfc:block/fruit_tree/banana_branch"}, parent='tfc:block/plant/banana_trunk_1')
            rm.block_model(('plant', 'banana_trunk_2_dead'), textures={"particle": "tfc:block/fruit_tree/banana_leaves_dead", "1_0": "tfc:block/fruit_tree/banana_leaves_dead"}, parent='tfc:block/plant/banana_trunk_2')
            rm.block_model(('plant', 'banana_sapling'), textures={'cross': 'tfc:block/fruit_tree/banana_sapling'}, parent='block/cross')
            rm.blockstate(('plant', 'banana_sapling'), model='tfc:block/plant/banana_sapling').with_lang(lang('Banana Sapling')).with_tag('fruit_tree_sapling').with_block_loot('tfc:plant/banana_sapling')

        rm.item_model(('plant', '%s_sapling' % fruit), 'tfc:block/fruit_tree/%s_sapling' % fruit)
        rm.item_model(('food', fruit), 'tfc:item/food/%s' % fruit).with_lang(lang(fruit))

    # Wood Blocks
    for wood in WOODS.keys():
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
                    {  # wood blocks will only drop themselves if non-natural
                        'name': 'tfc:wood/%s/%s' % (variant, wood),
                        'conditions': loot_tables.block_state_property('tfc:wood/%s/%s[natural=false]' % (variant, wood))
                    },
                    'tfc:wood/%s/%s' % (variant.replace('wood', 'log'), wood)
                ))
            else:
                block.with_block_loot((
                    stick_with_hammer,
                    'tfc:wood/%s/%s' % (variant, wood)  # logs drop themselves always
                ))

            rm.item_model(('wood', variant, wood), 'tfc:item/wood/%s/%s' % (variant, wood))

            end = 'tfc:block/wood/%s/%s' % (variant.replace('log', 'log_top').replace('wood', 'log'), wood)
            side = 'tfc:block/wood/%s/%s' % (variant.replace('wood', 'log'), wood)
            block.with_block_model({'end': end, 'side': side}, parent='block/cube_column')
            if 'stripped' in variant:
                block.with_lang(lang(variant.replace('_', ' ' + wood + ' ')))
            else:
                block.with_lang(lang('%s %s', wood, variant))
        for item_type in ('lumber', 'sign', 'chest_minecart', 'boat'):
            rm.item_model(('wood', item_type, wood)).with_lang(lang('%s %s', wood, item_type))
        rm.item_tag('minecraft:signs', 'tfc:wood/sign/' + wood)
        rm.item_tag('tfc:minecarts', 'tfc:wood/chest_minecart/' + wood)

        # Groundcover
        for variant in ('twig', 'fallen_leaves'):
            block = rm.blockstate('wood/%s/%s' % (variant, wood), variants={"": four_ways('tfc:block/wood/%s/%s' % (variant, wood))}, use_default_model=False)
            block.with_lang(lang('%s %s', wood, variant))

            if variant == 'twig':
                block.with_block_model({'side': 'tfc:block/wood/log/%s' % wood, 'top': 'tfc:block/wood/log_top/%s' % wood}, parent='tfc:block/groundcover/%s' % variant)
                rm.item_model('wood/%s/%s' % (variant, wood), 'tfc:item/wood/twig/%s' % wood)
                block.with_block_loot('tfc:wood/twig/%s' % wood)
            elif variant == 'fallen_leaves':
                block.with_block_model('tfc:block/wood/leaves/%s' % wood, parent='tfc:block/groundcover/%s' % variant)
                rm.item_model('wood/%s/%s' % (variant, wood), 'tfc:item/groundcover/fallen_leaves')
                block.with_block_loot('tfc:wood/%s/%s' % (variant, wood))
            else:
                block.with_item_model()

            block.with_tag('can_be_snow_piled')

        # Leaves
        block = rm.blockstate(('wood', 'leaves', wood), model='tfc:block/wood/leaves/%s' % wood)
        block.with_block_model('tfc:block/wood/leaves/%s' % wood, parent='block/leaves')
        block.with_item_model()
        block.with_tag('minecraft:leaves')
        block.with_block_loot(({
            'name': 'tfc:wood/leaves/%s' % wood,
            'conditions': [loot_tables.or_condition(loot_tables.match_tag('forge:shears'), loot_tables.silk_touch())]
        }, {
            'name': 'tfc:wood/sapling/%s' % wood,
            'conditions': ['minecraft:survives_explosion', loot_tables.random_chance(TREE_SAPLING_DROP_CHANCES[wood])]
        }), ({
            'name': 'minecraft:stick',
            'conditions': [loot_tables.match_tag('tfc:sharp_tools'), loot_tables.random_chance(0.2)],
            'functions': [loot_tables.set_count(1, 2)]
        }, {
            'name': 'minecraft:stick',
            'conditions': [loot_tables.random_chance(0.05)],
            'functions': [loot_tables.set_count(1, 2)]
        }))

        # Sapling
        block = rm.blockstate(('wood', 'sapling', wood), 'tfc:block/wood/sapling/%s' % wood)
        block.with_block_model({'cross': 'tfc:block/wood/sapling/%s' % wood}, 'block/cross')
        block.with_block_loot('tfc:wood/sapling/%s' % wood)
        rm.item_model(('wood', 'sapling', wood), 'tfc:block/wood/sapling/%s' % wood)

        # Planks and variant blocks
        block = rm.block(('wood', 'planks', wood))
        block.with_blockstate()
        block.with_block_model()
        block.with_item_model()
        block.with_block_loot('tfc:wood/planks/%s' % wood)
        block.with_lang(lang('%s planks', wood))
        block.make_slab()
        block.make_stairs()
        block.make_button()
        block.make_door()
        block.make_pressure_plate()
        block.make_trapdoor()
        block.make_fence()
        block.make_fence_gate()

        for block_type in ('button', 'fence', 'fence_gate', 'pressure_plate', 'stairs', 'trapdoor'):
            rm.block_loot('wood/planks/%s_%s' % (wood, block_type), 'tfc:wood/planks/%s_%s' % (wood, block_type))
        rm.block_loot('wood/planks/%s_bookshelf' % wood, loot_tables.alternatives({
            'name': 'tfc:wood/planks/%s_bookshelf' % wood,
            'conditions': [loot_tables.silk_touch()]
        }, '3 minecraft:book'))
        slab_loot(rm, 'tfc:wood/planks/%s_slab' % wood)
        rm.block_tag('minecraft:slabs', 'tfc:wood/planks/%s_slab' % wood)

        # Tool Rack
        rack_namespace = 'tfc:wood/planks/%s_tool_rack' % wood
        block = rm.blockstate(rack_namespace, model='tfc:block/wood/planks/%s_tool_rack' % wood, variants=four_rotations('tfc:block/wood/planks/%s_tool_rack' % wood, (270, 180, None, 90)))
        block.with_block_model(textures={'texture': 'tfc:block/wood/planks/%s' % wood, 'particle': 'tfc:block/wood/planks/%s' % wood}, parent='tfc:block/tool_rack')
        block.with_lang(lang('%s Tool Rack', wood)).with_block_loot(rack_namespace).with_item_model()

        # Loom
        block = rm.blockstate('tfc:wood/planks/%s_loom' % wood, model='tfc:block/wood/planks/%s_loom' % wood, variants=four_rotations('tfc:block/wood/planks/%s_loom' % wood, (270, 180, None, 90)))
        block.with_block_model(textures={'texture': 'tfc:block/wood/planks/%s' % wood, 'particle': 'tfc:block/wood/planks/%s' % wood}, parent='tfc:block/loom')
        block.with_item_model().with_lang(lang('%s loom', wood)).with_block_loot('tfc:wood/planks/%s_loom' % wood).with_tag('minecraft:mineable/axe')

        # Bookshelf
        block = rm.blockstate('tfc:wood/planks/%s_bookshelf' % wood).with_item_model().with_lang(lang('%s bookshelf', wood))
        block.with_block_model({'end': 'tfc:block/wood/planks/%s' % wood, 'side': 'tfc:block/wood/planks/%s_bookshelf' % wood}, parent='minecraft:block/bookshelf')

        # Workbench
        rm.blockstate(('wood', 'planks', '%s_workbench' % wood)).with_block_model(parent='minecraft:block/cube', textures={
            'particle': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'north': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'south': 'tfc:block/wood/planks/%s_workbench_side' % wood,
            'east': 'tfc:block/wood/planks/%s_workbench_side' % wood,
            'west': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'up': 'tfc:block/wood/planks/%s_workbench_top' % wood,
            'down': 'tfc:block/wood/planks/%s' % wood
        }).with_item_model().with_lang(lang('%s Workbench', wood)).with_tag('tfc:workbenches').with_block_loot('tfc:wood/planks/%s_workbench' % wood)

        # Doors
        rm.item_model('tfc:wood/planks/%s_door' % wood, 'tfc:item/wood/planks/%s_door' % wood)
        rm.block_loot('wood/planks/%s_door' % wood, {'name': 'tfc:wood/planks/%s_door' % wood, 'conditions': [loot_tables.block_state_property('tfc:wood/planks/%s_door[half=lower]' % wood)]})

        # Log Fences
        log_fence_namespace = 'tfc:wood/planks/' + wood + '_log_fence'
        rm.blockstate_multipart(log_fence_namespace, *block_states.fence_multipart('tfc:block/wood/planks/' + wood + '_log_fence_post', 'tfc:block/wood/planks/' + wood + '_log_fence_side'))
        rm.block_model(log_fence_namespace + '_post', textures={'texture': 'tfc:block/wood/log/' + wood}, parent='block/fence_post')
        rm.block_model(log_fence_namespace + '_side', textures={'texture': 'tfc:block/wood/planks/' + wood}, parent='block/fence_side')
        rm.block_model(log_fence_namespace + '_inventory', textures={'log': 'tfc:block/wood/log/' + wood, 'planks': 'tfc:block/wood/planks/' + wood}, parent='tfc:block/log_fence_inventory')
        rm.item_model('tfc:wood/planks/' + wood + '_log_fence', parent='tfc:block/wood/planks/' + wood + '_log_fence_inventory', no_textures=True)
        rm.block_loot(log_fence_namespace, log_fence_namespace)

        texture = 'tfc:block/wood/sheet/%s' % wood
        connection = 'tfc:block/wood/support/%s_connection' % wood
        rm.blockstate_multipart(('wood', 'vertical_support', wood),
            {'model': 'tfc:block/wood/support/%s_vertical' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ).with_tag('tfc:support_beam').with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)
        rm.blockstate_multipart(('wood', 'horizontal_support', wood),
            {'model': 'tfc:block/wood/support/%s_horizontal' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ).with_tag('tfc:support_beam').with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)

        rm.block_model('tfc:wood/support/%s_inventory' % wood, textures={'texture': texture}, parent='tfc:block/wood/support/inventory')
        rm.block_model('tfc:wood/support/%s_vertical' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/vertical')
        rm.block_model('tfc:wood/support/%s_connection' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/connection')
        rm.block_model('tfc:wood/support/%s_horizontal' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/horizontal')
        rm.item_model(('wood', 'support', wood), no_textures=True, parent='tfc:block/wood/support/%s_inventory' % wood).with_lang(lang('%s Support', wood))

        for chest in ('chest', 'trapped_chest'):
            rm.blockstate(('wood', chest, wood), model='tfc:block/wood/%s/%s' % (chest, wood)).with_lang(lang('%s %s', wood, chest)).with_tag('minecraft:features_cannot_replace').with_tag('minecraft:lava_pool_stone_cannot_replace')
            rm.block_model(('wood', chest, wood), textures={'particle': 'tfc:block/wood/planks/%s' % wood}, parent=None)
            rm.item_model(('wood', chest, wood), {'particle': 'tfc:block/wood/planks/%s' % wood}, parent='minecraft:item/chest')
            rm.block_loot(('wood', chest, wood), {'name': 'tfc:wood/%s/%s'%(chest,wood)})

        rm.block_model('wood/sluice/%s_upper' % wood, textures={'texture': 'tfc:block/wood/sheet/%s' % wood}, parent='tfc:block/sluice_upper')
        rm.block_model('wood/sluice/%s_lower' % wood, textures={'texture': 'tfc:block/wood/sheet/%s' % wood}, parent='tfc:block/sluice_lower')
        block = rm.blockstate(('wood', 'sluice', wood), variants={**four_rotations('tfc:block/wood/sluice/%s_upper' % wood, (90, 0, 180, 270), suffix=',upper=true'), **four_rotations('tfc:block/wood/sluice/%s_lower' % wood, (90, 0, 180, 270), suffix=',upper=false')}).with_lang(lang('%s sluice', wood))
        block.with_block_loot({'name': 'tfc:wood/sluice/%s' % wood, 'conditions': [loot_tables.block_state_property('tfc:wood/sluice/%s[upper=true]' % wood)]})
        rm.item_model(('wood', 'sluice', wood), parent='tfc:block/wood/sluice/%s_lower' % wood, no_textures=True)

        rm.blockstate(('wood', 'planks', '%s_sign' % wood), model='tfc:block/wood/planks/%s_sign' % wood).with_lang(lang('%s Sign', wood)).with_block_model({'particle': 'tfc:block/wood/planks/%s' % wood}, parent=None).with_block_loot('tfc:wood/sign/%s' % wood).with_tag('minecraft:standing_sings')
        rm.blockstate(('wood', 'planks', '%s_wall_sign' % wood), model='tfc:block/wood/planks/%s_sign' % wood).with_lang(lang('%s Sign', wood)).with_lang(lang('%s Sign', wood)).with_tag('minecraft:wall_signs')

        # Barrels
        texture = 'tfc:block/wood/planks/%s' % wood
        textures = {'particle': texture, 'planks': texture, 'sheet': 'tfc:block/wood/sheet/%s' % wood, 'hoop': 'tfc:block/barrel_hoop'}
        block = rm.blockstate(('wood', 'barrel', wood), variants={
            'sealed=true': {'model': 'tfc:block/wood/barrel_sealed/%s' % wood},
            'sealed=false': {'model': 'tfc:block/wood/barrel/%s' % wood}
        })
        item_model_property(rm, ('wood', 'barrel', wood), [{'predicate': {'tfc:sealed': 1.0}, 'model': 'tfc:block/wood/barrel_sealed/%s' % wood}], {'parent': 'tfc:block/wood/barrel/%s' % wood})
        block.with_block_model(textures, 'tfc:block/barrel')
        rm.block_model(('wood', 'barrel_sealed', wood), textures, 'tfc:block/barrel_sealed')
        block.with_lang(lang('%s barrel', wood))
        block.with_tag('tfc:barrels').with_tag('minecraft:mineable/axe')
        block.with_block_loot(({
            'name': 'tfc:wood/barrel/%s' % wood,
            'functions': [loot_tables.copy_block_entity_name(), loot_tables.copy_block_entity_nbt()],
            'conditions': [loot_tables.block_state_property('tfc:wood/barrel/%s[sealed=true]' % wood)]
        }, 'tfc:wood/barrel/%s' % wood))

        # Lecterns
        block = rm.blockstate('tfc:wood/lectern/%s' % wood, variants=four_rotations('tfc:block/wood/lectern/%s' % wood, (90, None, 180, 270)))
        block.with_block_model(textures={'bottom': 'tfc:block/wood/planks/%s' % wood, 'base': 'tfc:block/wood/lectern/%s/base' % wood, 'front': 'tfc:block/wood/lectern/%s/front' % wood, 'sides': 'tfc:block/wood/lectern/%s/sides' % wood, 'top': 'tfc:block/wood/lectern/%s/top' % wood, 'particle': 'tfc:block/wood/lectern/%s/sides' % wood}, parent='minecraft:block/lectern')
        block.with_item_model().with_lang(lang("%s lectern" % wood)).with_block_loot('tfc:wood/lectern/%s' % wood).with_tag('minecraft:mineable/axe')
        # Scribing Table
        block = rm.blockstate('tfc:wood/scribing_table/%s' % wood, variants=four_rotations('tfc:block/wood/scribing_table/%s' % wood, (90, None, 180, 270)))
        block.with_block_model(textures={'top': 'tfc:block/wood/scribing_table/%s' % wood, 'leg': 'tfc:block/wood/log/%s' % wood, 'side' : 'tfc:block/wood/planks/%s' % wood, 'misc': 'tfc:block/wood/scribing_table/scribing_paraphernalia', 'particle': 'tfc:block/wood/planks/%s' % wood}, parent='tfc:block/scribing_table')
        block.with_item_model().with_lang(lang("%s scribing table" % wood)).with_block_loot('tfc:wood/scribing_table/%s' % wood).with_tag('minecraft:mineable/axe')

        # Candles
        for color in [None, *COLORS]:
            namespace = 'tfc:candle' + ('/'+color if color else '')
            candle = '%s_candle' % color if color else 'candle'
            block = rm.blockstate(namespace, variants={
                "candles=1,lit=false": {"model": "minecraft:block/%s_one_candle" % candle},
                "candles=1,lit=true": {"model": "minecraft:block/%s_one_candle_lit" % candle},
                "candles=2,lit=false": {"model": "minecraft:block/%s_two_candles" % candle},
                "candles=2,lit=true": {"model": "minecraft:block/%s_two_candles_lit" % candle},
                "candles=3,lit=false": {"model": "minecraft:block/%s_three_candles" % candle},
                "candles=3,lit=true": {"model": "minecraft:block/%s_three_candles_lit" % candle},
                "candles=4,lit=false": {"model": "minecraft:block/%s_four_candles" % candle},
                "candles=4,lit=true": {"model": "minecraft:block/%s_four_candles_lit" % candle}
            })
            block.with_lang(lang('%s candle' % color if color else 'candle'))
            block.with_block_loot(*[{'name': namespace, 'functions': [loot_tables.set_count(i)], 'conditions': [loot_tables.block_state_property('%s[candles=%s]' % (namespace, i))]} for i in range(1,5)])
            rm.item_model(namespace, parent='minecraft:item/%s' % candle, no_textures=True)
            if color: rm.item_tag('tfc:colored_candles', namespace)

        # Lang
        for variant in ('door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs'):
            rm.lang('block.tfc.wood.planks.' + wood + '_' + variant, lang('%s %s', wood, variant))
        for variant in ('sapling', 'leaves'):
            rm.lang('block.tfc.wood.' + variant + '.' + wood, lang('%s %s', wood, variant))

    rm.blockstate('light', variants={'level=%s' % i: {'model': 'minecraft:block/light_%s' % i if i >= 10 else 'minecraft:block/light_0%s' % i} for i in range(0, 15 + 1)}).with_lang(lang('Light'))
    rm.item_model('light', no_textures=True, parent='minecraft:item/light')

    # Entity Stuff
    for creature in SPAWN_EGG_ENTITIES:
        rm.item_model('spawn_egg/%s' % creature, parent='minecraft:item/template_spawn_egg', no_textures=True).with_lang(lang('%s Spawn Egg', creature))
    for creature in BUCKETABLE_FISH:
        rm.item_model('bucket/%s' % creature).with_lang(lang('%s Bucket', creature))

    # Fluids

    water_based_fluid(rm, 'salt_water')
    water_based_fluid(rm, 'spring_water')

    cauldron(rm, 'salt water', 'salt_water')
    cauldron(rm, 'spring water', 'spring_water')

    for fluid in SIMPLE_FLUIDS:
        water_based_fluid(rm, fluid)
        cauldron(rm, fluid, fluid)
    for fluid in ALCOHOLS:
        water_based_fluid(rm, fluid)
        cauldron(rm, fluid, fluid)
    for color in COLORS:
        water_based_fluid(rm, color + '_dye')
        cauldron(rm, color + ' dye', color + '_dye')

    # River water, since it doesn't have a bucket
    rm.blockstate(('fluid', 'river_water')).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None).with_lang(lang('water'))
    rm.fluid_tag('minecraft:water', 'tfc:river_water')  # Need to use water fluid tag for behavior
    rm.fluid_tag('mixable', 'tfc:river_water')

    # Mixable tags for vanilla water
    rm.fluid_tag('mixable', '#minecraft:water')

    for metal in METALS.keys():
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None).with_lang(lang('Molten %s', metal))
        rm.lang('fluid.tfc.metal.%s' % metal, lang('Molten %s', metal))
        rm.fluid_tag(metal, 'tfc:metal/%s' % metal, 'tfc:metal/flowing_%s' % metal)
        rm.fluid_tag('molten_metals', 'tfc:metal/%s' % metal)

        # todo: 1.19 rename to forge:fluid_container due to deprecation
        item = rm.custom_item_model(('bucket', 'metal', metal), 'forge:bucket', {
            'parent': 'forge:item/bucket',
            'fluid': 'tfc:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))
        cauldron(rm, 'molten %s' % metal, 'metal/%s' % metal, False)

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

    rm.blockstate('bellows', model='tfc:block/bellows', variants=four_rotations('tfc:block/bellows', (270, 180, None, 90))).with_lang(lang('Bellows')).with_block_loot('tfc:bellows').with_tag('minecraft:mineable/axe')

    rm.blockstate('ingot_pile', 'tfc:block/empty').with_lang(lang('ingot pile'))
    rm.blockstate('sheet_pile', 'tfc:block/empty').with_lang(lang('sheet pile'))

    for be in BLOCK_ENTITIES:
        rm.lang('tfc.block_entity.%s' % be, lang(be))


def item_model_property(rm: ResourceManager, name_parts: utils.ResourceIdentifier, overrides: utils.Json, data: Dict[str, Any]) -> ItemContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'assets', res.domain, 'models', 'item', res.path), {
        **data,
        'overrides': overrides
    })
    return ItemContext(rm, res)


def water_based_fluid(rm: ResourceManager, name: str):
    rm.blockstate(('fluid', name)).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None).with_lang(lang(name))
    rm.fluid_tag(name, 'tfc:%s' % name, 'tfc:flowing_%s' % name)
    rm.fluid_tag('minecraft:water', 'tfc:%s' % name, 'tfc:flowing_%s' % name)  # Need to use water fluid tag for behavior
    rm.fluid_tag('mixable', 'tfc:%s' % name, 'tfc:flowing_%s' % name)

    # todo: 1.19 rename to forge:fluid_container due to deprecation
    item = rm.custom_item_model(('bucket', name), 'forge:bucket', {
        'parent': 'forge:item/bucket',
        'fluid': 'tfc:%s' % name
    })
    item.with_lang(lang('%s bucket', name))
    rm.lang('fluid.tfc.%s' % name, lang(name))


def cauldron(rm: ResourceManager, name: str, fluid: str, water: bool = True):
    block = rm.blockstate(('cauldron', fluid))
    block.with_block_model({
        'content': 'block/water_still' if water else 'tfc:block/molten_still',
        'inside': 'block/cauldron_inner',
        'particle': 'block/cauldron_side',
        'top': 'block/cauldron_top',
        'bottom': 'block/cauldron_bottom',
        'side': 'block/cauldron_side'
    }, parent='minecraft:block/template_cauldron_full')
    block.with_block_loot('minecraft:cauldron')
    block.with_lang(lang('%s cauldron', name))
    block.with_tag('minecraft:mineable/pickaxe')


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

    if not dead:
        # Tag contents are used for selecting a random coral to place by features
        rm.block_tag('wall_corals', 'coral/%s_coral_wall_fan' % color)
        rm.block_tag('corals', 'coral/%s_coral' % color, 'coral/%s_coral_fan' % color)


def four_ways(model: str) -> List[Dict[str, Any]]:
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
    rm.item_model(name_parts + '_throwing', {'particle': texture}, parent='minecraft:item/trident_throwing')
    rm.item_model(name_parts + '_in_hand', {'particle': texture}, parent='minecraft:item/trident_in_hand')
    rm.item_model(name_parts + '_gui', texture)
    model = rm.domain + ':item/' + name_parts
    # todo: 1.19 rename to forge:separate_transforms due to deprecation
    return rm.custom_item_model(name_parts, 'forge:separate-perspective', {
        'gui_light': 'front',
        'overrides': [{'predicate': {'tfc:throwing': 1}, 'model': model + '_throwing'}],
        'base': {'parent': model + '_in_hand'},
        'perspectives': {
            'none': {'parent': model + '_gui'},
            'fixed': {'parent': model + '_gui'},
            'ground': {'parent': model + '_gui'},
            'gui': {'parent': model + '_gui'}
        }
    })


def contained_fluid(rm: ResourceManager, name_parts: utils.ResourceIdentifier, base: str, overlay: str) -> 'ItemContext':
    return rm.custom_item_model(name_parts, 'tfc:contained_fluid', {
        'parent': 'forge:item/default',
        'textures': {
            'base': base,
            'fluid': overlay
        }
    })


def slab_loot(rm: ResourceManager, loot: str):
    return rm.block_loot(loot, {
        'name': loot,
        'functions': [{
            'function': 'minecraft:set_count',
            'conditions': [loot_tables.block_state_property(loot + '[type=double]')],
            'count': 2,
            'add': False
        }]
    })

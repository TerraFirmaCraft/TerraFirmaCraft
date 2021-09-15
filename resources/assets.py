#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

import itertools
from typing import Any

import mcresources.block_states as block_states
import mcresources.loot_tables as loot_tables
import mcresources.utils as utils
from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # Rock block variants
    for rock, rock_data in ROCKS.items():
        for block_type in ROCK_BLOCK_TYPES:
            if block_type == 'spike':
                # Spikes have special block states
                block = rm.blockstate(('rock', block_type, rock), variants=dict(('part=%s' % part, {'model': 'tfc:block/rock/%s/%s_%s' % (block_type, rock, part)}) for part in ROCK_SPIKE_PARTS))
                block.with_lang(lang('%s spike', rock))
                block.with_block_loot(counted_item('tfc:rock/loose/%s' % rock, 1, 2))
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

                block.with_lang(lang('loose %s', rock)).with_tag('can_be_snow_piled').with_block_loot(one_pool({
                    'name': 'tfc:rock/loose/%s' % rock,
                    'functions': [
                        {**loot_tables.set_count(2), 'conditions': [block_state_property('tfc:rock/loose/%s' % rock, {'count': '2'})]},
                        {**loot_tables.set_count(3), 'conditions': [block_state_property('tfc:rock/loose/%s' % rock, {'count': '3'})]},
                        explosion_decay()
                    ]
                }))

                # Model for the item
                rm.item_model(('rock', 'loose', rock), 'tfc:item/loose_rock/%s' % rock)

            elif block_type == 'pressure_plate':
                block = rm.block(('rock', 'pressure_plate', rock))
                block.make_pressure_plate(pressure_plate_suffix='', texture='tfc:block/rock/raw/%s' % rock)
                block.with_lang(lang('%s pressure plate', rock))
            elif block_type == 'button':
                block = rm.block(('rock', 'button', rock))
                block.make_button(button_suffix='', texture='tfc:block/rock/raw/%s' % rock)
                block.with_lang(lang('%s button', rock))
            else:
                block = rm.blockstate(('rock', block_type, rock))
                if block_type == 'hardened':
                    block.with_block_model('tfc:block/rock/raw/%s' % rock)  # Hardened uses the raw model
                else:
                    block.with_block_model('tfc:block/rock/%s/%s' % (block_type, rock))
                block.with_item_model()

                if block_type in CUTTABLE_ROCKS:
                    # Stairs
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_stairs()
                    rm.block_loot('tfc:rock/' + block_type + '/' + rock + '_stairs', 'tfc:rock/' + block_type + '/' + rock + '_stairs')
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_stairs', lang('%s %s Stairs', rock, block_type))
                    # Slabs
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_slab()
                    slab_namespace = 'tfc:rock/' + block_type + '/' + rock + '_slab'
                    rm.block_loot(slab_namespace, one_pool({
                        'functions': [
                            {**loot_tables.set_count(2), 'conditions': [block_state_property(slab_namespace, {'type': 'double'})]},
                            explosion_decay()
                        ],
                        'name': slab_namespace
                    }))
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_slab', lang('%s %s Slab', rock, block_type))
                    # Walls
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_wall()
                    rm.block_loot('tfc:rock/' + block_type + '/' + rock + '_wall', 'tfc:rock/' + block_type + '/' + rock + '_wall')
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_wall', lang('%s %s Wall', rock, block_type))
                    rm.block_tag('minecraft:walls', 'tfc:rock/' + block_type + '/' + rock + '_wall')
                # Loot
                if block_type == 'raw' or block_type == 'hardened':
                    block.with_block_loot(one_pool_alternatives([{
                        'name': 'tfc:rock/raw/%s' % rock,
                        'conditions': ['tfc:is_isolated'],
                    }, {
                        'name': 'tfc:rock/loose/%s' % rock,
                        'functions': [loot_tables.set_count(1, 4)]
                    }]))
                elif block_type == 'gravel':
                    block.with_block_loot(one_pool_alternatives({
                        'conditions': [silk_touch()],
                        'name': 'tfc:rock/gravel/%s' % rock
                    }, {
                        'type': 'minecraft:alternatives',
                        'conditions': 'minecraft:survives_explosion',
                        'children': [{
                            'type': 'minecraft:item',
                            'conditions': [fortune_table([0.1, 0.14285715, 0.25, 1.0])],
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
            rm.blockstate('tfc:rock/anvil/%s' % rock, model='tfc:block/rock/anvil/%s' % rock).with_lang(lang('%s Anvil', rock)).with_block_loot(counted_item('tfc:rock/loose/%s' % rock, 1, 4)).with_item_model()
            rm.block_model('tfc:rock/anvil/%s' % rock, parent='tfc:block/rock/anvil', textures={'texture': 'tfc:block/rock/raw/%s' % rock})

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
                block.with_lang(lang('%s %s', rock, ore))
                rm.block_loot('tfc:ore/%s/%s' % (ore, rock), 'tfc:ore/%s' % ore)

    # Loose Ore Items
    for ore, ore_data in ORES.items():
        if ore_data.graded:
            for grade in ORE_GRADES.keys():
                rm.item_model('tfc:ore/%s_%s' % (grade, ore)).with_lang(lang('%s %s', grade, ore))
            rm.item_model('tfc:ore/small_%s' % ore).with_lang(lang('small %s', ore))
        else:
            rm.item_model('tfc:ore/%s' % ore).with_lang(lang('%s', ore))

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
            block = rm.blockstate(('%s_sandstone' % variant, sand))
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
                rm.block(('%s_sandstone' % variant, sand + extra)).with_lang(lang('%s %s sandstone' + extra, variant, sand))

    # Groundcover
    for misc in MISC_GROUNDCOVER:
        block = rm.blockstate(('groundcover', misc), variants={"": four_ways('tfc:block/groundcover/%s' % misc)}, use_default_model=False)
        block.with_lang(lang(misc))
        block.with_tag('can_be_snow_piled')

        if misc in {'stick', 'flint', 'feather', 'rotten_flesh', 'bone'}:  # Vanilla ground cover
            block.with_block_loot('minecraft:%s' % misc)
        else:
            block.with_block_loot('tfc:groundcover/%s' % misc)
            rm.item_model(('groundcover', misc), 'tfc:item/groundcover/%s' % misc)

    rm.blockstate('peat').with_block_model().with_item_model().with_lang('tfc:peat').with_lang(lang('Peat'))
    rm.blockstate('aggregate').with_block_model().with_item_model().with_block_loot('tfc:aggregate').with_lang(lang('Aggregate'))
    rm.blockstate('fire_bricks').with_block_model().with_item_model().with_block_loot('tfc:fire_bricks').with_lang(lang('Fire Bricks'))
    rm.blockstate('fire_clay_block').with_block_model().with_item_model().with_block_loot('tfc:fire_clay_block').with_lang(lang('Fire Clay Block'))
    rm.blockstate('thatch').with_block_model().with_item_model().with_block_loot('tfc:thatch').with_lang(lang('Thatch'))

    rm.blockstate(('alabaster', 'raw', 'alabaster')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/raw/alabaster').with_lang(lang('Alabaster'))
    rm.blockstate(('alabaster', 'raw', 'alabaster_bricks')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/raw/alabaster_bricks').with_lang(lang('Alabaster Bricks'))
    rm.blockstate(('alabaster', 'raw', 'polished_alabaster')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/raw/polished_alabaster').with_lang(lang('Polished Alabaster'))

    for color in COLORS:
        rm.blockstate(('alabaster', 'stained', color + '_raw_alabaster')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/stained/' + color + '_raw_alabaster').with_lang(lang('%s Raw Alabaster', color))
        bricks = rm.blockstate(('alabaster', 'stained', color + '_alabaster_bricks')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/stained/' + color + '_alabaster_bricks').with_lang(lang('%s Alabaster Bricks', color))
        polished = rm.blockstate(('alabaster', 'stained', color + '_polished_alabaster')).with_block_model().with_item_model().with_block_loot('tfc:alabaster/stained/' + color + '_polished_alabaster').with_lang(lang('%s Polished Alabaster', color))
        bricks.make_slab().make_stairs().make_wall()
        polished.make_slab().make_stairs().make_wall()
        for extra in ('slab', 'stairs', 'wall'):
            rm.block(('alabaster', 'stained', color + '_alabaster_bricks_' + extra)).with_lang(lang('%s Alabaster Bricks %s', color, extra))
            rm.block(('alabaster', 'stained', color + '_polished_alabaster_' + extra)).with_lang(lang('%s Polished Alabaster %s', color, extra))

    rm.item_model('torch', 'minecraft:block/torch')
    rm.item_model('dead_torch', 'tfc:block/torch_off')
    rm.block_model('dead_torch', parent='minecraft:block/template_torch', textures={'torch': 'tfc:block/torch_off'})
    rm.block_model('dead_wall_torch', parent='minecraft:block/template_torch_wall', textures={'torch': 'tfc:block/torch_off'})
    rm.blockstate('wall_torch', variants=four_rotations('minecraft:block/wall_torch', (None, 270, 90, 180))).with_lang(lang('Torch')).with_block_loot('minecraft:stick')
    rm.blockstate('dead_wall_torch', variants=four_rotations('tfc:block/dead_wall_torch', (None, 270, 90, 180))).with_block_loot('minecraft:stick').with_lang(lang('Burnt Out Torch'))
    rm.blockstate('torch', 'minecraft:block/torch').with_lang(lang('Torch'))
    rm.blockstate('dead_torch', 'tfc:block/dead_torch').with_lang(lang('Burnt Out Torch'))

    rm.blockstate('charcoal_pile', variants=dict((('layers=%d' % i), {'model': 'tfc:block/charcoal_pile/charcoal_height%d' % (i * 2) if i != 8 else 'tfc:block/charcoal_pile/charcoal_block'}) for i in range(1, 1 + 8))).with_lang(lang('Charcoal Pile')).with_block_loot('minecraft:charcoal')
    rm.blockstate('charcoal_forge', variants=dict((('heat_level=%d' % i), {'model': 'tfc:block/charcoal_forge/heat_%d' % i}) for i in range(0, 7 + 1))).with_lang(lang('Forge')).with_block_loot(counted_item('minecraft:charcoal', 7, -1))
    rm.blockstate('log_pile', variants={'axis=x': {'model': 'tfc:block/log_pile', 'y': 90, 'x': 90}, 'axis=z': {'model': 'tfc:block/log_pile', 'x': 90}}) \
        .with_block_model(textures={'side': 'tfc:block/log_pile_side', 'end': 'tfc:block/log_pile_front'}, parent='minecraft:block/cube_column_horizontal').with_lang(lang('Log Pile'))
    rm.blockstate('burning_log_pile', model='tfc:block/burning_log_pile').with_block_model(parent='minecraft:block/cube_all', textures={'all': 'tfc:block/devices/charcoal_forge/lit'}).with_lang(lang('Burning Log Pile'))

    for i in range(0, 7 + 1):
        rm.block_model('charcoal_forge/heat_%d' % i, parent='tfc:block/charcoal_forge/template_forge', textures={'top': 'tfc:block/devices/charcoal_forge/%d' % i})

    # Uses a custom block model
    rm.blockstate('crucible').with_item_model().with_lang(lang('crucible')).with_block_loot(one_pool({
        'name': 'tfc:crucible',
        'functions': [copy_block_entity_name(), copy_block_entity_nbt()]
    }))

    block = rm.block('thatch_bed').with_lang(lang('Thatch Bed'))
    block.with_block_loot(one_pool({
        'name': 'tfc:thatch',
        'functions': [loot_tables.set_count(2)],
        'conditions': [
            'minecraft:survives_explosion',
            block_state_property('tfc:thatch_bed', {'part': 'head'})
        ]
    }))

    rm.blockstate('firepit', variants={
        'lit=true': {'model': 'tfc:block/firepit_lit'},
        'lit=false': {'model': 'tfc:block/firepit_unlit'}
    }).with_lang(lang('Firepit')).with_block_loot(counted_item('tfc:powder/wood_ash', 1, 4))
    rm.item_model('firepit', parent='tfc:block/firepit_unlit')

    rm.blockstate_multipart('grill', [
        ({'model': 'tfc:block/firepit_grill'}),
        ({'lit': True}, {'model': 'tfc:block/firepit_lit'}),
        ({'lit': False}, {'model': 'tfc:block/firepit_unlit'})
    ]).with_lang(lang('Grill')).with_block_loot([counted_item('tfc:powder/wood_ash', 1, 4), 'tfc:wrought_iron_grill'])
    rm.item_model('grill', parent='tfc:block/firepit_grill')

    rm.blockstate_multipart('pot', [
        ({'model': 'tfc:block/firepit_pot'}),
        ({'lit': True}, {'model': 'tfc:block/firepit_lit'}),
        ({'lit': False}, {'model': 'tfc:block/firepit_unlit'})
    ]).with_lang(lang('Pot')).with_block_loot([counted_item('tfc:powder/wood_ash', 1, 4), 'tfc:ceramic/pot'])
    rm.item_model('pot', parent='tfc:block/firepit_pot')

    rm.blockstate('quern', 'tfc:block/quern').with_item_model().with_lang(lang('Quern'))

    rm.blockstate('placed_item', 'tfc:block/empty')
    rm.blockstate('scraping', 'tfc:block/empty')
    rm.blockstate('pit_kiln', variants=dict((('stage=%d' % i), {'model': 'tfc:block/pitkiln/pitkiln_%d' % i}) for i in range(0, 1 + 16))).with_lang(lang('Pit Kiln'))

    # Dirt
    for soil in SOIL_BLOCK_VARIANTS:
        # Regular Dirt
        block = rm.blockstate(('dirt', soil), variants={'': [{'model': 'tfc:block/dirt/%s' % soil, 'y': i} for i in range(0, 360, 90)]}, use_default_model=False)
        block.with_block_model()
        block.with_item_model()
        block.with_block_loot('tfc:dirt/%s' % soil)
        block.with_lang(lang('%s Dirt', soil))

        # Clay Dirt
        block = rm.blockstate(('clay', soil), variants={'': [{'model': 'tfc:block/clay/%s' % soil, 'y': i} for i in range(0, 360, 90)]}, use_default_model=False)
        block.with_block_model()
        block.with_block_loot(one_pool({
            'name': 'minecraft:clay_ball',
            'functions': [loot_tables.set_count(1, 3)]
        }))
        block.with_lang(lang('%s Clay Dirt', soil))
        block.with_item_model()

        rm.block(('grass_path', soil)).with_lang(lang('%s path'))

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
        rm.block_model((name, 'top'), {'texture': texture}, parent='tfc:block/grass_top')
        rm.block_model((name, 'snowy_top'), {'texture': texture}, parent='tfc:block/grass_snowy_top')
        rm.block_model((name, 'side'), {'texture': texture}, parent='tfc:block/grass_side')
        rm.block_model((name, 'snowy_side'), {'texture': texture}, parent='tfc:block/grass_snowy_side')
        rm.block_model((name, 'bottom'), {'texture': texture}, parent='tfc:block/grass_bottom')

    # Peat Grass
    block = rm.blockstate_multipart('peat_grass', grass_multipart('tfc:block/peat_grass'))
    block.with_block_loot('tfc:peat')
    block.with_tag('grass')
    block.with_lang(lang('Peat Grass'))
    grass_models('peat_grass', 'tfc:block/peat')

    # Grass Blocks
    for soil in SOIL_BLOCK_VARIANTS:
        for grass_var, dirt in (('grass', 'tfc:block/dirt/%s' % soil), ('clay_grass', 'tfc:block/clay/%s' % soil)):
            block = rm.blockstate_multipart((grass_var, soil), grass_multipart('tfc:block/%s/%s' % (grass_var, soil)))
            if grass_var == 'grass':
                block.with_block_loot('tfc:dirt/%s' % soil)
            else:
                block.with_block_loot(one_pool({
                    'name': 'minecraft:clay_ball',
                    'functions': [loot_tables.set_count(1, 3)]
                }))
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

    # Loot table for snow blocks and snow piles - override the vanilla one to only return one snowball per layer
    def snow_block_loot_table(block: str):
        rm.block_loot(block, one_pool_alternatives({
            'conditions': [silk_touch()],
            'name': 'minecraft:snow'
        }, 'minecraft:snowball', conditions={
            'condition': 'minecraft:entity_properties',
            'predicate': {},
            'entity': 'this'
        }))

    snow_block_loot_table('snow_pile')
    snow_block_loot_table('minecraft:snow')

    # Sea Ice
    block = rm.blockstate('sea_ice').with_block_model().with_item_model().with_lang(lang('sea ice'))
    block.with_block_loot(one_pool({
        'name': 'minecraft:ice',
        'conditions': [silk_touch()]
    }))

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
                item = rm.item_model(('metal', '%s' % metal_item, '%s' % metal), texture, parent=metal_item_data.parent_model)
                if metal_item == 'propick':
                    item.with_lang('%s Prospector\'s Pick' % lang(metal))  # .title() works weird w.r.t the possessive.
                else:
                    item.with_lang(lang('%s %s', metal, metal_item))

        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type in metal_data.types or metal_block_data.type == 'all':
                block = rm.blockstate(('metal', '%s' % metal_block, metal))
                block.with_block_model({
                    'all': 'tfc:block/metal/%s' % metal,
                    'particle': 'tfc:block/metal/%s' % metal
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

    # Pottery
    for pottery in SIMPLE_POTTERY:  # both fired and unfired items
        rm.item_model(('ceramic', pottery)).with_lang(lang(pottery))
        rm.item_model(('ceramic', 'unfired_' + pottery)).with_lang(lang('Unfired %s', pottery))

    for pottery in SIMPLE_UNFIRED_POTTERY:  # just the unfired item (fired is a vanilla item)
        rm.item_model(('ceramic', 'unfired_' + pottery)).with_lang(lang('Unfired %s', pottery))

    rm.item_model(('ceramic', 'jug')).with_lang(lang('Ceramic Jug'))

    # Small Ceramic Vessels (colored)
    for color in COLORS:
        rm.item_model(('ceramic', color + '_unfired_vessel')).with_lang(lang('%s Unfired Vessel', color))
        rm.item_model(('ceramic', color + '_glazed_vessel')).with_lang(lang('%s Glazed Vessel', color))

    # Molds
    for variant, data in METAL_ITEMS.items():
        if data.mold:
            rm.item_model(('ceramic', 'unfired_%s_mold' % variant), 'tfc:item/ceramic/unfired_%s' % variant).with_lang(lang('unfired %s mold', variant))
            rm.custom_item_model(('ceramic', '%s_mold' % variant), 'tfc:contained_fluid', {
                'parent': 'forge:item/default',
                'textures': {
                    'base': 'tfc:item/ceramic/fired_mold/%s_empty' % variant,
                    'fluid': 'tfc:item/ceramic/fired_mold/%s_overlay' % variant
                }
            }).with_lang(lang('%s mold', variant))

    # Plants
    for plant, plant_data in PLANTS.items():
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        p = 'tfc:plant/%s' % plant
        lower_only = block_state_property(p, {'part': 'lower'})
        if plant_data.type == 'short_grass':
            rm.block_loot(p, one_pool_alternatives([{
                'name': p,
                'conditions': [match_tag('forge:shears')],
            }, {
                'name': 'tfc:straw',
                'conditions': [match_tag('tfc:knives')]
            }]))
        elif plant_data.type == 'tall_grass':
            rm.block_loot(p, one_pool_alternatives([{
                'name': p,
                'conditions': [match_tag('forge:shears'), lower_only],
            }, {
                'name': 'tfc:straw',
                'conditions': [match_tag('tfc:knives')]
            }]))
        elif plant_data.type in ('tall_plant', 'emergent', 'emergent_fresh'):
            rm.block_loot(p, one_pool({'name': p, 'conditions': [match_tag('tfc:knives'), lower_only]}))
        elif plant_data.type == 'cactus':
            rm.block_loot(p, p)
        else:
            rm.block_loot(p, one_pool({'name': p, 'conditions': [match_tag('tfc:knives')]}))
    for plant in MISC_PLANT_FEATURES:
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
    for plant in ('tree_fern', 'arundo', 'winged_kelp', 'leafy_kelp', 'giant_kelp_flower'):
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))
        rm.block_loot('tfc:plant/%s' % plant, 'tfc:plant/%s' % plant)
    rm.lang('block.tfc.sea_pickle', lang('sea_pickle'))

    # Berry Stuff
    for berry in BERRIES.keys():
        rm.item_model('food/' + berry).with_lang(lang(berry))

    for grain in GRAINS:
        for suffix in GRAIN_SUFFIXES:
            rm.item_model('food/%s%s' % (grain, suffix)).with_lang(lang('%s%s', grain, suffix))

    for meat in MEATS:
        rm.item_model(('food', meat)).with_lang(lang('raw %s', meat))
        rm.item_model(('food', 'cooked_' + meat)).with_lang(lang('cooked %s', meat))
    for veg in VEGETABLES:
        rm.item_model(('food', veg)).with_lang(lang(veg))

    rm.blockstate('plant/dead_berry_bush', variants={
        'stage=0': {'model': 'tfc:block/plant/dead_berry_bush_0'},
        'stage=1': {'model': 'tfc:block/plant/dead_berry_bush_1'},
        'stage=2': {'model': 'tfc:block/plant/dead_berry_bush_2'}
    }).with_lang(lang('Dead Bush')).with_tag('any_spreading_bush')
    rm.blockstate('plant/dead_cane', variants={
        **four_rotations('tfc:block/plant/dead_berry_bush_side_0', (90, None, 180, 270), suffix=',stage=0'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_1', (90, None, 180, 270), suffix=',stage=1'),
        **four_rotations('tfc:block/plant/dead_berry_bush_side_2', (90, None, 180, 270), suffix=',stage=2')
    }).with_lang(lang('Dead Cane')).with_tag('any_spreading_bush')

    state_to_model = {'healthy': '', 'dormant': 'dry_', 'fruiting': 'fruiting_', 'flowering': 'flowering_'}
    lifecycles = ['healthy', 'dormant', 'fruiting', 'flowering']
    for berry in ('blackberry', 'raspberry', 'blueberry', 'elderberry'):
        rm.blockstate('plant/%s_bush' % berry, variants=dict(('lifecycle=%s,stage=%d' % (state, i), {'model': 'tfc:block/plant/%s%s_bush_%d' % (state_to_model[state], berry, i)}) for state, i in itertools.product(lifecycles, range(0, 3)))).with_lang(lang('%s Bush', berry))
        cane_dict = {}
        for state, i in itertools.product(lifecycles, range(3)):
            cane_dict.update(four_rotations('tfc:block/plant/%s%s_bush_side_%d' % (state_to_model[state], berry, i), (90, None, 180, 270), suffix=',stage=%d' % i, prefix='lifecycle=%s,' % state))
        rm.blockstate('plant/%s_bush_cane' % berry, variants=cane_dict).with_lang(lang('%s Cane', berry))
        rm.item_model('plant/%s_bush' % berry, 'tfc:block/berry_bush/%s_cane' % berry)
        for state in state_to_model.values():
            bush_textures = {'cane': 'tfc:block/berry_bush/' + state + '%s_cane' % berry, 'bush': 'tfc:block/berry_bush/' + state + '%s_bush' % berry}
            for i in range(0, 3):
                rm.block_model('plant/' + state + berry + '_bush_%d' % i, parent='tfc:block/plant/berry_bush_%d' % i, textures=bush_textures)
                rm.block_model('plant/' + state + berry + '_bush_side_%d' % i, parent='tfc:block/plant/berry_bush_side_%d' % i, textures=bush_textures)
        rm.block_tag('spreading_bush', 'plant/%s_bush' % berry, 'plant/%s_bush_cane' % berry)

    for i in range(0, 3):
        rm.block_model('plant/dead_berry_bush_%d' % i, parent='tfc:block/plant/berry_bush_%d' % i, textures={'cane': 'tfc:block/berry_bush/dead_cane', 'bush': 'tfc:block/berry_bush/dead_bush'})
        rm.block_model('plant/dead_berry_bush_side_%d' % i, parent='tfc:block/plant/berry_bush_side_%d' % i, textures={'cane': 'tfc:block/berry_bush/dead_cane', 'bush': 'tfc:block/berry_bush/dead_bush'})

    for berry in ('gooseberry', 'snowberry', 'bunchberry', 'cloudberry', 'wintergreen_berry', 'strawberry', 'cranberry'):
        rm.blockstate('plant/%s_bush' % berry, variants=dict(('lifecycle=%s,stage=%d' % (state, i), {'model': 'tfc:block/plant/%s%s_bush_%d' % (state_to_model[state], berry, i)}) for state, i in itertools.product(lifecycles, range(0, 3)))).with_lang(lang('%s Bush', berry))
        rm.item_model('plant/%s_bush' % berry, 'tfc:block/berry_bush/%s_bush' % berry)
        for state, i in itertools.product(state_to_model.values(), range(0, 3)):
            rm.block_model('plant/' + state + berry + '_bush_%d' % i, parent='tfc:block/plant/stationary_bush_%d' % i, textures={'bush': 'tfc:block/berry_bush/' + state + '%s_bush' % berry})

    for fruit in FRUITS.keys():
        if fruit != 'banana':
            for prefix in ('', 'growing_'):
                block = rm.blockstate_multipart('plant/' + fruit + '_' + prefix + 'branch', [
                    ({'model': 'tfc:block/plant/%s_branch_core' % fruit}),
                    ({'down': True}, {'model': 'tfc:block/plant/%s_branch_down' % fruit}),
                    ({'up': True}, {'model': 'tfc:block/plant/%s_branch_up' % fruit}),
                    ({'north': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 90}),
                    ({'south': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 270}),
                    ({'west': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit}),
                    ({'east': True}, {'model': 'tfc:block/plant/%s_branch_side' % fruit, 'y': 180})
                ]).with_tag('fruit_tree_branch').with_item_model().with_lang(lang('%s Branch', fruit))
                if prefix == '':
                    block.with_block_loot(one_pool({
                        'type': 'minecraft:item',
                        'conditions': [{
                            'condition': 'alternative',
                            'terms': [
                                block_state_property('tfc:plant/%s_branch' % fruit, {'up': 'true', '%s' % direction: 'true'}) for direction in ['west', 'east', 'north', 'south']
                            ]
                        }],
                        'name': 'tfc:plant/%s_sapling' % fruit
                    }))
            for part in ('down', 'side', 'up', 'core'):
                rm.block_model('tfc:plant/%s_branch_%s' % (fruit, part), parent='tfc:block/plant/branch_%s' % part, textures={'bark': 'tfc:block/fruit_tree/%s_branch' % fruit})
            rm.blockstate('plant/%s_leaves' % fruit, variants={
                'lifecycle=flowering': {'model': 'tfc:block/plant/%s_flowering_leaves' % fruit},
                'lifecycle=fruiting': {'model': 'tfc:block/plant/%s_fruiting_leaves' % fruit},
                'lifecycle=dormant': {'model': 'tfc:block/plant/%s_dry_leaves' % fruit},
                'lifecycle=healthy': {'model': 'tfc:block/plant/%s_leaves' % fruit}
            }).with_item_model().with_tag('minecraft:leaves').with_tag('fruit_tree_leaves').with_lang(lang('%s Leaves', fruit))
            for life in ('', '_fruiting', '_flowering', '_dry'):
                rm.block_model('tfc:plant/%s%s_leaves' % (fruit, life), parent='block/leaves', textures={'all': 'tfc:block/fruit_tree/%s%s_leaves' % (fruit, life)})

            rm.blockstate(('plant', '%s_sapling' % fruit), variants={'saplings=%d' % i: {'model': 'tfc:block/plant/%s_sapling_%d' % (fruit, i)} for i in range(1, 4 + 1)}).with_lang(lang('%s Sapling', fruit)).with_tag('fruit_tree_sapling')
            rm.block_loot(('plant', '%s_sapling' % fruit), one_pool({
                'name': 'tfc:plant/%s_sapling' % fruit,
                'functions': [list({**loot_tables.set_count(i), 'conditions': [block_state_property('tfc:plant/%s_sapling' % fruit, {'saplings': '%s' % i})]} for i in range(1, 5)), explosion_decay()]
            }))
            for i in range(2, 4 + 1):
                rm.block_model(('plant', '%s_sapling_%d' % (fruit, i)), parent='tfc:block/plant/cross_%s' % i, textures={'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit})
            rm.block_model(('plant', '%s_sapling_1' % fruit), {'cross': 'tfc:block/fruit_tree/%s_sapling' % fruit}, 'block/cross')
        else:
            def banana_suffix(state: str, i: int) -> str:
                if i == 2 and (state == 'fruiting' or state == 'flowering'):
                    return '%d_%s' % (i, state)
                return str(i)

            rm.blockstate('plant/banana_plant', variants=dict({'lifecycle=%s,stage=%d' % (state, i): {'model': 'tfc:block/plant/banana_trunk_%s' % banana_suffix(state, i)} for state, i in itertools.product(lifecycles, range(0, 3))})).with_lang(lang('Banana Plant')).with_tag('fruit_tree_branch').with_block_loot(one_pool({
                'name': 'tfc:plant/banana_sapling',
                'functions': [{**loot_tables.set_count(1, 2)}],
                'conditions': [block_state_property('tfc:plant/banana_plant', {'stage': '2'})]
            }))
            rm.block_model(('plant', 'banana_sapling'), textures={'cross': 'tfc:block/fruit_tree/banana_sapling'}, parent='block/cross')
            rm.blockstate(('plant', 'banana_sapling'), model='tfc:block/plant/banana_sapling').with_lang(lang('Banana Sapling')).with_tag('fruit_tree_sapling')

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

            block.with_block_loot(one_pool_alternatives({
                'name': 'tfc:wood/%s/%s' % (variant, wood),
                'conditions': block_state_property('tfc:wood/%s/%s' % (variant, wood), {'natural': 'false'})
            }, 'tfc:wood/%s/%s' % (variant.replace('wood', 'log'), wood)))

            if variant != 'log':
                block.with_item_model()
            else:
                rm.item_model(('wood', variant, wood), 'tfc:item/wood/log/' + wood)
            end = 'tfc:block/wood/%s/%s' % (variant.replace('log', 'log_top').replace('wood', 'log'), wood)
            side = 'tfc:block/wood/%s/%s' % (variant.replace('wood', 'log'), wood)
            block.with_block_model({'end': end, 'side': side}, parent='block/cube_column')
            if 'stripped' in variant:
                block.with_lang(lang(variant.replace('_', ' ' + wood + ' ')))
            else:
                block.with_lang(lang('%s %s', wood, variant))
            if variant == 'log':
                block.with_tag('minecraft:logs')
        rm.item_model(('wood', 'lumber', wood)).with_lang(lang('%s Lumber', wood))

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

        # Sapling
        block = rm.blockstate(('wood', 'sapling', wood), 'tfc:block/wood/sapling/%s' % wood)
        block.with_block_model({'cross': 'tfc:block/wood/sapling/%s' % wood}, 'block/cross')
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

        # Tool Rack
        rack_namespace = 'tfc:wood/planks/%s_tool_rack' % wood
        rm.blockstate(rack_namespace, model='tfc:block/wood/planks/%s_tool_rack' % wood, variants=four_rotations('tfc:block/wood/planks/%s_tool_rack' % wood, (270, 180, None, 90)))
        rm.block_model(rack_namespace, textures={'texture': 'tfc:block/wood/planks/%s' % wood, 'particle': 'tfc:block/wood/planks/%s' % wood}, parent='tfc:block/tool_rack')
        rm.item_model(rack_namespace, parent='tfc:block/wood/planks/%s_tool_rack' % wood, no_textures=True)
        rm.lang('block.tfc.wood.planks.%s_tool_rack' % wood, lang('%s Tool Rack', wood))
        rm.block_loot(rack_namespace, rack_namespace)

        # Bookshelf
        block = rm.blockstate('tfc:wood/planks/%s_bookshelf' % wood)
        block.with_block_model({'end': 'tfc:block/wood/planks/%s' % wood, 'side': 'tfc:block/wood/planks/%s_bookshelf' % wood}, parent='minecraft:block/bookshelf')
        block.with_item_model()
        block.with_lang(lang('%s Bookshelf', wood))

        # Workbench
        rm.blockstate(('wood', 'planks', '%s_workbench' % wood)).with_block_model(parent='minecraft:block/cube', textures={
            'particle': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'north': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'south': 'tfc:block/wood/planks/%s_workbench_side' % wood,
            'east': 'tfc:block/wood/planks/%s_workbench_side' % wood,
            'west': 'tfc:block/wood/planks/%s_workbench_front' % wood,
            'up': 'tfc:block/wood/planks/%s_workbench_top' % wood,
            'down': 'tfc:block/wood/planks/%s' % wood
        }).with_item_model().with_lang(lang('%s Workbench', wood)).with_tag('tfc:workbench')

        # Doors
        rm.item_model('tfc:wood/planks/%s_door' % wood, 'tfc:item/wood/planks/%s_door' % wood)

        # Log Fences
        log_fence_namespace = 'tfc:wood/planks/' + wood + '_log_fence'
        rm.blockstate_multipart(log_fence_namespace, parts=block_states.fence_multipart('tfc:block/wood/planks/' + wood + '_log_fence_post', 'tfc:block/wood/planks/' + wood + '_log_fence_side'))
        rm.block_model(log_fence_namespace + '_post', textures={'texture': 'tfc:block/wood/log/' + wood}, parent='block/fence_post')
        rm.block_model(log_fence_namespace + '_side', textures={'texture': 'tfc:block/wood/planks/' + wood}, parent='block/fence_side')
        rm.block_model(log_fence_namespace + '_inventory', textures={'log': 'tfc:block/wood/log/' + wood, 'planks': 'tfc:block/wood/planks/' + wood}, parent='tfc:block/log_fence_inventory')
        rm.item_model('tfc:wood/planks/' + wood + '_log_fence', parent='tfc:block/wood/planks/' + wood + '_log_fence_inventory', no_textures=True)
        rm.block_loot(log_fence_namespace, log_fence_namespace)

        texture = 'tfc:block/wood/sheet/%s' % wood
        connection = 'tfc:block/wood/support/%s_connection' % wood
        rm.blockstate_multipart(('wood', 'vertical_support', wood), [
            {'model': 'tfc:block/wood/support/%s_vertical' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ]).with_tag('tfc:support_beam').with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)
        rm.blockstate_multipart(('wood', 'horizontal_support', wood), [
            {'model': 'tfc:block/wood/support/%s_horizontal' % wood},
            ({'north': True}, {'model': connection, 'y': 270}),
            ({'east': True}, {'model': connection}),
            ({'south': True}, {'model': connection, 'y': 90}),
            ({'west': True}, {'model': connection, 'y': 180}),
        ]).with_tag('tfc:support_beam').with_lang(lang('%s Support', wood)).with_block_loot('tfc:wood/support/' + wood)

        rm.block_model('tfc:wood/support/%s_inventory' % wood, textures={'texture': texture}, parent='tfc:block/wood/support/inventory')
        rm.block_model('tfc:wood/support/%s_vertical' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/vertical')
        rm.block_model('tfc:wood/support/%s_connection' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/connection')
        rm.block_model('tfc:wood/support/%s_horizontal' % wood, textures={'texture': texture, 'particle': texture}, parent='tfc:block/wood/support/horizontal')
        rm.item_model(('wood', 'support', wood), no_textures=True, parent='tfc:block/wood/support/%s_inventory' % wood).with_lang(lang('%s Support', wood))

        # Tags
        for fence_namespace in ('tfc:wood/planks/' + wood + '_fence', log_fence_namespace):
            rm.block_tag('minecraft:wooden_fences', fence_namespace)
            rm.block_tag('minecraft:fences', fence_namespace)
            rm.block_tag('forge:fences', fence_namespace)
            rm.block_tag('forge:fences/wooden', fence_namespace)
        fence_gate_namespace = 'tfc:wood/planks/' + wood + '_fence_gate'
        rm.block_tag('forge:fence_gates/wooden', fence_gate_namespace)
        rm.block_tag('forge:fence_gates', fence_gate_namespace)
        rm.block_tag('minecraft:doors', 'tfc:wood/planks/' + wood + '_door')
        rm.block_tag('minecraft:buttons', 'tfc:wood/planks/' + wood + '_button')
        rm.block_tag('minecraft:wooden_buttons', 'tfc:wood/planks/' + wood + '_button')
        rm.block_tag('minecraft:wooden_pressure_plates', 'tfc:wood/planks/' + wood + '_pressure_plate')
        rm.block_tag('minecraft:wooden_slabs', 'tfc:wood/planks/' + wood + '_slab')
        rm.block_tag('minecraft:wooden_stairs', 'tfc:wood/planks/' + wood + '_stairs')
        for variant in ('log', 'stripped_log', 'wood', 'stripped_wood'):
            if variant != 'log':
                rm.block_tag('minecraft:logs', 'tfc:wood/' + variant + '/' + wood)
            rm.block_tag('tfc:' + wood + '_logs', 'tfc:wood/' + variant + '/' + wood)
            rm.block_tag('tfc:creeping_plantable_on', 'tfc:wood/' + variant + '/' + wood)

        # Lang
        for variant in ('door', 'trapdoor', 'fence', 'log_fence', 'fence_gate', 'button', 'pressure_plate', 'slab', 'stairs'):
            rm.lang('block.tfc.wood.planks.' + wood + '_' + variant, lang('%s %s', wood, variant))
        for variant in ('sapling', 'leaves'):
            rm.lang('block.tfc.wood.' + variant + '.' + wood, lang('%s %s', wood, variant))

    # Fluids

    water_based_fluid(rm, 'salt_water')
    water_based_fluid(rm, 'spring_water')

    # Mixable tags for vanilla water
    rm.fluid_tag('mixable', '#minecraft:water')

    for metal in METALS.keys():
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None)
        rm.fluid_tag(metal, 'tfc:metal/%s' % metal, 'tfc:metal/flowing_%s' % metal)

        item = rm.custom_item_model(('bucket', 'metal', metal), 'forge:bucket', {
            'parent': 'forge:item/bucket',
            'fluid': 'tfc:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))

    # Thin Spikes: Calcite + Icicles
    for variant, texture in (('calcite', 'tfc:block/calcite'), ('icicle', 'minecraft:block/ice')):
        block = rm.blockstate(variant, variants={
            'tip=true': {'model': 'tfc:block/%s_tip' % variant},
            'tip=false': {'model': 'tfc:block/%s' % variant}
        })
        block.with_item_model()
        block.with_lang(lang(variant))

        rm.block_model(variant, textures={'0': texture, 'particle': texture}, parent='tfc:block/thin_spike')
        rm.block_model(variant + '_tip', textures={'0': texture, 'particle': texture}, parent='tfc:block/thin_spike_tip')

    for color in ('tube', 'brain', 'bubble', 'fire', 'horn'):
        corals(rm, color, False)
        corals(rm, color, True)


def water_based_fluid(rm: ResourceManager, name: str):
    rm.blockstate(('fluid', name)).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None)
    rm.fluid_tag(name, 'tfc:%s' % name, 'tfc:flowing_%s' % name)
    rm.fluid_tag('minecraft:water', 'tfc:%s' % name, 'tfc:flowing_%s' % name)  # Need to use water fluid tag for behavior
    rm.fluid_tag('mixable', 'tfc:%s' % name, 'tfc:flowing_%s' % name)

    item = rm.custom_item_model(('bucket', name), 'forge:bucket', {
        'parent': 'forge:item/bucket',
        'fluid': 'tfc:%s' % name
    })
    item.with_lang(lang('%s bucket', name))


def corals(rm: ResourceManager, color: str, dead: bool):
    # vanilla and tfc have a different convention for dead/color order
    left = 'dead_' + color if dead else color
    right = color + '_dead' if dead else color

    rm.blockstate('coral/%s_coral' % right, 'minecraft:block/%s_coral' % left)
    rm.blockstate('coral/%s_coral_fan' % right, 'minecraft:block/%s_coral_fan' % left)
    rm.blockstate('coral/%s_coral_wall_fan' % right, variants=dict(
        ('facing=%s' % d, {'model': 'minecraft:block/%s_coral_wall_fan' % left, 'y': r})
        for d, r in (('north', None), ('east', 90), ('south', 180), ('west', 270))
    ))

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


def one_pool(*entries, conditions: Any = None, functions: Any = None):
    """ Given a list of loot entry objects, returns a single loot pool, to be passed into ResourceManager#loot_table """
    return {
        'entries': utils.loot_entry_list(entries),
        'conditions': utils.loot_condition_list(conditions) if conditions is not None else utils.loot_default_conditions('block'),
        'functions': utils.loot_function_list(functions)
    }


def one_pool_alternatives(*entries, conditions: Any = None, functions: Any = None):
    return {
        'entries': [{
            'type': 'minecraft:alternatives',
            'children': utils.loot_entry_list(entries)
        }],
        'conditions': utils.loot_condition_list(conditions) if conditions is not None else utils.loot_default_conditions('block'),
        'functions': utils.loot_function_list(functions)
    }


def block_state_property(block: str, properties: Dict[str, str]) -> Dict[str, Any]:
    return {
        'condition': 'minecraft:block_state_property',
        'block': block,
        'properties': properties
    }


def explosion_decay() -> Dict[str, Any]:
    return {
        'function': 'minecraft:explosion_decay'
    }


def silk_touch() -> Dict[str, Any]:
    return {
        'condition': 'minecraft:match_tool',
        'predicate': {
            'enchantments': [{
                'enchantment': 'minecraft:silk_touch',
                'levels': {'min': 1}
            }]
        }
    }


def match_tag(tag: str) -> Dict[str, Any]:
    return {
        'condition': 'minecraft:match_tool',
        'predicate': {'tag': tag}
    }


def fortune_table(chances: List[float]) -> Dict[str, Any]:
    return {
        'condition': 'minecraft:table_bonus',
        'enchantment': 'minecraft:fortune',
        'chances': chances
    }


def counted_item(item: str, min_count: int, max_count: int) -> Dict[str, Any]:
    return {
        'entries': item,
        'functions': [
            loot_tables.set_count(min_count, max_count)
        ]
    }


def copy_block_entity_name() -> Dict[str, Any]:
    return {
        'function': 'minecraft:copy_name',
        'source': 'block_entity'
    }


def copy_block_entity_nbt() -> Dict[str, Any]:
    return {
        'function': 'minecraft:copy_nbt',
        'source': 'block_entity',
        'ops': [{
            'source': '',
            'target': 'BlockEntityTag',
            'op': 'replace'
        }]
    }

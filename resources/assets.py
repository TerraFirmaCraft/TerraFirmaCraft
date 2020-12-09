#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from typing import Any

import mcresources.block_states as block_states
import mcresources.loot_tables as loot_tables
import mcresources.utils as utils
from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # Rock block variants
    for rock in ROCKS.keys():
        for block_type in ROCK_BLOCK_TYPES:
            if block_type == 'spike':
                # Spikes have special block states
                block = rm.blockstate(('rock', block_type, rock), variants=dict(('part=%s' % part, {'model': 'tfc:block/rock/%s/%s_%s' % (block_type, rock, part)}) for part in ROCK_SPIKE_PARTS))
                block.with_lang(lang('%s spike', rock))
                block.with_block_loot({
                    'entries': 'tfc:rock/loose/%s' % rock,
                    'functions': [
                        loot_tables.set_count(1, 2)
                    ]
                })
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
                    'facing=east,count=1': {'model': 'tfc:block/rock/pebble/%s' % rock, 'y': 90},
                    'facing=north,count=1': {'model': 'tfc:block/rock/pebble/%s' % rock},
                    'facing=south,count=1': {'model': 'tfc:block/rock/pebble/%s' % rock, 'y': 180},
                    'facing=west,count=1': {'model': 'tfc:block/rock/pebble/%s' % rock, 'y': 270},
                    'facing=east,count=2': {'model': 'tfc:block/rock/rubble/%s' % rock, 'y': 90},
                    'facing=north,count=2': {'model': 'tfc:block/rock/rubble/%s' % rock},
                    'facing=south,count=2': {'model': 'tfc:block/rock/rubble/%s' % rock, 'y': 180},
                    'facing=west,count=2': {'model': 'tfc:block/rock/rubble/%s' % rock, 'y': 270},
                    'facing=east,count=3': {'model': 'tfc:block/rock/boulder/%s' % rock, 'y': 90},
                    'facing=north,count=3': {'model': 'tfc:block/rock/boulder/%s' % rock},
                    'facing=south,count=3': {'model': 'tfc:block/rock/boulder/%s' % rock, 'y': 180},
                    'facing=west,count=3': {'model': 'tfc:block/rock/boulder/%s' % rock, 'y': 270},
                })
                for loose_type in ('pebble', 'rubble', 'boulder'):
                    rm.block_model('tfc:rock/%s/%s' % (loose_type, rock), 'tfc:item/loose_rock/%s' % rock, parent='tfc:block/groundcover/%s' % loose_type)

                block.with_lang(lang('%s %s', rock, block_type))
                block.with_tag('can_be_snow_piled')
                block.with_block_loot({
                    'entries': [{
                        'name': 'tfc:rock/loose/%s' % rock,
                        'functions': [
                            {**loot_tables.set_count(2), 'conditions': [block_state_property('tfc:rock/loose/%s' % rock, {'count': '2'})]},
                            {**loot_tables.set_count(3), 'conditions': [block_state_property('tfc:rock/loose/%s' % rock, {'count': '3'})]},
                            explosion_decay()
                        ]
                    }]
                })

                # Model for the item
                rm.item_model(('rock', 'loose', rock), 'tfc:item/loose_rock/%s' % rock)

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
                    rm.block_loot(slab_namespace, {
                        'entries': [{
                            'functions': [
                                {**loot_tables.set_count(2), 'conditions': [block_state_property(slab_namespace, {'type': 'double'})]},
                                explosion_decay()
                            ],
                            'name': slab_namespace
                        }]
                    })
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_slab', lang('%s %s Slab', rock, block_type))
                    # Walls
                    rm.block('tfc:rock/' + block_type + '/' + rock).make_wall()
                    rm.block_loot('tfc:rock/' + block_type + '/' + rock + '_wall', 'tfc:rock/' + block_type + '/' + rock + '_wall')
                    rm.lang('block.tfc.rock.' + block_type + '.' + rock + '_wall', lang('%s %s Wall', rock, block_type))
                    rm.block_tag('minecraft:walls', 'tfc:rock/' + block_type + '/' + rock + '_wall')
                # Loot
                if block_type == 'raw':
                    block.with_block_loot(alternatives([{
                        'name': 'tfc:rock/raw/%s' % rock,
                        'conditions': ['tfc:is_isolated'],
                    }, {
                        'name': 'tfc:rock/loose/%s' % rock,
                        'functions': [loot_tables.set_count(1, 4)]
                    }]))
                elif block_type == 'hardened':
                    block.with_block_loot({
                        'entries': 'tfc:rock/loose/%s' % rock,
                        'functions': [
                            loot_tables.set_count(1, 4)
                        ]
                    })
                else:
                    block.with_block_loot('tfc:rock/%s/%s' % (block_type, rock))
                # Lang
                if block_type in {'smooth', 'raw', 'chiseled', 'hardened'}:
                    block.with_lang(lang('%s %s', block_type, rock))
                else:
                    block.with_lang(lang('%s %s', rock, block_type))

        # Ores
        for ore, ore_data in ORES.items():
            if ore_data.graded:
                # Small Ores / Groundcover Blocks
                block = rm.blockstate('tfc:ore/small_%s' % ore, variants={
                    'facing=east': {'model': 'tfc:block/groundcover/%s' % ore, 'y': 90},
                    'facing=north': {'model': 'tfc:block/groundcover/%s' % ore},
                    'facing=south': {'model': 'tfc:block/groundcover/%s' % ore, 'y': 180},
                    'facing=west': {'model': 'tfc:block/groundcover/%s' % ore, 'y': 270}
                })
                block.with_lang(lang('small %s', ore))
                block.with_block_loot('tfc:ore/small_%s' % ore)
                block.with_tag('can_be_snow_piled')

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

    # Groundcover
    for misc in MISC_GROUNDCOVER:
        block = rm.blockstate(('groundcover', misc), variants={
            'facing=east': {'model': 'tfc:block/groundcover/%s' % misc, 'y': 90},
            'facing=north': {'model': 'tfc:block/groundcover/%s' % misc},
            'facing=south': {'model': 'tfc:block/groundcover/%s' % misc, 'y': 180},
            'facing=west': {'model': 'tfc:block/groundcover/%s' % misc, 'y': 270}
        })
        block.with_lang(lang('%s Block', misc))
        block.with_tag('can_be_snow_piled')

        if misc in {'stick', 'flint', 'feather', 'rotten_flesh', 'bone'}:  # Vanilla ground cover
            block.with_block_loot('minecraft:%s' % misc)
        else:
            block.with_block_loot('tfc:groundcover/%s' % misc)
            rm.item_model(('groundcover', misc), 'tfc:item/groundcover/%s' % misc)

    # Peat
    block = rm.blockstate('peat')
    block.with_block_model('tfc:block/peat')
    block.with_item_model()
    block.with_block_loot('tfc:peat')
    block.with_lang(lang('Peat'))

    rm.blockstate('thatch').with_block_model().with_item_model().with_block_loot('tfc:thatch').with_lang(lang('Thatch'))

    block = rm.block_model('thatch_bed').with_item_model().with_lang(lang('Thatch Bed'))
    block.with_block_loot({
        'entries': [{
            'type': 'minecraft:item',
            'name': 'tfc:thatch_bed'
        }],
        'conditions': [
            'minecraft:survives_explosion',
            block_state_property('tfc:thatch_bed', {'part': 'head'})
        ]
    })

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
        block.with_block_loot('tfc:clay/%s' % soil)  # todo: fix loot table - should drop clay balls
        block.with_lang(lang('%s Clay Dirt', soil))
        block.with_item_model()

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
            block.with_block_loot('tfc:dirt/%s' % soil)
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
        rm.block_loot(block, {
            'entries': [{
                'type': 'minecraft:alternatives',
                'children': utils.loot_entry_list([{
                    'conditions': [silk_touch()],
                    'name': 'minecraft:snow'
                }, 'minecraft:snowball'])
            }]
        })

    snow_block_loot_table('snow_pile')
    snow_block_loot_table('minecraft:snow')

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
        for rock_item in ROCK_ITEMS:
            item = rm.item_model(('stone', rock_item, rock), 'tfc:item/stone/%s' % rock_item, parent='item/handheld')
            item.with_lang(lang('stone %s', rock_item))

    # Rock Items
    for rock in ROCKS.keys():
        rm.item_model(('brick', rock), 'tfc:item/brick/%s' % rock).with_lang(lang('%s brick', rock))

    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type in metal_data.types or metal_item_data.type == 'all':
                item = rm.item_model(('metal', '%s' % metal_item, '%s' % metal), 'tfc:item/metal/%s/%s' % (metal_item, metal), parent=metal_item_data.parent_model)
                item.with_lang(lang('%s %s' % (metal, metal_item)))

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

    # Gems
    for gem in GEMS:
        rm.item_model(('gem', gem)).with_lang(lang('cut %s', gem))
        rm.item_model(('powder', gem)).with_lang(lang('%s powder', gem))

    # Plants
    for plant, plant_data in PLANTS.items():
        rm.lang('block.tfc.plant.%s' % plant, lang(plant))

    # Wood Blocks
    for wood in WOODS:
        # Logs
        for variant in ('log', 'stripped_log', 'wood', 'stripped_wood'):
            block = rm.blockstate(('wood', variant, wood), variants={
                'axis=y': {'model': 'tfc:block/wood/%s/%s' % (variant, wood)},
                'axis=z': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'x': 90},
                'axis=x': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'x': 90, 'y': 90}
            }, use_default_model=False)
            block.with_item_model()
            end = 'tfc:block/wood/%s/%s' % (variant.replace('log', 'log_top').replace('wood', 'log'), wood)
            side = 'tfc:block/wood/%s/%s' % (variant.replace('wood', 'log'), wood)
            block.with_block_model({'end': end, 'side': side}, parent='block/cube_column')
            if 'stripped' in variant:
                block.with_lang(lang(variant.replace('_', ' ' + wood + ' ')))
            else:
                block.with_lang(lang('%s %s', wood, variant))
            if variant == 'log':
                block.with_tag('minecraft:logs')

        # Groundcover
        for variant in ('twig', 'fallen_leaves'):
            block = rm.blockstate('wood/%s/%s' % (variant, wood), variants={
                'facing=east': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'y': 90},
                'facing=north': {'model': 'tfc:block/wood/%s/%s' % (variant, wood)},
                'facing=south': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'y': 180},
                'facing=west': {'model': 'tfc:block/wood/%s/%s' % (variant, wood), 'y': 270}
            })
            block.with_item_model()
            block.with_lang(lang('%s %s', wood, variant))

            if variant == 'twig':
                block.with_block_model({'side': 'tfc:block/wood/log/%s' % wood, 'top': 'tfc:block/wood/log_top/%s' % wood}, parent='tfc:block/groundcover/%s' % variant)
                block.with_block_loot('minecraft:stick')
            elif variant == 'fallen_leaves':
                block.with_block_model('tfc:block/wood/leaves/%s' % wood, parent='tfc:block/groundcover/%s' % variant)
                block.with_block_loot('tfc:wood/%s/%s' % (variant, wood))

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
        rm.blockstate(rack_namespace, model='tfc:block/wood/planks/%s_tool_rack' % wood, variants={
            'facing=east': {'model': 'tfc:block/wood/planks/%s_tool_rack' % wood, 'y': 270},
            'facing=north': {'model': 'tfc:block/wood/planks/%s_tool_rack' % wood, 'y': 180},
            'facing=south': {'model': 'tfc:block/wood/planks/%s_tool_rack' % wood},
            'facing=west': {'model': 'tfc:block/wood/planks/%s_tool_rack' % wood, 'y': 90}
        })
        rm.block_model(rack_namespace, textures={'texture': 'tfc:block/wood/planks/%s' % wood}, parent='tfc:block/tool_rack')
        rm.item_model(rack_namespace, parent='tfc:block/wood/planks/%s_tool_rack' % wood, no_textures=True)
        rm.lang('block.tfc.wood.planks.%s_tool_rack' % wood, lang('%s Tool Rack', wood))
        rm.block_loot(rack_namespace, rack_namespace)

        # Bookshelf
        block = rm.blockstate('tfc:wood/planks/%s_bookshelf' % wood)
        block.with_block_model({'end': 'tfc:block/wood/planks/%s' % wood, 'side': 'tfc:block/wood/planks/%s_bookshelf' % wood}, parent='minecraft:block/bookshelf')
        block.with_item_model()
        block.with_lang(lang('%s Bookshelf', wood))

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

    def bucket_item_model(name_parts, fluid):
        res = utils.resource_location(rm.domain, name_parts)
        rm.write((*rm.resource_dir, 'assets', res.domain, 'models', 'item', res.path), {
            'parent': 'forge:item/bucket',
            'loader': 'forge:bucket',
            'fluid': fluid
        })
        return rm.item(name_parts)

    # Fluids
    def water_based_fluid(name: str):
        rm.blockstate(('fluid', name)).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None)
        rm.fluid_tag(name, 'tfc:%s' % name, 'tfc:flowing_%s' % name)
        rm.fluid_tag('minecraft:water', 'tfc:%s' % name, 'tfc:flowing_%s' % name)  # Need to use water fluid tag for behavior
        rm.fluid_tag('mixable', 'tfc:%s' % name, 'tfc:flowing_%s' % name)

        item = bucket_item_model(('bucket', name), 'tfc:%s' % name)
        item.with_lang(lang('%s bucket', name))

    def molten_fluid(name: str):
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None)
        rm.fluid_tag(metal, 'tfc:metal/%s' % metal, 'tfc:metal/flowing_%s' % metal)

        item = bucket_item_model(('bucket', 'metal', name), 'tfc:%s' % name)
        item.with_lang(lang('molten %s bucket', name))

    water_based_fluid('salt_water')
    water_based_fluid('spring_water')

    # Mixable tags for vanilla water
    rm.fluid_tag('mixable', '#minecraft:water')

    for metal in METALS.keys():
        molten_fluid(metal)

    # Calcite
    block = rm.blockstate('calcite', variants={
        'tip=true': {'model': 'tfc:block/calcite_tip'},
        'tip=false': {'model': 'tfc:block/calcite'}
    })
    block.with_item_model()
    block.with_lang(lang('calcite'))

    # Misc Items
    rm.item_model('mortar').with_lang(lang('mortar')).with_tag('tfc:mortar')

    for color in ('tube', 'brain', 'bubble', 'fire', 'horn'):
        for block_type in ('coral', 'coral_fan', 'coral_wall_fan'):
            for life in ('dead_', ''):
                second_type = block_type
                if block_type == 'coral_wall_fan' and life != 'dead_':
                    rm.block_tag('wall_corals', ('coral', color + '_' + life + block_type))
                    second_type = 'coral_fan'
                rm.item_model(('coral', color + '_' + life + block_type), 'minecraft:block/' + life + color + '_' + second_type).with_lang(lang('%s %s %s', life, color, block_type))
                if block_type == 'coral' or block_type == 'coral_fan' and life != 'dead_':
                    rm.block_tag('corals', ('coral', color + '_' + life + block_type))


def alternatives(entries: utils.Json) -> Dict[str, Any]:
    return {
        'entries': [{
            'type': 'minecraft:alternatives',
            'children': utils.loot_entry_list(entries)
        }]
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

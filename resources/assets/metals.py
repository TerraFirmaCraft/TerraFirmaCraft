from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # Metals
    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type == 'part' and not metal_data.has_parts: continue
            if metal_item_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_item_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_item_data.type == 'utility' and not metal_data.has_utilities: continue
            rm.item_model(('metal', '%s' % metal_item, '%s' % metal), \
                'tfc:item/metal/%s/%s' % (metal_item, metal), \
                parent=metal_item_data.parent_model) \
                .with_lang(lang('%s %s' % (metal, metal_item)))
                
        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type == 'part' and not metal_data.has_parts: continue
            if metal_block_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_block_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_block_data.type == 'utility' and not metal_data.has_utilities: continue
            # todo: facings
            rm.blockstate(('metal', '%s' % metal_block, metal)) \
                .with_block_model(
                {
                    'all': 'tfc:block/metal/%s' % metal,
                    'particle': 'tfc:block/metal/%s' % metal
                }, parent=metal_block_data.parent_model) \
                .with_block_loot('tfc:metal/%s/%s' % (metal_block, metal)) \
                .with_lang(lang('%s %s' % (metal, metal_block))) \
                .with_item_model()
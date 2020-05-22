from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type == 'part' and not metal_data.has_parts: continue
            if metal_item_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_item_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_item_data.type == 'utility' and not metal_data.has_utilities: continue
            rm.recipe(('metal_item', '%s' % metal, '%s' % metal_item), 'tfc:metal_item', {
                'ingredient': {
                    'item': 'tfc:metal/%s/%s' % (metal_item, metal)
                },
                'metal': 'tfc:%s' % metal,
                'amount': metal_item_data.smelt_amount
            })
            
        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type == 'part' and not metal_data.has_parts: continue
            if metal_block_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_block_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_block_data.type == 'utility' and not metal_data.has_utilities: continue
            rm.recipe(('metal_item', '%s' % metal, '%s' % metal_block), 'tfc:metal_item', {
                'ingredient': {
                    'item': 'tfc:metal/%s/%s' % (metal_block, metal)
                },
                'metal': 'tfc:%s' % metal,
                'amount': metal_block_data.smelt_amount
            })
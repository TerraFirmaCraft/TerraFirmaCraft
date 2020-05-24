from constants import *
from mcresources import ResourceManager


def generate(rm: ResourceManager):
    for metal, metal_data in METALS.items():
        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type == 'part' and not metal_data.has_parts: continue
            if metal_item_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_item_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_item_data.type == 'utility' and not metal_data.has_utilities: continue
            if metal_item_data.tag != '':
                tag = 'tag'
                value = metal_item_data.tag + '/' + metal
            else:
                tag = 'item'
                value = 'tfc:metal/%s/%s' % (metal_item, metal)
            rm.data(('tfc', 'item_heats', metal, metal_item), {
                'ingredient': {
                    tag: value
                },
                "heat_capacity": metal_data.heat_capacity,
                "forging_temperature": metal_data.melt_temperature * 0.6,
                "welding_temperature": metal_data.melt_temperature * 0.8
            })

        # Metal Blocks
        for metal_block, metal_block_data in METAL_BLOCKS.items():
            if metal_block_data.type == 'part' and not metal_data.has_parts: continue
            if metal_block_data.type == 'tool' and not metal_data.has_tools: continue
            if metal_block_data.type == 'armor' and not metal_data.has_armor: continue
            if metal_block_data.type == 'utility' and not metal_data.has_utilities: continue
            if metal_block_data.tag != '':
                tag = 'tag'
                value = metal_block_data.tag + '/' + metal
            else:
                tag = 'item'
                value = 'tfc:metal/%s/%s' % (metal_block, metal)
            rm.data(('tfc', 'item_heats', metal, metal_block), {
                'ingredient': {
                    tag: value
                },
                "heat_capacity": metal_data.heat_capacity,
                "forging_temperature": metal_data.melt_temperature * 0.6,
                "welding_temperature": metal_data.melt_temperature * 0.8
            })

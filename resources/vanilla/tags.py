from mcresources import ResourceManager

def generate(rm: ResourceManager):
    # Add cast iron ingot tag to vanilla iron
    rm.item_tag('forge:ingots/cast_iron', 'minecraft:iron_ingot')
    # Gold ingot tag is added automagically by forge.
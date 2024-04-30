#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum, auto

from mcresources import ResourceManager, utils, loot_tables
from mcresources.type_definitions import ResourceIdentifier

from constants import *
from recipes import fluid_ingredient, heat_recipe


class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()


class Category(Enum):
    fruit = auto()
    vegetable = auto()
    grain = auto()
    bread = auto()
    dairy = auto()
    meat = auto()
    cooked_meat = auto()
    other = auto()


def generate(rm: ResourceManager):

    # === Metals ===

    for metal, metal_data in METALS.items():
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier,
            'fluid': 'tfc:metal/%s' % metal,
            'melt_temperature': metal_data.melt_temperature,
            'specific_heat_capacity': metal_data.specific_heat_capacity(),
            'ingots': utils.ingredient('#forge:ingots/%s' % metal),
            'double_ingots': utils.ingredient('#forge:double_ingots/%s' % metal) if 'part' in metal_data.types else None,
            'sheets': utils.ingredient('#forge:sheets/%s' % metal) if 'part' in metal_data.types else None
        })

    # === Item Heats ===

    wrought_iron = METALS['wrought_iron']
    gold = METALS['gold']
    bronze = METALS['bronze']
    brass = METALS['brass']
    tin = METALS['tin']

    item_heat(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', wrought_iron.ingot_heat_capacity(), wrought_iron.melt_temperature, mb=400)  # made from one double sheet
    item_heat(rm, 'iron_door', 'minecraft:iron_door', wrought_iron.ingot_heat_capacity(), wrought_iron.melt_temperature, mb=200)
    item_heat(rm, 'gold_bell', 'minecraft:bell', gold.ingot_heat_capacity(), gold.melt_temperature, mb=100)
    item_heat(rm, 'bronze_bell', 'tfc:bronze_bell', bronze.ingot_heat_capacity(), bronze.melt_temperature, mb=100)
    item_heat(rm, 'brass_bell', 'tfc:brass_bell', brass.ingot_heat_capacity(), brass.melt_temperature, mb=100)
    item_heat(rm, 'jacks', 'tfc:jacks', brass.ingot_heat_capacity(), brass.melt_temperature, mb=100)
    item_heat(rm, 'gem_saw', 'tfc:gem_saw', brass.ingot_heat_capacity(), brass.melt_temperature, mb=50)
    item_heat(rm, 'jar_lid', 'tfc:jar_lid', tin.ingot_heat_capacity(), tin.melt_temperature, mb=12)
    item_heat(rm, 'blowpipe_with_glass', '#tfc:glass_blowpipes', 0.7)
    item_heat(rm, 'stick', '#forge:rods/wooden', 2.5)  # Includes twigs
    item_heat(rm, 'stick_bunch', 'tfc:stick_bunch', 20.0)  # < ~9 x sticks
    item_heat(rm, 'unfired_brick', 'tfc:ceramic/unfired_brick', 0.4)
    item_heat(rm, 'unfired_fire_brick', 'tfc:ceramic/unfired_fire_brick', 1.2)
    item_heat(rm, 'unfired_flower_pot', 'tfc:ceramic/unfired_flower_pot', 0.6)
    item_heat(rm, 'unfired_jug', 'tfc:ceramic/unfired_jug', 0.8)
    item_heat(rm, 'unfired_pan', 'tfc:ceramic/unfired_pan', 0.6)
    item_heat(rm, 'unfired_bowl', 'tfc:ceramic/unfired_bowl', 0.4)
    item_heat(rm, 'unfired_pot', 'tfc:ceramic/unfired_pot', 0.8)
    item_heat(rm, 'unfired_spindle_head', 'tfc:ceramic/unfired_spindle_head', 0.8)
    item_heat(rm, 'unfired_crucible', 'tfc:ceramic/unfired_crucible', 2.5)
    item_heat(rm, 'unfired_blowpipe', 'tfc:ceramic/unfired_blowpipe', 0.6)
    item_heat(rm, 'unfired_vessels', '#tfc:unfired_vessels', 1.0)
    item_heat(rm, 'unfired_large_vessels', '#tfc:unfired_large_vessels', 1.5)
    item_heat(rm, 'unfired_molds', '#tfc:unfired_molds', 1.0)
    item_heat(rm, 'clay_block', 'minecraft:clay', 0.5)
    item_heat(rm, 'kaolin_clay', 'tfc:kaolin_clay', 2.0)
    item_heat(rm, 'terracotta', ['minecraft:terracotta', *['minecraft:%s_terracotta' % color for color in COLORS]], 0.5)
    item_heat(rm, 'dough', '#tfc:foods/dough', 1.0, destroy_at=700)
    item_heat(rm, 'bread', '#tfc:foods/breads', 1.0, destroy_at=700)
    item_heat(rm, 'meat', '#tfc:foods/meats', 1.0, destroy_at=900)
    item_heat(rm, 'seaweed', 'tfc:food/fresh_seaweed', 1.0, destroy_at=700)
    item_heat(rm, 'dried_seaweed', 'tfc:food/dried_seaweed', 1.0, destroy_at=700)
    item_heat(rm, 'potato', 'tfc:food/potato', 1.0, destroy_at=700)
    item_heat(rm, 'giant_kelp_flower', 'tfc:plant/giant_kelp_flower', 1.0)
    item_heat(rm, 'dried_kelp', 'tfc:food/dried_kelp', 1.0)
    item_heat(rm, 'egg', 'minecraft:egg', 1.0, destroy_at=1050)
    item_heat(rm, 'blooms', '#tfc:blooms', wrought_iron.ingot_heat_capacity(), wrought_iron.melt_temperature, mb=100)
    item_heat(rm, 'flux', 'tfc:powder/flux', 0.7)

    for metal, metal_data in METALS.items():
        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            item_name = 'tfc:metal/block/%s_%s' % (metal, item.replace('block_', '')) if 'block_' in item else 'tfc:metal/%s/%s' % (item, metal)
            if item_data.type in metal_data.types or item_data.type == 'all':
                item_heat(rm, 'metal/%s_%s' % (metal, item), '#%s/%s' % (item_data.tag, metal) if item_data.tag else item_name, metal_data.ingot_heat_capacity(), metal_data.melt_temperature, mb=item_data.smelt_amount)

    for ore, ore_data in ORES.items():
        if ore_data.metal and ore_data.graded:
            metal_data = METALS[ore_data.metal]
            item_heat(rm, ('ore', ore), ['tfc:ore/small_%s' % ore, 'tfc:ore/normal_%s' % ore, 'tfc:ore/poor_%s' % ore, 'tfc:ore/rich_%s' % ore], metal_data.ingot_heat_capacity(), int(metal_data.melt_temperature), mb=40)  # Average at 40 mB / ore piece - consolidating does reduce net heat capacity, but not overly so, and less so for higher richness ores.

    # === Supports ===

    rm.data(('tfc', 'supports', 'horizontal_support_beam'), {
        'ingredient': ['tfc:wood/horizontal_support/%s' % wood for wood in WOODS],
        'support_up': 2,
        'support_down': 2,
        'support_horizontal': 4
    })

    # Fuels

    for wood, wood_data in WOODS.items():
        fuel_item(rm, wood + '_log', ['tfc:wood/log/' + wood, 'tfc:wood/wood/' + wood, 'tfc:wood/stripped_wood/' + wood, 'tfc:wood/stripped_log/' + wood], wood_data.duration, wood_data.temp, 0.6 if wood == 'pine' else 0.95)

    fuel_item(rm, 'coal', ['minecraft:coal', 'tfc:ore/bituminous_coal'], 2200, 1415)
    fuel_item(rm, 'lignite', 'tfc:ore/lignite', 2200, 1350)
    fuel_item(rm, 'charcoal', 'minecraft:charcoal', 1800, 1350)
    fuel_item(rm, 'peat', 'tfc:peat', 2500, 600, 0.7)
    fuel_item(rm, 'stick_bundle', 'tfc:stick_bundle', 600, 900, 0.8)
    fuel_item(rm, 'driftwood', 'tfc:groundcover/driftwood', 400, 650, 0.4)
    fuel_item(rm, 'pinecone', 'tfc:groundcover/pinecone', 220, 150, 0.15)  # very impure, very low temperature
    fuel_item(rm, 'paper', ['minecraft:paper', 'minecraft:book', 'minecraft:enchanted_book', 'minecraft:written_book', 'minecraft:writable_book'], 150, 199, 0.7)
    fuel_item(rm, 'fallen_leaves', '#tfc:fallen_leaves', 600, 100, 0.25)

    for rock in ROCKS.keys():
        for ore in ORE_DEPOSITS:
            panning(rm, 'deposits/%s_%s' % (ore, rock), 'tfc:deposit/%s/%s' % (ore, rock), ['tfc:item/pan/%s/%s_full' % (ore, rock), 'tfc:item/pan/%s/%s_half' % (ore, rock), 'tfc:item/pan/%s/result' % ore], 'tfc:panning/deposits/%s_%s' % (ore, rock))
            sluicing(rm, 'deposits/%s_%s' % (ore, rock), 'tfc:deposit/%s/%s' % (ore, rock), 'tfc:panning/deposits/%s_%s' % (ore, rock))


    # Item Sizes

    item_size(rm, 'logs', '#minecraft:logs', Size.very_large, Weight.medium)
    item_size(rm, 'quern', 'tfc:quern', Size.very_large, Weight.very_heavy)
    item_size(rm, 'tool_racks', '#tfc:tool_racks', Size.large, Weight.very_heavy)
    item_size(rm, 'chests', '#forge:chests', Size.large, Weight.light)
    item_size(rm, 'tables', ['#tfc:scribing_tables', '#tfc:sewing_tables', 'minecraft:loom'], Size.large, Weight.light)
    item_size(rm, 'slabs', '#minecraft:slabs', Size.small, Weight.very_light)
    item_size(rm, 'vessels', '#tfc:vessels', Size.normal, Weight.heavy)
    item_size(rm, 'large_vessels', '#tfc:large_vessels', Size.huge, Weight.heavy)
    item_size(rm, 'molds', '#tfc:molds', Size.normal, Weight.medium)
    item_size(rm, 'doors', '#minecraft:doors', Size.very_large, Weight.heavy)
    item_size(rm, 'mortar', '#tfc:mortar', Size.tiny, Weight.very_light)
    item_size(rm, 'stick_bunch', 'tfc:stick_bunch', Size.normal, Weight.light)
    item_size(rm, 'stick_bundle', 'tfc:stick_bundle', Size.very_large, Weight.medium)
    item_size(rm, 'jute_fiber', 'tfc:jute_fiber', Size.small, Weight.very_light)
    item_size(rm, 'burlap_cloth', 'tfc:burlap_cloth', Size.small, Weight.very_light)
    item_size(rm, 'straw', 'tfc:straw', Size.small, Weight.very_light)
    item_size(rm, 'wool', 'tfc:wool', Size.small, Weight.light)
    item_size(rm, 'wool_cloth', 'tfc:wool_cloth', Size.small, Weight.light)
    item_size(rm, 'silk_cloth', 'tfc:silk_cloth', Size.small, Weight.light)
    item_size(rm, 'alabaster_brick', 'tfc:alabaster_brick', Size.small, Weight.light)
    item_size(rm, 'glue', 'tfc:glue', Size.tiny, Weight.light)
    item_size(rm, 'brass_mechanisms', 'tfc:brass_mechanisms', Size.normal, Weight.light)
    item_size(rm, 'wrought_iron_grill', 'tfc:wrought_iron_grill', Size.large, Weight.heavy)
    item_size(rm, 'dyes', '#tfc:dyes', Size.tiny, Weight.light)
    item_size(rm, 'foods', '#tfc:foods', Size.small, Weight.light)
    item_size(rm, 'plants', '#tfc:plants', Size.tiny, Weight.very_light)
    item_size(rm, 'jute', 'tfc:jute', Size.small, Weight.very_light)
    item_size(rm, 'bloomery', 'tfc:bloomery', Size.large, Weight.very_heavy)
    item_size(rm, 'sluice', '#tfc:sluices', Size.very_large, Weight.very_heavy)
    item_size(rm, 'lamps', '#tfc:lamps', Size.normal, Weight.medium)
    item_size(rm, 'signs', '#minecraft:signs', Size.very_small, Weight.heavy)
    item_size(rm, 'hanging_signs', '#minecraft:hanging_signs', Size.very_small, Weight.heavy)
    item_size(rm, 'soups', '#tfc:soups', Size.very_small, Weight.medium)
    item_size(rm, 'redstone', '#forge:dusts/redstone', Size.very_small, Weight.very_light)
    item_size(rm, 'salads', '#tfc:salads', Size.very_small, Weight.medium)
    item_size(rm, 'buckets', '#tfc:buckets', Size.large, Weight.medium)
    item_size(rm, 'anvils', '#tfc:anvils', Size.huge, Weight.very_heavy)
    item_size(rm, 'minecarts', '#tfc:minecarts', Size.very_large, Weight.heavy)
    item_size(rm, 'boats', '#minecraft:boats', Size.very_large, Weight.heavy)
    item_size(rm, 'looms', '#tfc:looms', Size.large, Weight.very_heavy)
    item_size(rm, 'ingots', '#forge:ingots', Size.large, Weight.medium)
    item_size(rm, 'double_ingots', '#forge:double_ingots', Size.large, Weight.heavy)
    item_size(rm, 'sheets', '#forge:sheets', Size.large, Weight.medium)
    item_size(rm, 'double_sheets', '#forge:double_sheets', Size.large, Weight.heavy)
    item_size(rm, 'rods', '#forge:rods', Size.normal, Weight.light)
    item_size(rm, 'tuyeres', '#tfc:tuyeres', Size.large, Weight.heavy)
    item_size(rm, 'trapdoors', '#tfc:trapdoors', Size.large, Weight.heavy)
    item_size(rm, 'small_tools', ['#tfc:chisels', '#tfc:knives', '#tfc:shears'], Size.large, Weight.medium)
    item_size(rm, 'large_tools', ['#forge:fishing_rods', '#tfc:pickaxes', '#tfc:propicks', '#tfc:axes', '#tfc:shovels', '#tfc:hoes', '#tfc:hammers', '#tfc:saws', '#tfc:javelins', '#tfc:swords', '#tfc:maces', '#tfc:scythes', '#tfc:shields', '#tfc:glassworking_tools', '#tfc:all_blowpipes'], Size.very_large, Weight.very_heavy)
    item_size(rm, 'ore_pieces', '#tfc:ore_pieces', Size.small, Weight.medium)
    item_size(rm, 'small_ore_pieces', '#tfc:small_ore_pieces', Size.small, Weight.light)
    item_size(rm, 'jars', '#tfc:jars', Size.very_large, Weight.heavy)
    item_size(rm, 'empty_jar', ['tfc:empty_jar', 'tfc:empty_jar_with_lid'], Size.tiny, Weight.medium)
    item_size(rm, 'glass_bottles', '#tfc:glass_bottles', Size.large, Weight.heavy)
    item_size(rm, 'windmill_blades', '#tfc:windmill_blades', Size.very_large, Weight.very_heavy)
    item_size(rm, 'rustic_windmill_blade', 'tfc:rustic_windmill_blade', Size.very_large, Weight.very_heavy)
    item_size(rm, 'lattice_windmill_blade', 'tfc:lattice_windmill_blade', Size.very_large, Weight.very_heavy)
    item_size(rm, 'water_wheels', '#tfc:water_wheels', Size.very_large, Weight.very_heavy)

    # Food

    food_item(rm, 'banana', 'tfc:food/banana', Category.fruit, 4, 0.2, 0, 2, fruit=1)
    food_item(rm, 'blackberry', 'tfc:food/blackberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=0.75)
    food_item(rm, 'blueberry', 'tfc:food/blueberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=0.75)
    food_item(rm, 'bunchberry', 'tfc:food/bunchberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'cherry', 'tfc:food/cherry', Category.fruit, 4, 0.2, 5, 4, fruit=1)
    food_item(rm, 'cloudberry', 'tfc:food/cloudberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'cranberry', 'tfc:food/cranberry', Category.fruit, 4, 0.2, 5, 1.8, fruit=1)
    food_item(rm, 'elderberry', 'tfc:food/elderberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'gooseberry', 'tfc:food/gooseberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'green_apple', 'tfc:food/green_apple', Category.fruit, 4, 0.5, 0, 2.5, fruit=1)
    food_item(rm, 'lemon', 'tfc:food/lemon', Category.fruit, 4, 0.2, 5, 2, fruit=0.75)
    food_item(rm, 'olive', 'tfc:food/olive', Category.fruit, 4, 0.2, 0, 1.6, fruit=1)
    food_item(rm, 'orange', 'tfc:food/orange', Category.fruit, 4, 0.5, 10, 2.2, fruit=0.5)
    food_item(rm, 'peach', 'tfc:food/peach', Category.fruit, 4, 0.5, 10, 2.8, fruit=0.5)
    food_item(rm, 'plum', 'tfc:food/plum', Category.fruit, 4, 0.5, 5, 2.8, fruit=0.75)
    food_item(rm, 'raspberry', 'tfc:food/raspberry', Category.fruit, 4, 0.5, 5, 4.9, fruit=0.75)
    food_item(rm, 'red_apple', 'tfc:food/red_apple', Category.fruit, 4, 0.5, 0, 1.7, fruit=1)
    food_item(rm, 'snowberry', 'tfc:food/snowberry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'strawberry', 'tfc:food/strawberry', Category.fruit, 4, 0.5, 10, 4.9, fruit=0.5)
    food_item(rm, 'wintergreen_berry', 'tfc:food/wintergreen_berry', Category.fruit, 4, 0.2, 5, 4.9, fruit=1)
    food_item(rm, 'barley', 'tfc:food/barley', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'barley_grain', 'tfc:food/barley_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'barley_flour', 'tfc:food/barley_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'barley_dough', 'tfc:food/barley_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'barley_bread', 'tfc:food/barley_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    dynamic_food_item(rm, 'barley_sandwich', 'tfc:food/barley_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'barley_jam_sandwich', 'tfc:food/barley_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'maize', 'tfc:food/maize', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'maize_grain', 'tfc:food/maize_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'maize_flour', 'tfc:food/maize_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'maize_dough', 'tfc:food/maize_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'maize_bread', 'tfc:food/maize_bread', Category.bread, 4, 1, 0, 1, grain=1)
    dynamic_food_item(rm, 'maize_sandwich', 'tfc:food/maize_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'maize_jam_sandwich', 'tfc:food/maize_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'oat', 'tfc:food/oat', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'oat_grain', 'tfc:food/oat_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'oat_flour', 'tfc:food/oat_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'oat_dough', 'tfc:food/oat_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'oat_bread', 'tfc:food/oat_bread', Category.bread, 4, 1, 0, 1, grain=1)
    dynamic_food_item(rm, 'oat_sandwich', 'tfc:food/oat_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'oat_jam_sandwich', 'tfc:food/oat_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'rice', 'tfc:food/rice', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'rice_grain', 'tfc:food/rice_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'rice_flour', 'tfc:food/rice_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'rice_dough', 'tfc:food/rice_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'rice_bread', 'tfc:food/rice_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    dynamic_food_item(rm, 'rice_sandwich', 'tfc:food/rice_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'rice_jam_sandwich', 'tfc:food/rice_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'cooked_rice', 'tfc:food/cooked_rice', Category.bread, 4, 2, 5, 1, grain=1)
    food_item(rm, 'rye', 'tfc:food/rye', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'rye_grain', 'tfc:food/rye_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'rye_flour', 'tfc:food/rye_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'rye_dough', 'tfc:food/rye_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'rye_bread', 'tfc:food/rye_bread', Category.bread, 4, 1, 0, 1, grain=1.5)
    dynamic_food_item(rm, 'rye_sandwich', 'tfc:food/rye_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'rye_jam_sandwich', 'tfc:food/rye_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'wheat', 'tfc:food/wheat', Category.grain, 4, 0, 0, 2)
    food_item(rm, 'wheat_grain', 'tfc:food/wheat_grain', Category.grain, 4, 0.5, 0, 0.25)
    food_item(rm, 'wheat_flour', 'tfc:food/wheat_flour', Category.grain, 4, 0, 0, 0.5)
    food_item(rm, 'wheat_dough', 'tfc:food/wheat_dough', Category.grain, 4, 0, 0, 3)
    food_item(rm, 'wheat_bread', 'tfc:food/wheat_bread', Category.bread, 4, 1, 0, 1, grain=1)
    dynamic_food_item(rm, 'wheat_sandwich', 'tfc:food/wheat_bread_sandwich', 'dynamic')
    dynamic_food_item(rm, 'wheat_jam_sandwich', 'tfc:food/wheat_bread_jam_sandwich', 'dynamic')
    food_item(rm, 'beet', 'tfc:food/beet', Category.vegetable, 4, 2, 0, 0.7, veg=1)
    food_item(rm, 'cabbage', 'tfc:food/cabbage', Category.vegetable, 4, 0.5, 0, 1.2, veg=1)
    food_item(rm, 'carrot', 'tfc:food/carrot', Category.vegetable, 4, 2, 0, 0.7, veg=1)
    food_item(rm, 'garlic', 'tfc:food/garlic', Category.vegetable, 4, 0.5, 0, 0.4, veg=2)
    food_item(rm, 'green_bean', 'tfc:food/green_bean', Category.vegetable, 4, 0.5, 0, 3.5, veg=1)
    food_item(rm, 'green_bell_pepper', 'tfc:food/green_bell_pepper', Category.vegetable, 4, 0.5, 0, 2.7, veg=0.75)
    food_item(rm, 'onion', 'tfc:food/onion', Category.vegetable, 4, 0.5, 0, 0.5, veg=1)
    food_item(rm, 'potato', 'tfc:food/potato', Category.vegetable, 4, 0.5, 0, 0.666, veg=1.0)
    food_item(rm, 'baked_potato', 'tfc:food/baked_potato', Category.vegetable, 4, 2, 0, 1.0, veg=1.5)
    food_item(rm, 'red_bell_pepper', 'tfc:food/red_bell_pepper', Category.vegetable, 4, 1, 0, 2.5, veg=1)
    food_item(rm, 'dried_seaweed', 'tfc:food/dried_seaweed', Category.vegetable, 2, 1, 0, 2.0, veg=0.5)
    food_item(rm, 'fresh_seaweed', 'tfc:food/fresh_seaweed', Category.other, 2, 1, 0, 2.5, veg=0.25)
    food_item(rm, 'dried_kelp', 'tfc:food/dried_kelp', Category.vegetable, 2, 1, 0, 2.5, veg=0.5)
    food_item(rm, 'cattail_root', 'tfc:food/cattail_root', Category.vegetable, 2, 1, 0, 2.5, grain=0.5)
    food_item(rm, 'taro_root', 'tfc:food/taro_root', Category.vegetable, 2, 1, 0, 2.5, grain=0.5)
    food_item(rm, 'soybean', 'tfc:food/soybean', Category.vegetable, 4, 2, 0, 2.5, veg=0.5, protein=1)
    food_item(rm, 'squash', 'tfc:food/squash', Category.vegetable, 4, 1, 0, 1.67, veg=1.5)
    food_item(rm, 'sugarcane', 'tfc:food/sugarcane', Category.vegetable, 4, 0, 0, 0.5)
    food_item(rm, 'tomato', 'tfc:food/tomato', Category.vegetable, 4, 0.5, 5, 3.5, veg=1.5)
    food_item(rm, 'yellow_bell_pepper', 'tfc:food/yellow_bell_pepper', Category.vegetable, 4, 1, 0, 2.5, veg=1)
    food_item(rm, 'pumpkin', 'tfc:pumpkin', Category.other, 4, 0, 0, 0.5)
    food_item(rm, 'melon', 'tfc:melon', Category.other, 4, 0, 0, 0.5)
    food_item(rm, 'melon_slice', 'tfc:food/melon_slice', Category.fruit, 4, 0.2, 5, 2.5, fruit=0.75)
    food_item(rm, 'pumpkin_pie', 'minecraft:pumpkin_pie', Category.other, 4, 2, 5, 2.5, fruit=1.5, grain=1)
    food_item(rm, 'pumpkin_chunks', 'tfc:food/pumpkin_chunks', Category.fruit, 4, 1, 5, 1.5, fruit=0.75)
    food_item(rm, 'cheese', 'tfc:food/cheese', Category.dairy, 4, 2, 0, 0.3, dairy=3)
    food_item(rm, 'cooked_egg', 'tfc:food/cooked_egg', Category.other, 4, 0.5, 0, 4, protein=1.5, dairy=0.25)
    food_item(rm, 'boiled_egg', 'tfc:food/boiled_egg', Category.other, 4, 2, 10, 4, protein=1.5, dairy=0.25)
    food_item(rm, 'beef', 'tfc:food/beef', Category.meat, 4, 0, 0, 2, protein=2)
    food_item(rm, 'pork', 'tfc:food/pork', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'chicken', 'tfc:food/chicken', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'mutton', 'tfc:food/mutton', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'bluegill', 'tfc:food/bluegill', Category.meat, 4, 0, 0, 3, protein=0.75)
    food_item(rm, 'rainbow_trout', 'tfc:food/rainbow_trout', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'lake_trout', 'tfc:food/lake_trout', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'largemouth_bass', 'tfc:food/largemouth_bass', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'smallmouth_bass', 'tfc:food/smallmouth_bass', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'crappie', 'tfc:food/crappie', Category.meat, 4, 0, 0, 3, protein=0.75)
    food_item(rm, 'salmon', 'tfc:food/salmon', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'shellfish', 'tfc:food/shellfish', Category.meat, 2, 0, 0, 2, protein=0.5)
    food_item(rm, 'cod', 'tfc:food/cod', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'tropical_fish', 'tfc:food/tropical_fish', Category.meat, 4, 0, 0, 3, protein=1)
    food_item(rm, 'bear', 'tfc:food/bear', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'calamari', 'tfc:food/calamari', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'horse_meat', 'tfc:food/horse_meat', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'frog_legs', 'tfc:food/frog_legs', Category.meat, 4, 0, 0, 2, protein=1)
    food_item(rm, 'turtle', 'tfc:food/turtle', Category.meat, 4, 0, 0, 2, protein=1.5)
    food_item(rm, 'pheasant', 'tfc:food/pheasant', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'grouse', 'tfc:food/grouse', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'turkey', 'tfc:food/turkey', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'peafowl', 'tfc:food/peafowl', Category.meat, 4, 0, 0, 3, protein=1.5)
    food_item(rm, 'venison', 'tfc:food/venison', Category.meat, 4, 0, 0, 2, protein=1)
    food_item(rm, 'wolf', 'tfc:food/wolf', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'rabbit', 'tfc:food/rabbit', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'fox', 'tfc:food/fox', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'hyena', 'tfc:food/hyena', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'duck', 'tfc:food/duck', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'quail', 'tfc:food/quail', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'chevon', 'tfc:food/chevon', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'gran_feline', 'tfc:food/gran_feline', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'camelidae', 'tfc:food/camelidae', Category.meat, 4, 0, 0, 3, protein=0.5)
    food_item(rm, 'cooked_beef', 'tfc:food/cooked_beef', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_pork', 'tfc:food/cooked_pork', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_chicken', 'tfc:food/cooked_chicken', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_mutton', 'tfc:food/cooked_mutton', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_shellfish', 'tfc:food/cooked_shellfish', Category.cooked_meat, 2, 2, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_cod', 'tfc:food/cooked_cod', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_tropical_fish', 'tfc:food/cooked_tropical_fish', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_bluegill', 'tfc:food/cooked_bluegill', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_rainbow_trout', 'tfc:food/cooked_rainbow_trout', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_lake_trout', 'tfc:food/cooked_lake_trout', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_largemouth_bass', 'tfc:food/cooked_largemouth_bass', Category.cooked_meat, 4, 1, 0, 2.25, protein=2.25)
    food_item(rm, 'cooked_smallmouth_bass', 'tfc:food/cooked_smallmouth_bass', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_crappie', 'tfc:food/cooked_crappie', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_salmon', 'tfc:food/cooked_salmon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_bear', 'tfc:food/cooked_bear', Category.cooked_meat, 4, 1, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_calamari', 'tfc:food/cooked_calamari', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_horse_meat', 'tfc:food/cooked_horse_meat', Category.cooked_meat, 4, 2, 0, 1.5, protein=2.5)
    food_item(rm, 'cooked_frog_legs', 'tfc:food/cooked_frog_legs', Category.cooked_meat, 4, 2, 0, 1.5, protein=2)
    food_item(rm, 'cooked_turtle', 'tfc:food/cooked_turtle', Category.cooked_meat, 4, 0, 0, 2, protein=2.5)
    food_item(rm, 'cooked_pheasant', 'tfc:food/cooked_pheasant', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_turkey', 'tfc:food/cooked_turkey', Category.cooked_meat, 4, 1, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_peafowl', 'tfc:food/cooked_peafowl', Category.cooked_meat, 4, 1, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_grouse', 'tfc:food/cooked_grouse', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_venison', 'tfc:food/cooked_venison', Category.cooked_meat, 4, 1, 0, 1.5, protein=2)
    food_item(rm, 'cooked_wolf', 'tfc:food/cooked_wolf', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_rabbit', 'tfc:food/cooked_rabbit', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_fox', 'tfc:food/cooked_fox', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_hyena', 'tfc:food/cooked_hyena', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_duck', 'tfc:food/cooked_duck', Category.cooked_meat, 4, 1, 0, 2.25, protein=1.5)
    food_item(rm, 'cooked_quail', 'tfc:food/cooked_quail', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_chevon', 'tfc:food/cooked_chevon', Category.cooked_meat, 4, 1, 0, 2.25, protein=2)
    food_item(rm, 'cooked_gran_feline', 'tfc:food/cooked_gran_feline', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'cooked_camelidae', 'tfc:food/cooked_camelidae', Category.cooked_meat, 4, 2, 0, 2.25, protein=2.5)
    food_item(rm, 'jars', '#tfc:foods/sealed_preserves', Category.other, 0, 0, 0, 0.1)
    food_item(rm, 'open_jars', '#tfc:foods/preserves', Category.other, 0, 0, 0, 5, fruit=0.75)

    for nutrient in NUTRIENTS:
        dynamic_food_item(rm, '%s_soup' % nutrient, 'tfc:food/%s_soup' % nutrient, 'dynamic_bowl')
        dynamic_food_item(rm, '%s_salad' % nutrient, 'tfc:food/%s_salad' % nutrient, 'dynamic_bowl')

    # Always Rotten
    food_item(rm, 'rotten_flesh', 'minecraft:rotten_flesh', Category.other, 0, 0, 0, 99999)

    # Drinkables

    drinkable(rm, 'fresh_water', ['minecraft:water', 'tfc:river_water'], thirst=10)
    drinkable(rm, 'salt_water', 'tfc:salt_water', thirst=-1, effects=[{'type': 'tfc:thirst', 'duration': 600, 'chance': 0.25}], allow_full=True)
    drinkable(rm, 'alcohol', '#tfc:alcohols', thirst=10, intoxication=4000, allow_full=True)
    drinkable(rm, 'milk', '#tfc:milks', thirst=10, food={'hunger': 0, 'saturation': 0, 'dairy': 1.0})
    drinkable(rm, 'vinegar', 'tfc:vinegar', thirst=10, effects=[{'type': 'minecraft:nausea', 'duration': 40, 'chance': 0.8}])

    # Damage Types
    damage_type(rm, 'grill', exhaustion=0.1, effects='burning')
    damage_type(rm, 'pot', exhaustion=0.1, effects='burning')
    damage_type(rm, 'dehydration')
    damage_type(rm, 'coral', exhaustion=0.1)
    damage_type(rm, 'pluck')

    rm.tag('minecraft:bypasses_armor', 'damage_type', 'dehydration', 'pluck')
    rm.tag('minecraft:bypasses_effects', 'damage_type', 'dehydration', 'pluck')
    rm.tag('minecraft:is_fire', 'damage_type', 'grill', 'pot')
    rm.tag('bypasses_damage_resistance', 'damage_type', '#minecraft:bypasses_armor', '#minecraft:bypasses_invulnerability')
    rm.tag('is_piercing', 'damage_type', 'minecraft:cactus', 'minecraft:falling_stalactite', 'minecraft:thorns', 'minecraft:trident', 'minecraft:arrow', 'minecraft:sting')
    rm.tag('is_crushing', 'damage_type', 'minecraft:falling_block', 'minecraft:falling_anvil')
    rm.tag('is_slashing', 'damage_type')

    # Painting Variants
    rm.tag('minecraft:placeable', 'painting_variant', *['tfc:%s' % p for p in PAINTINGS])

    # Climate Ranges

    for berry, data in BERRIES.items():
        climate_range(rm, 'plant/%s_bush' % berry, temperature=(data.min_temp, data.max_temp, 0), hydration=(hydration_from_rainfall(data.min_rain), 100, 0))

    for fruit, data in FRUITS.items():
        climate_range(rm, 'plant/%s_tree' % fruit, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp - 7, data.max_temp + 7, 0))

    for crop, data in CROPS.items():
        climate_range(rm, 'crop/%s' % crop, hydration=(data.min_hydration, data.max_hydration, 0), temperature=(data.min_temp, data.max_temp, 5))

    # Fertilizer
    fertilizer(rm, 'sylvite', 'tfc:powder/sylvite', k=0.5)
    fertilizer(rm, 'wood_ash', 'tfc:powder/wood_ash', p=0.1, k=0.3)
    fertilizer(rm, 'guano', 'tfc:groundcover/guano', n=0.8, p=0.5, k=0.1)
    fertilizer(rm, 'saltpeter', 'tfc:powder/saltpeter', n=0.1, k=0.4)
    fertilizer(rm, 'bone_meal', 'minecraft:bone_meal', p=0.1)
    fertilizer(rm, 'compost', 'tfc:compost', n=0.4, p=0.2, k=0.4)
    fertilizer(rm, 'pure_nitrogen', 'tfc:pure_nitrogen', n=0.1)
    fertilizer(rm, 'pure_phosphorus', 'tfc:pure_phosphorus', p=0.1)
    fertilizer(rm, 'pure_potassium', 'tfc:pure_potassium', k=0.1)

    # Entities
    rm.data(('tfc', 'fauna', 'isopod'), fauna(distance_below_sea_level=20, climate=climate_config(max_temp=14)))
    rm.data(('tfc', 'fauna', 'crayfish'), fauna(distance_below_sea_level=2, climate=climate_config(min_temp=5, min_rain=125)))
    rm.data(('tfc', 'fauna', 'lobster'), fauna(distance_below_sea_level=1, climate=climate_config(max_temp=21)))
    rm.data(('tfc', 'fauna', 'horseshoe_crab'), fauna(distance_below_sea_level=1, climate=climate_config(min_temp=10, max_temp=21, max_rain=400)))
    rm.data(('tfc', 'fauna', 'cod'), fauna(climate=climate_config(max_temp=18), distance_below_sea_level=5))
    rm.data(('tfc', 'fauna', 'pufferfish'), fauna(climate=climate_config(min_temp=10), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'tropical_fish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'jellyfish'), fauna(climate=climate_config(min_temp=18), distance_below_sea_level=3))
    rm.data(('tfc', 'fauna', 'orca'), fauna(distance_below_sea_level=6, climate=climate_config(max_temp=19, min_rain=100), chance=10))
    rm.data(('tfc', 'fauna', 'dolphin'), fauna(distance_below_sea_level=6, climate=climate_config(min_temp=10, min_rain=200), chance=10))
    rm.data(('tfc', 'fauna', 'manatee'), fauna(distance_below_sea_level=3, climate=climate_config(min_temp=20, min_rain=300), chance=10))
    rm.data(('tfc', 'fauna', 'crocodile'), fauna(climate=climate_config(min_temp=15), distance_below_sea_level=0))
    rm.data(('tfc', 'fauna', 'bluegill'), fauna(climate=climate_config(min_temp=-10, max_temp=26)))
    rm.data(('tfc', 'fauna', 'crappie'), fauna(climate=climate_config(min_temp=-10, max_temp=26)))
    rm.data(('tfc', 'fauna', 'lake_trout'), fauna(climate=climate_config(max_temp=23, min_rain=250)))
    rm.data(('tfc', 'fauna', 'rainbow_trout'), fauna(climate=climate_config(max_temp=10, min_rain=150)))
    rm.data(('tfc', 'fauna', 'largemouth_bass'), fauna(climate=climate_config(max_temp=20, min_temp=-14, min_rain=100, max_rain=400)))
    rm.data(('tfc', 'fauna', 'smallmouth_bass'), fauna(climate=climate_config(max_temp=20, min_temp=-14, min_rain=100, max_rain=400)))
    rm.data(('tfc', 'fauna', 'salmon'), fauna(climate=climate_config(min_temp=-5)))
    rm.data(('tfc', 'fauna', 'penguin'), fauna(climate=climate_config(max_temp=-14, min_rain=75)))
    rm.data(('tfc', 'fauna', 'frog'), fauna(climate=climate_config(min_rain=150, min_temp=-13)))
    rm.data(('tfc', 'fauna', 'turtle'), fauna(climate=climate_config(min_temp=21, min_rain=250)))
    rm.data(('tfc', 'fauna', 'polar_bear'), fauna(climate=climate_config(max_temp=-10, min_rain=100)))
    rm.data(('tfc', 'fauna', 'grizzly_bear'), fauna(climate=climate_config(min_forest='edge', max_temp=15, min_temp=-15, min_rain=200)))
    rm.data(('tfc', 'fauna', 'black_bear'), fauna(climate=climate_config(min_forest='edge', max_temp=20, min_temp=5, min_rain=250)))
    rm.data(('tfc', 'fauna', 'cougar'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'panther'), fauna(climate=climate_config(min_temp=-10, max_temp=21, min_rain=150)))
    rm.data(('tfc', 'fauna', 'lion'), fauna(climate=climate_config(max_forest='edge', min_temp=16, min_rain=50, max_rain=300)))
    rm.data(('tfc', 'fauna', 'sabertooth'), fauna(climate=climate_config(max_temp=0, min_rain=250)))
    rm.data(('tfc', 'fauna', 'tiger'), fauna(climate=climate_config(min_temp=13, min_rain=100, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'squid'), fauna(distance_below_sea_level=15))
    rm.data(('tfc', 'fauna', 'octopoteuthis'), fauna(max_brightness=0, distance_below_sea_level=33))
    rm.data(('tfc', 'fauna', 'pig'), fauna(climate=climate_config(min_temp=-10, max_temp=35, min_rain=200, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'cow'), fauna(climate=climate_config(min_temp=-10, max_temp=35, min_rain=250)))
    rm.data(('tfc', 'fauna', 'goat'), fauna(climate=climate_config(min_temp=-12, max_temp=25, max_rain=300)))
    rm.data(('tfc', 'fauna', 'yak'), fauna(climate=climate_config(max_temp=-11, min_rain=100)))
    rm.data(('tfc', 'fauna', 'alpaca'), fauna(climate=climate_config(min_temp=-8, max_temp=20, min_rain=250)))
    rm.data(('tfc', 'fauna', 'sheep'), fauna(climate=climate_config(min_temp=0, max_temp=35, min_rain=70, max_rain=300)))
    rm.data(('tfc', 'fauna', 'musk_ox'), fauna(climate=climate_config(min_temp=-25, max_temp=0, min_rain=100)))
    rm.data(('tfc', 'fauna', 'chicken'), fauna(climate=climate_config(min_temp=14, min_rain=225, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'duck'), fauna(climate=climate_config(min_temp=-25, max_temp=30, min_rain=100, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'quail'), fauna(climate=climate_config(min_temp=-15, max_temp=10, min_rain=200)))
    rm.data(('tfc', 'fauna', 'rabbit'), fauna(climate=climate_config(min_rain=15)))
    rm.data(('tfc', 'fauna', 'fox'), fauna(climate=climate_config(min_rain=130, max_rain=400, max_temp=25, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'panda'), fauna(climate=climate_config(min_temp=18, max_temp=28, min_rain=300, max_rain=500, min_forest='normal', fuzzy=True)))
    rm.data(('tfc', 'fauna', 'boar'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-5, max_temp=25, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'wildebeest'), fauna(climate=climate_config(min_rain=90, max_rain=380, min_temp=13, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'ocelot'), fauna(climate=climate_config(min_rain=300, max_rain=500, min_temp=15, max_temp=30, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'caribou'), fauna(climate=climate_config(min_rain=110, max_rain=500, max_temp=-9)))
    rm.data(('tfc', 'fauna', 'deer'), fauna(climate=climate_config(min_rain=160, max_rain=500, min_temp=-12, max_temp=16, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'gazelle'), fauna(climate=climate_config(min_rain=90, max_rain=380, min_temp=12, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'bongo'), fauna(climate=climate_config(min_rain=230, max_rain=500, min_temp=15, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'moose'), fauna(climate=climate_config(min_rain=150, max_rain=300, min_temp=-15, max_temp=10, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'grouse'), fauna(climate=climate_config(min_rain=150, max_rain=400, min_temp=-12, max_temp=13)))
    rm.data(('tfc', 'fauna', 'pheasant'), fauna(climate=climate_config(min_rain=100, max_rain=300, min_temp=-5, max_temp=17, min_forest='edge')))
    rm.data(('tfc', 'fauna', 'turkey'), fauna(climate=climate_config(min_rain=250, max_rain=450, min_temp=0, max_temp=17, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'peafowl'), fauna(climate=climate_config(min_rain=190, max_rain=500, min_temp=14, min_forest='normal')))
    rm.data(('tfc', 'fauna', 'wolf'), fauna(climate=climate_config(min_rain=150, max_rain=420, min_temp=-12, max_temp=17, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'hyena'), fauna(climate=climate_config(min_rain=80, max_rain=380, min_temp=15, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'direwolf'), fauna(climate=climate_config(min_rain=150, max_rain=420, max_temp=-5, max_forest='normal')))
    rm.data(('tfc', 'fauna', 'donkey'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'mule'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))
    rm.data(('tfc', 'fauna', 'horse'), fauna(climate=climate_config(min_rain=130, max_rain=400, min_temp=-15, max_forest='edge')))

    # Lamp Fuel - burn rate = ticks / mB. 8000 ticks @ 250mB ~ 83 days ~ the 1.12 length of olive oil burning
    lamp_fuel(rm, 'olive_oil', 'tfc:olive_oil', 8000)
    lamp_fuel(rm, 'tallow', 'tfc:tallow', 1800)
    lamp_fuel(rm, 'lava', 'minecraft:lava', -1, 'tfc:metal/lamp/blue_steel')

    # Misc Block Loot
    rm.block_loot('minecraft:hanging_roots', {'name': 'minecraft:hanging_roots', 'conditions': [loot_tables.match_tag('tfc:sharp_tools')]})

    # Damage Resistances
    entity_damage_resistance(rm, 'skeletons', 'tfc:skeletons', piercing=1000000000, crushing=-50)
    entity_damage_resistance(rm, 'creeper', 'tfc:creepers', slashing=-25, crushing=50)
    entity_damage_resistance(rm, 'zombies', 'tfc:zombies', piercing=-25, crushing=50)

    item_damage_resistance(rm, 'leather_armor', ['minecraft:leather_%s' % piece for piece in ARMOR_SECTIONS], slashing=3)
    item_damage_resistance(rm, 'chainmail_armor', ['minecraft:chainmail_%s' % piece for piece in ARMOR_SECTIONS], slashing=8, piercing=8, crushing=2)

    # Entity Loot

    for mob in ('cod', 'tropical_fish', *SIMPLE_FRESHWATER_FISH):
        mob_loot(rm, mob, 'tfc:food/%s' % mob, killed_by_player=True)
    mob_loot(rm, 'pufferfish', 'minecraft:pufferfish', killed_by_player=True)
    mob_loot(rm, 'squid', 'minecraft:ink_sac', max_amount=10, extra_pool={'name': 'tfc:food/calamari'}, killed_by_player=True)
    mob_loot(rm, 'octopoteuthis', 'minecraft:glow_ink_sac', max_amount=10, extra_pool={'name': 'tfc:food/calamari'}, killed_by_player=True)
    for mob in ('isopod', 'lobster', 'horseshoe_crab', 'crayfish'):
        mob_loot(rm, mob, 'tfc:food/shellfish', killed_by_player=True)
    for mob in ('orca', 'dolphin', 'manatee'):
        mob_loot(rm, mob, 'tfc:blubber', min_amount=2, max_amount=7, bones=5)
    mob_loot(rm, 'penguin', 'minecraft:feather', max_amount=3, hide_size='small', hide_chance=0.5, bones=2)
    mob_loot(rm, 'turtle', 'minecraft:scute', extra_pool={'name': 'tfc:food/turtle'})
    mob_loot(rm, 'polar_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'grizzly_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'black_bear', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'cougar', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'panther', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'lion', 'tfc:large_raw_hide', bones=6)
    mob_loot(rm, 'sabertooth', 'tfc:large_raw_hide', bones=8)
    mob_loot(rm, 'tiger', 'tfc:large_raw_hide', bones=7)
    mob_loot(rm, 'crocodile', 'tfc:large_raw_hide', bones=7)
    mob_loot(rm, 'wolf', 'tfc:small_raw_hide', bones=3)
    mob_loot(rm, 'hyena', 'tfc:small_raw_hide', bones=3)
    mob_loot(rm, 'direwolf', 'tfc:medium_raw_hide', bones=4)
    mob_loot(rm, 'dog', 'tfc:small_raw_hide', bones=3)
    mob_loot(rm, 'cat', 'tfc:small_raw_hide', bones=3)
    mob_loot(rm, 'pig', 'tfc:food/pork', 4, 12, 'medium', bones=3, livestock=True, not_predated=True)
    mob_loot(rm, 'cow', 'tfc:food/beef', 6, 20, 'large', bones=4, livestock=True, not_predated=True)
    mob_loot(rm, 'goat', 'tfc:food/chevon', 4, 10, 'medium', bones=4, livestock=True, extra_pool={'name': 'tfc:goat_horn', 'conditions': [{'condition': 'tfc:is_male'}]}, not_predated=True)
    mob_loot(rm, 'yak', 'tfc:food/chevon', 8, 16, 'large', bones=4, livestock=True, not_predated=True)
    mob_loot(rm, 'alpaca', 'tfc:food/camelidae', 6, 13, bones=4, extra_pool={'name': 'tfc:medium_sheepskin_hide'}, livestock=True, not_predated=True)
    mob_loot(rm, 'sheep', 'tfc:food/mutton', 4, 15, bones=4, extra_pool={'name': 'tfc:small_sheepskin_hide'}, livestock=True, not_predated=True)
    mob_loot(rm, 'musk_ox', 'tfc:food/mutton', 6, 16, bones=4, extra_pool={'name': 'tfc:large_sheepskin_hide'}, livestock=True, not_predated=True)
    mob_loot(rm, 'chicken', 'tfc:food/chicken', 2, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(4, 12)]}, livestock=True, not_predated=True)
    mob_loot(rm, 'duck', 'tfc:food/duck', 2, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(4, 10)]}, livestock=True, not_predated=True)
    mob_loot(rm, 'quail', 'tfc:food/quail', 1, 3, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(4, 12)]}, livestock=True, not_predated=True)
    mob_loot(rm, 'rabbit', 'tfc:food/rabbit', hide_size='small', hide_chance=0.5, bones=1, extra_pool={'name': 'minecraft:rabbit_foot', 'conditions': [loot_tables.random_chance(0.1)]}, not_predated=True)
    mob_loot(rm, 'fox', 'tfc:food/fox', hide_size='small', bones=1)
    mob_loot(rm, 'boar', 'tfc:food/pork', 5, 10, 'small', hide_chance=0.8, bones=3, not_predated=True)
    mob_loot(rm, 'wildebeest', 'tfc:food/beef', 8, 14, 'small', hide_chance=0.8, bones=3, not_predated=True)
    mob_loot(rm, 'bongo', 'tfc:food/venison', 6, 10, 'medium', bones=6, not_predated=True)
    mob_loot(rm, 'gazelle', 'tfc:food/venison', 3, 8, 'medium', bones=6, not_predated=True)
    mob_loot(rm, 'deer', 'tfc:food/venison', 4, 10, 'medium', bones=6, not_predated=True)
    mob_loot(rm, 'caribou', 'tfc:food/venison', 6, 11, 'medium', bones=6, not_predated=True)
    mob_loot(rm, 'moose', 'tfc:food/venison', 10, 20, 'large', bones=10, not_predated=True)
    mob_loot(rm, 'grouse', 'tfc:food/grouse', 2, 3, bones=2, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(4, 10)]}, not_predated=True)
    mob_loot(rm, 'pheasant', 'tfc:food/pheasant', 2, 3, bones=2, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(4, 10)]}, not_predated=True)
    mob_loot(rm, 'turkey', 'tfc:food/turkey', 2, 4, bones=2, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(6, 10)]}, not_predated=True)
    mob_loot(rm, 'peafowl', 'tfc:food/peafowl', 2, 4, bones=2, extra_pool={'name': 'minecraft:feather', 'functions': [loot_tables.set_count(8, 14)]}, not_predated=True)
    mob_loot(rm, 'donkey', 'tfc:food/horse_meat', 4, 18, 'medium', bones=6, livestock=True, not_predated=True)
    mob_loot(rm, 'mule', 'tfc:food/horse_meat', 4, 18, 'medium', bones=6, livestock=True, not_predated=True)
    mob_loot(rm, 'horse', 'tfc:food/horse_meat', 4, 18, 'medium', bones=6, livestock=True, not_predated=True)
    mob_loot(rm, 'frog', 'tfc:food/frog_legs', 2, 2, bones=2)
    mob_loot(rm, 'minecraft:zombie', 'minecraft:rotten_flesh', 0, 2)  # it drops vanilla stuff we do not want
    mob_loot(rm, 'minecraft:drowned', 'minecraft:rotten_flesh', 0, 2)  # it drops vanilla stuff we do not want

    trim_material(rm, 'amethyst', '#9A5CC6', 'tfc:gem/amethyst', 0)
    trim_material(rm, 'diamond', '#6EECD2', 'tfc:gem/diamond', 0.1)
    trim_material(rm, 'emerald', '#11A036', 'tfc:gem/emerald', 0.2)
    trim_material(rm, 'lapis_lazuli', '#416E97', 'tfc:gem/lapis_lazuli', 0.3)
    trim_material(rm, 'opal', '#75e7eb', 'tfc:gem/opal', 0.4)
    trim_material(rm, 'pyrite', '#e6c44c', 'tfc:gem/pyrite', 0.4)
    trim_material(rm, 'ruby', '#971607', 'tfc:gem/ruby', 0.5)
    trim_material(rm, 'sapphire', '#183dde', 'tfc:gem/sapphire', 0.6)
    trim_material(rm, 'topaz', '#c27a0e', 'tfc:gem/topaz', 0.7)
    trim_material(rm, 'silver', '#edeadf', 'tfc:metal/ingot/silver', 0.8)
    trim_material(rm, 'sterling_silver', '#ccc7b6', 'tfc:metal/ingot/sterling_silver', 0.85)
    trim_material(rm, 'gold', '#DEB12D', 'tfc:metal/ingot/gold', 0.9)
    trim_material(rm, 'rose_gold', '#fcdd86', 'tfc:metal/ingot/rose_gold', 0.95)
    trim_material(rm, 'bismuth', '#8bbbc4', 'tfc:metal/ingot/bismuth', 1)


def entity_damage_resistance(rm: ResourceManager, name_parts: ResourceIdentifier, entity_tag: str, piercing: int = 0, slashing: int = 0, crushing: int = 0):
    rm.data(('tfc', 'entity_damage_resistances', name_parts), {
        'entity': entity_tag,
        'piercing': piercing,
        'slashing': slashing,
        'crushing': crushing
    })

def item_damage_resistance(rm: ResourceManager, name_parts: ResourceIdentifier, item: utils.Json, piercing: int = 0, slashing: int = 0, crushing: int = 0):
    rm.data(('tfc', 'item_damage_resistances', name_parts), {
        'ingredient': utils.ingredient(item),
        'piercing': piercing,
        'slashing': slashing,
        'crushing': crushing
    })

def mob_loot(rm: ResourceManager, name: str, drop: str, min_amount: int = 1, max_amount: int = None, hide_size: str = None, hide_chance: float = 1, bones: int = 0, extra_pool: Dict[str, Any] = None, livestock: bool = False, not_predated: bool = False, killed_by_player: bool = False):
    func = None if max_amount is None else loot_tables.set_count(min_amount, max_amount)
    if not_predated:
        conditions = [{'condition': 'tfc:not_predated'}]
    elif killed_by_player:
        conditions = [{'condition': 'minecraft:killed_by_player'}]
    else:
        conditions = None
    pools = [{'name': drop, 'functions': func, 'conditions': conditions}]
    if livestock:
        pools = [{'name': drop, 'functions': animal_yield(min_amount, (max(1, max_amount - 3), max_amount + 3))}]
    if hide_size is not None:
        func = None if hide_chance == 1 else loot_tables.random_chance(hide_chance)
        pools.append({'name': 'tfc:%s_raw_hide' % hide_size, 'conditions': func})
    if bones != 0:
        pools.append({'name': 'minecraft:bone', 'functions': loot_tables.set_count(1, bones)})
    if extra_pool is not None:
        pools.append(extra_pool)
    rm.entity_loot(name, *pools)

def animal_yield(lo: int, hi: Tuple[int, int]) -> utils.Json:
    return {
        'function': 'minecraft:set_count',
        'count': {
            'type': 'tfc:animal_yield',
            'min': lo,
            'max': {
                'type': 'minecraft:uniform',
                'min': hi[0],
                'max': hi[1]
            }
        }
    }

def lamp_fuel(rm: ResourceManager, name: str, fluid: str, burn_rate: int, valid_lamps: str = '#tfc:lamps'):
    rm.data(('tfc', 'lamp_fuels', name), {
        'fluid': fluid,
        'burn_rate': burn_rate,
        # This is a block ingredient, not an ingredient
        'valid_lamps': {'type': 'tfc:tag', 'tag': valid_lamps.replace('#', '')} if '#' in valid_lamps else valid_lamps
    })

def fertilizer(rm: ResourceManager, name: str, ingredient: str, n: float = None, p: float = None, k: float = None):
    rm.data(('tfc', 'fertilizers', name), {
        'ingredient': utils.ingredient(ingredient),
        'nitrogen': n,
        'potassium': k,
        'phosphorus': p
    })


def climate_config(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None, min_forest: Optional[str] = None, max_forest: Optional[str] = None) -> Dict[str, Any]:
    return {
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'min_forest': 'normal' if needs_forest else min_forest,
        'max_forest': max_forest,
        'fuzzy': fuzzy
    }


def fauna(chance: int = None, distance_below_sea_level: int = None, climate: Dict[str, Any] = None, solid_ground: bool = None, max_brightness: int = None) -> Dict[str, Any]:
    return {
        'chance': chance,
        'distance_below_sea_level': distance_below_sea_level,
        'climate': climate,
        'solid_ground': solid_ground,
        'max_brightness': max_brightness
    }


def food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category, hunger: int, saturation: float, water: int, decay: float, fruit: Optional[float] = None, veg: Optional[float] = None, protein: Optional[float] = None, grain: Optional[float] = None, dairy: Optional[float] = None):
    rm.item_tag('tfc:foods', ingredient)
    rm.data(('tfc', 'food_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'hunger': hunger,
        'saturation': saturation,
        'water': water if water != 0 else None,
        'decay_modifier': decay,
        'fruit': fruit,
        'vegetables': veg,
        'protein': protein,
        'grain': grain,
        'dairy': dairy
    })
    rm.item_tag('foods', ingredient)
    if category in (Category.fruit, Category.vegetable):
        rm.item_tag('foods/%ss' % category.name.lower(), ingredient)
    if category in (Category.meat, Category.cooked_meat):
        rm.item_tag('foods/meats', ingredient)
        if category == Category.cooked_meat:
            rm.item_tag('foods/cooked_meats', ingredient)
        else:
            rm.item_tag('foods/raw_meats', ingredient)
    if category == Category.dairy:
        rm.item_tag('foods/dairy', ingredient)

def dynamic_food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, handler_type: str):
    rm.item_tag('foods', ingredient)
    rm.data(('tfc', 'food_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'type': handler_type
    })

def drinkable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, fluid: utils.Json, thirst: Optional[int] = None, intoxication: Optional[int] = None, effects: Optional[utils.Json] = None, food: Optional[utils.Json] = None, allow_full: bool = None):
    rm.data(('tfc', 'drinkables', name_parts), {
        'ingredient': fluid_ingredient(fluid),
        'thirst': thirst,
        'intoxication': intoxication,
        'effects': effects,
        'food': food,
        'may_drink_when_full': allow_full,
    })

def damage_type(rm: ResourceManager, name_parts: utils.ResourceIdentifier, message_id: str = None, exhaustion: float = 0.0, scaling: str = 'when_caused_by_living_non_player', effects: str = None, message_type: str = None):
    rm.data(('damage_type', name_parts), {
        'message_id': message_id if message_id is not None else 'tfc.' + name_parts,
        'exhaustion': exhaustion,
        'scaling': scaling,
        'effects': effects,
        'death_message_type': message_type
    })

def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })


def item_heat(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[float] = None, mb: Optional[int] = None, destroy_at: Optional[int] = None):
    if melt_temperature is not None:
        forging_temperature = round(melt_temperature * 0.6)
        welding_temperature = round(melt_temperature * 0.8)
    else:
        forging_temperature = welding_temperature = None
    if mb is not None:
        # Interpret heat capacity as a specific heat capacity - so we need to scale by the mB present. Baseline is 100 mB (an ingot)
        # Higher mB = higher heat capacity = heats and cools slower = consumes proportionally more fuel
        heat_capacity = round(10 * heat_capacity * mb) / 1000
    rm.data(('tfc', 'item_heats', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'heat_capacity': heat_capacity,
        'forging_temperature': forging_temperature,
        'welding_temperature': welding_temperature
    })
    if destroy_at is not None:
        heat_recipe(rm, 'destroy_' + name_parts, ingredient, destroy_at)


def fuel_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, duration: int, temperature: float, purity: float = None):
    rm.data(('tfc', 'fuels', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'duration': duration,
        'temperature': temperature,
        'purity': purity,
    })


def panning(rm: ResourceManager, name_parts: utils.ResourceIdentifier, block: utils.Json, models: List[str], loot_table: str):
    rm.data(('tfc', 'panning', name_parts), {
        'ingredient': block,
        'model_stages': models,
        'loot_table': loot_table
    })


def sluicing(rm: ResourceManager, name_parts: utils.ResourceIdentifier, block: utils.Json, loot_table: str):
    rm.data(('tfc', 'sluicing', name_parts), {
        'ingredient': utils.ingredient(block),
        'loot_table': loot_table
    })


def trim_material(rm: ResourceManager, name: str, color: str, ingredient: str, item_model_index: float):
    rm.data(('trim_material', name), {
        'asset_name': name + '_' + rm.domain,  # this field is not properly namespaced, so we have to do that ourselves
        'description': {
            'color': color,
            'translate': 'trim_material.%s.%s' % (rm.domain, name)
        },
        'ingredient': ingredient,
        'item_model_index': item_model_index
    })
    rm.item_tag('tfc:trim_materials', ingredient)

def climate_range(rm: ResourceManager, name_parts: utils.ResourceIdentifier, hydration: Tuple[int, int, int] = None, temperature: Tuple[float, float, float] = None):
    data = {}
    if hydration is not None:
        data.update({'min_hydration': hydration[0], 'max_hydration': hydration[1], 'hydration_wiggle_range': hydration[2]})
    if temperature is not None:
        data.update({'min_temperature': temperature[0], 'max_temperature': temperature[1], 'temperature_wiggle_range': temperature[2]})
    rm.data(('tfc', 'climate_ranges', name_parts), data)


def hydration_from_rainfall(rainfall: float) -> int:
    return int(rainfall) * 60 // 500


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)

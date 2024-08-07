from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # https://github.com/emilyploszaj/emi/wiki/Tag-Translation

    def tfc_fluid(tag: str, name: str = None): emi_tag(rm, 'fluid', 'tfc', tag, name)
    def tfc_item(tag: str, name: str = None): emi_tag(rm, 'item', 'tfc', tag, name)
    def c_item(tag: str, name: str = None): emi_tag(rm, 'item', 'c', tag, name)

    tfc_fluid('alcohols')
    tfc_fluid('drinkables')
    tfc_fluid('hydrating')
    tfc_fluid('molten_metals')
    tfc_fluid('usable_in_scribing_table', 'Ink')  # Exception since this is directly used in a recipe

    c_item('books')
    c_item('bowls')
    c_item('foods/dairy', 'Dairy Products')
    c_item('foods/dough', 'Doughs')
    c_item('foods/fish', 'Fishes')
    c_item('foods/flour', 'Flours')
    c_item('foods/grain', 'Grains')
    c_item('foods/meat', 'Meats')
    c_item('foods/salad', 'Salads')
    tfc_item('foods/preserves', 'Preserves')
    tfc_item('foods/sealed_preserves', 'Sealed Preserves')
    c_item('minecarts')
    c_item('sands/hematitic', 'Hematitic Sands')
    c_item('sands/olivine', 'Olivine Sands')
    c_item('sands/silica', 'Silica Sands')
    c_item('sands/volcanic', 'Volcanic Sands')
    c_item('tools/blowpipe', 'Blowpipe')
    c_item('tools/chisel', 'Chisels')
    c_item('tools/glassworking', 'Glass Working Tools')
    c_item('tools/hammer', 'Hammers')
    c_item('tools/knife', 'Knives')
    c_item('tools/saw', 'Saws')
    c_item('tools/scythe', 'Scythes')
    tfc_item('tools/sharp', 'Sharp Tools')
    c_item('stones/hardened', 'Hardened Rocks')
    c_item('stones/loose', 'Loose Rocks')
    c_item('stones/pressure_plate', 'Stone Pressure Plates')
    c_item('stones/raw', 'Raw Rocks')
    c_item('stones/smooth', 'Smooth Stones')
    c_item('stones/smooth_slabs', 'Smooth Stone Slabs')

    for metal, info in METALS.items():
        c_item('ingots/%s' % metal, lang('%s ingots', metal))
        if info.type == 'part' or info.type == 'all':
            c_item('double_ingots/%s' % metal, lang('%s double ingots', metal))
            c_item('sheets/%s' % metal, lang('%s double ingots', metal))
            c_item('double_sheets/%s' % metal, lang('%s double sheets', metal))
            c_item('rods/%s' % metal, lang('%s rods', metal))
            c_item('storage_blocks/%s' % metal, lang('%s storage_blocks', metal))

    tfc_item('double_sheets/any_bronze', 'Any Bronze Double Sheets')
    tfc_item('stones/loose/igneous_extrusive', 'Loose Igneous Extrusive Rocks')
    tfc_item('stones/loose/igneous_intrusive', 'Loose Igneous Intrusive Rocks')
    tfc_item('stones/loose/metamorphic', 'Loose Metamorphic Rocks')
    tfc_item('stones/loose/sedimentary', 'Loose Sedimentary Rocks')

    tfc_item('alpaca_food', 'Alpaca Foods')
    tfc_item('cat_food', 'Cat Foods')
    tfc_item('chicken_food', 'Chicken Foods')
    tfc_item('cow_food', 'Cow Foods')
    tfc_item('dog_food', 'Dog Foods')
    tfc_item('donkey_food', 'Donkey Foods')
    tfc_item('duck_food', 'Duck Foods')
    tfc_item('frog_food', 'Frog Foods')
    tfc_item('goat_food', 'Goat Foods')
    tfc_item('horse_food', 'Horse Foods')
    tfc_item('mule_food', 'Mule Foods')
    tfc_item('musk_ox_food', 'Musk Ox Foods')
    tfc_item('penguin_food', 'Penguin Foods')
    tfc_item('pig_food', 'Pig Foods')
    tfc_item('quail_food', 'Quail Foods')
    tfc_item('rabbit_food', 'Rabbit Foods')
    tfc_item('sheep_food', 'Sheep Foods')
    tfc_item('turtle_food', 'Turtle Foods')
    tfc_item('yak_food', 'Yak Foods')

    tfc_item('usable_in_jam_sandwich', 'Jam Sandwich Ingredients')
    tfc_item('usable_in_powder_keg', 'Powder Keg Explosives')
    tfc_item('usable_in_salad', 'Salad Ingredients')
    tfc_item('usable_in_sandwich', 'Sandwich Ingredients')
    tfc_item('usable_in_soup', 'Soup Ingredients')
    tfc_item('salad_bowls')

    tfc_item('anvils')
    tfc_item('axles')
    tfc_item('barrels')
    tfc_item('clutches')
    tfc_item('gear_boxes')
    tfc_item('lamps')
    tfc_item('large_vessels')
    tfc_item('looms')
    tfc_item('lumber')
    tfc_item('molds')
    tfc_item('vessels')
    tfc_item('water_wheels')
    tfc_item('windmill_blades')
    tfc_item('workbenches')
    tfc_item('fired_large_vessels')
    tfc_item('fired_molds')
    tfc_item('fired_vessels')
    tfc_item('unfired_large_vessels')
    tfc_item('unfired_molds')
    tfc_item('unfired_vessels')
    tfc_item('tool_racks')
    tfc_item('sluices')

    tfc_item('blast_furnace_fuel', 'Blast Furnace Fuels')
    tfc_item('blast_furnace_sheets', 'Blast Furnace Sheets')
    tfc_item('blast_furnace_tuyeres', 'Blast Furnace Tuyeres')
    tfc_item('bowl_powders', 'Powders')
    tfc_item('firepit_fuel', 'Firepit Fuels')
    tfc_item('firepit_kindling', 'Firepit Kindling')
    tfc_item('forge_fuel', 'Charcoal Forge Fuels')
    tfc_item('gem_powders', 'Gemstone Powders')

    tfc_item('colored_alabaster_bricks')
    tfc_item('colored_banners')
    tfc_item('colored_beds')
    tfc_item('colored_candles')
    tfc_item('colored_carpets')
    tfc_item('colored_concrete_powder', 'Colored Concrete Powders')
    tfc_item('colored_glazed_terracotta')
    tfc_item('colored_large_vessels')
    tfc_item('colored_polished_alabaster')
    tfc_item('colored_raw_alabaster')
    tfc_item('colored_shulker_boxes')
    tfc_item('colored_terracotta')
    tfc_item('colored_vessels')
    tfc_item('colored_windmill_blades')
    tfc_item('colored_wool', 'Colored Wools')
    tfc_item('compost_browns', 'Brown Compost Items')
    tfc_item('compost_greens', 'Green Compost Items')
    tfc_item('deals_crushing_damage')
    tfc_item('deals_piercing_damage')
    tfc_item('deals_slashing_damage')

    for ore, info in ORES.items():
        if info.graded:
            for grade in ORE_GRADES:
                c_item('ores/%s/%s' % (info.metal, grade), lang('%s %s ores', grade, info.metal))
        else:
            c_item('ores/%s' % ore, lang('%s ores', ore))

    rm.data('emi:tag/exclusions/tfc', {
        'fluid': ignore_tfc(
            'any_fresh_water',
            'any_infinite_water',
            'fresh_water',
            'infinite_water',
            'ingredients',
            'mixable',
            # 'usable_in' tags are only really referenced via code
            'usable_in_barrel',
            'usable_in_bell_mold',
            'usable_in_blue_steel_bucket',
            'usable_in_ingot_mold',
            'usable_in_jug',
            'usable_in_pot',
            'usable_in_red_steel_bucket',
            'usable_in_scribing_table',
            'usable_in_sluice',
            'usable_in_tool_head_mold',
            'usable_in_wooden_bucket'
        ),
        'item': ignore_tfc(
            'can_be_lit_on_torch',
            'carried_by_horse',
            'fluid_item_ingredient_empty_containers',
            'fox_spawns_with',
            'firepit_logs',
            'firepit_sticks',
            'pit_kiln_logs',
            'pit_kiln_straw',
            'compost_browns/low',
            'compost_browns/medium',
            'compost_browns/high',
            'compost_greens/low',
            'compost_greens/medium',
            'compost_greens/high',
            'usable_on_tool_rack',
            'disabled_monster_held_items',
            'skeleton_weapons',
            'mob_chest_armor',
            'mob_feet_armor',
            'mob_head_armor',
            'mob_leg_armor',
        )
    }, root_domain='assets')


def emi_tag(rm: ResourceManager, tag_type: str, namespace: str, tag: str, name: str):
    if name is None:
        name = lang(tag)
    rm.lang('tag.%s.%s.%s' % (tag_type, namespace, tag.replace('/', '.')), name)


def ignore_tfc(*tags: str) -> list[str]:
    return ['tfc:%s' % tag for tag in tags]
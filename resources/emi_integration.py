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
    c_item('stones/hardened', 'Hardened Rocks')
    c_item('stones/loose', 'Loose Rocks')
    c_item('stones/loose/igneous_extrusive', 'Loose Igneous Extrusive Rocks')
    c_item('stones/loose/igneous_intrusive', 'Loose Igneous Intrusive Rocks')
    c_item('stones/loose/metamorphic', 'Loose Metamorphic Rocks')
    c_item('stones/loose/sedimentary', 'Loose Sedimentary Rocks')
    c_item('stones/pressure_plates', 'Stone Pressure Plates')
    c_item('stones/raw', 'Raw Rocks')
    c_item('stones/smooth', 'Smooth Stones')
    c_item('stones/smooth_slabs', 'Smooth Stone Slabs')

    for metal, info in METALS.items():
        c_item('ingots/%s' % metal, lang('%s ingots', metal))
        if 'part' in info.types:
            c_item('double_ingots/%s' % metal, lang('%s double ingots', metal))
            c_item('sheets/%s' % metal, lang('%s double ingots', metal))
            c_item('double_sheets/%s' % metal, lang('%s double sheets', metal))
            c_item('rods/%s' % metal, lang('%s rods', metal))
            c_item('storage_blocks/%s' % metal, lang('%s storage_blocks', metal))

    tfc_item('double_sheets/any_bronze', 'Any Bronze Double Sheets')

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
        )
    }, root_domain='assets')


def emi_tag(rm: ResourceManager, tag_type: str, namespace: str, tag: str, name: str):
    if name is None:
        name = lang(tag)
    rm.lang('tag.%s.%s.%s' % (tag_type, namespace, tag.replace('/', '.')), name)


def ignore_tfc(*tags: str) -> list[str]:
    return ['tfc:%s' % tag for tag in tags]
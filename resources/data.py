#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager, utils, loot_tables
from constants import *



def generate(rm: ResourceManager):

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
    mob_loot(rm, 'turtle', 'minecraft:turtle_scute', extra_pool={'name': 'tfc:food/turtle'})
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

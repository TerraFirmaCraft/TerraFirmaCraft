from mcresources import ResourceManager

from constants import *

# Crafting recipes
def generate(rm: ResourceManager):
# Rock Things
    for rock in ROCKS.keys():
        rm.crafting_shapeless('tfc:stone/%s/%s_rock' % (rock, rock), 'tfc:rock/cobble/%s' % rock,(4, 'tfc:rock/rock/%s' % rock),group = None,conditions = None)
        # Causes crash
        # rm.crafting_shaped('tfc:stone/%s/%s_cobble' % (rock, rock), ['XX','XX'],'tfc:rock/rock/%s' % rock,'tfc;rock/cobble/%s' % rock,group = None, conditions = None)
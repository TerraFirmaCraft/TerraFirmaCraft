#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager

from constants import *


# Crafting recipes
def generate(rm: ResourceManager):
    # Rock Things
    for rock in ROCKS.keys():

        # Cobble <-> Loose Rocks
        rm.crafting_shapeless('tfc:cobble_to_loose_rocks/%s' % rock, 'tfc:rock/cobble/%s' % rock, (4, 'tfc:rock/loose/%s' % rock))
        rm.crafting_shaped('tfc:loose_rocks_to_cobble/%s' % rock, ['XX', 'XX'], 'tfc:rock/loose/%s' % rock, 'tfc:rock/cobble/%s' % rock)

        # Stairs, Slabs and Walls
        for block_type in ROCK_BLOCK_TYPES:
            if block_type in CUTTABLE_ROCKS:
                rm.crafting_shaped('tfc:rock/%s/%s_%s_slab' % (rock, rock, block_type), ['XXX'], 'tfc:rock/%s/%s' % (block_type, rock), (6, 'tfc:rock/%s/%s_slab' % (block_type, rock)))
                rm.crafting_shaped('tfc:rock/%s/%s_%s_stairs' % (rock, rock, block_type), ['X  ', 'XX ', 'XXX'], 'tfc:rock/%s/%s' % (block_type, rock), (4, 'tfc:rock/%s/%s_stairs' % (block_type, rock)))
                rm.crafting_shaped('tfc:rock/%s/%s_%s_wall' % (rock, rock, block_type), ['XXX', 'XXX'], 'tfc:rock/%s/%s' % (block_type, rock), (6, 'tfc:rock/%s/%s_wall' % (block_type, rock)))

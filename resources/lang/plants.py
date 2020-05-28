from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    for plant in PLANTS:
        rm.lang('block.tfc.plant.%s' % plant, lang('%s' % plant))
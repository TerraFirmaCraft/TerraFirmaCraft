from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    for metal, metal_data in METALS.items():
        rm.lang(('metal.tfc.%s' % metal, lang('%s' % metal)))
from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    for metal, metal_data in METALS.items():
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier
        })

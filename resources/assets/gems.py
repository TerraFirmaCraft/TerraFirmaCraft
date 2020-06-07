from mcresources import ResourceManager

from constants import *

def generate(rm: ResourceManager):
    for gem in GEMS:
        for grade in GEM_GRADES:
            rm.item_model(('gem', '/', '%s' % grade, '/', '%s' % gem), \
                'tfc:item/gem/%s/%s' % (grade, gem)) \
                .with_lang(lang('%s %s' % (grade, gem)))
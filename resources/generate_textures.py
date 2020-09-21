from PIL import Image# pip install pillow, don't try to install PIL
import os
from constants import *

path = '../src/main/resources/assets/tfc/textures/'

def overlay_image(front_file_dir, back_file_dir, result_dir):
    foreground = Image.open(front_file_dir + '.png')
    background = Image.open(back_file_dir + '.png').convert('RGBA')
    background.paste(foreground, (0,0), foreground.convert('RGBA'))
    background.save(result_dir + '.png')

for wood in WOODS:
    overlay_image('texture_templates/bookshelf', path + 'block/wood/planks/%s' % wood, path + 'block/wood/planks/%s_bookshelf' % wood)
    overlay_image('texture_templates/log_top/%s' % wood, path + 'block/wood/log/%s' % wood, path + 'block/wood/log_top/%s' % wood)
    overlay_image('texture_templates/log_top/%s' % wood, path + 'block/wood/stripped_log/%s' % wood, path + 'block/wood/stripped_log_top/%s' % wood)
    for bench in ('workbench_front', 'workbench_side', 'workbench_top'):
        overlay_image('texture_templates/' + bench, path + 'block/wood/planks/%s' % wood, path + 'block/wood/planks/%s_' % wood + bench)

for rock in ROCKS.keys():
    overlay_image('texture_templates/mossy_stone_bricks', path + 'block/rock/bricks/%s' % rock, path + 'block/rock/mossy_bricks/%s' % rock)
    overlay_image('texture_templates/mossy_cobblestone', path + 'block/rock/cobble/%s' % rock, path + 'block/rock/mossy_cobble/%s' % rock)
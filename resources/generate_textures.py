from typing import List

from PIL import Image, ImageDraw
from PIL.Image import Transpose

import colorsys
from constants import *

path = './src/main/resources/assets/tfc/textures/'
mc_path = './src/main/resources/assets/minecraft/textures/'
templates = './resources/texture_templates/'


def overlay_image(front_file_dir, back_file_dir, result_dir, mask: str = None):
    foreground = Image.open(front_file_dir + '.png').convert('RGBA')
    background = Image.open(back_file_dir + '.png').convert('RGBA')
    if mask is None:
        mask = foreground
    else:
        mask = Image.open(mask + '.png').convert('L')
    background.paste(foreground, (0, 0), mask)
    background.save(result_dir + '.png')

def create_chest(wood: str):
    log = Image.open(path + 'block/wood/log/%s' % wood + '.png').convert('RGBA').crop((0, 0, 14, 14))
    sheet = Image.open(path + 'block/wood/sheet/%s' % wood + '.png').convert('RGBA').crop((0, 0, 14, 14))
    empty = (0, 0, 0, 0)
    frame = log.copy()
    ImageDraw.Draw(frame).rectangle((1, 1, 12, 12), fill=empty)
    top = sheet.copy().transpose(Transpose.TRANSVERSE)
    top.paste(frame, (0, 0), frame)

    side = top.copy()
    ImageDraw.Draw(side).rectangle((0, 0, 14, 3), fill=empty)
    log_section = log.copy()
    ImageDraw.Draw(log_section).rectangle((0, 1, 14, 14), fill=empty)
    side.paste(log_section, (0, 4), log_section)
    side.paste(log_section, (0, 13), log_section)

    rim = top.copy()
    ImageDraw.Draw(rim).rectangle((0, 0, 14, 9), fill=empty)
    rim.paste(log_section, (0, 9), log_section)
    underside = top.copy()
    ImageDraw.Draw(underside).rectangle((2, 2, 11, 11), fill=(0, 0, 0, 255))

    cover = top.copy()
    shaded_square = Image.new('RGBA', (10, 10), (0, 0, 0, 180))
    blank = Image.new('RGBA', (14, 14), empty)
    blank.paste(shaded_square, (2, 2), shaded_square)
    cover = Image.alpha_composite(cover, blank)

    handle = Image.open(templates + 'chest/handle.png').convert('RGBA')
    normal = Image.new('RGBA', (64, 64), empty)
    normal.paste(handle, (0, 0), handle)
    normal.paste(cover, (14, 0), cover)
    normal.paste(top, (28, 0), top)
    for i in range(0, 4):
        normal.paste(rim, (i * 14, 5), rim)
        normal.paste(side, (i * 14, 29), side)
    normal.paste(top, (14, 19), top)
    normal.paste(underside, (28, 19), underside)
    normal.save(path + 'entity/chest/normal/%s' % wood + '.png')
    trapped = normal.copy()
    trapped_overlay = Image.open(templates + 'chest/trapped_overlay.png')
    trapped = Image.alpha_composite(trapped, trapped_overlay)
    trapped.save(path + 'entity/chest/trapped/%s' % wood + '.png')

    # Double Chests
    log_rect = Image.open(path + 'block/wood/log/%s' % wood + '.png').convert('RGBA').crop((0, 0, 15, 14))
    sheet_rect = Image.open(path + 'block/wood/sheet/%s' % wood + '.png').convert('RGBA').crop((0, 0, 15, 14))

    top_right = sheet_rect.copy()
    top_right_frame = log_rect.copy()
    ImageDraw.Draw(top_right_frame).rectangle((0, 1, 13, 12), fill=empty)
    top_right.paste(top_right_frame, (0, 0), top_right_frame)

    top_left = sheet_rect.copy()
    top_left_frame = log_rect.copy()
    ImageDraw.Draw(top_left_frame).rectangle((1, 1, 15, 12), fill=empty)
    top_left.paste(top_left_frame, (0, 0), top_left_frame)

    underside_right = top_right.copy()
    ImageDraw.Draw(underside_right).rectangle((0, 2, 12, 11), fill=(0, 0, 0, 255))
    underside_left = top_left.copy()
    ImageDraw.Draw(underside_left).rectangle((2, 2, 15, 11), fill=(0, 0, 0, 255))

    cover_right = top_right.copy()
    shaded_rectangle = Image.new('RGBA', (13, 10), (0, 0, 0, 180))
    blank = Image.new('RGBA', (15, 14), empty)
    blank.paste(shaded_rectangle, (0, 2), shaded_rectangle)
    cover_right = Image.alpha_composite(cover_right, blank)
    cover_left = top_left.copy()
    blank = Image.new('RGBA', (15, 14), empty)
    blank.paste(shaded_rectangle, (2, 2), shaded_rectangle)
    cover_left = Image.alpha_composite(cover_left, blank)

    rim_right = top_right.copy()
    ImageDraw.Draw(rim_right).rectangle((0, 0, 15, 9), fill=empty)
    rim_right.paste(log_section, (0, 9), log_section)
    rim_right.paste(log_section, (1, 9), log_section)
    rim_left = top_left.copy()
    ImageDraw.Draw(rim_left).rectangle((0, 0, 15, 9), fill=empty)
    rim_left.paste(log_section, (0, 9), log_section)
    rim_left.paste(log_section, (1, 9), log_section)

    side_right = top_right.copy()
    ImageDraw.Draw(side_right).rectangle((0, 0, 15, 3), fill=empty)
    log_section = log.copy()
    ImageDraw.Draw(log_section).rectangle((0, 1, 15, 14), fill=empty)
    side_right.paste(log_section, (0, 4), log_section)
    side_right.paste(log_section, (1, 4), log_section)
    side_right.paste(log_section, (0, 13), log_section)
    side_right.paste(log_section, (1, 13), log_section)
    side_left = top_left.copy()
    ImageDraw.Draw(side_left).rectangle((0, 0, 15, 3), fill=empty)
    log_section = log.copy()
    ImageDraw.Draw(log_section).rectangle((0, 1, 15, 14), fill=empty)
    side_left.paste(log_section, (0, 4), log_section)
    side_left.paste(log_section, (1, 4), log_section)
    side_left.paste(log_section, (0, 13), log_section)
    side_left.paste(log_section, (1, 13), log_section)

    normal_left = Image.new('RGBA', (64, 64), empty)
    handle = Image.open(templates + 'chest/handle_left.png')
    normal_left.paste(handle, (0, 0), handle)
    normal_left.paste(cover_right, (14, 0), cover_right)
    normal_left.paste(top_right, (29, 0), top_right)
    normal_left.paste(rim_right, (14, 5), rim_right)
    normal_left.paste(rim, (29, 5), rim)
    normal_left.paste(rim_left, (43, 5), rim_left)
    normal_left.paste(top_right, (14, 19), top_right)
    normal_left.paste(underside_right, (29, 19), underside_right)
    normal_left.paste(side, (29, 29), side)
    normal_left.paste(side_right, (14, 29), side_right)
    normal_left.paste(side_left, (43, 29), side_left)
    normal_left.save(path + 'entity/chest/normal_left/%s' % wood + '.png')
    left_trapped_overlay = Image.open(templates + 'chest/trapped_left_overlay.png')
    left_trapped = Image.alpha_composite(normal_left, left_trapped_overlay)
    left_trapped.save(path + 'entity/chest/trapped_left/%s' % wood + '.png')

    normal_right = Image.new('RGBA', (64, 64), empty)
    handle = Image.open(templates + 'chest/handle_right.png')
    normal_right.paste(handle, (0, 0), handle)
    normal_right.paste(cover_left, (14, 0), cover_left)
    normal_right.paste(top_left, (29, 0), top_left)
    normal_right.paste(rim, (0, 5), rim)
    normal_right.paste(rim_left, (14, 5), rim_left)
    normal_right.paste(rim_right, (43, 5), rim_right)
    normal_right.paste(top_left, (14, 19), top_left)
    normal_right.paste(cover_left, (29, 19), cover_left)
    normal_right.paste(underside_left, (29, 19), underside_left)
    normal_right.paste(side, (0, 29), side)
    normal_right.paste(side_left, (14, 29), side_right)
    normal_right.paste(side_right, (43, 29), side_left)
    normal_right.save(path + 'entity/chest/normal_right/%s' % wood + '.png')
    right_trapped_overlay = Image.open(templates + 'chest/trapped_right_overlay.png')
    right_trapped = Image.alpha_composite(normal_right, right_trapped_overlay)
    right_trapped.save(path + 'entity/chest/trapped_right/%s' % wood + '.png')

def create_chest_boat(wood: str):
    log = Image.open(path + 'block/wood/log/%s.png' % wood).convert('RGBA')
    sheet = Image.open(path + 'block/wood/sheet/%s.png' % wood).convert('RGBA').transpose(Transpose.TRANSVERSE)
    log_mask = Image.open(templates + 'chest_boat_log_mask.png').convert('L')
    sheet_mask = Image.open(templates + 'chest_boat_sheet_mask.png').convert('L')
    big_log = fill_image(log, 128, 128, 16, 16)
    big_sheet = fill_image(sheet, 128, 128, 16, 16)
    cover = Image.open(templates + 'chest_boat_static.png')

    base = Image.new('RGBA', (128, 128))
    base.paste(big_log, mask=log_mask)
    base.paste(big_sheet, mask=sheet_mask)
    base.paste(cover, mask=cover)
    base.save(path + 'entity/chest_boat/%s.png' % wood)

def create_hanging_sign(wood: str, metal: str):
    img = Image.new('RGBA', (64, 32))
    sheet = Image.open(path + 'block/wood/sheet/%s.png' % wood).convert('RGBA').transpose(Transpose.TRANSVERSE)
    big_sheet = fill_image(sheet, 64, 32, 16, 16)
    mask = Image.open(templates + 'hanging_sign.png').convert('L')
    img.paste(big_sheet, mask=mask)
    smooth = Image.open(path + 'block/metal/smooth/%s.png' % metal).convert('RGBA').transpose(Transpose.TRANSVERSE)
    big_smooth = fill_image(smooth, 64, 32, 16, 16)
    chain_mask = Image.open(templates + 'hanging_sign_chains.png').convert('L')
    img.paste(big_smooth, mask=chain_mask)
    img.save(path + 'entity/signs/hanging/%s/%s.png' % (metal, wood))

    img = Image.new('RGBA', (16, 16))
    img.paste(sheet, mask=Image.open(templates + 'hanging_sign_edit.png').convert('L'))
    img.paste(smooth, mask=Image.open(templates + 'hanging_sign_edit_overlay.png').convert('L'))
    img.save(path + 'gui/hanging_signs/%s/%s.png' % (metal, wood))

def fill_image(tile_instance, width: int, height: int, tile_width: int, tile_height: int):
    image_instance = Image.new('RGBA', (width, height))
    for i in range(0, int(width / tile_width)):
        for j in range(0, int(height / tile_height)):
            image_instance.paste(tile_instance, (i * tile_width, j * tile_height))
    return image_instance

def stitch_images(width: int, height: int, name: str, paths: List[str]):
    img = Image.new('RGBA', (width, height))
    images = []
    for fp in paths:
        images.append(Image.open(fp).convert('RGBA'))
    for i in range(0, int(width / 16)):
        for j in range(0, int(height / 16)):
            if len(images) == 0:
                img.save(templates + name + '.png')
                return
            else:
                img.paste(images.pop(), (j * 16, i * 16))

def create_bookshelf(wood: str):
    planks = Image.open(path + 'block/wood/planks/%s' % wood + '.png').convert('RGBA')
    mask = Image.open(templates + 'chiseled_bookshelf_mask.png').convert('L')
    empty = Image.open(templates + 'chiseled_bookshelf_empty.png').convert('RGBA')
    filled = Image.open(templates + 'chiseled_bookshelf_occupied.png').convert('RGBA')
    empty.paste(planks, mask=mask)
    filled.paste(planks, mask=mask)
    empty.save(path + 'block/wood/planks/%s_bookshelf_empty.png' % wood)
    filled.save(path + 'block/wood/planks/%s_bookshelf_occupied.png' % wood)

def create_sign(wood: str):
    log = Image.open(path + 'block/wood/log/%s' % wood + '.png').convert('RGBA')
    planks = Image.open(path + 'block/wood/planks/%s' % wood + '.png').convert('RGBA')
    image = Image.new('RGBA', (64, 32), (0, 0, 0, 0))
    for coord in ((0, 0), (16, 0), (32, 0), (48, 0)):
        image.paste(planks, coord)
    image.paste(log, (0, 16))
    image.save(path + 'entity/signs/%s.png' % wood)

def create_sign_item(wood: str, log_color):
    mast = Image.open(templates + 'sign_mast.png')
    mast = put_on_all_pixels(mast, log_color)
    mast.save(path + 'item/wood/sign/%s.png' % wood)

def create_hanging_sign_chains_item(metal: str, smooth_color):
    chains = Image.open(templates + 'hanging_sign_head_chains.png')
    chains = put_on_all_pixels(chains, smooth_color)
    chains.save(path + 'item/metal/hanging_sign/%s.png' % metal)

def create_magma(rock: str):
    magma = Image.new('RGBA', (16, 48), (0, 0, 0, 0))
    raw = Image.open(templates + '/raw/%s.png' % rock)
    magma.paste(raw, (0, 0))
    magma.paste(raw, (0, 16))
    magma.paste(raw, (0, 32))
    overlay = Image.open(templates + 'magma.png')
    magma = Image.alpha_composite(magma, overlay)
    magma.save(path + 'block/rock/magma/%s.png' % rock)

def create_horse_chest(wood: str, plank_color, log_color):
    for variant in ('chest', 'barrel'):
        image = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
        overlay = Image.open(templates + 'horse_%s_overlay.png' % variant).convert('RGBA')
        frame = Image.open(templates + 'horse_%s_log.png' % variant).convert('RGBA')
        body = Image.open(templates + 'horse_%s_sheet.png' % variant).convert('RGBA')
        frame = put_on_all_pixels(frame, log_color)
        body = put_on_all_pixels(body, plank_color)
        image.paste(frame, (26, 21), frame)
        image.paste(body, (26, 21), body)
        image.paste(overlay, (26, 21), overlay)
        if variant == 'chest':
            image.save(path + 'entity/chest/horse/%s.png' % wood)
        elif variant == 'barrel':
            image.save(path + 'entity/chest/horse/%s_barrel.png' % wood)

def get_wood_colors(wood_path: str):
    wood = Image.open(path + 'block/wood/%s.png' % wood_path)
    return wood.getpixel((0, 0))

def get_metal_colors(metal_path: str):
    metal = Image.open(path + 'block/metal/%s.png' % metal_path)
    return metal.getpixel((0,0))

def put_on_all_pixels(img: Image, color, dark_threshold: int = 50) -> Image:
    if isinstance(color, int):
        color = (color, color, color, 255)
    img = img.convert('RGBA')
    _, _, _, alpha = img.split()
    img = img.convert('HSV')
    hue, sat, val = colorsys.rgb_to_hsv(color[0], color[1], color[2])
    for x in range(0, img.width):
        for y in range(0, img.height):
            dat = img.getpixel((x, y))
            tup = (int(hue * 255), int(sat * 255), int(dat[2] if val > dark_threshold else dat[2] * 0.5))
            img.putpixel((x, y), tup)
    img = img.convert('RGBA')
    img.putalpha(alpha)
    return img

def create_boat_texture(wood: str):
    img = Image.open(templates + 'boat.png').convert('RGBA')
    palette_key = Image.open(path + 'color_palettes/wood/planks/palette.png').convert('RGBA')
    palette = Image.open(path + 'color_palettes/wood/planks/%s.png' % wood).convert('RGBA')
    manual_palette_swap(img, palette_key, palette)
    img.save(path + 'entity/boat/%s.png' % wood)

def manual_palette_swap(img: Image, palette_key: Image, palette: Image) -> Image:
    data = {}
    for x in range(0, palette_key.width):
        data[palette_key.getpixel((x, 0))] = palette.getpixel((x, 0))
    for x in range(0, img.width):
        for y in range(0, img.height):
            dat = img.getpixel((x, y))
            if dat in data:
                img.putpixel((x, y), data[dat])
    return img

# TODO IM BROKEN
def main():
    for wood in WOODS.keys():
        for bench in ('workbench_front', 'workbench_side', 'workbench_top'):
            overlay_image(templates + bench, path + 'block/wood/planks/%s' % wood, path + 'block/wood/planks/%s_' % wood + bench)
        create_chest(wood)
        create_sign(wood)
        create_bookshelf(wood)
        plank_color = get_wood_colors('planks/%s' % wood)
        log_color = get_wood_colors('log/%s' % wood)
        create_horse_chest(wood, plank_color, log_color)
        create_chest_boat(wood)
        if wood != 'palm':
            create_boat_texture(wood)
        for metal, metal_data in METALS.items():
            if 'utility' in metal_data.types:
                create_hanging_sign(wood, metal)

    for rock, data in ROCKS.items():
        overlay_image(templates + 'mossy_stone_bricks', path + 'block/rock/bricks/%s' % rock, path + 'block/rock/mossy_bricks/%s' % rock)
        overlay_image(templates + 'mossy_cobblestone', path + 'block/rock/cobble/%s' % rock, path + 'block/rock/mossy_cobble/%s' % rock)
        overlay_image(templates + 'mossy_loose_knapping', path + 'block/rock/raw/%s' % rock, path + 'gui/knapping/rock/mossy_loose/%s' % rock)
        if data.category == 'igneous_intrusive' or data.category == 'igneous_extrusive':
            create_magma(rock)

    for soil in SOIL_BLOCK_VARIANTS:
        overlay_image(templates + 'rooted_dirt', path + 'block/dirt/%s' % soil, path + 'block/rooted_dirt/%s' % soil)
        overlay_image(path + 'block/dirt/%s' % soil, path + 'block/grass_path/%s_top' % soil, path + 'block/grass_path/%s_side' % soil, templates + 'grass_side_mask')
        overlay_image(templates + 'mangrove_roots_side', path + 'block/mud/%s' % soil, path + 'block/mud/%s_roots_side' % soil)
        overlay_image(templates + 'mangrove_roots_top', path + 'block/mud/%s' % soil, path + 'block/mud/%s_roots_top' % soil)

    for metal, metal_data in METALS.items():
        if 'utility' in metal_data.types:
            overlay_image(path + 'block/metal/smooth/%s' % metal, path + 'block/empty', path + 'block/metal/chain/%s' % metal, templates + 'chain_mask')
        if 'utility' in metal_data.types:
            smooth_color = get_metal_colors('smooth/%s' % metal)
            create_hanging_sign_chains_item(metal, smooth_color)


    for i in range(0, 32):
        number = str(i) if i > 9 else '0' + str(i)
        overlay_image(templates + 'compass_overlay', templates + 'compass/compass_%s' % number, mc_path + 'item/compass_%s' % number)

    for fruit in JAR_FRUITS:
        img = Image.open(path + 'item/jar/%s.png' % fruit).convert('RGBA')
        for x, y in ((7, 2), (8, 2), (9, 2), (7, 4), (8, 4), (9, 4)):
            img.putpixel((x, y), (0, 0, 0, 0))
        img.save(path + 'item/jar/%s_unsealed.png' % fruit)


if __name__ == '__main__':
    main()

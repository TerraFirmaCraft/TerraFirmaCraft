from PIL import Image, ImageDraw, ImageEnhance
from PIL.Image import Transpose

from constants import *

path = '../src/main/resources/assets/tfc/textures/'


def overlay_image(front_file_dir, back_file_dir, result_dir):
    foreground = Image.open(front_file_dir + '.png')
    background = Image.open(back_file_dir + '.png').convert('RGBA')
    background.paste(foreground, (0, 0), foreground.convert('RGBA'))
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

    handle = Image.open('texture_templates/chest/handle.png').convert('RGBA')
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
    trapped_overlay = Image.open('texture_templates/chest/trapped_overlay.png')
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
    handle = Image.open('texture_templates/chest/handle_left.png')
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
    left_trapped_overlay = Image.open('texture_templates/chest/trapped_left_overlay.png')
    left_trapped = Image.alpha_composite(normal_left, left_trapped_overlay)
    left_trapped.save(path + 'entity/chest/trapped_left/%s' % wood + '.png')

    normal_right = Image.new('RGBA', (64, 64), empty)
    handle = Image.open('texture_templates/chest/handle_right.png')
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
    right_trapped_overlay = Image.open('texture_templates/chest/trapped_right_overlay.png')
    right_trapped = Image.alpha_composite(normal_right, right_trapped_overlay)
    right_trapped.save(path + 'entity/chest/trapped_right/%s' % wood + '.png')

def create_sign(wood: str):
    log = Image.open(path + 'block/wood/log/%s' % wood + '.png').convert('RGBA')
    planks = Image.open(path + 'block/wood/planks/%s' % wood + '.png').convert('RGBA')
    image = Image.new('RGBA', (64, 32), (0, 0, 0, 0))
    for coord in ((0, 0), (16, 0), (32, 0), (48, 0)):
        image.paste(planks, coord)
    image.paste(log, (0, 16))
    image.save(path + 'entity/signs/%s.png' % wood)

def create_sign_item(wood: str, plank_color, log_color):
    head = Image.open('texture_templates/sign_head.png')
    mast = Image.open('texture_templates/sign_mast.png')
    head = put_on_all_pixels(head, plank_color)
    mast = put_on_all_pixels(mast, log_color)
    image = Image.alpha_composite(mast, head)
    image = ImageEnhance.Color(image).enhance(2)
    image.save(path + 'item/wood/sign/%s.png' % wood)

def create_magma(rock: str):
    magma = Image.new('RGBA', (16, 48), (0, 0, 0, 0))
    raw = Image.open('texture_templates/raw/%s.png' % rock)
    magma.paste(raw, (0, 0))
    magma.paste(raw, (0, 16))
    magma.paste(raw, (0, 32))
    overlay = Image.open('texture_templates/magma.png')
    magma = Image.alpha_composite(magma, overlay)
    magma.save(path + 'block/rock/magma/%s.png' % rock)

def get_wood_colors(wood_path: str):
    wood = Image.open(path + 'block/wood/%s.png' % wood_path)
    return wood.getpixel((0, 0))

def easy_colorize(color, from_path, to_path, saturation: float = 1):
    img = Image.open(from_path + '.png')
    new_image = put_on_all_pixels(img, color)
    if saturation != 1:
        new_image = ImageEnhance.Color(new_image).enhance(saturation)
    new_image.save(to_path + '.png')

def put_on_all_pixels(img: Image, color) -> Image:
    if isinstance(color, int):
        color = (color, color, color, 255)
    img = img.convert('RGBA')
    for x in range(0, img.width):
        for y in range(0, img.height):
            dat = img.getpixel((x, y))
            grey = (dat[0] + dat[1] + dat[2]) / 3 / 255
            if dat[3] > 0:
                tup = (int(color[0] * grey), int(color[1] * grey), int(color[2] * grey))
                img.putpixel((x, y), tup)
    return img

def main():
    for wood in WOODS.keys():
        overlay_image('texture_templates/bookshelf', path + 'block/wood/planks/%s' % wood, path + 'block/wood/planks/%s_bookshelf' % wood)
        overlay_image('texture_templates/log_top/%s' % wood, path + 'block/wood/log/%s' % wood, path + 'block/wood/log_top/%s' % wood)
        overlay_image('texture_templates/log_top/%s' % wood, path + 'block/wood/stripped_log/%s' % wood, path + 'block/wood/stripped_log_top/%s' % wood)
        for bench in ('workbench_front', 'workbench_side', 'workbench_top'):
            overlay_image('texture_templates/' + bench, path + 'block/wood/planks/%s' % wood, path + 'block/wood/planks/%s_' % wood + bench)
        create_chest(wood)
        create_sign(wood)
        plank_color = get_wood_colors('planks/%s' % wood)
        log_color = get_wood_colors('log/%s' % wood)
        create_sign_item(wood, plank_color, log_color)
        for item in ('twig', 'boat', 'lumber'):
            easy_colorize(plank_color, 'texture_templates/%s' % item, path + 'item/wood/%s/%s' % (item, wood), 2)

    for rock, data in ROCKS.items():
        overlay_image('texture_templates/mossy_stone_bricks', path + 'block/rock/bricks/%s' % rock, path + 'block/rock/mossy_bricks/%s' % rock)
        overlay_image('texture_templates/mossy_cobblestone', path + 'block/rock/cobble/%s' % rock, path + 'block/rock/mossy_cobble/%s' % rock)
        if data.category == 'igneous_intrusive' or data.category == 'igneous_extrusive':
            create_magma(rock)

    for soil in SOIL_BLOCK_VARIANTS:
        overlay_image('texture_templates/rooted_dirt', 'texture_templates/dirt/%s' % soil, path + 'block/rooted_dirt/%s' % soil)


if __name__ == '__main__':
    main()

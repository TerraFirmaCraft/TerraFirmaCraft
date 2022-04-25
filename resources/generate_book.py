from typing import NamedTuple, Tuple
from warnings import warn

from mcresources import ResourceManager, utils
from mcresources.type_definitions import JsonObject


class Warnings:
    enabled: bool = False

    @staticmethod
    def warn(content: str):
        if Warnings.enabled:
            warn(content, stacklevel=3)


def main():
    for target, show_warnings in (
        ('../src/main/resources', True),
        ('../out/production/resources', False)
    ):
        Warnings.enabled = show_warnings
        rm = ResourceManager('tfc', resource_dir=target)
        make_book(rm)
    print('Done')


def make_book(rm: ResourceManager):
    """
    Notes for those contributing to the book, for a consistent sort of style:

    - Entries and categories are named in easy to understand resource location IDs, matching the actual in-game name wherever possible
    - The book is written, generally, in second-person as guide (i.e. using 'you' pronouns)
    - It SHOULD contain all information that someone would NEED to play TFC, to a reasonable degree of competence.
    - It SHOULD be devoid of subjective opinion or other player suggestions (i.e. NO 'some people prefer to skip copper tools instead of bronze')
    - It SHOULD use straightforward descriptions of particular mechanics, assuming no knowledge about TFC (i.e. YES 'In order to build a fire pit, first throw (Q) a log, and three sticks onto the ground...')
    - It SHOULD lock content that is INACCESSIBLE behind advancements where said advancements effectively gate various content, but NOT gate knowledge (i.e. page for how to use a bloomery should be unlocked by obtaining a copper or bronze anvil (one step before the bloomery is relevant), not by obtaining a bloomery)
    - It SHOULD use crafting recipes, images, multiblocks, and other custom page types where necessary in order to improve or better the explanations being made (i.e. NO 'In order to craft a pickaxe, place the pickaxe head on top of the stick in a crafting grid')
    - It SHOULD NOT consider itself a complete reference for crafting recipes, or any data driven content (i.e. NO 'See page 143 for all crafting recipes)
    - It SHOULD NOT contain technical/implementation details that are either beyond the obvious, not needed in order to play the game, or intended for pack maker consumption (i.e. NO 'In order to build a fire pit, throw (Q) one log item matching the tfc:fire_pit_logs tag...')
    - It SHOULD NOT document bugs, unintentional features, exploits, or ANYTHING that might be resolved at a later date (i.e. 'Dont let a glow squid escape water, or your game may crash')
    - It SHOULD NOT make explicit or implicit reference to other mod or addon mechanics (i.e. 'Food preservation can be done with ... or if you have TFC Tech, with a freezer')

    All the documentation on entry(), category(), all the book page functions, are copied from the Patchouli documentation, found here:
    https://vazkiimods.github.io/Patchouli/docs/reference/overview
    """
    book = Book(rm, 'field_guide')

    book.category('the_world', 'The World', 'All about the natural world around you.', 'tfc:grass/loam', is_sorted=True, entries=(
        entry('a_first_look', 'A First Look', 'tfc:grass/loam', pages=(
            text('At first glance, the world of TerraFirmaCraft might seem like vanilla Minecraft, but don\'t let that fool you, nearly everything has changed, from the blocks on the ground. On the surface, you might find one of many different biomes, find yourself in one of many different climates, each with their own flora, fauna, and other decoration. Everything from the grass under your feet to the leaves on the trees to the ores in the ground has changed.'),
            text('First, the dirt and grass under your feet might be one of a few different types of soil. Sand, loam, silt, or some in-between shades might appear based on the climate or biome of the region. In addition to normal dirt and grass, clay dirt and clay grass will also be scattered around the world, in addition to peat soil and peat grass in specific $(thing)climates$().'),
            text('Most of the blocks under your feet are also affected by some form of $(thing)gravity$(). Dirt, gravel, sand, and other loose soils will not only fall downwards, but they will fall down slopes if they are stacked too tall without support. Harder materials such as raw stone does not collapse immediately, but be wary of mining in unsupported areas, as raw stone can collapse with destructive force, putting any careless miner\'s life in danger'),
            text('On top of the dirt and grass, you can find over one hundred different plants, from the $(thing)athyrium fern$() to the $(thing)yucca$(). Some of these plants serve as special indicators of different resources, and can indicate the presence of clay, or fresh water nearby. Others can be used for decoration, or dyes. And almost all plants can be harvested for straw, which has many uses.'),
            text('Perhaps most importantly in TerraFirmaCraft, is to look up to the sky. The natural world has a climate, and a yearly calendar. New worlds are created in June, but as the winter months approach, the temperature will gradually drop, the weather will get colder and snowier, and plants will die or retreat until next spring, when they will start flowering again. However, in addition to the seasons passing, different regions have different climates based on their latitude and longitude. This affects the average $(thing)temperature$() and $(thing)rainfall$() of an area, which will affect what flora and fauna is found there.')
        )),
    ))


# ==================== Book Resource Generation API Functions =============================

class BookPage(NamedTuple):
    type: str
    data: JsonObject


class BookEntry(NamedTuple):
    entry_id: str
    name: str
    icon: str
    pages: Tuple[BookPage]
    advancement: str | None


class Book:

    def __init__(self, rm: ResourceManager, root_name: str):
        self.rm: ResourceManager = rm
        self.root_name = root_name
        self.category_count = 0

        rm.data(('patchouli_books', self.root_name, 'book'), {
            'name': 'tfc.field_guide.book_name',
            'landing_text': 'tfc.field_guide.book_landing_text',
            'subtitle': 'TFC_VERSION',
            'dont_generate_book': True
        })

    def category(self, category_id: str, name: str, description: str, icon: str, parent: str | None = None, is_sorted: bool = False, entries: Tuple[BookEntry, ...] = ()):
        """
        :param category_id: The id of this category.
        :param name: The name of this category.
        :param description: The description for this category. This displays in the category's main page, and can be formatted.
        :param icon: The icon for this category. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png.
        :param parent: The parent category to this one. If this is a sub-category, simply put the name of the category this is a child to here. If not, don't define it. This should be fully-qualified and of the form domain:name where domain is the same as the domain of your Book ID.
        :param is_sorted: If the entries within this category are sorted
        :param entries: A list of entries (call entry() for each)

        https://vazkiimods.github.io/Patchouli/docs/reference/category-json/
        """
        self.rm.data(('patchouli_books', self.root_name, 'en_us', 'categories', category_id), {
            'name': name,
            'description': description,
            'icon': icon,
            'parent': parent,
            'sortnum': self.category_count
        })
        self.category_count += 1

        category_id = utils.resource_location(self.rm.domain, category_id).join()

        for i, e in enumerate(entries):
            self.rm.data(('patchouli_books', self.root_name, 'en_us', 'entries', e.entry_id), {
                'name': e.name,
                'category': category_id,
                'icon': e.icon,
                'pages': [{
                    'type': page.type,
                    **page.data
                } for page in e.pages],
                'advancement': e.advancement,
                'sortnum': i if is_sorted else None
            })


def entry(entry_id: str, name: str, icon: str, advancement: str | None = None, pages: Tuple[BookPage, ...] = ()) -> BookEntry:
    """
    :param entry_id: The id of this entry.
    :param name: The name of this entry.
    :param icon: The icon for this entry. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png
    :param advancement: The name of the advancement you want this entry to be locked behind. See Locking Content with Advancements for more info on locking content.
    :param pages: The array of pages for this entry.

    https://vazkiimods.github.io/Patchouli/docs/reference/entry-json/
    """
    return BookEntry(entry_id, name, icon, pages, advancement)


def text(text_contents: str, title: str | None = None) -> BookPage:
    """
    Text pages should always be the first page in any entry. If a text page is the first page in an entry, it'll display the header you see in the left page. For all other pages, it'll display as you can see in the right one.
    :param text_contents: The text to display on this page. This text can be formatted.
    :param title An optional title to display at the top of the page. If you set this, the rest of the text will be shifted down a bit. You can't use "title" in the first page of an entry.
    :return:
    """
    if len(text_contents) > 600:
        Warnings.warn('Possibly overlong text page (%d chars)' % len(text_contents))
    return BookPage('patchouli:text', {'text': text_contents, 'title': title})


def image(*images: str, text_contents: str | None = None, border: bool = True) -> BookPage:
    """
    :param images: An array with images to display. Images should be in resource location format. For example, the value botania:textures/gui/entries/banners.png will point to /assets/botania/textures/gui/entries/banners.png in the resource pack. For best results, make your image file 256 by 256, but only place content in the upper left 200 by 200 area. This area is then rendered at a 0.5x scale compared to the rest of the book in pixel size.
    If there's more than one image in this array, arrow buttons are shown like in the picture, allowing the viewer to switch between images.
    :param text_contents: The text to display on this page, under the image. This text can be formatted.
    :param border: Defaults to false. Set to true if you want the image to be bordered, like in the picture. It's suggested that border is set to true for images that use the entire canvas, whereas images that don't touch the corners shouldn't have it.
    """
    return BookPage('patchouli:image', {'images': images, 'text': text_contents, 'border': border})


def crafting(first_recipe: str, second_recipe: str | None = None, title: str | None = None, text_contents: str | None = None) -> BookPage:
    """
    :param first_recipe: The ID of the first recipe you want to show.
    :param second_recipe: The ID of the second recipe you want to show. Displaying two recipes is optional.
    :param title: The title of the page, to be displayed above both recipes. This is optional, but if you include it, only this title will be displayed, rather than the names of both recipe output items.
    :param text_contents: The text to display on this page, under the recipes. This text can be formatted.
    Note: the text will not display if there are two recipes with two different outputs, and "title" is not set. This is the case of the image displayed, in which both recipes have the output names displayed, and there's no space for text.
    """
    return BookPage('patchouli:crafting', {'recipe': first_recipe, 'recipe2': second_recipe, 'title': title, 'text': text_contents})


# todo: other default page types: (smelting, multiblock, entity, spotlight, link) as we need them

def empty() -> BookPage:
    return BookPage('patchouli:empty', {})


if __name__ == '__main__':
    main()

import os
import warnings
from typing import NamedTuple, Tuple, Mapping

from mcresources import ResourceManager, utils
from mcresources.type_definitions import JsonObject, ResourceIdentifier


class LocalInstance:
    INSTANCE_DIR = os.getenv('LOCAL_MINECRAFT_INSTANCE')  # The location of a local .minecraft directory, for testing in external minecraft instance (as hot reloading works much better)

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None


class Warnings:
    enabled: bool = False

    @staticmethod
    def warn(content: str):
        if Warnings.enabled:
            warnings.warn(content, stacklevel=3)


def main():
    Warnings.enabled = True
    rm = ResourceManager('tfc', '../src/main/resources')

    print('Writing book')
    make_book(rm)

    Warnings.enabled = False
    if LocalInstance.wrap(rm):
        print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
        make_book(rm, 'guide')

    print('Done')


def make_book(rm: ResourceManager, root: str = 'book'):
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


    In addition, here's some useful things for dev work, and also making standardized images:

    - Images of scenery are taken in screenshots, a square section is copied and downsized to 512 x 512
    - Images of guis are taken in screenshots, then JUST THE GUI (so erase all those little pixels in the corner) is copied out. A 256 x 256 image is used, and the gui is placed horizontally centered on the FIRST 200 PIXELS (so a 176 pixel wide gui image is placed with 14 blank pixels to it's left). Make the inventory clean, but also believable (i.e. if you were just talking about items X, Y, Z, have those items in your inventory. Don't make the inventory a focal point of the image.

    - Spotlights of single blocks can be done with block_spotlight()
    - Making a resource pack ready-to-go can be done with the following command:

    jar -cMf "<root directory>\.minecraft\resourcepacks\book-images.zip" pack.mcmeta assets

    Simply copy the /assets/tfc/textures/gui/book directory from /src/ into a different folder so you ONLY get those assets in the reloadable resource pack (makes things much faster)

    """
    book = Book(rm, root)

    book.category('the_world', 'The World', 'All about the natural world around you.', 'tfc:grass/loam', is_sorted=True, entries=(
        entry('biomes', 'Biomes', '', pages=(
            # Overview of biomes and what they are, and what they affect
            # Rough overview of how biomes spawn in terms of where to find them
            # Previews of most/all biomes in a showcase mode
            text('The world is made up of $(thing)biomes$(). Biomes determine the rough shape of the landscape, the surface material, and some other features. There are several different types of biomes, from oceans to plains to hills to mountains that can be found.'),
            text('The next few pages show a few (but not all) of the biomes that you might find in the world.'),
            text('Plains are a low elevation biome, similar to hills, just above sea level. They are flat, and can contain fields of grasses and flowers, or they may be forested.', title='Plains'),
            image('tfc:textures/gui/book/biomes/plains.png', text_contents='A Plains.'),
            text('Both Hills and Rolling Hills are low to mid elevation biomes often bordering plains or higher elevation regions. Large boulders can be found here, and rarely the empty remains of volcanic hot springs.', title='Hills & Rolling Hills'),
            image('tfc:textures/gui/book/biomes/rolling_hills_with_river.png', text_contents='A Rolling Hills with a river winding through it.'),
            text('Badlands are a mid elevation continental biome, often found near plateaus, mountains, or rolling hills. Ridges with layers of sand and sandstone are common. The types of sand vary, and badlands can either be red/brown, or yellow/white, or somewhere inbetween.', title='Badlands'),
            image('tfc:textures/gui/book/biomes/badlands.png', text_contents='A Badlands.'),
            text('In high elevation areas, multiple types of mountains, may be found. Old Mountains are shorter and smoother, while Mountains stretch tall with rocky cliff faces. Mountains formed in areas of high tectonic activity can also generate hot springs, and rare volcanoes.', title='Mountains'),
            image('tfc:textures/gui/book/biomes/old_mountains.png', text_contents='An Old Mountains with a hot spring on the snowy slopes.'),
            text('In the opposite environment to towering mountains, a Lowlands can appear as a swampy, water filled biome. At or below sea level, with plenty of fresh water, they can also contain mud and plenty of vegetation.', title='Lowlands'),
            image('tfc:textures/gui/book/biomes/lowlands.png', text_contents='A Lowlands.')
        )),
        entry('waterways', 'Where the River Flows', '', pages=(
            # Overview of rivers, oceans, and lakes
            # Minor mention of underground rivers and lakes
            # Resources found in rivers + lakes: ore deposits and other gem ores
            text('While exploring, you might come across large bodies of water: rivers, lakes, or vast oceans. Rivers and lakes contain $(thing)freshwater$(), while oceans contain $(thing)saltwater$(). Drinking freshwater can restore your thirst, however drinking saltwater will deplete it over time.'),
            image('tfc:textures/gui/book/biomes/river.png', text_contents='A river.'),
            text('Rivers in TerraFirmaCraft have $(thing)current$(). They will push along items, players, and entities the same as flowing water. River currents will ultimately lead out to the ocean, joining up with other branches along the way. Occasionally, rivers will also disappear underground, and there have even been rare sightings of vast cavernous underground lakes, but will always find their way to the ocean eventually.'),
            image('tfc:textures/gui/book/biomes/underground_river.png', text_contents='A segment of an underground river.'),
            text('Lakes and rivers can also be the source of some resources. The first of which is small ore deposits. Gravel with small flecks of ores can be found in the bottom of rivers and lakes. These can be $(thing)panned$() to obtain small amounts of ores. Native Copper, Native Silver, Native Gold, and Cassiterite can be found this way.', title='Ore Deposits'),
            block_spotlight('Example', 'A native gold deposit in some slate.', 'tfc:deposit/native_gold/slate'),
            text('In addition to gravel ore deposits, lakes can also hide clusters of some gemstones. Amethyst and Opal ores can be found this way in surface level ore veins under lakes and rivers.', title='Gemstones'),
            block_spotlight('Example', 'A block of amethyst ore in limestone.', 'tfc:ore/amethyst/limestone')
        )),
        entry('geology', 'Geology', '', pages=(
            # Minor intro to plate tectonics
            # Explanation of volcanoes with pictures and how to find them, and what resources they hold in fissures
            # Hot springs, empty hot springs, and what resources they hold
        )),
        entry('the_underground', 'The Underground', '', pages=(
            # Overview of rock layers, including what rock layers appear at what altitudes
            # Brief introduction to the fact ores are rock layer specific
            # Some info about caves, possible things to find in caves
        )),
        entry('ores_and_minerals', 'Ores and Minerals', '', pages=(
            # Overview of all underground ores
            # General spawning patterns of ores (deeper = richer)
            # Indicators
            # A decent list / showcase of most/all ores and their spawning conditions
        )),
        entry('climate', 'Climate', '', pages=(
            # Overview of both temperature and rainfall and where they spawn on X/Z
            # How to check current temperature, rainfall, and climate
            # What affects current temperature
            # What temperature can affect - mainly direct stuff like snow, ice, icicles, etc.
        )),
        entry('flora', 'Flora', '', pages=(
            # Overview of various plants
            # Mention some usages (dyes)
        )),
        entry('wild_crops', 'Wild Crops', '', pages=(
            # Wild crops - how to find them, why you'd want to, what they drop
        )),
        entry('berry_bushes', 'Berry Bushes', '', pages=(
            # Berry bushes - how to find them, how to harvest and move them
        )),
        entry('fruit_trees', 'Fruit Trees', '', pages=(
            # Fruit trees - how to find them, how to harvest and move them
        )),
        entry('wild_animals', 'Wild Animals', '', pages=(
            # Wild animals - address both hostile and passive important animals
        ))
        # DON'T ADD MORE ENTRIES. If possible, because this list fits neatly on one page
    ))

    book.category('getting_started', 'Getting Started', 'An introduction to surviving in the world of TerraFirmaCraft. How to survive the stone age and obtain your first pickaxe.', 'tfc:rock/loose/granite', is_sorted=True, entries=(
        entry('introduction', 'Introduction', '', pages=(
            text('In TerraFirmaCraft, the first things you can obtain are sticks, twigs, and loose rocks. They can be found in almost every climate, lying scattered on the ground. $(thing)$(k:key.use)$() or break these to pick them up.'),
            multiblock('Example', 'A smattering of common sticks and stones.', False, pattern=(
                ('1    ', ' 2  4', '  03 ', ' 4   ', '    5'),
                ('GGGGG', 'GGGGG', 'GGGGG', 'GGGGG', 'GGGGG')
            ), mapping={
                'G': 'tfc:grass/sandy_loam',
                '1': 'tfc:rock/loose/granite[count=1]',
                '2': 'tfc:rock/loose/granite[count=2]',
                '3': 'tfc:rock/loose/granite[count=3]',
                '4': 'tfc:groundcover/stick',
                '5': 'tfc:wood/twig/ash',
            }),
            text('In addition to gathering sticks and twigs on the ground, sticks can also be obtained by breaking leaves with your fist. Once you have a number of rocks and sticks, you are ready to start $(thing)Knapping$(). Knapping is a process where two rocks are hit together, to form a particular shape. In order to knap, first hold at least two rocks in your hand, then right click in the air, which will open up the $(thing)Knapping Interface$().'),
            image('tfc:textures/gui/book/gui/rock_knapping.png', text_contents='The Knapping Interface.', border=False),
            text('In order to knap a particular item, you want to remove squares until you form the desired pattern. For example, create a knife blade by matching the recipe shown to the right.'),
            empty(),  # todo: knapping recipe
            crafting('tfc:crafting/stone/knife_sedimentary', text_contents='Once you have obtained a knife blade, in order to create a stone knife, simply craft it with a stick in your inventory.'),
            crafting('tfc:crafting/wood/stick_from_twigs', text_contents='The twigs from earlier can also be used to create sticks, if needed.'),
            item_spotlight('tfc:stone/knife/sedimentary', text_contents='Knives are a very useful tool. One of their primary uses is to collect straw by breaking plants. Most tall grasses and plants will drop straw when broken with a knife.'),
            crafting('tfc:crafting/thatch', text_contents='Straw can be used to craft one of the first building materials: $(thing)thatch$(). Thatch is a lightweight block that isn\'t affected by gravity, however players and other entities can pass right through it!'),
            text('In addition to knives, you will likely want to craft a couple other tools. $(thing)Axes$() can be used to chop down trees (finally!), and also make a useful weapon. $(thing)Hammers$() can be used as a crushing weapon, but can also be used to turn logs into sticks, by breaking log blocks with the hammer.'),
            text('Finally, $(thing)Shovels$() and ($thing)Hoes$() behave the same as they do in Vanilla, and $(thing)Javelins$() can be used as a simple toss-once-and-retrieve ranged weapon.'),
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
                'category': category_id.replace('tfc:', 'patchouli:'),
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


# todo: other default page types: (smelting, entity, link) as we need them

def item_spotlight(item: str, title: str | None = None, link_recipe: bool = False, text_contents: str | None = None) -> BookPage:
    """
    :param item: An ItemStack String representing the item to be spotlighted.
    :param title: A custom title to show instead on top of the item. If this is empty or not defined, it'll use the item's name instead.
    :param link_recipe: Defaults to false. Set this to true to mark this spotlight page as the "recipe page" for the item being spotlighted. If you do so, when looking at pages that display the item, you can shift-click the item to be taken to this page. Highly recommended if the spotlight page has instructions on how to create an item by non-conventional means.
    :param text_contents: The text to display on this page, under the item. This text can be formatted.
    """
    return BookPage('patchouli:spotlight', {'item': item, 'title': title, 'link_recipes': link_recipe, 'text': text_contents})


def block_spotlight(title: str, text_content: str, block: str) -> BookPage:
    """ A shortcut for making a single block multiblock that is meant to act the same as item_spotlight() but for blocks """
    return multiblock(title, text_content, False, (('X',), ('0',)), {'X': block})


def multiblock(title: str, text_content: str, enable_visualize: bool, pattern: Tuple[Tuple[str, ...], ...] | None = None, mapping: Mapping[str, str] | None = None, offset: Tuple[int, int, int] | None = None, multiblock_id: str | None = None) -> BookPage:
    """
    Page type: "patchouli:multiblock"

    :param title: The name of the multiblock you're displaying. Shows as a header above the multiblock display.
    :param text_content: The text to display on this page, under the multiblock. This text can be formatted.
    :param enable_visualize: Set this to false to disable the "Visualize" button.
    :param pattern: Terse explanation of the format: the pattern attribute is an array of array of strings. It is indexed in the following order: y (top to bottom), x (west to east), then z (north to south).
    :param mapping: Patchouli already provides built in characters for Air and (Any Block), which are respectively a space, and an underscore, so we don't have to account for those. Patchouli uses the same vanilla logic to parse blockstate predicate as, for example, the /execute if block ~ ~ ~ <PREDICATE> command. This means you can use block ID's, tags, as well as specify blockstate properties you want to constraint. Therefore, we have:
    :param offset: An int array of 3 values ([X, Y, Z]) to offset the multiblock relative to its center.
    :param multiblock_id: For modders only. The ID of the multiblock you want to display.
    """
    data = {'name': title, 'text': text_content, 'enable_visualize': enable_visualize}
    if multiblock_id is not None:
        return BookPage('patchouli:multiblock', {'multiblock_id': multiblock_id, **data})
    elif pattern is not None and mapping is not None:
        return BookPage('patchouli:multiblock', {'multiblock': {
            'pattern': pattern,
            'mapping': mapping,
            'offset': offset,
        }, **data})
    else:
        raise ValueError('multiblock page must have either \'multiblock\' or \'pattern\' and \'mapping\' entries')


def empty() -> BookPage:
    return BookPage('patchouli:empty', {})


if __name__ == '__main__':
    main()

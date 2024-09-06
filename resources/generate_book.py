"""

=== Translation / Localization Tools ===

Translating the book is difficult and annoying. This allows you to translate one file, keep it up to date when the book updates, and output the entire book without changing any of the content.

In order to use, all you need to do is run

> python resources book --translate <lang>

Where <lang> is your language, i.e. en_us. This will do several things
- If a <lang>.json file does not already exist, create one
- If a <lang>.json file already exists, it will read it and use it to translate text in the book
- Finally, the <lang>.json will be updated with all text actually used by the book, and missing entries will be filled in.


=== Style Guide ===

- Entries and categories are named in easy to understand resource location IDs, matching the actual in-game name wherever possible
- The book is written, generally, in second-person as guide (i.e. using 'you' pronouns)
- It SHOULD contain all information that someone would NEED to play TFC, to a reasonable degree of competence.
- It SHOULD NOT consider itself a complete reference for every added recipe, block, item, etc. (i.e. NO 'See page 143 for all crafting recipes)
- It SHOULD be devoid of subjective opinion or other player suggestions (i.e. NO 'some people prefer to skip copper tools instead of bronze').
- It MAY contain jokes or references, as long as they do not obscure the actual meaning of things.
- It SHOULD use straightforward descriptions of particular mechanics, assuming no knowledge about TFC (i.e. YES 'In order to build a fire pit, first throw (Q) a log, and three sticks onto the ground...')
- It SHOULD NOT lock content behind any advancements unless that is specifically meant to be hidden / easter egg content
- It SHOULD use crafting recipes, images, multiblocks, and other custom page types where necessary in order to improve or better the explanations being made (i.e. NO 'In order to craft a pickaxe, place the pickaxe head on top of the stick in a crafting grid')
- It SHOULD NOT contain technical/implementation details that are either beyond the obvious, not needed in order to play the game, or intended for pack maker consumption (i.e. NO 'In order to build a fire pit, throw (Q) one log item matching the tfc:fire_pit_logs tag...')
- It SHOULD NOT document bugs, unintentional features, exploits, or ANYTHING that might be resolved at a later date (i.e. 'Dont let a glow squid escape water, or your game may crash')
- It SHOULD NOT make explicit or implicit reference to other mod or addon mechanics (i.e. 'Food preservation can be done with ... or if you have TFC Tech, with a freezer')

Other writing guides (these are enforced by resource generation):

- All entries SHOULD begin with a text() page, as Patchouli creates a standardized title page for the entry when done this way.
- All entries SHOULD have an even number of pages, as it prevents adding additional implicit empty() pages at the end of the entry.
- It MAY use page_break() to enforce that specific pages are grouped together

All the documentation on entry(), category(), all the book page functions, are copied from the Patchouli documentation, found here:
https://vazkiimods.github.io/Patchouli/docs/reference/overview

=== Dev Environment Setup ===

This enables hot reloading of book content and assets.

1. Run 'python resources book --local <.minecraft directory>' with the parameter --local set to the .minecraft/ directory of a local minecraft instance. It should say "copying into local instance at <path>"
2. Compile TFC (gradlew build) and run in this local instance
3. There will now be two books:
    /give @p patchouli:guide_book{"patchouli:book":"tfc:field_guide"}  // This is the TFC book, used by the inventory screen
    /give @p patchouli:guide_book{"patchouli:book":"patchouli:field_guide"} // This it the Patchouli namespaced book, which is hot reloadable. It is loaded from /.minecraft/patchouli_books/
4. The latter book can be hot reloaded at any time:
    4.1. Run generate_book.py with the above environment variable
    4.2. While in creative, go to the book main landing page, and shift-right-click the little pen/pencil icon.

Reloading assets - tips for creating a custom resource pack. The following command just zips up two files and places the zip in the resource pack directory, ready to be enabled:

jar -cMf "<root directory>\.minecraft\resourcepacks\book-images.zip" pack.mcmeta assets

Simply copy the /assets/tfc/textures/gui/book directory from /src/ into a different folder so you ONLY get those assets in the reloadable resource pack (makes things much faster)

=== Image Standards ===

In addition, here's some useful things for dev work, and also making standardized images:

- Images of scenery are taken in screenshots, a square section is copied and downsized to 400 x 400, and then placed in the top left corner of a 512 x 512 image
- Images of guis are taken in screenshots, then JUST THE GUI (so erase all those little pixels in the corner) is copied out. A 256 x 256 image is used, and the gui is placed horizontally centered on the FIRST 200 PIXELS (so a 176 pixel wide gui image is placed with 12 blank pixels to its left).
- Make the inventory clean, but also believable (i.e. if you were just talking about items X, Y, Z, have those items in your inventory. Don't make the inventory a focal point of the image.
- DO NOT include the book item in your inventory in screenshots! It is unobtainable in survival!
- For multiple images in the same location, i.e. to show a sort of 'animated' style, use /tp @p x y z pitch yaw to get an exact positioning before taking screenshots.

"""

from argparse import ArgumentParser
from typing import Optional

from constants import CROPS, METALS, FRUITS, BERRIES, GRAINS
from patchouli import *

GRADES = ['poor', 'normal', 'rich']  # Sorted so they appear in a nice order for animation
GRADES_ALL = ['small', 'poor', 'normal', 'rich']
TOOL_METALS = [metal for metal, data in METALS.items() if data.type == 'all']
ANIMAL_NBT = '{NoAI:1b,birth:-100000000L,oldDay:9223372036854775807L,geneticSize:16}'


class LocalInstance:
    INSTANCE_DIR = None

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject, root_domain: str = 'data'):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None


def main_with_args():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us', help='The language to translate to')
    parser.add_argument('--local', type=str, default=None, help='The directory of a local .minecraft to copy into')

    args = parser.parse_args()
    main(args.translate, args.local, False)


def main(translate_lang: str, local_minecraft_dir: Optional[str], validate: bool, validating_rm: ResourceManager = None, reverse_translate: bool = False):
    LocalInstance.INSTANCE_DIR = local_minecraft_dir

    rm = ResourceManager('tfc', './src/main/resources')
    if validate:
        rm = validating_rm
    i18n = I18n(translate_lang, validate)

    print('Writing book at %s' % translate_lang)
    make_book(rm, i18n, local_instance=False, reverse_translate=reverse_translate)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying %s book into local instance at: %s' % (translate_lang, LocalInstance.INSTANCE_DIR))
        make_book(rm, I18n(translate_lang, validate), local_instance=True)

    return rm.written_files


def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False, reverse_translate: bool = False):
    book = Book(rm, 'field_guide', {}, i18n, local_instance, reverse_translate)

    book.template('multimultiblock', custom_component(0, 0, 'MultiMultiBlockComponent', {'multiblocks': '#multiblocks'}), text_component(0, 115))

    book.template('knapping_recipe', custom_component(0, 0, 'KnappingComponent', {'recipe': '#recipe'}), text_component(0, 99))
    book.template('rock_knapping_recipe', custom_component(0, 0, 'RockKnappingComponent', {'recipes': '#recipes'}), text_component(0, 99))

    book.template('quern_recipe', custom_component(0, 0, 'QuernComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('heat_recipe', custom_component(0, 0, 'HeatingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('anvil_recipe', custom_component(0, 0, 'AnvilComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('welding_recipe', custom_component(0, 0, 'WeldingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('sealed_barrel_recipe', custom_component(0, 0, 'SealedBarrelComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('instant_barrel_recipe', custom_component(0, 0, 'InstantBarrelComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('loom_recipe', custom_component(0, 0, 'LoomComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('glassworking_recipe', custom_component(0, 0, 'GlassworkingComponent', {'recipe': '#recipe'}), text_component(0, 80))
    book.template('table', custom_component(0, 0, 'TableComponent', TABLE_KEYS), text_component(0, 115))
    book.template('table_small', custom_component(0, 0, 'TableComponent', TABLE_KEYS), text_component(0, 80))

    book.category('the_world', 'The World', 'All about the natural world around you.', 'tfc:grass/loam', is_sorted=True, entries=(
        entry('geology', 'Geology', 'tfc:rock/raw/shale', pages=(
            text('The world of TerraFirmaCraft is divided into large continents - landmasses many kilometers wide and seperated by oceans. In these, you may find mountain ranges, rivers, and many other environments.'),
            image('tfc:textures/gui/book/biomes/regions.png', text_contents='A typical TFC world viewed at a large scale.'),
            page_break(),
            text('The world is also divided up into different types of $(thing)Rock$(). Rock regions can be over a kilometer across, and there will usually be two or three different rock layers under your feet at all times. As different ores are found in different rock types, locating specific rock types can be very important for finding resources such as $(l:the_world/ores_and_minerals)Ores$(), which will often only appear in certain rock types.', title='Rock Layers').anchor('rocks'),
            image('tfc:textures/gui/book/biomes/rock_layers.png', text_contents='The cross-section of a TFC world.'),
            page_break(),
            text('Ocean floors are composed of $(l:the_world/geology#igneous_extrusive)igneous extrusive$() rock - rocks that are formed by magma which cools quickly. Underneath igneous extrusive rock will likely be a $(l:the_world/geology)igneous intrusive$() rock of the same $(item)grade$().$(br2)For example, under $(thing)Basalt$() (a mafic, igneous extrusive rock), will likely be $(thing)Gabbro$() (a mafic, igneous intrusive rock).', title='Ocean Floors'),
            text('$(li)$(l:https://en.wikipedia.org/wiki/Felsic)Felsic$() rocks are $(thing)Granite$() and $(thing)Rhyolite$().$(li)$(l:https://en.wikipedia.org/wiki/Intermediate_composition)Intermediate$() rocks are $(thing)Andesite$(), $(thing)Dacite$(), and $(thing)Diorite$().$(li)$(l:https://en.wikipedia.org/wiki/Mafic)Mafic$() rocks are $(thing)Basalt$() and $(thing)Gabbro$().', title='Igneous Rock Grades'),
            text('The top layer of rock on a continent will either be igneous extrusive, or $(l:the_world/geology#sedimentary)sedimentary$(). Underneath sedimentary rocks will likely be $(l:the_world/geology#metamorphic)metamorphosed$() forms of the rock above.$(br2)For example, $(thing)Marble$() (a metamorphic rock) will likely be found under $(thing)Limestone$() or $(thing)Chalk$(). High grade metamorphic rocks are found deep under other metamorphic or igneous rocks.'),
            text('$(li)$(thing)Slate$() forms under $(thing)Shale$(), $(thing)Claystone$(), and $(thing)Conglomerate$().$(li)$(thing)Marble$() forms under $(thing)Limestone$(), $(thing)Dolomite$(), and $(thing)Chalk$().$(li)$(thing)Quartzite$() forms under $(thing)Chert$().$(li)$(thing)Phyllite$() forms under $(thing)Slate$()$(li)$(thing)Schist$() and $(thing)Gneiss$() form under $(thing)Phyllite$(), or other igneous intrusive rocks.', title='Metamorphic Rocks'),
            text('Finally, in mountainous regions you might also see $(thing)uplift$(), where a metamorphic or igneous intrusive rock is found on the surface. Uplift rocks can be found above other continental sedimentary or higher-grade metamorphic rocks.$(br2)In addition, $(l:https://en.wikipedia.org/wiki/Dike_%28geology%29)Dikes$() - small vertical slices of igneous intrusive rock - may appear scattered around the world,', title='Uplift Regions'),
            text('protruding through the upper layers of rocks.$(br2)With all that in mind, the next few pages list the rocks of all four categories: $(thing)Sedimentary$(), $(thing)Metamorphic$(), $(thing)Igneous Extrusive$(), and $(thing)Igneous Intrusive$(). These categories determine where the rock can spawn (see the previous pages), and also what ores may spawn in this rock.'),
            page_break(),
            text('$(l:https://en.wikipedia.org/wiki/Sedimentary_rock)Sedimentary$() rocks are formed by the accumulation or deposition of mineral or organic particles. They are typically found on the top layers of rock in continental areas. They are:$(br)$(li)Shale$(li)Claystone$(li)Limestone$(li)Conglomerate$(li)Dolomite$(li)Chert$(li)Chalk', title='Sedimentary').anchor('sedimentary'),
            text('$(l:https://en.wikipedia.org/wiki/Metamorphic_rock)Metamorphic$() rocks are created by a process called metamorphism. They can be found underneath corresponding sedimentary or igneous rock, or in uplift areas. They are:$(br)$(li)Quartzite$(li)Slate$(li)Phyllite$(li)Schist$(li)Gneiss$(li)Marble', title='Metamorphic').anchor('metamorphic'),
            text('$(l:https://en.wikipedia.org/wiki/Igneous_rock#Extrusive)Igneous Extrusive$() rocks are formed from magma cooling on the Earth\'s surface. They can be found on the top layer of rock in continental areas, or on the floor of oceans. They are:$(br)$(li)Rhyolite$(li)Basalt$(li)Andesite$(li)Dacite', title='Igneous Extrusive').anchor('igneous_extrusive'),
            text('$(l:https://en.wikipedia.org/wiki/Igneous_rock#Intrusive)Igneous Intrusive$() rocks are formed from magma which cooled under the Earth\'s crust. They can be found deep underground, or rarely in dikes or uplift areas. They are:$(br)$(li)Granite$(li)Diorite$(li)Gabbro', title='Igneous Intrusive').anchor('igneous_intrusive'),
            page_break(),
        )),
        entry('ores_and_minerals', 'Ores and Minerals', 'tfc:ore/normal_hematite', pages=(
            text('Ores and Minerals in TFC are rare - unlike Vanilla, ores are found in massive, sparse, yet rare veins that require some $(l:mechanics/prospecting)prospecting$() to locate. Different ores will also appear in different rock types, and at different elevations, meaning finding the right rock type at the right elevation is key to locating the ore you are looking for.'),
            text('In addition, some ores are $(thing)Graded$(). Ore blocks may be Poor, Normal, or Rich, and different veins will have different concentrations of each type of block. Veins that are $(thing)richer$() are more lucrative.$(br2)The next several pages show the different types of ores, what they look like, and where to find them.'),
            # === Metal Ores Listing ===
            page_break(),
            text('Native Copper is an ore of $(thing)Copper$() metal. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks, at elevations above y=40.$(br2)It can also be found in deposits in $(thing)rivers$(), which can be $(l:mechanics/panning)panned$().', title='Native Copper').link(*['tfc:ore/%s_%s' % (g, 'native_copper') for g in GRADES_ALL]).anchor('native_copper'),
            multimultiblock('Native Copper Ores in Dacite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_copper', 'dacite')) for g in GRADES]),
            text('Native Gold is an ore of $(thing)Gold$() metal. It can be found at elevations below y=70, but deeper veins are larger and richer. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() and $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.$(br2)It can also be found in deposits in $(thing)rivers$(), which can be $(l:mechanics/panning)panned$().', title='Native Gold').link(*['tfc:ore/%s_%s' % (g, 'native_gold') for g in GRADES_ALL]).anchor('native_gold'),
            multimultiblock('Native Gold Ores in Diorite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_gold', 'diorite')) for g in GRADES]),
            text('Native Silver is an ore of $(thing)Silver$() metal. Small poor veins can be found in $(thing)Granite$() or $(thing)Diorite$() in uplift regions, above y=90. Larger and richer veins can be found in $(thing)Granite$(), $(thing)Diorite$(), $(thing)Schist$(), and $(thing)Gneiss$() deep underground below y=20.$(br2)It can also be found in deposits in $(thing)rivers$(), which can be $(l:mechanics/panning)panned$().', title='Native Silver').link(*['tfc:ore/%s_%s' % (g, 'native_silver') for g in GRADES_ALL]).anchor('native_silver'),
            multimultiblock('Native Silver Ores in Granite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_silver', 'granite')) for g in GRADES]),
            text('Tetrahedrite is an ore of $(thing)Copper$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#metamorphic)Metamorphic$() rocks.', title='Tetrahedrite').link(*['tfc:ore/%s_%s' % (g, 'tetrahedrite') for g in GRADES_ALL]).anchor('tetrahedrite'),
            multimultiblock('Tetrahedrite Ores in Schist.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'tetrahedrite', 'schist')) for g in GRADES]),
            text('Malachite is an ore of $(thing)Copper$() metal. It can be found primarily in $(thing)Marble$() or $(thing)Limestone$(), $(thing)Chalk$(), and $(thing)Dolomite$(). It can be found at most elevations, however deeper veins are often larger and richer.', title='Malachite').link(*['tfc:ore/%s_%s' % (g, 'malachite') for g in GRADES_ALL]).anchor('malachite'),
            multimultiblock('Malachite Ores in Marble.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'malachite', 'marble')) for g in GRADES]),
            text('Cassiterite is an ore of $(thing)Tin$() metal. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks at high elevation, above y=80 in uplift regions or in dikes.$(br2)It can also be found in deposits in $(thing)rivers$(), which can be $(l:mechanics/panning)panned$().', title='Cassiterite').link(*['tfc:ore/%s_%s' % (g, 'cassiterite') for g in GRADES_ALL]).anchor('cassiterite'),
            multimultiblock('Cassiterite Ores in Diorite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'cassiterite', 'diorite')) for g in GRADES]),
            text('Bismuthinite is an ore of $(thing)Bismuth$() metal. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks near the surface, or larger and richer veins in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks deep underground.', title='Bismuthinite').link(*['tfc:ore/%s_%s' % (g, 'bismuthinite') for g in GRADES_ALL]).anchor('bismuthinite'),
            multimultiblock('Bismuthinite Ores in Shale.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'bismuthinite', 'shale')) for g in GRADES]),
            text('Garnierite is an ore of $(thing)Nickel$() metal. It can be found at elevations below y=0. It can be found primarily in $(thing)Gabbro$() deep underground. Smaller, rarer veins can also be found in any $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rock.', title='Garnierite').link(*['tfc:ore/%s_%s' % (g, 'garnierite') for g in GRADES_ALL]).anchor('garnierite'),
            multimultiblock('Garnierite Ores in Gabbro.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'garnierite', 'gabbro')) for g in GRADES]),
            text('Hematite is an ore of $(thing)Iron$() metal. It can be found in large veins in any $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks near the surface.', title='Hematite').link(*['tfc:ore/%s_%s' % (g, 'hematite') for g in GRADES_ALL]).anchor('hematite'),
            multimultiblock('Hematite Ores in Andesite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'hematite', 'andesite')) for g in GRADES]),
            text('Magnetite is an ore of $(thing)Iron$() metal. It can be found in large veins in any $(l:the_world/geology#sedimentary)Sedimentary$() rocks near the surface.', title='Magnetite').link(*['tfc:ore/%s_%s' % (g, 'magnetite') for g in GRADES_ALL]).anchor('magnetite'),
            multimultiblock('Magnetite Ores in Limestone.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'magnetite', 'limestone')) for g in GRADES]),
            text('Limonite is an ore of $(thing)Iron$() metal. It can be found in large veins in any $(l:the_world/geology#sedimentary)Sedimentary$() rocks near the surface.', title='Limonite').link(*['tfc:ore/%s_%s' % (g, 'limonite') for g in GRADES_ALL]).anchor('limonite'),
            multimultiblock('Limonite Ores in Chalk.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'limonite', 'chalk')) for g in GRADES]),
            text('Sphalerite is an ore of $(thing)Zinc$() metal. Small, poor veins can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks near the surface, and large richer veins can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks deep underground.', title='Sphalerite').link(*['tfc:ore/%s_%s' % (g, 'sphalerite') for g in GRADES_ALL]).anchor('sphalerite'),
            multimultiblock('Sphalerite Ores in Quartzite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'sphalerite', 'quartzite')) for g in GRADES]),
            page_break(),
            # === Non-Metal / Mineral Ores Listing ===
            item_spotlight('tfc:ore/lignite', 'Lignite', text_contents='Lignite is a type of low-grade $(thing)Coal$() ore. It can be found in very large flat deposits near the surface in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'lignite').anchor('lignite'),
            block_spotlight('', 'Lignite in Dolomite.', 'tfc:ore/%s/%s' % ('lignite', 'dolomite')),
            item_spotlight('tfc:ore/bituminous_coal', 'Bituminous Coal', text_contents='Bituminous Coal is a type of mid-grade $(thing)Coal$() ore. It can be found in very large flat deposits near the surface in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'bituminous_coal').anchor('bituminous_coal'),
            block_spotlight('', 'Bituminous Coal in Chert.', 'tfc:ore/%s/%s' % ('bituminous_coal', 'chert')),
            item_spotlight('tfc:kaolin_clay', 'Kaolinite', text_contents='Kaolinite is a soft $(thing)Mineral$() which is used in the construction of $(l:mechanics/fire_clay)Fire Clay$(). It can be found spawning at high altitudes in Plateaus, Old Mountains, Rolling Hills, and Highlands, at a $(l:the_world/climate#temperature)temperature$() of at least 18°C, with a $(l:the_world/climate#rainfall)rainfall$() of at least 300mm. The $(thing)Blood Lily$() flower grows on Kaolin clay.').link('tfc:red_kaolin_clay', 'tfc:pink_kaolin_clay', 'tfc:white_kaolin_clay', 'tfc:kaolin_clay_grass', 'tfc:kaolin_clay').anchor('kaolinite'),
            multimultiblock('Variants of kaolin clay.', *[two_tall_block_spotlight('', '', 'tfc:%s' % b, 'tfc:plant/blood_lily') for b in ('kaolin_clay_grass', 'red_kaolin_clay', 'white_kaolin_clay', 'pink_kaolin_clay')]),
            item_spotlight('tfc:ore/graphite', 'Graphite', text_contents='Graphite is a $(thing)Mineral$() which is used in the construction of $(l:mechanics/fire_clay)Fire Clay$(). It can be found in $(thing)Gneiss$(), $(thing)Marble$(), $(thing)Quartzite$(), and $(thing)Schist$() rocks, in elevations below y=60.').link('tfc:ore/%s' % 'graphite').anchor('graphite'),
            block_spotlight('', 'Graphite in Gneiss.', 'tfc:ore/%s/%s' % ('graphite', 'gneiss')),
            item_spotlight('tfc:ore/cinnabar', 'Cinnabar', text_contents='Cinnabar is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to obtain $(thing)Redstone Dust$(). It can be found in veins deep underground, in $(thing)Quartzite$(), $(thing)Granite$(), $(thing)Phyllite$(), and $(thing)Schist$().').link('tfc:ore/%s' % 'cinnabar').anchor('cinnabar'),
            block_spotlight('', 'Cinnabar in Quartzite.', 'tfc:ore/%s/%s' % ('cinnabar', 'quartzite')),
            item_spotlight('tfc:ore/cryolite', 'Cryolite', text_contents='Cryolite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to obtain $(thing)Redstone Dust$(). It can be found in veins deep underground, in $(thing)Granite$(), and $(thing)Diorite$().').link('tfc:ore/%s' % 'cryolite').anchor('cryolite'),
            block_spotlight('', 'Cryolite in Granite.', 'tfc:ore/%s/%s' % ('cryolite', 'granite')),
            item_spotlight('tfc:ore/saltpeter', 'Saltpeter', text_contents='Saltpeter is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used in the crafting of $(thing)Gunpowder$(). It can be found in very large flat deposits near the surface in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'saltpeter').anchor('saltpeter'),
            block_spotlight('', 'Saltpeter in Shale.', 'tfc:ore/%s/%s' % ('saltpeter', 'shale')),
            item_spotlight('tfc:ore/sulfur', 'Sulfur', text_contents='Sulfur is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used in the crafting of $(thing)Gunpowder$(). It is found near lava level deep underground, in sparse but large and plentiful veins, in any $(l:the_world/geology#metamorphic)Metamorphic$() or $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rock.').link('tfc:ore/%s' % 'sulfur').anchor('sulfur'),
            block_spotlight('', 'Sulfur in Gabbro.', 'tfc:ore/%s/%s' % ('sulfur', 'gabbro')),
            item_spotlight('tfc:ore/sylvite', 'Sylvite', text_contents='Sylvite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used as a $(l:mechanics/fertilizers)Fertilizer$(). It can be found in very large flat deposits near the surface in $(thing)Shale$(), $(thing)Claystone$() and $(thing)Chert$().').link('tfc:ore/%s' % 'sylvite').anchor('sylvite'),
            block_spotlight('', 'Sylvite in Chert.', 'tfc:ore/%s/%s' % ('sylvite', 'chert')),
            item_spotlight('tfc:ore/borax', 'Borax', text_contents='Borax is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to produce $(l:mechanics/flux)Flux$(). It can be found in very large flat deposits near the surface in $(thing)Claystone$(), $(thing)Limestone$(), and $(thing)Shale$().').link('tfc:ore/%s' % 'borax').anchor('borax'),
            block_spotlight('', 'Borax in Shale.', 'tfc:ore/%s/%s' % ('borax', 'shale')),
            item_spotlight('tfc:ore/gypsum', 'Gypsum', text_contents='Gypsum is a decorative $(thing)Mineral$() which can be used to make $(l:mechanics/advanced_building_materials#alabaster)Alabaster$(). It can be found in very large flat deposits near the surface in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'gypsum').anchor('gypsum'),
            block_spotlight('', 'Gypsum in Chalk.', 'tfc:ore/%s/%s' % ('gypsum', 'chalk')),
            item_spotlight('tfc:ore/halite', 'Halite', text_contents='Halite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to make $(thing)Salt$(), which is an important $(l:mechanics/decay#salting)Preservative$(). It can be found in very large flat deposits near the surface in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'halite').anchor('halite'),
            block_spotlight('', 'Halite in Chalk.', 'tfc:ore/%s/%s' % ('halite', 'chalk')),
            item_spotlight('tfc:ore/emerald', 'Emerald', text_contents='Emerald is a decorative $(l:mechanics/gems)Gemstone$(). It looks quite pretty, maybe if you could find someone else in this incredibly lonely world you could trade it with them...$(br2)It appears in thin vertical ore formations which can be up to a hundred blocks tall. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.').link('tfc:ore/%s' % 'emerald').anchor('emerald'),
            block_spotlight('', 'Emerald in Diorite.', 'tfc:ore/%s/%s' % ('emerald', 'diorite')),
            item_spotlight('tfc:ore/diamond', 'Kimberlite', text_contents='Kimberlite is a decorative and priceless $(l:mechanics/gems)Gemstone$(). It appears in thin vertical ore formations called $(l:https://en.wikipedia.org/wiki/Volcanic_pipe)Kimberlite Pipes$() which can be up to a hundred blocks tall. It can only be found in $(thing)Gabbro$().').link('tfc:ore/%s' % 'diamond').anchor('diamond'),
            block_spotlight('', 'Kimberlite in Gabbro.', 'tfc:ore/%s/%s' % ('diamond', 'gabbro')),
            item_spotlight('tfc:ore/lapis_lazuli', 'Lapis Lazuli', text_contents='Lapis Lazuli is a decorative $(thing)Mineral$() which can be used to make $(l:mechanics/dye)Blue Dye$(). It can be found in large, but sparse veins in $(thing)Limestone$() and $(thing)Marble$(), between y=-20 and y=80.').link('tfc:ore/%s' % 'lapis_lazuli').anchor('lapis_lazuli'),
            block_spotlight('', 'Lapis Lazuli in Marble.', 'tfc:ore/%s/%s' % ('lapis_lazuli', 'marble')),
            item_spotlight('tfc:ore/amethyst', 'Amethyst', text_contents='Amethyst is a decorative $(thing)Mineral$() which can be used to make $(l:mechanics/glassworking)Glass$(). It can be found in $(thing)Sedimentary$() and $(thing)Metamorphic$() rock beneath rivers above y=40.').link('tfc:ore/%s' % 'amethyst').anchor('amethyst'),
            block_spotlight('', 'Amethyst in Marble.', 'tfc:ore/%s/%s' % ('amethyst', 'marble')),
            item_spotlight('tfc:ore/opal', 'Opal', text_contents='Opal is a decorative $(thing)Mineral$(). It can be found in $(thing)Sedimentary) and $(thing)Igneous Extrusive$() rock beneath rivers above y=40.').link('tfc:ore/%s' % 'opal').anchor('opal'),
            block_spotlight('', 'Opal in Basalt.', 'tfc:ore/%s/%s' % ('amethyst', 'basalt')),
        )),
        entry('climate', 'Calendar and Climate', 'tfc:textures/gui/book/icons/thermometer.png', pages=(
            # Overview of both temperature and rainfall and where they spawn on X/Z
            # How to check current temperature, rainfall, and climate
            # What affects current temperature
            # What temperature can affect - mainly direct stuff like snow, ice, icicles, etc.
            text('In TerraFirmaCraft, the climate and the time are both very important factors. Let\'s start with the $(thing)Calendar$().$(br2)At any time, you can view the calendar by pressing $(item)$(k:key.inventory)$(), and clicking on the calendar tab. This will show the $(thing)Season$(), the $(thing)Day$(), and the $(thing)Date$().').anchor('calendar'),
            image('tfc:textures/gui/book/gui/calendar.png', text_contents='The Calendar Screen', border=False),
            text('There are seasons, and the weather and climate will change along with them! There are four seasons in TerraFirmaCraft, each divided up into $(thing)Early$(), $(thing)Mid$() and $(thing)Late$() months. The four seasons are:$(br)$(li)$(bold)Spring$(): March - May$(li)$(bold)Summer$(): June - August$(li)$(bold)Autumn$(): September - November$(li)$(bold)Winter$(): December - February'),
            text('The current season can influence the temperature of the area, the precipitation (if it will rain or snow), among other things. Pay attention to the calendar tab, it will be useful!$(br2)Now, onto the climate...'),
            page_break(),
            text('Another tab on the main inventory screen is the $(thing)Climate$() screen. This one shows information about the current location$(br2)The first line shows the overall $(l:https://en.wikipedia.org/wiki/K%C3%B6ppen_climate_classification)Climate$() .$(br2)The second line shows the $(l:the_world/geology)Geologic Province$().$(br2)The third line shows the $(thing)Average Annual Temperature$().', title='Climate').anchor('climate'),
            image('tfc:textures/gui/book/gui/climate.png', text_contents='The Climate Screen', border=False),
            text('Temperature in TerraFirmaCraft is influenced by a number of factors:$(br)$(li)Firstly, the region, especially the latitude (Z coordinate) will play the largest role.$(li)Secondly, the current season will influence the temperature - it will be hottest during Summer, and coldest during Winter.$(li)Finally, the temperature can be different day to day as well as varying from hour to hour.').anchor('temperature'),
            text('The last line shows the current temperature, including all these aforementioned factors.$(br2)Temperature can influence many things: if crops and plants will grow, if snow and ice will form or melt, and more.'),
            page_break(),
            text('Rainfall is another climate value that can vary depending on where you are in the world. The annual rainfall is measured in millimeters (mm) and can be between 0mm - 500mm. Rainfall affects the types of flora that are found in an area, and also the types of soil, from sand and cacti, to loam, to silt and kapok trees.', title='Rainfall').anchor('rainfall'),
            text('Rainfall is also important as it affects what things can be grown in an area. Rainfall is one of the main contributors to $(l:mechanics/hydration)Hydration$(), which is an exact measure of how wet the soil is in a given location, and is used by $(l:mechanics/crops)Crops$(), $(l:the_world/wild_fruits#fruit_trees)Fruit Trees$(), and $(l:the_world/wild_fruits#tall_bushes)Berry Bushes$() to determine if they can grow.'),
        )),
        entry('flora', 'Flora', 'tfc:plant/goldenrod', pages=(
            # Overview of various plants
            # Mention some usages (dyes)
            text('There are many, many, $(italic)many$() different types of plants in TerraFirmaCraft.$(br2)Different plants appear in different $(l:the_world/climate)Climates$(), and their appearance may change over the current season - going through cycles of flowering and laying dormant, or changing color as the local temperature changes. Colorful flowers can typically be crushed in a $(l:mechanics/quern)Quern$() for $(l:mechanics/dye)Dye$().'),
            block_spotlight('Standard', 'Standard plants are like small flowers. They grow on grass, dirt, and farmland.', 'tfc:plant/anthurium'),
            block_spotlight('Dry', 'Dry plants are like standard plants, but they can grow on sand. These generally only spawn in areas with low rainfall.', 'tfc:plant/sagebrush'),
            two_tall_block_spotlight('Cacti', 'Cacti can grow two blocks high, and they will damage you!', 'tfc:plant/barrel_cactus[part=lower]', 'tfc:plant/barrel_cactus[part=upper]').anchor('cacti'),
            block_spotlight('Creeping', 'Creeping plants take the shape of the surrounding block faces. They mostly spawn in blobs on the ground', 'tfc:plant/moss[down=true]'),
            multiblock('Epiphyte', 'Epiphytes only live on the sides of logs.', False, pattern=(('XY',), ('0 ',)), mapping={'X': 'tfc:wood/wood/birch', 'Y': 'tfc:plant/licorice_fern[facing=south]'}),
            block_spotlight('Short Grass', 'Short grass blocks grow taller with age. They also are able to grow on peat and mud.', 'tfc:plant/bluegrass'),
            two_tall_block_spotlight('Tall Grass', 'Tall grass blocks are just tall enough to block your field of view.', 'tfc:plant/king_fern[part=lower]', 'tfc:plant/king_fern[part=upper]'),
            block_spotlight('Vines', 'Vine blocks spread around the world on their own, if it\'s warm enough.', 'tfc:plant/ivy[up=true,north=true]'),
            two_tall_block_spotlight('Weeping Vines', 'Weeping vines grow downward from an anchoring block.', 'tfc:plant/liana', 'tfc:plant/liana_plant'),
            two_tall_block_spotlight('Twisting Vines', 'Twisting vines twist upward from the Earth. They can come in a solid variant.', 'tfc:plant/arundo_plant', 'tfc:plant/arundo'),
            text('Water plants are restricted to either spawn in fresh or salty water. Otherwise, they grow and act much like the plants you see on the surface. Some water plants can be cooked for food.', title='Water Plants'),
            block_spotlight('Standard Water', 'Standard water plants can be broken with a $(thing)$()Knife$() to get $(thing)Seaweed$().', 'tfc:plant/sago[fluid=water]'),
            block_spotlight('Water Grass', 'Water grasses are grasses that grow under the water.', 'tfc:plant/manatee_grass[fluid=salt_water]'),
            two_tall_block_spotlight('Tall Water Plant', 'Tall water plants can grow with just the bottom block in water. $(thing)Water Taro$() and $()Cattail$() can be broken with a $(thing)Knife$() for $(thing)Roots$().', 'tfc:plant/cattail[part=lower,fluid=water]', 'tfc:plant/cattail[part=upper]'),
            two_tall_block_spotlight('Floating', 'Floating plants sit on top of the water. Boats will break them on contact.', 'minecraft:water', 'tfc:plant/duckweed'),
            two_tall_block_spotlight('Kelp', 'Kelp are twisting vines that grow underwater.', 'tfc:plant/winged_kelp_plant[fluid=salt_water]', 'tfc:plant/winged_kelp[fluid=salt_water]'),
            two_tall_block_spotlight('Tree Kelp', 'Tree kelp grow into intricate trees underwater. The flowers can be harvested with a $(thing)Knife$().', 'tfc:plant/giant_kelp_plant[down=true,up=true,fluid=salt_water]', 'tfc:plant/giant_kelp_flower[facing=up,fluid=salt_water]'),  # note: anyone want to make a nice multiblock for this?
        )),
        entry('wild_crops', 'Wild Crops', 'tfc:textures/gui/book/icons/wild_crops.png', pages=(
            # Wild crops - how to find them, why you'd want to, what they drop
            text('$(thing)Wild Crops$() can be found scattered around the world, growing in small patches. They can be harvested for food and seeds, which can then be cultivated themselves in the not-wild form.$(br2)Harvesting wild crops can be done with your fists, or with a $(thing)Knife$() or other sharp tool. When broken, they will drop $(thing)Seeds$() and some $(thing)Products$().').link('#tfc:wild_crops'),
            block_spotlight('Wild Wheat', 'An example of a wild crop, in this case $(l:mechanics/crops#wheat)Wheat$().', 'tfc:wild_crop/wheat[mature=true]'),
            text('Every $(l:mechanics/crops)crop$() that can be cultivated can also be found in the wild. Wild crops will look similar to their cultivated counterparts, but are more hidden within the grass. Wild crops are only mature from June to October. Otherwise, they appear dead until the next Summer.'),
            multimultiblock('All different varieties of wild crop', *(
                block_spotlight('', '', 'tfc:wild_crop/barley'),
                block_spotlight('', '', 'tfc:wild_crop/oat'),
                block_spotlight('', '', 'tfc:wild_crop/rye'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/maize[part=bottom]', 'tfc:wild_crop/maize[part=top]'),
                block_spotlight('', '', 'tfc:wild_crop/barley'),
                block_spotlight('', '', 'tfc:wild_crop/rice[fluid=water]'),
                block_spotlight('', '', 'tfc:wild_crop/beet'),
                block_spotlight('', '', 'tfc:wild_crop/cabbage'),
                block_spotlight('', '', 'tfc:wild_crop/carrot'),
                block_spotlight('', '', 'tfc:wild_crop/garlic'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/green_bean[part=bottom]', 'tfc:wild_crop/green_bean[part=top]'),
                block_spotlight('', '', 'tfc:wild_crop/potato'),
                block_spotlight('', '', 'tfc:wild_crop/onion'),
                block_spotlight('', '', 'tfc:wild_crop/soybean'),
                block_spotlight('', '', 'tfc:wild_crop/squash'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/sugarcane[part=bottom]', 'tfc:wild_crop/sugarcane[part=top]'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/tomato[part=bottom]', 'tfc:wild_crop/tomato[part=top]'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/jute[part=bottom]', 'tfc:wild_crop/jute[part=top]'),
                block_spotlight('', '', 'tfc:wild_crop/pumpkin'),
                block_spotlight('', '', 'tfc:wild_crop/melon'),
            )),
            text('Wild crops will spawn in climates near where the crop itself can be cultivated, so if looking for a specific crop, look in the climate where the crop can be cultivated. However, unlike $(l:mechanics/crops)Crops$() that the player has planted, wild crops do not require $(l:mechanics/hydration)Hydration$(). Instead, they are found in areas depending on the average $()Temperature$() and $()Rainfall$().', title='Finding Wild Crops'),
            text('The next pages show a table of the environments where wild crops can be found.'),
            table(
                make_crop_table(0, 11),
                '', 'Wild Crop Requirements', {}, [],
                2, 80, 70, 10, 2, 12, False
            ),
            table(
                make_crop_table(12, len(CROPS.keys())),
                '', 'Wild Crop Requirements', {}, [],
                2, 80, 70, 10, 2, 12, False
            ),
        )),
        entry('wild_fruits', 'Wild Fruits', 'tfc:food/elderberry', pages=(
            # Wild fruits
            text('Many different varieties of wild fruits can be found growing in the world. These can be collected to be eaten, or farmed, with the right equipment. These can be found on different varieties of bushes or trees. In general, fruits can be found in three types of plants: $(l:the_world/wild_fruits#fruit_trees)Fruit Trees$(), $(l:the_world/wild_fruits#tall_bushes)Tall Bushes$(), and $(l:the_world/wild_fruits#small_bushes)Small Bushes$().$(br2)All fruiting plants have a common lifecycle. They will grow, form flowers, sprout fruit, and then lay dormant in a yearly cycle.'),
            text('Fruit plants are seasonal. During their cold season, these plants will appear brown and lifeless. In the spring, they become green and healthy, getting ready to produce fruit and grow larger. The exact times this happen varies by the fruit. Fruit plants can die, as well: of old age, and of improper climate conditions.'),
            text('$(thing)Fruit trees$() grow from tiny saplings into large, flowering trees. The branches of fruit trees are their heart, and they will grow as long as the climate conditions are right. As fruit trees mature, they will grow $(thing)leaves$() all around their branches. The leaves can flower and fruit depending on the season.', title='Fruit Trees').anchor('fruit_trees'),
            image('tfc:textures/gui/book/tutorial/fruit_tree.png', text_contents='A typical fruit tree.'),
            text('Fruit trees start out at $(thing)Saplings$(). Saplings will only start growing, placing their first piece of the tree, if it is not the dormant season for that fruit. The size of the finished tree is loosely determined by how many saplings are in the original sapling block. More saplings means a bigger tree.$(br)More saplings can be added to a single block through $(thing)Splicing$(). To splice a sapling into another, just $(item)$(k:key.use)$() on it while holding a sapling and a $(thing)Knife$() in your off hand.'),
            text('To get saplings from a fruit tree, break the \'elbow\' blocks (branch blocks that are attached to a block on one side and above) tree with an $(thing)Axe$(). Saplings can also be placed on these \'elbow\' sections, if they are not too high up in the tree. This allows one fruit tree to grow multiple fruits. Harvesting fruit is done with $(item)$(k:key.use)$() when the leaf block is bearing fruit. This will give one fruit, and revert the plant back to its growing stage, until it goes dormant for the winter.'),
            page_break(),
            fruit_tree_text('cherry', 'Cherry Tree', '$(br2)Cherry trees grow in the months of January through March, start flowering in April and May, and bear fruit in June.'),
            fruit_tree_multiblock('cherry', 'An example cherry tree.'),
            page_break(),
            fruit_tree_text('green_apple', 'Green Apple Tree', '$(br2)Green apple trees grow from March to July, start flowering in August and September, and bear fruit in October.'),
            fruit_tree_multiblock('green_apple', 'An example green apple tree.'),
            page_break(),
            fruit_tree_text('lemon', 'Lemon Tree', '$(br2)Lemon trees grow from February to May, start flowering in June and July, and bear fruit in August.'),
            fruit_tree_multiblock('lemon', 'An example lemon tree.'),
            page_break(),
            fruit_tree_text('olive', 'Olive Tree', '$(br2)Olive trees grow from March to July, start flowering in August and September, and bear fruit in October.$(br2)Olives can be used to produce $(l:mechanics/lamps#olives)Olive Oil$(), which can be used as a fuel for lamps.'),
            fruit_tree_multiblock('olive', 'An example olive tree.'),
            page_break(),
            fruit_tree_text('orange', 'Orange Tree', '$(br2)Orange trees grow from March to June, start flowering in July and August, and bear fruit in September.'),
            fruit_tree_multiblock('orange', 'An example orange tree.'),
            page_break(),
            fruit_tree_text('peach', 'Peach Tree', '$(br2)Peach trees grow from December to March, start flowering in April and May, and bear fruit in June.'),
            fruit_tree_multiblock('peach', 'An example peach tree.'),
            page_break(),
            fruit_tree_text('plum', 'Plum Tree', '$(br2)Plum trees grow from January to April, start flowering in May and June, and bear fruit in July.'),
            fruit_tree_multiblock('plum', 'An example plum tree.'),
            page_break(),
            fruit_tree_text('red_apple', 'Red Apple Tree', '$(br2)Red Apple trees grow from March to July, start flowering in August and September, and bear fruit in October.'),
            fruit_tree_multiblock('red_apple', 'An example red apple tree.'),
            page_break(),
            fruit_tree_text('banana', 'Banana Tree', '$(br2)Bananas are a special kind of fruit tree. They grow only vertically, lack leaves, and only fruit at the topmost block. Saplings are dropped from the flowering part of the plant. Once a banana plant is harvested, it dies, and will not produce any more fruit. It must be replanted in the spring.'),
            multimultiblock('An example banana tree.', *[
                multiblock('', '', False, pattern=(('Y',), ('X',), ('X',), ('X',), ('0',)), mapping={
                    'Y': 'tfc:plant/banana_plant[stage=2,lifecycle=%s]' % life,
                    'X': 'tfc:plant/banana_plant[stage=1]',
                    '0': 'tfc:plant/banana_plant[stage=0]'
                })
                for life in ('dormant', 'healthy', 'flowering', 'fruiting')
            ]),
            table([
                '', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
                'Cherry', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D', 'D', 'D', 'D',
                'Apple',  'D', 'D', 'H', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D',
                'Lemon',  'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D', 'D',
                'Olive',  'D', 'D', 'H', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D',
                'Orange', 'D', 'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D',
                'Peach',  'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D', 'D', 'D', 'H',
                'Plum',   'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D', 'D', 'D',
                'Banana',  'D', 'D', 'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D',
            ],
                'The fruiting calendar for fruit trees.',
                'Fruit Trees',
                {'D': {'fill': '0xa8986a'}, 'H': {'fill': '0x6ab553'}, 'F': {'fill': '0xcca0db'}, 'R': {'fill': '0xa217ff'}},
                [{'text': 'Dormant', 'color': '0xa8986a'}, {'text': 'Healthy', 'color': '0x6ab553'}, {'text': 'Flowering', 'color': '0xcca0db'}, {'text': 'Fruiting', 'color': '0xa217ff'}],
                12, 45, 15, 10, 5, 11
            ),
            text('$(thing)Tall Bushes$() are fruit blocks that are able to grow in all directions, and spread. They do this by either growing directly upwards, up to three high, or placing $(thing)canes$() on their sides, which can mature into full bush blocks. After a while, the bushes will stop spreading, and reach maturity. Harvesting these bushes with a sharp tool has a chance to drop a new bush. Bushes that are fully mature will always drop themselves.', title='Tall Bushes').anchor('tall_bushes'),
            image('tfc:textures/gui/book/tutorial/berry_bush.png', text_contents='A wild tall bush.'),
            text('Tall bushes are able to spread when their canes have somewhere to take root. Practically, this means that they need a solid block under them to place a new bush on. Providing a flat, open area free of grass or other debris gives them the best chance to grow.'),
            text('Bushes, unlike fruit trees, take into account surrounding water blocks to determine their $(l:mechanics/hydration)Hydration$(), unlike fruit trees, which only care about rainfall.$(br)Any full bush block can grow berries, which are harvestable with $(item)$(k:key.use)$().'),
            page_break(),
            tall_bush_text('blackberry', 'Blackberry Bush', '$(br2)Blackberry bushes grow from February to May, start flowering in June and July, and bear fruit in August.$(br2)They can be found in areas with few trees.'),
            tall_bush_multiblock('blackberry', 'An example blackberry bush.'),
            page_break(),
            tall_bush_text('raspberry', 'Raspberry Bush', '$(br2)Raspberry bushes grow from April to July, start flowering in August and September, and bear fruit in October.$(br2)They can be found in areas with few trees.'),
            tall_bush_multiblock('raspberry', 'An example raspberry bush.'),
            page_break(),
            tall_bush_text('blueberry', 'Blueberry Bush', '$(br2)Blueberry bushes grow from February to May, start flowering in June and July, and bear fruit in August.$(br2)They can be found in areas with few trees.'),
            tall_bush_multiblock('blueberry', 'An example blueberry bush.'),
            page_break(),
            tall_bush_text('elderberry', 'Elderberry Bush', '$(br2)Elderberry bushes grow from February to May, start flowering in June and July, and bear fruit in August.$(br2)They can be found in areas with few trees.'),
            tall_bush_multiblock('elderberry', 'An example elderberry bush.'),
            page_break(),
            text('$(thing)Small Bushes$() are a kind of low lying fruit block that spawns in forests. Small bushes occasionally will spread to surrounding blocks, if there aren\'t too many other bushes nearby.$(br2)Small bushes will go through three sizes, and when grown, they are harvested just with $(item)$(k:key.use)$().', title='Small Bushes').anchor('small_bushes'),
            multimultiblock('Three different sizes of a healthy small bush', *[block_spotlight('', '', 'tfc:plant/bunchberry_bush[lifecycle=healthy,stage=%s]' % stage) for stage in range(0, 3)]),
            page_break(),
            small_bush_text('bunchberry', 'Bunchberry Bush', '$(br2)Bunchberry bushes grow from May to July, start flowering in August and September, and bear fruit in October.$(br2)They can be found in forests.'),
            small_bush_multiblock('bunchberry', 'The monthly stages of a bunchberry bush.'),
            page_break(),
            small_bush_text('gooseberry', 'Gooseberry Bush', '$(br2)Gooseberry bushes grow from April to July, start flowering in August and September, and bear fruit in October.$(br2)They can be found in forests.'),
            small_bush_multiblock('gooseberry', 'The monthly stages of a gooseberry bush.'),
            page_break(),
            small_bush_text('snowberry', 'Snowberry Bush', '$(br2)Snowberry bushes grow from March to June, start flowering in July and August, and bear fruit in September.$(br2)They can be found in forests.'),
            small_bush_multiblock('snowberry', 'The monthly stages of a snowberry bush.'),
            page_break(),
            small_bush_text('cloudberry', 'Cloudberry Bush', '$(br2)Cloudberry bushes grow from February to May, start flowering in June to August, and bear fruit in September.$(br2)They can be found in forests.'),
            small_bush_multiblock('cloudberry', 'The monthly stages of a cloudberry bush.'),
            page_break(),
            small_bush_text('strawberry', 'Strawberry Bush', '$(br2)Strawberry bushes grow from October to December, start flowering in January and February, and bear fruit in March.$(br2)They can be found in forests.'),
            small_bush_multiblock('strawberry', 'The monthly stages of a strawberry bush.'),
            page_break(),
            small_bush_text('wintergreen_berry', 'Wintergreen Berry Bush', '$(br2)Wintergreen berry bushes grow from May to September, start flowering in October and November, and bear fruit in December.$(br2)They can be found in forests.'),
            small_bush_multiblock('wintergreen_berry', 'The monthly stages of a wintergreen berry bush.'),
            page_break(),
            small_bush_text('cranberry', 'Cranberry Bush', '$(br2)Cranberry bushes grow from March to June, start flowering in July and August, and bear fruit in September.$(br2)They can be found in forests. Unlike most small bushes, cranberry bushes are grown underwater.'),
            small_bush_multiblock('cranberry', 'The monthly stages of a cranberry bush.'),
            table([
                '', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
                'Snowberry',   'D', 'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D',
                'Bunchberry',  'D', 'D', 'D', 'D', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D',
                'Gooseberry',  'D', 'D', 'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D',
                'Cloudberry',  'D', 'H', 'H', 'H', 'H', 'F', 'F', 'F', 'R', 'D', 'D', 'D',
                'Strawberry',  'F', 'F', 'R', 'D', 'D', 'D', 'D', 'D', 'D', 'H', 'H', 'H',
                'Wintergreen', 'D', 'D', 'D', 'H', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D',
                'Cranberry',   'D', 'D', 'H', 'H', 'H', 'H', 'F', 'F', 'R', 'D', 'D', 'D',
            ],
                'The fruiting calendar for berry bushes.',
                'Berry Bushes',
                {'D': {'fill': '0xa8986a'}, 'H': {'fill': '0x6ab553'}, 'F': {'fill': '0xcca0db'}, 'R': {'fill': '0xa217ff'}},
                [{'text': 'Dormant', 'color': '0xa8986a'}, {'text': 'Healthy', 'color': '0x6ab553'}, {'text': 'Flowering', 'color': '0xcca0db'}, {'text': 'Fruiting', 'color': '0xa217ff'}],
                12, 45, 15, 10, 5, 11
            ),
        )),
        entry('wild_animals', 'Wild Animals', 'tfc:medium_raw_hide', pages=(
            # Wild animals - address both hostile and passive important animals
            text('The world of TFC is full of animal life. Some animals are here to help, and some are incredibly dangerous. This section is about wild animals. For information on livestock, animals that can give you items you need, see the $(l:mechanics/animal_husbandry)Animal Husbandry$() page.'),
            text('Animals can be grouped into a few different categories: $(l:the_world/wild_animals#predators)Predators$(), $(l:the_world/wild_animals#prey)Prey$(), and $(l:the_world/wild_animals#aquatic)Aquatic$() animals.$(br2)The next few pages will detail each of these categories of animals.'),
            page_break(),
            text('$(thing)Predators$() are animals that can attack the player. They are either $(thing)Nocturnal$(), only hunting at night, or $(thing)Diurnal$(), only hunting during the day. Predators can be neutral or hostile, depending on if they have killed a target recently. Predators have a home territory that they will defend - if you run far enough away, the predator will stop chasing and return home.', title='Predators').anchor('predators'),
            entity('tfc:polar_bear', 'The polar bear spawns in only the coldest regions, $(l:the_world/climate#temperature)temperature$() at most 10°C, with a $(l:the_world/climate#rainfall)rainfall$() of at least 100mm.', 'Polar Bear', 0.55),
            entity('tfc:grizzly_bear', 'The grizzly bear spawns in forests of moderate climates, with a $(l:the_world/climate#temperature)temperature$() range of -15 to 15°C and at least 200mm of $(l:the_world/climate#rainfall)rainfall$().', 'Grizzly Bear', 0.55),
            entity('tfc:black_bear', 'The black bear spawns in forests of warmer, wetter climates, of $(l:the_world/climate#temperature)temperature$() 5 to 20°C and at least 250mm of $(l:the_world/climate#rainfall)rainfall$().', 'Black Bear', 0.55),
            entity('tfc:cougar', 'The cougar prefers most moderate climates, with $(l:the_world/climate#temperature)temperature$() from -10 to 21°C and at least 150mm of $(l:the_world/climate#rainfall)rainfall$().', 'Cougar', 0.6),
            entity('tfc:panther', 'The panther prefers most moderate climates, with $(l:the_world/climate#temperature)temperature$() from -10 to 21°C and at least 150mm of $(l:the_world/climate#rainfall)rainfall$().', 'Panther', 0.6),
            entity('tfc:lion', 'The lion spawns in plains with an average $(l:the_world/climate#temperature)temperature$() of at least 16°C, and $(l:the_world/climate#rainfall)rainfall$() between 50 and 300mm.', 'Lion', 0.55),
            entity('tfc:tiger', 'The tiger spawns in forests with an average $(l:the_world/climate#temperature)temperature$() of at least 13°C, and $(l:the_world/climate#rainfall)rainfall$() above 100mm.', 'Tiger', 0.55),
            entity('tfc:sabertooth', 'The sabertooth spawns at any $(l:the_world/climate#temperature)temperature$() above 0°C, and any $(l:the_world/climate#rainfall)rainfall$() above 250mm.', 'Sabertooth', 0.6),
            entity('tfc:wolf', 'The wolf spawns at any $(l:the_world/climate#temperature)temperature$() below 22°C, and $(l:the_world/climate#rainfall)rainfall$() between 150 and 420mm. They hunt in packs. Feeding a wolf enough times will tame it into a $(l:mechanics/pets)Dog$().'),
            entity('tfc:direwolf', 'The direwolf spawns at any $(l:the_world/climate#temperature)temperature$() below freezing, and $(l:the_world/climate#rainfall)rainfall$() between 150 and 420mm. They hunt in packs. They are larger than regular wolves, and are not tamable.'),
            entity('tfc:hyena', 'The hyena spawns at any $(l:the_world/climate#temperature)temperature$() warmer than 15°C, and $(l:the_world/climate#rainfall)rainfall$() between 80 and 380mm. They hunt in packs. They are not tamable.'),
            entity('tfc:crocodile', 'The crocodile spawns near rivers, lakes, and marshes at any $(l:the_world/climate#temperature)temperature$() warmer than 15°C, and at any $(l:the_world/climate#rainfall)rainfall$(). They are nocturnal, and most dangerous in the water.', 'Crocodile', 0.7),
            entity('tfc:ocelot', 'The ocelot attacks small animals. It spawns at any $(l:the_world/climate#temperature)temperature$() from 15 to 30°C, and $(l:the_world/climate#rainfall)rainfall$() between 300 and 500mm, in forests. Feeding an ocelot enough times will tame it into a $(l:mechanics/pets)Cat$(). Ocelots can eat raw fish'),
            page_break(),
            text('$(thing)Ramming$() animals occasionally attempt to charge nearby creatures, including the player. Their attacks are powerful, but can be dodged. They will ram more frequently if attacked.', title='Ramming').anchor('ramming_prey'),
            entity('tfc:boar', 'The boar spawns at $(l:the_world/climate#temperature)temperature$() below 25°C and above -5°C, and $(l:the_world/climate#rainfall)rainfall$() between 130 and 400mm.', 'Boar'),
            entity('tfc:moose', 'The moose spawns in forests at $(l:the_world/climate#temperature)temperature$() below 10°C and above -15°C, and $(l:the_world/climate#rainfall)rainfall$() between 150 and 300mm.'),
            entity('tfc:wildebeest', 'The wildebeest spawns in open plains at any $(l:the_world/climate#temperature)temperature$() above 13°C, and $(l:the_world/climate#rainfall)rainfall$() between 90 and 380mm.'),
            page_break(),
            text('$(thing)Prey$() animals fear players and predators. They are adept at fleeing from danger, but generally cannot fight back. Some prey animals enjoy snacking on crops.', title='Prey').anchor('prey'),
            entity('tfc:rabbit', 'The rabbit is known to chew on carrots and cabbage. They are ubiquitous in the world, changing their coat based on climate. They only need 15mm of $(l:the_world/climate#rainfall)rainfall$() to spawn.', 'Rabbit'),
            entity('tfc:fox', 'The fox likes to eat the berries off of bushes. It can be found in forests with $(l:the_world/climate#temperature)temperature$() below 25°C, and $(l:the_world/climate#rainfall)rainfall$() between 130 and 400mm.', 'Fox'),
            entity('tfc:deer', 'The deer spawns in forests at any $(l:the_world/climate#temperature)temperature$() below 25°C and above -15°C, and $(l:the_world/climate#rainfall)rainfall$() between 130 and 400mm.'),
            entity('tfc:caribou', 'The caribou spawns at any $(l:the_world/climate#temperature)temperature$() below -9°C, and $(l:the_world/climate#rainfall)rainfall$() between 110 and 500mm.', 'Caribou', 0.6),
            entity('tfc:gazelle', 'The gazelle spawns in open plains at any $(l:the_world/climate#temperature)temperature$() above 12°C, and $(l:the_world/climate#rainfall)rainfall$() between 90 and 380mm.'),
            entity('tfc:bongo', 'The bongo spawns in forests at any $(l:the_world/climate#temperature)temperature$() above 15°C, and $(l:the_world/climate#rainfall)rainfall$() between 230 and 500mm.', 'Bongo', 0.9),
            entity('tfc:grouse', 'The grouse spawns at any $(l:the_world/climate#temperature)temperature$() below 13°C and above -12°C, and $(l:the_world/climate#rainfall)rainfall$() between 150 and 400mm.'),
            entity('tfc:pheasant', 'The pheasant spawns in forests at any $(l:the_world/climate#temperature)temperature$() below 17°C and above -5°C, and $(l:the_world/climate#rainfall)rainfall$() between 100 and 300mm.'),
            entity('tfc:turkey', 'The turkey spawns in forests at any $(l:the_world/climate#temperature)temperature$() below 17°C and above 0°C, and $(l:the_world/climate#rainfall)rainfall$() between 250 and 450mm.'),
            entity('tfc:peafowl', 'The peafowl spawns in forests at any $(l:the_world/climate#temperature)temperature$() above 14°C, and $(l:the_world/climate#rainfall)rainfall$() between 190 and 500mm.', 'Peafowl', 0.8),
            empty(),
            page_break(),
            text('$(thing)Aquatic Animals$() are a broad category which covers a number of different behaviors. They may be $(thing)Shore Dwellers$(), $(thing)Fish$(), $(thing)Shellfish$(), or $(thing)Large Aquatic Creatures$()', title='Aquatic Animals').anchor('aquatic'),
            text('$(thing)Shore Animals$() only spawn on sea shores and spend some of their day swimming, and some walking on the beach. They are curious creatures, and will follow the player around, but cannot be tamed.'),
            entity('tfc:penguin', 'The penguin spawns in only the coldest beaches, with $(l:the_world/climate#temperature)temperature$() of at most -14°C and $(l:the_world/climate#rainfall)rainfall$() of at least 75mm.', 'Penguin'),
            entity('tfc:turtle', 'The sea turtle likes warm water. It spawns in $(l:the_world/climate#temperature)temperature$() of at least 21°C and $(l:the_world/climate#rainfall)rainfall$() of at least 250mm.', 'Sea Turtle'),
            page_break(),
            text('$(thing)Fish$() are small creatures that swim in water. Most of them can be $(l:mechanics/fishing)fished$(). Some prefer oceans, rivers, or lakes.', title='Fish'),
            entity('tfc:cod', 'Cod prefer colder oceans, $(l:the_world/climate#temperature)temperature$() at most 18°C. They can be fished.', 'Cod'),
            entity('tfc:pufferfish', 'Pufferfish live in any ocean with at least a $(l:the_world/climate#temperature)temperature$() of 10°C.', 'Pufferfish'),
            entity('tfc:jellyfish', 'Jellyfish live in warmer oceans, with a $(l:the_world/climate#temperature)temperature$() of at least 18°C.', 'Jellyfish'),
            entity('tfc:tropical_fish', 'Tropical fish prefer warmer oceans, with a $(l:the_world/climate#temperature)temperature$() of at least 18°C.', 'Tropical Fish'),
            entity('tfc:salmon', 'Salmon spawn in any river or lake with a $(l:the_world/climate#temperature)temperature$() of at least -5°C.', 'Salmon'),
            entity('tfc:bluegill', 'Bluegill spawn in any river or lake with a $(l:the_world/climate#temperature)temperature$() of at least -10°C and at most 26°C.', 'Bluegill'),
            text('$(thing)Shellfish$() are small animals that live on the floor of bodies of water. They cannot be fished, but drop shells that can be eaten or made into $(l:mechanics/flux)flux$(). Shellfish can be $(l:mechanics/fishing)bait$() for fish.', title='Shellfish').anchor('shellfish'),
            entity('tfc:isopod', 'Isopods spawn in deeper sections of oceans of $(l:the_world/climate#temperature)temperature$() at most 14°C.', 'Isopod'),
            entity('tfc:lobster', 'Lobster spawn in any ocean that is at most of a $(l:the_world/climate#temperature)temperature$() of 21°C.', 'Lobster'),
            entity('tfc:crayfish', 'Crayfish are like lobster, but spawn in rivers and lakes. They need a $(l:the_world/climate#temperature)temperature$() of at least 5°C and a $(l:the_world/climate#rainfall)rainfall$() of at least 125mm.', 'Crayfish'),
            entity('tfc:horseshoe_crab', 'Horseshoe crabs spawn in oceans of moderate climate, $(l:the_world/climate#temperature)temperature$() between 10 and 21°C and with a $(l:the_world/climate#rainfall)rainfall$() of at most 400mm.', 'Horseshoe Crab'),
            page_break(),
            text('$(thing)Large Water Creatures$() are larger animals that live in the bigger bodies of water. Some of them hunt fish. They drop $(l:mechanics/lamps#tallow)blubber$(), which can be made into lamp fuel.', title='Large Water Creatures'),
            entity('tfc:orca', 'Orca whales live in deep oceans with $(l:the_world/climate#temperature)temperature$() of at most 19°C and $(l:the_world/climate#rainfall)rainfall$() of at least 100mm.', 'Orca', scale=0.25),
            entity('tfc:dolphin', 'Dolphins live in deep oceans with $(l:the_world/climate#temperature)temperature$() of at least 10°C and $(l:the_world/climate#rainfall)rainfall$() of at least 200mm.', 'Dolphin', scale=0.4),
            entity('tfc:manatee', 'Manatees live in any warm lake, with $(l:the_world/climate#temperature)temperature$() of at least 20°C and $(l:the_world/climate#rainfall)rainfall$() of at most 300mm.', 'Manatee', scale=0.25),
            text('Squid can spawn in any deep ocean. They drop $(thing)Ink Sacs$(), and ink any player that gets too close. Some say that squids in deep, unexplored caves have strange properties.', 'Squid'),
            empty_last_page(),
        )),
    ))
    book.category('getting_started', 'Getting Started', 'An introduction to surviving in the world of TerraFirmaCraft. How to survive the stone age and obtain your first pickaxe.', 'tfc:stone/axe/sedimentary', is_sorted=True, entries=(
        entry('introduction', 'Introduction', 'tfc:rock/loose/granite', pages=(
            text('In TerraFirmaCraft, the first things you can obtain are sticks, twigs, and loose rocks. They can be found in almost every climate, lying scattered on the ground. $(item)$(k:key.use)$() or break these to pick them up.'),
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
            text('In order to knap a particular item, you want to remove squares until you form the desired pattern. For example, create a knife blade by matching the recipe shown to the right.$(br2)Like crafting recipes, the location of the desired pattern doesn\'t matter for the output, and some recipes have multiple variants that are valid.'),
            rock_knapping_typical('knife_head', 'A knife blade, crafted from several different rock types.').link(*link_rock_categories('tfc:stone/knife_head/%s'), *link_rock_categories('tfc:stone/knife/%s')).anchor('knife'),
            crafting('tfc:crafting/stone/knife/sedimentary', text_contents='All stone tool heads can be crafted with a stick or twig to assemble them into a tool.$(br2)The stone knife can be used to harvest $(thing)Straw$() by breaking plants.'),
            rock_knapping_typical('axe_head', '$(thing)Axes$() can be used to cut down entire trees, logs and leaves included. However, saplings and sticks will only be obtained if leaves were broken individually.').link(*link_rock_categories('tfc:stone/axe_head/%s'), *link_rock_categories('tfc:stone/axe/%s')),
            rock_knapping_typical('shovel_head', '$(thing)Shovels$() can be used to dig soil type blocks. They can also be used to create $(thing)Paths$() by $(item)using$() them on $(thing)Dirt$() or $(thing)Grass$().').link(*link_rock_categories('tfc:stone/shovel_head/%s'), *link_rock_categories('tfc:stone/shovel/%s')),
            rock_knapping_typical('hoe_head', '$(thing)Hoes$() are useful tools for $(l:mechanics/crops)Farming$(), but they can also be used to cut down leaves and other plant matter.').link(*link_rock_categories('tfc:stone/hoe_head/%s'), *link_rock_categories('tfc:stone/hoe/%s')),
            rock_knapping_typical('hammer_head', '$(thing)Hammers$() can be used as $(l:mechanics/damage_types)Crushing$() weapons, but are more important tools used for $(l:getting_started/primitive_anvils)Forging$().').link(*link_rock_categories('tfc:stone/hammer_head/%s'), *link_rock_categories('tfc:stone/hammer/%s')),
            rock_knapping_typical('javelin_head', 'Finally, a $(thing)Javelin$() can be used as a primitive weapon, capable of being thrown at targets, or used as a $(l:mechanics/damage_types)Piercing$() weapon.').link(*link_rock_categories('tfc:stone/javelin_head/%s'), *link_rock_categories('tfc:stone/javelin/%s')),
        )),
        entry('firepit', 'Pits of Fire', 'tfc:firepit', pages=(
            text('$(thing)Fire$() is an important technological advancement. In order to create fire, you will need a $(thing)Firestarter$(). In order to use, simply hold $(item)$(k:key.use)$() down on the ground. After a few moments, smoke, and then fire will be created. It may take a couple tries to light successfully.').anchor('firestarter').link('tfc:firepit'),
            crafting('tfc:crafting/firestarter', text_contents='Crafting a firestarter can be done with two sticks.'),
            text('With a firestarter, it is now possible to make a $(thing)Firepit$(). In order to make one, you will need one $(thing)log$(), three $(thing)sticks$(), and optionally up to five pieces of $(thing)kindling$(). Kindling can be items such as paper products, straw, or pinecones, and will increase the chance of successfully creating a firepit by 10 percent per item used. Throw ($(item)$(k:key.drop)$()) all the items on the ground, on the same block. Then use the firestarter on the block with the items floating above it.', 'Firepit'),
            block_spotlight('', 'If you were successful, a firepit will be created.', 'tfc:firepit[lit=true]'),
            text('Using the firepit will now open the firepit screen. On the left are four $(thing)fuel$() slots. Logs, Peat, and Stick Bundles can all be used as firepit fuel by placing them in the topmost slot. Fuel will be consumed from the bottommost slot. There is a gauge which displays the current $(thing)Temperature$() of the firepit, and on the right, a slot for items to be $(l:mechanics/heating)heated$() in.'),
            image('tfc:textures/gui/book/gui/firepit.png', text_contents='The Firepit Screen', border=False),
            heat_recipe('tfc:heating/torch_from_stick', 'Many useful items can be made in a firepit by heating them. Sticks can be heated, where they will produce two $(thing)Torches$(). Note that torches will eventually burn out, and need to be re-lit by using a $(thing)Firestarter$(), or using another $(thing)Torch$() on them.').link('tfc:torch', 'minecraft:torch'),
            heat_recipe('tfc:heating/food/cooked_cod', 'The fire pit is also a good device for $(thing)cooking food$(). All raw meats and doughs can be cooked in a firepit, which will lengthen their shelf life. (More on that $(l:mechanics/decay)here$())'),
            text('The firepit can be extinguished at any time by using a $(thing)Shovel$() on it.$(br2)A firepit can also have other devices added to it to extend its functionality. Using a $(thing)Wrought Iron Grill$() will convert the firepit into a $(l:mechanics/grill)Grill$(), and using a $(thing)Ceramic Pot$() will convert the firepit into a $(l:mechanics/pot)Pot$(). To remove either device, $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$(). Be careful not to try to remove a hot grill or pot!'),
            multimultiblock(
                'A firepit, with either a grill or pot added.',
                block_spotlight('', '', 'tfc:firepit'),
                block_spotlight('', '', 'tfc:grill'),
                block_spotlight('', '', 'tfc:pot'),
            ),
            text('Firepit fuels have different levels of purity. Adding impure fuels to fires makes them more smokey. If the fire burns very impurely, smoke will start to fly very high in the air. The most pure fuels are logs, with pine being the least pure log. Fuels like pinecones and fallen leaves do not hot enough to do much cooking, and are very impure.'),
            empty_last_page()
        )),
        entry('pottery', 'Pottery', 'tfc:ceramic/vessel', pages=(
            text('$(thing)Clay$() is an incredibly useful and balanced material which can be used for pottery. It can prove challenging to locate at first. Clay is usually hidden by grass, but it is often found in two locations. In areas with at least 175mm $(l:the_world/climate#rainfall)Annual Rainfall$(), clay can be found in patches all over the place, usually marked by the presence of certain $(thing)Plants$().').link('minecraft:clay'),
            multiblock('Clay Indicators', 'Clay, with one of the plants that may indicate its presence.', False, pattern=(
                ('   ', ' C ', '   '),
                ('XXX', 'X0X', 'XXX')
            ), mapping={
                '0': 'tfc:clay_grass/sandy_loam',
                'X': 'tfc:clay_grass/sandy_loam',
                'C': '#tfc:clay_indicators',
            }),
            text('$(thing)Athyrium Fern$(), $(thing)Canna$(), $(thing)Goldenrod$(), $(thing)Pampas Grass$(), $(thing)Perovskia$(), and $(thing)Water Canna$() all indicate the presence of clay nearby. Clay can also be found in smaller deposits close to water sources, such as rivers, lakes, or ponds.$(br2)Like with rocks, clay can be knapped to form new items. It requires five clay in your hand to knap. Unlike rocks, if you make a mistake, you can simply close the knapping interface, reshape your clay, and try again.').link('#tfc:clay_indicators'),
            image('tfc:textures/gui/book/gui/clay_knapping.png', text_contents='The Knapping Interface.', border=False),
            text('The small vessel is one such item. Like all pottery items, it must be $(l:https://en.wikipedia.org/wiki/Pottery)fired$() before it can be used. Firing is a process of $(l:mechanics/heating)heating$() the item up to a point where the clay will turn into a hard $(thing)Ceramic$() material, which requires heating to 1400 °C, or $(e)$(bold)$(t:Yellow White)Yellow White$().$(br2)In order to do this in the early game, you will need to use a $(l:getting_started/pit_kiln)Pit Kiln$().', title='Small Vessel').link('tfc:ceramic/unfired_vessel').link('tfc:ceramic/vessel').anchor('vessel'),
            knapping('tfc:knapping/ceramic/unfired_vessel', 'Knapping a Clay Small Vessel.'),
            text('Another useful pottery item is the $(thing)Jug$(). It can be used to pick up and $(thing)drink$() fluids, such as fresh water.$(br2)In order to use it, simply $(item)$(k:key.use)$() the jug on the fluid in the world. Then use the jug in order to drink from it. The jug can hold $(thing)100 mB$() of fluid at a time.', title='Jug').link('tfc:ceramic/unfired_jug').link('tfc:ceramic/jug').anchor('jug'),
            knapping('tfc:knapping/ceramic/unfired_jug', 'Knapping a Clay Jug.'),
            text('Clay is also necessary for making $(thing)Molds$(). Molds can have molten metal poured into them, which will eventually solidify into the shape of a mold. The item and potentially the mold can then be retrieved by using $(item)$(k:key.use)$() on the mold.$(br2)The most simple type of mold is the ingot mold, to the right.', title='Molds').anchor('mold'),
            knapping('tfc:knapping/ceramic/unfired_ingot_mold', 'Knapping a Clay Ingot Mold.').link('tfc:ceramic/unfired_ingot_mold', 'tfc:ceramic/ingot_mold'),
            heat_recipe('tfc:heating/ceramic/ingot_mold', 'The mold then needs to be fired, like all clay items, to be usable - likely in a $(l:getting_started/pit_kiln)Pit Kiln$().$(br2)Once it is fired, molten metal can be poured in. Once the metal has cooled enough, it can be extracted.'),
            item_spotlight('tfc:ceramic/ingot_mold[tfc:fluid={id:"tfc:metal/copper",amount:100}]', 'Casting', text_contents='The next few pages show several of the knapping patterns for various tools.'),
            knapping('tfc:knapping/ceramic/unfired_propick_head_mold', 'A $(l:mechanics/prospecting#propick)Prospector\'s Pick$() is an essential tool for locating large quantities of ore.').link('tfc:ceramic/unfired_propick_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_pickaxe_head_mold', 'A $(thing)Pickaxe$()! The bread and butter tool for mining.').link('tfc:ceramic/unfired_pickaxe_head_mold', 'tfc:ceramic/pickaxe_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_saw_blade_mold', 'A $(thing)Saw$() is a tool which is required in order to craft advanced wooden components like a $(thing)Workbench$() along with many other devices like $(l:mechanics/support_beams)Supports$().').link('tfc:ceramic/unfired_saw_blade_mold', 'tfc:ceramic/saw_blade_mold'),
            knapping('tfc:knapping/ceramic/unfired_scythe_blade_mold', 'A $(thing)Scythe$() is a tool that can harvest plants and leaves in a 3x3x3 area!').link('tfc:ceramic/unfired_scythe_blade_mold', 'tfc:ceramic/scythe_blade_mold'),
            knapping('tfc:knapping/ceramic/unfired_chisel_head_mold', 'A $(l:mechanics/chisel)Chisel$() is a tool used for smoothing blocks as well as creating a large number of decorative blocks.').link('tfc:ceramic/unfired_chisel_head_mold', 'tfc:ceramic/chisel_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_axe_head_mold', 'An $(thing)Axe$() for all your tree chopping purposes. Note that stone axes are less efficient than metal!').link('tfc:ceramic/unfired_axe_head_mold', 'tfc:ceramic/axe_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_hammer_head_mold', 'A $(thing)Hammer$() is an essential tool to create and work on $(l:mechanics/anvils)Anvils$().').link('tfc:ceramic/unfired_hammer_head_mold', 'tfc:ceramic/hammer_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_knife_blade_mold', 'A $(thing)Knife$() can be used as a weapon, or as a cutting tool for plant type blocks.').link('tfc:ceramic/unfired_knife_blade_mold', 'tfc:ceramic/knife_blade_mold'),
            knapping('tfc:knapping/ceramic/unfired_hoe_head_mold', 'A $(thing)Hoe$() used for planting and maintaining $(l:mechanics/crops)Crops$().').link('tfc:ceramic/unfired_hoe_head_mold', 'tfc:ceramic/hoe_head_mold'),
            knapping('tfc:knapping/ceramic/unfired_shovel_head_mold', 'A $(thing)Shovel$() for all your digging purposes.').link('tfc:ceramic/unfired_shovel_head_mold', 'tfc:ceramic/shovel_head_mold'),
        )),
        entry('pit_kiln', 'Pit Kilns', 'tfc:textures/gui/book/icons/pit_kiln.png', pages=(
            text('A pit kiln is an early game method of $(l:mechanics/heating)heating$() items up. It can be used to $(thing)fire$() clay into ceramic, for example. The pit kiln, over the time period of about eight hours, will heat its contents up to 1400 °C, or $(bold)$(e)$(t:Yellow White)Yellow White$().'),
            text('To build a pit kiln, you will need:$(br)$(li)Up to four items to be fired.$(li)Eight pieces of $(thing)Straw$()$(li)Eight $(thing)Logs$()$(li)An item capable of lighting fires, like a $(l:getting_started/firepit#firestarter)Firestarter$(), or a $(thing)Torch$().$(br2)$(bold)Note:$() Torches can start fires simply by tossing the torch on the pit kiln and waiting a few seconds.'),
            text('In order to create a pit kiln:$(br2)$(bold)1.$() Place up to four items down in a 1x1 hole with $(item)$(k:tfc.key.place_block)$().$(br)$(bold)2.$() Use eight $(thing)Straw$() on the pit kiln, until the items are covered.$(br)$(bold)3.$() Use eight $(thing)Logs$() on the pit kiln, until full.$(br)$(bold)4.$() Light the top of the pit kiln on fire!$(br2)The pit kiln will then burn for eight hours, slowly $(l:mechanics/heating)heating$() the items inside up.'),
            image(*['tfc:textures/gui/book/tutorial/pit_kiln_%d.png' % i for i in range(1, 1 + 5)], text_contents='Tutorial: creating a pit kiln.')
        )),
        entry('finding_ores', 'Ores, Metal, and Casting', 'tfc:ore/normal_native_copper', pages=(
            # Surface prospecting
            text('In addition to sticks, twigs, and stones on the ground, in your travels you may encounter small pieces of ores scattered around the ground. These are important, as they are one of the only sources of ore and metal before obtaining a pickaxe.'),
            multiblock('', 'All small ore pieces', False, pattern=(('    ', '  0 ', '    '), ('ABCD', 'EFGH', 'IJKL'), ('XXXX', 'XXXX', 'XXXX')), mapping={
                'X': 'tfc:grass/loam',
                **{k: 'tfc:ore/small_%s' % v for k, v in zip('ABCDEFGHIJKL', ('native_copper', 'native_gold', 'hematite', 'native_silver', 'cassiterite', 'bismuthinite', 'garnierite', 'malachite', 'magnetite', 'limonite', 'sphalerite', 'tetrahedrite'))},
            }),
            text('These small ore pieces can serve two purposes: they can provide a source of metal, and more importantly, they indicate the presence of a larger vein of ore somewhere nearby, probably underground and close to the surface. Be sure to note where you find small ores, as the location of ore veins will be useful later during $(l:mechanics/prospecting)Prospecting$().$(br2)The twelve types of small ores, and the metal they can be melted into are listed on the next page.'),
            text('$(li)Native Copper ($(thing)Copper$())$(li)Native Gold ($(thing)Gold$())$(li)Hematite ($(thing)Cast Iron$())$(li)Native Silver ($(thing)Silver$())$(li)Cassiterite ($(thing)Tin$())$(li)Bismuthinite ($(thing)Bismuth$())$(li)Garnierite ($(thing)Nickel$())$(li)Malachite ($(thing)Copper$())$(li)Magnetite ($(thing)Cast Iron$())$(li)Limonite ($(thing)Cast Iron$())$(li)Sphalerite ($(thing)Zinc$())$(li)Tetrahedrite ($(thing)Copper$())', title='Small Ores'),
            text('In TerraFirmaCraft, ores each contain a certain number of $(thing)units$(), or $(thing)mB (millibuckets)$() of actual metal which can be extracted. Small ores like this found on the surface are the lowest quality, and only provide $(thing)10 mB$() of metal. In order to extract this metal, it needs to be $(thing)melted$(), and made into tools using a process called $(thing)casting$().', title='Casting').anchor('casting'),
            text('You will need:$(br)$(li)A $(l:getting_started/pottery#vessel)Small Vessel$()$(li)Enough materials for a $(l:getting_started/pit_kiln)Pit Kiln$().$(li)One or more $(l:getting_started/pottery#mold)Mold(s)$() to cast the molten metal.$(li)And finally, at least 100 mB total of a metal which is suitable for casting: $(thing)Copper$(), in one or more of its three ore forms.$(br2)$(br)$(italic)Note: Casting can also be done with some $(l:getting_started/primitive_alloys)Alloys$()'),
            text('First, open the $(thing)Small Vessel$() and put the ores inside. Count up the total amount of metal in the ores carefully! Then, you need to build a $(l:getting_started/pit_kiln)Pit Kiln$() with the filled small vessel inside. As the vessel heats, the ores inside it will melt, and you\'ll be left with a vessel of molten metal.$(br2)Take the vessel out and $(item)$(k:key.use)$() it, to open the $(thing)Casting$() interface.'),
            image('tfc:textures/gui/book/gui/casting.png', text_contents='The Casting Interface.', border=False),
            text('With the casting interface open, place your empty fired mold in the center slot. It will fill up as long as the vessel remains liquid. (If the vessel solidifies, it can be reheated in another pit kiln.) Once the mold is full, it can be removed and left to cool. Once cool, the mold and its contents can be extracted by using the mold, or putting it in the crafting grid.'),
            crafting('tfc:crafting/metal/pickaxe/copper', text_contents='With a tool head in hand, you are now able to craft your first pickaxe! Find enough copper to make a pickaxe head, fire a pickaxe mold and melt the ore using a pit kiln, then cast a head. Slap it on a stick, and voila!'),
        )),
        entry('primitive_alloys', 'Primitive Alloys', 'tfc:ceramic/ingot_mold[tfc:fluid={id:"tfc:metal/bronze",amount:100}]', pages=(
            text('$(thing)Alloys$() are a method of mixing two or more metals together, to create a new, stronger metal. During the early game, while copper is a useful metal for creating tools, the next tier of metal is one of three types of $(thing)Bronze$(). An alloy is made up of component $(thing)metals$() which must each satisfy a specific percentage of the overall whole.'),
            text('One method through which alloys can be made during the early game is through the usage of a $(thing)Small Vessel$(). The process is very similar to $(l:getting_started/finding_ores#casting)Casting$(). However, instead of using just a single metal, place enough ore pieces inside the vessel in the correct ratio to form a known alloy mix.'),
            text('For example, to create 1000 mB of $(thing)Bronze$() (shown to the right), you would need between 880 and 920 mB of $(thing)Copper$(), and between 80 and 120 mB of $(thing)Tin$().$(br2)The next three pages show the recipes of the three bronzes. Each type of bronze can be used to make tools, $(l:mechanics/armor)Armor$(), and other metal items. They are slightly different so resulting tools will have different durability, efficiency, and attack damage.'),
            alloy_recipe('Bronze', 'bronze', '').link('tfc:metal/ingot/bronze'),
            alloy_recipe('Bismuth Bronze', 'bismuth_bronze', '').link('tfc:metal/ingot/bismuth_bronze'),
            alloy_recipe('Black Bronze', 'black_bronze', '').link('tfc:metal/ingot/black_bronze'),
            text('Other alloys are not suitable for creating tools. These are $(thing)Brass$(), $(thing)Rose Gold$(), and $(thing)Sterling Silver$().$(br2)$(thing)Brass$() is a useful alloy for all sorts of contraptions, including $(l:mechanics/mechanical_power)Mechanical Power$().$(br2)$(thing)Rose Gold$() and $(thing)Sterling Silver$() are used for crafting $(l:mechanics/steel#red_steel)Red Steel$() and $(l:mechanics/steel#blue_steel)Blue Steel$().', title='Utility Alloys'),
            alloy_recipe('Brass', 'brass', '').link('tfc:metal/ingot/brass').anchor('brass'),
            alloy_recipe('Rose Gold', 'rose_gold', ''),
            alloy_recipe('Sterling Silver', 'sterling_silver', ''),
        )),
        entry('primitive_anvils', 'Primitive Anvils', 'tfc:rock/anvil/granite', pages=(
            text('An alternative to casting tools directly in the early game, and a requirement for higher tier metals, is to use an $(thing)Anvil$(). An anvil is a block which can be used for two different processes: $(l:mechanics/anvils#working)Working$() and $(l:mechanics/anvils#welding)Welding$(). This chapter is just going to show you how to obtain your first primitive stone anvil.').link(*['tfc:rock/anvil/%s' % r for r in ('granite', 'diorite', 'gabbro', 'rhyolite', 'basalt', 'andesite', 'dacite')]),
            text('First, you need to acquire a block of $(thing)Raw Rock$(), that is $(thing)Igneous Extrusive$() (Rhyolite, Basalt, Andesite, or Dacite) or $(thing)Igneous Intrusive$() (Granite, Diorite, or Gabbro). You could find and use an exposed block in the world, or you could $(l:getting_started/primitive_anvils#raw_rock)extract one$() from the surrounding rock.'),
            text('You will also need any material of $(thing)Hammer$(). In order to make the anvil, simply right click the exposed $(thing)top$() face of one of those raw rock blocks with your $(thing)hammer$(), and voila! An anvil will be formed.$(br2)Anvils have $(l:mechanics/anvils#tiers)tiers$() and the rock anvil is Tier 0 - the lowest tier. It is only able to $(l:mechanics/anvils#welding)Weld$() Tier I ingots.', title='Rock Anvil').anchor('stone_anvils'),
            multimultiblock(
                'Converting the center raw rock to an anvil.',
                multiblock('', '', False, ((' 0 ',), ('RRR',)), {'0': 'AIR', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, multiblock_id='tfc:rock_anvil'),
            ),
            text('In order to obtain a raw rock block without breaking it into smaller rocks, it needs to be $(thing)extracted$(). You must mine the blocks on all six sides of a raw rock block - once it is surrounded by air on all sides - it will pop off as a item which can be picked up.', title='Obtaining Raw Rock').anchor('raw_rock'),
            multimultiblock(
                'Mining all six sides of a piece of raw stone - once complete, the center block will pop off as an item.',
                multiblock('', '', False, (('   ', ' R ', '   '), (' R ', 'RRR', ' R '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), (' R ', 'RRR', ' R '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), ('   ', 'RRR', ' R '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), ('   ', ' RR', ' R '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), ('   ', ' RR', '   '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), ('   ', ' R ', '   '), ('   ', ' 0 ', '   ')), {'0': 'tfc:rock/raw/gabbro', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, (('   ', '   ', '   '), ('   ', ' R ', '   '), ('   ', ' 0 ', '   ')), {'0': 'AIR', 'R': 'tfc:rock/raw/gabbro'}),
            ),
        )),
        entry('building_materials', 'Building Materials', 'tfc:wattle/unstained', pages=(
            text('In the early stages of the game, building can be a challenge as many sturdy building blocks require metal tools to obtain. However, there are a few building blocks that can be obtained with just stone tools.$(br2)More $(l:mechanics/advanced_building_materials)building blocks$() are obtainable with metal tools.'),
            text('$(br)  1. $(l:getting_started/building_materials#thatch)Thatch$()$(br)  2. $(l:getting_started/building_materials#mud_bricks)Mud Bricks$()$(br)  3. $(l:getting_started/building_materials#wattle_and_daub)Wattle and Daub$()$(br)  4. $(l:getting_started/building_materials#clay_and_peat)Clay Blocks and Peat$()$(br)', title='Contents'),
            page_break(),
            # Thatch
            text('With just a $(l:getting_started/introduction#knife)Stone Knife$(), you are able to obtain $(thing)Straw$() by breaking plant like blocks. This can be used to craft a very simple building material: $(thing)Thatch$(). Thatch is a lightweight block that isn\'t affected by gravity, however players and other entities can pass right through it! It can also be crafted back into $(thing)Straw$() if needed.', title='Thatch').anchor('thatch'),
            crafting('tfc:crafting/thatch', 'tfc:crafting/straw'),
            # Mud Bricks
            crafting('tfc:crafting/drying_bricks/loam', text_contents='$(thing)Mud$() can be found on the ground, underneath rivers and lakes, or in patches in low elevation swampy environments. With a little bit of $(thing)Straw$(), it can be crafted into $()Wet Mud Bricks$().', title='Mud Bricks').anchor('mud_bricks'),
            multimultiblock(
                'These can be placed on the ground in a dry location, and after a day they will harden into $(thing)Mud Bricks$().',
                two_tall_block_spotlight('', '', 'tfc:grass/loam', 'tfc:drying_bricks/loam[count=4,dried=false]'),
                two_tall_block_spotlight('', '', 'tfc:grass/loam', 'tfc:drying_bricks/loam[count=4,dried=true]'),
            ),
            crafting('tfc:crafting/mud_bricks/loam', text_contents='These dried mud bricks can then be crafted into $(thing)Mud Brick Blocks$(). They can also be made into $(thing)Stairs$(), $(thing)Slabs$(), or $(thing)Walls$(), if so desired.', title=' '),
            block_spotlight('', 'All different varieties of mud bricks.', '#tfc:mud_bricks'),
            page_break(),
            # Wattle and Daub
            text('$(thing)Wattle$() and $(thing)Daub$() is a versatile building and decoration block.$(br2)$(thing)Wattle$() can be crafted and placed, however it is breakable and allows players and mobs to walk through it. It can be augmented with $(thing)Daub$(), to make it solid.', 'Wattle and Daub').anchor('wattle_and_daub'),
            crafting('tfc:crafting/wattle', text_contents='In order to make $(thing)Wattle$() solid, it first must be woven, which requires adding sticks to the structure.'),
            crafting('tfc:crafting/daub', 'tfc:crafting/daub_from_mud', text_contents=''),
            image('tfc:textures/gui/book/tutorial/wattle_weave.png', text_contents='Four sticks are required to $(thing)weave$() wattle.'),
            text('At any time, sticks can be added on each diagonal, as well as the top and bottom. Hold a single $(thing)Stick$() in your hand and $(item)$(k:key.use)$() it to add a stick. Change what part of the wattle you\'re adding the stick to by selecting a different side of the face.'),
            image('tfc:textures/gui/book/tutorial/wattle_add_stick.png', text_contents='Adding sticks to wattle.'),
            image('tfc:textures/gui/book/tutorial/wattle_add_daub.png', text_contents='Using $(thing)Daub on $(thing)Woven Wattle$() creates a solid block.'),
            image('tfc:textures/gui/book/tutorial/wattle_stained.png', text_contents='It can then be $(thing)stained$() by using $(l:mechanics/dye)Dye$() on it.'),
            page_break(),
            # Clay Blocks and Peat
            # todo: Peat spawning details, screenshot?
            text('Clay obtained from the ground can be crafted into clay blocks. When placed and dug again, they\'ll turn back into clay. While unattractive, they are easy to obtain.$(br2)Peat spawns in the world in patches along water bodies and is mineable with stone tools. Some plants will grow on it.$(br2)However, peat is quite flammable.', title='Clay Blocks and Peat').anchor('clay_and_peat'),
            crafting('minecraft:clay', title='Clay Blocks'),
        )),
        entry('a_place_to_sleep', 'A Place to Sleep', 'tfc:medium_raw_hide', pages=(
            text('Just kidding! The $(thing)Thatch Bed$() is a primitive bed which can be used to set your spawn, although not to sleep through the night. To make a thatch bed, place two $(thing)Thatch$() blocks adjacent to each other, then right click with a $(thing)Large Raw Hide$(). Large hides are dropped by larger animals, like $(thing)bears$() and $(thing)cows$().').link('tfc:thatch_bed'),
            multiblock('Thatch Bed', 'A constructed thatch bed.', False, mapping={'0': 'tfc:thatch_bed[part=head,facing=east]', 'D': 'tfc:thatch_bed[part=foot,facing=east]'}, pattern=((' D ', ' 0 '),)),
        )),
        entry('size_and_weight', 'Size and Weight', 'tfc:textures/gui/book/icons/size_and_weight.png', pages=(
            text('Every item has a $(thing)Size ⇲$() and $(thing)Weight \u2696$(). An item\'s size and weight is shown on the $(thing)tooltip$(), which appears when you hover over it with your mouse.'),
            text('Size determines what storage blocks an item can fit inside of.$(br)$(li)$(thing)Tiny$() items fit in anything.$(li)$(thing)Very Small$() items fit in anything.$(li)$(thing)Small$() items are the largest item that will fit in $(l:mechanics/decay#small_vessels)Small Vessels$()$().$(li)$(thing)Normal$() items are the largest that will fit in $(l:mechanics/decay#small_vessels)Large Vessels$().', title='Size ⇲'),
            text('$(li)$(thing)Large$() items are the largest that will fit in Chests. $(l:getting_started/pit_kiln)Pit Kilns$() can hold four.$(li)$(thing)Very Large$() items are placed alone in Pit Kilns.$(li)$(thing)Huge$() items do not fit in any normal storage device. They also count towards overburdening.'),
            text('Weight determines the max stack size of items.$(br)$(li)$(thing)Very Light$(): 64$(li)$(thing)Light$(): 32$(li)$(thing)Medium$(): 16$(li)$(thing)Heavy$(): 4$(li)$(thing)Very Heavy$(): 1$(br2)Most items are $(thing)Very Light$() by default. Blocks are usually $(thing)Medium$().', title='Weight \u2696'),
            text('$(thing)Overburdening$() happens when you carry items that are both $(thing)Huge$() and $(thing)Very Heavy$(). Carrying just one $(thing)Huge, Very Heavy$() item causes you to become exhausted, making your food burn quicker. Carrying two or more gives you the $(thing)Overburdened$() status effect, which makes movement very slow.'),
            text('Items that count towards overburdening include $(thing)Anvils$(), $(thing)Sealed Barrels$(), $(thing)Crucibles$() (with contents)$(br2)Note: $(l:mechanics/animal_husbandry#horses)Horses$() can also be overburdened.', title='Overburdening').anchor('overburdening'),
        )),
        entry('food_and_water', 'Food and Water', 'tfc:food/orange', pages=(
            text('In TerraFirmaCraft, not only must you manage your hunger, but you must manage your thirst. Hunger works similar to vanilla. Most pieces of food restore about a fifth of your hunger bar. Some pieces of food may restore a bit less, such as $(thing)Cattail Roots$(). Eating food also restores $(thing)Saturation$(), which can be thought of as how full you are.'),
            text('Some foods are very filling, so they keep you from losing hunger for longer, while some foods have a short-lived effect.$(br2)You will lose hunger just from regular gameplay. Hunger drains faster if you do things like sprint, swim, or become $(l:getting_started/size_and_weight#overburdening)Overburdened$(). Information about the $(thing)Saturation$(), $(thing)Water$(), as well as $(thing)Nutrients$() available from a food is available by hovering over it in your inventory. Using $(item)$(k:key.sneak)$() reveals the full tooltip.'),
            text('All foods have a tooltip with this information. The tooltip includes the $(l:mechanics/decay)Decay Date$() of the food, which may be extended with preservation. When viewing this information, it\'s important to realize that not all foods have nutritional value. For example, $(l:mechanics/bread)Dough$() is a food, but it has no Nutrition, Saturation, or Water value. Eating it would not do much good.'),
            text('A food tooltip may look like:$(br2)$(bold)Orange$()$(br)  $(2)$(bold)Expires on: 11:59 July 6, 1004 (in 1 month(s) and 1 day(s))$()$(br)  $(8)Nutrition:$(br)  - Saturation: 2%$(br)  - Water: 10%$(br)$(a)  - Fruit: 0.5'),
            image('tfc:textures/gui/book/gui/nutrition.png', text_contents='The nutrition screen, with bars showing the levels of each nutrient.', border=False),
            text('There are five nutrients, all obtainable from food: $(l)$(a)Fruit$()$(), $(l)$(2)Vegetables$()$(), $(l)$(c)Protein$()$(), $(l)$(6)Grain$()$(), and $(l)$(5)Dairy$()$(). Having a large amounts of all nutrients increases your maximum health, while having a poor nutrition decreases it. Eating food gives you its nutrients. Meals such as $(l:mechanics/pot#soup)Soup$() combine more nutrients into one meal. This is important, because meals you ate a while ago don\'t count towards your nutrition.', title='Nutrients').anchor('nutrients'),
            text('$(l)$(a)Fruit$()$(): Fruit nutrients are mostly found from $(l:the_world/wild_fruits)Fruiting Plants$(), like berry bushes, and fruit trees. A notable exception to this is $(l:mechanics/crops#pumpkin)Pumpkins$() and $(l:mechanics/crops#pumpkin)Melons$(), which nutritionally are fruits.$(br2)$(l)$(2)Vegetables$()$(): Found in nearly every $(l:mechanics/crops)Crop$().$(br2)$(l)$(c)Protein$()$(): Protein can be gotten from the meat of $(l:the_world/wild_animals)Animals$(). It can also be obtained from $(l:mechanics/crops#soybean)Soybeans$(), which have protein and vegetable nutrients.'),
            text('$(l)$(6)Grain$()$(): Grain is found in grain crops, such as $(l:mechanics/crops#barley)Barley$(). The processing of grain is on the $(l:mechanics/bread)Bread$() page. $(thing)Cattail$() and $(thing)Taro$() Roots are also grains.$(br2)$(l)$(5)Dairy$()$(): All dairy comes from $(thing)Milk$(), which comes from $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). Processing and drinking milk is covered on the $(l:mechanics/dairy)Dairy$() page.$(br2)All the food in the world is not useful if it rots. See the $(l:mechanics/decay)Preservation$() page for information on preventing that.'),
            text('Thirst is the level of water in your body. It depletes at a similar rate to hunger. At high temperatures or levels of high hunger usage, your thirst will deplete faster. Luckily, drinking $(thing)Fresh Water$() replenishes thirst. This can be done by clicking $(item)$(k:key.use)$() on a water block. Drinking saltwater causes you to lose thirst, and even has a chance of giving you the $(thing)Thirst$() effect, which will drain you even more.', title='Thirst').anchor('thirst'),
            two_tall_block_spotlight('Water Safety', 'To avoid saltwater, look for rivers, lakes, and freshwater plants like $(thing)Cattails$().', 'tfc:plant/cattail[part=lower,fluid=water]', 'minecraft:air'),
            knapping('tfc:knapping/ceramic/unfired_jug', '$(l:getting_started/pottery)Knapping$() and firing a $(l:getting_started/pottery#jug)jug$() is a way to carry water with you. Fill it like a bucket with $(item)$(k:key.use)$(). Holding $(item)$(k:key.use)$() drinks the water.'),
            text('Depleting food or water completely results in sluggish movement and mining, and begin to take damage. If you die, your nutrition resets.'),
        )),
    ))
    book.category('mechanics', 'Advanced Mechanics', 'Advanced sections of the tech tree, from the first pickaxe, all the way to colored steel.$(br2)$(br)$(bold)Note:$() you can search entries simply by starting to type anywhere!', 'tfc:metal/axe/red_steel', entries=(
        # Possible new entries
        # todo: entity renderers have issues. squids don't work.
        entry('aqueducts', 'Aqueducts', 'tfc:rock/aqueduct/shale', pages=(
            text('$(thing)Aqueducts$() are a tool used for moving $(thing)Water$() around. Unlike $(l:mechanics/wooden_buckets)Wooden Buckets$(), which are not able to transport water sources, $(thing)Aqueducts$() are able to move source blocks horizontally any distance to their destination. They can be used to transport any type of water, including $(thing)Fresh Water$(), $(thing)Salt Water$(), and even $()Hot Spring Water$().').link('#tfc:aqueducts'),
            crafting('tfc:crafting/rock/aqueduct/shale', text_contents='A singe aqueduct block can be crafted with some $(l:mechanics/advanced_building_materials#bricks_and_mortar)Bricks and Mortar$().'),
            text('In order to use an $(thing)Aqueduct$(), you must simply connect them in a horizontal pattern, and place one end adjacent to either a $(thing)Source$() or $(thing)Falling$() water. After waiting a short while, water will begin to flow through the aqueduct network, and out the other end.$(br2)Note if aqueducts are removed, water will cease flowing, and $(thing)Aqueducts$() will not create permanent source blocks - any water will disappear after the aqueducts are removed.'),
            multiblock('An Aqueduct Network', '', False, (
                ('     ', '     ', '  0  ', '     ', '     '),
                ('     ', '     ', '     ', '     ', '     '),
                ('  B  ', '  B  ', 'CADAE', 'B   B', 'B   B'),
            ), {
                'A': 'tfc:rock/aqueduct/marble[south=true,north=true,fluid=water]',
                'B': 'tfc:rock/aqueduct/marble[east=true,west=true,fluid=water]',
                'C': 'tfc:rock/aqueduct/marble[east=true,south=true,fluid=water]',
                'D': 'tfc:rock/aqueduct/marble[west=true,north=true,south=true,fluid=water]',
                'E': 'tfc:rock/aqueduct/marble[east=true,north=true,fluid=water]',
            }),
        )),
        entry('animal_husbandry', 'Animal Husbandry', 'minecraft:egg', pages=(
            text('$(thing)Livestock$() are animals that can be tamed and bred by the player. Livestock can be either $(thing)male$() or $(thing)female$(). For some animals, it is possible to tell their sex visually. For example, male pigs have tusks.'),
            text('Livestock experience $(thing)aging$(). They are born as babies, which are smaller and cannot provide things for the player. After a certain number of days, they grow into $(thing)adult$() animals, which are able to do things like breed or produce milk. After they breed or are used enough times, animals become $(thing)old$(), and are only useful for their meat.'),
            image('tfc:textures/gui/book/tutorial/old_cow.png', text_contents='This bull is old and cannot breed, so it has a faded coat and grey, unseeing eyes.'),
            text('Livestock can be fed to raise $(thing)familiarity$(). Each animal has foods that it prefers to eat. To feed an animal, $(item)$(k:key.sneak)$() and $(item)$(k:key.use)$() with food in your hand.'),
            image('tfc:textures/gui/book/tutorial/unfamiliarized_pig.png', text_contents='Livestock have a $(thing)Familiarity Indicator$() that shows how familiar they are with you. Hold $(item)$(k:key.sneak)$() and look at the animal to reveal it.'),
            image('tfc:textures/gui/book/tutorial/no_familiarity_decay_pig.png', text_contents='Familiarity decays a little each day if not fed. Raising the familiarity enough prevents decay, indicated by the white outlined heart.'),
            image('tfc:textures/gui/book/tutorial/familiarity_limit_pig.png', text_contents='Adult livestock cannot be fully familiarized, indicated by the red outlined heart. Babies can reach 100% familiarity.'),
            text('$(thing)Mammals$() are livestock that experience $(thing)Pregnancy$(). Adult mammals that are above 30% familiarity and have been fed that day will mate, given that they are of opposite genders and near each other. The female animal will become $(thing)pregnant$(), which causes it to have children a set number of days after fertilization.$(br)An example is $(l:mechanics/animal_husbandry#pig)Pigs$().').anchor('mammals'),
            text('$(thing)Wooly Animals$() are $(l:mechanics/animal_husbandry#mammals)Mammals$() that can be $(thing)Sheared$() if they are adults and familiar enough to you. Some examples are $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#alpaca)Alpacas$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$().').anchor('wooly_animals'),
            text('$(thing)Dairy Animals$() are mammals that make $(thing)Milk$(). Female dairy animals can be clicked with a bucket to obtain milk. Some examples are $(l:mechanics/animal_husbandry#goat)Goats$(), $(l:mechanics/animal_husbandry#cow)Cows$(), and $(l:mechanics/animal_husbandry#yak)Yaks$().').anchor('dairy_animals'),
            crafting('tfc:crafting/nest_box', title='Nest Box', text_contents='$(thing)Oviparous Animals$() are not $(l:mechanics/animal_husbandry#mammals)Mammals$(), and instead produce children by laying $(thing)Eggs$(). They need a $(thing)Nest Box$() to lay eggs, which they are capable of locating on their own.$(br)Some examples are $(l:mechanics/animal_husbandry#duck)Ducks$(), $(l:mechanics/animal_husbandry#quail)Quails$(), and $(l:mechanics/animal_husbandry#chicken)Chickens$().').anchor('oviparous_animals'),
            heat_recipe('tfc:heating/food/cooked_egg', '$(thing)Eggs$() can be cooked or boiled for food. Male oviparous animals can fertilize females, which causes the next egg laid in the nest box to be fertilized. Fertilized eggs will have a tooltip with how long until they are ready to hatch.'),
            knapping('tfc:knapping/saddle', '$(thing)Equines$() are $(l:mechanics/animal_husbandry#mammals)Mammals$() that can be ridden when tamed. They become rideable after reaching 15% familiarity.').anchor('horses'),
            text('They need a $(thing)Saddle$() to ride, which can be $(thing)Knapped$(). This includes $(l:mechanics/animal_husbandry#mule)Mules$(), $(l:mechanics/animal_husbandry#donkey)Donkeys$(), and $(l:mechanics/animal_husbandry#horses)Horses$(). Mules and Donkeys can hold any chest or barrel. If holding a barrel, $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$() can be used to remove it. The same keys while holding a bucket can be used to drain fluid from the barrel. $(br2)The next few pages will go over all livestock types.'),
            page_break(),
            text('$(thing)Pigs$() spawn in mild forests with $(l:the_world/climate#temperature)temperature$() between -10 and 35°C, and at least 200mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#mammals)Mammals$() with no special abilities. They will eat any food, even if it is rotten. They have 1-10 children, are pregnant for just 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Pigs').anchor('pig'),
            entity('tfc:pig' + ANIMAL_NBT, 'A pig.', '', scale=0.6),
            text('$(thing)Cows$() spawn in most climates, between $(l:the_world/climate#temperature)temperature$() -10 and 35°C, and at least 250mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They only eat $(thing)grains$(), which may be rotten. They can have 1-2 children, are pregnant for 58 days, and reach adulthood in 192 days. They can have children 13 times, if they are never milked, or be milked 128 times, if they are never bred. They produce milk every day.', title='Cows').anchor('cow'),
            entity('tfc:cow' + ANIMAL_NBT, 'A cow.', '', scale=0.75),
            text('$(thing)Goats$() spawn in moderate climates, with $(l:the_world/climate#temperature)temperature$() between -12 and 25°C, and at least 300mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), and $(thing)vegetables$(), which may be rotten. They can have 1-2 children, are pregnant for 32 days, and reach adulthood in 96 days. They can have children 6 times if they are never milked, or be milked 60 times if they are never bred. They produce milk every 3 days.', title='Goats').anchor('goat'),
            entity('tfc:goat' + ANIMAL_NBT, 'A goat.', '', scale=0.75),
            text('$(thing)Yaks$() spawn in cold climates, with $(l:the_world/climate#temperature)temperature$() of at most -11°C, and at least 100mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They eat only fresh $(thing)grains$(). They always have 1 child, are pregnant for 64 days, and reach adulthood in 180 days. They can have children 23 times, if they are never milked, or be milked 230 times, if they are never bred. They produce milk once a day.', title='Yak').anchor('yak'),
            entity('tfc:yak' + ANIMAL_NBT, 'A yak.', '', scale=0.75),
            text('$(thing)Alpacas$() spawn in moderate climates, with $(l:the_world/climate#temperature)temperature$() between -8 and 20°C, and at least 250mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1-2 children, are pregnant for 36 days, and reach adulthood in 98 days. They can have children 13 times, if they are never sheared, or be sheared 128 times, if they are never bred. They grow wool every 6 days.', title='Alpaca').anchor('alpaca'),
            entity('tfc:alpaca' + ANIMAL_NBT, 'An alpaca.', '', scale=0.75),
            text('$(thing)Sheep$() spawn in drier climates, with $(l:the_world/climate#temperature)temperature$() between 0 and 35°C, and between 70 and 300mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$(). They have 1-2 children, are pregnant for 32 days, and reach adulthood in 56 days. They can have children 6 times, if they are never sheared, or be sheared 60 times, if they are never bred. They grow wool every 9 days.', title='Sheep').anchor('sheep'),
            entity('tfc:sheep' + ANIMAL_NBT, 'A sheep.', '', scale=0.75),
            text('$(thing)Musk Oxen$() spawn in cold climates, with $(l:the_world/climate#temperature)temperature$() between -25 and 0°C, and at least 100mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$(). They always have 1 child, are pregnant for 64 days, and reach adulthood in 168 days. They can have children 16 times, if they are never sheared, or be sheared 160 times if they are never bred. They grow wool every 96 hours.', title='Musk Ox').anchor('musk_ox'),
            entity('tfc:musk_ox' + ANIMAL_NBT, 'A musk ox.', '', scale=0.75),
            text('$(thing)Chickens$() spawn in warm forests, with $(l:the_world/climate#temperature)temperature$() of at least 14°C, and at least 225mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), and $(thing)seeds$(), which can be rotten. Their eggs hatch in 8 days, and become adults in 24 days. They can lay eggs 100 times. They produce eggs every 30 hours.', title='Chickens').anchor('chicken'),
            entity('tfc:chicken' + ANIMAL_NBT, 'A chicken.', '', scale=0.7),
            text('$(thing)Ducks$() spawn in most plains, with $(l:the_world/climate#temperature)temperature$() between -25 and 30°C, and at least 100mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), $(thing)bread$(), and $(thing)seeds$(). Their eggs hatch in 8 days, and become adults in 32 days. They can lay eggs 72 times. They produce eggs every 32 hours.', title='Ducks').anchor('duck'),
            entity('tfc:duck' + ANIMAL_NBT, 'A duck.', '', scale=0.7),
            text('$(thing)Quails$() spawn in colder climates, with $(l:the_world/climate#temperature)temperature$() between -15 and 15°C, and at least 200mm of  $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), and $(thing)seeds$(), which can be rotten. Their eggs hatch in 8 days, and become adults in 22 days. They can lay eggs 48 times. They produce eggs every 28 hours.', title='Quail').anchor('quail'),
            entity('tfc:quail' + ANIMAL_NBT, 'A quail.', '', scale=0.7),
            text('$(thing)Donkeys$() spawn in wetter plains, with $(l:the_world/climate#temperature)temperature$() of at least -15°C, and between 130 and 400mm of $(l:the_world/climate#rainfall)rainfall$(). They are a kind of $(l:mechanics/animal_husbandry#horses)Equine$() that can carry a $(thing)chest$(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1 child, are pregnant for 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Donkeys').anchor('donkey'),
            entity('tfc:donkey' + ANIMAL_NBT, 'A donkey.', '', scale=0.6),
            text('$(thing)Mules$() spawn in plains with $(l:the_world/climate#temperature)temperature$() of at least -15°C, and between 130 and 400mm of $(l:the_world/climate#rainfall)rainfall$(). They are a kind of $(l:mechanics/animal_husbandry#horses)Equine$() that can carry a $(thing)chest$() and are the always-male product of a $(thing)horse$() and a $()donkey$(). They eat $(thing)grains$() and $(thing)fruits$(). They reach adulthood in 80 days.', title='Mules').anchor('mule'),
            entity('tfc:mule' + ANIMAL_NBT, 'A mule.', '', scale=0.6),
            text('$(thing)Horses$() spawn in plains with $(l:the_world/climate#temperature)temperature$() of at least -15°C, and between 130 and 400mm of $(l:the_world/climate#rainfall)rainfall$(). They are a kind of $(l:mechanics/animal_husbandry#horses)Equine$(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1 child, are pregnant for 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Horses').anchor('horse'),
            entity('tfc:horse' + ANIMAL_NBT, 'A horse.', '', scale=0.6),
        )),
        entry('pets', 'Pets', 'minecraft:lead', pages=(
            text('$(thing)Pets$() are animals that function quite similar to $(l:mechanics/animal_husbandry)Livestock$(), but have no useful products. Instead, they can obey commands, follow you around your home, control pests, and help you hunt.'),
            text('Pets recognize a certain player as their $(thing)Owner$(). To become a pet\'s owner, feed it to raise its familiarity above 15%%. Pets will only obey commands that come from their owner.$(br2)Clicking on a pet with $(l:mechanics/dye)dye$() changes its collar color.'),
            entity('tfc:dog' + ANIMAL_NBT, 'A dog.'),
            text('Dogs are a pet that eats grains, vegetables, and meat, rotten or not rotten. They will help you hunt most any land predator or prey.'),
            entity('tfc:cat' + ANIMAL_NBT, 'A cat.'),
            text('Cats are a pet that eats grains, cooked meats, and dairy products. They will help you hunt land prey and small fish. When at home, they will kill pests without being asked to. Cats do not always listen when being told to sit.'),
            text('Responsible pet owners must learn to control their pet. Luckily, this is easy. By pressing $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$() with an empty hand, the $(thing)Pet Screen$() may be opened. This screen contains a number of commands that can be used to tell the pet what to do: $(thing)Relax$(), $(thing)We\'re Home$(), $(thing)Sit$(), $(thing)Follow Me$(), and $(thing)Hunt With Me$().'),
            text('$(thing)Relax$() is the default state of pets, and commanding them returns them to this state. If a pet it at home, it will wander around the property, but not leave, and sleep once a day. If it is too far from home, it will walk aimlessly.'),
            text('$(thing)We\'re Home$() sets the home position of the pet. This tells the animal that it should, when relaxing, stay in that general area. Telling an animal that it is at home does not change the activity it is currently doing.$(br2)$(thing)Sit$() tells the pet to sit where it is. However, animals have minds of their own, and will eventually get bored and stand up!'),
            text('$(thing)Hunt With Me$() tells the pet to follow you, and engage in combat when you attack or are attacked. Pets will only attack animals that they are able to (for example, cats cannot help attack bears!).$(br2)$(thing)Follow Me$() is the same as Hunt With Me, but the pet will not risk itself in combat.'),
        )),
        entry('leather_making', 'Leather Making', 'minecraft:leather', pages=(
            text('$(thing)Leather$() is a sturdy material formed from animal hides. It is required to craft $(l:mechanics/armor)Armor$(), $(thing)Saddles$(), or a $(l:mechanics/bellows)Bellows$(). $()Raw Hides$() must be treated through several processes: $(l:mechanics/leather_making#soaking)soaking$(), $(l:mechanics/leather_making#scraping)scraping$(), $(l:mechanics/leather_making#preparing)preparing$(), and $(l:mechanics/leather_making#tanning)tanning$() to turn them into $(thing)Leather$().'),
            item_spotlight(('tfc:small_raw_hide', 'tfc:medium_raw_hide', 'tfc:large_raw_hide'), title='Raw Hide', text_contents='To get started you will first need to obtain $(thing)Raw Hide$(), which is dropped when slaughtering many different $(l:the_world/wild_animals)Animals$(). Different animals will drop different sizes of $(thing)Raw Hide$().', link_recipe=True).link('tfc:small_raw_hide', 'tfc:medium_raw_hide', 'tfc:large_raw_hide'),
            text('Leather making will require several materials and tools. You will need:$(br)$(li)$(l:mechanics/barrels#limewater)Limewater$() - a solution of $(l:mechanics/flux)Flux$() in $(thing)Water$()$(li)Water$(li)$(l:mechanics/barrels#tannin)Tannin$() - an acidic solution made from the bark of some trees.$(li)$(thing)Raw Hides$()$(br2)With all that on hand, you are ready to begin the leather working process.'),
            sealed_barrel_recipe('tfc:barrel/medium_soaked_hide', 'First, raw hides must be $(thing)soaked$() to clean them and loosen unwanted material before processing. This can be done by sealing some $(thing)Raw Hide$() in a $(l:mechanics/barrels#limewater)Barrel of Limewater$() for at least eight hours.').anchor('soaking').link('tfc:small_soaked_hide', 'tfc:medium_soaked_hide', 'tfc:large_soaked_hide'),
            item_spotlight(('tfc:small_scraped_hide', 'tfc:medium_scraped_hide', 'tfc:large_scraped_hide'), link_recipe=True, title='Scraping', text_contents='After soaking, $(thing)Soaked Hides$() must be scraped which removes any excess material. In order to scrape hides, place the hide on the side of a $(thing)Log$(). With a $(thing)Knife$(), $(item)$(k:key.use)$() on each part of the hide and it will start to change texture.').anchor('scraping').link('tfc:small_scraped_hide', 'tfc:medium_scraped_hide', 'tfc:large_scraped_hide'),
            image(*['tfc:textures/gui/book/tutorial/soaked_hide_%d.png' % i for i in range(1, 1 + 4)], text_contents='Once the hide is fully scraped, it can be broken to pick up a $(thing)Scraped Hide$().'),
            sealed_barrel_recipe('tfc:barrel/medium_prepared_hide', 'After scraping, $(thing)Scraped Hides$() must be sealed in a $(l:mechanics/barrels)Barrel$() of $(thing)Water$() for at least eight hours, for one final cleaning before tanning.').anchor('preparing').link('tfc:small_prepared_hide', 'tfc:medium_prepared_hide', 'tfc:large_prepared_hide'),
            sealed_barrel_recipe('tfc:barrel/medium_leather', 'Finally, $(thing)Prepared Hides$() must be sealed in a $(l:mechanics/barrels#tannin)Barrel of Tannin$(), which is an acidic chemical compound that helps to convert the hide to $(thing)Leather$(). After another eight hours, you can remove the $(thing)Leather$() from the barrel.').anchor('tanning').link('minecraft:leather'),
        )),
        entry('weaving', 'Weaving', 'tfc:spindle', pages=(
            text('$(thing)Weaving$() is the process of combining different kinds of string into $(thing)Cloth$(). While the last step of weaving is done in a $(thing)Loom$(), some cloths such as $(thing)Wool$(), obtained from $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(), requires a $(thing)Spindle$() to obtain $(thing)Wool Yarn$() in order to be woven.'),
            knapping('tfc:knapping/unfired_spindle_head', 'The $(thing)Unfired Spindle Head$() is knapped from clay. It can then be $(l:mechanics/heating)fired$() to make a $(thing)Spindle Head$(). To complete the spindle, craft it with a $(thing)Stick$().').link('tfc:spindle'),
            crafting('tfc:crafting/wool_yarn', text_contents='Crafting $(thing)Wool$() with a Spindle yields $(thing)Wool Yarn$().').link('tfc:wool'),
            crafting('tfc:crafting/wood/loom/acacia', text_contents='The loom is crafted from just $(thing)Lumber$() and a $(thing)Stick$().').link('#tfc:looms'),
            loom_recipe('tfc:loom/wool_cloth', 'The recipe for $(thing)Wool Cloth$() takes 16 $(thing)Wool Yarn$(). Adding to the loom is done with $(item)$(k:key.use)$(). Then, hold down $(item)$(k:key.use)$() to begin working the loom. When it is done, press $(item)$(k:key.use)$() to retrieve the item.').link('tfc:wool_cloth').anchor('wool_cloth'),
            image(*['tfc:textures/gui/book/tutorial/loom_%s.png' % stage for stage in ('empty', 'full', 'working', 'done')], text_contents='The stages of the loom working.'),
            loom_recipe('tfc:loom/white_wool', '$(thing)Wool Cloth$() can be re-woven into $(thing)Wool Blocks$(). Wool blocks can be $(l:mechanics/dye)dyed$().').link('minecraft:white_wool'),
            loom_recipe('tfc:loom/silk_cloth', '$(thing)Silk Cloth$() can be made in the loom out of $(thing)String$(). It can be used as a wool cloth substitute in some cases.').link('tfc:silk_cloth'),
            loom_recipe('tfc:loom/burlap_cloth', '$(thing)Burlap Cloth$() does not have a use, but it can be made from $(l:mechanics/crops#jute)Jute Fiber$().').link('tfc:burlap_cloth').anchor('burlap_cloth'),
            crafting('minecraft:light_blue_bed', 'minecraft:painting'),
        )),
        entry('papermaking', 'Papermaking', 'tfc:unrefined_paper', pages=(
            text('$(thing)Paper$() is either made from the processed stalk of the $(l:mechanics/crops#papyrus)Papyrus$() crop, or from $(l:mechanics/papermaking#parchment)Animal Hides$(). Paper is useful for written materials like $(thing)Books$() and $(thing)Maps$().'),
            crafting('tfc:crafting/papyrus_strip', text_contents='Papyrus must first be cut into strips with a $(thing)Knife$()'),
            sealed_barrel_recipe('tfc:barrel/soaked_papyrus_strip', 'Then, papyrus strips are soaked in a $(l:mechanics/barrels)Barrel$() of $(thing)Water$().'),
            loom_recipe('tfc:loom/unrefined_paper', 'Then, soaked papyrus strips are woven together in a loom to make $(thing)Unrefined Paper$(). Finally, it must be placed on a log and $(l:mechanics/leather_making#scraping)Scraped$() to make $(thing)Paper$().'),
            crafting('minecraft:map', 'tfc:crafting/name_tag'),
            crafting('minecraft:writable_book', 'minecraft:book'),
            crafting('tfc:crafting/wood/lectern/kapok', 'tfc:crafting/wood/bookshelf/kapok'),
            text('Flip to the next page for information on papermaking via the parchment process.'),
            page_break(),
            text('$(thing)Parchment Paper$() starts with a scraped hide. Review the $(l:mechanics/leather_making)leather making chapter$() to learn how to make it. Parchment requires treatment with a few different items. First, $(thing)Pumice$() is needed. Pumice is found on the ground near $(l:the_world/geology)Volcanoes$(), or from $(l:mechanics/sluices)Sluicing$() or $(l:mechanics/panning)Panning$() ore deposits with Andesite, Rhyolite, or Dacite in them.', title='Parchment').anchor('parchment'),
            block_spotlight('Pumice', 'A Pumice rock placed on the ground.', 'tfc:groundcover/pumice'),
            crafting('tfc:crafting/treated_hide_from_large', text_contents='Crafting pumice, a hammer, and scraped hide gives sections of $(thing)Treated Hide$().'),
            crafting('tfc:crafting/paper', text_contents='Treated hide, $(l:mechanics/glassworking#lime)lime powder$(), flour, and a fresh egg will complete the treatment process and yield usable paper.'),
        )),
        entry('glassworking', 'Glassworking', 'tfc:silica_glass_bottle', pages=(
            text('Glassworking is the process of turning sand into glass. To start, you must create a $(thing)Glass Batch$(), of which there are four types:$(br)$(br) 1. $(thing)Silica$(), from white sand.$(br) 2. $(thing)Hematitic$(), from yellow, red, or pink sand.$(br) 3. $(thing)Olivine$(), from green or brown sand.$(br) 4. $(thing)Volcanic$(), from black sand.'),
            crafting('tfc:crafting/silica_glass_batch', text_contents='Glass batches can then be crafted using one of the aforementioned colors of sand, plus $(l:mechanics/glassworking#lime)Lime$() and a type of $(l:mechanics/glassworking#potash)Potash$().'),
            heat_recipe('tfc:heating/powder/lime', '$(thing)Lime$() is one of the ingredients required to make glass batches. It is a powder obtained by $(l:mechanics/heating)heating$() $(l:mechanics/flux)Flux$().').anchor('lime').link('tfc:powder/lime'),
            heat_recipe('tfc:heating/powder/soda_ash_from_seaweed', 'A type of $(thing)Potash$() or equivalent is also required for glass batches. $(thing)Soda Ash$() can be used, which is a powder made from heating $(thing)Dried Seaweed$() or $(thing)Kelp$(). $(l:the_world/ores_and_minerals#saltpeter)Saltpeter$() can be used as well.').anchor('potash').link('tfc:powder/soda_ash'),
            page_break(),
            text('Glassworking is done by starting with a glass batch, and then completing a series of steps. These steps may require specific tools:$(br)$(li)A $(l:mechanics/glassworking#blowpipe)Blowpipe$(), to $(thing)Blow$() and $(thing)Stretch$()$(li)A $(l:mechanics/glassworking#paddle)Paddle$(), to $(thing)Flatten$()$(li)$(l:mechanics/glassworking#jacks)Jacks$(), to $(thing)Pinch$()$(li)A $(l:mechanics/glassworking#saw)Gem Saw$(), to $(thing)Saw$()', title='Tools of the Trade'),
            knapping('tfc:knapping/ceramic/unfired_blowpipe', 'The most important tool is the $(thing)Blowpipe$(). It can be $(thing)knapped$() from clay, and then fired into a $(thing)Ceramic Blowpipe$().').anchor('blowpipe'),
            anvil_recipe('tfc:anvil/blowpipe', 'Ceramic blowpipes are brittle, and have a chance to to break when used. A more sturdy blowpipe can be $(l:mechanics/anvils#working)worked$() from a $(thing)Brass Rod$() on an anvil.'),
            crafting('tfc:crafting/paddle', text_contents='The $(thing)Flatten$() operation can be done with a $(thing)Paddle$(), which is crafted from wood.').anchor('paddle').link('tfc:paddle'),
            welding_recipe('tfc:welding/jacks', 'The $(thing)Pinch$() operation can be done with $(thing)Jacks$(), made from welding two brass rods together.').anchor('jacks').link('tfc:jacks'),
            crafting('tfc:crafting/gem_saw', text_contents='The $(thing)Saw$() operation can be done with a $(thing)Gem Saw$(). The gem saw is also used to break both $(thing)Glass Blocks$() and $(thing)Glass Panes$() and obtain them.').anchor('saw').link('tfc:gem_saw'),
            page_break(),
            text('First, glass on the blowpipe must be heated to $(4)$(bold)Faint Red$().Then, hold the blowpipe and hold $(item)$(k:key.use)$() to perform each step$().$(br2)$(bold)Blow$()$(br)Use the $(thing)Blowpipe$() while facing straight ahead.$(br2)$(bold)Stretch$()$(br)Use the $(thing)Blowpipe$() while facing straight down.', title='How to Glass'),
            text('$(bold)Flatten$()$(br)Use the $(thing)Blowpipe$() while holding a $(l:mechanics/glassworking#paddle)Paddle$() in your offhand.$(br2)$(bold)Pinch$()$(br)Use the $(thing)Blowpipe$() while holding $(l:mechanics/glassworking#jacks)Jacks$() in your offhand.$(br2)$(bold)Saw$()$(br)Use the $(thing)Blowpipe$() while holding a $(l:mechanics/glassworking#saw)Gem Saw$() in your offhand.$(br2)$(bold)Roll$()$(br)Use the $(thing)Blowpipe$() with a $(l:mechanics/weaving#wool_cloth)Wool Cloth$() in your offhand.'),
        )),
        entry('glassworking_applications', 'Glass Products', 'minecraft:glass', pages=(
            text('The most simple glass products are $(thing)Glass Panes$() and $(thing)Glass Blocks$(). In order to craft them, you must start with a $(l:mechanics/glassworking#blowpipe)Blowpipe$() with a $(thing)Glass Batch$(), and then perform a $(thing)Pour$().$(br)$(li)$(bold)Table Pours$() are used to create $(thing)Glass Panes$()$(li)$(bold)Basin Pours$() are used to create $(thing)Glass Blocks$()'),
            text('Glass can also be $(thing)dyed$() before it is poured to create colored glass. The color is dependent on the type of glass batch, and any powders that have been added.$(br2)Each type of $(thing)Glass Batch$() has a different natural color of glass that they will create. $(thing)Silica$() glass batches can be made into many colors, $(thing)Olivine$(), and $(thing)Volcanic$() glass can be made into relatively few colors.').link('#tfc:glass_batches'),
            page_break(),
            text('$(thing)Glass Panes$() are made with a $(thing)Table Pour$(). A pouring table is made by placing up to sixteen $(thing)Brass Plated Blocks$() in a continuous area.$(br2) 1. Add a $(l:mechanics/glassworking)Glass Batch$() to a $(thing)Blowpipe$().$(br) 2. Heat the blowpipe to $(4)$(bold)Faint Red$().$(br) 3. $()$(item)$(k:key.use)$() the $(thing)Blowpipe$() on the top of the table.$(br) 4. Finally $(item)$(k:key.use)$() with a $(l:mechanics/glassworking#paddle)Paddle$() to flatten the glass.', title='Table Pour').link('#c:glass_panes'),
            image(
                'tfc:textures/gui/book/tutorial/glass_panes_1.png',
                'tfc:textures/gui/book/tutorial/glass_panes_2.png',
                'tfc:textures/gui/book/tutorial/glass_panes_3.png',
                text_contents='Once the glass is cooled, it can be broken with a $(l:mechanics/glassworking#saw)Gem Saw$() to obtain.'
            ),
            text('$(thing)Glass Blocks$() are made with a $(thing)Basin Pour$(). A basin is made by surrounding all sides of an air block except the top with $(thing)Brass Plated Blocks$().$(br2) 1. Add a $(l:mechanics/glassworking)Glass Batch$() to a $(thing)Blowpipe$().$(br) 2. Heat the blowpipe to $(4)$(bold)Faint Red$().$(br) 3. $(item)$(k:key.use)$() the $(thing)Blowpipe$() on the top of the table.', title='Basin Pour').link('#c:glass_blocks'),
            image(
                'tfc:textures/gui/book/tutorial/glass_block_1.png',
                'tfc:textures/gui/book/tutorial/glass_block_2.png',
                'tfc:textures/gui/book/tutorial/glass_block_3.png',
                text_contents='Once the glass is cooled, it can be broken with a $(l:mechanics/glassworking#saw)Gem Saw$() to obtain.'
            ),
            page_break(),
            text('Glass has a natural color based on the type of $(l:mechanics/glassworking)Glass Batch$() that was used. Other colors can be made using a $(l:mechanics/bowls)Bowl$().$(br2)To use, place the $(l:mechanics/bowls)Bowl$() on the ground, then $(item)$(k:key.use)$() the required $(thing)Powder$(). Before $(thing)Pouring$(), use the $(thing)Blowpipe$() on the bowl to add the powder to the batch.', title='Coloring Glass').anchor('coloring'),
            text('$(br2)The next pages show the different combinations of glass types and powder materials to create each color.'),
            text('$(li)$(bold)$(7)White$(): Silica or Hematitic Glass + $(thing)Soda Ash$()$(li)$(bold)$(0)Black$(): Any Glass + $(thing)Graphite$()$(li)$(bold)$(8)Gray$(): Any + $(thing)Graphite$() + $(thing)Soda Ash$()$(li)$(bold)$(7)Light Gray$(): Any + $(thing)Graphite$() + 2x $(thing)Soda Ash$()$(li)$(bold)$(5)Purple$(): Any + $(thing)Iron$() + $(thing)Copper$()$(li)$(bold)$(#964b00)Brown$(): Any + $(thing)Nickel$()$(li)$(bold)$(3)Cyan$(): Non-Volcanic Glass + $(thing)Copper$() + $(thing)Sapphire$()$(li)$(bold)$(2)Green$(): Silica or Hematitic Glass + $(thing)Iron$()', title='Dye Colors'),
            text('$(li)$(bold)$(a)Lime$(): Silica or Hematitic Glass + $(thing)Iron$() + $(thing)Soda Ash$()$(li)$(bold)$(b)Light Blue$(): Silica Glass + $(thing)Lapis Lazuli$()$(li)$(bold)$(1)Blue$(): Silica Glass + $(thing)Copper$()$(li)$(bold)$(4)Red$(): Silica or Hematitic Glass + $(thing)Tin$()$(li)$(bold)$(6)Yellow$(): Silica or Hematitic Glass + $(thing)Silver$()$(li)$(bold)$(#ef8e38)Orange$(): Silica Glass + $(thing)Pyrite$()$(li)$(bold)$(5)Magenta$(): Silica or Hematitic Glass + $(thing)Ruby$()$(li)$(bold)$(d)Pink$(): Silica Glass + $(thing)Gold$()$(li)$(bold)$(0)Tinted$(): Non-Silica Glass + $(thing)Amethyst$()'),
            table([
                '', 'C',  'T', {'fill': '0xff42f2'}, {'fill': '0x8af3ff'}, {'fill': '0x526cff'}, {'fill': '0xe3e3e3'}, {'fill': '0xe69407'}, {'fill': '0xc738c9'}, {'fill': '0xffe81c'}, {'fill': '0x48ff1f'}, {'fill': '0xe01414'}, {'fill': '0x0c9400'}, {'fill': '0x188a9e'}, {'fill': '0x7d4f00'}, {'fill': '0x6e059c'}, {'fill': '0x7d7d7d'}, {'fill': '0xbdbdbd'}, {'fill': '0x000000'},
                'Silica',    'B', 'R', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G',
                'Hematitic', 'R', 'G', 'R', 'R', 'R', 'G', 'B', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G',
                'Olivine',   'R', 'G', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'B', 'G', 'G', 'G', 'G', 'G', 'G',
                'Volcanic',  'R', 'G', 'R', 'R', 'B', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'G', 'G', 'G', 'G', 'G',
            ],
                'The availability of glass colors. \'C\' is clear, \'T\' is tinted. Colors can only be crafted with certain glass batches.',
                'Glass Color Availability',
                {'R': {'fill': '0xb33e3e'}, 'G': {'fill': '0x3eb340'}, 'B': {'fill': '0x3d42a8'}},
                [{'text': 'Can be Crafted', 'color': '0x3eb340'}, {'text': 'Cannot be Crafted', 'color': '0xb33e3e'}, {'text': 'Default Color', 'color': '0x3d42a8'}],
                18, 35, 10, 10, 5, 15,
                small=True
            ),
            glassworking_recipe('tfc:glassworking/lamp_glass', '$(thing)Lamp Glass$() is a necessary component to craft $(l:mechanics/lamps)Lamps$().').anchor('lamp_glass'),
            glassworking_recipe('tfc:glassworking/empty_jar', '$(l:mechanics/jarring)Jars$() are also made from blown glass, but only silica or hematitic glass.').anchor('jar'),
            glassworking_recipe('tfc:glassworking/silica_glass_bottle', '$(thing)Glass Bottles$() can also be made. The quality of the glass bottle depends on the type of glass used to make it.').anchor('glass_bottle'),
            glassworking_recipe('tfc:glassworking/lens', 'The $(thing)Lens$() is used for crafting the spyglass, compasses, and daylight sensors.'),
        )),
        entry('bowls', 'Bowls', 'tfc:ceramic/bowl', pages=(
            text('$(thing)Bowls$() are a versatile tool which can be used to make $(l:mechanics/salad)Salads$(), to make $(l:mechanics/pot)Soups$(), to $(l:mechanics/decay#salting)Salt$() meat, or to apply $(thing)Powder$() to $(l:mechanics/glassworking_applications#coloring)Glass$() in order to change the resulting glass\'s color.$(br2)Bowls can be made both from $(thing)Ceramic$(), by knapping a bowl out of clay, and then firing them.'),
            knapping('tfc:knapping/ceramic/unfired_bowl_2', 'Multiple unfired bowls can be made from one knapping.').link('tfc:ceramic/unfired_bowl', 'tfc:ceramic/bowl'),
            crafting('minecraft:bowl', text_contents='Bowls can also be made out of wood via crafting them together with $(thing)Glue$()$(br2)$(thing)Glue$() can be made by soaking $(thing)Bone Meal$() in a barrel of $(l:mechanics/barrels#limewater)Limewater$().', title='Wooden Bowls').link('minecraft:bowl'),
            sealed_barrel_recipe('tfc:barrel/glue', 'Bowls can also be placed on the ground like blocks. When placed, they can be filled with any type of $(thing)Powder$() - the results of grinding ores, minerals, or gems in the $(l:mechanics/quern)Quern$().'),
            item_spotlight('#tfc:powders', 'Powders', text_contents='The Powder Bowl can hold up to 16 of a given powder. To insert items, $(item)$(k:key.use)$() while holding the powder. To extract items, $(item)$(k:key.use)$() with an empty hand.$(br2)$(item)$(k:key.sneak)$() allows extracting the entire contents of the bowl.'),
            item_spotlight('tfc:powder/salt', text_contents='If there is salt in the bowl, clicking with unsalted raw meat will salt the meat. This is the same as crafting the meat with the salt in your inventory.'),
        )),
        entry('jarring', 'Jarring', 'tfc:jar/plum', pages=(
            text('$(thing)Jars$() are used to preserve fruit for longer periods of time. Jars start as $(thing)Empty Jars$(), obtained through $(l:mechanics/glassworking_applications#jar)Glassworking$(). Then, a $(thing)Jar Lid$() must be smithed from $(thing)Tin$(). Crafting these together creates an $(thing)Empty Jar With Lid$().'),
            text('In a pot, boil $(thing)Sugar$() with 2-4 pieces of $(thing)Fruit$(). When the recipe is done, $(item)$(k:key.use)$() with the empty jar with lid to create a $(thing)Sealed Jar of Jam$(). The sealed jar will last quite a long time on its own.'),
            crafting('tfc:crafting/jar/plum_unsealed', text_contents='Unsealing a jar is done by crafting. The lid is not able to be retrieved. An unsealed jar only lasts for a few days!'),
            crafting('tfc:crafting/food/barley_bread_jam_sandwich_1', text_contents='Jam is used for making sandwiches. Jam sandwiches can contain Dairy, Cooked Meats, and Jam. An $(thing)Empty Jar$() is left over.'),
            text('Jars can be placed on solid surfaces with $(item)$(k:key.use)$(). A block can contain four jars of any kind.'),
            crafting('tfc:crafting/wood/shelf/oak', text_contents='$(thing)Shelves$() can be attached to solid walls. Items, including jars, can be placed on top of them by using the item - including jars - on the shelf, or underneath.')
        )),
        entry('bread', 'Bread', 'tfc:food/barley_bread', pages=(
            text('Bread is the processed form of the various grain crops, such as $(l:mechanics/crops#barley)Barley$(). Breaking a grain crop drops a raw, unprocessed grain item, which is not useful on its own. It must be processed into $(thing)Bread$(), which can then be eaten or used in $(l:mechanics/sandwiches)Sandwiches$().').link('#c:foods/bread', '#c:foods/dough', '#c:foods/grain', '#c:foods/flour'),
            crafting('tfc:crafting/food/barley_grain', text_contents='First, cut the straw off of the food with a $(thing)Knife$().'),
            item_spotlight('tfc:food/rye_grain', text_contents='Grains are the longest-lasting stage of the process, decaying much slower than most foods. On its own, a fresh piece of grain lasts 10 months and 7 days. In a small vessel, it lasts 1 year, 9 months, and 7 days.'),
            quern_recipe('tfc:quern/food/oat_flour', 'Grain must then be ground in a $(l:mechanics/quern)Quern$() to make flour.'),
            crafting('tfc:crafting/food/barley_dough_1', text_contents='Dough is crafted by adding a bucket of $(thing)Fresh Water$() to flour.'),
            heat_recipe('tfc:heating/food/barley_bread', 'Dough is then able to be $(l:mechanics/heating)heated$() to make bread. At this point it can also be used in $(l:mechanics/sandwiches)Sandwiches$().'),
        )),
        entry('sandwiches', 'Sandwiches', 'tfc:food/barley_bread_sandwich', pages=(
            text('$(thing)Sandwiches$() are a meal allowing the combination of two $(l:mechanics/bread)Bread$() items and three sandwich foods, which can be any combination of $(l:mechanics/bread)Vegetables$(), $(thing)Cooked Meats$(), and $(l:mechanics/dairy)Cheeses$().').link(*['tfc:food/%s_bread_sandwich' % g for g in GRAINS]),
            crafting('tfc:crafting/food/wheat_bread_sandwich', text_contents='The sandwich recipe, which is executed in a $(thing)Workbench$().'),
            text('The nutrients, water, and saturation of the food items are all combined into the sandwich\'s nutritional content. The breads are weighted at 50% of their values, whereas the ingredient foods are weighted at 80%. Sandwich ingredients may not be rotten, but once the sandwich is created, it is considered fresh, and decays like it is new.'),
            empty_last_page()
        )),
        entry('dairy', 'Dairy Products', 'tfc:food/cheese', pages=(
            text('$(thing)Dairy$() is a $(l:getting_started/food_and_water#nutrients)Nutrient$() obtained from the milk produced by $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). It can be drank, or processed into $(thing)Cheese$(). Drinking can be done out of a jug, and always restores $(l:getting_started/food_and_water#thirst)Thirst$(). However, it only adds to nutrition when drank after eating a food. Practically, this means that drinking milk twice in a row is ineffectual. A meal must precede it.', title='Dairy').link('minecraft:milk_bucket'),
            text('To start the $(thing)Cheesemaking$() process, add $(thing)Milk$() and $(thing)Vinegar$() in a $(l:mechanics/barrels)Barrel$() at a ratio of 9:1. This is easiest done by filling 9 buckets of milk in a barrel, and adding a single bucket of vinegar. This produces $(thing)Milk Vinegar$().').link('tfc:food/cheese'),
            sealed_barrel_recipe('tfc:barrel/curdled_milk', 'Once milk and vinegar are mixed, it will curdle if it is sealed in a barrel for eight hours. This requires no extra ingredients, except time.'),
            sealed_barrel_recipe('tfc:barrel/food/cheese', 'Cheese is then made by once again sealing the curdled milk in a barrel for eight hours. Cheese is a long-lasting dairy product, and can be used in some meals to add dairy to them, such as $(l:mechanics/sandwiches)Sandwiches$().')
        )),
        entry('scribing_table', 'Scribing Table', 'minecraft:black_dye', pages=(
            text('The $(thing)Scribing Table$() is used to rename items. It requires $(l:mechanics/dye)Black Dye$() to rename items, which can be supplied as the traditional dye item or as a bucket of dye fluid.'),
            block_spotlight('', 'The scribing table.', 'tfc:wood/scribing_table/kapok'),
            crafting('tfc:crafting/wood/scribing_table/birch', text_contents='The scribing table crafting recipe.').link('#tfc:scribing_tables'),
            image('tfc:textures/gui/book/gui/scribing.png', text_contents='The scribing screen takes text entry at the top, an input on the left, a dye in the center. The output is taken from the right slot.', border=False),
        )),
        entry('sewing_table', 'Sewing Table', 'tfc:wood/sewing_table/oak', pages=(
            text('The $(thing)Sewing Table$() is used to produce banner patterns and smithing templates. It requires $(l:mechanics/weaving#wool_cloth)Wool Cloth$(), $(l:mechanics/weaving#burlap_cloth)Burlap Cloth$(), String or Yarn, as well as a Needle.'),
            crafting('tfc:crafting/bone_needle', text_contents='A needle for sewing can be made from a bone.'),
            crafting('tfc:crafting/wood/sewing_table/oak', text_contents='The sewing table can be made from wood, leather, and a pair of shears.'),
            text('The sewing table\'s inventory is similar to that of a crafting table. If you ever make a mistake, you can simply exit the inventory, and your items will be returned to you. There is a recipe book available in the GUI. Selecting the book opens a menu where you can select a recipe. This places small indicators of what steps to perform on the working surface.'),
            page_break(),
            image('tfc:textures/gui/book/gui/sewing_table.png', text_contents='The sewing screen.'),
            text('The slots on the left are for the string and the needle. The two bottom slots can hold any combination of burlap and wool cloth. The slot on the right will show the result when it is ready to be taken. Taking the result from the result slot completes the craft, and uses the items.'),
            text('Each cloth item allows placing 8 squares of that material on the sewing surface. Similarly, each yarn item allows placing 16 stitches. When you have selected a recipe, just use the buttons on the top right of the screen to place squares and stitch them together as shown by the guide. Want to remove stitches? Use the red \'X\' button to get rid of squares.'),
            crafting('minecraft:loom', text_contents='The banner loom, for making things with banner patterns, can be crafted to do work with banner patterns.'),
            text('For information on the use of smithing templates, see the $(l:mechanics/armor_trims)Armor Trims$() chapter.'),
            empty_last_page()
        )),
        entry('armor_trims', 'Armor Trims', 'minecraft:rib_armor_trim_smithing_template', pages=(
            text('Armor trims are produced with a $(thing)Trim Material$() and a $(l:mechanics/sewing_table)Smithing Template$(). In TFC, all gems, along with Sterling Silver, Gold, Rose Gold, and Bismuth, can be used as trim materials. Armor trims are purely cosmetic.'),
            crafting('minecraft:smithing_table', text_contents='The smithing table can be used to apply armor trims to TFC armor.'),
        )),
        entry('advanced_building_materials', 'Advanced Materials', 'tfc:brick/rhyolite', pages=(
            text('In addition to the primitive $(l:getting_started/building_materials)building materials$() of the stone age, the acquisition of metal tools allows the construction of more advanced building materials'),
            text('$(br) 1. $(l:mechanics/advanced_building_materials#alabaster)Alabaster$()$(br) 2. $(l:mechanics/advanced_building_materials#bricks_and_mortar)Bricks and Mortar$()$(br)', title='Contents'),
            page_break(),
            # Alabaster
            text('Alabaster is a building block made from $(l:the_world/ores_and_minerals#gypsum)Gypsum$(). Alabaster can be made by directly crafting with $(l:the_world/ores_and_minerals#gypsum)Gypsum$(), however it can be made more efficiently by sealing some $(l:the_world/ores_and_minerals#gypsum)Gypsum$() with 100 mB of $(thing)Limewater$() in a barrel.', title='Alabaster').anchor('alabaster').link('tfc:alabaster/raw', 'tfc:alabaster/polished'),
            crafting('tfc:crafting/alabaster_brick', 'tfc:crafting/alabaster/bricks', title='Alabaster Bricks'),
            text('Alabaster can be $(l:mechanics/dye)dyed$() in a $(l:mechanics/barrels)Barrel$() of dye into any color. Raw Alabaster blocks can also be $(l:mechanics/chisel)chiseled$() into $(thing)Polished Alabaster$() using the $(thing)Smooth$() chisel mode, or crafted into stairs, slabs, or walls.', title='Alabaster Decorations'),
            crafting('tfc:crafting/alabaster/bricks/magenta_stairs', 'tfc:crafting/alabaster/polished/magenta_slab', title='Stairs and Slabs'),
            page_break(),
            text('$(thing)Stone Bricks$() can be made using a $(l:mechanics/chisel)Chisel$() with some individual loose rocks. It then requires $(thing)Mortar$() in order to form a strong building block.$(br2)Mortar can be made by adding $(thing)Sand$() to a barrel of $(l:mechanics/barrels#limewater)Limewater$().', title='Bricks and Mortar').anchor('bricks_and_mortar'),
            crafting('tfc:crafting/brick/gneiss', 'tfc:crafting/rock/bricks/gneiss'),
            text('Other decorative stone blocks can also be made, such as both $(thing)Cracked Bricks$(), and $(thing)Chiseled Stone$(). $(thing)Mossy$() stone blocks can also be created by placing cobblestone or bricks underwater, near existing mossy bricks or cobblestone. The moss will start to spread to these nearby blocks.'),
            crafting('tfc:crafting/rock/chiseled/gneiss', 'tfc:crafting/rock/cracked_bricks/gneiss'),
        )),
        entry('salad', 'Salads', 'tfc:food/protein_salad', pages=(
            text('$(thing)Salads$() are a meal prepared in a $(thing)Bowl$() from up to five $(thing)Fruits$(), $(thing)Vegetables$(), or $(thing)Cooked Meats$().$(br2)Salads are one of the simplest meals to make, and just require a $(l:mechanics/bowls)Bowl$(). First, hold the $(thing)Bowl$() and press $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$(). This will open the salad screen.').link(*['tfc:food/%s_salad' % g for g in ('fruit', 'dairy', 'vegetables', 'protein', 'grain')]),
            item_spotlight('#tfc:salads', title='', text_contents='Up to five ingredients can be added in the top slots. $(thing)Bowls$() can be added in the left bottom slot. When you are done, the salad can be taken out from the right bottom slot.'),
        )),
        entry('wooden_buckets', 'Wooden Buckets', 'tfc:wooden_bucket', pages=(
            text('$(thing)Wooden Buckets$() are an early game fluid container. They can contain 1000 mB of fluid. They can pick up any kind of fluid that is used for recipes, such as those in a $(l:mechanics/pot)Pot$() or $(l:mechanics/barrels)Barrel$(). However, wooden buckets cannot place source blocks. Dumping its fluid on the ground results in a small amount of fluid that quickly disappears.'),
            crafting('tfc:crafting/wooden_bucket', text_contents='The wooden bucket is made of $(thing)Lumber$().'),
        )),
        entry('damage_types', 'Damage Types', 'tfc:metal/sword/red_steel', pages=(
            text('$(thing)Physical Damage Types$() describe the nature of the damage that can be dealt by players and mobs. There are three types: $(thing)Piercing$(), $(thing)Slashing$(), and $(thing)Crushing$(). Some entities are able to resist certain damage types, making it difficult or impossible to kill them with certain methods.'),
            text('$(thing)Piercing$() damage is dealt by pointy weapons such as $(thing)Knives$(), projectiles like $(thing)Arrows$() and $(thing)Javelins$(), fanged monsters like $(thing)Spiders$(), and $(l:the_world/flora#cacti)Cacti$(). $(thing)Skeletons$() are invulnerable to piercing damage, and $(thing)Zombies$() are slightly vulnerable to it.', title='Piercing'),
            text('$(thing)Slashing$() damage is dealt by weapons with long sharp edges like $(thing)Axes$() and $(thing)Swords$(), as well as large $(l:the_world/wild_animals#predators)Predators$() like bears and cougars. $(thing)Creepers$() are vulnerable to slashing damage.', title='Slashing'),
            text('$(thing)Crushing$() damage is dealt by blunt weapons like $(thing)Hammers$() and $(thing)Maces$(). $(thing)Zombies$() deal crushing damage, and are somewhat resistant to it. $(thing)Creepers$() also resist some crushing damage$(). $(thing)Skeletons$() are vulnerable to it.', title='Crushing'),
            text('$(l:mechanics/armor)Armor$() has its own set of damage resistances. These resistances scale with the quality of armor, but may differ among armors of the same tier. Among bronzes, $(thing)Bismuth Bronze$() armor is best at resisting $(thing)Crushing$(), $(thing)Black Bronze$() is best at resisting $(thing)Piercing$() and regular $(thing)Bronze$() does moderately well with any damage.').anchor('armor'),
            text('For $(thing)Colored Steel$(), $(thing)Blue Steel$() is better at resisting $(thing)Crushing$() damage, whereas $(thing)Red Steel$() is better at resisting $(thing)Piercing$() damage.'),
        )),
        entry('armor', 'Armor', 'tfc:metal/chestplate/copper', pages=(
            text('$(thing)Armor$() provides protection against attacks from predators and monsters. The quality of armor scales with the tier of the $(thing)Metal$(), with $(l:mechanics/leather_making)Leather$() being the weakest and $(l:mechanics/steel)Colored Steels$() being the strongest.'),
            knapping('tfc:knapping/leather_chestplate', 'Leather armor is $(thing)Knapped$() from $(l:mechanics/leather_making)Leather$(). It does not last long, but provides some decent protection if you have a full suit.').link(*['minecraft:leather_%s' % g for g in ('helmet', 'chestplate', 'leggings', 'boots')]),
            anvil_recipe('tfc:anvil/metal/unfinished_helmet/copper', '$(thing)Metal Armor$() requires multiple processing steps in an $(l:mechanics/anvils)Anvil$(). First, an $(thing)unfinished$() armor piece must be smithed. These require a $(thing)Double Sheet$() of the metal, except for boots which require a single sheet.'),
            welding_recipe('tfc:welding/metal/greaves/bismuth_bronze', 'Next, a $(thing)Sheet$() must be $(l:mechanics/anvils#welding)Welded$() to the armor piece to finish it. Chestplates require a $(thing)Double Sheet$() to be finished.'),
            text('Armor that is of the same tier but different metals may be subtly different. These are expressed in durability, as well as $(l:mechanics/damage_types)Damage Resistances$(). In order of increasing durability, the bronzes are ranked $(thing)Bismuth Bronze$(), regular $(thing)Bronze$(), and then $(thing)Black Bronze$(). For colored steels, $(thing)Red Steel$() lasts longer than $(thing)Blue Steel$().'),
            text('Steel armor also provides benefits such as $(thing)Toughness$() and $(thing)Knockback Resistance$(). Regular $(thing)Steel$() has 1 toughness, $(thing)Black Steel$() has 2 toughness, and the $(thing)Colored Steels$() have 3 toughness. $(thing)Black Steel$() has 5% knockback resistance, whereas the $(thing)Colored Steels$() have 10%.')
        )),
        entry('powderkegs', 'Powderkegs', 'tfc:powderkeg', pages=(
            text('$(thing)Powderkegs$() are a device used to create explosions. They have an inventory of 12 slots, which may only contain $(thing)gunpowder$(). The strength of the explosion is proportional to the amount of gunpowder contained.'),
            block_spotlight('The Powderkeg', 'Similar to the $(l:mechanics/barrels)Barrel$(), the powderkeg is a device which can be sealed and unsealed with $(item)$(k:key.use)$(). Only sealed powderkegs may be ignited.', 'tfc:powderkeg[sealed=true]'),
            text('Explosions from powderkegs will cause other powderkegs to light and explode. All blocks broken in an explosion will drop items, but not necessarily at the same location of the block they were from.'),
            crafting('tfc:crafting/powderkeg', text_contents='Powderkegs can be made from adding $(l:mechanics/dye)Red Dye$() and string to the crafting shape of a barrel.'),
            crafting('tfc:crafting/gunpowder', 'tfc:crafting/gunpowder_with_graphite'),
            text('Gunpowder can be made in two ways: the first formulation is from $(l:the_world/ores_and_minerals#saltpeter)Saltpeter$(), $(l:the_world/ores_and_minerals#saltpeter)Sulfur$(), and $(l:mechanics/charcoal_pit)Charcoal$() powder. Powder is made using a $(l:mechanics/quern)Quern$(). If you have access to $(l:the_world/ores_and_minerals#graphite)Graphite$(), then a more productive recipe can be used.'),
        )),
        entry('sluices', 'Sluices', 'tfc:textures/gui/book/icons/sluice.png', pages=(
            text('The $(thing)Sluice$() is a device that is used to process $(thing)Ore Deposits$(), producing the same results that $(l:mechanics/panning)Panning$() does, with different probabilities.').link('#tfc:sluices'),
            crafting('tfc:crafting/wood/sluice/sequoia', text_contents='The sluice is made from sticks and lumber.'),
            text('When placed, the sluice takes up two blocks. Water must flow through the top of the sluice and out the bottom in order for it to operate. The sluice is able to work when water appears to be flowing through it. The water flowing into the sluice must be the last block of a water stream. There must be an empty block below the bottom of the sluice for water to flow into.'),
            image('tfc:textures/gui/book/tutorial/sluice_cross.png', text_contents='A sluice with the correct water setup.'),
            text('To use the sluice, drop deposit items into the water stream and let them flow into it. They will appear on the sluice, and after some time, there is a chance of the processed items being spat out the bottom.'),
            image('tfc:textures/gui/book/tutorial/sluice_in_use.png', text_contents='A working sluice with items inside of it.'),
            text('Sluicing can give three possible products: $(thing)Ore$(), $(thing)Loose Rocks$(), and $(l:mechanics/gems)Gems$(). The probabilities are as follows:$(br)$(li)Ore: 55%%$(li)Loose Rock: 22.5%%$(li)Gem: 0.9%%$()Each rock type has a specific gem that it will drop, that is exclusive to that rock type.', title='Products of Panning'),
            empty_last_page()
        )),
        entry('lighting', 'Light Sources', 'tfc:candle', pages=(
            text('There are a number of ways to produce light in TFC. One way is the use of $(l:mechanics/lamps)Lamps$(), but they require fuel. Some light sources only require a spark.'),
            text('$(br) 1. $(l:mechanics/lighting#torches)Torches$()$(br) 2. $(l:mechanics/lighting#candles)Candles$()$(br) 3. $(l:mechanics/lighting#jack_o_lanterns)Jack \'o Lanterns$()', 'Table of Contents'),
            heat_recipe('tfc:heating/torch_from_stick', 'Torches can be made by heating sticks or stick bunches in a $(l:getting_started/firepit)Firepit$() or other heating device.').anchor('torches'),
            text('Torches last for 3 days, and then become $(thing)Dead Torches$(), which may be relit by clicking them with a torch, or by using a fire starting tool on them. Sticks can also be clicked on torches to light them. Torches dropped in water turn into $(thing)Wood Ash$() and $(thing)Sticks$().'),
            block_spotlight('Candles', text_content='Candles last for 11 days, and may also be relit.', block='tfc:candle[candles=3,lit=true]').anchor('candles').link('tfc:candle', '#tfc:colored_candles'),
            sealed_barrel_recipe('tfc:barrel/candle', 'Candles can be made by sealing $(thing)String$() in a $(l:mechanics/barrels)Barrel$() of $(l:mechanics/lamps#tallow)Tallow$().'),
            block_spotlight('Jack \'o Lanterns', 'Jack \'o Lanterns are the lit version of $(thing)Carved Pumpkins$(), and last for 4.5 days, before reverting to carved pumpkins.', 'tfc:jack_o_lantern').anchor('jack_o_lanterns').link('tfc:jack_o_lantern'),
            text('$(thing)Carved Pumpkins$() are made by clicking $(item)$(k:key.use)$() with $(thing)Shears$() or a $(thing)Knife$() on a fresh $(l:mechanics/crops#pumpkin)Pumpkin$(). Carved pumpkins do not rot, and can be worn on your head.'),
        )),
        entry('panning', 'Panning', 'tfc:textures/gui/book/icons/panning.png', pages=(
            text('$(thing)Panning$() is a method of obtaining small pieces of certain native ores by searching in rivers and other waterways.$(br2)Panning makes use of $(thing)Ore Deposits$() which are found in gravel patches in the bottom of lakes and rivers.$(br2)In order to get started panning, you will need an empty pan.').link('#tfc:ore_deposits'),
            knapping('tfc:knapping/ceramic/unfired_pan', 'Clay can be $(l:getting_started/pottery)knapped$() into a pan as shown above.'),
            heat_recipe('tfc:heating/pan/empty', 'Once the pan has been $(thing)knapped$(), it needs to be $(l:mechanics/heating)fired$() to create a $(thing)Ceramic Pan$().$(br2)The next thing you will need to find is some sort of $(thing)Ore Deposit$(). Ore deposits can come in several different ores: Native Copper, Native Silver, Native Gold, and Cassiterite.'),
            block_spotlight('Example', 'A native gold deposit in some slate.', 'tfc:deposit/native_gold/slate'),
            text('Then you can begin panning!$(br2)$(bold)1.$() With the pan in hand, $(thing)use$() it on the ore deposit block.$(br2)$(bold)2.$() While standing in water with the pan in your hand, hold down $(item)$(k:key.use)$() and you will start panning.$(br2)$(bold)3.$() After a few moments, if you are lucky, you may be rewarded with a small piece of ore in your inventory.'),
            image('tfc:textures/gui/book/tutorial/panning.png'),
            text('Panning can give three possible products: $(thing)Ore$(), $(thing)Loose Rocks$(), and $(l:mechanics/gems)Gems$(). The probabilities are as follows:$(br)$(li)Ore: 50%%$(li)Loose Rock: 25%%$(li)Gem: 1%%$()Each rock type has a specific gem that it will drop, that is exclusive to that rock type.', title='Products of Panning'),
            empty_last_page()
        )),
        entry('heating', 'Heating', 'tfc:textures/gui/book/icons/heating.png', pages=(
            text('Heating items is a way of converting one item to another, or an item to a fluid. Items can be heated in many ways - in a $(l:getting_started/firepit)Firepit$(), a $(l:getting_started/pit_kiln)Pit Kiln$(), or a $(l:mechanics/charcoal_forge)Charcoal Forge$(), to name a few. However they all function in the same way. When you place items inside these devices, the items will gradually start to heat up. This is visible on the item\'s tooltip.'),
            text('The temperature of an item is represented by a color, which will change through the following values:$(br2)$(7)$(bold)Warming$(): 1 - 80 °C$(br)$(7)$(bold)Hot$(): 80 - 210 °C$(br)$(7)$(bold)Very Hot$(): 210 - 480 °C$(br)$(4)$(bold)Faint Red$(): 480 - 580 °C$(br)$(bold)$(4)Dark Red$(): 580 - 730 °C$(br)$(c)$(bold)Bright Red$(): 730 - 930 °C$(br)$(6)$(bold)Orange$(): 930 - 1100 °C$(br)$(e)$(bold)$(t:Yellow)Yellow$(): 1100 - 1300 °C$(br)$(e)$(t:Yellow White)$(bold)Yellow White$(): 1300 - 1400 °C$(br)$(d)$(bold)$(t:White)White$(): 1400 - 1500 °C$(br)$(d)$(bold)$(t:Brilliant White)Brilliant White$(): >1500 °C'),
        )),
        entry('charcoal_forge', 'Charcoal Forge', 'tfc:textures/block/molten_lit.png', pages=(
            text('The $(thing)Charcoal Forge$() is a device used to $(l:mechanics/heating)heat$() and melt items. Forges are necessary to make a $(l:mechanics/crucible)crucible$() work. They are typically used to heat items to prepare them for smithing on an $(l:mechanics/anvils)anvil$(). $(br)It is constructed with 5 $(thing)stone$() blocks surrounding a $(l:mechanics/charcoal_pit#charcoal_pile)charcoal pile$() of 7 or 8 layers which is then lit.'),
            multiblock('A Charcoal Forge', 'A complete forge multiblock, ready to be lit.', True, multiblock_id='tfc:charcoal_forge'),
            text('A Forge needs a chimney in order to work. The block directly above the Forge must be air, or a $(l:mechanics/crucible)Crucible$(). In addition, either the block directly above, or one of eight nearby blocks must be able to see the sky - the blue stained glass in the visualization to the right.$(br2)A $(l:mechanics/bellows)Bellows$() can be placed on one block up and adjacent to the Forge, in order to increase the maximum temperature it can reach.'),
            multiblock('', '', False, (
                ('  G  ', '  G  ', 'GGCGG', '  G  ', '  G  '),
                ('XXXXX', 'XXXXX', 'XX0XX', 'XXBXX', 'XXXXX'),
            ), {
                'X': 'tfc:rock/smooth/gabbro',
                'G': 'minecraft:light_blue_stained_glass',
                '0': 'tfc:charcoal_forge[heat_level=7]',
                'C': 'tfc:crucible',
                'B': 'tfc:bellows'
            }),
            image('tfc:textures/gui/book/gui/charcoal_forge.png', text_contents='The forge\'s interface.', border=False),
            text('The five slots at the bottom of the forge are used for fuel. While the forge is running, it periodically consumes fuel, which can be $(thing)Charcoal$() or $(l:the_world/ores_and_minerals#bituminous_coal)Coal$(). The top five slots will heat items, up to the temperature shown in the indicator on the left. The right side slots are for containers that can contain liquid metal, like $(thing)Vessels$() and $(thing)Molds$(). Items that melt in the forge will fill those containers.'),
        )),
        entry('charcoal_pit', 'Charcoal Pit', 'minecraft:charcoal', pages=(
            text('The $(thing)Charcoal Pit$() is a way of obtaining $(thing)Charcoal$(). Charcoal pits are made with $(thing)Log Piles$(). To place a log pile, $(item)$(k:key.use)$() and $(item)$(k:key.sneak)$() while holding a $(thing)Log$(). More logs can be inserted by either pressing $(item)$(k:key.use)$() directly while holding a log, or by pressing $(item)$(k:key.use)$() with something else to open the interface.').link('minecraft:charcoal'),
            block_spotlight('The Log Pile', 'Log piles need a solid block under them to be placed. They are highly flammable.', 'tfc:log_pile'),
            text('The charcoal pit is formed by surrounding log piles with solid, non-flammable blocks. The amount of charcoal produced is proportional to the amount of logs contained inside the log piles. To start the burning process, light one of the log piles, and then cover it. If it worked, you should see $(thing)smoke$() particles rise up from the structure.'),
            multimultiblock('The building of one possible charcoal pit, in layers.', *(
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:log_pile'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:burning_log_pile'}),
                multiblock('', '', False, (('     ', ' XXX ', ' XXX ', ' XXX ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:log_pile'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:charcoal_pile[layers=7]'}),
            )),
            text('After the charcoal pit burns out and stops smoking, you will be left with $(thing)Charcoal piles$(). The charcoal pile contains up to 8 layers of $(thing)Charcoal$(). Dig it with a shovel to obtain the charcoal items. Charcoal piles can be added to or placed with $(item)$(k:key.use)$().').anchor('charcoal_pile'),
            multimultiblock('The charcoal pile.', *[block_spotlight('', '', 'tfc:charcoal_pile[layers=%s]' % i) for i in range(1, 9)])
        )),
        entry('crucible', 'Crucible', 'tfc:crucible', pages=(
            text('A $(thing)Crucible$() is an advanced device used for the creation of $(l:mechanics/crucible#advanced_alloying)Alloys$(). It is a more precise method than using a $(l:getting_started/primitive_alloys)Small Vessel$() to make alloys.$(br2)To obtain a crucible, you will first need to obtain some $(l:mechanics/fire_clay)Fire Clay$(), which is a stronger material than clay. This fire clay can then be knapped to shape it into an $()Unfired Crucible$().'),
            knapping('tfc:knapping/ceramic/unfired_crucible', 'Knapping an $(thing)Unfired Crucible$().').link('tfc:ceramic/unfired_crucible'),
            heat_recipe('tfc:heating/crucible', 'After the crucible is knapped, it will need to be $(thing)fired$(), like any piece of pottery - a $(l:getting_started/pit_kiln)Pit Kiln$() or $(l:mechanics/charcoal_forge)Charcoal Forge$() would do.$(br2)In order to use the crucible, it needs a source of heat. The crucible can be heated by any heatable block below, usually a $(l:mechanics/charcoal_forge)Charcoal Forge$()').link('tfc:crucible'),
            multiblock('', 'A crucible heated by a charcoal forge below it.', False, (('   ', ' C ', '   '), ('GGG', 'G0G', 'GGG')), {
                'C': 'tfc:crucible',
                '0': 'tfc:charcoal_forge[heat_level=7]',
                'G': 'tfc:rock/bricks/granite'
            }),
            page_break(),
            text('Now, you are ready to use the crucible. When you use it, you will see the $(thing)Crucible Interface$(), shown to the right. The top region shows the current metal content of the crucible. The first metal shown is what would be produced, if it were to be extracted right now. Other metals shown are the makeup of the current alloy in the crucible.', title='Advanced Alloying').anchor('advanced_alloying'),
            image('tfc:textures/gui/book/gui/crucible.png', text_contents='The Crucible Interface', border=False),
            text('The crucible has nine slots where items can be added to be melted, and their liquefied contents will be directly added to the crucible. Molten metal containers such as $(l:getting_started/pottery#mold)Molds$() can also be placed here and they will be slowly drained into the crucible, allowing for precise control over your alloy\'s content. Holding $(item)$(k:key.sneak)$() while hovering over a draining mold or vessel makes it drain faster. $(br2)Molds or other fluid containers can also be placed in the output slot, and will be slowly filled with the current content of the crucible.'),
            text('The temperature indicator on the left will rise based on external sources of heat, such as a $(l:mechanics/charcoal_forge)Charcoal Forge$() below or heat from a $(l:mechanics/blast_furnace)Blast Furnace$() from above. Metal can be extracted from the crucible as long as it is still molten.$(br2)Finally, the crucible will keep its contents when broken, allowing you to transport the alloy container around if you wish.'),
        )),
        entry('bellows', 'Bellows', 'tfc:bellows', pages=(
            text('A $(thing)Bellows$() is a device which can be used to increase the air flow through another device which lets them burn at a hotter temperature. However, by burning at a hotter temperature they will also consume fuel faster. The bellows can provide air to a device that is directly in front of it, or in front and one block down. This allows it to provide air to a $(l:getting_started/firepit)Firepit$(), or a $(l:mechanics/charcoal_forge)Charcoal Forge$() for example.'),
            crafting('tfc:crafting/bellows', title='', text_contents='To use the bellows, simply place it facing the targeted heating device, and use it. The bellows will pump air into the device, raising the maximum temperature for a short time.')
        )),
        entry('grill', 'Firepit And Grill', 'tfc:grill', pages=(
            text('A $(thing)Grill$() is an item that can be added to a firepit to cook foods more efficiently. The grill is able to cook five items at once, and also gives these items the $(thing)Wood Grilled$() trait when cooking food, which provides a minor buff to the item\'s $(l:mechanics/decay)expiration date$(). In order to create a firepit with grill, first create a $(l:getting_started/firepit)Firepit$(), then use a $(thing)Wrought Iron Grill$() on the firepit.').link('tfc:wrought_iron_grill'),
            block_spotlight('A Firepit with Grill', '', 'tfc:grill'),
            anvil_recipe('tfc:anvil/grill', 'The grill is created by working a $(thing)Wrought Iron Double Sheet$() on an $(l:mechanics/anvils)Anvil$().$(br2)On the next page, you can see the grill interface. Like the firepit, it has four slots for fuel which must be added in the top slot, a temperature indicator, and five slots for heating items instead of one.'),
            image('tfc:textures/gui/book/gui/grill.png', text_contents='The grill interface.', border=False),
        )),
        entry('pot', 'Firepit And Pot', 'tfc:pot', pages=(
            text('A $(thing)Pot$() is an item that can be added to the firepit to cook new types of food and also produce some other useful items.$(br2)In order to create a firepit with a pot, first create a $(l:getting_started/firepit)Firepit$(), then use a $(thing)Ceramic Pot$() on the firepit.').link('tfc:pot'),
            block_spotlight('', 'A firepit with a pot attached.', 'tfc:pot'),
            knapping('tfc:knapping/ceramic/unfired_pot', 'A ceramic pot must be $(l:getting_started/pottery)Knapped$() out of clay first.').link('tfc:ceramic/unfired_pot'),
            heat_recipe('tfc:heating/ceramic/pot', 'It then must be $(l:mechanics/heating)fired$() to create a $(thing)Ceramic Pot$() which can be used on the firepit.').link('tfc:ceramic/pot'),
            text('Like the firepit, the pot has four slots for fuel which must be added in the top slot, and a temperature indicator. The pot also contains five item slots and holds up to $(thing)1000 mB$() of any fluid.$(br2)In order to cook something in the pot, first the fluid must be added by using any type of fluid container, such as a bucket, on the pot. Then add items and light the pot. It will begin boiling for a while until the recipe is completed.'),
            image('tfc:textures/gui/book/gui/pot.png', text_contents='The pot interface, actively boiling and making a type of soup.', border=False),
            item_spotlight('tfc:food/fruit_soup', 'Soup Recipes', text_contents='Soup is made from 3-5 $(thing)fruits$(), $(thing)vegetables$(), or $(thing)meats$() in a pot of $(thing)water$(). When the recipe is done, the water in the pot will turn red. $(item)$(k:key.use)$() with a $(l:mechanics/bowls)bowl$() to retrieve it. Soup combines multiple nutrients into a single meal.').anchor('soup'),
            item_spotlight('tfc:bucket/red_dye', 'Simple Recipes', text_contents='Other pot recipes transform the items and fluid in the pot into something else. For example, boiling five $(thing)ash$() in $(thing)water$() makes $(thing)lye$().')  # todo: better recipe page for the pot
        )),
        entry('chisel', 'Chisel', 'tfc:metal/chisel/wrought_iron', pages=(
            text('Chisels are a tool for creating decorative forms of other blocks, including slabs and stairs. In order to get started, you will need a $(thing)Chisel$() and any type of $(thing)Hammer$(). Chisels must be cast in molds or forged on an $(l:mechanics/anvils)Anvil$(). In order to start chiseling, hold the chisel in your main hand and a hammer in your off hand, and target a block in the world.').link('#c:tools/chisel'),
            text('If you can chisel that block, a $(c)red outline$() of the block to be chiseled will be shown based on what $(thing)Mode$() you have selected.$(br2)The chisel has three modes that can be switched by using $(item)$(k:tfc.key.cycle_chisel_mode)$(): $(thing)Slab$(), $(thing)Stair$(), and $(thing)Smooth$(). An indicator of your chisel mode should show up next to the hotbar.'),
            text('Press $(item)$(k:key.use)$() to convert the block to the shape that was highlighted in red. If you chiseled a slab, the half of the block you chiseled will pop off and you can grab it. Change the orientation of your player and your mouse to change the orientation of the chiseled block. As a rule of thumb, the orientation of the chiseled block will be the same as if you placed it in that orientation yourself.'),
            image('tfc:textures/gui/book/tutorial/chisel_block.png', 'tfc:textures/gui/book/tutorial/chisel_stair.png', 'tfc:textures/gui/book/tutorial/chisel_slab.png', text_contents='The three chisel modes usable on a $(thing)Raw Limestone$() block.'),
            crafting('tfc:crafting/rock/smooth/marble', text_contents='The chisel can also be used in some crafting recipes as a shortcut for large quantities.'),
            text('Be careful! Chiseling in a mineshaft is not safe. Each time you chisel, there is a chance of $(l:mechanics/support_beams)Collapse$().')
        )),
        entry('support_beams', 'Support Beams & Collapses', 'tfc:wood/support/oak', pages=(
            text('In TerraFirmaCraft, raw rock is unstable and susceptible to $(thing)Collapsing$(). Many rock blocks, including $(thing)Raw Rock$(), $(thing)Ores$(), $(thing)Smooth$() and $(thing)Spikes$() can all rain down on your head under the right circumstances.$(br2)$(thing)Support Beams$() can be used to prevent collapses from occurring.', title='Support Beams').link('#tfc:support_beams'),
            text('Collapses can occur whenever a player $(thing)mines any Raw Rock$() that is near to $(thing)Unsupported Raw Rock$(). Once a collapse has started, however, even previously $(thing)Supported$() rock can start to collapse.$(br2)The rock on the roof of caves is $(thing)Naturally Supported$(). Any raw rock with a non-collapsible solid block beneath it, is also $(thing)Supported$(). Alternatively, $(thing)Support Beams$() can support a wide area at once.'),
            text('When graded $(l:the_world/ores_and_minerals)Ore$() (ore that can be poor, normal, or rich) collapses, it degrades in quality. Rich ore will become normal, normal will become poor, and poor will turn to cobblestone. Mineral ores will turn into cobblestone right away.'),
            text('Dirt, grass, clay, gravel, cobblestone, and sand are also affected by gravity. However, unlike vanilla gravity blocks, these blocks fall down slopes, and cannot be stacked more than one high without supporting blocks around. $(l:mechanics/sluices)Ore Deposits$() also landslide, but do not degrade in quality.'),
            crafting('tfc:crafting/wood/support/oak', text_contents='To get started, $(thing)Support Beams$() can be crafted with a $(thing)Saw$() and any type of $(thing)Logs$().$(br2)Placing a $(thing)Support Beam$() on top of a block places a column up to three tall. These must have a solid block beneath them to stay upright.', title='Support Beams'),
            multimultiblock('$(thing)Horizontal$() beams can be placed between to connect two $(thing)Vertical$() beams that are within five blocks, as in the above diagram.', *[
                multiblock('', '', False, (
                    ('   ', 'CRD', '   '),
                    ('   ', 'V W', '   '),
                    ('   ', 'V W', '   '),
                    ('GGG', 'G0G', 'GGG'),
                    ('   ', '   ', '   '),
                ), {
                    'R': 'tfc:wood/horizontal_support/oak[north=true,south=true]' if step >= 3 else 'air',
                    'C': 'tfc:wood/vertical_support/oak[south=true]' if step >= 3 else ('tfc:wood/vertical_support/oak' if step >= 1 else 'air'),
                    'D': 'tfc:wood/vertical_support/oak[north=true]' if step >= 3 else ('tfc:wood/vertical_support/oak' if step >= 2 else 'air'),
                    'V': 'tfc:wood/vertical_support/oak' if step >= 1 else 'air',
                    'W': 'tfc:wood/vertical_support/oak' if step >= 2 else 'air',
                    '0': 'tfc:rock/raw/andesite',
                    'G': 'tfc:rock/raw/andesite',
                })
                for step in range(5)  # Duplicate the last step, to hold on the completed image
            ]),
            text('Only $(thing)Horizontal Support Beams$() cause nearby blocks to be $(thing)Supported$(). Any block within a $(bold)9 x 5 x 9$() area centered on a horizontal support beam is considered $(thing)Supported$().$(br2)In addition to being supported by support beams, rock can be supported simply by the virtue of having a solid block below it, such as more rock. However, it is important to note that $(thing)Non Solid Blocks$() such as $(thing)Stairs$() and $(thing)Slabs$(), along with $(thing)Smooth Stone$(), do $(bold)not$() count as supporting.'),
            text('Finally, it is important to know that $(l:mechanics/chisel)Chiseling$() has the potential to cause collapses, just as easily as mining does, when it is done on any $(thing)Raw Rock$() that has potential to cause a nearby collapse.$(br2)$(br2)Remember kids: practice safe mining!', title='Chiseling'),
        )),
        entry('prospecting', 'Prospecting', 'tfc:metal/propick/wrought_iron', pages=(
            text('You remembered where you picked up those $(l:getting_started/finding_ores)Small Metal Nuggets$(), right? Finding additional ores may require extensive exploration and mining. You should become very familiar with $(l:the_world/ores_and_minerals)Ores and Minerals$(). If you need a specific resource, you must find the rock type it spawns in either under your feet or across the world.'),
            text('When picking up small nuggets becomes unsatisfying, it is time to start prospecting to find ore veins:$(br)$(li)Small nuggets occur when ore is nearby, within 15 blocks horizontally and 35 vertically. If you find the center of a group of nuggets, it\'s likely that the vein is beneath you.$(li)Exposed ore can occur in cliffs and water bodies, which may be seen from farther away.'),
            item_spotlight('tfc:metal/propick/copper', 'Prospector\'s Pick', text_contents='If you\'re looking for metal ores or mineral veins (which have no nuggets), and you can\'t find the vein by guessing, it\'s time to pull out the $(thing)Prospector\'s Pick$(). It searches the 25x25x25 area centered on the block clicked, and reports to the action bar the amount and type of ore located.').link(*['tfc:metal/propick/%s' % m for m in TOOL_METALS]).anchor('propick'),
            knapping('tfc:knapping/ceramic/unfired_propick_head_mold', 'To make a Prospector\'s Pick, you can $(l:getting_started/pottery)knapp$() an unfired mold out of clay as shown above.'),
            heat_recipe('tfc:heating/ceramic/propick_head_mold', 'Once the mold has been $(l:getting_started/pottery)knapped$(), it needs to be $(l:mechanics/heating)fired$() to create a $(thing)Propick Head Mold.$()$(br2)To create the tool head, $(l:getting_started/finding_ores#casting)cast$() liquid metal into the mold.'),
            anvil_recipe('tfc:anvil/metal/propick_head/wrought_iron', 'A Prospector\'s Pick Head can also be $(l:mechanics/anvils#working)smithed$() out of an $(thing)ingot$() of any tool metal on an $(l:mechanics/anvils)Anvil$().$(br2)The Prospector\'s Pick is then created by crafting the tool head with a stick.'),
            text('The Prospector\'s Pick will never report finding something when nothing is actually there. However, it may incorrectly say nothing is there when a vein is in range. Higher tier tools will reduce or eliminate these false negatives.$(br2)Prospector\'s Picks at the same tier give identical results when used on the same block unless ores were removed.$(br2)If the Prospector\'s Pick finds multiple ores nearby, it will only report one.'),
            text('Right-clicking a Prospector\'s Pick on a block will report finding one of these possible results:$(br)$(li)Nothing (may be false)$(li)Traces$(li)A Small Sample$(li)A Medium Sample$(li)A Large Sample$(li)A Very Large Sample$(br2)Very large samples indicate at least eighty and potentially many more blocks.'),
        )),
        entry('bloomery', 'Bloomery', 'tfc:bloomery', pages=(
            text('The $(thing)Bloomery$() is a device used to smelt $(thing)Iron Ore$() into $(thing)Iron Blooms$() which can be worked into $(thing)Wrought Iron$(). The iron ores are $(l:the_world/ores_and_minerals#hematite)Hematite$(), $(l:the_world/ores_and_minerals#limonite)Limonite$(), and $(l:the_world/ores_and_minerals#magnetite)Magnetite$(). These ores melt into $(thing)Cast Iron$() rather than $(thing)Wrought Iron$(). All iron-bearing items melt into Cast Iron. To turn them into usable iron, the bloomery is needed. Any iron item can be used in the bloomery, including iron tools and cast iron ingots!'),
            crafting('tfc:crafting/bloomery', text_contents='The bloomery is made from 8 $(thing)Bronze Double Sheets$().'),
            multiblock('A Bloomery', 'A minimum size bloomery. The bloomery block will open and close with $(item)$(k:key.use)$().', True, multiblock_id='tfc:bloomery'),
            text('The bloomery can contain up to a maximum of 48 $(thing)Inputs$(), with 16 items per layer of the chimney. To add layers to the chimney, stack up two more layers of stone blocks.$(br2)To add items to the bloomery, climb up to the top and throw items inside. A tower of grey ore should form.'),
            image('tfc:textures/gui/book/tutorial/bloomery_hole.png', text_contents='Adding items to the bloomery.'),
            text('The bloomery consumes $(thing)2 Charcoal$(), and $(thing)100 mB of Cast Iron$() to produce one $(thing)Bloom$(). After filling the bloomery with a combination of $(thing)Charcoal$() and $(thing)Ore$(), light the bloomery block, and wait 15 hours for the bloomery to smelt. When the bloomery shuts off, it leaves behind a $(thing)Bloom$() block. This contains $(thing)Raw Iron Blooms$() which can be obtained by mining the $(thing)Bloom$() repeatedly with a pickaxe.'),
            block_spotlight('The Bloom Block', 'A large $(thing)Bloom$().', 'tfc:bloom[layers=8]').link('tfc:bloom'),
            anvil_recipe('tfc:anvil/refined_iron_bloom', 'The $(thing)Raw Iron Bloom$() must be worked in a $(l:mechanics/anvils)anvil$() to make $(thing)Refined Iron Bloom$().').link('tfc:raw_iron_bloom'),
            anvil_recipe('tfc:anvil/metal/ingot/wrought_iron', 'The $(thing)Refined Iron Bloom$() must be worked in a $(l:mechanics/anvils)anvil$() to make $(thing)Wrought Iron Ingots$().').link('tfc:refined_iron_bloom'),
            text('$(li)If the bloomery finds itself with more items contained than it can handle based on its chimney, it will try to spit them out the front.$()$(li)To retrieve your items from a bloomery that is not lit, do not break the molten block tower. Break the bloomery block.$()$(li)Blooms will only melt into cast iron, not wrought iron. They must be worked!$()', 'Smith\'s Notes'),
        )),
        entry('blast_furnace', 'Blast Furnace', 'tfc:blast_furnace', pages=(
            text('A $(thing)Blast Furnace$() is an advanced device which is used in the creation of $(thing)Steel$(). By mixing $(thing)Iron Ores$(), $(thing)Charcoal$(), and $(thing)Flux$() in a controlled, hot environment, you can create a stronger metal than cast or wrought iron.$(br2)To obtain a blast furnace, you will first need a $(l:mechanics/crucible)Crucible$() and a lot of $(thing)Wrought Iron Sheets$().'),
            crafting('tfc:crafting/blast_furnace', text_contents='Crafting a blast furnace itself requires a $(thing)Crucible$(), along with some of the $(thing)Wrought Iron Sheets$() you will need.'),
            text('You will then need to construct the blast furnace, along with its $(thing)Chimney$(). The chimney must be composed out of $(l:mechanics/fire_clay#fire_bricks)Fire Bricks$(), as they are strong enough to withstand the intense heat. It must then be lined with $(thing)Wrought Iron Sheets$(), for extra reinforcement. Stronger metals such as $(thing)Steel$() can also be used for the sheets, if desired.'),
            multiblock('A Blast Furnace', 'A blast furnace with a minimum height chimney.', True, multiblock_id='tfc:blast_furnace'),
            text('The blast furnace\'s chimney can be up to five layers - each layer requiring four $(thing)Fire Bricks$() and twelve $(thing)Wrought Iron Sheets$() to complete. Having more layers increases the total capacity of the blast furnace, allowing it to smelt more steel at once. Each chimney layer, up to a maximum of five, allows the blast furnace to hold four additional ore items.'),
            text('In order to use the blast furnace, you must drop items in the top of the chimney - for steel production, you must add an equal number of items of $(thing)Iron Ores$() and $(l:mechanics/flux)Flux$(). Any iron ores or items that are able to melt into $(thing)Cast Iron$() will do. You will also need to add $(l:mechanics/charcoal_pit)Charcoal$(), which will be consumed as the blast furnace works.'),
            page_break(),
            text('Using the blast furnace will open the blast furnace interface, seen to the right. In this interface, you will see meters for both ore and fuel contents of the blast furnace. The top right slot must have a $(thing)Tuyere$(), which is a metal pipe used to funnel air into the blast furnace, required to reach the hottest temperatures to smelt steel. A tuyere can be smithed on an $(l:mechanics/anvils)Anvil$().'),
            image('tfc:textures/gui/book/gui/blast_furnace.png', text_contents='The Blast Furnace Interface', border=False),
            text('You will also need a $(l:mechanics/bellows)Bellows$() in order for the Blast Furnace to reach a temperature which will melt iron. This can be placed on any of the four sides of the blast furnace.'),
            multiblock('', 'A full size blast furnace with bellows and crucible attached.', True, multiblock_id='tfc:full_blast_furnace'),
            text('Finally, to get started, light the blast furnace with a $(l:getting_started/firepit#firestarter)Fire Starter$() or a $(thing)Flint and Steel$(). It will begin to heat the ores inside. Make sure that the blast furnace continues to have fuel, and use the bellows to add air to the blast furnace after its internal temperature has reached the maximum for charcoal. After the ores inside heat up, they will melt and convert into $(l:mechanics/steel)Pig Iron$().'),
            text('This liquid metal will drip into any metal fluid container placed immediately below the blast furnace, such as a $(l:mechanics/crucible)Crucible$(). It can be cast into ingot molds from the output slot of the crucible and worked into $(l:mechanics/steel)Steel$().'),
        )),
        entry('steel', 'Steel', 'tfc:metal/ingot/steel', pages=(
            text('Steel is an advanced material which can be used to create tools and $(l:mechanics/armor)Armor$(), and comes in a few different varieties: $(thing)Steel$(), $(thing)Black Steel$(), $(thing)Red Steel$(), and $(thing)Blue Steel$().$(br2)In order to create steel, you must first create $(thing)Pig Iron$() in a $(l:mechanics/blast_furnace)Blast Furnace$(), and cast it into $(thing)Ingots$().'),
            anvil_recipe('tfc:anvil/metal/ingot/high_carbon_steel', 'A $(thing)Pig Iron Ingot$() can then be worked in an anvil to create a $(thing)High Carbon Steel$() ingot, which can be worked again to create a $(thing)Steel Ingot$()').link('tfc:metal/ingot/pig_iron', 'tfc:metal/ingot/high_carbon_steel', 'tfc:metal/ingot/steel'),
            page_break(),
            text('$(thing)Black Steel$() is an advanced form of steel formed from an alloy of steel and some other metals. In order to create it, you will need to create an alloy called $(thing)Weak Steel$() in a $(l:mechanics/crucible)Crucible$().', title='Black Steel').anchor('black_steel'),
            alloy_recipe('Weak Steel', 'weak_steel', 'Molten $(thing)Weak Steel$() can be cast into ingots.').link('tfc:metal/ingot/weak_steel'),
            welding_recipe('tfc:welding/metal/ingot/high_carbon_black_steel', '$(thing)Weak Steel Ingots$() can then be welded with $(thing)Pig Iron Ingots$() to create $(thing)High Carbon Black Steel Ingots$(). Finally, these can be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Black Steel Ingots$().').link('tfc:metal/ingot/high_carbon_black_steel', 'tfc:metal/ingot/black_steel'),
            text('$(thing)Black Steel$() can be used to craft tools and $(l:mechanics/armor)Armor$(), and also is used as a key ingredient in the creation of $(l:mechanics/steel#blue_steel)Blue Steel$() and $(l:mechanics/steel#red_steel)Red Steel$().'),
            page_break(),
            text('$(thing)Blue Steel$() is one of the two highest tier metals, along with $(l:mechanics/steel#red_steel)Red Steel$(). Similar to $(l:mechanics/steel#black_steel)Black Steel$(), the first step is to create an alloy of $(thing)Weak Blue Steel$() in a $(l:mechanics/crucible)Crucible$().', title='Blue Steel').anchor('blue_steel'),
            alloy_recipe('Weak Blue Steel', 'weak_blue_steel', '').link('tfc:metal/ingot/weak_blue_steel'),
            welding_recipe('tfc:welding/metal/ingot/high_carbon_blue_steel', '$(thing)Weak Blue Steel Ingots$() can be welded with $(l:mechanics/steel#black_steel)Black Steel Ingots$() to create $(thing)High Carbon Blue Steel Ingots$(). Finally, these can be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Blue Steel Ingots$().').link('tfc:metal/ingot/high_carbon_blue_steel', 'tfc:metal/ingot/blue_steel'),
            text('$(thing)Blue Steel$() can be used to create tools and $(l:mechanics/armor)Armor$(), and a $(thing)Blue Steel Bucket$(), which can be used to transport 1000 mB of both liquid fluids, and $(6)Lava$().'),
            page_break(),
            text('$(thing)Red Steel$() is one of the two highest tier metals, along with $(l:mechanics/steel#blue_steel)Blue Steel$(). Similar to $(l:mechanics/steel#black_steel)Black Steel$(), the first step is to create an alloy of $(thing)Weak Red Steel$() in a $(l:mechanics/crucible)Crucible$().', title='Red Steel').anchor('red_steel'),
            alloy_recipe('Weak Red Steel', 'weak_red_steel', '').link('tfc:metal/ingot/weak_red_steel'),
            welding_recipe('tfc:welding/metal/ingot/high_carbon_red_steel', '$(thing)Weak Red Steel Ingots$() can be welded with $(l:mechanics/steel#black_steel)Black Steel Ingots$() to create $(thing)High Carbon Red Steel Ingots$(). Finally, these can be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Red Steel Ingots$().').link('tfc:metal/ingot/high_carbon_red_steel', 'tfc:metal/ingot/red_steel'),
            text('$(thing)Red Steel$() can be used to create tools and $(l:mechanics/armor)Armor$(), and a $(thing)Red Steel Bucket$(), which can be used to transport 1000 mB of both liquid fluids, and $(6)Lava$()'),
        )),
        entry('anvils', 'Anvils', 'tfc:metal/anvil/copper', pages=(
            text('Anvils are an important tool required for metalworking, as they allow you to work and weld metal ingots into various different forms.$(br2)Anvils can be useful for both $(l:mechanics/anvils#working)Working$(), which is used to form one piece of metal into another, or $(l:mechanics/anvils#welding)Welding$(), which is used to fuse two metal items into one solid piece.').link('#tfc:anvils'),
            block_spotlight('', 'All types of metal anvils.', '#tfc:anvils'),
            crafting('tfc:crafting/metal/anvil/copper', text_contents='Anvils can be crafted with $(thing)Double Ingots$() of their respective metal. For your first anvil, you must $(l:mechanics/anvils#welding)weld$() double ingots first on a $(l:getting_started/primitive_anvils)Stone Anvil$().'),
            text('Anvils each have a $(thing)Tier$(), which defines what types of material they can work and weld. An anvil can work metals of its current tier, and it can weld metals that are one tier higher.$(br)$(li)$(bold)Tier 0$(): Stone Anvils$(li)$(bold)Tier I$(): Copper$(li)$(bold)Tier II$(): Bismuth Bronze, Black Bronze, Bronze$(li)$(bold)Tier III$(): Wrought Iron$(li)$(bold)Tier IV$(): Steel$(li)$(bold)Tier V$(): Black Steel$(li)$(bold)Tier VI$(): Red Steel, Blue Steel').anchor('tiers'),
            page_break(),
            text('In order to work an item on the anvil, you will need to use the anvil, to open up the anvil interface, seen to the right. On the left, there are two input slots for items - for working, the target item must be in the right hand slot. You will also need a hammer while working, either in the hammer slot on the right of the anvil $(bold)or$() in your main hand. The hammer will gradually take damage as you work the item.', title='Working').anchor('working'),
            image('tfc:textures/gui/book/gui/anvil_empty.png', text_contents='The anvil interface.', border=False),
            text('You will then need to select the $(thing)Plan$(), which chooses which item you want to create. $(item)$(k:key.attack)$() on the the scroll button, and then pick one of the items to create. The anvil interface will return, but now you will have selected a plan - the scroll will show the item you are working to create, and the $(thing)Rules$() and $(thing)Target$() will now be populated.'),
            image('tfc:textures/gui/book/gui/anvil_in_use.png', text_contents='After selecting to create a pickaxe.', border=False),
            text('In the middle of the anvil screen, there is a bar with two colored indicators. The $(2)green$() pointer, is your current working progress. The $(4)red$() pointer, is the target. Your goal is to line up the current progress, with the target.$(br2)In order to do this, you can use the $(2)green$() and $(4)red$() action buttons, which move your current progress a certain amount, depending on the action taken.', title='Targets'),
            text('$(2)Green$() actions will always move your target $(bold)right$(), and $(4)Red$() actions will always move your progress $(bold)left$(). Note that if you move your target off of the progress bar, you will have overworked your item - you will lose the ingot. However, while working, you must also match the $(thing)rules$()...'),
            text('The $(thing)rules$(), are the two or three icons shown on the top of the anvil interface. They represent specific actions that must be taken, at specific times, in order for your working to be a success. For example, a rule could be $(2)Bend Second Last$(), meaning the second to last action you take $(bold)must$() be a $(2)Bend$() action.', title='Working Rules'),
            text('Your last three actions are shown right underneath the rules. When a rule is satisfied, its outline will change to green. Success occurs when all rules are satisfied.$(br2)Finally, you have to be mindful of your item\'s $(l:mechanics/heating)temperature$(). Metals can only be worked when they are above a certain temperature where the tooltip shows "Can Work". You may take an item out and re-heat it during the working process.'),
            text('Working can be tedious, and take many steps to get correct. However, there is a reward for being efficient. Some items, such as tool heads, when they are worked in a low or minimal amount of steps, receive a Forging Bonus based on how efficiently they were forged. This bonus will then apply to tools that the item is used in, for example, a pickaxe head used to make a pickaxe.', title='Forging Bonuses'),
            item_spotlight('tfc:metal/pickaxe/wrought_iron[tfc:forging_bonus="perfect"]', 'Perfectly Forged', False, 'There are four tiers of forging bonus:$(li)Poorly Forged$(li)Well Forged$(li)Expertly Forged$(li)Perfectly Forged$(br2)These bonuses increase the power of your tool - making it break less often, mine faster, and/or do more damage in combat, depending on the tool.'),
            page_break(),
            text('Welding is a process through which two items are fused together to create a new item. Welding works the same whether on a $(l:getting_started/primitive_anvils)Stone Anvil$() or a metal anvil.$(br2)First, you must place the two items you want to weld on the anvil. You can do this either by using the items on the anvil, or by opening the anvil interface and inserting them in the two leftmost slots.', title='Welding').anchor('welding'),
            text('You also need to have at least one $(l:mechanics/flux)Flux$() in the anvil to aid the welding process. Then, while both items are $(l:mechanics/heating)hot enough$() to weld - the tooltip will say "Can Weld" - you must use any $(thing)Hammer$() on the anvil. You will hear a hammering sound and the items will be welded together. They can then be extracted by using $(item)$(k:key.use)$() on the anvil with an empty hand.'),
        )),
        entry('fire_clay', 'Fire Clay', 'tfc:fire_clay', pages=(
            text('The list of uses of fire clay is small, but all of them are important. Fire clay is a stronger variant of clay that has better heat resistance. It is used to make things that have to get very hot!'),
            crafting('tfc:crafting/fire_clay', text_contents='Fire clay is made from $(l:the_world/ores_and_minerals#graphite)graphite$() powder, crushed in a $(l:mechanics/quern)quern$(), as well as $(l:the_world/ores_and_minerals#kaolinite)kaolinite$() powder'),
            heat_recipe('tfc:heating/powder/kaolinite', 'Kaolinite powder is made by heating $(l:the_world/ores_and_minerals#kaolinite)Kaolin Clay$(). However, the process is not perfect, and only 20%% of clay will form powder!'),
            knapping('tfc:knapping/ceramic/unfired_crucible', 'The $(l:mechanics/crucible)Crucible$() in its unfired state is made from fire clay.').anchor('crucible'),
            knapping('tfc:knapping/ceramic/unfired_brick', 'The $(l:mechanics/blast_furnace)Blast Furnace$() only accepts fire bricks as insulation.').anchor('fire_bricks'),
            knapping('tfc:knapping/ceramic/unfired_fire_ingot_mold', '$(thing)Fire Ingot Molds$() are a stronger type of $(l:getting_started/pottery#mold)Ingot Mold$() that has just a 1 in 100 chance of breaking, compared to 1 in 10 for a regular ingot mold.'),
        )),
        entry('quern', 'Quern', 'tfc:quern', pages=(
            text('The $(thing)Quern$() is a device for grinding items. It can make powders, $(l:mechanics/dye)dyes$(), and some other items. It is assembled from a $(thing)Base$() and $(thing)Handstone$().$(br2)The Quern can also be connected to a $(l:mechanics/mechanical_power#quern)Mechanical Power$() network.'),
            crafting('tfc:crafting/quern', text_contents='The base of the quern can be crafted with three $(thing)smooth stone$() and three of any other $(thing)Stone$() blocks.'),
            crafting('tfc:crafting/handstone', text_contents='The quern needs a $(thing)Handstone$() to operate.'),
            image('tfc:textures/gui/book/tutorial/quern_empty.png', text_contents='Point at the top of the quern block and $(item)$(k:key.use)$() to place the handstone.'),
            image('tfc:textures/gui/book/tutorial/quern_add_item.png', text_contents='Use $(item)$(k:key.use)$() on the top of the handstone to add items.'),
            image('tfc:textures/gui/book/tutorial/quern_handle.png', text_contents='Use $(item)$(k:key.use)$() the handle to spin the handstone.'),
            image('tfc:textures/gui/book/tutorial/quern_result.png', text_contents='The output should appear on the base of the quern. $(item)$(k:key.use)$() anywhere on the base to retrieve it.'),
            quern_recipe('tfc:quern/powder/sulfur', 'The quern is used to make various $(thing)Powders$() from ores, like $(thing)Sulfur$().'),
            quern_recipe('tfc:quern/red_dye', '$(l:mechanics/dye)Dye$() can be obtained from various flowers.'),
            quern_recipe('tfc:quern/powder/emerald', '$(thing)Gems$() can also be ground into powder.'),
            quern_recipe('tfc:quern/food/barley_flour', '$(thing)Flour$() is also obtainable from the quern.'),
            quern_recipe('tfc:quern/powder/flux', '$(l:mechanics/flux)Flux$() is also obtainable from the quern.'),
        )),
        entry('fishing', 'Fishing', 'tfc:metal/fishing_rod/copper', pages=(
            text('$(thing)Fishing$() is a way of hunting the fish that swim around in rivers, lakes, and oceans. Fishing rods must be baited and cast. Fish attempt to eat the bait, and sometimes succeed. Reeling in a fish takes work, and becomes easier with higher level hooks.').link('#c:tools/fishing_rod'),
            anvil_recipe('tfc:anvil/metal/fish_hook/bismuth_bronze', 'First, you have to forge a fishing hook in an $(l:mechanics/anvils)Anvil$().'),
            crafting('tfc:crafting/metal/fishing_rod/bismuth_bronze', text_contents='The fishing rod is crafted with a fishing hook.', title='Fishing Rod'),
            text('Fishing rods are not useful without bait. Bait can be added to rods in a crafting table. To catch normal fish, you need $(thing)Seeds$() or $(thing)Shellfish$(). To catch larger fish, such as $(thing)Dolphins$() and $(thing)Orcas$(), you need $(item)cod$(), $(item)salmon$(), $(item)tropical fish$(), or $(item)bluegills$().'),
            text('To release the bobber, $(item)$(k:key.use)$(). Wait for a fish to spot the hook and bite it. Then $(item)$(k:key.use)$() to pull it in. As you do that, the meter on your hotbar will fill up. Pull too quickly, and the fish will get away with the bait. Each time you fish, the fish has a chance of eating the bait. To catch the fish, pull it up on land and kill it with a tool. You can hold the fishing rod in your off hand to make this easier.'),
            image('tfc:textures/gui/book/tutorial/fishing.png', text_contents='The fishing bar replaces the experience bar when active.'),
        )),
        entry('fertilizers', 'Fertilizers', 'tfc:powder/sylvite', pages=(
            text('Fertilizers are used to add nutrients to $(l:mechanics/crops)crops$(). $(item)$(k:key.use)$() with a fertilizer in your hand on some $(thing)Farmland$() or a $(thing)Crop$() to add the nutrients. Particles should appear, indicating the fertilizer was added.', title='Fertilization'),
            fertilizer('tfc:compost', 'Compost is the product of the $(l:mechanics/composter)Composter$().', 0.2, 0, 0.2),
            fertilizer('minecraft:bone_meal', 'Bonemeal is made of crushed bones.', p=0.1),
            fertilizer('tfc:powder/saltpeter', 'Saltpeter is made from its ore.', n=0.1, k=0.4),
            fertilizer('tfc:groundcover/guano', 'Guano is found deep underground and on gravelly shores.', 0.8, 0.5, 0.1),
            fertilizer('tfc:powder/wood_ash', 'Wood ash is produced by breaking firepits. Throwing a torch item into water also has a chance to produce ash.', p=0.1, k=0.2),
            fertilizer('tfc:powder/sylvite', 'Sylvite is made from its ore.', k=0.5),
            empty_last_page()
        )),
        entry('composter', 'Composter', 'tfc:textures/gui/book/icons/composter.png', pages=(
            text('The composter is an essential tool for making fertilizer. It needs both $(2)Green$() and $(4)Brown$() items to work. Different items contribute more to the amount of compost produced than others. To add an item to it, $(item)$(k:key.use)$(). The items that can be added are described on later pages.'),
            crafting('tfc:crafting/composter', text_contents='The composter just requires some $(thing)Lumber$() and $(thing)Dirt$() to make!'),
            text('Composters operate better in certain conditions. Composters that have a block of snow on top work slightly faster. Composters in regions of less than 150mm or greater than 350mm of rainfall operate much slower, with that effect getting stronger closer to the maximum and minimum rainfall. Also, composters that are touching other composters work slower.'),
            multimultiblock('The composter at its empty, working, and complete stage.', *[block_spotlight('', '', 'tfc:composter[stage=%s,type=%s]' % (s, t)) for s, t in (('0', 'normal'), ('8', 'normal'), ('8', 'ready'))]),
            text('The composter takes 12 days to complete in average conditions. When it is ready, it will have a dirt-like color and gray particles will be emitted from the top of it. The compost can then be retrieved with  $(item)$(k:key.use)$() and  $(item)$(k:key.sneak)$() with an empty hand. Adding things like $(c)meat$() and $(c)bones$() to compost spoils it, turning it reddish and causing it to emit gross particles. The rotten compost can be removed in the same way as good compost. When used on a crop, it instantly kills it.'),
            block_spotlight('Rotten Compost', 'A rotten composter.', 'tfc:composter[stage=8,type=rotten]'),
            item_spotlight('#tfc:compost_greens_low', text_contents='Some $(2)green$() items contribute little to the composter, such as plants. To fill a composter\'s appetite of green items, you need 16 of them.'),
            item_spotlight('#tfc:compost_greens', text_contents='Some $(2)green$() items contribute moderately to the composter, such as grains. To fill a composter\'s appetite of green items, you need 8 of them.'),
            item_spotlight('#tfc:compost_greens_high', text_contents='Some $(2)green$() items contribute a great amount to the composter, such as fruits and vegetables. To fill a composter\'s appetite of green items, you need 4 of them.'),
            item_spotlight('#tfc:compost_browns_low', text_contents='Some $(4)brown$() items contribute little to the composter, such as tall plants like dry phragmite, tree ferns, and vines, as well as fallen leaves. To fill a composter\'s appetite of brown items, you need 16 of them.'),
            item_spotlight('#tfc:compost_browns', text_contents='Some $(4)brown$() items contribute moderately to the composter, such as wood ash and jute. To fill a composter\'s appetite of brown items, you need 8 of them.'),
            item_spotlight('#tfc:compost_browns_high', text_contents='Some $(4)brown$() items contribute a great amount to the composter, such as melons, pumpkins, dead grass, pinecones, humus, and driftwood. To fill a composter\'s appetite of brown items, you need 4 of them.'),
            item_spotlight('#tfc:compost_poisons', text_contents='Some items will $(c)poison$() your compost. These include $(c)meat$() and $(c)bones$(). Poison compost, when used on a crop, instantly kills it.'),
            empty_last_page(),
        )),
        entry('flux', 'Flux', 'tfc:powder/flux', pages=(
            text('Flux is a powder which is required for $(l:mechanics/anvils#welding)Welding$() and also used as a catalyst in a $(l:mechanics/blast_furnace)Blast Furnace$(). Flux can be obtained by grinding specific items in a $(l:mechanics/quern)Quern$().$(br2)Flux can be obtained in a number of ways, one of which is from its native ore, $(l:the_world/ores_and_minerals#borax)Borax$().').link('tfc:powder/flux'),
            quern_recipe('tfc:quern/powder/flux', 'Some rocks - $(thing)Limestone$(), $(thing)Dolomite$(), $(thing)Chalk$(), or $(thing)Marble$() - can also be used as flux, after being ground in a $(l:mechanics/quern)Quern$(). Other items, including $(thing)Scutes$(), $(thing)Clams$(), $(thing)Mollusks$(), and the edible remains of $(l:the_world/wild_animals#shellfish)Shellfish$() can also be used to create flux.'),
        )),
        entry('gems', 'Gems', 'tfc:gem/opal', pages=(
            text('Gems are a kind of mineral that spawns in a variety of different places, such as $(thing)under rivers$() and in $(l:the_world/geology)Volcanoes$(). For information on the precise conditions, see $(l:the_world/ores_and_minerals)the ores and minerals chapter$().'),
            item_spotlight('#tfc:gem_powders', text_contents='Gems can be ground into powder using a $(l:mechanics/quern)Quern$(). Gem powders are particularly useful in $(l:mechanics/glassworking_applications#coloring)Coloring Glass$().'),
            text('Through $(l:mechanics/sluices)Sluicing$() and $(l:mechanics/panning)Panning$(), $(thing)Uncut Gems$() can be obtained. The gem that can be obtained with the sluice is tied to the rock type of the deposit being processed.'),
            text('Gems have higher $(thing)Hardness$() values than regular ore, requiring different strength tools to break them. This summarizes the minimum $(thing)Pickaxe$() tier required to break a gem ore block:$(br)$(li)Amethyst: Steel$(li)Diamond: Black Steel$(li)Emerald: Steel$(li)Lapis Lazuli: Wrought Iron$(li)Opal: Wrought Iron$(li)Pyrite: Copper$(li)Ruby: Black Steel$(li)Sapphire: Black Steel$(li)Topaz: Steel'),
            text('To cut a gem, you must craft $(thing)Sandpaper$(). Sandpaper is made from Black Sand, Flux, Glue, any Gem Powder, and Paper. Crafting an uncut gem with sandpaper cuts the gem.'),
            crafting('tfc:crafting/sandpaper'),
        )),
        entry('lamps', 'Lamps', 'tfc:metal/lamp/bismuth_bronze', pages=(
            text('Lamps are a long term source of light. They burn liquid fuel. Lamps retain their fuel content when broken. Using a bucket, $(item)$(k:key.use)$() on a lamp to add fuel to it. It can then be lit with a $(thing)firestarter$() or anything capable of lighting fires. $(thing)Olive Oil$() and $(thing)Tallow$() are lamp fuels. Shooting a lit lamp with an arrow can cause a fire.'),
            two_tall_block_spotlight('Lamps', 'A lit lamp hanging from a chain.', 'tfc:metal/lamp/copper[hanging=true,lit=true]', 'tfc:metal/chain/copper[axis=y]').link('#tfc:lamps'),
            crafting('tfc:crafting/metal/lamp/bronze', text_contents='Lamps are smithed in the $(l:mechanics/anvils)Anvil$() and crafted with $(l:mechanics/glassworking_applications#lamp_glass)Lamp Glass$() to be completed.'),
            quern_recipe('tfc:quern/olive_paste', 'One lamp fuel is $(thing)Olive Oil$(). The first step in its production is to make olive paste.').anchor('olives'),
            crafting('tfc:crafting/jute_net', text_contents='You will also need a jute net.'),
            text('Boil the $(thing)Olive Paste$() with $(thing)Water$() in a $(l:mechanics/pot)Pot$() to make $(thing)Olive Oil Water$(). Seal that in a $(l:mechanics/barrels)Barrel$() with your $(thing)Jute Net$() to produce $(thing)Olive Oil$(). Olive oil burns for 8 in-game hours for every unit of fluid.'),
            text('Another lamp fuel is $(thing)Tallow$(). To make it, cook 5 $(thing)Blubber$(), in a $(l:mechanics/pot)Pot$() of water. Tallow burns for less than 2 in-game hours per unit. It can be used to make $(l:mechanics/lighting#candles)Candles$()').anchor('tallow'),
            block_spotlight('Lava Lamps', text_content='Lava will keep burning forever, but can only be held in a $(l:mechanics/steel#blue_steel)Blue Steel$() lamp.', block='tfc:metal/lamp/blue_steel[lit=true]'),
            anvil_recipe('tfc:anvil/metal/chain/black_steel', '$(thing)Chains$() are a great way to hang your lamps, and can be smithed in an $(l:mechanics/anvils)Anvil$().'),
            empty_last_page(),
        )),
        entry('minecarts', 'Minecarts', 'tfc:wood/chest_minecart/kapok', pages=(
            text('$(thing)Minecarts$() are a means of transporting players, entities, and large blocks. Blocks that would normally $(l:getting_started/size_and_weight#overburdening)overburden$() the player can be transported in minecarts. $(l:mechanics/decay#large_vessels)Large Vessels$(), $(l:mechanics/barrels)Barrels$(), $(l:mechanics/powderkegs)Powderkegs$(), $(l:mechanics/anvils)Anvils$(), and $(l:mechanics/crucible)Crucibles$() can be added to carts by pressing both $(item)$(k:key.use)$() and $(item)$(k:key.sneak)$(). Using $(item)$(k:key.sneak)$() with an empty hand removes the block. Crafting recipes for minecart items can either be done with $(thing)Iron$() or $(thing)Steel$(), with the steel-based recipes being more productive.'),
            crafting('minecraft:activator_rail', 'tfc:crafting/activator_rail'),
            crafting('minecraft:detector_rail', 'tfc:crafting/detector_rail'),
            crafting('minecraft:minecart', 'tfc:crafting/minecart'),
            crafting('minecraft:rail', 'tfc:crafting/rail'),
            crafting('minecraft:powered_rail'),
            crafting('tfc:crafting/wood/chest_minecart/kapok', text_contents='$(thing)Chest minecarts$() operate the same as regular TFC chests, in that they only have 18 slots and cannot hold very large items like logs.'),
            empty_last_page(),
        )),
        entry('barrels', 'Barrels', 'tfc:wood/barrel/palm', pages=(
            text('The $(thing)Barrel$() is a device that can hold both items and fluids. The central slot is used to hold items. Fluids are shown in the tank on the left side, and can be added to the barrel by placing a filled $(thing)bucket$() or $(thing)jug$() in the top left slot. They can be removed by placing an empty fluid container in the same slot. Using $(item)$(k:key.use)$() on the block with a bucket also works.').link('#tfc:barrels'),
            image('tfc:textures/gui/book/gui/barrel.png', text_contents='The barrel interface.', border=False),
            text('Barrels can be $(thing)Sealed$(). Sealing allows the barrel to be broken while keeping its contents stored. It also allows for the execution of some recipes. In the interface, sealing can be toggled with the grey button on the right side. Using $(item)$(k:key.use)$() on the barrel with an empty hand while holding $(item)$(k:key.sneak)$() also toggles the seal.'),
            text('$(li)Barrels can be filled by clicking an empty one on a fluid block in the world.$()$(li)Barrels will slowly fill up with water in the rain.$()$(li)Icicles melting above barrels also adds water to them.$()$(li)Sealing a barrel will eject items that are not in the center slot.$()', 'Barrel Tips'),
            text('Barrels are an important device used for mixing various fluids and items together. To use a $(thing)Barrel$() recipe, the barrel must be filled with the correct ratio of fluid and items required. Depending on the recipe, it may need to be $(thing)Sealed$() for a period of time to allow the contents to transform.', title='Crafting'),
            text('If the contents of a barrel does not match the ratio required for the recipe, any excess fluid or items will be lost. If the recipe is one which converts instantly, however, you will always need at least enough items to fully convert the fluid, according to the recipe.'),
            instant_barrel_recipe('tfc:barrel/limewater', '$(bold)Limewater$() is made by adding $(l:mechanics/flux)Flux$() to a barrel of $(thing)Water$(). A single $(l:mechanics/flux)Flux$() is required for every $(thing) 500 mB$() of $(thing)Water$(). $(thing)Limewater$() is a reagent used in $(l:mechanics/leather_making)Leather Making$(), and is used to make $(thing)Mortar$().').anchor('limewater'),
            sealed_barrel_recipe('tfc:barrel/tannin', '$(bold)Tannin$() is an acidic fluid, made by dissolving bark from certain $(thing)Logs$() in a barrel of $(thing)Water$(). $(thing)Oak$(), $(thing)Birch$(), $(thing)Chestnut$(), $(thing)Douglas Fir$(), $(thing)Hickory$(), $(thing)Maple$(), and $()Sequoia$() are all able to be used to create $(thing)Tannin$().').anchor('tannin'),
            text('A couple barrel recipes operate by mixing two fluids at a certain ratio. This is done by taking a filled bucket of one of the ingredients, and putting it in the fluid addition slot of a barrel that has the required amount of the other fluid. This is done for making $(thing)Milk Vinegar$(), where $(thing)Vinegar$() is added to $(thing)Milk$() at a 9:1 ratio. Vinegar is also added in the same ratio to $(thing)Salt Water$() to make $(thing)Brine$().'),
            text('Barrels have the ability to cool $(l:mechanics/heating)hot$() items. Put a hot item in a barrel of $(thing)Water$(), $(thing)Olive Oil$(), or $(thing)Salt Water$(), and it will quickly bring its temperature down.'),
            text('Barrels have the ability to $(l:mechanics/dye)Dye$() and $(l:mechanics/dye#lye)Bleach$() items. Most color-able things, like carpet, candles, and $(l:mechanics/advanced_building_materials#alabaster)Alabaster$() can be dyed by sealing them in a barrel of dye or bleached by sealing them in a barrel of lye. See the dye chapter for more information.'),
            text('Barrels can preserve items in $(thing)Vinegar$(). Vinegar is made by sealing $(thing)Fruit$() in a barrel of $(thing)Alcohol$(). For information on how that process works, see the relevant $(l:mechanics/decay#vinegar)page$().'),
            crafting('tfc:crafting/barrel_rack', text_contents='Barrels placed against the side of a block work the same as vertical barrels, but have restrictions on placement. For example, they must have a $(thing)Barrel Rack$() added to them with $(item)$(k:key.use)$() in order to have another sideways barrel above them.'),
            two_tall_block_spotlight('Sideways Barrels', 'Sideways barrels show their sealed state using the $(thing)Tap$(). If the tap is sideways, it is sealed.', 'tfc:wood/barrel/kapok[facing=north,rack=true,sealed=false]', 'tfc:wood/barrel/kapok[facing=north,rack=false,sealed=true]'),
            text('Unsealed sideways barrels automatically drain their fluid into open barrels (and other fluid containers) placed below the tap. The block the tap extends into must be a free air block in order for fluid to be able to drain.'),
            image('tfc:textures/gui/book/tutorial/barrel_drip.png', text_contents='A barrel dripping.'),
        )),
        entry('dye', 'Dye', 'minecraft:red_dye', pages=(
            text('All 16 vanilla $(thing)Dye Colors$() can be obtained as items. Most flowers and other colorful plants can be ground into dye using a $(l:mechanics/quern)Quern$(). Powders of $(l:the_world/ores_and_minerals)metal ores$() can be crafted directly into dye. $(l:the_world/ores_and_minerals#graphite)Graphite$(), $(l:the_world/ores_and_minerals#kaolinite)Kaolinite$(), $(l:the_world/ores_and_minerals#sylvite)Sylvite$(), $(l:the_world/ores_and_minerals#lapis_lazuli)Lapis$(), Coke, and Charcoal powder can be crafted into dye as well.'),
            item_spotlight('tfc:bucket/red_dye', 'Dye Liquids', text_contents='Dyes can also be made into fluids. A dye item and 1000mB of $(item)Water$() boiled in a $(l:mechanics/pot)Pot$() produces the same amount of $(thing)Dye Fluid$(). Dye fluids are used in $(l:mechanics/barrels)Barrels$() to color items.'),
            sealed_barrel_recipe('tfc:barrel/red_concrete_powder', 'Dyeing items in a barrel is cheaper than using items, requiring only 25mB of dyed fluid.'),
            sealed_barrel_recipe('tfc:barrel/music_disc_chirp', 'Dye fluids can be used to finish $(item)Music Discs$().'),
            sealed_barrel_recipe('tfc:barrel/red_leather', 'Dye fluids can even dye leather as if it were crafted!'),
            sealed_barrel_recipe('tfc:barrel/bleaching_bed', 'Five $(item)Wood Ash$() and 1000mB of $(item)Water$() boiled in a $(l:mechanics/pot)Pot$() produces lye. 25mB of Lye removes the color from a colored item.').anchor('lye'),
            text('$(li)$(item)Black Dye$() is used for $(l:mechanics/scribing_table)Scribing Tables$().$(li)$(item)Red Dye$() is used for $(l:mechanics/powderkegs)Powderkegs$(). $(li)Unfired $(l:mechanics/decay#small_vessels)Small Vessels$() and $(l:mechanics/decay#large_vessels)Large Vessels$() can be dyed$().', title='Miscellaneous'),
            empty_last_page()
        )),
        entry('decay', 'Preservation', 'minecraft:rotten_flesh', pages=(
            text('In TerraFirmaCraft, no food will last forever! Food will $(thing)expire$() over time, turning rotten. Rotten food will not restore any hunger, and has the potential to give you unhelpful effects such as $(thing)Hunger$() or $(thing)Poison$()!$(br2)Fortunately, there are a number of ways to make your food last longer by $(thing)Preserving$() it.'),
            text('When you hover over a piece of food, you will see a tooltip which shows how long the food has until it will rot. It might look something like:$(br2)$(bold)$(2)Expires on: 5:30 July 5, 1000 (in 5 day(s))$()$(br2)By using various preservation mechanics, that date can be extended, giving you more time before your food roots.'),
            text('One of the easiest ways to preserve food is to use a $(thing)Vessel$(). $(thing)Large Vessels$() are a block which can store up to nine items, and when $(thing)sealed$() the items inside will gain the $(5)$(bold)Preserved$() status, which extends their remaining lifetime by 2x.$(br2)$(thing)Small Vessels$() are a item which can store up to four other items, and will also apply the $(5)$(bold)Preserved$() status to their contents.', title='Vessels').anchor('small_vessels'),
            block_spotlight('', 'A Sealed Large Vessel.', 'tfc:ceramic/large_vessel[sealed=true]').anchor('large_vessels'),
            text('One other way to easily preserve certain types of food is to cook them. $(thing)Meats$() will all expire slower when they are cooked than when they are raw.$(br2)It is also important to use the correct device for cooking. Certain devices that heat very hot, such as a $(l:mechanics/charcoal_forge)Charcoal Forge$() or a $(l:mechanics/crucible)Crucible$() are $(bold)bad$() for cooking food, which will make them expire faster!', title='Cooking'),
            heat_recipe('tfc:heating/food/cooked_mutton', 'Instead, a $(l:getting_started/firepit)Firepit$() or a $(l:mechanics/grill)Grill$() can even provide a buff for using it! For example, cooking mutton (pictured above) in a $(thing)Firepit$() will increase its lifetime by 1.33x, and cooking in a $(thing)Grill$() will increase its lifetime by 1.66x!'),
            text('$(thing)Salting$() is a way to make meat last longer. To salt meat, it must be crafted with $(thing)Salt$() in a crafting grid. Only raw meat can be salted. Afterwards, cooking or otherwise preserving the meat does not take away the salted property.', title='Salting').anchor('salting'),
            quern_recipe('tfc:quern/powder/salt', 'One way of getting salt is through $(l:mechanics/quern)grinding$() $(l:the_world/ores_and_minerals#halite)Halite$(), which is a mineral.'),
            block_spotlight('Salt Licks', 'Salt can be found naturally in forests. It can be placed and picked back up.', 'tfc:groundcover/salt_lick'),
            text('$(thing)Vinegar Preservation$() is a way of making fruits, veggies, and meat last longer.$(br2)$(thing)Vinegar$() is made in a $(l:mechanics/barrels)Barrel$(), by sealing a fruit with 250mB of Alcohol. To preserve food in vinegar it must first be $(thing)Pickled$() in $(thing)Brine$(). Brine is made in a barrel with 1 part $(thing)Vinegar$() and 9 parts $(thing)Salt Water$().').anchor('vinegar'),
            text('Once food is pickled, it can be sealed in a Barrel of Vinegar. If there is 125mB of Vinegar per pickled food item, the food will last longer.'),
            text('Food should be stored in either sealed large vessels or in $(l:getting_started/pottery#vessel)Small Vessels$(). Food that is left sitting in chests or other devices can attract $(thing)Rats$(). Rats are able to open containers and eat the food out of them, but will despawn if they can\'t find something for a few minutes. You will be alerted to a rat\'s presence by the message $(thing)\'This container has a foul smell\'$(). It is important to note that unsealing and resealing a large vessel before you leave the screen will never attract pests.').anchor('pests'),
        )),
        entry('hydration', 'Keeping Hydrated', 'tfc:textures/gui/book/icons/hydrated.png', pages=(
            text('One challenge when farming is keeping your crops hydrated. Based on the $(l:the_world/climate#rainfall)Rainfall$() in the area, the ground may have some latent moisture. However, this may not be enough especially for particularly water-intensive crops.$(br2)In order to see the hydration of any specific block, you must have a $(thing)Hoe$() in hand.'),
            text('Then simply look at any $(thing)Farmland$() block, or any crop which requires hydration. You will see a tooltip which shows the current hydration as a percentage from 0% to 100%.$(br2)Hydration cannot be decreased except by moving to an area with less rainfall - however, it can be increased by the proximity to nearby $(thing)water$() blocks, much like Vanilla farmland.'),
        )),
        entry('crops', 'Crops', 'tfc:food/wheat', pages=(
            text('Crops are a source of food and some other materials. While each crop is slightly different, crops all have some similar principles. In order to start growing crops, you will need some $(thing)Seeds$(), which can be obtained by searching for $(l:the_world/wild_crops)Wild Crops$() and breaking them.$(br2)Once you have obtained seeds, you will also need a $(thing)Hoe$(). Seeds are also useful as $(l:mechanics/fishing)bait$().'),
            text('In addition to finding wild crops, seeds can also be produced from existing crops. When a fully mature crop dies - either by weather, or leaving it to rot - it will go to seed, dropping more seeds that can be used to grow even more crops next season.'),
            rock_knapping_typical('hoe_head', 'To get started, a $(thing)Stone Hoe$() can be $(thing)knapped$() as seen above.'),
            crafting('tfc:crafting/stone/hoe/sedimentary', text_contents='Once the hoe head is knapped, it can be crafted into a Hoe.$(br2)Hoes function as in Vanilla, by right clicking dirt blocks to turn them into $(thing)Farmland$(). They can also be used to convert $(thing)Rooted Dirt$() into $(thing)Dirt$().'),
            text('All crops need to be planted on farmland in order to grow. Some crops have additional requirements such as being waterlogged or requiring a stick to grow on.$(br2)Crops do not need $(thing)nutrients$() to grow, but they certainly help. There are three nutrients: $(b)Nitrogen$(), $(6)Phosphorous$(), and $(d)Potassium$(). Each crop has a favorite nutrient.', title='Growing Crops'),
            text('Consuming its favorite nutrient causes a crop to grow faster, and improves the yield of the crop at harvest time. That means that crops that consumed more nutrients drop more food when broken! Consuming a nutrient also has the effect of replenishing the other nutrients around it a small amount.$(br2)The next several pages list all the crops present in TFC'),
            # Listing of all crops, their growth conditions, and how to grow them
            text(f'{detail_crop("barley")}Barley is a single block crop. Barley seeds can be planted on farmland and will produce $(thing)Barley$() and $(thing)Barley Seeds$() as a product.', title='Barley').link('tfc:seeds/barley').link('tfc:food/barley').anchor('barley'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/barley[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("oat")}Oat is a single block crop. Oat seeds can be planted on farmland and will produce $(thing)Oat$() and $(thing)Oat Seeds$() as a product.', title='Oat').link('tfc:seeds/oat').link('tfc:food/oat').anchor('oat'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/oat[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("rye")}Rye is a single block crop. Rye seeds can be planted on farmland and will produce $(thing)Rye$() and $(thing)Rye Seeds$() as a product.', title='Rye').link('tfc:seeds/rye').link('tfc:food/rye').anchor('rye'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/rye[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("maize")}Maize is a two block tall crop. Maize seeds can be planted on farmland, will grow two blocks tall, and will produce $(thing)Maize$() and $(thing)Maize Seeds$() as a product.', title='Maize').link('tfc:seeds/maize').link('tfc:food/maize').anchor('maize'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/maize[age=%d,part=top]' % i if i >= 3 else 'minecraft:air',
                'Y': 'tfc:crop/maize[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(6)]),
            text(f'{detail_crop("wheat")}Wheat is a single block crop. Wheat seeds can be planted on farmland and will produce $(thing)Wheat$() and $(thing)Wheat Seeds$() as a product.', title='Wheat').link('tfc:seeds/wheat').link('tfc:food/wheat').anchor('wheat'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/wheat[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("rice")}Rice is a single block crop. Rice must be grown underwater - it must be planted on farmland, in freshwater that is a single block deep. It will produce $(thing)Rice$() and $(thing)Rice Seeds$() as a product.', title='Rice').link('tfc:seeds/rice').link('tfc:food/rice').anchor('rice'),
            multimultiblock(
                'Note: in order to grow, the rice block must be $(thing)Waterlogged$().',
                *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/rice[age=%d,fluid=water]' % i) for i in range(8)],
            ),
            text(f'{detail_crop("beet")}Beets are a a single block crop. Beet seeds can be planted on farmland and will produce $(thing)Beet$() and $(thing)Beet Seeds$() as a product.', title='Beet').link('tfc:seeds/beet').link('tfc:food/beet').anchor('beet'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/beet[age=%d]' % i) for i in range(6)]),
            text(f'{detail_crop("cabbage")}Cabbage is a single block crop. Cabbage seeds can be planted on farmland and will produce $(thing)Cabbage$() and $(thing)Cabbage Seeds$() as a product.', title='Cabbage').link('tfc:seeds/cabbage').link('tfc:food/cabbage').anchor('cabbage'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/cabbage[age=%d]' % i) for i in range(6)]),
            text(f'{detail_crop("carrot")}Carrot is a single block crop. Carrot seeds can be planted on farmland and will produce $(thing)Carrot$() and $(thing)Carrot Seeds$() as a product.', title='Carrot').link('tfc:seeds/carrot').link('tfc:food/carrot').anchor('carrot'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/carrot[age=%d]' % i) for i in range(5)]),
            text(f'{detail_crop("garlic")}Garlic is a single block crop. Garlic seeds can be planted on farmland and will produce $(thing)Garlic$() and $(thing)Garlic Seeds$() as a product.', title='Garlic').link('tfc:seeds/garlic').link('tfc:food/garlic').anchor('garlic'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/garlic[age=%d]' % i) for i in range(5)]),
            text(f'{detail_crop("green_bean")}Green Beans is a climbing two block tall crop. Green Bean seeds can be planted on farmland, will grow two blocks tall if a stick is present, and will produce $(thing)Green Beans$() and $(thing)Green Bean Seeds$() as a product.', title='Green Beans').link('tfc:seeds/green_bean').link('tfc:food/green_bean').anchor('green_bean'),
            multimultiblock('The stick is required in order for the crop to fully grow.', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/green_bean[age=%d,part=top,stick=true]' % i,
                'Y': 'tfc:crop/green_bean[age=%d,part=bottom,stick=true]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("potato")}Potatoes are a single block crop. Potato seeds can be planted on farmland and will produce $(thing)Potatoes$() and $(thing)Potato Seeds$() as a product.', title='Potatoes').link('tfc:seeds/potato').link('tfc:food/potato').anchor('potato'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/potato[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("pumpkin")}Pumpkins are a spreading crop. Pumpkin seeds can be planted on farmland and will place up to two $(thing)Pumpkin Blocks$() on the ground next to it while it is mature. If the pumpkin blocks are harvested, and the plant matures again, it can grow more pumpkins. Pumpkins can be made into $(l:mechanics/lighting#jack_o_lanterns)Jack \'o Lanterns$().', title='Pumpkins').link('tfc:seeds/pumpkin').anchor('pumpkin'),
            multimultiblock('', *[multiblock('', '', False, pattern=(('   ', ' CP', '   '), ('GGG', 'G0G', 'GGG')), mapping={'G': 'tfc:farmland/loam', '0': 'tfc:farmland/loam', 'C': 'tfc:crop/pumpkin[age=%d]' % i, 'P': 'minecraft:air' if i != 7 else 'tfc:pumpkin'}) for i in range(8)]),
            text(f'{detail_crop("melon")}Melons are a spreading crop. Melon seeds can be planted on farmland and will place up to two $(thing)Melon Blocks$() on the ground next to it while it is mature. If the melon blocks are harvested, and the plant matures again, it can grow more melon.', title='Melons').link('tfc:seeds/melon').anchor('melon'),
            multimultiblock('', *[multiblock('', '', False, pattern=(('   ', ' CP', '   '), ('GGG', 'G0G', 'GGG')), mapping={'G': 'tfc:farmland/loam', '0': 'tfc:farmland/loam', 'C': 'tfc:crop/melon[age=%d]' % i, 'P': 'minecraft:air' if i != 7 else 'tfc:melon'}) for i in range(8)]),
            text(f'{detail_crop("red_bell_pepper")}Red Bell Peppers are a pickable crop. When they are near mature, they can be harvested with $(item)$(k:key.use)$() to obtain Green Bell Peppers. Harvesting with $(item)$(k:key.use)$() allows the plant to grow more peppers in the future rather than destroying the plant.', title='Red Bell Peppers').link('tfc:seeds/red_bell_pepper').anchor('red_bell_pepper'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/red_bell_pepper[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("yellow_bell_pepper")}Yellow Bell Peppers are a pickable crop. When they are near mature, they can be harvested with $(item)$(k:key.use)$() to obtain Green Bell Peppers. Harvesting with $(item)$(k:key.use)$() allows the plant to grow more peppers in the future rather than destroying the plant.', title='Yellow Bell Peppers').link('tfc:seeds/yellow_bell_pepper').anchor('yellow_bell_pepper'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/yellow_bell_pepper[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("onion")}Onions are a single block crop. Onion seeds can be planted on farmland and will produce $(thing)Onions$() and $(thing)Onion Seeds$() as a product.', title='Onions').link('tfc:seeds/onion').link('tfc:food/onion').anchor('onion'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/onion[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("soybean")}Soybean is a single block crop. Soybean seeds can be planted on farmland and will produce $(thing)Soybean$() and $(thing)Soybean Seeds$() as a product.', title='Soybean').link('tfc:seeds/soybean').link('tfc:food/soybean').anchor('soybean'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/soybean[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("squash")}Squash is a single block crop. Squash seeds can be planted on farmland and will produce $(thing)Squash$() and $(thing)Squash Seeds$() as a product.', title='Squash').link('tfc:seeds/squash').link('tfc:food/squash').anchor('squash'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/squash[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("sugarcane")}Sugarcane is a two block tall crop. Sugarcane seeds can be planted on farmland, will grow two blocks tall, and will produce $(thing)Sugarcane$() and $(thing)Sugarcane Seeds$() as a product. Sugarcane can be used to make $(thing)Sugar$().', title='Sugarcane').link('tfc:seeds/sugarcane').link('tfc:food/sugarcane').anchor('sugarcane'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/sugarcane[age=%d,part=top]' % i if i >= 4 else 'minecraft:air',
                'Y': 'tfc:crop/sugarcane[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("tomato")}Tomatoes are a climbing two block tall crop. Tomato seeds can be planted on farmland, will grow two blocks tall if a stick is present, and will produce $(thing)Tomatoes$() and $(thing)Tomato Seeds$() as a product.', title='Tomatoes').link('tfc:seeds/tomato').link('tfc:food/tomato').anchor('tomatoes'),
            multimultiblock('The stick is required in order for the crop to fully grow.', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/tomato[age=%d,part=top,stick=true]' % i,
                'Y': 'tfc:crop/tomato[age=%d,part=bottom,stick=true]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("jute")}Jute is a two block tall crop. Jute seeds can be planted on farmland, will grow two blocks tall, and will produce $(thing)Jute$() and $(thing)Jute Seeds$() as a product.', title='Jute').link('tfc:seeds/jute').link('tfc:jute').anchor('jute'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/jute[age=%d,part=top]' % i if i >= 3 else 'minecraft:air',
                'Y': 'tfc:crop/jute[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(6)]),
            text(f'{detail_crop("papyrus")}Papyrus is a two block tall crop. Papyrus seeds can be planted on farmland, will grow two blocks tall, and will produce $(l:mechanics/papermaking)Papyrus$() and $(thing)Papyrus Seeds$() as a product.', title='Papyrus').link('tfc:seeds/papyrus').link('tfc:papyrus').anchor('papyrus'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/papyrus[age=%d,part=top]' % i if i >= 3 else 'minecraft:air',
                'Y': 'tfc:crop/papyrus[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(6)]),
        )),
        entry('mechanical_power', 'Mechanical Power', 'tfc:wood/water_wheel/oak', pages=(
            text('Mechanical power is the art of making things rotate or move, by harnessing the elemental power of either wind or water.$(br2)Practically, many devices can be hooked up to mechanical power networks either to automate their movement, or provide power for other functionality'),
            text('In order to get started with harnessing mechanical power, you will first need a $(thing)Source$() of power.$(br2)$(l:mechanics/mechanical_power#windmill)Windmills$() are a way of harnessing the wind. They can be built nearly everywhere with enough space.$(br2)$(l:mechanics/mechanical_power#water_wheel)Water Wheels$() are a slightly stronger power source, as they harness the current found in $(thing)Rivers$()'),
            page_break(),
            text('$(thing)Windmills$() are a way of harnessing the power of the wind to rotate an $(l:mechanics/mechanical_power#axle)Axle$(). They are large, and require a completely unobstructed area of 13 x 13 x 1 to be placed. In order to create one, you will first need an $(thing)Axle$(), and then you will need one or more $(thing)Windmill Blades$()', title='Windmills').anchor('windmill'),
            crafting('tfc:crafting/windmill_blade/white', text_contents='$(thing)Windmill Blades$() can be crafted from cloth. A single windmill can have up to five blades, and the more blades, the faster it will rotate.'),
            text('In order to create a windmill, first place an $(l:mechanics/mechanical_power#axle)Axle$() in any horizontal orientation. Then, $(item)$(k:key.use)$() up to five $(thing)Windmill Blades$() on the axle to create the windmill. It will slowly start spinning.$(br2)The windmill may break if the axle is already connected to another source, or if there is not enough clear space.'),
            multiblock('', '', multiblock_id='tfc:windmill'),
            page_break(),
            text('$(thing)Water Wheels$() are a way of harnessing the power of flowing water in $(3)Rivers$() in order to generate power. When placed optimally, they can rotate at some of the fastest possible speeds available.$(br2)A water wheel requires a 5 x 5 x 1 area of space to be placed. Note that the water wheel may break if there are any obstructions in the area around it,', title='Water Wheels').anchor('water_wheel').link('#tfc:water_wheels'),
            crafting('tfc:crafting/wood/water_wheel/oak', text_contents='or if the axle was already connected to another source.$(br2)Water wheels can be crafted simply from lumber.', title=' '),
            text('In order to place a $(thing)Water Wheel$(), simply attach it on the end of any $(thing)Axle$(). The axle should be one or two blocks above a source of flowing water, i.e. a river.$(br2)Water below the height of the axle, when flowing, will cause the wheel to speed up. Any water elsewhere, or stationary water below the axle will impede the wheel\'s movement and cause it to slow down or stop.'),
            multiblock('', 'A $(thing)Water Wheel$()', multiblock_id='tfc:water_wheel'),
            crafting('tfc:crafting/wood/axle/oak', text_contents='Axles are the bread and butter of a rotational network, and are the block that transfers rotational power from one location to the other. They can be connected up to five blocks long, but any longer and they will break!', title='Axles').anchor('axle').link('#tfc:axles'),
            crafting('tfc:crafting/wood/encased_axle/oak', text_contents='Axles can also be crafted $(thing)Encased$(). These can either be placed by themselves, or on top of existing axles.$(br2)However, in order to transfer power further, you will need a $(l:mechanics/mechanical_power#gearbox)Gear Box$()', title='Encased Axles'),
            crafting('tfc:crafting/wood/gear_box/oak', text_contents='$(thing)Gear Boxes$() are a way of changing the $(thing)Direction$() of rotational power by using $(l:https://en.wikipedia.org/wiki/Gear)Gears$(). They are also useful in order to connect long chains of $(l:mechanics/mechanical_power#axle)Axles$() together without breaking.', title='Gear Box').anchor('gearbox').link('#tfc:gear_boxes'),
            text('In order to use a gearbox, its sides must be configured. $(item)$(k:key.use)$() while holding a $(thing)Hammer$() on any face of the gearbox to enable input/output through that face.$(br2)Note that due to their internal structure, gear boxes can only have faces enabled on two different axes of rotation at the same time.'),
            crafting('tfc:crafting/wood/clutch/oak', text_contents='A $(thing)Clutch$() is a way of controlling a rotational network via $(c)Redstone$(). It is an $(l:mechanics/mechanical_power#axle)Encased Axle$(), which disconnects when powered, allowing rotation to pass through only when unpowered.', title='Clutch').link('#tfc:clutches'),
            multimultiblock('An example of a $(thing)Clutch$() in use.', *[
                multiblock('', '', multiblock_id='tfc:clutch_off'),
                multiblock('', '', multiblock_id='tfc:clutch_on')
            ]),
            text('One device that can be connected to the mechanical network is a $(l:mechanics/quern)Quern$(), simply by placing a vertical-facing axle above the Quern. While connected, the Quern cannot be turned manually, instead it will grind items automatically as the axle above rotates.$(br2)The Quern will grind faster if the axle above spins faster.', title='Quern Automation').anchor('quern'),
            multiblock('', 'A $(thing)Quern$() connected to an $(l:mechanics/mechanical_power#axle)Axle$().', multiblock_id='tfc:rotating_quern'),
            text('Another mechanical device is the $(thing)Trip Hammer$(). The Trip Hammer automatically performs smithing on an $(l:mechanics/anvils)Anvil$(). The Trip Hammer needs a $(thing)Bladed Axle$() to work. The Bladed Axle works like a regular axle, but has a blade on it that is used to activate the hammer.', title='Trip Hammer').anchor('trip_hammer'),
            crafting('tfc:crafting/wood/bladed_axle/oak', 'tfc:crafting/trip_hammer'),
            multiblock('', 'A setup for a trip hammer.', pattern=(('X ',), ('YZ',), ('0 ',)), mapping={
                'X': 'tfc:wood/bladed_axle/oak[axis=x]',
                'Y': 'tfc:trip_hammer[facing=north]',
                'Z': 'tfc:metal/anvil/copper[facing=west]'
            }),
            text('Place a trip hammer below the axle, and $(item)$(k:key.use)$() to add a hammer to it. The hammer must be a metal hammer. Make sure the trip hammer is oriented such that the bladed axle will push the hammer handle down. The hammer will then hit an anvil placed in front of it. The trip hammer always records the $(thing)\'Light Hit\'$() action, and always moves the cursor closer towards the target, requiring no input from the player. If an ingot is not hot enough or the anvil is the incorrect tier, a deep metal banging sound will alert you. '),
        )),
        entry('crankshaft', 'Crankshafts', 'tfc:crankshaft', pages=(
            text('A $(thing)Crankshaft$() is a way of turning $(l:mechanics/mechanical_power)Rotational Power$() into $(thing)Moving-back-and-forth Power$(). This can be useful in order to power devices such as the $(l:mechanics/bellows)Bellows$(), or $(l:mechanics/pumps)Water Pumps$().$(br2)The $(thing)Crankshaft$() consists of two parts: the base, and the shaft. The base must be connected to an $(l:mechanics/mechanical_power#axle)Axle$(), and devices can be connected to the end of the shaft.'),
            multiblock('', 'A $(thing)Crankshaft$() being powered by an $(l:mechanics/mechanical_power#axle)Axle$()', multiblock_id='tfc:crankshaft'),
            crafting('tfc:crafting/crankshaft', text_contents='The base can be crafted from $(l:getting_started/primitive_alloys#brass)Brass$(). Once a base is placed, a shaft can be added to it by using a $(thing)Steel Rod$() on the base. If there is an empty space to the side of the crankshaft, the rod will be placed.'),
            text('The crankshaft can be connected to a $(l:mechanics/bellows)Bellows$() by placing one adjacent to the shaft. This will then push automatically every time the shaft extends then contracts. When connected to a crankshaft it cannot be operated manually.$(br2)The crankshaft can also be connected to a $(l:mechanics/pumps)Pump$(), which can be used to transport fluids.'),
        )),
        entry('pumps', 'Pumps', 'tfc:steel_pipe', pages=(
            text('$(thing)Steel Pumps$() and $(thing)Steel Pipes$() are blocks useful for transporting fluids, such as $(thing)Water$(), $(thing)Salt Water$(), and $(thing)Spring Water$(). They can also be used to move source blocks, and fill small enclosed areas. Unlike $(l:mechanics/aqueducts)Aqueducts$(), they can also be used to transport source blocks upwards.').link('tfc:steel_pipe', 'tfc:steel_pump'),
            anvil_recipe('tfc:anvil/steel_pipe', 'The steel pipe can be crafted from, predictably, $(l:mechanics/steel)Steel$(), on an $(l:mechanics/anvils)Anvil$(). A $(thing)Steel Pump$() can be crafted from some $(thing)Steel Pipes$(), and $(l:getting_started/primitive_alloys#brass)Brass Mechanisms$().'),
            crafting('tfc:crafting/steel_pump', text_contents='In order to operate the pump, you will also need a $(l:mechanics/crankshaft)Crankshaft$(), and a source of $(l:mechanics/mechanical_power)Mechanical Power$():$(br)$(li)The pump must be connected on the $(bold)bottom$() to a pipe.'),
            text('$(li)On the narrow side, the pump must be adjacent to the business end of a $(l:mechanics/crankshaft)Crankshaft$().$(li)The pump can reach up to $(bold)16$() blocks. The end of the pipe must be $(bold)submerged$() in the target fluid, and adjacent to a source fluid block.$(li)If the crankshaft is active, fluid will be brought up and appear on the wide end of the $(thing)Pump$().$(li)The pump can fill an enclosed area up to $(bold)32$() blocks with source blocks.'),
        )),
    ))

    book.build()


def make_crop_table(start_index: int, end_index: int) -> List[str | Dict[str, Any]]:
    crop_table = [
        {'text': contents, 'bold': True}
        for contents in ('Crop', 'Temperature (°C)', 'Rainfall (mm)')
    ]
    for idx, (crop, data) in enumerate(CROPS.items()):
        if start_index <= idx <= end_index:
            crop_table += [
                {'text': lang(crop)},
                '%3s - %s' % (data.min_temp, data.max_temp),
                '%3s - %s' % (data.min_water, data.max_water)
            ]
    return crop_table


def detail_crop(crop: str) -> str:
    data = CROPS[crop]
    return '$(bold)$(l:the_world/climate#temperature)Temperature$(): %d - %d °C$(br)$(bold)$(l:mechanics/hydration)Hydration$(): %d - %d %%$(br)$(bold)Nutrient$(): %s$(br2)' % (data.min_temp, data.max_temp, data.min_hydration, data.max_hydration, data.nutrient.title())


def fruit_tree_text(fruit: str, title: str, text_contents: str) -> Page:
    data = FRUITS[fruit]
    return text(defer('$(bold)$(l:the_world/climate#temperature)Temperature$(): {0} - {1} °C$(br)$(bold)$(l:mechanics/hydration)Rainfall$(): {2} - {3}mm' + text_contents, data.min_temp, data.max_temp, data.min_water, data.max_water), title=title).anchor(fruit).link('tfc:food/%s' % fruit, 'tfc:plant/%s_sapling' % fruit)


def fruit_tree_multiblock(fruit: str, text_contents: str) -> Page:
    return multimultiblock(text_contents, *[multiblock('', '', False, pattern=(
        ('     ', '  L  ', '     ', '     ', '     '),
        ('  L  ', ' L1L ', '  L  ', '     ', '     '),
        ('  L  ', ' L1L ', '  L  ', '     ', '     '),
        ('  L  ', ' L3L ', ' L2L ', '  L  ', '     '),
        ('     ', '     ', '  0  ', '     ', '     '),
        ('     ', '     ', '  1  ', '     ', '     '),
    ), mapping={
        '0': 'tfc:plant/%s_branch[down=true,up=true]' % fruit,
        '1': 'tfc:plant/%s_branch[down=true,up=true]' % fruit,
        '2': 'tfc:plant/%s_branch[down=true,west=true]' % fruit,
        '3': 'tfc:plant/%s_branch[east=true,up=true]' % fruit,
        'L': 'tfc:plant/%s_leaves[lifecycle=%s]' % (fruit, stage),
    }) for stage in ('dormant', 'healthy', 'flowering', 'fruiting')])


def tall_bush_text(fruit: str, title: str, text_contents: str) -> Page:
    data = BERRIES[fruit]
    return text(defer('$(bold)$(l:the_world/climate#temperature)Temperature$(): {0} - {1} °C$(br)$(bold)$(l:mechanics/hydration)Rainfall$(): {2} - {3}mm' + text_contents, data.min_temp, data.max_temp, hydration_from_rainfall(data.min_water), 100), title=title).anchor(fruit).link('tfc:food/%s' % fruit, 'tfc:plant/%s_bush' % fruit)


def tall_bush_multiblock(fruit: str, text_contents: str) -> Page:
    return multimultiblock(text_contents, *[multiblock('', '', False, pattern=(('X ',), ('YZ',), ('AZ',), ('0 ',),), mapping={
        'X': 'tfc:plant/%s_bush[stage=0,lifecycle=%s]' % (fruit, life),
        'Y': 'tfc:plant/%s_bush[stage=1,lifecycle=%s]' % (fruit, life),
        'Z': 'tfc:plant/%s_bush_cane[stage=0,lifecycle=%s,facing=south]' % (fruit, life),
        'A': 'tfc:plant/%s_bush[stage=2,lifecycle=%s]' % (fruit, life)
    }) for life in ('dormant', 'healthy', 'flowering', 'fruiting')])


def small_bush_text(fruit: str, title: str, text_contents: str) -> Page:
    data = BERRIES[fruit]
    return text(defer('$(bold)$(l:the_world/climate#temperature)Temperature$(): {0} - {1} °C$(br)$(bold)$(l:mechanics/hydration)Hydration$(): {2} - {3} %' + text_contents, data.min_temp, data.max_temp, hydration_from_rainfall(data.min_water), 100), title=title).anchor(fruit).link('tfc:food/%s' % fruit, 'tfc:plant/%s_bush' % fruit)


def small_bush_multiblock(fruit: str, text_contents: str) -> Page:
    water = ',fluid=water' if fruit == 'cranberry' else ''
    return multimultiblock(text_contents, *[block_spotlight('', '', 'tfc:plant/%s_bush[lifecycle=%s,stage=2%s]' % (fruit, life, water)) for life in ('dormant', 'healthy', 'flowering', 'fruiting')])


def hydration_from_rainfall(rainfall: float) -> int:
    return int(rainfall) * 60 // 500


def link_rock_categories(item_name: str) -> List[str]:
    return [item_name % c for c in ROCK_CATEGORIES]


if __name__ == '__main__':
    main_with_args()

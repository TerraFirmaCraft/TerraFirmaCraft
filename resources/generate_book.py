"""

=== Translation / Localization Tools ===

Translating the book is difficult and annoying. This allows you to translate one file, keep it up to date when the book updates, and output the entire book without changing any of the content.

In order to use, all you need to do is run

> python generate_book.py --translate <lang>

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

All the documentation on entry(), category(), all the book page functions, are copied from the Patchouli documentation, found here:
https://vazkiimods.github.io/Patchouli/docs/reference/overview

=== Dev Environment Setup ===

This enables hot reloading of book content and assets.

1. Run generate_book.py with the environment variable LOCAL_MINECRAFT_INSTANCE set to the .minecraft/ directory of a local minecraft instance. It should say "copying into local instance at <path>"
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
- Images of guis are taken in screenshots, then JUST THE GUI (so erase all those little pixels in the corner) is copied out. A 256 x 256 image is used, and the gui is placed horizontally centered on the FIRST 200 PIXELS (so a 176 pixel wide gui image is placed with 12 blank pixels to it's left). Make the inventory clean, but also believable (i.e. if you were just talking about items X, Y, Z, have those items in your inventory. Don't make the inventory a focal point of the image.
- For multiple images in the same location, i.e. to show a sort of 'animated' style, use /tp @p x y z pitch yaw to get an exact positioning before taking screenshots.

"""

import os

from argparse import ArgumentParser

from mcresources import ResourceManager, utils
from mcresources.type_definitions import JsonObject, ResourceIdentifier

from constants import CROPS, METALS
from patchouli import *
from i18n import I18n

GRADES = ['poor', 'normal', 'rich']  # Sorted so they appear in a nice order for animation
GRADES_ALL = ['small', 'poor', 'normal', 'rich']
TOOL_METALS = [key for key, val in METALS.items() if 'tool' in val.types]


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


def main():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us')

    args = parser.parse_args()

    rm = ResourceManager('tfc', '../src/main/resources')
    i18n = I18n.create(args.translate)

    print('Writing book')
    make_book(rm, i18n)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
        make_book(rm, I18n.create('en_us'), local_instance=True)

    print('Done')


def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False):
    book = Book(rm, 'field_guide', {}, i18n, local_instance)

    book.template('multimultiblock', custom_component(0, 0, 'MultiMultiBlockComponent', {'multiblocks': '#multiblocks'}), text_component(0, 115))

    book.template('rock_knapping_recipe', custom_component(0, 0, 'RockKnappingComponent', {'recipes': '#recipes'}), text_component(0, 99))
    book.template('clay_knapping_recipe', custom_component(0, 0, 'ClayKnappingComponent', {'recipe': '#recipe'}), text_component(0, 99))
    book.template('fire_clay_knapping_recipe', custom_component(0, 0, 'FireClayKnappingComponent', {'recipe': '#recipe'}), text_component(0, 99))
    book.template('leather_knapping_recipe', custom_component(0, 0, 'LeatherKnappingComponent', {'recipe': '#recipe'}), text_component(0, 99))

    book.template('quern_recipe', custom_component(0, 0, 'QuernComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('heat_recipe', custom_component(0, 0, 'HeatingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('anvil_recipe', custom_component(0, 0, 'AnvilComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('welding_recipe', custom_component(0, 0, 'WeldingComponent', {'recipe': '#recipe'}), text_component(0, 45))

    book.category('the_world', 'The World', 'All about the natural world around you.', 'tfc:grass/loam', is_sorted=True, entries=(
        entry('biomes', 'Biomes', 'tfc:textures/gui/book/icons/biomes.png', pages=(
            # Overview of biomes and what they are, and what they affect
            # Rough overview of how biomes spawn in terms of where to find them
            # Previews of most/all biomes in a showcase mode
            text('The world is made up of $(thing)biomes$(). Biomes determine the rough shape of the landscape, the surface material, and some other features. There are several different types of biomes, from oceans to plains to hills to mountains that can be found.'),
            text('The next few pages show a few (but not all) of the biomes that you might find in the world.'),
            page_break(),
            text('Plains are a low elevation biome, similar to hills, just above sea level. They are flat, and can contain fields of grasses and flowers, or they may be forested.', title='Plains'),
            image('tfc:textures/gui/book/biomes/plains.png', text_contents='A Plains.').anchor('plains'),
            text('Both Hills and Rolling Hills are low to mid elevation biomes often bordering plains or higher elevation regions. Large boulders can be found here, and rarely the empty remains of volcanic hot springs.', title='Hills & Rolling Hills').anchor('hills'),
            image('tfc:textures/gui/book/biomes/rolling_hills_with_river.png', text_contents='A Rolling Hills with a river winding through it.'),
            text('Badlands are a mid elevation continental biome, often found near plateaus, mountains, or rolling hills. Ridges with layers of sand and sandstone are common. The types of sand vary, and badlands can either be red/brown, or yellow/white, or somewhere inbetween.', title='Badlands').anchor('badlands'),
            image('tfc:textures/gui/book/biomes/badlands.png', text_contents='A Badlands.'),
            text('Plateaus are a high elevation continental biome. They are similar to plains but at a higher altitude - flat, grassy areas. Plateaus can have frequent boulders dotted across them, and dry or empty hot springs are occasional sightings here.', title='Plateaus').anchor('plateau'),
            image('tfc:textures/gui/book/biomes/plateau.png', text_contents='A Plateau with a deep river canyon running through it.'),
            text('In high elevation areas, multiple types of mountains, may be found. Old Mountains are shorter and smoother, while Mountains stretch tall with rocky cliff faces. Mountains formed in areas of high tectonic activity can also generate hot springs, and rare volcanoes.', title='Mountains').anchor('mountains'),
            image('tfc:textures/gui/book/biomes/old_mountains.png', text_contents='An Old Mountains with a hot spring on the snowy slopes.'),
            text('In the opposite environment to towering mountains, a Lowlands can appear as a swampy, water filled biome. At or below sea level, with plenty of fresh water, they can also contain mud and plenty of vegetation.', title='Lowlands').anchor('lowlands'),
            image('tfc:textures/gui/book/biomes/lowlands.png', text_contents='A Lowlands.'),
            text('The low canyons is another low elevation continental biome, often found bordering oceans or other low elevation biomes. It is a moderately hilly area with frequent twisting ponds. It is similar to a Geologic Shield, and empty inactive hot springs can appear here.', title='Low Canyons').anchor('low_canyons'),
            image('tfc:textures/gui/book/biomes/low_canyons.png', text_contents='A Low Canyons.'),
            text('Similar to the $(l:the_world/biomes#low_canyons)Low Canyons$(), the Canyons is a mid elevation continental biome with moderate hills and frequent twisting ponds and lakes. This is a very geologically active area, with frequent short and stubby volcanoes, boulders, and active hot springs', title='Canyons').anchor('canyons'),
            image('tfc:textures/gui/book/biomes/canyons.png', text_contents='A Canyons, with a volcano in the distance.'),
            text('The vast oceans of TerraFirmaCraft separate continents from each other. Oceans are large featureless expanses of water on the surface, but underneath various plants, sea creatures, and kelp will be found. In colder climates, oceans can be occupied by towering icebergs and floating chunks of sea ice.', title='Oceans').anchor('ocean'),
            image('tfc:textures/gui/book/biomes/ocean.png', text_contents='An ocean, pictured standing on the coast.'),
        )),
        entry('waterways', 'Where the River Flows', 'tfc:textures/gui/book/icons/rivers.png', pages=(
            # Overview of rivers, oceans, and lakes
            # Minor mention of underground rivers and lakes
            # Resources found in rivers + lakes: ore deposits and other gem ores
            text('While exploring, you might come across large bodies of water: rivers, lakes, or vast oceans. Rivers and lakes contain $(thing)freshwater$(), while oceans contain $(thing)saltwater$(). Drinking freshwater can restore your thirst, however drinking saltwater will deplete it over time.'),
            image('tfc:textures/gui/book/biomes/river.png', text_contents='A river.'),
            text('Rivers in TerraFirmaCraft have $(thing)current$(). They will push along items, players, and entities the same as flowing water. River currents will ultimately lead out to $(l:the_world/biomes#ocean)Oceans$(), joining up with other branches along the way. Occasionally, rivers will also disappear underground, and there have even been rare sightings of vast cavernous underground lakes, but will always find their way to the ocean eventually.'),
            image('tfc:textures/gui/book/biomes/underground_river.png', text_contents='A segment of an underground river.'),
            text('Lakes and rivers can also be the source of some resources. The first of which is small ore deposits. Gravel with small flecks of ores can be found in the bottom of rivers and lakes. These can be $(l:mechanics/panning)panned$() to obtain small amounts of ores. Native Copper, Native Silver, Native Gold, and Cassiterite can be found this way.', title='Ore Deposits').anchor('ore_deposits'),
            block_spotlight('Example', 'A native gold deposit in some slate.', 'tfc:deposit/native_gold/slate'),
            text('In addition to gravel ore deposits, lakes can also hide clusters of some gemstones. Amethyst and Opal ores can be found this way in surface level ore veins under lakes and rivers.', title='Gemstones').anchor('gemstones'),
            block_spotlight('Example', 'A block of amethyst ore in limestone.', 'tfc:ore/amethyst/limestone')
        )),
        entry('geology', 'Geology', 'tfc:rock/raw/shale', pages=(
            # Minor intro to plate tectonics
            # Explanation of volcanoes with pictures and how to find them, and what resources they hold in fissures
            # Hot springs, empty hot springs, and what resources they hold
            text('The world of TerraFirmaCraft is formed by the movement of $(l:https://en.wikipedia.org/wiki/Plate_tectonics)plate tectonics$(), and some of that is still visible in the ground around you. By pressing $(item)$(k:key.inventory)$(), and clicking on the $(thing)Climate$() tab, the current tectonic area will be listed under $(thing)Region$(). There are several regions, and they will influence what kinds of biomes, and also what kind of features are present in the area.'),
            text('Below is a list of the different types of regions, and their primary features$(br2)$(bold)Oceanic$()$(br)The tectonic plate covering most oceans, mostly covered with normal and deep $(l:the_world/biomes#ocean)Oceans$().$(br2)$(bold)Low Altitude Continental$()$(br)One of three main continental areas. Low altitude biomes such as $(l:the_world/biomes#lowlands)Lowlands$(), $(l:the_world/biomes#low_canyons)Low Canyons$(), or $(l:the_world/biomes#plains)Plains$() are common.'),
            text('$(bold)Mid Altitude Continental$()$(br)A mid elevation continental area, can contain many biomes and usually borders low or high altitude continental areas.$(br2)$(bold)High Altitude Continental$()$(br)A high altitude area with $(l:the_world/biomes#hills)Rolling Hills$(), $(l:the_world/biomes#plateau)Plateaus$(), and $(l:the_world/biomes#mountains)Old Mountains$().$(br2)$(bold)Mid-Ocean Ridge$()$(br)A mid ocean ridge forms when two oceanic plates diverge away from each other.'),
            text('It can generate rare volcanism and some volcanic mountains.$(br2)$(bold)Oceanic Subduction$()$(br)A subduction zone is where one plate slips under the other. In the ocean, this can form lots of volcanic mountains, island chains, and deep ocean ridges.$(br2)$(bold)Continental Subduction$()$(br)A continental subduction zone is a area of frequent volcanic activity, and huge coastal mountains. Active hot springs and volcanoes are common.'),
            text('$(bold)Continental Rift$()$(br)A continental rift is the site where two continents diverge, like $(l:https://en.wikipedia.org/wiki/Geology_of_Iceland)Iceland$(). It is the location of $(l:the_world/biomes#canyons)Canyons$() biomes, and shorter less active volcanoes, along with some other high altitude biomes.$(br2)$(bold)Orogenic Belt$()$(br)An $(l:https://en.wikipedia.org/wiki/Orogeny)Orogeny$() is the site of major mountain building. It forms where two continental plates collide and produces tall $(l:the_world/biomes#mountains)Mountains$() and $(l:the_world/biomes#plateau)Plateaus$().'),
            text('$(bold)Continental Shelf$()$(br)Finally, a continental shelf is a section of shallow ocean off the coast of a continent. It is where coral reefs appear in warmer climates.'),
            page_break(),
            text('The world is also divided up into different types of $(thing)Rock$(). Rock regions can be over a kilometer across, and there will usually be two or three different rock layers under your feet at all times. As different ores are found in different rock types, locating specific rock types can be very important for finding resources such as $(l:the_world/ores_and_minerals)Ores$(), which will often only appear in certain rock types.', title='Rock Layers'),
            text('Rocks come in four categories: $(thing)Sedimentary$(), $(thing)Metamorphic$(), $(thing)Igneous Extrusive$(), and $(thing)Igneous Intrusive$(). These categories determine at what depth the different rock layers can be found. A listing of all the different rock types and what category they belong to can be found on the following pages.'),
            text('$(l:https://en.wikipedia.org/wiki/Sedimentary_rock)Sedimentary$() rocks are formed by the accumulation or deposition of mineral or organic particles. They can be found in $(thing)mid to high altitude$() rock layers. They are:$(br)$(li)Shale$(li)Claystone$(li)Limestone$(li)Conglomerate$(li)Dolomite$(li)Chert$(li)Chalk', title='Sedimentary').anchor('sedimentary'),
            text('$(l:https://en.wikipedia.org/wiki/Metamorphic_rock)Metamorphic$() rocks are created by a process called metamorphism. They can be found at $(thing)any elevation$(). They are:$(br)$(li)Quartzite$(li)Slate$(li)Phyllite$(li)Schist$(li)Gneiss$(li)Marble', title='Metamorphic').anchor('metamorphic'),
            text('$(l:https://en.wikipedia.org/wiki/Igneous_rock#Extrusive)Igneous Extrusive$() rocks are formed from magma cooling on the Earth\'s surface. They can be found at $(thing)mid to high altitude$() rock layers. They are:$(br)$(li)Rhyolite$(li)Basalt$(li)Andesite$(li)Dacite', title='Igneous Extrusive').anchor('igneous_extrusive'),
            text('$(l:https://en.wikipedia.org/wiki/Igneous_rock#Intrusive)Igneous Intrusive$() rocks are formed from magma which cooled under the Earth\'s crust. They can be found at $(thing)mid to low altitude$() rock layers. They are:$(br)$(li)Granite$(li)Diorite$(li)Gabbro', title='Igneous Intrusive').anchor('igneous_intrusive'),
        )),
        entry('ores_and_minerals', 'Ores and Minerals', 'tfc:ore/normal_hematite', pages=(
            text('Ores and Minerals in TerraFirmaCraft are rare - unlike Vanilla, ores are found in massive, sparse, yet rare veins that require some $(l:mechanics/prospecting)prospecting$() to locate. Different ores will also appear in different rock types, and at different elevations, meaning finding the right rock type at the right altitude is key to locating the ore you are looking for.'),
            text('In addition, some ores are $(thing)Graded$(). Ore blocks may be Poor, Normal, or Rich, and different veins will have different concentrations of each type of block. Veins that are $(thing)richer$() are more lucrative.$(br2)The next several pages show the different types of ores, and where to find them.'),
            # === Metal Ores Listing ===
            page_break(),
            text('Native Copper is a ore of $(thing)Copper$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks.', title='Native Copper').link(*['tfc:ore/%s_%s' % (g, 'native_copper') for g in GRADES_ALL]).anchor('native_copper'),
            multimultiblock('Native Copper Ores in Dacite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_copper', 'dacite')) for g in GRADES]),
            text('Native Gold is a ore of $(thing)Gold$() metal. It can be found at elevations below y=60, but deeper veins are often richer. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() and $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.', title='Native Gold').link(*['tfc:ore/%s_%s' % (g, 'native_gold') for g in GRADES_ALL]).anchor('native_gold'),
            multimultiblock('Native Gold Ores in Diorite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_gold', 'dacite')) for g in GRADES]),
            text('Native Silver is a ore of $(thing)Silver$() metal. It can be found at elevations between y=-32 and y=100. It can be found in $(thing)Granite$(), and $(thing)Gneiss$() primarily, however smaller poorer veins can be found in any $(l:the_world/geology#metamorphic)Metamorphic$() rocks.', title='Native Gold').link(*['tfc:ore/%s_%s' % (g, 'native_silver') for g in GRADES_ALL]).anchor('native_silver'),
            multimultiblock('Native Silver Ores in Granite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'native_silver', 'granite')) for g in GRADES]),
            text('Hematite is a ore of $(thing)Iron$() metal. It can be found at elevations below y=75. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks.', title='Hematite').link(*['tfc:ore/%s_%s' % (g, 'hematite') for g in GRADES_ALL]).anchor('hematite'),
            multimultiblock('Hematite Ores in Andesite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'hematite', 'andesite')) for g in GRADES]),
            text('Cassiterite is a ore of $(thing)Tin$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.$(br2)When found in $(thing)Granite$(), cassiterite veins can also contain traces of $(thing)Topaz$().', title='Cassiterite').link(*['tfc:ore/%s_%s' % (g, 'cassiterite') for g in GRADES_ALL]).anchor('cassiterite'),
            multimultiblock('Cassiterite Ores in Diorite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'cassiterite', 'diorite')) for g in GRADES]),
            text('Bismuthinite is a ore of $(thing)Bismuth$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() and $(l:the_world/geology#sedimentary)Sedimentary$() rocks.', title='Bismuthinite').link(*['tfc:ore/%s_%s' % (g, 'bismuthinite') for g in GRADES_ALL]).anchor('bismuthinite'),
            multimultiblock('Bismuthinite Ores in Shale.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'bismuthinite', 'shale')) for g in GRADES]),
            text('Garnierite is a ore of $(thing)Nickel$() metal. It can be found at at elevations between y=-32 and y=100. It can be found primarily in $(thing)Gabbro$(), however smaller poorer veins can be found in any $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.', title='Garnierite').link(*['tfc:ore/%s_%s' % (g, 'garnierite') for g in GRADES_ALL]).anchor('garnierite'),
            multimultiblock('Garnierite Ores in Gabbro.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'garnierite', 'gabbro')) for g in GRADES]),
            text('Malachite is a ore of $(thing)Copper$() metal. It can be found at at elevations between y=-32 and y=100. It can be found primarily in $(thing)Marble$() or $(thing)Limestone$(), however smaller poorer veins can also be found in $(thing)Phyllite$(), $(thing)Chalk$(), and $(thing)Dolomite$().$(br2)When found in $(thing)Limestone$(), malachite veins can also contain traces of $(thing)Gypsum$().', title='Malachite').link(*['tfc:ore/%s_%s' % (g, 'malachite') for g in GRADES_ALL]).anchor('malachite'),
            multimultiblock('Malachite Ores in Marble.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'malachite', 'marble')) for g in GRADES]),
            text('Magnetite is a ore of $(thing)Iron$() metal. It can be found at elevations below y=60, but deeper veins are often richer. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.', title='Magnetite').link(*['tfc:ore/%s_%s' % (g, 'magnetite') for g in GRADES_ALL]).anchor('magnetite'),
            multimultiblock('Magnetite Ores in Limestone.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'magnetite', 'limestone')) for g in GRADES]),
            text('Limonite is a ore of $(thing)Iron$() metal. It can be found at elevations below y=60, but deeper veins are often richer. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.$(br2)When found in $(thing)Limestone$() or $(thing)Shale$(), malachite veins can also contain traces of $(thing)Rubies$().', title='Limonite').link(*['tfc:ore/%s_%s' % (g, 'limonite') for g in GRADES_ALL]).anchor('limonite'),
            multimultiblock('Limonite Ores in Chalk.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'limonite', 'chalk')) for g in GRADES]),
            text('sphalerite is a ore of $(thing)Zinc$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#metamorphic)Metamorphic$() rocks.', title='Sphalerite').link(*['tfc:ore/%s_%s' % (g, 'sphalerite') for g in GRADES_ALL]).anchor('sphalerite'),
            multimultiblock('Sphalerite Ores in Quartzite.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'sphalerite', 'quartzite')) for g in GRADES]),
            text('Tetrahedrite is a ore of $(thing)Copper$() metal. It can be found at any elevation, but deeper veins are often richer. It can be found in $(l:the_world/geology#metamorphic)Metamorphic$() rocks.', title='Tetrahedrite').link(*['tfc:ore/%s_%s' % (g, 'tetrahedrite') for g in GRADES_ALL]).anchor('tetrahedrite'),
            multimultiblock('Tetrahedrite Ores in Schist.', *[block_spotlight('', '', 'tfc:ore/%s_%s/%s' % (g, 'tetrahedrite', 'schist')) for g in GRADES]),
            # === Non-Metal / Mineral Ores Listing ===
            item_spotlight('tfc:ore/bituminous_coal', 'Bituminous Coal', text_contents='Bituminous Coal is a type of $(thing)Coal$() ore. It can be found at elevations above y=0. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'bituminous_coal').anchor('bituminous_coal'),
            block_spotlight('', 'Bituminous Coal in Chert.', 'tfc:ore/%s/%s' % ('bituminous_coal', 'chert')),
            item_spotlight('tfc:ore/lignite', 'Lignite', text_contents='Lignite is a type of $(thing)Coal$() ore. It can be found at elevations below y=100. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'lignite').anchor('lignite'),
            block_spotlight('', 'Lignite in Dolomite.', 'tfc:ore/%s/%s' % ('lignite', 'dolomite')),
            item_spotlight('tfc:ore/kaolinite', 'Kaolinite', text_contents='Kaolinite is a $(thing)Mineral$() which is used in the construction of $(l:mechanics/fire_clay)Fire Clay$(). It can be found at elevations above y=0. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'kaolinite').anchor('kaolinite'),
            block_spotlight('', 'Kaolinite in Claystone.', 'tfc:ore/%s/%s' % ('kaolinite', 'claystone')),
            item_spotlight('tfc:ore/graphite', 'Graphite', text_contents='Graphite is a $(thing)Mineral$() which is used in the construction of $(l:mechanics/fire_clay)Fire Clay$(). It can be found at elevations below y=100. It can be found in $(thing)Gneiss$(), $(thing)Marble$(), $(thing)Quartzite$(), and $(thing)Schist$().').link('tfc:ore/%s' % 'graphite').anchor('graphite'),
            block_spotlight('', 'Graphite in Gneiss.', 'tfc:ore/%s/%s' % ('graphite', 'gneiss')),
            item_spotlight('tfc:ore/cinnabar', 'Cinnabar', text_contents='Cinnabar is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to obtain $(thing)Redstone Dust$(). It can be found at elevations below y=100. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks, $(thing)Quartzite$(), and $(thing)Shale$().$(br2)When found in $(thing)Quartzite$(), cinnabar veins can also contain traces of $(thing)Opal$().').link('tfc:ore/%s' % 'cinnabar').anchor('cinnabar'),
            block_spotlight('', 'Cinnabar in Quartzite.', 'tfc:ore/%s/%s' % ('cinnabar', 'quartzite')),
            item_spotlight('tfc:ore/cryolite', 'Cryolite', text_contents='Cryolite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to obtain $(thing)Redstone Dust$(). It can be found at elevations below y=100. It can be found only in $(thing)Granite$().').link('tfc:ore/%s' % 'cryolite').anchor('cryolite'),
            block_spotlight('', 'Cryolite in Granite.', 'tfc:ore/%s/%s' % ('cryolite', 'granite')),
            item_spotlight('tfc:ore/saltpeter', 'Saltpeter', text_contents='Saltpeter is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used in the crafting of $(thing)Gunpowder$(). It can be found at elevations below y=100. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.$(br2)When found in $(thing)Limestone$(), saltpeter veins also can contain traces of $(thing)Gypsum$().').link('tfc:ore/%s' % 'saltpeter').anchor('saltpeter'),
            block_spotlight('', 'Saltpeter in Shale.', 'tfc:ore/%s/%s' % ('saltpeter', 'shale')),
            item_spotlight('tfc:ore/sulfur', 'Sulfur', text_contents='Sulfur is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used in the crafting of $(thing)Gunpowder$(). It can be found at elevations above y=0. It can be found in $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() rocks. It can also be found more commonly in $(thing)Volcanic$() areas, in both $(l:the_world/geology#igneous_extrusive)Igneous Extrusive$() and $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks, at higher elevations.').link('tfc:ore/%s' % 'sulfur').anchor('sulfur'),
            block_spotlight('', 'Sulfur in Basalt.', 'tfc:ore/%s/%s' % ('sulfur', 'basalt')),
            item_spotlight('tfc:ore/sylvite', 'Sylvite', text_contents='Sylvite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$(), and then used as a $(l:mechanics/fertilizers)Fertilizer$(). It can be found at elevations above y=0. It can be found in $(thing)Shale$(), $(thing)Claystone$() and $(thing)Chert$().').link('tfc:ore/%s' % 'sylvite').anchor('sylvite'),
            block_spotlight('', 'Sylvite in Chert.', 'tfc:ore/%s/%s' % ('sylvite', 'chert')),
            item_spotlight('tfc:ore/borax', 'Borax', text_contents='Borax is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to produce $(l:mechanics/flux)Flux$(). It can be found at elevations above y=0. It can be found in $(thing)Claystone$(), $(thing)Limestone$(), and $(thing)Shale$().').link('tfc:ore/%s' % 'borax').anchor('borax'),
            block_spotlight('', 'Borax in Shale.', 'tfc:ore/%s/%s' % ('borax', 'shale')),
            item_spotlight('tfc:ore/lapis_lazuli', 'Lapis Lazuli', text_contents='Lapis Lazuli is a decorative $(thing)Mineral$() which can be used to make $(thing)Dye$(). It can be found at elevations below y=100. It can be found in $(thing)Limestone$() and $(thing)Marble$().').link('tfc:ore/%s' % 'lapis_lazuli').anchor('lapis_lazuli'),
            block_spotlight('', 'Lapis Lazuli in Limestone.', 'tfc:ore/%s/%s' % ('lapis_lazuli', 'limestone')),
            item_spotlight('tfc:ore/gypsum', 'Gypsum', text_contents='Gypsum is a decorative $(thing)Mineral$() which can be used to make $(l:getting_started/building_materials#alabaster)Alabaster$(). It appears in dense disc like formations at elevations between y=30 and y=90. It can be found in $(l:the_world/geology#metamorphic)Metamorphic$() rocks.').link('tfc:ore/%s' % 'gypsum').anchor('gypsum'),
            block_spotlight('', 'Gypsum in Phyllite.', 'tfc:ore/%s/%s' % ('gypsum', 'phyllite')),
            item_spotlight('tfc:ore/halite', 'Halite', text_contents='Halite is a $(thing)Mineral$() which can be ground in the $(l:mechanics/quern)Quern$() to make $(thing)Salt$(), which is an important $(l:food/decay#salting)Preservative$(). It appears in dense disc like formations at elevations between y=30 and y=90. It can be found in $(l:the_world/geology#sedimentary)Sedimentary$() rocks.').link('tfc:ore/%s' % 'halite').anchor('halite'),
            block_spotlight('', 'Halite in Chalk.', 'tfc:ore/%s/%s' % ('halite', 'chalk')),
            item_spotlight('tfc:ore/emerald', 'Emerald', text_contents='Emerald is a decorative $(thing)Gemstone$(). It looks quite pretty, maybe if you could find someone else in this incredibly lonely world you could trade it with them...$(br2)It appears in thin vertical ore formations which can be up to 60 blocks high. It can be found in $(l:the_world/geology#igneous_intrusive)Igneous Intrusive$() rocks.').link('tfc:ore/%s' % 'emerald').anchor('emerald'),
            block_spotlight('', 'Emerald in Diorite.', 'tfc:ore/%s/%s' % ('emerald', 'diorite')),
            item_spotlight('tfc:ore/diamond', 'Kimberlite', text_contents='Kimberlite is a decorative and priceless $(thing)Gemstone$(). It appears in thin vertical ore formations called $(l:https://en.wikipedia.org/wiki/Volcanic_pipe)Kimberlite Pipes$() which can be up to 60 blocks high. It can only be found in $(thing)Gabbro$().').link('tfc:ore/%s' % 'diamond').anchor('diamond'),
            block_spotlight('', 'Kimberlite in Gabbro.', 'tfc:ore/%s/%s' % ('diamond', 'gabbro')),
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
            text('Temperature in TerraFirmaCraft is influenced by a number of factors:$(br)$(li)Firstly, the region, especially the latitude (Z coordinate) will play the largest role.$(li)Secondly, the current season will influence the temperature - it will be hottest during Summer, and coldest during Winter.$(li)Finally, the temperature can also be different day-to-day, and even hourly.').anchor('temperature'),
            text('The last line shows the current temperature, including all these aforementioned factors.$(br2)Temperature can influence many things: if crops and plants will grow, if snow and ice will form or melt, and more.'),
            page_break(),
            text('Rainfall is another climate value that can vary depending on where you are in the world. The annual rainfall is measured in millimeters (mm) and can be between 0mm - 500mm. Rainfall affects the types of flora that are found in an area, and also the types of soil, from sand and cacti, to loam, to silt and kapok trees.', title='Rainfall').anchor('rainfall'),
            text('Rainfall is also important as it affects what things can be grown in an area. Rainfall is one of the main contributors to $(l:food/hydration)Hydration$(), which is an exact measure of how wet the soil is in a given location, and is used by $(l:food/crops)Crops$(), $(l:the_world/fruit_trees)Fruit Trees$(), and $(l:the_world/berry_bushes)Berry Bushes$() to determine if they can grow.'),
        )),
        entry('flora', 'Flora', 'tfc:plant/goldenrod', pages=(
            # Overview of various plants
            # Mention some usages (dyes)
            text('There are many, many, $(italic)many$() different types of plants in TerraFirmaCraft.$(br2)Different plants appear in different $(l:the_world/climate)Climates$(), and their appearance may change over the current season - going through cycles of flowering and laying dormant, or changing color as the local temperature changes. Colorful flowers can typically be crushed in a $(l:mechanics/quern)Quern$() for dye.'),
            block_spotlight('Standard', 'Standard plants are like small flowers. They grow on grass, dirt, and farmland.', 'tfc:plant/anthurium'),
            block_spotlight('Dry', 'Dry plants are like standard plants, but they can grow on sand. These generally only spawn in areas with low rainfall.', 'tfc:plant/sagebrush'),
            two_tall_block_spotlight('Cacti', 'Cacti can grow two blocks high, and they will damage you!', 'tfc:plant/barrel_cactus[part=lower]', 'tfc:plant/barrel_cactus[part=upper]'),
            block_spotlight('Creeping', 'Creeping plants take the shape of the surrounding block faces. They spawn in blobs on the ground, mostly.', 'tfc:plant/moss[down=true]'),
            two_tall_block_spotlight('Hanging', 'Hanging plants are relatable, because they don\'t do work and mostly just hang out.', 'tfc:plant/spanish_moss[hanging=true]', 'tfc:wood/wood/oak'),
            multiblock('Epiphyte', 'Epiphytes only live on the sides of logs.', False, pattern=(('XY',), ('0 ',)), mapping={'X': 'tfc:wood/wood/birch', 'Y': 'tfc:plant/licorice_fern[facing=south]'}),
            block_spotlight('Short Grass', 'Short grass blocks grow taller with age. They also are able to grow on peat and mud.', 'tfc:plant/bluegrass'),
            two_tall_block_spotlight('Tall Grass', 'Tall grass blocks are just tall enough to block your field of view.', 'tfc:plant/king_fern[part=lower]', 'tfc:plant/king_fern[part=upper]'),
            block_spotlight('Vines', 'Vine blocks spread around the world on their own, if it\'s warm enough.', 'tfc:plant/ivy[up=true,north=true]'),
            two_tall_block_spotlight('Weeping Vines', 'Weeping vines grow downward from the an anchoring block.', 'tfc:plant/liana', 'tfc:plant/liana_plant'),
            two_tall_block_spotlight('Twisting Vines', 'Twisting vines twist upward from the Earth. They can come in a solid variant.', 'tfc:plant/arundo_plant', 'tfc:plant/arundo'),
            text('Water plants are restricted to either spawn in fresh or salty water. Otherwise, they grow and act much like the plants you see on the surface. Some water plants can be cooked for food.', title='Water Plants'),
            block_spotlight('Standard Water', 'Standard water plants can be broken with a $(thing)$()Knife$() to get $(thing)Seaweed$().', 'tfc:plant/sago[fluid=water]'),
            block_spotlight('Water Grass', 'Water grasses are grasses that grow under the water.', 'tfc:plant/manatee_grass[fluid=salt_water]'),
            two_tall_block_spotlight('Tall Water Plant', 'Tall water plants can grow with just the bottom block in water. $(thing)Water Taro$() and $()Cattail$() can be broken with a $(thing)Knife$() for $(thing)Roots$().', 'tfc:plant/cattail[part=lower,fluid=water]', 'tfc:plant/cattail[part=upper]'),
            two_tall_block_spotlight('Floating', 'Floating plants sit on top of the water. Boats will break them on contact.', 'minecraft:water', 'tfc:plant/duckweed'),
            two_tall_block_spotlight('Kelp', 'Kelp are twisting vines that grow underwater.', 'tfc:plant/winged_kelp_plant[fluid=salt_water]', 'tfc:plant/winged_kelp[fluid=salt_water]'),
            two_tall_block_spotlight('Tree Kelp', 'Tree kelp grow into intricate trees underwater. The flowers can be harvested with a $(thing)Knife$().', 'tfc:plant/giant_kelp_plant[down=true,up=true,fluid=salt_water]', 'tfc:plant/giant_kelp_flower[facing=up,fluid=salt_water]'),  # note: anyone want to make a nice multiblock for this?
            empty_last_page(),
        )),
        entry('wild_crops', 'Wild Crops', 'tfc:wild_crop/wheat', pages=(
            # Wild crops - how to find them, why you'd want to, what they drop
            text('$(thing)Wild Crops$() can be found scattered around the world, growing in small patches. They can be harvested for food and seeds, which can then be cultivated themselves in the not-wild form.$(br2)Harvesting wild crops can be done with your fists, or with a $(thing)Knife$() or other sharp tool. When broken, they will drop $(thing)Seeds$() and some $(thing)Products$().'),
            block_spotlight('Wild Wheat', 'An example of a wild crop, in this case $(l:food/crops#wheat)Wheat$().', 'tfc:wild_crop/wheat'),
            text('There are many different types of wild crop - every crop that can be cultivated has a wild variant that can be found in the world somewhere. See the list of $(l:food/crops)Crops$() for all different crops that can be grown. Wild crops will look similar to their cultivated counterparts, but are more hidden within the grass. Wild crops will spawn in climates near where the crop itself can be cultivated, so if looking for a specific crop, look in the climate where the crop can be cultivated.'),
            multimultiblock('All different varieties of wild crop', *(
                block_spotlight('', '', 'tfc:wild_crop/barley'),
                block_spotlight('', '', 'tfc:wild_crop/oat'),
                block_spotlight('', '', 'tfc:wild_crop/rye'),
                two_tall_block_spotlight('', '', 'tfc:wild_crop/maize[part=bottom]', 'tfc:wild_crop/maize[part=top]'),
                block_spotlight('', '', 'tfc:wild_crop/barley'),
                block_spotlight('', '', 'tfc:wild_crop/rice'),  # todo: change to waterlogged when the rice block itself is capable of that
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
            ))
        )),
        entry('berry_bushes', 'Berry Bushes', 'tfc:food/elderberry', pages=(
            image('tfc:textures/gui/book/wip.png'),
            empty_last_page(),
            # Berry bushes - how to find them, how to harvest and move them
            # todo: info about berry bushes
            # todo: listing of various berry bushes
        )),
        entry('fruit_trees', 'Fruit Trees', 'tfc:food/red_apple', pages=(
            image('tfc:textures/gui/book/wip.png'),
            empty_last_page(),
            # Fruit trees - how to find them, how to harvest and move them
            # todo: info about fruit trees
            # todo: listing of various fruit trees
        )),
        entry('wild_animals', 'Wild Animals', 'tfc:medium_raw_hide', pages=(
            # Wild animals - address both hostile and passive important animals
            text('The world of TFC is full of animal life. Some animals are here to help, and some are incredibly dangerous. This section is about wild animals. For information on livestock, animals that can give you items you need, see the $(l:mechanics/animal_husbandry)Animal Husbandry$() page.'),
            text('Animals can be grouped into a couple different categories: $(l:the_world/wild_animals#predators)Predators$(), $(l:the_world/wild_animals#prey)Prey$(), and $(l:the_world/wild_animals#aquatic)Aquatic$() animals.$(br2)The next few pages will detail each of these categories of animals.'),
            page_break(),
            text('$(thing)Predators$() are animals that can attack the player. They are either $(thing)Nocturnal$(), only hunting at night, or $(thing)Diurnal$(), only hunting during the day. Predators can be neutral or hostile, depending on if they have killed a target recently. Predators have a home territory that they will defend - if you run far enough away, the predator will stop chasing and return home.', title='Predators').anchor('predators'),
            entity('tfc:polar_bear', 'The polar bear spawns in only the coldest regions, $(l:the_world/climate#temperature)temperature$() at most 10°C, with a $(l:the_world/climate#rainfall)rainfall$() of at least 100mm.', 'Polar Bear', 0.55),
            entity('tfc:grizzly_bear', 'The grizzly bear spawns in forests of moderate climates, with a $(l:the_world/climate#temperature)temperature$() range of -15 to 15°C and at least 200mm of $(l:the_world/climate#rainfall)rainfall$().', 'Grizzly Bear', 0.55),
            entity('tfc:black_bear', 'The black bear spawns in forests of warmer, wetter climates, of $(l:the_world/climate#temperature)temperature$() 5 to 20°C and at least 250mm of $(l:the_world/climate#rainfall)rainfall$().', 'Black Bear', 0.55),
            entity('tfc:cougar', 'The cougar prefers most moderate climates, with $(l:the_world/climate#temperature)temperature$() from -10 to 21°C and at least 150mm of $(l:the_world/climate#rainfall)rainfall$().', 'Cougar', 0.6),
            entity('tfc:panther', 'The panther prefers most moderate climates, with $(l:the_world/climate#temperature)temperature$() from -10 to 21°C and at least 150mm of $(l:the_world/climate#rainfall)rainfall$().', 'Panther', 0.6),
            entity('tfc:lion', 'The lion spawns in plains with an average $(l:the_world/climate#temperature)temperature$() of at least 16°C, and $(l:the_world/climate#rainfall)rainfall$() between 50 and 300mm.', 'Lion', 0.55),
            entity('tfc:sabertooth', 'The sabertooth spawns at any $(l:the_world/climate#temperature)temperature$() above 0°C, and any $(l:the_world/climate#rainfall)rainfall$() above 250mm.', 'Sabertooth', 0.6),
            page_break(),
            text('$(thing)Prey$() animals fear players and predators. They are adept at fleeing from danger, but generally cannot fight back. Some prey animals enjoy snacking on crops.', title='Prey').anchor('prey'),
            entity('tfc:boar', 'The boar spawns in plains with $(l:the_world/climate#temperature)temperature$() below 25°C, and $(l:the_world/climate#rainfall)rainfall$() between 130 and 400mm.', 'Boar'),
            entity('tfc:rabbit', 'The rabbit is known to chew on carrots and cabbage. They are ubiquitous in the world, changing their coat based on climate. They only need 15mm of $(l:the_world/climate#rainfall)rainfall$() to spawn.', 'Rabbit'),
            entity('tfc:fox', 'The fox likes to eat the berries off of bushes. It can be found in forests with $(l:the_world/climate#temperature)temperature$() below 25°C, and $(l:the_world/climate#rainfall)rainfall$() between 130 and 400mm.', 'Fox'),
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
            entity('tfc:salmon', 'Salmon spawn in any river or lake with a $(l:the_world/climate#temperature)temperature$() of at least -5°C.'),
            entity('tfc:bluegill', 'Bluegill spawn in any river or lake with a $(l:the_world/climate#temperature)temperature$() of at least -10°C and at most 26°C.'),
            text('$(thing)Shellfish$() are small animals that live on the floor of bodies of water. They cannot be fished, but drop shells that can be eaten or made into $(l:mechanics/flux)flux$(). Shellfish can be $(l:mechanics/fishing)bait$() for fish.', title='Shellfish').anchor('shellfish'),
            entity('tfc:isopod', 'Isopods spawn in deeper sections of oceans of $(l:the_world/climate#temperature)temperature$() at most 14°C.', 'Isopod'),
            entity('tfc:lobster', 'Lobster spawn in any ocean that is at most of a $(l:the_world/climate#temperature)temperature$() of 21°C.', 'Lobster'),
            entity('tfc:crayfish', 'Crayfish are like lobster, but spawn in rivers and lakes. They need a $(l:the_world/climate#temperature)temperature$() of at least 5°C and a $(l:the_world/climate#rainfall)rainfall$() of at least 125mm.', 'Crayfish'),
            entity('tfc:horseshoe_crab', 'Horseshoe crabs spawn in oceans of moderate climate, $(l:the_world/climate#temperature)temperature$() between 10 and 21°C and with a $(l:the_world/climate#rainfall)rainfall$() of at most 400mm.', 'Horseshoe Crab'),
            page_break(),
            text('$(thing)Large Water Creatures$() are larger animals that live in the bigger bodies of water. Some of them predate on fish. They drop $(l:mechanics/lamps#tallow)blubber$(), which can be made into lamp fuel.', title='Large Water Creatures'),
            entity('tfc:orca', 'Orca whales live in deep oceans with $(l:the_world/climate#temperature)temperature$() of at most 19°C and $(l:the_world/climate#rainfall)rainfall$() of at least 100mm.', 'Orca', scale=0.25),
            entity('tfc:dolphin', 'Dolphins live in deep oceans with $(l:the_world/climate#temperature)temperature$() of at least 10°C and $(l:the_world/climate#rainfall)rainfall$() of at least 200mm.', 'Dolphin', scale=0.4),
            entity('tfc:manatee', 'Manatees live in any warm lake, with $(l:the_world/climate#temperature)temperature$() of at least 20°C and $(l:the_world/climate#rainfall)rainfall$() of at most 300mm.', 'Manatee', scale=0.25),
            text('Squid can spawn in any deep ocean. They drop $(thing)Ink Sacs$(), and ink any player that gets too close. Some say that squids in deep, unexplored caves have strange properties.', 'Squid'),  # todo: squid renderer does not work without proper context...
            empty_last_page(),
        ))
        # DON'T ADD MORE ENTRIES. If possible, because this list fits neatly on one page
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
            rock_knapping_typical('tfc:rock_knapping/knife_head_%s', 'A knife blade, crafted from several different rock types.'),
            crafting('tfc:crafting/stone/knife_sedimentary', text_contents='Once you have obtained a knife blade, in order to create a stone knife, simply craft it with a stick in your inventory.'),
            crafting('tfc:crafting/wood/stick_from_twigs', text_contents='The twigs from earlier can also be used to create sticks, if needed.'),
            item_spotlight('tfc:stone/knife/sedimentary', text_contents='Knives are a very useful tool. One of their primary uses is to collect straw by breaking plants. Most tall grasses and plants will drop straw when broken with a knife.'),
            crafting('tfc:crafting/thatch', text_contents='Straw can be used to craft one of the first building materials: $(thing)Thatch$(). Thatch is a lightweight block that isn\'t affected by gravity, however players and other entities can pass right through it!'),
            text('In addition to knives, you will likely want to craft a couple other tools. $(thing)Axes$() can be used to chop down trees (finally!), and also make a useful weapon. $(thing)Hammers$() can be used as a crushing weapon, but can also be used to turn logs into sticks, by breaking log blocks with the hammer.'),
            text('Finally, $(thing)Shovels$() and $(thing)Hoes$() behave the same as they do in Vanilla, and $(thing)Javelins$() can be used as a simple toss-once-and-retrieve ranged weapon.'),
        )),
        entry('firepit', 'Pits of Fire', 'tfc:firepit', pages=(
            text('$(thing)Fire$() is an important technological advancement. In order to create fire, you will need a $(thing)Firestarter$(). In order to use, simply hold $(item)$(k:key.use)$() down on the ground. After a few moments, smoke, and then fire will be created. It may take a couple tries to light successfully.').anchor('firestarter'),
            crafting('tfc:crafting/firestarter', text_contents='Crafting a firestarter can be done with two sticks.'),
            text('With a firestarter, it is now possible to make a $(thing)Firepit$(). In order to make one, you will need one $(thing)log$(), three $(thing)sticks$(), and optionally up to three pieces of $(thing)kindling$(). Kindling can be items such as paper, straw, or other items, and will increase the chance of successfully creating a firepit. Throw ($(item)$(k:key.drop)$()) all the items on the ground, on the same block. Then use the firestarter on the block with the items floating above it.', 'Firepit'),
            block_spotlight('', 'If you were successful, a firepit will be created.', 'tfc:firepit[lit=true]'),
            text('Using the firepit again will now open the firepit screen. On the left are four $(thing)fuel$() slots. Logs, Peat, and Stick Bundles can all be used as firepit fuel by placing them in the topmost slot. Fuel will be consumed from the bottommost slot. There is a gauge which displays the current $(thing)Temperature$() of the firepit, and on the right, a slot for items to be $(l:getting_started/heating)heated$() in.'),
            image('tfc:textures/gui/book/gui/firepit.png', text_contents='The Firepit Screen', border=False),
            heat_recipe('tfc:heating/torch_from_stick', 'Many useful items can be made in a firepit by heating them. Sticks can be heated, where they will produce two $(thing)Torches$(). Note that torches will eventually burn out, and need to be re-lit by using a $(thing)Firestarter$(), or using another $(thing)Torch$() on them.'),
            heat_recipe('tfc:heating/cod', 'The fire pit is also a good device for $(thing)cooking food$(). All raw meats and doughs can be cooked in a firepit, which will lengthen their shelf life. (More on that $(l:food/decay)here$())'),
            text('The firepit can be extinguished at any time by using a $(thing)Shovel$() on it.$(br2)A firepit can take can also have other devices added to it to extend it\'s functionality. Using a $(thing)Wrought Iron Grill$() will convert the firepit into a $(l:mechanics/grill)Grill$(), and using a $(thing)Ceramic Pot$() will convert the firepit into a $(l:mechanics/pot)Pot$(). To remove either device, $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$(). Be careful not to try to remove a hot grill or pot!'),
            multimultiblock(
                'A firepit, with either a grill or pot added.',
                block_spotlight('', '', 'tfc:firepit'),
                block_spotlight('', '', 'tfc:grill'),
                block_spotlight('', '', 'tfc:pot'),
            ),
        )),
        entry('pottery', 'Pottery', 'tfc:ceramic/vessel', pages=(
            text('$(thing)Clay$() is an incredibly useful and balanced material, which can be used for pottery. However first, it needs to be located. Clay is usually hidden by grass, but it is found often in two locations. In areas with of at least 175mm $(l:the_world/climate#rainfall)Annual Rainfall$(), clay can be found in patches all over the place, however these patches are usually marked the by presence of certain $(thing)Plants$().'),
            multiblock('Clay Indicators', 'Clay, with one of the plants that may indicate it\'s presence.', False, pattern=(
                ('   ', ' C ', '   '),
                ('XXX', 'X0X', 'XXX')
            ), mapping={
                '0': 'tfc:clay_grass/sandy_loam',
                'X': 'tfc:clay_grass/sandy_loam',
                'C': '#tfc:clay_indicators',
            }),
            text('$(thing)Athyrium Fern$(), $(thing)Canna$(), $(thing)Goldenrod$(), $(thing)Pampas Grass$(), $(thing)Perovskia$(), and $(thing)Water Canna$() all can indicate the presence of clay nearby. Clay can be found in smaller deposits close to water sources, such as rivers, lakes, or ponds.$(br2)Like with rocks, clay can be knapped to form new items. It requires five clay in your hand to knap. Unlike rocks, if you make a mistake, you can simply close the knapping interface, reshape your clay, and try again.').link('#tfc:clay_indicators'),
            image('tfc:textures/gui/book/gui/clay_knapping.png', text_contents='The Knapping Interface.', border=False),
            text('The small vessel is one such item. Like all pottery items, before it can be used it must be $(l:https://en.wikipedia.org/wiki/Pottery)fired$(). Firing is a process of $(l:mechanics/heating)heating$() the item up to a point where the clay will turn into a hard $(thing)Ceramic$() material, which requires heating to 1200 °C, or $(e)$(bold)$(t:Yellow)Yellow$().$(br2)In order to do this in the early game, you will need to use a $(l:getting_started/pit_kiln)Pit Kiln$().', title='Small Vessel').link('tfc:ceramic/unfired_vessel').link('tfc:ceramic/vessel').anchor('vessel'),
            clay_knapping('tfc:clay_knapping/vessel', 'Knapping a Clay Small Vessel.'),
            text('Another useful pottery item is the $(thing)Jug$(). It can be used to pick up and $(thing)drink$() fluids, such as fresh water.$(br2)In order to use it, simply $(item)$(k:key.use)$() the jug on the fluid in the world. Then use the jug in order to drink from it. The jug can hold $(thing)100 mB$() of fluid at a time.', title='Jug').link('tfc:ceramic/unfired_jug').link('tfc:ceramic/jug').anchor('jug'),
            clay_knapping('tfc:clay_knapping/jug', 'Knapping a Clay Jug.'),
            text('Clay is also a very useful material for making $(thing)Molds$(). Molds can have molten metal poured into them, which will eventually solidify into the shape of a mold. The item and potentially the mold can then be retrieved by using $(item)$(k:key.use)$() on the mold.$(br2)The most simple type of mold is the ingot mold, to the right.', title='Molds').anchor('mold'),
            clay_knapping('tfc:clay_knapping/ingot_mold', 'Knapping a clay ingot mold.'),
            heat_recipe('tfc:heating/ingot_mold', 'The mold then needs to be fired, like all clay items, to be usable - likely in a $(l:getting_started/pit_kiln)Pit Kiln$().$(br2)Once it is fired, molten metal can be poured in. Once the metal has cooled enough, it can be extracted.'),
            item_spotlight('tfc:ceramic/ingot_mold{tank:{"Amount":100,"FluidName":"tfc:metal/copper"}}', 'Casting', text_contents='The next few pages show a couple of the knapping patterns for various useful tools.'),
            clay_knapping('tfc:clay_knapping/propick_head_mold', 'A $(l:mechanics/prospecting#propick)Prospector\'s Pick$() is an essential tool for locating large quantities of ore, other than surface prospecting.'),
            clay_knapping('tfc:clay_knapping/pickaxe_head_mold', 'A $(thing)Pickaxe$()! The bread and butter tool for mining.'),
            clay_knapping('tfc:clay_knapping/saw_blade_mold', 'An $(thing)Saw$() is a tool which is required in order to craft many advanced wooden components like a $(thing)Workbench$(), along with many other useful devices like $(l:mechanics/support_beams)Supports$().'),
            clay_knapping('tfc:clay_knapping/scythe_blade_mold', 'A $(thing)Scythe$() is a tool that can harvest plants and leaves in a 3x3x3 area!'),
            clay_knapping('tfc:clay_knapping/chisel_head_mold', 'A $(l:mechanics/chisel)Chisel$() is a tool used for decorating, and creating a large number of decoration blocks.'),
            clay_knapping('tfc:clay_knapping/axe_head_mold', 'An $(thing)Axe$() for all your tree chopping purposes.'),
            clay_knapping('tfc:clay_knapping/hammer_head_mold', 'A $(thing)Hammer$(), which is an essential tool to create $(l:mechanics/anvils)Anvils$(), and work them.'),
            clay_knapping('tfc:clay_knapping/knife_blade_mold', 'A $(thing)Knife$(), which can be used as a weapon, or as a cutting tool for plant type blocks.'),
            clay_knapping('tfc:clay_knapping/hoe_head_mold', 'A $(thing)Hoe$(), used for planting and maintaining $(l:food/crops)Crops$().'),
            clay_knapping('tfc:clay_knapping/shovel_head_mold', 'A $(thing)Shovel$() for all your digging purposes.'),
        )),
        entry('pit_kiln', 'Pit Kilns', 'tfc:textures/block/molten.png', pages=(
            text('A pit kiln is an early game method of $(l:mechanics/heating)heating$() items up. It can be used to $(thing)fire$() clay into ceramic, for example. The pit kiln, over the time period of about eight hours, will heat it\'s contents up to 1600 °C, or $(bold)$(f)$(t:Brilliant White)Brilliant White$().'),
            text('To build a pit kiln, you will need:$(br)$(li)Up to four items to be fired.$(li)Eight pieces of $(thing)Straw$()$(li)Eight $(thing)Logs$()$(li)An item capable of lighting fires, like a $(l:getting_started/firepit#firestarter)Firestarter$(), or a $(thing)Torch$().$(br2)$(bold)Note:$() Torches can start fires simply by tossing the torch on the pit kiln, and waiting a few seconds.'),
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
            text('These small ore pieces can serve two purposes: they can provide a source of metal, and more importantly, they indicate the presence of an underground, close to the surface, larger vein of ore somewhere nearby. Be sure to note where you find small ores, as the location of ore veins will be useful later during $(l:mechanics/prospecting)Prospecting$().$(br2)The twelve types of small ores, and the metal they can be melted into are listed on the next page.'),
            text('$(li)Native Copper ($(thing)Copper$())$(li)Native Gold ($(thing)Gold$())$(li)Hematite ($(thing)Cast Iron$())$(li)Native Silver ($(thing)Silver$())$(li)Cassiterite ($(thing)Tin$())$(li)Bismuthinite ($(thing)Bismuth$())$(li)Garnierite ($(thing)Nickel$())$(li)Malachite ($(thing)Copper$())$(li)Magnetite ($(thing)Cast Iron$())$(li)Limonite ($(thing)Cast Iron$())$(li)Sphalerite ($(thing)Zinc$())$(li)Tetrahedrite ($(thing)Copper$())', title='Small Ores'),
            text('In TerraFirmaCraft, ores each contain a certain number of $(thing)units$(), or $(thing)mB (millibuckets)$() of actual metal which can be extracted. Small ores like this found on the surface are the lowest quality, and only provide $(thing)10 mB$() of metal. In order to extract this metal, it needs to be $(thing)melted$(), and made into tools using a process called $(thing)casting$().', title='Casting').anchor('casting'),
            text('You will need:$(br)$(li)A $(l:getting_started/pottery#vessel)Small Vessel$()$(li)Enough materials for a $(l:getting_started/pit_kiln)Pit Kiln$().$(li)A $(l:getting_started/pottery#mold)Mold$(), or multiple to cast the molten metal.$(li)And finally, you will need at least 100 mB total of a metal which is suitable for casting: $(thing)Copper$(), in one of it\'s three ore forms.$(br2)$(br)$(italic)Note: Casting can also be done with $(l:getting_started/primitive_alloys)Alloys$()'),
            text('First, open the $(thing)Small Vessel$() and put the ores inside. Count up the total amount of metal in the ores carefully! Then, you need to build a $(l:getting_started/pit_kiln)Pit Kiln$(), and this time, put the entire small vessel inside it. As the vessel heats, the ores inside it will melt, and you\'ll be left with a vessel of molten metal.$(br2)Take the vessel out and $(item)$(k:key.use)$() it, to open the $(thing)Casting$() interface.'),
            image('tfc:textures/gui/book/gui/casting.png', text_contents='The Casting Interface.', border=False),
            text('With the casting interface open, place your empty fired mold in the center slot. It will fill up as long as the vessel remains liquid. (If the vessel solidifies, it can be reheated in another pit kiln.) Once the mold is full, it can be removed and left to cool. Once cool, the mold and it\'s contents can be extracted by using the mold, or putting it in the crafting grid.'),
            crafting('tfc:crafting/metal/pickaxe/copper', text_contents='With a tool head in hand, you are now able to craft your first pickaxe! Find enough copper to make a single pickaxe head, cast it using a mold and a few pit kilns, and then slap it on a stick, and voila!'),
        )),
        entry('primitive_alloys', 'Primitive Alloys', 'tfc:ceramic/ingot_mold{tank:{"Amount":100,"FluidName":"tfc:metal/bronze"}}', pages=(
            text('$(thing)Alloys$() are a method of mixing two or more metals together, to create a new, stronger metal. During the early game, while copper is a useful metal for creating tools, the next tier of metal is one of three types of $(thing)Bronze$(). An alloy is made up of component $(thing)metals$(), which must each satisfy a specific percentage of the overall whole.'),
            text('One method through which alloys can be made during the early game is through the usage of a $(thing)Small Vessel$(). The process is very similar to $(l:getting_started/finding_ores#casting)Casting$(). However, instead of using just a single metal, inside the vessel, place enough ore pieces, in the correct ratio, to form a known alloy mix.'),
            text('For example, to create 1000 mB of $(thing)Bronze$() (shown to the right), you would need between 880 and 920 mB of $(thing)Copper$(), and between 80 and 120 mB of $(thing)Tin$().$(br2)The next three pages show the recipes of the three bronzes. Each type of bronze can be used to make tools, armor, and other metal items, although each metal is slightly different, and tools will have different durability, efficiency, or attack damage.'),
            alloy_recipe('Bronze', 'bronze', ''),
            alloy_recipe('Bismuth Bronze', 'bismuth_bronze', ''),
            alloy_recipe('Black Bronze', 'black_bronze', ''),
        )),
        entry('primitive_anvils', 'Primitive Anvils', 'tfc:rock/anvil/granite', pages=(
            text('An alternative to casting tool molds directly in the early game, and a requirement for higher tier metals, is to use an $(thing)Anvil$(). An anvil is a block which can be used for two different processes: $(l:mechanics/anvils#working)Working$(), ad $(l:mechanics/anvils#welding)Welding$(). This chapter is just going to show you how to obtain your first, primitive stone anvil.'),
            text('First, you need to acquire a block of $(thing)Raw Rock$(), that is $(thing)Igneous Intrusive$() (Rhyolite, Basalt, Andesite, or Dacite), or $(thing)Igneous Extrusive$() (Granite, Diorite, or Gabbro). You could find and use an exposed block in the world, or you could $(l:getting_started/primitive_anvils#raw_rock)extract one$() from the surrounding rock.'),
            text('You will also need any material of $(thing)Hammer$(). In order to make the anvil, simply right click the exposed $(thing)top$() face of one of the aforementioned raw rocks with your $(thing)hammer$(), and voila! An anvil will be formed.$(br2)Anvils each have $(l:mechanics/anvils#tiers)tiers$(), and the rock anvil is a Tier 0 - the lowest tier. It is only able to $(l:mechanics/anvils#welding)Weld$() Tier I ingots.', title='Rock Anvil').anchor('stone_anvils'),
            multimultiblock(
                'Converting the center raw rock to an anvil',
                multiblock('', '', False, ((' 0 ',), ('RRR',)), {'0': 'AIR', 'R': 'tfc:rock/raw/gabbro'}),
                multiblock('', '', False, multiblock_id='tfc:rock_anvil'),
            ),
            text('In order to obtain raw rock without it breaking into smaller rocks, it needs to be $(thing)extracted$(). You must mine the blocks on all six sides of a raw rock block - once it is surrounded by air on all sides, it will pop off as a raw rock item which you can then move.', title='Obtaining Raw Rock').anchor('raw_rock'),
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
        entry("leather_making","Leather Making", "minecraft:leather", pages=(
            text("$(thing)Leather$() is a useful item that could be used in the creation of leather armor or used in crafting recipes, but it takes some steps to make it.$(br2)To get started you will first need to obtain hide. This can be done by slaughtering $(l:the_world/wild_animals)animals$().$(br2)Each $(l:the_world/wild_animals)animal$() can drop small, medium or large hide depending on it's size."),
            item_spotlight("tfc:powder/flux",link_recipe=True,text_contents="Next step is obtaining $(l:mechanics/flux)Flux$().$(br)After you are done obtaining $(l:mechanics/flux)Flux$() you will need to craft a $(l:mechanics/barrels)Barrel$() and fill it with water using a $(thing)Bucket$().$(br)For a full $(l:mechanics/barrels)Barrel$() of water you will need 20 $(l:mechanics/flux)Flux$().$(br)Place the $(l:mechanics/flux)Flux$() inside the $(l:mechanics/barrels)Barrel$() and seal it, as you do, the water should transform to limewater."),
            item_spotlight("tfc:small_soaked_hide,tfc:medium_soaked_hide,tfc:large_soaked_hide",link_recipe=True,title="Soaked Hide",text_contents="After you have a $(l:mechanics/barrels)Barrel$() full of limewater, proceed to place the $(thing)Raw Hide$() inside and seal it, letting it soak. You will need to be patient and wait but after some time you should get a $(thing)Soaked Hide$(). Then you can proceed to the next step. If you have trouble check out the $(l:mechanics/barrels)Barrel entry$()."),
            image(*['tfc:textures/gui/book/tutorial/soaked_hide_%d.png' % i for i in range(1, 1 + 4)],title="Creating Scraped Hide",border=True,text_contents="Right click with a $(thing)Soaked Hide$() on a sideways placed log. Then keep right clicking with a knife until it all changes color.$(br)"),
            item_spotlight("tfc:small_scraped_hide,tfc:medium_scraped_hide,tfc:large_scraped_hide",link_recipe=True,title="Scraped Hide",text_contents="After you will be done scraping the $(thing)Soaked Hide$() on sideways placed log. It can finally be removed from the log.$(br2)Next step in the hide processing is placing the $(thing)Scraped Hide$() inside a $(l:mechanics/barrels)Barrel$() full of water and letting it sit there until it will turn to $(thing)Prepared Hide$()."),
            item_spotlight("tfc:small_prepared_hide,tfc:medium_prepared_hide,tfc:large_prepared_hide",link_recipe=True,title="Prepared Hide",text_contents="After you will be done obtaining $(thing)Prepared Hide$() you will need to collect certain logs that can be used in the creation of $(l:mechanics/barrels)tannin$(), these logs are:$(br)$(li)Oak, Birch or Chestnut $(br)$(li)Douglas Fir ,Hickory ,Maple or Sequoia"),
            item_spotlight("minecraft:leather",link_recipe=True,title="Leather creation",text_contents="First create a $(l:mechanics/barrels)Barrel$() full of Tannin, to do that place the specific logs you collected inside the $(l:mechanics/barrels)Barrel$(), seal it and wait. For a full $(l:mechanics/barrels)Barrel$() you will need 10 logs.$(br)When you will have a barrel full of Tannin then simply place $(thing)Prepared Hide$() inside, seal it. After some time you should get $(thing)Leather$()."),
            leather_knapping('tfc:leather_knapping/saddle', "More information about $(l:mechanics/animal_husbandry)Sadles$() can be found $(l:mechanics/animal_husbandry)Here$()."),
            crafting("tfc:crafting/bellows",text_contents="More information about $(l:mechanics/bellows)Bellows$() can be found $(l:mechanics/bellows)here$()."),
            empty_last_page(),
        )),
        entry('building_materials', 'Building Materials', 'tfc:wattle/unstained', pages=(
            text('In the early stages of the game building can be a challenge as many sturdy building blocks require metal tools to obtain. However, there are a couple useful building blocks that can be accessed just with stone tools.'),
            text('$(br)  1. $(l:getting_started/building_materials#mud_bricks)Mud Bricks$()$(br)  2. $(l:getting_started/building_materials#wattle_and_daub)Wattle and Daub$()$(br)  3. $(l:getting_started/building_materials#alabaster)Alabaster$()', title='Contents'),
            # Mud Bricks
            crafting('tfc:crafting/soil/loam_drying_bricks', text_contents='$(thing)Mud$() can be found on the ground, underneath rivers and lakes, or in patches in low elevation swampy environments. With a little bit of $(thing)Straw$(), it can be crafted into some $()Wet Mud Bricks$().', title='Mud Bricks').anchor('mud_bricks'),
            multimultiblock(
                'These can be placed on the ground in a dry location, and after a day they will harden into $(thing)Mud Bricks$().',
                two_tall_block_spotlight('', '', 'tfc:grass/loam', 'tfc:drying_bricks/loam[count=4,dried=false]'),
                two_tall_block_spotlight('', '', 'tfc:grass/loam', 'tfc:drying_bricks/loam[count=4,dried=true]'),
            ),
            crafting('tfc:crafting/soil/loam_mud_bricks', text_contents='These dried mud bricks can then be crafted into $(thing)Mud Brick Blocks$(). They can additionally be made into $(thing)Stairs$(), $(thing)Slabs$(), or $(thing)Walls$(), if so desired.'),
            block_spotlight('', 'All different varieties of mud bricks', '#tfc:mud_bricks'),
            # Wattle and Daub
            text('$(thing)Wattle$() is a versatile building block. On its own, it is transparent, and players and mobs can walk through it. However, with the addition of $(thing)Sticks$() and $(thing)Daub$(), it becomes something much more..', 'Wattle and Daub').anchor('wattle_and_daub'),
            crafting('tfc:crafting/wattle', text_contents='The crafting recipe for Wattle.'),
            crafting('tfc:crafting/daub', 'tfc:crafting/daub_from_mud', text_contents='Wattle\'s companion is daub.'),
            image('tfc:textures/gui/book/tutorial/wattle_weave.png', text_contents='Holding at least four $(thing)Sticks$() in your hand, press $(item)$(k:key.use)$() to $(thing)weave$() it. This makes the block solid.'),
            text('At any time, sticks can be added on each diagonal, and the top and bottom. Hold a single $(thing)Stick$() in your hand and $(item)$(k:key.use)$() it to add a stick. Change what part of the wattle you\'re adding the stick to by selecting a different side of the face..'),
            image('tfc:textures/gui/book/tutorial/wattle_add_stick.png', text_contents='Adding sticks to wattle.'),
            image('tfc:textures/gui/book/tutorial/wattle_add_daub.png', text_contents='Woven Wattle can be made into $(thing)Unstained Wattle$() by adding $(thing)Daub$() with $(item)$(k:key.use)$().'),
            image('tfc:textures/gui/book/tutorial/wattle_stained.png', text_contents='Unstained Wattle can be dyed into $(thing)Stained Wattle$() by adding any $(thing)Dye$() with $(item)$(k:key.use)$().'),
            # Alabaster
            text('Alabaster is a building block made from $(l:the_world/ores_and_minerals#gypsum)Gypsum$(). To make it, seal 100mB of $(thing)Limewater$() in a barrel with gypsum. After an hour, one $(thing)Raw Alabaster$() will be created.', title='Alabaster').anchor('alabaster'),
            crafting('tfc:crafting/alabaster_brick', 'tfc:crafting/alabaster_bricks', title='Alabaster Bricks', text_contents='Alabaster bricks are also made from gypsum.'),
            text('Alabaster can be dyed in a $(l:mechanics/barrels)Barrel$() of dye into any color. Raw Alabaster blocks can also be $(l:mechanics/chisel)chiseled$() into $(thing)Polished Alabaster$() using the $(thing)Smooth() chisel mode.'),
            crafting('tfc:crafting/alabaster/magenta_polished_stairs', text_contents='Polished Alabaster and Alabaster Bricks can be crafted or $(l:mechanics/chisel)chiseled$() into stairs, walls, and slabs.', title='Alabaster Decorations'),
        )),
        entry('a_place_to_sleep', 'A Place to Sleep', 'tfc:medium_raw_hide', pages=(
            text('The $(thing)Thatch Bed$() is a primitive bed which can be used to set your spawn, although not to sleep through the night. To make a thatch bed, place two $(thing)Thatch$() blocks adjacent to each other. Then, right click with a $(thing)Large Raw Hide$(). Large hides are dropped by larger animals, like $(thing)bears$() and $(thing)cows$().'),
            multiblock('Thatch Bed', 'A constructed thatch bed.', False, mapping={'0': 'tfc:thatch_bed[part=head,facing=west]', 'D': 'tfc:thatch_bed[part=foot,facing=east]'}, pattern=((' D ', ' 0 '),)),
        )),
    ))


    book.category('mechanics', 'Advanced Mechanics', 'Advanced sections of the tech tree, from the first pickaxe, all the way to colored steel.', 'tfc:metal/axe/red_steel', entries=(
        entry('animal_husbandry', 'Animal Husbandry', 'minecraft:egg', pages=(
            text('$(thing)Livestock$() are animals that can be tamed and bred by the player. Livestock can be either $(thing)male$() or $(thing)female$(). For some animals, it is possible to tell their sex visually. For example, male pigs have tusks.'),
            text('Livestock experience $(thing)aging$(). They are born as babies, which are smaller and cannot provide things for the player. After a certain amount of days, they grow into $(thing)adult$() animals, which are able to do things like breed or produce milk. After they breed or are used enough times, animals become $(thing)old$(), and are only useful for their meat.'),
            image('tfc:textures/gui/book/tutorial/old_cow.png', text_contents='This bull is old and cannot breed, so it has faded coat and grey, unseeing eyes.'),
            text('Livestock can be fed to raise $(thing)familiarity$(). Each animal has a set of food that it prefers to eat. To feed an animal, $(item)$(k:key.sneak)$() and $(item)$(k:key.use)$() with food in your hand.'),
            image('tfc:textures/gui/book/tutorial/unfamiliarized_pig.png', text_contents='Livestock have a $(thing)Familiarity Indicator$() that shows how familiar they are with you. It is visible when holding $(item)$(k:key.sneak)$() and looking at the animal.'),
            image('tfc:textures/gui/book/tutorial/no_familiarity_decay_pig.png', text_contents='Familiarity decays each day by a little, unless it reaches a certain amount, indicated by the heart being outlined in white.'),
            image('tfc:textures/gui/book/tutorial/familiarity_limit_pig.png', text_contents='Adult livestock cannot be familiarized above a certain amount, indicated by the red outlined heart. Babies can reach up to 100% familiarity.'),
            text('$(thing)Mammals$() are livestock that experience $(thing)Pregnancy$(). Adult mammals that are above 30% familiarity and have been fed that day will mate, given that they are of opposite genders and near each other. The female animal will become $(thing)pregnant$(), which causes it to have children a set number of days after fertilization.$(br)An example is $(l:mechanics/animal_husbandry#pig)Pigs$().').anchor('mammals'),
            text('$(thing)Wooly Animals$() are $(l:mechanics/animal_husbandry#mammals)Mammals$() that can be $(thing)Sheared$() if they are adults and familiar enough to you. Some examples are $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#alpaca)Alpacas$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$().').anchor('wooly_animals'),
            text('$(thing)Dairy Animals$() are mammals that make $(thing)Milk$(). Female dairy animals can be clicked with a bucket to obtain milk. Some examples are $(l:mechanics/animal_husbandry#goat)Goats$(), $(l:mechanics/animal_husbandry#cow)Cows$(), and $(l:mechanics/animal_husbandry#yak)Yaks$().').anchor('dairy_animals'),
            crafting('tfc:crafting/nest_box', title='Nest Box', text_contents='$(thing)Oviparous Animals$() are not $(l:mechanics/animal_husbandry#mammals)Mammals$(), and instead produce children by laying $(thing)Eggs$(). They need a $(thing)Nest Box$() to lay eggs, which they are capable of locating on their own.$(br)Some examples are $(l:mechanics/animal_husbandry#duck)Ducks$(), $(l:mechanics/animal_husbandry#quail)Quails$(), and $(l:mechanics/animal_husbandry#chicken)Chickens$().').anchor('oviparous_animals'),
            heat_recipe('tfc:heating/cooked_egg', '$(thing)Eggs$() can be cooked or boiled for food. Male oviparous animals can fertilize females, which causes the eggs in the nest box to be fertilized. The eggs will say how long until they are ready to hatch.'),
            leather_knapping('tfc:leather_knapping/saddle', '$(thing)Horses$() are $(l:mechanics/animal_husbandry#mammals)Mammals$() that can be ridden when tamed. Horses become rideable after reaching 15% familiarity.').anchor('horses'),
            text(' They need a $(thing)Saddle$() to ride, which can be $(thing)Knapped$(). This includes $(l:mechanics/animal_husbandry#mule)Mules$(), $(l:mechanics/animal_husbandry#donkey)Donkeys$(), and $(l:mechanics/animal_husbandry#Horses)Horses$().$(br2)The next few pages will go over the different types of domesticatable animals.'),
            page_break(),
            text('$(thing)Pigs$() spawn in mild forests of $(l:the_world/climate#temperature)temperature$() between -10 and 35°C, and $(l:the_world/climate#rainfall)rainfall$() of at least 200mm. They are $(l:mechanics/animal_husbandry#mammals)Mammals$() with no special abilities. They will eat any food, even if it is rotten. They have 1-10 children, are pregnant for just 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Pigs').anchor('pig'),
            entity('tfc:pig', 'A pig.', '', scale=1),
            text('$(thing)Cows$() spawn in most climates, between $(l:the_world/climate#temperature)temperature$() -10 and 35°C, and with a $(l:the_world/climate#rainfall)rainfall$() of at least 250mm. They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They only eat $(thing)grains$(), which may be rotten. They can have 1-2 children, are pregnant for 58 days, and reach adulthood in 192 days. They can have children 13 times, if they are never milked, or be milked 128 times, if they are never bred. They produce milk every day.', title='Cows').anchor('cow'),
            entity('tfc:cow', 'A cow.', '', scale=1),
            text('$(thing)Goats$() spawn in moderate climates, $(l:the_world/climate#temperature)temperature$() between -12 and 25°C, with a $(l:the_world/climate#rainfall)rainfall$() of at least 300mm. They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), and $(thing)vegetables$(), which may be rotten. They can have 1-2 children, are pregnant for 32 days, and reach adulthood in 96 days. They can have children 6 times if they are never milked, or be milked 60 times if they are never bred. They produce milk every 3 days.', title='Goats').anchor('goat'),
            entity('tfc:goat', 'A goat.', '', scale=1),
            text('$(thing)Yaks$() spawn in cold climates, with $(l:the_world/climate#temperature)temperature$() of at most -11°C and at least 100mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#dairy_animals)Dairy Animals$(). They eat only fresh $(thing)grains$(). They always have 1 child, are pregnant for 64 days, and reach adulthood in 180 days. They can have children 23 times, if they are never milked, or be milked 230 times, if they are never bred. They produce milk once a day.', title='Yak').anchor('yak'),
            entity('tfc:yak', 'A yak.', '', scale=1),
            text('$(thing)Alpacas$() spawn in colder climates, $(l:the_world/climate#temperature)temperature$() between -8 and 20°C, with at least 250mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1-2 children, are pregnant for 36 days, and reach adulthood in 98 days. They can have children 13 times, if they are never sheared, or be sheared 128 times, if they are never bred. They grow wool every 6 days.', title='Alpaca').anchor('alpaca'),
            entity('tfc:alpaca', 'An alpaca.', '', scale=1),
            text('$(thing)Sheep$() spawn in drier climates, $(l:the_world/climate#temperature)temperature$() between 0 and 35°C, and 70 and 300mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$(). They have 1-2 children, are pregnant for 32 days, and reach adulthood in 56 days. They can have children 6 times, if they are never sheared, or be sheared 60 times, if they are never bred. They grow wool every 9 days.', title='Sheep').anchor('sheep'),
            entity('tfc:sheep', 'A sheep.', '', scale=1),
            text('$(thing)Musk Oxen$() spawn in moderate climates, $(l:the_world/climate#temperature)temperature$() between 0 and 25°C, and at least 100mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#wooly_animals)Wooly Animals$(). They eat $(thing)grains$(). They always have 1 child, are pregnant for 64 days, and reach adulthood in 168 days. They can have children 16 times, if they are never sheared.', title='Musk Ox').anchor('musk_ox'),
            entity('tfc:musk_ox', 'A musk ox.', '', scale=1),
            text('$(thing)Chickens$() spawn in warm forests, with $(l:the_world/climate#temperature)temperature$() of at least 14°C, and 225mm of $(l:the_world/climate#rainfall)rainfall$(). They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), and $(thing)seeds$(), which can be rotten. Their eggs hatch in 8 days, and become adults in 24 days. They can lay eggs 100 times. They produce eggs every 30 hours.', title='Chickens').anchor('chicken'),
            entity('tfc:chicken', 'A chicken.', '', scale=1),
            text('$(thing)Ducks$() spawn in most plains, $(l:the_world/climate#temperature)temperature$() between -25 and 30°C, with $(l:the_world/climate#rainfall)rainfall$() of at least 100mm. They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), $(thing)bread$(), and $(thing)seeds$(). Their eggs hatch in 8 days, and become adults in 32 days. They can lay eggs 72 times. They produce eggs every 32 hours.', title='Ducks').anchor('duck'),
            entity('tfc:duck', 'A duck.', '', scale=1),
            text('$(thing)Quails$() spawn in colder climates, $(l:the_world/climate#temperature)temperature$() between -15 and 15°C, with a $(l:the_world/climate#rainfall)rainfall$() of at least 200mm. They are $(l:mechanics/animal_husbandry#oviparous_animals)Oviparous Animals$(). They eat $(thing)grains$(), $(thing)fruits$(), $(thing)vegetables$(), and $(thing)seeds$(), which can be rotten. Their eggs hatch in 8 days, and become adults in 22 days. They can lay eggs 48 times. They produce eggs every 28 hours.', title='Quail').anchor('quail'),
            entity('tfc:quail', 'A quail.', '', scale=1),
            text('$(thing)Donkeys$() spawn in wetter plains, with a $(l:the_world/climate#temperature)temperature$() of at least -15°C, and a $(l:the_world/climate#rainfall)rainfall$() of between 130 and 400mm. They are a kind of $(l:mechanics/animal_husbandry#horses)Horse() which can have a $(thing)chest$(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1 child, are pregnant for 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Donkeys').anchor('donkey'),
            entity('tfc:donkey', 'A donkey.', '', scale=1),
            text('$(thing)Mules$() spawn in plains with a $(l:the_world/climate#temperature)temperature$() of at least -15°C, and a $(l:the_world/climate#rainfall)rainfall$() of between 130 and 400mm. They are a kind of $(l:mechanics/animal_husbandry#horses)Horse() that can wear a chest, which are the always-male product of a $(thing)horse$() and a $()donkey$(). They can have $(thing)grains$() and $(thing)fruits$(). They reach adulthood in 80 days.', title='Mules').anchor('mule'),
            entity('tfc:mule', 'A mule.', '', scale=1),
            text('$(thing)Horses$() spawn in plains with a $(l:the_world/climate#temperature)temperature$() of at least -15°C, and a $(l:the_world/climate#rainfall)rainfall$() of between 130 and 400mm. They are a kind of $(l:mechanics/animal_husbandry#horses)Horse(). They eat $(thing)grains$() and $(thing)fruits$(). They have 1 child, are pregnant for 19 days, and reach adulthood in 80 days. They can have children 6 times.', title='Horses').anchor('horse'),
            entity('tfc:horse', 'A horse.', '', scale=1),
        )),
        entry('panning', 'Panning', 'tfc:pan/empty', pages=(
            text('$(thing)Panning$() is a method of obtaining small pieces of certain native ores by searching in rivers and other waterways.$(br2)Panning makes use of $(l:the_world/waterways#ore_deposits)Ore Deposits$() which are found in gravel patches in the bottom of lakes and rivers.$(br2)In order to get started panning, you will need a empty pan.').link('#tfc:ore_deposits'),
            clay_knapping('tfc:clay_knapping/pan', 'Clay can be $(l:getting_started/pottery)knapped$() into a pan as shown above.'),
            heat_recipe('tfc:heating/ceramic_pan', 'Once the pan has been $(thing)knapped$(), it needs to be $(l:mechanics/heating)fired$() to create a $(thing)Ceramic Pan$().$(br2)The next thing you will need to find is some sort of $(thing)Ore Deposit$(). Ore deposits can come in several different ores: Native Copper, Native Silver, Native Gold, and Cassiterite.'),
            block_spotlight('Example', 'A native gold deposit in some slate.', 'tfc:deposit/native_gold/slate'),
            text('Then, you can begin panning!$(br2)$(bold)1.$() With the pan in hand, $(thing)use$() it on the ore deposit block.$(br2)$(bold)2.$() While standing in water with the pan in your hand, hold down $(item)$(k:key.use)$(), and you will start panning.$(br2)$(bold)3.$() After a few moments, if you are lucky, you may be rewarded with a small piece of ore in your inventory.'),
            image('tfc:textures/gui/book/tutorial/panning.png'),
        )),
        entry('heating', 'Heating', 'tfc:firestarter', pages=(
            text('Heating items is a way of converting one item to another, or an item to a fluid. Items can be heated in many ways - in a $(l:firepit)Firepit$(), a $(l:getting_started/pit_kiln)Pit Kiln$(), or a $(l:getting_started/charcoal_forge)Charcoal Forge$(), to name a few. However they all function in the same way. When you place items inside these devices, the items will gradually start to heat up. This is visible on the item\'s tooltip.'),
            text('The temperature of an item is represented by a color, which will change through the following values:$(br2)$(7)$(bold)Warming$(): 1 - 80 °C$(br)$(7)$(bold)Hot$(): 80 - 210 °C$(br)$(7)$(bold)Very Hot$(): 210 - 480 °C$(br)$(4)$(bold)Faint Red$(): 480 - 580 °C$(br)$(bold)$(4)Dark Red$(): 580 - 730 °C$(br)$(c)$(bold)Bright Red$(): 730 - 930 °C$(br)$(6)$(bold)Orange$(): 930 - 1100 °C$(br)$(e)$(bold)$(t:Yellow)Yellow$(): 1100 - 1300 °C$(br)$(e)$(t:Yellow White)$(bold)Yellow White$(): 1300 - 1400 °C$(br)$(f)$(bold)$(t:White)White$(): 1400 - 1500 °C$(br)$(f)$(bold)$(t:Brilliant White)Brilliant White$(): >1500 °C'),
        )),
        entry('charcoal_forge', 'Charcoal Forge', 'tfc:textures/block/devices/charcoal_forge/lit_static.png', pages=(
            text('The $(thing)Charcoal Forge$() is a device used to $(l:mechanics/heating)heat$() and melt items. Forges are necessary to make a $(l:mechanics/crucible)crucible$() work. They are typically used to heat items to prepare them for $(l:mechanics/anvils)anvils$(). $(br)It is constructed with 5 $(thing)stone$() blocks surrounding a $(l:mechanics/charcoal_pit#charcoal_pile)charcoal pile$() of 7 or 8 layers, which is then lit.'),
            multiblock('A Charcoal Forge', 'A complete forge multiblock, ready to be lit.', True, multiblock_id='tfc:charcoal_forge'),
            text('Forges need to be well-ventilated to work. Either they should be able to see the sky directly, or be connected horizontally to a block that can, as in the photo. Forges can reach higher temperatures with a $(l:mechanics/bellows)bellows$(), placed horizontally from the open block above the forge. Chimneys are useful, because forges will go out in the rain'),
            image('tfc:textures/gui/book/tutorial/forge_chimney.png', text_contents='A forge with a valid chimney setup.'),
            image('tfc:textures/gui/book/gui/charcoal_forge.png', text_contents='The forge\'s interface.', border=False),
            text('The five slots at the bottom of the forge are used for fuel. While the forge is running, it periodically consumes fuel, which can be $(thing)Charcoal$() or $(l:the_world/ores_and_minerals#bituminous_coal)Coal$(). The top five slots will heat items, up to the temperature shown in the indicator on the left. The right side slots are for containers that can contain liquid metal, like $(thing)Vessels$(). Items that melt in the forge will fill those containers.'),
        )),
        entry('charcoal_pit', 'Charcoal Pit', 'minecraft:charcoal', pages=(
            text('The $(thing)Charcoal Pit$() is a way of obtaining $(thing)Charcoal$(). Charcoal pits are made with $(thing)Log Piles$(). To place a log pile, $(item)$(k:key.use)$() and $(item)$(k:key.sneak)$() while holding a $(thing)Log$(). More logs can be inserted by either pressing $(item)$(k:key.use)$() directly while holding a log, or by pressing $(item)$(k:key.use)$() with something else to open the interface.'),
            block_spotlight('The Log Pile', 'Log piles need a solid block under them to be placed. They are highly flammable.', 'tfc:log_pile'),
            text('The charcoal pit is formed by surrounding log piles with solid, non-flammable blocks. The amount of charcoal produced is proportional to the amount of logs contained inside the log piles. To start the burning process, light one of the log piles, and then cover it. If it worked, you should see $(thing)smoke$() particles rise up from the structure.'),
            multimultiblock('The building of one possible charcoal pit, in layers.', *(
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:log_pile'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:burning_log_pile'}),
                multiblock('', '', False, (('     ', ' XXX ', ' XXX ', ' XXX ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:log_pile'}),
                multiblock('', '', False, (('     ', '     ', '     ', '     ', '     '), ('XXXXX', 'XYYYX', 'XYYYX', 'XYYYX', 'XXXXX'), ('XXXXX', 'XXXXX', 'XX0XX', 'XXXXX', 'XXXXX'),), {'X': 'tfc:dirt/sandy_loam', '0': 'tfc:dirt/sandy_loam', 'Y': 'tfc:charcoal_pile[layers=7]'}),
            )),
            text('After the charcoal pile burns, you will be left with $(thing)Charcoal piles$(). The charcoal pile contains up 8 layers of $(thing)Charcoal$(). Dig it with a shovel to obtain the charcoal items. Charcoal piles can be added to or placed with $(item)$(k:key.use)$().').anchor('charcoal_pile'),
            multimultiblock('The charcoal pile.', *[block_spotlight('', '', 'tfc:charcoal_pile[layers=%s]' % i) for i in range(1, 9)])
        )),
        entry('crucible', 'Crucible', 'tfc:crucible', pages=(
            text('A $(thing)Crucible$() is an advanced device used for the creation of $(l:)Alloys$(). It is a more precise method than using a $(l:getting_started/primitive_alloys)Small Vessel$() to make alloys.$(br2)To obtain a crucible, you will first need to obtain some $(l:mechanics/fire_clay)Fire Clay$(), which is a stronger material than clay. This fire clay can then be knapped to shape it into and $()Unfired Crucible$().'),
            fire_clay_knapping('tfc:fire_clay_knapping/crucible', 'Knapping an $(thing)Unfired Crucible$().'),
            heat_recipe('tfc:heating/crucible', 'After the crucible is knapped, it will need to be $(thing)fired$(), like any piece of pottery - a $(l:getting_started/pit_kiln)Pit Kiln$(), or a $(l:mechanics/charcoal_forge)Charcoal Forge$() would do.$(br2)n order to use the crucible, it needs a source of heat. The crucible can be heated by any heatable block below, for instance a $(l:mechanics/charcoal_forge)Charcoal Forge$()'),
            multiblock('', 'A crucible powered by a charcoal forge below it.', False, (('   ', ' C ', '   '), ('GGG', 'G0G', 'GGG')), {
                'C': 'tfc:crucible',
                '0': 'tfc:charcoal_forge[heat_level=7]',
                'G': 'tfc:rock/bricks/granite'
            }),
            page_break(),
            text('Now, you are ready to use the crucible. When you use it, you will see the $(thing)Crucible Interface$(), shown to the right. The top region shows the current metal content of the crucible. The first metal shown is what would be produced, if it were to be extracted right now. Other metals shown are the makeup of the current alloy in the crucible.', title='Advanced Alloying').anchor('advanced_alloying'),
            image('tfc:textures/gui/book/gui/crucible.png', text_contents='The Crucible Interface', border=False),
            text('The crucible has nine slots where items can be added to be melted, and their liquefied contents will be directly added to the crucible. Molten metal containers such as $(l:getting_started/pottery#mold)Molds$() can also be placed here, and they will be slowly drained into the crucible, allowing for precise control over your alloy\'s content.$(br2)Molds, or other fluid containers can also be placed in the output slot, and will be slowly filled with the current content of the crucible.'),
            text('The temperature indicator on the left will rise based on external sources of heat, such as a $(l:mechanics/charcoal_forge)Charcoal Forge$() below, or heat from a $(l:mechanics/blast_furnace)Blast Furnace$() from above. Metal can be extracted from the crucible as long as it is hot enough.$(br2)Finally, the crucible will keep it\'s content when broken, allowing you to transport the alloy container around if you wish.'),
        )),
        entry('bellows', 'Bellows', 'tfc:bellows', pages=(
            text('A $(thing)Bellows$() is a device which can be used to increase the air flow through other device, which lets them burn at a hotter temperature. However, by burning at a hotter temperature, they will also consume fuel faster. The bellows can provide heat to a device that is directly in front of it, or in front and one block down. This allows it to heat a $(l:getting_started/firepit)Firepit$(), or a $(l:mechanics/charcoal_forge)Charcoal Forge$(), for example.'),
            crafting('tfc:crafting/bellows', title='', text_contents='To use the bellows, simply place it facing the targeted heating device, and use it. The bellows will pump air into the device, raising the maximum temperature for a short time.')
        )),
        entry('grill', 'Firepit And Grill', 'tfc:grill', pages=(
            text('A $(thing)Grill$() is an item that can be added to a firepit to cook foods more efficiently. The grill is able to cook five items at once, and it also gives these items the $(thing)Wood Grilled$() trait when cooking food, which provides a minor buff to the item\'s $(l:food/preservation)expiration date$(). In order to create a firepit with grill, first create a $(l:getting_started/firepit)Firepit$(), then use a $(thing)Wrought Iron Grill$() on the firepit.').link('tfc:wrought_iron_grill'),
            block_spotlight('A Firepit with Grill', '', 'tfc:grill'),
            anvil_recipe('tfc:anvil/wrought_iron_grill', 'The grill is created by working a $(thing)Wrought Iron Double Sheet$() on an $(l:mechanics/anvils)Anvil$().$(br2)On the next page, you can see the grill interface. Like the firepit, it has four slots for fuel which must be added in the top slot, a temperature indicator, but instead, five slots for heating items.'),
            image('tfc:textures/gui/book/gui/grill.png', text_contents='The grill interface.', border=False),
        )),
        entry('pot', 'Firepit And Pot', 'tfc:pot', pages=(
            text('A $(thing)Pot$() is an item that can be added to the firepit, in order to both cook new types of food, and produce some other useful items.$(br2)In order to create a firepit with a pot, first create a $(l:getting_started/firepit)Firepit$(), then use a $(l:mechanics/pot#ceramic_pot)Ceramic Pot$() on the firepit.'),
            block_spotlight('', 'A firepit with a pot attached.', 'tfc:pot'),
            clay_knapping('tfc:clay_knapping/pot', 'A ceramic pot must be $(l:getting_started/pottery)Knapped$() out of clay first.'),
            heat_recipe('tfc:heating/fired_pot', 'It then must be $(l:mechanics/heating)fired$() to create a $(thing)Ceramic Pot$() which can be used on the firepit.'),
            text('Like the firepit, the pot has four slots for fuel which must be added in the top slot, and a temperature indicator. The pot also contains five item slots and holds up to $(thing)1000 mB$() of any fluid.$(br2)In order to cook something in the pot, first the fluid must be added by using any type of fluid container, such as a bucket, on the pot. Then add items, and light the pot. It will start boiling for a while until the recipe is completed.'),
            image('tfc:textures/gui/book/gui/pot.png', text_contents='The pot interface, actively boiling and making a type of soup.', border=False),
            item_spotlight('tfc:food/fruit_soup', 'Soup Recipes', text_contents='Soup is made from 3-5 $(thing)fruits$(), $(thing)vegetables$(), or $(thing)meats$() in a pot of $(thing)water$(). When the recipe is done, the water in the pot will turn red. $(item)$(k:key.use)$() with a $(thing)bowl$() to retrieve it. Soup combines multiple nutrients into a single meal.'),
            item_spotlight('tfc:bucket/red_dye', 'Simple Recipes', text_contents='Other pot recipes transform the items and fluid in the pot into something else. For example, boiling 5 $(thing)ash$() in $(thing)water$() makes $(thing)lye$().')  # todo: better recipe page for the pot
        )),
        entry('chisel', 'Chisel', 'tfc:metal/chisel/wrought_iron', pages=(
            text('Chisels are a tool for creating decorative forms of other blocks, including slabs and stairs. In order to get started, you will need a $(thing)Chisel$() and any type of $(thing)Hammer$(). Chisels must be made from metals on an $(l:mechanics/anvils)Anvil$(). In order to start chiseling, hold the chisel in your main hand, and a hammer in your off hand, and target a block in the world.'),
            text('If you can chisel that block, a $(c)red outline$() of the block to be chiseled will be shown based on what $(thing)Mode$() you have selected.$(br2)The chisel has three modes that can be switched by using $(item)$(k:tfc.key.cycle_chisel_mode)$(): $(thing)Slab$(), $(thing)Stair$(), and $(thing)Smooth$(). An indicator of your chisel mode should show up next to the hotbar.'),
            text('Press $(item)$(k:key.use)$() to convert the block to the shape that was highlighted in red. If you chiseled a slab, the half of the block you chiseled will pop off, and you can grab it. Change the orientation of your player and your mouse to change the orientation of the chiseled block. As a rule of thumb, the orientation of the chiseled block will be the same as if you placed it in that orientation yourself.'),
            image('tfc:textures/gui/book/tutorial/chisel_block.png', 'tfc:textures/gui/book/tutorial/chisel_stair.png', 'tfc:textures/gui/book/tutorial/chisel_slab.png', text_contents='The three chisel modes usable on a $(thing)Raw Limestone$() block.'),
            crafting('tfc:crafting/rock/marble_smooth', text_contents='The chisel can also be used in some crafting recipes as a shortcut for large quantities.'),
            text('Be careful! Chiseling in a mineshaft is not safe. Each time you chisel, there is a chance of $(thing)collapse$().')  # todo: ref gravity
        )),
        entry('support_beams', 'Support Beams', 'tfc:wood/support/oak', pages=(
            text('$(thing)Support Beams$() are a means of preventing block collapses. They are most often used during mining, when rocky blocks are susceptible to falling on your head.', title='Support Beams'),
            crafting('tfc:crafting/wood/oak_support', text_contents='Supports can be crafted with a $(thing)Saw$() and some $(thing)Logs$()'),
            text('Supports can come in horizontal and vertical variations. Placing supports on top of a block places a column of up to three support beams. These vertical supports will be destroyed if they lose their base of support. Horizontal supports are automatically placed by pressing $(item)$(k:key.use)$() on the side of a vertical support beam. If there is a direct path to another vertical support beam, and enough items, the horizontal support will be formed.', title='Supporting Supports'),
            multiblock('Basic Structure', '', False, (('CRD',), ('V V',), ('V0V',)), {'C': 'tfc:wood/vertical_support/oak[south=true]', 'R': 'tfc:wood/horizontal_support/oak[north=true,south=true]', 'D': 'tfc:wood/vertical_support/oak[north=true]', 'V': 'tfc:wood/vertical_support/oak'}),
            text('Only horizontal support beams, the kind that have no \'elbow\' components, can support blocks. The range that block supports is 4 blocks horizontal in each direction, and one block above and below. Blocks in this area are protected from collapsing. However, be warned! Mining outside of this range can easily cause collapses that end up in (and potentially destroy) your safe zone.'),
            empty_last_page(), # todo: in 1.12, we had a support visualization. and also i'm sure more info about gravity, collapses is wanted.
        )),
        entry('prospecting', 'Prospecting', 'tfc:metal/propick/wrought_iron', pages=(
            text('You remembered where you picked up those $(l:getting_started/finding_ores)Small Metal Nuggets$(), right? Finding additional ores may require extensive exploration and mining. You should become very familiar with $(l:the_world/ores_and_minerals)Ores and Minerals$(). If you need a specific resource, you must find the rock type it spawns in, either under your feet or across the world.'),
            text('When picking up small nuggets becomes unsatisfying, it is time to start prospecting to find ore veins:$(br)$(li)Small nuggets occur when ore is nearby, within 15 blocks horizontally and 35 vertically.If you find the center of a group of nuggets, it\'s likely that the vein is beneath you.$(li)Exposed ore can occur in cliffs and water bodies, which may be seen from farther away.'),
            item_spotlight('tfc:metal/propick/copper', 'Prospector\'s Pick', text_contents='If you\'re looking for metal ores or mineral veins (which have no nuggets), and you can\'t find the vein by guessing, it\'s time to pull out the $(thing)Prospector\'s Pick$(). It searches the 25x25x25 area centered on the block clicked, and reports to the action bar the amount and type of ore located.').link(*['tfc:metal/propick/%s' % m for m in TOOL_METALS]).anchor('propick'),
            clay_knapping('tfc:clay_knapping/propick_head_mold', 'To make a Prospector\'s Pick, you can $(l:getting_started/pottery)knapp$() an unfired mold out of clay as shown above.'),
            heat_recipe('tfc:heating/propick_head_mold', 'Once the mold has been $(l:getting_started/pottery)knapped$(), it needs to be $(l:mechanics/heating)fired$() to create a $(thing)Propick Head Mold.$()$(br2)To create the tool head, $(l:getting_started/finding_ores#casting)cast$() liquid metal into the mold.'),
            anvil_recipe('tfc:anvil/wrought_iron_propick_head', 'A Prospector\'s Pick Head can also be $(l:mechanics/anvils#working)smithed$() out of an $(thing)ingot$() of any tool metal on an $(l:mechanics/anvils)Anvil$().$(br2)The Prospector\'s Pick is then created by crafting the tool head with a stick.'),
            text('The Prospector\'s Pick may incorrectly say nothing is there. It will never report finding something when nothing is actually there. Higher tier tools will reduce or eliminate these false negatives.$(br2)A Prospector\'s Pick of the same metal tier will always have the same result when used on the same block unless ores were removed.$(br2)If the Prospector\'s Pick finds multiple ores nearby, it will select only one to report.'),
            text('Right-clicking a Prospector\'s Pick on a block will report finding one of these possible results:$(br)$(li)Nothing (may be false)$(li)Traces$(li)A Small Sample$(li)A Medium Sample$(li)A Large Sample$(li)A Very Large Sample$(br2)Very large samples indicate at least eighty and potentially many more blocks.'),
        )),
        entry('bloomery', 'Bloomery', 'tfc:bloomery', pages=(
            crafting('tfc:crafting/bloomery', text_contents='The $(thing)Bloomery$() is a device used to smelt $(thing)Iron Ore$() into $(thing)Wrought Iron$(). The iron ores are $(l:the_world/ores_and_minerals#hematite)hematite$(), $(l:the_world/ores_and_minerals#limonite)limonite$(), and $(l:the_world/ores_and_minerals#magnetite)magnetite$(). You may notice that these ores seem to melt into $(thing)cast iron$(). This is where the bloomery comes in handy.'),
            multiblock('A Bloomery', 'A minimum size bloomery. The bloomery block can be open and shut with $(item)$(k:key.use)$().', True, multiblock_id='tfc:bloomery'),
            text('The bloomery can contain up to 24 $(thing)iron ore$() and 24 $(l:mechanics/charcoal_pit)charcoal$(), with 8 of each item per additional layer of the chimney. To add layers to the chimney, stack up two more layers of stone blocks.$(br)$(br)To add items to the bloomery, climb up to the top and throw items inside. A tower of grey ore should form.'),
            image('tfc:textures/gui/book/tutorial/bloomery_hole.png', text_contents='Adding items to the bloomery.'),
            text('The bloomery must have an equal amount of charcoal and ore $(thing)items$(). Light the bloomery block, and wait most of a day. When the bloomery block shuts off, it is done. Each 100mB of iron ore that melts in the bloomery is transformed into a $(thing)Bloom Block$(). The bloom block contains $(thing)Raw Iron Blooms$(), which can be obtained by mining the Bloom Block repeatedly with a pickaxe.'),
            block_spotlight('The Bloom Block', 'The bloom block, at full bloom.', 'tfc:bloom[layers=8]'),
            anvil_recipe('tfc:anvil/refined_iron_bloom', 'The $(thing)Raw Iron Bloom$() must be worked in a $(l:mechanics/anvils)anvil$() to make $(thing)Refined Iron Bloom$().'),
            anvil_recipe('tfc:anvil/wrought_iron_from_bloom', 'The $(thing)Refined Iron Bloom$() must be worked in a $(l:mechanics/anvils)anvil$() to make $(thing)Wrought Iron Ingots$().'),
            text('$(li)If the bloomery finds itself with more items contained than it can handle based on its chimney, it will try to spit them out the front.$()$(li)To retrieve your items from a bloomery that is not lit, do not break the molten block tower. Break the bloomery block.$()$(li)Blooms will only melt into cast iron, not wrought iron. They must be worked!$()', 'Smith\'s Notes'),
            text('$(li)The bloomery cares about the quantity of items thrown into it, not the quality. Using rich ore in the bloomery consumes the same amount of charcoal as poor ore!$()$(li)Molten blocks will readily spread fire to their environment! Be careful.$()'),
        )),
        entry('blast_furnace', 'Blast Furnace', 'tfc:blast_furnace', pages=(
            text('A $(thing)Blast Furnace$() is an advanced device which is used in the creation of $(thing)Steel$(). By mixing $(thing)Iron Ores$(), $(thing)Charcoal$(), and $(thing)Flux$() in a controlled, hot environment, you can create a stronger metal than cast or wrought iron.$(br2)To obtain a blast furnace, you will first need a $(l:mechanics/crucible)Crucible$(), and a lot of $(thing)Wrought Iron Sheets$().'),
            crafting('tfc:crafting/blast_furnace', text_contents='Crafting a blast furnace itself requires a $(thing)Crucible$(), along with some of the $(thing)Wrought Iron Sheets$() you will need.'),
            text('You will then need to construct the blast furnace, along with it\'s $(thing)Chimney$(). The chimney must be composed out of $(l:mechanics/fire_clay#fire_bricks)Fire Bricks$(), as they are strong enough to withstand the intense heat. It must then be lined with $(thing)Wrought Iron Sheets$(), for extra reinforcement. Stronger metals such as $(thing)Steel$() can also be used for the sheets, if desired.'),
            multiblock('A Blast Furnace', 'A blast furnace with a minimum height chimney.', True, multiblock_id='tfc:blast_furnace'),
            text('The blast furnace\'s chimney can be up to five layers - each layer requiring four $(thing)Fire Bricks$(), and twelve $(thing)Wrought Iron Sheets$() to complete. Having more layers increases the total capacity of the blast furnace, allowing it to smelt more steel at a time. Each chimney layer, up to a maximum of five, allows the blast furnace to hold four ore items at a time.'),
            text('In order to use the blast furnace, you must drop items in the top of the chimney - for steel production, you must add an equal number of items of $(thing)Iron Ores$(), and $(l:mechanics/flux)Flux$(). Any iron ores, or items that are able to melt into $(thing)Cast Iron$() will do. You will also need to add $(l:mechanics/charcoal_pit)Charcoal$(), which will be consumed as the blast furnace works.'),
            page_break(),
            text('Using the blast furnace will open the blast furnace interface, seen to the right. In this interface, you will see meters for both the ore, and the fuel content of the blast furnace. The top right slot must have a $(thing)Tuyere$(), which is a metal pipe used to funnel air into the blast furnace, required to reach the hottest temperatures to smelt steel. A tuyere can be crafted with an $(l:mechanics/anvils)Anvil$().'),
            image('tfc:textures/gui/book/gui/blast_furnace.png', text_contents='The Blast Furnace Interface', border=False),
            text('You will also need a $(l:mechanics/bellows)Bellows$() in order for the Blast Furnace to reach it\'s maximum temperature. This can be placed on any of the four sides of the blast furnace.'),
            multiblock('', 'A full size blast furnace, with bellows and crucible attached.', True, multiblock_id='tfc:full_blast_furnace'),
            text('Finally, to get started, light the blast furnace with a $(l:getting_started/firepit#firestarter)Fire Starter$(), or a $(thing)Flint and Steel$(). It will begin to heat the ores inside. Make sure that the blast furnace has enough fuel, and that it\'s internal temperature is at a maximum, and after a while, it\'s contents will liquify and convert into $(l:mechanics/steel)Pig Iron$().'),
            text('This liquid metal will drip into any fluid container placed immediately below the blast furnace, such as a $(l:mechanics/crucible)Crucible$(). This can then be collected from the output slot of the crucible and then worked into $(l:mechanics/steel)Steel$().'),
        )),
        entry('steel', 'Steel', 'tfc:metal/ingot/steel', pages=(
            text('Steel is an advanced material which can be used to create tools and armor, and comes in a few different varieties: $(thing)Steel$(), $(thing)Black Steel$(), $(thing)Red Steel$(), and $(thing)Blue Steel$().$(br2)In order to create steel, you must first create $(thing)Pig Iron$() in a $(l:mechanics/blast_furnace)Blast Furnace$(), and cast it into $(thing)Ingots$().'),
            anvil_recipe('tfc:anvil/high_carbon_steel_ingot', 'A $(thing)Pig Iron Ingot$() can then be worked in an anvil to create a $(thing)High Carbon Steel$() ingot, which can be worked again to create a $(thing)Steel Ingot$()'),
            page_break(),
            text('$(thing)Black Steel$() is an advanced form of steel formed from an alloy of steel, along with some other metals. In order to create it, you will need to create an alloy called $(thing)Weak Steel$() in a $(l:mechanics/crucible)Crucible$().', title='Black Steel').anchor('black_steel'),
            alloy_recipe('Weak Steel', 'weak_steel', 'Molten $(thing)Weak Steel$() can then be cast into ingots.'),
            welding_recipe('tfc:welding/high_carbon_black_steel_ingot', '$(thing)Weak Steel Ingots$() can then be welded with $(thing)Pig Iron Ingots$() to create $(thing)High Carbon Black Steel Ingots$(). Finally, these can then be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Black Steel Ingots$().'),
            text('$(thing)Black Steel$() can be used to craft tools and armor, and also is used as a key ingredient in the creation of $(l:mechanics/steel#blue_steel)Blue Steel$() and $(l:mechanics/steel#red_steel)Red Steel$().'),
            page_break(),
            text('$(thing)Blue Steel$() is one of the two highest forms of steel able to be created, along with $(l:mechanics/steel#red_steel)Red Steel$(). Similar to $(l:mechanics/steel#black_steel)Black Steel$(), the first step is to create an alloy of $(thing)Weak Blue Steel$() $(l:mechanics/crucible)Crucible$().', title='Blue Steel').anchor('blue_steel'),
            alloy_recipe('Weak Blue Steel', 'weak_blue_steel', ''),
            welding_recipe('tfc:welding/high_carbon_blue_steel_ingot', '$(thing)Weak Blue Steel Ingots$() can then be welded with $(l:mechanics/steel#black_steel)Black Steel Ingots$() to create $(thing)High Carbon Blue Steel Ingots$(). Finally, these can then be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Blue Steel Ingots$().'),
            text('$(thing)Blue Steel$() is one of the two strongest tier metals in TerraFirmaCraft, along with $(l:mechanics/steel#red_steel)Red Steel$(). It can be used to create tools and armor, and a $(thing)Blue Steel Bucket$(). The blue steel bucket is a powerful bucket able of transporting source blocks of lava and molten metals. Blue steel is also used as a crafting ingredient in one of the most rare after items in the game... a $(thing)Bucket$().'),
            page_break(),
            text('$(thing)Red Steel$() is one of the two highest forms of steel able to be created, along with $(l:mechanics/steel#blue_steel)Blue Steel$(). Similar to $(l:mechanics/steel#black_steel)Black Steel$(), the first step is to create an alloy of $(thing)Weak Red Steel$() $(l:mechanics/crucible)Crucible$().', title='Red Steel').anchor('red_steel'),
            alloy_recipe('Weak Red Steel', 'weak_red_steel', ''),
            welding_recipe('tfc:welding/high_carbon_red_steel_ingot', '$(thing)Weak Red Steel Ingots$() can then be welded with $(l:mechanics/steel#black_steel)Black Steel Ingots$() to create $(thing)High Carbon Red Steel Ingots$(). Finally, these can then be worked on an $(l:mechanics/anvils)Anvil$() to create $(thing)Red Steel Ingots$().'),
            text('$(thing)Red Steel$() is one of the two strongest tier metals in TerraFirmaCraft, along with $(l:mechanics/steel#blue_steel)Blue Steel$(). It can be used to create tools and armor, and a $(thing)Red Steel Bucket$(). The red steel bucket is a powerful bucket able of transporting source blocks of water and other fluids. Red steel is also used as a crafting ingredient in one of the most rare after items in the game... a $(thing)Bucket$().'),
        )),
        entry('anvils', 'Anvils', 'tfc:metal/anvil/copper', pages=(
            text('Anvils are a important tool required for metalworking, as they allow you to work and weld metal ingots into various different forms.$(br2)Anvils can be useful for both $(l:mechanics/anvils#working)Working$(), which is used to form one piece of metal into another, or $(l:mechanics/anvils#welding)Welding$(), which is used to fuse two metal items into one solid piece.'),
            block_spotlight('', 'All types of metal anvils.', '#tfc:anvils'),
            crafting('tfc:crafting/metal/anvil/copper', text_contents='Anvils can be crafted with $(thing)Double Ingots$() of their respective metal. For your first anvil, you must $(l:mechanics/anvils#welding)weld$() double ingots first on a $(l:getting_started/primitive_anvils)Stone Anvil$().'),
            text('Anvils each have a $(thing)Tier$(), which defines what types of material they can work and weld. An anvil can work metals of it\'s current tier, and it can weld metals that are one tier higher.$(br)$(li)$(bold)Tier 0$(): Stone Anvils$(li)$(bold)Tier I$(): Copper$(li)$(bold)Tier II$(): Bismuth Bronze, Black Bronze, Bronze$(li)$(bold)Tier III$(): Wrought Iron$(li)$(bold)Tier IV$(): Steel$(li)$(bold)Tier V$(): Black Steel$(li)$(bold)Tier VI$(): Red Steel, Blue Steel').anchor('tiers'),
            page_break(),
            text('In order to work an item on the anvil, you will need to use the anvil, to open up the anvil interface, seen to the right. On the left, there are two input slots for items - for working, the target item must be in the right hand slot. You will also need a hammer while working, either in the hammer slot, on the right of the anvil $(bold)or$(), in your main hand. The hammer will gradually take damage as you work the item.', title='Working').anchor('working'),
            image('tfc:textures/gui/book/gui/anvil_empty.png', text_contents='The anvil interface.', border=False),
            text('You will then need to select the $(thing)Plan$(), which chooses which item you want to create. $(item)$(k:key.attack)$() on the the scroll button, and then pick one of the items to create. The anvil interface will return, but now you will have selected a plan - the scroll will show the item you are working to create, and the $(thing)Rules$(), and $(thing)Target$() will now be populated.'),
            image('tfc:textures/gui/book/gui/anvil_in_use.png', text_contents='After selecting to create a pickaxe.', border=False),
            text('In the middle of the anvil screen, there is a bar with two colored indicators. The $(2)green$() pointer, is your current working progress. The $(4)red$() pointer, is the target. Your goal is to line up the current progress, with the target.$(br2)In order do this, you can use the $(2)green$() and $(4)red$() action buttons, which move your current progress a certain amount, depending on the action taken.', title='Targets'),
            text('$(2)Green$() actions will always move your target $(bold)right$(), and $(4)Red$() actions will always move your progress $(bold)left$(). Note that if you move your target off of the progress bar, you will have overworked your item - you will lose the ingot. However, while working, you must also obey the $(thing)rules$()...'),
            text('The $(thing)rules$(), are the two or three icons shown on the top of the anvil interface. They represent specific actions that must be taken, at specific times, in order for your working to be a success. For example, you have have to $(2)Bend Second Last$(), meaning the second last action you take $(bold)must$(), be a $(2)Bend$() action.', title='Working Rules'),
            text('Your last three actions are shown right underneath your the rules, for convenience. When a rule is satisfied, it\'s outline will change to green.$(br2)Finally, you have to be mindful of your item\'s $(l:mechanics/heating)temperature$(). Metals can only be worked when they are above a certain temperature, when the tooltip will show "Can Work". You may take an item out and re-heat it during the working process.'),
            text('Working can be tedious, and take many steps to get correct. However, there is a reward for being efficient. Some items, such as tool heads, when they are worked in a low or minimal amount of steps, receive a Forging Bonus, based on how efficiently they were forged. This bonus can then be applied to tools that the item is used in, for example, a pickaxe head used to make a pickaxe.', title='Forging Bonuses'),
            item_spotlight('tfc:metal/pickaxe/wrought_iron{"tfc:forging_bonus":4}', 'Perfectly Forged', False, 'There are four tiers of forging bonus:$(li)Poorly Forged$(li)Well Forged$(li)Expertly Forged$(li)Perfectly Forged$(br2)These bonuses increase the power of your tool - making it break less often, mine faster, or do more damage in combat, depending on the tool.'),
            page_break(),
            text('Welding is a process through which two items are fused together to create a new item. Welding works the same as it does on a $(l:getting_started/primitive_anvils)Stone Anvil$(), on a metal anvil.$(br2)First, you must place the two items you want to weld, on the anvil. You can do this either by using the items on the anvil, or by opening the anvil interface, and inserting them in the two leftmost slots.', title='Welding').anchor('welding'),
            text('You also need to place one $(l:mechanics/flux)Flux$() in the anvil, to aid the welding process. Then, while both items are $(l:mechanics/heating)hot enough$() to weld - the tooltip will say "Can Weld" - you must use any $(thing)Hammer$() on the anvil. You will hear a hammering sound, and the items will be welded together. They can then be extracted by using $(item)$(k:key.use)$() on the anvil with an empty hand.'),
        )),
        entry('fire_clay', 'Fire Clay', 'tfc:fire_clay', pages=(
            text('The list of uses of fire clay is small, but all of them are important. Fire clay is a stronger variant of clay, that has better heat resistance. It is used to make things that have to get very hot!'),
            crafting('tfc:crafting/fire_clay', text_contents='Fire clay is made from the remnants of $(l:the_world/ores_and_minerals#kaolinite)kaolinite$() and $(l:the_world/ores_and_minerals#graphite)graphite$() crushed in a $(l:mechanics/quern)quern$().'),
            fire_clay_knapping('tfc:fire_clay_knapping/crucible', 'The $(l:mechanics/crucible)Crucible$() in its unfired state is made from fire clay.').anchor('crucible'),
            fire_clay_knapping('tfc:fire_clay_knapping/brick', 'The $(l:mechanics/blast_furnace)Blast Furnace$() only accepts fire bricks as insulation.').anchor('fire_bricks')
        )),
        entry('quern', 'Quern', 'tfc:quern', pages=(
            text('The $(thing)Quern$() is a device for grinding items. It can make powder, dye, and some other items. It is assembled from a $(thing)Base$() and $(thing)Handstone$().'),
            crafting('tfc:crafting/quern', text_contents='The base of the quern can be crafted with three $(thing)smooth stone$() and three of any other $(thing)Stone$() blocks.'),
            crafting('tfc:crafting/handstone', text_contents='The quern needs a $(thing)Handstone$() to operate.'),
            image('tfc:textures/gui/book/tutorial/quern_empty.png', text_contents='Select the quern at the top of the block and $(item)$(k:key.use)$() to place the handstone.'),
            image('tfc:textures/gui/book/tutorial/quern_add_item.png', text_contents='Select the top of the handstone and $(item)$(k:key.use)$() to add an item to it.'),
            image('tfc:textures/gui/book/tutorial/quern_handle.png', text_contents='Select the handle and $(item)$(k:key.use)$() to spin the handstone.'),
            image('tfc:textures/gui/book/tutorial/quern_result.png', text_contents='The output should place on the base of the quern. $(item)$(k:key.use)$() anywhere on the base to retrieve it.'),
            quern_recipe('tfc:quern/sulfur', 'The quern is used to make various $(thing)Powders$() from ores, like $(thing)Sulfur$().'),
            quern_recipe('tfc:quern/plant/black_orchid', '$(thing)Dye$() can be obtained from various flowers.'),
            quern_recipe('tfc:quern/emerald', '$(l:the_world/waterways#gemstones)Gems$() can also be ground into powder.'),
            quern_recipe('tfc:quern/barley_grain', '$(thing)Flour$() is also obtainable from the quern.'),
            quern_recipe('tfc:quern/fluxstone', '$(l:mechanics/flux)Flux$() is also obtainable from the quern.'),
        )),
        entry('fishing', 'Fishing', 'tfc:metal/fishing_rod/copper', pages=(
            text('$(thing)Fishing$() is a way of hunting the fish entities that swim around in rivers, lakes, and oceans. Fishing rods must be baited and cast. Fish entities attempt to eat the bait, and sometimes succeed. Reeling in a fish takes work, and becomes easier with higher level hooks.'),
            anvil_recipe('tfc:anvil/bismuth_bronze_fish_hook', 'First, you have to forge a fishing hook in an $(l:mechanics/anvils)Anvil$().'),
            crafting('tfc:crafting/metal/fishing_rod/bismuth_bronze', text_contents='The fishing rod is crafted with a fishing hook.', title='Fishing Rod'),
            text('Fishing rods are not useful without bait. Bait can be added to rods in a crafting table. To catch normal fish, you need $(thing)Seeds$() or $(thing)Shellfish$(). To catch larger fish, such as $(thing)Dolphins$() and $(thing)Orcas$(), you need $(item)cod$(), $(item)salmon$(), $(item)tropical fish$(), or $(item)bluegills$().'),
            text('To release the bobber, $(item)$(k:key.use)$(). Wait for a fish to come and grab it. Then, $(item)$(k:key.use)$() to pull it in. As you do that, the meter on your hotbar will fill up. Pull too quickly, and the fish will get away with the bait. Each time you fish, the fish has a chance of eating the bait. To catch the fish, pull it up on land and kill it with a tool. You can hold the fishing rod in your off hand to make this easier.'),
            image('tfc:textures/gui/book/tutorial/fishing.png', text_contents='The fishing bar replaces the experience bar when active.'),
        )),
        entry('fertilizers', 'Fertilizers', 'tfc:powder/sylvite', pages=(
            text('Fertilizers are used to add nutrients to $(l:food/crops)crops$(). $(item)$(k:key.use)$() with a fertilizer in your hand on some $(thing)Farmland$() or a $(thing)Crop$() to add the nutrients. Some particles should appear.', title='Fertilization'),
            crafting('tfc:crafting/composter', text_contents='The composter is an essential tool for making fertilizer. It needs 4 $(2)green$() and 4 $(4)brown$() items to work. To add an item to it, $(item)$(k:key.use)$().'),
            text('Green items include $(2)plants$(), $(2)fruits$(), $(2)vegetables$() and $(2)grains$(). Brown items include $(4)humus$(), $(4)paper$(), $(4)dead grass$(), $(4)driftwood$(), and $(4)pinecones$(). When compost is ready, the composter will begin to visually smoke.'),
            item_spotlight('tfc:rotten_compost', text_contents='Some items will $(c)poison$() your compost. These include $(c)meat$() and $(c)bones$(). Poison compost, when used on a crop, instantly kills it.'),
            fertilizer('tfc:compost', 'Compost is the product of the composter.', 0.4, 0.2, 0.4),
            fertilizer('minecraft:bone_meal', 'Bonemeal is made of crushed bones.', p=0.1),
            fertilizer('tfc:powder/saltpeter', 'Saltpeter is made from its ore.', n=0.1, k=0.4),
            fertilizer('tfc:groundcover/guano', 'Guano is found deep underground and on gravelly shores.', 0.8, 0.5, 0.1),
            fertilizer('tfc:powder/wood_ash', 'Wood ash is made from broken firepits.', p=0.1, k=0.3),
            fertilizer('tfc:powder/sylvite', 'Sylvite is made from its ore.', p=0.5)
        )),
        entry('flux', 'Flux', 'tfc:powder/flux', pages=(
            text('Flux is a powder which is required for $(l:mechanics/anvils#welding)Welding$(), and is also used as a catalyst in a $(l:mechanics/blast_furnace)Blast Furnace$(). Flux can be obtained by grinding specific items in a $(l:mechanics/quern)Quern$().$(br2)Flux can be obtained in a number of ways, one of which is from it\'s native ore, $(l:the_world/ores_and_minerals#borax)Borax$().'),
            quern_recipe('tfc:quern/fluxstone', 'Some rocks - $(thing)Limestone$(), $(thing)Dolomite$(), $(thing)Chalk$(), or $(thing)Marble$() - can also be used as flux, after being ground in a $(l:mechanics/quern)Quern$(). Other items, including $(thing)Scutes$(), $(thing)Clams$(), $(thing)Mollusks$(), and the edible remains of $(l:the_world/wild_animals#shellfish)Shellfish$(), can all also be used to create flux.'),
        )),
        entry('lamps', 'Lamps and Candles', 'tfc:metal/lamp/bismuth_bronze', pages=(
            # add a text page at the start
            two_tall_block_spotlight('Lamps', 'Lamps are a long term source of light. They burn liquid fuel.', 'tfc:metal/lamp/copper[hanging=true,lit=true]', 'tfc:metal/chain/copper[axis=y]'),
            text('Using a bucket, $(item)$(k:key.use)$() on a lamp to add fuel to it. It can then be lit with a $(thing)firestarter$() or anything capable of lighting fires. Lamps retain their fuel content when broken.'),
            quern_recipe('tfc:quern/olive', 'One lamp fuel is $(thing)Olive Oil$(). The first step in its production is to make olive paste.'),
            crafting('tfc:crafting/jute_net', text_contents='You will also need a jute net.'),
            text('Seal the $(thing)Olive Paste$() with $(thing)Water$() in a $(l:mechanics/barrels)Barrel$() to make $(thing)Olive Oil Water$(). Seal that in with your $(thing)Jute Net$() to produce $(thing)Olive Oil$(). Olive oil burns for 8 in-game hours for every unit of fluid.'),
            text('Another lamp fuel is $(thing)Tallow$(). To make it, cook 5 $(thing)Blubber$(), in a $(l:mechanics/pot)Pot$() of water. Tallow burns for less than 2 in-game hours per unit.').anchor('tallow'),
            block_spotlight('Candles', text_content='The candle is made from sealing $(thing)String$() in a bucket of $(thing)Tallow$().', block='tfc:candle[candles=3,lit=true]'),
            block_spotlight('Lava Lamps', text_content='Lava will keep burning forever, but can only be held in a $(l:mechanics/steel#blue_steel)Blue Steel$() lamp.', block='tfc:metal/lamp/blue_steel[lit=true]'),
            anvil_recipe('tfc:anvil/black_steel_chain', '$(thing)Chains$() are a great way to hang your lamps, and can be smithed in an $(l:mechanics/anvils)Anvil$().'),
            empty_last_page(),
        )),
        entry('barrels', 'Barrels', 'tfc:wood/barrel/palm', pages=(
            text('The $(thing)Barrel$() is a device that can hold both items and fluids. The central slot is used to hold items. Fluids are shown in the tank on the left side, and can be added to the barrel by placing a filled $(thing)bucket$() or $(thing)jug$() in the top left slot. They can be removed by placing an empty fluid container in the same slot. Using $(item)$(k:key.use)$() on the block with a bucket also works.'),
            image('tfc:textures/gui/book/gui/barrel.png', text_contents='The barrel interface.', border=False),
            text('Barrels can be $(thing)Sealed$(). Sealing allows the barrel to be broken while keeping its contents stored. It also allows for the execution of some recipes. In the interface, sealing can be toggled with the grey button on the right side. Pressing $(item)$(k:key.use)$() while holding $(item)$(k:key.sneak)$() with an empty hand also toggles the seal.'),
            text('$(li)Barrels can be filled by clicking an empty one on a fluid block in the world.$()$(li)Barrels will slowly fill up with water in the rain$()$(li)Icicles melting above barrels also adds water to them$()$(li)Sealing a barrel will eject items that are not in the center slot.$()', 'Barrel Tips'),
            text('Barrels are mostly known for their ability to execute $(thing)Barrel Recipe$(). The simplest kind of barrel recipe is an $(thing)Instant Recipe$(). Barrel recipes are based on ratios of fluids to items. For example, 500mB of water is needed for each $(l:mechanics/flux)flux$() item to create 500mB of $(thing)Limewater$(). Since making Limewater is an instant recipe, having at least enough flux to convert all the present water will instantly create limewater.'),
            text('Some barrel recipes require the barrel to be $(thing)Sealed$() for a period of time. For example, sealing $(thing)Barley Flour$() in 500mB of water for three days will make $(thing)Beer$().'),
            text('A couple barrel recipes operate by mixing two fluids at a certain ratio. This is done by taking a filled bucket of one of the ingredients, and putting it in the fluid addition slot of a barrel that has the required amount of the other fluid. This is done for making $(thing)Milk Vinegar$(), where $(thing)Vinegar$() is added to $(thing)Milk$() at a 9:1 ratio. Vinegar is also added in the same ratio to $(thing)Salt Water$() to make $(thing)Brine$().'),
            text('Barrels have the ability to cool $(l:mechanics/heating)hot$() items. Put a hot item in a barrel of $(thing)Water$(), $(thing)Olive Oil$(), or $(thing)Salt Water$(), and it will quickly bring its temperature lower.'),
            text('Barrels have the ability to $(thing)Dye$() and $(thing)Bleach$() items. Dye fluids are made by boiling a single dye item in a $(l:mechanics/pot)Pot$(). Most color-able things, like carpet, candles, and $(l:getting_started/building_materials#alabaster)Alabaster$() can be dyed by sealing them in a barrel of dye. Dyed items can also be bleached by sealing them in a barrel of $(thing)Lye$(). Lye is made by boiling $(thing)Wood Ash$(), a product of breaking $(l:getting_started/firepit)Firepits$(), in a $(l:mechanics/pot)Pot$() of Water.'),
            text('Barrels can preserve items in $(thing)Vinegar$(). Vinegar is made by sealing $(thing)Fruit$() in a barrel of $(thing)Alcohol$(). For information on how that process works, see the relevant $(l:food/decay#vinegar)page$().')
        )),
    ))
    # todo: merge into mechanics + getting started? keeps us at three nice categories: world gen, tutorial, and "everything else"
    book.category('food', 'Food', 'How to find, harvest, and cook food.', 'tfc:food/wheat', entries=(
        entry('decay', 'Preservation', 'minecraft:rotten_flesh', pages=(
            text('In TerraFirmaCraft, no food will last forever! Food will $(thing)expire$() over time, turning rotten. Rotten food will not restore any hunger, and has the potential to give you unhelpful effects such as $(thing)Hunger$() or $(thing)Poison$()!$(br2)Fortunately, there are a number of ways to make your food last longer by $(thing)Preserving$() it.'),
            text('When you hover over a piece of food, you will see a tooltip which shows how long the food has until it will rot. It might look something like:$(br2)$(bold)$(2)Expires on: 5:30 July 5, 1000 (in 5 day(s))$()$(br2)By using various preservation mechanics, that date can be extended, giving you more time before your food roots.'),
            text('One of the easiest ways to preserve food, is to use a $(thing)Vessel$(). $(thing)Large Vessels$() are a block which can store up to nine items, and when $(thing)sealed$(), the items inside will gain the $(5)$(bold)Preserved$() status, which extends their remaining lifetime by 2x.$(br2)$(thing)Small Vessels$() are a item which can store up to four other items, and will also apply the $(5)$(bold)Preserved$() status to their contents.', title='Vessels'),
            block_spotlight('', 'A Sealed Large Vessel.', 'tfc:ceramic/large_vessel[sealed=true]'),
            text('One other way to preserve certain types of food easily, is to cook it. $(thing)Meats$() will all expire slower when they are cooked than when they are raw.$(br2)It is also important to use the correct device for cooking. Certain devices that heat very hot, such as a $(l:mechanics/charcoal_forge)Charcoal Forge$() or a $(l:mechanics/crucible)Crucible$() are $(bold)bad$() for cooking food, which will make them expire faster!', title='Cooking'),
            heat_recipe('tfc:heating/mutton', 'Instead, a $(l:getting_started/firepit)Firepit$(), or a $(l:mechanics/grill)Grill$() can even provide a buff for using it! For example, cooking mutton (pictured above) in a $(thing)Firepit$() will increase it\'s lifetime by 1.33x, and cooking in a $(thing)Grill$() will increase it\'s lifetime by 1.66x!'),
            text('$(thing)Salting$() is a way to make meat last longer. To salt meat, it must be crafted with $(thing)Salt$() in a crafting grid. Only uncooked, raw meat can be salted. Cooking or otherwise changing meat does not take away the salted properly.', title='Salting').anchor('salting'),
            quern_recipe('tfc:quern/salt', 'One way of getting salt is through $(l:mechanics/quern)grinding$() $(l:the_world/ores_and_minerals#halite)Halite$(), which is a mineral.'),
            block_spotlight('Salt Licks', 'Salt can be found naturally in forests. It can be placed and picked back up.', 'tfc:groundcover/salt_lick'),
            text('$(thing)Vinegar Preservation$() is a way of making fruits, veggies, and meat last longer.$(br2)$(thing)Vinegar$() is made in a $(l:mechanics/barrel)Barrel$(), by sealing a fruit with 250mB of Alcohol. To preserve food in vinegar it must first be $(thing)Pickled$() in $(thing)Brine$(). Brine is made in a Barrel 1 part $(thing)Vinegar$() with 9 parts $(thing)Salt Water$().').anchor('vinegar'),
            text('Once food is pickled, it can be sealed in a Barrel of Vinegar. If there is 125mB of Vinegar per pickled food item, the food will last longer.'),
            empty_last_page(),
        )),
        entry('hydration', 'Keeping Hydrated', 'tfc:wooden_bucket{fluid:{"Amount":100,"FluidName":"minecraft:water"}}', pages=(
            text('One challenge when farming is keeping your crops hydrated. Based on the $(l:the_world/climate#rainfall)Rainfall$() in the area, the ground may have some latent moisture, however this may not be enough, especially for particularly water-intensive crops.$(br2)In order to see the hydration of any specific block, you must have a $(thing)Hoe$() in hand.'),
            text('Then, simply look at any $(thing)Farmland$() block, or any crop which require hydration. You will see a tooltip which shows the current hydration as a percentage from 0% to 100%.$(br2)Hydration cannot be decreased except by moving to a area with less rainfall - however, it can be increased by the proximity to nearby $(thing)water$() blocks, much like Vanilla farmland.'),
        )),
        entry('crops', 'Crops', 'tfc:food/wheat', pages=(
            text('Crops are a source of food and some other materials. While each crop is slightly different, crops all have some similar principles. In order to start growing crops, you will need some $(thing)Seeds$(), which can be obtained by searching for $(l:the_world/wild_crops)Wild Crops$(), and breaking them.$(br2)Once you have obtained seeds, you will also need a $(thing)Hoe$(). Seeds are also useful as $(l:mechanics/fishing)bait$().'),
            rock_knapping_typical('tfc:rock_knapping/hoe_head_%s', 'In the stone age, a Hoe can be $(thing)knapped$() as seen above.'),
            crafting('tfc:crafting/stone/hoe_sedimentary', text_contents='Once the hoe head is knapped, it can be crafted into a Hoe.$(br2)Hoes function as in Vanilla, by right clicking dirt blocks to turn them into $(thing)Farmland$(). They can also be used to create $(thing)Path Blocks$(), and convert $(thing)Rooted Dirt$() into $(thing)Dirt$().'),
            text('All crops need to be planted on farmland in order to grow. Some crops have additional requirements such as being waterlogged, or requiring a stick to grow on.'),
            text('Crops do not need $(thing)nutrients$() to grow, but they certainly help. There are three nutrients: $(b)Nitrogen$(), $(6)Phosphorous$(), and $(d)Potassium$(). Each crop has a favorite nutrient.'),
            text('Consuming its favorite nutrient causes a crop to grow faster, and improves the yield of the crop at harvest time. That means that crops that consumed more nutrients drop more food when broken! Consuming a nutrient also has the effect of replenishing the other nutrients around it a small amount.'),
            # Listing of all crops, their growth conditions, and how to grow them
            page_break(),
            text(f'{detail_crop("barley")}Barley is a single block crop. Barley seeds can be planted on farmland to be grown, and will produce $(thing)Barley$() and $(thing)Barley Seeds$() as a product.', title='Barley').link('tfc:seeds/barley').link('tfc:food/barley').anchor('barley'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/barley[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("oat")}Oat is a single block crop. Oat seeds can be planted on farmland to be grown, and will produce $(thing)Oat$() and $(thing)Oat Seeds$() as a product.', title='Oat').link('tfc:seeds/oat').link('tfc:food/oat').anchor('oat'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/oat[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("rye")}Rye is a single block crop. Rye seeds can be planted on farmland to be grown, and will produce $(thing)Rye$() and $(thing)Rye Seeds$() as a product.', title='Rye').link('tfc:seeds/rye').link('tfc:food/rye').anchor('rye'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/rye[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("maize")}Maize is a two block tall crop. Maize seeds can be planted on farmland to be grown, will grow two blocks tall, and will produce $(thing)Maize$() and $(thing)Maize Seeds$() as a product.', title='Maize').link('tfc:seeds/maize').link('tfc:food/maize').anchor('maize'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/maize[age=%d,part=top]' % i if i >= 3 else 'minecraft:air',
                'Y': 'tfc:crop/maize[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(6)]),
            text(f'{detail_crop("wheat")}Wheat is a single block crop. Wheat seeds can be planted on farmland to be grown, and will produce $(thing)Wheat$() and $(thing)Wheat Seeds$() as a product.', title='Wheat').link('tfc:seeds/wheat').link('tfc:food/wheat').anchor('wheat'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/wheat[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("rice")}Rice is a single block crop. Rice must be grown underwater - it must be planted on farmland, in freshwater that is a single block deep. It will produce $(thing)Rice$() and $(thing)Rice Seeds$() as a product.', title='Rice').link('tfc:seeds/rice').link('tfc:food/rice').anchor('rice'),
            multimultiblock(
                'Note: in order to grow, the rice block must be $(thing)Waterlogged$().',
                *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/rice[age=%d,fluid=water]' % i) for i in range(8)],
            ),
            text(f'{detail_crop("beet")}Beets area a single block crop. Beet seeds can be planted on farmland to be grown, and will produce $(thing)Beet$() and $(thing)Beet Seeds$() as a product.', title='Beet').link('tfc:seeds/beet').link('tfc:food/beet').anchor('beet'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/beet[age=%d]' % i) for i in range(6)]),
            text(f'{detail_crop("cabbage")}Cabbage is a single block crop. Cabbage seeds can be planted on farmland to be grown, and will produce $(thing)Cabbage$() and $(thing)Cabbage Seeds$() as a product.', title='Cabbage').link('tfc:seeds/cabbage').link('tfc:food/cabbage').anchor('cabbage'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/cabbage[age=%d]' % i) for i in range(6)]),
            text(f'{detail_crop("carrot")}Carrot is a single block crop. Carrot seeds can be planted on farmland to be grown, and will produce $(thing)Carrot$() and $(thing)Carrot Seeds$() as a product.', title='Carrot').link('tfc:seeds/carrot').link('tfc:food/carrot').anchor('carrot'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/carrot[age=%d]' % i) for i in range(5)]),
            text(f'{detail_crop("garlic")}Garlic is a single block crop. Garlic seeds can be planted on farmland to be grown, and will produce $(thing)Garlic$() and $(thing)Garlic Seeds$() as a product.', title='Garlic').link('tfc:seeds/garlic').link('tfc:food/garlic').anchor('garlic'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/garlic[age=%d]' % i) for i in range(5)]),
            text(f'{detail_crop("green_bean")}Green Beans is a climbing two block tall crop. Green Bean seeds can be planted on farmland to be grown, will grow two blocks tall if a stick is present, and will produce $(thing)Green Beans$() and $(thing)Green Bean Seeds$() as a product.', title='Green Beans').link('tfc:seeds/green_bean').link('tfc:food/green_bean').anchor('green_bean'),
            multimultiblock('The stick is required in order for the crop to fully grow.', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/green_bean[age=%d,part=top,stick=true]' % i,
                'Y': 'tfc:crop/green_bean[age=%d,part=bottom,stick=true]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("potato")}Potatoes are a single block crop. Potato seeds can be planted on farmland to be grown, and will produce $(thing)Potatoes$() and $(thing)Potato Seeds$() as a product.', title='Potatoes').link('tfc:seeds/potato').link('tfc:food/potato').anchor('potato'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/potato[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("pumpkin")}Pumpkins are a spreading crop. Pumpkin seeds can be planted on farmland to be grown, and will place up to two $(thing)Pumpkin Blocks$() on the ground next to it while it is mature. If the pumpkin blocks are harvested, if the pumpkin plant matures again, it can grow more pumpkins.', title='Pumpkins').link('tfc:seeds/pumpkin').anchor('pumpkin'),
            multimultiblock('', *[multiblock('', '', False, pattern=(('   ', ' CP', '   '), ('GGG', 'G0G', 'GGG')), mapping={'G': 'tfc:farmland/loam', '0': 'tfc:farmland/loam', 'C': 'tfc:crop/pumpkin[age=%d]' % i, 'P': 'minecraft:air' if i != 7 else 'tfc:pumpkin'}) for i in range(8)]),
            text(f'{detail_crop("melon")}Melons are a spreading crop. Melon seeds can be planted on farmland to be grown, and will place up to two $(thing)Melon Blocks$() on the ground next to it while it is mature. If the melon blocks are harvested, if the pumpkin plant matures again, it can grow more melon.', title='Melons').link('tfc:seeds/melon').anchor('melon'),
            multimultiblock('', *[multiblock('', '', False, pattern=(('   ', ' CP', '   '), ('GGG', 'G0G', 'GGG')), mapping={'G': 'tfc:farmland/loam', '0': 'tfc:farmland/loam', 'C': 'tfc:crop/melon[age=%d]' % i, 'P': 'minecraft:air' if i != 7 else 'tfc:melon'}) for i in range(8)]),
            text(f'{detail_crop("onion")}Onions are a single block crop. Onion seeds can be planted on farmland to be grown, and will produce $(thing)Onions$() and $(thing)Onion Seeds$() as a product.', title='Onions').link('tfc:seeds/onion').link('tfc:food/onion').anchor('onion'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/onion[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("soybean")}Soybean is a single block crop. Soybean seeds can be planted on farmland to be grown, and will produce $(thing)Soybean$() and $(thing)Soybean Seeds$() as a product.', title='Soybean').link('tfc:seeds/soybean').link('tfc:food/soybean').anchor('soybean'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/soybean[age=%d]' % i) for i in range(7)]),
            text(f'{detail_crop("squash")}Squash is a single block crop. Squash seeds can be planted on farmland to be grown, and will produce $(thing)Squash$() and $(thing)Squash Seeds$() as a product.', title='Squash').link('tfc:seeds/squash').link('tfc:food/squash').anchor('squash'),
            multimultiblock('', *[two_tall_block_spotlight('', '', 'tfc:farmland/loam', 'tfc:crop/squash[age=%d]' % i) for i in range(8)]),
            text(f'{detail_crop("sugarcane")}Sugarcane is a two block tall crop. Sugarcane seeds can be planted on farmland to be grown, will grow two blocks tall, and will produce $(thing)Sugarcane$() and $(thing)Sugarcane Seeds$() as a product. Sugarcane can be used to make $(thing)Sugar$().', title='Sugarcane').link('tfc:seeds/sugarcane').link('tfc:food/sugarcane').anchor('sugarcane'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/sugarcane[age=%d,part=top]' % i if i >= 4 else 'minecraft:air',
                'Y': 'tfc:crop/sugarcane[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("tomato")}Tomatoes are a climbing two block tall crop. Tomato seeds can be planted on farmland to be grown, will grow two blocks tall if a stick is present, and will produce $(thing)Tomatoes$() and $(thing)Tomato Seeds$() as a product.', title='Tomatoes').link('tfc:seeds/tomato').link('tfc:food/tomato').anchor('tomatoes'),
            multimultiblock('The stick is required in order for the crop to fully grow.', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/tomato[age=%d,part=top,stick=true]' % i,
                'Y': 'tfc:crop/tomato[age=%d,part=bottom,stick=true]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(8)]),
            text(f'{detail_crop("jute")}Jute is a two block tall crop. Jute seeds can be planted on farmland to be grown, will grow two blocks tall, and will produce $(thing)Jute$() and $(thing)Jute Seeds$() as a product.', title='Jute').link('tfc:seeds/jute').link('tfc:jute').anchor('jute'),
            multimultiblock('', *[multiblock('', '', False, (('X',), ('Y',), ('Z',), ('0',)), {
                'X': 'tfc:crop/jute[age=%d,part=top]' % i if i >= 3 else 'minecraft:air',
                'Y': 'tfc:crop/jute[age=%d,part=bottom]' % i,
                'Z': 'tfc:farmland/loam',
            }) for i in range(6)]),
        )),
    ))


def detail_crop(crop: str) -> str:
    data = CROPS[crop]
    return '$(bold)$(l:the_world/climate#temperature)Temperature$(): %d - %d °C$(br)$(bold)$(l:food/hydration)Hydration$(): %d - %d %%$(br)$(bold)Nutrient$(): %s$(br2)' % (data.min_temp, data.max_temp, data.min_hydration, data.max_hydration, data.nutrient.title())


if __name__ == '__main__':
    main()

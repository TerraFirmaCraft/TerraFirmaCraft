from typing import NamedTuple, Tuple, List, Mapping

from mcresources import ResourceManager, utils
from mcresources.type_definitions import JsonObject, ResourceLocation

from i18n import I18n
from constants import ROCK_CATEGORIES, ALLOYS, lang

import re


class Component(NamedTuple):
    type: str
    x: int
    y: int
    data: JsonObject


class Page(NamedTuple):
    type: str
    data: JsonObject
    custom: bool  # If this page is a custom template.
    anchor_id: str | None  # Anchor for referencing from other pages
    link_ids: List[str]  # Items that are linked to this page
    translation_keys: Tuple[str, ...]  # Keys into 'data' that need to be passed through the Translation

    def anchor(self, anchor_id: str) -> 'Page':
        return Page(self.type, self.data, self.custom, anchor_id, self.link_ids, self.translation_keys)

    def link(self, *link_ids: str) -> 'Page':
        for link_id in link_ids:
            if link_id.startswith('#'):  # Patchouli format for linking tags
                link_id = 'tag:' + link_id[1:]
            self.link_ids.append(link_id)
        return self

    def translate(self, i18n: I18n):
        for key in self.translation_keys:
            if key in self.data and self.data[key] is not None:
                self.data[key] = i18n.translate(self.data[key])


class Entry(NamedTuple):
    entry_id: str
    name: str
    icon: str
    pages: Tuple[Page]
    advancement: str | None


class Book:

    def __init__(self, rm: ResourceManager, root_name: str, macros: JsonObject, i18n: I18n, local_instance: bool):
        self.rm: ResourceManager = rm
        self.root_name = root_name
        self.category_count = 0
        self.i18n = i18n
        self.local_instance = local_instance

        if self.i18n.lang == 'en_us':  # Only generate the book.json if we're in the root language
            rm.data(('patchouli_books', self.root_name, 'book'), {
                'name': 'tfc.field_guide.book_name',
                'landing_text': 'tfc.field_guide.book_landing_text',
                'subtitle': '${version}',
                # Even though we don't use the book item, we still need patchy to make a book item for us, as it controls the title
                # If neither we nor patchy make a book item, this will show up as 'Air'. So we make one to allow the title to work properly.
                'dont_generate_book': False,
                'show_progress': False,
                'macros': macros
            })

    def template(self, template_id: str, *components: Component):
        self.rm.data(('patchouli_books', self.root_name, 'en_us', 'templates', template_id), {
            'components': [{
                'type': c.type, 'x': c.x, 'y': c.y, **c.data
            } for c in components]
        })

    def category(self, category_id: str, name: str, description: str, icon: str, parent: str | None = None, is_sorted: bool = False, entries: Tuple[Entry, ...] = ()):
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
        self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'categories', category_id), {
            'name': self.i18n.translate(name),
            'description': self.i18n.translate(description),
            'icon': icon,
            'parent': parent,
            'sortnum': self.category_count
        })
        self.category_count += 1

        category_res: ResourceLocation = utils.resource_location(self.rm.domain, category_id)

        assert not isinstance(entries, Entry), 'One entry in singleton entries, did you forget a comma after entry(), ?\n  at: %s' % str(entries)
        for i, e in enumerate(entries):
            assert not isinstance(e.pages, Page), 'One entry in singleton pages, did you forget a comma after page(), ?\n  at: %s' % str(e.pages)

            # Iterate through and assert that all page boundaries are enforced
            pages = []
            allow_empty_last_page = False

            for j, p in enumerate(e.pages):
                if p.type == 'PAGE_BREAK':
                    assert len(pages) % 2 == 0, 'A page_break() required that the next entry must start on a new page, a page has been added that breaks this!\n  at: entry \'%s\', page break at index %d' % (str(e.name), j)
                elif p.type == 'EMPTY_LAST_PAGE':
                    allow_empty_last_page = True
                    assert j == len(e.pages) - 1, 'An empty_last_page() was used but it was not the last page?\n  at: %s' % str(e.name)
                else:
                    pages.append(p)

            assert allow_empty_last_page or len(pages) % 2 == 0, 'An entry has an odd number of pages: this leaves a implicit empty() page at the end.\nIf this is intentional, add an empty_last_page() as the last page in this entry!\n  at: entry \'%s\'' % str(e.name)

            extra_recipe_mappings = {}
            for index, p in enumerate(e.pages):
                for link in p.link_ids:
                    extra_recipe_mappings[link] = index
            if not extra_recipe_mappings:  # Exclude if there's nothing here
                extra_recipe_mappings = None

            # Validate no duplicate anchors or links
            seen_anchors = set()
            seen_links = set()
            for p in e.pages:
                if p.anchor_id:
                    assert p.anchor_id not in seen_anchors, 'Duplicate anchor "%s" on page %s' % (p.anchor_id, p)
                    seen_anchors.add(p.anchor_id)
                for link in p.link_ids:
                    assert link not in seen_links, 'Duplicate link "%s" on page %s' % (link, p)
                    seen_links.add(link)

            # Separately translate each page
            entry_name = self.i18n.translate(e.name)
            for p in pages:
                p.translate(self.i18n)

            self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'entries', category_res.path, e.entry_id), {
                'name': entry_name,
                'category': self.prefix(category_res.path),
                'icon': e.icon,
                'pages': [{
                    'type': self.prefix(p.type) if p.custom else p.type,
                    'anchor': p.anchor_id,
                    **p.data
                } for p in pages],
                'advancement': e.advancement,
                'read_by_default': True,
                'sortnum': i if is_sorted else None,
                'extra_recipe_mappings': extra_recipe_mappings
            })

    def prefix(self, path: str) -> str:
        """ In a local instance, domains are all under patchouli, otherwise under tfc """
        return ('patchouli' if self.local_instance else 'tfc') + ':' + path


def entry(entry_id: str, name: str, icon: str, advancement: str | None = None, pages: Tuple[Page, ...] = ()) -> Entry:
    """
    :param entry_id: The id of this entry.
    :param name: The name of this entry.
    :param icon: The icon for this entry. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png
    :param advancement: The name of the advancement you want this entry to be locked behind. See Locking Content with Advancements for more info on locking content.
    :param pages: The array of pages for this entry.

    https://vazkiimods.github.io/Patchouli/docs/reference/entry-json/
    """
    return Entry(entry_id, name, icon, pages, advancement)


def text(text_contents: str, title: str | None = None) -> Page:
    """
    Text pages should always be the first page in any entry. If a text page is the first page in an entry, it'll display the header you see in the left page. For all other pages, it'll display as you can see in the right one.
    :param text_contents: The text to display on this page. This text can be formatted.
    :param title An optional title to display at the top of the page. If you set this, the rest of the text will be shifted down a bit. You can't use "title" in the first page of an entry.
    :return:
    """
    return page('patchouli:text', {'text': text_contents, 'title': title}, translation_keys=('text', 'title'))


def image(*images: str, text_contents: str | None = None, title: str = None, border: bool = True) -> Page:
    """
    :param images: An array with images to display. Images should be in resource location format. For example, the value botania:textures/gui/entries/banners.png will point to /assets/botania/textures/gui/entries/banners.png in the resource pack. For best results, make your image file 256 by 256, but only place content in the upper left 200 by 200 area. This area is then rendered at a 0.5x scale compared to the rest of the book in pixel size.
    If there's more than one image in this array, arrow buttons are shown like in the picture, allowing the viewer to switch between images.
    :param text_contents: The text to display on this page, under the image. This text can be formatted.
    :param border: Defaults to false. Set to true if you want the image to be bordered, like in the picture. It's suggested that border is set to true for images that use the entire canvas, whereas images that don't touch the corners shouldn't have it.
    """
    assert all(re.match('[a-z_/.]+', i) for i in images), ('Invalid images: %s, did you mean to declare one as \'text_contents=\' ?' % str(images))
    return page('patchouli:image', {'images': images, 'text': text_contents, 'title': title, 'border': border}, translation_keys=('text',))


def entity(entity_type: str, text_contents: str = None, title: str = None, scale: float = 0.7, offset: float = None, rotate: bool = None, default_rotation: float = None) -> Page:
    """
    :param entity_type: The entity type
    :param text_contents: The text to display under the entity display
    :param title: The title of the page
    :param scale: The scale of the entity. Defaults to 1
    :param offset: The vertical offset of the entity renderer. Defaults to 0
    :param rotate: Whether the entity should rotate in the view. Defaults to true.
    :param default_rotation: The rotation at which the entity is displayed. Only used if rotate is False.
    """
    if title == '':
        title = ' '  # Patchy will draw a title on name == null || name.isEmpty() which is dumb
    return page('patchouli:entity', {'entity': entity_type, 'scale': scale, 'offset': offset, 'rotate': rotate, 'default_rotation': default_rotation, 'name': title, 'text': text_contents})


def crafting(first_recipe: str, second_recipe: str | None = None, title: str | None = None, text_contents: str | None = None) -> Page:
    """
    :param first_recipe: The ID of the first recipe you want to show.
    :param second_recipe: The ID of the second recipe you want to show. Displaying two recipes is optional.
    :param title: The title of the page, to be displayed above both recipes. This is optional, but if you include it, only this title will be displayed, rather than the names of both recipe output items.
    :param text_contents: The text to display on this page, under the recipes. This text can be formatted.
    Note: the text will not display if there are two recipes with two different outputs, and "title" is not set. This is the case of the image displayed, in which both recipes have the output names displayed, and there's no space for text.
    """
    return page('patchouli:crafting', {'recipe': first_recipe, 'recipe2': second_recipe, 'title': title, 'text': text_contents}, translation_keys=('text', 'title'))


# todo: other default page types: (smelting, entity, link) as we need them

def item_spotlight(item: str, title: str | None = None, link_recipe: bool = False, text_contents: str | None = None) -> Page:
    """
    :param item: An ItemStack String representing the item to be spotlighted.
    :param title: A custom title to show instead on top of the item. If this is empty or not defined, it'll use the item's name instead.
    :param link_recipe: Defaults to false. Set this to true to mark this spotlight page as the "recipe page" for the item being spotlighted. If you do so, when looking at pages that display the item, you can shift-click the item to be taken to this page. Highly recommended if the spotlight page has instructions on how to create an item by non-conventional means.
    :param text_contents: The text to display on this page, under the item. This text can be formatted.
    """
    return page('patchouli:spotlight', {'item': item, 'title': title, 'link_recipes': link_recipe, 'text': text_contents}, translation_keys=('title', 'text'))


def block_spotlight(title: str, text_content: str, block: str, lower: str | None = None) -> Page:
    """ A shortcut for making a single block multiblock that is meant to act the same as item_spotlight() but for blocks """
    return multiblock(title, text_content, False, pattern=(('X',), ('0',)), mapping={'X': block, '0': lower})


def two_tall_block_spotlight(title: str, text_content: str, lower: str, upper: str) -> Page:
    """ A shortcut for making a single block multiblock for a double tall block, such as crops or tall grass """
    return multiblock(title, text_content, False, pattern=(('X',), ('Y',), ('0',)), mapping={'X': upper, 'Y': lower})


def multiblock(title: str, text_content: str, enable_visualize: bool, pattern: Tuple[Tuple[str, ...], ...] | None = None, mapping: Mapping[str, str] | None = None, offset: Tuple[int, int, int] | None = None, multiblock_id: str | None = None) -> Page:
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
        return page('patchouli:multiblock', {'multiblock_id': multiblock_id, **data}, translation_keys=('name', 'text'))
    elif pattern is not None and mapping is not None:
        return page('patchouli:multiblock', {'multiblock': {
            'pattern': pattern,
            'mapping': mapping,
            'offset': offset,
        }, **data}, translation_keys=('name', 'text'))
    else:
        raise ValueError('multiblock page must have either \'multiblock\' or \'pattern\' and \'mapping\' entries')


def empty() -> Page:
    return page('patchouli:empty', {})


# ==============
# TFC Page Types
# ==============

def multimultiblock(text_content: str, *pages) -> Page:
    return page('multimultiblock', {'text': text_content, 'multiblocks': [p.data['multiblock'] if 'multiblock' in p.data else p.data['multiblock_id'] for p in pages]}, custom=True, translation_keys=('text',))


def rock_knapping_typical(recipe_with_category_format: str, text_content: str) -> Page:
    return rock_knapping(*[recipe_with_category_format % c for c in ROCK_CATEGORIES], text_content=text_content)


def rock_knapping(*recipes: str, text_content: str) -> Page:
    return page('rock_knapping_recipe', {'recipes': recipes, 'text': text_content}, custom=True, translation_keys=('text',))


def leather_knapping(recipe: str, text_content: str) -> Page:
    return page('leather_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def clay_knapping(recipe: str, text_content: str) -> Page:
    return page('clay_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def fire_clay_knapping(recipe: str, text_content: str) -> Page:
    return page('fire_clay_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def heat_recipe(recipe: str, text_content: str) -> Page:
    return page('heat_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def quern_recipe(recipe: str, text_content: str) -> Page:
    return page('quern_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def anvil_recipe(recipe: str, text_content: str) -> Page:
    return page('anvil_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def welding_recipe(recipe: str, text_content: str) -> Page:
    return page('welding_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def alloy_recipe(title: str, alloy_name: str, text_content: str) -> Page:
    # Components can be copied from alloy_recipe() declarations in
    alloy_components = ALLOYS[alloy_name]
    recipe = ''.join(['$(li)%d - %d %% : $(thing)%s$()' % (round(100 * lo), round(100 * hi), lang(alloy)) for (alloy, lo, hi) in alloy_components])
    return item_spotlight('tfc:metal/ingot/%s' % alloy_name, title, False, '$(br)$(bold)Requirements:$()$(br)' + recipe + '$(br2)' + text_content)


def fertilizer(item: str, text_contents: str, n: float = 0, p: float = 0, k: float = 0) -> Page:
    text_contents += ' $(br)'
    if n > 0:
        text_contents += '$(li)$(b)Nitrogen: %d$()' % (n * 100)
    if p > 0:
        text_contents += '$(li)$(6)Phosphorous: %d$()' % (p * 100)
    if k > 0:
        text_contents += '$(li)$(d)Potassium: %d$()' % (k * 100)
    return item_spotlight(item, text_contents=text_contents)


def page_break() -> Page:
    return page('PAGE_BREAK', {})


def empty_last_page() -> Page:
    return page('EMPTY_LAST_PAGE', {})


def page(page_type: str, page_data: JsonObject, custom: bool = False, translation_keys: Tuple[str, ...] = ()) -> Page:
    return Page(page_type, page_data, custom, None, [], translation_keys)


# Components

def text_component(x: int, y: int) -> Component:
    return Component('patchouli:text', x, y, {'text': '#text'})


def header_component(x: int, y: int) -> Component:
    return Component('patchouli:header', x, y, {'text': '#header'})


def seperator_component(x: int, y: int) -> Component:
    return Component('patchouli:separator', x, y, {})


def custom_component(x: int, y: int, class_name: str, data: JsonObject) -> Component:
    return Component('patchouli:custom', x, y, {'class': 'net.dries007.tfc.compat.patchouli.component.' + class_name, **data})
import json
import os
import re
from typing import NamedTuple, Tuple, List, Mapping, Set, Any, Dict

from mcresources import ResourceManager, utils
from mcresources.type_definitions import JsonObject, ResourceLocation, ResourceIdentifier

from constants import ROCK_CATEGORIES, ALLOYS, lang
from i18n import I18n

NON_TEXT_FIRST_PAGE = 'non_text_first_page'
PAGE_BREAK = 'page_break'
EMPTY_LAST_PAGE = 'empty_last_page'
TABLE_PAGE = 'table'
TABLE_PAGE_SMALL = 'table_small'
TABLE_KEYS = {'strings': '#strings', 'columns': '#columns', 'first_column_width': '#first_column_width', 'column_width': '#column_width', 'row_height': '#row_height', 'left_buffer': '#left_buffer', 'top_buffer': '#top_buffer', 'title': '#title', 'legend': '#legend', 'draw_background': '#draw_background'}


class Component(NamedTuple):
    type: str
    x: int
    y: int
    data: JsonObject


class SubstitutionStr(NamedTuple):
    value: str
    params: Tuple[Any, ...]

    def __str__(self) -> str: return self.value


def defer(text_contents: str, *params) -> SubstitutionStr:
    return SubstitutionStr(text_contents, params)


TranslatableStr = str | SubstitutionStr


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
                value = self.data[key]
                if isinstance(value, SubstitutionStr):
                    try:
                        self.data[key] = i18n.translate(value.value).format(*value.params)
                    except IndexError as e:
                        raise ValueError('Error performing replacement for lang %s\n  \'%s\' -> \'%s\'' % (i18n.lang, value.value, i18n.translate(value.value))) from e
                else:
                    self.data[key] = i18n.translate(value)

    def iter_all_text(self):
        for key in self.translation_keys:
            if key in self.data and self.data[key] is not None:
                yield str(self.data[key])


class Entry(NamedTuple):
    entry_id: str
    name: str
    icon: str
    pages: Tuple[Page]
    advancement: str | None


class Category(NamedTuple):
    category_id: str
    name: str
    description: str
    icon: str
    parent: str | None
    is_sorted: bool
    entries: Tuple[Entry, ...]


class Book:

    def __init__(self, rm: ResourceManager, root_name: str, macros: JsonObject, i18n: I18n, local_instance: bool, reverse_translate: bool):
        self.rm: ResourceManager = rm
        self.root_name = root_name
        self.category_count = 0
        self.i18n = i18n
        self.local_instance = local_instance
        self.reverse_translate = reverse_translate

        self.categories: List[Category] = []
        self.macros = macros

    def template(self, template_id: str, *components: Component):
        if self.i18n.is_root():  # Templates are only required in root
            self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'templates', template_id), {
                'components': [{
                    'type': c.type, 'x': c.x, 'y': c.y, **c.data
                } for c in components]
            }, root_domain='assets')

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
        self.categories.append(Category(category_id, name, description, icon, parent, is_sorted, entries))

    def build(self):
        # Only generate the book.json if we're in the root language, and we're writing to a local instance
        if self.i18n.lang == 'en_us' and self.local_instance:
            self.rm.data(('patchouli_books', self.root_name, 'book'), {
                'name': 'tfc.field_guide.book_name',
                'landing_text': 'tfc.field_guide.book_landing_text',
                'subtitle': '${version}',
                # Even though we don't use the book item, we still need patchy to make a book item for us, as it controls the title
                # If neither we nor patchy make a book item, this will show up as 'Air'. So we make one to allow the title to work properly.
                'dont_generate_book': False,
                'show_progress': False,
                'macros': self.macros,
                'use_resource_pack': False,
            })

        # Find all valid link targets
        link_targets = {}
        for c in self.categories:
            for e in c.entries:
                link_targets['%s/%s' % (c.category_id, e.entry_id)] = {p.anchor_id for p in e.pages if p.anchor_id is not None}

        for c in self.categories:
            self.build_category(link_targets, c.category_id, c.name, c.description, c.icon, c.parent, c.is_sorted, c.entries)

    def build_category(self, link_targets: Mapping[str, Set[str]], category_id: str, name: str, description: str, icon: str, parent: str | None, is_sorted: bool, entries: Tuple[Entry, ...]):
        if self.reverse_translate:
            data = self.load_data(('patchouli_books', self.root_name, self.i18n.lang, 'categories', category_id))
            self.i18n.after[name] = data['name']
            self.i18n.after[description] = data['description']
        else:
            self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'categories', category_id), {
                'name': self.i18n.translate(name),
                'description': self.i18n.translate(description),
                'icon': icon,
                'parent': parent,
                'sortnum': self.category_count,
            }, root_domain='assets')
        self.category_count += 1

        category_res: ResourceLocation = utils.resource_location(self.rm.domain, category_id)

        assert not isinstance(entries, Entry), 'One entry in singleton entries, did you forget a comma after entry(), ?\n  at: %s' % str(entries)
        for i, e in enumerate(entries):
            assert not isinstance(e.pages, Page), 'One entry in singleton pages, did you forget a comma after page(), ?\n  at: %s' % str(e.pages)
            assert len(e.pages) > 0, 'Entry must have at least one page!\n  at: %s' % str(e.name)

            # First page must be either text or a marker that it's not
            if e.pages[0].type == NON_TEXT_FIRST_PAGE:
                pages = e.pages[1:]
            else:
                assert e.pages[0].type == 'patchouli:text', 'An entry starts with a non text() page: Patchouli uses a standard title page with text() pages when used first for each entry which should be kept.\nIf this is intentional, add a non_text_first_page() as the first page in this entry!\n  at: entry \'%s\'' % str(e.name)
                pages = e.pages

            allow_empty_last_page = False
            real_pages = []
            for j, p in enumerate(pages):
                if p.type == PAGE_BREAK:
                    assert len(real_pages) % 2 == 0, 'A page_break() required that the next entry must start on a new page, a page has been added that breaks this!\n  at: entry \'%s\', page break at index %d' % (str(e.name), j)
                elif p.type == EMPTY_LAST_PAGE:
                    allow_empty_last_page = True
                    assert j == len(pages) - 1, 'An empty_last_page() was used but it was not the last page?\n  at: %s' % str(e.name)
                elif p.type == TABLE_PAGE or p.type == TABLE_PAGE_SMALL:
                    assert len(real_pages) % 2 == 0, 'A table() requires that it starts on a new page!'
                    real_pages.append(p)
                    real_pages.append(blank())  # Tables take up two pages
                else:
                    real_pages.append(p)

            assert allow_empty_last_page or len(real_pages) % 2 == 0, 'An entry has an odd number of pages: this leaves a implicit empty() page at the end.\nIf this is intentional, add an empty_last_page() as the last page in this entry!\n  at: entry \'%s\'' % str(e.name)

            extra_recipe_mappings = {}
            for index, p in enumerate(real_pages):
                for link in p.link_ids:
                    extra_recipe_mappings[link] = index
            if not extra_recipe_mappings:  # Exclude if there's nothing here
                extra_recipe_mappings = None

            # Validate no duplicate anchors or links
            seen_anchors = set()
            seen_links = set()
            for p in real_pages:
                if p.anchor_id:
                    assert p.anchor_id not in seen_anchors, 'Duplicate anchor "%s" on page %s' % (p.anchor_id, p)
                    seen_anchors.add(p.anchor_id)
                for link in p.link_ids:
                    assert link not in seen_links, 'Duplicate link "%s" on page %s' % (link, p)
                    seen_links.add(link)

            # Validate all internal links of the form $(l:...)
            for p in real_pages:
                for page_text in p.iter_all_text():
                    for match in re.finditer(r'\$\(l:([^)]*)\)', page_text):
                        key = match.group(1)
                        if key.startswith('http'):
                            continue  # Don't validate external links
                        if '#' in key:
                            target, anchor = key.split('#')
                        else:
                            target, anchor = key, None
                        assert target in link_targets, 'Link target \'%s\' not found for link \'%s\'\n  at page: %s\n  at entry: \'%s\'' % (target, key, p, e.entry_id)
                        if anchor is not None:
                            assert anchor in link_targets[target], 'Link anchor \'%s\' not found for link \'%s\'\n  at page: %s\n  at entry: \'%s\'' % (anchor, key, p, e.entry_id)

            # Separately translate each page
            if self.reverse_translate:
                rev_entry = self.load_data(('patchouli_books', self.root_name, self.i18n.lang, 'entries', category_res.path, e.entry_id))
                if rev_entry:
                    rev_pages = rev_entry['pages']
                    for p, rp in zip(real_pages, rev_pages):
                        for key in p.translation_keys:
                            if key in p.data and p.data[key] is not None and key in rp:
                                self.i18n.after[str(p.data[key])] = rp[key]

                    self.i18n.after[e.name] = rev_entry['name']
                else:
                    print('Warning: missing book entry: %s/%s' % (category_res.path, e.entry_id))
                continue

            entry_name = self.i18n.translate(e.name)
            for p in real_pages:
                p.translate(self.i18n)

            self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'entries', category_res.path, e.entry_id), {
                'name': entry_name,
                'category': self.prefix(category_res.path),
                'icon': e.icon,
                'pages': [{
                    'type': self.prefix(p.type) if p.custom else p.type,
                    'anchor': p.anchor_id,
                    **p.data
                } for p in real_pages],
                'advancement': e.advancement,
                'read_by_default': True,
                'sortnum': i if is_sorted else None,
                'extra_recipe_mappings': extra_recipe_mappings
            }, root_domain='assets')

    def prefix(self, path: str) -> str:
        """ In a local instance, domains are all under patchouli, otherwise under tfc """
        return ('patchouli' if self.local_instance else 'tfc') + ':' + path

    def load_data(self, name_parts: ResourceIdentifier) -> JsonObject:
        res = utils.resource_location(self.rm.domain, name_parts)
        path = os.path.join(*self.rm.resource_dir, 'data', res.domain, res.path) + '.json'
        if os.path.isfile(path):
            with open(path, 'r', encoding='utf-8') as f:
                return json.load(f)


def entry(entry_id: str, name: str, icon: str, advancement: str | None = None, pages: Tuple[Page, ...] = ()) -> Entry:
    """
    :param entry_id: The id of this entry.
    :param name: The name of this entry.
    :param icon: The icon for this entry. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png
    :param advancement: The name of the advancement you want this entry to be locked behind. See Locking Content with Advancements for more info on locking content.
    :param pages: The array of pages for this entry.

    https://vazkiimods.github.io/Patchouli/docs/reference/entry-json/
    """
    if icon.startswith('tfc:food/'):  # Food items decay - this is a stupid hack to just replace them with their .png image, so they don't! Wizard!
        icon = icon.replace('tfc:', 'tfc:textures/item/') + '.png'
    # This is a heuristic, it is not accurate (as crafting recipes also generate ctrl-links). But it is useful as a start
    # requires `import warnings`
    # if all(not p.link_ids for p in pages):
    #     warnings.warn('Entry \'%s\' does not have any .link()s' % entry_id, stacklevel=2)
    return Entry(entry_id, name, icon, pages, advancement)


def text(text_contents: TranslatableStr, title: TranslatableStr | None = None) -> Page:
    """
    Text pages should always be the first page in any entry. If a text page is the first page in an entry, it'll display the header you see in the left page. For all other pages, it'll display as you can see in the right one.
    :param text_contents: The text to display on this page. This text can be formatted.
    :param title An optional title to display at the top of the page. If you set this, the rest of the text will be shifted down a bit. You can't use "title" in the first page of an entry.
    :return:
    """
    return page('patchouli:text', {'text': text_contents, 'title': title}, translation_keys=('text', 'title'))


def image(*images: str, text_contents: TranslatableStr | None = None, title: TranslatableStr = None, border: bool = True) -> Page:
    """
    :param images: An array with images to display. Images should be in resource location format. For example, the value botania:textures/gui/entries/banners.png will point to /assets/botania/textures/gui/entries/banners.png in the resource pack. For best results, make your image file 256 by 256, but only place content in the upper left 200 by 200 area. This area is then rendered at a 0.5x scale compared to the rest of the book in pixel size.
    If there's more than one image in this array, arrow buttons are shown like in the picture, allowing the viewer to switch between images.
    :param text_contents: The text to display on this page, under the image. This text can be formatted.
    :param title: The title of the page, shown above the image.
    :param border: Defaults to false. Set to true if you want the image to be bordered, like in the picture. It's suggested that border is set to true for images that use the entire canvas, whereas images that don't touch the corners shouldn't have it.
    """
    assert all(re.match('[a-z_/.]+', i) for i in images), ('Invalid images: %s, did you mean to declare one as \'text_contents=\' ?' % str(images))
    return page('patchouli:image', {'images': images, 'text': text_contents, 'title': title, 'border': border}, translation_keys=('text', 'title'))


def entity(entity_type: str, text_contents: TranslatableStr = None, title: TranslatableStr = None, scale: float = 0.7, offset: float = None, rotate: bool = None, default_rotation: float = None) -> Page:
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
    return page('patchouli:entity', {'entity': entity_type, 'scale': scale, 'offset': offset, 'rotate': rotate, 'default_rotation': default_rotation, 'name': title, 'text': text_contents}, translation_keys=('name', 'text'))


def crafting(first_recipe: str, second_recipe: str | None = None, title: TranslatableStr | None = None, text_contents: TranslatableStr | None = None) -> Page:
    """
    :param first_recipe: The ID of the first recipe you want to show.
    :param second_recipe: The ID of the second recipe you want to show. Displaying two recipes is optional.
    :param title: The title of the page, to be displayed above both recipes. This is optional, but if you include it, only this title will be displayed, rather than the names of both recipe output items.
    :param text_contents: The text to display on this page, under the recipes. This text can be formatted.
    Note: the text will not display if there are two recipes with two different outputs, and "title" is not set. This is the case of the image displayed, in which both recipes have the output names displayed, and there's no space for text.
    """
    if second_recipe is not None:
        assert ' ' not in second_recipe
    return page('patchouli:crafting', {'recipe': first_recipe, 'recipe2': second_recipe, 'title': title, 'text': text_contents}, translation_keys=('text', 'title'))


def item_spotlight(item: str | Tuple[str, ...], title: TranslatableStr | None = None, link_recipe: bool = False, text_contents: TranslatableStr | None = None) -> Page:
    """
    :param item: An ItemStack String representing the item to be spotlighted.
    :param title: A custom title to show instead on top of the item. If this is empty or not defined, it'll use the item's name instead.
    :param link_recipe: Defaults to false. Set this to true to mark this spotlight page as the "recipe page" for the item being spotlighted. If you do so, when looking at pages that display the item, you can shift-click the item to be taken to this page. Highly recommended if the spotlight page has instructions on how to create an item by non-conventional means.
    :param text_contents: The text to display on this page, under the item. This text can be formatted.
    """
    if isinstance(item, tuple):
        assert all(re.match('[a-z]+:[a-z_/]+', i) for i in item), 'item_spotlight() item may be a tuple of item names, or a tag, specified with #foo:bar syntax'
        item = ','.join(item)
    elif isinstance(item, str):
        assert re.match('#?[a-z]+:[a-z/_]+', item), 'item_spotlight() item may be a tuple of item names, or a tag, specified with #foo:bar syntax'
        if item.startswith('#'):  # Patchy format for tags
            item = 'tag:' + item[1:]
    return page('patchouli:spotlight', {'item': item, 'title': title, 'link_recipes': link_recipe, 'text': text_contents}, translation_keys=('title', 'text'))


def block_spotlight(title: TranslatableStr, text_content: TranslatableStr, block: str, lower: str | None = None) -> Page:
    """ A shortcut for making a single block multiblock that is meant to act the same as item_spotlight() but for blocks """
    return multiblock(title, text_content, False, pattern=(('X',), ('0',)), mapping={'X': block, '0': lower})


def two_tall_block_spotlight(title: TranslatableStr, text_content: TranslatableStr, lower: str, upper: str) -> Page:
    """ A shortcut for making a single block multiblock for a double tall block, such as crops or tall grass """
    return multiblock(title, text_content, False, pattern=(('X',), ('Y',), ('0',)), mapping={'X': upper, 'Y': lower})


def multiblock(title: TranslatableStr = '', text_content: TranslatableStr = '', enable_visualize: bool = False, pattern: Tuple[Tuple[str, ...], ...] | None = None, mapping: Mapping[str, str] | None = None, offset: Tuple[int, int, int] | None = None, multiblock_id: str | None = None) -> Page:
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


def blank() -> Page:
    return page('patchouli:empty', {'draw_filler': False})


# ==============
# TFC Page Types
# ==============


def multimultiblock(text_content: TranslatableStr, *pages) -> Page:
    return page('multimultiblock', {'text': text_content, 'multiblocks': [p.data['multiblock'] if 'multiblock' in p.data else p.data['multiblock_id'] for p in pages]}, custom=True, translation_keys=('text',))


def knapping(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('knapping_recipe', recipe, text_content)
def heat_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('heat_recipe', recipe, text_content)
def quern_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('quern_recipe', recipe, text_content)
def anvil_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('anvil_recipe', recipe, text_content)
def welding_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('welding_recipe', recipe, text_content)
def sealed_barrel_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('sealed_barrel_recipe', recipe, text_content)
def instant_barrel_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('instant_barrel_recipe', recipe, text_content)
def loom_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('loom_recipe', recipe, text_content)
def glassworking_recipe(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('glassworking_recipe', recipe, text_content)


def rock_knapping_typical(item_type: str, text_content: TranslatableStr) -> Page:
    return page('rock_knapping_recipe', {'recipes': ['tfc:knapping/stone/%s/%s' % (item_type, c) for c in ROCK_CATEGORIES], 'text': text_content}, custom=True, translation_keys=('text',))


def alloy_recipe(title: str, alloy_name: str, text_content: TranslatableStr) -> Page:
    # Components can be copied from alloy_recipe() declarations in
    alloy_components = ALLOYS[alloy_name]
    recipe = ''.join(['$(li)%d - %d %% : $(thing)%s$()' % (round(100 * lo), round(100 * hi), lang(alloy)) for (alloy, lo, hi) in alloy_components])
    return item_spotlight('tfc:metal/ingot/%s' % alloy_name, title, False, '$(br)$(bold)Requirements:$()$(br)' + recipe + '$(br2)' + text_content)


def fertilizer(item: str, text_contents: TranslatableStr, n: float = 0, p: float = 0, k: float = 0) -> Page:
    text_contents += ' $(br)'
    if n > 0:
        text_contents += '$(li)$(b)Nitrogen: %d$()' % (n * 100)
    if p > 0:
        text_contents += '$(li)$(6)Phosphorous: %d$()' % (p * 100)
    if k > 0:
        text_contents += '$(li)$(d)Potassium: %d$()' % (k * 100)
    return item_spotlight(item, text_contents=text_contents)


def non_text_first_page() -> Page:
    return page(NON_TEXT_FIRST_PAGE, {})


def page_break() -> Page:
    return page(PAGE_BREAK, {})


def empty_last_page() -> Page:
    return page(EMPTY_LAST_PAGE, {})


def recipe_page(recipe_type: str, recipe: str, text_content: TranslatableStr) -> Page:
    return page(recipe_type, {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def table(strings: List[str | Dict], text_content: TranslatableStr, title: TranslatableStr, keywords: Dict[str, Any], legend: List[Dict[str, Any]], columns: int, first_column_width: int, column_width: int, row_height: int, left_buffer: int, top_buffer: int, draw_background: bool = True, small: bool = False) -> Page:
    fixed_strings = []
    for string in strings:
        fixed_str = string
        if keywords:
            for k, v in keywords.items():
                if k == string:
                    fixed_str = v
        if isinstance(fixed_str, str):
            fixed_strings.append({'text': fixed_str})
        else:
            fixed_strings.append(fixed_str)
    return page(TABLE_PAGE_SMALL if small else TABLE_PAGE, {
        'strings': fixed_strings,
        'text': text_content,
        'title': title,
        'legend': legend,
        'columns': columns,
        'first_column_width': first_column_width,
        'column_width': column_width,
        'row_height': row_height,
        'left_buffer': left_buffer,
        'top_buffer': top_buffer,
        'draw_background': draw_background
    }, custom=True, translation_keys=('text', 'title'))


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

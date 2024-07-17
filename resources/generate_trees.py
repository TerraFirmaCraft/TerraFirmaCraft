import os

from collections import deque
from typing import NamedTuple

from nbtlib import nbt, File as RootTag
from nbtlib.tag import String as StringTag, Int as IntTag, List as ListTag, Compound as CompoundTag


class Tree(NamedTuple):
    normal: str
    large: str | None
    dead: str | None


class Pos(NamedTuple):
    """ A `BlockPos` or basic 3D integer valued point. """

    x: int
    y: int
    z: int

    @staticmethod
    def from_nbt(pos_nbt: ListTag) -> 'Pos':
        return Pos(*map(int, pos_nbt))

    def norm1(self) -> int: return abs(self.x) + abs(self.y) + abs(self.z)

    def __add__(self, other) -> 'Pos': return Pos(self.x + other[0], self.y + other[1], self.z + other[2])
    def __sub__(self, other) -> 'Pos': return Pos(self.x - other[0], self.y - other[1], self.z - other[2])
    def __neg__(self) -> 'Pos': return Pos(-self.x, -self.y, -self.z)

    def __str__(self) -> str: return self.__repr__()
    def __repr__(self) -> str: return '(%d, %d, %d)' % self


class Worker(NamedTuple):
    size: Pos

    axis_states: dict[int, tuple[str, StringTag]]

    log_positions: dict[Pos, int]
    leaf_positions: dict[Pos, int]
    root_positions: set[Pos]

    def get_trunk(self, dx: int = 0, dz: int = 0) -> Pos:
        return Pos(self.size.x // 2 + dx, 0, self.size.z // 2 + dz)

    def get_log(self, pos: Pos) -> tuple[str, StringTag]:
        return self.axis_states[self.log_positions[pos]]

    def palette(self) -> 'Palette':
        return Palette([], [], self)

    def translate(self, offset: Pos) -> 'Worker':
        return Worker(self.size, self.axis_states, translate(self.log_positions, offset), translate(self.leaf_positions, offset), translate(self.root_positions, offset))


class Palette(NamedTuple):
    """ A palette representation of a structure. Stores a palette and positions in serialized NBT form. """

    blocks: list[CompoundTag]
    palette: list[CompoundTag]
    worker: Worker

    def add_blocks(self, wood: str, log_paths: dict[Pos, Pos], leaf_paths: dict[Pos, int]):
        """
        Adds all log and leaf blocks to the palette
        :param wood: The wood name, a TFC wood type
        :param log_paths: The log positions, mapped to a branch direction
        :param leaf_paths: The leaf positions, mapped to a distance
        """
        for log_pos, adj in log_paths.items():
            block_name, block_axis = self.worker.get_log(log_pos)
            block = create_log_block_tag(wood, block_name, block_axis, adj)

            self.add_block(log_pos, block)

        for leaf_pos, dist in leaf_paths.items():
            block = create_leaf_block_tag(wood, dist)

            self.add_block(leaf_pos, block)

    def add_block(self, pos: Pos, block: CompoundTag):
        if block in self.palette:
            block_id = self.palette.index(block)
        else:
            block_id = len(self.palette)
            self.palette.append(block)

        entry = CompoundTag()
        entry['state'] = IntTag(block_id)
        entry['pos'] = ListTag([IntTag(pos.x), IntTag(pos.y), IntTag(pos.z)])

        self.blocks.append(entry)

    def make_root(self):
        """ Creates a new root tag representing the current structure. """
        return RootTag({
            'size': ListTag([IntTag(self.worker.size.x), IntTag(self.worker.size.y), IntTag(self.worker.size.z)]),
            'entities': ListTag(),
            'blocks': ListTag(self.blocks),
            'palette': ListTag(self.palette),
            'DataVersion': IntTag(DATA_VERSION),
        })


# Trunk directions are represented by a y = -2
TRUNK_NW: Pos = Pos(-1, -2, -1)
TRUNK_NE: Pos = Pos(1, -2, -1)
TRUNK_SW: Pos = Pos(-1, -2, 1)
TRUNK_SE: Pos = Pos(1, -2, 1)

# The default trunk direction used for the base of trees
TRUNK_ROOT: Pos = Pos(0, -1, 0)


# All possible branch directions
# Ordered by strongest connection.
BRANCH_DIRECTIONS: dict[Pos, str] = {
    Pos(0, 0, 0): 'none',

    TRUNK_ROOT: 'down',

    Pos(0, 0, -1): 'north',
    Pos(-1, 0, 0): 'west',
    Pos(1, 0, 0): 'east',
    Pos(0, 0, 1): 'south',

    Pos(0, -1, -1): 'down_north',
    Pos(-1, -1, 0): 'down_west',
    Pos(1, -1, 0): 'down_east',
    Pos(0, -1, 1): 'down_south',

    Pos(-1, 0, -1): 'north_west',
    Pos(1, 0, -1): 'north_east',
    Pos(-1, 0, 1): 'south_west',
    Pos(1, 0, 1): 'south_east',

    Pos(-1, -1, -1): 'down_north_west',
    Pos(1, -1, -1): 'down_north_east',
    Pos(-1, -1, 1): 'down_south_west',
    Pos(1, -1, 1): 'down_south_east',

    Pos(-1, 1, -1): 'up_north_west',
    Pos(1, 1, -1): 'up_north_east',
    Pos(-1, 1, 1): 'up_south_west',
    Pos(1, 1, 1): 'up_south_east',

    Pos(0, 1, -1): 'up_north',
    Pos(1, 1, 0): 'up_west',
    Pos(-1, 1, 0): 'up_east',
    Pos(0, 1, 1): 'up_south',

    Pos(0, 1, 0): 'up',

    TRUNK_NW: 'trunk_north_west',
    TRUNK_NE: 'trunk_north_east',
    TRUNK_SW: 'trunk_south_west',
    TRUNK_SE: 'trunk_south_east',
}

# Exclude trunk directions here, but keep ordering
# This is the set of directions used for the BFS
NORMAL_BRANCH_DIRECTIONS: list[Pos] = [pos for pos in BRANCH_DIRECTIONS if pos.y != -2]

# Mapping of branch direction -> index in the list
BRANCH_STRENGTH: dict[Pos, int] = {pos: i for i, pos in enumerate(NORMAL_BRANCH_DIRECTIONS)}

# Tuple of (dx, dy, trunk) where the dx, dy are offset from a divide-by-two center position
TRUNK_BRANCH_DIRECTIONS: tuple[tuple[int, int, Pos], ...] = (
    (0, 0, TRUNK_NW),
    (-1, 0, TRUNK_NE),
    (0, -1, TRUNK_SW),
    (-1, -1, TRUNK_SE),
)

LOG_BLOCKS = {'minecraft:oak_log', 'minecraft:oak_wood', 'minecraft:stripped_oak_log', 'minecraft:stripped_oak_wood'}
LEAF_BLOCKS = {'minecraft:oak_leaves'}

# The maximum allowed value of the 'distance' property across all leaves
MAX_DISTANCE = 9


# Trees in TFC, along with their variants, and corresponding template files/names
# We infer which type of feature/template they are using by the existence of particular files:
#
# For a name 'tree':
#   `tree.nbt`    -> overlay
#   `tree1.nbt`   -> random
#   `tree_layer1_1.nbt` -> stacked
#
# For random and stacked trees, we then count sequentially to include all found structure files
TREES: dict[str, Tree] = {
    'acacia': Tree('acacia', 'koa', 'dead_acacia'),
    'ash': Tree('aspen', 'thin', 'dead_aspen'),
    'aspen': Tree('aspen', 'thin', 'dead_aspen'),
    'birch': Tree('birch', 'emergent', 'dead_aspen'),
    'blackwood': Tree('blackwood', 'blackwood_large', 'dead_small'),
    'chestnut': Tree('chestnut', 'normal_large', 'dead_chestnut'),
    'douglas_fir': Tree('fluffy_conifer', 'fluffy_old_conifer', 'fir_snag'),
    'hickory': Tree('hickory', 'round_large', 'dead_branching'),
    'kapok': Tree('jungle', None, 'dead_jungle'),
    'mangrove': Tree('mangrove', None, 'dead_stump'),
    'maple': Tree('round', 'round_large', 'dead_small'),
    'oak': Tree('pin_oak', None, 'dead_branching'),
    'palm': Tree('tropical', None, 'dead_palm'),
    'pine': Tree('red_pine', None, 'pine_snag'),
    'rosewood': Tree('tall_branches', 'pin_oak', 'dead_branching'),
    'sequoia': Tree('conifer', 'conifer_large', 'dead_tall'),
    'spruce': Tree('tall_boreal', None, 'dead_tall'),
    'sycamore': Tree('medium_round', 'normal_large', 'dead_chestnut'),
    'white_cedar': Tree('white_cedar', 'tall', 'dead_tall'),
    'willow': Tree('willow', 'willow_large', 'dead_stump'),
}


OVERLAY = 'overlay'
RANDOM = 'random'
STACKED = 'stacked'


# The data version used by DFU
# Keep this up-to-date with the version in SharedConstants.VERSION
DATA_VERSION = 3953

TEMPLATES_DIR = './resources/structure_templates'
STRUCTURES_DIR = './src/main/resources/data/tfc/structure'

# If `STRICT_MODE` is true, it performs some additional checks on the structure of trees that may not hold in general
# These should be used to help while making tree structures and only ignored if they're catching false positives.
STRICT_CHECKS: bool = False


def main():
    print('Generating...')

    leaves: dict[str, int] = {}
    for name, tree in TREES.items():
        print('   ', name)
        leaves[name] = make_tree(name, tree.normal)
        make_tree(name, tree.large, '_large')
        make_tree(name, tree.dead, '_dead')

    print('# Automatically Generated by generate_trees.py')
    print('TREE_SAPLING_DROP_CHANCES = {')
    for name, count in leaves.items():
        # Base value: every tree results in 3.5 saplings, on average, if every leaf was broken
        # We bias this towards returning larger values, for larger trees, as it requires more leaves to break
        chance = 3.5 / count
        if chance < 0.02:
            chance = 0.2 * 0.02 + 0.8 * chance
        print('    %s: %.4f,' % (repr(name), chance))
    print('}')


def make_tree(wood: str, tree: str | None, suffix: str = '') -> int:
    """ Generates tree structures for a given wood type + tree template type
    :param wood: The name of the tree to be generated (a TFC wood type)
    :param tree: The name of the template tree structures to use
    :param suffix: The suffix to use for the tree structure
    :return: The number of leaves (average) in the normal structure variant
    """
    if tree is None:
        return 0  # Nothing to do

    tree_type, arg = infer_type(tree)
    count = 0

    if tree_type == RANDOM:
        for n in range(1, 1 + arg):
            src = '%s%d' % (tree, n)
            dest = '%s%s/%d.nbt' % (wood, suffix, n)
            try:
                base, leaves = make_single_structure(wood, src)
                save_structure(dest, base)
                count += leaves
            except AssertionError as e:
                print('Error: %s.nbt -> %s.nbt : %s' % (src, dest, e))
        count /= arg

    elif tree_type == OVERLAY:
        try:
            base, overlay, leaves = make_overlay_tree(wood, tree)
            save_structure('%s%s/base.nbt' % (wood, suffix), base)
            save_structure('%s%s/overlay.nbt' % (wood, suffix), overlay)
            count += leaves
        except AssertionError as e:
            print('Error: %s_base|overlay.nbt -> %s%s/base|overlay.nbt : %s' % (tree, wood, suffix, e))

    elif tree_type == STACKED:
        # Each layer of a stacked tree parses independently as a tree
        # This works for our use cases, although is probably not the best generic solution
        for layer in range(1, 1 + len(arg)):
            for n in range(1, 1 + arg[layer - 1]):
                src = '%s_layer%d_%d' % (tree, layer, n)
                dest = '%s%s/layer%d_%d.nbt' % (wood, suffix, layer, n)
                try:
                    base, _ = make_single_structure(wood, src)
                    save_structure(dest, base)
                except AssertionError as e:
                    print('Error %s.nbt -> %s.nbt : %s' % (src, dest, e))

    else:
        raise AssertionError('Invalid tree_type: %s' % repr(tree_type))
    return count


def infer_type(wood: str) -> tuple[str, None | int | tuple[int, ...]]:

    def exists(_path: str) -> bool:
        return os.path.isfile(os.path.join(TEMPLATES_DIR, wood + _path + '.nbt'))

    if exists(''):
        return OVERLAY, None
    if exists('1'):
        n = 2
        while exists('%d' % n):
            n += 1
        return RANDOM, n - 1
    if exists('_layer1_1'):
        layers = []
        n = 1
        while exists('_layer%d_1' % n):
            m = 2
            while exists('_layer%d_%d' % (n, m)):
                m += 1
            layers.append(m - 1)
            n += 1
        return STACKED, tuple(layers)

    raise ValueError('Cannot infer the type of a tree named %s' % wood)


def make_single_structure(wood: str, name: str) -> tuple[RootTag, int]:
    # Decompose a single structure, based on `tree` and `n`
    root_nbt: RootTag = nbt.load('%s/%s.nbt' % (TEMPLATES_DIR, name))
    worker = make_worker(root_nbt)
    parity = worker.size.x % 2, worker.size.z % 2

    if parity == (0, 0):
        centers = [
            worker.get_trunk(dx, dz)
            for dx, dz, _ in TRUNK_BRANCH_DIRECTIONS
        ]
    elif parity == (1, 1):
        centers = [worker.get_trunk()]
    else:
        raise AssertionError('Structure is not uniform width: %s' % str(worker.size))

    if STRICT_CHECKS:
        assert all(c in worker.root_positions for c in centers), 'Center roots %s not found in roots: %s' % (centers, worker.root_positions)
        assert worker.root_positions == set(centers), 'Additional roots %s found outside centers: %s' % (worker.root_positions - set(centers), centers)

    centers = [c for c in centers if c in worker.root_positions]

    assert centers, 'No valid root positions were found!'

    log_paths = find_log_paths([c for c in centers if c in worker.root_positions], worker.log_positions)
    leaf_paths = find_leaf_paths(worker.log_positions, worker.leaf_positions)

    # If a 2x2 tree, update the main 'trunk' to use 'trunk' properties
    if parity == (0, 0):
        for dx, dz, trunk in TRUNK_BRANCH_DIRECTIONS:
            pos = worker.get_trunk(dx, dz)
            while pos in log_paths:
                log_paths[pos] = trunk
                pos += (0, 1, 0)

    palette = worker.palette()
    palette.add_blocks(wood, log_paths, leaf_paths)

    if leaf_paths:
        assert (decay := max(leaf_paths.values())) <= MAX_DISTANCE, 'Max distance is higher than supported decay distance: %d > %d at %s' % (decay, MAX_DISTANCE, [p for p, d in leaf_paths.items() if d > MAX_DISTANCE])

    return palette.make_root(), len(leaf_paths)


def make_overlay_tree(wood: str, tree: str) -> tuple[RootTag, RootTag, int]:
    # Decompose the base and overlay structure
    # When pathing the overlay structure, apply it additively to the base structure
    base_nbt: RootTag = nbt.load('%s/%s.nbt' % (TEMPLATES_DIR, tree))
    overlay_nbt: RootTag = nbt.load('%s/%s_overlay.nbt' % (TEMPLATES_DIR, tree))

    base = make_worker(base_nbt)
    overlay = make_worker(overlay_nbt)

    assert base.size.x % 2 == 1 and base.size.z % 2 == 1, 'Base structure is not odd width: %s' % str(base.size)
    assert overlay.size.x % 2 == 1 and overlay.size.z % 2 == 1, 'Overlay structure is not odd width: %s' % str(overlay.size)

    centers = [base.get_trunk()]

    assert all(c in base.root_positions for c in centers), 'Center roots %s not found in roots: %s' % (centers, base.root_positions)

    log_paths = find_log_paths(centers, base.log_positions)
    leaf_paths = find_leaf_paths(base.log_positions, base.leaf_positions)

    # Since base and overlay are allowed to be different sizes, before we BFS, we need to translate the overlay by an offset to ensure it lines up
    # We then un-translate the positions after the fact
    offset = base.size - overlay.size
    offset = Pos(offset.x // 2, 0, offset.z // 2)
    overlay = overlay.translate(offset)

    # The overlay is generally treated as a union of the base and overlay, but then filtered to only contain overlay positions
    #  -> logs use `enqueue=True` to make sure they connect immediately to the base
    #  -> leaves path over both the base and overlay
    overlay_log_paths = {
        pos: key
        for pos, key in find_log_paths(list(log_paths.keys()), overlay.log_positions, enqueue=True).items()
        if pos in overlay.log_positions
    }
    overlay_leaf_paths = {
        pos: d
        for pos, d in find_leaf_paths(base.log_positions | overlay.log_positions, base.leaf_positions | overlay.leaf_positions).items()
        if pos in overlay.leaf_positions
    }

    palette = base.palette()
    palette.add_blocks(wood, log_paths, leaf_paths)

    overlay = overlay.translate(-offset)
    overlay_palette = overlay.palette()
    overlay_palette.add_blocks(wood, translate(overlay_log_paths, -offset), translate(overlay_leaf_paths, -offset))

    return palette.make_root(), overlay_palette.make_root(), len(leaf_paths)


def make_worker(root_nbt: RootTag):
    """
    Common parts of all tree construction

    :param root_nbt: The base NBT tag for the tree template structure
    :return: A worker with all basic properties for this structure
    """
    size_nbt, palette_nbt, blocks_nbt = root_nbt['size'], root_nbt['palette'], root_nbt['blocks']

    size = Pos.from_nbt(size_nbt)

    log_states = states_of(palette_nbt, LOG_BLOCKS)
    leaf_states = states_of(palette_nbt, LEAF_BLOCKS)

    states: dict[int, (str, StringTag)] = {
        i: (str(block_nbt['Name']), block_nbt['Properties']['axis'])
        for i, block_nbt in enumerate(palette_nbt)
        if 'Properties' in block_nbt and 'axis' in block_nbt['Properties']
    }

    missing_states = {
        str(state['Name'])
        for state in palette_nbt
    } - LOG_BLOCKS - LEAF_BLOCKS

    assert missing_states == set(), 'Not-expected states found in template: %s' % missing_states

    log_positions = positions_of(blocks_nbt, log_states)
    leaf_positions = positions_of(blocks_nbt, leaf_states)
    root_positions = {p for p in log_positions if p.y == 0}

    return Worker(size, states, log_positions, leaf_positions, root_positions)


def states_of(palette_nbt: ListTag, blocks: set[str]) -> set[int]:
    """ Given a palette tag and a block name, returns the set of indexes corresponding to that block """
    return {
        i
        for i, block_nbt in enumerate(palette_nbt)
        if block_nbt['Name'] in blocks
    }


def positions_of(blocks_nbt: ListTag, states: set[int]) -> dict[Pos, int]:
    """ Given a blocks tag and a set of states (produced from `states_of()`), returns the mapping of positions to index of those blocks """
    return {
        Pos(*map(int, block_nbt['pos'])): block_nbt['state']
        for block_nbt in blocks_nbt
        if block_nbt['state'] in states
    }


def find_log_paths(root_positions: list[Pos], log_positions: dict[Pos, int], enqueue: bool = True) -> dict[Pos, Pos]:
    """
    Breadth-first searches a tree structure, to find the parent connected log for each log

    :param root_positions: The origin root positions which all other positions must connect to. Either the single 'center' root, or a 2x2 set of roots.
    :param log_positions: The set of all log positions we must be able to reach
    :param enqueue: If false, then this will not enqueue any new positions, and only BFS a single step out from the origins
    :return: A mapping of log positions to their branch direction

    """
    queue = deque(root_positions)
    paths: dict[Pos, Pos] = {pos: TRUNK_ROOT for pos in root_positions}
    while queue:
        pos = queue.popleft()

        for offset in NORMAL_BRANCH_DIRECTIONS:  # Iterate ordered by branch directions, for strongest connections
            adj = pos - offset
            if adj != pos and adj in log_positions:
                if adj not in paths:
                    # First time we see this connection, so always connect
                    paths[adj] = offset
                    if enqueue:
                        queue.append(adj)
                elif BRANCH_STRENGTH[offset] < BRANCH_STRENGTH[paths[adj]]:
                    # If we see an existing connection, we might be able to make a stronger connection
                    # To avoid creating cycles, we only do this if we can't detect a short cycle
                    if pos + paths[pos] != adj:
                        paths[adj] = offset

    assert len(paths) >= len(log_positions), 'Structure is disconnected - %d unreachable logs at %s' % (len(log_positions) - len(paths), log_positions - paths.keys())
    return paths


def find_leaf_paths(log_positions: dict[Pos, int], leaf_positions: dict[Pos, int]) -> dict[Pos, int]:
    """
    Breadth-first searches a tree structure, to find the distance of each leaf block to the nearest log, used to set the 'distance' property

    :param log_positions: The set of all possible log positions
    :param leaf_positions: The set of all possible leaf positions
    :return: A mapping of leaf positions the distance to the nearest log
    """
    queue = deque([(pos, 0) for pos in log_positions])
    paths: dict[Pos, int] = {}
    while queue:
        pos, dist = queue.popleft()
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                for dz in (-1, 0, 1):
                    if Pos(dx, dy, dz).norm1() == 1:
                        adj = pos + (dx, dy, dz)
                        if adj in leaf_positions and adj not in paths:
                            paths[adj] = dist + 1
                            queue.append((adj, dist + 1))

    assert len(paths) == len(leaf_positions), 'Structure is disconnected - %d unreachable leaves at %s' % (len(leaf_positions) - len(paths), leaf_positions - paths.keys())
    return paths


def create_log_block_tag(wood: str, block_name: str, block_axis: StringTag, adj: Pos) -> CompoundTag:
    """ Creates the NBT tag for a log block. """

    if block_name == 'minecraft:oak_log':
        block_name = StringTag('tfc:wood/log/%s' % wood)
    else:
        block_name = StringTag('tfc:wood/wood/%s' % wood)

    block = CompoundTag()
    block['Name'] = block_name
    block['Properties'] = CompoundTag()
    block['Properties']['axis'] = block_axis
    block['Properties']['branch_direction'] = StringTag(BRANCH_DIRECTIONS[adj])
    return block


def create_leaf_block_tag(wood: str, distance: int) -> CompoundTag:
    """ Creates the NBT tag for a leaf block """

    block = CompoundTag()
    block['Name'] = StringTag('tfc:wood/leaves/%s' % wood)
    block['Properties'] = CompoundTag()
    block['Properties']['persistent'] = StringTag('false')
    block['Properties']['distance'] = StringTag(str(distance))
    return block


def save_structure(path: str, root_nbt: RootTag):
    """
    :param path: The destination file path, relative to the `structures/` directory
    :param root_nbt: The NBT to be saved
    """
    # Uncomment for hotswap
    # root_nbt.save('./out/production/resources/data/tfc/structure/' + path.replace('/', '_'), gzipped=True)

    path = os.path.join(STRUCTURES_DIR, path)
    if os.path.isfile(path):
        old_nbt = nbt.load(path)
        if root_nbt == old_nbt:
            # Avoid replacing if the file exists and compares identical, because gzip is not deterministic
            # This avoids unnecessary git diffs
            return

    os.makedirs(os.path.dirname(path), exist_ok=True)
    root_nbt.save(path, gzipped=True)


def translate(positions: dict | set, offset: Pos) -> dict | set:
    if isinstance(positions, set):
        return {p + offset for p in positions}
    if isinstance(positions, dict):
        return {p + offset: value for p, value in positions.items()}
    raise AssertionError('No idea how to translate %s' % repr(positions))


if __name__ == '__main__':
    main()

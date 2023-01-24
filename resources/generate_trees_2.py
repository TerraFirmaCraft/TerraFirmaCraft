from typing import NamedTuple, Literal, Iterable
from collections import deque

from nbtlib import nbt, File as RootTag
from nbtlib.tag import String as StringTag, Int as IntTag, List as ListTag, Compound as CompoundTag


class Tree(NamedTuple):
    name: str
    feature: Literal['random', 'overlay', 'stacked']
    variant: str
    count: int | tuple[int, ...]


class BlockPos(NamedTuple):
    x: int
    y: int
    z: int

    def norm1(self) -> int: return abs(self.x) + abs(self.y) + abs(self.z)

    def __add__(self, other) -> 'BlockPos': return BlockPos(self.x + other[0], self.y + other[1], self.z + other[2])
    def __sub__(self, other) -> 'BlockPos': return BlockPos(self.x - other[0], self.y - other[1], self.z - other[2])

    def __neg__(self) -> 'BlockPos': return BlockPos(-self.x, -self.y, -self.z)


class Palette(NamedTuple):
    blocks: list[CompoundTag]
    palette: list[CompoundTag]

    def add_block(self, pos: BlockPos, block: CompoundTag):
        if block in self.palette:
            block_id = self.palette.index(block)
        else:
            block_id = len(self.palette)
            self.palette.append(block)

        entry = CompoundTag()
        entry['state'] = IntTag(block_id)
        entry['pos'] = ListTag([IntTag(pos.x), IntTag(pos.y), IntTag(pos.z)])

        self.blocks.append(entry)


# All possible branch directions
# Ordered by strongest connection.
BRANCH_DIRECTIONS: dict[tuple[int, int, int], str] = {
    (0, 0, 0): 'none',

    (0, -1, 0): 'down',

    (0, 0, -1): 'north',
    (-1, 0, 0): 'west',
    (1, 0, 0): 'east',
    (0, 0, 1): 'south',

    (0, -1, -1): 'down_north',
    (-1, -1, 0): 'down_west',
    (1, -1, 0): 'down_east',
    (0, -1, 1): 'down_south',

    (-1, 0, -1): 'north_west',
    (1, 0, -1): 'north_east',
    (-1, 0, 1): 'south_west',
    (1, 0, 1): 'south_east',

    (-1, -1, -1): 'down_north_west',
    (1, -1, -1): 'down_north_east',
    (-1, -1, 1): 'down_south_west',
    (1, -1, 1): 'down_south_east',

    #(0, 1, 0): 'up',

    #(0, 1, -1): 'up_north',
    #(-1, 1, 0): 'up_west',
    #(1, 1, 0): 'up_east',
    #(0, 1, 1): 'up_south',

    #(-1, 1, -1): 'up_north_west',
    #(1, 1, -1): 'up_north_east',
    #(-1, 1, 1): 'up_south_west',
    #(1, 1, 1): 'up_south_east',
}

BRANCH_STRENGTH: dict[tuple[int, int, int], int] = {pos: i for i, pos in enumerate(BRANCH_DIRECTIONS)}

DATA_VERSION = 2975

TEMPLATES_DIR = './resources/structure_templates'
STRUCTURES_DIR = './src/main/resources/data/tfc/structures'

NORMAL_TREES = (
    Tree('acacia', 'random', 'acacia', 35),
    Tree('ash', 'overlay', 'normal', 0),
    Tree('aspen', 'random', 'aspen', 16),
    Tree('birch', 'random', 'aspen', 16),
    Tree('blackwood', 'random', 'blackwood', 10),
    Tree('chestnut', 'overlay', 'normal', 0),
    Tree('douglas_fir', 'random', 'fir', 9),
    Tree('hickory', 'random', 'fir', 9),
    Tree('kapok', 'random', 'jungle', 17),
    Tree('maple', 'overlay', 'normal', 0),
    Tree('oak', 'overlay', 'tall', 0),
    Tree('palm', 'random', 'tropical', 7),
    Tree('pine', 'random', 'fir', 9),
    Tree('rosewood', 'overlay', 'tall', 0),
    Tree('sequoia', 'random', 'conifer', 9),
    Tree('spruce', 'random', 'conifer', 9),
    Tree('sycamore', 'overlay', 'normal', 0),
    Tree('white_cedar', 'overlay', 'white_cedar', 0),
    Tree('willow', 'random', 'willow', 7),
)

LARGE_TREES = (
    Tree('acacia', 'random', 'kapok_large', 6),
    Tree('ash', 'random', 'normal_large', 5),
    Tree('blackwood', 'random', 'blackwood_large', 10),
    Tree('chestnut', 'random', 'normal_large', 5),
    Tree('douglas_fir', 'random', 'fir_large', 5),
    Tree('hickory', 'random', 'fir_large', 5),
    Tree('maple', 'random', 'normal_large', 5),
    Tree('pine', 'random', 'fir_large', 5),
    Tree('sequoia', 'stacked', 'conifer_large', (3, 3, 3)),
    Tree('spruce', 'stacked', 'conifer_large', (3, 3, 3)),
    Tree('sycamore', 'random', 'normal_large', 5),
    Tree('white_cedar', 'overlay', 'tall', 0),
    Tree('willow', 'random', 'willow_large', 14)
)

DEAD_TREES = (
    Tree('acacia', 'random', 'dead_small', 6),
    Tree('ash', 'random', 'dead_tall', 6),
    Tree('aspen', 'random', 'dead_tall', 6),
    Tree('birch', 'random', 'dead_tall', 6),
    Tree('blackwood', 'random', 'dead_small', 6),
    Tree('chestnut', 'random', 'dead_small', 6),
    Tree('douglas_fir', 'random', 'dead_tall', 6),
    Tree('hickory', 'random', 'dead_tall', 6),
    Tree('kapok', 'random', 'dead_jungle', 4),
    Tree('maple', 'random', 'dead_small', 6),
    Tree('oak', 'random', 'dead_small', 6),
    Tree('palm', 'random', 'dead_stump', 3),
    Tree('pine', 'random', 'dead_tall', 6),
    Tree('rosewood', 'random', 'dead_tall', 6),
    Tree('sequoia', 'random', 'dead_tall', 6),
    Tree('spruce', 'random', 'dead_tall', 6),
    Tree('sycamore', 'random', 'dead_small', 6),
    Tree('white_cedar', 'random', 'dead_tall', 6),
    Tree('willow', 'random', 'dead_stump', 3),
)


def main():
    for tree in NORMAL_TREES:
        if tree.feature == 'random':
            for n in range(1, 1 + tree.count):
                try:
                    build_random_tree(tree, n)
                except AssertionError as e:
                    print('Error: %s in random tree %s%d.nbt, in %s' % (e, tree.variant, n, tree.name))
        elif tree.feature == 'overlay':
            try:
                build_overlay_tree(tree)
            except AssertionError as e:
                print('Error: %s in overlay tree %s' % (e, tree.variant))


def build_overlay_tree(tree: Tree):
    # Decompose the base and overlay structure
    # When pathing the overlay structure, apply it additively to the base structure
    base_nbt: RootTag = nbt.load('%s/%s.nbt' % (TEMPLATES_DIR, tree.variant))
    overlay_nbt: RootTag = nbt.load('%s/%s_overlay.nbt' % (TEMPLATES_DIR, tree.variant))

    size_nbt, palette_nbt, blocks_nbt = base_nbt['size'], base_nbt['palette'], base_nbt['blocks']
    overlay_size_nbt, overlay_palette_nbt, overlay_blocks_nbt = overlay_nbt['size'], overlay_nbt['palette'], overlay_nbt['blocks']

    size = BlockPos(*map(int, size_nbt))
    assert size.x % 2 == 1 and size.z % 2 == 1, 'Base structure is not odd width (%d, %d)' % (size.x, size.z)

    overlay_size = BlockPos(*map(int, overlay_size_nbt))
    assert overlay_size.x % 2 == 1 and overlay_size.z % 2 == 1, 'Overlay structure is not odd width (%d, %d)' % (overlay_size.x, overlay_size.z)

    offset = size - overlay_size
    offset = BlockPos(offset.x // 2, offset.y, offset.z // 2)

    log_states, wood_states, leaf_states = states_of(palette_nbt, 'minecraft:oak_log'), states_of(palette_nbt, 'minecraft:oak_wood'), states_of(palette_nbt, 'minecraft:oak_leaves')

    states: dict[int, (str, StringTag)] = {
        i: (str(block_nbt['Name']), block_nbt['Properties']['axis'])
        for i, block_nbt in enumerate(palette_nbt)
        if 'Properties' in block_nbt and 'axis' in block_nbt['Properties']
    }

    log_positions = positions_of(blocks_nbt, log_states | wood_states)
    leaf_positions = positions_of(blocks_nbt, leaf_states)
    root_positions = {p for p in log_positions if p.y == 0}

    root_center = BlockPos(size.x // 2, 0, size.z // 2)
    assert root_center in root_positions, 'Center root %s not found in roots: %s' % (root_center, root_positions)

    log_paths = find_log_paths([root_center], root_positions, log_positions)
    leaf_paths = find_leaf_paths(log_positions, leaf_positions)

    # Overlay is independently pathed, and can only connect to the nearest block in the base structure.

    palette = Palette([], [])

    # Add log blocks to palette
    for log_pos, adj in log_paths.items():
        block_name, block_axis = states[log_positions[log_pos]]
        block = create_log_block_tag(tree, block_name, block_axis, adj)

        palette.add_block(log_pos, block)

    # Add leaf blocks to palette
    for leaf_pos, dist in leaf_paths.items():
        block = create_leaf_block_tag(tree, dist)

        palette.add_block(leaf_pos, block)


def build_random_tree(tree: Tree, n: int):
    # Decompose a single structure, based on `tree` and `n`
    root_nbt: RootTag = nbt.load('%s/%s%d.nbt' % (TEMPLATES_DIR, tree.variant, n))
    size_nbt, palette_nbt, blocks_nbt = root_nbt['size'], root_nbt['palette'], root_nbt['blocks']

    size = BlockPos(*map(int, size_nbt))
    assert size.x % 2 == 1 and size.z % 2 == 1, 'Structure is not odd width (%d, %d)' % (size.x, size.z)

    log_states, wood_states, leaf_states = states_of(palette_nbt, 'minecraft:oak_log'), states_of(palette_nbt, 'minecraft:oak_wood'), states_of(palette_nbt, 'minecraft:oak_leaves')

    states: dict[int, (str, StringTag)] = {
        i: (str(block_nbt['Name']), block_nbt['Properties']['axis'])
        for i, block_nbt in enumerate(palette_nbt)
        if 'Properties' in block_nbt and 'axis' in block_nbt['Properties']
    }

    log_positions = positions_of(blocks_nbt, log_states | wood_states)
    leaf_positions = positions_of(blocks_nbt, leaf_states)
    root_positions = {p for p in log_positions if p.y == 0}

    root_center = BlockPos(size.x // 2, 0, size.z // 2)
    assert root_center in root_positions, 'Center root %s not found in roots: %s' % (root_center, root_positions)

    log_paths = find_log_paths([root_center], root_positions, log_positions)
    leaf_paths = find_leaf_paths(log_positions, leaf_positions)
    palette = Palette([], [])

    # Add log blocks to palette
    for log_pos, adj in log_paths.items():
        block_name, block_axis = states[log_positions[log_pos]]
        block = create_log_block_tag(tree, block_name, block_axis, adj)

        palette.add_block(log_pos, block)

    # Add leaf blocks to palette
    for leaf_pos, dist in leaf_paths.items():
        block = create_leaf_block_tag(tree, dist)

        palette.add_block(leaf_pos, block)

    new_root_nbt = create_root_tag(size_nbt, palette)
    new_root_nbt.save('./out/production/resources/data/tfc/structures/%s/%d.nbt' % (tree.name, n), gzipped=True)


def states_of(palette_nbt: ListTag, block: str) -> set[int]:
    return {i for i, block_nbt in enumerate(palette_nbt) if block_nbt['Name'] == block}


def positions_of(blocks_nbt: ListTag, states: set[int]) -> dict[BlockPos, int]:
    return {BlockPos(*map(int, block_nbt['pos'])): block_nbt['state'] for block_nbt in blocks_nbt if block_nbt['state'] in states}

def find_log_paths(root_centers: Iterable[BlockPos], root_positions: Iterable[BlockPos], log_positions: dict[BlockPos, int]) -> dict[BlockPos, BlockPos]:
    queue = deque(root_centers)
    paths: dict[BlockPos, BlockPos] = {root_pos: BlockPos(0, 0, 0) for root_pos in root_centers}  # All root positions are 'any'
    while queue:
        pos = queue.popleft()

        for dx, dy, dz in BRANCH_DIRECTIONS.keys():  # Iterate ordered by branch directions, for strongest connections
            offset = BlockPos(dx, dy, dz)
            adj = pos - offset
            if adj != pos and adj in log_positions:
                if adj not in paths:
                    # First time we see this connection, so always connect
                    paths[adj] = offset
                    queue.append(adj)
                elif BRANCH_STRENGTH[offset] < BRANCH_STRENGTH[paths[adj]]:
                    # If we see an existing connection, we might be able to make a stronger connection
                    # To avoid creating cycles, we only do this if we can't detect a short cycle
                    if pos + paths[pos] != adj:
                        paths[adj] = offset

    assert len(paths) == len(log_positions), 'Structure is disconnected - %d unreachable logs at %s' % (len(log_positions) - len(paths), format_positions(log_positions - paths.keys()))
    return paths


def find_leaf_paths(log_positions: dict[BlockPos, int], leaf_positions: dict[BlockPos, int]) -> dict[BlockPos, int]:
    queue = deque([(pos, 0) for pos in log_positions])
    paths: dict[BlockPos, int] = {}
    while queue:
        pos, dist = queue.popleft()
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                for dz in (-1, 0, 1):
                    if BlockPos(dx, dy, dz).norm1() == 1:
                        adj = pos + (dx, dy, dz)
                        if adj in leaf_positions and adj not in paths:
                            paths[adj] = dist + 1
                            queue.append((adj, dist + 1))

    assert len(paths) == len(leaf_positions), 'Structure is disconnected - %d unreachable leaves at %s' % (len(leaf_positions) - len(paths), format_positions(leaf_positions - paths.keys()))
    return paths


def create_log_block_tag(tree: Tree, block_name: str, block_axis: StringTag, adj: BlockPos) -> CompoundTag:
    if block_name == 'minecraft:oak_log':
        block_name = StringTag('tfc:wood/log/%s' % tree.name)
    else:
        block_name = StringTag('tfc:wood/wood/%s' % tree.name)

    block = CompoundTag()
    block['Name'] = block_name
    block['Properties'] = CompoundTag()
    block['Properties']['axis'] = block_axis
    block['Properties']['natural'] = StringTag('true')
    block['Properties']['branch_direction'] = StringTag(BRANCH_DIRECTIONS[adj])
    return block

def create_leaf_block_tag(tree: Tree, distance: int) -> CompoundTag:
    block = CompoundTag()
    block['Name'] = StringTag('tfc:wood/leaves/%s' % tree.name)
    block['Properties'] = CompoundTag()
    block['Properties']['persistent'] = StringTag('false')
    block['Properties']['distance'] = StringTag(str(distance))
    return block

def create_root_tag(size_nbt: ListTag, palette: Palette) -> RootTag:
    return RootTag({
        'size': size_nbt,
        'entities': ListTag(),
        'blocks': ListTag(palette.blocks),
        'palette': ListTag(palette.palette),
        'DataVersion': IntTag(DATA_VERSION),
    })

def format_positions(positions: set[BlockPos]) -> str:
    return '[%s]' % ', '.join('(%d %d %d)' % pos for pos in positions)

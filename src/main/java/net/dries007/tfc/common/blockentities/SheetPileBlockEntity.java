/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MetalItem;

public class SheetPileBlockEntity extends TFCBlockEntity
{
    private final ItemStack[] stacks;
    private final MetalItem[] cachedMetals;

    private static final DirectionProperty FACING = SheetPileBlock.FACING;
    private static final BooleanProperty MIRROR = SheetPileBlock.MIRROR;

    /**
     * Sheet piles use a separate rotation + mirror block state property to map their states (up, down, left, etc.) to an index.
     * We do this as we want to be able to mirror and rotate the block (which only provide a state, not a block entity).
     * So, this method takes the state and a requested direction, and converts it to an index into the (un-rotated) {@code stacks} array.
     * <p>
     * A mirror of {@code Mirror.NONE} and a rotation of {@code Direction.NORTH} is considered the reference frame of no-rotation, i.e. where the {@code stacks} is indexed by direction ordinal.
     */
    private int faceToIndex(Direction face)
    {
        final BlockState state = this.getBlockState();

        if (face.getAxis() == Direction.Axis.Y)
        {
            return face.ordinal();
        }

        final Mirror mirror = state.getValue(MIRROR) ? Mirror.FRONT_BACK : Mirror.NONE;
        final Rotation rot = switch (state.getValue(FACING))
            {
                case SOUTH -> Rotation.CLOCKWISE_180;
                case EAST -> Rotation.COUNTERCLOCKWISE_90;
                case WEST -> Rotation.CLOCKWISE_90;
                default -> Rotation.NONE;
            };

        return mirror.mirror(rot.rotate(face)).ordinal();
    }

    public SheetPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SHEET_PILE.get(), pos, state);

        this.stacks = new ItemStack[6];
        this.cachedMetals = new MetalItem[6];

        Arrays.fill(stacks, ItemStack.EMPTY);
    }

    public void addSheet(Direction direction, ItemStack stack)
    {
        final int index = faceToIndex(direction);
        stacks[index] = stack;
        cachedMetals[index] = null;
        markForSync();
    }

    public ItemStack removeSheet(Direction direction)
    {
        final int index = faceToIndex(direction);
        final ItemStack stack = stacks[index];
        stacks[index] = ItemStack.EMPTY;
        cachedMetals[index] = null;
        markForSync();
        return stack;
    }

    public ItemStack getSheet(Direction direction)
    {
        return stacks[faceToIndex(direction)].copy();
    }

    /**
     * Returns a cached metal for the given side, if present, otherwise grabs from the cache.
     * The metal is defined by checking what metal the stack would melt into if heated.
     * Any other items turn into {@link MetalItem#unknown()}.
     */
    public MetalItem getOrCacheMetal(Direction direction)
    {
        final int index = faceToIndex(direction);
        final ItemStack stack = stacks[index];

        MetalItem metal = cachedMetals[index];
        if (metal == null)
        {
            metal = MetalItem.getOrUnknown(stack);
            cachedMetals[index] = metal;
        }
        return metal;
    }

    /**
     * Sets the cached metals for a block entity that is not placed in the world
     */
    public void setAllMetalsFromOutsideWorld(MetalItem metal)
    {
        Arrays.fill(cachedMetals, metal);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.put("stacks", Helpers.writeItemStacksToNbt(provider, stacks));
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        Helpers.readItemStacksFromNbt(provider, stacks, tag.getList("stacks", Tag.TAG_COMPOUND));
        Arrays.fill(cachedMetals, null); // Invalidate metal cache
        super.loadAdditional(tag, provider);
    }

    public void fillTooltip(Consumer<Component> tooltip)
    {
        class Counter
        {
            final ItemStack stack;
            int count = 0;

            Counter(ItemStack stack) { this.stack = stack; }
        }

        final Map<MetalItem, Counter> counts = new LinkedHashMap<>(); // Deterministic iteration order
        for (int i = 0; i < cachedMetals.length; i++)
        {
            if (cachedMetals[i] != null)
            {
                final ItemStack stack = stacks[i];
                counts.compute(cachedMetals[i], (key, old) -> {
                    if (old == null) old = new Counter(stack);
                    old.count++;
                    return old;
                });
            }
        }
        for (Counter value : counts.values())
        {
            tooltip.accept(Component.literal(value.count + "x ").append(value.stack.getHoverName()));
        }
    }
}

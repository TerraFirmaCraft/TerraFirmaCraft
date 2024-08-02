/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.function.Supplier;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.HotPouredGlassBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

// todo: don't extend TickableInventory and just extend Tickable, with a saved item stack?
public class HotPouredGlassBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, HotPouredGlassBlockEntity glass)
    {
        glass.checkForLastTickSync();
        if (!glass.initialized)
        {
            return;
        }
        if (glass.animationTicks > 0)
        {
            glass.animationTicks--;
        }
        else
        {
            if (glass.isInitialTransition)
            {
                glass.isInitialTransition = false;
                if (glass.capacity > 0)
                {
                    doFloodFill(level, pos, state, glass);
                    glass.capacity = 0;
                }
                glass.markForSync();
            }
            else
            {
                level.setBlockAndUpdate(pos, glass.getInternalBlock());
                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                final var random = level.getRandom();
                Supplier<Vec3> supplier = () -> new Vec3(Mth.nextDouble(level.getRandom(), -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F));
                ParticleUtils.spawnParticlesOnBlockFace(level, pos.below(), ParticleTypes.SMOKE, UniformInt.of(4, 10), Direction.UP, supplier, 0.6);
            }
        }
    }

    private static void doFloodFill(Level level, BlockPos pos, BlockState state, HotPouredGlassBlockEntity center)
    {
        record Path(BlockPos pos, int cost) {}

        if (level.isClientSide)
        {
            return;
        }

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Object2IntMap<BlockPos> filled = new Object2IntOpenHashMap<>();
        final Queue<Path> queue = new ArrayDeque<>();

        filled.put(pos, 0);
        queue.add(new Path(pos, 0));

        int maxCost = -1, capacity = center.capacity;

        while (!queue.isEmpty())
        {
            final Path current = queue.remove();

            capacity--;
            if (capacity >= 0 && current.cost > maxCost)
            {
                maxCost = current.cost;
            }
            if (capacity <= 0 && current.cost > maxCost)
            {
                break;
            }

            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                cursor.setWithOffset(current.pos, direction);
                if (!filled.containsKey(cursor) && canFloodFillAt(level, cursor))
                {
                    final BlockPos posNext = cursor.immutable();

                    queue.add(new Path(posNext, current.cost + 1));
                    filled.put(posNext, current.cost + 1);
                }
            }
        }

        filled
            .object2IntEntrySet()
            .stream()
            .sorted(Comparator.<Object2IntMap.Entry<BlockPos>>comparingInt(Object2IntMap.Entry::getIntValue)
                .thenComparing(e -> e.getKey().distSqr(pos)))
            .limit(16)
            .forEach(entry -> {
                final BlockPos fillPos = entry.getKey();
                final int cost = entry.getIntValue();

                level.setBlockAndUpdate(fillPos, state.setValue(HotPouredGlassBlock.FLAT, true));
                if (level.getBlockEntity(fillPos) instanceof HotPouredGlassBlockEntity side)
                {
                    side.isInitialTransition = false;
                    side.animationTicks = 40 + (cost * 10);
                    side.initialized = true;
                    side.capacity = 0;
                    side.setGlassItem(center.getGlassItem().copy());
                    side.markForSync();
                }
            });
    }

    private static boolean canFloodFillAt(Level level, BlockPos.MutableBlockPos cursor)
    {
        if (level.getBlockState(cursor).isAir())
        {
            cursor.move(Direction.DOWN);
            if (Helpers.isBlock(level.getBlockState(cursor), TFCTags.Blocks.GLASS_POURING_TABLE))
            {
                cursor.move(Direction.UP);
                return true;
            }
            cursor.move(Direction.UP);
        }
        return false;
    }

    private int capacity = 0;
    private boolean isInitialTransition = true;
    private int animationTicks = 0;
    private boolean initialized = false;

    public HotPouredGlassBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.HOT_POURED_GLASS.get(), pos, state);
    }

    public HotPouredGlassBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1));
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        capacity = nbt.getInt("capacity");
        isInitialTransition = nbt.getBoolean("isInitialTransition");
        animationTicks = nbt.getInt("animationTicks");
        initialized = nbt.getBoolean("initialized");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.putInt("capacity", capacity);
        nbt.putBoolean("isInitialTransition", isInitialTransition);
        nbt.putInt("animationTicks", animationTicks);
        nbt.putBoolean("initialized", initialized);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof BlockItem;
    }

    public void setGlassItem(ItemStack stack)
    {
        inventory.setStackInSlot(0, stack);
    }

    public void flattenFirstBlock()
    {
        animationTicks = 20;
        initialized = true;
        capacity = 15; // There's an off-by-one somewhere in the flood fill, this actually results in 16 total blocks
        markForSync();
    }

    private ItemStack getGlassItem()
    {
        return inventory.getStackInSlot(0);
    }

    private BlockState getInternalBlock()
    {
        return ((BlockItem) inventory.getStackInSlot(0).getItem()).getBlock().defaultBlockState();
    }

    public boolean isInitialTransition()
    {
        return isInitialTransition;
    }

    public int getAnimationTicks()
    {
        return animationTicks;
    }

    public boolean isInitialized()
    {
        return initialized;
    }
}
/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.BufferedList;
import net.dries007.tfc.util.loot.TFCLoot;

public class WorldTracker implements IWorldTracker, ICapabilitySerializable<CompoundTag>
{
    private final Random random = new Random();

    private final LazyOptional<IWorldTracker> capability;
    private final BufferedList<TickEntry> landslideTicks;
    private final BufferedList<BlockPos> isolatedPositions;
    private final List<Collapse> collapsesInProgress;

    public WorldTracker()
    {
        this.capability = LazyOptional.of(() -> this);
        this.landslideTicks = new BufferedList<>();
        this.isolatedPositions = new BufferedList<>();
        this.collapsesInProgress = new ArrayList<>();
    }

    @Override
    public void addLandslidePos(BlockPos pos)
    {
        landslideTicks.add(new TickEntry(pos, 2));
    }

    @Override
    public void addIsolatedPos(BlockPos pos)
    {
        isolatedPositions.add(pos);
    }

    @Override
    public void addCollapseData(Collapse collapse)
    {
        collapsesInProgress.add(collapse);
    }

    @Override
    public void addCollapsePositions(BlockPos centerPos, Collection<BlockPos> positions)
    {
        List<BlockPos> collapsePositions = new ArrayList<>();
        double maxRadiusSquared = 0;
        for (BlockPos pos : positions)
        {
            double distSquared = pos.distSqr(centerPos);
            if (distSquared > maxRadiusSquared)
            {
                maxRadiusSquared = distSquared;
            }
            if (random.nextFloat() < TFCConfig.SERVER.collapseExplosionPropagateChance.get())
            {
                collapsePositions.add(pos.above()); // Check the above position
            }
        }
        addCollapseData(new Collapse(centerPos, collapsePositions, maxRadiusSquared));
    }

    public void tick(Level world)
    {
        if (!world.isClientSide())
        {
            if (!collapsesInProgress.isEmpty() && random.nextInt(10) == 0)
            {
                for (Collapse collapse : collapsesInProgress)
                {
                    Set<BlockPos> updatedPositions = new HashSet<>();
                    for (BlockPos posAt : collapse.nextPositions)
                    {
                        // Check the current position for collapsing
                        BlockState stateAt = world.getBlockState(posAt);
                        if (TFCTags.Blocks.CAN_COLLAPSE.contains(stateAt.getBlock()) && TFCFallingBlockEntity.canFallThrough(world, posAt.below()) && posAt.distSqr(collapse.centerPos) < collapse.radiusSquared && random.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                        {
                            if (CollapseRecipe.collapseBlock(world, posAt, stateAt))
                            {
                                // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                                updatedPositions.add(posAt.above());
                            }
                        }
                    }
                    collapse.nextPositions.clear();
                    if (!updatedPositions.isEmpty())
                    {
                        world.playSound(null, collapse.centerPos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
                        collapse.nextPositions.addAll(updatedPositions);
                        collapse.radiusSquared *= 0.8; // lower radius each successive time
                    }
                }
                collapsesInProgress.removeIf(collapse -> collapse.nextPositions.isEmpty());
            }

            landslideTicks.flush();
            Iterator<TickEntry> tickIterator = landslideTicks.listIterator();
            while (tickIterator.hasNext())
            {
                TickEntry entry = tickIterator.next();
                if (entry.tick())
                {
                    final BlockState currentState = world.getBlockState(entry.getPos());
                    LandslideRecipe.tryLandslide(world, entry.getPos(), currentState);
                    tickIterator.remove();
                }
            }

            isolatedPositions.flush();
            Iterator<BlockPos> isolatedIterator = isolatedPositions.listIterator();
            while (isolatedIterator.hasNext())
            {
                final BlockPos pos = isolatedIterator.next();
                final BlockState currentState = world.getBlockState(pos);
                if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(currentState.getBlock()) && isIsolated(world, pos))
                {
                    Helpers.destroyBlockAndDropBlocksManually(world, pos, ctx -> ctx.withParameter(TFCLoot.ISOLATED, true));
                }
                isolatedIterator.remove();
            }
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        landslideTicks.flush();
        isolatedPositions.flush();

        CompoundTag nbt = new CompoundTag();
        ListTag landslideNbt = new ListTag();
        for (TickEntry entry : landslideTicks)
        {
            landslideNbt.add(entry.serializeNBT());
        }
        nbt.put("landslideTicks", landslideNbt);

        LongArrayTag isolatedNbt = new LongArrayTag(isolatedPositions.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.put("isolatedPositions", isolatedNbt);

        ListTag collapseNbt = new ListTag();
        for (Collapse collapse : collapsesInProgress)
        {
            collapseNbt.add(collapse.serializeNBT());
        }
        nbt.put("collapsesInProgress", collapseNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable CompoundTag nbt)
    {
        if (nbt != null)
        {
            landslideTicks.clear();
            collapsesInProgress.clear();
            isolatedPositions.clear();

            ListTag landslideNbt = nbt.getList("landslideTicks", Tag.TAG_COMPOUND);
            for (int i = 0; i < landslideNbt.size(); i++)
            {
                landslideTicks.add(new TickEntry(landslideNbt.getCompound(i)));
            }

            long[] isolatedNbt = nbt.getLongArray("isolatedPositions");
            Arrays.stream(isolatedNbt).mapToObj(BlockPos::of).forEach(isolatedPositions::add);

            ListTag collapseNbt = nbt.getList("collapsesInProgress", Tag.TAG_COMPOUND);
            for (int i = 0; i < collapseNbt.size(); i++)
            {
                collapsesInProgress.add(new Collapse(collapseNbt.getCompound(i)));
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return WorldTrackerCapability.CAPABILITY.orEmpty(cap, capability);
    }

    private boolean isIsolated(LevelAccessor world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            if (!world.isEmptyBlock(pos.relative(direction)))
            {
                return false;
            }
        }
        return true;
    }
}
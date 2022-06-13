/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.*;

import net.dries007.tfc.util.climate.BiomeBasedClimateModel;
import net.dries007.tfc.util.climate.ClimateModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
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

public class WorldTracker implements ICapabilitySerializable<CompoundTag>
{
    private final Random random;
    private final LazyOptional<WorldTracker> capability;

    private final BufferedList<TickEntry> landslideTicks;
    private final BufferedList<BlockPos> isolatedPositions;
    private final List<Collapse> collapsesInProgress;

    private final ClimateModel defaultClimateModel = new BiomeBasedClimateModel();
    @Nullable private ClimateModel climateModel;

    public WorldTracker()
    {
        this.random = new Random();
        this.capability = LazyOptional.of(() -> this);
        this.climateModel = null;
        this.landslideTicks = new BufferedList<>();
        this.isolatedPositions = new BufferedList<>();
        this.collapsesInProgress = new ArrayList<>();
    }

    public void addLandslidePos(BlockPos pos)
    {
        landslideTicks.add(new TickEntry(pos, 2));
    }

    public void addIsolatedPos(BlockPos pos)
    {
        isolatedPositions.add(pos);
    }

    public void addCollapseData(Collapse collapse)
    {
        collapsesInProgress.add(collapse);
    }

    public void setClimateModel(ClimateModel climateModel)
    {
        this.climateModel = climateModel;
    }

    public ClimateModel getClimateModel()
    {
        return climateModel == null ? defaultClimateModel : climateModel;
    }

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

    public void tick(Level level)
    {
        if (!level.isClientSide())
        {
            if (!collapsesInProgress.isEmpty() && random.nextInt(10) == 0)
            {
                for (Collapse collapse : collapsesInProgress)
                {
                    Set<BlockPos> updatedPositions = new HashSet<>();
                    for (BlockPos posAt : collapse.nextPositions)
                    {
                        // Check the current position for collapsing
                        BlockState stateAt = level.getBlockState(posAt);
                        if (Helpers.isBlock(stateAt, TFCTags.Blocks.CAN_COLLAPSE) && TFCFallingBlockEntity.canFallInDirection(level, posAt, Direction.DOWN) && posAt.distSqr(collapse.centerPos) < collapse.radiusSquared && random.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                        {
                            if (CollapseRecipe.collapseBlock(level, posAt, stateAt))
                            {
                                // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                                updatedPositions.add(posAt.above());
                            }
                        }
                    }
                    collapse.nextPositions.clear();
                    if (!updatedPositions.isEmpty())
                    {
                        level.playSound(null, collapse.centerPos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
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
                    final BlockState currentState = level.getBlockState(entry.getPos());
                    LandslideRecipe.tryLandslide(level, entry.getPos(), currentState);
                    tickIterator.remove();
                }
            }

            isolatedPositions.flush();
            Iterator<BlockPos> isolatedIterator = isolatedPositions.listIterator();
            while (isolatedIterator.hasNext())
            {
                final BlockPos pos = isolatedIterator.next();
                final BlockState currentState = level.getBlockState(pos);
                if (Helpers.isBlock(currentState.getBlock(), TFCTags.Blocks.BREAKS_WHEN_ISOLATED) && isIsolated(level, pos))
                {
                    Helpers.destroyBlockAndDropBlocksManually((ServerLevel) level, pos, ctx -> ctx.withParameter(TFCLoot.ISOLATED, true));
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return WorldTrackerCapability.CAPABILITY.orEmpty(cap, capability);
    }

    private boolean isIsolated(LevelAccessor level, BlockPos pos)
    {
        for (Direction direction : Helpers.DIRECTIONS)
        {
            BlockState state = level.getBlockState(pos.relative(direction));
            if (!state.getCollisionShape(level, pos).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}
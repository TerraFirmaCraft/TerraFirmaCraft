package net.dries007.tfc.world.tracker;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.objects.entities.TFCFallingBlockEntity;
import net.dries007.tfc.objects.recipes.CollapseRecipe;
import net.dries007.tfc.objects.recipes.LandslideRecipe;

public class WorldTracker implements IWorldTracker, ICapabilitySerializable<CompoundNBT>
{
    private static final Random RANDOM = new Random();

    private final LazyOptional<IWorldTracker> capability;
    private final List<BlockPos> landslidePositions;
    private final List<BlockPos> landslidePositionsToAdd;
    private final List<CollapseData> collapsesInProgress;

    public WorldTracker()
    {
        this.capability = LazyOptional.of(() -> this);
        this.landslidePositions = new ArrayList<>();
        this.landslidePositionsToAdd = new ArrayList<>();
        this.collapsesInProgress = new ArrayList<>();
    }

    @Override
    public void addLandslidePos(BlockPos pos)
    {
        landslidePositionsToAdd.add(pos);
    }

    @Override
    public void addCollapseData(CollapseData collapse)
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
            double distSquared = pos.distanceSq(centerPos);
            if (distSquared > maxRadiusSquared)
            {
                maxRadiusSquared = distSquared;
            }
            if (RANDOM.nextFloat() < TFCConfig.SERVER.collapseExplosionPropagateChance.get())
            {
                collapsePositions.add(pos.up()); // Check the above position
            }
        }
        addCollapseData(new CollapseData(centerPos, collapsePositions, maxRadiusSquared));
    }

    public void tick(World world)
    {
        if (!world.isRemote())
        {
            if (!collapsesInProgress.isEmpty() && RANDOM.nextInt(10) == 0)
            {
                for (CollapseData collapse : collapsesInProgress)
                {
                    Set<BlockPos> updatedPositions = new HashSet<>();
                    for (BlockPos posAt : collapse.nextPositions)
                    {
                        // Check the current position for collapsing
                        BlockState stateAt = world.getBlockState(posAt);
                        if (TFCTags.CAN_COLLAPSE.contains(stateAt.getBlock()) && TFCFallingBlockEntity.canFallThrough(world, posAt.down()) && posAt.distanceSq(collapse.centerPos) < collapse.radiusSquared && RANDOM.nextFloat() < TFCConfig.SERVER.collapsePropagateChance.get())
                        {
                            if (CollapseRecipe.collapseBlock(world, posAt, stateAt))
                            {
                                // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                                updatedPositions.add(posAt.up());
                            }
                        }
                    }
                    collapse.nextPositions.clear();
                    if (!updatedPositions.isEmpty())
                    {
                        world.playSound(null, collapse.centerPos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundCategory.BLOCKS, 0.6f, 1.0f);
                        collapse.nextPositions.addAll(updatedPositions);
                        collapse.radiusSquared *= 0.8; // lower radius each successive time
                    }
                }
                collapsesInProgress.removeIf(collapse -> collapse.nextPositions.isEmpty());
            }

            updateLandslidePositions();
            for (BlockPos pos : landslidePositions)
            {
                LandslideRecipe.tryLandslide(world, pos, world.getBlockState(pos));
            }
            landslidePositions.clear();
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        updateLandslidePositions();

        CompoundNBT nbt = new CompoundNBT();
        nbt.putLongArray("landslidePositions", landslidePositions.stream().mapToLong(BlockPos::toLong).toArray());
        ListNBT list = new ListNBT();
        for (CollapseData collapse : collapsesInProgress)
        {
            list.add(collapse.serializeNBT());
        }
        nbt.put("collapsesInProgress", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            landslidePositions.clear();
            collapsesInProgress.clear();

            landslidePositions.addAll(Arrays.stream(nbt.getLongArray("landslidePositions")).mapToObj(BlockPos::fromLong).collect(Collectors.toList()));
            ListNBT list = nbt.getList("collapsesInProgress", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
            {
                collapsesInProgress.add(new CollapseData(list.getCompound(i)));
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        return CapabilityWorldTracker.CAPABILITY.orEmpty(cap, capability);
    }

    private void updateLandslidePositions()
    {
        // Use a buffered list because iterating over it can cause landslides
        landslidePositions.addAll(landslidePositionsToAdd);
        landslidePositionsToAdd.clear();
    }
}

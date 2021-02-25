/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.worldtracker;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.util.FallingBlockManager;
import net.dries007.tfc.client.TFCSounds;

public class WorldTracker implements ICapabilitySerializable<NBTTagCompound>
{
    private static final Random RANDOM = new Random();

    private final List<CollapseData> collapsesInProgress;

    public WorldTracker()
    {
        this.collapsesInProgress = new ArrayList<>();
    }

    public void addCollapseData(CollapseData collapse)
    {
        collapsesInProgress.add(collapse);
    }

    public void tick(World world)
    {
        if (!world.isRemote)
        {
            if (!collapsesInProgress.isEmpty() && RANDOM.nextInt(20) == 0)
            {
                for (CollapseData collapse : collapsesInProgress)
                {
                    Set<BlockPos> updatedPositions = new ObjectOpenHashSet<>();
                    for (BlockPos posAt : collapse.nextPositions)
                    {
                        // Check the current position for collapsing
                        IBlockState stateAt = world.getBlockState(posAt);
                        FallingBlockManager.Specification specAt = FallingBlockManager.getSpecification(stateAt);
                        if (specAt != null && specAt.isCollapsable() && FallingBlockManager.canFallThrough(world, posAt.down(), Material.ROCK) && specAt.canCollapse(world, posAt) && posAt.distanceSq(collapse.centerPos) < collapse.radiusSquared && RANDOM.nextFloat() < ConfigTFC.General.FALLABLE.propagateCollapseChance)
                        {
                            IBlockState fallState = specAt.getResultingState(stateAt);
                            world.setBlockState(posAt, fallState);
                            FallingBlockManager.checkFalling(world, posAt, fallState, true);
                            // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                            updatedPositions.add(posAt.up());
                        }
                    }
                    collapse.nextPositions.clear();
                    if (!updatedPositions.isEmpty())
                    {
                        world.playSound(null, collapse.centerPos, TFCSounds.ROCK_SLIDE_SHORT, SoundCategory.BLOCKS, 0.6f, 1.0f);
                        collapse.nextPositions.addAll(updatedPositions);
                        collapse.radiusSquared *= 0.8; // lower radius each successive time
                    }
                }
                collapsesInProgress.removeIf(collapse -> collapse.nextPositions.isEmpty());
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (CollapseData collapse : collapsesInProgress)
        {
            list.appendTag(collapse.serializeNBT());
        }
        nbt.setTag("collapsesInProgress", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            collapsesInProgress.clear();
            NBTTagList list = nbt.getTagList("collapsesInProgress", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
            {
                collapsesInProgress.add(new CollapseData(list.getCompoundTagAt(i)));
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing enumFacing)
    {
        return capability == CapabilityWorldTracker.CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing enumFacing)
    {
        return hasCapability(capability, enumFacing) ? (T) this : null;
    }
}

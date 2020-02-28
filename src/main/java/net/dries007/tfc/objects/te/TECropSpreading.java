/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.objects.blocks.agriculture.BlockCropSpreading;

@ParametersAreNonnullByDefault
public class TECropSpreading extends TECropBase
{
    private int maxGrowthStage; // The max value this crop can grow to
    private int baseAge; // The current age, including all spreading attempts
    private boolean isSeedPlant; // Was the plant the initial one that was planted? (controls whether it should drop a seed or not)

    public TECropSpreading()
    {
        this.maxGrowthStage = 0;
        this.isSeedPlant = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        maxGrowthStage = nbt.getInteger("maxGrowthStage");
        baseAge = nbt.getInteger("baseAge");
        isSeedPlant = nbt.getBoolean("isSeedPlant");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("maxGrowthStage", maxGrowthStage);
        nbt.setInteger("baseAge", baseAge);
        nbt.setBoolean("isSeedPlant", isSeedPlant);
        return super.writeToNBT(nbt);
    }

    public void onPlaced()
    {
        IBlockState state = world.getBlockState(pos);
        BlockCropSpreading block = (BlockCropSpreading) state.getBlock();
        // Calculate initial max growth stage
        maxGrowthStage = 3 + state.getValue(block.getStageProperty());
        if (maxGrowthStage > block.getCrop().getMaxStage())
        {
            maxGrowthStage = block.getCrop().getMaxStage();
        }
        // Reset counter
        resetCounter();
    }

    public int getMaxGrowthStage()
    {
        return maxGrowthStage;
    }

    public void setMaxGrowthStage(int maxGrowthStage)
    {
        this.maxGrowthStage = maxGrowthStage;
        if (this.maxGrowthStage > 7)
        {
            this.maxGrowthStage = 7;
        }
        markDirty();
    }

    public int getBaseAge()
    {
        return baseAge;
    }

    public void setBaseAge(int baseAge)
    {
        this.baseAge = baseAge;
        markDirty();
    }

    public boolean isSeedPlant()
    {
        return isSeedPlant;
    }

    public void setSeedPlant(boolean seedPlant)
    {
        isSeedPlant = seedPlant;
        markDirty();
    }
}

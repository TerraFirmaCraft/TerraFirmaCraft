/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FarmlandTileEntity extends TFCTileEntity
{
    // NPK Nutrients
    private float nitrogen;
    private float phosphorous;
    private float potassium;

    public FarmlandTileEntity(BlockPos pos, BlockState state)
    {
        this(TFCTileEntities.FARMLAND.get(), pos, state);
    }

    protected FarmlandTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        nitrogen = phosphorous = potassium = 0;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        nitrogen = nbt.getFloat("nitrogen");
        phosphorous = nbt.getFloat("phosphorous");
        potassium = nbt.getFloat("potassium");
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putFloat("nitrogen", nitrogen);
        nbt.putFloat("phosphorous", phosphorous);
        nbt.putFloat("potassium", potassium);
        return super.save(nbt);
    }

    public float getNitrogen()
    {
        return nitrogen;
    }

    public void setNitrogen(float nitrogen)
    {
        this.nitrogen = nitrogen;
    }

    public float getPhosphorous()
    {
        return phosphorous;
    }

    public void setPhosphorous(float phosphorous)
    {
        this.phosphorous = phosphorous;
    }

    public float getPotassium()
    {
        return potassium;
    }

    public void setPotassium(float potassium)
    {
        this.potassium = potassium;
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FarmlandTileEntity extends TFCTileEntity
{
    // NPK Nutrients
    private float nitrogen;
    private float phosphorous;
    private float potassium;

    public FarmlandTileEntity()
    {
        this(TFCTileEntities.FARMLAND.get());
    }

    protected FarmlandTileEntity(BlockEntityType<?> type)
    {
        super(type);

        nitrogen = phosphorous = potassium = 0;
    }

    @Override
    public void load(BlockState state, CompoundTag nbt)
    {
        nitrogen = nbt.getFloat("nitrogen");
        phosphorous = nbt.getFloat("phosphorous");
        potassium = nbt.getFloat("potassium");
        super.load(state, nbt);
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

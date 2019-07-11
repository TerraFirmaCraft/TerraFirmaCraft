/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.egg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EggHandler implements IEgg, ICapabilitySerializable<NBTTagCompound>
{
    private boolean fertilized;
    private long hatchDay;
    private NBTTagCompound entitytag;

    public EggHandler()
    {
        this(null);
    }

    public EggHandler(@Nullable NBTTagCompound nbt)
    {
        deserializeNBT(nbt);
    }

    public EggHandler(boolean fertilized, long hatchDay, Entity entity)
    {
        this.fertilized = fertilized;
        this.hatchDay = hatchDay;
        this.entitytag = entity.serializeNBT();
    }

    @Override
    public long getHatchDay()
    {
        return hatchDay;
    }

    @Nullable
    @Override
    public Entity getEntity(World world)
    {
        return entitytag != null ? EntityList.createEntityFromNBT(entitytag, world) : null;
    }

    @Override
    public boolean isFertilized()
    {
        return fertilized;
    }

    public void setFertilized(@Nonnull Entity entity, long hatchDay)
    {
        this.fertilized = true;
        this.entitytag = entity.serializeNBT();
        this.hatchDay = hatchDay;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityEgg.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityEgg.CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        if (entitytag != null)
        {
            nbt.setBoolean("fertilized", fertilized);
            nbt.setLong("hatchDay", hatchDay);
            nbt.setTag("entity", entitytag);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("entity"))
        {
            fertilized = nbt.getBoolean("fertilized");
            hatchDay = nbt.getLong("hatchDay");
            entitytag = nbt.getCompoundTag("entity");
        }
        else
        {
            fertilized = false;
            entitytag = null;
            hatchDay = 0;
        }
    }
}

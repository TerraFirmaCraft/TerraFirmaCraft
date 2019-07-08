package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import net.dries007.tfc.util.DataSerializersTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

public abstract class EntityAnimalMammal extends EntityAnimalTFC
{
    protected static final DataParameter<Long> BREEDTIME = EntityDataManager.createKey(EntityAnimalMammal.class, DataSerializersTFC.LONG);
    protected static final DataParameter<Boolean> PREGNANT = EntityDataManager.createKey(EntityAnimalMammal.class, DataSerializers.BOOLEAN);
    private long breedTime; //Controls pregnancy for females, cooldown to the next breeding for males
    private boolean pregnant;

    public EntityAnimalMammal(World worldIn, boolean gender, long birthTime)
    {
        super(worldIn, gender, birthTime);
    }

    public boolean isPregnant()
    {
        return this.pregnant;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && pregnant && CalendarTFC.INSTANCE.getTotalTime() > breedTime + gestationTicks())
        {
            birthChildren();
            pregnant = false;
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(BREEDTIME, breedTime);
        getDataManager().register(PREGNANT, pregnant);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("pregnant", pregnant);
        nbt.setLong("breedTime", breedTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.pregnant = nbt.getBoolean("pregnant");
        this.breedTime = nbt.getLong("breedTime");
    }

    /**
     * Return the number of ticks for a full gestation
     *
     * @return long value in ticks
     */
    public abstract long gestationTicks();

    /**
     * Spawns childs of this animal
     */
    public abstract void birthChildren();

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable)
    {
        this.pregnant = !this.getGender(); //Female
        breedTime = CalendarTFC.INSTANCE.getTotalTime();
        return null;
    }
}

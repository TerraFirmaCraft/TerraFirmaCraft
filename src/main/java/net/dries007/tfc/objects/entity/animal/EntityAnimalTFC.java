/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import net.dries007.tfc.util.DataSerializersTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

public abstract class EntityAnimalTFC extends EntityAnimal
{
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Long> BIRTHTIME = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializersTFC.LONG);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);

    public EntityAnimalTFC(World worldIn, boolean gender, long birthTime)
    {
        super(worldIn);
        this.setGender(gender);
        this.setBirthTime(birthTime);
        this.setFamiliarity(0);
    }

    public boolean getGender()
    {
        return this.dataManager.get(GENDER);
    }

    public void setGender(boolean gender)
    {
        this.dataManager.set(GENDER, gender);
    }

    public long getBirthTime()
    {
        return this.dataManager.get(BIRTHTIME);
    }

    public void setBirthTime(long value)
    {
        this.dataManager.set(BIRTHTIME, value);
    }

    public float getFamiliarity()
    {
        return this.dataManager.get(FAMILIARITY);
    }

    public void setFamiliarity(float value)
    {
        this.dataManager.set(FAMILIARITY, value);
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.ticksExisted % CalendarTFC.TICKS_IN_HOUR == 0)
        {
            float familiarity = getFamiliarity();
            //Familiarity mechanics
            if (familiarity > 0f && familiarity < 0.3f)
            {
                familiarity -= 0.001f; //12.5 days to reset?
                if (familiarity < 0) familiarity = 0;
            }
            setFamiliarity(familiarity);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", getGender());
        nbt.setLong("birth", getBirthTime());
        nbt.setFloat("familiarity", getFamiliarity());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setGender(nbt.getBoolean("gender"));
        this.setBirthTime(nbt.getLong("birth"));
        this.setFamiliarity(nbt.getFloat("familiarity"));

    }

    /**
     * Used by models renderer to scale the size of the animal
     *
     * @return float value between 0(birthday) to 1(full grown adult)
     */
    public abstract float getPercentToAdulthood();

    /**
     * Get this entity age, based on birth
     *
     * @return the Age enum of this entity
     */
    public abstract Age getAge();

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHTIME, 0L);
        getDataManager().register(FAMILIARITY, 0f);
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == Age.CHILD;
    }

    public enum Age
    {
        CHILD, ADULT, OLD
    }
}

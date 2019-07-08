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
    protected static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Long> BIRTHTIME = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializersTFC.LONG);
    protected static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);
    private boolean gender; //Male = true, Female = false
    private long birthTime; //The time this entity was birth, from CalendarTFC#totalTime
    private float familiarity;

    public EntityAnimalTFC(World worldIn, boolean gender, long birthTime)
    {
        super(worldIn);
        this.gender = gender;
        this.birthTime = birthTime;
        this.familiarity = 0;
    }

    public boolean getGender()
    {
        return gender;
    }

    public long getBirthTime()
    {
        return birthTime;
    }

    public float getFamiliarity()
    {
        return familiarity;
    }

    public void addFamiliarity(float value)
    {
        this.familiarity += value;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.ticksExisted % CalendarTFC.TICKS_IN_HOUR == 0)
        {
            //Familiarity mechanics
            if (familiarity < 0.3f)
            {
                familiarity -= 0.001f; //12.5 days to reset?
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", gender);
        nbt.setLong("birthTime", birthTime);
        nbt.setFloat("familiarity", familiarity);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.gender = nbt.getBoolean("gender");
        this.birthTime = nbt.getLong("birthTime");
        this.familiarity = nbt.getFloat("familiarity");
    }

    /**
     * Get this entity age, based on {@link #birthTime}
     *
     * @return the Age of this entity
     */
    public abstract Age getAge();

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, gender);
        getDataManager().register(BIRTHTIME, birthTime);
        getDataManager().register(FAMILIARITY, familiarity);
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

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.agriculture.Nutrient;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.capability.player.CapabilityPlayer.MAX_PLAYER_NUTRIENTS;
import static net.dries007.tfc.api.capability.player.CapabilityPlayer.MIN_PLAYER_NUTRIENTS;

public class PlayerDataHandler implements IPlayerData, ICapabilitySerializable<NBTTagCompound>
{
    private static final float BASE_NUTRIENT = 0.8f, MAX_NUTRIENT = 1.0f, MIN_NUTRIENT = 0.5f; //To determine max health
    private final float[] nutrients;
    private long lastUpdateTick;
    private float thirst;

    public PlayerDataHandler()
    {
        this(null);
    }

    public PlayerDataHandler(@Nullable NBTTagCompound nbt)
    {
        nutrients = new float[Nutrient.TOTAL];
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = BASE_NUTRIENT * MAX_PLAYER_NUTRIENTS;
        }
        thirst = 70.0f;
        deserializeNBT(nbt);
    }

    @Override
    public float getNutrient(Nutrient nutrient)
    {
        updateData();
        return nutrients[nutrient.ordinal()];
    }

    @Override
    public float[] getNutrients()
    {
        updateData();
        return nutrients;
    }

    @Override
    public void setNutrients(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
    }

    @Override
    public void setNutrient(Nutrient nutrient, float amount)
    {
        if (amount < MIN_PLAYER_NUTRIENTS)
        {
            nutrients[nutrient.ordinal()] = MIN_PLAYER_NUTRIENTS;
        }
        else if (amount > MAX_PLAYER_NUTRIENTS)
        {
            nutrients[nutrient.ordinal()] = MAX_PLAYER_NUTRIENTS;
        }
        else
        {
            nutrients[nutrient.ordinal()] = amount;
        }
    }

    @Override
    public void addNutrient(Nutrient nutrient, float amount)
    {
        updateData();
        float newAmount = nutrients[nutrient.ordinal()] + amount;
        setNutrient(nutrient, newAmount);
    }

    public void updateNutrientsFastForward()
    {
        lastUpdateTick = CalendarTFC.getCalendarTime();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayer.CAPABILITY_PLAYER_DATA;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayer.CAPABILITY_PLAYER_DATA ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        updateData();

        NBTTagCompound nbt = new NBTTagCompound();
        for (Nutrient nutrient : Nutrient.values())
        {
            nbt.setFloat(nutrient.name().toLowerCase(), this.nutrients[nutrient.ordinal()]);
        }
        nbt.setFloat("thirst", thirst);
        nbt.setLong("lastUpdateTick", lastUpdateTick);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            for (Nutrient nutrient : Nutrient.values())
            {
                nutrients[nutrient.ordinal()] = nbt.getFloat(nutrient.name().toLowerCase());
            }
            thirst = nbt.getFloat("thirst");
            lastUpdateTick = nbt.getLong("lastUpdateTick");
        }
    }

    private void updateData()
    {
        int ticksPassed = (int) (CalendarTFC.getCalendarTime() - lastUpdateTick);
        for (Nutrient nutrient : Nutrient.values())
        {
            setNutrient(nutrient, nutrients[nutrient.ordinal()] - (float) (ConfigTFC.GENERAL.playerNutritionDecayModifier * nutrient.getDecayModifier() * ticksPassed));
        }
        thirst -= (float) (ConfigTFC.GENERAL.playerThirstModifier * ticksPassed / 240);
        if(thirst < 0)thirst = 0;
        lastUpdateTick = CalendarTFC.getCalendarTime();
    }

    @Override
    public float getMaxHealth()
    {
        float average = 0;
        for (Nutrient nutrient : Nutrient.values())
        {
            average += this.nutrients[nutrient.ordinal()];
        }
        average = average / Nutrient.TOTAL / MAX_PLAYER_NUTRIENTS;
        if(average < BASE_NUTRIENT)
        {
            //Less than base health
            if(average <= MIN_NUTRIENT)return ConfigTFC.GENERAL.playerMinHealth;
            float difNutrientValue = BASE_NUTRIENT - MIN_NUTRIENT;
            int difHealthValue = ConfigTFC.GENERAL.playerBaseHealth - ConfigTFC.GENERAL.playerMinHealth;
            average -= MIN_NUTRIENT;
            return average * difHealthValue / difNutrientValue + ConfigTFC.GENERAL.playerMinHealth;
        }else{
            //More than base health;
            if(average >= MAX_NUTRIENT)return ConfigTFC.GENERAL.playerMaxHealth;
            float difNutrientValue = MAX_NUTRIENT - BASE_NUTRIENT;
            int difHealthValue = ConfigTFC.GENERAL.playerMaxHealth - ConfigTFC.GENERAL.playerBaseHealth;
            average -= BASE_NUTRIENT;
            return average * difHealthValue / difNutrientValue + ConfigTFC.GENERAL.playerBaseHealth;
        }
    }

    @Override
    public float getThirst()
    {
        return this.thirst;
    }

    @Override
    public void setThirst(float value)
    {
        this.thirst = value;
    }

    @Override
    public void drink(float value)
    {
        this.thirst += value;
        if(this.thirst > 100)this.thirst = 100;
        if(this.thirst < 0)this.thirst = 0;
    }
}

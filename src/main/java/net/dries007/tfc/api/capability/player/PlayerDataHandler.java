/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.agriculture.Nutrient;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.capability.player.CapabilityPlayer.MAX_PLAYER_NUTRIENTS;
import static net.dries007.tfc.api.capability.player.CapabilityPlayer.MIN_PLAYER_NUTRIENTS;

public class PlayerDataHandler implements IPlayerData, ICapabilitySerializable<NBTTagCompound>
{
    //To determine max health, compare (averageSkills + averageNutrients) / 2.0f to these
    private static final float MAX_HEALTH_THRESHOULD = 1.0f, BASE_HEALTH_THRESHOULD = 0.4f, MIN_HEALTH_THRESHOULD = 0.1f;
    private static final int PLACEHOLDER_SKILL_TOTAL = 1; //TODO Implement this
    private final float[] nutrients;
    private final float[] skills;
    private long lastUpdateTick, lastDrinkTick;
    private float thirst;

    public PlayerDataHandler()
    {
        this(null);
    }

    public PlayerDataHandler(@Nullable NBTTagCompound nbt)
    {
        nutrients = new float[Nutrient.TOTAL];
        skills = new float[PLACEHOLDER_SKILL_TOTAL]; //TODO change this to real value later
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = 0.8f * MAX_PLAYER_NUTRIENTS;
        }
        for (int i = 0; i < skills.length; i++)
        {
            skills[i] = 0;
        }
        thirst = 70.0f;
        deserializeNBT(nbt);
    }

    @Override
    public float getNutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    @Override
    public float[] getNutrients()
    {
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
        float newAmount = nutrients[nutrient.ordinal()] + amount;
        setNutrient(nutrient, newAmount);
    }

    @Override
    public void onUpdate(@Nonnull EntityPlayer player)
    {
        int ticksPassed = (int) (CalendarTFC.getCalendarTime() - lastUpdateTick);
        for (Nutrient nutrient : Nutrient.values())
        {
            setNutrient(nutrient, nutrients[nutrient.ordinal()] - (float) (ConfigTFC.GENERAL.playerNutritionDecayModifier * nutrient.getDecayModifier() * ticksPassed));
        }
        //Reduces thirst bar for normal living
        thirst -= (float) (ConfigTFC.GENERAL.playerThirstModifier * ticksPassed / 240);
        if(player.getFoodStats().foodExhaustionLevel >= 4.0F){
            thirst -= ConfigTFC.GENERAL.playerThirstModifier * 4.0F;
        }
        if (thirst < 0) thirst = 0;
        lastUpdateTick = CalendarTFC.getCalendarTime();
    }

    @Override
    public float getMaxHealth()
    {
        float nutrientPercent = getNutrientAverage() / MAX_PLAYER_NUTRIENTS;
        float skillPercent = getSkillAverage() / 100; //MAX_PLAYER_SKILLS; This should return a value between 0 and 1.0F
        float averagePercent = (nutrientPercent + skillPercent) / 2.0f;
        float maxHealth;
        if (averagePercent < BASE_HEALTH_THRESHOULD)
        {
            //Less than base health
            if (averagePercent <= MIN_HEALTH_THRESHOULD) return ConfigTFC.GENERAL.playerMinHealth;
            float difNutrientValue = BASE_HEALTH_THRESHOULD - MIN_HEALTH_THRESHOULD;
            int difHealthValue = ConfigTFC.GENERAL.playerBaseHealth - ConfigTFC.GENERAL.playerMinHealth;
            averagePercent -= MIN_HEALTH_THRESHOULD;
            maxHealth = averagePercent * difHealthValue / difNutrientValue + ConfigTFC.GENERAL.playerMinHealth;
        }
        else
        {
            //More than base health;
            if (averagePercent >= MAX_HEALTH_THRESHOULD) return ConfigTFC.GENERAL.playerMaxHealth;
            float difNutrientValue = MAX_HEALTH_THRESHOULD - BASE_HEALTH_THRESHOULD;
            int difHealthValue = ConfigTFC.GENERAL.playerMaxHealth - ConfigTFC.GENERAL.playerBaseHealth;
            averagePercent -= BASE_HEALTH_THRESHOULD;
            maxHealth = averagePercent * difHealthValue / difNutrientValue + ConfigTFC.GENERAL.playerBaseHealth;
        }
        return maxHealth;
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
        if (this.thirst > 100) this.thirst = 100;
        if (this.thirst < 0) this.thirst = 0;
    }

    @Override
    public boolean drink(float value)
    {
        int ticksPassed = (int) (CalendarTFC.getCalendarTime() - lastDrinkTick);
        if (ticksPassed < 30 || this.thirst > 95)
            return false; //One sip per one and a half sec and stops after almost full(to stop drinking after that)
        lastDrinkTick = CalendarTFC.getCalendarTime();
        this.setThirst(this.thirst + value);
        return true;
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
        NBTTagCompound nbt = new NBTTagCompound();
        for (Nutrient nutrient : Nutrient.values())
        {
            nbt.setFloat(nutrient.name().toLowerCase(), this.nutrients[nutrient.ordinal()]);
        }
        //TODO serialize skills
        nbt.setFloat("thirst", thirst);
        nbt.setLong("lastUpdateTick", lastUpdateTick);
        nbt.setLong("lastDrinkTick", lastDrinkTick);
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
            //TODO deserialize skills
            thirst = nbt.getFloat("thirst");
            lastUpdateTick = nbt.getLong("lastUpdateTick");
            lastDrinkTick = nbt.getLong("lastDrinkTick");
        }
    }

    private float getNutrientAverage()
    {
        float total = 0;
        for (Nutrient nutrient : Nutrient.values())
        {
            total += this.nutrients[nutrient.ordinal()];
        }
        return total / Nutrient.TOTAL;
    }

    private float getSkillAverage()
    {
        //TODO implement this like getNutrientAverage()
        return 0;
    }


}

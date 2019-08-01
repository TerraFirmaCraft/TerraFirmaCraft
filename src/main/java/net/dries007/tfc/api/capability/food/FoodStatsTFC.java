/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketFoodStatsUpdate;
import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public class FoodStatsTFC extends FoodStats implements IFoodStatsTFC
{
    private final EntityPlayer sourcePlayer;
    private final FoodStats originalStats;
    private final float[] nutrients;
    private long lastDrinkTick;
    private float thirst;

    public FoodStatsTFC(EntityPlayer sourcePlayer, FoodStats originalStats)
    {
        this.sourcePlayer = sourcePlayer;
        this.originalStats = originalStats;
        this.nutrients = new float[Nutrient.TOTAL];
        this.thirst = MAX_PLAYER_THIRST;

        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = 0.8f * MAX_PLAYER_NUTRIENTS;
        }
    }

    @Override
    public void addStats(int hungerAmount, float saturationAmount)
    {
        // In TFC, all foods have a constant amount of food filled (In 1.7.10 this was 5oz, out of a 24 oz stomach.) We will go with real units, and just make this 1/5 of the sourcePlayer's food bar (4 haunches)
        // However, old vanilla foods have a bonus to their saturation based on their hunger value
        // Vanilla foods are generally in the range 0.0 - 1.0 for saturation, and 1 - 8 for hunger
        float extraSaturationModifier = 1f + (hungerAmount - FOOD_HUNGER_AMOUNT) * 0.125f;
        originalStats.addStats(FOOD_HUNGER_AMOUNT, saturationAmount * extraSaturationModifier);
    }

    @Override
    public void addStats(ItemFood foodItem, ItemStack stack)
    {
        // Eating items has nutritional benefits
        IFood foodCap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (foodCap != null)
        {
            if (!foodCap.isRotten())
            {
                // Add nutrients
                for (Nutrient nutrient : Nutrient.values())
                {
                    addNutrient(nutrient.ordinal(), foodCap.getNutrient(stack, nutrient));
                }

                // Add water
                addThirst(foodCap.getWater());

                // Add food
                originalStats.addStats(FOOD_HUNGER_AMOUNT, foodCap.getCalories());
            }
            else
            {
                // Minor effects from eating rotten food
                if (Constants.RNG.nextFloat() < 0.6)
                {
                    sourcePlayer.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1800, 1));
                    if (Constants.RNG.nextFloat() < 0.15)
                    {
                        sourcePlayer.addPotionEffect(new PotionEffect(MobEffects.POISON, 1800, 0));
                    }
                }
            }
        }
        else
        {
            // Default behavior, shouldn't happen except in *very* special cases
            originalStats.addStats(foodItem, stack);
        }
    }

    /**
     * Called from {@link EntityPlayer#onUpdate()} on server side only
     *
     * @param player the player who's food stats this is
     */
    @Override
    public void onUpdate(EntityPlayer player)
    {
        EnumDifficulty difficulty = player.world.getDifficulty();

        if (difficulty != EnumDifficulty.PEACEFUL)
        {
            // First, we check exhaustion, to decrement thirst
            if (originalStats.foodExhaustionLevel >= 4.0F)
            {
                addThirst(-(float) ConfigTFC.GENERAL.playerThirstModifier);
            }

            // Then, we decrement nutrients
            for (int i = 0; i < nutrients.length; i++)
            {
                addNutrient(i, -(float) ConfigTFC.GENERAL.playerNutritionDecayModifier);
            }
        }

        // Next, update the original food stats
        originalStats.onUpdate(player);

        // Last, apply negative effects due to thirst
        if (!player.capabilities.isCreativeMode)
        {
            if (player.ticksExisted % 100 == 0)
            {
                if (thirst < 10f)
                {
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 1, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 160, 1, false, false));
                    if (thirst <= 0f)
                    {
                        // Hurt the player, same as starvation
                        player.attackEntityFrom(DamageSource.STARVE, 1);
                    }
                }
                else if (thirst < 20f)
                {
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 0, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 160, 0, false, false));
                }
            }
        }

        // Since this is only called server side, and vanilla has a custom packet for this stuff, we need our own
        if (player instanceof EntityPlayerMP)
        {
            TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsUpdate(nutrients, thirst), (EntityPlayerMP) player);
        }
    }

    @Override
    public void readNBT(NBTTagCompound nbt)
    {
        // Thirst
        thirst = nbt.getFloat("thirst");
        lastDrinkTick = nbt.getLong("lastDrinkTick");

        // Nutrients
        for (Nutrient nutrient : Nutrient.values())
        {
            nutrients[nutrient.ordinal()] = nbt.getFloat(nutrient.name().toLowerCase());
        }

        // Food
        originalStats.readNBT(nbt);
    }

    @Override
    public void writeNBT(NBTTagCompound nbt)
    {
        // Thirst
        nbt.setFloat("thirst", thirst);
        nbt.setFloat("lastDrinkTick", lastDrinkTick);

        // Nutrients
        for (Nutrient nutrient : Nutrient.values())
        {
            nbt.setFloat(nutrient.name().toLowerCase(), this.nutrients[nutrient.ordinal()]);
        }

        // Food
        originalStats.writeNBT(nbt);
    }

    @Override
    public int getFoodLevel()
    {
        return originalStats.getFoodLevel();
    }

    @Override
    public boolean needFood()
    {
        return originalStats.needFood();
    }

    @Override
    public void addExhaustion(float exhaustion)
    {
        originalStats.addExhaustion(exhaustion);
    }

    @Override
    public void setFoodLevel(int foodLevelIn)
    {
        originalStats.setFoodLevel(foodLevelIn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setFoodSaturationLevel(float foodSaturationLevelIn)
    {
        originalStats.setFoodSaturationLevel(foodSaturationLevelIn);
    }

    @SideOnly(Side.CLIENT)
    public void onReceivePacket(float[] nutrients, float thirst)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
        this.thirst = thirst;
    }

    @Override
    public float getHealthModifier()
    {
        float totalNutrients = 0;
        for (float nutrient : nutrients)
        {
            totalNutrients += nutrient;
        }
        return 0.2f + totalNutrients / (MAX_PLAYER_NUTRIENTS * Nutrient.TOTAL);
    }

    @Override
    public float getThirst()
    {
        return thirst;
    }

    @Override
    public boolean attemptDrink(float value)
    {
        int ticksPassed = (int) (CalendarTFC.TOTAL_TIME.getTicks() - lastDrinkTick);
        if (ticksPassed >= 20 && thirst < MAX_PLAYER_THIRST)
        {
            // One drink every so often
            lastDrinkTick = CalendarTFC.TOTAL_TIME.getTicks();
            addThirst(value);
            return true;
        }
        return false;
    }

    @Override
    public void addThirst(float value)
    {
        this.thirst += value;
        if (thirst < 0)
        {
            thirst = 0;
        }
        if (thirst > MAX_PLAYER_THIRST)
        {
            thirst = MAX_PLAYER_THIRST;
        }
    }

    @Override
    public float getNutrient(@Nonnull Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    /**
     * Sets the nutrient value directly. Used by command nutrients and for debug purposes
     *
     * @param nutrient the nutrient to set
     * @param value    the value to set to, in [0, 100]
     */
    @Override
    public void setNutrient(@Nonnull Nutrient nutrient, float value)
    {
        setNutrient(nutrient.ordinal(), value);
    }

    private void addNutrient(int index, float amount)
    {
        setNutrient(index, nutrients[index] + amount);
    }

    private void setNutrient(int index, float amount)
    {
        nutrients[index] = amount;
        if (nutrients[index] < 0)
        {
            nutrients[index] = 0;
        }
        else if (nutrients[index] > MAX_PLAYER_NUTRIENTS)
        {
            nutrients[index] = MAX_PLAYER_NUTRIENTS;
        }
    }
}

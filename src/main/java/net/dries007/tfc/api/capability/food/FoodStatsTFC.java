/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.Arrays;
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
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class FoodStatsTFC extends FoodStats implements IFoodStatsTFC
{
    public static final float PASSIVE_HEAL_AMOUNT = 20 * 0.0002f; // On the display: 1 HP / 5 seconds
    public static final float EXHAUSTION_MULTIPLIER = 0.4f; // Multiplier for vanilla sources of exhaustion (we use passive exhaustion to keep hunger decaying even when not sprinting everywhere. That said, vanilla exhaustion should be reduced to compensate
    public static final float PASSIVE_EXHAUSTION = 20f * 4f / (2.5f * ICalendar.TICKS_IN_DAY); // Passive exhaustion will deplete your food bar once every 2.5 days. Food bar holds ~5 "meals", this requires two per day
    public static final DamageSource DEHYDRATION = (new DamageSource("dehydration")).setDamageBypassesArmor().setDamageIsAbsolute(); // Same as starvation, but another message on death

    private final EntityPlayer sourcePlayer;
    private final FoodStats originalStats;
    private final float[] nutrients;
    private long lastDrinkTick;
    private float thirst;
    private int healTimer;

    public FoodStatsTFC(EntityPlayer sourcePlayer, FoodStats originalStats)
    {
        this.sourcePlayer = sourcePlayer;
        this.originalStats = originalStats;
        this.nutrients = new float[Nutrient.TOTAL];
        this.thirst = MAX_PLAYER_THIRST;

        Arrays.fill(nutrients, 0.8f * MAX_PLAYER_NUTRIENTS);
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

        // Extra-Peaceful Difficulty
        if (difficulty == EnumDifficulty.PEACEFUL && ConfigTFC.GENERAL.peacefulDifficultyPassiveRegeneration)
        {
            // Copied / Modified from EntityPlayer#onLivingUpdate
            if (player.shouldHeal() && player.ticksExisted % 20 == 0)
            {
                player.heal(1.0F);
            }

            if (player.ticksExisted % 10 == 0)
            {
                if (needFood())
                {
                    player.foodStats.setFoodLevel(player.foodStats.getFoodLevel() + 1);
                }

                if (thirst < MAX_PLAYER_THIRST)
                {
                    addThirst(5f);
                }

                for (int i = 0; i < nutrients.length; i++)
                {
                    addNutrient(i, 5f);
                }
            }
        }
        else
        {
            // Passive exhaustion - call the source player instead of the local method
            player.addExhaustion(PASSIVE_EXHAUSTION * (float) ConfigTFC.GENERAL.foodPassiveExhaustionMultiplier);

            // Same check as the original food stats, so hunger, thirst, and nutrition loss are synced
            if (originalStats.foodExhaustionLevel >= 4.0F)
            {
                addThirst(-(float) ConfigTFC.GENERAL.playerThirstModifier);

                // Nutrition only decays when food decays. The base ratio (in config), is 0.8 nutrition / haunch
                if (getSaturationLevel() <= 0f)
                {
                    for (int i = 0; i < nutrients.length; i++)
                    {
                        addNutrient(i, -(float) ConfigTFC.GENERAL.playerNutritionDecayModifier);
                    }
                }
            }

            if (difficulty == EnumDifficulty.PEACEFUL)
            {
                // Copied from vanilla's food stats, so we consume food in peaceful mode (would normally be part of the super.onUpdate call
                if (originalStats.foodExhaustionLevel > 4.0F)
                {
                    setFoodLevel(Math.max(getFoodLevel() - 1, 0));
                }
            }
        }

        // Next, update the original food stats
        originalStats.onUpdate(player);

        // Apply custom TFC regeneration
        if (player.shouldHeal() && getFoodLevel() >= 4.0f && getThirst() > 20f)
        {
            healTimer++;
            float multiplier = 1;
            if (getFoodLevel() > 16.0f && getThirst() > 80f)
            {
                multiplier = 3;
            }

            if (healTimer > 10)
            {
                player.heal(multiplier * PASSIVE_HEAL_AMOUNT * (float) ConfigTFC.GENERAL.playerNaturalRegenerationModifier);
                healTimer = 0;
            }
        }

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
                        player.attackEntityFrom(DEHYDRATION, 1);
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
        originalStats.addExhaustion(EXHAUSTION_MULTIPLIER * exhaustion);
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
    public boolean attemptDrink(float value, boolean simulate)
    {
        int ticksPassed = (int) (sourcePlayer.world.getTotalWorldTime() - lastDrinkTick);
        if (ticksPassed >= 20 && thirst < MAX_PLAYER_THIRST)
        {
            if (!simulate)
            {
                // One drink every so often
                lastDrinkTick = sourcePlayer.world.getTotalWorldTime();
                addThirst(value);
            }
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
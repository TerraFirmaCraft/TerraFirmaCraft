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
    private final NutritionStats nutritionStats; // Separate handler for nutrition, because it's a bit complex
    private long lastDrinkTick;
    private float thirst;
    private int healTimer;

    public FoodStatsTFC(EntityPlayer sourcePlayer, FoodStats originalStats)
    {
        this.sourcePlayer = sourcePlayer;
        this.originalStats = originalStats;
        this.nutritionStats = new NutritionStats(0.5f);
        this.thirst = MAX_PLAYER_THIRST;
    }

    @Override
    public void addStats(int hungerAmount, float saturationAmount)
    {
        // This should never be called directly - when it is we assume it's direct stat modifications (saturation potion, eating cake)
        // We make modifications to vanilla logic, as saturation needs to be unaffected by hunger
        // todo: handle cake
        foodLevel = Math.min(hungerAmount + foodLevel, 20);
        foodSaturationLevel = Math.min(foodSaturationLevel + saturationAmount, foodLevel);
    }

    @Override
    public void addStats(ItemFood foodItem, ItemStack stack)
    {
        // Eating items has nutritional benefits
        IFood foodCap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (foodCap != null)
        {
            FoodData data = foodCap.getData();
            if (!foodCap.isRotten())
            {
                addThirst(data.getWater());
                nutritionStats.addNutrients(data);
                addStats(data.getHunger(), data.getSaturation());
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
            }
        }
        else
        {
            // Passive exhaustion - call the source player instead of the local method
            player.addExhaustion(PASSIVE_EXHAUSTION / EXHAUSTION_MULTIPLIER * (float) ConfigTFC.GENERAL.foodPassiveExhaustionMultiplier);

            // Same check as the original food stats, so hunger and thirst loss are synced
            if (originalStats.foodExhaustionLevel >= 4.0F)
            {
                addThirst(-(float) ConfigTFC.GENERAL.playerThirstModifier);
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
            TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsUpdate(nutritionStats.getNutrients(), thirst), (EntityPlayerMP) player);
        }
    }

    @Override
    public void readNBT(NBTTagCompound nbt)
    {
        // Thirst
        thirst = nbt.getFloat("thirst");
        lastDrinkTick = nbt.getLong("lastDrinkTick");

        // Nutrients
        nutritionStats.deserializeNBT(nbt.getCompoundTag("nutrients"));

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
        nbt.setTag("nutrients", nutritionStats.serializeNBT());

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
        this.nutritionStats.onReceivePacket(nutrients);
        this.thirst = thirst;
    }

    @Override
    public float getHealthModifier()
    {
        return 0.25f + 1.5f * nutritionStats.getAverageNutrition();
    }

    @Override
    public float getThirst()
    {
        return thirst;
    }

    @Nonnull
    @Override
    public NutritionStats getNutrition()
    {
        return nutritionStats;
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
                resetCooldown();
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
}
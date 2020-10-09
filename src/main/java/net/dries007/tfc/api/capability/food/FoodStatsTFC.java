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
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketFoodStatsReplace;
import net.dries007.tfc.network.PacketFoodStatsUpdate;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.DamageSourcesTFC;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class FoodStatsTFC extends FoodStats implements IFoodStatsTFC
{
    public static final float PASSIVE_HEAL_AMOUNT = 20 * 0.0002f; // On the display: 1 HP / 5 seconds
    public static final float EXHAUSTION_MULTIPLIER = 0.4f; // Multiplier for vanilla sources of exhaustion (we use passive exhaustion to keep hunger decaying even when not sprinting everywhere. That said, vanilla exhaustion should be reduced to compensate
    public static final float PASSIVE_EXHAUSTION = 20f * 4f / (2.5f * ICalendar.TICKS_IN_DAY); // Passive exhaustion will deplete your food bar once every 2.5 days. Food bar holds ~5 "meals", this requires two per day

    public static void replaceFoodStats(EntityPlayer player)
    {
        // Only replace the server player's stats if they aren't already
        if (!(player.getFoodStats() instanceof IFoodStatsTFC))
        {
            player.foodStats = new FoodStatsTFC(player, player.getFoodStats());
        }
        // Send the update regardless so the client can perform the same logic
        if (player instanceof EntityPlayerMP)
        {
            TerraFirmaCraft.getNetwork().sendTo(new PacketFoodStatsReplace(), (EntityPlayerMP) player);
        }
    }

    private final EntityPlayer sourcePlayer;
    private final FoodStats originalStats; // We keep this here to do normal vanilla tracking (rather than using super). This is also friendlier to other mods if they replace this
    private final NutritionStats nutritionStats; // Separate handler for nutrition, because it's a bit complex
    private long lastDrinkTick;
    private float thirst;
    private int healTimer;

    public FoodStatsTFC(EntityPlayer sourcePlayer, FoodStats originalStats)
    {
        this.sourcePlayer = sourcePlayer;
        this.originalStats = originalStats;
        this.nutritionStats = new NutritionStats(0.5f, 0.0f);
        this.thirst = MAX_PLAYER_THIRST;
    }

    @Override
    public void addStats(int hungerAmount, float saturationAmount)
    {
        // This should never be called directly - when it is we assume it's direct stat modifications (saturation potion, eating cake)
        // We make modifications to vanilla logic, as saturation needs to be unaffected by hunger
    }

    @Override
    public void addStats(ItemFood foodItem, ItemStack stack)
    {
        IFood foodCap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (foodCap != null)
        {
            addStats(foodCap);
        }
        else
        {
            TerraFirmaCraft.getLog().info("Player ate a weird food: {} / {} that was not a food capability but was an ItemFood...", foodItem, stack);
        }
    }

    @Override
    public void addStats(IFood foodCap)
    {
        // Eating items has nutritional benefits
        FoodData data = foodCap.getData();
        if (!foodCap.isRotten())
        {
            addThirst(data.getWater());
            nutritionStats.addNutrients(data);

            // In order to get the exact saturation we want, apply this scaling factor here
            originalStats.addStats(data.getHunger(), data.getSaturation() / (2f * data.getHunger()));
        }
        else if (this.sourcePlayer instanceof EntityPlayerMP) // Check for server side first
        {
            // Minor effects from eating rotten food
            if (Constants.RNG.nextFloat() < 0.6)
            {
                sourcePlayer.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1800, 1));
                if (Constants.RNG.nextFloat() < 0.15)
                {
                    sourcePlayer.addPotionEffect(new PotionEffect(PotionEffectsTFC.FOOD_POISON, 1800, 0));
                }
            }
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
        if (difficulty == EnumDifficulty.PEACEFUL && ConfigTFC.General.PLAYER.peacefulDifficultyPassiveRegeneration)
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
                    setFoodLevel(getFoodLevel() + 1);
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
            player.addExhaustion(PASSIVE_EXHAUSTION / EXHAUSTION_MULTIPLIER * (float) ConfigTFC.General.PLAYER.passiveExhaustionMultiplier);

            // Same check as the original food stats, so hunger and thirst loss are synced
            if (originalStats.foodExhaustionLevel >= 4.0F)
            {
                addThirst(-(float) ConfigTFC.General.PLAYER.thirstModifier);
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
                player.heal(multiplier * PASSIVE_HEAL_AMOUNT * (float) ConfigTFC.General.PLAYER.naturalRegenerationModifier);
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
                        player.attackEntityFrom(DamageSourcesTFC.DEHYDRATION, 1);
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

    /**
     * Use instead of {@link FoodStats#setFoodSaturationLevel(float)} as it's client only
     */
    public void setSaturation(float saturation)
    {
        originalStats.foodSaturationLevel = saturation;
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

    @Override
    public void setThirst(float thirst)
    {
        this.thirst = thirst;
        if (thirst < 0)
        {
            this.thirst = 0;
        }
        if (thirst > MAX_PLAYER_THIRST)
        {
            this.thirst = MAX_PLAYER_THIRST;
        }
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
        if (ticksPassed >= ConfigTFC.General.PLAYER.drinkDelay && thirst < MAX_PLAYER_THIRST)
        {
            if (!simulate)
            {
                // One drink every so often
                resetCooldown();
                addThirst(value);
                // Salty drink effect
                if (value < 0 && Constants.RNG.nextDouble() < ConfigTFC.General.PLAYER.chanceThirstOnSaltyDrink)
                {
                    sourcePlayer.addPotionEffect(new PotionEffect(PotionEffectsTFC.THIRST, 600, 0));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void resetCooldown()
    {
        // Using total world time is okay here because it's done on a per-player basis
        lastDrinkTick = sourcePlayer.world.getTotalWorldTime();
    }
}
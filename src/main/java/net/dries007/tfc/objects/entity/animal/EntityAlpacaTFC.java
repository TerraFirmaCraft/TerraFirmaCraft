/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

/**
 * A Sheep of the colder regions!
 * Actually, they produce wool faster, but takes longer to reach maturity, have long gestation periods and only give birth to one individual
 */
@ParametersAreNonnullByDefault
public class EntityAlpacaTFC extends EntitySheepTFC implements ILivestock
{
    @SuppressWarnings("unused")
    public EntityAlpacaTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(ConfigTFC.Animals.ALPACA.adulthood, ConfigTFC.Animals.ALPACA.elder), EntitySheep.getRandomSheepColor(Constants.RNG));
    }

    public EntityAlpacaTFC(World worldIn, Gender gender, int birthDay, EnumDyeColor dye)
    {
        super(worldIn, gender, birthDay, dye);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.TUNDRA || biomeType == BiomeHelper.BiomeType.TAIGA))
        {
            return ConfigTFC.Animals.ALPACA.rarity;
        }
        return 0;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.ALPACA.babies;
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityAlpacaTFC baby = new EntityAlpacaTFC(world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays(), getDyeColor());
            baby.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            baby.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
            world.spawnEntity(baby);
        }
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack)
    {
        // Check for rotten
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (!ConfigTFC.Animals.ALPACA.acceptRotten && cap != null && cap.isRotten())
        {
            return false;
        }
        // Check if item is accepted
        for (String input : ConfigTFC.Animals.ALPACA.food)
        {
            String[] split = input.split(":");
            if (split.length == 2)
            {
                // Check for ore tag first
                if (split[0].equals("ore"))
                {
                    if (OreDictionaryHelper.doesStackMatchOre(stack, split[1]))
                    {
                        return true;
                    }
                }
                else
                {
                    try
                    {
                        String item = split[1];
                        int meta = -1;
                        // Parse meta if specified
                        if (split[1].contains(" "))
                        {
                            String[] split2 = split[1].split(" ");
                            item = split2[0];
                            meta = Integer.parseInt(split2[1]);
                        }
                        // Check for item registry name
                        ResourceLocation location = new ResourceLocation(split[0], item);
                        if (location.equals(stack.getItem().getRegistryName()))
                        {
                            if (meta == -1 || meta == stack.getMetadata())
                            {
                                return true;
                            }
                        }
                    }
                    catch (NumberFormatException ignored)
                    {
                    }
                }
            }
        }
        return false;
    }

    @Override
    public long gestationDays()
    {
        return ConfigTFC.Animals.ALPACA.gestation;
    }

    @Override
    public double getOldDeathChance()
    {
        return ConfigTFC.Animals.ALPACA.oldDeathChance;
    }

    @Override
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.ALPACA.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.ALPACA.elder;
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, ConfigTFC.Animals.ALPACA.woolTicks + getShearedTick() - CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return TFCSounds.ANIMAL_ALPACA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.ANIMAL_ALPACA_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSounds.ANIMAL_ALPACA_CRY : TFCSounds.ANIMAL_ALPACA_SAY;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_ALPACA;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        playSound(TFCSounds.ANIMAL_ALPACA_STEP, 0.15F, 1.0F);
    }
}

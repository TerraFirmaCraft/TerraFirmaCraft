/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.IEgg;
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
 * A Chicken of the colder regions!
 * Actually, ducks takes longer to reach maturity, but hey, they are cute!
 */
@ParametersAreNonnullByDefault
public class EntityDuckTFC extends EntityChickenTFC implements ILivestock
{
    public EntityDuckTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(ConfigTFC.Animals.DUCK.adulthood, ConfigTFC.Animals.DUCK.elder));
    }

    public EntityDuckTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.9F, 0.9F);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS || biomeType == BiomeHelper.BiomeType.TEMPERATE_FOREST))
        {
            return ConfigTFC.Animals.DUCK.rarity;
        }
        return 0;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.DUCK.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.DUCK.elder;
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack)
    {
        // Check for rotten
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (!ConfigTFC.Animals.DONKEY.acceptRotten && cap != null && cap.isRotten())
        {
            return false;
        }
        // Check if item is accepted
        for (String input : ConfigTFC.Animals.DONKEY.food)
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
    public List<ItemStack> getProducts()
    {
        List<ItemStack> eggs = new ArrayList<>();
        ItemStack egg = new ItemStack(Items.EGG);
        if (this.isFertilized())
        {
            IEgg cap = egg.getCapability(CapabilityEgg.CAPABILITY, null);
            if (cap != null)
            {
                EntityDuckTFC chick = new EntityDuckTFC(this.world);
                chick.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
                cap.setFertilized(chick, ConfigTFC.Animals.DUCK.hatch + CalendarTFC.PLAYER_TIME.getTotalDays());
            }
        }
        eggs.add(egg);
        return eggs;
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, ConfigTFC.Animals.DUCK.eggTicks + getLaidTicks() - CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return TFCSounds.ANIMAL_DUCK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.ANIMAL_DUCK_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSounds.ANIMAL_DUCK_CRY : TFCSounds.ANIMAL_DUCK_SAY;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_DUCK;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        // Same sound, no need to create another
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public double getOldDeathChance()
    {
        return ConfigTFC.Animals.DUCK.oldDeathChance;
    }
}

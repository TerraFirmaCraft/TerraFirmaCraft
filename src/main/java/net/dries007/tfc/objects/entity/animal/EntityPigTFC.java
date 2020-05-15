/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
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
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

@ParametersAreNonnullByDefault
public class EntityPigTFC extends EntityAnimalMammal implements ILivestock
{
    @SuppressWarnings("unused")
    public EntityPigTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(ConfigTFC.Animals.PIG.adulthood, ConfigTFC.Animals.PIG.elder));
    }

    public EntityPigTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        setSize(0.9F, 0.9F);
    }

    @Override
    public double getOldDeathChance()
    {
        return ConfigTFC.Animals.PIG.oldDeathChance;
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS || biomeType == BiomeHelper.BiomeType.SAVANNA ||
                biomeType == BiomeHelper.BiomeType.TROPICAL_FOREST))
        {
            return ConfigTFC.Animals.PIG.rarity;
        }
        return 0;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.ELDER_AND_POPULATION;
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack)
    {
        // Check for rotten
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (!ConfigTFC.Animals.PIG.acceptRotten && cap != null && cap.isRotten())
        {
            return false;
        }
        // Check if item is accepted
        for (String input : ConfigTFC.Animals.PIG.food)
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
    public int getMinGroupSize()
    {
        return 4;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 5;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.PIG.babies;
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityPigTFC baby = new EntityPigTFC(world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            baby.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
            world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return ConfigTFC.Animals.PIG.gestation;
    }

    @Override
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.PIG.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.PIG.elder;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void initEntityAI()
    {
        EntityAnimalTFC.addCommonLivestockAI(this, 1.3D);
        EntityAnimalTFC.addCommonPreyAI(this, 1.3D);

        tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_PIG;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }
}

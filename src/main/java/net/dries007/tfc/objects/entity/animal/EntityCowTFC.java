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

import net.dries007.tfc.util.Helpers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class EntityCowTFC extends EntityAnimalMammal implements ILivestock
{
    private static final int DEFAULT_TICKS_TO_MILK = ICalendar.TICKS_IN_DAY;
    private static final int DAYS_TO_ADULTHOOD = 1080;
    private static final int DAYS_TO_FULL_GESTATION = 270;

    private static final DataParameter<Long> LASTMILKED = EntityDataManager.createKey(EntityCowTFC.class, Helpers.LONG_DATA_SERIALIZER);

    @SuppressWarnings("unused")
    public EntityCowTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(DAYS_TO_ADULTHOOD));
    }

    public EntityCowTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.9F, 1.3F);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS || biomeType == BiomeHelper.BiomeType.SAVANNA || biomeType == BiomeHelper.BiomeType.TROPICAL_FOREST))
        {
            return ConfigTFC.WORLD.livestockSpawnRarity;
        }
        return 0;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.ELDER_AND_POPULATION;
    }

    @Override
    public int getMinGroupSize()
    {
        return 3;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 4;
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setLong("milked", lastMilked());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setLastMilked(compound.getLong("milked"));
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityCowTFC baby = new EntityCowTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            baby.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
            this.world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return DAYS_TO_FULL_GESTATION;
    }

    @Override
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return DAYS_TO_ADULTHOOD;
    }

    @Override
    public boolean isReadyForAnimalProduct()
    {
        return getFamiliarity() > 0.15f && hasMilk();
    }

    @Override
    public TextComponentTranslation getTooltip()
    {
        if (this.getGender() == Gender.MALE)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.male_milk");
        }
        else if (this.getAge() == Age.OLD)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.old", getAnimalName());
        }
        else if (this.getAge() == Age.CHILD)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.young", getAnimalName());
        }
        else if (getFamiliarity() <= 0.15f)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.low_familiarity", getAnimalName());
        }
        else if (!hasMilk())
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.no_milk", getAnimalName());
        }
        return null;
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        FluidActionResult fillResult = FluidUtil.tryFillContainer(itemstack, FluidUtil.getFluidHandler(new ItemStack(Items.MILK_BUCKET)),
            Fluid.BUCKET_VOLUME, player, false);

        // First check if it is possible to fill the player's held item with milk
        if (fillResult.isSuccess())
        {
            if (this.getFamiliarity() > 0.15f && isReadyForAnimalProduct())
            {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                this.setProductsCooldown();
                player.setHeldItem(hand, FluidUtil.tryFillContainerAndStow(itemstack, FluidUtil.getFluidHandler(new ItemStack(Items.MILK_BUCKET)),
                    new PlayerInvWrapper(player.inventory), Fluid.BUCKET_VOLUME, player, true).getResult());
            }
            else if (!this.world.isRemote)
            {
                //Return chat message indicating why this entity isn't giving milk
                TextComponentTranslation tooltip = getTooltip();
                if (tooltip != null)
                {
                    player.sendMessage(tooltip);
                }
            }
            return true;
        }
        else
        {
            return super.processInteract(player, hand);
        }
    }

    @Override
    protected boolean eatFood(@Nonnull ItemStack stack, EntityPlayer player)
    {
        // Refuses to eat rotten stuff
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (cap != null)
        {
            if (cap.isRotten())
            {
                return false;
            }
        }
        return super.eatFood(stack, player);
    }

    @Override
    public void setProductsCooldown()
    {
        setLastMilked(CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, lastMilked() + DEFAULT_TICKS_TO_MILK - CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_COW_DEATH;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.3D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        for (ItemStack is : OreDictionary.getOres("grain"))
        {
            Item item = is.getItem();
            this.tasks.addTask(3, new EntityAITempt(this, 1.1D, item, false));
        }
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_COW_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_COW;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    protected boolean hasMilk()
    {
        return this.getGender() == Gender.FEMALE && this.getAge() == Age.ADULT && getProductsCooldown() == 0;
    }

    protected long lastMilked()
    {
        return dataManager.get(LASTMILKED);
    }

    protected void setLastMilked(long day)
    {
        dataManager.set(LASTMILKED, day);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(LASTMILKED, 0L);
    }
}

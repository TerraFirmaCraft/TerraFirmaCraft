/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.IShearable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class EntitySheepTFC extends EntityAnimalMammal implements IShearable, ILivestock
{
    private static final DataParameter<Integer> DYE_COLOR = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Long> SHEARED = EntityDataManager.createKey(EntitySheepTFC.class, Helpers.LONG_DATA_SERIALIZER);

    @SuppressWarnings("unused")
    public EntitySheepTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()), getRandomGrowth(ConfigTFC.Animals.SHEEP.adulthood, ConfigTFC.Animals.SHEEP.elder), EntitySheep.getRandomSheepColor(Constants.RNG));
    }

    public EntitySheepTFC(World worldIn, Gender gender, int birthDay, EnumDyeColor dye)
    {
        super(worldIn, gender, birthDay);
        setSize(0.9F, 1.3F);
        setDyeColor(dye);
        setShearedTick(0);
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.PLAINS || biomeType == BiomeHelper.BiomeType.SAVANNA || biomeType == BiomeHelper.BiomeType.TEMPERATE_FOREST))
        {
            return ConfigTFC.Animals.SHEEP.rarity;
        }
        return 0;
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack)
    {
        // Check for rotten
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (!ConfigTFC.Animals.SHEEP.acceptRotten && cap != null && cap.isRotten())
        {
            return false;
        }
        // Check if item is accepted
        for (String input : ConfigTFC.Animals.SHEEP.food)
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
        return 5;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.SHEEP.babies;
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntitySheepTFC baby = new EntitySheepTFC(world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays(), getDyeColor());
            baby.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            baby.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
            world.spawnEntity(baby);
        }
    }

    @Override
    public long gestationDays()
    {
        return ConfigTFC.Animals.SHEEP.gestation;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataManager.register(DYE_COLOR, 0);
        dataManager.register(SHEARED, 0L);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setLong("shearedTick", getShearedTick());
        compound.setInteger("dyecolor", getDyeColor().getMetadata());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setShearedTick(compound.getLong("shearedTick"));
        setDyeColor(EnumDyeColor.byMetadata(compound.getByte("dyecolor")));
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (OreDictionaryHelper.doesStackMatchOre(stack, "knife"))
        {
            if (!world.isRemote)
            {
                if (isReadyForAnimalProduct())
                {
                    stack.damageItem(1, player);
                    ItemStack woolStack = new ItemStack(ItemsTFC.WOOL, 1);
                    Helpers.spawnItemStack(player.world, new BlockPos(posX, posY, posZ), woolStack);
                    playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
                    setProductsCooldown();
                }
                else
                {
                    TextComponentTranslation tooltip = getTooltip();
                    if (tooltip != null)
                    {
                        player.sendMessage(tooltip);
                    }
                }
            }
            return true;
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stack, "shears"))
        {
            if (!world.isRemote)
            {
                if (!isReadyForAnimalProduct())
                {
                    TextComponentTranslation tooltip = getTooltip();
                    if (tooltip != null)
                    {
                        player.sendMessage(tooltip);
                    }
                }
            }
            return false; // Process done in #onSheared by vanilla
        }
        else
        {
            return super.processInteract(player, hand);
        }
    }

    @Override
    public double getOldDeathChance()
    {
        return ConfigTFC.Animals.SHEEP.oldDeathChance;
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
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.SHEEP.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.SHEEP.elder;
    }

    @Override
    public boolean isReadyForAnimalProduct()
    {
        return getAge() != Age.CHILD && hasWool() && getFamiliarity() > 0.15f;
    }

    @Override
    public List<ItemStack> getProducts()
    {
        // Only white for now
        return Collections.singletonList(new ItemStack(ItemsTFC.WOOL, 1));
    }

    @Override
    public void setProductsCooldown()
    {
        setShearedTick(CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, ConfigTFC.Animals.SHEEP.woolTicks + getShearedTick() - CalendarTFC.PLAYER_TIME.getTicks());
    }

    @Override
    public TextComponentTranslation getTooltip()
    {
        if (getAge() == Age.CHILD)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.young", getAnimalName());
        }
        else if (getFamiliarity() <= 0.15f)
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.low_familiarity", getAnimalName());
        }
        else if (!hasWool())
        {
            return new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.no_wool", getAnimalName());
        }
        return null;
    }

    public EnumDyeColor getDyeColor()
    {
        return EnumDyeColor.byMetadata(dataManager.get(DYE_COLOR));
    }

    public void setDyeColor(EnumDyeColor color)
    {
        dataManager.set(DYE_COLOR, color.getMetadata());
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return isReadyForAnimalProduct();
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        setProductsCooldown();
        List<ItemStack> products = getProducts();
        // Fortune makes this less random and more towards the maximum (3) amount.
        int i = 1 + fortune + rand.nextInt(3 - Math.min(2, fortune));

        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack stack : products)
        {
            stack.setCount(i);
            ret.add(stack);
        }
        playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    public long getShearedTick()
    {
        return dataManager.get(SHEARED);
    }

    public void setShearedTick(long tick)
    {
        dataManager.set(SHEARED, tick);
    }

    public boolean hasWool()
    {
        return getShearedTick() <= 0 || getProductsCooldown() <= 0;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    @Override
    protected void initEntityAI()
    {
        EntityAnimalTFC.addCommonLivestockAI(this, 1.2D);
        EntityAnimalTFC.addCommonPreyAI(this, 1.2D);

        tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_SHEEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }
}

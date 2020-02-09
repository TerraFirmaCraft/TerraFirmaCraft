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
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class EntitySheepTFC extends EntityAnimalMammal implements IShearable
{
    private static final int DAYS_TO_ADULTHOOD = 360;
    private static final int DAYS_TO_GROW_WOOL = 7;
    private static final int DAYS_TO_FULL_GESTATION = 150;

    private static final DataParameter<Integer> DYE_COLOR = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SHEARED = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializers.VARINT);

    public EntitySheepTFC(World worldIn)
    {
        this(worldIn, Gender.valueOf(Constants.RNG.nextBoolean()),
            getRandomGrowth(DAYS_TO_ADULTHOOD),
            EntitySheep.getRandomSheepColor(Constants.RNG));
    }

    public EntitySheepTFC(World worldIn, Gender gender, int birthDay, EnumDyeColor dye)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.9F, 1.3F);
        this.setDyeColor(dye);
        this.setShearedDay(-1); //Spawn with wool
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall)
    {
        return 100;
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
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            if (this.getShearedDay() > CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.setShearedDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
            }
        }
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("sheared", this.getShearedDay());
        compound.setInteger("dyecolor", this.getDyeColor().getMetadata());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setShearedDay(compound.getInteger("sheared"));
        this.setDyeColor(EnumDyeColor.byMetadata(compound.getByte("dyecolor")));
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = Constants.RNG.nextInt(3) + 1; //1-3
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntitySheepTFC baby = new EntitySheepTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays(), this.getDyeColor());
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
        return getAge() != Age.CHILD && hasWool() && getFamiliarity() > 0.15f;
    }

    @Override
    public List<ItemStack> getProducts()
    {
        // Only white for now
        return Collections.singletonList(new ItemStack(ItemsTFC.WOOL, 1));
    }

    @Override
    public TextComponentTranslation getTooltip()
    {
        if (this.getAge() == Age.CHILD)
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
        return EnumDyeColor.byMetadata(this.dataManager.get(DYE_COLOR));
    }

    public void setDyeColor(EnumDyeColor color)
    {
        this.dataManager.set(DYE_COLOR, color.getMetadata());
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
        this.setShearedDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
        List<ItemStack> products = getProducts();
        // Fortune makes this less random and more towards the maximum (3) amount.
        int i = 1 + fortune + this.rand.nextInt(3 - Math.min(2, fortune));

        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack stack : products)
        {
            stack.setCount(i);
            ret.add(stack);
        }
        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    public int getShearedDay()
    {
        return this.dataManager.get(SHEARED);
    }

    public void setShearedDay(int value)
    {
        this.dataManager.set(SHEARED, value);
    }

    public boolean hasWool()
    {
        return this.getShearedDay() == -1 || CalendarTFC.PLAYER_TIME.getTotalDays() >= getShearedDay() + DAYS_TO_GROW_WOOL;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (OreDictionaryHelper.doesStackMatchOre(stack, "knife"))
        {
            if (!this.world.isRemote)
            {
                if (this.isReadyForAnimalProduct())
                {
                    stack.damageItem(1, player);
                    ItemStack woolStack = new ItemStack(ItemsTFC.WOOL, 1);
                    Helpers.spawnItemStack(player.world, new BlockPos(this.posX, this.posY, this.posZ), woolStack);
                    this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
                    this.setShearedDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
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
            if (!this.world.isRemote)
            {
                if (!this.isReadyForAnimalProduct())
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
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DYE_COLOR, 0);
        this.dataManager.register(SHEARED, 0);
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
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
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
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }
}

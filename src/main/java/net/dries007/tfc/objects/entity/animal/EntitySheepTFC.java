/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import net.dries007.tfc.Constants;
import net.dries007.tfc.util.DataSerializersTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntitySheepTFC extends EntityAnimalMammal implements IShearable
{
    private static final int DAYS_TO_ADULTHOOD = 360;
    private static final int DAYS_TO_GROW_WOOL = 7;
    private static final int DAYS_TO_FULL_GESTATION = 150;

    private static final DataParameter<Integer> DYE_COLOR = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Long> SHEARED = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializersTFC.LONG);

    public EntitySheepTFC(World worldIn)
    {
        this(worldIn, Constants.RNG.nextBoolean(),
            CalendarTFC.INSTANCE.getTotalTime() - (Constants.RNG.nextBoolean() ? CalendarTFC.TICKS_IN_DAY * 360 : 0), //Adult or child
            EntitySheep.getRandomSheepColor(Constants.RNG));
    }

    public EntitySheepTFC(World worldIn, boolean gender, long birthTime, EnumDyeColor dye)
    {
        super(worldIn, gender, birthTime);
        this.setSize(0.9F, 1.3F);
        this.setDyeColor(dye);
        this.setShearedTime(birthTime - DAYS_TO_GROW_WOOL * CalendarTFC.TICKS_IN_DAY); //Spawn with wool
    }

    public EnumDyeColor getDyeColor()
    {
        return EnumDyeColor.byMetadata(this.dataManager.get(DYE_COLOR));
    }

    public void setDyeColor(EnumDyeColor color)
    {
        this.dataManager.set(DYE_COLOR, color.getMetadata());
    }

    public long getShearedtime()
    {
        return this.dataManager.get(SHEARED);
    }

    public void setShearedTime(long value)
    {
        this.dataManager.set(SHEARED, value);
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return getAge() == Age.ADULT && hasWool();
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        this.setShearedTime(CalendarTFC.INSTANCE.getTotalTime());
        int i = 1 + this.rand.nextInt(3);

        java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        for (int j = 0; j < i; ++j)
            ret.add(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, this.getDyeColor().getMetadata()));

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    public boolean hasWool()
    {
        return CalendarTFC.INSTANCE.getTotalTime() > getShearedtime() + CalendarTFC.TICKS_IN_DAY * DAYS_TO_GROW_WOOL;
    }

    /*@Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && !hasWool() && getShouldHaveWool())
        {
            setSheared(false);
        }
    }*/

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setLong("sheared", this.getShearedtime());
        compound.setInteger("dyecolor", this.getDyeColor().getMetadata());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setShearedTime(compound.getLong("sheared"));
        this.setDyeColor(EnumDyeColor.byMetadata(compound.getByte("dyecolor")));
    }

    @Override
    public long gestationTicks()
    {
        return CalendarTFC.TICKS_IN_DAY * DAYS_TO_FULL_GESTATION;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = Constants.RNG.nextInt(3) + 1; //1-3
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntitySheepTFC baby = new EntitySheepTFC(this.world, Constants.RNG.nextBoolean(), CalendarTFC.INSTANCE.getTotalTime(), this.getDyeColor());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }

    }

    @Override
    public float getPercentToAdulthood()
    {
        long div = this.getBirthTime() + CalendarTFC.TICKS_IN_DAY * DAYS_TO_ADULTHOOD;
        if (div == 0) return 1;
        float value = CalendarTFC.INSTANCE.getTotalTime() / div;
        if (value > 1f) value = 1f;
        return value;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
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

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.INSTANCE.getTotalTime() > this.getBirthTime() + CalendarTFC.TICKS_IN_DAY * DAYS_TO_ADULTHOOD ? Age.ADULT : Age.CHILD; //1 Year to adulthood
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DYE_COLOR, 0);
        this.dataManager.register(SHEARED, 0L);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
    }
}

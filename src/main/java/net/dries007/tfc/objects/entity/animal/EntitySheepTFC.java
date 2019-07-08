/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    protected static final DataParameter<Long> SHEARED = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializersTFC.LONG);
    private static final DataParameter<Byte> DYE = EntityDataManager.createKey(EntitySheepTFC.class, DataSerializers.BYTE);

    public static void createSpawn(World world, BlockPos pos)
    {
        long ticks = Constants.RNG.nextBoolean() ? CalendarTFC.TICKS_IN_DAY * 360 : 0; //Adult or child
        EntitySheepTFC spawn = new EntitySheepTFC(world, Constants.RNG.nextBoolean(), CalendarTFC.INSTANCE.getTotalTime() + ticks, (byte) 0);
        spawn.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
        world.spawnEntity(spawn);
    }

    private long lastSheared;
    private byte dye;

    public EntitySheepTFC(World worldIn)
    {
        this(worldIn, Constants.RNG.nextBoolean(), 0, (byte) 0);
    }

    public EntitySheepTFC(World worldIn, boolean gender, long birthTime, byte dye)
    {
        super(worldIn, gender, birthTime);
        lastSheared = birthTime;
        this.dye = dye;
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.INSTANCE.getCalendarTime() > getBirthTime() + CalendarTFC.TICKS_IN_DAY * 360 ? Age.ADULT : Age.CHILD; //1 Year
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return getAge() == Age.ADULT && CalendarTFC.INSTANCE.getTotalTime() > lastSheared + CalendarTFC.TICKS_IN_DAY * 7; //7 Days
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        this.lastSheared = CalendarTFC.INSTANCE.getTotalTime();
        int i = 1 + this.rand.nextInt(3);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int j = 0; j < i; ++j)
            ret.add(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, dye));

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
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
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(DYE, dye);
        getDataManager().register(SHEARED, lastSheared);
    }

    @Override
    public long gestationTicks()
    {
        return CalendarTFC.TICKS_IN_DAY * 150; //5 months
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = Constants.RNG.nextInt(3) + 1; //1-3
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntitySheepTFC baby = new EntitySheepTFC(this.world, Constants.RNG.nextBoolean(), CalendarTFC.INSTANCE.getTotalTime(), this.dye);
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }

    }
}

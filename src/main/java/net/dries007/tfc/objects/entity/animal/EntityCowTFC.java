/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@ParametersAreNonnullByDefault
public class EntityCowTFC extends EntityAnimalMammal implements IAnimalTFC
{
    private static final int DAYS_TO_ADULTHOOD = 1080;
    private static final int DAYS_TO_FULL_GESTATION = 270;

    private static int getRandomGrowth()
    {
        int lifeTimeDays = Constants.RNG.nextInt(DAYS_TO_ADULTHOOD * 4);
        return (int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays);
    }
    private long lastMilked;

    @SuppressWarnings("unused")
    public EntityCowTFC(World worldIn)
    {
        this(worldIn, Gender.fromBool(Constants.RNG.nextBoolean()),
            getRandomGrowth());
    }

    public EntityCowTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.setSize(0.9F, 1.3F);
        this.setMilkedDay(-1); //Spawn with milk
    }

    @Override
    public boolean isValidSpawnConditions(Biome biome, float temperature, float rainfall)
    {
        return (biome == BiomesTFC.PLAINS || biome == BiomesTFC.HIGH_PLAINS) && temperature > -10 && rainfall > 100 && rainfall < 400;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            if (this.getMilkedDay() > CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.setMilkedDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setLong("milked", this.getMilkedDay());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setMilkedDay(compound.getInteger("milked"));
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            EntityCowTFC baby = new EntityCowTFC(this.world, Gender.fromBool(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            this.world.spawnEntity(baby);
        }

    }

    @Override
    public long gestationDays()
    {
        return DAYS_TO_FULL_GESTATION;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.BUCKET)
        {
            if (this.getFamiliarity() > 0.15f && hasMilk())
            {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                this.setMilkedDay(CalendarTFC.PLAYER_TIME.getTotalDays());
                itemstack.shrink(1);

                if (itemstack.isEmpty())
                {
                    player.setHeldItem(hand, new ItemStack(Items.MILK_BUCKET));
                }
                else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET)))
                {
                    player.dropItem(new ItemStack(Items.MILK_BUCKET), false);
                }
            }
            else if (!this.world.isRemote)
            {
                //Return chat message indicating why this entity isn't giving milk
                if (this.getGender() == Gender.MALE)
                {
                    player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.milk.male"));
                }
                else if (this.getAge() == Age.OLD)
                {
                    player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.milk.old"));
                }
                else if (this.getAge() == Age.CHILD)
                {
                    player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.milk.child"));
                }
                else if (getFamiliarity() <= 0.15f)
                {
                    player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.milk.lowfamiliarity"));
                }
                else if (!hasMilk())
                {
                    player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.milk.empty"));
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
    public float getPercentToAdulthood()
    {
        if (this.getAge() != Age.CHILD) return 1;
        double value = (CalendarTFC.PLAYER_TIME.getTotalDays() - this.getBirthDay()) / (double) DAYS_TO_ADULTHOOD;
        if (value > 1f) value = 1f;
        if (value < 0f) value = 0;
        return (float) value;
    }

    @Override
    public Age getAge()
    {
        return CalendarTFC.PLAYER_TIME.getTotalDays() >= this.getBirthDay() + DAYS_TO_ADULTHOOD ? Age.ADULT : Age.CHILD;
    }

    public long getMilkedDay()
    {
        return this.lastMilked;
    }

    public void setMilkedDay(long value)
    {
        this.lastMilked = value;
    }

    public boolean hasMilk()
    {
        return this.getGender() == Gender.FEMALE && this.getAge() == Age.ADULT && (this.getMilkedDay() == -1 || CalendarTFC.PLAYER_TIME.getTotalDays() > getMilkedDay());
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
}

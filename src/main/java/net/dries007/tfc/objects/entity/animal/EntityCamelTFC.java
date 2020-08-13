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
import net.minecraft.block.SoundType;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.BiomeHelper;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class EntityCamelTFC extends EntityLlamaTFC implements IAnimalTFC, ILivestock
{
    private static final DataParameter<Integer> DATA_COLOR_ID = EntityDataManager.createKey(EntityCamelTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> HALTER = EntityDataManager.createKey(EntityCamelTFC.class, DataSerializers.BOOLEAN);

    public EntityCamelTFC(World world)
    {
        this(world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), EntityAnimalTFC.getRandomGrowth(ConfigTFC.Animals.CAMEL.adulthood, ConfigTFC.Animals.CAMEL.elder));
        this.setSize(0.9F, 2.0F);
    }

    public EntityCamelTFC(World world, IAnimalTFC.Gender gender, int birthDay)
    {
        super(world, gender, birthDay);
    }

    public boolean canBeSteered()
    {
        return this.getControllingPassenger() instanceof EntityLivingBase;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Constants.RNG.nextInt(100) < 5 ? TFCSounds.ANIMAL_CAMEL_CRY : TFCSounds.ANIMAL_CAMEL_SAY;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return TFCSounds.ANIMAL_CAMEL_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return TFCSounds.ANIMAL_CAMEL_DEATH; }

    @SuppressWarnings("deprecation")
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        if (!blockIn.getDefaultState().getMaterial().isLiquid())
        {
            SoundType soundtype = blockIn.getSoundType();
            if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER)
            {
                soundtype = Blocks.SNOW_LAYER.getSoundType();
            }

            if (this.isBeingRidden() && this.canGallop)
            {
                ++this.gallopTime;
                if (this.gallopTime > 5 && this.gallopTime % 3 == 0)
                {
                    this.playGallopSound(soundtype);
                }
                else if (this.gallopTime <= 5)
                {
                    this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            }
            else if (soundtype == SoundType.WOOD)
            {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
            else
            {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
        }
    }

    public boolean wearsArmor()
    {
        return true;
    }

    public boolean isArmor(ItemStack stack)
    {
        return stack.getItem() == Item.getItemFromBlock(Blocks.CARPET);
    }

    public boolean canBeSaddled() {return true;}

    public void onInventoryChanged(IInventory invBasic)
    {
        EnumDyeColor enumdyecolor = this.getColor();
        super.onInventoryChanged(invBasic);
        EnumDyeColor enumdyecolor1 = this.getColor();
        if (this.ticksExisted > 20 && enumdyecolor1 != null && enumdyecolor1 != enumdyecolor)
        {
            this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
        }
        boolean flag = this.isHorseSaddled();
        this.updateHorseSlots();
        if (this.ticksExisted > 20 && !flag && this.isHorseSaddled())
        {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
        }
    }

    protected void updateHorseSlots()
    {
        if (!this.world.isRemote)
        {
            super.updateHorseSlots();
            this.setColorByItem(this.horseChest.getStackInSlot(1));
        }
    }

    @Nullable
    public EnumDyeColor getColor()
    {
        int i = this.dataManager.get(DATA_COLOR_ID);
        return i == -1 ? null : EnumDyeColor.byMetadata(i);
    }

    private void setColor(@Nullable EnumDyeColor color)
    {
        this.dataManager.set(DATA_COLOR_ID, color == null ? -1 : color.getMetadata());
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!isHalter() && OreDictionaryHelper.doesStackMatchOre(stack, "halter"))
        {
            if (this.getAge() != Age.CHILD && getFamiliarity() > 0.15f)
            {
                if (!this.world.isRemote)
                {
                    this.consumeItemFromStack(player, stack);
                    this.setHalter(true);
                }
                return true;
            }
            else
            {
                // Show tooltips
                if (!this.world.isRemote)
                {
                    if (this.getAge() == Age.CHILD)
                    {
                        player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.young", getName()));
                    }
                    else
                    {
                        player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.product.low_familiarity", getName()));
                    }

                }
                return false;
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onFertilized(@Nonnull IAnimalTFC male)
    {
        this.setPregnantTime(CalendarTFC.PLAYER_TIME.getTotalDays());
        int selection = this.rand.nextInt(9);
        int i;
        if (selection < 4)
        {
            i = this.getVariant();
        }
        else if (selection < 8)
        {
            i = ((EntityCamelTFC) male).getVariant();
        }
        else
        {
            // Mutation
            i = this.rand.nextInt(4);
        }
        this.geneVariant = i;
        EntityCamelTFC father = (EntityCamelTFC) male;
        this.geneHealth = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.getModifiedMaxHealth()) / 3.0D);
        this.geneSpeed = (float) ((father.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed()) / 3.0D);
        this.geneJump = (float) ((father.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength()) / 3.0D);

        this.geneStrength = this.rand.nextInt(Math.max(this.getStrength(), father.getStrength())) + 1;
        if (this.rand.nextFloat() < 0.03F)
        {
            this.geneStrength++;
        }
    }

    @Override
    public int getDaysToAdulthood()
    {
        return ConfigTFC.Animals.CAMEL.adulthood;
    }

    @Override
    public int getDaysToElderly()
    {
        return ConfigTFC.Animals.CAMEL.elder;
    }

    @Override
    public int getSpawnWeight(Biome biome, float temperature, float rainfall, float floraDensity, float floraDiversity)
    {
        BiomeHelper.BiomeType biomeType = BiomeHelper.getBiomeType(temperature, rainfall, floraDensity);
        if (!BiomesTFC.isOceanicBiome(biome) && !BiomesTFC.isBeachBiome(biome) &&
            (biomeType == BiomeHelper.BiomeType.DESERT || biomeType == BiomeHelper.BiomeType.SAVANNA))
        {
            return ConfigTFC.Animals.CAMEL.rarity;
        }
        return 0;
    }

    @Override
    public BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return AnimalGroupingRules.MALE_AND_FEMALES;
    }

    @Override
    public int getMinGroupSize()
    {
        return 1;
    }

    @Override
    public int getMaxGroupSize()
    {
        return 2;
    }

    @Override
    protected void mountTo(EntityPlayer player)
    {
        if (isHalter())
        {
            super.mountTo(player);
        }
    }

    @Override
    public long gestationDays()
    {
        return ConfigTFC.Animals.CAMEL.gestation;
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Variant", this.getVariant());
        nbt.setInteger("Strength", this.getStrength());
        if (!this.horseChest.getStackInSlot(1).isEmpty())
        {
            nbt.setTag("DecorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
        }
        nbt.setBoolean("halter", isHalter());
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setStrength(nbt.getInteger("Strength"));
        this.setVariant(nbt.getInteger("Variant"));
        if (nbt.hasKey("DecorItem", 10))
        {
            this.horseChest.setInventorySlotContents(1, new ItemStack(nbt.getCompoundTag("DecorItem")));
        }
        this.updateHorseSlots();
        this.setHalter(nbt.getBoolean("halter"));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(DATA_COLOR_ID, -1);
        getDataManager().register(HALTER, false);
    }

    @Override
    protected ResourceLocation getLootTable() { return LootTablesTFC.ANIMALS_CAMEL; }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityCamelTFC other = (EntityCamelTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Nullable
    @Override
    public EntityCamelTFC createChild(@Nonnull EntityAgeable other)
    {
        // Cancel default vanilla behaviour (immediately spawns children of this animal) and set this female as fertilized
        if (other != this && this.getGender() == Gender.FEMALE && other instanceof IAnimalTFC)
        {
            super.setFertilized(true);
            this.resetInLove();
            this.onFertilized((IAnimalTFC) other);
        }
        else if (other == this)
        {
            // Only called if this animal is interacted with a spawn egg
            // Try to return to vanilla's default method a baby of this animal, as if bred normally
            return new EntityCamelTFC(this.world, IAnimalTFC.Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
        }
        return null;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChildren = ConfigTFC.Animals.CAMEL.babies; //one always
        for (int i = 0; i < numberOfChildren; i++)
        {
            EntityCamelTFC baby = new EntityCamelTFC(this.world, Gender.valueOf(Constants.RNG.nextBoolean()), (int) CalendarTFC.PLAYER_TIME.getTotalDays());
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
            if (this.geneHealth > 0)
            {
                baby.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.geneHealth);
            }
            if (this.geneSpeed > 0)
            {
                baby.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.geneSpeed);
            }
            if (this.geneJump > 0)
            {
                baby.getEntityAttribute(JUMP_STRENGTH).setBaseValue(this.geneJump);
            }
            if (this.geneStrength > 0)
            {
                this.setStrength((int) this.geneStrength);
            }
            baby.setVariant(geneVariant);
            this.world.spawnEntity(baby);
        }
        geneJump = 0;
        geneSpeed = 0;
        geneJump = 0;
        geneStrength = 0;
        geneVariant = 0;
    }

    public boolean isHalter()
    {
        return dataManager.get(HALTER);
    }

    public void setHalter(boolean value)
    {
        dataManager.set(HALTER, value);
    }

    protected void playGallopSound(SoundType p_190680_1_)
    {
        this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, p_190680_1_.getVolume() * 0.15F, p_190680_1_.getPitch());
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    private void setColorByItem(ItemStack stack)
    {
        if (this.isArmor(stack))
        {
            this.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
        }
        else
        {
            this.setColor(null);
        }
    }
}
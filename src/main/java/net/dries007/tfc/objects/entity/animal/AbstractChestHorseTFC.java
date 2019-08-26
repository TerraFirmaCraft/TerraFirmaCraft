/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.world.World;

import net.dries007.tfc.util.OreDictionaryHelper;

public class AbstractChestHorseTFC extends AbstractHorseTFC
{
    private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.createKey(AbstractChestHorseTFC.class, DataSerializers.BOOLEAN);

    public static void registerFixesAbstractChestHorseTFC(DataFixer fixer, Class<?> entityClass)
    {
        AbstractHorseTFC.registerFixesAbstractHorseTFC(fixer, entityClass);
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists(entityClass, "Items"));
    }

    public AbstractChestHorseTFC(World worldIn)
    {
        super(worldIn);
        this.canGallop = false;
    }

    public boolean hasChest()
    {
        return this.dataManager.get(DATA_ID_CHEST).booleanValue();
    }

    public void setChested(boolean chested)
    {
        this.dataManager.set(DATA_ID_CHEST, Boolean.valueOf(chested));
    }

    public double getMountedYOffset()
    {
        return super.getMountedYOffset() - 0.25D;
    }

    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (this.hasChest())
        {
            if (!this.world.isRemote)
            {
                this.dropItem(Item.getItemFromBlock(Blocks.CHEST), 1);
            }

            this.setChested(false);
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("ChestedHorse", this.hasChest());

        if (this.hasChest())
        {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.horseChest.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.horseChest.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte) i);
                    itemstack.writeToNBT(nbttagcompound);
                    nbttaglist.appendTag(nbttagcompound);
                }
            }

            compound.setTag("Items", nbttaglist);
        }
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setChested(compound.getBoolean("ChestedHorse"));

        if (this.hasChest())
        {
            NBTTagList nbttaglist = compound.getTagList("Items", 10);
            this.initHorseChest();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;

                if (j >= 2 && j < this.horseChest.getSizeInventory())
                {
                    this.horseChest.setInventorySlotContents(j, new ItemStack(nbttagcompound));
                }
            }
        }

        this.updateHorseSlots();
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DATA_ID_CHEST, Boolean.valueOf(false));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double) this.getModifiedMaxHealth());
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776D);
        this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        if (inventorySlot == 499)
        {
            if (this.hasChest() && itemStackIn.isEmpty())
            {
                this.setChested(false);
                this.initHorseChest();
                return true;
            }

            if (!this.hasChest() && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.CHEST))
            {
                this.setChested(true);
                this.initHorseChest();
                return true;
            }
        }

        return super.replaceItemInInventory(inventorySlot, itemStackIn);
    }

    protected int getInventorySize()
    {
        return this.hasChest() ? 17 : super.getInventorySize();
    }

    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return SoundEvents.ENTITY_DONKEY_ANGRY;
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.SPAWN_EGG)
        {
            return super.processInteract(player, hand);
        }
        else
        {
            if (!this.isChild())
            {
                if (this.isTame() && player.isSneaking())
                {
                    this.openGUI(player);
                    return true;
                }

                if (this.isBeingRidden())
                {
                    return super.processInteract(player, hand);
                }
            }

            if (!itemstack.isEmpty())
            {
                if (super.processInteract(player, hand) && !this.world.isRemote && this.handleEating(player, itemstack))
                {
                    return true;
                }

                if (!this.isTame())
                {
                    if (itemstack.interactWithEntity(player, this, hand))
                    {
                        return true;
                    }

                    this.makeMad();
                    return true;
                }

                if (!this.hasChest() && OreDictionaryHelper.doesStackMatchOre(itemstack, "chest"))
                {
                    this.setChested(true);
                    this.playChestEquipSound();
                    this.initHorseChest();
                    if (!player.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }
                    return true;
                }

                if (!this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE)
                {
                    this.openGUI(player);
                    return true;
                }
            }

            if (this.isChild())
            {
                return super.processInteract(player, hand);
            }
            else if (itemstack.interactWithEntity(player, this, hand))
            {
                return true;
            }
            else
            {
                this.mountTo(player);
                return true;
            }
        }
    }

    public int getInventoryColumns()
    {
        return 5;
    }

    protected void playChestEquipSound()
    {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.objects.items.ItemHandstone;
import net.dries007.tfc.objects.items.ItemsTFC;

import static net.minecraft.init.SoundEvents.*;

@ParametersAreNonnullByDefault
public class TEQuern extends TEInventory implements ITickable
{
    public static final int SLOT_HANDSTONE = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    private int rotationTimer;
    private boolean hasHandstone;

    public TEQuern()
    {
        super(3);
        rotationTimer = 0;
    }

    public ItemStack insertOrSwapItem(int slot, ItemStack playerStack)
    {
        ItemStack quernStack = inventory.getStackInSlot(slot);

        if (quernStack.isEmpty() || (playerStack.isStackable() && quernStack.isStackable() && quernStack.getItem() == playerStack.getItem() && (!playerStack.getHasSubtypes() || playerStack.getMetadata() == quernStack.getMetadata()) && ItemStack.areItemStackTagsEqual(playerStack, quernStack)))
        {
            return inventory.insertItem(slot, playerStack, false);
        }
        inventory.setStackInSlot(slot, playerStack);
        return quernStack;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return slot == SLOT_HANDSTONE ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_HANDSTONE:
                return stack.getItem() instanceof ItemHandstone;
            case SLOT_INPUT:
                return QuernRecipe.get(stack) != null;
            default:
                return false;
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        updateBlock();
        if (slot == SLOT_HANDSTONE)
        {
            hasHandstone = inventory.getStackInSlot(SLOT_HANDSTONE).getItem() instanceof ItemHandstone;
        }
        super.setAndUpdateSlots(slot);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        rotationTimer = nbt.getInteger("rotationTimer");
        super.readFromNBT(nbt);
        hasHandstone = inventory.getStackInSlot(SLOT_HANDSTONE).getItem() instanceof ItemHandstone;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("rotationTimer", rotationTimer);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return super.canInteractWith(player) && rotationTimer == 0;
    }

    public int getRotationTimer()
    {
        return rotationTimer;
    }

    public boolean isGrinding()
    {
        return rotationTimer > 0;
    }

    public boolean hasHandstone()
    {
        return hasHandstone;
    }

    public void grind()
    {
        this.rotationTimer = 90;
        updateBlock();
    }

    @Override
    public void update()
    {
        if (rotationTimer > 0)
        {
            rotationTimer--;

            if (rotationTimer == 0)
            {
                grindItem();
                world.playSound(null, pos, ENTITY_ARMORSTAND_FALL, SoundCategory.BLOCKS, 1.0f, 0.8f);
                inventory.getStackInSlot(SLOT_HANDSTONE).damageItem(1, new EntityCow(world));

                if (inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty())
                {
                    for (int i = 0; i < 15; i++)
                    {
                        world.spawnParticle(EnumParticleTypes.ITEM_CRACK, pos.getX() + 0.5D, pos.getY() + 0.875D, pos.getZ() + 0.5D, (world.rand.nextDouble() - world.rand.nextDouble()) / 4, world.rand.nextDouble() / 4, (world.rand.nextDouble() - world.rand.nextDouble()) / 4, Item.getIdFromItem(ItemsTFC.HANDSTONE));
                    }
                    world.playSound(null, pos, BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 0.8f);
                    world.playSound(null, pos, ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 0.6f);
                }

                setAndUpdateSlots(SLOT_HANDSTONE);
            }
        }
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 2, 1));
    }

    public ItemStack takeCraftingResult(ItemStack stack)
    {
        return inventory.extractItem(SLOT_OUTPUT, stack.getCount(), false);
    }

    private void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    private void grindItem()
    {
        ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
        if (!inputStack.isEmpty())
        {
            QuernRecipe recipe = QuernRecipe.get(inputStack);
            if (recipe != null && !world.isRemote)
            {
                ItemStack leftover = inventory.insertItem(SLOT_OUTPUT, recipe.getOutputItem(inputStack), false);
                if (!leftover.isEmpty())
                {
                    InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, leftover);
                }
                inputStack.shrink(1);
            }
        }
    }
}

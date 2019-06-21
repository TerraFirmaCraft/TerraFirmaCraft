/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketQuernUpdate;
import net.dries007.tfc.network.PacketRequestQuernUpdate;
import net.dries007.tfc.objects.items.ItemHandstone;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.recipes.QuernRecipeManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

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

    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    public void setInventory(@Nonnull NBTTagCompound nbt)
    {
        inventory.deserializeNBT(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        if (!world.isRemote)
        {
            TerraFirmaCraft.getNetwork().sendToAllTracking(new PacketQuernUpdate(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        super.setAndUpdateSlots(slot);
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
                return OreDictionaryHelper.doesStackMatchOre(stack, "quernHandstone");
            case SLOT_INPUT:
                return QuernRecipeManager.getInstance().getIsValidGrindingIngredient(stack);
            default:
                return false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        rotationTimer = nbt.getInteger("rotationTimer");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("rotationTimer", rotationTimer);
        return super.writeToNBT(nbt);
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
    }

    @Override
    public void update()
    {
        hasHandstone = inventory.getStackInSlot(SLOT_HANDSTONE).getItem() instanceof ItemHandstone;

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
            }
            setAndUpdateSlots(SLOT_HANDSTONE);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 2, 1));
    }

    @Override
    public void onLoad()
    {
        if (world.isRemote)
        {
            TerraFirmaCraft.getNetwork().sendToServer(new PacketRequestQuernUpdate(this));
        }
    }

    public ItemStack takeCraftingResult(EntityPlayer player, ItemStack stack)
    {
        if (!player.world.isRemote)
        {
            int count = stack.getCount();
            float xpValue = QuernRecipeManager.getInstance().getGrindingExperience(stack);

            if (xpValue == 0.0F)
            {
                count = 0;
            }
            else if (xpValue < 1.0F)
            {
                int j = MathHelper.floor((float) count * xpValue);
                if (j < MathHelper.ceil((float) count * xpValue) && Math.random() < (double) ((float) count * xpValue - (float) j))
                {
                    ++j;
                }
                count = j;
            }

            while (count > 0)
            {
                int k = EntityXPOrb.getXPSplit(count);
                count -= k;
                player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, k));
                world.playSound(null, player.getPosition(), ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F + ((world.rand.nextFloat() - world.rand.nextFloat()) / 16F));
            }
        }
        stack.onCrafting(player.world, player, stack.getCount());
        return inventory.extractItem(SLOT_OUTPUT, stack.getCount(), false);
    }

    private void grindItem()
    {
        if (hasHandstone)
        {
            ItemStack inputStack = inventory.getStackInSlot(SLOT_INPUT);
            ItemStack resultStack = QuernRecipeManager.getInstance().getGrindingResult(inputStack);
            ItemStack outputStack = inventory.getStackInSlot(SLOT_OUTPUT);

            if (outputStack.isEmpty())
            {
                inventory.setStackInSlot(SLOT_OUTPUT, resultStack.copy());
            }
            else if (outputStack.getItem() == resultStack.getItem())
            {
                if (!world.isRemote)
                    Helpers.spawnItemStack(world, pos.add(0.5, 1.0D, 0.5), inventory.insertItem(SLOT_OUTPUT, resultStack, true));
                inventory.insertItem(SLOT_OUTPUT, resultStack, false);
            }
            else if (!world.isRemote)
            {
                Helpers.spawnItemStack(world, pos.add(0.5, 1.0D, 0.5), resultStack);
            }

            inputStack.shrink(1);
        }
    }
}

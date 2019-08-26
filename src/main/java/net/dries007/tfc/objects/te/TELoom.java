/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.LoomRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.network.PacketLoomUpdate;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

@ParametersAreNonnullByDefault
public class TELoom extends TEInventory implements ITickable
{
    private Tree cachedWood;

    private int progress = 0;

    private LoomRecipe recipe = null;
    private long lastPushed = 0L;
    private boolean needsUpdate = false;

    public TELoom()
    {
        super(2);
    }

    @Nullable
    public Tree getWood()
    {
        if (cachedWood == null)
        {
            if (world != null)
            {
                cachedWood = ((BlockLoom) world.getBlockState(pos).getBlock()).wood;
            }
        }
        return cachedWood;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        progress = nbt.getInteger("progress");
        recipe = nbt.hasKey("recipe") ? TFCRegistries.LOOM.getValue(new ResourceLocation(nbt.getString("recipe"))) : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("progress", progress);
        if (recipe != null)
        {
            //noinspection ConstantConditions
            nbt.setString("recipe", recipe.getRegistryName().toString());
        }
        return nbt;
    }

    public void onReceivePacket(long lastPushed)
    {
        this.lastPushed = lastPushed;
    }

    @SideOnly(Side.CLIENT)
    public double getAnimPos()
    {
        int time = (int) (world.getTotalWorldTime() - lastPushed);
        if (time < 10)
        {
            return Math.sin((Math.PI / 20) * time) * 0.23125;
        }
        else if (time < 20)
        {
            return Math.sin((Math.PI / 20) * (20 - time)) * 0.23125;
        }
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public String getAnimElement()
    {
        return (progress % 2 == 0) ? "u" : "l";
    }

    public boolean onRightClick(EntityPlayer player)
    {
        if (player.isSneaking())
        {
            if (!inventory.getStackInSlot(0).isEmpty() && progress == 0)
            {
                ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);

                if (heldItem.isEmpty())
                {
                    ItemStack temp = inventory.getStackInSlot(0).copy();
                    temp.setCount(1);
                    player.addItemStackToInventory(temp);
                    inventory.getStackInSlot(0).shrink(1);

                    if (inventory.getStackInSlot(0).isEmpty())
                    {
                        recipe = null;
                    }
                    updateBlock();
                    return true;
                }
            }
        }
        else
        {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);

            if (inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() && LoomRecipe.get(heldItem) != null)
            {
                inventory.setStackInSlot(0, heldItem.copy());
                inventory.getStackInSlot(0).setCount(1);
                heldItem.shrink(1);
                recipe = LoomRecipe.get(inventory.getStackInSlot(0));

                updateBlock();
                return true;
            }
            else if (!inventory.getStackInSlot(0).isEmpty())
            {
                if (IIngredient.of(inventory.getStackInSlot(0)).testIgnoreCount(heldItem) && recipe.getInputCount() > inventory.getStackInSlot(0).getCount())
                {
                    heldItem.shrink(1);
                    inventory.getStackInSlot(0).grow(1);

                    updateBlock();
                    return true;
                }
            }

            if (recipe != null && heldItem.isEmpty())
            {
                if (recipe.getInputCount() == inventory.getStackInSlot(0).getCount() && progress < recipe.getStepCount() && !needsUpdate)
                {
                    if (!world.isRemote)
                    {
                        long time = world.getTotalWorldTime() - lastPushed;
                        if (time < 20)
                            return true;
                        lastPushed = world.getTotalWorldTime();
                        needsUpdate = true;

                        TerraFirmaCraft.getNetwork().sendToDimension(new PacketLoomUpdate(this, lastPushed), world.provider.getDimension());
                    }
                    return true;
                }
            }

            if (!inventory.getStackInSlot(1).isEmpty())
            {
                if (heldItem.isEmpty())
                {
                    player.addItemStackToInventory(inventory.getStackInSlot(1).copy());
                    inventory.setStackInSlot(1, ItemStack.EMPTY);
                    progress = 0;
                    recipe = null;
                    updateBlock();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update()
    {
        if (recipe != null)
        {
            if (needsUpdate)
            {
                if (world.getTotalWorldTime() - lastPushed >= 20)
                {
                    needsUpdate = false;
                    progress++;

                    if (progress == recipe.getStepCount())
                    {
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                        inventory.setStackInSlot(1, recipe.getOutputItem());
                    }
                    updateBlock();
                }
            }
        }
    }

    public int getMaxInputCount()
    {
        return (recipe == null) ? 1 : recipe.getInputCount();
    }

    public int getCount()
    {
        return inventory.getStackInSlot(0).getCount();
    }

    public int getMaxProgress()
    {
        return (recipe == null) ? 1 : recipe.getStepCount();
    }

    public int getProgress()
    {
        return progress;
    }

    public boolean hasRecipe()
    {
        return recipe != null;
    }

    public ResourceLocation getInProgressTexture()
    {
        return recipe.getInProgressTexture();
    }

    private void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }
}

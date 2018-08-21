/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import net.dries007.tfc.client.gui.GuiSmallInventory;
import net.dries007.tfc.client.gui.GuiSmallVesselLiquid;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.container.ContainerLogPile;
import net.dries007.tfc.objects.container.ContainerSmallVessel;
import net.dries007.tfc.objects.container.ContainerSmallVesselLiquid;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;

public class TFCGuiHandler implements IGuiHandler
{
    public static final int LOG_PILE = 0;
    public static final int SMALL_VESSEL = 1;
    public static final int SMALL_VESSEL_LIQUID = 2;
    public static final int MOLD = 3;

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack stack;
        switch (ID)
        {
            case LOG_PILE:
                TELogPile teLogPile = Helpers.getTE(world, pos, TELogPile.class);
                return teLogPile == null ? null : new ContainerLogPile(player.inventory, teLogPile);
            case SMALL_VESSEL:
                stack = player.getHeldItemMainhand();
                return new ContainerSmallVessel(player.inventory, stack.getItem() instanceof ItemSmallVessel ?
                    stack : player.getHeldItemOffhand());
            case SMALL_VESSEL_LIQUID:
                stack = player.getHeldItemMainhand();
                return new ContainerSmallVesselLiquid(player.inventory, stack.getItem() instanceof ItemSmallVessel ?
                    stack : player.getHeldItemOffhand());
            case MOLD:

            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Container container = getServerGuiElement(ID, player, world, x, y, z);
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack stack;
        switch (ID)
        {
            case LOG_PILE:
                return new GuiSmallInventory(container, player.inventory, BlocksTFC.LOG_PILE.getTranslationKey());
            case SMALL_VESSEL:
                return new GuiSmallInventory(container, player.inventory, ItemsTFC.CERAMICS_FIRED_VESSEL.getTranslationKey());
            case SMALL_VESSEL_LIQUID:
                stack = player.getHeldItemMainhand();
                return new GuiSmallVesselLiquid(container, player.inventory, stack.getItem() instanceof ItemSmallVessel ?
                    stack : player.getHeldItemOffhand());
            case MOLD:

            default:
                return null;
        }
    }
}

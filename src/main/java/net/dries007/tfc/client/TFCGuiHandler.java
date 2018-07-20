/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import net.dries007.tfc.client.gui.GuiLogPile;
import net.dries007.tfc.objects.container.ContainerLogPile;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;

public class TFCGuiHandler implements IGuiHandler
{
    public static final int LOG_PILE = 0;

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        switch (ID)
        {
            case LOG_PILE:
                TELogPile teLogPile = Helpers.getTE(world, pos, TELogPile.class);
                return teLogPile == null ? null : new ContainerLogPile(player.inventory, teLogPile);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Container container = getServerGuiElement(ID, player, world, x, y, z);
        BlockPos pos = new BlockPos(x, y, z);
        switch (ID)
        {
            case LOG_PILE:
                return new GuiLogPile(container, player.inventory);
            default:
                return null;
        }
    }
}

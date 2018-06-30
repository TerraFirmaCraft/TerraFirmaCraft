/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.Constants.MOD_ID;

public class TELogPile extends TileEntity
{

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "log_pile");

    public TELogPile()
    {
        super();
    }
}

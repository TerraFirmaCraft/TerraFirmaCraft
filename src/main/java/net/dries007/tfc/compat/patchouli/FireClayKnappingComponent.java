/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.TFCGuiHandler;

@SuppressWarnings("unused")
public class FireClayKnappingComponent extends KnappingComponent
{
    @Nullable
    @Override
    protected ResourceLocation getSquareLow(int ticks)
    {
        return TFCGuiHandler.FIRE_CLAY_DISABLED_TEXTURE;
    }

    @Nullable
    @Override
    protected ResourceLocation getSquareHigh(int ticks)
    {
        return TFCGuiHandler.FIRE_CLAY_TEXTURE;
    }
}

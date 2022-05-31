/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.dries007.tfc.util.Helpers;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliIntegration
{
    public static final ResourceLocation BOOK_ID = Helpers.identifier("field_guide");
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/book/icons.png");

    public static void openGui(ServerPlayer player)
    {
        PatchouliAPI.get().openBookGUI(player, BOOK_ID);
    }
}

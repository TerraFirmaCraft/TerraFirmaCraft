/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class PatchouliIntegration
{
    public static void ifEnabled(Runnable action) {}
    public static void openGui(ServerPlayer player) {}
    public static void openGui(ServerPlayer player, ResourceLocation id, int page) {}
    public static ItemStack getFieldGuide(boolean special) { return new ItemStack(Items.APPLE); }
}

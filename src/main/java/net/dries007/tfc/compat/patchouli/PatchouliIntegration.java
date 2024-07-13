/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.server.level.ServerPlayer;

public final class PatchouliIntegration
{
    public static void ifEnabled(Runnable action) {}
    public static void openGui(ServerPlayer player) {}
}

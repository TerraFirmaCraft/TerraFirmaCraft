/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Removes the experimental world gen screen warning that shows up every time loading a TFC world.
     * Incidentally, saves the second 'reload' of data, cutting world loading time in half.
     */
    @ModifyVariable(method = "doLoadLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft$ExperimentalDialogType;NONE:Lnet/minecraft/client/Minecraft$ExperimentalDialogType;", ordinal = 0), ordinal = 3, index = 13, name = "flag1")
    private boolean ignoreExperimentalWarningsScreen(boolean flag1)
    {
        if (TFCConfig.CLIENT.ignoreExperimentalWorldGenWarning.get())
        {
            TerraFirmaCraft.LOGGER.warn("Experimental world gen... dragons or some such.. blah blah.");
            return false;
        }
        return flag1;
    }
}

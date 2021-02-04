/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.concurrent.RecursiveEventLoop;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends RecursiveEventLoop<Runnable>
{
    private MinecraftMixin(String name)
    {
        super(name);
    }

    /**
     * This is a hack to remove the "Experimental Settings" screen which will pop up every time you generate or load a TFC world.
     *
     * Fixed by https://github.com/MinecraftForge/MinecraftForge/pull/7275
     */
    @ModifyVariable(method = "loadWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft$WorldSelectionType;NONE:Lnet/minecraft/client/Minecraft$WorldSelectionType;", ordinal = 0), ordinal = 2, index = 11, name = "flag1")
    private boolean modify$doLoadLevel$flag1(boolean flag1)
    {
        if (TFCConfig.CLIENT.ignoreExperimentalWorldGenWarning.get())
        {
            TerraFirmaCraft.LOGGER.warn("Experimental world gen... dragons or some such.. blah blah.");
            return false;
        }
        return flag1;
    }
}

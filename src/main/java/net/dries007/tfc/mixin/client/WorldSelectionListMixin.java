/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldSelectionListMixin
{
    @Inject(method = "renderExperimentalWarning", at = @At("HEAD"), cancellable = true, remap = false)
    private void ignoreExperimentalWarningIcon(PoseStack stack, int mouseX, int mouseY, int top, int left, CallbackInfo ci)
    {
        if (TFCConfig.CLIENT.ignoreExperimentalWorldGenWarning.get())
        {
            ci.cancel();
        }
    }
}

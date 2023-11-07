/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.patchouli;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.gui.GuiBook;

/**
 * This has been annoying me enough in dev that I want it fixed.
 * Fixes <a href="https://github.com/VazkiiMods/Patchouli/issues/696">Patchouli#696</a>, see the linked <a href="https://github.com/VazkiiMods/Patchouli/pull/697">Pull Request</a>
 */
@Mixin(GuiBook.class)
public abstract class GuiBookMixin implements ContainerEventHandler
{
    @Inject(method = "mouseClickedScaled", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z"), cancellable = true)
    private void preventFocusForPatchyBookButtons(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir)
    {
        for (GuiEventListener listener : this.children())
        {
            if (listener.mouseClicked(mouseX, mouseY, mouseButton))
            {
                if (mouseButton == 0)
                {
                    this.setDragging(true);
                }
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }
}

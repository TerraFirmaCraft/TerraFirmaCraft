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
 * See <a href="https://github.com/VazkiiMods/Patchouli/issues/696">Patchouli#696</a>
 * <p>
 * Unfortunately, when Patchy fixed this issue, they also bumped to a Forge version requirement of >47.2. This means for us, we can't depend on that version without introducing a hard dep on that (and no NeoForge compat possible, since we rely on earlier versions there due to another issue)
 * This mixin has to exist for the edge case of someone using an out-of-date patchouli version on old Forge versions, and we don't want the bugged behavior to appear (for example, in a development environment).
 * <p>
 * This target is set to require = 0, so it should fail without issue on new Patchy versions, as the target method was removed (and replaced with exactly what this mixin is doing).
 */
@Mixin(GuiBook.class)
public abstract class GuiBookMixin implements ContainerEventHandler
{
    @Inject(method = "mouseClickedScaled", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z"), cancellable = true, require = 0)
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

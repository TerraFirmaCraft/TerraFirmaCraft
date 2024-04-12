/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin
{
    /**
     * When a game test server is launched, but fails to startup (i.e. due to a dedicated server not finding a client class too early error), this can cause bugs to slip through, since the game test will appear to pass
     * This simply injects a non-zero return code at the end of `main()`, in the branch where an exception was thrown somewhere in loading.
     */
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Lorg/slf4j/Marker;Ljava/lang/String;Ljava/lang/Throwable;)V", shift = At.Shift.AFTER, remap = false), require = 0)
    private static void exitWithErrorInCIWhenServerCrashesDuringStartup(String[] args, CallbackInfo ci)
    {
        System.exit(1);
    }
}

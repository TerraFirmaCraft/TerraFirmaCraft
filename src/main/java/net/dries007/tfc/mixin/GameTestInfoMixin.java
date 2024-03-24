/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Locale;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestInfo.class)
public abstract class GameTestInfoMixin
{
    /**
     * When storing a game test name into a structure block, first convert the name to lowercase. It is required for it to parse correctly
     * as a {@link ResourceLocation}, and it is compared in a case-insensitive manner later. This is the best solution that doesn't require
     * removing nice display names, and allows {@code /test runthis} to function correctly.
     */
    @Redirect(method = "spawnStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/GameTestInfo;getTestName()Ljava/lang/String;"))
    private String replaceTestNameWithLowerCase(GameTestInfo info)
    {
        return info.getTestName().toLowerCase(Locale.ROOT);
    }
}

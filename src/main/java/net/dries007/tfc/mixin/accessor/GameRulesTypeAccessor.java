/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import java.util.function.BiConsumer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.Type.class)
public interface GameRulesTypeAccessor
{
    @Accessor("callback")
    BiConsumer<MinecraftServer, Object> accessor$getCallback();

    @Mutable
    @Accessor("callback")
    void accessor$setCallback(BiConsumer<MinecraftServer, Object> callback);
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world;

import java.util.function.BiConsumer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to modify the doDaylightCycle game rule callback
 *
 * @see GameRulesAccessor
 */
@Mixin(GameRules.RuleType.class)
public interface GameRulesRuleTypeAccessor
{
    @Accessor("callback")
    BiConsumer<MinecraftServer, Object> accessor$getCallback();

    @Accessor("callback")
    void accessor$setCallback(BiConsumer<MinecraftServer, Object> callback);
}

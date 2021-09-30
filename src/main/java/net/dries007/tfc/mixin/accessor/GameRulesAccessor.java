/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import java.util.Map;

import net.minecraft.world.level.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.class)
public interface GameRulesAccessor
{
    @Accessor("GAME_RULE_TYPES")
    static Map<GameRules.Key<?>, GameRules.Type<?>> accessor$getGameRuleTypes() { throw new AssertionError(); }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world;

import java.util.Map;

import net.minecraft.world.GameRules;

import mcp.MethodsReturnNonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.class)
@MethodsReturnNonnullByDefault
@SuppressWarnings("ConstantConditions")
public interface GameRulesAccessor
{
    /**
     * This is used in order to get and modify game rule callbacks. This is used to run additional callbacks on the doDaylightCycle game rule.
     */
    @Accessor("GAME_RULES")
    static Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> accessor$getGameRuleTypes() { return null; }
}

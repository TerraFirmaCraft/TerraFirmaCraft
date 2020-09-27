package net.dries007.tfc.mixin.world;

import java.util.Map;

import javax.annotation.Nonnull;

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
    @Accessor("GAME_RULE_TYPES")
    static Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> accessor$getGameRuleTypes() { return null; }
}

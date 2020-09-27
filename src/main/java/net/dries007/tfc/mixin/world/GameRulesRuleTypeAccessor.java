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

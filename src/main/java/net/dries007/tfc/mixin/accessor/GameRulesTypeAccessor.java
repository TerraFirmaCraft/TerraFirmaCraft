package net.dries007.tfc.mixin.accessor;

import java.util.function.BiConsumer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.Type.class)
public interface GameRulesTypeAccessor
{
    @Accessor("callback")
    BiConsumer<MinecraftServer, Object> accessor$getCallback();

    @Accessor("callback")
    void accessor$setCallback(BiConsumer<MinecraftServer, Object> callback);
}

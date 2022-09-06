/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes MC-219083 by only doing natural regeneration on server
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends Entity
{
    private PlayerMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"), require = 0)
    private boolean onlyDoNaturalRegenerationOnServer(GameRules instance, GameRules.Key<GameRules.BooleanValue> key)
    {
        return instance.getBoolean(key) && !level.isClientSide;
    }
}

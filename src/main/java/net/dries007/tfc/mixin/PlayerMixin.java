/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerBridge;
import net.dries007.tfc.common.player.PlayerInfo;


@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements PlayerBridge
{
    @Shadow protected FoodData foodData;
    @Unique private PlayerInfo tfc$playerInfo;

    private PlayerMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    @NotNull
    @Override
    public IPlayerInfo tfc$getPlayerInfo()
    {
        return tfc$playerInfo;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replacePlayerInfo(CallbackInfo ci)
    {
        this.tfc$playerInfo = new PlayerInfo((Player) (Object) this);
        this.foodData = this.tfc$playerInfo;
    }

    /**
     * Fixes MC-219083 by only doing natural regeneration on server
     */
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"), require = 0)
    private boolean onlyDoNaturalRegenerationOnServer(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original)
    {
        return key == GameRules.RULE_NATURAL_REGENERATION
            ? original.call(instance, key) && !level().isClientSide
            : original.call(instance, key);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.ai.JavelinAttackGoal;
import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Who knew that mixins could be spooky?
 */
@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Monster
{
    private AbstractSkeletonMixin(EntityType<? extends Monster> type, Level level)
    {
        super(type, level);
    }

    @Inject(method = "reassessWeaponGoal", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void inject$reassessWeaponGoal(CallbackInfo ci)
    {
        ItemStack held = getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof JavelinItem));
        if (held.getItem() instanceof JavelinItem)
        {
            goalSelector.addGoal(4, new JavelinAttackGoal<>(this, 1, 15f));
            ci.cancel();
        }
        else
        {
            goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof JavelinAttackGoal);
        }
    }
}

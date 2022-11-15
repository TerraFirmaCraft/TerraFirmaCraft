/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.items.JavelinItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For some reason mobs do not intelligently select arm poses like players do. Should probably be a forge PR someday.
 */
@Mixin(SkeletonModel.class)
public abstract class SkeletonModelMixin<T extends Mob & RangedAttackMob> extends HumanoidModel<T>
{
    public SkeletonModelMixin(ModelPart part)
    {
        super(part);
    }

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/Mob;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private void inject$prepareMobModel(T entity, float ageInTicks, float limbSwing, float limbSwingAmount, CallbackInfo ci)
    {
        ItemStack itemstack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemstack.getItem() instanceof JavelinItem && entity.isAggressive())
        {
            if (entity.getMainArm() == HumanoidArm.RIGHT)
            {
                this.rightArmPose = ArmPose.THROW_SPEAR;
            }
            else
            {
                this.leftArmPose = ArmPose.THROW_SPEAR;
            }
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void inject$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, CallbackInfo ci)
    {
        if (entity.getMainHandItem().getItem() instanceof JavelinItem)
        {
            ci.cancel();
        }
    }
}

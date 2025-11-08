/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.items.ToolItem.*;

@Mixin(Item.class)
public abstract class ItemMixin
{

    @Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
    public void injectToolDamageCheck(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity, CallbackInfoReturnable<Boolean> cir)
    {
        if (isDurabilityModified(level, pos, state) && !level.isClientSide())
        {
            Tool tool = stack.get(DataComponents.TOOL);
            if (tool == null)
            {
                cir.setReturnValue(false);
            }
            if (modifiedDurability(level, pos, state))
            {
                Helpers.damageItem(stack, miningEntity, EquipmentSlot.MAINHAND);
                cir.setReturnValue(true);
            }
            cir.setReturnValue(false);
        }
    }

}

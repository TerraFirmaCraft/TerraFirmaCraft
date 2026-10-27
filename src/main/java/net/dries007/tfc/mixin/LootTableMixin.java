/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.util.Helpers;

@Mixin(LootTable.class)
public abstract class LootTableMixin
{
    /**
     * Forces the initialization of FoodCapability, needed for mod compatibility when Modern Fix is added.
     */
    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private void initFoodCapabilityOfLootedItems(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir)
    {
        ObjectArrayList<ItemStack> list = cir.getReturnValue();
        if (list != null)
        {
            for (ItemStack stack : list)
            {
                Helpers.initOrUpdateFoodCapability(stack);
            }
        }
    }
}
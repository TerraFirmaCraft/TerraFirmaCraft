/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.dries007.tfc.common.fluids.FluidHelpers;
import org.jetbrains.annotations.Nullable;

public class CopyFluidFunction extends LootItemConditionalFunction
{
    public static void copyToItem(ItemStack stack, @Nullable BlockEntity be)
    {
        if (be == null || stack.isEmpty()) return;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(itemCap -> {
            be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                FluidHelpers.transferUpTo(cap, itemCap, cap.getFluidInTank(0).getAmount());
            });
        });
    }

    public static void copyFromItem(ItemStack stack, @Nullable BlockEntity be)
    {
        if (be == null || stack.isEmpty()) return;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(itemCap -> {
            be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                FluidHelpers.transferUpTo(itemCap, cap, itemCap.getFluidInTank(0).getAmount());
            });
        });
    }

    public CopyFluidFunction(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType getType()
    {
        return TFCLoot.COPY_FLUID.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY))
        {
            copyToItem(stack, context.getParam(LootContextParams.BLOCK_ENTITY));
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyFluidFunction>
    {
        @Override
        public CopyFluidFunction deserialize(JsonObject json, JsonDeserializationContext ctx, LootItemCondition[] conditions)
        {
            return new CopyFluidFunction(conditions);
        }
    }
}

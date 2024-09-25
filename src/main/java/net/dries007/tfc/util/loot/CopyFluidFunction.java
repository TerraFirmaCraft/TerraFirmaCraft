/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.List;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class CopyFluidFunction extends LootItemConditionalFunction
{
    public static final MapCodec<CopyFluidFunction> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).apply(i, CopyFluidFunction::new));

    /**
     * Copies the fluid contained in {@code entity} into the {@code stack}. This does not mutate the block entity's content.
     * @return The {@code stack} but with the fluid contained within the block entity
     */
    public static ItemStack copyToItem(ItemStack stack, @Nullable BlockEntity entity)
    {
        if (entity != null && !stack.isEmpty())
        {
            final IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            final IFluidHandler blockHandler = Helpers.getCapability(Capabilities.FluidHandler.BLOCK, entity);
            if (itemHandler != null && blockHandler != null)
            {
                itemHandler.fill(blockHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE);
                return itemHandler.getContainer();
            }
        }
        return stack;
    }

    /**
     * Copies the fluid contained in {@code stack} into the {@code entity}. This does not modify the stack, only the block entity.
     */
    public static void copyFromItem(ItemStack stack, @Nullable BlockEntity entity)
    {
        if (entity != null && !stack.isEmpty())
        {
            final IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            final IFluidHandler blockHandler = Helpers.getCapability(Capabilities.FluidHandler.BLOCK, entity);
            if (itemHandler != null && blockHandler != null)
            {
                blockHandler.fill(itemHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public CopyFluidFunction(List<LootItemCondition> conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType<CopyFluidFunction> getType()
    {
        return TFCLoot.COPY_FLUID.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY))
        {
            return copyToItem(stack, context.getParam(LootContextParams.BLOCK_ENTITY));
        }
        return stack;
    }
}

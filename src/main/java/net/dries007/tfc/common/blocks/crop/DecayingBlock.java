/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;

public class DecayingBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final ResourceLocation DECAYING_DYNAMIC_DROP_ID = Helpers.identifier("decaying");

    public static boolean isRotten(Level level, BlockPos pos)
    {
        return level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying && decaying.isRotten();
    }

    private final Supplier<? extends Block> rotted;

    public DecayingBlock(ExtendedProperties properties, Supplier<? extends Block> rotted)
    {
        super(properties);
        this.rotted = rotted;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying)
        {
            decaying.setStack(stack);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying && player.isCreative())
        {
            decaying.setStack(ItemStack.EMPTY);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
    {
        BlockEntity blockentity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof DecayingBlockEntity be)
        {
            params.withDynamicDrop(DECAYING_DYNAMIC_DROP_ID, dynamicDrop -> {
                dynamicDrop.accept(be.getStack());
            });
        }
        return super.getDrops(state, params);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final @Nullable IFood food = FoodCapability.get(context.getItemInHand());
        return food != null && food.isRotten()
            ? getRottedBlock().defaultBlockState()
            : defaultBlockState();
    }

    public Block getRottedBlock()
    {
        return rotted.get();
    }
}

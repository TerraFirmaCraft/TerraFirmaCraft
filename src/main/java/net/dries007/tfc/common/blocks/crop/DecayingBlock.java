/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.loot.TFCLoot;
import org.jetbrains.annotations.Nullable;

public class DecayingBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static boolean isRotten(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying)
        {
            final ItemStack stack = decaying.getStack();
            return stack.isEmpty() || stack.getCapability(FoodCapability.CAPABILITY).map(IFood::isRotten).orElse(false);
        }
        return false;
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
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DecayingBlockEntity decaying && !(Helpers.isBlock(state, newState.getBlock())))
        {
            Helpers.spawnItem(level, pos, decaying.getStack());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getItemInHand().getCapability(FoodCapability.CAPABILITY).map(cap ->
            cap.isRotten() ? getRottedBlock().defaultBlockState() : defaultBlockState()
        ).orElse(defaultBlockState());
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        builder = builder.withParameter(TFCLoot.DECAY_HANDLED, true);
        return super.getDrops(state, builder);
    }

    public Block getRottedBlock()
    {
        return rotted.get();
    }
}

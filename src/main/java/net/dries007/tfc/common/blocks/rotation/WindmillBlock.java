/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.network.RotationOwner;

public class WindmillBlock extends DeviceBlock implements EntityBlockExtension, ConnectedAxleBlock
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_5;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private final Supplier<? extends AxleBlock> axle;

    public WindmillBlock(ExtendedProperties properties, Supplier<? extends AxleBlock> axle)
    {
        super(properties, InventoryRemoveBehavior.DROP);

        this.axle = axle;

        registerDefaultState(getStateDefinition().any().setValue(COUNT, 1));
    }

    @Override
    public AxleBlock getAxle()
    {
        return axle.get();
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        RotationOwner.onTick(level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final int count = state.getValue(COUNT);
        if (level.getBlockEntity(pos) instanceof WindmillBlockEntity windmill)
        {
            final IItemHandler inventory = windmill.getInventory();
            if (!stack.isEmpty())
            {
                if (count == WindmillBlockEntity.SLOTS)
                {
                    return ItemInteractionResult.FAIL; // Inventory already full, so fail, rather than trying to extract
                }
                inventory.insertItem(count, stack.copyWithCount(1), false);
                if (!player.isCreative()) stack.shrink(1); // Consume the item that was inserted, if not creative
                final int newCount = windmill.updateState();
                if (newCount == WindmillBlockEntity.SLOTS && player instanceof ServerPlayer server)
                {
                    TFCAdvancements.MAX_WINDMILL.trigger(server);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                final ItemStack removed = inventory.extractItem(count - 1, 1, false);
                if (!player.isCreative()) ItemHandlerHelper.giveItemToPlayer(player, removed);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS, COUNT));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.X ? AxleBlock.SHAPE_X : AxleBlock.SHAPE_Z;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return new ItemStack(TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE));
    }
}

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.items.BarrelBlockItem;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.Helpers;

public class BarrelRackBlock extends ExtendedBlock
{
    public BarrelRackBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof BarrelBlockItem blockItem)
        {
            BlockState barrelState = blockItem.getBlock().defaultBlockState().setValue(BarrelBlock.FACING, Direction.orderedByNearest(player)[0].getOpposite()).setValue(BarrelBlock.RACK, true);
            barrelState = BlockItemPlacement.updateBlockStateFromTag(pos, level, item, barrelState);
            level.setBlockAndUpdate(pos, barrelState);
            BlockItem.updateCustomBlockEntityTag(level, player, pos, item);
            if (!player.isCreative()) item.shrink(1);
            Helpers.playSound(level, pos, SoundEvents.WOOD_PLACE);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
    {
        return context.getItemInHand().getItem() instanceof BarrelBlockItem && BottomSupportedDeviceBlock.canSurvive(context.getLevel(), context.getClickedPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return BottomSupportedDeviceBlock.canSurvive(level, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return BottomSupportedDeviceBlock.canSurvive(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !facingState.isFaceSturdy(level, facingPos, Direction.UP) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return BarrelBlock.RACK_SHAPE;
    }

}

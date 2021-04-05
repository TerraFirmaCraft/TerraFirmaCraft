package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class LogPileBlock extends DeviceBlock implements IForgeBlockProperties
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public LogPileBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!worldIn.isClientSide() && worldIn instanceof World)
        {
            if (facingState.is(BlockTags.FIRE))
            {
                BurningLogPileBlock.tryLightLogPile((World) worldIn, currentPos);
            }
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.isClientSide()) return ActionResultType.SUCCESS;
        LogPileTileEntity te = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
        ItemStack stack = player.getItemInHand(hand);
        if (te != null)
        {
            if (stack.getItem().is(TFCTags.Items.LOG_PILE_LOGS))
            {
                if (!player.isShiftKeyDown()) // shift interaction handled in InteractionManager
                {
                    if (te.insertLog(stack.copy()))
                    {
                        if (!world.isClientSide())
                        {
                            Helpers.playSound(world, pos, SoundEvents.WOOD_PLACE);
                            if (!player.isCreative())
                                stack.shrink(1);
                        }
                        return ActionResultType.sidedSuccess(world.isClientSide());
                    }
                }
            }

            if (!player.isShiftKeyDown() && player instanceof ServerPlayerEntity)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.FAIL;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        LogPileTileEntity te = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
        return te != null ? te.getLog().copy() : ItemStack.EMPTY;
    }
}

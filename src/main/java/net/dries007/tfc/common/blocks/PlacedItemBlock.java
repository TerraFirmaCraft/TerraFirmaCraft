package net.dries007.tfc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.InventoryTileEntity;
import net.dries007.tfc.common.tileentity.PlacedItemTileEntity;
import net.dries007.tfc.util.Helpers;

public class PlacedItemBlock extends Block implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    private static final BooleanProperty ITEM_0 = TFCBlockStateProperties.ITEM_0;
    private static final BooleanProperty ITEM_1 = TFCBlockStateProperties.ITEM_1;
    private static final BooleanProperty ITEM_2 = TFCBlockStateProperties.ITEM_2;
    private static final BooleanProperty ITEM_3 = TFCBlockStateProperties.ITEM_3;

    public static final BooleanProperty[] ITEMS = new BooleanProperty[] {ITEM_0, ITEM_1, ITEM_2, ITEM_3};

    private static final VoxelShape SHAPE_0 = box(0, 0, 0, 8.0D, 1.0D, 8.0D);
    private static final VoxelShape SHAPE_1 = box(8.0D, 0, 0, 16.0D, 1.0D, 8.0D); // x
    private static final VoxelShape SHAPE_2 = box(0, 0, 8.0D, 8.0D, 1.0D, 16.0D); // z
    private static final VoxelShape SHAPE_3 = box(8.0D, 0, 8.0D, 16.0D, 1.0D, 16.0D); // xz

    private static final VoxelShape[] SHAPES = new VoxelShape[] {SHAPE_0, SHAPE_1, SHAPE_2, SHAPE_3};

    public PlacedItemBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(ITEM_0, false).setValue(ITEM_1, false).setValue(ITEM_2, false).setValue(ITEM_3, false));
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide())
        {
            PlacedItemTileEntity te = Helpers.getTileEntity(worldIn, pos, PlacedItemTileEntity.class);
            if (te != null)
            {
                ItemStack held = player.getItemInHand(handIn);
                return te.onRightClick(player, held, hit) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        BlockState updateState = updateStateValues(worldIn, currentPos.below(), stateIn);
        PlacedItemTileEntity te = Helpers.getTileEntity(worldIn, currentPos, PlacedItemTileEntity.class);
        if (te != null)
        {
            if (isBlank(updateState))
            {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return updateState;
    }

    public boolean isBlank(BlockState state)
    {
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEMS[i]))
                return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        InventoryTileEntity te = Helpers.getTileEntity(world, pos, InventoryTileEntity.class);
        if (te != null)
        {
            te.onBreak();
            te.setRemoved();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape = VoxelShapes.empty();
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEMS[i]))
                shape = VoxelShapes.or(shape, SHAPES[i]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ITEM_0, ITEM_1, ITEM_2, ITEM_3);
    }

    /**
     * Pos refers to below block, state refers to the placed item state.
     */
    public static BlockState updateStateValues(IWorld world, BlockPos pos, BlockState state)
    {
        for (int i = 0; i < 4; i++)
        {
            state = state.setValue(ITEMS[i], isSlotSupportedOn(world, pos, world.getBlockState(pos), i));
        }
        return state;
    }

    /**
     * Pos and state refer to the below block
     */
    public static boolean isSlotSupportedOn(IWorld world, BlockPos pos, BlockState state, int slot)
    {
        VoxelShape supportShape = state.getBlockSupportShape(world, pos).getFaceShape(Direction.UP);
        return !VoxelShapes.joinIsNotEmpty(supportShape, SHAPES[slot], IBooleanFunction.ONLY_SECOND);
    }
}

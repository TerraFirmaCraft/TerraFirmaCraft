package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.common.tileentity.PlacedItemTileEntity;
import net.dries007.tfc.util.Helpers;

public class PlacedItemBlock extends DeviceBlock implements IForgeBlockProperties
{
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

    private static void convertPlacedItemToPitKiln(World world, BlockPos pos, ItemStack strawStack)
    {
        PlacedItemTileEntity teOld = Helpers.getTileEntity(world, pos, PlacedItemTileEntity.class);
        if (teOld != null)
        {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            ItemStack[] inventory = new ItemStack[4];
            teOld.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
                for (int i = 0; i < 4; i++)
                {
                    inventory[i] = cap.extractItem(i, 64, false);
                }
            });

            // Replace the block
            world.setBlockAndUpdate(pos, TFCBlocks.PIT_KILN.get().defaultBlockState());
            teOld.setRemoved();
            // Play placement sound
            world.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
            // Copy TE data
            PitKilnTileEntity teNew = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
            if (teNew != null)
            {
                // Copy inventory
                teNew.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
                    for (int i = 0; i < 4; i++)
                    {
                        if (inventory[i] != null && !inventory[i].isEmpty())
                        {
                            cap.insertItem(i, inventory[i], false);
                        }
                    }
                });
                // Copy misc data
                teNew.isHoldingLargeItem = teOld.isHoldingLargeItem;
                teNew.addStraw(strawStack, 0);
            }
        }
    }

    public PlacedItemBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(ITEM_0, true).setValue(ITEM_1, true).setValue(ITEM_2, true).setValue(ITEM_3, true));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        BlockState updateState = updateStateValues(worldIn, currentPos.below(), stateIn);
        PlacedItemTileEntity te = Helpers.getTileEntity(worldIn, currentPos, PlacedItemTileEntity.class);
        if (te != null)
        {
            if (isEmpty(updateState))
            {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return updateState;
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
                if (held.getItem().is(TFCTags.Items.PIT_KILN_STRAW) && held.getCount() >= 4 && PitKilnTileEntity.isValid(worldIn, pos))
                {
                    convertPlacedItemToPitKiln(worldIn, pos, held.split(4));
                    return ActionResultType.SUCCESS;
                }
                return te.onRightClick(player, held, hit) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
            }
        }
        return ActionResultType.FAIL;
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

    public boolean isEmpty(BlockState state)
    {
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEMS[i]))
                return false;
        }
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ITEM_0, ITEM_1, ITEM_2, ITEM_3);
    }
}

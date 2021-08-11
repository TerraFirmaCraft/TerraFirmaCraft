/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.common.tileentity.PlacedItemTileEntity;
import net.dries007.tfc.util.Helpers;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

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
    public static BlockState updateStateValues(LevelAccessor world, BlockPos pos, BlockState state)
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
    public static boolean isSlotSupportedOn(LevelAccessor world, BlockPos pos, BlockState state, int slot)
    {
        VoxelShape supportShape = state.getBlockSupportShape(world, pos).getFaceShape(Direction.UP);
        return !Shapes.joinIsNotEmpty(supportShape, SHAPES[slot], BooleanOp.ONLY_SECOND);
    }

    private static void convertPlacedItemToPitKiln(Level world, BlockPos pos, ItemStack strawStack)
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
            world.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
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
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide())
        {
            PlacedItemTileEntity te = Helpers.getTileEntity(worldIn, pos, PlacedItemTileEntity.class);
            if (te != null)
            {
                ItemStack held = player.getItemInHand(handIn);
                if (TFCTags.Items.PIT_KILN_STRAW.contains(held.getItem()) && held.getCount() >= 4 && PitKilnTileEntity.isValid(worldIn, pos))
                {
                    convertPlacedItemToPitKiln(worldIn, pos, held.split(4));
                    return InteractionResult.SUCCESS;
                }
                return te.onRightClick(player, held, hit) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = Shapes.empty();
        for (int i = 0; i < 4; i++)
        {
            if (state.getValue(ITEMS[i]))
                shape = Shapes.or(shape, SHAPES[i]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ITEM_0, ITEM_1, ITEM_2, ITEM_3);
    }
}

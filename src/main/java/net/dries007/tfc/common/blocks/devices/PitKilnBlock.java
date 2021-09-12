/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.util.Helpers;

public class PitKilnBlock extends DeviceBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.PIT_KILN_STAGE;
    public static final int STRAW_END = 7;
    public static final int LOG_START = 8;
    public static final int LIT = 16;
    public static final VoxelShape[] SHAPE_BY_LAYER = Util.make(new VoxelShape[17], shapes -> {
        for (int i = 0; i < 8; i++)
        {
            shapes[i] = Block.box(0.0D, 0.0D, 0.0D, 16.0D, i + 1, 16.0D);
        }
        for (int i = 0; i < 4; i++)
        {
            shapes[8 + i] = Shapes.or(shapes[7 + i], Block.box(4 * i, 8.0D, 0.0D, 4 * (i + 1), 12.0D, 16.0D));
        }
        for (int i = 0; i < 4; i++)
        {
            shapes[12 + i] = Shapes.or(shapes[11 + i], Block.box(4 * i, 12.0D, 0.0D, 4 * (i + 1), 16.0D, 16.0D));
        }
        shapes[16] = Shapes.block(); // lit stage
    });

    public PitKilnBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(STAGE) == LIT)
        {
            double x = pos.getX() + rand.nextFloat();
            double y = pos.getY() + rand.nextFloat();
            double z = pos.getZ() + rand.nextFloat();
            for (int i = 0; i < rand.nextInt(3); i++)
            {
                worldIn.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 0, 0.1f + rand.nextFloat() / 8, 0);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND)
        {
            PitKilnTileEntity te = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
            if (te != null)
            {

                ItemStack held = player.getItemInHand(hand);
                Item item = held.getItem();
                int stage = state.getValue(STAGE);
                if (stage < STRAW_END && TFCTags.Items.PIT_KILN_STRAW.contains(item))
                {
                    world.setBlock(pos, state.setValue(STAGE, stage + 1), 10);
                    te.addStraw(held.split(1), stage + 1);
                    Helpers.playSound(world, pos, SoundEvents.GRASS_PLACE);
                }
                else if (stage >= STRAW_END && stage < LIT - 1 && TFCTags.Items.PIT_KILN_LOGS.contains(item))
                {
                    world.setBlock(pos, state.setValue(STAGE, stage + 1), 10);
                    te.addLog(held.split(1), stage - LOG_START + 1);
                    Helpers.playSound(world, pos, SoundEvents.WOOD_PLACE);
                }
                else if (held.isEmpty())
                {
                    if (stage != LIT)
                    {
                        NonNullList<ItemStack> logItems = te.getLogs();
                        NonNullList<ItemStack> strawItems = te.getStraws();
                        ItemStack dropStack;
                        if (stage >= LOG_START)
                        {
                            dropStack = logItems.get(stage - LOG_START).copy();
                            te.deleteLog(stage - LOG_START);
                        }
                        else
                        {
                            dropStack = strawItems.get(stage).copy();
                            te.deleteStraw(stage);
                        }
                        if (!dropStack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, dropStack);
                        }
                        if (stage == 0)
                        {
                            PitKilnTileEntity.convertPitKilnToPlacedItem(world, pos);
                        }
                        else
                        {
                            world.setBlock(pos, state.setValue(STAGE, stage - 1), 10);
                        }
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(worldIn, pos.below()), Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }
}

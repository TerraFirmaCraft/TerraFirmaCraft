package net.dries007.tfc.common.blocks.devices;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.util.Helpers;

public class PitKilnBlock extends DeviceBlock implements IForgeBlockProperties
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
            shapes[8 + i] = VoxelShapes.or(shapes[7 + i], Block.box(4 * i, 8.0D, 0.0D, 4 * (i + 1), 12.0D, 16.0D));
        }
        for (int i = 0; i < 4; i++)
        {
            shapes[12 + i] = VoxelShapes.or(shapes[11 + i], Block.box(4 * i, 12.0D, 0.0D, 4 * (i + 1), 16.0D, 16.0D));
        }
        shapes[16] = VoxelShapes.block(); // lit stage
    });

    public PitKilnBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (!world.isClientSide() && hand == Hand.MAIN_HAND)
        {
            PitKilnTileEntity te = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
            if (te == null) return ActionResultType.FAIL;

            ItemStack held = player.getItemInHand(hand);
            Item item = held.getItem();
            int stage = state.getValue(STAGE);
            if (stage < STRAW_END && item.is(TFCTags.Items.PIT_KILN_STRAW) && held.getCount() >= 4)
            {
                world.setBlock(pos, state.setValue(STAGE, stage + 1), 10);
                te.addStraw(held.split(4), stage + 1);
                Helpers.playSound(world, pos, SoundEvents.GRASS_PLACE);
            }
            else if (stage >= STRAW_END && stage < LIT - 1 && item.is(TFCTags.Items.PIT_KILN_LOGS))
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
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(worldIn, pos.below()), Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }
}

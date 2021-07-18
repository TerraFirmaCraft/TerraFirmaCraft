/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;
import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.tileentity.CharcoalForgeTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MultiBlock;

public class CharcoalForgeBlock extends DeviceBlock
{
    public static final IntegerProperty HEAT = TFCBlockStateProperties.HEAT_LEVEL;

    private static final MultiBlock FORGE_MULTIBLOCK;

    static
    {
        BiPredicate<IWorld, BlockPos> skyMatcher = IWorld::canSeeSky;
        BiPredicate<IWorld, BlockPos> isValidSide = Helpers.createTagCheck(TFCTags.Blocks.FORGE_INSULATION);
        FORGE_MULTIBLOCK = new MultiBlock()
            // Top block
            .match(new BlockPos(0, 1, 0), BlockState::isAir)//todo: crucible is also acceptable
            // Chimney
            .matchOneOf(new BlockPos(0, 1, 0), new MultiBlock()
                .match(new BlockPos(0, 0, 0), skyMatcher)
                .match(new BlockPos(0, 0, 1), skyMatcher)
                .match(new BlockPos(0, 0, 2), skyMatcher)
                .match(new BlockPos(0, 0, -1), skyMatcher)
                .match(new BlockPos(0, 0, -2), skyMatcher)
                .match(new BlockPos(1, 0, 0), skyMatcher)
                .match(new BlockPos(2, 0, 0), skyMatcher)
                .match(new BlockPos(-1, 0, 0), skyMatcher)
                .match(new BlockPos(-2, 0, 0), skyMatcher)
            )
            // Underneath
            .match(new BlockPos(1, 0, 0), isValidSide)
            .match(new BlockPos(-1, 0, 0), isValidSide)
            .match(new BlockPos(0, 0, 1), isValidSide)
            .match(new BlockPos(0, 0, -1), isValidSide)
            .match(new BlockPos(0, -1, 0), isValidSide);
    }

    public static boolean isValid(IWorld world, BlockPos pos)
    {
        return FORGE_MULTIBLOCK.test(world, pos);
    }

    public CharcoalForgeBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HEAT, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (player instanceof ServerPlayerEntity && !player.isShiftKeyDown())
        {
            CharcoalForgeTileEntity te = Helpers.getTileEntity(world, pos, CharcoalForgeTileEntity.class);
            if (te != null)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        if (state.getValue(HEAT) > 0 && !isValid(world, pos))
        {
            world.setBlockAndUpdate(pos, defaultBlockState().setValue(HEAT, 0));
        }
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && world.getBlockState(pos).getValue(HEAT) > 0)
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(world, pos, entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(HEAT) > 0 && !isValid(world, currentPos) ? state.setValue(HEAT, 0) : state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return CharcoalPileBlock.SHAPE_BY_LAYER[7];
    }
}

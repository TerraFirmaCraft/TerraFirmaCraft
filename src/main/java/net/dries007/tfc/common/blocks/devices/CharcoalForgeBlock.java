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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
        BlockPos origin = BlockPos.ZERO;
        FORGE_MULTIBLOCK = new MultiBlock()
            // Top block
            .match(origin.above(), state -> state.isAir() || state.is(TFCTags.Blocks.FORGE_INVISIBLE_WHITELIST))//todo: crucible is also acceptable
            // Chimney
            .matchOneOf(origin.above(), new MultiBlock()
                .match(origin, skyMatcher)
                .matchHorizontal(origin, skyMatcher, 1)
                .matchHorizontal(origin, skyMatcher, 2)
            )
            // Underneath
            .matchEachDirection(origin, isValidSide, new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN}, 1);
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if (state.getValue(HEAT) == 0) return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.875D;
        double z = pos.getZ() + 0.5D;

        if (rand.nextInt(10) == 0)
        {
            world.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < 1 + rand.nextInt(2); i++)
        {
            world.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, x + Helpers.fastGaussian(rand), y + rand.nextDouble(), z + Helpers.fastGaussian(rand), 0, 0.07D, 0);
        }
        for (int i = 0; i < rand.nextInt(3); i++)
        {
            world.addParticle(ParticleTypes.SMOKE, x + Helpers.fastGaussian(rand), y + rand.nextDouble(), z + Helpers.fastGaussian(rand), 0, 0.005D, 0);
        }
        if (rand.nextInt(8) == 1)
        {
            world.addParticle(ParticleTypes.LAVA, x + Helpers.fastGaussian(rand), y + rand.nextDouble(), z + Helpers.fastGaussian(rand), 0, 0.005D, 0);
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(HEAT) > 0 && !isValid(world, currentPos) ? state.setValue(HEAT, 0) : state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        CharcoalForgeTileEntity te = Helpers.getTileEntity(world, pos, CharcoalForgeTileEntity.class);
        if (te != null)
        {
            if (player instanceof ServerPlayerEntity)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return CharcoalPileBlock.SHAPE_BY_LAYER[7];
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
}

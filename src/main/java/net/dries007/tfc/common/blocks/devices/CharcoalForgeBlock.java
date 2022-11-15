/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.MultiBlock;

public class CharcoalForgeBlock extends DeviceBlock implements IBellowsConsumer
{
    public static final IntegerProperty HEAT = TFCBlockStateProperties.HEAT_LEVEL;

    private static final MultiBlock FORGE_MULTIBLOCK;

    static
    {
        BiPredicate<LevelAccessor, BlockPos> skyMatcher = LevelAccessor::canSeeSky;
        BiPredicate<LevelAccessor, BlockPos> isValidSide = (level, pos) -> isForgeInsulationBlock(level.getBlockState(pos));
        BlockPos origin = BlockPos.ZERO;
        FORGE_MULTIBLOCK = new MultiBlock()
            // Top block
            .match(origin.above(), state -> state.isAir() || Helpers.isBlock(state, TFCTags.Blocks.FORGE_INVISIBLE_WHITELIST))
            // Chimney
            .matchOneOf(origin.above(), new MultiBlock()
                .match(origin, skyMatcher)
                .matchHorizontal(origin, skyMatcher, 1)
                .matchHorizontal(origin, skyMatcher, 2)
            )
            // Underneath
            .matchEachDirection(origin, isValidSide, new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN}, 1);
    }

    public static boolean isValid(LevelAccessor world, BlockPos pos)
    {
        return FORGE_MULTIBLOCK.test(world, pos);
    }

    public static boolean isForgeInsulationBlock(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.FORGE_INSULATION);
    }

    public CharcoalForgeBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HEAT, 0));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand)
    {
        if (state.getValue(HEAT) == 0) return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.875D;
        double z = pos.getZ() + 0.5D;

        if (rand.nextInt(10) == 0)
        {
            world.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < 1 + rand.nextInt(2); i++)
        {
            world.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.07D, 0);
        }
        for (int i = 0; i < rand.nextInt(3); i++)
        {
            world.addParticle(ParticleTypes.SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
        if (rand.nextInt(8) == 1)
        {
            world.addParticle(ParticleTypes.LAVA, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && world.getBlockState(pos).getValue(HEAT) > 0)
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(world, pos, state, entity);
    }

    @Override
    public void intakeAir(Level level, BlockPos pos, BlockState state, int amount)
    {
        level.getBlockEntity(pos, TFCBlockEntities.CHARCOAL_FORGE.get()).ifPresent(forge -> forge.intakeAir(amount));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HEAT));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(HEAT) > 0 && !isValid(world, currentPos) ? state.setValue(HEAT, 0) : state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        CharcoalForgeBlockEntity forge = level.getBlockEntity(pos, TFCBlockEntities.CHARCOAL_FORGE.get()).orElse(null);
        if (forge != null)
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, forge, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return CharcoalPileBlock.SHAPE_BY_LAYER[7];
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (state.getValue(HEAT) > 0)
        {
            if (isValid(level, pos))
            {
                Helpers.fireSpreaderTick(level, pos, rand, 3);
            }
            else
            {
                level.setBlockAndUpdate(pos, defaultBlockState().setValue(HEAT, 0));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return false;
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.client.IGhostBlockHandler;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class FirepitBlock extends DeviceBlock implements IGhostBlockHandler, IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final VoxelShape BASE_SHAPE = Shapes.or(
        box(0, 0, 0.5, 3, 1.5, 3),
        box(5, 0, 0.5, 9, 1, 3),
        box(11, 0, 0, 13, 1.5, 2),
        box(9, 0, 0.5, 11, 0.5, 2),
        box(3, 0, 0, 5.5, 0.5, 2),
        box(13, 0, 0, 15.5, 1, 2),
        box(14, 0, 2, 16, 1, 4),
        box(14, 0, 6, 16, 1.5, 9),
        box(13.5, 0, 4, 15.5, 0.5, 6),
        box(13.5, 0, 10, 15.5, 1.5, 12),
        box(14, 0, 9, 16, 1, 11),
        box(1, 0, 3, 3, 1, 5),
        box(0, 0, 4, 1, 1, 6),
        box(1, 0, 5, 2, 1, 6),
        box(0, 0, 6, 3, 1.5, 10),
        box(0.5, 0, 10, 2.5, 1, 12),
        box(0, 0, 11, 3, 0.5, 13),
        box(1, 0, 13, 3, 1, 15),
        box(3, 0, 14, 5.5, 1.5, 16),
        box(5.5, 0, 14, 8.5, 1, 16),
        box(8.5, 0, 13.5, 10.5, 1.5, 15.5),
        box(10.5, 0, 14, 12.5, 0.5, 16),
        box(12.5, 0, 12, 15.5, 1, 15),
        box(2, 0, 2, 14, 1.0, 14)
    );

    public static boolean canSurvive(LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP);
    }

    public FirepitBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);

        registerDefaultState(getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        Helpers.fireSpreaderTick(level, pos, random, 2);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        if (!state.getValue(LIT)) return;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + getParticleHeightOffset();
        double z = pos.getZ() + 0.5;

        if (rand.nextInt(10) == 0)
        {
            level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < 1 + rand.nextInt(3); i++)
        {
            level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.07D, 0);
        }
        for (int i = 0; i < rand.nextInt(4); i++)
        {
            level.addParticle(ParticleTypes.SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
        if (rand.nextInt(8) == 1)
        {
            level.addParticle(ParticleTypes.LARGE_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && level.getBlockState(pos).getValue(LIT))
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState lookState, Direction direction, BlockPos pos, double x, double y, double z, ItemStack item)
    {
        if (Helpers.isItem(item, TFCItems.POT.get()))
        {
            return TFCBlocks.POT.get().defaultBlockState().setValue(LIT, lookState.getValue(LIT));
        }
        else if (Helpers.isItem(item, TFCItems.WROUGHT_IRON_GRILL.get()))
        {
            return TFCBlocks.GRILL.get().defaultBlockState().setValue(LIT, lookState.getValue(LIT));
        }
        return null;
    }

    @Override
    public float alpha()
    {
        return 0.33F;
    }

    @Override
    public void intakeAir(Level level, BlockPos pos, BlockState state, int amount)
    {
        level.getBlockEntity(pos, TFCBlockEntities.FIREPIT.get()).ifPresent(firepit -> firepit.intakeAir(amount));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final AbstractFirepitBlockEntity<?> firepit = level.getBlockEntity(pos, TFCBlockEntities.FIREPIT.get()).orElse(null);
        if (firepit != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == TFCItems.POT.get() || stack.getItem() == TFCItems.WROUGHT_IRON_GRILL.get())
            {
                if (!level.isClientSide)
                {
                    AbstractFirepitBlockEntity.convertTo(level, pos, state, firepit, stack.getItem() == TFCItems.POT.get() ? TFCBlocks.POT.get() : TFCBlocks.GRILL.get());
                    if (!player.isCreative()) stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            else if (Helpers.isItem(stack.getItem(), TFCTags.Items.EXTINGUISHER) && state.getValue(LIT))
            {
                firepit.extinguish(state);
                return InteractionResult.SUCCESS;
            }
            else
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    NetworkHooks.openGui(serverPlayer, firepit, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
    {
        return FirepitBlock.canSurvive(world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return BASE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return false;
    }

    protected double getParticleHeightOffset()
    {
        return 0.35D;
    }
}

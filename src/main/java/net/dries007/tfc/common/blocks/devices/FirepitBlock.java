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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.items.FirestarterItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class FirepitBlock extends DeviceBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final BooleanProperty LIT = TFCBlockStateProperties.LIT;

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

    public static boolean canSurvive(LevelReader world, BlockPos pos)
    {
        return world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP);
    }

    public FirepitBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(LIT, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand)
    {
        if (!state.getValue(LIT)) return;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + getParticleHeightOffset();
        double z = pos.getZ() + 0.5;

        if (rand.nextInt(10) == 0)
        {
            world.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < 1 + rand.nextInt(3); i++)
        {
            world.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.07D, 0);
        }
        for (int i = 0; i < rand.nextInt(4); i++)
        {
            world.addParticle(ParticleTypes.SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
        if (rand.nextInt(8) == 1)
        {
            world.addParticle(ParticleTypes.LARGE_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && world.getBlockState(pos).getValue(LIT))
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(world, pos, state, entity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final AbstractFirepitBlockEntity<?> firepit = Helpers.getBlockEntity(world, pos, AbstractFirepitBlockEntity.class);
        if (firepit != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == TFCItems.POT.get() || stack.getItem() == TFCItems.WROUGHT_IRON_GRILL.get())
            {
                if (!world.isClientSide)
                {
                    AbstractFirepitBlockEntity.convertTo(world, pos, state, firepit, stack.getItem() == TFCItems.POT.get() ? TFCBlocks.POT.get() : TFCBlocks.GRILL.get());
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            else if (TFCTags.Items.EXTINGUISHER.contains(stack.getItem()))
            {
                firepit.extinguish(state);
                return InteractionResult.SUCCESS;
            }
            else
            {
                // Special case: when using a firestarter on an unlit firepit, assume we want to start a fire and don't open the gui
                if ((stack.getItem() instanceof FirestarterItem || (hand == InteractionHand.OFF_HAND && player.getMainHandItem().getItem() instanceof FirestarterItem)) && !state.getValue(LIT))
                {
                    return InteractionResult.PASS;
                }
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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return BASE_SHAPE;
    }

    protected double getParticleHeightOffset()
    {
        return 0.35D;
    }
}

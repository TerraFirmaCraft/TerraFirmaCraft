/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
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
            .match(origin.above(), state -> state.isAir() || Helpers.isBlock(state, TFCTags.Blocks.CHARCOAL_FORGE_INVISIBLE))
            // Chimney
            .matchOneOf(origin.above(), new MultiBlock()
                .match(origin, skyMatcher)
                .matchHorizontal(origin, skyMatcher, 1)
                .matchHorizontal(origin, skyMatcher, 2)
            )
            // Underneath
            .matchEachDirection(origin, isValidSide, new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN}, 1);
    }

    public static boolean isValid(LevelAccessor level, BlockPos pos)
    {
        return FORGE_MULTIBLOCK.test(level, pos);
    }

    public static boolean isForgeInsulationBlock(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.CHARCOAL_FORGE_INSULATION);
    }

    public CharcoalForgeBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HEAT, 0));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
    {
        if (state.getValue(HEAT) == 0) return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.875D;
        double z = pos.getZ() + 0.5D;

        if (rand.nextInt(10) == 0)
        {
            level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < 1 + rand.nextInt(2); i++)
        {
            level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.07D, 0);
        }
        for (int i = 0; i < rand.nextInt(3); i++)
        {
            level.addParticle(ParticleTypes.SMOKE, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
        if (rand.nextInt(8) == 1)
        {
            level.addParticle(ParticleTypes.LAVA, x + Helpers.triangle(rand), y + rand.nextDouble(), z + Helpers.triangle(rand), 0, 0.005D, 0);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (entity instanceof LivingEntity && state.getValue(HEAT) > 0)
        {
            entity.hurt(entity.damageSources().hotFloor(), 1f);
        }
        super.stepOn(level, pos, state, entity);
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
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
    {
        return state.getValue(HEAT) > 0 && !isValid(world, currentPos) ? state.setValue(HEAT, 0) : state;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return TFCConfig.SERVER.charcoalForgeEnableAutomation.get();
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof CharcoalForgeBlockEntity charcoalForge)
        {
            if (!charcoalForge.getInventory().getStackInSlot(0).isEmpty())
            {
                final int maxSlot = CharcoalForgeBlockEntity.SLOT_FUEL_MAX;
                for (int i = 0; i <= maxSlot; i++)
                {
                    if (charcoalForge.getInventory().getStackInSlot(i).isEmpty())
                    {
                        return Mth.clamp(i * 15 / (maxSlot + 1), 1, 15);
                    }
                }
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof CharcoalForgeBlockEntity forge)
        {
            if (tryInsert(forge, player, stack))
            {
                Helpers.playPlaceSound(player, level, pos, state);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(forge, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean tryInsert(CharcoalForgeBlockEntity forge, Player player, ItemStack stack)
    {
        if (forge.isItemValid(CharcoalForgeBlockEntity.SLOT_FUEL_MIN, stack))
        {
            return Helpers.insertSlotsFromHand(forge.getInventory(), player, stack, stack.getCount(), CharcoalForgeBlockEntity.SLOT_FUEL_MIN, CharcoalForgeBlockEntity.SLOT_FUEL_MAX + 1);
        }
        if (stack.getItem() instanceof MoldItem)
        {
            final IMold mold = IMold.get(stack);
            return mold != null && mold.getFluidInTank(0).isEmpty() && Helpers.insertSlotsFromHand(forge.getInventory(), player, stack, 1, CharcoalForgeBlockEntity.SLOT_EXTRA_MIN, CharcoalForgeBlockEntity.SLOT_EXTRA_MAX + 1);
        }
        // prevent tools getting sucked in
        if (!stack.isDamageableItem() && HeatingRecipe.getRecipe(stack) != null)
        {
            return Helpers.insertSlotsFromHand(forge.getInventory(), player, stack, 1, CharcoalForgeBlockEntity.SLOT_INPUT_MIN, CharcoalForgeBlockEntity.SLOT_INPUT_MAX + 1);
        }
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return CharcoalPileBlock.SHAPE_BY_LAYER[7];
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (state.getValue(HEAT) > 0)
        {
            if (isValid(level, pos))
            {
                Helpers.fireSpreaderTick(level, pos.above(), rand, 3);
            }
            else
            {
                level.setBlockAndUpdate(pos, defaultBlockState().setValue(HEAT, 0));
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return new ItemStack(Items.CHARCOAL);
    }
}

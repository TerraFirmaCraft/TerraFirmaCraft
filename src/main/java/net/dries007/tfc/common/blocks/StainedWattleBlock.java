/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.IGhostBlockHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.util.Helpers;

public class StainedWattleBlock extends ExtendedBlock implements IGhostBlockHandler
{
    public static final BooleanProperty TOP = TFCBlockStateProperties.TOP;
    public static final BooleanProperty BOTTOM = TFCBlockStateProperties.BOTTOM;
    public static final BooleanProperty LEFT = TFCBlockStateProperties.LEFT;
    public static final BooleanProperty RIGHT = TFCBlockStateProperties.RIGHT;

    private static final double TOP_LIMIT = 0.75D;
    private static final double BOTTOM_LIMIT = 0.25D;
    private static final double MIDDLE = 0.5D;

    @Nullable
    private static BlockState getStateFor(BlockState state, Direction lookDirection, double x, double y, double z)
    {
        if (lookDirection.getAxis() == Direction.Axis.Y) return null;
        final double horizontal = lookDirection.getAxis() == Direction.Axis.Z ? x : z;
        if (y > TOP_LIMIT && !state.getValue(TOP))
        {
            return state.setValue(TOP, true);
        }
        else if (y < BOTTOM_LIMIT && !state.getValue(BOTTOM))
        {
            return state.setValue(BOTTOM, true);
        }
        else if (horizontal < MIDDLE && !state.getValue(LEFT))
        {
            return state.setValue(LEFT, true);
        }
        else if (horizontal > MIDDLE && !state.getValue(RIGHT))
        {
            return state.setValue(RIGHT, true);
        }
        return null;
    }

    @Nullable
    private static BlockState removeStateFor(BlockState state, Direction lookDirection, double x, double y, double z)
    {
        if (lookDirection.getAxis() == Direction.Axis.Y) return null;
        final double horizontal = lookDirection.getAxis() == Direction.Axis.Z ? x : z;
        if (y > TOP_LIMIT && state.getValue(TOP))
        {
            return state.setValue(TOP, false);
        }
        else if (y < BOTTOM_LIMIT && state.getValue(BOTTOM))
        {
            return state.setValue(BOTTOM, false);
        }
        else if (horizontal < MIDDLE && state.getValue(LEFT))
        {
            return state.setValue(LEFT, false);
        }
        else if (horizontal > MIDDLE && state.getValue(RIGHT))
        {
            return state.setValue(RIGHT, false);
        }
        return null;
    }

    @Nullable
    private static BlockState getPossibleDyedState(ItemStack item, BlockState current)
    {
        BlockState found = Arrays.stream(Helpers.DYE_COLORS)
            .filter(color -> Helpers.isItem(item, DyeItem.byColor(color)))
            .map(color -> TFCBlocks.STAINED_WATTLE.get(color).get().defaultBlockState())
            .findFirst().orElse(null);
        return found != null && found.getBlock() != current.getBlock() ? found : null;
    }

    public StainedWattleBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TOP, false).setValue(BOTTOM, false).setValue(LEFT, false).setValue(RIGHT, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (hand == InteractionHand.OFF_HAND)
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack item = player.getItemInHand(hand);
        if (item.isEmpty() && player.isShiftKeyDown())
        {
            return tryTakeStick(state, level, pos, player, item, hitResult);
        }
        else if (Helpers.isItem(item, Tags.Items.RODS_WOODEN))
        {
            return tryAddStick(state, level, pos, player, item, hitResult);
        }
        else
        {
            // can only dye filled unstained wattle blocks
            if (Helpers.isBlock(state, TFCBlocks.WATTLE.get()))
            {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            BlockState dyed = getPossibleDyedState(item, state);
            if (dyed != null)
            {
                if (level.isClientSide)
                {
                    for (int i = 0; i < 5; i++)
                    {
                        Vec3 loc = hitResult.getLocation();
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), loc.x, loc.y, loc.z, Helpers.triangle(level.random) / 3, Helpers.triangle(level.random) / 3, Helpers.triangle(level.random) / 3);
                    }
                }
                Helpers.playSound(level, pos, TFCSounds.WATTLE_DYED.get());
                dyed = dyed.setValue(BOTTOM, state.getValue(BOTTOM)).setValue(TOP, state.getValue(TOP)).setValue(LEFT, state.getValue(LEFT)).setValue(RIGHT, state.getValue(RIGHT));
                return setState(level, pos, dyed, player, item, 1);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState state, Direction direction, BlockPos pos, double x, double y, double z, ItemStack item)
    {
        if (item.isEmpty() && player.isShiftKeyDown())
        {
            return removeStateFor(state, direction, x, y, z);
        }
        else if (Helpers.isItem(item, Tags.Items.RODS_WOODEN))
        {
            return getStateFor(state, direction, x, y, z);
        }
        else
        {
            if (Helpers.isBlock(state, TFCBlocks.WATTLE.get()))
            {
                return null;
            }
            BlockState dyed = getPossibleDyedState(item, state);
            if (dyed != null)
            {
                return dyed.setValue(BOTTOM, state.getValue(BOTTOM)).setValue(TOP, state.getValue(TOP)).setValue(LEFT, state.getValue(LEFT)).setValue(RIGHT, state.getValue(RIGHT));
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(TOP, BOTTOM, LEFT, RIGHT));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return canSurvive(state, level, currentPos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return !level.getBlockState(pos.below()).canBeReplaced();
    }

    protected ItemInteractionResult tryAddStick(BlockState state, Level level, BlockPos pos, Player player, ItemStack item, BlockHitResult hit)
    {
        final Vec3 location = hit.getLocation();
        BlockState placeState = getStateFor(state, hit.getDirection(), location.x - pos.getX(), location.y - pos.getY(), location.z - pos.getZ());
        if (placeState != null)
        {
            Helpers.playSound(level, pos, TFCSounds.WATTLE_WOVEN.get());
            return setState(level, pos, placeState, player, item, 1);
        }
        return ItemInteractionResult.FAIL; // avoid triggering the stick placing behavior
    }

    protected ItemInteractionResult tryTakeStick(BlockState state, Level level, BlockPos pos, Player player, ItemStack item, BlockHitResult hit)
    {
        final Vec3 location = hit.getLocation();
        BlockState placeState = removeStateFor(state, hit.getDirection(), location.x - pos.getX(), location.y - pos.getY(), location.z - pos.getZ());
        if (placeState != null)
        {
            ItemHandlerHelper.giveItemToPlayer(player, Items.STICK.getDefaultInstance());
            Helpers.playSound(level, pos, TFCSounds.WATTLE_WOVEN.get());
            return setState(level, pos, placeState, player, item, 0);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected ItemInteractionResult setState(Level level, BlockPos pos, BlockState state, Player player, ItemStack item, int toShrink)
    {
        if (!player.isCreative()) item.shrink(toShrink);
        level.setBlockAndUpdate(pos, state);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

}

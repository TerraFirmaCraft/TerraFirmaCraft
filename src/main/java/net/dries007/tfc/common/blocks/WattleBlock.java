/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.client.IGhostBlockHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class WattleBlock extends StainedWattleBlock implements IGhostBlockHandler
{
    public static final BooleanProperty WOVEN = TFCBlockStateProperties.WATTLE_WOVEN;

    public WattleBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TOP, false).setValue(BOTTOM, false).setValue(LEFT, false).setValue(RIGHT, false).setValue(WOVEN, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        final ItemStack item = player.getItemInHand(hand);
        final boolean woven = state.getValue(WOVEN);
        if (!woven && Helpers.isItem(item, Tags.Items.RODS_WOODEN) && item.getCount() >= 4)
        {
            Helpers.playSound(level, pos, TFCSounds.WATTLE_WOVEN.get());
            return setState(level, pos, state.setValue(WOVEN, true), player, item, 4); // add sticks
        }
        else if (woven && Helpers.isItem(item, TFCItems.DAUB.get()))
        {
            Helpers.playSound(level, pos, TFCSounds.WATTLE_DAUBED.get());
            return setState(level, pos, TFCBlocks.UNSTAINED_WATTLE.get().withPropertiesOf(state), player, item, 1); // add daub
        }
        return super.use(state, level, pos, player, hand, hit); // other behavior
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState lookState, Direction direction, BlockPos lookPos, double x, double y, double z, ItemStack item)
    {
        boolean woven = lookState.getValue(WOVEN);
        if (!woven && Helpers.isItem(item, Tags.Items.RODS_WOODEN) && item.getCount() >= 4)
        {
            return lookState.setValue(WOVEN, true);
        }
        else if (woven && Helpers.isItem(item, TFCItems.DAUB.get()))
        {
            return TFCBlocks.UNSTAINED_WATTLE.get().withPropertiesOf(lookState);
        }
        return super.getStateToDraw(level, player, lookState, direction, lookPos, x, y, z, item);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(WOVEN));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(WOVEN) ? super.getCollisionShape(state, level, pos, context) : Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(WOVEN) ? super.getOcclusionShape(state, level, pos) : Shapes.empty();
    }
}

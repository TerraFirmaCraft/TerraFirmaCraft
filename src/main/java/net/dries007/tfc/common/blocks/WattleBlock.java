/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Locale;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.client.IGhostBlockHandler;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class WattleBlock extends StainedWattleBlock implements IGhostBlockHandler
{
    public static final EnumProperty<Type> TYPE = TFCBlockStateProperties.WATTLE_TYPE;

    public WattleBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TOP, false).setValue(BOTTOM, false).setValue(LEFT, false).setValue(RIGHT, false).setValue(TYPE, Type.EMPTY));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        final ItemStack item = player.getItemInHand(hand);
        final Type type = state.getValue(TYPE);
        if (type == Type.EMPTY && Helpers.isItem(item, Tags.Items.RODS_WOODEN) && item.getCount() >= 4)
        {
            return setState(level, pos, state.setValue(TYPE, Type.WOVEN), player, item, 4); // add sticks
        }
        else if (type == Type.WOVEN && Helpers.isItem(item, TFCItems.DAUB.get()))
        {
            return setState(level, pos, state.setValue(TYPE, Type.FILLED), player, item, 1); // add daub
        }
        return super.use(state, level, pos, player, hand, hit); // other behavior
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState lookState, Direction direction, BlockPos lookPos, double x, double y, double z, ItemStack item)
    {
        Type type = lookState.getValue(TYPE);
        if (type == Type.EMPTY && Helpers.isItem(item, Tags.Items.RODS_WOODEN) && item.getCount() >= 4)
        {
            return lookState.setValue(TYPE, Type.WOVEN);
        }
        else if (type == Type.WOVEN && Helpers.isItem(item, TFCItems.DAUB.get()))
        {
            return lookState.setValue(TYPE, Type.FILLED);
        }
        return super.getStateToDraw(level, player, lookState, direction, lookPos, x, y, z, item);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(TYPE));
    }

    public enum Type implements StringRepresentable
    {
        EMPTY,
        WOVEN,
        FILLED;

        private final String serializedName;

        Type()
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}

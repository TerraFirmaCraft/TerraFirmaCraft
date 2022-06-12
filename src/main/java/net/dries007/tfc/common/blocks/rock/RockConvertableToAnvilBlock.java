/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

public class RockConvertableToAnvilBlock extends Block
{
    public static Block createForIgneousOnly(Properties properties, Rock rock)
    {
        return rock.getCategory() == RockCategory.IGNEOUS_EXTRUSIVE || rock.getCategory() == RockCategory.IGNEOUS_INTRUSIVE ? new RockConvertableToAnvilBlock(properties, TFCBlocks.ROCK_ANVILS.get(rock)) : new Block(properties);
    }

    private final Supplier<? extends Block> anvil;

    public RockConvertableToAnvilBlock(Properties properties, Supplier<? extends Block> anvil)
    {
        super(properties);
        this.anvil = anvil;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (Helpers.isItem(stack, TFCTags.Items.HAMMERS) && !Helpers.isItem(player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND), TFCTags.Items.CHISELS) && hit.getDirection() == Direction.UP && level.getBlockState(pos.above()).isAir())
        {
            level.setBlockAndUpdate(pos, anvil.get().defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

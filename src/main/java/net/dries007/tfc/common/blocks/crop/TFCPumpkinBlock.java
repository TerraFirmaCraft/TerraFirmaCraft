/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCPumpkinBlock extends DecayingBlock
{
    public TFCPumpkinBlock(ExtendedProperties properties, Supplier<? extends Block> rotted)
    {
        super(properties, rotted);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying && (Helpers.isItem(held, TFCTags.Items.TOOLS_KNIVES) || Helpers.isItem(held, Tags.Items.TOOLS_SHEAR)) && TFCConfig.SERVER.enablePumpkinCarving.get())
        {
            if (!level.isClientSide && !decaying.isRotten())
            {
                Direction hitDir = hitResult.getDirection();
                Direction facing = hitDir.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : hitDir;
                level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                decaying.setStack(ItemStack.EMPTY);
                level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing), 11);

                Helpers.damageItem(held, player, hand);

                level.gameEvent(player, GameEvent.SHEAR, pos);
                player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

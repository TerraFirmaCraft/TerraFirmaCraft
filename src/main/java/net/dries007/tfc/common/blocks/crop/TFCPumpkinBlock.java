package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.RottingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCPumpkinBlock extends RottingBlock
{
    public TFCPumpkinBlock(ExtendedProperties properties, Supplier<? extends Block> rotted)
    {
        super(properties, rotted);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack held = player.getItemInHand(hand);
        if (Helpers.isItem(held, TFCTags.Items.KNIVES) && TFCConfig.SERVER.enablePumpkinCarving.get())
        {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof RottingBlockEntity rotting && !rotting.isRotten())
            {
                Direction hitDir = hit.getDirection();
                Direction facing = hitDir.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : hitDir;
                level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing), 11);

                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

                level.gameEvent(player, GameEvent.SHEAR, pos);
                player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}

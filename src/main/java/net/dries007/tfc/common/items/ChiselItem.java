/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.items.ItemHandlerHelper;

import com.mojang.datafixers.util.Either;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class ChiselItem extends ToolItem
{
    public ChiselItem(Tier tier, float attackDamage, float attackSpeed, Properties properties)
    {
        super(tier, attackDamage, attackSpeed, TFCTags.Blocks.MINEABLE_WITH_CHISEL, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Player player = context.getPlayer();
        if (player != null)
        {
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            final BlockState state = level.getBlockState(pos);
            final Either<BlockState, InteractionResult> result = ChiselRecipe.computeResult(player, state, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, context.isInside()), true);
            return result.map(resultState -> {
                player.playSound(resultState.getSoundType().getHitSound(), 1f, 1f);

                ItemStack held = player.getMainHandItem();
                if (!level.isClientSide)
                {
                    if (TFCConfig.SERVER.enableChiselsStartCollapses.get())
                    {
                        if (Helpers.isBlock(state, TFCTags.Blocks.CAN_TRIGGER_COLLAPSE) && CollapseRecipe.tryTriggerCollapse(level, pos))
                        {
                            return InteractionResult.SUCCESS; // Abort chiseling
                        }
                    }

                    player.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(cap -> {
                        final ChiselRecipe recipeUsed = ChiselRecipe.getRecipe(state, held, cap.getChiselMode());
                        if (recipeUsed != null)
                        {
                            ItemStack extraDrop = recipeUsed.getExtraDrop(held);
                            if (!extraDrop.isEmpty())
                            {
                                ItemHandlerHelper.giveItemToPlayer(player, extraDrop);
                            }
                        }
                    });
                }

                level.setBlockAndUpdate(pos, resultState);
                if (player instanceof ServerPlayer serverPlayer)
                {
                    TFCAdvancements.CHISELED.trigger(serverPlayer, resultState);
                }

                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }, Function.identity()); // returns the interaction result if we are given one
        }
        return InteractionResult.PASS;
    }
}
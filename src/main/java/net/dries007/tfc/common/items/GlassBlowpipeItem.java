/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.GlassBasinBlockEntity;
import net.dries007.tfc.common.blockentities.HotPouredGlassBlockEntity;
import net.dries007.tfc.common.blocks.GlassBasinBlock;
import net.dries007.tfc.common.blocks.HotPouredGlassBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.GlassWorking;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class GlassBlowpipeItem extends BlowpipeItem
{
    private static ItemStack getOtherHandItem(Player player)
    {
        return player.getItemInHand(player.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    private static ItemStack getOtherHandItem(Player player, InteractionHand hand)
    {
        return player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    private final float breakChance;

    public GlassBlowpipeItem(Properties properties,float breakChance)
    {
        super(properties);
        this.breakChance = breakChance;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        final Direction face = context.getClickedFace();
        final Player player = context.getPlayer();

        final BlockPos center = pos.relative(face);
        if (GlassBasinBlock.isValid(level, center) && player != null)
        {
            final ItemStack item = context.getItemInHand();

            // test on a copy so that if it doesn't work we don't cause irreversible changes
            final ItemStack copy = item.copy();
            GlassWorking.apply(copy, GlassOperation.BASIN_POUR);

            final @Nullable GlassworkingRecipe recipe = GlassworkingRecipe.get(level, copy);
            if (recipe != null)
            {
                if (!GlassOperation.BASIN_POUR.hasRequiredTemperature(copy))
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.glass.not_hot_enough"), true);
                }
                else
                {
                    consumeBlowpipe(player, context.getHand(), item);
                    level.setBlockAndUpdate(center, TFCBlocks.GLASS_BASIN.get().defaultBlockState());
                    if (level.getBlockEntity(center) instanceof GlassBasinBlockEntity glass)
                    {
                        glass.setGlassItem(recipe.getResultItem(level.registryAccess()));
                    }
                    if (player instanceof ServerPlayer server)
                    {
                        TFCAdvancements.BASIN_POUR.trigger(server);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }

        if (face == Direction.UP && Helpers.isBlock(state, TFCTags.Blocks.GLASS_POURING_TABLE) && level.getBlockState(pos.above()).isAir() && player != null)
        {
            final ItemStack item = context.getItemInHand();

            // test on a copy so that if it doesn't work we don't cause irreversible changes
            final ItemStack copy = item.copy();
            GlassWorking.apply(copy, GlassOperation.TABLE_POUR);

            final @Nullable GlassworkingRecipe recipe = GlassworkingRecipe.get(level, copy);
            if (recipe != null)
            {
                if (!GlassOperation.TABLE_POUR.hasRequiredTemperature(copy))
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.glass.not_hot_enough"), true);
                }
                else
                {
                    consumeBlowpipe(player, context.getHand(), item);
                    level.setBlockAndUpdate(pos.above(), TFCBlocks.HOT_POURED_GLASS.get().defaultBlockState().setValue(HotPouredGlassBlock.FLAT, false));
                    if (level.getBlockEntity(pos.above()) instanceof HotPouredGlassBlockEntity glass)
                    {
                        glass.setGlassItem(recipe.getResultItem(level.registryAccess()));
                    }
                    if (player instanceof ServerPlayer server)
                    {
                        TFCAdvancements.TABLE_POUR.trigger(server);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    protected boolean consumeBlowpipe(Player player, InteractionHand hand, ItemStack item)
    {
        final boolean broken = player.getRandom().nextFloat() < breakChance;
        player.setItemInHand(hand, broken ? ItemStack.EMPTY : BlowpipeItem.transform(item.getItem()).getDefaultInstance());
        return broken;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        final ItemStack otherItem = getOtherHandItem(player, hand);
        final GlassOperation op = GlassOperation.get(otherItem, player);
        if (op != null)
        {
            if (!op.hasRequiredTemperature(held))
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.glass.not_hot_enough"), true);
                return InteractionResultHolder.fail(held);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
        return InteractionResultHolder.pass(held);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksLeft)
    {
        if (ticksLeft % 30 == 0 && entity instanceof Player player)
        {
            final GlassOperation op = GlassOperation.get(getOtherHandItem(player), player);
            if (op != null)
            {
                level.playSound(null, entity.blockPosition(), op.getSound(), SoundSource.PLAYERS, 1f, 0.8f + (float) (player.getLookAngle().y / 2f));
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (entity instanceof Player player)
        {
            stopUsing(player, stack);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    protected void stopUsing(LivingEntity entity, ItemStack stack)
    {
        if (entity instanceof Player player)
        {
            final ItemStack otherHand = getOtherHandItem(player);
            final GlassOperation op = GlassOperation.get(otherHand, player);
            if (op != null && stack.getItem() instanceof GlassBlowpipeItem)
            {
                GlassWorking.apply(stack, op);

                final Level level = entity.level();
                final @Nullable GlassworkingRecipe recipe = GlassworkingRecipe.get(level, stack);
                if (recipe != null)
                {
                    final boolean broken = consumeBlowpipe(player, player.getUsedItemHand(), stack);
                    ItemHandlerHelper.giveItemToPlayer(player, recipe.getResultItem(level.registryAccess()));
                    level.playSound(null, player.blockPosition(), broken ? SoundEvents.ITEM_BREAK : SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);
                }
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

            // Cooldown all blowpipe items
            player.getCooldowns().addCooldown(TFCItems.BLOWPIPE.asItem(), 80);
            player.getCooldowns().addCooldown(TFCItems.CERAMIC_BLOWPIPE.asItem(), 80);
        }
    }
}

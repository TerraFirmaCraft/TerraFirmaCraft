/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

/**
 * Doesn't extend {@link HoeItem} because we don't want our hoes to be mining tools - we have knives/scythes for that. In the interest
 * of mod compatibility, we repurpose {@link net.minecraft.tags.BlockTags#MINEABLE_WITH_HOE} as "mineable with sharp tool", and exclude
 * our hoes from it.
 */
public class TFCHoeItem extends DiggerItem
{
    public TFCHoeItem(Tier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_HOE, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity)
    {
        if (!level.isClientSide && ToolItem.willConsumeDurability(level, pos, state)) // use TFC check
        {
            Helpers.damageItem(stack, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        // `HoeItem.useOn()` doesn't invoke `this`, so this is safe, rather than duplicating the method
        return Items.DIAMOND_HOE.useOn(context);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility)
    {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
    }
}

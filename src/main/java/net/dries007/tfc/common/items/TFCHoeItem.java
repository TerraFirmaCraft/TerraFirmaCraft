/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class TFCHoeItem extends HoeItem
{
    public TFCHoeItem(Tier tier, int damage, float speed, Properties properties)
    {
        super(tier, damage, speed, properties);
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
}

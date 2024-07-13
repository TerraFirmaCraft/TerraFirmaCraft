/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class ToolItem extends DiggerItem
{
    /**
     * The vanilla constructor sets the attack damage to {@code attackDamage + tier.getAttackDamageBonus()}.
     * Whereas, we want it to be equal to {@code attackDamage * tier.getAttackDamageBonus()}.
     * So, we pass upwards a reverse-engineered constant, in order to get the value we want at the end
     */
    public static float calculateVanillaAttackDamage(float attackDamage, Tier tier)
    {
        return (attackDamage - 1) * tier.getAttackDamageBonus();
    }

    /**
     * Mining plants should consume some durability
     */
    public static boolean willConsumeDurability(Level level, BlockPos pos, BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.PLANTS) || state.getDestroySpeed(level, pos) != 0.0F;
    }

    public ToolItem(Tier tier, float attackDamage, float attackSpeed, TagKey<Block> mineableBlocks, Properties properties)
    {
        super(attackDamage, attackSpeed, tier, mineableBlocks, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity)
    {
        if (willConsumeDurability(level, pos, state))
        {
            Helpers.damageItem(stack, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }
}

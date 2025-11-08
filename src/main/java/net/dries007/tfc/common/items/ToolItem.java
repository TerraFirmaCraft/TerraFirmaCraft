/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
     *
     * @see SwordItem#createAttributes
     */
    public static ItemAttributeModifiers productAttributes(Tier tier, float attackDamageFactor, float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    BASE_ATTACK_DAMAGE_ID, attackDamageFactor * tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    /**
     * Mining plants should consume some durability, mining snow should not
     */
    public static boolean isDurabilityModified(Level level, BlockPos pos, BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.CONSUMES_TOOL_DURABILITY) || Helpers.isBlock(state.getBlock(), TFCTags.Blocks.DOES_NOT_CONSUME_TOOL_DURABILITY);
    }

    public static boolean modifiedDurability(Level level, BlockPos pos, BlockState state)
    {
        if (Helpers.isBlock(state.getBlock(), TFCTags.Blocks.CONSUMES_TOOL_DURABILITY))
        {
            return true;
        }
        if (Helpers.isBlock(state.getBlock(), TFCTags.Blocks.DOES_NOT_CONSUME_TOOL_DURABILITY))
        {
            return false;
        }
        return state.getDestroySpeed(level, pos) != 0.0F;
    }

    public ToolItem(Tier tier, TagKey<Block> mineableBlocks, Properties properties)
    {
        super(tier, mineableBlocks, properties);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import java.util.Collections;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;

/**
 * Generic class for tools that shouldn't override vanilla's {@link ToolItem}
 * Main issue here is that TFC damage is not additive like vanilla but instead multiplicative
 *
 * For comparison:
 * Vanilla: Tool value (ie: 3.0 for swords) + material damage value (ie: 2.0 for iron) + 1.0 (hand) = 6.0
 * TFC: Tool value (ie: 1.3 for maces) * material damage value (ie: 5.75 for steel) + 1.0 (hand) ~= 7.5
 */
public class TFCToolItem extends ToolItem
{
    protected final float attackDamage;
    protected final float attackSpeed;
    protected final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public TFCToolItem(IItemTier tier, float attackDamageMultiplier, float attackSpeed, Properties builder)
    {
        super(0, attackSpeed, tier, Collections.emptySet(), builder);
        this.attackDamage = attackDamageMultiplier * tier.getAttackDamageBonus();
        this.attackSpeed = attackSpeed;
        this.attributeModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
            .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION))
            .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION))
            .build();
    }

    public float getAttackDamage()
    {
        return attackDamage;
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    {
        return slot == EquipmentSlotType.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean mineBlock(ItemStack stack, World level, BlockState state, BlockPos pos, LivingEntity entity)
    {
        // tfc: mining plants should consume durability
        if (!level.isClientSide && (state.getBlock().is(TFCTags.Blocks.PLANT) || state.getDestroySpeed(level, pos) != 0.0F))
        {
            stack.hurtAndBreak(1, entity, p -> p.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }
        return true;
    }
}
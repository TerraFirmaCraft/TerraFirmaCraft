/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.tools;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Generic class for weapons that shouldn't override vanilla's {@link SwordItem}
 * Possible reasons to avoid extending it: Sweeping effect, enchantments, other mods would think it is a sword.
 *
 * Also, TFC material damage is multiplicative instead of additive.
 * For comparison:
 * Vanilla: Tool value (ie: 3.0 for swords) + material damage value (ie: 2.0 for iron) + 1.0 (hand) = 6.0
 * TFC: Tool value (ie: 1.3 for maces) * material damage value (ie: 5.75 for steel) + 1.0 (hand) ~= 7.5
 */
public class WeaponItem extends TieredItem
{
    private final float attackDamage;
    private final float attackSpeed;

    public WeaponItem(IItemTier tier, float attackDamageMultiplier, float attackSpeed, Item.Properties builder)
    {
        super(tier, builder);
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamageMultiplier * tier.getAttackDamage();
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    public float getAttackDamage()
    {
        return attackDamage;
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
        if (state.getBlockHardness(worldIn, pos) != 0.0F)
        {
            stack.damageItem(2, entityLiving, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}
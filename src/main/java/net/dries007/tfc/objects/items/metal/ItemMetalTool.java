/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMetalTool extends ItemMetal
{
    public final ToolMaterial material;
    private final float efficiency;
    private final double attackDamage;
    private final int areaOfAttack; // todo: implement
    private final float attackSpeed;

    public ItemMetalTool(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        if (metal.toolMetal == null) throw new IllegalArgumentException("You can't make tools out of non tool metals.");
        material = metal.toolMetal;
        float typeDamage;
        switch (type)
        {
            case PICK:
                setHarvestLevel("pickaxe", metal.toolMetal.getHarvestLevel());
                typeDamage = 1.2f; // todo: use some central spot for this (config maybe?) and make the rock equivalents use the same numbers.
                areaOfAttack = 1;
                attackSpeed = -2.8f;
                break;
            case SHOVEL:
                setHarvestLevel("shovel", metal.toolMetal.getHarvestLevel());
                typeDamage = 1.3f;
                areaOfAttack = 1;
                attackSpeed = -3f;
                break;
            case AXE:
                setHarvestLevel("axe", metal.toolMetal.getHarvestLevel());
                typeDamage = 1.5f;
                areaOfAttack = 1;
                attackSpeed = -3f;
                break;
            case HOE:
                setHarvestLevel("hoe", metal.toolMetal.getHarvestLevel());
                typeDamage = 0.7f;
                areaOfAttack = 1;
                attackSpeed = -3;
                break;
            case CHISEL:
                setHarvestLevel("chisel", metal.toolMetal.getHarvestLevel());
                typeDamage = 0.7f;
                areaOfAttack = 1;
                attackSpeed = 0;
                break;
            case SAW:
                setHarvestLevel("saw", metal.toolMetal.getHarvestLevel());
                typeDamage = 0.5f;
                areaOfAttack = 1;
                attackSpeed = -1;
                break;
            case PROPICK:
                setHarvestLevel("pickaxe", metal.toolMetal.getHarvestLevel());
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -3.5f;
                break;
            case SCYTHE:
                setHarvestLevel("scythe", metal.toolMetal.getHarvestLevel());
                typeDamage = 1.5f;
                areaOfAttack = 3;
                attackSpeed = -3.5f;
                break;
            case KNIFE:
                setHarvestLevel("knife", metal.toolMetal.getHarvestLevel());
                typeDamage = 0.5f;
                areaOfAttack = 1;
                attackSpeed = 3f;
                break;
            case HAMMER:
                setHarvestLevel("hammer", metal.toolMetal.getHarvestLevel());
                typeDamage = 2f;
                areaOfAttack = 1;
                attackSpeed = -3.5f;
                break;
            case SWORD:
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -0.75f;
                break;
            case MACE:
                typeDamage = 1.1f;
                areaOfAttack = 1;
                attackSpeed = -1;
                break;
            case JAVELIN:
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -1;
                break;
            default:
                throw new IllegalArgumentException("Tool from non tool type.");
        }

        setMaxStackSize(1);
        setMaxDamage(material.getMaxUses());
        efficiency = material.getEfficiency();
        attackDamage = typeDamage * material.getAttackDamage();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        Material material = state.getMaterial();
        switch (type)
        {
            case AXE:
                if (material == Material.WOOD || material == Material.PLANTS || material == Material.VINE)
                    return efficiency;
                break;
            case PICK:
                if (material == Material.IRON || material == Material.ANVIL || material == Material.ROCK)
                    return efficiency;
                break;
            case SHOVEL:
                if (material == Material.SNOW || material == Material.CRAFTED_SNOW) return efficiency;
            case SCYTHE:
                if (material == Material.PLANTS || material == Material.VINE || material == Material.LEAVES)
                    return efficiency;
        }
        for (String type : getToolClasses(stack))
        {
            if (state.getBlock().isToolEffective(type, state)) return efficiency;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        switch (type)
        {
            case PROPICK:
            case SAW:
                stack.damageItem(4, attacker);
                break;
            case HOE:
            case CHISEL:
                stack.damageItem(3, attacker);
                break;
            case PICK:
            case SHOVEL:
            case AXE:
            case SCYTHE:
                stack.damageItem(2, attacker);
                break;
            case SWORD:
            case MACE:
            case JAVELIN:
            case HAMMER:
            case KNIFE:
                stack.damageItem(1, attacker);
                break;
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote && (double) state.getBlockHardness(worldIn, pos) != 0.0D)
        {
            stack.damageItem(1, entityLiving);
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        return super.canHarvestBlock(blockIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return material.getEnchantability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", attackSpeed, 0));
        }
        return multimap;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }
}

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.TFCSoundEvents;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMetalTool extends ItemMetal
{
    public final ToolMaterial material;
    private final double attackDamage;
    private final int areaOfAttack; // todo: implement
    private final float attackSpeed;
    private float efficiency;

    public ItemMetalTool(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        if (metal.getToolMetal() == null)
            throw new IllegalArgumentException("You can't make tools out of non tool metals.");
        material = metal.getToolMetal();
        int harvestLevel = material.getHarvestLevel();

        setMaxStackSize(1);
        setMaxDamage(material.getMaxUses());
        efficiency = material.getEfficiency();

        float typeDamage;
        switch (type)
        {
            case PICK:
                setHarvestLevel("pickaxe", harvestLevel);
                typeDamage = 1.2f; // todo: use some central spot for this (config maybe?) and make the rock equivalents use the same numbers.
                areaOfAttack = 1;
                attackSpeed = -2.8f;
                break;
            case SHOVEL:
                setHarvestLevel("shovel", harvestLevel);
                typeDamage = 1.3f;
                areaOfAttack = 1;
                attackSpeed = -3f;
                break;
            case AXE:
                setHarvestLevel("axe", harvestLevel);
                typeDamage = 1.5f;
                areaOfAttack = 1;
                attackSpeed = -3f;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case HOE:
                setHarvestLevel("hoe", harvestLevel);
                typeDamage = 0.7f;
                areaOfAttack = 1;
                attackSpeed = -3;
                break;
            case CHISEL:
                setHarvestLevel("chisel", harvestLevel);
                typeDamage = 0.7f;
                areaOfAttack = 1;
                attackSpeed = 0;
                break;
            case SAW:
                setHarvestLevel("saw", harvestLevel);
                typeDamage = 0.5f;
                areaOfAttack = 1;
                attackSpeed = -1;
                break;
            case PROPICK:
                setHarvestLevel("pickaxe", harvestLevel);
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -3.5f;
                setMaxDamage(material.getMaxUses() / 3);
                efficiency = material.getEfficiency() * 0.5F;
                break;
            case SCYTHE:
                setHarvestLevel("scythe", harvestLevel);
                typeDamage = 1.5f;
                areaOfAttack = 3;
                attackSpeed = -3.5f;
                break;
            case KNIFE:
                setHarvestLevel("knife", harvestLevel);
                typeDamage = 0.5f;
                areaOfAttack = 1;
                attackSpeed = 3f;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case HAMMER:
                setHarvestLevel("hammer", harvestLevel);
                typeDamage = 2f;
                areaOfAttack = 1;
                attackSpeed = -3.5f;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case SWORD:
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -0.75f;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case MACE:
                typeDamage = 1.1f;
                areaOfAttack = 1;
                attackSpeed = -1;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case JAVELIN:
                typeDamage = 1f;
                areaOfAttack = 1;
                attackSpeed = -1;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            default:
                throw new IllegalArgumentException("Tool from non tool type.");
        }

        attackDamage = typeDamage * material.getAttackDamage();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        return canHarvestBlock(state, stack) ? efficiency : 1.0f;
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
        // Knives and scythes always take damage, as they break blocks like grass for extra drops n stuff
        if (state.getBlockHardness(worldIn, pos) > 0 || type == Metal.ItemType.KNIFE || type == Metal.ItemType.SCYTHE)
        {
            if (!worldIn.isRemote)
            {
                stack.damageItem(1, entityLiving);
            }
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state)
    {
        Material material = state.getMaterial();
        switch (type)
        {
            case AXE:
                return material == Material.WOOD || material == Material.PLANTS || material == Material.VINE;
            case PICK:
                return material == Material.IRON || material == Material.ANVIL || material == Material.ROCK;
            case SHOVEL:
                return material == Material.SNOW || material == Material.CRAFTED_SNOW;
            case SCYTHE:
                return material == Material.PLANTS || material == Material.VINE || material == Material.LEAVES;
        }
        return false;
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
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {
        for (String type : getToolClasses(stack))
        {
            if (state.getBlock().isToolEffective(type, state))
            {
                return true;
            }
        }
        return canHarvestBlock(state);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        // Hammers need to activate anvils for welding
        return this.type == Metal.ItemType.HAMMER || super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (type == Metal.ItemType.JAVELIN)
        {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return type == Metal.ItemType.JAVELIN ? 72000 : 0;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof EntityPlayer && type == Metal.ItemType.JAVELIN)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            if (charge > 5)
            {
                float f = ItemBow.getArrowVelocity(charge); //Same charge time as bow

                if (!worldIn.isRemote)
                {
                    EntityThrownJavelin javelin = new EntityThrownJavelin(worldIn, player);
                    javelin.setDamage(attackDamage);
                    javelin.setWeapon(stack);
                    javelin.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 1.5F, 0.5F);
                    worldIn.spawnEntity(javelin);
                    worldIn.playSound(null, player.posX, player.posY, player.posZ, TFCSoundEvents.ITEM_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (Constants.RNG.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                }
                player.inventory.deleteStack(stack);
                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return canHarvestBlock(world.getBlockState(pos));
    }
}

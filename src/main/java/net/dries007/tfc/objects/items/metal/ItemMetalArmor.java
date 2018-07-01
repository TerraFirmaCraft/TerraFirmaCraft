/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;

import static net.minecraft.entity.SharedMonsterAttributes.ARMOR;
import static net.minecraft.entity.SharedMonsterAttributes.ARMOR_TOUGHNESS;
import static net.minecraft.item.ItemArmor.DISPENSER_BEHAVIOR;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemMetalArmor extends ItemMetal implements ISpecialArmor
{
    //todo: render items

    public final EntityEquipmentSlot slot;
    public final double damageReduceAmount = 5; //todo: actual numbers
    public final double toughness = 1; //todo: actual numbers
    private final ToolMaterial toolMaterial;
    private final UUID uuid;

    public ItemMetalArmor(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        toolMaterial = metal.toolMetal;
        switch (type)
        {
            case HELMET:
                slot = EntityEquipmentSlot.HEAD;
                uuid = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
                setMaxDamage(2500);
                break;
            case CHESTPLATE:
                slot = EntityEquipmentSlot.CHEST;
                uuid = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
                setMaxDamage(3750);
                break;
            case GREAVES:
                slot = EntityEquipmentSlot.LEGS;
                uuid = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");
                setMaxDamage(3000);
                break;
            case BOOTS:
                slot = EntityEquipmentSlot.FEET;
                uuid = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
                setMaxDamage(2500);
                break;
            default:
                throw new IllegalArgumentException("You cannot make armor out of non armor item types.");
        }
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (playerIn.getItemStackFromSlot(slot).isEmpty())
        {
            playerIn.setItemStackToSlot(slot, heldItem.copy());
            heldItem.shrink(1);
            return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
        }
        else
        {
            return new ActionResult<>(EnumActionResult.FAIL, heldItem);
        }
    }

    @Override
    public int getItemEnchantability()
    {
        return toolMaterial.getEnchantability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == this.slot)
        {
            multimap.put(ARMOR.getName(), new AttributeModifier(uuid, "Armor modifier", damageReduceAmount, 0));
            multimap.put(ARMOR_TOUGHNESS.getName(), new AttributeModifier(uuid, "Armor toughness", toughness, 0));
        }
        return multimap;
    }

    @Nullable
    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return slot;
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return super.getArmorTexture(stack, entity, slot, type); // todo
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
    {
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default); // todo
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot)
    {
        return new ArmorProperties(1, 0.5, 10); // todo: vary on damage source //todo: remove junk data
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot)
    {
        return 7; //todo
    }

    @Override
    public void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot)
    {
        //todo
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.forge.ForgeableHeatableHandler;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockTrapDoorMetalTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.objects.items.itemblock.ItemBlockMetalLamp;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class ItemMetal extends ItemTFC implements IMetalItem
{
    private static final Map<Metal, EnumMap<Metal.ItemType, ItemMetal>> TABLE = new HashMap<>();

    public static Item get(Metal metal, Metal.ItemType type)
    {
        if (type == Metal.ItemType.SWORD)
        {
            // Make sure to not crash (in 1.15+, don't forget to rewrite all metal items to extend the proper vanilla classes)
            return ItemMetalSword.get(metal);
        }
        if (type == Metal.ItemType.LAMP)
        {
            return ItemBlockMetalLamp.get(metal);
        }
        if (type == Metal.ItemType.TRAPDOOR)
        {
            return ItemBlock.getItemFromBlock(BlockTrapDoorMetalTFC.get(metal));
        }
        return TABLE.get(metal).get(type);
    }

    protected final Metal metal;
    protected final Metal.ItemType type;

    @SuppressWarnings("ConstantConditions")
    public ItemMetal(Metal metal, Metal.ItemType type)
    {
        this.metal = metal;
        this.type = type;

        if (!TABLE.containsKey(metal))
            TABLE.put(metal, new EnumMap<>(Metal.ItemType.class));
        TABLE.get(metal).put(type, this);

        setNoRepair();
        if (type == Metal.ItemType.DOUBLE_INGOT)
        {
            OreDictionaryHelper.register(this, "ingot", "double", metal.getRegistryName().getPath());
            if (metal == Metal.BRONZE || metal == Metal.BISMUTH_BRONZE || metal == Metal.BLACK_BRONZE)
            {
                OreDictionaryHelper.register(this, "ingot", "double", "Any", "Bronze");
            }
            if (metal == Metal.WROUGHT_IRON && ConfigTFC.General.MISC.dictionaryIron)
            {
                OreDictionaryHelper.register(this, "ingot", "double", "Iron");
            }
        }
        else if (type == Metal.ItemType.DOUBLE_SHEET)
        {
            OreDictionaryHelper.register(this, "sheet", "double", metal.getRegistryName().getPath());
            if (metal == Metal.BRONZE || metal == Metal.BISMUTH_BRONZE || metal == Metal.BLACK_BRONZE)
            {
                OreDictionaryHelper.register(this, "sheet", "double", "Any", "Bronze");
            }
            if (metal == Metal.WROUGHT_IRON && ConfigTFC.General.MISC.dictionaryIron)
            {
                OreDictionaryHelper.register(this, "sheet", "double", "Iron");
            }
        }
        else if (type.isToolItem())
        {
            OreDictionaryHelper.register(this, type);
        }
        else
        {
            OreDictionaryHelper.register(this, type, metal.getRegistryName().getPath());
            if (metal == Metal.BRONZE || metal == Metal.BISMUTH_BRONZE || metal == Metal.BLACK_BRONZE)
            {
                OreDictionaryHelper.register(this, type, "Any", "Bronze");
            }
            if (type == Metal.ItemType.SHEET && ConfigTFC.General.MISC.dictionaryPlates)
            {
                OreDictionaryHelper.register(this, "plate", metal);
            }
            if (metal == Metal.WROUGHT_IRON && ConfigTFC.General.MISC.dictionaryIron)
            {
                OreDictionaryHelper.register(this, type, "Iron");
                if (type == Metal.ItemType.SHEET && ConfigTFC.General.MISC.dictionaryPlates) //Register plate for iron too
                {
                    OreDictionaryHelper.register(this, "plate", "Iron");
                }
            }

        }

        if (type == Metal.ItemType.TUYERE)
        {
            setMaxDamage(metal.getToolMetal() != null ? (int) (metal.getToolMetal().getMaxUses() * 0.2) : 100);
        }
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        if (!isDamageable() || !stack.isItemDamaged()) return type.getSmeltAmount();
        double d = (stack.getMaxDamage() - stack.getItemDamage()) / (double) stack.getMaxDamage() - .10;
        return d < 0 ? 0 : MathHelper.floor(type.getSmeltAmount() * d);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        switch (type)
        {
            case NUGGET:
            case DUST:
            case SCRAP:
                return Size.SMALL; // Fits in Small Vessels
            case PICK_HEAD:
            case HAMMER_HEAD:
            case HOE_HEAD:
            case AXE_HEAD:
            case CHISEL_HEAD:
            case JAVELIN_HEAD:
            case MACE_HEAD:
            case PROPICK_HEAD:
            case SHOVEL_HEAD:
            case KNIFE_BLADE:
            case SAW_BLADE:
            case SCYTHE_BLADE:
            case SWORD_BLADE:
                return Size.NORMAL; // Tool heads fits in large vessels
            case ANVIL:
                return Size.HUGE; // Overburdens
            default:
                return Size.LARGE; // Everything else fits only in chests
        }
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        switch (type)
        {
            case DUST:
            case NUGGET:
            case SCRAP:
                return Weight.VERY_LIGHT; // Stacksize = 64
            case INGOT:
            case DOUBLE_INGOT:
            case SHEET:
            case DOUBLE_SHEET:
                return Weight.LIGHT; // Stacksize = 32
            case ANVIL:
            case HELMET:
            case GREAVES:
            case CHESTPLATE:
            case BOOTS:
            case UNFINISHED_CHESTPLATE:
            case UNFINISHED_GREAVES:
            case UNFINISHED_HELMET:
            case UNFINISHED_BOOTS:
                return Weight.VERY_HEAVY; // Stacksize = 1
            default:
                return Weight.MEDIUM; // Stacksize = 16 for everything else, but tools will still stack only to 1
        }
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        switch (type)
        {
            case ROD:
            case DUST:
            case LAMP:
            case ANVIL:
            case TRAPDOOR:
            case SCRAP:
            case INGOT:
            case SHEET:
            case NUGGET:
            case AXE_HEAD:
            case HOE_HEAD:
            case MACE_HEAD:
            case PICK_HEAD:
            case SAW_BLADE:
            case CHISEL_HEAD:
            case HAMMER_HEAD:
            case KNIFE_BLADE:
            case SHOVEL_HEAD:
            case SWORD_BLADE:
            case DOUBLE_INGOT:
            case DOUBLE_SHEET:
            case JAVELIN_HEAD:
            case PROPICK_HEAD:
            case SCYTHE_BLADE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return this.type == Metal.ItemType.KNIFE || super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ForgeableHeatableHandler(nbt, metal.getSpecificHeat(), metal.getMeltTemp());
    }

    @Override
    @Nonnull
    public IRarity getForgeRarity(@Nonnull ItemStack stack)
    {
        switch (metal.getTier())
        {
            case TIER_I:
            case TIER_II:
                return EnumRarity.COMMON;
            case TIER_III:
                return EnumRarity.UNCOMMON;
            case TIER_IV:
                return EnumRarity.RARE;
            case TIER_V:
                return EnumRarity.EPIC;
        }
        return super.getForgeRarity(stack);
    }

    public Metal.ItemType getType()
    {
        return type;
    }
}

package net.dries007.tfc.objects.items.rock;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemRockAxe extends ItemAxe
{
    private static final EnumMap<Rock.Category, ItemRockAxe> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockAxe get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public final Rock.Category category;

    public ItemRockAxe(Rock.Category category)
    {
        super(category.toolMaterial, 1.5f * category.toolMaterial.getAttackDamage(), -3);
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("axe", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "axe");
        OreDictionaryHelper.register(this, "axe", "stone");
        OreDictionaryHelper.register(this, "axe", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }
}

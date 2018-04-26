package net.dries007.tfc.objects.items.rock;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemRockShovel extends ItemSpade
{
    private static final EnumMap<Rock.Category, ItemRockShovel> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockShovel get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public final Rock.Category category;

    public ItemRockShovel(Rock.Category category)
    {
        super(category.toolMaterial);
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        attackDamage = 1.5f * category.toolMaterial.getAttackDamage();
        setHarvestLevel("shovel", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "shovel");
        OreDictionaryHelper.register(this, "shovel", "stone");
        OreDictionaryHelper.register(this, "shovel", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }
}

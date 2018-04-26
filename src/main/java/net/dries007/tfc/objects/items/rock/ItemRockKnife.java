package net.dries007.tfc.objects.items.rock;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemRockKnife extends ItemTool
{
    private static final EnumMap<Rock.Category, ItemRockKnife> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockKnife get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public final Rock.Category category;

    public ItemRockKnife(Rock.Category category)
    {
        super(0.5f * category.toolMaterial.getAttackDamage(), 3, category.toolMaterial, ImmutableSet.of());
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("knife", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "knife");
        OreDictionaryHelper.register(this, "knife", "stone");
        OreDictionaryHelper.register(this, "knife", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }
}

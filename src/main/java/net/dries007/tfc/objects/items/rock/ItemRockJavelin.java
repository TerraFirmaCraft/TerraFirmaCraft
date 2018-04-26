/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

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

public class ItemRockJavelin extends ItemTool
{
    private static final EnumMap<Rock.Category, ItemRockJavelin> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockJavelin get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public final Rock.Category category;

    public ItemRockJavelin(Rock.Category category)
    {
        super(1f * category.toolMaterial.getAttackDamage(), -1, category.toolMaterial, ImmutableSet.of());
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        OreDictionaryHelper.register(this, "javelin");
        OreDictionaryHelper.register(this, "javelin", "stone");
        OreDictionaryHelper.register(this, "javelin", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }
}

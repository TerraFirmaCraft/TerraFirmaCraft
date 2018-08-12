/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.rock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IItemSize;
import net.dries007.tfc.api.util.Size;
import net.dries007.tfc.api.util.Weight;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemRockHoe extends ItemHoe implements IItemSize
{
    private static final Map<RockCategory, ItemRockHoe> MAP = new HashMap<>();

    public static ItemRockHoe get(RockCategory category)
    {
        return MAP.get(category);
    }

    public final RockCategory category;

    public ItemRockHoe(RockCategory category)
    {
        super(category.getToolMaterial());
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("hoe", category.getToolMaterial().getHarvestLevel());
        OreDictionaryHelper.register(this, "hoe");
        OreDictionaryHelper.register(this, "hoe", "stone");
        OreDictionaryHelper.register(this, "hoe", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.LARGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }
}

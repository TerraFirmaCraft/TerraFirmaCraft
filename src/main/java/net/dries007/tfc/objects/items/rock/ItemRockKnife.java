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

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemRockKnife extends ItemTool implements IItemSize
{
    private static final Map<RockCategory, ItemRockKnife> MAP = new HashMap<>();

    public static ItemRockKnife get(RockCategory category)
    {
        return MAP.get(category);
    }

    public final RockCategory category;

    public ItemRockKnife(RockCategory category)
    {
        super(0.5f * category.getToolMaterial().getAttackDamage(), 3, category.getToolMaterial(), ImmutableSet.of());
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("knife", category.getToolMaterial().getHarvestLevel());
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

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }
}

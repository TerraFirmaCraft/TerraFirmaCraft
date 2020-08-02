/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.rock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
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
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemRockHammer extends ItemTool implements IItemSize, IRockObject
{
    private static final Map<RockCategory, ItemRockHammer> MAP = new HashMap<>();

    public static ItemRockHammer get(RockCategory category)
    {
        return MAP.get(category);
    }

    public final RockCategory category;

    public ItemRockHammer(RockCategory category)
    {
        // Vanilla ItemTool constructor actually treats this as "bonus attack damage", and as a result, adds + getAttackDamage(). So for our purposes, this is 1.0 * attack damage.
        super(0f, -3f, category.getToolMaterial(), ImmutableSet.of());
        this.category = category;
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("hammer", category.getToolMaterial().getHarvestLevel());
        OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
        OreDictionaryHelper.register(this, "hammer");
        OreDictionaryHelper.register(this, "hammer", "stone");
        OreDictionaryHelper.register(this, "hammer", "stone", category);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Rock type: " + OreDictionaryHelper.toString(category));
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.LARGE;  // Stored only in chests
    }

    @Nonnull
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

    @Nullable
    @Override
    public Rock getRock(ItemStack stack)
    {
        return null;
    }

    @Nonnull
    @Override
    public RockCategory getRockCategory(ItemStack stack)
    {
        return category;
    }
}

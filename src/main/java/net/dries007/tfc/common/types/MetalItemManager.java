/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class MetalItemManager extends DataManager<MetalItem>
{
    public static final MetalItemManager INSTANCE = new MetalItemManager();
    private static final IndirectHashCollection<Item, MetalItem> CACHE = new IndirectHashCollection<>(MetalItem::getValidItems);

    @Nullable
    public static MetalItem get(ItemStack stack)
    {
        for (MetalItem def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        MetalItem def = get(stack);
        if (def != null)
        {
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.metal", def.getMetal().getDisplayName()));
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.units", def.getAmount()));
            text.add(def.getMetal().getTier().getDisplayName());
        }
    }

    private MetalItemManager()
    {
        super(new GsonBuilder().create(), "metal_items", "metal item");
    }

    @Override
    protected MetalItem read(ResourceLocation id, JsonObject obj)
    {
        return new MetalItem(id, obj);
    }
}
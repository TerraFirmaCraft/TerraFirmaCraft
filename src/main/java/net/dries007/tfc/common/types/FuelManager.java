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

public class FuelManager extends DataManager<Fuel>
{
    public static final FuelManager INSTANCE = new FuelManager();
    private static final IndirectHashCollection<Item, Fuel> CACHE = new IndirectHashCollection<>(Fuel::getValidItems);

    @Nullable
    public static Fuel get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static boolean isItemFuel(ItemStack stack)
    {
        return get(stack) != null;
    }

    public static boolean isItemForgeFuel(ItemStack stack)
    {
        Fuel fuel = get(stack);
        return fuel != null && fuel.isForgeFuel();
    }

    public static boolean isItemBloomeryFuel(ItemStack stack)
    {
        Fuel fuel = get(stack);
        return fuel != null && fuel.isBloomeryFuel();
    }

    public static boolean isFirepitFuel(ItemStack stack)
    {
        Fuel fuel = get(stack);
        return fuel != null && !fuel.isBloomeryFuel() && !fuel.isForgeFuel();
    }

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        Fuel def = get(stack);
        if (def != null)
        {
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.fuel", def.getDuration(), def.getTemperature()));
        }
    }

    private FuelManager()
    {
        super(new GsonBuilder().create(), "fuels", "fuel", true);
    }

    @Override
    protected Fuel read(ResourceLocation id, JsonObject obj)
    {
        return new Fuel(id, obj);
    }
}

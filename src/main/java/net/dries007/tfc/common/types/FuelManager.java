package net.dries007.tfc.common.types;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class FuelManager extends DataManager<Fuel>
{
    public static final FuelManager INSTANCE = new FuelManager();
    private static final IndirectHashCollection<Item, Fuel> CACHE = new IndirectHashCollection<>(Fuel::getValidItems);

    @Nonnull
    public static Optional<Fuel> get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return Optional.of(def);
            }
        }
        return Optional.empty();
    }

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        Optional<Fuel> opt = get(stack);
        if (opt.isPresent())
        {
            Fuel fuel = opt.get();
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.fuel", fuel.getDuration(), fuel.getTemperature()));
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

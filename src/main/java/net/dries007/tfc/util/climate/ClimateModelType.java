package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;

public record ClimateModelType(Supplier<ClimateModel> factory, ResourceLocation id)
{
    public ClimateModel create()
    {
        return factory.get();
    }
}

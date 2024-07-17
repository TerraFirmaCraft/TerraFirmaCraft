package net.dries007.tfc.data.providers;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

import net.dries007.tfc.util.Helpers;

public class BuiltinPaintings
{
    private final BootstrapContext<PaintingVariant> context;

    public BuiltinPaintings(BootstrapContext<PaintingVariant> context)
    {
        this.context = context;

        register("golden_field", 1, 1);
        register("hot_spring", 2, 2);
        register("lake", 2, 2);
        register("supports", 2, 2);
        register("volcano", 3, 3);
    }

    private void register(String name, int width, int height)
    {
        final ResourceLocation id = Helpers.identifier(name);
        context.register(ResourceKey.create(Registries.PAINTING_VARIANT, id), new PaintingVariant(width, height, id));
    }
}

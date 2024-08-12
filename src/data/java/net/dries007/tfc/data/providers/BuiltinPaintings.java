package net.dries007.tfc.data.providers;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

import net.dries007.tfc.util.Helpers;

public class BuiltinPaintings
{
    public static final ResourceKey<PaintingVariant> GOLDEN_FIELD = key("golden_field");
    public static final ResourceKey<PaintingVariant> HOT_SPRING = key("hot_spring");
    public static final ResourceKey<PaintingVariant> LAKE = key("lake");
    public static final ResourceKey<PaintingVariant> SUPPORTS = key("supports");
    public static final ResourceKey<PaintingVariant> VOLCANO = key("volcano");

    private static ResourceKey<PaintingVariant> key(String name)
    {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Helpers.identifier(name));
    }

    private final BootstrapContext<PaintingVariant> context;

    public BuiltinPaintings(BootstrapContext<PaintingVariant> context)
    {
        this.context = context;

        register(GOLDEN_FIELD, 1, 1);
        register(HOT_SPRING, 2, 2);
        register(LAKE, 2, 2);
        register(SUPPORTS, 2, 2);
        register(VOLCANO, 3, 3);
    }

    private void register(ResourceKey<PaintingVariant> key, int width, int height)
    {
        context.register(key, new PaintingVariant(width, height, key.location()));
    }
}

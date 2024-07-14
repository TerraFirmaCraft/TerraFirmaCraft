/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.registry.RegistryHolder;

@SuppressWarnings("unused")
public class TFCPaintings
{
    public static final DeferredRegister<PaintingVariant> PAINTING_TYPES = DeferredRegister.create(Registries.PAINTING_VARIANT, TerraFirmaCraft.MOD_ID);

    public static final Id GOLDEN_FIELD = register("golden_field", 16, 16);
    public static final Id HOT_SPRING = register("hot_spring", 32, 32);
    public static final Id LAKE = register("lake", 32, 32);
    public static final Id SUPPORTS = register("supports", 32, 32);
    public static final Id VOLCANO = register("volcano", 48, 48);

    private static Id register(String name, int width, int height)
    {
        return new Id(PAINTING_TYPES.register(name, () -> new PaintingVariant(width, height, Helpers.identifier(name))));
    }

    public record Id(DeferredHolder<PaintingVariant, PaintingVariant> holder) implements RegistryHolder<PaintingVariant, PaintingVariant> {}
}

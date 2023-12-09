/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;

@SuppressWarnings("unused")
public class TFCPaintings
{
    public static final DeferredRegister<PaintingVariant> PAINTING_TYPES = DeferredRegister.create(Registries.PAINTING_VARIANT, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<PaintingVariant> GOLDEN_FIELD = register("golden_field", 16, 16);
    public static final RegistryObject<PaintingVariant> HOT_SPRING = register("hot_spring", 32, 32);
    public static final RegistryObject<PaintingVariant> LAKE = register("lake", 32, 32);
    public static final RegistryObject<PaintingVariant> SUPPORTS = register("supports", 32, 32);
    public static final RegistryObject<PaintingVariant> VOLCANO = register("volcano", 48, 48);

    private static RegistryObject<PaintingVariant> register(String name, int width, int height)
    {
        return PAINTING_TYPES.register(name, () -> new PaintingVariant(width, height));
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.world.entity.decoration.Motive;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;

@SuppressWarnings("unused")
public class TFCPaintings
{
    public static final DeferredRegister<Motive> PAINTING_TYPES = DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<Motive> GOLDEN_FIELD = register("golden_field", 16, 16);
    public static final RegistryObject<Motive> HOT_SPRING = register("hot_spring", 32, 32);
    public static final RegistryObject<Motive> LAKE = register("lake", 32, 32);
    public static final RegistryObject<Motive> SUPPORTS = register("supports", 32, 32);
    public static final RegistryObject<Motive> VOLCANO = register("volcano", 48, 48);

    private static RegistryObject<Motive> register(String name, int width, int height)
    {
        return PAINTING_TYPES.register(name, () -> new Motive(width, height));
    }
}

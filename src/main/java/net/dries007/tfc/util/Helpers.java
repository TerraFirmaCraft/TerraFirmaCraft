/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.function.FromByteFunction;
import net.dries007.tfc.util.function.ToByteFunction;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class Helpers
{
    public static ResourceLocation identifier(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }

    /**
     * Avoids IDE warnings by returning null for fields that are injected in by forge
     *
     * @return Not null!
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull()
    {
        return null;
    }

    public static <T> byte[] createByteArray(T[] array, ToByteFunction<T> byteConverter)
    {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            bytes[i] = byteConverter.get(array[i]);
        }
        return bytes;
    }

    public static <T> void createArrayFromBytes(byte[] byteArray, T[] array, FromByteFunction<T> byteConverter)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            array[i] = byteConverter.get(byteArray[i]);
        }
    }
}

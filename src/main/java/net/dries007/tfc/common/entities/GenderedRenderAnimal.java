/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.Helpers;

public interface GenderedRenderAnimal
{
    boolean displayMaleCharacteristics();

    boolean displayFemaleCharacteristics();

    public static <T> ResourceLocation getGenderedTexture(GenderedRenderAnimal animal, String name)
    {
        return Helpers.animalTexture(animal.displayMaleCharacteristics() ? name + "_male" : name + "_female");
    }
}


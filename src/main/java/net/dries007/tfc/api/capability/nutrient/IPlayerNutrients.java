/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nutrient;

import net.dries007.tfc.util.agriculture.Nutrient;

public interface IPlayerNutrients
{
    float getNutrient(Nutrient nutrient);

    float[] getNutrients();

    void setNutrients(float[] nutrients);

    void setNutrient(Nutrient nutrient, float amount);

    void addNutrient(Nutrient nutrient, float amount);

    void updateNutrientsFastForward();
}

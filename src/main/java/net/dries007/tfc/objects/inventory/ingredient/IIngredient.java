/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import java.util.function.Predicate;

public interface IIngredient<T> extends Predicate<T>
{
    T consume(T input);

    default int getAmount()
    {
        return 1;
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import javax.annotation.Nonnull;

public interface IKnappingType
{
    @Nonnull
    String getName();

    int getAmountToConsume();
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.entities.animals;

public interface ILivestock extends IAnimal
{
    /**
     * Gets the familiarity value
     *
     * @return float between 0-1.
     */
    float getFamiliarity();

    /**
     * Set the familiarity value
     *
     * @param value float between 0-1.
     */
    void setFamiliarity(float value);

    @Override
    default boolean isWild()
    {
        return getFamiliarity() < 0.25F;
    }
}

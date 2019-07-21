/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import javax.annotation.Nonnull;

public enum SkillTier
{
    NOVICE, ADEPT, EXPERT, MASTER;

    private static final SkillTier[] VALUES = values();

    @Nonnull
    public static SkillTier valueOf(int index)
    {
        return index < 0 ? NOVICE : index >= VALUES.length ? MASTER : VALUES[index];
    }
}

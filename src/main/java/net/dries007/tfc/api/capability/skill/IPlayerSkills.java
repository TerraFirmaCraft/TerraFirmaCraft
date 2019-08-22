/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;


import javax.annotation.Nonnull;

public interface IPlayerSkills
{
    /**
     * Gets the current tier of the skill
     *
     * @param instance the skill
     * @return a tier.
     */
    default Tier getTier(ISkill instance)
    {
        return Tier.get(instance, getSkill(instance));
    }

    /**
     * Gets the value of the skill, relative to the current level
     *
     * @param instance the skill
     * @return a value between 0 and instance.getLevelValue()
     */
    default int getLevel(ISkill instance)
    {
        return getSkill(instance) % instance.getLevelValue();
    }

    /**
     * Gets the total value of a skill
     *
     * @param instance the skill
     * @return a value between 0 and 4 * instance.getLevels() * instance.getLevelValue()
     */
    int getSkill(ISkill instance);

    /**
     * Sets the skill to a specific value.
     * Used to sync on client and also to reset values
     *
     * @param instance the skill
     * @param value    the value to set
     */
    void setSkill(ISkill instance, int value);

    /**
     * Adds one point to a skill
     *
     * @param instance the skill
     */
    void addSkill(ISkill instance);

    enum Tier
    {
        NOVICE,
        ADEPT,
        EXPERT,
        MASTER;

        private static final Tier[] VALUES = values();

        @Nonnull
        private static Tier get(ISkill skill, int value)
        {
            int index = value / (skill.getLevelValue() * skill.getLevels());
            if (index < 0)
            {
                return NOVICE;
            }
            if (index > 3)
            {
                return MASTER;
            }
            return VALUES[index];
        }
    }

    /**
     * Gets the current chiseling mode.
     *
     * @return enum value of the chiseling mode
     */
    @Nonnull
    ChiselMode getChiselMode();

    /**
     * Sets the current chiseling mode.
     *
     * @param chiselMode enum value for the new chiseling mode
     */
    void setChiselMode(@Nonnull ChiselMode chiselMode);

    enum ChiselMode
    {
        SMOOTH,
        STAIR,
        SLAB;

        public final ChiselMode getNextMode()
        {
            return ChiselMode.values()[(this.ordinal() + 1) % ChiselMode.values().length];
        }
    }
}

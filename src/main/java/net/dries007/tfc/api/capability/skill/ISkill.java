/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

/**
 * Skills have three distinct numbers:
 * TIERS = Novice, Adept, etc.
 * LEVEL = The player visible level, i.e. 1 / 15, visible in a progress bar
 * LEVEL VALUE = The total amount of points (number of times this skill can be incremented) before gaining + 1 level
 *
 * So the total points of a skill is 4 (# of tiers) * getLevels() * getLevelValue()
 */
public interface ISkill
{
    /**
     * Used to store the skill to nbt, and also as a translation key for the skill in tfc.skill.[getName()].name=
     *
     * @return The name of the skill
     */
    String getName();

    /**
     * This is the maximum number of player visible levels the skill has per tier
     */
    int getLevels();

    int getLevelValue();
}

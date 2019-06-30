/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

public enum Skill implements ISkill
{
    PROSPECTING(10, 100),
    BUTCHERY(10, 5),
    AGRICULTURE(10, 20),
    COOKING(10, 40),
    WEAPON_SMITHING(10, 2),
    ARMOR_SMITHING(10, 2),
    TOOL_SMITHING(10, 4),
    GENERAL_SMITHING(10, 8);

    private final int levels;
    private final int levelValue;

    Skill(int levels, int levelValue)
    {
        this.levels = levels;
        this.levelValue = levelValue;
    }

    @Override
    public String getName()
    {
        return name().toLowerCase();
    }

    @Override
    public int getLevels()
    {
        return levels;
    }

    @Override
    public int getLevelValue()
    {
        return levelValue;
    }
}

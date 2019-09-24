/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.api.capability.player.IPlayerData;

/**
 * A wrapper interface for a single skill. The individual skill class should have methods to add skill
 *
 * @see SkillType
 */
public abstract class Skill implements INBTSerializable<NBTTagCompound>
{
    private final IPlayerData playerData;

    public Skill(IPlayerData playerData)
    {
        this.playerData = playerData;
    }

    /**
     * @return the current tier of the skill
     */
    @Nonnull
    public abstract SkillTier getTier();

    /**
     * This is the progress per skill tier, not the total skill.
     * Should return a value between [0, 1)
     *
     * @return the current level of the skill
     */
    public abstract float getLevel();

    /**
     * Helper function to calculate the total progress of the skill
     *
     * @return a value between [0, 1]
     */
    public float getTotalLevel()
    {
        return 0.25f * (getTier().ordinal() + getLevel());
    }

    /**
     * Helper method to set this skill total level.
     * Should match getTotalLevel()
     *
     * @param value a value between [0, 1]
     */
    public abstract void setTotalLevel(double value);

    /**
     * Helper method to add levels to this skill's total level.
     * Should match getTotalLevel()
     *
     * @param value a value between [0, 1]
     */
    public void addTotalLevel(double value)
    {
        setTotalLevel(Math.min(getTotalLevel() + value, 1D));
    }

    /**
     * Subclasses should call this when the skill updates
     */
    protected final void updateAndSync()
    {
        playerData.updateAndSync();
    }
}

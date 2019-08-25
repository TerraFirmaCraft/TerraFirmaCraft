/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Interface for the capability attached to a player
 * Holds an internal list of skill implementations
 *
 * @see SkillType
 */
public interface IPlayerSkills extends INBTSerializable<NBTTagCompound>
{
    @Nullable
    <S extends Skill> S getSkill(SkillType<S> skillType);

    @Nonnull
    EntityPlayer getPlayer();

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

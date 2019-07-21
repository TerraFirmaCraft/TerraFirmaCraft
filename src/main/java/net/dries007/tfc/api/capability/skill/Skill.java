/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketSkillsUpdate;

/**
 * A wrapper interface for a single skill. The individual skill class should have methods to add skill
 *
 * @see SkillType
 */
public abstract class Skill implements INBTSerializable<NBTTagCompound>
{
    private final IPlayerSkills rootSkills;

    public Skill(IPlayerSkills rootSkills)
    {
        this.rootSkills = rootSkills;
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
     * Subclasses should call this when the skill updates
     */
    protected void updateAndSync()
    {
        if (rootSkills.getPlayer() instanceof EntityPlayerMP)
        {
            TerraFirmaCraft.getNetwork().sendTo(new PacketSkillsUpdate(rootSkills.serializeNBT()), (EntityPlayerMP) rootSkills.getPlayer());
        }
    }
}

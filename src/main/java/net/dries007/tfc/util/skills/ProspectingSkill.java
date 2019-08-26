/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.capability.skill.IPlayerSkills;
import net.dries007.tfc.api.capability.skill.Skill;
import net.dries007.tfc.api.capability.skill.SkillTier;

public class ProspectingSkill extends Skill
{
    private static final double MAX_PROSPECT_DISTANCE = 3 * 64;

    private final Set<BlockPos> foundPositions;
    private int level;

    public ProspectingSkill(IPlayerSkills rootSkills)
    {
        super(rootSkills);
        this.foundPositions = new HashSet<>(40);
    }

    @Override
    @Nonnull
    public SkillTier getTier()
    {
        return SkillTier.valueOf(level % 5);
    }

    @Override
    public float getLevel()
    {
        return (level % 5) / 5.0f;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("level", level);
        int i = 0;
        for (BlockPos pos : foundPositions)
        {
            nbt.setLong("p" + i, pos.toLong());
            i++;
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            level = nbt.getInteger("level");
            for (int i = 0; i < level; i++)
            {
                foundPositions.add(BlockPos.fromLong(nbt.getLong("p" + i)));
            }
        }
    }

    public void addSkill(BlockPos pos)
    {
        // Check that it's not too near to other found positions
        for (BlockPos foundPos : foundPositions)
        {
            if (pos.distanceSq(foundPos) < MAX_PROSPECT_DISTANCE)
            {
                return;
            }
        }

        // New position, so add skill and send update
        foundPositions.add(pos);
        level++;
        rootSkills.updateAndSync();
    }
}

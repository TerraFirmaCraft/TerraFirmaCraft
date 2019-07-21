/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityPlayerSkills
{
    @CapabilityInject(IPlayerSkills.class)
    public static final Capability<IPlayerSkills> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "player_skills");

    public static void preInit()
    {
        // Player skills
        CapabilityManager.INSTANCE.register(IPlayerSkills.class, new DumbStorage<>(), () -> null);
    }

    /**
     * Helper method to get a skill instance
     *
     * @param player       The player to get skills fromm
     * @param skillType    The skill type
     * @param <S>          The skill class
     */
    @Nullable
    public static <S extends Skill> S getSkill(EntityPlayer player, SkillType<S> skillType)
    {
        IPlayerSkills skills = player.getCapability(CAPABILITY, null);
        if (skills != null)
        {
            return skills.getSkill(skillType);
        }
        return null;
    }
}

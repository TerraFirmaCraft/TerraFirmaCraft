/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final List<ISkill> SKILLS = new ArrayList<>(Arrays.asList(Skill.values()));

    /**
     * Get the current list of skills
     * If you want to add your own, or modify TFC ones, this is where it should happen
     */
    public static List<ISkill> getAllSkills()
    {
        return SKILLS;
    }

    public static void preInit()
    {
        // Player skills
        CapabilityManager.INSTANCE.register(IPlayerSkills.class, new DumbStorage<>(), PlayerSkillsHandler::new);
    }
}

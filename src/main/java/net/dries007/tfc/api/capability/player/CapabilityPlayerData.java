/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public final class CapabilityPlayerData
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "player_skills");
    @CapabilityInject(IPlayerData.class)
    public static Capability<IPlayerData> CAPABILITY;

    public static void preInit()
    {
        // Player skills
        CapabilityManager.INSTANCE.register(IPlayerData.class, new DumbStorage<>(), () -> null);
    }

    /**
     * Helper method to get a skill instance
     *
     * @param player    The player to get skills fromm
     * @param skillType The skill type
     * @param <S>       The skill class
     */
    @Nullable
    public static <S extends Skill> S getSkill(EntityPlayer player, SkillType<S> skillType)
    {
        IPlayerData skills = player.getCapability(CAPABILITY, null);
        if (skills != null)
        {
            return skills.getSkill(skillType);
        }
        return null;
    }
}

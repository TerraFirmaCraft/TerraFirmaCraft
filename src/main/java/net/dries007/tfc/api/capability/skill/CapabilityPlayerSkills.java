/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityPlayerSkills
{
    @CapabilityInject(IPlayerSkills.class)
    public static final Capability<IPlayerSkills> CAPABILITY_SKILLS = Helpers.getNull();

    private static final ResourceLocation PLAYER_KEY = new ResourceLocation(MOD_ID, "player_skills");

    public static void preInit()
    {
        // Player skills
        CapabilityManager.INSTANCE.register(IPlayerSkills.class, new DumbStorage<>(), PlayerSkillsHandler::new);
    }

    /**
     * This is the handler for anything player data / food / decay related
     */
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class EventHandler
    {
        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
        {
            // todo: this may need to be copied to player clone event / player change dimension event
            if (event.getObject() instanceof EntityPlayer)
            {
                event.addCapability(PLAYER_KEY, new PlayerSkillsHandler());
            }
        }
    }
}

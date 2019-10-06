package net.dries007.tfc.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
import net.dries007.tfc.api.capability.skill.IPlayerSkills;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public class ChiselModeOverlay
{
    @SubscribeEvent
    public static void drawChiselModeOverlayEvent(RenderGameOverlayEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player.inventory.player;

        // We check for crosshairs just because it's always drawn and is before air bar
        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            return;
        }

        IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);
        if (capability == null)
            return;

        IPlayerSkills.ChiselMode chiselMode = capability.getChiselMode();


    }
}

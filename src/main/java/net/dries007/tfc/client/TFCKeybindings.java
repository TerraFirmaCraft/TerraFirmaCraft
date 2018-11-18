/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
@SideOnly(Side.CLIENT)
public class TFCKeybindings
{
    // todo: make category for TFC

    public static final KeyBinding OPEN_CRAFTING_TABLE = new KeyBinding("tfc.key.craft", KeyConflictContext.UNIVERSAL, Keyboard.KEY_C, "key.categories.inventory");

    public static void init()
    {
        ClientRegistry.registerKeyBinding(OPEN_CRAFTING_TABLE);
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent()
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        if (OPEN_CRAFTING_TABLE.isPressed())
        {
            mc.displayGuiScreen(new GuiCrafting(mc.player.inventory, mc.world));
        }
    }
}

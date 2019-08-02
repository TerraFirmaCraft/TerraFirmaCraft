/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketOpenCraftingGui;
import net.dries007.tfc.network.PacketPlaceBlockSpecial;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.api.util.TFCConstants.MOD_NAME;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
@SideOnly(Side.CLIENT)
public class TFCKeybindings
{
    private static final KeyBinding OPEN_CRAFTING_TABLE = new KeyBinding("tfc.key.craft", KeyConflictContext.IN_GAME, Keyboard.KEY_C, MOD_NAME);
    private static final KeyBinding PLACE_BLOCK = new KeyBinding("tfc.key.placeblock", KeyConflictContext.IN_GAME, Keyboard.KEY_V, MOD_NAME);

    public static void init()
    {
        ClientRegistry.registerKeyBinding(OPEN_CRAFTING_TABLE);
        ClientRegistry.registerKeyBinding(PLACE_BLOCK);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent()
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (OPEN_CRAFTING_TABLE.isPressed())
        {
            TerraFirmaCraft.getNetwork().sendToServer(new PacketOpenCraftingGui());
        }
        if (PLACE_BLOCK.isPressed())
        {
            TerraFirmaCraft.getNetwork().sendToServer(new PacketPlaceBlockSpecial());
        }
    }
}

package net.dries007.tfc.client;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;

import static net.dries007.tfc.TerraFirmaCraft.MOD_NAME;

public class TFCKeyBindings
{
    public static final KeyBinding PLACE_BLOCK = new KeyBinding("tfc.key.place_block", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, MOD_NAME);
}

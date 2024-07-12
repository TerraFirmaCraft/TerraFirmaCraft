/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class TFCKeyBindings
{
    public static final KeyMapping PLACE_BLOCK = new KeyMapping("tfc.key.place_block", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, MOD_NAME);
    public static final KeyMapping CYCLE_CHISEL_MODE = new KeyMapping("tfc.key.cycle_chisel_mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, MOD_NAME);
    public static final KeyMapping STACK_FOOD = new KeyMapping("tfc.key.stack_food", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, MOD_NAME);
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.function.Supplier;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import net.dries007.tfc.config.TFCConfig;

import static net.dries007.tfc.TerraFirmaCraft.MOD_NAME;

public class TFCKeyBindings
{
    public static final KeyMapping PLACE_BLOCK = create("tfc.key.place_block", KeyConflictContext.IN_GAME, InputConstants.KEY_V, GLFW.GLFW_MOUSE_BUTTON_4, TFCConfig.CLIENT.useMouseKeyForSpecialPlace);
    public static final KeyMapping CYCLE_CHISEL_MODE = create("tfc.key.cycle_chisel_mode", KeyConflictContext.IN_GAME, InputConstants.KEY_M, GLFW.GLFW_MOUSE_BUTTON_5, TFCConfig.CLIENT.useMouseKeyForChiselCycle);
    public static final KeyMapping STACK_FOOD = create("tfc.key.stack_food", KeyConflictContext.GUI, InputConstants.KEY_I, GLFW.GLFW_MOUSE_BUTTON_6, TFCConfig.CLIENT.useMouseKeyForFoodStack);

    public static KeyMapping create(String description, IKeyConflictContext keyConflictContext, int keyCode, int mouseKeyCode, Supplier<Boolean> mouseKey)
    {
        return new KeyMapping(description, keyConflictContext, mouseKey.get() ? InputConstants.Type.MOUSE : InputConstants.Type.KEYSYM, mouseKey.get() ? mouseKeyCode : keyCode, MOD_NAME);
    }

}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class CapabilityFood
{
    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY = Helpers.getNull();
    public static final int DEFAULT_ROT_TICKS = CalendarTFC.TICKS_IN_DAY * 12;
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);
    }
}

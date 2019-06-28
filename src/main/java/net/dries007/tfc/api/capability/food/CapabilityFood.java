/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class CapabilityFood
{
    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY = Helpers.getNull();
    public static final int DEFAULT_ROT_TICKS = CalendarTFC.TICKS_IN_DAY * 15;
    private static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class EventHandler
    {
        @SubscribeEvent
        public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
        {
            // This is only to attach food capabilities
            // todo: create a lookup or something for vanilla items
            // future plans: add via craft tweaker or json (1.14)
            ItemStack stack = event.getObject();
            if (stack.getItem() instanceof ItemFood && !stack.hasCapability(CAPABILITY, null))
            {
                event.addCapability(KEY, new FoodHandler(stack.getTagCompound(), new float[] {1, 0, 0, 0, 0}, 0, 0, 1));
            }
        }
    }
}

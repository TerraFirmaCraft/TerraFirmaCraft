/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class CapabilityFood
{
    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");

    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~4 days, grains take ~16 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_ROT_TICKS = ICalendar.TICKS_IN_DAY * 16;

    public static final IFoodTrait.Impl SMOKED = new IFoodTrait.Impl("smoked", 0.5f);
    public static final IFoodTrait.Impl BRINED = new IFoodTrait.Impl("brined", 0.5f);
    public static final IFoodTrait.Impl SALTED = new IFoodTrait.Impl("salted", 0.75f);
    public static final IFoodTrait.Impl PICKLED = new IFoodTrait.Impl("pickled", 0.75f);
    public static final IFoodTrait.Impl PRESERVED = new IFoodTrait.Impl("preserved", 0.5f);

    private static final Map<String, IFoodTrait> TRAITS = new HashMap<>();

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);

        TRAITS.put("smoked", SMOKED);
        TRAITS.put("brined", BRINED);
        TRAITS.put("salted", SALTED); // todo: In 1.7.10 this was 0.5 for uncooked meat, 0.75 for cooked. Requires a custom class.
        TRAITS.put("pickled", PICKLED); // todo: same as above
        TRAITS.put("preserved", PRESERVED);
    }

    public static Map<String, IFoodTrait> getTraits()
    {
        return TRAITS;
    }
}

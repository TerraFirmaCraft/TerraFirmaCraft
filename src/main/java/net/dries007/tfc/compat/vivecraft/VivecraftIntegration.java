/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.vivecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class VivecraftIntegration
{

    public static boolean isVREnabled()
    {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return false;

        try
        {
            Class<?> vrStateClass = Class.forName("org.vivecraft.client_vr.VRState");
            java.lang.reflect.Field vrRunningField = vrStateClass.getDeclaredField("VR_RUNNING");
            return vrRunningField.getBoolean(null);
        }
        catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoClassDefFoundError e)
        {
            return false;
        }
    }

}

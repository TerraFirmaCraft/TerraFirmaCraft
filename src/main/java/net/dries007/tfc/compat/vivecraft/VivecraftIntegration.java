/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.vivecraft;

public class VivecraftIntegration
{

    public static boolean isVREnabled()
    {
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

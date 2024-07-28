/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

/**
 * todo: it is worth having a native EMI plugin, as otherwise it will populate from JEI, which keeps both JEI and EMI
 * runtime loaded. Ultimately this is poor, and I would like to provide first-class EMI compat
 */
@EmiEntrypoint
public final class EmiIntegration implements EmiPlugin
{
    @Override
    public void register(EmiRegistry emiRegistry)
    {

    }
}

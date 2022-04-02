/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Locale;

public enum Alcohol
{
    BEER(0xFFC39E37),
    CIDER(0xFFB0AE32),
    RUM(0xFF6E0123),
    SAKE(0xFFB7D9BC),
    VODKA(0xFFDCDCDC),
    WHISKEY(0xFF583719),
    CORN_WHISKEY(0xFFD9C7B7),
    RYE_WHISKEY(0xFFC77D51);

    private final String id;
    private final int color;

    Alcohol(int color)
    {
        this.id = name().toLowerCase(Locale.ROOT);
        this.color = color;
    }

    public String getId()
    {
        return id;
    }

    public int getColor()
    {
        return color;
    }
}

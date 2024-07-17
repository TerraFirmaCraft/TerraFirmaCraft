/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Locale;

public enum SimpleFluid
{
    BRINE(0xFFDCD3C9),
    CURDLED_MILK(0xFFFFFBE8),
    LIMEWATER(0xFFB4B4B4),
    LYE(0xFFfeffde),
    MILK_VINEGAR(0xFFFFFBE8),
    OLIVE_OIL(0xFF6A7537),
    OLIVE_OIL_WATER(0xFF4A4702),
    TANNIN(0xFF63594E),
    TALLOW(0xFFEDE9CF),
    VINEGAR(0xFFC7C2AA),

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

    SimpleFluid(int color)
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

    public boolean isTransparent()
    {
        return this != CURDLED_MILK && this != MILK_VINEGAR;
    }
}

package net.dries007.tfc.common.fluids;

import java.util.Locale;

// todo; everything
public enum Alcohol
{
    BEER,
    CIDER,
    RUM,
    SAKE,
    VODKA,
    WHISKEY,
    CORN_WHISKEY,
    RYE_WHISKEY;

    private final String id;

    Alcohol()
    {
        this.id = name().toLowerCase(Locale.ROOT);
    }

    public String getId()
    {
        return id;
    }
}

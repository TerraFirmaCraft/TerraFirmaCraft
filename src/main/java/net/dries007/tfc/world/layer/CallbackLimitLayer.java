package net.dries007.tfc.world.layer;

public class CallbackLimitLayer
{
    protected int limit;

    public CallbackLimitLayer(int limit)
    {
        setLimit(limit);
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }
}

package net.dries007.tfc.util;

public class ImmutablePair<A, B>
{
    public final A a;
    public final B b;

    public ImmutablePair(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A getA()
    {
        return a;
    }

    public B getB()
    {
        return b;
    }
}

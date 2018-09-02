/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

// todo: this isn't used anywhere anymore, remove?
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

package net.dries007.tfc.images;

import java.awt.*;

@FunctionalInterface
public interface ColorTransformer
{
    Color apply(double value);
}

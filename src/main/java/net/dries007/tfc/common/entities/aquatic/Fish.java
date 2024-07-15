/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.Locale;
import net.minecraft.sounds.SoundEvents;

import net.dries007.tfc.client.TFCSounds;

public enum Fish
{
    BLUEGILL(0x00658A, 0xE3E184),
    CRAPPIE(0xf542ef, 0xd6d5c5),
    LAKE_TROUT(0x707594, 0x8f816d),
    LARGEMOUTH_BASS(0x619c57, 0x9c7f57),
    RAINBOW_TROUT(0xc928c7, 0xc97a0a),
    SALMON(10489616, 951412),
    SMALLMOUTH_BASS(0x9c7f57, 0x619c57);

    private final String serializedName;
    private final int color1;
    private final int color2;

    Fish(int color1, int color2)
    {
        this.color1 = color1;
        this.color2 = color2;
        serializedName = name().toLowerCase(Locale.ROOT);
    }

    public String getSerializedName()
    {
        return serializedName;
    }

    public TFCSounds.FishId makeSound()
    {
        if (this == SALMON)
        {
            return new TFCSounds.FishId(() -> SoundEvents.SALMON_AMBIENT, () -> SoundEvents.SALMON_DEATH, () -> SoundEvents.SALMON_HURT, () -> SoundEvents.SALMON_FLOP);
        }
        return TFCSounds.registerFish(serializedName);
    }

    public float getWidth()
    {
        return this == BLUEGILL ? 0.5f : 0.7f;
    }

    public float getHeight()
    {
        return this == BLUEGILL ? 0.3f : 0.4f;
    }

    public int getEggColor2()
    {
        return color2;
    }

    public int getEggColor1()
    {
        return color1;
    }
}

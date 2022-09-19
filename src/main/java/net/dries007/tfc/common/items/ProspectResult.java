/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.util.Helpers;

public enum ProspectResult
{
    VERY_LARGE("tfc.tooltip.propick.found_very_large"),
    LARGE("tfc.tooltip.propick.found_large"),
    MEDIUM("tfc.tooltip.propick.found_medium"),
    SMALL("tfc.tooltip.propick.found_small"),
    TRACES("tfc.tooltip.propick.found_traces"),

    FOUND("tfc.tooltip.propick.found"),
    NOTHING("tfc.tooltip.propick.nothing");

    private static final ProspectResult[] VALUES = values();

    public static ProspectResult valueOf(int i)
    {
        return i < 0 || i >= VALUES.length ? NOTHING : VALUES[i];
    }

    private final String translationKey;

    ProspectResult(String translation)
    {
        this.translationKey = translation;
    }

    public Component getText(Block block)
    {
        return this == NOTHING ?
            Helpers.translatable(translationKey) :
            Helpers.translatable(translationKey, Helpers.translatable(block.getDescriptionId() + ".prospected"));
    }
}

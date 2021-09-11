package net.dries007.tfc.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;

public enum ProspectResult
{
    VERY_LARGE("tfc.tooltip.propick.found_very_large"),
    LARGE("tfc.tooltip.propick.found_large"),
    MEDIUM("tfc.tooltip.propick.found_medium"),
    SMALL("tfc.tooltip.propick.found_small"),
    TRACES("tfc.tooltip.propick.found_traces"),

    FOUND("tfc.tooltip.propick.found"),
    NOTHING("tfc.tooltip.propick.nothing"),
    NOTHING_FALSE_NEGATIVE("tfc.tooltip.propick.nothing"); // The player cannot tell these two apart

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
        MutableComponent text = new TranslatableComponent(translationKey);
        if (this != NOTHING && this != NOTHING_FALSE_NEGATIVE)
        {
            text = text.append(" ").append(block.getName());
        }
        return text;
    }
}

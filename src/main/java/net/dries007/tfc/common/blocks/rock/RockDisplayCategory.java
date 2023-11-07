/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.util.Helpers;

/**
 * A superset of {@link RockCategory} used for tooltip accuracy, although items are only seperated into categories, hence the name of this enum as "display" categories.
 */
public enum RockDisplayCategory
{
    FELSIC_IGNEOUS_EXTRUSIVE,
    INTERMEDIATE_IGNEOUS_EXTRUSIVE,
    MAFIC_IGNEOUS_EXTRUSIVE,
    FELSIC_IGNEOUS_INTRUSIVE,
    INTERMEDIATE_IGNEOUS_INTRUSIVE,
    MAFIC_IGNEOUS_INTRUSIVE,
    METAMORPHIC,
    SEDIMENTARY;

    public RockCategory category()
    {
        return switch (this)
        {
            case FELSIC_IGNEOUS_EXTRUSIVE, INTERMEDIATE_IGNEOUS_EXTRUSIVE, MAFIC_IGNEOUS_EXTRUSIVE -> RockCategory.IGNEOUS_EXTRUSIVE;
            case FELSIC_IGNEOUS_INTRUSIVE, INTERMEDIATE_IGNEOUS_INTRUSIVE, MAFIC_IGNEOUS_INTRUSIVE -> RockCategory.IGNEOUS_INTRUSIVE;
            case METAMORPHIC -> RockCategory.METAMORPHIC;
            case SEDIMENTARY -> RockCategory.SEDIMENTARY;
        };
    }

    public Component createTooltip()
    {
        return Helpers.translateEnum(this).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }
}

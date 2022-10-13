/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

public interface MouthHolder
{
    void translateToMouth(PoseStack stack);
}

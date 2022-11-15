/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.animation;

import com.mojang.math.Vector3f;

public record Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation)
{
}

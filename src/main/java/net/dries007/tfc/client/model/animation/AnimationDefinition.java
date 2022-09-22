/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.animation;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public record AnimationDefinition(float lengthInSeconds, boolean loop, Map<String, List<AnimationChannel>> boneAnimations)
{
    public static class Builder
    {
        private final float time;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static AnimationDefinition.Builder withLength(float seconds)
        {
            return new AnimationDefinition.Builder(seconds);
        }

        private Builder(float seconds)
        {
            this.time = seconds;
        }

        public AnimationDefinition.Builder looping()
        {
            this.looping = true;
            return this;
        }

        public AnimationDefinition.Builder addAnimation(String bone, AnimationChannel channel)
        {
            this.animationByBone.computeIfAbsent(bone, c -> Lists.newArrayList()).add(channel);
            return this;
        }

        public AnimationDefinition build()
        {
            return new AnimationDefinition(this.time, this.looping, this.animationByBone);
        }
    }
}

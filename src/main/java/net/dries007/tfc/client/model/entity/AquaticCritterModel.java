/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;

import net.dries007.tfc.common.entities.aquatic.AquaticCritter;

public class AquaticCritterModel extends HierarchicalAnimatedModel<AquaticCritter>
{
    private final AnimationDefinition crawl;
    private final AnimationDefinition calm;
    private final AnimationDefinition swim;
    private final AnimationDefinition damage;

    public AquaticCritterModel(ModelPart root, AnimationDefinition crawl, AnimationDefinition calm, AnimationDefinition swim, AnimationDefinition damage)
    {
        super(root);
        this.crawl = crawl;
        this.calm = calm;
        this.swim = swim;
        this.damage = damage;
    }

    @Override
    public void setupAnim(AquaticCritter entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.hurtAnimation.isStarted())
        {
            animate(entity.hurtAnimation, damage, ageInTicks);
        }
        else if (!entity.onGround())
        {
            animateWalk(swim, limbSwing, limbSwingAmount, 1f, 2.5f);
        }
        else
        {
            if (entity.idleAnimation.isStarted())
            {
                animate(entity.idleAnimation, calm, ageInTicks);
            }
            else
            {
                animateWalk(crawl, limbSwing, limbSwingAmount, 1f, 2.5f);
            }
        }

    }
}

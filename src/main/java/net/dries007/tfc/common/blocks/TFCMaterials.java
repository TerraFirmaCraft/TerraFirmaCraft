/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class TFCMaterials
{
    public static final Material MOLTEN_METAL = new Builder(MaterialColor.FIRE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material SALT_WATER = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material SPRING_WATER = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();

    public static final Material NON_SOLID_STONE = new Builder(MaterialColor.STONE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material THATCH_COLOR_LEAVES = new Builder(MaterialColor.SAND).flammable().notSolidBlocking().destroyOnPush().build();

    /**
     * This is an exact copy of {@link Material.Builder} except with all builder methods set to public
     */
    public static class Builder
    {
        private final MaterialColor color;
        private PushReaction pushReaction = PushReaction.NORMAL;
        private boolean blocksMotion = true;
        private boolean flammable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid = true;
        private boolean solidBlocking = true;

        public Builder(MaterialColor colorIn)
        {
            this.color = colorIn;
        }

        public Builder liquid()
        {
            this.liquid = true;
            return this;
        }

        public Builder nonSolid()
        {
            this.solid = false;
            return this;
        }

        public Builder noCollider()
        {
            this.blocksMotion = false;
            return this;
        }

        public Builder notSolidBlocking()
        {
            this.solidBlocking = false;
            return this;
        }

        public Builder flammable()
        {
            this.flammable = true;
            return this;
        }

        public Builder replaceable()
        {
            this.replaceable = true;
            return this;
        }

        public Builder destroyOnPush()
        {
            this.pushReaction = PushReaction.DESTROY;
            return this;
        }

        public Builder notPushable()
        {
            this.pushReaction = PushReaction.BLOCK;
            return this;
        }

        public Material build()
        {
            return new Material(color, liquid, solid, blocksMotion, solidBlocking, flammable, replaceable, pushReaction);
        }
    }
}

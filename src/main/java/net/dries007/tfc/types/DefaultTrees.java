/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.*;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultTrees
{

    public static final ResourceLocation ACACIA = new ResourceLocation(MOD_ID, "acacia");
    public static final ResourceLocation ASH = new ResourceLocation(MOD_ID, "ash");
    public static final ResourceLocation ASPEN = new ResourceLocation(MOD_ID, "aspen");
    public static final ResourceLocation BIRCH = new ResourceLocation(MOD_ID, "birch");
    public static final ResourceLocation BLACKWOOD = new ResourceLocation(MOD_ID, "blackwood");
    public static final ResourceLocation CHESTNUT = new ResourceLocation(MOD_ID, "chestnut");
    public static final ResourceLocation DOUGLAS_FIR = new ResourceLocation(MOD_ID, "douglas_fir");
    public static final ResourceLocation HICKORY = new ResourceLocation(MOD_ID, "hickory");
    public static final ResourceLocation MAPLE = new ResourceLocation(MOD_ID, "maple");
    public static final ResourceLocation OAK = new ResourceLocation(MOD_ID, "oak");
    public static final ResourceLocation PALM = new ResourceLocation(MOD_ID, "palm");
    public static final ResourceLocation PINE = new ResourceLocation(MOD_ID, "pine");
    public static final ResourceLocation ROSEWOOD = new ResourceLocation(MOD_ID, "rosewood");
    public static final ResourceLocation SEQUOIA = new ResourceLocation(MOD_ID, "sequoia");
    public static final ResourceLocation SPRUCE = new ResourceLocation(MOD_ID, "spruce");
    public static final ResourceLocation SYCAMORE = new ResourceLocation(MOD_ID, "sycamore");
    public static final ResourceLocation WHITE_CEDAR = new ResourceLocation(MOD_ID, "white_cedar");
    public static final ResourceLocation WILLOW = new ResourceLocation(MOD_ID, "willow");
    public static final ResourceLocation KAPOK = new ResourceLocation(MOD_ID, "kapok");

    /**
     * Simple ITreeGenerator instances.
     */
    public static final ITreeGenerator GEN_NORMAL = new TreeGenNormal(1, 3);
    public static final ITreeGenerator GEN_TALL = new TreeGenNormal(3, 3);
    public static final ITreeGenerator GEN_LARGE = new TreeGenNormal(0, 2);
    public static final ITreeGenerator GEN_CONIFER = new TreeGenVariants(false, 7);
    public static final ITreeGenerator GEN_TROPICAL = new TreeGenVariants(true, 7);
    public static final ITreeGenerator GEN_WILLOW = new TreeGenWillow();
    public static final ITreeGenerator GEN_ACACIA = new TreeGenAcacia();
    public static final ITreeGenerator GEN_KAPOK = new TreeGenKapok();
    public static final ITreeGenerator GEN_SEQUOIA = new TreeGenSequoia();

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<Tree> event)
    {
        IForgeRegistry<Tree> r = event.getRegistry();
        r.registerAll(
            new Tree(ACACIA, 250f, 4000f, 28f, 50f, 4f, 8f, 2, GEN_ACACIA),
            new Tree(ASH, 125f, 4000f, 4f, 24f, 1f, 8f, 2, GEN_TALL),
            new Tree(ASPEN, 125f, 1000f, -5f, 18f, 0.25f, 4f, 2, GEN_TALL),
            new Tree(BIRCH, 62.5f, 250f, -10f, 12f, 0f, 4f, 2, GEN_TALL),
            new Tree(BLACKWOOD, 62.5f, 500f, 4f, 28f, 0.5f, 2f, 2, GEN_NORMAL),
            new Tree(CHESTNUT, 125f, 4000f, 3f, 24f, 0f, 2f, 2, GEN_NORMAL),
            new Tree(DOUGLAS_FIR, 500f, 4000f, 1f, 14f, 0f, 4f, 2, GEN_LARGE),
            new Tree(HICKORY, 125f, 4000f, 4f, 28f, 0f, 4f, 2, GEN_TALL),
            new Tree(MAPLE, 125f, 4000f, 3f, 20f, 0f, 4f, 2, GEN_NORMAL),
            new Tree(OAK, 250f, 1000f, 5f, 25f, 0.25f, 8f, 2, GEN_NORMAL),
            new Tree(PALM, 1000f, 4000f, 12f, 50f, 2f, 8f, 2, GEN_TROPICAL),
            new Tree(PINE, 125f, 4000f, -15f, 24f, 0.25f, 8f, 2, GEN_CONIFER),
            new Tree(ROSEWOOD, 500f, 4000f, 8f, 18f, 0f, 2f, 2, GEN_LARGE),
            new Tree(SEQUOIA, 1000f, 4000f, 10f, 16f, 0f, 1f, 2, GEN_SEQUOIA),
            new Tree(SPRUCE, 125f, 4000f, -5f, 24f, 0f, 4f, 2, GEN_CONIFER),
            new Tree(SYCAMORE, 250f, 4000f, 6f, 30f, 0f, 4f, 2, GEN_NORMAL),
            new Tree(WHITE_CEDAR, 125f, 4000f, -5f, 24f, 0f, 8f, 2, GEN_LARGE),
            new Tree(WILLOW, 2000f, 4000f, 10f, 30f, 0f, 1f, 2, GEN_WILLOW),
            new Tree(KAPOK, 1000f, 4000f, 30f, 50f, 0f, 4f, 2, GEN_KAPOK)
        );
    }

    /**
     * Do not rely on this to contain all the tree objects. Because it won't. (not necessarily)
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static final class O
    {

        public static final Tree ACACIA = null;
        public static final Tree ASH = null;
        public static final Tree ASPEN = null;
        public static final Tree BIRCH = null;
        public static final Tree BLACKWOOD = null;
        public static final Tree CHESTNUT = null;
        public static final Tree DOUGLAS_FIR = null;
        public static final Tree HICKORY = null;
        public static final Tree MAPLE = null;
        public static final Tree OAK = null;
        public static final Tree PALM = null;
        public static final Tree PINE = null;
        public static final Tree ROSEWOOD = null;
        public static final Tree SEQUOIA = null;
        public static final Tree SPRUCE = null;
        public static final Tree SYCAMORE = null;
        public static final Tree WHITE_CEDAR = null;
        public static final Tree WILLOW = null;
        public static final Tree KAPOK = null;

    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.*;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultTrees
{

    /**
     * Simple ITreeGenerator instances. You can use these when registering custom trees.
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

    /**
     * Default tree ResourceLocations
     */
    private static final ResourceLocation ACACIA = new ResourceLocation(MOD_ID, "acacia");
    private static final ResourceLocation ASH = new ResourceLocation(MOD_ID, "ash");
    private static final ResourceLocation ASPEN = new ResourceLocation(MOD_ID, "aspen");
    private static final ResourceLocation BIRCH = new ResourceLocation(MOD_ID, "birch");
    private static final ResourceLocation BLACKWOOD = new ResourceLocation(MOD_ID, "blackwood");
    private static final ResourceLocation CHESTNUT = new ResourceLocation(MOD_ID, "chestnut");
    private static final ResourceLocation DOUGLAS_FIR = new ResourceLocation(MOD_ID, "douglas_fir");
    private static final ResourceLocation HICKORY = new ResourceLocation(MOD_ID, "hickory");
    private static final ResourceLocation MAPLE = new ResourceLocation(MOD_ID, "maple");
    private static final ResourceLocation OAK = new ResourceLocation(MOD_ID, "oak");
    private static final ResourceLocation PALM = new ResourceLocation(MOD_ID, "palm");
    private static final ResourceLocation PINE = new ResourceLocation(MOD_ID, "pine");
    private static final ResourceLocation ROSEWOOD = new ResourceLocation(MOD_ID, "rosewood");
    private static final ResourceLocation SEQUOIA = new ResourceLocation(MOD_ID, "sequoia");
    private static final ResourceLocation SPRUCE = new ResourceLocation(MOD_ID, "spruce");
    private static final ResourceLocation SYCAMORE = new ResourceLocation(MOD_ID, "sycamore");
    private static final ResourceLocation WHITE_CEDAR = new ResourceLocation(MOD_ID, "white_cedar");
    private static final ResourceLocation WILLOW = new ResourceLocation(MOD_ID, "willow");
    private static final ResourceLocation KAPOK = new ResourceLocation(MOD_ID, "kapok");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<Tree> event)
    {
        IForgeRegistry<Tree> r = event.getRegistry();
        r.registerAll(
            new Tree.Builder(ACACIA, 250f, 4000f, 28f, 50f, 4f, 8f, GEN_ACACIA).setMaxGrowthRadius(3).setMaxHeight(10).build(),
            new Tree.Builder(ASH, 125f, 4000f, 4f, 24f, 1f, 8f, GEN_TALL).build(),
            new Tree.Builder(ASPEN, 125f, 1000f, -5f, 18f, 0.25f, 4f, GEN_TALL).build(),
            new Tree.Builder(BIRCH, 62.5f, 250f, -10f, 12f, 0f, 4f, GEN_TALL).build(),
            new Tree.Builder(BLACKWOOD, 62.5f, 500f, 4f, 28f, 0.5f, 2f, GEN_LARGE).setMaxHeight(10).build(),
            new Tree.Builder(CHESTNUT, 125f, 4000f, 3f, 24f, 0f, 2f, GEN_NORMAL).build(),
            new Tree.Builder(DOUGLAS_FIR, 500f, 4000f, 1f, 14f, 0f, 4f, GEN_TALL).setMaxHeight(14).build(),
            new Tree.Builder(HICKORY, 125f, 4000f, 4f, 28f, 0f, 4f, GEN_TALL).build(),
            new Tree.Builder(MAPLE, 125f, 4000f, 3f, 20f, 0f, 4f, GEN_NORMAL).build(),
            new Tree.Builder(OAK, 250f, 1000f, 5f, 25f, 0.25f, 8f, GEN_TALL).setMaxHeight(14).setGrowthTime(10f).build(),
            new Tree.Builder(PALM, 1000f, 4000f, 12f, 50f, 2f, 8f, GEN_TROPICAL).build(),
            new Tree.Builder(PINE, 125f, 4000f, -15f, 24f, 0.25f, 8f, GEN_CONIFER).setIsConifer().build(),
            new Tree.Builder(ROSEWOOD, 500f, 4000f, 8f, 18f, 0f, 2f, GEN_LARGE).setMaxHeight(10).build(),
            new Tree.Builder(SEQUOIA, 1000f, 4000f, 10f, 16f, 0f, 1f, GEN_SEQUOIA).setMaxGrowthRadius(3).setMaxDecayDistance(8).setIsConifer().setGrowthTime(22f).build(),
            new Tree.Builder(SPRUCE, 125f, 4000f, -5f, 24f, 0f, 4f, GEN_CONIFER).setIsConifer().build(),
            new Tree.Builder(SYCAMORE, 250f, 4000f, 6f, 30f, 0f, 4f, GEN_NORMAL).build(),
            new Tree.Builder(WHITE_CEDAR, 125f, 4000f, -5f, 24f, 0f, 8f, GEN_LARGE).setMaxHeight(10).build(),
            new Tree.Builder(WILLOW, 2000f, 4000f, 10f, 30f, 0f, 1f, GEN_WILLOW).build(),
            new Tree.Builder(KAPOK, 1000f, 4000f, 30f, 50f, 0f, 4f, GEN_KAPOK).setMaxDecayDistance(6).setMaxGrowthRadius(3).setMaxHeight(24).setGrowthTime(18f).build()
        );
    }
}

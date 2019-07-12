/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.entity.animal.*;
import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class EntitiesTFC
{
    private static int id = 1; // don't use id 0, it's easier to debug if something goes wrong

    public static void preInit()
    {
        register("sitblock", EntitySeatOn.class);
        register("falling_block", EntityFallingBlockTFC.class);
        register("thrown_javelin", EntityThrownJavelin.class);
        registerLiving("sheeptfc", EntitySheepTFC.class, 0xFFFFFF, 0xFF6347);
        registerLiving("cowtfc", EntityCowTFC.class, 0xA52A2A, 0xFFFFFF);
        registerLiving("beartfc", EntityBearTFC.class, 0x557755, 0xF1FFF1);
        registerLiving("chickentfc", EntityChickenTFC.class, 0x557755, 0xFFF91F);
        registerLiving("pheasanttfc", EntityPheasantTFC.class, 0xAA7722, 0xF81FFA);
        registerLiving("deertfc", EntityDeerTFC.class, 0x55FF55, 0x5FFAAF);
        registerLiving("pigtfc", EntityPigTFC.class, 0x5577FF, 0xFFFA90);
    }

    private static void register(String name, Class<? extends Entity> cls)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, name), cls, name, id++, TerraFirmaCraft.getInstance(), 160, 20, true);
    }

    private static void registerLiving(String name, Class<? extends Entity> cls, int eggPrimaryColor, int eggSecondaryColor)
    {
        //Register entity and create a spawn egg for creative
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, name), cls, name, id++, TerraFirmaCraft.getInstance(), 80, 3, true, eggPrimaryColor, eggSecondaryColor);
    }
}

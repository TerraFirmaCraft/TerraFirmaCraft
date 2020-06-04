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

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class EntitiesTFC
{
    private static int id = 1; // don't use id 0, it's easier to debug if something goes wrong

    public static void preInit()
    {
        register("sitblock", EntitySeatOn.class);
        register("falling_block", EntityFallingBlockTFC.class);
        register("thrown_javelin", EntityThrownJavelin.class);
        register("boat", EntityBoatTFC.class);
        registerLiving("sheeptfc", EntitySheepTFC.class, 0xFFFFFF, 0xFF6347);
        registerLiving("cowtfc", EntityCowTFC.class, 0xA52A2A, 0xFFFFFF);
        registerLiving("beartfc", EntityBearTFC.class, 0xB22222, 0xDEB887);
        registerLiving("chickentfc", EntityChickenTFC.class, 0x557755, 0xFFF91F);
        registerLiving("pheasanttfc", EntityPheasantTFC.class, 0x5577FF, 0xFFFA90);
        registerLiving("deertfc", EntityDeerTFC.class, 0x55FF55, 0x5FFAAF);
        registerLiving("pigtfc", EntityPigTFC.class, 0xAA7722, 0xFFEBCD);
        registerLiving("wolftfc", EntityWolfTFC.class, 0xB0ACAC, 0x796555);
        registerLiving("rabbittfc", EntityRabbitTFC.class, 0x885040, 0x462612);
        registerLiving("horsetfc", EntityHorseTFC.class, 0xA5886B, 0xABA400);
        registerLiving("donkeytfc", EntityDonkeyTFC.class, 0x493C32, 0x756659);
        registerLiving("muletfc", EntityMuleTFC.class, 0x180200, 0x482D1A);
        registerLiving("polarbeartfc", EntityPolarBearTFC.class, 0xF1FFF1, 0xA0A0A0);
        registerLiving("parrottfc", EntityParrotTFC.class, 0x885040, 0xB0ACAC);
        registerLiving("llamatfc", EntityLlamaTFC.class, 0xA52A2A, 0xAA7722);
        registerLiving("ocelottfc", EntityOcelotTFC.class, 0x3527FA, 0x7F23A0);
        registerLiving("panthertfc", EntityPantherTFC.class, 0x000066, 0x000000);
        registerLiving("ducktfc", EntityDuckTFC.class, 0xFFF91F, 0x462612);
        registerLiving("alpacatfc", EntityAlpacaTFC.class, 0x00CC66, 0x006633);
        registerLiving("goattfc", EntityGoatTFC.class, 0xA0A0A0, 0x404040);
        registerLiving("sabertoothtfc", EntitySaberToothTFC.class, 0xFF8000, 0xFFD700);
        registerLiving("cameltfc", EntityCamelTFC.class, 0xA5886B, 0x006633);
        registerLiving("liontfc", EntityLionTFC.class, 0xDAA520, 0xA0522D);
        registerLiving("hyenatfc", EntityHyenaTFC.class, 0x666600, 0x331900);
        registerLiving("direwolftfc", EntityDireWolfTFC.class, 0x666600, 0x331900);
        registerLiving("haretfc", EntityHareTFC.class, 0x666600, 0x331900);
        registerLiving("hogtfc", EntityHogTFC.class, 0x666600, 0x331900);
        registerLiving("zebutfc", EntityZebuTFC.class, 0x666600, 0x331900);
        registerLiving("sealiontfc", EntitySeaLionTFC.class, 0x666600, 0x331900);
        registerLiving("gazellefc", EntityGazelleTFC.class, 0x666600, 0x331900);
        registerLiving("wildebeestfc", EntityWildebeestTFC.class, 0x666600, 0x331900);
        registerLiving("quailtfc", EntityQuailTFC.class, 0x666600, 0x331900);
        registerLiving("grousetfc", EntityGrouseTFC.class, 0x666600, 0x331900);
        registerLiving("mongoosetfc", EntityMongooseTFC.class, 0x666600, 0x331900);
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

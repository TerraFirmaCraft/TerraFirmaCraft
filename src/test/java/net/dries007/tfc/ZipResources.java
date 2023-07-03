/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

// Use an event bus subscriber here, because this is the test source set, and we don't want this compiled with the main mod.
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TerraFirmaCraft.MOD_ID)
public final class ZipResources
{
    @SubscribeEvent
    public static void onPackFinderEvent(AddPackFindersEvent event)
    {
        final @Nullable String env = System.getProperty("tfc.zippedResources");

        TerraFirmaCraft.LOGGER.info("Checking zip resource support... {}", env);
        if (env == null)
        {
            return;
        }

        final IModFile modFile = ModList.get().getModFileById(TerraFirmaCraft.MOD_ID).getFile();
        final Path resourcePath = modFile.getFilePath();
        final Path dataZip = resourcePath.resolve("data_zipped.zip");
        final Path assetZip = resourcePath.resolve("assets_zipped.zip");

        if (!Files.exists(dataZip) || !Files.exists(assetZip))
        {
            TerraFirmaCraft.LOGGER.error("No override datapack or resource pack found.");
        }
        else
        {
            if (event.getPackType() == PackType.SERVER_DATA)
            {
                TerraFirmaCraft.LOGGER.info("Injecting TFC override datapack");
                event.addRepositorySource((consumer) -> {
                    consumer.accept(Pack.readMetaAndCreate("tfc_data", Component.literal("TFC Data"), true, s -> new FilePackResources(s, dataZip.toFile(), false), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN));
                });
            }
            else
            {
                TerraFirmaCraft.LOGGER.info("Injecting TFC override resource pack");
                event.addRepositorySource((consumer) -> {
                    consumer.accept(Pack.readMetaAndCreate("tfc_assets", Component.literal("TFC Assets"), true, s -> new FilePackResources(s, assetZip.toFile(), false), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN));
                });
            }
        }
    }
}

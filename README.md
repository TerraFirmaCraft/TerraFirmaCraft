# TerraFirmaCraft: The Next Generation
*TFC: TNG*

This project is the official port of TerraFirmaCraft (the original, also called classic) to Minecraft 1.12 and beyond.

You can find the mod on [Curseforge](https://www.curseforge.com/minecraft/mc-mods/tfcraft), or stay updated by joining our [Discord](https://invite.gg/terrafirmacraft), and see documentation on the [Wiki](https://tng.terrafirmacraft.com/Main_Page).

## Goal

- The ultimate goal of this project is to be **the** TerraFirmaCraft for 1.12+, not a mere partial imitation.
- The idea is to stay faithful to the original where possible, but also improve the various systems and allow for better cross-mod compatibility and integration.

## Contributing

 - Firstly, join our [Discord](https://invite.gg/terrafirmacraft), as it's where all discussion surrounding development, tasks, and decisions happens.
 - As per the Github terms of service, you grant us the right to use your contribution under the same license as this project.
 - In addition, we request that you give us the right to change the license in the future.
 - Import & use the project's Code Style. (Recommend using Intellij as that's what our code style xml is based on)
- Before creating a Pull Request, run `gradlew updateLicenses`. This will apply the correct license header to all project files.
- This project uses [Mappificator](https://github.com/alcatrazEscapee/mappificator) for its mappings. This is a tool based on the official mappings provided by Mojang. For information on how to set that up, see the project's README.md, it should detail how to create a customized mapping set and publish it to your maven local for use by FG. (or the comment in [build.gradle](build.gradle))
- We use python for data and asset generation, among other things. In order to run the generation scripts (all found in `/resources`), you will need to install the python modules `mcresources`, `pillow`, and `nbtlib`.

## License / Copyright / Credit

Primarily Copyright (c) Dries007, AlcatrazEscapee 2018 - ...

For a full list of contributors, see the git commit log.

### License

Please do read the full license. It's less than 300 lines of 80 characters long.

Licensed under the EUPL, Version 1.2.
You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
It is also included in [LICENSE.txt](LICENSE.txt)

### Credit

Based on original work by Robert "Bioxx" Anthony, Amanda "Kittychanley" Halek and others.

Parts of this project are edited source code from the original TerraFirmaCraft for 1.7.10 mod. They are used under with permission from the original author (Bioxx).

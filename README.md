# TerraFirmaCraft: The Next Generation
*TFC: TNG*

![Build and Release 1.12.x](https://github.com/TerraFirmaCraft/TerraFirmaCraft/workflows/Build%20and%20Release%201.12.x/badge.svg?branch=1.12.x)

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
 - Add the following copyright header to all Java source files:

```java
/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */
```
Hint: Setup IntelliJ to do this automatically with a copyright profile applied to a shared scope matching pattern `net.dries007.tfc..*`.


#### Optional

+ To enable debug logging, set your run config to include this argument: `-Dlog4j.configurationFile=../debug_log4j2_client.xml` or `_server`.

## Addons

We've tried to make systems extensible to addons wherever possible. If there's specific compatibility hooks or changes you'd like to see, let us know on discord.

That said, we are aware with some large structural issues surrounding the 1.12 code base, and are looking to improve those moving forward (i.e. 1.14+), to better allow addons to use TFC base classes.

## License / Copyright / Credit

Primarily Copyright (c) Dries007, AlcatrazEscapee 2018 - ...

For a full list of contributors, see the git commit log.

### License

Please do read the full license. It's less than 300 lines of 80 characters long.

This project is under the European Union Public Licence v1.2 (**EUPL v. 1.2**). Full text of the license is available in [License.txt](LICENSE.txt).

Textures and other art assets are made available under Creative Commons Attribution Share Alike 4.0 International (**CC-BY-SA-4.0**)

### Credit

Based on original work by Robert "Bioxx" Anthony, Amanda "Kittychanley" Halek and others.

Parts of this project (mostly worldgen) are edited source code from the original TerraFirmaCraft for 1.7.10 mod. They are used under with permission from the original author (Bioxx).

# TerraFirmaCraft

Work in progress.

## Goal

The goal of this project is to be **the** TerraFirmaCraft for 1.12.2, not a mire partial imitation.

## Contributing

+ As per the Github terms of service, you grant us the right to use your contribution
  under the same license as this project.
+ In addition we request that you give use the right to changed the license in the future.
  Your pull request may not be accepted if you don't.

### Required

+ Import & use the project's Code Style.
+ Add the proper copyright header to new files. (See below.)
+ Before you submit a PR:
    + Run the `generateResources.py` script.
    + Reformat & Rearrange your code. (IntelliJ can do this for you.)

#### Copyright header
This header should be applied to all java source files.
Other files will be assessed on a case by case basis.
```java
/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */
```
Hint: Setup IntelliJ to do this automatically with a copyright profile applied 
to a shared scope matching pattern `net.dries007.tfc..*`.

### Optional

+ To enable debug logging, set your run config to include this argument: `-Dlog4j.configurationFile=../debug_log4j2_client.xml` or `_server`.
+ To auto-run the python script before every launch:
    1. Add a python run configuration (named `generateResources`) that runs the script with a python 3 interpreter with the root of the project set as run directory.
    2. In the (java) `run client` run config, add a 'Before launch' step of type `Run another configuration` and select `generateResources` there.
    3. Move the new `run 'Python 'generateResources''` task to before the `Build` step.
    4. Done. Whenever you launch the client, it will first run the python script now. You'll see it appear in the run tab. 

## License / Copyright / Credit

Primarily Copyright (c) Dries007 2018 - ...

For a full list of contributors, see the git commit log.

### License

Please do read the full license. It's less than 300 lines of 80 characters long.

Licensed under **EUPL v. 1.2** with additional provisions and clarifications, as specified in [LICENSE.txt](LICENSE.txt).

### Credit

Based on original work by Robert "Bioxx" Anthony, Amanda "Kittychanley" Halek and others.

Parts of this project (mostly worldgen) are edited source code from the original TerraFirmaCraft for 1.7.10 mod.
They are used under a different license with permission from the original author (Bioxx).

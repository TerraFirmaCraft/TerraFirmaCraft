# TerraFirmaCraft

Work in progress.

## Goal

The goal of this project is to be **the** TerraFirmaCraft for 1.12.2, not a mire partial imitation.

## Contributing

### Required

- Import the project's Code Style.

Before you submit a PR:

- Run the `generateResources.py` script.
- Reformat & Rearrange your code. (IntelliJ can do this for you.)

### Optional

- To enable debug logging, set your run config to include this argument: `-Dlog4j.configurationFile=../debug_log4j2_client.xml` or `_server`.
- To auto-run the python script before every launch:
    1. Add a python run configuration (named `generateResources`) that runs the script with a python 3 interpreter with the root of the project set as run directory.
    2. In the (java) `run client` run config, add a 'Before launch' step of type `Run another configuration` and select `generateResources` there.
    3. Move the new `run 'Python 'generateResources''` task to before the `Build` step.
    4. Done. Whenever you launch the client, it will first run the python script now. You'll see it appear in the run tab. 

## License / Copyright / Credit

todo : Choose a license

Parts of this project are mostly edited source code from the original TerraFirmaCraft for 1.7.10 mod.

Based on original work by Robert "Bioxx" Anthony, Amanda "Kittychanley" Halek and others.

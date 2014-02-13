OpenBlocks
=========

An open source random collection of blocks and miscellaneous cool stuff

Current Version
-

**[v1.2.4](https://dl.dropboxusercontent.com/u/4295615/OpenBlocks-1.2.4.jar) for Minecraft 1.6.x**
You'll also need [OpenModsLib](https://dl.dropboxusercontent.com/u/4295615/OpenModsLib-0.1.jar).

For stable release downloads, click [here](http://www.openmods.info/openblocks/downloads).  
For snapshot versions, check the [Jenkins](http://www.openmods.info:8080/).

FREQUENTLY ASKED QUESTIONS:
-----------
* "I dropped OpenBlocks into the mod folder and I **can't get it to work**!"
  * OpenModsLib is required for versions past v.1.2.2.
* "I want to **disable** feature X!"
  * Setting any block or item's ID to 0 will disable it.
* "How come my **elevators don't always work?**"
  * Recently, we made a change to the way elevators work-- they now require XP. You can disable this change in the configs.
* "About these automatic anvils and enchantment t--"
  * They require bookshelves, just like their mundane counterparts.
* "How can I **contact** the OpenMods team?"
  * We're often on EsperNet IRC at #OpenMods, but you can also open up a new issue on the GitHub or send a message to us on reddit.


What does it contain?
-----------
**In (mostly) chronological order:**

* A combination ladder and trapdoor, also known as the **Jaded Ladder**
* A creative-only **healer** block that slowly replenishes your health and food when you stand on it.
* A **guide block** to assist in various constructions
* Colorable **elevator blocks** to quickly travel between floors. Requires XP by default.
* **Light boxes** to display your maps on the wall, ceiling or floor
* Archery **targets** for a shooting range
* ~~**Torch arrows!**~~ *No longer implemented*
* **Player graves**, safekeeping for your last death (assuming you’re capable of regeneration)
* Colored **flags** for various purposes
* Liquid **tanks**-- portable, practical, and pliable any-size liquid containment
* Hang **gliders**!
* Random **trophies** that may or may not do odd things when right-clicked
* **Bear traps**-- like venus fly traps, but snappier
* **Luggage**, the traveling sentient chest
* **Sprinklers**, to accelerate the growth of crops and other growables // m1.5.2, v1.0.3
* **Item cannons** to shoot items around for transport
* **Vaccum hoppers**-- like regular hoppers, but not limited to the top and sucks up XP. Compatible with BuildCraft!
* **Sonic goggles**-- echolocation for Steves
* **Sponges** for getting rid of liquids fast and effectively
* The Redstone Configurable Pulse Lightweight Mega Touch Sensor, also known as the **BIG BUTTON**
* Blocks drawn by **pencils** and **crayons** that are only seen by the imaginative eye
* **Fans**-- the power of an industrial fan in the size of a desk fan
* A wearable **crane** for picking up blocks and entities
* **XP Bottler**-- should be self-explanatory
* **Magnet turtles**-- turtles don't have CRT screens, so we should be fine
* **Village highlighter**-- shows the village borders and where their golem guardians spawn
* The **Slimalizer**, which detects slime chunks
* **Paths**, an alternative to gravel roads // m1.6.x, v1.2.0
* The **Block Breaker** and **Block Placer** are back! A perfect replacement for the now-dormant RedPower mod
* **XP Drain**-- stand on one attached to a tank, and your XP will be drained and turned into a liquid state
* The **Auto Anvil** and **Auto Enchantment Table**, imported fresh from OpenXP. Uses liquid XP to perform their function automatically.
* The **Sleeping Bag**. Finally, a way to sleep on the go without resetting your spawn point!
* The **Rope Ladder**-- a magic ladder that doesn’t need support to hold itself up and adjusts its own size to match the ground. Single use.
* The **Donation Station**-- tells you which mod a block or item comes from, and lets you support the modder that created it
* A painting/staining system for blocks, including: // m1.6.x, v.1.2.2
  * A **Paint Mixer**-- makes paint using dyes and milk. Millions of colors possible. 
  * A paint **brush** to paint white **canvas**es and other blocks.
  * **Stencils** that are made using the **Drawing Table** to paint specific areas of blocks
  * A **Squeegee** that liquefies paint to clean it off of blocks
* Listen to music among other things with the **radio**!
* Exciting graphics and much more to come.

Compiling
--------------
We will try keep the building instructions as up-to-date as possible, but things may change without notice.

### Windows
You will require [msysgit](http://code.google.com/p/msysgit/downloads/list) or git installed with cygwin. Alternatively I would suggest installing [GitHub for Windows](http://windows.github.com/) and checking out the repo with that.
You will also require [Apache Ant](http://ant.apache.org/bindownload.cgi). In Windows you will want to download the ant zip and extract the contents to somewhere like C:\Ant. Then add the bin folder inside ant to your environment path.

#### Check out the repo
Use Github for Windows or use a command prompt/powershell with git binaries in the Environment path.
```git clone https://github.com/OpenMods/OpenBlocks.git```
#### Change directory
```cd OpenBlocks```
#### Fetch dependencies (OpenModsLib etc.)
```
git submodule init
git submodule update
```
#### Run ant build
```ant ```

The resulting file should be in the `build/dist` folder.

### Linux or OSX
Much the same as Windows, open a terminal window where you wish to checkout the repo and type
```
git clone https://github.com/OpenMods/OpenBlocks.git
cd OpenBlocks
git submodule init
git submodule update
```

In **OSX**, git is typically supplied. Otherwise it can be installed through the apps thingy that OSX has (Obviously I know very little about OSX but you want the dev tools stuff). 

The linux git can be fetched from any package manager. If you're on a Debian based machine (This includes **Ubuntu**) you likely have aptitude, so the command would be ```sudo apt-get install git```. 

If you're running a RedHat based system, such as **CentOS** or **Fedora** (or some other Distro I've never heard of), then you might have the yum package manager ```sudo yum install git-core```. In the case that this does not work through yum, you might not have the packages added to yum to be able to find git. I wont go in to these here, but if you have issues feel free to contact NeverCast in #OpenMods on irc.esper.net.
#### Then run ant

Ant can be downloaded from the same place as the Windows Ant, as it's just a Java application. I assume that if you're using Linux, you're proficient enough to work out how to get that working, and if you're developing under OSX I assume you can work it out as you're brave enough to be developing in OSX :P

Again feel free to come in to IRC for assistance.

Once you have ant, just run.

```ant ```

The mod jar will be in the bin folder after the completion of the build process.

Contact
-
Feel free to chat with us in #OpenMods on *irc.esper.net*

License
-

OpenBlocks is open source, please check the Licence.txt for more information and the licences of individual components of this mod.

    

WorldInventories
=======================

Provided with no guarantee whatsoever. I've tested it a lot on my server and the code's here if you want to check it over.

This plugin lets you specify 'groups' for inventories. Useful, for example, on a server with both a Creative and a Survival world. Armour is also group specific.

Important: If the world isn't listed in the configuration the plugin will assume it to be in a 'default' group. For example, if you have worlds "creative", "survival" and "creative2", with "survival" having it's own inventory, you may teleport between the creatives with no change in inventory. Your inventory will change only if you go in to a new group.

Simply put WorldInventories in the Bukkit plugins folder, run it once, edit the config file it creates. An example is included so it should be self-explanatory.

The first time you teleport in to a new world group it will wipe the items you had! There's not really a better way of doing this.
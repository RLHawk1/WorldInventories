package me.drayshak.WorldInventories;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class WorldInventories extends JavaPlugin
{
    public static final Logger log = Logger.getLogger("Minecraft");
    protected static Configuration config;
    public static PluginManager pluginManager = null;
    public static Server bukkitServer = null;
    public static ArrayList<Group> groups = null;
    private final WIPlayerListener playerListener = new WIPlayerListener(this);
    private final WIEntityListener entityListener = new WIEntityListener(this);
    private final WIWorldListener worldListener = new WIWorldListener(this);
    public static boolean doNotifications = true;
    public static boolean doMultiInvImport = false;
    public static boolean doStats = false;
    
    public WIPlayerInventory getPlayerInventory(Player player)
    {
        return new WIPlayerInventory(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }
    
    public void setPlayerInventory(Player player, WIPlayerInventory playerInventory)
    {
        if(playerInventory != null)
        {
            player.getInventory().setContents(playerInventory.getItems());
            player.getInventory().setArmorContents(playerInventory.getArmour());
        }
    }
    
    public void setPlayerStats(Player player, WIPlayerStats playerstats)
    {
        // Never kill a player - must be a bug if it was 0
        player.setHealth(Math.max(playerstats.getHealth(), 1));
        player.setFoodLevel(playerstats.getFoodLevel());
        player.setExhaustion(playerstats.getExhaustion());
        player.setSaturation(playerstats.getSaturation());
    }
    
    public void savePlayerInventory(String player, Group group, WIPlayerInventory toStore)
    {        
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;
        
        if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player + ".inventory";
        
        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(toStore);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save inventory for player: " + player + ": " + e.getMessage());
        }
    }
    
    public WIPlayerInventory loadPlayerInventory(Player player, Group group)
    {
        WIPlayerInventory playerInventory = null;
        
        FileInputStream fIS = null;
        ObjectInputStream obIn = null;
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".inventory";
        
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (WIPlayerInventory) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (FileNotFoundException e)
        {
            WorldInventories.logError("Player " + player.getName() + " will get a new item file on next save (clearing now).");
            player.getInventory().clear();
            ItemStack[] armour = new ItemStack[4];
            for(int i = 0; i < 4; i++)
            {
                armour[i] = new ItemStack(Material.AIR);
            }
            
            player.getInventory().setArmorContents(armour);
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to load inventory for player: " + player.getName() + ", giving empty inventory: " + e.getMessage());
        }
                
        return playerInventory;
    }    
    
    public WIPlayerStats loadPlayerStats(Player player, Group group)
    {
        WIPlayerStats playerstats = null;
        
        FileInputStream fIS = null;
        ObjectInputStream obIn = null;
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".stats";
        
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerstats = (WIPlayerStats) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (FileNotFoundException e)
        {
            WorldInventories.logError("Player " + player.getName() + " will get a new stats file on next save (clearing now).");
            playerstats = new WIPlayerStats(20, 20, 0, 0);
            this.setPlayerStats(player, playerstats);
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to load stats for player: " + player.getName() + ", giving defaults: " + e.getMessage());
        }
                
        return playerstats;
    }    
    
    public void savePlayerStats(Player player, Group group, WIPlayerStats playerstats)
    {
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;
        
        if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".stats";
        
        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(playerstats);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }          
    }
    
    public void savePlayerStats(Player player, Group group)
    {
        WIPlayerStats playerstats = new WIPlayerStats(player.getHealth(), player.getFoodLevel(), player.getExhaustion(), player.getSaturation());
 
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;
        
        if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".stats";
        
        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(playerstats);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }        
    }
    
    public boolean importMultiInvData()
    {
        Plugin pMultiInv = WorldInventories.pluginManager.getPlugin("MultiInv");
        if(pMultiInv == null)
        {
            WorldInventories.logError("Failed to import MultiInv shares - Bukkit couldn't find MultiInv. Make sure it is installed and enabled whilst doing the import, then when successful remove it.");
        }
        
        File MISharesLocation = new File(pMultiInv.getDataFolder(), "Worlds" + File.separator);
        if(!MISharesLocation.exists())
        {
            WorldInventories.logError("Failed to import MultiInv shares - " + MISharesLocation.toString() + " doesn't seem to exist.");
            return false;
        }
        
        File fMIConfig = new File(WorldInventories.pluginManager.getPlugin("MultiInv").getDataFolder(), "shares.yml");
        if(!fMIConfig.exists())
        {
            WorldInventories.logError("Failed to import MultiInv shares - shares file doesn't seem to exist.");
            return false;
        }
        
        Configuration MIConfig = new Configuration(fMIConfig);
        MIConfig.load();
        
        for (String sGroup : MIConfig.getKeys())
        {
            List<String> sWorlds = MIConfig.getStringList(sGroup, null);
            if(sWorlds != null)
            {
                Group group = new Group(sGroup, sWorlds);
                WorldInventories.groups.add(group); 
                config.setProperty("groups." + sGroup, sWorlds);
            }
            else
            {
                WorldInventories.logError("Skipping import of group because it is empty: " + sGroup);
            }
        }                           
        
        config.save();
        
        ArrayList<String> sMIShares = new ArrayList(Arrays.asList(MISharesLocation.list()));
        
        if(sMIShares.size() <= 0)
        {
            WorldInventories.logError("Failed to import MultiInv shares - there weren't any shares found!");
            return false;
        }
        else
        {
            for(int i = 0; i < sMIShares.size(); i++)
            {
                String sWorld = sMIShares.get(i);
                
                File fWorld = new File(MISharesLocation, sWorld);
                if(fWorld.isDirectory() && fWorld.exists())
                {
                    Group group = findFirstGroupForWorld(sWorld);
                    if(group == null)
                    {
                        group = new Group(sWorld, Arrays.asList(sWorld));
                        WorldInventories.groups.add(group); 
                        config.setProperty("groups." + sWorld, Arrays.asList(sWorld));
                        config.save();

                        WorldInventories.logError("A world was found that doesn't belong to any groups! It was saved as its own group. To put it in a group, edit the WorldInventories config.yml: " + sWorld);
                    }                       
                    
                    //List<String> sPlayer = Arrays.asList(fWorld.list());
                    
                    for(File shareFile : fWorld.listFiles())
                    {
                        if(shareFile.getAbsolutePath().endsWith(".yml"))
                        {
                            String sFilename = shareFile.getName();
                            String playerName = sFilename.substring(0, sFilename.length() - 4);
                                
                            Configuration playerConfig = new Configuration(shareFile);
                            playerConfig.load();
                            
                            String sPlayerInventory = playerConfig.getString("survival");
                            WIPlayerInventory playerInventory = MultiInvImportHelper.playerInventoryFromMIString(sPlayerInventory);
                            if(playerInventory == null) sPlayerInventory = playerConfig.getString("creative");
                            if(playerInventory == null)
                            {
                                logError("Failed to load MultiInv data - found player file but failed to convert it: " + playerName);
                            }
                            else
                            {
                                this.savePlayerInventory(playerName, group, playerInventory);
                            }
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    // NetBeans complains about these log lines but message formatting breaks for me
    public static void logStandard(String line)
    {
        log.log(Level.INFO, "[WorldInventories] " + line);
    }
    
    public static void logError(String line)
    {
        log.log(Level.SEVERE, "[WorldInventories] " + line);
    }
    
    public static void logDebug(String line)
    {
        log.log(Level.FINE, "[WorldInventories] " + line);
    }
    
    private void createDefConfigIfNecessary()
    {
        boolean bConfigChanged = false;
        
        List<String> worldgroups = WorldInventories.config.getKeys("groups");
        if(worldgroups == null)
        {
            bConfigChanged = true;
            
            ArrayList<String> examplegroups = new ArrayList<String>();
            examplegroups.add("examplegroupone");
            examplegroups.add("examplegrouptwo");
            
            config.setProperty("groups", examplegroups);
            
            List<String> exampleworlds = Arrays.asList("exampleworldone", "exampleworldtwo");
            
            config.setProperty("groups.exampleworldone", exampleworlds);
            
            List<String> exampleworldstwo = Arrays.asList("exampleworldthree");
            
            config.setProperty("groups.examplegrouptwo", exampleworldstwo);
        }
        
        Boolean btDoNotifications = WorldInventories.config.getBoolean("donotifications", true);
        if(btDoNotifications == null)
        {
            bConfigChanged = true;
            config.setProperty("donotifications", true);
        }
        else doNotifications = btDoNotifications;
     
        Boolean btDoMVImport = WorldInventories.config.getBoolean("domiimport", false);
        if(btDoMVImport == null)
        {
            bConfigChanged = true;
            config.setProperty("domiimport", false);
        }
        else doMultiInvImport = btDoMVImport;    
        
        Boolean btDoStats = WorldInventories.config.getBoolean("dostats", false);
        if(btDoStats == null)
        {
            bConfigChanged = true;
            config.setProperty("dostats", false);
        }
        else doStats = btDoStats;         
        
        if(bConfigChanged) config.save();
    }
    
    private boolean loadConfiguration()
    {
        WorldInventories.groups = new ArrayList<Group>();
        
        List<String> nodes =  WorldInventories.config.getKeys("groups");
        for(String group : nodes)
        {
            List<String> worldnames = WorldInventories.config.getStringList("groups." + group, null);
            if(worldnames != null)
            {
                WorldInventories.groups.add(new Group(group, worldnames));
            }
        }
        
        return true;
    }
 
    public static Group findFirstGroupForWorld(String world)
    {
        for(Group tGroup : WorldInventories.groups)
        {
            for(String tWorld : tGroup.getWorlds())
            {
                if(tWorld.equals(world))
                {
                    return tGroup;
                }                    
            }
        }
        
        return null;
    }    
    
    @Override
    public void onEnable()
    {
        WorldInventories.logStandard("Initialising...");
        
        boolean bInitialised = true;
        
        WorldInventories.bukkitServer = this.getServer();
        WorldInventories.pluginManager = WorldInventories.bukkitServer.getPluginManager();
        
        config = this.getConfiguration();
        config.load();
        
        WorldInventories.logStandard("Loading configuration...");
        this.createDefConfigIfNecessary();
        
        boolean bConfiguration = this.loadConfiguration();
        
        if(!bConfiguration)
        {
            WorldInventories.logError("Failed to load configuration.");
            bInitialised = false;
        }
        else
        {
            WorldInventories.logStandard("Loaded configuration successfully");
        }   
        
        if(bInitialised)
        {
            if(doMultiInvImport)
            {
                boolean bSuccess = this.importMultiInvData();

                

                config.setProperty("domiimport", false);
                config.save();
                
                if(bSuccess)
                {
                    WorldInventories.logStandard("MultiInv data import was a success!");
                }
            }           
            
            //WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_CHANGED_WORLD, playerListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.WORLD_SAVE, worldListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
            
            WorldInventories.logStandard("Initialised successfully!");
            
        }
        else WorldInventories.logError("Failed to initialise.");
        
    }
    
    @Override
    public void onDisable()
    {
        Player[] players = WorldInventories.bukkitServer.getOnlinePlayers();
        for(Player player : players)
        {
            String world = player.getLocation().getWorld().getName();
        
            Group tGroup = findFirstGroupForWorld(world);            
            
            if(tGroup != null)
            {    
                WorldInventories.logStandard("Saving inventory of " + player);
                savePlayerInventory(player.getName(), findFirstGroupForWorld(world), getPlayerInventory(player));
            }
        }
        WorldInventories.logStandard("Plugin disabled");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        String command = cmd.getName();
        
        if(command.equalsIgnoreCase("witoggleplayer"))
        {
            if(args.length == 2)
            {
                boolean bToggle = false;
                
                if(args[0].equalsIgnoreCase("on"))          bToggle = true;
                else if(args[0].equalsIgnoreCase("off"))    bToggle = false;
                else return false;
                
                Player player = WorldInventories.bukkitServer.getPlayerExact(args[1]);
                if(player == null) sender.sendMessage("Player not found!");
                else
                {
                    //TODO: Toggle inventory management for this player
                }
            }
            
            return true;
        }

        return false; 
    }    
}

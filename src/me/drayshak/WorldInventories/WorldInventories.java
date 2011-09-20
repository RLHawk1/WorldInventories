package me.drayshak.WorldInventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class WorldInventories extends JavaPlugin
{
    public static final Logger log = Logger.getLogger("Minecraft");
    protected static Configuration config;
    public static PluginManager pluginManager = null;
    public static Server bukkitServer = null;
    public static ArrayList<Group> groups = null;
    private final WIPlayerListener playerListener = new WIPlayerListener(this);
    
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
            WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
            WorldInventories.pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
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
                WIPlayerListener.savePlayerInventory(player, findFirstGroupForWorld(world), WIPlayerListener.getPlayerInventory(player));
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

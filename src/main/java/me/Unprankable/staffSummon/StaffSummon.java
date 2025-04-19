package me.Unprankable.staffSummon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class StaffSummon extends JavaPlugin implements Listener, TabCompleter {
    private final Map<Player, String> summonReqeusts = new HashMap<>();
    private File ConfigFile;
    private FileConfiguration Config;
    private String version = "1.0";
    private Map<String, String> messages = new HashMap<>();
    //Messages
    //started
    //nopermission
    //requestmessage
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("enabled StaffSummon v" + version + " by Unprankable");
        Config = createConfig();
        String[] values = { "nopermission", "requestsent", "newrequest", "teleporting", "requestaccepted", "requesttaken", "nosummonrequests","nostaffonline" };
        for (String value: values){
            messages.put(value,Config.getString("message." + value));
        }
    }
    public boolean validate(CommandSender sender, Command commnand, String[] args){
        if(commnand.getName().equalsIgnoreCase("staffsummon") && sender.hasPermission("staffsummon.use")){
            if (sender instanceof Player) {
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("get")) {
                        if (args[0].equalsIgnoreCase("accept") && sender.hasPermission(Config.getString("staffpermission"))) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                if (summonReqeusts.containsKey(Bukkit.getPlayer(args[1]))) {
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "player does not have a summon request");
                                    return false;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "player is null");
                                return false;
                            }
                        } else if(args[0].equalsIgnoreCase("request") && sender.hasPermission("staffsummon.use")){
                            return true;
                        } else if(args[0].equalsIgnoreCase("get") && sender.hasPermission("staffsummon.use")){
                            if (args[1].equalsIgnoreCase("requests") || args[1].equalsIgnoreCase("staff") || args[1].equalsIgnoreCase("author")){
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid argument for get " + args[1] + " try requests or staff");
                                return false;
                            }
                        } else {
                            sender.sendMessage(formatting(messages.get("nopermission"),"","",""));
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please choose either accept request or get");
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "expected 2 arguments but got " + args.length);
                    return false;
                }
            } else {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
        } else {
            return false;
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
            Player player = event.getPlayer();
            if (player.hasPermission(Config.getString("staffpermission")))player.sendMessage("There are currently " + summonReqeusts.size() + " staffsummon requests");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        summonReqeusts.remove(event.getPlayer());
    }
    public String formatting(String text,String requester,String accepter,String reason){
        text = text.replace("${requester}",requester);
        text = text.replace("${accepter}",accepter);
        text = text.replace("${reason}",reason);
        text = text.replace("${yellow}",ChatColor.YELLOW.toString());
        text = text.replace("${green}",ChatColor.GREEN.toString());
        text = text.replace("${gray}",ChatColor.GRAY.toString());
        text = text.replace("${red}",ChatColor.RED.toString());
        return text;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (validate(sender,command,args)) {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("request")){
                String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                summonReqeusts.put(player, reason);
                player.sendMessage(formatting(messages.get("requestsent"),player.getName(),"",reason));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission(Config.getString("staffpermission"))) {
                        p.sendMessage(formatting(messages.get("newrequest"),player.getName(),"",reason));
                        p.sendMessage((TextComponent) Component.text("[Teleport]")
                                .color(NamedTextColor.BLUE)
                                .clickEvent(ClickEvent.runCommand("/staffsummon accept " + player.getName())));
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("accept")){
                Player target = Bukkit.getPlayer(args[1]);
                player.sendMessage(formatting(messages.get("teleporting"),target.getName(),player.getName(),""));
                target.sendMessage(formatting(messages.get("requestaccepted"),target.getName(),player.getName(),""));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission(Config.getString("staffpermission")))p.sendMessage(formatting(messages.get("requesttaken"),target.getName(),player.getName(),""));
                }
                player.teleport(target);
                if (Config.getBoolean("spawnlightning"))player.getWorld().strikeLightningEffect(player.getLocation());
                summonReqeusts.remove(target);
                return true;
            } else if(args[0].equalsIgnoreCase("get")){
                if (args[1].equalsIgnoreCase("requests")){
                    if (!player.hasPermission(Config.getString("staffpermission"))){
                        player.sendMessage(formatting(messages.get("nopermission"),"","",""));
                        return true;
                    }
                    if (summonReqeusts.isEmpty()){
                        player.sendMessage(formatting(messages.get("nosummonrequests"),"","",""));
                    } else {
                        for (Player req : summonReqeusts.keySet()) {
                            String item = req.getName() + " | " + summonReqeusts.get(player);
                            player.sendMessage(item);
                        }
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("staff")){
                    boolean staffonline = false;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                        if (onlinePlayer.hasPermission(Config.getString("staffpermission"))){
                            staffonline = true;
                            player.sendMessage(onlinePlayer.getName());
                        }
                    }
                    if(!staffonline)player.sendMessage(formatting(messages.get("nostaffonline"),"","",""));;
                    return true;
                } else if (args[1].equalsIgnoreCase("author")){
                    player.sendMessage("This plugin was made by Unprankable");
                    return true;
                } else {
                    return true;
                }
            }else {
                return true;
            }
        } else {
            return true;
        }
    }
     @Override
     public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
         if (command.getName().equalsIgnoreCase("staffsummon")) {
             if (args.length == 1) {
                 return Arrays.asList("request", "accept","get").stream()
                         .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                         .collect(Collectors.toList());
             } else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
                 return Bukkit.getOnlinePlayers().stream()
                         .map(Player::getName)
                         .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                         .collect(Collectors.toList());
             } else if (args.length == 2 && args[0].equalsIgnoreCase("get")){
                 return Arrays.asList("requests","staff","author").stream()
                         .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                         .collect(Collectors.toList());
             }
         }
         return Arrays.asList("HELPME","PLEASE",args.length+"").stream()
                 .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                 .collect(Collectors.toList());
    }
    public FileConfiguration createConfig() {
        String filename = "config.yml";
        ConfigFile = new File(getDataFolder(), "Config/" + filename);

        if (!ConfigFile.getParentFile().exists()) ConfigFile.getParentFile().mkdirs();
        if (!ConfigFile.exists()) {
            try {
                ConfigFile.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
                config.set("message.nopermission", "${red}You do not have permission to run this command");
                config.set("message.requestsent", "${green}Your request has been sent");
                config.set("message.newrequest", "${yellow}${requester}${green} is requesting staff for ${gray}${reason}");
                config.set("message.teleporting", "${green}Teleporting to ${requester}");
                config.set("message.requestaccepted", "${green}${accepter} is on there way");
                config.set("message.requesttaken", "${yellow}${accepter} accepted ${requester}'s request");
                config.set("message.nosummonrequests", "${gray}There are currently no requests");
                config.set("message.nostaffonline", "${gray}There are no staff online");
                config.set("staffpermission", "staffsummon.staff");
                config.set("spawnlightning", true);
                config.save(ConfigFile);
            } catch (IOException e) {
                getLogger().severe("Failed to create or save " + filename + ": " + e.getMessage());
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
        return config;
    }
    public void setConfigValue(String path, Object value) {
        Config.set(path, value);

        saveConfig();
    }
    public void saveConfig() {
        try {
            Config.save(ConfigFile);
        } catch (IOException e) {
            getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }
    @Override
    public void onDisable() {
        this.getLogger().info("disabled staffsummon");
    }
}
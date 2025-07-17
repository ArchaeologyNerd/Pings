package femboys.are.cute;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Pings extends JavaPlugin implements Listener {

    private String replacementText;
    private Sound sound;
    private float volume;
    private float pitch;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    private void loadConfigValues() {
        replacementText = getConfig().getString("replacement-text", "&8@&b%loop-player%&7");

        try {
            sound = Sound.valueOf(getConfig().getString("sound", "BLOCK_NOTE_BLOCK_PLING"));
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid sound in config, defaulting to BLOCK_NOTE_BLOCK_PLING");
            sound = Sound.BLOCK_NOTE_BLOCK_BELL;
        }

        volume = (float) getConfig().getDouble("volume", 1.0);
        pitch = (float) getConfig().getDouble("pitch", 0.8);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player sender = event.getPlayer();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (message.contains(player.getName())) {
                message = message.replaceAll("(?i)" + player.getName(), ChatColor.translateAlternateColorCodes('&', replacementText.replace("%player%", player.getName())));

                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }

        event.setMessage(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pings") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("pings.reload")) {
                reloadConfig();
                loadConfigValues();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            }
            return true;
        }

        return false;
    }
}

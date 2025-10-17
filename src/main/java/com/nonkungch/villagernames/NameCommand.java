package com.nonkungch.villagernames;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class NameCommand implements CommandExecutor {

    private final VillagerNames plugin;

    public NameCommand(VillagerNames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "--- VillagerNames v2.0 ---");
            sender.sendMessage(ChatColor.YELLOW + "/vn reload" + ChatColor.WHITE + " - รีโหลด config และรายชื่อ");
            sender.sendMessage(ChatColor.YELLOW + "/vn nameall" + ChatColor.WHITE + " - ตั้งชื่อให้ชาวบ้านทั้งหมด (ในโลกของคุณ)");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("villagernames.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "VillagerNames config and name list have been reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("nameall")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }
            if (!sender.hasPermission("villagernames.nameall")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            Player player = (Player) sender;
            World world = player.getWorld();
            int namedCount = 0;

            // ดึงการตั้งค่ามาเตรียมไว้
            ChatColor nameColor = ChatColor.valueOf(plugin.getConfig().getString("name-settings.color", "GOLD").toUpperCase());
            boolean alwaysVisible = plugin.getConfig().getBoolean("name-settings.always-visible", false);

            for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                if (villager.getCustomName() == null) {
                    String randomName = plugin.getNameManager().getUniqueName();
                    villager.setCustomName(nameColor + randomName);
                    villager.setCustomNameVisible(alwaysVisible);
                    namedCount++;
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Successfully named " + namedCount + " villagers in your world.");
            return true;
        }
        return false;
    }
}

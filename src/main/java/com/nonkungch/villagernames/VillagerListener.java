package com.nonkungch.villagernames;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VillagerListener implements Listener {

    private final VillagerNames plugin;
    private final NameManager nameManager;

    public VillagerListener(VillagerNames plugin) {
        this.plugin = plugin;
        this.nameManager = plugin.getNameManager();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.VILLAGER) {
            return;
        }

        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() != null) {
            return;
        }

        // ดึงการตั้งค่าจาก config.yml
        String colorCode = plugin.getConfig().getString("name-settings.color", "GOLD");
        boolean alwaysVisible = plugin.getConfig().getBoolean("name-settings.always-visible", false);

        ChatColor nameColor;
        try {
            nameColor = ChatColor.valueOf(colorCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid color '" + colorCode + "' in config.yml. Defaulting to GOLD.");
            nameColor = ChatColor.GOLD;
        }

        // ดึงชื่อจาก NameManager
        String randomName = nameManager.getUniqueName();

        villager.setCustomName(nameColor + randomName);
        villager.setCustomNameVisible(alwaysVisible);
    }
}

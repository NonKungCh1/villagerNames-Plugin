package com.nonkungch.villagernames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VillagerNames extends JavaPlugin {

    private NameManager nameManager;

    @Override
    public void onEnable() {
        // สร้างและโหลด Config
        saveDefaultConfig();

        // สร้างและโหลด NameManager
        this.nameManager = new NameManager(this);
        this.nameManager.loadNames();

        // ลงทะเบียน Listener และ Command
        getServer().getPluginManager().registerEvents(new VillagerListener(this), this);
        getCommand("villagernames").setExecutor(new NameCommand(this));

        // << ใหม่: สั่งให้เมธอด nameExistingVillagers() ทำงานหลังจากเซิร์ฟเวอร์โหลดเสร็จ 1 tick
        Bukkit.getScheduler().runTaskLater(this, this::nameExistingVillagers, 1L);

        getLogger().info("VillagerNames v2.1 has been enabled!");
    }

    // เมธอดสำหรับ Reload
    public void reload() {
        reloadConfig();
        nameManager.loadNames();
    }

    // ทำให้คลาสอื่นเข้าถึง NameManager ได้
    public NameManager getNameManager() {
        return nameManager;
    }

    // << ใหม่: เมธอดสำหรับไล่ตั้งชื่อชาวบ้านที่ยังไม่มีชื่อทั้งหมดในเซิร์ฟเวอร์
    private void nameExistingVillagers() {
        getLogger().info("Scanning for existing unnamed villagers...");
        int namedCount = 0;

        // ดึงการตั้งค่าสีและ
        String colorCode = getConfig().getString("name-settings.color", "GOLD");
        boolean alwaysVisible = getConfig().getBoolean("name-settings.always-visible", false);
        ChatColor nameColor;
        try {
            nameColor = ChatColor.valueOf(colorCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid color '" + colorCode + "' in config.yml. Defaulting to GOLD.");
            nameColor = ChatColor.GOLD;
        }

        // วนลูปทุก World ที่มีในเซิร์ฟเวอร์
        for (World world : Bukkit.getWorlds()) {
            // วนลูป Entity ทั้งหมดที่เป็น Villager ใน World นั้นๆ
            for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                // ถ้าชาวบ้านตัวนั้นยังไม่มีชื่อ (getCustomName() == null)
                if (villager.getCustomName() == null) {
                    // สุ่มชื่อใหม่และตั้งค่าให้
                    String randomName = nameManager.getUniqueName();
                    villager.setCustomName(nameColor + randomName);
                    villager.setCustomNameVisible(alwaysVisible);
                    namedCount++;
                }
            }
        }

        if (namedCount > 0) {
            getLogger().info("Successfully named " + namedCount + " existing villagers!");
        } else {
            getLogger().info("No existing unnamed villagers found.");
        }
    }
}

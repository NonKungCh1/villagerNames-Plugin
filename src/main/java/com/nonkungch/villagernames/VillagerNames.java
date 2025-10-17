package com.nonkungch.villagernames;

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

        getLogger().info("VillagerNames v2.0 has been enabled!");
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
}

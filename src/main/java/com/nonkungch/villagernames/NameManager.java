package com.nonkungch.villagernames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameManager {

    private final VillagerNames plugin;
    private final List<String> availableNames = new ArrayList<>();
    private final List<String> allNames = new ArrayList<>();

    public NameManager(VillagerNames plugin) {
        this.plugin = plugin;
    }

    public void loadNames() {
        allNames.clear(); // เคลียร์ข้อมูลเก่าก่อนโหลดใหม่

        try (InputStream is = plugin.getResource("first-names.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                plugin.getLogger().severe("first-names.txt not found! The plugin will not work.");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allNames.add(line.trim());
                }
            }
            // เติมชื่อที่พร้อมใช้งานและสับเปลี่ยนลำดับ
            refillAvailableNames();
            plugin.getLogger().info("Successfully loaded " + allNames.size() + " unique names.");

        } catch (IOException | NullPointerException e) {
            plugin.getLogger().severe("Could not load first-names.txt!");
            e.printStackTrace();
        }
    }

    /**
     * ดึงชื่อที่ไม่ซ้ำกันออกมา ถ้าชื่อหมดแล้วจะเติมใหม่แล้วเริ่มสุ่มอีกครั้ง
     * @return ชื่อที่ไม่ซ้ำ (ถ้าเป็นไปได้)
     */
    public String getUniqueName() {
        if (allNames.isEmpty()) {
            return "Villager"; // ชื่อสำรองกรณีไฟล์ว่าง
        }
        
        // ถ้าชื่อที่ยังไม่เคยใช้หมดแล้ว ให้เติมใหม่จากคลังทั้งหมด
        if (availableNames.isEmpty()) {
            refillAvailableNames();
            plugin.getLogger().warning("All unique names have been used. Starting over!");
        }

        // ดึงชื่อแรกสุดออก (เพราะเราสับลำดับไว้แล้ว)
        return availableNames.remove(0);
    }

    /**
     * เติมรายชื่อที่พร้อมใช้งานจากคลังชื่อทั้งหมดและสับเปลี่ยนลำดับ
     */
    private void refillAvailableNames() {
        availableNames.clear();
        availableNames.addAll(allNames);
        Collections.shuffle(availableNames); // สุ่มลำดับชื่อทั้งหมด
    }
}

package com.nekeki.kawaiiparkour;

import org.bukkit.plugin.java.JavaPlugin;

public class KawaiiParkour extends JavaPlugin {

    @Override
    public void onEnable() {
        //Start message
        System.out.println("KawaiiParkour by nekeki enabled. Version: 1.0");
        System.out.println("More info at: https://www.nekeki.com/kawaiiparkour");
        //Register commands
        this.getCommand("parkour").setExecutor(new CommandParkour());
        this.getCommand("kawaiiparkour").setExecutor(new CommandKawaiiParkour());
        //Register Events Listener
        getServer().getPluginManager().registerEvents(new EventsListener(), this);
        //Start storage file
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        TimeManager.initFile();
        //Cleanup scripts
        TimeManager.validWarning();
        TimeManager.removeGhostCourses();
    }

    @Override
    public void onDisable() {
    }

}

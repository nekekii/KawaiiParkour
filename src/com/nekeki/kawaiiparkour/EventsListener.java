package com.nekeki.kawaiiparkour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventsListener implements Listener {


    @EventHandler
    public void onPressurePlate(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.PHYSICAL)) {
            TimeManager.playerSetup(event.getPlayer());
            if(TimeManager.inSetup(event.getPlayer())) {
                //If in parkour setup
                int stage = TimeManager.creationCheckpoint(event.getPlayer());
                if(event.getClickedBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                    if(stage == -1) {
                        TimeManager.setupPoint(event.getPlayer(), "start", event.getClickedBlock().getLocation());
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "You've already logged a start point!");
                    }
                } else if(event.getClickedBlock().getType().equals(Material.STONE_PRESSURE_PLATE)) {
                    if(stage == -1) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You need to log a start (iron) pressure plate first!");
                    } else {
                        TimeManager.setupPoint(event.getPlayer(), "checkpoint", event.getClickedBlock().getLocation());
                    }
                } else if(event.getClickedBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                    if(stage == -1) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You need to log a start (iron) pressure plate first!");
                    } else {
                        TimeManager.setupPoint(event.getPlayer(), "end", event.getClickedBlock().getLocation());
                    }
                }
            } else {
                //If not in parkour setup
                String[] uwu = TimeManager.getPoint(event.getClickedBlock().getLocation(), true);
                event.getPlayer().sendMessage(uwu[0] + " " + uwu[1] + " " + uwu[2]);
                if(event.getClickedBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                    TimeManager.startRun(event.getPlayer(), event.getClickedBlock().getLocation());
                } else if(event.getClickedBlock().getType().equals(Material.STONE_PRESSURE_PLATE)) {
                    TimeManager.checkpointRun(event.getPlayer(), event.getClickedBlock().getLocation());
                } else if(event.getClickedBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                    TimeManager.endRun(event.getPlayer(), event.getClickedBlock().getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        //This deletes a parkour creation if it is still in setup while the player leaves
        if(TimeManager.inSetup(event.getPlayer())) {
            TimeManager.deleteParkour(TimeManager.getSetupName(event.getPlayer()), false);
        }
    }

}

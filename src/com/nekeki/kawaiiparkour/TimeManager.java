package com.nekeki.kawaiiparkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TimeManager {

    private static File file;
    private static FileConfiguration kawaiiFile;

    public static void initFile() {
        //Make sure file works
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("KawaiiParkour").getDataFolder(), "KawaiiStorage");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error initializing storage file.");
            }
        }

        kawaiiFile = YamlConfiguration.loadConfiguration(file);

    }

    public static void startRun(Player player, Location location) {
        //Starts parkour run and saves relevant variables in file.
        String playerID = "player." + player.getUniqueId().toString();
        String[] point = getPoint(location, true);
        if(point[0].equals("true")) {
            if(player.getAllowFlight()) {
                player.sendMessage(ChatColor.RED + "You can't start the parkour with flight mode enabled!");
            } else {
                Date t = new Date();

                if(kawaiiFile.getBoolean(playerID + ".hasStarted") && kawaiiFile.getString(playerID + ".runCourse").equals(point[1])) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Restarted parkour " + ChatColor.LIGHT_PURPLE + point[1] + ChatColor.DARK_PURPLE + "!");
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Started parkour " + ChatColor.LIGHT_PURPLE + point[1] + ChatColor.DARK_PURPLE + "!");
                }

                ConfigurablesExecutor configurablesExecutor = new ConfigurablesExecutor();
                configurablesExecutor.sound(player);

                kawaiiFile.set(playerID + ".startYaw", (double) player.getLocation().getYaw());
                kawaiiFile.set(playerID + ".checkpointYaw", (double) player.getLocation().getYaw());
                kawaiiFile.set(playerID + ".startTime", t.getTime());
                kawaiiFile.set(playerID + ".hasStarted", true);
                kawaiiFile.set(playerID + ".runCourse", point[1]);
                kawaiiFile.set(playerID + ".checkpoint", 0);

                try {
                    kawaiiFile.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving start run data for " + player.getName() + ".");
                }
            }

        }

    }

    public static void checkpointRun(Player player, Location location) {
        //Logs checkpoint and saves
        String playerID = "player." + player.getUniqueId().toString();
        String[] point = getPoint(location, true);
        if(point[0].equals("true")) {
            if(kawaiiFile.getBoolean(playerID + ".hasStarted") && kawaiiFile.getString(playerID + ".runCourse").equals(point[1])) {
                int neededCheckpoint = kawaiiFile.getInt(playerID + ".checkpoint") + 1;
                if(neededCheckpoint == Integer.parseInt(point[2])) {
                    Date t = new Date();
                    long checkpointTime = t.getTime() - kawaiiFile.getLong(playerID + ".startTime");
                    player.sendMessage(ChatColor.DARK_PURPLE + "Reached " + ChatColor.LIGHT_PURPLE + "Checkpoint " + point[2] + ChatColor.DARK_PURPLE + "! Course time: " + ChatColor.LIGHT_PURPLE + readableTime(checkpointTime) + ChatColor.DARK_PURPLE + ".");
                    kawaiiFile.set(playerID + ".checkpoint", Integer.parseInt(point[2]));
                    kawaiiFile.set(playerID + ".checkpointYaw", (double) player.getLocation().getYaw());

                    ConfigurablesExecutor configurablesExecutor = new ConfigurablesExecutor();
                    configurablesExecutor.sound(player);

                    try {
                        kawaiiFile.save(file);
                    } catch (IOException e) {
                        System.out.println("Error saving checkpoint run data for " + player.getName() + ".");
                    }
                } else if(neededCheckpoint > kawaiiFile.getInt("parkour." + point[1] + ".nocheckpoints")) {
                    if(kawaiiFile.getInt(playerID + ".checkpoint") == 0) {
                        player.sendMessage(ChatColor.RED + "Wrong order! This is " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "The Finish" + ChatColor.RED + "! Use " + ChatColor.DARK_RED + "/parkour reset " + ChatColor.RED + "to start over.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Wrong order! This is " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "The Finish" + ChatColor.RED + "! Use " + ChatColor.DARK_RED + "/parkour checkpoint " + ChatColor.RED + "to go to your last checkpoint.");
                    }
                } else if(Integer.parseInt(point[2]) < neededCheckpoint) {
                    if(kawaiiFile.getInt(playerID + ".checkpoint") == 0) {
                        player.sendMessage(ChatColor.RED + "You have already completed " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "Checkpoint " + neededCheckpoint + ChatColor.RED + ". Use " + ChatColor.DARK_RED + "/parkour reset " + ChatColor.RED + "to start over.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You have already completed " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "Checkpoint " + neededCheckpoint + ChatColor.RED + ". Use " + ChatColor.DARK_RED + "/parkour checkpoint " + ChatColor.RED + "to go to your last checkpoint.");
                    }

                }  else {
                    if(kawaiiFile.getInt(playerID + ".checkpoint") == 0) {
                        player.sendMessage(ChatColor.RED + "Wrong order! This is " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "Checkpoint " + neededCheckpoint + ChatColor.RED + "! Use " + ChatColor.DARK_RED + "/parkour reset " + ChatColor.RED + "to start over.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Wrong order! This is " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + "! You need to complete " + ChatColor.DARK_RED + "Checkpoint " + neededCheckpoint + ChatColor.RED + "! Use " + ChatColor.DARK_RED + "/parkour checkpoint " + ChatColor.RED + "to go to your last checkpoint.");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "This is " + ChatColor.DARK_RED + "Checkpoint " + point[2] + ChatColor.RED + " for parkour " + ChatColor.DARK_RED + point[1] + ChatColor.RED + ", which you have not started.");
            }
        }
    }

    public static void endRun(Player player, Location location) {
        //Compares start and end times, save variables for end run, send player end message
        String playerID = "player." + player.getUniqueId().toString();
        String[] point = getPoint(location, true);
        if(kawaiiFile.getBoolean(playerID + ".hasStarted") && point[1].equals(kawaiiFile.getString(playerID + ".runCourse"))) {
           if(kawaiiFile.getInt(playerID + ".checkpoint") == kawaiiFile.getInt("parkour." + point[1] + ".nocheckpoints")) {
               Date t = new Date();
               Long startTime = kawaiiFile.getLong(playerID + ".startTime");
               Long endTime = t.getTime();
               Long totalTime = endTime - startTime;
               if(kawaiiFile.isSet(playerID + ".best." + point[1])) {
                   //If personal best
                   if(kawaiiFile.getLong(playerID + ".best." + point[1]) <= totalTime) {
                       player.sendMessage(ChatColor.DARK_PURPLE + "Completed the parkour " + ChatColor.LIGHT_PURPLE + point[1] + ChatColor.DARK_PURPLE + " in " + ChatColor.LIGHT_PURPLE + readableTime(totalTime) + ChatColor.DARK_PURPLE + ". Personal best: " + ChatColor.LIGHT_PURPLE + readableTime(kawaiiFile.getLong(playerID + ".best." + point[1])));
                   } else {
                       player.sendMessage(ChatColor.DARK_PURPLE + "New personal best for " + ChatColor.LIGHT_PURPLE + point[1] + ChatColor.DARK_PURPLE + "! " + ChatColor.LIGHT_PURPLE + readableTime(totalTime));
                       kawaiiFile.set(playerID + ".best." + point[1], totalTime);
                   }
               } else {
                   player.sendMessage(ChatColor.DARK_PURPLE + "You completed the parkour " + ChatColor.LIGHT_PURPLE + point[1] + ChatColor.DARK_PURPLE + " in " + ChatColor.LIGHT_PURPLE + readableTime(totalTime) + ChatColor.DARK_PURPLE + "!");
                   kawaiiFile.set(playerID + ".best." + point[1], totalTime);
               }

               if(kawaiiFile.isSet("parkour." + point[1] + ".recordTime")) {
                   //If course record
                   if(totalTime < kawaiiFile.getLong("parkour." + point[1] + ".recordTime")) {
                       player.sendMessage(ChatColor.DARK_PURPLE + "New course record!!!");
                       kawaiiFile.set("parkour." + point[1] + ".recordTime", totalTime);
                       kawaiiFile.set("parkour." + point[1] + ".recordId", player.getUniqueId().toString());
                       kawaiiFile.set("parkour." + point[1] + ".recordName", player.getName());
                   }
               } else {
                   player.sendMessage(ChatColor.DARK_PURPLE + "New course record!!!");
                   kawaiiFile.set("parkour." + point[1] + ".recordTime", totalTime);
                   kawaiiFile.set("parkour." + point[1] + ".recordId", player.getUniqueId().toString());
                   kawaiiFile.set("parkour." + point[1] + ".recordName", player.getName());
               }

               ConfigurablesExecutor configurablesExecutor = new ConfigurablesExecutor();
               configurablesExecutor.firework(player);
               configurablesExecutor.sound(player);

               kawaiiFile.set(playerID + ".hasStarted", false);

               try {
                   kawaiiFile.save(file);
               } catch (IOException e) {
                   System.out.println("Error saving end run data for " + player.getName() + ".");
               }

           } else {
               if(kawaiiFile.getInt(playerID + ".checkpoint") == 0) {
                   player.sendMessage(ChatColor.RED + "You have not completed all the checkpoints! Use " + ChatColor.DARK_RED + "/parkour reset " + ChatColor.RED + "to start over.");
               } else {
                   player.sendMessage(ChatColor.RED + "You have not completed all the checkpoints! Use " + ChatColor.DARK_RED + "/parkour checkpoint " + ChatColor.RED + "to go to your last checkpoint.");
               }
           }


        } else if(point[0].equals("true")) {
            player.sendMessage(ChatColor.RED + "You have not started parkour " + ChatColor.DARK_RED + point[1] + ChatColor.RED + " yet.");
        }
    }

    public static void commandCheckpoint (Player player) {
        //This places a player at their most recent checkpoint
        String playerID = "player." + player.getUniqueId().toString();
        if(kawaiiFile.getBoolean(playerID + ".hasStarted")) {
            if(kawaiiFile.getInt(playerID + ".checkpoint") == 0) {
                player.sendMessage(ChatColor.RED + "You haven't completed a checkpoint. Use /parkour reset to start over!");
            } else {
                Location location = kawaiiFile.getLocation("parkour." + kawaiiFile.getString(playerID + ".runCourse") + ".checkpoint" + Integer.toString(kawaiiFile.getInt(playerID + ".checkpoint")));
                Location l = location.clone();
                l.add(0.5, 0, 0.5);
                l.setYaw((float) kawaiiFile.getDouble(playerID + ".checkpointYaw"));
                player.teleport(l);
                player.sendMessage(ChatColor.DARK_PURPLE + "Sent you to " + ChatColor.LIGHT_PURPLE + "Checkpoint " + kawaiiFile.getInt(playerID + ".checkpoint") + ChatColor.DARK_PURPLE + "!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You have not started a parkour yet!");
        }
    }

    public static void commandReset (Player player) {
        //This places a player at a parkour start line
        String playerID = "player." + player.getUniqueId().toString();
        if(kawaiiFile.getBoolean(playerID + ".hasStarted")) {
            cancelRun(player, false);
            Location location = kawaiiFile.getLocation("parkour." + kawaiiFile.getString(playerID + ".runCourse") + ".start");
            Location l = location.clone();
            l.add(0.5, 0, 0.5);
            l.setYaw((float) kawaiiFile.getDouble(playerID + ".startYaw"));
            player.teleport(l);
        } else {
            player.sendMessage(ChatColor.RED + "You have not started a parkour yet!");
        }
    }

    public static void cancelRun (Player player, boolean isFlying) {
        String playerID = "player." + player.getUniqueId().toString();
        boolean save = false;
        if(isFlying) {
            if(kawaiiFile.getBoolean(playerID + ".hasStarted") && !kawaiiFile.getBoolean(playerID + ".creationIntercept")) {
                player.sendMessage(ChatColor.RED + "You can't be in flight mode. Cancelled your parkour run.");
                save = true;
            }
        } else {
            player.sendMessage(ChatColor.DARK_PURPLE + "Cancelled your parkour run and sent you to the start!");
            save = true;
        }

        if(save) {
            kawaiiFile.set(playerID + ".hasStarted", false);
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save cancel run for " + player.getName() + ".");
            }
        }

    }

    public static void parkourSetup(Player player, String name) {
        //This starts the parkour creation capture
        String playerID = "player." + player.getUniqueId().toString();
        if(kawaiiFile.getBoolean(playerID + ".creationIntercept")) {
            player.sendMessage(ChatColor.RED + "You are already in the parkour creation process! Use " + ChatColor.LIGHT_PURPLE + "/parkour cancel " + ChatColor.RED + "if you want to cancel the creation process.");
        } else {
            kawaiiFile.set(playerID + ".creationIntercept", true);
            kawaiiFile.set(playerID + ".creationName", name);
            kawaiiFile.set("parkour." + name + ".nocheckpoints", -1);
            kawaiiFile.set("parkour." + name + ".enabled", false);
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save start creation process data for " + player.getName() + ".");
            }
        }

    }

    public static void cancelSetup(Player player) {
        //this takes someone out of a parkour creation capture and deletes all points already made
        String playerID = "player." + player.getUniqueId().toString();
        if(!kawaiiFile.getBoolean(playerID + ".creationIntercept")) {
            player.sendMessage(ChatColor.RED + "You are not in the parkour creation process.");
        } else {
            kawaiiFile.set(playerID + ".creationIntercept", false);
            deleteParkour(kawaiiFile.getString(playerID + ".creationName"), false);
            player.sendMessage(ChatColor.DARK_PURPLE + "Cancelled the parkour creation process.");
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save end creation process data for " + player.getName() + ".");
            }
        }
    }

    public static void setupPoint(Player player, String type, Location location) {
        //this setups a point in the parkour
        String playerID = "player." + player.getUniqueId().toString();
        String parkourID = "parkour." + kawaiiFile.getString(playerID + ".creationName");
        String[] point = getPoint(location, false);
        player.sendMessage(point[0] + " " + point[1] + " " + point[2]);
        boolean save = false;
        if(type.equals("start")) {
            if(point[0].equals("false")) {
                kawaiiFile.set(parkourID + ".start", location);
                kawaiiFile.set(parkourID + ".nocheckpoints", 0);
                player.sendMessage(ChatColor.DARK_PURPLE + "Set start point!");
                save = true;
            } else {
                player.sendMessage(ChatColor.RED + "This is already a start point!");
            }
        } else if(type.equals("checkpoint")) {
            if(point[0].equals("false")) {
                int nocheckpoints = kawaiiFile.getInt(parkourID + ".nocheckpoints");
                nocheckpoints++;
                kawaiiFile.set(parkourID + ".checkpoint" + nocheckpoints, location);
                kawaiiFile.set(parkourID + ".nocheckpoints", nocheckpoints);
                player.sendMessage(ChatColor.DARK_PURPLE + "Set " + ChatColor.LIGHT_PURPLE + "Checkpoint " + nocheckpoints + ChatColor.DARK_PURPLE + "!");
                save = true;
            } else {
                player.sendMessage(ChatColor.RED + "This is already a checkpoint!");
            }
        } else if(type.equals("end")) {
            if(point[0].equals("false")) {
                kawaiiFile.set(parkourID + ".end", location);
                kawaiiFile.set(playerID + ".creationIntercept", false);
                kawaiiFile.set(parkourID + ".enabled", true);
                player.sendMessage(ChatColor.DARK_PURPLE + "Set end point and finished making " + ChatColor.LIGHT_PURPLE + kawaiiFile.getString(playerID  + ".creationName") + ChatColor.DARK_PURPLE + ".");
                save = true;
            } else {
                player.sendMessage(ChatColor.RED + "This is already a finish point!");
            }
        }
        if(save) {
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save parkour point.");
            }
        }

    }

    public static void deleteParkour(String courseName, boolean fromConsole) {
        //this deletes a parkour
        boolean cont = false;
        if(fromConsole) {
            Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
            for(String x : parkourKeys) {
                if(x.equals(courseName)){
                    cont = true;
                }
            }
        } else {
            cont = true;
        }

        if(cont) {
            kawaiiFile.set("parkour." + courseName, null);
            deleteAllTimes(courseName);
            if(fromConsole) {
                System.out.println("Deleted parkour " + courseName + ".");
            }
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save deletion of " + courseName + ".");
            }
        } else {
            System.out.println("That parkour doesn't exist! This is case sensitive.");
        }

    }

    public static boolean preDeleteParkour(String courseName, Player player) {
        //this captures the parkour to be deleted for further confirmation
        boolean exists = false;
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        for(String x : parkourKeys) {
            if(x.equals(courseName)){
                exists = true;
            }
        }

        if(exists) {
            String playerID = "player." + player.getUniqueId().toString();
            kawaiiFile.set(playerID + ".delete", courseName);
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save deletion capture.");
            }
            return true;
        } else {
            return false;
        }

    }

    public static String getToBeDeleted(Player player) {
        //this gets the course in the to be deleted capture
        String playerID = "player." + player.getUniqueId().toString();
        return kawaiiFile.getString(playerID + ".delete");
    }

    public static void resetToBeDeleted(Player player) {
        //this resets the to be deleted capture
        String playerID = "player." + player.getUniqueId().toString();
        kawaiiFile.set(playerID + ".delete", "null");
        try {
            kawaiiFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to reset deletion capture.");
        }
    }

    public static void deleteAllTimes(String course) {
        Set<String> playerKeys = kawaiiFile.getConfigurationSection("player").getKeys(false);
        for(String x : playerKeys) {
            kawaiiFile.set("player." + x + ".best." + course, null);
        }

        try {
            kawaiiFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to delete all course times.");
        }
        deleteRecordTime(course);
    }

    public static void deleteRecordTime(String course) {
        kawaiiFile.set("parkour." + course + ".recordTime", null);
        kawaiiFile.set("parkour." + course + ".recordId", null);
        kawaiiFile.set("parkour." + course + ".recordName", null);

        try {
            kawaiiFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to delete record course time.");
        }
    }

    public static void playerSetup(Player player) {
        //Sets up default values for player in file if player is not already in file.
        String playerID = "player." + player.getUniqueId().toString();
        Date t = new Date();
        if(!kawaiiFile.contains(playerID)) {
            kawaiiFile.set(playerID + ".name", player.getName());
            kawaiiFile.set(playerID + ".startTime", t.getTime());
            kawaiiFile.set(playerID + ".hasStarted", false);
            kawaiiFile.set(playerID + ".runCourse", "null");
            kawaiiFile.set(playerID + ".checkpoint", 0);
            kawaiiFile.set(playerID + ".creationIntercept", false);
            kawaiiFile.set(playerID + ".creationName", "null");
            kawaiiFile.set(playerID + ".delete", "null");
            float yaw = 0;
            kawaiiFile.set(playerID + ".checkpointYaw", (double) yaw);
            kawaiiFile.set(playerID + ".startYaw", (double) yaw);
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to save player initialization data for " + player.getName() + ".");
            }
        } else if(!kawaiiFile.getString(playerID + ".name").equals(player.getName())) {
            kawaiiFile.set(playerID + ".name", player.getName());
            try {
                kawaiiFile.save(file);
            } catch (IOException e) {
                System.out.println("Failed to update name change data for " + player.getName() + ".");
            }
        }

    }

    public static boolean inSetup(Player player) {
        //this returns if the player is in the parkour course setup phase
        return kawaiiFile.getBoolean("player." + player.getUniqueId().toString() + ".creationIntercept");
    }

    public static String getSetupName(Player player) {
        //this returns the parkour course in setup
        String playerID = "player." + player.getUniqueId().toString();
        return kawaiiFile.getString(playerID + ".creationName");
    }

    public static String[] getPoint(Location location, boolean isEnabled) {
        //Check if given location is already a point. Optional to only check if course is enabled.
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        String[] locationSet = {"null", "null", "null"};
        for(String x : parkourKeys) {
            if(isEnabled && !kawaiiFile.getBoolean("parkour." + x + ".enabled")) {
                //skip
            } else {
                if(location.equals(kawaiiFile.getLocation("parkour." + x + ".start"))) {
                    locationSet[0] = "true";
                    locationSet[1] = x;
                    locationSet[2] = "start";
                    return locationSet;
                } else if(location.equals(kawaiiFile.getLocation("parkour." + x + ".end"))) {
                    locationSet[0] = "true";
                    locationSet[1] = x;
                    locationSet[2] = "end";
                    return locationSet;
                } else {
                    int l = kawaiiFile.getInt("parkour." + x + ".nocheckpoints");
                    for (int i = 1; i <= l; i++) {
                        if(location.equals(kawaiiFile.getLocation("parkour." + x + ".checkpoint" + i))) {
                            locationSet[0] = "true";
                            locationSet[1] = x;
                            locationSet[2] = String.valueOf(i);
                            return locationSet;
                        }
                    }
                }
            }

        }
        locationSet[0] = "false";
        return locationSet;
    }

    public static int creationCheckpoint(Player player) {
        //this returns the checkpoint phase the player is in the process of creating
        String parkourName = kawaiiFile.getString("player." + player.getUniqueId().toString() + ".creationName");
        return kawaiiFile.getInt("parkour." + parkourName + ".nocheckpoints");
    }

    public static void sendParkourInfo(CommandSender sender, String parkourName) {
        //Sends a sender info about a given parkour course.
        boolean sendMessage = false;
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        for(String x : parkourKeys) {
            if(x.equals(parkourName)){
                sendMessage = true;
            }
        }

        if(sendMessage) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "----- " + ChatColor.LIGHT_PURPLE + parkourName + ChatColor.DARK_PURPLE + " -----");
            sender.sendMessage(ChatColor.DARK_PURPLE + "Number of checkpoints: " + ChatColor.LIGHT_PURPLE + kawaiiFile.getInt("parkour." + parkourName + ".nocheckpoints"));
            if(kawaiiFile.isSet("parkour." + parkourName + ".recordName")) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "Record holder: " + ChatColor.LIGHT_PURPLE + kawaiiFile.getString("parkour." + parkourName + ".recordName"));
                sender.sendMessage(ChatColor.DARK_PURPLE + "Record time: " + ChatColor.LIGHT_PURPLE + readableTime((long) kawaiiFile.getDouble("parkour." + parkourName + ".recordTime")));
            } else {
                sender.sendMessage(ChatColor.DARK_PURPLE + "Record holder: " + ChatColor.LIGHT_PURPLE + "None");
                sender.sendMessage(ChatColor.DARK_PURPLE + "Record time: " + ChatColor.LIGHT_PURPLE + "None");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "That is not a valid parkour! This is case sensitive.");
        }
    }

    public static void sendParkourList(CommandSender sender) {
        //Sends a sender with a list of all parkour courses.
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        sender.sendMessage(ChatColor.DARK_PURPLE + "----- Parkour List -----");
        for(String x : parkourKeys) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + x);
        }
    }

    static String readableTime(Long milliseconds) {
        //Returns amount of time in milliseconds to H:MM:ss.mmm format
        Long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        String minutesS = minutes.toString();
        if(minutesS.length() == 1) {
            minutesS = "0" + minutesS;
        }
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        String secondsS = seconds.toString();
        if(secondsS.length() == 1) {
            secondsS = "0" + secondsS;
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds);
        Long millis = milliseconds;
        String millisS = millis.toString();
        if(millisS.length() == 1) {
            millisS = "00" + millisS;
        } else if(millisS.length() == 2) {
            millisS = "0" + minutesS;
        }
        return hours.toString() + ":" + minutesS + ":" + secondsS + "." + millisS;
    }

    public static void validWarning() {
        //This fixes parkour courses that might not have valid start, end, or checkpoints.
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        boolean warning = false;
        for(String x : parkourKeys) {
            if(!kawaiiFile.getLocation("parkour." + x + ".start").getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                System.out.println("WARNING: Parkour " + x + " does not have a valid start point! Replacing...");
                kawaiiFile.getLocation("parkour." + x + ".start").getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
                warning = true;
            }
            if(!kawaiiFile.getLocation("parkour." + x + ".end").getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                System.out.println("WARNING: Parkour " + x + " does not have a valid end point! Replacing...");
                kawaiiFile.getLocation("parkour." + x + ".end").getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
                warning = true;
            }
            int nocheckpoints = kawaiiFile.getInt("parkour." + x + ".nocheckpoints");
            for (int i = 1; i <= nocheckpoints; i++) {
                if(!kawaiiFile.getLocation("parkour." + x + ".checkpoint" + i).getBlock().getType().equals(Material.STONE_PRESSURE_PLATE)) {
                    System.out.println("WARNING: Parkour " + x + " does not have a valid Checkpoint " + i + "! Replacing...");
                    kawaiiFile.getLocation("parkour." + x + ".checkpoint" + i).getBlock().setType(Material.STONE_PRESSURE_PLATE);
                    warning = true;
                }
            }
        }
        if(warning) {
            System.out.println("If you get repeatedly get non valid point warnings on restarts, please investigate the parkour course. Try deleting and reconfiguring the course.");
        }
    }

    public static void removeGhostCourses() {
        //This removes courses that were abandoned in the creation process for whatever reason.
        Set<String> parkourKeys = kawaiiFile.getConfigurationSection("parkour").getKeys(false);
        for(String x : parkourKeys) {
            if(kawaiiFile.isSet("parkour." + x + ".enabled")) {
                if(!kawaiiFile.getBoolean("parkour." + x + ".enabled")) {
                    System.out.println("WARNING: Detected ghost parkour " + x + ". Deleting...");
                    deleteParkour(x, false);
                }
            }
        }
    }



}

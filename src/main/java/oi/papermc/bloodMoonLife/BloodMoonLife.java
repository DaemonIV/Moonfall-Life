package oi.papermc.bloodMoonLife;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.entity.LivingEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public final class BloodMoonLife extends JavaPlugin implements Listener {
    private boolean hasAnnouncedNight = false;
    private boolean isBloodMoon = false; //
    private int a = 0;
    private int b = 0;
    private final Set<UUID> handledPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        BloodMoonLife plugin = this;
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            World world = Bukkit.getWorlds().get(0);
            if (world == null) return;

            long time = world.getTime();

            if (time >= 13000 && !hasAnnouncedNight) {
                hasAnnouncedNight = true;
                Random r = new Random();
                a = r.nextInt(2);

                b = r.nextInt(10);
                b = 9;

                while (b == 9 && countDemocracyPlayers() > 2) {
                    b = r.nextInt(10);

                }



                if (b == 0) {
                    if (a == 0) {

                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood tiny moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }
                                for (World w : Bukkit.getWorlds()) {
                                    for (Entity entity : w.getEntities()) { // fixed: w not world
                                        if (entity instanceof LivingEntity living) {
                                            AttributeInstance scale = living.getAttribute(Attribute.SCALE);
                                            if (scale != null) {
                                                scale.setBaseValue(0.5);
                                            }
                                        }
                                    }
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 20L);
                        return;
                    }


                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The tiny moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                AttributeInstance scale = player.getAttribute(Attribute.SCALE);
                                if (scale != null) {
                                    scale.setBaseValue(0.5);
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);


                // monster moon
                } else if (b == 1) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood monster moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        world.setMonsterSpawnLimit(300);
                        world.setAnimalSpawnLimit(40);
                        world.setWaterAnimalSpawnLimit(25);
                        world.setTicksPerMonsterSpawns(0);
                        return;
                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The monster moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    world.setMonsterSpawnLimit(200);
                    world.setAnimalSpawnLimit(30);
                    world.setWaterAnimalSpawnLimit(15);
                    world.setTicksPerMonsterSpawns(0);


                } else if (b == 2) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood berserk moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @e[type=!minecraft:player] minecraft:strength infinite 1 true");
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @e[type=!minecraft:player] minecraft:speed infinite 3 true");
                            }
                        }.runTaskTimer(plugin, 0L, 20L);
                        return;
                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The berserk moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:strength infinite 2 true");
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:speed infinite 3 true");
                        }
                    }.runTaskTimer(plugin, 0L, 20L);

                // small moon

                } else if (b == 3) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood small moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:slow_falling infinite 1 true");
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:jump_boost 4 true");
                            }
                        }.runTaskTimer(plugin, 0L, 20L);
                        return;
                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The small moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:slow_falling infinite 1 true");
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "effect give @a minecraft:jump_boost infinite 2 true");
                        }
                    }.runTaskTimer(plugin, 0L, 20L);

                // harvest moon

                } else if (b == 4) {
                    if (a == 0){
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood harvest moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamerule random_tick_speed 0");
                        return;

                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The harvest moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamerule random_tick_speed 30");
                    return;

                // treasure moon

                } else if (b == 5) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood treasure moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        int numberChests = r.nextInt(10) + 1;
                        int intervalSeconds = 500 / numberChests;

                        for (int i = 1; i <= numberChests; i++) {


                            Location loc = getRandomLocation();

                            int x = loc.getBlockX();
                            int y = loc.getBlockY();

                            int z = loc.getBlockZ();

                            final int chestNumber = i;
                            long spawnDelaySeconds = (long) intervalSeconds * i;


                            long warnDelaySeconds = Math.max(0, spawnDelaySeconds - 60);
                            long secondsLeft = spawnDelaySeconds - warnDelaySeconds;

                            this.getServer().getScheduler().runTaskLater(this, () -> {
                                Bukkit.broadcastMessage("§eA chest will spawn within " + secondsLeft + " seconds at " + x + " " + z);
                            }, warnDelaySeconds * 20L);


                            this.getServer().getScheduler().runTaskLater(this, () -> {

                                Bukkit.broadcastMessage("§aA chest has spawned at " + x + " " + z);
                                String[] lootitems = {"DIAMOND", "IRON_INGOT", "NETHERITE_INGOT", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "WITHER_SKELETON_SKULL", "COBWEB", "REDSTONE", "COAL", "IRON_GOLEM_SPAWN_EGG", "VILLAGER_SPAWN_EGG", "BOGGED_SPAWN_EGG", "CREEPER_SPAWN_EGG", "WITHER_SKELETON_SPAWN_EGG", "ZOMBIE_SPAWN_EGG", "VEX_SPAWN_EGG", "GUARDIAN_SPAWN_EGG", "WARDEN_SPAWN_EGG", "BLAZE_SPAWN_EGG", "PIGLIN_BRUTE_SPAWN_EGG", "DRIED_GHAST", "LAPIS_LAZULI", "EMERALD", "ENDER_PEARL", "EXPERIENCE_BOTTLE", "BREEZE_ROD", "GOLDEN_APPLE", "GOLDEN_CARROT", "STEAK", "APPLE", "BREAD", "DIAMOND_HORSE_ARMOR", "NETHERITE_HORSE_ARMOR", "TRIDENT", "ARROW", "AXOLOTL_BUCKET", "LAVA_BUCKET", "POWDER_SNOW_BUCKET", "RECOVERY_COMPASS", "ECHO_SHARD", "GOAT_HORN", "SCULK_SENSOR", "TNT", "TNT_MINECART", "OBSIDIAN", "POINTED_DRIPSTONE", "SNIFFER_EGG", "TURTLE_EGG", "OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG", "PALE_OAK_LOG", "CRIMSON_NYLIUM", "WARPED_STEM", "NETHER_STAR", "ENCHANTING_TABLE", "SPAWNER"};
                                // Min, Max, Weight, ID
                                int[][] lootpoolvar = {{1, 10, 15, 0}, {5, 20, 20, 1}, {1, 1, 5, 2}, {1, 1, 5, 3}, {1, 3, 3, 4}, {6, 15, 15, 5}, {10, 40, 10, 6}, {10, 64, 10, 7}, {1, 3, 7, 8}, {1, 1, 5, 9}, {1, 12, 5, 10}, {1, 12, 15, 11}, {1, 8, 5, 12}, {1, 12, 15, 13}, {1, 8, 3, 14}, {1, 8, 5, 15}, {1, 1, 1, 16}, {1, 12, 5, 17}, {1, 1, 3, 18}, {1, 2, 10, 19}, {10, 40, 10, 20}, {10, 32, 7, 21}, {2, 12, 10, 22}, {8, 32, 10, 23}, {1, 12, 10, 24}, {3, 16, 5, 25}, {8, 32, 7, 26}, {8, 48, 10, 27}, {4, 20, 15, 28}, {10, 32, 20, 29}, {1, 1, 10, 30}, {1, 1, 1, 31}, {1, 1, 5, 32}, {8, 64, 20, 33}, {1, 1, 5, 34}, {1, 1, 7, 35}, {1, 1, 7, 36}, {1, 1, 10, 37}, {2, 8, 10, 38}, {1, 1, 10, 39}, {1, 6, 15, 40}, {1, 16, 7, 41}, {1, 1, 10, 42}, {8, 22, 15, 43}, {1, 12, 10, 44}, {1, 1, 7, 45}, {1, 4, 5, 46}, {12, 32, 5, 47}, {12, 32, 5, 48}, {12, 32, 5, 49}, {12, 32, 5, 50}, {12, 32, 5, 51}, {12, 32, 5, 52}, {12, 32, 5, 53}, {12, 32, 5, 54}, {12, 32, 5, 55}, {12, 32, 5, 56}, {12, 32, 5, 57}, {1, 1, 3, 58}, {1, 1, 10, 59}, {1, 1, 1, 60}};

                                int n = lootpoolvar.length - 1;


                                List<int[]> weightedlootpoolvar = new ArrayList<>();

                                for (int[] entry : lootpoolvar) {
                                    int weight = entry[2];
                                    for (int t = 0; t < weight; t++) {
                                        weightedlootpoolvar.add(entry);
                                    }
                                }

                                // ts works

                                int lootnum = r.nextInt(15 + 1 - 4) + 4;
                                ArrayList<Integer> lootamount = new ArrayList<Integer>();
                                ArrayList<Integer> lootid = new ArrayList<Integer>();
                                ArrayList<Integer> lootpos = new ArrayList<Integer>();

                                int lootcall = r.nextInt(weightedlootpoolvar.size());

                                while (lootnum != 0) {
                                    lootnum = lootnum - 1;
                                    lootcall = r.nextInt(weightedlootpoolvar.size());
                                    int[] chosen = weightedlootpoolvar.get(lootcall);
                                    lootamount.add(r.nextInt(chosen[1] + 1 - chosen[0]) + chosen[0]);
                                    lootid.add(chosen[3]);
                                    lootpos.add(r.nextInt(27));
                                }
                                StringBuilder sb = new StringBuilder("trapped_chest{Items:[");

                                for (int f = 0; f < lootid.size(); f++) {
                                    String itemName = lootitems[lootid.get(f)].toLowerCase(); // DIAMOND -> diamond

                                    if (f > 0) sb.append(",");
                                    sb.append("{Slot:").append(lootpos.get(f))
                                            .append(",id:").append(itemName)
                                            .append(",count:").append(lootamount.get(f))
                                            .append("}");
                                }

                                sb.append("]}");
                                String result = sb.toString();

                                int trapnum = r.nextInt(2);

                                // FINISHH THIS
                                if (trapnum == 0) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + y + " " + z + " " + result);
                                } else {
                                    if (trapnum == 1) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + y + " " + z + " " + result);
                                        
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + (y - 2) + " " + z + " tnt");
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + (x + 1) + " " + (y - 2) + " " + z + " tnt");
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + (x - 1) + " " + (y - 2) + " " + z + " tnt");
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + (y - 2) + " " + (z + 1) + " tnt");
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + (y - 2) + " " + (z - 1) + " tnt");
                                    }

                                }

                            }, spawnDelaySeconds * 20L);
                        }

                        return;
                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The treasure moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    int numberChests = r.nextInt(10) + 1;
                    int intervalSeconds = 500 / numberChests;

                    for (int i = 1; i <= numberChests; i++) {


                        Location loc = getRandomLocation();

                        int x = loc.getBlockX();
                        int y = loc.getBlockY();
                        int z = loc.getBlockZ();

                        final int chestNumber = i;
                        long spawnDelaySeconds = (long) intervalSeconds * i;


                        long warnDelaySeconds = Math.max(0, spawnDelaySeconds - 60);
                        long secondsLeft = spawnDelaySeconds - warnDelaySeconds;

                        this.getServer().getScheduler().runTaskLater(this, () -> {
                            Bukkit.broadcastMessage("§eA chest will spawn within " + secondsLeft + " seconds at " + x + " " + z);
                        }, warnDelaySeconds * 20L);


                        this.getServer().getScheduler().runTaskLater(this, () -> {

                            Bukkit.broadcastMessage("§aA chest has spawned at " + x + " " + z);
                            String[] lootitems = {"DIAMOND", "IRON_INGOT", "NETHERITE_INGOT", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "WITHER_SKELETON_SKULL", "COBWEB", "REDSTONE", "COAL", "IRON_GOLEM_SPAWN_EGG", "VILLAGER_SPAWN_EGG", "BOGGED_SPAWN_EGG", "CREEPER_SPAWN_EGG", "WITHER_SKELETON_SPAWN_EGG", "ZOMBIE_SPAWN_EGG", "VEX_SPAWN_EGG", "GUARDIAN_SPAWN_EGG", "WARDEN_SPAWN_EGG", "BLAZE_SPAWN_EGG", "PIGLIN_BRUTE_SPAWN_EGG", "DRIED_GHAST", "LAPIS_LAZULI", "EMERALD", "ENDER_PEARL", "EXPERIENCE_BOTTLE", "BREEZE_ROD", "GOLDEN_APPLE", "GOLDEN_CARROT", "COOKED_BEEF", "APPLE", "BREAD", "DIAMOND_HORSE_ARMOR", "NETHERITE_HORSE_ARMOR", "TRIDENT", "ARROW", "AXOLOTL_BUCKET", "LAVA_BUCKET", "POWDER_SNOW_BUCKET", "RECOVERY_COMPASS", "ECHO_SHARD", "GOAT_HORN", "SCULK_SENSOR", "TNT", "TNT_MINECART", "OBSIDIAN", "POINTED_DRIPSTONE", "SNIFFER_EGG", "TURTLE_EGG", "OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG", "PALE_OAK_LOG", "CRIMSON_NYLIUM", "WARPED_STEM", "NETHER_STAR", "ENCHANTING_TABLE", "SPAWNER"};
                            // Min, Max, Weight, ID
                            int[][] lootpoolvar = {{1, 10, 15, 0}, {5, 20, 20, 1}, {1, 1, 5, 2}, {1, 1, 5, 3}, {1, 3, 3, 4}, {6, 15, 15, 5}, {10, 40, 10, 6}, {10, 64, 10, 7}, {1, 3, 7, 8}, {1, 1, 5, 9}, {1, 12, 5, 10}, {1, 12, 15, 11}, {1, 8, 5, 12}, {1, 12, 15, 13}, {1, 8, 3, 14}, {1, 8, 5, 15}, {1, 1, 1, 16}, {1, 12, 5, 17}, {1, 1, 3, 18}, {1, 2, 10, 19}, {10, 40, 10, 20}, {10, 32, 7, 21}, {2, 12, 10, 22}, {8, 32, 10, 23}, {1, 12, 10, 24}, {3, 16, 5, 25}, {8, 32, 7, 26}, {8, 48, 10, 27}, {4, 20, 15, 28}, {10, 32, 20, 29}, {1, 1, 10, 30}, {1, 1, 1, 31}, {1, 1, 5, 32}, {8, 64, 20, 33}, {1, 1, 5, 34}, {1, 1, 7, 35}, {1, 1, 7, 36}, {1, 1, 10, 37}, {2, 8, 10, 38}, {1, 1, 10, 39}, {1, 6, 15, 40}, {1, 16, 7, 41}, {1, 1, 10, 42}, {8, 22, 15, 43}, {1, 12, 10, 44}, {1, 1, 7, 45}, {1, 4, 5, 46}, {12, 32, 5, 47}, {12, 32, 5, 48}, {12, 32, 5, 49}, {12, 32, 5, 50}, {12, 32, 5, 51}, {12, 32, 5, 52}, {12, 32, 5, 53},{12, 32, 5, 54}, {12, 32, 5, 55}, {12, 32, 5, 56}, {12, 32, 5, 57}, {1, 1, 3, 58}, {1, 1, 10, 59}, {1, 1, 1, 60}};

                            int n = lootpoolvar.length - 1;


                            List<int[]> weightedlootpoolvar = new ArrayList<>();

                            for (int[] entry : lootpoolvar) {
                                int weight = entry[2];
                                for (int t = 0; t < weight; t++) {
                                    weightedlootpoolvar.add(entry);
                                }
                            }

                            // ts works

                            int lootnum = r.nextInt(15 + 1 - 4) + 4;
                            ArrayList<Integer> lootamount = new ArrayList<Integer>();
                            ArrayList<Integer> lootid = new ArrayList<Integer>();
                            ArrayList<Integer> lootpos = new ArrayList<Integer>();

                            int lootcall = r.nextInt(weightedlootpoolvar.size());

                            while (lootnum != 0) {
                                lootnum = lootnum - 1;
                                lootcall = r.nextInt(weightedlootpoolvar.size());
                                int[] chosen = weightedlootpoolvar.get(lootcall);
                                lootamount.add(r.nextInt(chosen[1] + 1 - chosen[0]) + chosen[0]);
                                lootid.add(chosen[3]);
                                lootpos.add(r.nextInt(27));
                            }
                            StringBuilder sb = new StringBuilder("trapped_chest{Items:[");

                            for (int f = 0; f < lootid.size(); f++) {
                                String itemName = lootitems[lootid.get(f)].toLowerCase(); // DIAMOND -> diamond

                                if (f > 0) sb.append(",");
                                sb.append("{Slot:").append(lootpos.get(f))
                                        .append(",id:").append(itemName)
                                        .append(",count:").append(lootamount.get(f))
                                        .append("}");
                            }

                            sb.append("]}");
                            String result = sb.toString();

                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + x + " " + y + " " + z + " " + result);
                        }, spawnDelaySeconds * 20L);
                        }
                    return;

                    //Giant Moon
                    } else if (b == 6) {
                    if (a == 0) {
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The blood Giant moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (handledPlayers.add(player.getUniqueId())) {
                                        double size = 2.5 + r.nextDouble() * 2.0; // 1.5 to 3.5
                                        int buff = r.nextInt(3);

                                        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
                                        if (scale != null) {
                                            scale.setBaseValue(size);
                                        }

                                        if (buff == 0) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " weakness infinite 1 true");
                                        } else if (buff == 1) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " slowness infinite 1 true");
                                        } else if (buff == 2) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " weaving infinite 1 true");
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " oozing infinite 1 true");
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " infestation infinite 1 true");
                                        }
                                    }
                                }

                            }
                        }.runTaskTimer(plugin, 0L, 20L);
                        return;
                    }
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Giant moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (handledPlayers.add(player.getUniqueId())) {
                                    double size = 1.5 + r.nextDouble() * 2.0; // 1.5 to 3.5
                                    int buff = r.nextInt(3);

                                    AttributeInstance scale = player.getAttribute(Attribute.SCALE);
                                    if (scale != null) {
                                        scale.setBaseValue(size);
                                    }

                                    if (buff == 0) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " resistance infinite 1 true");
                                    } else if (buff == 1) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " strength infinite 1 true");
                                    } else if (buff == 2) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + player.getName() + " regeneration infinite 2 true");
                                    }
                                }
                            }

                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                    return;
                //nether moon

                } else if (b == 7) {
                    if (a == 0) {
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Blood Nether moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                        int numbernether = r.nextInt(8) + 5;
                        int nethertimer = 10000 / numbernether;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }
                                Location loc = getRandomLocation();
                                int x = loc.getBlockX();
                                int y = loc.getBlockY();
                                int z = loc.getBlockZ();
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + x + " " + y + " " + z);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "place structure minecraft:ruined_portal_nether " + x + " " + y + " " + z);
                                int mobnum = r.nextInt(11) + 5;
                                int mobid = r.nextInt(12);
                                while (mobnum != 0) {
                                    mobnum = mobnum - 1;
                                    mobid = r.nextInt(9);

                                    if (mobid == 0) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon ghast " + x + " " + (y + 2) + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                    } else if (mobid == 1) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:iron_sword},chest:{id:iron_chestplate},feet:{id:iron_boots}}}");
                                    } else if (mobid == 2) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:stone_axe},chest:{id:chainmail_chestplate},feet:{id:iron_boots}}}");
                                    } else if (mobid == 3) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon skeleton_horse " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,Passengers:[{id:\"minecraft:wither_skeleton\",Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:\"minecraft:diamond_spear\"},chest:{id:\"minecraft:chainmail_chestplate\"},legs:{id:\"minecraft:leather_leggings\"},feet:{id:\"minecraft:leather_boots\"}}}]}");
                                    } else if (mobid == 4) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon magma_cube " + x + " " + y + " " + z + " {Size:5,Glowing:1b,PersistenceRequired:1b}");
                                    } else if (mobid == 5) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon magma_cube " + x + " " + y + " " + z + " {Size:6,Glowing:1b,PersistenceRequired:1b}");
                                    } else if (mobid == 6) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon blaze " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                    } else if (mobid == 7) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon hoglin " + x + " " + y + " " + z + " {IsImmuneToZombification:1,Glowing:1b,PersistenceRequired:1b}");
                                    } else if (mobid == 8) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow,components:{enchantments:{power:3,punch:1}}},head:{id:leather_helmet},chest:{id:leather_chestplate},legs:{id:leather_leggings},feet:{id:leather_boots}}}");
                                    }

                                }
                            }
                        }.runTaskTimer(plugin, 0L, nethertimer);
                        return;
                    }
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Nether moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                    int numbernether = r.nextInt(8) + 5;
                    int nethertimer = 10000 / numbernether;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }
                            Location loc = getRandomLocation();
                            int x = loc.getBlockX();
                            int y = loc.getBlockY();
                            int z = loc.getBlockZ();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + x + " " + y + " " + z);
                            int mobnum = r.nextInt(11) + 5;
                            int mobid = r.nextInt(12);
                            while (mobnum != 0) {
                                mobnum = mobnum - 1;
                                mobid = r.nextInt(11);

                                if (mobid == 0) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon ghast " + x + " " + (y + 2) + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 1) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon strider " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 2) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon happy_ghast " + x + " " + (y + 2) + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 3) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:stone_spear}}}");
                                } else if (mobid == 4) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow}}}");
                                } else if (mobid == 5) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon hoglin " + x + " " + y + " " + z + " {IsImmuneToZombification:1,Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 6) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon magma_cube " + x + " " + y + " " + z + " {Size:3,Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 7) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon magma_cube " + x + " " + y + " " + z + " {Size:4,Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 8) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon blaze " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                } else if (mobid == 9) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:stone_sword}}}");
                                } else if (mobid == 10) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon wither_skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:golden_axe}}}");
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0L, nethertimer);
                    return;

                // party moon
                } else if (b == 8) {
                    if (a == 0) {
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Blood Party moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!hasAnnouncedNight) {
                                    this.cancel();
                                    return;
                                }

                                Location loc = getRandomLocation();
                                int x = loc.getBlockX();
                                int y = loc.getBlockY();
                                int z = loc.getBlockZ();

                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:firework_rocket " + x + " " + y + " " + z + " {LifeTime:20,FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{\"fireworks\":{explosions:[{shape:\"star\",colors:[I;11743532,4312372,14188952,6719955,2437522,8073150,12801229,3887386,15435844],fade_colors:[I;15790320],has_trail:1b}],flight_duration:3b}}}}");
                                if (r.nextInt(5) == 0) {
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon tnt_minecart " + x + " " + (y + 9) + " " + z);
                                    }, 20L);
                                }


                            }
                        }.runTaskTimer(plugin, 0L, r.nextInt( 101) + 20);
                        return;
                    }

                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Party moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!hasAnnouncedNight) {
                                this.cancel();
                                return;
                            }

                            Location loc = getRandomLocation();
                            int x = loc.getBlockX();
                            int y = loc.getBlockY();
                            int z = loc.getBlockZ();

                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:firework_rocket " + x + " " + y + " " + z + " {LifeTime:20,FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{\"fireworks\":{explosions:[{shape:\"star\",colors:[I;11743532,4312372,14188952,6719955,2437522,8073150,12801229,3887386,15435844],fade_colors:[I;15790320],has_trail:1b}],flight_duration:3b}}}}");


                        }
                    }.runTaskTimer(plugin, 0L, r.nextInt(51) + 20);
                    return;
                } else if (b == 9) {
                    if (a == 0) {
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Blood Democratic moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                        return;
                    }
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The Democratic moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");


                    return;
                }


            }





            if (time < 13000 && hasAnnouncedNight) {
                hasAnnouncedNight = false;

                world.setMonsterSpawnLimit(70);
                world.setTicksPerMonsterSpawns(1);
                world.setAnimalSpawnLimit(10);
                world.setWaterAnimalSpawnLimit(5);
                handledPlayers.clear();

                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "effect clear @e");
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamerule random_tick_speed 3");



                if (isBloodMoon) {

                    for (World w : Bukkit.getWorlds()) {
                        for (Entity entity : w.getEntities()) { // fixed: w not world
                            if (entity instanceof LivingEntity living) {
                                AttributeInstance scale = living.getAttribute(Attribute.SCALE);
                                if (scale != null) {
                                    scale.setBaseValue(1);
                                }
                            }
                        }
                    }
                } else {

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
                        if (scale != null) {
                            scale.setBaseValue(1);
                        }
                    }
                }
            }

        }, 0L, 20L);
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("vote")) return false;
        World world = Bukkit.getWorlds().get(0);
        long time = world.getTime();
        if (b != 9 && time >= 13000 && time <= 14000) {

        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /vote <player>");
            return true;
        }

        if (args.length != 0) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();


            for (Player player : Bukkit.getOnlinePlayers()) {
                Team team = board.getEntryTeam(player.getName());
                if (team == null) continue;

                String name = team.getName();
                if (name.equals("DG6") || name.equals("DG5") || name.equals("DG4")
                        || name.equals("G") || name.equals("Y")) {

                }
            }
            return true;
        }
        sender.sendMessage("§cUnknown subcommand.");
        return true;
    }
    @Override
    public void onDisable() {
    }
    public double getCurrentBorderSize() {
        World world = (World)this.getServer().getWorlds().get(0);
        return world.getWorldBorder().getSize();
    }
    public int getCentreX() {
        World world = (World)this.getServer().getWorlds().get(0);
        WorldBorder border = world.getWorldBorder();
        return (int) border.getCenter().getX();
    }

    public int getCentreZ() {
        World world = (World)this.getServer().getWorlds().get(0);
        WorldBorder border = world.getWorldBorder();
        return (int) border.getCenter().getZ();
    }

    public Location getRandomLocation() {
        World world = this.getServer().getWorlds().get(0);
        WorldBorder border = world.getWorldBorder();

        double half = border.getSize() / 2 - 5; // stay 5 blocks inside the edge
        int centreX = getCentreX();
        int centreZ = getCentreZ();

        int x = centreX + (int) ThreadLocalRandom.current().nextDouble(-half, half);
        int z = centreZ + (int) ThreadLocalRandom.current().nextDouble(-half, half);
        int y = world.getHighestBlockYAt(x, z) + 1; // one block above the surface

        return new Location(world, x, y, z);
    }

    private int countDemocracyPlayers() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        int count = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = board.getEntryTeam(player.getName());
            if (team == null) continue;

            String name = team.getName();
            if (name.equals("DG6") || name.equals("DG5") || name.equals("DG4")
                    || name.equals("G") || name.equals("Y")) {
                count++;
            }
        }
        return count;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws InterruptedException {
        Player player = event.getEntity();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if (team == null) {
            // admin.sendMessage("Yo, you fucked up somewhere, that person that died isn't on a team! " + player.getName());
        } else {
            String teamName = team.getName();

            if (teamName.equalsIgnoreCase("DG4")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team join G " + player.getName());


            } else if (teamName.equalsIgnoreCase("DG5")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team join DG4 " + player.getName());

            } else if (teamName.equalsIgnoreCase("DG6")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team join DG5 " + player.getName());

            } else if (teamName.equalsIgnoreCase("G")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team join Y " + player.getName());

            } else if (teamName.equalsIgnoreCase("Y")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team join R " + player.getName());

            } else if (teamName.equalsIgnoreCase("R")) {
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "team leave " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamemode spectator " + player.getName());
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "playsound minecraft:entity.lightning_bolt.thunder neutral @a ~ ~ ~ 1000000");
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 0 140 40");
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a subtitle {\"text\":\"Has lost their last life\"}");
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"" + player.getName() + "\",\"color\":\"red\"}");
            }
        }
    }
}

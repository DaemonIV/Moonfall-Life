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
import org.bukkit.event.player.PlayerJoinEvent;
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
    private boolean assassinRewarded = false;

    private File getBoogeymanFile() {
        return new File(this.getDataFolder(), "boogeyman.txt");
    }

    // Reads boogeyman.txt -> { name : 0 or 1 }
    public Map<String, Integer> readBoogeymen() {
        Map<String, Integer> map = new LinkedHashMap<>();
        File file = getBoogeymanFile();
        if (!file.exists()) return map;
        try {
            for (String line : Files.readAllLines(file.toPath())) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;
                map.put(parts[0], parts[1].equals("1") ? 1 : 0);
            }
        } catch (IOException e) {
            this.getLogger().warning("Could not read boogeyman file: " + e.getMessage());
        }
        return map;
    }

    // Writes { name : 0 or 1 } -> boogeyman.txt, one "name 0/1" per line
    public void writeBoogeymen(Map<String, Integer> map) {
        getDataFolder().mkdirs();
        try (PrintWriter printer = new PrintWriter(new FileWriter(getBoogeymanFile()))) {
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                printer.println(e.getKey() + " " + e.getValue());
            }
        } catch (IOException e) {
            this.getLogger().warning("Could not save boogeyman file: " + e.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (hasAnnouncedNight) {
            Bukkit.getScheduler().runTaskLater(this, () -> applyMoonPhase(p, b), 40L);
        }
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) p.resetPlayerTime();
    }

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





                while (b == 9 && countDemocracyPlayers() > 4) {
                   b = r.nextInt(9);

                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    applyMoonPhase(p, b);
                }


                if (b == 0) {
                    if (a == 0) {

                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The shrinking blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
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
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The shrinking moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The horde blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        int mobnether = r.nextInt(10) + 6;
                        int mobtimer = 10000 / mobnether;
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

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!hasAnnouncedNight) {
                                            this.cancel();
                                            return;
                                        }
                                        int mobnum = r.nextInt(13) + 6;
                                        int mobid = r.nextInt(12);
                                        while (mobnum != 0) {
                                            mobnum = mobnum - 1;
                                            mobid = r.nextInt(13);

                                            if (mobid == 0) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon creeper    " + x + " " + y + " " + z + "  {powered:1,Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 1) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon husk " + x + " " + y + " " + z + "  {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:diamond_sword},head:{id:iron_helmet},chest:{id:iron_chestplate},legs:{id:chainmail_leggings},feet:{id:iron_boots}}}");
                                            } else if (mobid == 2) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon husk " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:iron_axe},head:{id:chainmail_helmet},chest:{id:iron_chestplate},legs:{id:chainmail_leggings},feet:{id:chainmail_boots}}}");
                                            } else if (mobid == 3) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon camel_husk " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,Passengers:[{id:husk,Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:iron_spear},head:{id:leather_helmet},chest:{id:iron_chestplate},legs:{id:leather_leggings},feet:{id:chainmail_boots}}}]}");
                                            } else if (mobid == 4) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon bogged " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow},head:{id:chainmail_boots},chest:{id:iron_chestplate},legs:{id:iron_leggings},feet:{id:chainmail_boots}}}");
                                            } else if (mobid == 5) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon stray " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow},head:{id:chainmail_boots},chest:{id:iron_chestplate},legs:{id:iron_leggings},feet:{id:chainmail_boots}}}");
                                            } else if (mobid == 6) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon witch " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 7) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon phantom " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 8) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon guardian " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 9) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon vindicator " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 10) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon ravager " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 11) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon vex " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                            } else if (mobid == 12) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon breeze " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,attributes:[{id:scale,base:2f}]}");
                                            }

                                        }
                                    }
                                }.runTaskLater(plugin,  20L);

                            }
                        }.runTaskTimer(plugin, 0L, mobtimer);
                        return;
                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The horde moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    int mobnether = r.nextInt(8) + 6;
                    int mobtimer = 10000 / mobnether;
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

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!hasAnnouncedNight) {
                                        this.cancel();
                                        return;
                                    }
                                    int mobnum = r.nextInt(11) + 5;
                                    int mobid = r.nextInt(12);
                                    while (mobnum != 0) {
                                        mobnum = mobnum - 1;
                                        mobid = r.nextInt(10);

                                        if (mobid == 0) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon creeper    " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                        } else if (mobid == 1) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon zombie " + x + " " + y + " " + z + "  {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:iron_sword},head:{id:chainmail_helmet},chest:{id:iron_chestplate},legs:{id:chainmail_leggings},feet:{id:iron_boots}}}");
                                        } else if (mobid == 2) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon zombie " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:iron_axe},head:{id:leather_helmet},chest:{id:chainmail_chestplate},legs:{id:leather_leggings},feet:{id:chainmail_boots}}}");
                                        } else if (mobid == 3) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow},head:{id:leather_helmet},chest:{id:chainmail_chestplate},legs:{id:leather_leggings},feet:{id:chainmail_boots}}}");
                                        } else if (mobid == 4) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon skeleton " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b,equipment:{mainhand:{id:bow,components:{enchantments:{punch:3}}},head:{id:leather_helmet},chest:{id:leather_chestplate},legs:{id:leather_leggings},feet:{id:leather_boots}}}");
                                        } else if (mobid == 5) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon slime " + x + " " + y + " " + z + " {Size:5,Glowing:1b,PersistenceRequired:1b}");
                                        } else if (mobid == 6) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon witch " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                        } else if (mobid == 7) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon phantom " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                        } else if (mobid == 8) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon cave_spider " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                        } else if (mobid == 9) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon pillager " + x + " " + y + " " + z + " {Glowing:1b,PersistenceRequired:1b}");
                                        }

                                    }
                                }
                            }.runTaskLater(plugin,  20L);

                        }
                    }.runTaskTimer(plugin, 0L, mobtimer);
                    return;


                } else if (b == 2) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The berserk blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The astral blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
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
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The astral moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The harvest blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamerule random_tick_speed 0");
                        return;

                    }
                    isBloodMoon = false;
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The harvest moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "gamerule random_tick_speed 20");
                    return;

                // treasure moon

                } else if (b == 5) {
                    if (a == 0) {
                        isBloodMoon = true;
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The greed blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
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
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:firework_rocket " + x + " " + y + " " + z + " {LifeTime:20,FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{\"fireworks\":{explosions:[{shape:\"star\",colors:[I;11743532,4312372,14188952,6719955,2437522,8073150,12801229,3887386,15435844],fade_colors:[I;15790320],has_trail:1b}],flight_duration:3b}}}}");
                                String[] lootitems = {"DIAMOND", "IRON_INGOT", "NETHERITE_INGOT", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "WITHER_SKELETON_SKULL", "COBWEB", "REDSTONE", "COAL", "IRON_GOLEM_SPAWN_EGG", "VILLAGER_SPAWN_EGG", "BOGGED_SPAWN_EGG", "CREEPER_SPAWN_EGG", "WITHER_SKELETON_SPAWN_EGG", "ZOMBIE_SPAWN_EGG", "VEX_SPAWN_EGG", "GUARDIAN_SPAWN_EGG", "WARDEN_SPAWN_EGG", "BLAZE_SPAWN_EGG", "PIGLIN_BRUTE_SPAWN_EGG", "DRIED_GHAST", "LAPIS_LAZULI", "EMERALD", "ENDER_PEARL", "EXPERIENCE_BOTTLE", "BREEZE_ROD", "GOLDEN_APPLE", "GOLDEN_CARROT", "COOKED_BEEF", "APPLE", "BREAD", "DIAMOND_HORSE_ARMOR", "NETHERITE_HORSE_ARMOR", "TRIDENT", "ARROW", "AXOLOTL_BUCKET", "LAVA_BUCKET", "POWDER_SNOW_BUCKET", "RECOVERY_COMPASS", "ECHO_SHARD", "GOAT_HORN", "SCULK_SENSOR", "TNT", "TNT_MINECART", "OBSIDIAN", "POINTED_DRIPSTONE", "SNIFFER_EGG", "TURTLE_EGG", "OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG", "PALE_OAK_LOG", "CRIMSON_NYLIUM", "WARPED_STEM", "NETHER_STAR", "ENCHANTING_TABLE", "SPAWNER" , "enchanted_book[enchantments={sharpness:2}]", "enchanted_book[enchantments={sharpness:3}]", "enchanted_book[enchantments={knockback:2}]", "enchanted_book[enchantments={fire_aspect:2}]", "enchanted_book[enchantments={protection:2}]", "enchanted_book[enchantments={protection:3}]", "enchanted_book[enchantments={loyalty:3}]", "enchanted_book[enchantments={feather_falling:3}]", "enchanted_book[enchantments={looting:2}]", "enchanted_book[enchantments={power:3}]"};
                                // Min, Max, Weight, ID
                                int[][] lootpoolvar = {{1, 10, 15, 0}, {5, 20, 20, 1}, {1, 1, 5, 2}, {1, 1, 5, 3}, {1, 3, 3, 4}, {6, 15, 15, 5}, {10, 40, 10, 6}, {10, 64, 10, 7}, {1, 3, 7, 8}, {1, 1, 5, 9}, {1, 12, 5, 10}, {1, 12, 15, 11}, {1, 8, 5, 12}, {1, 12, 15, 13}, {1, 8, 3, 14}, {1, 8, 5, 15}, {1, 1, 1, 16}, {1, 12, 5, 17}, {1, 1, 3, 18}, {1, 2, 10, 19}, {10, 40, 10, 20}, {10, 32, 7, 21}, {2, 12, 10, 22}, {8, 32, 10, 23}, {1, 12, 10, 24}, {3, 16, 5, 25}, {8, 32, 7, 26}, {8, 48, 10, 27}, {4, 20, 15, 28}, {10, 32, 20, 29}, {1, 1, 10, 30}, {1, 1, 1, 31}, {1, 1, 5, 32}, {8, 64, 20, 33}, {1, 1, 5, 34}, {1, 1, 7, 35}, {1, 1, 7, 36}, {1, 1, 10, 37}, {2, 8, 10, 38}, {1, 1, 10, 39}, {1, 6, 15, 40}, {1, 16, 7, 41}, {1, 1, 10, 42}, {8, 22, 15, 43}, {1, 12, 10, 44}, {1, 1, 7, 45}, {1, 4, 5, 46}, {12, 32, 5, 47}, {12, 32, 5, 48}, {12, 32, 5, 49}, {12, 32, 5, 50}, {12, 32, 5, 51}, {12, 32, 5, 52}, {12, 32, 5, 53},{12, 32, 5, 54}, {12, 32, 5, 55}, {12, 32, 5, 56}, {12, 32, 5, 57}, {1, 1, 3, 58}, {1, 1, 10, 59}, {1, 1, 1, 60}, {1, 1, 5, 61}, {1, 1, 3, 62}, {1, 1, 3, 63}, {1, 1, 3, 64}, {1, 1, 5, 65}, {1, 1, 3, 66}, {1, 1, 5, 67}, {1, 1, 3, 68}, {1, 1, 3, 69}, {1, 1, 3, 70}};

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
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83c\\udf19 The greed moon has risen! \\ud83c\\udf19\",\"color\":\"dark_blue\"}");
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
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:firework_rocket " + x + " " + y + " " + z + " {LifeTime:20,FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{\"fireworks\":{explosions:[{shape:\"star\",colors:[I;11743532,4312372,14188952,6719955,2437522,8073150,12801229,3887386,15435844],fade_colors:[I;15790320],has_trail:1b}],flight_duration:3b}}}}");
                            String[] lootitems = {"DIAMOND", "IRON_INGOT", "NETHERITE_INGOT", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "WITHER_SKELETON_SKULL", "COBWEB", "REDSTONE", "COAL", "IRON_GOLEM_SPAWN_EGG", "VILLAGER_SPAWN_EGG", "BOGGED_SPAWN_EGG", "CREEPER_SPAWN_EGG", "WITHER_SKELETON_SPAWN_EGG", "ZOMBIE_SPAWN_EGG", "VEX_SPAWN_EGG", "GUARDIAN_SPAWN_EGG", "WARDEN_SPAWN_EGG", "BLAZE_SPAWN_EGG", "PIGLIN_BRUTE_SPAWN_EGG", "DRIED_GHAST", "LAPIS_LAZULI", "EMERALD", "ENDER_PEARL", "EXPERIENCE_BOTTLE", "BREEZE_ROD", "GOLDEN_APPLE", "GOLDEN_CARROT", "COOKED_BEEF", "APPLE", "BREAD", "DIAMOND_HORSE_ARMOR", "NETHERITE_HORSE_ARMOR", "TRIDENT", "ARROW", "AXOLOTL_BUCKET", "LAVA_BUCKET", "POWDER_SNOW_BUCKET", "RECOVERY_COMPASS", "ECHO_SHARD", "GOAT_HORN", "SCULK_SENSOR", "TNT", "TNT_MINECART", "OBSIDIAN", "POINTED_DRIPSTONE", "SNIFFER_EGG", "TURTLE_EGG", "OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG", "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG", "PALE_OAK_LOG", "CRIMSON_NYLIUM", "WARPED_STEM", "NETHER_STAR", "ENCHANTING_TABLE", "SPAWNER" , "enchanted_book[enchantments={sharpness:2}]", "enchanted_book[enchantments={sharpness:3}]", "enchanted_book[enchantments={knockback:2}]", "enchanted_book[enchantments={fire_aspect:2}", "enchanted_book[enchantments={protection:2}]", "enchanted_book[enchantments={protection:3}]", "enchanted_book[enchantments={loyalty:3}]", "enchanted_book[enchantments={feather_falling:3}]", "enchanted_book[enchantments={looting:2}]", "enchanted_book[enchantments={power:3}]"};
                            // Min, Max, Weight, ID
                            int[][] lootpoolvar = {{1, 10, 15, 0}, {5, 20, 20, 1}, {1, 1, 5, 2}, {1, 1, 5, 3}, {1, 3, 3, 4}, {6, 15, 15, 5}, {10, 40, 10, 6}, {10, 64, 10, 7}, {1, 3, 7, 8}, {1, 1, 5, 9}, {1, 12, 5, 10}, {1, 12, 15, 11}, {1, 8, 5, 12}, {1, 12, 15, 13}, {1, 8, 3, 14}, {1, 8, 5, 15}, {1, 1, 1, 16}, {1, 12, 5, 17}, {1, 1, 3, 18}, {1, 2, 10, 19}, {10, 40, 10, 20}, {10, 32, 7, 21}, {2, 12, 10, 22}, {8, 32, 10, 23}, {1, 12, 10, 24}, {3, 16, 5, 25}, {8, 32, 7, 26}, {8, 48, 10, 27}, {4, 20, 15, 28}, {10, 32, 20, 29}, {1, 1, 10, 30}, {1, 1, 1, 31}, {1, 1, 5, 32}, {8, 64, 20, 33}, {1, 1, 5, 34}, {1, 1, 7, 35}, {1, 1, 7, 36}, {1, 1, 10, 37}, {2, 8, 10, 38}, {1, 1, 10, 39}, {1, 6, 15, 40}, {1, 16, 7, 41}, {1, 1, 10, 42}, {8, 22, 15, 43}, {1, 12, 10, 44}, {1, 1, 7, 45}, {1, 4, 5, 46}, {12, 32, 5, 47}, {12, 32, 5, 48}, {12, 32, 5, 49}, {12, 32, 5, 50}, {12, 32, 5, 51}, {12, 32, 5, 52}, {12, 32, 5, 53},{12, 32, 5, 54}, {12, 32, 5, 55}, {12, 32, 5, 56}, {12, 32, 5, 57}, {1, 1, 3, 58}, {1, 1, 10, 59}, {1, 1, 1, 60}, {1, 1, 5, 61}, {1, 1, 3, 62}, {1, 1, 3, 63}, {1, 1, 3, 64}, {1, 1, 5, 65}, {1, 1, 3, 66}, {1, 1, 5, 67}, {1, 1, 3, 68}, {1, 1, 3, 69}, {1, 1, 3, 70}};

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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The titan blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_red\"}");
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
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The titan moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The nether blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                        int numbernether = r.nextInt(11) + 5;
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
                                int mobnum = r.nextInt(8) + 5;
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
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The nether moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                    int numbernether = r.nextInt(11) + 5;
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
                            int mobnum = r.nextInt(8) + 5;
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
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The party blood moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
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
                        }.runTaskTimer(plugin, 0L, r.nextInt( 41) + 20);
                        return;
                    }

                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The party moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
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
                    }.runTaskTimer(plugin, 0L, r.nextInt(41) + 20);
                    return;
                } else if (b == 9) {
                    presidentId = null;
                    assassinId = null;
                    bodyguards.clear();
                    diedTonight.clear();
                    electionActive = false;
                    votedFor.clear();
                    voteCounts.clear();
                    assassinRewarded = false;

                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a times 20 140 40");
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title {\"text\":\"\\ud83e\\ude78 The regicide moon has risen! \\ud83e\\ude78\",\"color\":\"dark_blue\"}");
                    Bukkit.broadcastMessage("§6Voting closes in 1 minute!");
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        UUID winner = null;
                        int best = 0;
                        for (Map.Entry<UUID, Integer> e : voteCounts.entrySet()) {
                            if (e.getValue() > best) {
                                best = e.getValue();
                                winner = e.getKey();
                            }
                        }
                        if (winner != null) {
                            Bukkit.broadcastMessage("§6And the winner is...");
                            presidentId = winner;
                            int finalBest = best;
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                Bukkit.broadcastMessage("§6" + Bukkit.getOfflinePlayer(presidentId).getName() + " won with " + finalBest + " votes!");
                            }, 60L);
                        }

                        if (presidentId != null) {
                            Player president = Bukkit.getPlayer(presidentId);
                            if (president != null) {
                                president.sendMessage("§6Welcome President! You now have 1 minute to decide 2 non-reds to be your bodyguards (/hire). Failure to do so will result in the loss of your life. Once they are picked, a random player will become an assassin. This player will have the task to kill you. If they kill you, you lose that life and your bodyguards will also each lose a life if they didn't die. If you survive the night, you shall gain rewards and the assassin shall lose a life. Good luck President.");
                            }
                        }
                        if (presidentId == null) {
                            Bukkit.broadcastMessage("§cNobody voted, so there is no president tonight.");
                            return;
                        }
                        electionActive = true;

// 1 minute (1200 ticks) later: check bodyguards, then pick the assassin
                        Bukkit.getScheduler().runTaskLater(this, () -> {
                            Player president = Bukkit.getPlayer(presidentId);

                            if (bodyguards.size() < 2) {
                                loseLife(Bukkit.getOfflinePlayer(presidentId).getName());
                                Bukkit.broadcastMessage("§cThe president failed to hire 2 bodyguards and loses a life!");
                            }

                            List<Player> candidates = new ArrayList<>();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.getUniqueId().equals(presidentId)) continue;
                                if (bodyguards.contains(p.getUniqueId())) continue;
                                if (p.getGameMode() == GameMode.SPECTATOR) continue;
                                candidates.add(p);
                            }
                            if (!candidates.isEmpty()) {
                                Player assassin = candidates.get(new Random().nextInt(candidates.size()));
                                assassinId = assassin.getUniqueId();
                                assassin.sendMessage("§4You are the ASSASSIN. Kill the president before morning!");
                            }
                            tell(presidentId, "§6Your bodyguards are set. An assassin has been chosen. Survive until morning!");
                        }, 1200L);



                    }, 1100L);
                    return;
                }


            }





            if (time < 13000 && hasAnnouncedNight) {
                hasAnnouncedNight = false;
                if (electionActive) {
                    electionActive = false;
                    Bukkit.broadcastMessage("§aThe president survived the night!");
                    tell(presidentId, "§6You survived! Here is your reward.");
                    Player p = Bukkit.getPlayer(presidentId);
                    if (p != null) giveAssassinReward(p);
                    assassinRewarded = true; // stops the penalty below running twice
                    if (assassinId != null) {
                        String aName = Bukkit.getOfflinePlayer(assassinId).getName();
                        if (aName != null && !isRed(aName)) loseLife(aName);
                    }
                }
                if (assassinId != null && !assassinRewarded) {
                    String aName = Bukkit.getOfflinePlayer(assassinId).getName();
                    if (aName != null && !isRed(aName)) loseLife(aName);
                }

                assassinId = null;


                handledPlayers.clear();
                for (Player p : Bukkit.getOnlinePlayers()) p.resetPlayerTime();
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
        if (command.getName().equalsIgnoreCase("vote")) {
            World world = Bukkit.getWorlds().get(0);
            long time = world.getTime();
            if (b != 9 && time >= 13000 && time <= 14200) {
                sender.sendMessage("§cThe Ballot is closed!");
                return true;
            }

            if (!(sender instanceof Player voter)) {
                sender.sendMessage("Only players can vote.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§cUsage: /vote <player>");
                return true;
            }

            if (votedFor.containsKey(voter.getUniqueId())) {
                sender.sendMessage("§cYou have already voted!");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }
            if (target.equals(voter)) {
                sender.sendMessage("§cYou are not able to vote for yourself!");
                return true;
            }
            if (!canBePresident(target)) {
                sender.sendMessage("§c" + target.getName() + " cannot be president.");
                return true;
            }

            votedFor.put(voter.getUniqueId(), target.getUniqueId());
            voteCounts.merge(target.getUniqueId(), 1, Integer::sum);
            sender.sendMessage("§aVote counted for " + target.getName());
            return true;
        }
        if (command.getName().equalsIgnoreCase("hire")) {
            if (!(sender instanceof Player p) || presidentId == null || !p.getUniqueId().equals(presidentId)) {
                sender.sendMessage("§cOnly the president can hire bodyguards.");
                return true;
            }
            long time = Bukkit.getWorlds().get(0).getTime();
            if (time < 14100 || time > 15400) {
                sender.sendMessage("§cYou cannot hire at this time");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage("§cUsage: /hire <player>");
                return true;
            }
            if (bodyguards.size() >= 2) {
                sender.sendMessage("§cYou already have 2 bodyguards.");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }
            if (target.equals(p)) {
                sender.sendMessage("§cYou are not able to become your own bodyguard.");
                return true;
            }
            if (!canBePresident(target)) {   // non-red, on a team
                sender.sendMessage("§c" + target.getName() + " cannot be a bodyguard.");
                return true;
            }
            if (!bodyguards.add(target.getUniqueId())) {
                sender.sendMessage("§cThey are already your bodyguard.");
                return true;
            }
            sender.sendMessage("§a" + target.getName() + " is now your bodyguard (" + bodyguards.size() + "/2).");
            target.sendMessage("§6You are now the president's bodyguard. Keep them alive until morning!");
            return true;
        }
        if (command.getName().equalsIgnoreCase("assassinreward")) {
            if (assassinId == null) {
                sender.sendMessage("§cThere is no assassin right now.");
                return true;
            }
            if (assassinRewarded) {
                sender.sendMessage("§cThe assassin has already been rewarded.");
                return true;
            }
            Player assassin = Bukkit.getPlayer(assassinId);
            if (assassin == null) {
                sender.sendMessage("§cThe assassin is not online.");
                return true;
            }
            giveAssassinReward(assassin);
            sender.sendMessage("§aRewarded " + assassin.getName() + " and protected them from losing a life.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("election")) {
            if (presidentId == null) {
                sender.sendMessage("§cThere is no president right now.");
                return true;
            }

            sender.sendMessage("§6--- Election Status ---");
            sender.sendMessage("§ePresident: §f" + nameOf(presidentId) + statusOf(presidentId));

            if (bodyguards.isEmpty()) {
                sender.sendMessage("§eBodyguards: §fnone hired yet");
            } else {
                sender.sendMessage("§eBodyguards (" + bodyguards.size() + "/2):");
                for (UUID id : bodyguards) {
                    sender.sendMessage("§7 - §f" + nameOf(id) + statusOf(id));
                }
            }

            sender.sendMessage("§eAssassin: §f" + (assassinId == null ? "not chosen yet" : nameOf(assassinId) + statusOf(assassinId)
                    + (assassinRewarded ? " §6(rewarded)" : "")));
            sender.sendMessage("§eElection active: §f" + electionActive);
            return true;
        }
        if (command.getName().equalsIgnoreCase("Boogeyroll")) {
            if (!sender.hasPermission("boogeyroll.use")) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            } else {
                this.getServer().broadcastMessage("§6§lA Boogeyman will be selected in §e§l1 minute§6§l!");
                this.getServer().getScheduler().runTaskLater(this, () -> {
                    this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title [{\"text\":\"3\",\"bold\":true,\"color\":\"dark_green\"}]");
                    this.getServer().getScheduler().runTaskLater(this, () -> {
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title [{\"text\":\"2\",\"bold\":true,\"color\":\"yellow\"}]");
                        this.getServer().getScheduler().runTaskLater(this, () -> {
                            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "title @a title [{\"text\":\"1\",\"bold\":true,\"color\":\"dark_red\"}]");
                            this.getServer().getScheduler().runTaskLater(this, () -> {
                                List<Player> eligiblePlayers = new ArrayList<>();

                                for (Player player : this.getServer().getOnlinePlayers()) {
                                    Team team = player.getScoreboard().getPlayerTeam(player);
                                    if (team != null && !team.getName().equalsIgnoreCase("R")) {
                                        eligiblePlayers.add(player);
                                    }
                                }

                                if (eligiblePlayers.isEmpty()) {
                                    this.getServer().broadcastMessage("§cNo eligible players for boogeyman!");
                                } else {
                                    Random random = new Random();
                                    Collections.shuffle(eligiblePlayers, random);

                                    // 1-3 boogeymen, but never more than the eligible players available
                                    int count = Math.min(random.nextInt(3) + 1, eligiblePlayers.size());
                                    List<Player> chosen = new ArrayList<>(eligiblePlayers.subList(0, count));

                                    Map<String, Integer> boogeymen = new LinkedHashMap<>();
                                    for (Player boogeyman : chosen) {
                                        boogeymen.put(boogeyman.getName(), 0); // 0 = hasn't killed yet
                                        boogeyman.sendTitle("§4You are the Boogeyman!", "§cKill someone to lift the curse", 10, 70, 20);
                                    }

                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (!chosen.contains(player)) {
                                            player.sendTitle("§2You are NOT the Boogeyman!", "§aSurvive against the Boogeyman!", 10, 70, 20);
                                        }
                                    }

                                    this.writeBoogeymen(boogeymen);
                                }
                            }, 50L);
                        }, 50L);
                    }, 50L);
                }, 1200L);
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("amiboogeyman")) {
            Integer status = readBoogeymen().get(sender.getName());
            if (status == null) {
                sender.sendMessage("You are NOT the Boogeyman!");
            } else if (status == 1) {
                sender.sendMessage("You were a Boogeyman, but you already got your kill. You are cured!");
            } else {
                sender.sendMessage("You are the Boogeyman!");
                sender.sendMessage("You need to kill someone who isn't a red to be cured!");
            }
            return true;
        }
        return true;
    }

    public void saveBoogeymanToFile(String boogeymanName) {
        try {
            File dataFolder = this.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File boogeymanFile = new File(dataFolder, "boogeyman.txt");
            FileWriter writer = new FileWriter(boogeymanFile);
            PrintWriter printer = new PrintWriter(writer);
            printer.println(boogeymanName);
            printer.println(System.currentTimeMillis());
            printer.close();
            this.getLogger().info("Boogeyman saved to file: " + boogeymanName);
        } catch (IOException e) {
            this.getLogger().warning("Could not save boogeyman to file: " + e.getMessage());
        }

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

    private boolean canBePresident(Player p) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
        if (team == null) return false;
        String name = team.getName();
        return name.equals("DG6") || name.equals("DG5") || name.equals("DG4")
                || name.equals("G") || name.equals("Y");
    }

    private boolean isRed(String playerName) {
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(playerName);
        return t != null && t.getName().equals("R");
    }


    private void loseLife(String playerName) {
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(playerName);
        if (t == null) return;
        String next = switch (t.getName()) {
            case "DG6" -> "DG5";
            case "DG5" -> "DG4";
            case "DG4" -> "G";
            case "G" -> "Y";
            case "Y" -> "R";
            default -> null;   // R (or unknown) -> out of the game
        };
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team leave " + playerName);
        if (next != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join " + next + " " + playerName);
        } else if (t.getName().equals("R")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode spectator " + playerName);
        }
    }

    private void giveAssassinReward(Player assassin) {
        Random r = new Random();
        if (assassinRewarded) return;   // never reward twice
        assassinRewarded = true;
        Location assloc = assassin.getLocation();

        double assx = assloc.getX();
        double assy = assloc.getY() + 2;
        double assz = assloc.getZ();


        Bukkit.getScheduler().runTaskLater(this, () -> {
            assassin.sendMessage("§6Congratulations! Here is your reward.");
        }, 60L);
        // Food

        Bukkit.getScheduler().runTaskLater(this, () -> {

            int f = r.nextInt(5);
            if (f == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:bread\",count:32}}");
            } else if (f == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:cooked_beef\",count:28}}");
            } else if (f == 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:golden_carrot\",count:20}}");
            } else if (f == 3) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:golden_apple\",count:8}}");
            } else if (f == 4) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:apple\",count:16}}");
            }
        }, 40L);

        // Blocks
        Bukkit.getScheduler().runTaskLater(this, () -> {

            int f = r.nextInt(5);
            if (f == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:cobbled_deepslate\",count:48}}");
            } else if (f == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:oak_log\",count:38}}");
            } else if (f == 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:obsidian\",count:20}}");
            } else if (f == 3) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:sand\",count:16}}");
            } else if (f == 4) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:blue_ice\",count:16}}");
            }
        }, 40L);

        // Materials

        Bukkit.getScheduler().runTaskLater(this, () -> {

            int f = r.nextInt(6);
            if (f == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:emerald\",count:32}}");
            } else if (f == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:iron_ingot\",count:28}}");
            } else if (f == 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:diamond\",count:16}}");
            } else if (f == 3) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:netherite_upgrade_smithing_template\",count:1}}");
            } else if (f == 4) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:netherite_ingot\",count:1}}");
            } else if (f == 5) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"redstone\",count:48}}");
            }
        }, 40L);

        // extras

        Bukkit.getScheduler().runTaskLater(this, () -> {

            int f = r.nextInt(10);
            if (f == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:trident\",count:1}}");
            } else if (f == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:ender_pearl\",count:8}}");
            } else if (f == 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:breeze_rod\",count:12}}");
            } else if (f == 3) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:tnt_minecart\",count:1}}");
            } else if (f == 4) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"minecraft:silence_armor_trim_smithing_template\",count:1}}");
            } else if (f == 5) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"creeper_spawn_egg\",count:8}}");
            }  else if (f == 6) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"bogged_spawn_egg\",count:12}}");
            }  else if (f == 7) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"wither_skeleton_skull\",count:1}}");
            }  else if (f == 8) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"experience_bottle\",count:28}}");
            }  else if (f == 9) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon minecraft:item " + assx + " " + assy + " " + assz + " {Item:{id:\"ominous_trial_key\",count:1}}");
            }
        }, 40L);
    }



    private void tell(UUID id, String msg) {
        Player p = id == null ? null : Bukkit.getPlayer(id);
        if (p != null) p.sendMessage(msg);
    }

    private final Map<UUID, UUID> votedFor = new HashMap<>();

    private final Map<UUID, Integer> voteCounts = new HashMap<>();

    private UUID presidentId = null;

    private final Set<UUID> bodyguards = new LinkedHashSet<>();

    private final Set<UUID> diedTonight = new HashSet<>();

    private UUID assassinId = null;

    private boolean electionActive = false;

    private String nameOf(UUID id) {
        String name = Bukkit.getOfflinePlayer(id).getName();
        return name == null ? id.toString() : name;
    }

    private String statusOf(UUID id) {
        Player p = Bukkit.getPlayer(id);
        if (p == null) return " §8(offline)";
        if (diedTonight.contains(id)) return " §c(died tonight)";
        return " §a(online)";
    }

    private static final int MOON_LOOP = 10; // must match daysLoop in the pack

    private void applyMoonPhase(Player p, int moonType) {
        World overworld = Bukkit.getWorlds().get(0);
        int currentSlot = (int) ((overworld.getFullTime() / 24000L) % MOON_LOOP);
        long offsetDays = (moonType - currentSlot + MOON_LOOP) % MOON_LOOP;
        p.setPlayerTime(offsetDays * 24000L, true);
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws InterruptedException {
        Player player = event.getEntity();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        Player killerPlayer = player.getKiller();
        if (killerPlayer != null) {
            Map<String, Integer> boogeymen = readBoogeymen();
            Integer status = boogeymen.get(killerPlayer.getName());
            if (status != null && status == 0) {
                boogeymen.put(killerPlayer.getName(), 1); // got their kill
                writeBoogeymen(boogeymen);
                killerPlayer.sendMessage(new String[]{"§a§lCURSE LIFTED!", "§eYou are no longer the boogeyman"});
            }
        }
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
        if (electionActive) {
            diedTonight.add(player.getUniqueId());

            if (player.getUniqueId().equals(presidentId)) {
                // president died: bodyguards who are still alive lose a life (unless red)
                for (UUID id : bodyguards) {
                    if (diedTonight.contains(id)) continue;
                    String name = Bukkit.getOfflinePlayer(id).getName();
                    if (name != null && !isRed(name)) loseLife(name);
                }
                Player killer = player.getKiller();
                if (killer != null && killer.getUniqueId().equals(assassinId)) {
                    Bukkit.broadcastMessage("§4The assassin has killed the president!");

                    giveAssassinReward(killer);
                }
                electionActive = false;   // resolved, so morning doesn't resolve it again
            }
        }
    }

}

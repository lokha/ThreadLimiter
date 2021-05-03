package ru.sommerfugl.threadlimiter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class ThreadLimiter extends JavaPlugin implements CommandExecutor {

    private int threadLimit;
    private BukkitTask timer;

    @Override
    public void onEnable() {
        this.getCommand("threadlimiter").setExecutor(this);
        this.setupConfig();
    }

    public void reloadConfigParams() {
        if(timer != null) {
            try {
                timer.cancel();
            } catch (Exception ignored) {}
            timer = null;
        }
        threadLimit = this.getConfig().getInt(("limit-threads"), 300);
        timer = this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                int activeCount = Thread.activeCount();
                if(activeCount > threadLimit) {
                    System.out.println("Количество потоков " + activeCount + " выше лимита " + threadLimit + ", выключаем сервер..");
                    Bukkit.shutdown();
                }
            }
        }, 20, 20);
    }

    public void setupConfig() {
        this.saveDefaultConfig();
        this.reloadConfigParams();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Конфиг успешно перезагружен!");
        this.reloadConfig();
        this.reloadConfigParams();
        return true;
    }
}

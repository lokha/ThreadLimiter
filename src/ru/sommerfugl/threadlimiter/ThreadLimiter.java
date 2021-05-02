package ru.sommerfugl.threadlimiter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class ThreadLimiter extends JavaPlugin implements CommandExecutor {

    private String threadPermission;
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
        threadLimit = this.getConfig().getInt(("threadlimiter.limit-threads"), 300);
        threadPermission = this.getConfig().getString("threadlimiter.permission");
        timer = this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if(Thread.activeCount() > threadLimit) {
                    System.out.println("Количество потоков " + Thread.activeCount() + " выше лимита " + threadLimit + ", выключаем сервер..");
                    Bukkit.shutdown();
                }
            }
        }, 100L, 100L);
    }

    public void setupConfig() {
        this.saveDefaultConfig();
        this.reloadConfigParams();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       if(sender instanceof Player) {
           if (!sender.hasPermission(threadPermission)) {
               sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
               return true;
           }
           sender.sendMessage(ChatColor.GREEN + "Конфиг успешно перезагружен!");
           this.reloadConfig();
           this.reloadConfigParams();
           return true;
       }
       sender.sendMessage(ChatColor.GREEN + "Конфиг успешно перезагружен!");
       this.reloadConfig();
       this.reloadConfigParams();
       return true;
    }
}

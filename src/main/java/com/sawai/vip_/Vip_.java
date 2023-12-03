package com.sawai.vip_;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class Vip_ extends JavaPlugin implements CommandExecutor {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        luckPerms = LuckPermsProvider.get();
        getCommand("vip").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが使用できます。");
            return true;
        }

        Player player = (Player) sender;

        if (hasVipInvitation(player)) {
            grantVipPermission(player);
            removeVipInvitation(player);
            player.sendMessage(ChatColor.GREEN + "VIP権限を取得しました！有効期間は2週間です。");
        } else {
            player.sendMessage(ChatColor.RED + "VIP招待券がインベントリにありません。");
        }

        return true;
    }

    private boolean hasVipInvitation(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_INGOT) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "VIP招待券")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void grantVipPermission(Player player) {
        final Node vipNode = Node.builder("group.vip")
                .expiry(14, TimeUnit.DAYS) // 有効期間を2週間に設定
                .build();
        luckPerms.getUserManager().modifyUser(
                player.getUniqueId(),
                user -> user.data().add(vipNode));
    }

    private void removeVipInvitation(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_INGOT) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "VIP招待券")) {
                    meta.setDisplayName(ChatColor.GOLD + "VIP招待券");
                    item.setItemMeta(meta);

                    int amount = item.getAmount();
                    if (amount > 1) {
                        item.setAmount(amount - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                    break;
                }
            }
        }
    }
}

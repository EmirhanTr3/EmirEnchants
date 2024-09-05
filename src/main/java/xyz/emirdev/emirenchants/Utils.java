package xyz.emirdev.emirenchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Objects;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static TypedKey<Enchantment> createEnchantmentKey(String key) {
        return TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", key));
    }

    public static void giveOrDrop(Player player, Block block, ItemStack item) {
        PlayerInventory inventory = player.getInventory();

        long inventoryUsedSlotCount = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).count();
        long armorUsedSlotCount = Arrays.stream(inventory.getArmorContents()).filter(Objects::nonNull).count();

        if ((inventoryUsedSlotCount - armorUsedSlotCount - (inventory.getItemInOffHand().getType() != Material.AIR ? 1 : 0)) < 36) {
            inventory.addItem(item);
        } else {
            block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), item);
        }
    }
}
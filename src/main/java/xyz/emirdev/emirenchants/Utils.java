package xyz.emirdev.emirenchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static TypedKey<Enchantment> createEnchantmentKey(String key) {
        return TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", key));
    }
}
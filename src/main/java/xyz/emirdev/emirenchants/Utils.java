package xyz.emirdev.emirenchants;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }
}
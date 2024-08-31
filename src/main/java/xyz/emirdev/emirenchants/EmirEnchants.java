package xyz.emirdev.emirenchants;

import io.papermc.paper.registry.TypedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("all")
public final class EmirEnchants extends JavaPlugin {
    public static EmirEnchants instance;
    public static Logger logger;
    public static List<TypedKey<Enchantment>> enchantments = new ArrayList<>();

    public static EmirEnchants getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("The instance of EmirUtils is null!");
        }
        return instance;
    }

    public static List<TypedKey<Enchantment>> getEnchantments() {
        return enchantments;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    @Override
    public void onEnable() {
        this.instance = this;
        this.logger = getLogger();

        getLogger().info("Registering events...");

        for (Class<?> clazz: new Reflections("xyz.emirdev.emirenchants.enchantments", new SubTypesScanner(false))
                .getSubTypesOf(CustomEnchantment.class)) {

            if (!Arrays.stream(clazz.getInterfaces()).toList().contains(Listener.class)) continue;

            try {
                getServer().getPluginManager().registerEvents((Listener) clazz.getConstructor().newInstance(), this);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                getLogger().log(Level.SEVERE, "Couldn't register event in " + clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "") + ": ", e);
                continue;
            }

            getLogger().info("Registered events in " + clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "") + ".");
        }

        getLogger().info("Loaded successfully.");
    }
}

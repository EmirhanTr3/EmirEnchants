package xyz.emirdev.emirenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        for (Class<?> clazz: new Reflections("xyz.emirdev.emirenchants.enchantments", new SubTypesScanner(false))
            .getSubTypesOf(CustomEnchantment.class)) {

            // get enchantment key
            TypedKey<Enchantment> key;
            try {
                key = (TypedKey<Enchantment>) clazz.getDeclaredField("key").get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                context.getLogger().error("Couldn't find the enchantment key in "+clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "")+": ", e);
                continue;
            }

            EmirEnchants.enchantments.add(key);
        }

        context.getLogger().info("Generating enchantments datapack...");
        EnchantmentDatapack.init(context.getLogger());
        context.getLogger().info("Generated enchantments datapack.");

        final LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();
        manager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze(), event -> {
            context.getLogger().info("Loading enchantments...");

            for (Class<?> clazz: new Reflections("xyz.emirdev.emirenchants.enchantments", new SubTypesScanner(false))
                .getSubTypesOf(CustomEnchantment.class)) {

                // get enchantment key
                TypedKey<Enchantment> key;
                try {
                    key = (TypedKey<Enchantment>) clazz.getDeclaredField("key").get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    context.getLogger().error("Couldn't find the enchantment key in "+clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "")+": ", e);
                    continue;
                }

                // get builder method
                Method builderMethod;
                try {
                    builderMethod = clazz.getDeclaredMethod("builder", RegistryFreezeEvent.class, EnchantmentRegistryEntry.Builder.class);
                } catch (NoSuchMethodException e) {
                    context.getLogger().error("Couldn't find the enchantment builder method in "+clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "")+": ", e);
                    continue;
                }

                // register enchantment
                event.registry().register(key, builder -> {
                    try {
                        builderMethod.invoke(null, event, builder);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        context.getLogger().error("Couldn't find the enchantment data in "+clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "")+": ", e);
                    }
                });

                String className = clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "");
                context.getLogger().info("Registered enchantment in class " + className + " named " + key.key().value());
            }

            context.getLogger().info("Loaded enchantments successfully.");
        });
    }
}
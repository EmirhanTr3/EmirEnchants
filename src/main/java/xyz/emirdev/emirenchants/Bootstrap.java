package xyz.emirdev.emirenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        TypedKey<Enchantment> reachEnchant = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", "reach"));
        TagKey<ItemType> enchantableMiningTag = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

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

            context.getLogger().info("Loaded enchantments successfully");
        });
    }
}
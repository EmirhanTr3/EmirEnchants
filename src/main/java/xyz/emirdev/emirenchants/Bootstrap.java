package xyz.emirdev.emirenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import xyz.emirdev.emirenchants.tags.ToolsTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("all")
public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        final LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            final PreFlattenTagRegistrar<ItemType> registrar = event.registrar();
            context.getLogger().info("Loading tags...");

            for (Class<?> clazz: new Reflections("xyz.emirdev.emirenchants.tags", new SubTypesScanner(false))
                .getSubTypesOf(CustomTag.class)) {
                String className = clazz.getName().replace("xyz.emirdev.emirenchants.tags.", "");

                // get tag key
                TagKey<ItemType> key;
                try {
                    key = (TagKey<ItemType>) clazz.getDeclaredField("key").get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    context.getLogger().error("Couldn't find the tag key in "+ className +": ", e);
                    continue;
                }

                // get tags
                List<TagEntry<ItemType>> tags;
                try {
                    tags = (List<TagEntry<ItemType>>) clazz.getDeclaredField("tags").get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    context.getLogger().error("Couldn't find the tags in "+ className +": ", e);
                    continue;
                }

                registrar.setTag(key, tags);
                context.getLogger().info("Registered tag in class " + className + " named " + key.key().value());
            }

            context.getLogger().info("Loaded tags successfully.");
        });

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
        context.getLogger().info("Loaded list of all enchantments.");

        manager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze(), event -> {
            context.getLogger().info("Loading enchantments...");

            for (Class<?> clazz: new Reflections("xyz.emirdev.emirenchants.enchantments", new SubTypesScanner(false))
                .getSubTypesOf(CustomEnchantment.class)) {
                String className = clazz.getName().replace("xyz.emirdev.emirenchants.enchantments.", "");

                // get enchantment key
                TypedKey<Enchantment> key;
                try {
                    key = (TypedKey<Enchantment>) clazz.getDeclaredField("key").get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    context.getLogger().error("Couldn't find the enchantment key in "+className+": ", e);
                    continue;
                }

                // get builder method
                Method builderMethod;
                try {
                    builderMethod = clazz.getDeclaredMethod("builder", RegistryFreezeEvent.class, EnchantmentRegistryEntry.Builder.class);
                } catch (NoSuchMethodException e) {
                    context.getLogger().error("Couldn't find the enchantment builder method in "+className+": ", e);
                    continue;
                }

                // register enchantment
                event.registry().register(key, builder -> {
                    try {
                        builderMethod.invoke(null, event, builder);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        context.getLogger().error("Couldn't find the enchantment data in "+className+": ", e);
                    }
                });

                context.getLogger().info("Registered enchantment in class " + className + " named " + key.key().value());
            }

            context.getLogger().info("Loaded enchantments successfully.");
        });

        manager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
            final PostFlattenTagRegistrar<Enchantment> registrar = event.registrar();
            registrar.addToTag(
                    EnchantmentTagKeys.IN_ENCHANTING_TABLE,
                    EmirEnchants.getEnchantments()
            );
        });

        context.getLogger().info("Registered enchantments to enchanting table.");
    }
}
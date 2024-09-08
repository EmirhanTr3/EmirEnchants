package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

import java.util.List;

@SuppressWarnings("all")
public class HeadshotEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("headshot");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_CROSSBOW.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Headshot"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 4))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 4))
                .anvilCost(4)
                .maxLevel(5)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        if (!event.getBow().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;
        int enchantLevel = event.getBow().getEnchantmentLevel(Registry.ENCHANTMENT.get(key));

        event.getProjectile().setMetadata("emirenchants:headshot", new FixedMetadataValue(EmirEnchants.getInstance(), enchantLevel));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!event.getDamager().hasMetadata("emirenchants:headshot")) return;
        int enchantLevel = event.getDamager().getMetadata("emirenchants:headshot").getFirst().asInt();

        if (List.of(EntityType.ARROW, EntityType.SPECTRAL_ARROW).contains(event.getDamager().getType()) && event.getEntity() instanceof LivingEntity hitEntity) {
            Location eyeLocation = hitEntity.getEyeLocation();
            Location projectileLocation = event.getDamager().getLocation();

            double minY = Math.min(eyeLocation.getY(), projectileLocation.getY());
            double maxY = Math.max(eyeLocation.getY(), projectileLocation.getY());
            double diff = maxY - minY;

            if (diff <= 0.3) {
                event.setDamage(event.getFinalDamage() + (event.getFinalDamage() / 100 * (enchantLevel * 10)));
            }
        }
    }
}

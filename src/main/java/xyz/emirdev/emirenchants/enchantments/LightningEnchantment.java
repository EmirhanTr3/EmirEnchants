package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

import java.util.List;

@SuppressWarnings("all")
public class LightningEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("lightning");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_BOW.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Lightning"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 4))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 4))
                .anvilCost(4)
                .maxLevel(1)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        if (!event.getBow().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        event.getProjectile().setMetadata("emirenchants:lightning", new FixedMetadataValue(EmirEnchants.getInstance(), true));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!event.getEntity().hasMetadata("emirenchants:lightning")) return;

        if (event.getHitEntity() != null) {
            Entity hitEntity = event.getHitEntity();
            hitEntity.getWorld().spawnEntity(hitEntity.getLocation(), EntityType.LIGHTNING_BOLT);

        } else if (event.getHitBlock() != null) {
            Block hitBlock = event.getHitBlock();
            hitBlock.getWorld().spawnEntity(hitBlock.getLocation(), EntityType.LIGHTNING_BOLT);
        }
    }
}

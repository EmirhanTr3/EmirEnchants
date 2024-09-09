package xyz.emirdev.emirenchants.tags;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomTag;
import xyz.emirdev.emirenchants.Utils;

import java.util.List;

@SuppressWarnings("all")
public class ToolsTag extends CustomTag {
    public static TagKey<ItemType> key = Utils.createItemTagKey("tools");

    public static List<TagEntry<ItemType>> tags = List.of(
            TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_MINING, true),
            TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON, true)
    );
}

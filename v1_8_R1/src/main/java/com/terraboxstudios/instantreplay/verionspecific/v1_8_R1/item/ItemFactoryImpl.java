package com.terraboxstudios.instantreplay.verionspecific.v1_8_R1.item;

import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactoryImpl implements ItemFactory {

    @Override
    public ItemStack getEmptyItemGUIPlaceholder() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        return item;
    }

}

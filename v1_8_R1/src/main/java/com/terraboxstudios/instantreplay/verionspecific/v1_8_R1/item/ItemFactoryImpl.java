package com.terraboxstudios.instantreplay.verionspecific.v1_8_R1.item;

import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemFactoryImpl implements ItemFactory {

    @Override
    public ItemStack getEmptyItemGUIPlaceholder() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
    }

    @Override
    public ItemStack getHealthItemGUI() {
        return new ItemStack(Material.SPECKLED_MELON, 1);
    }

}

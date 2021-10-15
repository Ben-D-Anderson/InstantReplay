package com.terraboxstudios.instantreplay.versionspecific.v1_10_R1.item;

import com.terraboxstudios.instantreplay.versionspecific.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemFactoryImpl implements ItemFactory {

    @Override
    public ItemStack[] getHands(PlayerInventory playerInventory) {
        return new ItemStack[]{playerInventory.getItemInMainHand(), playerInventory.getItemInOffHand()};
    }

    @Override
    public ItemStack getEmptyItemGUIPlaceholder() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
    }

    @Override
    public ItemStack getHealthItemGUI() {
        return new ItemStack(Material.SPECKLED_MELON, 1);
    }

}

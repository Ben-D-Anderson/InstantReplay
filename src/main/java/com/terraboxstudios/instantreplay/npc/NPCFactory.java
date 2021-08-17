package com.terraboxstudios.instantreplay.npc;

import com.terraboxstudios.instantreplay.Main;
import com.terraboxstudios.instantreplay.npc.nms.NPC_1_7_R4;
import com.terraboxstudios.instantreplay.util.SkinCache;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class NPCFactory {

    private static NPCFactory instance;
    private final String version;

    public static NPCFactory getInstance() {
        if (instance == null) instance = new NPCFactory();
        return instance;
    }

    public NPCFactory() {
        version = getVersion();
    }

    private String getVersion() {
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1).replace("v_", "").replace("v", "");
    }

    public void setSkin(GameProfile profile, UUID uuid) {
        try {
            if (SkinCache.isSkinCached(uuid)) {
                profile.getProperties().put("textures", SkinCache.getCachedSkin(uuid));
                return;
            }
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = convertStreamToString(connection.getInputStream());
                String skin, signature;
                try {
                    skin = reply.split("\"value\":\"")[1].split("\"")[0];
                    signature = reply.split("\"signature\":\"")[1].split("\"")[0];
                } catch (ArrayIndexOutOfBoundsException e) {
                    skin = reply.split("\"value\" : \"")[1].split("\"")[0];
                    signature = reply.split("\"signature\" : \"")[1].split("\"")[0];
                }
                Property property = new Property("textures", skin, signature);
                profile.getProperties().put("textures", property);
                connection.disconnect();
                SkinCache.addToCachedSkins(uuid, property);
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> SkinCache.removeCachedSkin(uuid), 60 * 20L);
            } else {
                Bukkit.getLogger().log(Level.SEVERE, "[REPLAY ERROR] Could not load skin for " + profile.getName() + " - Reason: " + connection.getResponseMessage());
            }
        } catch (IOException ignored) {
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public NPC createNPC(Player viewer, String npcName, UUID npcUUID) throws ClassNotFoundException {
        switch (version) {
            case "1_7_R4":
                return new NPC_1_7_R4(viewer, npcName, npcUUID);
            default:
                throw new ClassNotFoundException("Minecraft version not supported");
        }
    }

}

package com.terraboxstudios.instantreplay.verionspecific.v1_8_R1.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPC;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCFactory;
import com.terraboxstudios.instantreplay.versionspecific.npc.NPCSkin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class NPCFactoryImpl extends NPCFactory {

    @Override
    public NPCSkin<GameProfile> createSkin(UUID uniqueId, String name) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uniqueId))).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                String skin, signature;
                try {
                    skin = reply.split("\"value\":\"")[1].split("\"")[0];
                    signature = reply.split("\"signature\":\"")[1].split("\"")[0];
                } catch (Exception e) {
                    skin = reply.split("\"value\": \"")[1].split("\"")[0];
                    signature = reply.split("\"signature\": \"")[1].split("\"")[0];
                }
                profile.getProperties().put("textures", new Property("textures", skin, signature));
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't contact Mojang skin servers.");
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't parse data from Mojang skin servers. Error: " + e.getLocalizedMessage());
        }
        return new NPCSkinImpl(profile);
    }

    @Override
    public NPC createNPC(UUID viewer, UUID uniqueId, String name, NPCSkin<?> skin, World world) {
        return new NPCImpl(viewer, uniqueId, name, skin, world);
    }

}

package net.thedgtl.stargate;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PMListener implements PluginMessageListener {
    private final Stargate stargate;

    public PMListener(Stargate stargate) {
        this.stargate = stargate;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player unused, byte[] message) {
        if (!stargate.enableBungee || !channel.equals("BungeeCord")) {
            return;
        }

        // Get data from message
        String inChannel;
        byte[] data;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            inChannel = in.readUTF();
            short len = in.readShort();
            data = new byte[len];
            in.readFully(data);
        } catch (IOException ex) {
            stargate.getLogger().severe("Error receiving BungeeCord message");
            ex.printStackTrace();
            return;
        }

        // Verify that it's an SGBungee packet
        if (!inChannel.equals("SGBungee")) {
            return;
        }

        // Data should be player name, and destination gate name
        String msg = new String(data);
        String[] parts = msg.split("#@#");

        String playerName = parts[0];
        String destination = parts[1];

        // Check if the player is online, if so, teleport, otherwise, queue
        Player player = stargate.getServer().getPlayer(playerName);
        if (player == null) {
            stargate.bungeeQueue.put(playerName.toLowerCase(), destination);
        } else {
            Portal dest = Portal.getBungeeGate(destination);
            // Specified an invalid gate. For now we'll just let them connect at their current location
            if (dest == null) {
                stargate.getLogger().info("Bungee gate " + destination + " does not exist");
                return;
            }
            dest.teleport(player, dest, null);
        }
    }
}

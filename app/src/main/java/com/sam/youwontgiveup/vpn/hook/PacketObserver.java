package com.sam.youwontgiveup.vpn.hook;

import android.util.Log;

import com.sam.youwontgiveup.vpn.core.Packet;

public class PacketObserver {

    private static final String TAG = "PacketObserver";

    public static void inspect(Packet packet) {
        if (packet.isUDP()) {

            int src = packet.udpHeader.sourcePort;
            int dst = packet.udpHeader.destinationPort;

            if (src == 53 || dst == 53) {

                Log.d("DNS-ycty", "Detected DNS packet");

                String domain = DnsParser.extract(packet.backingBuffer);

                Log.d("DNS-ycty", "Returned: " + domain);
            }
        }
    }

}
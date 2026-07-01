package com.sam.youwontgiveup.vpn.hook;

import java.nio.ByteBuffer;

public class DnsParser {

    public static String extract(ByteBuffer payload) {

        ByteBuffer dns = payload.duplicate();

        dns.position(dns.position() + 12);

        StringBuilder domain = new StringBuilder();

        while (dns.hasRemaining()) {

            int len = dns.get() & 0xff;

            if (len == 0)
                break;

            if (domain.length() > 0)
                domain.append('.');

            byte[] label = new byte[len];

            dns.get(label);

            domain.append(new String(label));
        }

        return domain.length() == 0
                ? null
                : domain.toString();
    }
}
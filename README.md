# YouWontGiveUp

An experimental Android application designed to interrupt distracting browsing habits by detecting websites and displaying a fullscreen motivational overlay.

> **Project Status:** Archived / Unfinished

## Why I Built This

The goal was to create an Android app that could detect when I visited distracting websites (YouTube, Instagram, Reddit, etc.) and interrupt me with a 30-second fullscreen overlay before allowing me to continue.

Unlike traditional website blockers, I wanted a short interruption instead of permanently blocking access.

---

## Features Implemented

- Android VPN using `VpnService`
- Local TCP/UDP packet forwarding
- DNS packet interception
- DNS domain extraction
- SQLite database for blocked websites
- History logging
- Quote database
- Bottom navigation UI
- Website management screen
- Fullscreen overlay prototype

---

## What Worked

- Successfully built a working local VPN.
- Successfully intercepted TCP and UDP packets.
- Successfully parsed DNS packets.
- Successfully extracted requested domains from DNS queries.

Example:

```
www.instagram.com
static.cdninstagram.com
www.facebook.com
```

The project was able to detect many websites directly from DNS traffic.

---

## Current Problems

The VPN implementation is based on the excellent LocalVPN project:

https://github.com/hexene/LocalVPN

However, the project is several years old and was never intended to be production ready.

Issues encountered:

- Intermittent connection failures.
- Random `Connection reset by peer` errors.
- IPv6 packets are not handled.
- Modern Android networking is not fully supported.
- Website detection becomes unreliable because of the above issues.

Because of these limitations, I decided not to continue developing this VPN implementation.

---

## What I Learned

This project taught me a lot about Android networking.

Topics explored include:

- Android `VpnService`
- Raw IP packets
- TCP forwarding
- UDP forwarding
- DNS protocol
- Java NIO
- Selectors
- SocketChannel
- DatagramChannel
- Android overlays
- SQLite
- Packet parsing

---

This repository is provided for learning purposes.

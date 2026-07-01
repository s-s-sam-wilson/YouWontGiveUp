package com.sam.youwontgiveup.vpn

import com.sam.youwontgiveup.database.DatabaseHelper
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.os.ParcelFileDescriptor
import com.sam.youwontgiveup.ui.overlay.OverlayManager


class FocusVpnService : VpnService() {
    companion object {
        var isRunning = false

        const val ACTION_STOP = "STOP_VPN"

        private val overlayCooldowns = HashMap<String, Long>()

        private var lastSavedDomain = ""

        private var lastSavedTime = 0L

        private const val OVERLAY_COOLDOWN = 10 * 60 * 1000L

        private const val HISTORY_COOLDOWN = 30 * 1000L
    }

    private var vpnInterface: ParcelFileDescriptor? = null

    private lateinit var overlayManager: OverlayManager

    private val ignoredDomains = setOf(
        "dns.google",
        "connectivitycheck.gstatic.com"
    )

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACTION_STOP) {

            vpnInterface?.close()
            vpnInterface = null

            isRunning = false

            stopSelf()

            return START_NOT_STICKY
        }

        val builder = Builder()

        builder.setSession("You Won't Give Up")

        builder.addAddress(
            "10.0.0.2",
            24
        )

        builder.addDnsServer("8.8.8.8")

        builder.addRoute(
            "0.0.0.0",
            0
        )

        vpnInterface = builder.establish()

        overlayManager = OverlayManager(this)

        if (vpnInterface != null) {

            isRunning = true

            val inputStream =
                java.io.FileInputStream(
                    vpnInterface!!.fileDescriptor
                )

            Thread {

                val packet = ByteArray(32767)

                val databaseHelper = DatabaseHelper(this@FocusVpnService)

                while (isRunning) {

                    try {

                        val length =
                            inputStream.read(packet)

                        if (length > 0) {
                            val protocol =
                                packet[9].toInt() and 0xFF

//                            android.util.Log.d(
//                                "VPN",
//                                "Protocol=$protocol Size=$length bytes"
//                            )
                            val ipHeaderLength =
                                (packet[0].toInt() and 0x0F) * 4

                            val dnsOffset = ipHeaderLength + 8
                            val queryOffset = dnsOffset + 12

                            val destPort =
                                ((packet[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or
                                        (packet[ipHeaderLength + 3].toInt() and 0xFF)

                            if (protocol == 17 && destPort == 53) {

                                val dnsOffset = ipHeaderLength + 8
                                var pos = dnsOffset + 12

                                val domain = StringBuilder()

                                while (true) {

                                    val len = packet[pos].toInt() and 0xFF

                                    if (len == 0) break

                                    pos++

                                    for (i in 0 until len) {
                                        domain.append(packet[pos + i].toInt().toChar())
                                    }

                                    pos += len

                                    domain.append('.')
                                }

                                val finalDomain =
                                    normalizeDomain(
                                        domain.toString().removeSuffix(".")
                                    )

                                if (finalDomain !in ignoredDomains) {

                                    val now = System.currentTimeMillis()

                                    if (
                                        finalDomain != lastSavedDomain ||
                                        now - lastSavedTime > HISTORY_COOLDOWN
                                    ) {

                                        lastSavedDomain = finalDomain
                                        lastSavedTime = now

                                        android.util.Log.d(
                                            "VPN",
                                            "DOMAIN: $finalDomain"
                                        )

                                        databaseHelper.addHistory(
                                            finalDomain,
                                            System.currentTimeMillis()
                                        )



                                        // Database save comes next
                                    }

                                    if (!databaseHelper.isUrlBlocked(finalDomain)) {
                                        continue
                                    }

                                    val lastOverlay =
                                        overlayCooldowns[finalDomain] ?: 0L

                                    if (
                                        now - lastOverlay >= OVERLAY_COOLDOWN
                                    ) {

                                        overlayCooldowns[finalDomain] = now

                                        overlayManager.show(
                                            this@FocusVpnService,
                                            finalDomain
                                        )

                                        // Later:
                                        // showOverlay(finalDomain)
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {

                        android.util.Log.e(
                            "VPN",
                            "Read error",
                            e
                        )

                        break
                    }
                }

            }.start()
        }

        return START_NOT_STICKY
    }

    private fun normalizeDomain(domain: String): String {

        var d = domain.lowercase()

        if (d.startsWith("www."))
            d = d.removePrefix("www.")

        val parts = d.split(".")

        if (parts.size >= 2) {
            d = parts.takeLast(2).joinToString(".")
        }

        return d
    }

    override fun onDestroy() {

        isRunning = false

        vpnInterface?.close()
        vpnInterface = null

        overlayManager.remove()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }


}
package com.sam.youwontgiveup.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sam.youwontgiveup.databinding.FragmentHomeBinding
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.result.contract.ActivityResultContracts
import com.sam.youwontgiveup.vpn.FocusVpnService
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.sam.youwontgiveup.vpn.core.FocusVpnEngine

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vpnPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val serviceIntent = Intent(
                    requireContext(),
                    FocusVpnService::class.java
                )

                requireContext().startService(serviceIntent)

                binding.txtStatus.text = "ACTIVE"
            }
        }

    private val overlayPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (Settings.canDrawOverlays(requireContext())) {

                binding.cardOverlayPermission.visibility =
                    View.GONE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )

        val root = binding.root

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Settings.canDrawOverlays(requireContext())
        ) {

            binding.cardOverlayPermission.visibility =
                View.VISIBLE

        } else {

            binding.cardOverlayPermission.visibility =
                View.GONE
        }

        if (FocusVpnService.isRunning) {

            binding.txtStatus.text = "ACTIVE"
            binding.btnVpn.text = "STOP VPN"

        } else {

            binding.txtStatus.text = "INACTIVE"
            binding.btnVpn.text = "START VPN"
        }

        binding.btnVpn.setOnClickListener {

            if (FocusVpnService.isRunning) {

                val serviceIntent = Intent(
                    requireContext(),
                    FocusVpnService::class.java
                )

                serviceIntent.action = FocusVpnService.ACTION_STOP

                requireContext().startService(serviceIntent)

                binding.txtStatus.text = "INACTIVE"
                binding.btnVpn.text = "START VPN"

            } else {

                val intent =
                    VpnService.prepare(requireContext())

                if (intent != null) {

                    vpnPermissionLauncher.launch(intent)

                } else {

                    val serviceIntent = Intent(
                        requireContext(),
                        FocusVpnEngine::class.java
                    )

                    requireContext().startService(serviceIntent)

                    binding.txtStatus.text = "ACTIVE"
                    binding.btnVpn.text = "STOP VPN"
                }
            }
        }

        binding.btnOverlayPermission.setOnClickListener {

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse(
                    "package:${requireContext().packageName}"
                )
            )

            overlayPermissionLauncher.launch(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
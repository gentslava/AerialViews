package com.neilturner.aerialviews.ui.settings

import android.media.MediaCodecList
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.neilturner.aerialviews.R
import com.neilturner.aerialviews.services.CodecType
import com.neilturner.aerialviews.services.HDRFormat
import com.neilturner.aerialviews.services.getCodecs
import com.neilturner.aerialviews.services.getDisplay

class CapabilitiesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_capabilities, rootKey)
        updateCapabilities()
    }

    private fun updateCapabilities()
    {
        val display = findPreference<ListPreference>("capabilities_display") as Preference
        val resolution = findPreference<ListPreference>("capabilities_resolution") as Preference
        val codecs = findPreference<ListPreference>("capabilities_codecs") as Preference
        val decoders = findPreference<ListPreference>("capabilities_decoders") as Preference

        display.summary = buildDisplaySummary()
        codecs.summary = buildCodecSummary()
        decoders.summary = buildDecoderSummary()
        resolution.summary = buildResolutionSummary()
    }

    private fun buildDisplaySummary(): String {
        var summary = ""
        var supportsHDR10 = "False"
        var supportsDolbyVision = "False"

        val display = getDisplay(activity)
        if (display.supportsHDR && display.hdrFormats.isNotEmpty()) {
            if (display.hdrFormats.contains(HDRFormat.DOLBY_VISION))
                supportsDolbyVision = "True"
            if (display.hdrFormats.contains(HDRFormat.HDR10))
                supportsHDR10 = "True"
        }

        summary += "Supports HDR10: $supportsHDR10\n"
        summary += "Supports Dolby Vision: $supportsDolbyVision"

        return summary
    }

    private fun buildResolutionSummary(): String {
        val display = getDisplay(activity)
        var summary = "Render Output: ${display.renderOutput}"
        if (display.physicalOutput != null) {
            summary += "\nPhysical Output: ${display.physicalOutput}"
        }
        return summary
    }

    private fun buildCodecSummary(): String {
        var summary = ""
        val foundAVC = "Found"
        var foundHEVC = "Not Found"
        var foundDolbyVision = "Not Found"

        getCodecs().forEach{ codec ->

            if (codec.name.lowercase().contains("hevc") &&
                codec.isHardwareAccelerated &&
                codec.codingFunction == CodecType.DECODER) {
                foundHEVC = "Found"
            }


            if (codec.name.lowercase().contains("dolby") && codec.codingFunction == CodecType.DECODER) {
                foundDolbyVision = "Found (does not guarantee HDR playback)"
            }
        }

        summary += "AVC: $foundAVC\n"
        summary += "HEVC: $foundHEVC\n"
        summary += "Dolby Vision: $foundDolbyVision"

        return summary
    }

    private fun buildDecoderSummary(): String {
        var summary = ""

        val allCodecs = getCodecs().filter {
            it.codingFunction == CodecType.DECODER && isVideoCodec(it.mimeTypes)
        }

        if (allCodecs.isNotEmpty())
            summary = allCodecs.joinToString (", ", "", "", -1, "") { it.name }

        Log.i("", "Decoders found: ${allCodecs.count()}")
        return summary
    }

    private fun isVideoCodec(codecs: Array<String>): Boolean {
        val videoCodecs = codecs.filter { it.contains("video", true) &&
                (it.contains("avc", true) ||
                it.contains("hevc", true) ||
                it.contains("dolby", true))
        }



        return videoCodecs.count() > 0
    }
}
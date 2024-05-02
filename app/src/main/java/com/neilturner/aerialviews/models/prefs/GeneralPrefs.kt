package com.neilturner.aerialviews.models.prefs

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.nullableEnumValuePref
import com.neilturner.aerialviews.models.enums.ClockType
import com.neilturner.aerialviews.models.enums.DateType
import com.neilturner.aerialviews.models.enums.FilenameAsLocation
import com.neilturner.aerialviews.models.enums.ImageScale
import com.neilturner.aerialviews.models.enums.LocationType
import com.neilturner.aerialviews.models.enums.OverlayType

object GeneralPrefs : KotprefModel() {
    override val kotprefName = "${context.packageName}_preferences"

    // Overlays
    var slotBottomLeft1 by nullableEnumValuePref(OverlayType.CLOCK, "slot_bottom_left1")
    var slotBottomLeft2 by nullableEnumValuePref(OverlayType.EMPTY, "slot_bottom_left2")
    var slotBottomRight1 by nullableEnumValuePref(OverlayType.LOCATION, "slot_bottom_right1")
    var slotBottomRight2 by nullableEnumValuePref(OverlayType.EMPTY, "slot_bottom_right2")

    var slotTopLeft1 by nullableEnumValuePref(OverlayType.EMPTY, "slot_top_left1")
    var slotTopLeft2 by nullableEnumValuePref(OverlayType.EMPTY, "slot_top_left2")
    var slotTopRight1 by nullableEnumValuePref(OverlayType.EMPTY, "slot_top_right1")
    var slotTopRight2 by nullableEnumValuePref(OverlayType.EMPTY, "slot_top_right2")

    // Clock
    var clockFormat by nullableEnumValuePref(ClockType.DEFAULT, "clock_format")
    var clockSize by stringPref("18", "clock_size")
    var clockWeight by stringPref("300", "clock_weight")
    var clockForceLatinDigits by booleanPref(false, "clock_force_latin_digits")

    // Date
    var dateFormat by nullableEnumValuePref(DateType.COMPACT, "date_format")
    var dateCustom by stringPref("yyyy-MM-dd", "date_custom")
    var dateWeight by stringPref("300", "date_weight")
    var dateSize by stringPref("18", "date_size")

    // Location
    var locationStyle by nullableEnumValuePref(LocationType.POI, "location_style")
    var locationSize by stringPref("18", "location_size")
    var locationWeight by stringPref("300", "location_weight")
    var filenameAsLocation by nullableEnumValuePref(FilenameAsLocation.DISABLED, "filename_as_location") // location_use_filename ?

    // Message
    var messageLine1 by stringPref("", "message_line1")
    var messageLine2 by stringPref("", "message_line2")
    var messageSize by stringPref("18", "message_size")
    var messageWeight by stringPref("300", "message_weight")

    // Other
    var alternateTextPosition by booleanPref(false, "alt_text_position")

    // Startup + Shutdown
    var showLoadingText by booleanPref(false, "startup_show_loading")
    var startupSize by stringPref("18", "startup_size")
    var startupWeight by stringPref("300", "startup_weight")

    // Gradients
    var showTopGradient by booleanPref(false, "gradient_top_show")
    var showBottomGradient by booleanPref(false, "gradient_bottom_show")

    // Fonts
    var fontTypeface by stringPref("open-sans", "font_typeface")
    var fontWeight by stringPref("300", "font_weight")

    // Locale
    var localeMenu by stringPref("default", "locale_menu")
    var localeScreensaver by stringPref("default", "locale_screensaver")

    // Playlist
    var muteVideos by booleanPref(true, "mute_videos")
    var shuffleVideos by booleanPref(true, "shuffle_videos")
    var removeDuplicates by booleanPref(true, "remove_duplicates")
    var playbackSpeed by stringPref("1", "playback_speed")
    var maxVideoLength by stringPref("0", "playback_max_video_length")
    var ignoreNonManifestVideos by booleanPref(false, "any_videos_ignore_non_manifest_videos")
    var slideshowSpeed by stringPref("30", "slideshow_speed")
    var imageScale by nullableEnumValuePref(ImageScale.CENTER_CROP, "image_scale")

    // D-pad
    var enableSkipVideos by booleanPref(true, "enable_skip_videos")
    var enablePlaybackSpeedChange by booleanPref(false, "enable_playback_speed_change")
    var enableMediaButtonPassthrough by booleanPref(true, "enable_media_button_passthrough")

    // Advanced
    var enableTunneling by booleanPref(true, "enable_tunneling")
    var refreshRateSwitching by booleanPref(false, "refresh_rate_switching")
    var allowFallbackDecoders by booleanPref(false, "allow_fallback_decoders")
    var enablePlaybackLogging by booleanPref(false, "enable_playback_logging")
    var philipsDolbyVisionFix by booleanPref(false, "philips_dolby_vision_fix")

    // Old devices
    var checkForHevcSupport by booleanPref(false, "check_for_hevc_support")
}

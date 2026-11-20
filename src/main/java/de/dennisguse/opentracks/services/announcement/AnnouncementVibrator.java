package de.dennisguse.opentracks.services.announcement;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Issues a strong vibration to accompany announcements.
 */
class AnnouncementVibrator {

    private static final String TAG = AnnouncementVibrator.class.getSimpleName();

    // Alternating off/on durations in ms, starting with an initial "off" of 0 ms.
    private static final long[] TIMINGS = {0, 500, 200, 500, 200, 500};
    private static final int[] AMPLITUDES = {0, 255, 0, 255, 0, 255};

    private AnnouncementVibrator() {
    }

    static void vibrate(@NonNull Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null || !vibrator.hasVibrator()) {
            Log.w(TAG, "No vibrator available.");
            return;
        }

        // Devices without amplitude control treat any non-zero amplitude as "on" at their fixed strength.
        VibrationEffect effect = VibrationEffect.createWaveform(TIMINGS, AMPLITUDES, -1);
        // Use ALARM usage so the vibration isn't dropped e.g. while battery saver is active.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM));
        } else {
            vibrator.vibrate(effect, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build());
        }
    }

    @Nullable
    private static Vibrator getVibrator(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
        }
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
}

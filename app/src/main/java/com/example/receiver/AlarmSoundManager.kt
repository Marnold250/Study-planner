package com.example.receiver

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AlarmSoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private var ringtone: Ringtone? = null
    private var isToneLooping = false

    var activeAlarmTaskId = mutableStateOf<Int?>(null)
    var activeAlarmTaskTitle = mutableStateOf<String?>(null)
    var activeAlarmTaskSubject = mutableStateOf<String?>(null)
    var activeAlarmTaskDescription = mutableStateOf<String?>(null)

    fun startAlarm(context: Context, taskId: Int, taskTitle: String, taskSubject: String, taskDescription: String, soundName: String) {
        Log.d("AlarmSoundManager", "Starting alarm for task $taskId ($taskTitle) with sound $soundName")
        stopAlarm()

        activeAlarmTaskId.value = taskId
        activeAlarmTaskTitle.value = taskTitle
        activeAlarmTaskSubject.value = taskSubject
        activeAlarmTaskDescription.value = taskDescription

        try {
            when (soundName) {
                "Digital Chime" -> {
                    // Loop Digital Chime
                    isToneLooping = true
                    CoroutineScope(Dispatchers.Default).launch {
                        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                        while (isToneLooping) {
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                            delay(1000)
                        }
                    }
                }
                "Classic Bell" -> {
                    // Loop Classic Bell
                    isToneLooping = true
                    CoroutineScope(Dispatchers.Default).launch {
                        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                        while (isToneLooping) {
                            toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 500)
                            delay(1200)
                        }
                    }
                }
                "Gentle Harp" -> {
                    // Loop Gentle Harp
                    isToneLooping = true
                    CoroutineScope(Dispatchers.Default).launch {
                        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 85)
                        while (isToneLooping) {
                            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                            delay(200)
                            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                            delay(200)
                            toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                            delay(1500)
                        }
                    }
                }
                else -> {
                    // Default / other sounds play looping standard alarm or notification sound or custom URI
                    val alertUri = try {
                        if (soundName.startsWith("content://") || soundName.startsWith("file://") || soundName.startsWith("android.resource://")) {
                            Uri.parse(soundName)
                        } else {
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                        }
                    } catch (e: Exception) {
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    }
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, alertUri)
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                        )
                        isLooping = true
                        prepare()
                        start()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error in startAlarm, falling back to default ringtone: ${e.message}", e)
            try {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ringtone = RingtoneManager.getRingtone(context, alertUri)?.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        isLooping = true
                    }
                    play()
                }
            } catch (ex: Exception) {
                Log.e("AlarmSoundManager", "Fallback alarm player failed: ${ex.message}", ex)
            }
        }
    }

    fun stopAlarm() {
        Log.d("AlarmSoundManager", "Stopping active alarm")
        isToneLooping = false
        activeAlarmTaskId.value = null
        activeAlarmTaskTitle.value = null
        activeAlarmTaskSubject.value = null
        activeAlarmTaskDescription.value = null

        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error stopping MediaPlayer", e)
        } finally {
            mediaPlayer = null
        }

        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error stopping Ringtone", e)
        } finally {
            ringtone = null
        }
    }
}

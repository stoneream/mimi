package net.yap_yap_dog.mimi.repository

import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import android.util.Log

abstract class MidiDeviceRepository {
    abstract fun getDevices(): List<MidiDeviceInfo>
    abstract fun openDevice(
        targetDevice: MidiDeviceInfo,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    )

    abstract fun closeDevice(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    )

    /***
     * sendNoteOn
     * @param channel [0-15]
     * @param noteNumber
     * @param velocity [0-127]
     * @param onError error handler
     */
    abstract fun sendNoteOn(
        channel: Int,
        noteNumber: Int,
        velocity: Int,
        onError: (message: String) -> Unit
    )

    /***
     * noteOff
     * @param channel [0-15]
     * @param noteNumber
     * @param onError error handler
     */
    abstract fun sendNoteOff(
        channel: Int,
        noteNumber: Int,
        onError: (message: String) -> Unit
    )

}

class MidiDeviceRepositoryImpl(
    private val midiManager: MidiManager
) : MidiDeviceRepository() {
    private val TAG = "${this.javaClass.name}"
    private var midiDevice: MidiDevice? = null
    private var midiInputPort: MidiInputPort? = null;

    private fun deviceInfo(deviceInfo: MidiDeviceInfo): String {
        val name = deviceInfo.properties.getString(MidiDeviceInfo.PROPERTY_NAME)
        return "${name}:${deviceInfo.id}(${deviceInfo.inputPortCount}/${deviceInfo.outputPortCount})"
    }

    override fun getDevices(): List<MidiDeviceInfo> {
        val devices = midiManager.devices

        val message = devices.joinToString(separator = ",") { deviceInfo(it) }
        Log.d("$TAG#getDevices", message)

        return devices.toList()
    }

    override fun openDevice(
        targetDevice: MidiDeviceInfo,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        try {
            midiManager.openDevice(
                targetDevice,
                { device ->
                    midiDevice = device
                    midiInputPort = device.openInputPort(0)

                    Log.d("$TAG#openDevice", deviceInfo(device.info))
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            Log.d("$TAG#openDevice", e.message, e)
            onError(e.message.orEmpty())
        }
    }

    override fun closeDevice(onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun sendNoteOn(
        channel: Int,
        noteNumber: Int,
        velocity: Int,
        onError: (message: String) -> Unit
    ) {
        if (midiInputPort != null) {
            try {
                val message = ByteArray(3)
                message[0] = (0x90 + channel).toByte()
                message[1] = noteNumber.toByte()
                message[2] = velocity.toByte()

                midiInputPort!!.send(message, 0, 3)
            } catch (e: Exception) {
                Log.d("$TAG#sendNoteOn", e.message, e)
                onError(e.message.orEmpty())
            }
        } else {
            Log.d("$TAG#sendNoteOff", "port isn't open")
            onError("port isn't open")
        }
    }

    override fun sendNoteOff(channel: Int, noteNumber: Int, onError: (message: String) -> Unit) {
        if (midiInputPort != null) {
            try {
                val message = ByteArray(3)
                message[0] = (0x80 + channel).toByte()
                message[1] = noteNumber.toByte()

                midiInputPort!!.send(message, 0, 3)
            } catch (e: Exception) {
                Log.d("$TAG#sendNoteOff", e.message, e)
                onError(e.message.orEmpty())
            }
        } else {
            Log.d("$TAG#sendNoteOff", "port isn't open")
            onError("port isn't open")
        }
    }
}
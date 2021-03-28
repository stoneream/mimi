package net.yap_yap_dog.mimi.service

import android.media.midi.MidiDeviceService
import android.media.midi.MidiReceiver

class MockMidiDeviceService : MidiDeviceService() {
    private val input = object : MidiReceiver() {
        override fun onSend(msg: ByteArray?, offset: Int, count: Int, timestamp: Long) {
        }
    }

    override fun onGetInputPortReceivers(): Array<MidiReceiver> {
        return arrayOf(input)
    }
}
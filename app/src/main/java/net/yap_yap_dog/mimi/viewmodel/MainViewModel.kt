package net.yap_yap_dog.mimi.viewmodel

import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiDeviceStatus
import android.media.midi.MidiManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.yap_yap_dog.mimi.model.Key
import net.yap_yap_dog.mimi.model.Pad
import net.yap_yap_dog.mimi.repository.MidiDeviceRepository

class MainViewModel(private val midiDeviceRepository: MidiDeviceRepository) : ViewModel() {
    val midiDevices: MutableLiveData<List<MidiDeviceInfo>> by lazy {
        MutableLiveData<List<MidiDeviceInfo>>(midiDeviceRepository.getDevices())
    }

    // 勝手に切り替える案...
    val deviceCallBack = object : MidiManager.DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo?) {
            super.onDeviceAdded(device)
            midiDevices.value = midiDeviceRepository.getDevices()
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo?) {
            super.onDeviceRemoved(device)
            midiDevices.value = midiDeviceRepository.getDevices()
        }

        override fun onDeviceStatusChanged(status: MidiDeviceStatus?) {
            super.onDeviceStatusChanged(status)
            midiDevices.value = midiDeviceRepository.getDevices()
        }
    }

    fun selectDevice(midiDeviceInfo: MidiDeviceInfo) {
        // 開けてるデバイスを取得する
        // すでに開いてる場合はオープンしない
        // 違うデバイスの場合はすでに開いてるものをクローズして新しくオープン
        midiDeviceRepository.openDevice(midiDeviceInfo, {}, {})
    }

    fun pressKey(pad: Pad, key: Key) {
        midiDeviceRepository.sendNoteOn(
            pad.channel,
            key.noteNumber,
            key.velocity,
            {}
        )
    }

    fun releaseKey(pad: Pad, key: Key) {
        midiDeviceRepository.sendNoteOff(
            pad.channel,
            key.noteNumber,
            {}
        )
    }

}
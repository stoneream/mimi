package net.yap_yap_dog.mimi.view

import android.media.midi.MidiDeviceInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import net.yap_yap_dog.mimi.R
import net.yap_yap_dog.mimi.databinding.FragmentPadConfigurationBinding
import net.yap_yap_dog.mimi.model.Key
import net.yap_yap_dog.mimi.model.Pad
import net.yap_yap_dog.mimi.viewmodel.PadConfigurationFragmentViewModel

class PadConfigurationFragment(
    private val midiDevices: MutableLiveData<List<MidiDeviceInfo>>,
    private val onMidiDeviceSelect: (MidiDeviceInfo) -> Unit,
    private val onCreatePad: (Pad) -> Unit
) : Fragment() {
    private lateinit var binding: FragmentPadConfigurationBinding
    private val vm = PadConfigurationFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pad_configuration,
            container,
            false
        )
        binding.vm = vm

        // midi device spinner
        val midiDeviceSpinnerAdapter = ArrayAdapter<String>(
            binding.root.context,
            android.R.layout.simple_spinner_dropdown_item,
            midiDevices.value?.map {
                it.properties.getString(MidiDeviceInfo.PROPERTY_NAME).orEmpty()
            }.orEmpty()
        )
        midiDevices.observeForever { value ->
            midiDeviceSpinnerAdapter.clear()
            midiDeviceSpinnerAdapter.addAll(value.map {
                it.properties.getString(MidiDeviceInfo.PROPERTY_NAME).orEmpty()
            })
            midiDeviceSpinnerAdapter.notifyDataSetChanged()
        }
        binding.midiDeviceSpinner.apply {
            adapter = midiDeviceSpinnerAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    midiDevices.value?.get(position)?.let {
                        onMidiDeviceSelect(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }
            }
        }

        // midi channel spinner
        val midiChannelSpinnerAdapter = ArrayAdapter<Int>(
            binding.root.context,
            android.R.layout.simple_spinner_dropdown_item,
            (1..16).toList()
        )
        binding.midiChannelSpinner.apply {
            adapter = midiChannelSpinnerAdapter
        }

        // save button
        binding.saveButton.setOnClickListener {
            val channel = binding.midiChannelSpinner.selectedItemPosition // return [0-15]
            val keys = vm.textNoteNumbers
                .map {
                    // toIntに失敗したら0番にする
                    it.value?.toIntOrNull() ?: 0
                }
                .mapIndexed { index, noteNumber ->
                    Key(index, noteNumber, 127, 0x0A5ACD)
                }
            val pad = Pad(channel, vm.padName.value ?: "untitled", keys)

            onCreatePad(pad)
        }

        return binding.root
    }
}
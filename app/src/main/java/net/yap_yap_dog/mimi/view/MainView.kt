package net.yap_yap_dog.mimi.view

import android.media.midi.MidiManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.yap_yap_dog.mimi.R
import net.yap_yap_dog.mimi.databinding.ActivityMainBinding
import net.yap_yap_dog.mimi.repository.MidiDeviceRepositoryImpl
import net.yap_yap_dog.mimi.viewmodel.MainViewModel

// todo 設定画面からMIDIデバイスの選択やページを増やしたりできるようにする
// todo ページを増やす際にボタンごとにMIDIノートを手動で割り当てられるようにする
// todo ページを増やす際にMIDIチャネルやポート等の設定をできるようにする
// todo パッドだけではなくつまみにも対応する
// todo ダークモードの対応
// todo 横画面の対応

// memo 多重で開放したりするのを防ぐとかやるのが面倒なので、決め打ちでMIDIデバイスを選択したら開けっぱなしというかどこかに開けたやつを持っておく
//

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val padFragments = mutableListOf<PadFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ContextCompat.getSystemService(this, MidiManager::class.java)?.let { midiManager ->
            viewModel = MainViewModel(MidiDeviceRepositoryImpl(midiManager))
            
            midiManager.registerDeviceCallback(viewModel.deviceCallBack, Handler(mainLooper))

            val configurationFragment = PadConfigurationFragment(
                viewModel.midiDevices,
                {
                    viewModel.selectDevice(it)
                },
                { pad ->
                    val padFragment = PadFragment(
                        pad,
                        { key -> viewModel.pressKey(pad, key) },
                        { key -> viewModel.releaseKey(pad, key) }
                    )

                    padFragments.add(padFragment)
                    binding.pager.adapter?.notifyDataSetChanged() // モヤモヤする
                }
            )

            binding.pager.adapter = object : FragmentStateAdapter(this) {
                override fun createFragment(position: Int): Fragment {
                    if (position > 0) {
                        return padFragments[position - 1]
                    }

                    return configurationFragment
                }

                override fun getItemCount(): Int {
                    return padFragments.size + 1
                }
            }
        }
    }
}


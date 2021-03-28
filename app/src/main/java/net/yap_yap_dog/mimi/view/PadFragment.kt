package net.yap_yap_dog.mimi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import net.yap_yap_dog.mimi.R
import net.yap_yap_dog.mimi.databinding.FragmentPadBinding
import net.yap_yap_dog.mimi.model.Key
import net.yap_yap_dog.mimi.model.Pad


class PadFragment(
    private val pad: Pad,
    private val onPressKey: (Key) -> Unit,
    private val onReleaseKey: (Key) -> Unit
) : Fragment() {
    private lateinit var binding: FragmentPadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pad,
            container,
            false
        )

        listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8
        ).forEachIndexed { index, button ->
            button.setOnTouchListener { v, event ->
                val key = pad.keys[index]

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> onPressKey(key)
                    MotionEvent.ACTION_UP -> onReleaseKey(key)
                }

                true
            }
        }

        return binding.root
    }
}
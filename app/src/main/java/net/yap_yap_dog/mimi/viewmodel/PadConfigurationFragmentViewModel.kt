package net.yap_yap_dog.mimi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PadConfigurationFragmentViewModel : ViewModel() {
    val textNoteNumbers: List<MutableLiveData<String>> =
        (24 until 32).map { value ->
            MutableLiveData<String>("$value")
        }
    val padName = MutableLiveData<String>("untitled")

}
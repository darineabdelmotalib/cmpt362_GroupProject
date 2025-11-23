package darine_abdelmotalib.example.groupproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel : ViewModel() {
    private val _name = MutableLiveData("John Doe")
    val name: LiveData<String> = _name

    private val _major = MutableLiveData("Computing Science")
    val major: LiveData<String> = _major

    private val _minor = MutableLiveData("None")
    val minor: LiveData<String> = _minor

    fun updateProfile(newName: String, newMajor: String, newMinor: String) {
        _name.value = newName
        _major.value = newMajor
        _minor.value = newMinor
    }
}
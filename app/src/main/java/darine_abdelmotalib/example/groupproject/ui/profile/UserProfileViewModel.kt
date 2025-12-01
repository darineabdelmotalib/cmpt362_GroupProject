package darine_abdelmotalib.example.groupproject.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _name = MutableLiveData(UserProfilePrefs.getName(context))
    val name: LiveData<String> = _name

    private val _major = MutableLiveData(UserProfilePrefs.getMajor(context))
    val major: LiveData<String> = _major

    private val _major2 = MutableLiveData(UserProfilePrefs.getMajor2(context))
    val major2: LiveData<String> = _major2

    private val _minor = MutableLiveData(UserProfilePrefs.getMinor(context))
    val minor: LiveData<String> = _minor

    private val _avatarUri = MutableLiveData(UserProfilePrefs.getAvatarUri(context))
    val avatarUri: LiveData<String?> = _avatarUri

    fun updateProfile(newName: String, newMajor: String, newMajor2: String, newMinor: String, newAvatarUri: String?) {
        _name.value = newName
        _major.value = newMajor
        _major2.value = newMajor2
        _minor.value = newMinor
        _avatarUri.value = newAvatarUri

        UserProfilePrefs.saveProfile(
            context,
            newName,
            newMajor,
            newMajor2,
            newMinor,
            newAvatarUri
        )
    }
}
package com.shino72.saying.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shino72.saying.repository.ApiRepository
import com.shino72.saying.repository.ImageSaveRepository
import com.shino72.saying.utils.Status
import com.shino72.saying.utils.data.PhotoResponse
import com.shino72.saying.utils.data.Slip
import com.shino72.saying.utils.data.SlipResponse
import com.shino72.saying.utils.networkState.ImageSaveState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class ImageViewModel
@Inject
constructor(
    private var imageSaveRepository: ImageSaveRepository,
    private var apiRepository: ApiRepository,
) : ViewModel() {
    private var _save = MutableStateFlow(ImageSaveState())
    private val _image = MutableLiveData<Status<PhotoResponse>>()
    private val _advice =  MutableLiveData<Status<SlipResponse>>()

    val save : StateFlow<ImageSaveState> = _save
    fun saveImage(bitmap: Bitmap) {
        _save.value = ImageSaveState()
        imageSaveRepository.saveImageToStorage(bitmap).onEach {
            when(it) {
                is Status.Loading -> {
                    _save.value = ImageSaveState(isLoading = true)
                }
                is Status.Error -> {
                    _save.value = ImageSaveState(error = it.message ?: "")
                }
                is Status.Success -> {
                    _save.value = ImageSaveState(data = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    val advice : LiveData<Status<SlipResponse>> = _advice
    fun getAdvice() {

        viewModelScope.launch {
            _advice.value = Status.Loading()
            val response = apiRepository.getAdvice()
            if(response.isSuccessful) {
                _advice.value = Status.Success(response.body())
            }
            else {
                _advice.value = Status.Error(response.errorBody().toString())
            }
        }
    }

    val image : LiveData<Status<PhotoResponse>> = _image
    fun getImage() {
        _image.value = null
        viewModelScope.launch {
            _image.value = Status.Loading()
            val response = apiRepository.getImage(Random.nextInt(1,301).toString())
            if(response.isSuccessful) {
                _image.value = Status.Success(response.body())
            }
            else {
                _image.value = Status.Error(response.errorBody().toString())
            }
        }
    }
}
package com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.practice.paintting.coloringbook.data.image.ImageRepository
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private val _images = MutableStateFlow<List<ImageEntity>>(emptyList())
    val images: StateFlow<List<ImageEntity>> = _images

    fun getAllImages() {
        viewModelScope.launch {
            _images.value = imageRepository.getAllImages().filter { it.isStarted }
            Log.e("MyApp", "MyWorksViewModel: images.value = ${_images.value}")
        }
    }


}
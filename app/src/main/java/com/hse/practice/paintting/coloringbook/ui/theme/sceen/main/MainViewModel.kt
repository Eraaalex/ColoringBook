package com.hse.practice.paintting.coloringbook.ui.theme.sceen.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.practice.paintting.coloringbook.data.image.ImageRepository
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {
    private val images_: MutableState<List<ImageEntity>> = mutableStateOf(listOf())
    val images: State<List<ImageEntity>> = images_

    @Inject
    lateinit var imageRepository: ImageRepository
    fun getAllImages(): List<ImageEntity> {
        viewModelScope.launch() {
            val all = imageRepository.getAllImages()

            images_.value = all.filter { !it.isStarted }
        }
        return images_.value
    }

}
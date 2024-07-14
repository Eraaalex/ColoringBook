package com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload.stage1

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.practice.paintting.coloringbook.data.HexagonRepository
import com.hse.practice.paintting.coloringbook.data.image.ImageRepository
import com.hse.practice.paintting.coloringbook.data.triangle.TriangleRepository
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import com.hse.practice.paintting.coloringbook.service.FilterService
import com.hse.practice.paintting.coloringbook.service.ProcessImageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val processImageService: ProcessImageService,
    private val triangleRepository: TriangleRepository,
    private val hexagonRepository: HexagonRepository,
    private val filterService: FilterService
) :
    ViewModel() {

    private val _imageId = mutableStateOf<Long?>(null)
    val imageId: State<Long?> get() = _imageId
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState


    private suspend fun saveImage(bitmap: Bitmap?, uri: Uri, selectedOption: String): Long? {
        return bitmap?.let {
            val imageEntity = ImageEntity(
                uri = Uri.encode(uri.toString()),
                imageData = it,
                isStarted = true,
                isUploaded = true,
                name = Uri.encode(uri.toString()),
                uid = 0,
                selectedOption = selectedOption
            )
            imageRepository.insertImage(imageEntity)
        }
    }

    fun saveImageAndColoringData(
        bitmapFromUri: Bitmap?,
        uri: Uri,
        selectedOption: String,
        trianglesNumber: Int,
        isBlackAndWhite: Boolean = false
    ) {
        viewModelScope.launch {
            var bitmap = bitmapFromUri
            Log.i("MyApp", "EditImageViewModel: bitmap $bitmapFromUri / \n $isBlackAndWhite")
            if (isBlackAndWhite && bitmapFromUri != null) {

                 bitmap= filterService.applyBlackAndWhiteFilter(bitmapFromUri)

                Log.i("MyApp", "EditImageViewModel: finished black and white filter")

            }
            val imageId = saveImage(bitmap, uri, selectedOption)
            _saveState.value = SaveState.Saving
            Log.i("MyApp", "EditImageViewModel: imageId was saved with id = $imageId")
            _imageId.value = imageId
            Log.i("MyApp", "EditImageViewModel: bitmap $bitmapFromUri")

            if (imageId != null) {
                when (selectedOption) {
                    "triangles" -> {
                        Log.d("MyApp", "[EditVM] will generate triangle")

                        val figs = processImageService.processImageTriangles(
                            bitmap!!,
                            trianglesNumber
                        )
                        Log.d("MyApp", "[EditVM] will save triangles $figs")
                        triangleRepository.insertTriangles(figs, imageId)
                    }

                    "hexagons" -> {
                        val figs = processImageService.processImageHexagons(
                            bitmapFromUri!!,
                            trianglesNumber
                        )
                        Log.d("MyApp", "[EditVM] generated hexagones: $figs")
                        hexagonRepository.insertAllHexagons(figs, imageId)
                    }

                    else -> {
                        Log.d("MyApp", "[EditVM] will generate triangle")

                        val figs = processImageService.processImageTriangles(
                            bitmapFromUri!!,
                            trianglesNumber
                        )
                        triangleRepository.insertTriangles(figs, imageId)
                    }
                }
                val hexagons = hexagonRepository.getAllHexagons()
                Log.i(
                    "MyApp",
                    "EditImageViewModel: objects were saved hex.size = ${hexagons.size} \n"
                )
            }
            _saveState.value = SaveState.Saved(imageId)
        }
    }

}

sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    data class Saved(val imageId: Long?) : SaveState()
}
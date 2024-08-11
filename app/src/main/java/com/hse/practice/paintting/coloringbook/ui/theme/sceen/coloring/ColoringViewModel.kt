package com.hse.practice.paintting.coloringbook.ui.theme.sceen.coloring

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.practice.paintting.coloringbook.data.HexagonRepository
import com.hse.practice.paintting.coloringbook.data.image.ImageRepository
import com.hse.practice.paintting.coloringbook.data.triangle.TriangleRepository
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.Triangle
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import com.hse.practice.paintting.coloringbook.service.ProcessImageService
import com.hse.practice.paintting.coloringbook.utils.toHexagon
import com.hse.practice.paintting.coloringbook.utils.toTriangle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColoringViewModel @Inject constructor(
    val imageRepository: ImageRepository,
    val triangleRepository: TriangleRepository,
    val hexagonRepository: HexagonRepository,
    val processImageService: ProcessImageService
) : ViewModel() {
    private val _image = MutableStateFlow<ImageEntity?>(null)
    val image: StateFlow<ImageEntity?> = _image

    private val _triangles = MutableStateFlow<List<Triangle>>(emptyList())
    val triangles: StateFlow<List<Triangle>> = _triangles

    private val _hexagons = MutableStateFlow<List<Hexagon>>(emptyList())
    val hexagons: StateFlow<List<Hexagon>> = _hexagons

    private val _shape = MutableStateFlow<String>("triangles")
    val shape: StateFlow<String> = _shape


    private val _pallete = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val pallete: StateFlow<Map<Int, Int>> = _pallete

    fun fetchImageById(id: Long) {
        viewModelScope.launch {
            val image = imageRepository.getImageById(id)
            _image.value = image
            _shape.value = image?.selectedOption ?: "triangles"
            Log.e("MyApp", "ColoringViewModel: _image = ${_image.value}")

            if (_shape.value == "triangles") {
                val triangles = triangleRepository.getTrianglesForImage(id).map {
                    it.toTriangle()
                }
                _triangles.value = triangles
                Log.e("MyApp", "ColoringViewModel: _triangles = ${_triangles.value}")
                val newPallete = mutableMapOf<Int, Int>()
                triangles.forEach {
                    newPallete[it.number] = it.color
                }
                _pallete.value = newPallete
                Log.e("MyApp", "ColoringViewModel: _pallete = ${_pallete.value}")
            } else {
                val hexagons = hexagonRepository.getHexagonsForImage(id).map {
                    it.toHexagon()
                }
                _hexagons.value = hexagons
                Log.e("MyApp", "[ColoringVM]: _hexagons = ${_hexagons.value}")
                val newPallete = mutableMapOf<Int, Int>()
                hexagons.forEach {
                    newPallete[it.number] = it.color
                }
                _pallete.value = newPallete
                Log.e("MyApp", "[ColoringVM]: _pallete = ${_pallete.value}")
            }


        }
    }

    fun updateTriangles(coloringTriangles: List<Triangle>) {
        viewModelScope.launch {
            _image.value?.let { img ->
                val id = img.uid
                _image.value = img.copy(isStarted = true)
                imageRepository.updateImage(img)
                coloringTriangles.forEach {
                    triangleRepository.updateTriangle(it, id)
                    Log.e("MyApp", "CWM: updateTriangles: triangle one saved as = $it")
                }
                val imdd = imageRepository.getImageById(img.uid)
                Log.e("MyApp", "CWM: updateTriangles: image saved as = $imdd")
                val trigs = triangleRepository.getTrianglesForImage(id)
                Log.e("MyApp", "CWM: updateTriangles: triangles saved as = $trigs")

            }

        }
    }

    fun updateHexagones(coloringHexagons: List<Hexagon>) {
        viewModelScope.launch {
            _image.value?.let { img ->
                val id = img.uid
                _image.value = img.copy(isStarted = true)
                imageRepository.updateImage(img)
                coloringHexagons.forEach {
                    hexagonRepository.updateHexagon(it, id)
                    Log.d("MyApp", "update hexagon: $it")
                }
            }

        }
    }


}
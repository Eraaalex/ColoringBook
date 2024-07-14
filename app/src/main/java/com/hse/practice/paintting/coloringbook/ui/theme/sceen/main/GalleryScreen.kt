package com.hse.practice.paintting.coloringbook.ui.theme.sceen.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import com.hse.practice.paintting.coloringbook.ui.theme.EmptyHeader
import com.hse.practice.paintting.coloringbook.ui.theme.ImagesList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GalleryScreen(viewModel: MainViewModel = hiltViewModel(), navController: NavHostController) {

    var fetchedImages by remember { mutableStateOf<List<ImageEntity>>(emptyList()) }

    LaunchedEffect(Unit) {

        fetchedImages = viewModel.getAllImages()
        Log.e("MyApp", "GalleryScreen: fetchedImages = $fetchedImages")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                EmptyHeader(text = "New")
            },
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                ImagesList(images = fetchedImages, navController = navController)
            }
        }

    }
}


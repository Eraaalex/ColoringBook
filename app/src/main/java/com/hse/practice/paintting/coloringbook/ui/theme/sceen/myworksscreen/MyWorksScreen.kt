package com.hse.practice.paintting.coloringbook.ui.theme.sceen.myworksscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

@Composable
fun MyWorksScreen(viewModel: MyWorksViewModel = hiltViewModel(), navController: NavHostController) {
    val fetchedImages by viewModel.images.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllImages()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        EmptyHeader(text = "Painted works")
        ImagesList(images = fetchedImages, navController = navController)
    }
}
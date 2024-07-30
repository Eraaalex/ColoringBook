package com.hse.practice.paintting.coloringbook.ui.theme.sceen.myworksscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hse.practice.paintting.coloringbook.ui.theme.EmptyHeader
import com.hse.practice.paintting.coloringbook.ui.theme.ImagesList

@Composable
fun MyWorksScreen(viewModel: MyWorksViewModel = hiltViewModel(), navController: NavHostController) {
    val fetchedImages by viewModel.images.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllImages()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 72.dp)) {
        EmptyHeader(text = "Painted works")
        ImagesList(images = fetchedImages, navController = navController)
//        Spacer(modifier = Modifier.height(30.dp))
    }
}
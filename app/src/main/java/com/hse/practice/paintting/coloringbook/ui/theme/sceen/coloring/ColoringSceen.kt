package com.hse.practice.paintting.coloringbook.ui.theme.sceen.coloring


import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hse.practice.paintting.coloringbook.getBitmapFromUri
import com.hse.practice.paintting.coloringbook.ui.theme.ColoringCanvas
import com.hse.practice.paintting.coloringbook.ui.theme.ColoringCanvas2

@Composable
fun ColoringSceen(
    viewModel: ColoringViewModel = hiltViewModel(),
    id: Long,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }
    val hexagons by viewModel.hexagons.collectAsState()
    val triangles by viewModel.triangles.collectAsState()
    val pallete by viewModel.pallete.collectAsState()
    val shape by viewModel.shape.collectAsState()

    if (showDialog) {
        BasicDialog(
            onDismissRequest = { showDialog = false },
            onSaveChanges = {
                navController.navigate("gallery")
                showDialog = false
            }
        )
    }
    LaunchedEffect(Unit) {
        Log.i("MyApp", "ColoringScreen: LaunchedEffect, started fetching image by id = $id")
        viewModel.fetchImageById(id)

    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Coloring", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 0.dp,
            )
        },
        content = { paddingValues ->
            if (pallete.isNotEmpty()) {
                if (shape == "triangles") {
                    ColoringCanvas(
                        modifier = Modifier.padding(paddingValues),
                        triangles = triangles,
                        pallete = pallete,
                        scaledImage = getBitmapFromUri(
                            context = LocalContext.current, uri = Uri.parse(
                                viewModel.image.value?.uri
                            )
                        ),
                        onUpgated = viewModel::updateTriangles
                    )
                } else {
                    ColoringCanvas2(
                        modifier = Modifier.padding(paddingValues),
                        hexagons = hexagons,
                        pallete = pallete,
                        scaledImage = getBitmapFromUri(
                            context = LocalContext.current, uri = Uri.parse(
                                viewModel.image.value?.uri
                            )
                        ),
                        onUpgated = viewModel::updateHexagones
                    )
                }

            } else {
                Text("Loading...")
            }
        }
    )


}


@Composable
fun BasicDialog(onDismissRequest: () -> Unit, onSaveChanges: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Save Changes")
        },
        text = {
            Text("Do you want to save changes?")
        },
        confirmButton = {
            Button(
                onClick = onSaveChanges,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}


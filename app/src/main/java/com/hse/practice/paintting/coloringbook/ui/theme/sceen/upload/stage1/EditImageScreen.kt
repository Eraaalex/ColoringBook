package com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload.stage1


import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.hse.practice.paintting.coloringbook.getBitmapFromUri
import com.hse.practice.paintting.coloringbook.getScreenWidth
import com.hse.practice.paintting.coloringbook.scaleBitmap
import com.hse.practice.paintting.coloringbook.ui.theme.EmptyHeader

@Composable
fun EditImageScreen(
    viewModel: EditImageViewModel = hiltViewModel<EditImageViewModel>(),
    uri: Uri,
    navController: NavHostController
) {
    val radioOptions = listOf("Triangles", "Hexagons")
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }
    var sliderValue by remember { mutableStateOf(100f) }
    var isBlackAndWhite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageId by viewModel.imageId
    val saveState by viewModel.saveState.collectAsState()
    LaunchedEffect(saveState) {
        when(saveState){
            is SaveState.Saved -> {
                val id = (saveState as SaveState.Saved).imageId
                if (id != null) {
                    navController.navigate("coloringImage/$imageId")
                }
            } else -> {}
        }
    }

    Column {
        EmptyHeader(text = "Create")
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(460.dp)
                .background(Color.Gray)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = "Shapes",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            radioOptions.forEach { text ->
                RadioButtonWithText(
                    text = text,
                    selected = text == selectedOption,
                    onClick = { selectedOption = text }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Switch(
                    checked = isBlackAndWhite,
                    onCheckedChange = { isChecked -> isBlackAndWhite = isChecked }
                )
                Text(
                    "Black&White",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Figures ~${sliderValue.toInt()}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "100-1000",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 100f..1000f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

//        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("upload") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Icon(imageVector = Icons.Outlined.Cancel, contentDescription = "Retake")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retake", style = MaterialTheme.typography.bodyLarge)
            }
            Button(
                onClick = {
                    val screenWidth = getScreenWidth(context)
                    val scaledBitmap =
                        getBitmapFromUri(context, uri)?.let { scaleBitmap(it, screenWidth) }
                    Log.i("MyApp", "EditImageScreen: press ok, $scaledBitmap, $uri, $selectedOption, ${sliderValue.toInt()}")
                    viewModel.saveImageAndColoringData(
                        scaledBitmap,
                        uri,
                        selectedOption.toLowerCase(),
                        sliderValue.toInt(),
                        isBlackAndWhite
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.Black
                )
            ) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = "Ok")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ok", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }


}


@Composable
fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
fun CircleColorSelector(color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color, shape = CircleShape)
            .clickable(onClick = onClick)
    )
}

@Preview
@Composable
fun EditImageScreen(
) {
    EditImageScreen(
        viewModel = hiltViewModel(),
        uri = Uri.EMPTY,
        navController = NavHostController(LocalContext.current)
    )
}
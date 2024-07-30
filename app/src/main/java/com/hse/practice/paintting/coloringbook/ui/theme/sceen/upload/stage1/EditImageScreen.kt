package com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload.stage1


import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val scrollState = rememberScrollState()
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Saved -> {
                val id = (saveState as SaveState.Saved).imageId
                if (id != null) {
                    navController.navigate("coloringImage/$imageId")
                }
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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

        ColorPicker(onColorSelected = {
            Log.e("MyApp", "EditImageScreen: color selected $it")
            selectedColor = it
        }, onSave = {
            val screenWidth = getScreenWidth(context)
            val scaledBitmap =
                getBitmapFromUri(context, uri)?.let { scaleBitmap(it, screenWidth) }
            Log.i(
                "MyApp",
                "EditImageScreen: press ok, $scaledBitmap, $uri, $selectedOption, ${sliderValue.toInt()}"
            )
            Log.i(
                "MyApp",
                "EditImageScreen: color: ${selectedColor}"
            )
            viewModel.saveImageAndColoringData(
                scaledBitmap,
                uri,
                selectedOption.toLowerCase(),
                sliderValue.toInt(),
                isBlackAndWhite,
                selectedColor.toArgb()
            )

        }, onRetake = {
            navController.navigate("upload")
        })

        Spacer(modifier = Modifier.height(16.dp))
    }


}


@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit,
    onSave: () -> Unit = {},
    onRetake: () -> Unit = {}
) {
    var selectedColor by remember { mutableStateOf(Color.White) }
    var alpha by remember { mutableStateOf(1f) }
    val clickOffset = remember { mutableStateOf<Offset?>(null) }
    val clickOffsetForTransparency = remember { mutableStateOf<Offset?>(null) }
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Choose target color", style = MaterialTheme.typography.bodyLarge)
            Spacer(
                modifier = Modifier
                    .size(30.dp)
                    .background(selectedColor, shape = RoundedCornerShape(7.dp))
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(370.dp)
                .aspectRatio(1f)
                .graphicsLayer(alpha = 0.99f)
                .clip(RoundedCornerShape(15.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        clickOffset.value = offset
                        val hue = (offset.x / size.width) * 360f
                        val saturation = 1f
                        val brightness = 1f - (offset.y / size.height)
                        selectedColor = Color
                            .hsv(hue, saturation, brightness)
                            .copy(alpha = alpha)
                        onColorSelected(selectedColor)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val shader = LinearGradientShader(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    ),
                    from = Offset.Zero,
                    to = Offset(size.width, 0f)
                )
                drawRect(brush = ShaderBrush(shader))

                val verticalShader = LinearGradientShader(
                    colors = listOf(Color.Transparent, Color.Black),
                    from = Offset.Zero,
                    to = Offset(0f, size.height + 30.dp.toPx())
                )
                drawRect(brush = ShaderBrush(verticalShader), alpha = 1f)
                clickOffset.value?.let {
                    drawCircle(
                        color = Color.White,
                        radius = 10.dp.toPx(),
                        center = it,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx())
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        BasicText(text = "Adjust transparency", style = MaterialTheme.typography.bodyLarge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(15.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        clickOffsetForTransparency.value = offset
                        val newAlpha = 1f - offset.x / size.width
                        alpha = newAlpha.coerceIn(0f, 1f)
                        selectedColor = selectedColor.copy(alpha = alpha)
                        onColorSelected(selectedColor)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val alphaShader = LinearGradientShader(
                    colors = listOf(selectedColor.copy(alpha = 1f), selectedColor.copy(alpha = 0f)),
                    from = Offset.Zero,
                    to = Offset(size.width, 0f)
                )
                drawRect(brush = ShaderBrush(alphaShader))
                clickOffsetForTransparency.value?.let {
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = it,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx())
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onRetake,
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
                onClick = onSave,
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
        Spacer(modifier = Modifier.height(50.dp))

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

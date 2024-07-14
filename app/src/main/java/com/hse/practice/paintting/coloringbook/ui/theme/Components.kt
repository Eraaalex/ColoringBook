package com.hse.practice.paintting.coloringbook.ui.theme

import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hse.practice.paintting.coloringbook.getBitmapFromUri
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.Point
import com.hse.practice.paintting.coloringbook.model.Triangle
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity

@Composable
fun ColorCircle(color: Int, number: Int, onClick: () -> Unit) {
    val colorWithAlpha = if ((color and 0xFF000000.toInt()) == 0) {
        color or 0xFF000000.toInt()
    } else {
        color
    }
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(color = Color(colorWithAlpha), shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = number.toString(), fontSize = MaterialTheme.typography.labelSmall.fontSize)
    }

}

@Preview
@Composable
fun ColorCirclePreview() {
    ColorCircle(color = 16391324, number = 3, {})

}

@Preview
@Composable
fun ColorRowListPreview() {
    val colorList = listOf(
        Pair(16391324, 1),
        Pair(16414721, 2),
        Pair(16414840, 2),
        Pair(16391324, 1),
        Pair(16414721, 2),
        Pair(16414840, 2),
        Pair(16391324, 1),
        Pair(16414721, 2),
        Pair(16414840, 2),
        Pair(16391324, 1),
        Pair(16414721, 2),
        Pair(16414840, 2),
    )

    val colorMap = colorList.associate { it.second to it.first }

//    ColorRowList(
//        colorList = colorMap, {}
//    )
}

@Composable
fun ColorRowList(
    modifier: Modifier = Modifier,
    colorList: Map<Int, Int>,
    onColorSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp).background(Color.White),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        for (pair in colorList) {
            item {
                ColorCircle(color = pair.value, number = pair.key) {
                    onColorSelected(pair.value)
                }
            }
        }
    }
}


@Composable
@Preview
fun ColoringCanvasPreview() {
    val triangles = listOf(

        Triangle(
            Point(100.0, 100.0),
            Point(200.0, 100.0),
            Point(150.0, 200.0),
            Color.Gray.toArgb()
        ),
        Triangle(
            Point(200.0, 100.0),
            Point(300.0, 100.0),
            Point(250.0, 200.0),
            Color.Yellow.toArgb()
        ),
        Triangle(
            Point(300.0, 100.0),
            Point(400.0, 100.0),
            Point(350.0, 200.0),
            Color.Blue.toArgb()
        ),
        Triangle(
            Point(400.0, 100.0),
            Point(500.0, 100.0),
            Point(450.0, 200.0),
            Color.White.toArgb()
        ),
    )
//    ColoringCanvas(initialTriangles = triangles, pallete = mapOf(1 to Color.Blue.toArgb(), 2 to Color.Red.toArgb()))
}

@Composable
fun ColoringCanvas(
    modifier: Modifier,
    triangles: List<Triangle>,
    pallete: Map<Int, Int>,
    scaledImage: Bitmap?,
    onUpgated: (List<Triangle>) -> Unit
) {
    var selectedColor by remember { mutableStateOf(Color.White.toArgb()) }
    var selectedColorNumber by remember { mutableStateOf(0) }
    val coloringTriangles = remember { mutableStateListOf(*triangles.toTypedArray()) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var delta by remember { mutableStateOf(Offset.Zero) }

    Log.i(
        "MyApp",
        "ColoringScreen: number of fetched triangle by id, num = ${triangles.size} \n pallete = ${pallete}"
    )
    Box(modifier = modifier.fillMaxSize()) {

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                    delta = Offset(delta.x + pan.x / zoom, delta.y + pan.y / zoom)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offsetClick ->
                    val oldCoords = Offset(
                        coloringTriangles[0].v0.x.toFloat(),
                        coloringTriangles[0].v0.y.toFloat()
                    )
                    val adjustedClick = (offsetClick - offset) / scale
                    coloringTriangles.forEach { triangle ->
                        if (triangle.contains(adjustedClick)) {
                            triangle.colorState = if ((selectedColor and 0xFF000000.toInt()) == 0) {
                                selectedColor or 0xFF000000.toInt()
                            } else {
                                selectedColor
                            }
                            triangle.currentColor = triangle.colorState
                            triangle.currentColorState = triangle.colorState
                            if (triangle.currentColor == triangle.color) {
                                triangle.showNumber = false
                            }
                            onUpgated(listOf(triangle))
                            Log.d("MyApp", "update color = ${triangle}")
                        }
                    }
                }
            }) {
            withTransform({
                translate(offset.x, offset.y)
                scale(scale, scale)
            }) {
                coloringTriangles.forEach { triangle ->
                    drawIntoCanvas { canvas ->
                        val path = Path().apply {
                            fillType = PathFillType.EvenOdd
                            moveTo(triangle.v0.x.toFloat(), triangle.v0.y.toFloat())
                            lineTo(triangle.v1.x.toFloat(), triangle.v1.y.toFloat())
                            lineTo(triangle.v2.x.toFloat(), triangle.v2.y.toFloat())
                            close()
                        }

                        drawPath(path, color = Color(triangle.currentColorState))
                        if (triangle.showNumber) {
                            drawNumber(triangle)
                        }
                    }
                }
            }
        }

        ColorRowList(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 72.dp),
            colorList = pallete, onColorSelected = { newColor ->
                selectedColor = newColor
                selectedColorNumber = pallete[newColor] ?: 0
            })
    }

}


@Composable
fun ColoringCanvasHex(
    modifier: Modifier,
    hexagons: List<Hexagon>,
    palette: Map<Int, Int>,
    scaledImage: Bitmap?,
    onUpgated: (List<Hexagon>) -> Unit
) {
    var selectedColor by remember { mutableStateOf(Color.White.toArgb()) }
    val coloringHexagons = remember { mutableStateListOf(*hexagons.toTypedArray()) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var delta by remember { mutableStateOf(Offset.Zero) }

    Log.i(
        "MyApp",
        "ColoringScreen: number of fetched hexagons by id, num = ${hexagons.size} \n palette = $palette"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offset += pan
                        delta = Offset(delta.x + pan.x / zoom, delta.y + pan.y / zoom)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offsetClick ->
                        val adjustedClick = (offsetClick - offset) / scale
                        Log.d("MyApp", "adjustedClick = $adjustedClick")
                        coloringHexagons.forEach { hexagon ->
                            if (hexagon.contains(adjustedClick)) {
                                Log.d("MyApp", "HEX: hexagon clicked $hexagon")
                                hexagon.colorState =
                                    if ((selectedColor and 0xFF000000.toInt()) == 0) {
                                        selectedColor or 0xFF000000.toInt()
                                    } else {
                                        selectedColor
                                    }
                                hexagon.currentColor = if ((selectedColor and 0xFF000000.toInt()) == 0) {
                                    selectedColor or 0xFF000000.toInt()
                                } else {
                                    selectedColor
                                }
                                onUpgated(listOf(hexagon))
                                hexagon.showNumber = false
                            }
                        }
                    }
                }
        ) {
            withTransform({
                translate(offset.x, offset.y)
                scale(scale, scale)
            }) {
                coloringHexagons.forEach { hexagon ->
                    drawIntoCanvas { canvas ->
                        val path = Path().apply {
                            fillType = PathFillType.EvenOdd
                            moveTo(hexagon.vertices[0].x.toFloat(), hexagon.vertices[0].y.toFloat())
                            hexagon.vertices.forEach { vertex ->
                                lineTo(vertex.x.toFloat(), vertex.y.toFloat())
                            }
                            close()
                        }
                        drawPath(path, color = androidx.compose.ui.graphics.Color(hexagon.color))
                        if (hexagon.showNumber) {
                            drawNumber(hexagon)
                        }
                    }
                }
            }
        }
        ColorRowList(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            colorList = palette,
            onColorSelected = { newColor ->
                selectedColor = newColor
            }
        )
    }
}


fun DrawScope.drawNumber(triangle: Triangle) {
    val text = triangle.number.toString()
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 24f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }

    val centerX = (triangle.v0.x + triangle.v1.x + triangle.v2.x) / 3
    val centerY = (triangle.v0.y + triangle.v1.y + triangle.v2.y) / 3

    drawIntoCanvas {
        it.nativeCanvas.drawText(text, centerX.toFloat(), centerY.toFloat(), textPaint)
    }
}


@Composable
fun ColoringCanvas2(
    modifier: Modifier,
    hexagons: List<Hexagon>,
    pallete: Map<Int, Int>,
    scaledImage: Bitmap?,
    onUpgated: (List<Hexagon>) -> Unit
) {
    var selectedColor by remember { mutableStateOf(Color.White.toArgb()) }
    var selectedColorNumber by remember { mutableStateOf(0) }
    val coloringHexagons = remember { mutableStateListOf(*hexagons.toTypedArray()) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var delta by remember { mutableStateOf(Offset.Zero) }
    Box(modifier = modifier.fillMaxSize()) {

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                    delta = Offset(delta.x + pan.x / zoom, delta.y + pan.y / zoom)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offsetClick ->

                    val adjustedClick = (offsetClick - offset) / scale
                    Log.e("MyApp", "hex adjustedClick = $adjustedClick")
                    coloringHexagons.forEach { hexagon ->
                        if (hexagon.contains(adjustedClick)) {
                            hexagon.colorState = if ((selectedColor and 0xFF000000.toInt()) == 0) {
                                selectedColor or 0xFF000000.toInt()
                            } else {
                                selectedColor
                            }
                            hexagon.currentColor = hexagon.colorState
                            hexagon.currentColorState = hexagon.colorState
                            if (hexagon.currentColor == hexagon.color) {
                                hexagon.showNumber = false
                            }
                            onUpgated(listOf(hexagon))
                            Log.d("MyApp", "update color = ${hexagon}")
                        }
                    }
                }
            }) {
            withTransform({
                translate(offset.x, offset.y)
                scale(scale, scale)
            }) {
                coloringHexagons.forEach { hexagon ->
                    drawIntoCanvas { canvas ->
                        val path = Path().apply {
                            fillType = PathFillType.EvenOdd
                            moveTo(hexagon.vertices[0].x.toFloat(), hexagon.vertices[0].y.toFloat())
                            hexagon.vertices.forEach { vertex ->
                                lineTo(vertex.x.toFloat(), vertex.y.toFloat())
                            }
                            close()
                        }

                        drawPath(path, color = Color(hexagon.currentColorState))
                        if (hexagon.showNumber) {
                            drawNumber(hexagon)
                        }
                    }
                }
            }
        }

        ColorRowList(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 72.dp),
            colorList = pallete, onColorSelected = { newColor ->
                selectedColor = newColor
                selectedColorNumber = pallete[newColor] ?: 0
            })
    }

}


fun DrawScope.drawNumber(hexagon: Hexagon) {
    val text = hexagon.number.toString()
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 24f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }

    val centerX = hexagon.center.x
    val centerY = hexagon.center.y

    drawIntoCanvas {
        it.nativeCanvas.drawText(text, centerX.toFloat(), centerY.toFloat(), textPaint)
    }
}

@Composable
fun EmptyHeader(text: String) {
    TopAppBar(
        backgroundColor =
        MaterialTheme.colorScheme.surface,
        elevation = 0.dp,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .height(64.dp)
            .padding(4.dp, 8.dp)
    ) {

        androidx.compose.material.Text(
            text = text, textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .align(Alignment.CenterVertically)
        )
    }
}


@Preview
@Composable
fun EmptyHeaderPreview() {
    EmptyHeader(text = "New")
}

@Composable
fun ImagesList(
    images: List<ImageEntity>,
    navController: NavHostController,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Log.e("MyApp", "MyWorksScreen: fetchedImages.size = ${images.size}")
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        for (image in images) {
            item {
                val bitmap = getBitmapFromUri(context, Uri.parse(image.uri))
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(185.dp, 190.dp)
                            .clickable {
                                navController.navigate("coloringImage/${image.uid}")
                            }
                    )
                } else if (image.imageData != Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) {
                    Image(
                        bitmap = image.imageData.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(185.dp, 190.dp)
                            .clickable {
                                navController.navigate("coloringImage/${image.uid}")
                            }
                    )
                }

            }
        }

    }
}

fun createMockBitmap(width: Int = 100, height: Int = 100): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = Paint()
    paint.color = Color.Blue.toArgb()
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    return bitmap
}


@Preview
@Composable
fun ImagesListWithButtonPreview(

) {
    val images: List<ImageEntity> = listOf(
        ImageEntity(0, "ariel"),
        ImageEntity(0, "bulba"),
        ImageEntity(0, "patrik")
    )
    ImagesListWithButton(
        images = images,
        navController = NavHostController(LocalContext.current),
        onClick = {})
}


@Composable
fun ImagesListWithButton(
    images: List<ImageEntity>,
    navController: NavHostController,
    onClick: () -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    onClick()
                }, modifier = Modifier
                    .size(185.dp, 190.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add image",
                    tint = Color.Black,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        for (image in images) {
            item {
                getBitmapFromUri(context = LocalContext.current, uri = Uri.parse(image.uri))?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(185.dp, 190.dp)
                            .clickable {
                                //                            navController.navigate("coloring/${image.name}")
                            }
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

    }
}
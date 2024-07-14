package com.hse.practice.paintting.coloringbook.ui.theme.sceen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hse.practice.paintting.coloringbook.ui.theme.sceen.coloring.ColoringSceen
import com.hse.practice.paintting.coloringbook.ui.theme.sceen.main.GalleryScreen
import com.hse.practice.paintting.coloringbook.ui.theme.sceen.myworksscreen.MyWorksScreen
import com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload.UploadScreen
import com.hse.practice.paintting.coloringbook.ui.theme.sceen.upload.stage1.EditImageScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") { GalleryScreen(navController = navController) }
        composable("editImage/{uri}") { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            Log.e("MyApp", uriString.orEmpty())
            val uri = uriString?.let { Uri.parse(it) }
            uri?.let {
                EditImageScreen(uri = it, navController = navController)
            }
        }
        composable("myworks") { MyWorksScreen(navController = navController) }
        composable("coloringImage/{imageId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("imageId")?.toLong() ?: -1
            Log.d("MyApp", "NavigationGraph: image id = $id")
            ColoringSceen(id = id, navController = navController)
        }
        composable("upload") {
            UploadScreen(navController = navController)
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = "gallery", modifier = modifier) {
        composable("gallery") { GalleryScreen(navController = navController) }
        composable("myworks") { MyWorksScreen(navController = navController) }
        composable("upload") { UploadScreen(navController = navController) }
        composable("editImage/{uri}") { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            val uri = uriString?.let { Uri.parse(it) }
            uri?.let {
                EditImageScreen(uri = it, navController = navController)
            }
        }
    }
}


data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Gallery", "gallery", Icons.Filled.Folder),
        BottomNavItem("My works", "myworks", Icons.Filled.Work),
        BottomNavItem("Upload", "upload", Icons.Filled.Upload)
    )

    BottomNavigation(
        modifier = Modifier.height(72.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(width = 64.dp, height = 32.dp)
                            .background(
                                color = if (currentRoute == item.route) Color(0xFFe6def6) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                           )
                    }
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


//@Composable
//fun TopBar(s: String) {
//    TopAppBar {
//        Text(text = s)
//    }
//}


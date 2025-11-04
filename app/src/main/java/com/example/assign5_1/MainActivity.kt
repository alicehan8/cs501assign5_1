package com.example.assign5_1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assign5_1.ui.theme.Assign5_1Theme

data class Recipe(
    val title: String,
    val ingredients: String,
    val steps: String
)

class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assign5_1Theme {
                    MainScreen(viewModel)
            }
        }
    }
}

class MyViewModel : ViewModel(){
    var recipes by mutableStateOf(listOf<Recipe>())

    fun addRecipe(recipe: Recipe) {
        recipes = recipes + recipe
    }
}

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Details: Screen("details")
    data object Add : Screen("add", "Add", Icons.Default.Add)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

// A list of all our screens to easily iterate over for the navigation bar.
val screens = listOf(
    Screen.Home,
    Screen.Add,
    Screen.Settings
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MyViewModel = MyViewModel(), modifier: Modifier = Modifier){
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My App") }
            )
        },
        bottomBar = {
            // Our custom bottom navigation bar.
            NavigationBar {
                // 3. Get the current back stack entry. This tells us which screen is currently displayed.
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 4. Iterate over our list of screens to create a navigation item for each one.
                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) }, // The text label for the item.
                        icon = { Icon(screen.icon?: Icons.Default.Home, contentDescription = screen.title) }, // The icon for the item.

                        // 5. Determine if this item is currently selected.
                        // We check if the current route is part of the destination's hierarchy.
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,

                        // 6. Define the click action for the item.
                        onClick = {
                            // This is the core navigation logic.
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true // Save the state of the screen you're leaving.
                                }
                                // Avoid multiple copies of the same destination when re-selecting the same item.
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ){
            innerPadding ->
        // 7. Define the NavHost, which is the container for our screen content.
        // The content of the NavHost changes based on the current route.
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route, // The first screen to show.
            modifier = Modifier.padding(innerPadding) // Apply padding from the Scaffold.
        ) {
            // Define a composable for each screen in our navigation graph.
            composable(Screen.Home.route) { HomeScreen(viewModel) }
            composable(Screen.Add.route) { AddScreen(viewModel)}
            composable(Screen.Settings.route) { Text("Settings") }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MyViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Recipe List", fontSize = 20.sp)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.recipes.size) { i ->
                Text(viewModel.recipes[i].title)
            }
        }
    }
}

@Composable
fun AddScreen(viewModel: MyViewModel) {
    var recipe by remember { mutableStateOf(Recipe("", "", "")) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Recipe", fontSize = 20.sp)
        TextField(
            value = recipe.title,
            onValueChange = {recipe = recipe.copy(title = it)},
            label = { Text("Title") }
        )
        TextField(
            value = recipe.ingredients,
            onValueChange = {recipe = recipe.copy(ingredients = it)},
            label = { Text("Ingredients") }
        )
        TextField(
            value = recipe.steps,
            onValueChange = {recipe = recipe.copy(steps = it)},
            label = { Text("Steps") }
        )
        Button(onClick = { viewModel.addRecipe(recipe) }) {
            Text("Add")
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assign5_1Theme {
        Greeting("Android")
    }
}
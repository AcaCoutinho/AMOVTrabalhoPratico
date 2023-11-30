package pt.isec.amovtp.touristapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.isec.amovtp.touristapp.Greeting
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

enum class Screens(val display: String, val showAppBar : Boolean) {
    MENU("Menu", false),
    LOGIN("Login", false),
    REGISTER("Register", true),
    USER("User", true),
    LOCATIONS("Locations", true),
    ADD_LOCATIONS("Add Locations", true),
    POI("Point of Interest", true),
    ADD_POI("Add Points of Interest", true),
    ADD_CATEGORY("Add Category", true),
    SHOW_MAP("Show map", true),
    CREDITS("Credits", true);

    val route : String
        get() = this.toString()
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController:NavHostController = rememberNavController(), viewModel: LocationViewModel) {
    var showAdd by remember { mutableStateOf(false) }

    val currentScreen by navController.currentBackStackEntryAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    navController.addOnDestinationChangedListener {
        controller, destination, arguments ->
        showAdd = (destination.route in arrayOf(
            Screens.LOCATIONS.route, Screens.POI.route
        ))
    }

    Scaffold (
        snackbarHost = { SnackbarHost (hostState = snackbarHostState) },
        topBar = {
            if(currentScreen != null && Screens.valueOf(currentScreen!!.destination.route!!).showAppBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.app_name))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp()
                        }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if(Screens.valueOf(currentScreen!!.destination.route!!) == Screens.POI)
                        IconButton(onClick = {
                            navController.navigate(Screens.ADD_CATEGORY.route)
                        }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add"
                            )
                        }

                        if (Screens.valueOf(currentScreen!!.destination.route!!) != Screens.LOGIN)
                        IconButton(onClick = {
                            navController.navigate(Screens.LOGIN.route) {
                                popUpTo(Screens.LOGIN.route) { inclusive = true }
                            }
                        }) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = stringResource(R.string.msgLogout),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                )
            }
        },
        floatingActionButton = {
            if(currentScreen != null && (Screens.valueOf(currentScreen!!.destination.route!!) == Screens.POI ||
                        Screens.valueOf(currentScreen!!.destination.route!!) == Screens.LOCATIONS))
            FloatingActionButton(
                onClick = {
                    if(Screens.valueOf(currentScreen!!.destination.route!!) == Screens.POI)
                        navController.navigate(Screens.ADD_POI.route)
                    else
                        navController.navigate(Screens.ADD_LOCATIONS.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

            if(currentScreen != null && Screens.valueOf(currentScreen!!.destination.route!!) == Screens.MENU)
                FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.LOGIN.route) {
                        popUpTo(Screens.LOGIN.route) { inclusive = true }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = stringResource(R.string.msgLogout)
                    )
                }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.LOGIN.route,
            modifier = Modifier.padding(it)
        ) {
            composable (Screens.MENU.route) {
                MenuScreen(stringResource(R.string.msgHomeMenu), navController, Screens.LOCATIONS.route, Screens.CREDITS.route)
            }
            composable (Screens.LOGIN.route) {
                LoginScreen(navController, Screens.MENU.route)
            }
            composable (Screens.REGISTER.route) {
                RegisterScreen(navController)
            }
            composable (Screens.USER.route) {
                Greeting(name = Screens.USER.route)
            }
            composable (Screens.LOCATIONS.route) {
                LocationsScreen(navController = navController, viewModel = viewModel)
            }
            composable (Screens.ADD_LOCATIONS.route) {
                Greeting(name = Screens.ADD_LOCATIONS.route)
            }
            composable (Screens.SHOW_MAP.route) {
                ShowMapScreen(navController = navController, viewModel = viewModel)
            }
            composable (Screens.POI.route) {
                POIScreen(navController = navController, viewModel = viewModel)
            }
            composable (Screens.ADD_POI.route) {
                Greeting(name = Screens.ADD_POI.route)
            }
            composable (Screens.ADD_CATEGORY.route) {
                Greeting(name = Screens.ADD_CATEGORY.route)
            }
            composable (Screens.CREDITS.route) {
                CreditsScreen()
            }
        }
    }
}

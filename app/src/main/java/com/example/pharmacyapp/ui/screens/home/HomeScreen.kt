package com.example.pharmacyapp.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pharmacyapp.ui.AppScreens
import com.example.pharmacyapp.ui.theme.PharmacyAppTheme

data class PromotionItemData(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
)

val sampleSymptoms = listOf(
    "Головная боль", "Боль в желудке", "Насморк", "Кашель",
    "Боль в горле", "Спазм", "Аллергия", "Бессонница"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val samplePromotions = listOf(
        PromotionItemData(
            id = "promo1",
            title = "Весенние скидки на витамины!",
            description = "Скидка 20% на все витамины группы B и C.",
            imageUrl = "https://superapteka.ru/promos/storage/34892/01JJVPRVCRFNAB4SKJKBPJQ0VQ.jpg" // Замените
        ),
        PromotionItemData(
            id = "promo2",
            title = "Бесплатная доставка от 1000 рублей",
            description = "Заказ от 1000 рублей доставим бесплатно.",
            imageUrl = "https://img.freepik.com/free-vector/humanitarian-help-concept_52683-36821.jpg?semt=ais_hybrid&w=740" // Замените
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Главная") })
        },
        bottomBar = {
            AppBottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Акции и предложения",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(samplePromotions) { promotion ->
                        PromotionCard(
                            promotion = promotion,
                            modifier = Modifier.width(300.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Что вас беспокоит?",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(sampleSymptoms) { symptom ->
                        SymptomButton(
                            symptomName = symptom,
                            onClick = {
                                val searchQuery = symptom
                                Log.d("HomeScreen", "Navigating to Catalog with search: $searchQuery")
                                navController.navigate(AppScreens.Catalog.createRoute(searchQuery = searchQuery))
                            }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PromotionCard(promotion: PromotionItemData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = promotion.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = promotion.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = promotion.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SymptomButton(
    symptomName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Text(symptomName)
    }
}


@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val items = listOf(

        BottomNavigationItemData(AppScreens.Favorites.route, "Избранное", Icons.Filled.FavoriteBorder),
        BottomNavigationItemData(AppScreens.Catalog.createRoute(), "Поиск", Icons.Filled.Search)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { navDest ->
                    val currentRoute = navDest.route?.substringBefore('?') ?: navDest.route
                    val screenRouteBase = screen.route.substringBefore('?')
                    currentRoute == screenRouteBase
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
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

data class BottomNavigationItemData(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeScreenPreview() {
    PharmacyAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}
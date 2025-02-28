package com.example.basky


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.basky.ui.theme.BaskyTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager




data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)


class MainActivity : ComponentActivity() {
    private lateinit var dataStoreHelper: DataStoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreHelper = DataStoreHelper(this)
        setContent {
            val navController = rememberNavController()


            BaskyTheme {
                MainScreen(navController = navController, dataStoreHelper = dataStoreHelper)
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: androidx.navigation.NavHostController, dataStoreHelper: DataStoreHelper) {



    BackPressHandler(navController, "main_screen")


    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.surface,
        darkIcons = !isDarkTheme
    )

    val items = listOf(
        NavigationItem(
            title = "All",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = "История",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info
        ),
        NavigationItem(
            title = "О приложении",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()







    AppNavigationDrawer(
        drawerState = drawerState,
        items = items,
        onItemSelected = { item ->
            scope.launch { drawerState.close() }
            when (item.title) {
                "All" -> navController.navigate("main_screen")
                "История" -> navController.navigate("history_screen")
                "О приложении" -> navController.navigate("about_screen")
            }
        }
    ) {
        Scaffold(
            topBar = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                if (currentRoute != "search_screen"){
                TopAppBar(
                    title = {

                    },
                    actions = {
                        // Проверка текущего маршрута


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .windowInsetsPadding(WindowInsets(0))
                        )
                        {
                            when (currentRoute)
                            {
                                 "about_screen" ->
                                     {
                                         IconButton(
                                             onClick = {
                                                 scope.launch { drawerState.open() }
                                             },
                                             modifier = Modifier.zIndex(1f)
                                         ) {
                                             Icon(Icons.Default.Menu, contentDescription = "Меню")
                                         }
                                            Text(
                                                text = "О приложении",
                                                modifier = Modifier.fillMaxWidth()
                                                    .wrapContentSize(Alignment.CenterStart).padding(top = 10.dp, start = 50.dp),
                                                fontSize = 24.sp
                                            )
                                         }
                                "history_screen" ->
                                {
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                        modifier = Modifier.zIndex(1f)
                                    ) {
                                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                                    }
                                    Text(
                                        text = "История",
                                        modifier = Modifier.fillMaxWidth()
                                            .wrapContentSize(Alignment.CenterStart).padding(top = 10.dp, start = 50.dp),
                                        fontSize = 24.sp
                                    )
                                }

                                else -> {
                                    Button(
                                        onClick = { navController.navigate("search_screen") },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Text(
                                            text = "Поиск",
                                            textAlign = TextAlign.Start,
                                            fontSize = 16.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                        modifier = Modifier.zIndex(1f)
                                    ) {
                                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                                    }
                                }
                            }
                        }
                    }
                )
                }
            },
            floatingActionButton = {
                // Показываем только если маршрут соответствует MainScreenContent
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                if (currentRoute == "main_screen") {
                    FloatingActionButton(onClick = { navController.navigate("add_screen")}) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            },
            content = { padding ->
                NavHost(
                    navController = navController,
                    startDestination = "main_screen",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("main_screen") {
                        MainScreenContent(dataStoreHelper = dataStoreHelper)
                    }
                    composable("history_screen") {
                        HistoryScreen(dataStoreHelper = dataStoreHelper, navController = navController)
                    }
                    composable("about_screen") {
                        AboutScreen(navController = navController)
                    }
                    composable("add_screen") {
                        AddItemScreen(dataStoreHelper = dataStoreHelper, navController = navController)
                    }
                    composable("search_screen") {
                        SearchScreen(dataStoreHelper = dataStoreHelper, navController = navController)
                    }
                }
            }
        )
    }
}

@Composable
fun HistoryScreen(dataStoreHelper: DataStoreHelper, navController: NavController) {
    // Подключаем кастомный обработчик кнопки "Назад"
    BackPressHandler(navController, "main_screen")
    val historyItems by dataStoreHelper.detailedHistoryItemsFlow.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            if (historyItems.isNotEmpty()) {
                LazyColumn {
                    itemsIndexed(historyItems) { index, item ->
                        val (name, timestamp) = item
                        val formattedTime = SimpleDateFormat(
                            "HH:mm:ss dd-MM-yyyy", Locale.getDefault()
                        ).format(timestamp)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                                    onClick = { }
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${index + 1}. $name",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Удалено: $formattedTime",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "История пуста",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center) // Выровнять текст по центру
                )

            }
        }
        FloatingActionButton(
            onClick = {
                scope.launch {
                    dataStoreHelper.clearHistory()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить историю")
        }
    }
}


@Composable
fun AboutScreen(navController: NavController) {

    BackPressHandler(navController, "main_screen")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Центрируем Column
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Работу выполнили:",
                fontSize = 20.sp
            )
            Text(
                text = "Растатуров Никита Сергеевич",
                fontSize = 18.sp
            )
            Text(
                text = "Журавлев Даниил Сергеевич",
                fontSize = 18.sp
            )
        }
    }

}


@Composable
fun MainScreenContent(dataStoreHelper: DataStoreHelper) {

    // Наблюдаем за данными из DataStore
    val items by dataStoreHelper.itemsFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn {
            itemsIndexed(items) { index, item ->
                // Контейнер с ripple-эффектом
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp) // Отступ между элементами
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                            onClick = { }
                        )
                        .padding(horizontal = 16.dp, vertical = 24.dp), // Равные вертикальные и горизонтальные отступы
                    contentAlignment = Alignment.CenterStart // Центрируем содержимое по вертикали
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // Центрирование контента по вертикали
                    ) {
                        Text(
                            text = "${index + 1}. $item",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = {
                                // Удаляем элемент из списка
                                CoroutineScope(Dispatchers.IO).launch {
                                    dataStoreHelper.removeItem(item)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AddItemScreen(dataStoreHelper: DataStoreHelper, navController: NavController) {
    var text by remember { mutableStateOf("") }
    var navigateBack by remember { mutableStateOf(false) }

    // Менеджер фокуса и FocusRequester
    val textFieldFocusRequester = remember { FocusRequester() }

    // Устанавливаем фокус на TextField при открытии экрана
    LaunchedEffect(Unit) {
        textFieldFocusRequester.requestFocus()
    }

    if (navigateBack) {
        // Навигация назад после успешного добавления
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Введите данные") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(textFieldFocusRequester), // Привязываем FocusRequester
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStoreHelper.addItem(text) // Добавляем данные

                        // Сигнализируем Compose, что пора навигировать
                        withContext(Dispatchers.Main) {
                            navigateBack = true
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Сохранить")
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(dataStoreHelper: DataStoreHelper, navController: NavController) {
    var query by remember { mutableStateOf("") }

    // Собираем поток данных поиска как состояние
    val searchResults by dataStoreHelper.searchItems(query).collectAsState(initial = emptyList())

    // Локальный менеджер фокуса

    val textFieldFocusRequester = remember { FocusRequester() } // Для запроса фокуса

    // Открытие клавиатуры при появлении TextField
    LaunchedEffect(Unit) {
        textFieldFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it }, // Обновление query при каждом изменении текста
                        placeholder = { Text("Введите запрос") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(textFieldFocusRequester), // Привязка FocusRequester
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface, // Цвет фона TextField
                            cursorColor = MaterialTheme.colorScheme.onSurface // Цвет курсора
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.padding(16.dp)
        ) {
            items(searchResults) { item ->
                Text(
                    text = item,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
            }
        }
    }
}



@Composable
fun BackPressHandler(navController: NavController, destination: String) {
    val context = LocalContext.current
    val activity = context as? Activity


    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BackHandler {
        when (currentRoute) {
            "main_screen" -> {
                if (!navController.navigateUp()) {
                    activity?.finishAffinity()
                }
            }
            else -> {
                // Явный переход на указанный экран
                navController.navigate(destination) {
                    popUpTo(destination) { inclusive = true }
                }
            }
        }
    }
}


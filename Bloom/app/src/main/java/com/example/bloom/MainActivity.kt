package com.example.bloom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bloom.network.ApiSecurityManager
import com.example.bloom.network.QuoteViewModel
import com.example.bloom.ui.theme.BloomTheme
import kotlin.getValue
import com.example.bloom.network.RetrofitClient
class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var themeRepository: ThemeRepository

    //Used Google gemini for getting api key using the security manager
    private val securityManager by lazy { ApiSecurityManager(applicationContext) }

    private var meditateService by mutableStateOf<MeditateService?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MeditateService.LocalBinder
            meditateService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            meditateService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize the Security Manager inside the RetrofitClient
        RetrofitClient.init(applicationContext)

        //Save the key (This encrypts it using Keystore via SecurityManager)
        securityManager.saveApiKey(BuildConfig.API_KEY)

        themeRepository = ThemeRepository(this)

        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(themeRepository.getTheme()) }
            // Bind to the music service
            val intent = Intent(this, MeditateService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            BloomTheme(darkTheme = isDarkMode.value) {
                    MyApp(modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        meditateService,
                        themeRepository,
                        isDarkMode
                        )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel,
    meditateService: MeditateService?,
    themeRepository: ThemeRepository,
    isDarkMode: MutableState<Boolean>) {
    // Remember the NavController
    val navController = rememberNavController()

    //Remember the scroll state
    val scrollState = rememberScrollState()

    //Get number of completed tasks
    val numCompletedTasks by viewModel.completedTaskCount.collectAsState(initial = 0)

    //Get details of first incomplete task
    val firstIncompleteTask by viewModel.firstIncompleteTask.collectAsState(initial = null)
    val currentTask = firstIncompleteTask?.title?: "Well done! You have completed all your tasks!"
    val currentDescription = firstIncompleteTask?.description?: "Take a break and be proud :)"

    // Scaffold provides a framework for the app's layout
    Scaffold(
        modifier = modifier,
        topBar = {
            // Top app bar
            TopBar(navController = navController, isDarkMode,  themeRepository)
        },
        bottomBar = {
            // Bottom navigation bar
            BottomNavigationBar(currentRoute = Screen.Home.route)
        }
    ) { innerPadding ->
        // NavHost for navigating between screens
        NavHost(
            navController = navController, startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Composable for the Test screen
            composable(Screen.Home.route) {

                Column(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(state = scrollState)
                ) {
                    Flower(numCompletedTasks)

                    MeditateMusic(context = LocalContext.current, music = meditateService)

                    Spacer(modifier = Modifier.height(15.dp))

                    CurrentTask(task = currentTask, description = currentDescription)

                    Spacer(modifier = Modifier.height(15.dp))

                    DailyQuote(modifier = Modifier, quoteViewModel = viewModel())
                }
            }
        }
    }
}


// Composable for the top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, isDarkMode: MutableState<Boolean>, themeRepository: ThemeRepository = ThemeRepository(LocalContext.current)) {
    var menuExpanded by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    // A screen is a top-level screen if it's in our bottom nav.
    val isTopLevelDestination = bottomNavItems.any { it.route == currentDestination?.route }

    TopAppBar(
        title = { Text(
            "Bloom",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            fontSize = 55.sp)
                },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary// Background color
        ),
        navigationIcon = {
            // Show the back button if we're not on a top-level screen.
            if (!isTopLevelDestination) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            //https://www.geeksforgeeks.org/kotlin/icon-toggle-button-in-android-using-jetpack-compose/
            IconToggleButton (
                checked = isDarkMode.value,
                onCheckedChange = {
                    isDarkMode.value = it
                    themeRepository.saveTheme(it)
                },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background, shape = CircleShape),
            ){
                Icon(
                    imageVector = if (isDarkMode.value) Icons.Filled.WbSunny else Icons.Filled.DarkMode,
                    contentDescription = "Icon",
                    modifier = Modifier.size(30.dp)
                )

            }
            // More icon button to expand the dropdown menu
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
            // Dropdown menu for navigation
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                // Dropdown menu item for Journal
                DropdownMenuItem(
                    text = { Text("Journal") },
                    onClick = {
                        val intent = Intent(context, JournalActivity::class.java)
                        context.startActivity(intent)
                        menuExpanded = false
                    }
                )
                // Dropdown menu item for Tasks
                DropdownMenuItem(
                    text = { Text("Tasks") },
                    onClick = {
                        val intent = Intent(context, TaskActivity::class.java)
                        context.startActivity(intent)
                        menuExpanded = false
                    }
                )

            }
        }
    )
}

// Used GeeksforGeeks for using services to play music - https://www.geeksforgeeks.org/kotlin/services-in-android-using-jetpack-compose/
@Composable
fun MeditateMusic(modifier: Modifier = Modifier, context: Context, music: MeditateService?) {

    var isPlaying by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }

    var expanded by rememberSaveable { mutableStateOf(false) }

    //https://dev.to/atif_rehman_dec1a8f2103de/mastering-launched-effect-in-jetpack-compose-what-why-how-3kh1
    LaunchedEffect(music) {
        music?.let{
            isPlaying = it.isPlaying()
            isStarted = it.isStarted()
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp))
        {//Used Developers for UI styling -
            // https://developer.android.com/develop/ui/compose/text/style-paragraph
            //https://developer.android.com/develop/ui/compose/layouts/basics
            Text(
                text = "Guided Meditation",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) {
                            isStarted = true
                            music?.play()
                        } else {
                            music?.pause()
                        }
                    },
                ) {
                    if (isPlaying) {
                        Icon(
                            Icons.Filled.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(60.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                if (isStarted) {
                    IconButton(
                        onClick = {
                            music?.stop()
                            isPlaying = false
                            isStarted = false
                        },
                    ) {
                        Icon(
                            Icons.Filled.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "This is 'The Tension Release Meditation' by Vidyamala Burch, Breathworks",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "For more you can visit The Free Mindfulness Project at https://www.freemindfulness.org/download",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {expanded = !expanded},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (expanded) "Hide Details" else "Show Details",
                    fontSize = 20.sp
                )
            }
        }
    }

}

@Composable
fun CurrentTask(modifier: Modifier = Modifier, task: String, description: String){

    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .padding(bottom = extraPadding.coerceAtLeast(0.dp))
        )
        {//https://developer.android.com/develop/ui/compose/text/style-paragraph
            //https://developer.android.com/develop/ui/compose/layouts/basics
            Text(
                text = "Current Task",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = task,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (expanded) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            Button(
                onClick = {expanded = !expanded},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (expanded) "Hide Details" else "Show Details",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun DailyQuote(modifier: Modifier = Modifier, quoteViewModel: QuoteViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        quoteViewModel.fetchQuotes()
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp))
        {//https://developer.android.com/develop/ui/compose/text/style-paragraph
            //https://developer.android.com/develop/ui/compose/layouts/basics
            Text(
                text = "Quote of the Day",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (quoteViewModel.isLoading) {
                Text(
                    "Loading...",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            quoteViewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
            }

            quoteViewModel.quotes.forEach { quoteItem ->
                Text(
                    text = "\"${quoteItem.quote}\"",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "- ${quoteItem.author}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center

                )
            }

        }
    }

}

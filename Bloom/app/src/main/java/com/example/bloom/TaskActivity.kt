package com.example.bloom

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.bloom.ui.theme.BloomTheme
import kotlin.getValue


class TaskActivity : ComponentActivity() {

    //Initialising the viewModel for tasks
    private val viewModel: TaskViewModel by viewModels()
    //Initialising the theme repository
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialise the theme repository
        themeRepository = ThemeRepository(this)

        //Create a notification channel for tasks
        val notificationChannel= NotificationChannel(
            "task_notification",
            "Task",
            NotificationManager.IMPORTANCE_HIGH
        )
        //Get the system NotificationManager service
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //Register the notification channel with the system
        notificationManager.createNotificationChannel(notificationChannel)

        enableEdgeToEdge()
        setContent {
            BloomTheme(darkTheme = themeRepository.getTheme()) {

                //Create a notification service
                val notificationService=NotificationService(this)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TaskScreen(
                        viewModel = viewModel,
                        onBack = { finish() },
                        notificationService
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel, onBack: () -> Unit, notificationService: NotificationService) {

    //Get all tasks and the number of completed tasks
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val numCompletedTasks by viewModel.completedTaskCount.collectAsState(initial = 0)

    Scaffold(
        topBar = {//Top app bar
            TopAppBar(
                title = { Text("My Tasks", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        bottomBar =  { BottomNavigationBar(currentRoute = Screen.Tasks.route) },//Bottom navigation bar
        containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyColumn(//Lazy column for tasks
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                item{//Flower composable
                    Flower(numCompletedTasks)
                }
                items(tasks) { task ->//Display each task
                    TaskItem(
                        task = task,
                        onToggle = {//Toggle the task's completion status

                            val isNowComplete = !task.isComplete
                            viewModel.update(task.copy(isComplete = !task.isComplete))//Update the task in the database

                            if (isNowComplete) {//If the task is now complete

                                if (numCompletedTasks + 1 >= 5) {//If all tasks are complete show completed task notification
                                    notificationService.showTaskCompletedNotification()
                                } else {

                                    val nextIncomplete = tasks.firstOrNull {//Find the next incomplete task
                                        it.id != task.id && !it.isComplete
                                    }
                                    if (nextIncomplete != null) {//If there is a next incomplete task show new task notification
                                        notificationService.showNewTaskNotification(nextIncomplete.title)
                                    }
                                }
                            }
                       },
                    )
                }
            }
        }

    }
}

//Task item composable
@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, modifier: Modifier = Modifier) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(//Checkbox for task completion
                checked = task.isComplete,
                onCheckedChange = { onToggle() }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(//Displaying task title
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (expanded) {//If expanded, display task description
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            IconButton(onClick = { expanded = !expanded }) {//Button to expand task details
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Show More",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

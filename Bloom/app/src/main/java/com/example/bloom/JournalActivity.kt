package com.example.bloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bloom.ui.theme.BloomTheme
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.getValue

class JournalActivity : ComponentActivity() {

    //Initialising the viewModel for journal
    private val viewModel: JournalViewModel by viewModels()

    //Initialising the theme repository
    private lateinit var themeRepository: ThemeRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialising the theme repository
        themeRepository = ThemeRepository(this)
        enableEdgeToEdge()
        setContent {
            BloomTheme(darkTheme = themeRepository.getTheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    JournalScreen(
                        viewModel = viewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

//Composable for layout of journal screen layout and functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel, onBack: () -> Unit) {

    //Getting all the journals from the viewModel
    val journals by viewModel.allJournals.collectAsState(initial = emptyList())

    //State for the new note
    var newNote by remember { mutableStateOf("") }

    Scaffold(
        topBar = {//Top Bar of screen
            TopAppBar(
                title = { Text("Journal", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                actions = {//Button for clearing all journals
                    IconButton(onClick = { viewModel.deleteAll() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear All")
                    }
                }
            )
        },
        bottomBar =  { BottomNavigationBar(currentRoute = Screen.Journal.route) },//Bottom navigation bar
        ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(//Text field for user to input their entry
                    value = newNote,
                    onValueChange = { newNote = it },
                    label = { Text("Your Reflection") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {//Button for adding the new entry
                    if (newNote.isNotBlank()) {
                        val current = System.currentTimeMillis()
                        viewModel.insert(Journal(notes = newNote, entryDate = current))//Inserting the new entry into the database
                        newNote = ""
                    }
                }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Lazy column for displaying all the entries
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(journals) { journal ->
                    JournalEntry(
                        journal = journal,
                        onDelete = { viewModel.delete(journal) }//Deleting the entry when delete button is pressed
                    )
                }
            }
        }
    }
}

@Composable
fun JournalEntry(journal: Journal, onDelete: () -> Unit) {

    //Used https://medium.com/@atharvapajgade/working-with-date-objects-in-kotlin-e6af6cb9688c
    //Formatting the current date
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val formattedDate = formatter.format(Date(journal.entryDate))


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(//Displaying the entry date
                text = "Entry Date:  $formattedDate",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(//Displaying the entry text
                text = journal.notes,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onDelete) {//Button for deleting the entry
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

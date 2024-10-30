package edu.farmingdale.datastoresimplestoredemo
import android.content.Context
import java.io.PrintWriter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.farmingdale.datastoresimplestoredemo.data.AppPreferences
import edu.farmingdale.datastoresimplestoredemo.ui.theme.DataStoreSimpleStoreDemoTheme
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Write and read from internal file
        writeToInternalFile()
        val fileContents = readFromInternalFile()
        Log.d("MainActivity", fileContents) // Log the contents for debugging

        // Set up the theme and content
        setContent {
            val store = AppStorage(LocalContext.current)
            val appPrefs = store.appPreferenceFlow.collectAsState(initial = AppPreferences())

            DataStoreSimpleStoreDemoTheme(
                darkTheme = appPrefs.value.darkMode // Set dark theme based on preference
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    // Function to write a haiku to an internal file
    private fun writeToInternalFile() {
        val outputStream: FileOutputStream = openFileOutput("fav_haiku", Context.MODE_PRIVATE)
        val writer = PrintWriter(outputStream)

        // Write three lines of a haiku
        writer.println("This world of dew")
        writer.println("is a world of dew,")
        writer.println("and yet, and yet.")
        writer.close()
    }

    // Function to read from the internal file and add custom formatting
    private fun readFromInternalFile(): String {
        val inputStream = openFileInput("fav_haiku")
        val reader = inputStream.bufferedReader()
        val stringBuilder = StringBuilder()

        // Append each line and additional text
        reader.forEachLine {
            stringBuilder.append(it).append("\nBCS 371\n").append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}


@Composable
fun DataStoreDemo(modifier: Modifier) {
    val store = AppStorage(LocalContext.current)
    val appPrefs = store.appPreferenceFlow.collectAsState(AppPreferences())
    val coroutineScope = rememberCoroutineScope()
    var userNameInput by remember { mutableStateOf("")}

    Column (modifier = Modifier.padding(50.dp)) {
        Text("Values = ${appPrefs.value.userName}, " +
                "${appPrefs.value.highScore}, ${appPrefs.value.darkMode}")
        TextField(
            value = userNameInput,
            onValueChange = { userNameInput = it},
            label = { Text("Enter Username")},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)

        )
        Button(onClick = {
            coroutineScope.launch {
                store.saveUsername(userNameInput)
            }

        }) {
            Text("Save UserName")
        }
        Button(onClick = {
            coroutineScope.launch {
                store.saveHighScore(200)
            }
        }) {
            Text("Save High Score")
        }
        Button(onClick = {
            coroutineScope.launch {
                store.saveDarkMode(!appPrefs.value.darkMode)
            }
        }) {
            Text("Toggle Dark Mode")
        }
    }
}

// ToDo 1: Done Modify the App to store a high score and a dark mode preference
// ToDo 2: Done Modify the APP to store the username through a text field
// ToDo 3: Done Modify the App to save the username when the button is clicked
// ToDo 4: Modify the App to display the values stored in the DataStore



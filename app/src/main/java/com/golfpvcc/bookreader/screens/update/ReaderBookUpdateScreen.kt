package com.golfpvcc.bookreader.screens.update

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.golfpvcc.bookreader.components.InputField
import com.golfpvcc.bookreader.components.ReaderAppBar
import com.golfpvcc.bookreader.data.DataOrException
import com.golfpvcc.bookreader.model.MBook
import com.golfpvcc.bookreader.screens.home.HomeScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookUpdateScreen(
    navController: NavController,
    bookItemId: String,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Update Book",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.popBackStack()
        }

    }) { values ->
        val bookInfo = produceState<DataOrException<List<MBook>,
                Boolean,
                Exception>>(
            initialValue = DataOrException(
                data = emptyList(),
                true, Exception("")
            )
        ) {
            value = viewModel.data.value
        }.value

        Surface(
            modifier = androidx.compose.ui.Modifier
                .padding(values)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Log.d("VIN", "Bookupdate screen: ${viewModel.data.value.data.toString()}")
                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                    bookInfo.loading = false
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        ShowBookUpdate(
                            bookInfo = viewModel.data.value,
                            bookItemId = bookItemId
                        )
                    }
                    ShowSimpleForm(book = viewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController)
                } // end of surface 2
            } // end if
        } // end of column

    } // end of surface
}

@Composable
fun ShowSimpleForm(book: MBook, navController: NavController) {

    val notesText = remember {
        mutableStateOf("")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }
    val isFinishedReading = remember {
        mutableStateOf(false)
    }
    SimpleForm(
        Modifier,
        defaultVules = if (book.notes.toString().isNotEmpty()) book.notes.toString()
        else "No thoughts available",
    ) { note ->
        notesText.value = note
    } // end of simple form

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(
            onClick = {isStartedReading.value = true },
            enabled = book.startedReading == null
        ) {

            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading!!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started on: ${book.startedReading}") // format for date
            }
        }   // end of row
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = {isFinishedReading.value = true },
            enabled = book.finishedReading == null
        ) {

            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as read")
                } else {
                    Text(
                        text = "Finished Reading!!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Finished on: ${book.finishedReading}") // format for date
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier,
    loading: Boolean = false,
    defaultVules: String = "Great Book!",
    onSearch: (String) -> Unit
) {
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultVules) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid =
            remember(textFieldValue.value) { textFieldValue.value.trim().isNotEmpty() }

        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelId = "Enter your thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })

    } // end of column
}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>,
            Boolean,
            Exception>, bookItemId: String
) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})
            }
        } // end of If
    } // end of row

}

@Composable
fun CardListItem(
    book: MBook,
    onPressDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(
                start = 4.dp,
                end = 4.dp,
                top = 4.dp,
                bottom = 8.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { },
        elevation = CardDefaults.cardElevation()
    ) {
        val imageUrl: String = if (book.photoUrl?.isEmpty() == true)
            ""
        else
            book.photoUrl.toString()
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Book image",
                modifier = Modifier
                    .heightIn(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 3.dp, end = 38.dp)
                        .width(130.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp)
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp)
                )
            } // end of column
        } // end of row
    } // end of Card
} // CardListItem()

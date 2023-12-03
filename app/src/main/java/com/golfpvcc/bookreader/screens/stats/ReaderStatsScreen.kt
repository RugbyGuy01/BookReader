package com.golfpvcc.bookreader.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.golfpvcc.bookreader.components.ReaderAppBar
import com.golfpvcc.bookreader.model.Item
import com.golfpvcc.bookreader.model.MBook
import com.golfpvcc.bookreader.navigation.ReaderScreens
import com.golfpvcc.bookreader.screens.home.HomeScreenViewModel
import com.golfpvcc.bookreader.screens.search.BookRow
import com.golfpvcc.bookreader.utils.formatDate
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderStatsScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Book Details",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController
            ) {
                navController.popBackStack()
            }
        }
    ) { values ->
        Surface(
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
        ) {
            // only show books by this user that have been read stop at 2:56 Class 280
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()     // empty list is assigned to the books
            }
            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Person,
                            contentDescription = "Icon"
                        )
                    }   // end of box
                    Text(
                        text = "Hi, ${
                            currentUser?.email.toString().split("@")[0]
                                .uppercase(Locale.getDefault())
                        }"
                    )
                } // end of row
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    val readBooksList: List<MBook> =
                        if (!viewModel.data.value.data.isNullOrEmpty()) {
                            books.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)

                            }
                        } else {
                            emptyList()
                        } // end of If
                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }
                    Column(
                        modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.titleMedium)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")
                    }
                }// end of card
                if (viewModel.data.value.loading == true) {
                    LinearProgressIndicator()
                } else {
                    Divider()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // filler books by finisj=hed
                        val readBooks: List<MBook> =
                            if (!viewModel.data.value.data.isNullOrEmpty()) {
                                viewModel.data.value.data!!.filter { mBook ->
                                    (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                                }
                            } else {
                                emptyList()
                            } // end if
                        items(items = readBooks) { book ->
                            BookRowStats(book = book)
                        }
                    } // lazy column

                }
            } // end of surface
        }
    }
}   // end of function

@Composable
fun BookRowStats(
    book: MBook,
) {
    Card(modifier = Modifier
        .clickable {
        }
        .fillMaxWidth()
        .height(120.dp) // vinnie 11/21/2023
        .padding(3.dp),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        )) {
        val painter = rememberAsyncImagePainter("")

        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imageUrl: String = if (book.photoUrl.toString().isEmpty())
                ""
            else
                book.photoUrl.toString()
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Book image",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .padding(4.dp)
            )
            Column() {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    if(book.rating!! >= 4){
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription ="Thumps Up",
                            tint = Color.Green.copy(alpha = 0.5f))
                    } else
                        Box { }
                }
                Text(
                    text = "Author: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall
                )
                // doto add more fields later
                Text(
                    text = "Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = "Finished ${formatDate(book.finishedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall
                )
            } // end of column

        } // end of row
    }


}
package com.golfpvcc.bookreader.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.golfpvcc.bookreader.components.FABContent
import com.golfpvcc.bookreader.components.ListCard
import com.golfpvcc.bookreader.components.ReaderAppBar
import com.golfpvcc.bookreader.components.TitleSection
import com.golfpvcc.bookreader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import com.golfpvcc.bookreader.model.MBook as MBook


//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val showProfile: Boolean = true
    val title: String = "A.Reader"

    Scaffold(topBar = {
        ReaderAppBar(title = "A.Reader", navController = navController)
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showProfile) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Logo Icon",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .scale(scale = 0.9f)
                        )
                    }
                    Text(
                        text = title,
                        color = Color.Red.copy(alpha = 0.7f),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    FirebaseAuth.getInstance().signOut().run {
                        navController.navigate(ReaderScreens.LoginScreen.name)
                    }
                }) {
                    Icon(
                        Icons.Default.Close, contentDescription = null,
                        tint = Color.Green.copy(alpha = 0.4f)
                    )        // log out Icon
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            )
        )
    }, floatingActionButton = {
        FABContent {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) { values ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            HomeContent(navController, viewModel)
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if(!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
        Log.d("DATA",  "HomeCotent list books: ${listOfBooks.toString()}" )
    } else
    {
        Log.d("DATA",  "HomeCotent isNullOrEmpty" )
    }

    var email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty())
        email?.split("@")?.get(0)
    else
        "N/A"

    Column(
        Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {        // row here
            TitleSection(label = "Your reading \n " + "activity right now ...")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable { navController.navigate(ReaderScreens.ReaderStatsScreen.name) }
                        .size(45.dp),
                    tint = Color.Magenta)

                Text(
                    text = currentUserName!!,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Divider()
            }
        }   // end of row
        ReadingRightNowArea(listOfBooks = listOf(), navController = navController)
        TitleSection(label = "Reading List")
        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {

    HorizontalScrollableComponent(listOfBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    onCardPresses: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .horizontalScroll(scrollState)
    ) {
        for (book in listOfBooks) {
            ListCard(book) {
                onCardPresses(book.googleBookId.toString())

            }
        }
    }
}


@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {
    //Filter books by reading now
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(readingNowList){
        Log.d("TAG", "BoolListArea: $it")
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}


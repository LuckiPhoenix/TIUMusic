package com.example.TIUMusic.Screens

import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.getTopPicks
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.SecondaryColor
import java.io.File

@Composable
fun LibraryScreen(navController: NavController,
                  onItemClick :  (MusicItem) -> Unit,
                  onTabSelected: (Int) -> Unit,
                  ytmusicViewModel : YtmusicViewModel,
                  userViewModel: UserViewModel = hiltViewModel(),
                  modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val windowSize = rememberWindowSize()

    val userPlaylists by ytmusicViewModel.userPlaylists.collectAsState()
    var showUsernameDialog by remember { mutableStateOf(false) } // use for username update
    var showProfileImageDialog by remember { mutableStateOf(false) } // use for profile image update
    var showPlaylistDialog by remember { mutableStateOf(false) } // use for creating user's playlist

    // Transition variables
    var isScrolled by remember { mutableStateOf(false) }
    val transitionState = updateTransition(targetState = isScrolled, label = "AppBarTransition")

    // Calculate dynamic values
    val expandedHeight = Dimensions.topBarExpandedHeight()
    val collapsedHeight = Dimensions.topBarCollapsedHeight()
    val expandedTitleSize = Dimensions.expandedTitleSize()
    val collapsedTitleSize = Dimensions.collapsedTitleSize()
    val bottomNavHeight = 56.dp // Define bottom nav height

    LaunchedEffect(Unit) {
        ytmusicViewModel.getUserPlaylists(true);
    }

    // Animation values
    val alpha by transitionState.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Alpha"
    ) { state -> if (state) 0.9f else 1f }

    val translationX by transitionState.animateDp(
        transitionSpec = { tween(durationMillis = 500) },
        label = "TranslationX"
    ) { state ->
        if (state) {
            when (windowSize) {
                WindowSize.COMPACT -> (LocalConfiguration.current.screenWidthDp.dp / 2) - 52.dp
                WindowSize.MEDIUM -> (LocalConfiguration.current.screenWidthDp.dp / 2) - 48.dp
            }
        } else 0.dp
    }

    val titleSize by transitionState.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "TextSize"
    ) { state ->
        if (state) collapsedTitleSize.value else expandedTitleSize.value
    }

    val height by transitionState.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "height"
    ) { state -> if (state) collapsedHeight else expandedHeight }

    LaunchedEffect(scrollState.value) {
        isScrolled = scrollState.value > expandedHeight.value
    }

    val currentUser by userViewModel.currentUser.observeAsState()
    val username: String = currentUser?.fullName ?: "User"
    var isPressed by remember { mutableStateOf(false) }
    val buttonWidth by animateDpAsState(
        targetValue = if (isPressed) 300.dp else 80.dp,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseInOut
        )
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(BackgroundColor)
                    .padding(top = expandedHeight)
            ) {
                Row(Modifier.padding(start = 32.dp)) {
                    Image(
                        painter = if(currentUser?.profilePicture == null) painterResource(R.drawable.profile_pic) else rememberAsyncImagePainter(
                            model = currentUser?.profilePicture
                        ),
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(180.dp))
                            .size(86.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = username,
                            fontSize = 24.sp,
                            color = White,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(4.dp)
                        )
                        Button(
                            onClick = { isPressed = !isPressed },
                            modifier = Modifier
                                .padding(4.dp)
                                .background(
                                    color = SecondaryColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .height(48.dp) // Fixed height
                                .width(buttonWidth), // Elongate horizontally on press
                            contentPadding = PaddingValues(0.dp) // Remove internal padding to make it compact
                        ) {
                            // Main button text
                            if(!isPressed) {
                                Text(
                                    "Edit",
                                    fontSize = 16.sp,
                                    color = White
                                )
                            }

                            // When button is pressed, show sub-buttons horizontally
                            if (isPressed) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, end = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Sub-button 1: Username
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RectangleShape)
                                            .clickable { showUsernameDialog = true }
                                    ) {
                                        Text("Username", fontSize = 12.sp)
                                    }
                                    // Divider between buttons
                                    Divider(
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(1.dp)
                                            .background(Color.Gray)
                                    )

                                    // Sub-button 2: Picture
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RectangleShape)
                                            .clickable { showProfileImageDialog = true }
                                    ) {
                                        Text("Picture", fontSize = 12.sp)
                                    }

                                    // Divider between buttons
                                    Divider(
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(1.dp)
                                            .background(Color.Gray)
                                    )

                                    // Sub-button 3: Password
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RectangleShape)
                                            .clickable { navController.navigate("recover/${currentUser?.email}") }
                                    ) {
                                        Text("Password", fontSize = 12.sp)
                                    }
                                    // Divider between buttons
                                    Divider(
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(1.dp)
                                            .background(Color.Gray)
                                    )

                                    // Sub-button 4: Create a new Playlist
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RectangleShape)
                                            .clickable { showPlaylistDialog = true }
                                    ) {
                                        Text("Create new Playlist", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                }
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)) {
                    Text(
                        "$username's playlists:",
                        fontSize = 16.sp,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = {
                            userViewModel.logout()
                            WebStorage.getInstance().deleteAllData()
                            CookieManager.getInstance().removeAllCookies(null)
                            CookieManager.getInstance().flush()
                            navController.navigate("youtubeLogin") {
                                popUpTo("library") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .background(color = SecondaryColor, shape = RoundedCornerShape(16.dp))
                            .height(32.dp)
                    ) {
                        Text("Log Out", fontSize = 12.sp)
                    }
                }
                // Use Modifier.height or a fixed height if needed
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .height(600.dp), // Adjust height as needed
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Local Playlist in Database
                    currentUser?.let {
                        items(it.playlists){ item ->
                            val musicItem = MusicItem(
                                title = item.title,
                                videoId = "",
                                type = 1,
                                playlistId = item.id,
                                artist = item.description,
                                imageUrl = item.picture.toString(),
                            )
                            AlbumCard(
                                item = musicItem,
                                modifier = Modifier,
                                imageSize = 180.dp,
                                onClick = {
                                    onItemClick(musicItem)
                                }
                            )
                        }
                    }
                    //Remote Playlist from Youtube
                    items(userPlaylists) { item ->
                        AlbumCard(
                            item = item,
                            modifier = Modifier,
                            imageSize = 180.dp,
                            onClick = {
                                onItemClick(item)
                            }
                        )
                    }
                    item {Spacer(modifier = Modifier.height(88.dp))}
                }
            }

            InputDialog(showUsernameDialog, {showUsernameDialog = false}, {userViewModel.updateUsername(it)})

            InputDialog(showPlaylistDialog, {showPlaylistDialog = false}, {userViewModel.addPlaylist(it)})

            if (showProfileImageDialog) {
                ProfilePictureDialog(
                    showDialog = showProfileImageDialog,
                    currentProfilePicture = currentUser?.profilePicture,
                    onConfirm = { newImagePath ->
                        showProfileImageDialog = false
                        userViewModel.updateProfilePicture(newImagePath)
                    },
                    onRemove = {
                        showProfileImageDialog = false
                        userViewModel.updateProfilePicture(null)
                    },
                    onDismissRequest = { showProfileImageDialog = false }
                )
            }

            // Top app bar
            AnimatedTopAppBar(
                title = "Library",
                alpha = alpha,
                translationX = translationX,
                titleSize = titleSize.sp,
                height = height
            )


            // Bottom navigation
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
fun InputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    if (showDialog) {
        var userInput by remember { mutableStateOf(TextFieldValue("")) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Enter your new Username", fontSize = 18.sp) },
            text = {
                Column {
                    Text("My new Username is: ")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("input") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(userInput.text)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfilePictureDialog(
    showDialog: Boolean,
    currentProfilePicture: String?,
    onConfirm: (String) -> Unit,
    onRemove: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

        val context = LocalContext.current
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri = uri
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(if (selectedImageUri == null) "Select Profile Picture" else "Confirm Profile Picture")
            },
            text = {
                if (selectedImageUri == null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (currentProfilePicture != null) {
                            Image(
                                painter = rememberAsyncImagePainter(currentProfilePicture),
                                contentDescription = "Current Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pick Image")
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            },
            confirmButton = {
                if (selectedImageUri != null) {
                    Button(onClick = {
                        val newImagePath = saveImageToInternalStorage(context, selectedImageUri!!)
                        onConfirm(newImagePath)
                        onDismissRequest()
                    }) {
                        Text("Confirm")
                    }
                } else {
                    Button(onClick = onRemove) {
                        Text("Remove Image")
                    }
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun saveImageToInternalStorage(context: Context, imageUri: Uri): String {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri) ?: return ""
    val fileName = "profile_picture_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, fileName)

    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    inputStream.close()
    return file.absolutePath
}

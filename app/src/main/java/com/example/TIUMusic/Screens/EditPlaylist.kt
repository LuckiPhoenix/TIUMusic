package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.TIUMusic.Login.Playlist
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistScreen(
    navController: NavController,
    originalPlaylist: String,
    onDismiss: () -> Unit,
    onPlaylistEdit: (Playlist) -> Unit,
    viewModel: UserViewModel
) {
    var isPublic by remember { mutableStateOf(false) }
    val copyOfOriginalPlaylist by viewModel.playlist.observeAsState()
    val songsToDelete = remember { mutableStateOf(mutableListOf<MusicItem>()) }

    LaunchedEffect(Unit) {
        viewModel.getPlaylistById(originalPlaylist)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if(copyOfOriginalPlaylist != null){
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.x),
                        contentDescription = "Close",
                        tint = PrimaryColor
                    )
                }
                IconButton(onClick = {
                    copyOfOriginalPlaylist!!.songs =
                        copyOfOriginalPlaylist!!.songs.filter { it !in songsToDelete.value }.toMutableList()
                    onPlaylistEdit(copyOfOriginalPlaylist!!)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.check_solid),
                        contentDescription = "Confirm",
                        tint = Color.Red
                    )
                }
            }

            AsyncImage(
                model = copyOfOriginalPlaylist!!.picture,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            // Playlist Title Input
            OutlinedTextField(
                value = copyOfOriginalPlaylist!!.title,
                onValueChange = { copyOfOriginalPlaylist!!.title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Enter Playlist Title", color = Color.Gray) },
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add Description Input
            OutlinedTextField(
                value = copyOfOriginalPlaylist!!.description,
                onValueChange = { copyOfOriginalPlaylist!!.description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Add Description", color = Color.Gray) },
                textStyle = TextStyle(color = Color.White),
                maxLines = 2,
                singleLine = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show on Profile Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show on My Profile and in Search",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Red,
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playlist Songs

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(copyOfOriginalPlaylist!!.songs) { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isChecked = songsToDelete.value.contains(song)
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (it) {
                                    songsToDelete.value.add(song)
                                } else {
                                    songsToDelete.value.remove(song)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Red,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = song.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = song.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlaylistScreen(
    navController: NavController,
    originalPlaylist: Playlist,
    onDismiss: () -> Unit,
    onPlaylistEdit: (Playlist) -> Unit
) {
    val viewModel: UserViewModel = viewModel()
    var isPublic by remember { mutableStateOf(false) }
    var copyOfOriginalPlaylist = originalPlaylist
    val songsToDelete = remember { mutableStateOf(mutableListOf<MusicItem>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = "Close",
                    tint = PrimaryColor
                )
            }
            IconButton(onClick = {
                copyOfOriginalPlaylist.songs =
                    copyOfOriginalPlaylist.songs.filter { it !in songsToDelete.value }.toMutableList()
                onPlaylistEdit(copyOfOriginalPlaylist)
            }) {
                Icon(
                    painter = painterResource(R.drawable.check_solid),
                    contentDescription = "Confirm",
                    tint = Color.Red
                )
            }
        }

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .background(PrimaryColor)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera Icon",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Playlist Title Input
        OutlinedTextField(
            value = copyOfOriginalPlaylist.title,
            onValueChange = { copyOfOriginalPlaylist.title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Playlist Title", color = Color.Gray) },
            textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
package com.example.fitnesspaw.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnesspaw.R
import com.example.fitnesspaw.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Observe state from ViewModel
    val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()
    val petName by viewModel.petName.collectAsStateWithLifecycle()
    val petHappiness by viewModel.petHappiness.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()

    var newName by remember { mutableStateOf("") }
    var showFeedHearts by remember { mutableStateOf(false) }

    // Always show idle (happy) image on the Pets screen
    val petImageRes = when (selectedPet) {
        0    -> R.drawable.cat_happy   // Cat idle
        1    -> R.drawable.dog_happy   // Dog idle
        else -> R.drawable.panda_happy // Panda idle
    }

    val petTypeText = when (selectedPet) {
        0 -> "Cat"
        1 -> "Dog"
        else -> "Panda"
    }

    val petMoodText = when {
        petHappiness < 40 -> "Sad 😢"
        petHappiness < 80 -> "Sleepy 😴"
        else -> "Happy! ✨"
    }

    // Trigger hearts animation when fed
    LaunchedEffect(showFeedHearts) {
        if (showFeedHearts) {
            delay(1500)
            showFeedHearts = false
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))

                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pet Companion 🐾",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Keep your virtual pet active and fed",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }

                    // COINS DISPLAY
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = coins.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // MAIN PET HERO DISPLAY CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(200.dp)
                        ) {
                            // Pet Image Frame (no ring)
                            Image(
                                painter = painterResource(id = petImageRes),
                                contentDescription = "Virtual Pet Companion",
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )

                            // Heart Animation Overlay
                            androidx.compose.animation.AnimatedVisibility(
                                visible = showFeedHearts,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "❤️🍗❤️",
                                    fontSize = 32.sp,
                                    modifier = Modifier.background(Color.Transparent)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pet Name & Type + Mood Badge
                        Text(
                            text = petName,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = petTypeText,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // FEED PET ACTION SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Feed your Companion 🍖",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Spend 10 tokens to feed your pet. Adds +15 happiness instantly!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (coins >= 10) {
                                    val success = viewModel.feedPet()
                                    if (success) {
                                        showFeedHearts = true
                                        Toast.makeText(context, "$petName says Thank you! Yum! 🍖", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Insufficient Coins! Complete habits to earn tokens.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🍖 Feed Milo (10 Coins)", fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // COMPANION SELECTOR AND RENAMER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Customize Companion",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Rename text field
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Rename Pet") },
                            placeholder = { Text(petName) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            trailingIcon = {
                                Button(
                                    onClick = {
                                        if (newName.isNotBlank()) {
                                            viewModel.renamePet(newName)
                                            newName = ""
                                            Toast.makeText(context, "Pet renamed successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Save")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Switch active Pet type:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Three card switcher: Cat, Dog, Panda
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Cat Selector
                            PetTypeItem(
                                title = "Cat",
                                emoji = "🐱",
                                isSelected = selectedPet == 0,
                                onClick = { viewModel.changeSelectedPet(0) },
                                modifier = Modifier.weight(1f)
                            )

                            // Dog Selector
                            PetTypeItem(
                                title = "Dog",
                                emoji = "🐶",
                                isSelected = selectedPet == 1,
                                onClick = { viewModel.changeSelectedPet(1) },
                                modifier = Modifier.weight(1f)
                            )

                            // Panda Selector
                            PetTypeItem(
                                title = "Panda",
                                emoji = "🐼",
                                isSelected = selectedPet == 2,
                                onClick = { viewModel.changeSelectedPet(2) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun PetTypeItem(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emoji, fontSize = 22.sp)
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

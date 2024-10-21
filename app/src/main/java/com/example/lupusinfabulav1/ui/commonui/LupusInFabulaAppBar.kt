package com.example.lupusinfabulav1.ui.commonui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LupusInFabulaAppBar(
    title : String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
){
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                    )
                }
            }
        }
    )
}
package com.example.lupusinfabulav1.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.ImageRepository
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.getPainter
import com.example.lupusinfabulav1.ui.commonui.CancelAndConfirmButtons

@Composable
fun NewPlayerScreen(
    onConfirmClick: (String, PlayerImageSource) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    var name by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),//"contract" says what action/activity we want to perform/launch
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    var randomImage by remember { mutableIntStateOf(ImageRepository.defaultImages.random()) }
    val onRandomImageClick = {
        selectedImageUri = null
        randomImage = ImageRepository.defaultImages.random()
    }

    val imageSource = if (selectedImageUri != null) {
        PlayerImageSource.UriSource(selectedImageUri.toString())
    } else {
        PlayerImageSource.Resource(randomImage)
    }

    val painter = when (imageSource) {
        is PlayerImageSource.Resource -> painterResource(id = imageSource.resId)
        is PlayerImageSource.UriSource -> rememberAsyncImagePainter(model = imageSource.uri) // Use coil for Uri
    }

    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        //Spacer( modifier = Modifier.weight(1f) )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
        ) {
            val imageModifier = Modifier
                .fillMaxSize()
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)  //it tells what type of media you want to show (if only video, or only images and so on )
                    )
                }
            Image(
                painter = imageSource.getPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
            /*
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } else {
                Image(
                    painter = painterResource(id = randomImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
             */
            FilledIconButton(
                onClick = { onRandomImageClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                )
            }
        }
        Spacer( modifier = Modifier.weight(1f) )
        EditNumberField(
            label = R.string.name_field,
            leadingIcon = R.drawable.baseline_keyboard_24,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            value = name,
            onValueChanged = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer( modifier = Modifier.weight(1f) )
        CancelAndConfirmButtons(
            onConfirmClick = { onConfirmClick(name, imageSource) },
            onCancelClick = onCancelClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EditNumberField(@StringRes label: Int, @DrawableRes leadingIcon: Int, keyboardOptions: KeyboardOptions, value: String, onValueChanged: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        singleLine = true,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}






@Preview(showBackground = true)
@Composable
fun NewPlayerScreenPreview(){
    NewPlayerScreen(
        onConfirmClick = { _, _ -> },
        onCancelClick = {  }
    )
}
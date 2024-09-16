package com.example.lupusinfabulav1

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NewPlayerScreen(
    onConfirmClick: (String, Int) -> Unit,
    onImageClick: () -> Int,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
    onRandomImageClick: () -> Unit = {},
){
    var name by remember { mutableStateOf("") }
    var imageRes by remember { mutableIntStateOf(R.drawable.android_superhero1) }

    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Spacer( modifier = Modifier.weight(1f) )
        Box(
            modifier = Modifier.fillMaxWidth().weight(4f)
        ){
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { imageRes = onImageClick( ) }
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onRandomImageClick() }
            )
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
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Spacer( modifier = Modifier.weight(1f) )
        Row(
            modifier = Modifier.weight(1f)
        ){
            Button(
                onClick = { onCancelClick() },
                modifier = Modifier.weight(2f)
            ) {
                Text( text = stringResource(id = R.string.cancel_button) )
            }
            Spacer( modifier = Modifier.weight(1f) )
            Button(
                onClick = { onConfirmClick(name, imageRes) }, //Check if the name is available, if yes return to HOME_PAGE, if not prompt a message to change the name
                modifier = Modifier.weight(2f)
            ) {
                Text( text = stringResource(id = R.string.confirm_button) )
            }
        }

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
        onImageClick = { R.drawable.android_superhero1 },
        onRandomImageClick = {}
    )
}
package com.example.lupusinfabulav1.ui.commonui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.lupusinfabulav1.R

@Composable
fun CancelAndConfirmButtons(
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier : Modifier = Modifier
){
    Row(
        modifier = modifier
    ){
        ElevatedButton(
            onClick = { onCancelClick() },
            modifier = Modifier.weight(2f)
        ) {
            Text( text = stringResource(id = R.string.cancel_button) )
        }
        Spacer( modifier = Modifier.weight(1f) )
        Button(
            onClick = { onConfirmClick() },
            modifier = Modifier.weight(2f)
        ) {
            Text( text = stringResource(id = R.string.confirm_button) )
        }
    }

}
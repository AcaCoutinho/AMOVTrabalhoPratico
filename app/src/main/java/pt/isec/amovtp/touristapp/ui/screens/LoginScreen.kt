package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@Composable
fun LoginScreen(
    navController: NavHostController?,
    firebaseViewModel: FirebaseViewModel,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { firebaseViewModel.error }
    val authUser by remember { firebaseViewModel.authUser }
    var isFormValid by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = authUser) {
        if(authUser != null && error == null)
            onSuccess()
    }

    fun validateForm (){
        isFormValid = email.isNotBlank() && password.isNotBlank()
    }

    Box (
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        if(isError || error != null){
            ErrorAlertDialog {
                isError = false
                error = null
            }
        }

        Text(
            text = stringResource(R.string.msgWB),
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(24.dp, 54.dp)
        )

        Column (
            modifier = Modifier.align(Alignment.Center)
        ){
            OutlinedTextField(
                value = email,
                onValueChange ={
                    email = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = { Text(text = stringResource(R.string.msgUsername),) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange ={
                    password = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                visualTransformation = PasswordVisualTransformation(Char(42)),
                label = { Text(text = stringResource(R.string.msgPassword),) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!isFormValid) {
                        isError = true
                    } else {
                        firebaseViewModel.signInWithEmail(email, password)
                        if (firebaseViewModel.authUser.value != null) {
                            navController?.navigate(Screens.MENU.route)
                        }
                    }
                },
                colors = buttonColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.btnSignIn))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "If you don't have an account, click the button bellow",
                textAlign = TextAlign.Center,
                softWrap = true,
                //modifier = Modifier.weight(3f, true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    navController?.navigate(Screens.REGISTER.route)
                },
                colors = buttonColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Register Now")
            }
        }
    }
}


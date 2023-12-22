package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import pt.isec.amovtp.touristapp.data.User
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@Composable
fun RegisterScreen(navController: NavHostController, firebaseViewModel: FirebaseViewModel) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var error by remember { firebaseViewModel.error }
    var isFormValid by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validateForm () {
        isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && email.isNotBlank()
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isError || error != null) {
            ErrorAlertDialog {
                isError = false
                error = null
            }
        }
        
        Text(
            text = "Create Account",
            fontSize = 26.sp,
            modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 64.dp)
        )

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "First and Last Name",
                textAlign = TextAlign.Start
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange ={
                        firstName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = "First Name") },
                    modifier = Modifier.weight(1f, false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange ={
                        lastName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = "Last Name") },
                    modifier = Modifier.weight(1f, false)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Email",
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange ={
                email = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Password",
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions{
                focusManager.clearFocus()
            },
            visualTransformation = PasswordVisualTransformation(Char(42)),
            label = { Text(text = stringResource(R.string.msgPassword),) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    val user = User(email, firstName, lastName)
                    firebaseViewModel.createUserWithEmail(user, password)
                    if(firebaseViewModel.error.value == null)
                        navController.navigate(Screens.LOGIN.route)
                }
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = "Register")
        }
    }
}
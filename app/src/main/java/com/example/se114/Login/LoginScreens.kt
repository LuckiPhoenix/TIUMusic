package com.example.se114.Login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.se114.R

@Composable
fun LoginScreen( navController: NavController) {
    reusableInputField(
        header = stringResource(id = R.string.welcome_header),
        description = stringResource(id = R.string.welcome_description),
        input2 = stringResource(id = R.string.email_label),
        input3 = stringResource(id = R.string.password_label),
        buttonText = stringResource(id = R.string.sign_in_button),
        onButtonClick = { authInputs -> if(AuthViewModel().onLogin(authInputs)) navController.navigate("home") },
        onTextClick = { navController.navigate("register") },
        modifier = Modifier
    )
}
@Composable
fun RegisterScreen(navController: NavController) {
    reusableInputField(
        header = stringResource(id = R.string.get_started_header),
        description = stringResource(id = R.string.get_started_description),
        input1 = stringResource(id = R.string.full_name_label),
        input2 = stringResource(id = R.string.email_label),
        input3 = stringResource(id = R.string.password_label),
        buttonText = stringResource(id = R.string.sign_up_button),
        onButtonClick = { authInputs -> if(AuthViewModel().onRegister(authInputs)) navController.navigate("home") },
        onTextClick = { navController.navigate("login") },
        modifier = Modifier
    )
}

@Composable
fun ResetPasswordScreen(navController: NavController) {
    reusableInputField(
        header = stringResource(id = R.string.reset_password_header),
        description = stringResource(id = R.string.reset_password_description),
        input3 = stringResource(id = R.string.new_password_label),
        buttonText = stringResource(id = R.string.reset_button),
        onButtonClick = { authInputs -> if(AuthViewModel().onReset(authInputs.password)) navController.navigate("recover") },
        modifier = Modifier
    )
}

@Composable
fun RecoverPasswordScreen(navController: NavController) {
    reusableInputField(
        header = stringResource(id = R.string.forgot_password_header),
        description = stringResource(id = R.string.forgot_password_description),
        input2 = stringResource(id = R.string.email_label),
        buttonText = stringResource(id = R.string.recover_button),
        onButtonClick = { authInputs -> if(AuthViewModel().onRecover(authInputs.email)) navController.navigate("home") },
        modifier = Modifier
    )
}


@Composable
fun reusableInputField(
    header: String,
    description: String,
    input1: String = "", //full name
    input2: String = "", //email
    input3: String = "", //password
    buttonText: String,
    onButtonClick: (AuthInputs) -> Unit,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val myButtonColor = Color(0xFF001E2F)

    // State to hold user inputs
    var inputValues by remember { mutableStateOf(AuthInputs()) }

    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = header,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(64.dp))

            // Dynamic Reusable Text Fields based on Screens
            if (input1.isNotEmpty()) {
                MyTextField(
                    title = input1,
                    value = inputValues.email,
                    onValueChange = { inputValues = inputValues.copy(fullName = it) }
                )
            }
            if (input2.isNotEmpty()) {
                MyTextField(
                    title = input2,
                    value = inputValues.password,
                    onValueChange = { inputValues = inputValues.copy(email = it) }
                )
            }
            if (input3.isNotEmpty()) {
                MyTextField(
                    title = input3,
                    value = inputValues.fullName,
                    onValueChange = { inputValues = inputValues.copy(password = it) },
                    hidden = true
                )
            }

            Button(
                onClick = { onButtonClick(inputValues) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = myButtonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(buttonText)
            }

        }
        if(onTextClick != null){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 64.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text("Don't have an account? ")
                ClickableText(
                    text = AnnotatedString("Create one"),
                    onClick = { onTextClick() },
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
        }
    }
}

@Composable
fun MyTextField(
    title: String,
    value: String = "",
    onValueChange: (String) -> Unit,
    hidden: Boolean = false,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
        ) {
            //TODO: make text align StartCenter of field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(32.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.border(border = BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(4.dp))) {
                        innerTextField()
                    }
                }
            )
            //used for password visibility
            //TODO: make password ****
            if (hidden) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.password_hidden_button),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                )
            }
        }
    }
}
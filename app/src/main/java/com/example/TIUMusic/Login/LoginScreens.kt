package com.example.TIUMusic.Login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.TIUMusic.R
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val authStatus by userViewModel.userAuthStatus.observeAsState()
    var shouldShake by remember { mutableStateOf(false) }

    LaunchedEffect(authStatus) {
        when (authStatus) {
            is Result.Success -> navController.navigate("home"){
                popUpTo("login") { inclusive = true }
            }

            is Result.Error -> {
                shouldShake = true
                delay(300)
                shouldShake = false
            }
            else -> {} //auth nhanh lắm nên khỏi loading indicator
        }
    }

    reusableInputField(
        header = stringResource(id = R.string.welcome_header),
        description = stringResource(id = R.string.welcome_description),
        input2 = stringResource(id = R.string.email_label),
        input3 = stringResource(id = R.string.password_label),
        buttonText = stringResource(id = R.string.sign_in_button),
        onButtonClick = { user ->
            userViewModel.authenticate(user.email, user.password)
        },
        onTextClick = { navController.navigate("register") },
        onForgot = { navController.navigate("reset") },
        modifier = Modifier.background(BackgroundColor),
        authStatus = authStatus,
        shouldShake = shouldShake
    )
}

@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val registerStatus by userViewModel.userAuthStatus.observeAsState()
    var shouldShake by remember { mutableStateOf(false) }

    LaunchedEffect(registerStatus) {
        when (registerStatus) {
            is Result.Success -> navController.navigate("login")
            is Result.Error -> {
                shouldShake = true
                delay(300)
                shouldShake = false
            }
            else -> {}
        }
    }

    reusableInputField(
        header = stringResource(id = R.string.get_started_header),
        description = stringResource(R.string.get_started_description),
        input1 = stringResource(id = R.string.full_name_label),
        input2 = stringResource(id = R.string.email_label),
        input3 = stringResource(id = R.string.password_label),
        buttonText = stringResource(id = R.string.sign_up_button),
        onButtonClick = { user ->
            userViewModel.insertUser(user)
        },
        onTextClick = { navController.navigate("login") },
        modifier = Modifier.background(BackgroundColor),
        authStatus = registerStatus,
        shouldShake = shouldShake
    )
}

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val resetStatus by userViewModel.resetPasswordStatus.observeAsState()
    var shouldShake by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(resetStatus) {
        when (resetStatus) {
            is Result.Success -> {
                navController.navigate("recover/$email")
            }
            is Result.Error -> {
                shouldShake = true
                delay(300)
                shouldShake = false
            }
            else -> {}
        }
    }

    reusableInputField(
        header = stringResource(id = R.string.forgot_password_header),
        description = stringResource(id = R.string.forgot_password_description),
        input2 = stringResource(id = R.string.email_label),
        buttonText = stringResource(id = R.string.recover_button),
        onButtonClick = { user ->
            email = user.email
            userViewModel.checkEmailExists(user.email)
        },
        modifier = Modifier.background(BackgroundColor),
        authStatusForReset = resetStatus,
        shouldShake = shouldShake
    )
}

@Composable
fun RecoverPasswordScreen(
    navController: NavController,
    email: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val resetStatus by userViewModel.resetPasswordStatus.observeAsState()
    var shouldShake by remember { mutableStateOf(false) }

    LaunchedEffect(resetStatus) {
        when (resetStatus) {
            is Result.Success -> {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is Result.Error -> {
                shouldShake = true
                delay(300)
                shouldShake = false
            }
            else -> {}
        }
    }

    reusableInputField(
        header = stringResource(id = R.string.reset_password_header),
        description = stringResource(id = R.string.reset_password_description),
        input3 = stringResource(id = R.string.new_password_label),
        buttonText = stringResource(id = R.string.reset_button),
        onButtonClick = { user ->
            userViewModel.updatePassword(email, user.password)
        },
        modifier = Modifier.background(BackgroundColor),
        authStatusForReset = resetStatus,
        shouldShake = shouldShake
    )
}

@Composable
fun reusableInputField(
    header: String,
    description: String,
    input1: String = "", // Username
    input2: String = "", // Email
    input3: String = "", // Password
    buttonText: String, // Button
    onButtonClick: (User) -> Unit,
    onTextClick: (() -> Unit)? = null, //Create Account
    onForgot: (() -> Unit)? = null, // Forgot Password
    modifier: Modifier = Modifier,
    authStatus: Result<User?>? = null, // Authentication status
    authStatusForReset: Result<Boolean>? = null, //workaround AuthStatus cho Reset la Boolean
    shouldShake: Boolean = false // Shake animation
) {
    val myButtonColor = PrimaryColor
    var inputValues by remember { mutableStateOf(User("", "", "")) }

    // Clear Fields when auth fail
    LaunchedEffect(authStatus) {
        if (authStatus is Result.Error) {
            inputValues = User("", "", "")
        }
    }

    // Shake animation
    val offsetX by animateFloatAsState(
        targetValue = if (shouldShake) 10f else 0f,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 500f
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = header,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 60.sp,
                modifier = Modifier.padding(start = 16.dp)
            )

            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Input fields
            if (input1.isNotEmpty()) {
                MyTextField(
                    title = input1,
                    value = inputValues.fullName,
                    onValueChange = { inputValues = inputValues.copy(fullName = it) }
                )
            }

            if (input2.isNotEmpty()) {
                MyTextField(
                    title = input2,
                    value = inputValues.email,
                    onValueChange = { inputValues = inputValues.copy(email = it) }
                )
            }

            if (input3.isNotEmpty()) {
                MyTextField(
                    title = input3,
                    value = inputValues.password,
                    onValueChange = { inputValues = inputValues.copy(password = it) },
                    hidden = true
                )
            }

            if (onForgot != null) {
                Text(
                    text = "Forgot Password?",
                    textAlign = TextAlign.End,
                    color = PrimaryColor,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.End)
                        .clickable { onForgot() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                    .offset(x = offsetX.dp)
            ) {
                Text(buttonText)
            }

            // Error message
            if (authStatus is Result.Error || authStatusForReset is Result.Error) {
                val errorMessage = when {
                    authStatus is Result.Error -> (authStatus).getErrorMessage()
                    authStatusForReset is Result.Error -> (authStatusForReset).getErrorMessage()
                    else -> "ERROR: You should never receive this message???" // khong bao gio xay ra
                }

                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

        }

        if(onTextClick != null) {
            var whatToSay = "Don't have an account? "
            var whatToSay2 = "Create one!"
            if (input1 != "") {
                whatToSay = "Had an account already? "
                whatToSay2 = "Sign in now!"
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 64.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(whatToSay, color = Color.White)
                Text(
                    text = whatToSay2,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryColor
                    ),
                    modifier = Modifier.clickable { onTextClick() }
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
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.LightGray, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color.LightGray),
                    singleLine = true,
                    visualTransformation = if (hidden && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 0.dp)
                        .background(Color.Transparent)
                )

                if (hidden) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = if (passwordVisible) R.drawable.password_visible_button else R.drawable.password_hidden_button),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .clickable {
                                passwordVisible = !passwordVisible
                            }
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun preview(){
    reusableInputField(
        header = stringResource(id = R.string.get_started_header),
        description = stringResource(R.string.get_started_description),
        input2 = stringResource(id = R.string.email_label),
        input3 = stringResource(id = R.string.password_label),
        buttonText = stringResource(id = R.string.sign_up_button),
        onButtonClick = {
        },
        onTextClick = {  },
        modifier = Modifier.background(BackgroundColor)
    )
}

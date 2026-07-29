package com.jayr.firecrud.ui.screens.authentication.login


@Composable
fun LoginScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
){
    val emailInput = rememberTextFieldState("")
    val passwordInput = rememberTextFieldState("")
    val uiState = authViewModel.uiState.collectAsState()
    val respMessage = authViewModel.responseMessage.collectAsState()
    val context =LocalContext.current
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally
    ){
        TextField(
            state = emailInput,
            label = { Text("EMAIL") }
        )
        TextField(
            state = passwordInput,
            label = { Text("PASSWORD") }
        )

        Text("==>> ${respMessage.value} <<==")
        Text("==>> ${uiState.value} <<==")

        if(uiState.value == AuthUiState.isLoading){
            CircularProgressIndicator()
//            Toast.makeText(context,respMessage.value, Toast.LENGTH_LONG).show()
//            ToastMessage(message=respMessage.value )


        }else{
            Button(
                onClick = {
                    authViewModel.login(email = emailInput.text.toString(), password = passwordInput.text.toString() )

                }
            ) {
                Text("Login")
            }
            if (uiState.value == AuthUiState.isSuccess){
                ToastMessage(message=respMessage.value )
            }else{
                ToastMessage(message=respMessage.value )
            }        }

    }
}

@Composable
fun ToastMessage(message:String ){
    val context = LocalContext.current
    Toast.makeText(context,message, Toast.LENGTH_LONG).show()
}
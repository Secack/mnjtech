package su.akari.mnjtech.ui.screen.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import su.akari.mnjtech.PR
import su.akari.mnjtech.R
import su.akari.mnjtech.ui.component.LabelledCheckBox
import su.akari.mnjtech.ui.component.LabelledRadioButton
import su.akari.mnjtech.ui.component.Md3TopBar
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.ui.screen.online.index.exceedDeviceLimit
import su.akari.mnjtech.util.*

@SuppressLint("ComposableNaming")
@Composable
fun <T> initJwglState(
    vararg children: DataState<T>, initChildren: (Unit) -> Unit
) {
    val activity = LocalActivity.current
    initLoginState(
        loginFlow = activity.viewModel.loginJwglFlow,
        initLogin = {
            activity.viewModel.loginJwgl()
        },
        initError = {
            //context.toast(it)
        },
        children = children,
        initChildren = initChildren
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> DataState<T>.awaitJwglLogin(onSuccess: @Composable (T) -> Unit) {
    val mainViewModel = LocalActivity.current.viewModel
    val state by mainViewModel.loginJwglFlow.collectAsState()
    state.handlerWithLoadingAnim(
        errorAction = {
            mainViewModel.loginJwgl()
        }
    ) {
        handlerWithLoadingAnim(
            errorAction = {
                mainViewModel.loginJwgl()
            },
            onSuccess = onSuccess
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> initLoginState(
    loginFlow: MutableStateFlow<DataState<T>>,
    initLogin: () -> Unit,
    initError: (String) -> Unit,
    vararg children: DataState<*>,
    initChildren: (T) -> Unit,
) {
    val state by loginFlow.collectAsState()
    LaunchedEffect(state) {
        if (state is DataState.Empty) {
            initLogin()
        }
    }
    bindDataState(state, *children, initChildren = initChildren, parentError = initError)
}

@Composable
fun LoginScreen() {
    val navController = LocalNavController.current
    val viewModel by viewModel<LoginViewModel> {
        parametersOf(navController.currentBackStackEntry!!.arguments!!)
    }
    val activity = LocalActivity.current
    val mainViewModel = activity.viewModel
    var username by rememberState(PR.username.getBlocking())
    var password by rememberState(PR.password.getBlocking())
    val provider by PR.provider.collectAsState()
    val saveSession by PR.saveSession.collectAsState()
    val autoLogin by PR.autoLogin.collectAsState()

    val login by viewModel.loginFlow.collectAsState()
    var loginJob: Job? by rememberState(null)
    var errorDialog by rememberState(false)

    fun login() {
        loginJob = viewModel.login(username = username, password = password, provider = provider) {
            it?.takeIf { it.contains("超限") }?.let { exceedDeviceLimit = it }
            activity.toast(it ?: "登录成功")
        }
    }

    login.onLoading {
        AlertDialog(
            title = {
                ResourceText(R.string.logging_in)
            },
            text = {
                ResourceText(R.string.wait_for_a_while)
            },
            icon = {
                CircularProgressIndicator(Modifier.size(30.dp))
            },
            onDismissRequest = {
                loginJob?.cancel()
                viewModel.loginFlow.value = DataState.Empty
            },
            confirmButton = {}
        )
    }

    login.observeState(
        onSuccess = {
            activity.viewModel.prepareUserData()
            navController.navigate(Destinations.Index) {
                popUpTo(0)
            }
        },
        onError = {
            errorDialog = true
        }
    )

    LaunchedEffect(Unit) {
        if (mainViewModel.userDataFetched && username.isNotEmpty() && password.isNotEmpty()
            && (viewModel.autoLogin == -1 && autoLogin) or (viewModel.autoLogin == 1)
        ) {
            login()
        }
    }

    if (errorDialog && login is DataState.Error) {
        AlertDialog(
            title = {
                ResourceText(R.string.login_failed)
            },
            text = {
                Text((login as DataState.Error).msg)
            },
            onDismissRequest = {
                errorDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    errorDialog = false
                }) {
                    ResourceText(R.string.ok)
                }
            }
        )
    }
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    ResourceText(R.string.login)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(padding)
                .navigationBarsPadding()
                .imePadding(), contentAlignment = Alignment.Center
        ) {
            var showPwd by rememberState(false)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val focusManager = LocalFocusManager.current

                val focusRequester = remember {
                    FocusRequester()
                }

                LaunchedEffect(Unit) {
                    if (username.isEmpty()) focusRequester.requestFocus()
                }

                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = R.drawable.banner,
                    contentDescription = null
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = username,
                    onValueChange = {
                        username = it
                        password = ""
                    },
                    label = {
                        ResourceText(R.string.username)
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBox, contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        ResourceText(R.string.password)
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock, contentDescription = null
                        )
                    },
                    visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Crossfade(targetState = showPwd) { state ->
                            IconButton(onClick = { showPwd = !showPwd }) {
                                Icon(
                                    imageVector = if (state) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = {
                        login()
                    })
                )

                Column {
                    AnimatedVisibility(visible = saveSession.not()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("校园内网", "中国移动", "中国电信").forEachIndexed { it, name ->
                                LabelledRadioButton(
                                    selected = it == provider,
                                    onClick = {
                                        PR.provider.setBlocking(it)
                                    },
                                    label = name
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LabelledCheckBox(
                            checked = saveSession,
                            onCheckedChange = {
                                if (it) activity.toast("下次进入将不会同时认证校园网")
                                PR.saveSession.setBlocking(it)
                            },
                            label = "保持登录状态"
                        )

                        LabelledCheckBox(
                            checked = autoLogin,
                            onCheckedChange = {
                                PR.autoLogin.setBlocking(it)
                            },
                            label = "自动登录"
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = ::login,
                ) {
                    ResourceText(R.string.login)
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(Destinations.LoginWeb)
                    },
                ) {
                    Text(text = "网页登录")
                }

                Text(
                    text = "密码仅保存在本地，不会被上传到任何第三方服务器",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

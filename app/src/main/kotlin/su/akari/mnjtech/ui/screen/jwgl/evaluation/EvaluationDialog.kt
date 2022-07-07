package su.akari.mnjtech.ui.screen.jwgl.evaluation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.R
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.screen.login.initJwglState
import su.akari.mnjtech.util.ResourceText
import su.akari.mnjtech.util.observeState
import su.akari.mnjtech.util.rememberState
import su.akari.mnjtech.util.toast

@Composable
fun EvaluationDialog() {
    val viewModel by viewModel<EvaluationViewModel>()
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val evaluationList by viewModel.evaluationListFlow.collectAsState()
    var loadingDialog by rememberState(true)

    initJwglState(evaluationList) {
        viewModel.getEvaluationList()
    }

    evaluationList.observeState(
        onSuccess = {
            loadingDialog = false
        }
    )

    if (loadingDialog) {
        AlertDialog(
            title = {
                Text(text = "教学评价")
            },
            text = {
                ResourceText(R.string.wait_for_a_while)
            },
            icon = {
                CircularProgressIndicator(Modifier.size(30.dp))
            },
            onDismissRequest = {
                navController.popBackStack()
            },
            confirmButton = {}
        )
    } else {
        val quitDialog = { text: String ->
            context.toast(text)
            navController.popBackStack()
        }
        val list = evaluationList.read().toList()
        if (list.isEmpty()) {
            quitDialog("未到评教时间或评教均已完成")
        } else {
            AlertDialog(
                title = {
                    Text(text = "教学评价")
                },
                text = {
                    Text(
                        text = buildAnnotatedString {
                            append("即将对《${list[0].first}》等${list.size}门课")
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("满分")
                            }
                            append("评教，继续？")
                        }
                    )
                },
                onDismissRequest = {
                    navController.popBackStack()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                loadingDialog = true
                                list.forEach {
                                    it.second.invoke()
                                }
                                quitDialog("已完成所有评教")
                            }
                        }
                    ) {
                        ResourceText(R.string.yes)
                    }
                }
            )
        }
    }
}







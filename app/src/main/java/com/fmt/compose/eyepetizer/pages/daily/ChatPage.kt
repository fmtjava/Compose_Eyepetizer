@file:OptIn(ExperimentalLayoutApi::class)

package com.fmt.compose.eyepetizer.pages.daily

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.model.ChatMessage
import com.fmt.compose.eyepetizer.model.ChatRole
import com.fmt.compose.eyepetizer.pages.daily.viewmodel.ChatViewModel
import com.mikepenz.markdown.m2.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState

/*
                      Jetpack Compose
                        │
                        │ sendMessage()
                        ▼
                  ChatViewModel
                        │
                        │ List<BailianMessage>
                        ▼
                  ChatRepository
                        │
                        ▼
                     OkHttp
                        │
                        │ HTTPS
                        ▼
              ┌──────────────────┐
              │   阿里云百炼      │
              │                  │
              │ qwen3.8-max      │
              │ stream = true    │
              └────────┬─────────┘
                       │
                       │ SSE
                       ▼
                data: {...}
                       │
                       ▼
             delta.content
                       │
                       ▼
                 Flow<String>
                       │
                       ▼
                 ChatViewModel
                       │
                       │ append
                       ▼
                  StateFlow
                       │
                       ▼
               Compose 重组
                       │
                       ▼
                AI 文字逐字出现
 */
/**
 * 聊天页面的状态入口。
 *
 * [ChatViewModel.uiState] 是唯一的消息数据源；输入框文本仅属于当前组合，发送后由
 * ViewModel 负责将其转换为会话消息并驱动流式回复。
 */
@Composable
fun ChatPage(viewModel: ChatViewModel = viewModel()) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val materialTypography = MaterialTheme.typography
    val assistantMarkdownTypography = remember(materialTypography) {
        compactChatMarkdownTypography(materialTypography)
    }

    var input by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    // 仅跟踪用户手势拖拽，避免流式消息自动滚动时误收起键盘。
    val isDraggingMessages by listState.interactionSource.collectIsDraggedAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    // 新增用户消息或助手占位消息时，将视口移动到会话末尾，保证流式回复可见。
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                state.messages.lastIndex
            )
        }
    }

    LaunchedEffect(isDraggingMessages) {
        if (isDraggingMessages) {
            // 用户开始浏览历史消息时，释放输入焦点并关闭软键盘。
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        ChatTopBar(onNewChat = {
            // 当前什么都没有，不需要处理
            if (state.messages.isEmpty() && input.isBlank()) {
                return@ChatTopBar
            }
            // 1. 清空输入框
            input = ""

            // 2. 清除输入框焦点
            focusManager.clearFocus()

            // 3. 收起键盘
            keyboardController?.hide()

            // 4. 创建新会话
            viewModel.newChat()

            // 5. 给用户反馈
            Toast.makeText(context, R.string.new_chat_has_done, Toast.LENGTH_SHORT).show()
        })
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                },
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = state.messages,
                key = { it.id },
                // 按消息角色复用列表项，避免滚动时在不同消息布局间反复重建节点。
                contentType = { it.role }
            ) { message ->
                ChatMessageItem(
                    message = message,
                    assistantMarkdownTypography = assistantMarkdownTypography,
                )
            }
        }

        state.error?.let {
            Text(
                text = it, color = MaterialTheme.colors.error, modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }

        ChatInput(value = input, generating = state.isGenerating, onValueChange = {
            input = it
        }, onSend = {
            val text = input
            // 1. 清空输入框
            input = ""
            // 2. 清除焦点
            focusManager.clearFocus()
            // 3. 收起软键盘
            keyboardController?.hide()
            // 4. 发送
            viewModel.sendMessage(text)
        }, onStop = {
            viewModel.stopGenerating()
        })
    }
}

@Composable
private fun ChatTopBar(onNewChat: () -> Unit) {
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.ai_assist),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = {
                if (context is Activity) {
                    context.finish()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onNewChat) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.new_chat)
                )
            }
        })
}

/**
 * 底部输入栏。
 *
 * 生成期间将发送按钮切换为停止按钮，避免同一会话并发发起多个流式请求。
 */
@Composable
private fun ChatInput(
    value: String,
    generating: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text("问点什么...")
            },
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (value.isNotBlank() && !generating) {
                    onSend()
                }
            })
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (generating) {
            IconButton(onClick = onStop) {
                Icon(
                    imageVector = Icons.Default.Stop, contentDescription = "停止生成"
                )
            }

        } else {
            IconButton(
                enabled = value.isNotBlank(), onClick = onSend
            ) {
                Icon(
                    imageVector = Icons.Filled.Send, contentDescription = "发送"
                )
            }
        }
    }
}

/**
 * 根据消息角色绘制不同方向的气泡；助手消息使用 Markdown，以保留模型输出的格式。
 */
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    assistantMarkdownTypography: MarkdownTypography,
) {
    val isUser = message.role == ChatRole.USER

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (isUser) {
            Surface(
                modifier = Modifier.widthIn(max = 320.dp), shape = RoundedCornerShape(
                    topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 4.dp
                ), color = MaterialTheme.colors.primary, elevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(
                        horizontal = 16.dp, vertical = 12.dp
                    ),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body1
                )
            }
        } else {
            // 助手尚未产生首个文本块时，用“思考中”反馈请求仍在进行。
            if (message.content.isEmpty() && message.isStreaming) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp), strokeWidth = 2.dp
                    )
                    Text(
                        stringResource(R.string.thinking),
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                val markdownState = rememberMarkdownState(
                    content = message.content,
                    retainState = true,
                )
                Surface(
                    modifier = Modifier.widthIn(max = 320.dp),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 12.dp,
                    ),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Markdown(
                            markdownState = markdownState,
                            typography = assistantMarkdownTypography,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

/** 为聊天气泡创建紧凑的 Markdown 排版；由 [ChatPage] 缓存并供全部消息共用。 */
private fun compactChatMarkdownTypography(
    typography: androidx.compose.material.Typography,
): MarkdownTypography {
    val body = typography.body1.copy(fontSize = 16.sp, lineHeight = 24.sp)
    return DefaultMarkdownTypography(
        h1 = typography.h6.copy(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
        h2 = typography.subtitle1.copy(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
        h3 = typography.subtitle1.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
        h4 = typography.body1.copy(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold),
        h5 = typography.body1.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold),
        h6 = typography.body1.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold),
        text = body,
        code = typography.body2.copy(fontFamily = FontFamily.Monospace),
        inlineCode = body.copy(fontFamily = FontFamily.Monospace),
        quote = typography.body2.copy(fontStyle = FontStyle.Italic),
        paragraph = body,
        ordered = body,
        bullet = body,
        list = body,
        textLink = TextLinkStyles(
            style = body.toSpanStyle().copy(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            ),
        ),
        table = body,
    )
}

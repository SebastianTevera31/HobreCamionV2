package com.rfz.appflotal.presentation.ui.forums.navigation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.data.model.forum.toComment
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.forums.components.ForumErrorView
import com.rfz.appflotal.presentation.ui.forums.components.ForumShimmerList
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.BottomCommentField
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumModuleScaffold
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumSearchConfig
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumTopBarConfig
import com.rfz.appflotal.presentation.ui.forums.screen.RoomsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.post.NewTopicScreen
import com.rfz.appflotal.presentation.ui.forums.screen.post.TopicsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.savedcomments.SavedCommentsRoute
import com.rfz.appflotal.presentation.ui.forums.screen.topic.DiscussionScreen
import com.rfz.appflotal.presentation.ui.forums.screen.topic.ReplyScreen
import com.rfz.appflotal.presentation.ui.forums.screen.topic.ReportScreen
import com.rfz.appflotal.presentation.ui.forums.viewmodel.CameraUiState
import com.rfz.appflotal.presentation.ui.forums.viewmodel.ForumScreenType
import com.rfz.appflotal.presentation.ui.forums.viewmodel.ForumViewModel
import com.rfz.appflotal.presentation.ui.utils.LoadState
import kotlinx.serialization.Serializable

@Serializable
object ForumsGraph

@Serializable
object ForumRooms

@Serializable
data class RoomTopics(
    val roomId: String,
    val roomTitle: String,
)

@Serializable
data class TopicDiscussion(
    val roomId: String,
    val topicId: String,
    val topicTitle: String,
    val selectedComment: String = "0"
)

@Serializable
data class ReportContent(
    val id: Int,
    val isTopic: Boolean
)

@Serializable
data class NewTopicNav(
    val roomId: String,
    val roomTitle: String
)

@Serializable
data class NewCommentNav(
    val commentId: Int
)

@Serializable
object SavedCommentsNav

fun NavGraphBuilder.forumsGraph(
    navController: NavHostController
) {
    navigation<ForumsGraph>(
        startDestination = ForumRooms
    ) {
        composable<ForumRooms> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.clearFilterSearch()
                viewModel.getInitialData()
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_title),
                    subtitle = stringResource(R.string.forum_subtitle),
                    showBackButton = true,
                    showMenuButton = true,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.forum_search_placeholder),
                        onValueChange = {
                            viewModel.onSearchChanged(it, ForumScreenType.ROOM)
                        }
                    ),
                    onBackClick = {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMenuClick = {
                        navController.navigate(SavedCommentsNav)
                    }
                )
            ) { paddingValues ->
                when (state.roomState) {
                    is LoadState.Loading -> {
                        ForumShimmerList(modifier = Modifier.padding(paddingValues))
                    }

                    is LoadState.Success -> {
                        RoomsScreen(
                            rooms = state.filteredRooms,
                            loadState = state.screenState,
                            onNavigate = { room ->
                                navController.navigate(
                                    RoomTopics(
                                        roomId = room.id.toString(),
                                        roomTitle = room.title
                                    )
                                )
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.getInitialData() },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<RoomTopics> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }

            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<RoomTopics>()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(args.roomId) {
                viewModel.clearFilterSearch()
                viewModel.loadTopicsByRoom(args.roomId)
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.roomTitle,
                    subtitle = stringResource(R.string.forum_room_topics_subtitle),
                    showBackButton = true,
                    showMenuButton = true,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.forum_search_topics_placeholder),
                        onValueChange = {
                            viewModel.onSearchChanged(it, ForumScreenType.TOPIC)
                        }
                    ),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onMenuClick = {
                        navController.navigate(SavedCommentsNav)
                    }
                ),
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        navController.navigate(
                            NewTopicNav(
                                roomId = args.roomId,
                                roomTitle = args.roomTitle
                            )
                        )
                    }) {
                        Row(
                            modifier = Modifier.padding(Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.forum_add_topic_desc),
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(R.string.forum_new_topic_button),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                when (state.screenState) {
                    is LoadState.Loading -> {
                        ForumShimmerList(modifier = Modifier.padding(paddingValues))
                    }

                    is LoadState.Success -> {
                        TopicsScreen(
                            topics = state.filteredTopics,
                            loadState = state.screenState,
                            onTopicClick = { topic ->
                                navController.navigate(
                                    TopicDiscussion(
                                        roomId = args.roomId,
                                        topicId = topic.id.toString(),
                                        topicTitle = topic.title
                                    )
                                )
                            },
                            onReport = { id, type ->
                                navController.navigate(
                                    ReportContent(
                                        id = id,
                                        isTopic = !type.isComment
                                    )
                                )
                            },
                            modifier = Modifier.padding(paddingValues),
                            onSaved = {
                                viewModel.doLike(
                                    id = it,
                                    isComment = false,
                                    fromPostsView = true
                                )
                            }
                        )
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.loadTopicsByRoom(args.roomId) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<TopicDiscussion> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<TopicDiscussion>()
            val state by viewModel.uiState.collectAsState()
            val context = LocalContext.current
            var commentText by remember { mutableStateOf("") }
            val errorMessage = stringResource(R.string.forum_capture_cancelled)

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    viewModel.onPhotoCaptured()
                } else {
                    viewModel.onPhotoError(errorMessage)
                }
            }

            LaunchedEffect(args.topicId) {
                viewModel.loadTopicMessages(args.topicId.toInt())
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.topicTitle,
                    subtitle = state.selectedTopic?.let {
                        stringResource(R.string.forum_responses_format, it.title, it.numComments)
                    } ?: args.topicTitle,
                    showBackButton = true,
                    showMenuButton = false,
                    searchConfig = null,
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = {}
                ),
                bottomBar = {
                    Surface(
                        modifier = Modifier.navigationBarsPadding(),
                        tonalElevation = Dimens.PaddingExtraSmall,
                        color = Color.White
                    ) {
                        BottomCommentField(
                            comment = commentText,
                            onCommentChange = { commentText = it },
                            onSend = {
                                viewModel.sendComment(commentText)
                                commentText = ""
                            },
                            isLoading = state.sendCommentState is LoadState.Loading,
                            onCancel = viewModel::cancelPublication,
                            selectedImage = (state.photoEvidence as? CameraUiState.Captured)?.uri,
                            onAddImage = {
                                viewModel.startCamera(context) { uri ->
                                    cameraLauncher.launch(uri)
                                }
                            },
                            onRemoveImage = viewModel::clearPhoto
                        )
                    }
                }
            ) { paddingValues ->
                when (state.screenState) {
                    is LoadState.Loading -> {
                        ForumShimmerList(modifier = Modifier.padding(paddingValues))
                    }

                    is LoadState.Success -> {
                        val topic = state.selectedTopic
                        val comments = state.comments

                        if (topic != null) {
                            DiscussionScreen(
                                topic = topic,
                                comments = comments,
                                onReply = { comment ->
                                    navController.navigate(NewCommentNav(commentId = comment.id))
                                },
                                onSave = { id, isTopic -> viewModel.doLike(id, isTopic) },
                                onReport = { id, type ->
                                    navController.navigate(
                                        ReportContent(
                                            id = id,
                                            isTopic = !type.isComment
                                        )
                                    )
                                },
                                selectedComment = args.selectedComment,
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.loadTopicMessages(args.topicId.toInt()) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<ReportContent> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<ReportContent>()
            val uiState = viewModel.uiState.collectAsState()
            val context = LocalContext.current

            val message = if (args.isTopic) {
                uiState.value.selectedTopic?.takeIf { it.id == args.id }?.toComment()
                    ?: uiState.value.topics.find { it.id == args.id }?.toComment()
            } else uiState.value.comments.find { it.id == args.id }

            val forumNotFoundMsg = stringResource(R.string.forum_post_not_found)
            val reportSuccessMsg = stringResource(R.string.forum_report_success)

            LaunchedEffect(uiState.value.reportState) {
                when (val reportState = uiState.value.reportState) {
                    is LoadState.Success -> {
                        Toast.makeText(context, reportSuccessMsg, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        viewModel.resetReportState()
                    }

                    is LoadState.Error -> {
                        Toast.makeText(context, reportState.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetReportState()
                    }

                    else -> {}
                }
            }

            if (message == null) {
                Toast.makeText(context, forumNotFoundMsg, Toast.LENGTH_LONG).show()
                navController.popBackStack()
            } else {
                ForumModuleScaffold(
                    topBarConfig = ForumTopBarConfig(
                        title = stringResource(R.string.forum_report_title),
                        subtitle = stringResource(R.string.forum_report_subtitle),
                        showBackButton = true,
                        showMenuButton = false,
                        onBackClick = { navController.popBackStack() }
                    )
                ) { paddingValues ->
                    ReportScreen(
                        modifier = Modifier.padding(paddingValues),
                        onSendReport = { reportTypeId: Int, details: String ->
                            viewModel.sendReport(
                                id = args.id,
                                isTopic = args.isTopic,
                                reportTypeId = reportTypeId,
                                details = details
                            )
                        },
                        comment = message
                    )
                }
            }
        }

        composable<NewTopicNav> { backStackEntry ->
            val args = backStackEntry.toRoute<NewTopicNav>()
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.resetNewTopicState()
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_new_topic_title),
                    subtitle = args.roomTitle,
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            ) { paddingValues ->
                NewTopicScreen(
                    modifier = Modifier.padding(paddingValues),
                    newTopicStatus = state.newTopicState,
                    onSend = { title, message, tags, color ->
                        viewModel.sendTopic(
                            title = title,
                            description = message,
                            tags = tags,
                            color = color
                        )
                    },
                    onCancel = viewModel::cancelPublication,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable<NewCommentNav> { backStackEntry ->
            val args = backStackEntry.toRoute<NewCommentNav>()
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            val commentToReply = state.comments.find { it.id == args.commentId }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_reply_title),
                    subtitle = state.selectedTopic?.title ?: "",
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = { navController.popBackStack() }
                )
            ) { paddingValues ->
                commentToReply?.let { comment ->
                    ReplyScreen(
                        comment = comment,
                        modifier = Modifier.padding(paddingValues),
                        replyStatus = state.sendCommentState,
                        onSend = { message: String ->
                            viewModel.sendComment(message)
                        },
                        onCancel = viewModel::cancelPublication,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable<SavedCommentsNav> {
            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = "Gustados",
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            ) { paddingValues ->
                SavedCommentsRoute(
                    modifier = Modifier.padding(paddingValues),
                    onNavigateTo = { idTopic, idComment, title ->
                        navController.navigate(
                            TopicDiscussion(
                                roomId = "",
                                topicId = idTopic.toString(),
                                topicTitle = title,
                                selectedComment = idComment.toString()
                            )
                        )
                    }
                )
            }
        }
    }
}

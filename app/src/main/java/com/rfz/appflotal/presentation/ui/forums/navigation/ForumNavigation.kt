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
import com.rfz.appflotal.data.mapper.toComment
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.forums.components.ForumErrorView
import com.rfz.appflotal.presentation.ui.forums.components.ForumShimmerList
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.BottomCommentField
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumModuleScaffold
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumSearchConfig
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumTopBarConfig
import com.rfz.appflotal.presentation.ui.forums.screen.ForumsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.post.NewTopicScreen
import com.rfz.appflotal.presentation.ui.forums.screen.post.PostsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.topic.ReplyScreen
import com.rfz.appflotal.presentation.ui.forums.screen.topic.ReportPostScreen
import com.rfz.appflotal.presentation.ui.forums.screen.topic.TopicScreen
import com.rfz.appflotal.presentation.ui.forums.viewmodel.CameraUiState
import com.rfz.appflotal.presentation.ui.forums.viewmodel.ForumScreenType
import com.rfz.appflotal.presentation.ui.forums.viewmodel.ForumViewModel
import com.rfz.appflotal.presentation.ui.utils.LoadState
import kotlinx.serialization.Serializable

@Serializable
object ForumsGraph

@Serializable
object ForumsRooms

@Serializable
data class PostsTopics(
    val roomId: String,
    val roomTitle: String,
)

@Serializable
data class TopicDetail(
    val roomId: String,
    val topicId: String,
    val topicTitle: String
)


@Serializable
data class ReportPost(
    val idPost: Int,
    val isPost: Boolean
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

fun NavGraphBuilder.forumsGraph(
    navController: NavHostController
) {
    navigation<ForumsGraph>(
        startDestination = ForumsRooms
    ) {
        composable<ForumsRooms> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.clearFilterSearch()
                viewModel.getForums()
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_title),
                    subtitle = stringResource(R.string.forum_subtitle),
                    showBackButton = true,
                    showMenuButton = false,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.forum_search_placeholder),
                        onValueChange = {
                            viewModel.onSearchChanged(it, ForumScreenType.TOPIC)
                        }
                    ),
                    onBackClick = {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMenuClick = {}
                )
            ) { paddingValues ->
                when (state.screenState) {
                    is LoadState.Loading -> {
                        ForumShimmerList(modifier = Modifier.padding(paddingValues))
                    }

                    is LoadState.Success -> {
                        ForumsScreen(
                            foros = state.filteredForums,
                            loadState = state.screenState,
                            onNavigate = { topic ->
                                navController.navigate(
                                    PostsTopics(
                                        roomId = topic.id.toString(),
                                        roomTitle = topic.title
                                    )
                                )
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.getForums() },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<PostsTopics> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }

            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<PostsTopics>()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(args.roomId) {
                viewModel.clearFilterSearch()
                viewModel.loadPostsByRoom(args.roomId)
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.roomTitle,
                    subtitle = stringResource(R.string.forum_room_topics_subtitle),
                    showBackButton = true,
                    showMenuButton = false,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.forum_search_topics_placeholder),
                        onValueChange = {
                            viewModel.onSearchChanged(it, ForumScreenType.POST)
                        }
                    ),
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = {}
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
                        PostsScreen(
                            posts = state.filteredPosts,
                            loadState = state.screenState,
                            onPostClick = { post ->
                                navController.navigate(
                                    TopicDetail(
                                        roomId = args.roomId,
                                        topicId = post.id.toString(),
                                        topicTitle = post.title
                                    )
                                )
                            },
                            onReport = { roomId, type ->
                                navController.navigate(
                                    ReportPost(
                                        idPost = roomId,
                                        isPost = type.isPost
                                    )
                                )
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.loadPostsByRoom(args.roomId) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<TopicDetail> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<TopicDetail>()
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
                viewModel.loadPostNComments(args.topicId.toInt())
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.topicTitle,
                    subtitle = state.selectedPost?.let {
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
                        val topic = state.selectedPost
                        val comments = state.comments

                        if (topic != null && comments.isNotEmpty()) {
                            TopicScreen(
                                topic = topic,
                                comments = comments,
                                onReply = { comment ->
                                    navController.navigate(NewCommentNav(commentId = comment.id))
                                },
                                onSave = { id, isPost -> viewModel.doLike(id, isPost) },
                                onReport = { postId, type ->
                                    navController.navigate(
                                        ReportPost(
                                            idPost = postId,
                                            isPost = type.isPost
                                        )
                                    )
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }

                    is LoadState.Error -> {
                        ForumErrorView(
                            onRetry = { viewModel.loadPostNComments(args.topicId.toInt()) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    else -> {}
                }
            }
        }

        composable<ReportPost> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ForumViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<ReportPost>()
            val uiState = viewModel.uiState.collectAsState()
            val context = LocalContext.current

            val message = if (args.isPost) {
                uiState.value.selectedPost?.takeIf { it.id == args.idPost }?.toComment()
                    ?: uiState.value.posts.find { it.id == args.idPost }?.toComment()
            } else uiState.value.comments.find { it.id == args.idPost }

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
                    ReportPostScreen(
                        modifier = Modifier.padding(paddingValues),
                        onSendReport = { reportTypeId, details ->
                            viewModel.sendReport(
                                id = args.idPost,
                                isPost = args.isPost,
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
                } catch (e: Exception) {
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
                        viewModel.sendPost(
                            title = title,
                            description = message,
                            tags = tags,
                            color = color
                        )
                    },
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

            // Buscamos el comentario al que se está respondiendo en el estado actual
            val commentToReply = state.comments.find { it.id == args.commentId }
                ?: state.selectedPost?.let {
                    // Si no está en comentarios, podría ser el post principal (que actúa como comentario inicial)
                    // Nota: En un sistema real, el post principal suele tener un ID de comentario asociado.
                    null
                }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_reply_title),
                    subtitle = state.selectedPost?.title ?: "",
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = { navController.popBackStack() }
                )
            ) { paddingValues ->
                commentToReply?.let { comment ->
                    ReplyScreen(
                        comment = comment,
                        modifier = Modifier.padding(paddingValues),
                        onOptions = {},
                        onSend = { message: String ->
                            // Aquí iría la lógica para guardar el comentario
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

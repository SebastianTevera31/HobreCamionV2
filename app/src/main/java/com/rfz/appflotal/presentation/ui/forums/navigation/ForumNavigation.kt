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
import androidx.compose.runtime.remember
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
                viewModel.getInitialData(forceRefresh = true)
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
                                        isComment = type.isComment
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

            LaunchedEffect(state.shouldNavigateToReply) {
                if (state.shouldNavigateToReply) {
                    val currentRoute = navController.currentDestination?.route
                    if (currentRoute?.contains("TopicDiscussion") == true) {
                        state.selectedTopic?.let { topic ->
                            navController.navigate(NewCommentNav(id = topic.id, isTopic = true))
                            viewModel.onNavigatedToReply()
                        }
                    } else {
                        // If we are not in TopicDiscussion, we still might want to reset the flag 
                        // so it doesn't trigger later when we go back.
                        // Or only reset if it was intended for this.
                        viewModel.onNavigatedToReply()
                    }
                }
            }

            LaunchedEffect(args.topicId) {
                viewModel.loadTopicMessages(args.topicId.toInt())
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = state.selectedTopic?.title ?: "Topic",
                    subtitle = state.selectedTopic?.let {
                        stringResource(
                            R.string.forum_responses_format,
                            it.description,
                            it.numComments
                        )
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
                            comment = state.commentText,
                            onCommentChange = viewModel::onCommentTextChanged,
                            onSend = {
                                viewModel.sendComment(state.commentText)
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
                                    viewModel.resetCommentState()
                                    navController.navigate(
                                        NewCommentNav(
                                            id = comment.id,
                                            isTopic = false
                                        )
                                    )
                                },
                                onSave = { id, isTopic -> viewModel.doLike(id, isTopic) },
                                onReport = { id, type ->
                                    navController.navigate(
                                        ReportContent(
                                            id = id,
                                            isComment = type.isComment
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

            val message = if (!args.isComment) {
                uiState.value.selectedTopic?.takeIf { it.id == args.id }?.toComment()
                    ?: uiState.value.topics.find { it.id == args.id }?.toComment()
            } else uiState.value.comments.find { it.id == args.id }

            val forumNotFoundMsg = stringResource(R.string.forum_post_not_found)
            val reportSuccessMsg = stringResource(R.string.forum_report_success)

            LaunchedEffect(message) {
                if (message == null) {
                    Toast.makeText(context, forumNotFoundMsg, Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            }

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

            if (message != null) {
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
                                isComment = args.isComment,
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
            val context = LocalContext.current
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

            LaunchedEffect(Unit) {
                viewModel.resetNewTopicState()
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_new_topic_title),
                    subtitle = args.roomTitle,
                    showBackButton = true,
                    showMenuButton = false,
                    showPublishButton = true,
                    isPublishing = state.newTopicState is LoadState.Loading,
                    onBackClick = {
                        viewModel.clearPhoto()
                        navController.popBackStack()
                    },
                    onPublishClick = viewModel::sendTopic,
                    onCancelClick = viewModel::cancelPublication
                )
            ) { paddingValues ->
                NewTopicScreen(
                    modifier = Modifier.padding(paddingValues),
                    newTopicStatus = state.newTopicState,
                    onBack = {
                        navController.popBackStack()
                    },
                    selectedImage = (state.photoEvidence as? CameraUiState.Captured)?.uri,
                    onAddImage = {
                        viewModel.startCamera(context) { uri ->
                            cameraLauncher.launch(uri)
                        }
                    },
                    onRemoveImage = viewModel::clearPhoto,
                    title = state.topicTitle,
                    onTitleChange = viewModel::onTopicTitleChanged,
                    description = state.topicDescription,
                    onDescriptionChange = viewModel::onTopicDescriptionChanged,
                    selectedColor = state.topicColor,
                    onColorChange = viewModel::onTopicColorChanged,
                    tags = state.topicTags,
                    onTagsChange = viewModel::onTopicTagsChanged
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
            val context = LocalContext.current
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

            val commentToReply = if (args.isTopic) {
                state.selectedTopic?.toComment()
            } else {
                state.comments.find { it.id == args.id }
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.forum_reply_title),
                    subtitle = state.selectedTopic?.title ?: "",
                    showBackButton = true,
                    showPublishButton = true,
                    showMenuButton = false,
                    isPublishing = state.sendCommentState is LoadState.Loading,
                    onBackClick = {
                        viewModel.clearPhoto()
                        navController.popBackStack()
                    },
                    onPublishClick = {
                        viewModel.sendComment(
                            state.commentText,
                            parentId = if (args.isTopic) null else args.id
                        )
                    },
                    onCancelClick = viewModel::cancelPublication
                )
            ) { paddingValues ->
                commentToReply?.let { comment ->
                    ReplyScreen(
                        comment = comment,
                        modifier = Modifier.padding(paddingValues),
                        replyStatus = state.sendCommentState,
                        onBack = {
                            navController.popBackStack()
                        },
                        selectedImage = (state.photoEvidence as? CameraUiState.Captured)?.uri,
                        onAddImage = {
                            viewModel.startCamera(context) { uri ->
                                cameraLauncher.launch(uri)
                            }
                        },
                        onRemoveImage = viewModel::clearPhoto,
                        message = state.commentText,
                        onMessageChange = viewModel::onCommentTextChanged
                    )
                }
            }
        }
        composable<SavedCommentsNav> {
            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.favorites),
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

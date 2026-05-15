package com.rfz.appflotal.presentation.ui.forums.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rfz.appflotal.presentation.ui.forums.components.ForumModuleScaffold
import com.rfz.appflotal.presentation.ui.forums.components.ForumSearchConfig
import com.rfz.appflotal.presentation.ui.forums.components.ForumTopBarConfig
import com.rfz.appflotal.presentation.ui.forums.screen.ForumsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.PostsScreen
import com.rfz.appflotal.presentation.ui.forums.screen.TopicScreen
import com.rfz.appflotal.presentation.ui.forums.viewmodel.ForumViewModel
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

fun NavGraphBuilder.forumsGraph(
    navController: NavHostController,
    viewModel: ForumViewModel
) {
    navigation<ForumsGraph>(
        startDestination = ForumsRooms
    ) {
        composable<ForumsRooms> {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.getForums()
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = "Foros",
                    subtitle = "Comunidad Hombre Camión",
                    showBackButton = false,
                    showMenuButton = true,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = "Buscar tema...",
                        onValueChange = viewModel::onSearchChanged
                    ),
                    onMenuClick = viewModel::onMenuClick
                )
            ) { paddingValues ->
                ForumsScreen(
                    foros = state.forums,
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
        }

        composable<PostsTopics> { backStackEntry ->
            val args = backStackEntry.toRoute<PostsTopics>()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(args.roomId) {
                viewModel.loadPostsByRoom(args.roomId)
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.roomTitle,
                    subtitle = "Temas de la sala",
                    showBackButton = true,
                    showMenuButton = true,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = "Buscar temas...",
                        onValueChange = viewModel::onSearchChanged
                    ),
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = viewModel::onMenuClick
                )
            ) { paddingValues ->
                PostsScreen(
                    posts = state.posts,
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
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable<TopicDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<TopicDetail>()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(args.topicId) {
                viewModel.loadTopicDetail(args.topicId.toInt())
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = args.topicTitle,
                    subtitle = state.selectedPost?.let { "${it.title} · ${it.numComments} respuestas" }
                        ?: args.topicTitle,
                    showBackButton = true,
                    showMenuButton = true,
                    searchConfig = null,
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = viewModel::onMenuClick
                )
            ) { paddingValues ->
                TopicScreen(
                    topic = state.selectedPost ?: return@ForumModuleScaffold,
                    mainComment = state.comments.firstOrNull() ?: return@ForumModuleScaffold,
                    comments = state.comments.drop(1),
                    onReply = { },
                    onSave = { },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rfz.appflotal.data.model.database.CommentEntity
import com.rfz.appflotal.data.model.database.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {
    // --- Topics ---
    @Query("SELECT * FROM forum_topic WHERE idRoom = :roomId")
    fun getTopicsByRoom(roomId: Int): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Query("DELETE FROM forum_topic WHERE idRoom = :roomId")
    suspend fun clearTopicsByRoom(roomId: Int)

    @Transaction
    suspend fun refreshTopics(roomId: Int, topics: List<TopicEntity>) {
        clearTopicsByRoom(roomId)
        insertTopics(topics)
    }

    // --- Comments ---
    @Query("SELECT * FROM forum_comment WHERE idTopic = :topicId")
    fun getCommentsByTopic(topicId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM forum_comment WHERE idTopic = :topicId")
    suspend fun clearCommentsByTopic(topicId: Int)

    @Transaction
    suspend fun refreshComments(topicId: Int, comments: List<CommentEntity>) {
        clearCommentsByTopic(topicId)
        insertComments(comments)
    }

    @Query("DELETE FROM forum_topic WHERE idTopic = :topicId")
    suspend fun deleteTopic(topicId: Int)
}

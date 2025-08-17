package com.example.musicapptraining.data.repositories

import android.content.Context
import com.example.musicapptraining.data.entities.SongEntity
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.data.utils.SongEntityMock.fakeAudioFetcher
import com.example.musicapptraining.data.utils.SongEntityMock.invalidSongEntity
import com.example.musicapptraining.data.utils.SongEntityMock.secondValidSongEntity
import com.example.musicapptraining.data.utils.SongEntityMock.validSongEntity
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.SongRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SongRepositoryImplTest{
    private lateinit var songRepository: SongRepository
    private val musicDao : MusicDao = mockk()
    private val context : Context = mockk(relaxed = true)
    private val audioFetcher : DataFetcher<Song> = mockk()

    @BeforeEach
    fun setup(){
        songRepository = SongRepositoryImpl(
            musicDao = musicDao,
            context = context,
            audioFetcher = fakeAudioFetcher
        )
    }
    @Test
    fun `searchSong should return a flow of list of songs if the search name is valid`()= runTest{
        //Given
        val query = "Hello"
        val songEntities = listOf(validSongEntity)
        val domainSongs = songEntities.map { it.toDomain() }
        coEvery { musicDao.searchSongsName(query) } returns flowOf(songEntities)
        //When
        val searchedSong = songRepository.searchSong(query).first()
        //Then
        assertThat(searchedSong).isEqualTo(domainSongs)
    }
    @Test
    fun `searchSong should return an empty body if entered name can't be found in musicDao`() =
        runTest{
        //Given
        val query = "Hello"
        val songEntities = listOf(invalidSongEntity)
        val domainSongs = songEntities.map { it.toDomain() }
        coEvery { musicDao.searchSongsName(query) } returns flowOf(emptyList())
        //When
        val searchedSong = songRepository.searchSong(query)
        //Then
        assertThat(searchedSong).isNotEqualTo(domainSongs)
    }
    @Test
    fun `getAllSongs should return a flow of list of songs when dao is not empty`()=
        runTest{
            //Given
            val songEntities = listOf(validSongEntity)
            val domainSongs = songEntities.map { it.toDomain() }
            coEvery { musicDao.getAllSongs() } returns flowOf(songEntities)
            //when
            val allSongs = songRepository.getAllSongs().first()
            //Then
            assertThat(allSongs).isEqualTo(domainSongs)
    }
    @Test
    fun `getAllSongs should fetch songs from device if there are no songs in room`()= runTest {
        //Given
        coEvery { musicDao.getAllSongs() } returns flowOf(emptyList())
        val slot = slot<List<SongEntity>>()
        coEvery { musicDao.deleteSongs() } just Runs
        coEvery { musicDao.insertAllSongs(capture(slot))} just Runs
        //when
        songRepository.getAllSongs().first()
        //Then
        assertThat(slot.captured).isNotEmpty()
        assertThat(slot.captured.first().songId).isEqualTo("1")
    }
    @Test
    fun `checkAndRefresh should return songs from device if local audios are not equal to device audios`() =
        runTest{
            //Given
            val testRepository = SongRepositoryImpl(
                musicDao = musicDao,
                context = context,
                audioFetcher = audioFetcher
            )
            val localSongs = listOf(validSongEntity)
            val deviceSongs = listOf(secondValidSongEntity)
            coEvery { audioFetcher.fetchDataFromDevice() } returns deviceSongs.toDomain()
            coEvery { musicDao.getAllSongs() } returns flowOf(localSongs)
            coEvery { musicDao.deleteSongs() } just Runs
            coEvery { musicDao.insertAllSongs(any()) } just Runs
            //When
            val result = testRepository.checkAndRefresh()
            //Then
            assertThat(result).isEqualTo(deviceSongs.toDomain())
            coVerify { musicDao.deleteSongs() }
            coVerify { musicDao.insertAllSongs(deviceSongs) }
        }
    @Test
    fun `checkAndRefresh should return local audios without dealing with db`() = runTest {
        //Given
        val songs = listOf(validSongEntity)
        coEvery { audioFetcher.fetchDataFromDevice() } returns songs.toDomain()
        coEvery { musicDao.getAllSongs() } returns flowOf(songs)
        //When
        val result = songRepository.checkAndRefresh()
        //Then
        assertThat(result).isEqualTo(songs.toDomain())
        coVerify(exactly = 0) { musicDao.deleteSongs()}
        coVerify(exactly = 0) { musicDao.insertAllSongs(any()) }
    }
}
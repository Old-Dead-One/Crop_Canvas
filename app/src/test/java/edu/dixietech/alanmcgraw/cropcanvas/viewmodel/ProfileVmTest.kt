package edu.dixietech.alanmcgraw.cropcanvas.viewmodel

import app.cash.turbine.test
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserRepository
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.ProfileUiState
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.ProfileVm
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileVmTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: CropCanvasRepository
    private lateinit var userRepo: UserRepository
    private lateinit var viewModel: ProfileVm

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        userRepo = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile emits Loading then Success`()= runTest {
        // Setup
        val profile = Profile("test-name", 1000, emptyList(), emptyList()) {
            fun toProfile() = profile.toProfile(
                seeds = seeds.map(SeedEntity::toSeed),
                products = products.map(ProductEntity::toProduct),
            )
        }

        // Creates a flow of AsyncResult objects that emits Loading and Success with the given profile.
        val flow = flowOf(
            AsyncResult.Loading(),
            AsyncResult.Success(profile)
        )

        // Mock the repository's getUserProfile method to return the flow
        coEvery { repository.getUserProfile() } returns flow

        // Run
        viewModel = ProfileVm(repository, userRepo)

        // Assert
        viewModel.uiState.test {
            assertEquals(ProfileUiState.Loading, awaitItem())

            advanceUntilIdle()

            assertEquals(ProfileUiState.Success(profile), awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadProfile emits loading them error`() = runTest {
        val errorMessage = "Test error message"

        val flow = flowOf(
            AsyncResult.Loading<Profile>(),
            AsyncResult.Error(errorMessage)
        )

        coEvery { repository.getUserProfile() } returns flow

        viewModel = ProfileVm(repository, userRepo)

        viewModel.uiState.test {
            assertEquals(ProfileUiState.Loading, awaitItem())

            advanceUntilIdle()

            assertEquals(ProfileUiState.Error(errorMessage), awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `signOut calls userRepo signOut`() = runTest {
        coEvery { repository.getUserProfile() } returns flowOf(AsyncResult.Loading())
        coEvery { userRepo.signOut() } just Runs // Does not expect a return value | Just runs the block of code

        viewModel = ProfileVm(repository, userRepo)

        viewModel.signOut()

        advanceUntilIdle()

        coVerify(exactly = 1) { userRepo.signOut() } // Verify that signOut was called exactly once
    }
}
package edu.dixietech.alanmcgraw.cropcanvas.repository

import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.database.CropCanvasDatabase
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.network.CropCanvasApi
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlotDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlantSeedsDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProfileDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ReceiptDto
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepositoryImpl
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CropCanvasRepositoryTest {

    private lateinit var repository: CropCanvasRepositoryImpl
    private lateinit var mockApi: CropCanvasApi
    private lateinit var mockDatabase: CropCanvasDatabase
    private lateinit var mockAuthenticator: UserAuthenticator

    @Before
    fun setup() {
        mockApi = mock()
        mockDatabase = mock()
        mockAuthenticator = mock()
        repository = CropCanvasRepositoryImpl(
            network = mockApi,
            userAuthenticator = mockAuthenticator,
            database = mockDatabase,
            context = kotlinx.coroutines.test.StandardTestDispatcher()
        )
    }

    @Test
    fun `getPlots reads from local database`() = runTest {
        // Given
        val token = "test-token"
        val mockProfileEntity = createMockProfileEntity()
        whenever(mockAuthenticator.authToken).thenReturn(flowOf(token))
        whenever(mockDatabase.getProfile(token)).thenReturn(mockProfileEntity)

        // When
        val result = repository.getPlots().first()

        // Then
        assertTrue(result is AsyncResult.Success)
        val plots = (result as AsyncResult.Success).data
        assertEquals(2, plots.size)
        assertEquals("plot1", plots[0].id)
        assertEquals("plot2", plots[1].id)
    }

    @Test
    fun `plantSeeds calls API and updates local database`() = runTest {
        // Given
        val token = "test-token"
        val plotId = "plot1"
        val seedName = "corn"
        val seedAmount = 5
        
        val mockPlantSeedsDto = PlantSeedsDto(
            plantedDate = "2024-01-01T00:00:00Z",
            amount = seedAmount,
            name = seedName,
            maturationDate = "2024-01-02T00:00:00Z"
        )
        
        val mockUpdatedProfileDto = createMockProfileDto()
        
        whenever(mockAuthenticator.authToken).thenReturn(flowOf(token))
        whenever(mockApi.plantSeeds(token, plotId, seedName, seedAmount)).thenReturn(mockPlantSeedsDto)
        whenever(mockApi.getProfile(token)).thenReturn(mockUpdatedProfileDto)

        // When
        val result = repository.plantSeeds(plotId, seedName, seedAmount).first()

        // Then
        assertTrue(result is AsyncResult.Success)
        verify(mockApi).plantSeeds(token, plotId, seedName, seedAmount)
        verify(mockApi).getProfile(token)
        verify(mockDatabase).saveProfile(any())
    }

    private fun createMockProfileEntity() = edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntity(
        profile = edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntityStub(
            name = "test-user",
            balance = 100,
            token = "test-token"
        ),
        seeds = emptyList(),
        products = emptyList(),
        plots = listOf(
            edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.PlotEntity(
                id = "plot1",
                name = "Plot 1",
                size = 10,
                profileName = "test-user"
            ),
            edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.PlotEntity(
                id = "plot2",
                name = "Plot 2",
                size = 10,
                profileName = "test-user"
            )
        )
    )

    private fun createMockProfileDto() = ProfileDto(
        name = "test-user",
        balance = 100,
        inventory = ProfileDto.Inventory(
            seeds = emptyList(),
            products = emptyList()
        ),
        plots = listOf(
            PlotDto(
                id = "plot1",
                name = "Plot 1",
                size = 10
            )
        )
    )
} 
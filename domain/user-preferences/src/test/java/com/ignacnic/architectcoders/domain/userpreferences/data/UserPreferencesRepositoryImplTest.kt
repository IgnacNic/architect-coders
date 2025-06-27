package com.ignacnic.architectcoders.domain.userpreferences.data

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.Assert

class UserPreferencesRepositoryImplTest {
    private val sharedPreferencesDataSource = mockk<SharedPreferencesDataSource>()

    private val sut = UserPreferencesRepositoryImpl(sharedPreferencesDataSource)

    @Test
    fun `SHOULD return default value WHEN getString GIVEN data source returns default`() {
        val expectedDefault = "test"
        every { sharedPreferencesDataSource.getString(any(), any()) } returnsArgument 1

        Assert.assertEquals(sut.getString(MOCK_KEY, expectedDefault), expectedDefault)
    }

    @Test
    fun `SHOULD return actual value WHEN getString GIVEN data source returns actual value`() {
        val expectedDefault = "test"
        every { sharedPreferencesDataSource.getString(MOCK_KEY, any()) } returns MOCK_VALUE

        Assert.assertEquals(sut.getString(MOCK_KEY, expectedDefault), MOCK_VALUE)
    }

    @Test
    fun `SHOULD call data source put string with same values WHEN putString`() {
        justRun { sharedPreferencesDataSource.putString(any(), any()) }
        sut.putString(MOCK_KEY, MOCK_VALUE)

        verify {
            sharedPreferencesDataSource.putString(MOCK_KEY, MOCK_VALUE)
        }
    }

    companion object {
        private const val MOCK_KEY = "key"
        private const val MOCK_VALUE = "value"
    }
}

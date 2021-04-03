package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setup() {
        stopKoin() //Koin stopped, because just DataSource is required(I think)
        reminderDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    // subject - action - returns
    fun loadReminders_showNoData() {
        //GIVEN
        reminderListViewModel.loadReminders()

        //WHEN
        val showNoData = reminderListViewModel.showNoData.getOrAwaitValue()

        //THEN
        assertThat(showNoData, `is`(true))
    }

    @Test
    fun loadReminders_displayError()  {
        reminderDataSource.setReturnError(true)

        reminderListViewModel.loadReminders()

        val displayErrorMessage = reminderListViewModel.showSnackBar.getOrAwaitValue()

        assertThat(displayErrorMessage.isNotEmpty(), `is`(true))
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()

        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

}
package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeTestDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
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
class SaveReminderViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderTestDataSource: FakeTestDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var reminderToAdd: ReminderDataItem

    @Before
    fun setup() {
        stopKoin() //Koin stopped, because just DataSource is required(I think)
        reminderTestDataSource = FakeTestDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), reminderTestDataSource)

        reminderToAdd = ReminderDataItem(
            "Palace of the López",
            "Serves as workplace for the President of Paraguay, and is also the seat of the government of Paraguay",
            "Paraguay",
            0.0,
            0.0
        )
    }

    @Test
    fun saveReminder_loading() {
        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(reminderToAdd)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun saveReminder_showToastAfterSave() {
        saveReminderViewModel.saveReminder(reminderToAdd)

        val showToastMessage = saveReminderViewModel.showToast.getOrAwaitValue()

        assertThat(showToastMessage.isNotEmpty(), `is`(true))
    }

    @Test
    fun saveReminder_notifyMissingData() {
        reminderToAdd.title = null

        saveReminderViewModel.validateEnteredData(reminderToAdd)

        val snackBarMessageResource = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        assertThat(snackBarMessageResource, `is`(R.string.err_enter_title))
    }
}
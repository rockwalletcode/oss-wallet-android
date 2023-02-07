package com.rockwallet.kyc.ui.features.personalinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.common.utils.hideKeyboard
import com.rockwallet.common.utils.showKeyboard
import com.rockwallet.common.utils.textOrEmpty
import com.rockwallet.kyc.R
import com.rockwallet.kyc.data.model.Country
import com.rockwallet.kyc.data.model.CountryState
import com.rockwallet.kyc.data.model.CountryStates
import com.rockwallet.kyc.databinding.FragmentPersonalInformationBinding
import com.rockwallet.kyc.ui.features.countryselection.CountrySelectionFragment
import com.rockwallet.kyc.ui.features.stateselection.StateSelectionFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collect
import java.util.*

class PersonalInformationFragment : Fragment(),
    RockWalletView<PersonalInformationContract.State, PersonalInformationContract.Effect> {

    private lateinit var binding: FragmentPersonalInformationBinding
    private val viewModel: PersonalInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_personal_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonalInformationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.ConfirmClicked)
            }

            etName.doAfterTextChanged {
                viewModel.setEvent(
                    PersonalInformationContract.Event.NameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etLastName.doAfterTextChanged {
                viewModel.setEvent(
                    PersonalInformationContract.Event.LastNameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etDay.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etYear.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etMonth.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etCountry.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.CountryClicked
                )
            }

            etState.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.StateClicked
                )
            }

            parentFragmentManager.setFragmentResultListener(REQUEST_KEY_STATE_SELECTION, this@PersonalInformationFragment) { _, bundle ->
                val state = bundle.getParcelable(StateSelectionFragment.EXTRA_SELECTED_STATE) as CountryState?
                if (state != null) {
                    viewModel.setEvent(
                        PersonalInformationContract.Event.StateChanged(state)
                    )
                }
            }

            parentFragmentManager.setFragmentResultListener(REQUEST_KEY_COUNTRY_SELECTION, this@PersonalInformationFragment) { _, bundle ->
                val country = bundle.getParcelable(CountrySelectionFragment.EXTRA_SELECTED_COUNTRY) as Country?
                if (country != null) {
                    viewModel.setEvent(
                        PersonalInformationContract.Event.CountryChanged(country)
                    )
                }
            }
        }

        if (viewModel.state.value.name.isEmpty()) {
            binding.etName.showKeyboard()
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        viewModel.setEvent(PersonalInformationContract.Event.LoadProfile)
    }

    override fun render(state: PersonalInformationContract.State) {
        with(binding) {
            viewLoading.root.isVisible = state.loadingVisible
            viewCompleted.root.isVisible = state.completedViewVisible
            btnConfirm.isEnabled = state.confirmEnabled

            tilState.isVisible = state.stateVisible
            etState.setText(state.state?.name)
            etCountry.setText(state.country?.name)

            etName.setText(state.name)
            etName.setSelection(state.name.length)
            etLastName.setText(state.lastName)
            etLastName.setSelection(state.lastName.length)

            val date = state.dateOfBirth
            etDay.setText(date?.get(Calendar.DAY_OF_MONTH)?.toString())
            etYear.setText(date?.get(Calendar.YEAR)?.toString())
            etMonth.setText(date?.get(Calendar.MONTH)?.plus(1)?.toString())
        }
    }

    override fun handleEffect(effect: PersonalInformationContract.Effect) {
        when (effect) {
            is PersonalInformationContract.Effect.GoBack -> {
                hideKeyboard()
                findNavController().popBackStack()
            }

            is PersonalInformationContract.Effect.Dismiss -> {
                hideKeyboard()
                findNavController().navigate(
                    PersonalInformationFragmentDirections.actionAccountVerification()
                )
            }

            is PersonalInformationContract.Effect.ShowToast -> {
                hideKeyboard()
                RockWalletToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
            }

            is PersonalInformationContract.Effect.DateSelection -> {
                hideKeyboard()
                val constraints = CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                val picker = MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(constraints)
                    .setSelection(effect.date?.timeInMillis)
                    .build()

                picker.addOnPositiveButtonClickListener {
                    viewModel.setEvent(PersonalInformationContract.Event.DateChanged(it))
                }

                picker.show(childFragmentManager, TAG_DATE_PICKER)
            }

            is PersonalInformationContract.Effect.StateSelection -> {
                hideKeyboard()
                findNavController().navigate(
                    PersonalInformationFragmentDirections.actionStateSelection(
                        REQUEST_KEY_STATE_SELECTION, CountryStates(effect.states.toTypedArray())
                    )
                )
            }

            is PersonalInformationContract.Effect.CountrySelection -> {
                hideKeyboard()
                findNavController().navigate(
                    PersonalInformationFragmentDirections.actionCountrySelection(
                        REQUEST_KEY_COUNTRY_SELECTION
                    )
                )
            }
        }
    }

    companion object {
        const val TAG_DATE_PICKER = "date_of_birth_picker"
        const val REQUEST_KEY_STATE_SELECTION = "request_key_state"
        const val REQUEST_KEY_COUNTRY_SELECTION = "request_key_country"
    }

    private fun hideKeyboard() {
        with(binding) {
            etName.hideKeyboard()
            etLastName.hideKeyboard()
        }
    }
}
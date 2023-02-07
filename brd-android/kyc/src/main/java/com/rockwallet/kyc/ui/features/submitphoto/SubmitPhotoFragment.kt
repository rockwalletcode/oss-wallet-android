package com.rockwallet.kyc.ui.features.submitphoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.kyc.R
import com.rockwallet.kyc.data.enums.DocumentSide
import com.rockwallet.kyc.data.enums.DocumentType.SELFIE
import com.rockwallet.kyc.data.enums.DocumentType.PASSPORT
import com.rockwallet.kyc.databinding.FragmentSubmitPhotoBinding
import kotlinx.coroutines.flow.collect

class SubmitPhotoFragment : Fragment(),
    RockWalletView<SubmitPhotoContract.State, SubmitPhotoContract.Effect> {

    private lateinit var binding: FragmentSubmitPhotoBinding
    private val viewModel: SubmitPhotoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_submit_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubmitPhotoBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.ConfirmClicked)
            }

            btnRetakePhoto.setOnClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.RetakeClicked)
            }
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

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.setEvent(SubmitPhotoContract.Event.BackClicked)
        }
    }

    override fun render(state: SubmitPhotoContract.State) {
        with(binding) {
            loadingView.root.isVisible = state.loadingVisible

            val content1 = when (state.documentType) {
                SELFIE -> R.string.AccountKYCLevelTwo_FaceCaptureInstructions
                PASSPORT -> R.string.AccountKYCLevelTwo_Instructions
                else -> when (state.currentData.documentSide) {
                    DocumentSide.FRONT -> R.string.AccountKYCLevelTwo_FrontPageInstructions
                    DocumentSide.BACK -> R.string.AccountKYCLevelTwo_BackPageInstructions
                }
            }

            val content2 = when (state.documentType) {
                SELFIE -> R.string.AccountKYCLevelTwo_FaceVisibleConfirmation
                else -> R.string.AccountKYCLevelTwo_DocumentConfirmation
            }

            checkedItem1.setContent(context?.getString(content1))
            checkedItem2.setContent(context?.getString(content2))

            Glide.with(requireContext())
                .load(state.currentData.imageUri)
                .into(ivReviewPhoto)
        }
    }

    override fun handleEffect(effect: SubmitPhotoContract.Effect) {
        when (effect) {
            is SubmitPhotoContract.Effect.Back ->
                findNavController().popBackStack()

            is SubmitPhotoContract.Effect.Dismiss ->
                findNavController().navigate(
                    SubmitPhotoFragmentDirections.actionAccountVerification()
                )

            is SubmitPhotoContract.Effect.TakePhoto ->
                findNavController().navigate(
                    SubmitPhotoFragmentDirections.actionTakePhoto(
                        documentData = effect.documentData,
                        documentType = effect.documentType
                    )
                )

            is SubmitPhotoContract.Effect.PostValidation -> {
                findNavController().navigate(
                    SubmitPhotoFragmentDirections.actionPostValidation()
                )
            }

            is SubmitPhotoContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
        }
    }
}
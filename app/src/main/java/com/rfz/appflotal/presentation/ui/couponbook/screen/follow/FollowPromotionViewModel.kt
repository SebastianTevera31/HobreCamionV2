package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.data.repository.promotions.PromotionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowPromotionViewModel @Inject constructor(
    private val promotionsRepository: PromotionsRepository
) : ViewModel() {

}
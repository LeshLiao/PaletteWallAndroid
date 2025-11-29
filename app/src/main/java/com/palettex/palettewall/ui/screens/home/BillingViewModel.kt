package com.palettex.palettewall.ui.screens.home

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class BillingViewModel(private val context: Context) : ViewModel() {
    private lateinit var billingClient: BillingClient
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                viewModelScope.launch {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
        }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        Log.d("GDT", "setupBillingClient()")
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Implement retry logic here
            }
        })
    }

    private fun queryPurchases() {
        viewModelScope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val subscriptionResult = billingClient.queryPurchasesAsync(params)

            val oneTimeParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            val oneTimeResult = billingClient.queryPurchasesAsync(oneTimeParams)

            val hasPremium = (subscriptionResult.purchasesList + oneTimeResult.purchasesList)
                .any { purchase ->
                    purchase.products.any { it == "monthly_premium" || it == "weekly_premium" } &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }

            _isPremium.value = hasPremium
//            _isPremium.value = true // TODO: test
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isPremium.value = true

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            }
        }
    }

    fun launchBillingFlow(activity: Activity, subscriptionType: String) {
        viewModelScope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(subscriptionType)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetailsList.firstOrNull()?.let { productDetails ->
                        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                        if (offerToken != null) {
                            val productDetailsParamsList = listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build()
                            )

                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()

                            billingClient.launchBillingFlow(activity, billingFlowParams)
                        }
                    }
                }
            }
        }
    }

    fun getSubscriptionPrices(callback: (Map<String, String>) -> Unit) {
        viewModelScope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("weekly_premium")
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("monthly_premium")
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val priceMap = mutableMapOf<String, String>()
                    productDetailsList.forEach { productDetails ->
                        val priceInfo = productDetails.subscriptionOfferDetails?.firstOrNull()
                            ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                        if (priceInfo != null) {
                            priceMap[productDetails.productId] = priceInfo.formattedPrice
                        }
                    }
                    callback(priceMap)
                } else {
                    callback(emptyMap())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::billingClient.isInitialized) {
            billingClient.endConnection()
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BillingViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


package com.palettex.palettewall.ui.screens.home

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private lateinit var billingClient: BillingClient
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    // Track all purchased product IDs
    private val _purchasedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val purchasedProductIds: StateFlow<Set<String>> = _purchasedProductIds

    // Track if the current item/product has been purchased
    private val _currentProductId = MutableStateFlow<String?>(null)
    private val _isAlreadyPurchase = MutableStateFlow(false)
    val isAlreadyPurchase: StateFlow<Boolean> = _isAlreadyPurchase

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
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()  // Enable support for one-time purchases
                    .build()
            )
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

            // Collect all purchased product IDs
            val allPurchases = subscriptionResult.purchasesList + oneTimeResult.purchasesList
            val purchasedIds = mutableSetOf<String>()

            allPurchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Add all product IDs from this purchase
                    purchasedIds.addAll(purchase.products)
                    Log.d("GDT", "Found purchased product IDs: ${purchase.products.joinToString()}")
                }
            }

            // mock purchased data test
            // purchasedIds.add("20251101_0859_1ae4cbbe")

            _purchasedProductIds.value = purchasedIds
            Log.d("GDT", "All purchased product IDs: ${purchasedIds.joinToString()}")

            // Update isAlreadyPurchase based on current product ID
            updateIsAlreadyPurchase()

            // Premium status is only for subscriptions, not individual wallpaper purchases
            // Individual wallpaper purchases are tracked via isAlreadyPurchase
            val hasPremium = allPurchases.any { purchase ->
                purchase.products.any {
                    it == "monthly_premium" ||
                    it == "weekly_premium"
                } && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }

            _isPremium.value = hasPremium
//            _isPremium.value = true // TODO: test
        }
    }

    private val subscriptionProductIds = setOf(
        "weekly_premium",
        "monthly_premium"
    )

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Get product IDs from this purchase
            val productIds = purchase.products
            Log.d("GDT", "Purchase completed! Product IDs: ${productIds.joinToString()}")
            Log.d("GDT", "Purchase token: ${purchase.purchaseToken}")
            Log.d("GDT", "Purchase order ID: ${purchase.orderId}")

            // Update the purchased product IDs set
            val currentPurchasedIds = _purchasedProductIds.value.toMutableSet()
            currentPurchasedIds.addAll(productIds)
            _purchasedProductIds.value = currentPurchasedIds

            Log.d("GDT", "Updated purchased product IDs list: ${_purchasedProductIds.value.joinToString()}")

            // Update isAlreadyPurchase based on current product ID
            updateIsAlreadyPurchase()

            val isSubscriptionPurchase = productIds.any { it in subscriptionProductIds }

            if (isSubscriptionPurchase) {
                _isPremium.value = true
            }

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            }
        }
    }

    /**
     * Get a list of all purchased product IDs
     * @return List of product IDs that the user has purchased
     */
    fun getPurchasedProductIds(): List<String> {
        return _purchasedProductIds.value.toList()
    }

    /**
     * Check if a specific product ID has been purchased
     * @param productId The product ID to check
     * @return true if the product has been purchased, false otherwise
     */
    fun isProductPurchased(productId: String): Boolean {
        return _purchasedProductIds.value.contains(productId)
    }

    /**
     * Get all purchased product IDs as a flow for reactive updates
     * @return StateFlow containing the set of purchased product IDs
     */
    fun getPurchasedProductIdsFlow(): StateFlow<Set<String>> {
        return purchasedProductIds
    }

    /**
     * Set the current product ID (itemId) to check if it's already purchased
     * This should be called when the user views a specific wallpaper
     * @param productId The product ID (same as itemId) to check
     */
    fun setCurrentProductId(productId: String) {
        _currentProductId.value = productId
        updateIsAlreadyPurchase()
    }

    /**
     * Update the isAlreadyPurchase state based on current product ID
     */
    private fun updateIsAlreadyPurchase() {
        val currentProductId = _currentProductId.value
        if (currentProductId != null) {
            val isPurchased = _purchasedProductIds.value.contains(currentProductId)
            _isAlreadyPurchase.value = isPurchased
            Log.d("GDT", "Checking purchase for productId: $currentProductId, isPurchased: $isPurchased")
        } else {
            _isAlreadyPurchase.value = false
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

    fun getOneTimePurchasePrice(productId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            // Check billing client is ready
            if (!billingClient.isReady) {
                Log.e("GDT", "Billing client is not ready")
                callback(null)
                return@launch
            }

            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetails = productDetailsList.firstOrNull()
                    val price = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice

                    Log.d("GDT", "Product ID: ${productDetails?.productId}")
                    Log.d("GDT", "Price: $price")

                    callback(price)
                } else {
                    Log.e("GDT", "Failed to query price: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    callback(null)
                }
            }
        }
    }

    fun launchBillingFlowWithOneTimePurchase(activity: Activity, productId: String) {
        viewModelScope.launch {
            // Check billing client is ready
            if (!billingClient.isReady) {
                Log.e("GDT", "Billing client is not ready")
                return@launch
            }

            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                Log.d("GDT", "Response code: ${billingResult.responseCode}")
                Log.d("GDT", "Debug message: ${billingResult.debugMessage}")
                Log.d("GDT", "Product list size: ${productDetailsList.size}")

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetailsList.firstOrNull()?.let { productDetails ->
                        Log.d("GDT", "Product ID: ${productDetails.productId}")
                        Log.d("GDT", "Product Type: ${productDetails.productType}")
                        Log.d("GDT", "Price: ${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}")

                        // For simple one-time purchases, don't use offer token
                        val productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                        )

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                        val flowResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                        Log.d("GDT", "Launch billing flow result: ${flowResult.responseCode}")
                    } ?: run {
                        Log.e("GDT", "Product details not found for productId: $productId")
                    }
                } else {
                    Log.e("GDT", "Failed to query product details: ${billingResult.responseCode} - ${billingResult.debugMessage}")
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
}


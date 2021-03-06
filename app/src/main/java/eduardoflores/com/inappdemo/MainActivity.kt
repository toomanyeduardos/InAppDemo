package eduardoflores.com.inappdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Google's directions: https://developer.android.com/google/play/billing/billing_library_overview
 */

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {
    val TAG = "MainActivity"
    private lateinit var billingClient: BillingClient
    private lateinit var skuDetails: List<SkuDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBillingClient()

        queryBtn.setOnClickListener { queryItems() }
    }

    private fun startBillingClient() {
        billingClient = BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "Billing client not ready")
            }

            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    Log.d(TAG, "Billing client is ready!")

                    queryExistingPurchases()
                } else {
                    // Possible to end up here if Play Store not set on device (including emulator)
                    Log.e(TAG, "Billing client failed during setup. Error code is $responseCode")

                }
            }
        })
    }

    private fun queryExistingPurchases() {
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { responseCode, purchasesList ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                hello_tv.text = getString(R.string.current_purchases, purchasesList.toString())
            } else {
                Log.e(TAG, "Failed to query existing purchases. Response Code = $responseCode")
            }
        }
    }

    private fun getItemsSkuList() = listOf("coins_100", "coins_200")

    private fun queryItems() {
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(getItemsSkuList())
        params.setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                Log.d(TAG, "query details = $skuDetailsList")

                setupRecyclerView(skuDetailsList)
                skuDetails = skuDetailsList
            } else {
                Log.e(TAG, "Query for details failed")
            }
        }
    }

    fun buyItem(itemPosition: Int) {
        val purchaseParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails[itemPosition])
            .build()

        val responseCode = billingClient.launchBillingFlow(this, purchaseParams)
        Log.d(TAG, "purchase flow response code = $responseCode")
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK) {
            Log.d(TAG, "Purchase succeeded. Response = $purchases")
        } else {
            Log.e(TAG, "Purchase failed!. Response code = $responseCode")
        }
    }



    // list layout elements
    private fun setupRecyclerView(skuDetails: List<SkuDetails>) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = ListAdapter(getAvailableItemsFromSdk(skuDetails))
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        findViewById<RecyclerView>(R.id.items_recycler_view).apply {/**/
            setHasFixedSize(true)
            layoutManager = viewManager
            addItemDecoration(decoration)
            adapter = viewAdapter
        }
    }

    private fun getAvailableItemsFromSdk(skuDetails: List<SkuDetails>): MutableList<AvailableItem> {
        val newList = mutableListOf<AvailableItem>()
        skuDetails.forEach { skuDetail ->
            newList.add(AvailableItem(title = skuDetail.title, description = skuDetail.description,
                price = skuDetail.price, sku = skuDetail.sku))
        }
        return newList
    }
}

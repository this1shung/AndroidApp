package com.example.myfshop.firestore

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myfshop.R
import com.example.myfshop.models.Address
import com.example.myfshop.models.CartItem
import com.example.myfshop.models.Order
import com.example.myfshop.models.Product
import com.example.myfshop.models.Rating
import com.example.myfshop.models.SoldProduct
import com.example.myfshop.ui.activities.LoginActivity
import com.example.myfshop.ui.activities.RegisterActivity
import com.example.myfshop.ui.activities.UserProfileActivity
import com.example.myfshop.models.User
import com.example.myfshop.ui.activities.AddEditAddressActivity
import com.example.myfshop.ui.activities.AddProductActivity
import com.example.myfshop.ui.activities.AddressListActivity
import com.example.myfshop.ui.activities.CartListActivity
import com.example.myfshop.ui.activities.CheckoutActivity
import com.example.myfshop.ui.activities.EditProductActivity
import com.example.myfshop.ui.activities.ProductDetailsActivity
import com.example.myfshop.ui.activities.SettingsActivity
import com.example.myfshop.ui.fragments.DashboardFragment
import com.example.myfshop.ui.fragments.OrdersFragment
import com.example.myfshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import  com.example.myfshop.ui.fragments.ProductsFragment
import com.example.myfshop.ui.fragments.SoldProductsFragment
import com.example.myfshop.ui.fragments.UserFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {


                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    //    fun getUserDetails(activity: Activity) {
//        mFireStore.collection(Constants.USERS)
//            .document(getCurrentUserID())
//            .get()
//            .addOnSuccessListener { document ->
//
//                Log.i(activity.javaClass.simpleName, "Document: ${document.data}")
//
//                val user = document.toObject(User::class.java)!!
//
//                // Ghi log giá trị của role
//                Log.i(activity.javaClass.simpleName, "User role: ${user.role}")
//
//                val sharedPreferences =
//                    activity.getSharedPreferences(
//                        Constants.MYFSHOP_PREFERENCES,
//                        Context.MODE_PRIVATE
//                    )
//
//                val editor: SharedPreferences.Editor = sharedPreferences.edit()
//                editor.putString(
//                    Constants.LOGGED_IN_USERNAME,
//                    "${user.firstName} ${user.lastName}"
//                )
//                editor.apply()
//
//                when (activity) {
//                    is LoginActivity -> {
//                        activity.userLoggedInSuccess(user)
//                    }
//
//                    is SettingsActivity -> {
//                        activity.userDetailsSuccess(user)
//                    }
//                }
//
//            }
//            .addOnFailureListener { e ->
//                when (activity) {
//                    is LoginActivity -> {
//                        activity.hideProgressDialog()
//                    }
//
//                    is SettingsActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                }
//
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error while getting user details.",
//                    e
//                )
//            }
//    }
//
    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Convert the document into a User object
                val user = document.toObject(User::class.java)!!

                // Store the logged-in user's full name in SharedPreferences
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYFSHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                // Handle different activities based on the retrieved user's role
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }

                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }

                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }

                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                        // END
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                // TODO Step 4: Notify the success result.
                // START
                activity.productDetailsSuccess(product)
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating the document for cart item.", e)
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }

                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
                // END
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun getAllProductsList(activity: Activity) {
        // END
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }

                    // TODO Step 5: Notify the success result to the base class.
                    // START
                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    // END
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    // TODO Step 6: Hide the progress dialog.
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 5: Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }

    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while placing an order.", e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFireStore.batch()
        for (cart in cartList) {

            val soldProduct = SoldProduct(
                "XzZ4uM0LuRaBjTG3rI24zru0CcG2",
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address,
                "",
                order.size

            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }
                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val list: ArrayList<SoldProduct> = ArrayList()

                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }
                fragment.successSoldProductsList(list)

            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }




//    fun getUsersList(callback: (List<User>) -> Unit) {
//        FirebaseFirestore.getInstance().collection("users")
//            .get()
//            .addOnSuccessListener { result: QuerySnapshot ->
//                val usersList = ArrayList<User>()
//                for (document in result) {
//                    val user = document.toObject(User::class.java)
//                    usersList.add(user)
//                }
//                callback(usersList)
//            }
//            .addOnFailureListener {
//                callback(emptyList())
//            }
//    }
//
//    fun getUserDetails(userID: String, callback: (User?) -> Unit) {
//        FirebaseFirestore.getInstance().collection("users")
//            .document(userID)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    val user = document.toObject(User::class.java)
//                    callback(user)
//                } else {
//                    callback(null) // Handle error: user not found
//                }
//            }
//            .addOnFailureListener { exception ->
//                // Handle failure
//                callback(null)
//            }
//    }

    fun getUserRatingForProduct(productId: String, userId: String, callback: (Rating?) -> Unit) {
        mFireStore.collection(Constants.RATINGS)
            .whereEqualTo("productId", productId)
            .whereEqualTo("userId", userId)
            .limit(1)  // Thêm giới hạn này để đảm bảo chỉ lấy một kết quả
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val rating = documents.documents[0].toObject(Rating::class.java)
                    callback(rating)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting user rating", e)
                callback(null)
            }
    }

    fun submitRating(rating: Rating, callback: (Boolean) -> Unit) {
        val ratingsCollection = mFireStore.collection(Constants.RATINGS)

        // Kiểm tra nếu đánh giá đã tồn tại
        ratingsCollection
            .whereEqualTo("productId", rating.product_id)
            .whereEqualTo("userId", rating.user_id)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Nếu chưa có đánh giá, thêm mới
                    ratingsCollection
                        .add(mapOf(
                            "productId" to rating.product_id,
                            "userId" to rating.user_id,
                            "rating" to rating.rating
                        ))
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error submitting new rating", e)
                            callback(false)
                        }
                } else {
                    // Nếu đã có đánh giá, cập nhật
                    val documentId = documents.documents[0].id
                    ratingsCollection.document(documentId)
                        .set(mapOf(
                            "productId" to rating.product_id,
                            "userId" to rating.user_id,
                            "rating" to rating.rating
                        ))
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error updating existing rating", e)
                            callback(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking for existing rating", e)
                callback(false)
            }
    }



    fun getProductAverageRating(productId: String, callback: (Float) -> Unit) {
        mFireStore.collection(Constants.RATINGS)
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener { documents ->
                val ratings = documents.mapNotNull { it.toObject(Rating::class.java).rating }
                val averageRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f
                callback(averageRating)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting average rating", e)
                callback(0f)
            }
    }

    fun getUsersList(fragment: UserFragment) {
        mFireStore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { document ->
                val usersList: ArrayList<User> = ArrayList()
                for (i in document.documents) {
                    val user = i.toObject(User::class.java)!!
                    user.id = i.id
                    usersList.add(user)
                }
                fragment.successUsersListFromFireStore(usersList)
            }
            .addOnFailureListener { e ->
                fragment.userDeleteFailure(e)
            }
    }

    fun deleteUser(fragment: UserFragment, userId: String) {
        mFireStore.collection(Constants.USERS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                fragment.userDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.userDeleteFailure(e)
            }
    }

    fun getUserDetails(userId: String) = mFireStore.collection("users").document(userId).get()

    fun updateUserDetails(userId: String, userHashMap: HashMap<String, Any>) =
        mFireStore.collection("users").document(userId).update(userHashMap)


    fun getProductDetails(productId: String, callback: ProductDetailsCallback) {
        // Fetch product details from Firestore
        FirebaseFirestore.getInstance().collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) callback.onSuccess(product)
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception)
            }
    }

    fun addProduct(product: Product, callback: (Boolean) -> Unit) {
        // Add product to Firestore
        FirebaseFirestore.getInstance().collection("products").add(product)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun updateProduct(productId: String, product: Product, callback: (Boolean) -> Unit) {
        // Update product in Firestore
        FirebaseFirestore.getInstance().collection("products").document(productId).set(product)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
//    fun updateProductDetails(activity: EditProductActivity, productId: String, productHashMap: HashMap<String, Any>) {
//        mFireStore.collection(Constants.PRODUCTS)
//            .document(productId)
//            .update(productHashMap)
//            .addOnSuccessListener {
//                activity.productUpdateSuccess()
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while updating the product details.", e)
//            }
//    }
    fun updateProductDetails(activity: Activity, productId: String, productHashMap: HashMap<String, Any>): Task<Void> {
        return FirebaseFirestore.getInstance()
            .collection(Constants.PRODUCTS)
            .document(productId)
            .update(productHashMap)
    }


    interface ProductDetailsCallback {
        fun onSuccess(product: Product)
        fun onFailure(exception: Exception)
    }

}



# AndroidKotlinBaseProject
Library for Android project development using Kotlin language.

## Index
Here below list of how to use libraries for Android development, the majority being actively maintained.

[1. BaseActivity](https://github.com/Rorp-Dev/RorpDevAndroidLibs#BaseActivity)

[2. BaseViewModel](https://github.com/Rorp-Dev/RorpDevAndroidLibs#BaseViewModel)

[3. Internet indicator](https://github.com/Rorp-Dev/RorpDevAndroidLibs#NetworkCallBack)

[4. Biometric](https://github.com/Rorp-Dev/RorpDevAndroidLibs#Biometric)

[5. BaseViewGroup](https://github.com/Rorp-Dev/RorpDevAndroidLibs#BaseViewGroup)

[6. BaseFragment](https://github.com/Rorp-Dev/RorpDevAndroidLibs#BaseFragment)

[7. RetrofitUtils](https://github.com/Rorp-Dev/RorpDevAndroidLibs#RetrofitUtils)

### BaseActivity
Your activity should have viewmodel otherwise you can use **__BaseViewModel__** instead.
All activities can extends this abstract class.

-   To handle the back process for all its child fragments
-   To handle the navigation of fragments(add/replace)
-   To inflate the view of activities using data binding util

 1. Extends from BaseActivity
 2. Override method
 3. [In case you have your own ViewModel] extends from **__BaseViewModel__** 

```
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(){  
  
  override var frameContainerId: Int = R.id.rl_main  
  override val layoutId: Int = R.layout.activity_main  
  override val viewModel: MainViewModel = MainViewModel()
  
	override fun onCreate(savedInstanceState: Bundle?){
		super.onCreate(savedInstanceState)
	}
}
```

    class MainViewModel: BaseViewModel()

### BaseViewModel
Sometimes we have a same logic in many parts of our application. Such as checking the internet connection, showing/hiding progressBar when there is no data, or displaying a message/an error message when needed. So we put all these cases in only one ViewModel as called BaseViewModel and all our ViewModels extend it.
In order to request any API, we can do as following code sample:
```
class MainViewModel: BaseViewModel(), ApiResponse {  
    val postsData = MutableLiveData<List<Post>>()  
    val messageData = MutableLiveData<String>()  
  
    private var retrofitUtils = RetrofitUtils(scope, this)  
  
    fun getPosts() {  
        super.showLoading()  
        // request api
        retrofitUtils.requestData()  
    }  
  
    fun postArticle(){  
        super.showLoading()  
        val article = Article()  
        article.setTitle("Wotaku ni Koi wa Muzukashii")  
        article.setUserId(1)  
        retrofitUtils.requestData(article)  
    }  
  
    override fun onCleared() {  
        viewModelScope.cancel()  
        super.onCleared()  
    }  
  
    companion object {  
        private val TAG = MainViewModel::class.java.name  
  }  
  
    override fun onSuccess(response: String) {  
        super.hideLoading()  
        messageData.value = ""  
        // Parse response to your data class
		postsData.value = RetrofitUtils.jsonToList(response, Post::class.java)?.filterIsInstance<Post>()  
    }  
  
    override fun onError(error: ApiError?) {  
        messageData.value = error?.getErrorMessage()  
        // No internet connection return status code
        if(error?.code == 499){  
            super.hideLoading()  
        }  
    }  
}
```

### NetworkCallBack
Checking/observing internet status online/offline. You must do as following : 
1. create NetworkCallback instance
2. register callback in onResume() and onPause()
3. call method **__observeNetworkConnection()__** for observing internet status and you can do whatever you want here.
3. unregister in onDestroy()
```
  var networkCallback = NetworkCallback(this)
  networkCallback.observeNetworkConnection(object : NetworkCallback.NetworkObservation{
            override fun isActive(isOnline: Boolean) {
                // Handle your TODO
            }
        })
        
   override fun onResume() {
        super.onResume()
        networkCallback.registerNetworkCallback()
   }
   
   override fun onPause() {
        networkCallback.registerNetworkCallback()
        super.onPause()
    }
    
    override fun onDestroy() {
        networkCallback.unregisterNetworkCallback()
        super.onDestroy()
    }
```
### Biometric
The BiometricAuthenticator can also determine if the current device has biometric capabilities and whether or not the biometric feature has been enabled. After you extended **__BaseActivity__**, you can use the function by call ***showCredentialScreen()*** . It handle all events automatically. example:
```
class MainActivity...{
	override fun onCreate(savedInstanceState: Bundle?){
		super.onCreate(savedInstanceState)
		binding.fabSend.setOnClickListener{  
		  super.showCredentialScreen()  
		}
	}
}
```
If you want to custom the authentication results, you can do as following:
```
val biometricAuthenticator =  
    BiometricAuthenticator.instance(this, object : BiometricAuthenticator.Listener {  
        override fun onNewMessage(message: String) {  
            // TODO: Log your message  
		  }  
	})
	
// Here you can do your things
biometricAuthenticator.biometricListener = object : BiometricAuthenticator.BiometricListener{  
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {  
        Toast.makeText(this@BaseActivity, "SUCCEED", Toast.LENGTH_LONG).show()  
    }  
  
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {  
        Toast.makeText(this@BaseActivity, "ERROR", Toast.LENGTH_LONG).show()  
    }  
  
    override fun onAuthenticationFailed() {  
        Toast.makeText(this@BaseActivity, "FAILED", Toast.LENGTH_LONG).show()  
    }  
}
```

### BaseViewGroup
It`s an interface that interacts with activities and fragments to determine ViewModel, LayoutId and ViewDataBinding of them

### BaseFragment
All fragments extend this abstract class.

-   To inflate root view using data binding util
-   To override a Toolbar for fragment using toolBarId if it exists
-   To check the Authentication of user

### RetrofitUtils
We use **Retrofit** which is API interfaces are turned into callable objects and **Moshi** is a great JSON library for Kotlin. It makes it easy to parse JSON into objects.

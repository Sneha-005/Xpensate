import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AuthPreferences")

object TokenDataStore {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")

    private val ACCESS_TOKEN_KEY_LOGIN = stringPreferencesKey("accessTokenLogin")
    private val REFRESH_TOKEN_KEY_LOGIN = stringPreferencesKey("refreshTokenLogin")

    suspend fun saveTokens(context: Context, accessToken: String, refreshToken: String, isLogin: Boolean = false) {
        context.dataStore.edit { preferences ->
            if (isLogin) {
                preferences[ACCESS_TOKEN_KEY_LOGIN] = accessToken
                preferences[REFRESH_TOKEN_KEY_LOGIN] = refreshToken
            } else {
                preferences[ACCESS_TOKEN_KEY] = accessToken
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    fun getAccessToken(context: Context, isLogin: Boolean = false): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            if (isLogin) {
                preferences[ACCESS_TOKEN_KEY_LOGIN]
            } else {
                preferences[ACCESS_TOKEN_KEY]
            }
        }
    }

    fun getRefreshToken(context: Context, isLogin: Boolean = false): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            if (isLogin) {
                preferences[REFRESH_TOKEN_KEY_LOGIN]
            } else {
                preferences[REFRESH_TOKEN_KEY]
            }
        }
    }
}
